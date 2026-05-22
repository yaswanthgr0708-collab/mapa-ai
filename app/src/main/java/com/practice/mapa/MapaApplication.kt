package com.practice.mapa

import android.app.Application
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatDelegate
import com.practice.mapa.data.SettingsManager
import com.practice.mapa.util.TestMode
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class MapaApplication : Application() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate() {
        super.onCreate()

        // Apply persisted theme before any Activity starts.
        val nightMode = runBlocking { settingsManager.nightMode.first() }
        AppCompatDelegate.setDefaultNightMode(nightMode)

        // Allow Appium / Chrome DevTools to inspect WebView content.
        WebView.setWebContentsDebuggingEnabled(true)

        TestMode.init(this)
        if (TestMode.isEnabled) {
            Log.i(TAG, "APPIUM_MODE is ON — animations off, splash skipped, seeded data")
        }
    }

    companion object {
        private const val TAG = "MapaApplication"
    }
}
