package com.example.newsbara.domain.model


data class DictionaryEntry(
    val word: String,
    val wordnetSenses: List<String>,
    val wordnetSensesKo: List<String>
)