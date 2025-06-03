package com.example.newsbara.data

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.example.newsbara.DefinitionProvider.getDefinition
import com.example.newsbara.showWordPopup

data class SubtitleLine(
    val startTime: Double,
    val endTime: Double,
    val text: String
)
fun SubtitleLine.getHighlightedText(highlightWords: List<String>): String {
    val engLine = text.lineSequence().firstOrNull()?.trim() ?: ""
    return highlightWords.fold(engLine) { acc, keyword ->
        acc.replace(keyword, "<span style='color: #FF6B84; font-weight: bold;'>$keyword</span>"
        , ignoreCase = true)
    }
}

fun SubtitleLine.getClickableSpannable(
    highlightWords: List<String>,
    context: Context,
    anchorTextView: TextView,
    onDefinitionFetch: (String) -> String
): SpannableString {
    val spannable = SpannableString(this.text)
    val engLine = text.lineSequence().firstOrNull()?.trim() ?: ""

    highlightWords.forEach { word ->

        val startIndex = this.text.indexOf(word, ignoreCase = true)
        if (startIndex != -1) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val definition = onDefinitionFetch(word)
                    showWordPopup(context, anchorTextView, word, definition, startIndex)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = Color.parseColor("#FF6B84")
                    ds.isUnderlineText = false
                    ds.isFakeBoldText = true
                }
            }
            spannable.setSpan(
                clickableSpan,
                startIndex,
                startIndex + word.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    return spannable
}
