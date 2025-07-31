package com.example.newsbara.data.model.youtube

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.example.newsbara.domain.model.KeywordInfo
import com.example.newsbara.domain.model.ScriptLine
import com.example.newsbara.showWordPopup
import java.io.Serializable


fun ScriptLine.getHighlightedText(highlightWords: List<String>): String {
    val engLine = sentence.trim()
    return highlightWords.fold(engLine) { acc, keyword ->
        acc.replace(
            keyword,
            "<span style='color: #FF6B84; font-weight: bold;'>$keyword</span>",
            ignoreCase = true
        )
    }
}

fun ScriptLine.getStartMillis(): Long {
    val parts = timestamp.split(":")
    if (parts.size != 3) return 0L

    val minutes = parts[0].toLongOrNull() ?: 0
    val seconds = parts[1].toLongOrNull() ?: 0

    val millisRaw = parts[2]
    val millis = millisRaw.toLongOrNull()
        ?: millisRaw.split(".").getOrNull(0)?.toLongOrNull()
        ?: 0

    return (minutes * 60 + seconds) * 1000 + millis
}

fun ScriptLine.getClickableSpannable(
    highlightWords: List<String>,
    context: Context,
    anchorTextView: TextView,
    onDefinitionFetch: (String) -> String
): SpannableString {
    val combinedText = "$sentence\n$sentenceKo"
    val spannable = SpannableString(combinedText)

    highlightWords.forEach { word ->
        val startIndex = sentence.indexOf(word, ignoreCase = true)
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
