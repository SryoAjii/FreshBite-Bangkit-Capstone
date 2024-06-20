package com.example.freshbite.view.main

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshbite.databinding.ArticleItemBinding
import com.example.freshbite.retrofit.response.ArticlesResponseItem

class MainAdapter: ListAdapter<ArticlesResponseItem, MainAdapter.MyViewHolder>(DIFF_CALLBACK) {
    inner class MyViewHolder(val binding: ArticleItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesResponseItem) {
            val title = binding.articleTitle
            val description = binding.articleDescription
            val image = binding.articleImage
            title.text = article.title
            description.text = article.content
            Glide.with(itemView.context)
                .load(article.image)
                .into(image)
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(article.link)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ArticlesResponseItem>() {
            override fun areItemsTheSame(oldItem: ArticlesResponseItem, newItem: ArticlesResponseItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticlesResponseItem, newItem: ArticlesResponseItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}