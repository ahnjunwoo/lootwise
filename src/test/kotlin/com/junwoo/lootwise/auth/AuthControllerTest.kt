package com.junwoo.lootwise.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.auth.config.AuthProperties
import com.junwoo.lootwise.auth.dto.LoginRequest
import com.junwoo.lootwise.auth.dto.SignUpRequest
import com.junwoo.lootwise.auth.service.AuthService
import com.junwoo.lootwise.auth.service.JwtAccessTokenProvider
import com.junwoo.lootwise.common.exception.GlobalExceptionHandler
import com.junwoo.lootwise.user.domain.User
import com.junwoo.lootwise.user.repository.UserRepository
import io.kotest.core.spec.style.FunSpec
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest : FunSpec({
    lateinit var userRepository: UserRepository
    lateinit var mockMvc: MockMvc

    beforeTest {
        userRepository = Mockito.mock(UserRepository::class.java)
        val authService = AuthService(
            userRepository = userRepository,
            passwordEncoder = BCryptPasswordEncoder(),
            jwtAccessTokenProvider = JwtAccessTokenProvider(
                authProperties = AuthProperties(
                    secret = "test-secret-that-is-long-enough",
                    issuer = "lootwise-test",
                    accessTokenExpiration = Duration.ofMinutes(15),
                ),
                clock = Clock.fixed(Instant.parse("2026-04-30T00:00:00Z"), ZoneOffset.UTC),
                objectMapper = objectMapper,
            ),
        )
        mockMvc = MockMvcBuilders
            .standaloneSetup(com.junwoo.lootwise.auth.controller.AuthController(authService))
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    test("sign up returns created response") {
        Mockito.`when`(userRepository.existsByEmail("user@example.com")).thenReturn(false)
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenAnswer { invocation ->
            val user = invocation.arguments[0] as User
            User(
                id = 1L,
                email = user.email,
                nickname = user.nickname,
                passwordHash = user.passwordHash,
                role = user.role,
                status = user.status,
            )
        }

        mockMvc.post("/api/v1/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                SignUpRequest(
                    email = "user@example.com",
                    password = "Password123!",
                    nickname = "loot_user",
                )
            )
        }.andExpect {
            status { isCreated() }
            jsonPath("$.userId") { value(1) }
            jsonPath("$.email") { value("user@example.com") }
            jsonPath("$.nickname") { value("loot_user") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("sign up returns conflict when email already exists") {
        Mockito.`when`(userRepository.existsByEmail("user@example.com")).thenReturn(true)

        mockMvc.post("/api/v1/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                SignUpRequest(
                    email = "user@example.com",
                    password = "Password123!",
                    nickname = "loot_user",
                )
            )
        }.andExpect {
            status { isConflict() }
            jsonPath("$.message") { value("Email already exists: user@example.com") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("login returns access token") {
        val passwordEncoder = BCryptPasswordEncoder()
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(
            User(
                id = 1L,
                email = "user@example.com",
                nickname = "loot_user",
                passwordHash = passwordEncoder.encode("Password123!"),
            )
        )

        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                LoginRequest(
                    email = "user@example.com",
                    password = "Password123!",
                )
            )
        }.andExpect {
            status { isOk() }
            jsonPath("$.tokenType") { value("Bearer") }
            jsonPath("$.expiresInSeconds") { value(900) }
            jsonPath("$.accessToken") { exists() }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("login returns unauthorized response when email does not exist") {
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(null)

        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                LoginRequest(
                    email = "user@example.com",
                    password = "Password123!",
                )
            )
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Invalid email or password") }
            jsonPath("$.accessToken") { doesNotExist() }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("login returns unauthorized response when password does not match") {
        val passwordEncoder = BCryptPasswordEncoder()
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(
            User(
                id = 1L,
                email = "user@example.com",
                nickname = "loot_user",
                passwordHash = passwordEncoder.encode("Password123!"),
            )
        )

        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                LoginRequest(
                    email = "user@example.com",
                    password = "WrongPassword123!",
                )
            )
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Invalid email or password") }
            jsonPath("$.accessToken") { doesNotExist() }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }
})

private val objectMapper = ObjectMapper().findAndRegisterModules()
