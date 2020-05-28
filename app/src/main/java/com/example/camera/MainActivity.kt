package com.example.camera

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.camera.fragments.CameraFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        return if (
            findNavController(R.id.navHost).currentDestination?.id == R.id.cameraFragment &&
            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        ) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
            val cameraFragment = navHostFragment.childFragmentManager.fragments[0] as CameraFragment
            cameraFragment.onVolumeKeyDown()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}