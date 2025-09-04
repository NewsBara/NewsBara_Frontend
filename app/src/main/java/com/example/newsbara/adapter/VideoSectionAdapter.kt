package com.example.newsbara.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.R
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.youtube.VideoSection
class VideoSectionAdapter(
    private val sections: List<VideoSection>,
    private val onVideoClick: (HistoryItem) -> Unit
) : RecyclerView.Adapter<VideoSectionAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.sectionTitle)
        val recycler: RecyclerView = view.findViewById(R.id.videoRecyclerView)

        init {
            recycler.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        }

        fun bind(section: VideoSection) {
            title.text = section.channelName
            recycler.adapter = HistoryItemAdapter(section.videos, onVideoClick)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.section_item, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]

        if (position == 0 && section.videos.isNotEmpty()) {
            val firstVideo = section.videos[0]

            val customizedVideo = firstVideo.copy(
                title = "The surprising benefits of walking backwards | BBC Global",
                thumbnail = "https://img.youtube.com/vi/1iiCkCokunI/mqdefault.jpg"
            )

            val customizedSection = section.copy(videos = listOf(customizedVideo) + section.videos.drop(1))

            holder.bind(customizedSection)
        } else {
            holder.bind(section)
        }
    }

    override fun getItemCount(): Int = sections.size
}