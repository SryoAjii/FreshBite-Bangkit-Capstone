package com.example.freshbite.view.result

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

        val fruit = intent.getStringExtra("EXTRA_RESULT")
        val score = intent.getFloatExtra("EXTRA_SCORE", 0f)

        if (fruit != null) {
            binding.resultAnimation.setAnimation(R.raw.success)
            binding.resultAnimation.repeatCount = LottieDrawable.INFINITE
            binding.resultText.text = getText(R.string.result_fresh)
            when (fruit) {
                "freshoranges" -> {
                    getBenefit(fruit)
                    binding.resultLabel.text = "Jeruk Segar ${(score * 100).toInt()}%"
                    list.addAll(getArticleList(fruit))
                    showRecycleList()
                }
                "freshapple" -> {
                    getBenefit(fruit)
                    binding.resultLabel.text = "Apel Segar ${(score * 100).toInt()}%"
                    list.addAll(getArticleList(fruit))
                    showRecycleList()
                }
                "freshbanana" -> {
                    getBenefit(fruit)
                    binding.resultLabel.text = "Pisang Segar ${(score * 100).toInt()}%"
                    list.addAll(getArticleList(fruit))
                    showRecycleList()
                }
                else -> {
                    binding.resultAnimation.setAnimation(R.raw.error)
                    binding.resultText.text = getText(R.string.result_rotten)
                    binding.fruitBenefitCardView.visibility = View.GONE
                    binding.resultArticleCardView.visibility = View.GONE
                    binding.articleTextview.visibility = View.GONE
                    binding.resultLabel.setBackgroundResource(R.drawable.red_bg)
                    when (fruit) {
                        "rottenapples" -> binding.resultLabel.text = "Apel Busuk ${(score * 100).toInt()}%"
                        "rottenoranges" -> binding.resultLabel.text = "Jeruk Busuk ${(score * 100).toInt()}%"
                        "rottenbananas" -> binding.resultLabel.text = "Pisang Busuk ${(score * 100).toInt()}%"
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getBenefit(fruit: String) {
        when (fruit) {
            "freshoranges" -> binding.fruitBenefit.text = getText(R.string.orange_benefit)
            "freshapple" -> binding.fruitBenefit.text = getText(R.string.apple_benefit)
            "freshbanana" -> binding.fruitBenefit.text = getText(R.string.banana_benefits)
        }
    }

    private fun getArticleList(fruit: String): ArrayList<ResultArticle> {
        val dataTitle = resources.getStringArray(R.array.data_title)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataImg = resources.getStringArray(R.array.data_photo)
        val dataUrl = resources.getStringArray(R.array.data_url)
        val listArticle = ArrayList<ResultArticle>()

        val indices = when (fruit) {
            "freshoranges" -> 0..2
            "freshapple" -> 3..5
            "freshbanana" -> 6..8
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