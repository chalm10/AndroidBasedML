package com.example.androidbasedml

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.androidbasedml.utils.DoubleClick
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_camera.*

const val TAG = "MainActivity"


abstract class BaseFragment : Fragment() {
   //TODO implement this fragment for preventing redundant retyping of same code(setting up the camera)
    //TODO make all fragments inherit from this base fragment

    protected lateinit var imageAnalysis: ImageAnalysis

    private var cameraProvider: ProcessCameraProvider? = null

    lateinit var cameraSelector: CameraSelector

    lateinit var preview: Preview

    var cameraSide = CameraSelector.LENS_FACING_BACK

//    abstract val imageAnalyser : ImageAnalysis.Analyzer


    override fun onCreate(savedInstanceState: Bundle?) {
        startCamera()
        super.onCreate(savedInstanceState)
//        imageAnalysis = ImageAnalysis.Builder()
//            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        startCamera()
        super.onViewCreated(view, savedInstanceState)

        previewView.setOnClickListener(
            object : DoubleClick.OnDoubleClickListener() {
                override fun onDoubleClick(v: View?) {
                    cameraSide = if (cameraSide == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                    bindPreview()
                }
            })

    }

    override fun onResume() {
        super.onResume()
        bindPreview()
    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            bindPreview()
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun bindPreview() {
        preview = Preview.Builder()
            //                .setTargetResolution(Size(1080,1080))
            //                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()

        preview.setSurfaceProvider(previewView.createSurfaceProvider())

        imageAnalysis = ImageAnalysis.Builder()
            .build()

        val orientationEventListener = object : OrientationEventListener(context) {
            @SuppressLint("RestrictedApi")
            override fun onOrientationChanged(orientation: Int) {
                val rotation = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                preview.targetRotation = rotation
                imageAnalysis.targetRotation = rotation
//              Log.d(TAG, rotation.toString())

            }
        }
        orientationEventListener.enable()

        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraSide)
            .build()

        bindCameraUseCases()
    }


    private fun bindCameraUseCases() {
        try {
            //unbind use cases before rebinding
            cameraProvider!!.unbindAll()
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.d(TAG, "use case binding failed + $e")
        }
    }







}