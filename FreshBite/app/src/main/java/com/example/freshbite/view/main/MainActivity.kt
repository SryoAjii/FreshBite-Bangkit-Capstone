package com.example.freshbite.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshbite.R
import com.example.freshbite.databinding.ActivityMainBinding
import com.example.freshbite.di.StateResult
import com.example.freshbite.retrofit.response.ArticlesResponseItem
import com.example.freshbite.view.ViewModelFactory
import com.example.freshbite.view.camera.CameraActivity
import com.example.freshbite.view.profile.ProfileActivity
import com.example.freshbite.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        viewModel.getArticles().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StateResult.Loading -> {
                        loading(true)
                    }
                    is StateResult.Success -> {
                        loading(false)
                        setArticle(result.data)
                    }
                    is StateResult.Error -> {
                        loading(false)
                    }
                }
            }
        }

        with(binding) {
            searchEditText.setOnEditorActionListener{ _, _, _, ->
                val search = searchEditText.text.toString()
                searchEditText.hint = search
                searchArticle(search)
                Toast.makeText(this@MainActivity, searchEditText.text, Toast.LENGTH_SHORT).show()
                false
            }
        }

        binding.cameraPage.setOnClickListener {
            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profileMenu -> {
                    val profileIntent = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(profileIntent)
                    true
                }
                else -> false
            }
        }
    }

    private fun searchArticle(title: String) {
        viewModel.searchArticles(title).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StateResult.Loading -> {
                        loading(true)
                    }
                    is StateResult.Success -> {
                        loading(false)
                        setArticle(result.data)
                    }
                    is StateResult.Error -> {
                        loading(false)
                    }
                }
            }
        }
    }

    private fun setArticle(article: List<ArticlesResponseItem>) {
        val adapter = MainAdapter()
        adapter.submitList(article)
        binding.articleList.layoutManager = LinearLayoutManager(this)
        binding.articleList.adapter = adapter
    }

    private fun loading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}