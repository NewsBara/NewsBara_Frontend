package com.example.newsbara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.data.DictionaryItem
import com.example.newsbara.R

class DictionaryAdapter(private val items: List<DictionaryItem>) : RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val tvPronunciation: TextView = itemView.findViewById(R.id.tvPronunciation)
        val tvMeanings: TextView = itemView.findViewById(R.id.tvMeanings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dictionary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvWord.text = item.word
        holder.tvPronunciation.text = item.pronunciation
        holder.tvMeanings.text = item.meanings.withIndex().joinToString("\n") {
            "${it.index + 1}.  ${it.value}"
        }
    }

    override fun getItemCount(): Int = items.size
}