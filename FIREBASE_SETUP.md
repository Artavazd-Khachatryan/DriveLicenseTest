# Firebase Crashlytics setup

The app uses [GitLive Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk) for crash reporting on **Android** and **iOS**.

## 1. Create a Firebase project

1. Open [Firebase Console](https://console.firebase.google.com/).
2. Create a project (or use an existing one).
3. Add an **Android** app with package name `com.drive.license.test`.
4. Add an **iOS** app with bundle ID `com.drive.license.test.DriveLicenseTest`.

## 2. Replace config files

Copy the downloaded files over the placeholders:

| Platform | File |
|----------|------|
| Android | `androidApp/google-services.json` (see `google-services.json.example`) |
| iOS | `iosApp/iosApp/GoogleService-Info.plist` (see `GoogleService-Info.plist.example`) |

`GoogleService-Info.plist` is bundled via the **iosApp** Xcode target. Firebase iOS SDK is linked through Swift Package Manager (`https://github.com/firebase/firebase-ios-sdk`): **FirebaseCore**, **FirebaseCrashlytics**, and **FirebaseAnalytics**.

## 3. Enable Crashlytics

In Firebase Console → Project settings → Integrations, enable **Crashlytics** for both apps.

## 4. Verify

Build and run a release-like build, then trigger a test crash from a debug menu or temporary button and confirm the event in the Crashlytics dashboard within a few minutes.

Initialization runs in `App.kt` via `CrashReporting.initialize()`. On iOS, `FirebaseApp.configure()` also runs in `iOSApp.swift` (`AppDelegate`) at launch per Firebase’s SwiftUI setup guide.

Until real config files are in place, Crashlytics stays **disabled** (placeholder API keys are detected so the app still launches on device/simulator).
