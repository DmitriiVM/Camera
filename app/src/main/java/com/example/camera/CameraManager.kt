package com.example.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.camera.util.flip
import com.example.camera.util.toBitmap
import java.util.concurrent.TimeUnit

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
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            imageCapture = ImageCapture.Builder().setFlashMode(flashMode).build()

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

        }, ContextCompat.getMainExecutor(context))
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

    fun setFlash(flashMode: Int) {
        this.flashMode = flashMode
        startCamera()
    }

    companion object {
        private const val TAG = "CameraApp"
    }
}