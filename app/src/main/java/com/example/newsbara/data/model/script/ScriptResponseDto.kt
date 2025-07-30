package com.example.newsbara.data.model.script

import com.google.gson.annotations.SerializedName

data class ScriptResponseDto(
    @SerializedName("sentence")
    val sentence: String,
    @SerializedName("sentence_ko")
    val sentenceKo: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("keywords")
    val keywords: List<KeywordDto>
)

data class KeywordDto(
    @SerializedName("word")
    val word: String,
    @SerializedName("gpt_definition")
    val gptDefinition: String,
    @SerializedName("gpt_definition_ko")
    val gptDefinitionKo: String,
    @SerializedName("bert_definition")
    val bertDefinition: String,
    @SerializedName("bert_definition_ko")
    val bertDefinitionKo: String,
    @SerializedName("bert_source")
    val bertSource: String,
    @SerializedName("bert_confidence")
    val bertConfidence: Float,
    @SerializedName("wordnet_senses")
    val wordnetSenses: List<String>,
    @SerializedName("wordnet_senses_ko")
    val wordnetSensesKo: List<String>
)