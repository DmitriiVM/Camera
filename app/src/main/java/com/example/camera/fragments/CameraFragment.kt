package com.example.camera.fragments

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.camera.CameraManager
import com.example.camera.CameraViewModel
import com.example.camera.CustomOrientationEventListener
import com.example.camera.R
import com.example.camera.util.SharedPrefHelper
import com.example.camera.util.rotate
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var cameraManager: CameraManager
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private lateinit var orientationListener: OrientationEventListener

    private val viewModel by activityViewModels<CameraViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initOrientationListener()
        initCameraManager()
    }

    private fun initOrientationListener() {
        orientationListener =
            CustomOrientationEventListener(requireContext()) { lastAngle, currentAngle ->
                imageViewCapture.rotate(lastAngle, currentAngle)
                imageViewFlash.rotate(lastAngle, currentAngle)
                imageViewSwitchCamera.rotate(lastAngle, currentAngle)
            }
        orientationListener.enable()
    }

    private fun initCameraManager() {
        cameraManager =
            CameraManager(requireContext(), this, viewFinder)
        cameraManager.startCamera()

        imageViewCapture.setOnClickListener { takePicture() }

        if (cameraManager.hasFrontCamera()) {
            lensFacing = SharedPrefHelper.getLens(requireContext())
            cameraManager.setLens(lensFacing)
            imageViewSwitchCamera.setOnClickListener { switchCamera() }
        } else {
            imageViewSwitchCamera.isGone = true
        }

        flashMode = SharedPrefHelper.getFlashMode(requireContext())
        if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            imageViewFlash.setImageResource(R.drawable.ic_flash_off)
        } else {
            imageViewFlash.setImageResource(R.drawable.ic_flash_on)
        }
        cameraManager.setFlash(flashMode)
        imageViewFlash.setOnClickListener { switchFlash() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        orientationListener.disable()
    }

    private fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        SharedPrefHelper.saveLens(requireContext(), lensFacing)
        cameraManager.setLens(lensFacing)
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
        cameraManager.setFlash(flashMode)
    }

    fun onVolumeKeyDown() {
        takePicture()
    }

    private fun takePicture() {
        cameraManager.takePicture { openImageFragment(it) }
    }

    private fun openImageFragment(bitmap: Bitmap) {
        viewModel.bitmap.value = bitmap
        findNavController().navigate(R.id.action_cameraFragment_to_imageFragment)
    }
}