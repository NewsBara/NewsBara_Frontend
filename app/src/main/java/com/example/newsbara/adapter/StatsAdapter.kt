package com.example.newsbara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbara.R
import com.example.newsbara.data.model.history.HistoryItem


class StatsAdapter(
    private var items: List<HistoryItem> = emptyList(),
    private val onContinueClicked: (HistoryItem) -> Unit
) : RecyclerView.Adapter<StatsAdapter.StatsViewHolder>() {

    inner class StatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.imageThumbnail)
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val progress: TextView = itemView.findViewById(R.id.textProgress)
        val btnContinue: Button = itemView.findViewById(R.id.buttonContinue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return StatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val historyItem = items[position]

        // YouTube 썸네일 URL 생성
        val thumbnailUrl = "https://img.youtube.com/vi/${historyItem.videoId}/mqdefault.jpg"
        Glide.with(holder.itemView)
            .load(thumbnailUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.ic_error)
            .into(holder.thumbnail)

        // 제목 표시
        holder.title.text = historyItem.title

        // 진행 상태
        holder.progress.text = when (historyItem.status) {
            "WATCHED" -> "1/4"
            "SHADOWING" -> "2/4"  // 예시
            "SUMMARY" -> "3/4"
            "COMPLETE" -> "4/4"
            else -> "0/4"
        }

        // 버튼 클릭 시 처리
        holder.btnContinue.setOnClickListener {
            onContinueClicked(historyItem)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<HistoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

