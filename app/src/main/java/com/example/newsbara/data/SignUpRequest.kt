package com.example.newsbara.data

data class SignUpRequest(
    val email: String,
    val password: String,
    val phone: String,
    val name: String
)
