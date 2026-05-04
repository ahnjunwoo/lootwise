package com.junwoo.lootwise.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email format is invalid")
    @field:Size(max = 255, message = "Email must be 255 characters or less")
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    val password: String,
    @field:NotBlank(message = "Nickname is required")
    @field:Size(min = 2, max = 30, message = "Nickname must be between 2 and 30 characters")
    @field:Pattern(
        regexp = "^[A-Za-z0-9가-힣_]+$",
        message = "Nickname can contain letters, numbers, Korean characters, and underscore",
    )
    val nickname: String,
)
