package com.example.androidbasedml

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

private const val RC_CAMERA = 123


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            bindViewPager()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA
                ), RC_CAMERA
            )
        }
    }

    private fun bindViewPager() {
        viewPager.adapter = ScreenSliderAdapter(this)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "BARCODE SCANNER"
                1 -> tab.text = "FACE DETECTION"
                2 -> tab.text = "IMAGE LABELER"
                3 -> tab.text = "TEXT RECOGNITION"
            }
        }.attach()



    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bindViewPager()
            } else {
                Toast.makeText(this, "Camera Access Required", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

