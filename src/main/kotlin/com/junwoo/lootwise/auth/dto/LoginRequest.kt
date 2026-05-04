package com.junwoo.lootwise.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email format is invalid")
    @field:Size(max = 255, message = "Email must be 255 characters or less")
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(max = 72, message = "Password must be 72 characters or less")
    val password: String,
)
