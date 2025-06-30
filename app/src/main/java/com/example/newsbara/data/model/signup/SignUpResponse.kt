package com.example.newsbara.data.model.signup

data class SignUpResponse(
    val id: Int,
    val email: String,
    val phone: String,
    val name: String,
    val point: Int,
    val profileImg: String
)
