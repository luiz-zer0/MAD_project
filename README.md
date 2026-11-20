# Lost & Found Android app

This repository contains the Kotlin/Jetpack Compose Android app for Strathmore Lost & Found. Its connected flow uses Firebase email-link sign-in and the FastAPI service in [SemProject_MLEngine](https://github.com/nimrod-kobia/SemProject_MLEngine). A text-match suggestion never proves ownership; office staff must review a claim and record the physical handover.

## Build from a fresh checkout

1. Install Android Studio and an Android SDK that supports this project's `compileSdk`. Open this repository in Android Studio.
2. Copy `firebase.properties.example` to `firebase.properties`. The example contains the public Firebase Android app identifiers. Set `ML_API_BASE_URL` to the HTTPS URL of the deployed FastAPI service. Never put a service-account key in the Android app.
3. Build with `./gradlew assembleDebug` (PowerShell: `.\gradlew.bat assembleDebug`) or select **Run** in Android Studio. The APK is at `app/build/outputs/apk/debug/app-debug.apk`.
4. Install the APK on an Android device. Sign in with a verified `@strathmore.edu` address using the email link. Office and technical-admin roles are assigned by trusted Firebase custom claims, never selected in the app. Sign out and in after a role change.

The app initializes Firebase from `firebase.properties`; the local `app/google-services.json` file is not required for Gradle to build. A debug build without `firebase.properties` uses the local Firebase emulators, not the live project. See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for the emulator workflow.

For a development phone connected by USB or wireless ADB, `ML_API_BASE_URL=http://127.0.0.1:8000` works only while `adb reverse tcp:8000 tcp:8000` is active and the service runs on the developer computer. This address does not work on another person's phone by itself. The service must be deployed at a reachable HTTPS URL before distributing a live APK.

## Current delivery boundary

The live Firebase project is `lost-and-found-9671c`. Authentication and Firestore are configured. The FastAPI service and matching worker are not yet hosted, and Firebase Storage photo uploads are not enabled on the current no-billing project. The source builds and the connected UI runs, but report submission, matching, claims, and admin operations require the service. There is no production-signed release APK in this repository yet; the debug APK is for development testing.

`firebase.properties`, `local.properties`, generated build outputs, Android Studio state, and service-account credentials stay out of Git. The Firebase Android API key is a public app identifier; authorization is enforced by Firebase rules and the service's token and role checks.
