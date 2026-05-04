package com.junwoo.lootwise.auth.controller

import com.junwoo.lootwise.auth.dto.LoginRequest
import com.junwoo.lootwise.auth.dto.LoginResponse
import com.junwoo.lootwise.auth.dto.SignUpRequest
import com.junwoo.lootwise.auth.dto.SignUpResponse
import com.junwoo.lootwise.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(
        @Valid
        @RequestBody
        request: SignUpRequest,
    ): SignUpResponse = authService.signUp(request)

    @PostMapping("/login")
    fun login(
        @Valid
        @RequestBody
        request: LoginRequest,
    ): LoginResponse = authService.login(request)
}
