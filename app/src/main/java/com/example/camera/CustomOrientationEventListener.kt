package com.example.camera

import android.content.Context
import android.view.OrientationEventListener
import com.example.camera.OrientationMode.*

class CustomOrientationEventListener(
    context: Context,
    private val listener: (lastAngle: Int, currentAngle: Int) -> Unit
) : OrientationEventListener(context) {

    private var lastMode = ORIENTATION_PORTRAIT
    private var currentMode = ORIENTATION_PORTRAIT

    override fun onOrientationChanged(angle: Int) {
        if (angle < 0) return

        if (angle in 0..30) currentMode =  ORIENTATION_PORTRAIT
        if (angle in 60..120) currentMode =  ORIENTATION_LANDSCAPE_REVERSE
        if (angle in 150..210) currentMode =  ORIENTATION_PORTRAIT_REVERSE
        if (angle in 240..300) currentMode =  ORIENTATION_LANDSCAPE
        if (angle in 330..359) currentMode =  ORIENTATION_PORTRAIT

        if (currentMode != lastMode) {

            val lastAngle = when (lastMode) {
                ORIENTATION_PORTRAIT -> if (currentMode == ORIENTATION_LANDSCAPE_REVERSE) ANGLE_360 else ANGLE_0
                ORIENTATION_LANDSCAPE -> ANGLE_90
                ORIENTATION_PORTRAIT_REVERSE -> ANGLE_180
                ORIENTATION_LANDSCAPE_REVERSE -> ANGLE_270
            }
            val currentAngle = when (currentMode) {
                ORIENTATION_PORTRAIT -> if (lastMode == ORIENTATION_LANDSCAPE) ANGLE_0 else ANGLE_360
                ORIENTATION_LANDSCAPE -> ANGLE_90
                ORIENTATION_PORTRAIT_REVERSE -> ANGLE_180
                ORIENTATION_LANDSCAPE_REVERSE -> ANGLE_270
            }
            listener(lastAngle, currentAngle)
            lastMode = currentMode
        }
    }

    companion object {
        private const val ANGLE_0 = 0
        private const val ANGLE_90 = 90
        private const val ANGLE_180 = 180
        private const val ANGLE_270 = 270
        private const val ANGLE_360 = 360
    }
}

enum class OrientationMode {
    ORIENTATION_PORTRAIT,
    ORIENTATION_LANDSCAPE,
    ORIENTATION_PORTRAIT_REVERSE,
    ORIENTATION_LANDSCAPE_REVERSE
}