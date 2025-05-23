package com.example.newsbara

object DefinitionProvider {

    private val wordDefinitions = mapOf(
        "accelerating" to "가속, 속도가 증가하는 것",
        "global" to "전 세계적인",
        "urgent" to "긴급한, 시급한",
        "emission" to "배출, 방출",
        "renewable" to "재생 가능한",
        "climate" to "기후",
        "carbon" to "탄소",
        "temperature" to "온도",
        // ... 이후 30~40개 추가
    )

    fun getDefinition(word: String): String {
        return wordDefinitions[word.lowercase()] ?: "정의 없음"
    }
}
