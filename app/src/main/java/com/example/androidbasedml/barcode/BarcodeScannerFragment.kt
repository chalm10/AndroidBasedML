package com.example.androidbasedml.barcode

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.example.androidbasedml.BaseFragment
import com.example.androidbasedml.TAG
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeScannerFragment : BaseFragment() {

    private val barcodeScanner by lazy {
        BarcodeScanning.getClient()
    }

//    lateinit var imageAnalysis: ImageAnalysis

//    private var cameraProvider: ProcessCameraProvider? = null

//    lateinit var cameraSelector: CameraSelector

//    lateinit var preview: Preview

//    var cameraSide = CameraSelector.LENS_FACING_BACK

//    abstract val imageAnalyser : ImageAnalysis.Analyzer

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.activity_camera, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        startCamera()
//
//        previewView.setOnClickListener(
//            object : DoubleClick.OnDoubleClickListener() {
//                override fun onDoubleClick(v: View?) {
//                    cameraSide = if (cameraSide == CameraSelector.LENS_FACING_BACK) {
//                        CameraSelector.LENS_FACING_FRONT
//                    } else {
//                        CameraSelector.LENS_FACING_BACK
//                    }
//                    bindPreview()
//                }
//            })
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
                        barcodeScanner.process(inputImage)
                            .addOnSuccessListener {
                                it.forEach { barcode ->
                                    Log.d(
                                        TAG, """
                                        FORMAT = ${barcode.format}
                                        VALUE = ${barcode.rawValue}
                                    """.trimIndent()
                                    )
                                }
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "Barcode Scanning failed with exception ", it)
                            }
                            .addOnCompleteListener {
                                //close the image for next to be analysed
                                image.close()
                            }
                    } ?: image.close()   //close either way for the next image to be analysed

                }
            })

//        bindPreview()
    }
//
//    override fun onResume() {
//        super.onResume()
//        bindPreview()
//    }

//    private fun startCamera() {
//        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
//            ProcessCameraProvider.getInstance(context!!)
//        cameraProviderFuture.addListener(Runnable {
//            cameraProvider = cameraProviderFuture.get()
//            bindPreview()
//        }, ContextCompat.getMainExecutor(context!!))
//
//    }

    private fun bindPreview() {
//        preview = Preview.Builder()
//            //                .setTargetResolution(Size(1080,1080))
//            //                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .build()
//
//        preview.setSurfaceProvider(previewView.createSurfaceProvider())
//
//        imageAnalysis = ImageAnalysis.Builder()
//            .build()
//
//        val orientationEventListener = object : OrientationEventListener(context) {
//            @SuppressLint("RestrictedApi")
//            override fun onOrientationChanged(orientation: Int) {
//                val rotation = when (orientation) {
//                    in 45..134 -> Surface.ROTATION_270
//                    in 135..224 -> Surface.ROTATION_180
//                    in 225..314 -> Surface.ROTATION_90
//                    else -> Surface.ROTATION_0
//                }
//                preview.targetRotation = rotation
//                imageAnalysis.targetRotation = rotation
////              Log.d(TAG, rotation.toString())
//
//            }
//        }
//        orientationEventListener.enable()

//        imageAnalysis.setAnalyzer(
//            ContextCompat.getMainExecutor(context),
//            object : ImageAnalysis.Analyzer {
//                @SuppressLint("UnsafeExperimentalUsageError")
//                override fun analyze(image: ImageProxy) {
//                    //write image analysis code here
////                    Log.d(TAG, "image analysed")
////                    Log.d(TAG, "image analysis : rotation ${image.imageInfo.rotationDegrees}")
////                    Log.d(TAG, "rotation listener : rotation ${imageAnalysis.targetRotation}")
//                    val mediaImage = image.image
//                    mediaImage?.let {
//                        val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
//                        barcodeScanner.process(inputImage)
//                            .addOnSuccessListener {
//                                it.forEach { barcode ->
//                                    Log.d(
//                                        TAG, """
//                                        FORMAT = ${barcode.format}
//                                        VALUE = ${barcode.rawValue}
//                                    """.trimIndent()
//                                    )
//                                }
//                            }
//                            .addOnFailureListener {
//                                Log.d(TAG, "Barcode Scanning failed with exception ", it)
//                            }
//                            .addOnCompleteListener {
//                                //close the image for next to be analysed
//                                image.close()
//                            }
//                    } ?: image.close()   //close either way for the next image to be analysed
//
//                }
//            })

//        cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(cameraSide)
//            .build()
//
//        bindCameraUseCases()
    }


//    private fun bindCameraUseCases() {
//        try {
//            //unbind use cases before rebinding
//            cameraProvider!!.unbindAll()
//            cameraProvider!!.bindToLifecycle(
//                this,
//                cameraSelector,
//                preview,
//                imageAnalysis
//            )
//        } catch (e: Exception) {
//            Log.d(TAG, "use case binding failed + $e")
//        }
//    }


}





