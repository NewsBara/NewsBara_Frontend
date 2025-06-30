package com.example.newsbara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbara.R
import com.example.newsbara.data.model.history.HistoryItem

class HistoryItemAdapter(
    private val items: List<HistoryItem>,
    private val onClick: (HistoryItem) -> Unit
) : RecyclerView.Adapter<HistoryItemAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.thumbnailImage)
        val title: TextView = view.findViewById(R.id.videoTitle)

        fun bind(video: HistoryItem) {
            title.text = video.title
            Glide.with(itemView)
                .load(video.thumbnail)
                .into(thumbnail)

            itemView.setOnClickListener {
                onClick(video)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
