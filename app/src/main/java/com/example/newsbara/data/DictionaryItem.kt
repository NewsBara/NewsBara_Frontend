package com.example.newsbara.data

data class DictionaryMeaning(
    val partOfSpeech: String,
    val definition: String
)

data class DictionaryItem(
    val word: String,
    val meanings: List<DictionaryMeaning>
)