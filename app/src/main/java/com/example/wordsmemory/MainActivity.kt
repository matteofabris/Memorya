package com.example.wordsmemory

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.sqrt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = if (isTablet()) {
            Log.i(Constants.packageName, "Tablet mode")
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            Log.i(Constants.packageName, "Phone mode")
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