package com.example.camera.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.camera.R
import kotlinx.android.synthetic.main.layout_dialog.view.*
import java.io.File

private const val EXTENSION = ".jpg"

fun createFile(context: Context, name: String): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.resources.getString(R.string.app_name)).apply {
            mkdirs()
        }
    }
    return File(mediaDir, "$name$EXTENSION")
}

fun showDialog(activity: Activity, listener: (fileName: String) -> Unit) {
    val dialogView = activity.layoutInflater.inflate(R.layout.layout_dialog, null)
    AlertDialog.Builder(activity)
        .setView(dialogView)
        .setTitle(activity.getString(R.string.save_dialog_title))
        .setNegativeButton(activity.getString(R.string.save_dialog_negative_button), null)
        .setPositiveButton(activity.getString(R.string.save_dialog_positive_button)) { _, _ ->
            listener(dialogView.editText.text.toString())
        }
        .show()
}