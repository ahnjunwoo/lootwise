package com.junwoo.lootwise.auth.service

import com.junwoo.lootwise.auth.dto.SignUpRequest
import com.junwoo.lootwise.auth.dto.SignUpResponse
import com.junwoo.lootwise.auth.dto.LoginRequest
import com.junwoo.lootwise.auth.dto.LoginResponse
import com.junwoo.lootwise.auth.exception.DuplicateEmailException
import com.junwoo.lootwise.auth.exception.InvalidLoginException
import com.junwoo.lootwise.user.domain.User
import com.junwoo.lootwise.user.domain.UserStatus
import com.junwoo.lootwise.user.repository.UserRepository
import java.util.Locale
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtAccessTokenProvider: JwtAccessTokenProvider,
) {
    @Transactional
    fun signUp(request: SignUpRequest): SignUpResponse {
        val normalizedEmail = normalizeEmail(request.email)
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw DuplicateEmailException(normalizedEmail)
        }

        val user = userRepository.save(
            User(
                email = normalizedEmail,
                nickname = request.nickname.trim(),
                passwordHash = passwordEncoder.encode(request.password),
            )
        )

        return SignUpResponse(
            userId = requireNotNull(user.id) { "Saved user id must not be null" },
            email = user.email,
            nickname = user.nickname,
        )
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val normalizedEmail = normalizeEmail(request.email)
        val user = userRepository.findByEmail(normalizedEmail)
            ?: throw InvalidLoginException()

        if (user.status == UserStatus.WITHDRAWN || !passwordEncoder.matches(request.password, user.passwordHash)) {
            throw InvalidLoginException()
        }

        val accessToken = jwtAccessTokenProvider.issue(
            userId = requireNotNull(user.id) { "User id must not be null" },
            role = user.role,
        )

        return LoginResponse(
            accessToken = accessToken.token,
            expiresInSeconds = accessToken.expiresInSeconds,
        )
    }

    private fun normalizeEmail(email: String): String =
        email.trim().lowercase(Locale.ROOT)
}
