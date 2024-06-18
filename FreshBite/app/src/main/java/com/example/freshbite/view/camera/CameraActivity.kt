package com.example.freshbite.view.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.freshbite.databinding.ActivityCameraBinding
import com.example.freshbite.view.result.ResultActivity

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraButton.setOnClickListener { camera() }
        binding.galleryButton.setOnClickListener { gallery() }
        binding.successButton.setOnClickListener { getResult() }
        binding.errorButton.setOnClickListener { getErrorResult() }
    }

    private fun getResult() {
        val fruit = "orange"
        val result = "fresh"
        val intent = Intent(this@CameraActivity, ResultActivity::class.java)
        intent.putExtra("EXTRA_NAME", fruit)
        intent.putExtra("EXTRA_RESULT", result)
        startActivity(intent)
    }

    private fun getErrorResult() {
        val fruit = "orange"
        val result = "rotten"
        val intent = Intent(this@CameraActivity, ResultActivity::class.java)
        intent.putExtra("EXTRA_NAME", fruit)
        intent.putExtra("EXTRA_RESULT", result)
        startActivity(intent)
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
}