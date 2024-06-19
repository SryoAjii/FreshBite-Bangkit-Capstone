package com.example.freshbite.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.freshbite.databinding.ActivityProfileBinding
import com.example.freshbite.di.StateResult
import com.example.freshbite.view.ViewModelFactory
import com.example.freshbite.view.welcome.WelcomeActivity

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
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            if (user.email.isNotEmpty()) {
                val email = user.email
                binding.userEmail.text = email
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.logoutButton.setOnClickListener { userLogout() }
    }

    private fun userLogout() {
        viewModel.logout()
        viewModel.userLogout().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StateResult.Loading -> {
                        loading(true)
                    }
                    is StateResult.Success -> {
                        loading(false)
                        result.data.message?.let { it1 -> toast(it1) }
                    }
                    is StateResult.Error -> {
                        loading(false)
                        toast(result.error)
                    }
                }
            }
        }
    }

    private fun loading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}