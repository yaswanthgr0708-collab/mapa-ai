# MAPA — Mobile Automation Practice App

A native Android (Kotlin) "kitchen-sink" app built specifically as an Appium
automation practice ground. This README covers Phase-1 setup.

## What Phase 1 ships

A buildable, installable shell with:

- Single Activity (`MainActivity`) hosting a Navigation Component graph
- Bottom navigation with 5 tabs (Home, Catalog, Cart, Forms, Profile)
- Side drawer with placeholder entries
- Toolbar wired up to the nav graph
- A `TestMode` singleton that detects "Appium mode" three ways
- A `BuildConfig.APPIUM_MODE` flag, settable via Gradle property

All 5 tabs currently render a placeholder. Real features land in Phases 2–5.

---

## Prerequisites

| Tool | Version |
|---|---|
| JDK | 17 (required by AGP 8.x) |
| Android Studio | Meerkat (2024.3.1) or newer |
| Android SDK | Platform 36 (Android 16) + Build-Tools 34+ |
| Device | Pixel 10 (Pro) XL with USB debugging on, OR an emulator |

Confirm Java 17:
```bash
java -version
# openjdk version "17.x.x" ...
```

---

## First-time setup

### 1. Open in Android Studio

```
File → Open → select this folder (the one with settings.gradle.kts)
```

Studio will offer to:
- Download the Gradle wrapper jar (accept)
- Sync the project (accept)
- Install any missing SDK platforms (accept — you want API 36)

If Studio doesn't offer to create the wrapper jar, run from terminal:

```bash
# Requires a Gradle installation on PATH, or use Studio's bundled one
gradle wrapper --gradle-version 8.11.1
```

### 2. Plug in the Pixel 10 XL

On the phone:
```
Settings → About phone → tap Build number 7 times to unlock Developer Options
Settings → System → Developer options → USB debugging: ON
```

Confirm the device shows up:
```bash
adb devices
# List of devices attached
# 1A2B3C4D5E6F     device
```

If it says `unauthorized`, accept the RSA prompt on the phone.

### 3. Build & install the debug APK

From the project root:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

OR straight from Studio: click the green **Run** button.

### 4. Launch and verify

```bash
adb shell am start -n com.practice.mapa/.MainActivity
```

Expected: app opens to the Home tab showing the text **"Home"**.
Tap each bottom-nav icon — the title text changes to **Catalog / Cart / Forms / Profile**.
Swipe from the left edge — drawer opens.

If you see all of that: Phase 1 is green. ✅

---

## Building with Appium mode

To pre-arm the app for automation (animations off, splash skipped, deterministic data):

```bash
./gradlew assembleDebug -PappiumMode=true
./gradlew installDebug -PappiumMode=true
```

OR toggle at runtime without rebuilding:

```bash
adb shell setprop debug.mapa.appium_mode true
adb shell am force-stop com.practice.mapa
adb shell am start -n com.practice.mapa/.MainActivity
```

Check the logcat to confirm:

```bash
adb logcat -s MapaApplication
# I MapaApplication: APPIUM_MODE is ON — animations off, splash skipped, seeded data
```

---

## Appium capabilities (preview — full version ships in Phase 6)

```json
{
  "platformName": "Android",
  "appium:platformVersion": "16",
  "appium:deviceName": "Pixel 10 XL",
  "appium:udid": "<run `adb devices` and paste your UDID here>",
  "appium:automationName": "UiAutomator2",
  "appium:appPackage": "com.practice.mapa",
  "appium:appActivity": "com.practice.mapa.MainActivity",
  "appium:noReset": false,
  "appium:autoGrantPermissions": true,
  "appium:newCommandTimeout": 120
}
```

---

## Project layout (Phase 1)

```
mapa-android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/practice/mapa/
│       │   ├── MainActivity.kt
│       │   ├── MapaApplication.kt
│       │   ├── ui/
│       │   │   ├── common/PlaceholderFragment.kt
│       │   │   ├── home/HomeFragment.kt
│       │   │   ├── catalog/CatalogFragment.kt
│       │   │   ├── cart/CartFragment.kt
│       │   │   ├── forms/FormsFragment.kt
│       │   │   └── profile/ProfileFragment.kt
│       │   └── util/TestMode.kt
│       └── res/  (layouts, themes, strings, drawables, nav-graph, menus)
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradle/libs.versions.toml
```

---

## Troubleshooting

| Symptom | Fix |
|---|---|
| `Could not resolve com.android.application:8.9.1` | Update Android Studio to Meerkat (2024.3.1)+ — AGP 8.9 requires it |
| `Unsupported class file major version 65` | You're on JDK 21+; install JDK 17 and point `JAVA_HOME` at it |
| `compileSdk 36` red-underlined | Open SDK Manager → install "Android 16 (Baklava)" |
| `INSTALL_FAILED_USER_RESTRICTED` | Phone is blocking USB install. Settings → Developer options → "Install via USB" ON |
| App opens but back-stack is weird | Clear data: `adb shell pm clear com.practice.mapa` |

---

## Next phases

- **Phase 2** — Auth (Login/Register/Forgot/Logout) + Navigation shell polish
- **Phase 3** — Catalog + Cart + Checkout
- **Phase 4** — Forms playground + Gestures + Alerts
- **Phase 5** — WebView + Settings + Permissions
- **Phase 6** — Appium project skeleton (Java + TestNG) with POM
- **Phase 7** — Traceability matrix + zero-defect test runs
