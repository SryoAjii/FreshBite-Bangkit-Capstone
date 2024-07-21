package com.example.freshbite.view.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.freshbite.databinding.ActivityCameraBinding
import com.example.freshbite.retrofit.api.ApiConfig
import com.example.freshbite.view.ViewModelFactory
import com.example.freshbite.view.result.ResultActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var imageUri: Uri? = null

    private val viewModel by viewModels<CameraViewModel>{
        ViewModelFactory.getInstance(this)
    }

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

        viewModel.getSession().observe(this) { user ->
            if (user.token.isNotEmpty()) {
                val token = user.token
                val email = user.email
                binding.analyzeButton.setOnClickListener { analyzeImage(token, email) }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun analyzeImage(token: String, user: String) {
        imageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image Classification File", "showImage: ${imageFile.path}")
            loading(true)

            uploadImageToFirebase(imageFile) { imageUrl ->
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestImageFile
                )

                lifecycleScope.launch {
                    try {
                        val apiService = ApiConfig.getApiService(token)
                        val successResponse = apiService.uploadImage(multipartBody)

                        val fruitResponse = successResponse.label
                        val fruitScore = successResponse.confidenceScore

                        if (fruitScore != null) {
                            if (fruitScore >= 70) {
                                saveClassificationToFirestore(fruitResponse, user, imageUrl)
                                val intent = Intent(this@CameraActivity, ResultActivity::class.java)
                                intent.putExtra("EXTRA_SCORE", fruitScore)
                                intent.putExtra("EXTRA_FRUIT", fruitResponse)
                                startActivity(intent)
                                loading(false)
                            } else {
                                toast("Buah yang dapat di deteksi adalah jeruk, apel, dan pisang")
                                loading(false)
                            }
                        }
                    } catch (e: HttpException) {
                        toast("Error")
                        loading(false)
                    }
                }
            }
        } ?: toast("Masukkan gambar terlebih dahulu")
    }

    private fun uploadImageToFirebase(file: File, callback: (String) -> Unit) {
        val uri = Uri.fromFile(file)
        val storageReference = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val uploadTask = storageReference.putFile(uri)

        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }
        }.addOnFailureListener {
            toast("Gagal mengunggah gambar")
            loading(false)
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