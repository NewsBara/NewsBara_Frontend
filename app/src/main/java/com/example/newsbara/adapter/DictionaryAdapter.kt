package com.example.newsbara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.R
import com.example.newsbara.domain.model.DictionaryEntry

class DictionaryAdapter(
    private var items: List<DictionaryEntry>
) : RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    fun submit(newItems: List<DictionaryEntry>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWord: TextView = view.findViewById(R.id.tvWord)
        val tvMeanings: TextView = view.findViewById(R.id.tvMeanings)
        init {
            tvMeanings.setLineSpacing(0f, 1f)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dictionary, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvWord.text = item.word

        val en = item.wordnetSenses
        val ko = item.wordnetSensesKo
        val n = minOf(en.size, ko.size)

        holder.tvMeanings.text =
            if (n == 0) "No definition available"
            else buildString {
                for (i in 0 until n) {
                    append("${i + 1}.  ${en[i]}\n")
                    append("    ${ko[i]}")
                    if (i != n - 1) append("\n\n")
                }
            }
    }

    override fun getItemCount(): Int = items.size
}