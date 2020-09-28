package com.example.androidbasedml.facedetection

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.androidbasedml.R
import com.example.androidbasedml.TAG
import com.example.androidbasedml.utils.DoubleClick
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.android.synthetic.main.activity_camera.*

class FaceDetectionFragment : Fragment() {

    private val faceDetector by lazy {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .build()

        )
    }

    lateinit var imageAnalysis: ImageAnalysis

    private var cameraProvider: ProcessCameraProvider? = null

    lateinit var cameraSelector: CameraSelector

    lateinit var preview: Preview

    var cameraSide = CameraSelector.LENS_FACING_BACK

//    abstract val imageAnalyser : ImageAnalysis.Analyzer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startCamera()

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
            ProcessCameraProvider.getInstance(context!!)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            bindPreview()
        }, ContextCompat.getMainExecutor(context!!))

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
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            object : ImageAnalysis.Analyzer {
                @SuppressLint("UnsafeExperimentalUsageError")
                override fun analyze(image: ImageProxy) {
                    //write image analysis code here
//                    Log.d(TAG, "image analysed")
//                    Log.d(TAG, "image analysis : rotation ${image.imageInfo.rotationDegrees}")
//                    Log.d(TAG, "rotation listener : rotation ${imageAnalysis.targetRotation}")
                    val mediaImage = image.image
                    mediaImage?.let {
                        val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                        faceDetector.process(inputImage)
                            .addOnSuccessListener {
                                if (it.size>0){
                                    Log.d(TAG, "Faces = ${it.size}")
                                }
                                it.forEach { face ->
                                    Log.d(TAG, """
                                        left eye = ${face.leftEyeOpenProbability}
                                        right eye =${face.rightEyeOpenProbability}
                                        smiling = ${face.smilingProbability}
                                    """.trimIndent())
                                }
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "Face Detection failed with exception ", it)
                            }
                            .addOnCompleteListener {
                                //close the image for next to be analysed
                                image.close()
                            }
                    } ?: image.close()   //close either way for the next image to be analysed

                }
            })

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
