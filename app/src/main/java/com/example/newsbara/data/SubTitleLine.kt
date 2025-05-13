package com.example.newsbara.data

data class SubtitleLine(
    val startTime: Double,
    val endTime: Double,
    val text: String
)
fun SubtitleLine.getHighlightedText(highlightWords: List<String>): String {
    return highlightWords.fold(text) { acc, keyword ->
        acc.replace(keyword, "<span style='background-color: #A974FF;'>$keyword</span>"
            , ignoreCase = true)
    }
}
