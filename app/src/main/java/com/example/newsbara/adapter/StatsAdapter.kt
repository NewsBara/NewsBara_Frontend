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

        val thumbnailUrl = if (position == 0) {
            "https://img.youtube.com/vi/1iiCkCokunI/mqdefault.jpg"
        } else {
            historyItem.thumbnail
        }
        Glide.with(holder.itemView)
            .load(thumbnailUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.ic_error)
            .into(holder.thumbnail)

        holder.title.text = historyItem.title

        holder.progress.text = when (historyItem.status) {
            "WATCHED" -> "1/4"
            "SHADOWING" -> "1/4"
            "TEST" -> "2/4"
            "WORD" -> "3/4"
            "COMPLETE" -> "4/4"
            else -> "4/4"
        }

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

