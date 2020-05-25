package com.example.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.fragment.app.Fragment
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startCamera()
        imageViewCapture.setOnClickListener { takePicture() }
    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {

                cameraProvider.unbindAll()

                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera?.cameraInfo))

            } catch (exc: Exception){
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePicture() {
        imageCapture?.takePicture(ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageCapturedCallback(){

            @SuppressLint("UnsafeExperimentalUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                imageView.setImageBitmap(image.image?.toBitmap())
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }
        })


    }

    companion object {
        private const val TAG = "CameraApp"
    }
}