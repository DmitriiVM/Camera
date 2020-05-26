package com.example.camera.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.media.MediaScannerConnection
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

private const val BITMAP_QUALITY = 100

fun Image.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun File.saveBitmap(bitmap: Bitmap) {
    FileOutputStream(this).apply {
        bitmap.compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, this)
        close()
    }
}

fun File.makeAvailableForScanning(context: Context) {
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    MediaScannerConnection.scanFile(context, arrayOf(absolutePath), arrayOf(mimeType), null)
}

fun Bitmap.flip(): Bitmap {
    val matrix = Matrix().apply { postScale(-1f, 1f, width/2f, width/2f) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}