package com.memorya.utils

import androidx.fragment.app.Fragment
import com.memorya.R
import com.skydoves.balloon.*
import com.skydoves.balloon.overlay.BalloonOverlayShape

fun Fragment.createBalloon(text: String, overlayShape: BalloonOverlayShape): Balloon {
    return Balloon.Builder(requireContext())
        .setWidthRatio(0.7f)
        .setHeight(BalloonSizeSpec.WRAP)
        .setText(text)
        .setTextColorResource(R.color.white)
        .setTextSize(16f)
        .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        .setArrowSize(10)
        .setArrowPosition(0.5f)
        .setPadding(10)
        .setMargin(8)
        .setCornerRadius(8f)
        .setBackgroundColorResource(R.color.palette_color_3)
        .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        .setIsVisibleOverlay(true)
        .setOverlayColorResource(R.color.balloon_overlay_color)
        .setOverlayShape(overlayShape)
        .setOverlayPadding(6f)
        .setBalloonHighlightAnimation(BalloonHighlightAnimation.SHAKE)
        .setAutoDismissDuration(6000)
        .setLifecycleOwner(viewLifecycleOwner)
        .build()
}