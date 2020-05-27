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
        currentMode = when (angle) {
            in 46..135 -> ORIENTATION_LANDSCAPE_REVERSE
            in 136..225 -> ORIENTATION_PORTRAIT_REVERSE
            in 226..315 -> ORIENTATION_LANDSCAPE
            else -> ORIENTATION_PORTRAIT
        }
        if (currentMode != lastMode) {

            val lastAngle = when (lastMode) {
                ORIENTATION_PORTRAIT -> if (currentMode == ORIENTATION_LANDSCAPE_REVERSE) 360 else 0
                ORIENTATION_LANDSCAPE -> 90
                ORIENTATION_PORTRAIT_REVERSE -> 180
                ORIENTATION_LANDSCAPE_REVERSE -> 270
            }
            val currentAngle = when (currentMode) {
                ORIENTATION_PORTRAIT -> if (lastMode == ORIENTATION_LANDSCAPE) 0 else 360
                ORIENTATION_LANDSCAPE -> 90
                ORIENTATION_PORTRAIT_REVERSE -> 180
                ORIENTATION_LANDSCAPE_REVERSE -> 270
            }
            listener(lastAngle, currentAngle)
            lastMode = currentMode
        }
    }
}

enum class OrientationMode {
    ORIENTATION_PORTRAIT,
    ORIENTATION_LANDSCAPE,
    ORIENTATION_PORTRAIT_REVERSE,
    ORIENTATION_LANDSCAPE_REVERSE
}