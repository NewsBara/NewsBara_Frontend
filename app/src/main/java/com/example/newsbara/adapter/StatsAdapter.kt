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
import com.example.newsbara.data.HistoryItem
import com.example.newsbara.data.VideoItem
import com.example.newsbara.data.VideoProgress

class StatsAdapter(
    private val videoList: List<HistoryItem>,
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
        val video = videoList[position]

        val thumbnailUrl = "https://img.youtube.com/vi/${video.videoId}/mqdefault.jpg"
        Glide.with(holder.itemView)
            .load(thumbnailUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.ic_error)
            .into(holder.thumbnail)

        holder.title.text = video.title

        holder.progress.text = "3/4"

        holder.btnContinue.setOnClickListener {
            onContinueClicked(video)
        }
    }

    override fun getItemCount(): Int = videoList.size
}

