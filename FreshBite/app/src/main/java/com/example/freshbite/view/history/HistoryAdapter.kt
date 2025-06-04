package com.example.freshbite.view.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshbite.databinding.HistoryItemBinding

class HistoryAdapter(private val historyList: List<HistoryData>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: HistoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        val historyImg = binding.imgItemPhoto
        val historyLabel = binding.tvItemName
        val historyDate = binding.tvItemDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = historyList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]
        when (history.result) {
            "freshoranges" -> holder.historyLabel.text = "Jeruk Segar"
            "freshapple" -> holder.historyLabel.text = "Apel Segar"
            "freshbanana" -> holder.historyLabel.text = "Pisang Segar"
            "rottenoranges" -> holder.historyLabel.text = "Jeruk Busuk"
            "rottenapples" -> holder.historyLabel.text = "Apel Busuk"
            "rottenbananas" -> holder.historyLabel.text = "Pisang Busuk"
        }
        holder.historyDate.text = history.date
        Glide.with(holder.itemView.context)
            .load(history.imageUrl)
            .circleCrop()
            .into(holder.historyImg)
    }
}