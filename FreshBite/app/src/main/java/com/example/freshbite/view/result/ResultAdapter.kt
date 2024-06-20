package com.example.freshbite.view.result

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshbite.databinding.ResultArticleItemBinding

class ResultAdapter(private val listArticle: ArrayList<ResultArticle>): RecyclerView.Adapter<ResultAdapter.ListViewHolder>() {

    inner class ListViewHolder(binding: ResultArticleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imgPhoto = binding.imgItemPhoto
        val itemTitle = binding.tvItemName
        val itemDescription = binding.tvItemDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ResultArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listArticle.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (title, description, image, url) = listArticle[position]
        holder.itemTitle.text = title
        holder.itemDescription.text = description
        Glide.with(holder.itemView.context)
            .load(image)
            .into(holder.imgPhoto)
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            holder.itemView.context.startActivity(intent)
        }
    }
}