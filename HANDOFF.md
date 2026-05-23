# MAPA — Claude Code Handoff Document

> **You are Claude Code, picking up an in-progress Android project from a planning session that happened in Claude.ai. This document is your complete brief. Read it top to bottom before touching code.**

---

## 0. Quick Start — Do These In Order

1. Read this entire document. It is the source of truth for the project.
2. Run `ls -la` and confirm the Phase 1 project skeleton exists in the current directory (you should see `app/`, `gradle/`, `settings.gradle.kts`, etc.).
3. Verify Phase 1 builds cleanly:
   ```bash
   ./gradlew assembleDebug
   ```
   If the `gradlew` script doesn't exist yet, generate it: `gradle wrapper --gradle-version 8.11.1`. If Gradle isn't installed system-wide, use Android Studio's bundled one or install via SDKMAN.
4. Verify the user has a Pixel 10 (Pro) XL connected:
   ```bash
   adb devices
   ```
   If empty, tell the user to plug in and enable USB debugging.
5. Install and launch:
   ```bash
   ./gradlew installDebug
   adb shell am start -n com.practice.mapa/.MainActivity
   ```
   Confirm with the user that they see the 5-tab bottom navigation and the side drawer.
6. **Only after Phase 1 is verified green**, proceed to Section 8 (Phase 2 Kickoff).

---

## 1. Project Context

### What MAPA is
MAPA (Mobile Automation Practice App) is a **native Android (Kotlin) "kitchen-sink" application built specifically as a deterministic, well-instrumented practice ground for Appium mobile automation**. It is not a real product. Every interactive element will carry a stable `resource-id` and `content-description` so that Appium Inspector reveals a clean, learnable UI tree.

### Who the user is
A test engineer learning Appium. They want a real app to practice on. They are **not** primarily an Android developer — they will rely on you for Android decisions and will be the one running `adb`, building, and installing.

### What success looks like
1. The app builds clean on the user's machine.
2. The app covers every major Appium locator strategy and gesture vocabulary.
3. The app is deterministic — no flakiness from network, animations, or non-seeded data.
4. A starter Appium project (Java + TestNG, Page Object Model) ships alongside the app with sample tests for each epic.
5. Every user story maps 1:1 to at least one Appium test in a traceability matrix.

### Constraints
- **Platform:** Android first (Phase 1–7), iOS port later (out of scope for this engagement).
- **UI toolkit:** XML + ViewBinding. **Do not use Jetpack Compose.** XML gives stable `resource-id`s that Appium Inspector reads cleanly; Compose adds an indirection layer that is a bad first lesson for an Appium learner.
- **Min/target SDK:** minSdk 24, compileSdk/targetSdk 36 (Android 16).
- **Build tools:** AGP 8.9.1, Gradle 8.11.1, Kotlin 2.0.21, JDK 17.

---

## 2. Phase Status & Roadmap

| Phase | Scope | Status |
|---|---|---|
| **1** | Project skeleton: Gradle, manifest, single-Activity + Nav Component, 5 placeholder fragments, bottom nav, drawer, toolbar, `TestMode` util | ✅ **DONE** — verify it builds & launches before Phase 2 |
| **2** | Epic A (Auth) + Epic B (Navigation polish) | ⏳ NEXT |
| **3** | Epic C (Catalog) + Epic D (Cart & Checkout) | Pending |
| **4** | Epic E (Forms) + Epic F (Gestures) + Epic G (Alerts) | Pending |
| **5** | Epic H (WebView) + Epic I (Settings) + Epic J (Permissions) | Pending |
| **6** | Appium project skeleton (Java + TestNG) with Page Object Model + sample tests | Pending |
| **7** | Full traceability matrix + zero-defect test run | Pending |

**Rule:** finish a phase entirely (code + manual smoke test + commit) before starting the next. Each phase ends with a working APK installed on the user's device.

---

## 3. Architecture Decisions (Locked)

These were debated and decided during the planning session. **Do not relitigate without an explicit user request.**

| Concern | Decision | Why |
|---|---|---|
| Language | Kotlin 1.9+ | Standard, null-safe |
| UI toolkit | XML layouts + ViewBinding | Stable `resource-id`s; clean Appium tree |
| Architecture | MVVM, single-Activity + Fragments | Standard Android; one fragment ↔ one Appium page object |
| Navigation | Jetpack Navigation Component (XML graph) | Deep links for free |
| DI | Hilt — **add in Phase 2** when first ViewModel is needed | Phase 1 doesn't need it; keep skeleton minimal |
| Local data | Room + DataStore — **add in Phase 3** | Phase 2 auth uses in-memory user table; Phase 3 needs catalog persistence |
| Async | Coroutines + Flow | Built-in |
| Image loading | Coil — **add in Phase 3** | Catalog needs images |
| Network | None. All data is local & in-memory or Room-backed | Eliminates network flakiness |

