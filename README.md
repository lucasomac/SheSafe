# SheSafe: Your Personal Safety Companion

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.12.1-4285F4)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Android API](https://img.shields.io/badge/Android-API%2026+-3DDC84)](https://developer.android.com/)
[![iOS](https://img.shields.io/badge/iOS-16+-000000)](https://developer.apple.com/ios/)
[![Material 3](https://img.shields.io/badge/Material%203-Compose-6750A4)](https://m3.material.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Overview

**SheSafe** is a Kotlin Multiplatform (`Android` & `iOS`) personal safety application built with **Compose Multiplatform (Material 3)** and Clean Architecture (`Presentation → Domain → Data`). It empowers users to manage trusted emergency contacts, customize distress messages, and dispatch emergency SMS alerts enriched with real-time GPS coordinates.

Academic reference: [Monografia (IFSP)](docs/IFSP-SheSafe.pdf)

---

## Architecture & Tech Stack

SheSafe maximizes shared code in `:shared` (`commonMain`) with thin platform hosts (`:androidApp` and `iosApp`):

- **UI & Design System (`commonMain`)**: Compose Multiplatform `1.12.1`, Material 3 `SheSafeTheme`, multiplatform `composeResources` (`Res.string.*`, `Res.drawable.*`, `Res.font.*`), Coil 3 (`3.6.3`), and `@Serializable` type-safe Navigation (`2.10.0`).
- **Presentation (`commonMain`)**: Unidirectional Data Flow (`StateFlow<*UiState>` + `SharedFlow<*Event>`), lifecycle-aware `collectAsStateWithLifecycle()`, and per-destination `koinViewModel()`.
- **Domain (`commonMain`)**: Pure Kotlin entities, `operator fun invoke()` UseCases, `AppEventBus`, and typed `Resource<T>` + `AppError` error hierarchy.
- **Offline-First Data (`commonMain`)**:
  - **Local Persistence**: Room KMP (`2.8.5` + `BundledSQLiteDriver`) for emergency contacts & help request history, and `DataStore<Preferences>` (`1.3.0`) for user settings & custom distress message.
  - **Cloud Sync & Auth**: GitLive Firebase KMP (`2.8.0`: `firebase-auth`, `firebase-firestore`, `firebase-config`) + `KMPAuth` (`GoogleSignIn`).
  - **Emergency SMS Delivery**: Hardened Ktor (`3.5.1`) + Ktorfit (`2.7.2`) HTTP gateways (`SmsDev` / `InfoBip` selected dynamically via Firebase Remote Config) with native `SmsService` (`SmsManager` on Android, `MFMessageComposeViewController` on iOS) as an offline fallback.
- **Platform Adapters (`androidMain` / `iosMain`)**:
  - `EmergencyMapView`: Google Maps Compose (`GoogleMap`) on Android and Apple MapKit (`MKMapView` via `UIKitView`) on iOS.
  - `LocationService` & `rememberLocationPermissionControl`: Fused Location Provider on Android and `CLLocationManager` on iOS.
  - Structured Logging: `Timber` + PII-scrubbing `CrashlyticsTree` on Android and `os_log` on iOS.
- **Dependency Injection**: Koin Multiplatform (`4.2.0`).
- **Quality & Testing**: Detekt static analysis and Fakes-based multiplatform unit tests (`kotlin-test`, `kotlinx-coroutines-test`, `Turbine` — zero mocking frameworks).

---

## Project Structure

```text
SheSafe/
├── shared/
│   └── src/
│       ├── commonMain/
│       │   ├── composeResources/          # Shared strings, drawables, Roboto fonts
│       │   └── kotlin/br/com/lucolimac/shesafe/
│       │       ├── core/                  # Resource/AppError, Room DB, DataStore, Network, Theme, Logging
│       │       ├── feature/
│       │       │   ├── auth/              # Google Sign-In & session observation
│       │       │   ├── home/              # Map + one-tap emergency alert dispatch
│       │       │   ├── contacts/          # Offline-first trusted contacts CRUD
│       │       │   ├── helprequests/      # Alert history & multi-provider SMS gateway
│       │       │   └── profile/           # User profile, settings & custom help message
│       │       ├── navigation/            # @Serializable routes & SheSafeNavHost
│       │       ├── platform/              # Multiplatform Location, SMS & message composer contracts
│       │       └── di/                    # KoinInitializer & aggregated appModules
│       ├── androidMain/                   # GoogleMap, FusedLocation, SmsManager, Room/DataStore builders, Timber
│       ├── iosMain/                       # MKMapView, CLLocationManager, MFMessageCompose, os_log, MainViewController
│       └── commonTest/                    # Fakes + UseCase & ViewModel UDF unit tests
├── androidApp/                            # Thin Android host (SheSafeApplication, MainActivity, R8 rules)
└── iosApp/                                # Thin SwiftUI host (iOSApp.swift, ComposeView.swift)
```

---

## Local Setup & Secrets Configuration

Secrets are injected at build time via `BuildKonfig` (for `:shared`) and `manifestPlaceholders` (for `:androidApp`) and must **never** be committed to Git.

Add the following keys to your untracked root `local.properties` (or export them as environment variables in CI):

```properties
MAPS_API_KEY=your_google_maps_android_api_key
GOOGLE_WEB_CLIENT_ID=your_oauth_web_client_id.apps.googleusercontent.com
SMS_DEV_API_KEY=your_smsdev_api_key
INFO_BIP_API_KEY=your_infobip_api_key
```

### Android

1. Place `google-services.json` inside `androidApp/` (for Firebase Auth, Firestore, Remote Config, and Crashlytics).
2. Build and install:
   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

### iOS

1. Copy `iosApp/Config/GoogleSignIn.xcconfig.example` to `iosApp/Config/GoogleSignIn.local.xcconfig` and fill in your iOS OAuth client IDs.
2. Add `GoogleService-Info.plist` to the `iosApp` target in Xcode (git-ignored).
3. In Xcode (`iosApp/iosApp.xcodeproj`), ensure Swift Package Manager links `firebase-ios-sdk` (`FirebaseCore`, `FirebaseAuth`, `FirebaseFirestore`, `FirebaseRemoteConfig`) alongside `GoogleSignIn-iOS`. `iOSApp.swift` automatically calls `FirebaseApp.configure()` when `FirebaseCore` is linked.

---

## Verification Commands

```bash
# Run multiplatform unit tests (UseCases + ViewModels)
./gradlew :shared:testDebugUnitTest

# Run Detekt static analysis across shared & androidApp
./gradlew detekt

# Compile iOS Simulator target & assemble Android Debug APK
./gradlew :shared:compileKotlinIosSimulatorArm64 :androidApp:assembleDebug
```

---

## License

This project is licensed under the [MIT License](LICENSE).