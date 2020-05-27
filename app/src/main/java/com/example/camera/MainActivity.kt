package com.example.camera

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
            val cameraFragment = navHostFragment.childFragmentManager.fragments[0] as CameraFragment
            cameraFragment.onVolumeKeyDown()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d("mmm", "MainActivity :  onConfigurationChanged --  ")

        super.onConfigurationChanged(newConfig)
    }

    companion object {
        const val KEY_EVENT_ACTION = "key_event_action"
        const val KEY_EVENT_EXTRA = "key_event_extra"
    }
}