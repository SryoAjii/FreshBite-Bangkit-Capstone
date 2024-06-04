package com.example.freshbite.view.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.freshbite.databinding.ActivityProfileBinding
import com.example.freshbite.view.ViewModelFactory

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding

    private val viewModel by viewModels<ProfileViewModel>{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (user.email.isNotEmpty()) {
                val email = user.email
                binding.userEmail.text = email
            }
        }

        binding.logoutButton.setOnClickListener { viewModel.logout() }
    }
}