# Firebase + Android + existing text classifier

This integration uses Firebase Authentication, Cloud Firestore, private Cloud Storage photos,
one FastAPI service, and a separate worker using the existing scikit-learn feature pipeline.
There is no CLIP, image inference, or vector database. Existing experiment scripts and results
are not replaced. Photographs are viewed by their uploader and authorized staff only.

Validation on 19 November 2026: the Android debug APK and unit tests passed. The Figma-based
email-link screen was installed and inspected on a connected Android phone. Firebase project
`lost-and-found-9671c` accepts email-link requests, and its Hosting domain is verified as an
Android App Link on that phone. Completing a real email-link sign-in still requires opening the
received link. The live API and worker are not deployed; reports and ML matching are unavailable
after sign-in until those services are reachable.

## Local setup (no Firebase account required)

Use Python 3.12+, Node.js supported by firebase-tools, and Java 21+ on PATH.
On this computer Java is available in Android Studio's `jbr/bin` folder.
Run these commands from `SemProject_MLEngine`:

```powershell
py -3.12 -m venv .venv-service
.\.venv-service\Scripts\python.exe -m pip install -r requirements-service.lock.txt
.\.venv-service\Scripts\python.exe -m scripts.export_mobile_model
cd firebase
npm ci
cd ..
```

In separate terminals, run:

```powershell
.\scripts\start_local.ps1 -Service emulators
.\scripts\start_local.ps1 -Service api
.\scripts\start_local.ps1 -Service worker
```

After the emulators start, seed fictional accounts once:

```powershell
.\scripts\start_local.ps1 -Service seed
```

Local accounts: `demo-student@strathmore.edu` and `demo-office@strathmore.edu`.
Both use `LocalDemo123!`. The seed script refuses non-emulator projects.
The Emulator UI is at http://127.0.0.1:4000 and API docs at http://127.0.0.1:8000/docs.
Emulator data is temporary unless you explicitly export/import it with Firebase CLI.

Open `C:\Users\HP\AndroidStudioProjects\MAD_project` in Android Studio and run the debug
build on an Android emulator. Without `firebase.properties`, debug connects to the host
through `10.0.2.2`. The API/Auth/Storage ports are 8000/9099/9199. This configuration is
for the Android emulator; a physical device needs a deliberately configured development
connection. Release builds never enable the emulator settings.

1. Sign in as the office account and record a found item using the New tab.
2. Sign out, sign in as the student, and submit a lost item in the same category.
3. Refresh Reports after the worker runs, then review suggestions and request ownership review.
4. Sign in as office staff, check the claim and report details, approve after verification,
   then mark collected only after physical handover. Both reports close together.

## Persistence, security and matching

- FastAPI verifies Firebase ID tokens, email verification and the university email domain.
  `role` is a server-assigned custom claim, never a user-selected profile field.
- Report creation atomically stores a report and matching job. A stable request ID prevents
  retries from creating duplicate reports; changing the body for the same ID returns 409.
- The worker uses a 120-second lease and retries failures up to three times. Failed jobs can
  be explicitly retried. Model failure never removes the report. Run one worker initially.
- Same-category active candidates are scored with the existing features. More than 200
  candidates fails explicitly rather than silently evaluating a truncated set. The app shows
  up to 20 ranked suggestions; report lists are limited to 100 records per type.
- Scores come from constructed coursework labels, not validated real-world ownership probabilities.
  No automatic release/ownership decision is made. Do not interpret this setup as model validation.
- The model artifact is trained only on the existing campus development partition. Load only
  trusted artifacts: joblib is not a safe format for untrusted uploaded models.
- Firestore client writes are denied; business mutations run through authorized API endpoints.
  Admin SDK bypasses Rules, so API ownership and role checks are mandatory.
- Photo objects are private and immutable, under the uploader's UID and request UUID. The app
  stores object paths rather than shareable download URLs. JPEG/PNG/WebP are limited to <5 MB.
- Claim approval reserves both reports transactionally; collection closes both and writes an
  audit record. A second claim cannot reserve an already-reserved report.
- In-app notifications work locally. FCM push is wired for a real project, with generic text,
  and is skipped in emulator mode. FCM has no local emulator; live delivery needs cloud testing.
- This API-mediated workflow requires connectivity to submit. It does not claim Firestore's
  automatic offline synchronization for API requests. A saved local input is not a submitted report.

## Tests

For an automated run that starts/stops its own emulators, use this from `firebase/`
with Java on PATH and no other emulators running:

```powershell
npx.cmd firebase-tools emulators:exec --project demo-strathmore-lost-found --only "auth,firestore,storage" "..\.venv-service\Scripts\python.exe ..\scripts\verify_local.py"
```

To run checks against already-running emulators:

```powershell
.\.venv-service\Scripts\python.exe -m pytest tests -q
# With local emulators running, in this shell:
$env:APP_ENV='emulator'
$env:FIREBASE_PROJECT_ID='demo-strathmore-lost-found'
$env:FIREBASE_AUTH_EMULATOR_HOST='127.0.0.1:9099'
$env:FIRESTORE_EMULATOR_HOST='127.0.0.1:8080'
$env:RUN_FIREBASE_INTEGRATION='1'
.\.venv-service\Scripts\python.exe -m pytest tests/test_firebase_integration.py -q
cd firebase
node rules.test.mjs
```

## Live project status and remaining deployment

Firebase project `lost-and-found-9671c` is configured for the Android debug app. Its Email/Password
provider accepts passwordless email links, and the debug SHA-1 and SHA-256 are registered. The
default Hosting site serves `.well-known/assetlinks.json`; the device reports the domain as
verified. The `firebase.properties` file is local and must not be committed. No Admin key belongs
in the APK. A release build needs its own signing SHA hashes and another App Link check.

1. Open the link received at the university email address on the connected Android phone to
   complete the first end-to-end sign-in test. The same link creates an account for a new user.
2. Provision Firestore and Cloud Storage in a chosen region if they are not already available.
   Review current billing requirements before enabling Storage or deploying paid services.
3. Deploy `firebase/firestore.rules`, `firebase/storage.rules` and indexes with an explicit
   `--project lost-and-found-9671c` once those products are provisioned.
4. Deploy the API and worker with `FIREBASE_PROJECT_ID`, `MODEL_PATH` and Application Default
   Credentials for a dedicated service account. Unset all emulator environment variables and
   set `APP_ENV=production`. Use HTTPS for the API URL in the Android configuration.
5. Grant staff custom claims through trusted administrative tooling. The local seed script
   intentionally cannot assign production roles. Users must refresh/sign in again after a role change.
6. Verify Storage access, FCM delivery, API/worker restart recovery and Android report behavior
   before using real reports. The current local `ML_API_BASE_URL=http://127.0.0.1:8000` only
   works on a USB-connected phone with `adb reverse tcp:8000 tcp:8000` and a compatible API
   running on the computer; no such live-project API is running now.

The Dockerfile packages the API and trusted exported artifact. Deploy the worker using the same
image with command `python -m service.worker`. The container recipe is provided but has not been
built locally; local validation uses the Python environment and Firebase emulators.

The code is a working integration baseline, not a finished production operations setup:
add deployment monitoring, rate limiting, pagination, retention/deletion policy and independently
reviewed campus evaluation before a real rollout. Sensitive-ID matching is outside this general-item flow.

Official references: https://firebase.google.com/docs/android/setup,
https://firebase.google.com/docs/auth/admin/verify-id-tokens,
https://firebase.google.com/docs/emulator-suite/connect_auth,
https://firebase.google.com/docs/emulator-suite/connect_firestore.
