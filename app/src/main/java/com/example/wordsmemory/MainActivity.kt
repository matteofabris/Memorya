package com.example.wordsmemory

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.wordsmemory.databinding.ActivityMainBinding
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        checkIfIsTablet()

        requestedOrientation = if (Constants.isTablet) {
            Log.i("Orientation", "Tablet mode")
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            Log.i("Orientation", "Phone mode")
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun checkIfIsTablet() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val yInches = metrics.heightPixels / metrics.ydpi
        val xInches = metrics.widthPixels / metrics.xdpi
        val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())
        if (diagonalInches >= 7) {
            Constants.isTablet = true
        }
    }
}