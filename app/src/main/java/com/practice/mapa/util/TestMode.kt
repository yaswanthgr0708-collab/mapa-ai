package com.practice.mapa.util

import android.content.Context
import android.provider.Settings

/**
 * Central switch that flips the app into "automation-friendly" mode.
 *
 * Three ways it turns on, in priority order:
 *   1. BuildConfig.APPIUM_MODE — set at build time via `-PappiumMode=true`
 *   2. System property `debug.mapa.appium_mode` — set on device via
 *        adb shell setprop debug.mapa.appium_mode true
 *   3. Developer-options "animator duration scale" is 0 (a common
 *      Appium-prep step) — we infer it and behave accordingly.
 *
 * When enabled, the app:
 *   - Disables in-app animations (fragment transitions, snackbar enter/exit)
 *   - Skips the splash/onboarding (Phase 2+)
 *   - Pre-seeds catalog/cart data deterministically (Phase 3+)
 */
object TestMode {

    @Volatile
    var isEnabled: Boolean = false
        private set

    fun init(context: Context) {
        val buildFlag = com.practice.mapa.BuildConfig.APPIUM_MODE
        val sysProp = runCatching {
            val clazz = Class.forName("android.os.SystemProperties")
            val get = clazz.getMethod("get", String::class.java, String::class.java)
            (get.invoke(null, "debug.mapa.appium_mode", "false") as String).toBoolean()
        }.getOrDefault(false)

        val animScale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )

        isEnabled = buildFlag || sysProp || animScale == 0f
    }
}