### Single-module choice
We use a single `app` module in v1. **Do not split into feature modules.** It adds Gradle complexity without benefit for a single-target practice app.

### `compileSdk = 36`
This requires AGP 8.9.0+ and Gradle 8.11.1+. Both are locked in `gradle/libs.versions.toml` and `gradle/wrapper/gradle-wrapper.properties`. If the user is on an older Android Studio that complains, the fix is to upgrade Studio (Meerkat 2024.3.1+), not to downgrade these versions.

---

## 4. Locator & Naming Conventions

**This is the most important section. Locator instability is the #1 source of flaky Appium suites. We avoid that by convention, not by retrofit.**

### 4.1 `android:id` naming

Pattern: `<screen>_<element-type>_<purpose>`

```
login_input_username
login_input_password
login_button_submit
login_button_forgot
login_text_error
home_recycler_products
catalog_item_title
catalog_item_price
cart_button_checkout
cart_row_swipe
forms_picker_date
forms_seekbar_volume
```

- All lowercase, snake_case.
- The `<screen>` prefix matches the fragment's package (`login`, `home`, `catalog`, `cart`, `forms`, `gestures`, `alerts`, `webview`, `settings`, `profile`).
- The `<element-type>` is one of: `input`, `button`, `text`, `image`, `recycler`, `row`, `item`, `picker`, `spinner`, `radio`, `checkbox`, `switch`, `seekbar`, `webview`, `dialog`, `toolbar`, `nav`, `drawer`.

### 4.2 `android:contentDescription` (accessibility id)

**Every interactive element gets a `contentDescription` matching its semantic purpose.** This is what Appium reads as `accessibility id` — the most stable locator strategy.

```xml
<Button
    android:id="@+id/login_button_submit"
    android:contentDescription="login_button_submit"
    android:text="@string/login_submit" />
```

Yes, the `id` and `contentDescription` are the same string. That's intentional — it lets the user practice with either locator strategy on the same element.

### 4.3 List items get indexed accessibility ids

For RecyclerView rows, set `contentDescription` dynamically in the adapter using the item's position:

```kotlin
override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
    holder.itemView.contentDescription = "product_item_$position"
    holder.title.contentDescription = "product_item_${position}_title"
    holder.price.contentDescription = "product_item_${position}_price"
}
```

This lets Appium find "the 3rd product" with `accessibility id = product_item_2`, far more stable than xpath.

### 4.4 What we deliberately don't do

- No `View.generateViewId()` on anything testable.
- No anonymous click listeners on container views that swallow taps without exposing their purpose.
- No invisible overlays.
- No identical `contentDescription`s on multiple visible elements.
- No xpath-only locators — every actionable element must be findable by `id` or `accessibility id`.

### 4.5 Test-mode flag

The app reads `BuildConfig.APPIUM_MODE` (set via `-PappiumMode=true` at build time) and the `debug.mapa.appium_mode` system property (set via `adb shell setprop ...`). When on:
- In-app animations disabled (fragment transitions instant)
- Splash/onboarding skipped
- Catalog/cart pre-seeded with deterministic data
- Network/delay simulators use a fixed clock

The `TestMode` singleton (already at `app/src/main/java/com/practice/mapa/util/TestMode.kt`) is the entry point. Read its `isEnabled` property anywhere you'd otherwise check for animations or seeded state.

---

## 5. Test Data Contracts

These users and behaviors are **hard contracts**. Appium tests will assert against them. Do not change usernames, passwords, error strings, or delay values without flagging.

### 5.1 Seeded users

| Username | Password | Behavior |
|---|---|---|
| `standard_user` | `password123` | Normal happy-path login |
| `locked_user` | `password123` | Login returns error `This account is locked` |
| `slow_user` | `password123` | Login succeeds after a **3-second** artificial delay |
| `error_user` | `password123` | Login fails with toast `Something went wrong. Try again.` |
| `empty_user` | `password123` | Logs in, but Catalog/Cart/Profile show empty states |

Implement in a `SeedUsers.kt` object in the `data/` package. The auth repository checks this object first.

