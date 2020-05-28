package com.example.camera

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    val bitmap = MutableLiveData<Bitmap>()
}