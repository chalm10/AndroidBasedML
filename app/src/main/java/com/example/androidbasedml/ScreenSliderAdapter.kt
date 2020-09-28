package com.example.androidbasedml

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidbasedml.barcode.BarcodeScannerFragment
import com.example.androidbasedml.facedetection.FaceDetectionFragment
import com.example.androidbasedml.imagelabeler.ImageLabelerFragment
import com.example.androidbasedml.textrecognition.TextRecognitionFragment

class ScreenSliderAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> BarcodeScannerFragment()
            1 -> FaceDetectionFragment()
            2 -> ImageLabelerFragment()
            else -> TextRecognitionFragment()
        }
    }
}