### 5.2 Catalog products (Phase 3)

100 products, ids 1–100. Categories: `Electronics`, `Clothing`, `Books`, `Home` (25 each). Prices range $5–$499. Names follow `${Category} Product ${id}` for deterministic test assertions.

### 5.3 Error message strings

All user-facing error/status strings live in `strings.xml`. Tests will reference them. **Do not hard-code error strings in Kotlin.**

---

## 6. Code Conventions

- **Package layout:** `com.practice.mapa.{ui.<screen>, data, di, util}`. One fragment + one ViewModel per screen, in the same package.
- **Naming:** Fragments end in `Fragment`, ViewModels in `ViewModel`, repositories in `Repository`, DAOs in `Dao`.
- **ViewBinding:** Always. Never `findViewById`.
- **Strings:** Always in `strings.xml`. Never hard-coded in layouts or Kotlin.
- **Dimens:** Use `dp` and `sp`. No raw pixels.
- **Comments:** Comment the *why*, not the *what*. Where a choice is Appium-driven (e.g. "we use a stable string here so the test can assert it"), say so.
- **Commits:** One commit per logical unit. Suggested message format: `phase2: implement login screen with validation`.

---

## 7. Full PRD with User Stories

> *Format:* `US-<EpicID><n>` · As a [role], I want [capability], so that [benefit].
> Acceptance criteria use Given/When/Then.

### Epic A — Authentication

**US-A1 · Successful Login**
As a registered user, I want to log in with valid credentials so that I can access the app's main features.

- **AC1 (Happy):** Given I'm on the Login screen, When I enter `standard_user` / `password123` and tap **Login**, Then I land on Home and see text `Welcome, standard_user`.
- **AC2 (Invalid):** Given any wrong credentials, When I tap **Login**, Then I see inline error `Invalid username or password` under the password field, and remain on the Login screen.
- **AC3 (Empty):** Given both fields empty, When I tap **Login**, Then I see `Username required` and `Password required` inline errors.
- **AC4 (Locked):** Given `locked_user`, When I attempt login, Then I see error `This account is locked`.
- **AC5 (Slow):** Given `slow_user`, When I tap **Login**, Then a progress spinner appears for ~3 s before Home loads.

**US-A2 · Register New Account**
As a new user, I want to register so that I can have a personal account.

- **AC1:** Form requires: full name, email, password, confirm password, date of birth (date picker), country (spinner), accept-terms (checkbox).
- **AC2:** Confirm-password mismatch shows inline error `Passwords do not match`.
- **AC3:** Submit with terms unchecked shows snackbar `You must accept the terms`.
- **AC4:** Successful submit returns to Login with snackbar `Account created — please log in`.

**US-A3 · Forgot Password**
- **AC1:** Tapping `Forgot Password?` opens an AlertDialog with one email field, **Send** and **Cancel** buttons.
- **AC2:** Empty email + Send → dialog stays open, field shows error `Enter your email`.
- **AC3:** Valid email + Send → dialog dismisses, toast `Reset link sent to <email>`.

**US-A4 · Logout**
- **AC1:** Tapping **Logout** in the side drawer opens confirm dialog `Are you sure?`.
- **AC2:** Confirm returns to Login screen with all input fields cleared.

### Epic B — Navigation Shell

**US-B1 · Bottom-Nav Switching** — Five tabs (Home, Catalog, Cart, Forms, Profile) switch fragments without re-launching the activity. Selected tab is highlighted. Toolbar title updates.

**US-B2 · Side Drawer** — Opens from left-edge swipe or hamburger icon. Closes on outside tap or back press.

**US-B3 · Deep Link to Product** — `adb shell am start -W -a android.intent.action.VIEW -d "mapa://product/42"` launches directly to product 42's detail screen.

### Epic C — Product Catalog

**US-C1 · View Catalog** — 20 items render initially in a RecyclerView with image, title, price. Infinite scroll loads next 20 (up to 100).

**US-C2 · Toggle Grid/List** — Toolbar icon toggles between 1-column list and 2-column grid; state persists across tab switches.

**US-C3 · Search Products** — Search bar filters list after 300 ms debounce. Empty-state text `No products match "<query>"` when no hits.

**US-C4 · Filter Products** — Filter icon opens bottom sheet with category checkboxes (Electronics, Clothing, Books, Home) and price-range slider (0–500). Apply re-renders list; active filters shown as chip row.

**US-C5 · Product Detail** — Tapping item opens detail with image, title, price, description, **Add to cart** button. Image supports pinch-to-zoom. Back returns to catalog at the same scroll position.

