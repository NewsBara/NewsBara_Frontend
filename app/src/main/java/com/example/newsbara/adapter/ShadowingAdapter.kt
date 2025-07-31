package com.example.newsbara.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.data.model.shadowing.ShadowingSentence
import com.example.newsbara.domain.model.ScriptLine

class ShadowingAdapter(
    private val items: List<ScriptLine>,
    private val highlightWords: List<String>,
    private val highlightColor: String = "#FF6B84"
) : RecyclerView.Adapter<ShadowingAdapter.ViewHolder>() {

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tv = TextView(parent.context).apply {
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setLineSpacing(8f, 1f)
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val numbered = "${position + 1}. ${item.sentence}"
        val spannable = SpannableString(numbered)

        highlightWords.forEach { word ->
            var startIndex = numbered.indexOf(word, ignoreCase = true)
            while (startIndex >= 0) {
                val endIndex = startIndex + word.length

                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor(highlightColor)),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                startIndex = numbered.indexOf(word, startIndex + 1, ignoreCase = true)
            }
        }

        holder.textView.text = spannable
    }

    override fun getItemCount(): Int = items.size
}