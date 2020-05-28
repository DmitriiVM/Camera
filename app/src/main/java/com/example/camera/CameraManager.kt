package com.example.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.camera.util.flip
import com.example.camera.util.toBitmap
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewFinder: PreviewView
) {

    private var imageCapture: ImageCapture? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var flashMode = ImageCapture.FLASH_MODE_OFF

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(Runnable {

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio())
                .build()

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(aspectRatio())
                .setFlashMode(flashMode)
                .build()


            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            try {
                cameraProvider.unbindAll()
                val camera =
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )


                // -----
                viewFinder.setOnTouchListener { v, event ->

                    val cameraControl = camera.cameraControl

                    val factory = SurfaceOrientedMeteringPointFactory(
                        viewFinder.width.toFloat(),
                        viewFinder.height.toFloat()
                    )
                    val point = factory.createPoint(event.x, event.y)
                    val action = FocusMeteringAction.Builder(point)
                        .setAutoCancelDuration(5, TimeUnit.SECONDS)
                        .build()

                    val future = cameraControl.startFocusAndMetering(action)

                    future.addListener(Runnable {
//                        val result = future.get().isFocusSuccessful
                    }, ContextCompat.getMainExecutor(context))

                    v.performClick()
                    return@setOnTouchListener true
                }
                // ------


                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(null))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }


            val orientationEventListener = CustomOrientationEventListener(context){ _, angle ->
                val rotation : Int = when (angle) {
                    0 -> Surface.ROTATION_0
                    90 -> Surface.ROTATION_90
                    180 -> Surface.ROTATION_180
                    else -> Surface.ROTATION_270
                }
                imageCapture!!.targetRotation = rotation
            }
            orientationEventListener.enable()

        }, ContextCompat.getMainExecutor(context))
    }

    private fun aspectRatio(): Int {
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    fun takePicture(onCaptureSuccess: (bitmap: Bitmap) -> Unit) {
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {

                @SuppressLint("UnsafeExperimentalUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {

                    var bitmap = image.image?.toBitmap()
                    image.close()
                    if (bitmap != null) {

                        if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            bitmap = bitmap.flip()
                        }
                        onCaptureSuccess(bitmap)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }


    fun hasFrontCamera(): Boolean {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        return cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
    }

    fun setLens(lensFacing: Int) {
        this.lensFacing = lensFacing
        startCamera()
    }

    fun setFlash(flash: Int) {
        imageCapture?.flashMode = flash
//        this.flashMode = flashMode
//        startCamera()
    }

    companion object {
        private const val TAG = "CameraApp"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}