### Epic D — Cart & Checkout

**US-D1 · Add to Cart** — **Add to cart** shows snackbar `Added to cart` with **Undo** action. Cart tab badge increments.

**US-D2 · Quantity Stepper** — **+** / **−** on cart rows. At quantity 1, **−** is `enabled=false` so Appium can assert state.

**US-D3 · Swipe to Delete** — Left-swipe on cart row reveals red **Delete** background; release past 50% removes with undo snackbar.

**US-D4 · Checkout (3-step)** — Step 1 Address, Step 2 Payment (16-digit number masked as `•••• •••• •••• 1234`), Step 3 Review with **Place Order**. Validation prevents advancing with empty required fields.

**US-D5 · Order Confirmation** — Shows order id `ORD-XXXXXX` (deterministic — derived from a seeded random or counter).

### Epic E — Forms Playground

**US-E1** — All input types: text, number, email, password (with show/hide eye), multiline.
**US-E2** — Date picker + time picker; selected values render as `YYYY-MM-DD` and `HH:mm`.
**US-E3** — Spinner of 10 countries.
**US-E4** — Radio group, single-select, 3 options.
**US-E5** — Checkbox multi-select; summary text shows `Selected: A, C`.
**US-E6** — Three labeled switches with state-text nodes.
**US-E7** — SeekBar 0–100, real-time value text.
**US-E8** — Submit validates all and shows results screen listing every value entered.

### Epic F — Gestures Playground

**US-F1** — Tap, double-tap, long-press buttons each increment own counter.
**US-F2** — Swipe-direction detector: center box shows `Last swipe: <direction>` after 4-directional swipe.
**US-F3** — Drag-and-drop reorderable 5-item list.
**US-F4** — Pinch-to-zoom image; scale value shown as text.
**US-F5** — 100-item list; item 87 carries a unique `resource-id` for scroll-to-element practice.

### Epic G — Alerts & Dialogs
**US-G1** Native AlertDialog OK/Cancel · **US-G2** Bottom sheet with 3 actions · **US-G3** Snackbar with Undo · **US-G4** Toast · **US-G5** Full-screen custom dialog.
Each triggered by a dedicated button on the Alerts screen.

### Epic H — WebView

**US-H1 · WebView Form** — Embeds local HTML (`file:///android_asset/form.html`) with name input, email input, submit button. Submitting shows result text inside the WebView. **Test requires switching from `NATIVE_APP` to `WEBVIEW_*` context.**

### Epic I — Settings
**US-I1** Dark/light theme toggle (recreates activity) · **US-I2** Language selector (English/French/Spanish — strings only) · **US-I3** Notification preference switches (3) · **US-I4** Clear data button with confirm.

### Epic J — Permissions & System
**US-J1** Camera permission request — triggers the real Android runtime permission dialog (assertable via UIAutomator2 in Appium) · **US-J2** Share intent → system share sheet · **US-J3** In-app notification trigger.

---

## 8. Phase 2 Kickoff — Start Here After Phase 1 Is Verified

### Goal
Implement Epic A (Authentication) end-to-end. By the end of Phase 2:
- Login is the launch screen (not Home).
- All five seeded users behave per Section 5.1.
- Register, Forgot Password, and Logout flows work per Epic A user stories.
- The drawer surfaces Logout.
- Successful login navigates into the existing bottom-nav shell (Home, Catalog, Cart, Forms, Profile).

### Concrete tasks

1. **Add Hilt** to the build:
   - Add Hilt plugin to `libs.versions.toml` and root `build.gradle.kts`.
   - Apply to `app/build.gradle.kts`.
   - Annotate `MapaApplication` with `@HiltAndroidApp`.

2. **Create `data/` package:**
   - `SeedUsers.kt` — object holding the 5 hard-coded users (Section 5.1).
   - `User.kt` — data class.
   - `AuthRepository.kt` — exposes `suspend fun login(username: String, password: String): AuthResult` where `AuthResult` is a sealed class `Success(user) | InvalidCredentials | AccountLocked | ServerError | Empty`.
   - `SessionManager.kt` — DataStore-backed; remembers whether a user is logged in across app restarts.

3. **Create `ui/auth/` package:**
   - `LoginFragment.kt` + `fragment_login.xml` — username/password inputs, submit button, forgot-password link, register link. Wire to `LoginViewModel`.
   - `RegisterFragment.kt` + `fragment_register.xml` — multi-field form per US-A2 AC1.
   - `ForgotPasswordDialog.kt` — DialogFragment per US-A3.
   - `LoginViewModel.kt` and `RegisterViewModel.kt`.

