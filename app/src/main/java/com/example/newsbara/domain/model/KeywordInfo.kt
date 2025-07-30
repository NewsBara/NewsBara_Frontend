package com.example.newsbara.domain.model

data class KeywordInfo(
    val word: String,
    val gptDefinition: String,
    val gptDefinitionKo: String,
    val bertDefinition: String,
    val bertDefinitionKo: String,
    val bertSource: String,
    val bertConfidence: Float
)