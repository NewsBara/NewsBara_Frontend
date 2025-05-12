package com.example.newsbara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.R
import com.example.newsbara.data.VideoItem
import com.example.newsbara.data.VideoSection

class VideoSectionAdapter(
    private val sections: List<VideoSection>,
    private val onVideoClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<VideoSectionAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.sectionTitle)
        val recycler: RecyclerView = view.findViewById(R.id.videoRecyclerView)

        init {
            recycler.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        }

        fun bind(section: VideoSection) {
            title.text = section.categoryTitle
            recycler.adapter = VideoItemAdapter(section.videos, onVideoClick)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.section_item, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount(): Int = sections.size
}
