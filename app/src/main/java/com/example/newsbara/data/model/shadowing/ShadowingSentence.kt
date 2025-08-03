package com.example.newsbara.data.model.shadowing

import com.google.gson.annotations.SerializedName

data class PronunciationDto(
    val score: Int,
    @SerializedName("recognized_text")
    val recognizedText: String,
    val differences: List<DifferenceDto>
)

data class DifferenceDto(
    val expected: String,
    val pronounced: String
)