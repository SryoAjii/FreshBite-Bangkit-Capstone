package com.example.freshbite.view.result

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.freshbite.R
import com.example.freshbite.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private val list = ArrayList<ResultArticle>()

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.resultArticle.layoutManager = layoutManager

        val fruit = intent.getStringExtra("EXTRA_NAME")
        val result = intent.getStringExtra("EXTRA_RESULT")

        if (result == "fresh") {
            binding.resultAnimation.setAnimation(R.raw.success)
            binding.resultAnimation.repeatCount = LottieDrawable.INFINITE
            binding.resultText.text = getText(R.string.result_fresh)
            if (fruit != null) {
                getBenefit(fruit)
                list.addAll(getArticleList(fruit))
                showRecycleList()
            }
        } else {
            binding.resultAnimation.setAnimation(R.raw.error)
            binding.resultText.text = getText(R.string.result_rotten)
            binding.fruitBenefitCardView.visibility = View.GONE
            binding.resultArticleCardView.visibility = View.GONE
            binding.articleTextview.visibility = View.GONE
        }
    }

    private fun getBenefit(fruit: String) {
        when (fruit) {
            "orange" -> binding.fruitBenefit.text = getText(R.string.orange_benefit)
            "apple" -> binding.fruitBenefit.text = getText(R.string.apple_benefit)
            "banana" -> binding.fruitBenefit.text = getText(R.string.banana_benefits)
        }
    }

    private fun getArticleList(fruit: String): ArrayList<ResultArticle> {
        val dataTitle = resources.getStringArray(R.array.data_title)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataImg = resources.getStringArray(R.array.data_photo)
        val dataUrl = resources.getStringArray(R.array.data_url)
        val listArticle = ArrayList<ResultArticle>()

        val indices = when (fruit) {
            "orange" -> 0..2
            "apple" -> 3..5
            "banana" -> 6..8
            else -> return listArticle
        }

        for (i in indices) {
            val article = ResultArticle(dataTitle[i], dataDescription[i], dataImg[i], dataUrl[i])
            listArticle.add(article)
        }

        return listArticle
    }

    private fun showRecycleList() {
        val listArticleAdapter = ResultAdapter(list)
        binding.resultArticle.adapter = listArticleAdapter
    }
}