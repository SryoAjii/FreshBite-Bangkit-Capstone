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
import androidx.appcompat.app.AppCompatActivity
import com.example.freshbite.databinding.ActivityCameraBinding
import com.example.freshbite.ml.Model
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
                analyzeImage(imageUri!!, scaledBitmap)
            } else {
                toast("Mohon masukkan gambar")
            }
        }


        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun analyzeImage(imageUri: Uri, image: Bitmap?) {
        loading(true)
        try {
            val model = Model.newInstance(applicationContext)

            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3),
                DataType.FLOAT32
            )
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

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

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val confidences = outputFeature0.floatArray
            Log.e("OUTPUT", confidences.joinToString())

            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            Log.e("DETECTFRUITFRESHNESS", maxPos.toString())

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

            moveToResult(resultLabel, resultConfidence)
            uploadImageToFirebase(imageUri) { imageUrl ->
                saveClassificationToFirestore(resultLabel, imageUrl)
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

    private fun saveClassificationToFirestore(result: String?, imageUrl: String) {
        val data = hashMapOf(
            "result" to result,
            "date" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()),
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