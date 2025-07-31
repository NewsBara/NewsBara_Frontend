package com.example.newsbara.domain.model


data class ScriptLine(
    val sentence: String,
    val sentenceKo: String,
    val timestamp: String,
    val keywords: List<KeywordInfo>
)