4. **Update navigation:**
   - In `nav_graph.xml`, add `loginFragment` and `registerFragment` destinations.
   - Make Login the start destination.
   - Add `<action>` from Login → Home on successful auth, with `popUpTo="loginFragment"` and `popUpToInclusive="true"` (so back from Home doesn't return to Login).

5. **Update `MainActivity`:**
   - Hide the bottom nav and toolbar when on Login/Register destinations. Use `NavController.OnDestinationChangedListener`.

6. **Add logout to drawer:**
   - Update `drawer_menu.xml` with a Logout item.
   - Wire in `MainActivity` to show confirm dialog, then navigate back to Login and clear `SessionManager`.

7. **Apply locator conventions strictly.** Every input, button, error text, and dialog button must have both an `android:id` (snake_case, per Section 4.1) and a matching `android:contentDescription` (Section 4.2).

8. **Add strings to `strings.xml`** — every visible string, especially error messages per Section 5.3. Tests will assert against these exact strings.

### Definition of Done (Phase 2)

- `./gradlew assembleDebug` succeeds with zero warnings about deprecated APIs.
- The user can launch the app and see Login as the first screen.
- All 5 acceptance criteria of US-A1 pass via manual smoke test.
- US-A2, US-A3, US-A4 acceptance criteria pass via manual smoke test.
- Inspecting any auth screen with Appium Inspector (`appium-inspector`) shows clean, named `resource-id`s and `content-desc`s.
- Code committed with a meaningful message.

### Don't do these in Phase 2
- Don't touch the Catalog/Cart/Forms/Profile stubs. They stay placeholders until Phase 3.
- Don't add Room yet — auth is in-memory.
- Don't add image loading.
- Don't try to be clever with Compose interop or feature modules.

---

## 9. Operational Commands Reference

### Build & install
```bash
./gradlew assembleDebug                            # Build debug APK
./gradlew installDebug                             # Build + install
./gradlew assembleDebug -PappiumMode=true          # Build with Appium mode baked in
./gradlew clean                                    # Wipe build cache
```

### Device & app
```bash
adb devices                                        # List connected devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.practice.mapa/.MainActivity
adb shell am force-stop com.practice.mapa
adb shell pm clear com.practice.mapa               # Reset app to first-launch state
```

### Logs
```bash
adb logcat -s MapaApplication                      # App-tagged logs only
adb logcat *:E                                     # Errors across the device
adb logcat | grep -i mapa                          # Anything mentioning MAPA
```

### Test-mode toggling without rebuild
```bash
adb shell setprop debug.mapa.appium_mode true
adb shell am force-stop com.practice.mapa
adb shell am start -n com.practice.mapa/.MainActivity
```

### Deep-link testing
```bash
adb shell am start -W -a android.intent.action.VIEW -d "mapa://product/42"
```

### Appium server (Phase 6)
```bash
appium                                             # Default: 127.0.0.1:4723
appium-inspector                                   # GUI for inspecting UI tree
```

---

## 10. Definition of Done per Phase

For every phase the bar is the same:

1. **Builds clean.** `./gradlew assembleDebug` produces an APK with no errors and no new warnings.
2. **Installs and runs** on the user's Pixel 10 XL.
3. **Manual smoke test passes** for every acceptance criterion in scope for that phase.
4. **Appium Inspector check** — open `appium-inspector`, point at the app, walk through the new screens, confirm every interactive element has a sensible `resource-id` and `content-desc`. Document any element that intentionally has neither (rare).
5. **Strings localised** — every visible string in `strings.xml`.
6. **Committed** with a phase tag in the message.

When all six are true, **stop and tell the user the phase is done.** Don't roll into the next phase without an explicit go-ahead — the user may want to inspect with Appium first, or change priorities.

---

## 11. When You Get Stuck

- **Build error you don't understand:** paste the full Gradle output and ask the user before guessing. Don't downgrade versions to "make it work" — versions are locked in Section 3.
- **An acceptance criterion is ambiguous:** ask the user. Don't invent behavior.
- **A locator choice is non-obvious:** default to the convention in Section 4. If the convention truly doesn't fit, ask.
- **Phase is bigger than you expected:** break it into sub-phases and confirm the split with the user before plowing through.

---

*End of HANDOFF.md — version 1.0*
