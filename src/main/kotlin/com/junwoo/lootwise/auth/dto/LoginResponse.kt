package com.junwoo.lootwise.auth.dto

data class LoginResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long,
)
