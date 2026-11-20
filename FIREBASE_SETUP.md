# Firebase setup for the Android app

The connected app uses Firebase Authentication for email-link sign-in and a separate FastAPI service for reports, matching, office workflows, and technical administration. The service lives in [SemProject_MLEngine](https://github.com/nimrod-kobia/SemProject_MLEngine); its [Firebase setup guide](https://github.com/nimrod-kobia/SemProject_MLEngine/blob/main/FIREBASE_SETUP.md) is the source of truth for emulators, security rules, service tests, and deployment.

## Android setup

1. Copy `firebase.properties.example` to `firebase.properties`. The example contains the public identifiers for Firebase project `lost-and-found-9671c`. Do not put an Admin SDK key in the APK.
2. Set `ML_API_BASE_URL` to a reachable HTTPS FastAPI endpoint. For a local development phone only, use `http://127.0.0.1:8000` while the service is running and `adb reverse tcp:8000 tcp:8000` is active over USB or wireless ADB.
3. Build with `.\gradlew.bat assembleDebug` on Windows, or Run from Android Studio. Install `app/build/outputs/apk/debug/app-debug.apk` on the device.
4. Sign in with a verified `@strathmore.edu` email link. The account's role comes from a trusted Firebase custom claim. A role change requires signing in again.

Without `firebase.properties`, debug builds use the local emulator configuration. Follow the service guide to start and seed the emulators before running that variant. Release builds never use the emulators.

## Current limits

As of 20 November 2026, the Android app builds and runs on a Samsung phone, and the service and Firebase emulator tests pass. Firebase email-link sign-in and Firestore rules are configured. Storage is not initialized on the current Spark project, so photo upload is unavailable. The FastAPI service and matching worker are not deployed to HTTPS. A local API over ADB reverse supports development on one attached or wirelessly paired phone, but it does not make the app usable on other phones. A production-signed APK and hosted service are required before general distribution.
