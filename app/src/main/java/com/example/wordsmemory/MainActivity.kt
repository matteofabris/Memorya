package com.example.wordsmemory

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.sqrt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize timber in application class
        Timber.plant(WMDebugTree())

        requestedOrientation = if (isTablet()) {
            Timber.i("Tablet mode")
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            Timber.i("Phone mode")
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    @Suppress("DEPRECATION")
    private fun isTablet(): Boolean {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val yInches = metrics.heightPixels / metrics.ydpi
        val xInches = metrics.widthPixels / metrics.xdpi
        val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())

        if (diagonalInches >= 7) return true
        return false
    }
}