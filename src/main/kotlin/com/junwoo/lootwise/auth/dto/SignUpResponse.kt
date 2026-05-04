package com.junwoo.lootwise.auth.dto

data class SignUpResponse(
    val userId: Long,
    val email: String,
    val nickname: String,
)
