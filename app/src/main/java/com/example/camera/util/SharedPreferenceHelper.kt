package com.example.camera.util

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture

object SharedPrefHelper {

    private const val SHARED_PREF = "shared_preference"
    private const val KEY_FLASH_MODE = "key_flash_mode"
    private const val KEY_LENS = "key_lens"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)

    fun saveFlashMode(context: Context, flashMode: Int) =
        getPrefs(context).edit().putInt(KEY_FLASH_MODE, flashMode).apply()

    fun getFlashMode(context: Context): Int =
        getPrefs(context).getInt(KEY_FLASH_MODE, ImageCapture.FLASH_MODE_OFF)

    fun saveLens(context: Context, lens: Int) =
        getPrefs(context).edit().putInt(KEY_LENS, lens).apply()

    fun getLens(context: Context): Int =
        getPrefs(context).getInt(KEY_LENS, CameraSelector.LENS_FACING_BACK)
}