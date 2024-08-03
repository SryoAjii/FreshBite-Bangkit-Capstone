package com.example.freshbite.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshbite.R
import com.example.freshbite.databinding.ActivityMainBinding
import com.example.freshbite.di.StateResult
import com.example.freshbite.retrofit.response.Article
import com.example.freshbite.view.ViewModelFactory
import com.example.freshbite.view.camera.CameraActivity
import com.example.freshbite.view.history.HistoryActivity
import com.example.freshbite.view.info.InfoActivity
import com.example.freshbite.view.profile.ProfileActivity
import com.example.freshbite.view.welcome.WelcomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        viewModel.getArticle().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StateResult.Loading -> {
                        loading(true)
                    }
                    is StateResult.Success -> {
                        setArticle(result.data)
                        loading(false)
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

        binding.filterButton.setOnClickListener { showRadioButtonDialog() }
        viewModel.newsItem.observe(this) { articles ->
            setArticle(articles)
        }

        binding.infoButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, InfoActivity::class.java))
        }

        binding.historyButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
        }
    }

    private fun showRadioButtonDialog() {
        val items = arrayOf("jeruk", "apel", "pisang", "tidak ada")
        var selectedItemIndex = 3

        val builder = MaterialAlertDialogBuilder(this)
            .setTitle("Pilih Buah untuk Memfilter Artikel")
            .setSingleChoiceItems(items, selectedItemIndex) { dialog, which ->
                selectedItemIndex = which
            }
            .setPositiveButton("OK") { dialog, which ->
                val selectedFruit = items[selectedItemIndex]
                filterArticles(selectedFruit)
            }
            .setNegativeButton("Batalkan", null)

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun filterArticles(tag: String) {
        viewModel.filterArticlesByTag(tag)
    }

    private fun searchArticle(title: String) {
        viewModel.searchArticle(title).observe(this) { result ->
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

    private fun setArticle(article: Map<String, Article>) {
        val adapter = MainAdapter(article.values.toList())
        binding.articleList.layoutManager = LinearLayoutManager(this)
        binding.articleList.adapter = adapter
    }

    private fun loading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}