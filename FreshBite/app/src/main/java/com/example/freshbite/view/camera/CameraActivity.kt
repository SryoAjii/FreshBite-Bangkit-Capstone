package com.example.freshbite.view.camera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.freshbite.databinding.ActivityCameraBinding
import com.example.freshbite.di.StateResult
import com.example.freshbite.ml.Model
import com.example.freshbite.view.ViewModelFactory
import com.example.freshbite.view.result.ResultActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var imageUri: Uri? = null

    private val viewModel by viewModels<CameraViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var imageSize: Int = 224

    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        binding.cameraButton.setOnClickListener { camera() }
        binding.galleryButton.setOnClickListener { gallery() }

        viewModel.getUser().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StateResult.Loading -> {
                        //
                    }

                    is StateResult.Success -> {
                        val username = result.data.second
                        binding.analyzeButton.setOnClickListener {
                            if (imageUri != null) {
                                val inputStream = contentResolver.openInputStream(imageUri!!)
                                val imageBitmap = BitmapFactory.decodeStream(inputStream)
                                val scaledBitmap = Bitmap.createScaledBitmap(
                                    imageBitmap,
                                    imageSize,
                                    imageSize,
                                    false
                                )
                                analyzeImage(imageUri!!, scaledBitmap, username)
                            } else {
                                toast("Mohon masukkan gambar")
                            }
                        }
                        //
                    }

                    is StateResult.Error -> {
                        toast(result.error)
                        //
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun analyzeImage(imageUri: Uri, image: Bitmap?, user: String) {
        loading(true)
        try {
            val model = Model.newInstance(applicationContext)

            // Prepare input tensor buffer
            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3),
                DataType.FLOAT32
            )
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            // Normalize image pixel values to [0, 1] range
            val intValues = IntArray(imageSize * imageSize)
            image?.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++]
                    byteBuffer.putFloat(((`val` shr 16) and 0xFF) / 255.0f)
                    byteBuffer.putFloat(((`val` shr 8) and 0xFF) / 255.0f)
                    byteBuffer.putFloat((`val` and 0xFF) / 255.0f)
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            // Run inference
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Get the confidence array
            val confidences = outputFeature0.floatArray
            Log.e("OUTPUT", confidences.joinToString())

            // Find the index with the highest confidence
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            Log.e("DETECTFRUITFRESHNESS", maxPos.toString())

            // Map the index to the corresponding label
            val labels = listOf(
                "freshapple",
                "freshbanana",
                "freshoranges",
                "rottenapples",
                "rottenbananas",
                "rottenoranges"
            )
            val resultLabel = labels[maxPos]
            val resultConfidence = confidences[maxPos]
            model.close()

            // Move to the result activity
            moveToResult(resultLabel, resultConfidence)
            uploadImageToFirebase(imageUri) { imageUrl ->
                saveClassificationToFirestore(resultLabel, user, imageUrl)
            }
            loading(false)
        } catch (e: IOException) {
            Log.e("Classify_Image", "error gagal klasifikasi", e)
            loading(false)
        }
    }

    private fun moveToResult(result: String?, confidence: Float?) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("EXTRA_RESULT", result)
        intent.putExtra("EXTRA_SCORE", confidence)
        startActivity(intent)
    }

    private fun uploadImageToFirebase(image: Uri, callback: (String) -> Unit) {
        val uri = image
        val storageReference = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val uploadTask = storageReference.putFile(uri)

        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }
        }.addOnFailureListener {
            toast("Gagal mengunggah gambar")
        }
    }

    private fun saveClassificationToFirestore(result: String?, username: String, imageUrl: String) {
        val data = hashMapOf(
            "result" to result,
            "date" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()),
            "username" to username,
            "imageUrl" to imageUrl
        )

        firestore.collection("classifications")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

    private fun gallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun camera() {
        imageUri = getImageUri(this)
        launchCamera.launch(imageUri)
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImg()
        }
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            showImg()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private fun showImg() {
        imageUri?.let {
            Log.d("Image URI", "showImg: $it")
            binding.cameraImageView.setImageURI(it)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun loading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}