package com.example.camera

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.camera.util.createFile
import com.example.camera.util.makeAvailableForScanning
import com.example.camera.util.saveBitmap
import com.example.camera.util.showDialog
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment(R.layout.fragment_image) {

    private val args: ImageFragmentArgs by navArgs()
    private lateinit var bitmap: Bitmap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bitmap = args.bitmap
        imageView.setImageBitmap(bitmap)

        imageViewSave.setOnClickListener { showSaveImageDialog() }
        imageViewBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun showSaveImageDialog() {
        showDialog(requireActivity()) { fileName ->
            saveImage(fileName)
            Toast.makeText(requireActivity(), getString(R.string.photo_save_result), Toast.LENGTH_LONG).show()
        }
    }

    private fun saveImage(fileName: String) {
        createFile(requireContext(), fileName).apply {
            saveBitmap(bitmap)
            makeAvailableForScanning(requireContext())
        }
    }
}