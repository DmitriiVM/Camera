package com.example.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.camera.util.SharedPrefHelper
import com.example.camera.util.flip
import com.example.camera.util.toBitmap
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private var imageCapture: ImageCapture? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var flashMode = ImageCapture.FLASH_MODE_OFF

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startCamera()
        imageViewCapture.setOnClickListener { takePicture() }
        imageViewFlash.setOnClickListener { switchFlash() }
        if (hasFrontCamera()) {
            imageViewSwitchCamera.setOnClickListener { switchCamera() }
            lensFacing = SharedPrefHelper.getLens(requireContext())
        } else {
            imageViewSwitchCamera.isGone = true
        }

        flashMode = SharedPrefHelper.getFlashMode(requireContext())
        if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            imageViewFlash.setImageResource(R.drawable.ic_flash_off)
        } else {
            imageViewFlash.setImageResource(R.drawable.ic_flash_on)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector =
                CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

            val tt = ImageCapture.Metadata()
            tt.isReversedVertical = true

            imageCapture = ImageCapture.Builder()
                .setFlashMode(flashMode)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(null))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePicture() {
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {

                @SuppressLint("UnsafeExperimentalUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {

                    var bitmap = image.image?.toBitmap()
                    image.close()
                    if (bitmap != null) {

                        if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            bitmap = bitmap.flip()
                        }

                        findNavController().navigate(
                            CameraFragmentDirections.actionCameraFragmentToImageFragment(
                                bitmap
                            )
                        )
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }


    private fun hasFrontCamera(): Boolean {
        val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).get()
        return cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
    }

    private fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        SharedPrefHelper.saveLens(requireContext(), lensFacing)
        startCamera()
    }

    private fun switchFlash() {
        flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            imageViewFlash.setImageResource(R.drawable.ic_flash_on)
            ImageCapture.FLASH_MODE_ON
        } else {
            imageViewFlash.setImageResource(R.drawable.ic_flash_off)
            ImageCapture.FLASH_MODE_OFF
        }
        SharedPrefHelper.saveFlashMode(requireContext(), flashMode)
        startCamera()
    }

    fun onVolumeKeyDown() {
        takePicture()
    }

    companion object {
        private const val TAG = "CameraApp"
    }
}