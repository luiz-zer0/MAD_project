# Strathmore Lost and Found Android app

This repository contains the Android client for the Strathmore Lost and Found project. It uses Firebase Authentication and the authenticated API, Firestore data model, and matching worker in [SemProject_MLEngine](https://github.com/nimrod-kobia/SemProject_MLEngine).

The app presents possible lost/found matches for staff and users to review. A match score is evidence for human review; it does not prove ownership or authorize release.

## Connected workflow

- Users sign in through Firebase email links. Staff and admin roles come from server-assigned Firebase claims.
- Students submit lost reports. Office staff record found items and review claims.
- Reports are persisted through the MLEngine `/v1` API. A separate worker uses TF-IDF text retrieval and knowledge agents to rank possible matches.
- The score shown in the app is lexical text similarity, not a calibrated ownership probability. The offline Logistic Regression coursework experiment is not loaded by this worker.
- Photo uploads are disabled in the current Firebase project because Storage is not available for this demo. Remove a locally saved photo from the draft before submitting.
- Some sample screens remain as previews and are not connected to the live service.

## Run and build

Open this repository root in Android Studio. For the local emulator workflow, follow [FIREBASE_SETUP.md](FIREBASE_SETUP.md) to start the MLEngine Firebase emulators, API, and worker. Then build the demo APK from PowerShell:

```powershell
.\gradlew.bat -PdemoFirebase=true testDebugUnitTest assembleDebug
```

The demo build uses `http://10.0.2.2:8000`, which reaches the host computer from an Android emulator. A physical device needs a reachable development API URL and Firebase configuration. See [PRESENTATION_DEMO.md](PRESENTATION_DEMO.md) for the walkthrough and known limits.

## Validation status

The Android demo build and unit tests passed on 24 November 2026. The service and Firebase worker smoke check was run separately. A full phone walkthrough is still pending; the local emulator was unavailable during the last demo attempt.

For the coursework datasets, notebook, and offline classifier, see the [SemProject_MLEngine repository](https://github.com/nimrod-kobia/SemProject_MLEngine). Its constructed-pair metrics are academic experiments and do not represent real campus matching accuracy.
