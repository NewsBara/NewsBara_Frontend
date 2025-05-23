package com.example.newsbara.data

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.example.newsbara.DefinitionProvider.getDefinition

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
    onWordClick: (String, String) -> Unit
): SpannableString {
    val spannable = SpannableString(this.text)
    for (word in highlightWords) {
        val engLine = text.lineSequence().firstOrNull()?.trim() ?: ""
        val start = engLine.indexOf(word, ignoreCase = true)
        if (start != -1) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onWordClick(word, getDefinition(word))
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = Color.parseColor("#A974FF")
                    ds.isUnderlineText = false
                    ds.isFakeBoldText = true
                }
            }
            spannable.setSpan(clickableSpan, start, start + word.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return spannable
}
