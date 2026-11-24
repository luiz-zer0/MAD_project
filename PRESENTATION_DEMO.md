# Local Lost & Found demo

The connected Android app submits reports to the authenticated API in `SemProject_MLEngine`. The Firestore worker ranks active found reports with TF-IDF retrieval and knowledge agents. Results show ranked possible matches, lexical similarity and an explanation; staff verify ownership before release. The offline version 2 hybrid benchmark is not the deployed model.

Start the Firebase emulators, seed accounts, API and worker with `SemProject_MLEngine/scripts/start_local.ps1` as described in that repository's `FIREBASE_SETUP.md`. Then build in this repository:

```powershell
.\gradlew.bat -PdemoFirebase=true testDebugUnitTest assembleDebug
```

The flag forces the fictional demo project and `http://10.0.2.2:8000` for an Android emulator, even when local live Firebase settings exist. The debug APK is `app/build/outputs/apk/debug/app-debug.apk`. Demo login uses seeded `demo-office@strathmore.edu` and `demo-student@strathmore.edu` with `LocalDemo123!`. The flag does not affect release builds. `10.0.2.2` is specific to an Android emulator; a physical phone needs reachable development hosts.

On 23 November 2026, normal and demo debug builds and unit tests passed. The service HTTP/Firebase/worker smoke passed separately. No phone was connected, and the configured emulator could not start because C: lacked free disk space. Complete the app sign-in → found report → lost report → worker → ranked results walkthrough on a device before claiming a mobile end-to-end demonstration.
