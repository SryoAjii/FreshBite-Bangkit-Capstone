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
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var imageUri: Uri? = null

    private val viewModel by viewModels<CameraViewModel>{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraButton.setOnClickListener { camera() }
        binding.galleryButton.setOnClickListener { gallery() }

        viewModel.getSession().observe(this) { user ->
            if (user.token.isNotEmpty()) {
                val token = user.token
                binding.analyzeButton.setOnClickListener { analyzeImage(token) }
            }
        }
    }

    private fun analyzeImage(token: String) {
        imageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image Classification File", "showImage: ${imageFile.path}")
            loading(true)
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
                            val intent = Intent(this@CameraActivity, ResultActivity::class.java)
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
        } ?: toast("Masukkan gambar terlebih dahulu")
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