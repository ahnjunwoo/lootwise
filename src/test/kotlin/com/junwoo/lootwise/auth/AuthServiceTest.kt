package com.junwoo.lootwise.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.auth.config.AuthProperties
import com.junwoo.lootwise.auth.dto.LoginRequest
import com.junwoo.lootwise.auth.dto.SignUpRequest
import com.junwoo.lootwise.auth.exception.DuplicateEmailException
import com.junwoo.lootwise.auth.exception.InvalidLoginException
import com.junwoo.lootwise.auth.service.AuthService
import com.junwoo.lootwise.auth.service.JwtAccessTokenProvider
import com.junwoo.lootwise.user.domain.User
import com.junwoo.lootwise.user.domain.UserStatus
import com.junwoo.lootwise.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import org.mockito.Mockito
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthServiceTest : FunSpec({
    test("sign up stores normalized email and BCrypt password hash") {
        val userRepository = Mockito.mock(UserRepository::class.java)
        val passwordEncoder = BCryptPasswordEncoder()
        val savedUsers = mutableListOf<User>()
        val authService = authService(userRepository, passwordEncoder)

        Mockito.`when`(userRepository.existsByEmail("user@example.com")).thenReturn(false)
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenAnswer { invocation ->
            val user = invocation.arguments[0] as User
            savedUsers.add(user)
            User(
                id = 1L,
                email = user.email,
                nickname = user.nickname,
                passwordHash = user.passwordHash,
                role = user.role,
                status = user.status,
            )
        }

        val response = authService.signUp(
            SignUpRequest(
                email = " User@Example.com ",
                password = "Password123!",
                nickname = "loot_user",
            )
        )

        response.userId shouldBe 1L
        response.email shouldBe "user@example.com"
        response.nickname shouldBe "loot_user"
        savedUsers.single().email shouldBe "user@example.com"
        savedUsers.single().passwordHash shouldNotBe "Password123!"
        passwordEncoder.matches("Password123!", savedUsers.single().passwordHash).shouldBeTrue()
    }

    test("sign up rejects duplicated email") {
        val userRepository = Mockito.mock(UserRepository::class.java)
        val authService = authService(userRepository)

        Mockito.`when`(userRepository.existsByEmail("user@example.com")).thenReturn(true)

        shouldThrow<DuplicateEmailException> {
            authService.signUp(
                SignUpRequest(
                    email = "user@example.com",
                    password = "Password123!",
                    nickname = "loot_user",
                )
            )
        }

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User::class.java))
    }

    test("login returns access token for active user with valid password") {
        val userRepository = Mockito.mock(UserRepository::class.java)
        val passwordEncoder = BCryptPasswordEncoder()
        val authService = authService(userRepository, passwordEncoder)
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(
            User(
                id = 1L,
                email = "user@example.com",
                nickname = "loot_user",
                passwordHash = passwordEncoder.encode("Password123!"),
            )
        )

        val response = authService.login(
            LoginRequest(
                email = " User@Example.com ",
                password = "Password123!",
            )
        )

        response.tokenType shouldBe "Bearer"
        response.expiresInSeconds shouldBe 900L
        response.accessToken.split(".").size shouldBe 3
    }

    test("login rejects missing email with same error") {
        val userRepository = Mockito.mock(UserRepository::class.java)
        val authService = authService(userRepository)
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(null)

        shouldThrow<InvalidLoginException> {
            authService.login(
                LoginRequest(
                    email = "user@example.com",
                    password = "Password123!",
                )
            )
        }.message shouldBe "Invalid email or password"
    }

    test("login rejects invalid password with same error") {
        val userRepository = Mockito.mock(UserRepository::class.java)
        val passwordEncoder = BCryptPasswordEncoder()
        val authService = authService(userRepository, passwordEncoder)
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(
            User(
                id = 1L,
                email = "user@example.com",
                nickname = "loot_user",
                passwordHash = passwordEncoder.encode("Password123!"),
            )
        )

        shouldThrow<InvalidLoginException> {
            authService.login(
                LoginRequest(
                    email = "user@example.com",
                    password = "WrongPassword123!",
                )
            )
        }.message shouldBe "Invalid email or password"
    }

    test("login rejects withdrawn user with same error") {
        val userRepository = Mockito.mock(UserRepository::class.java)
        val passwordEncoder = BCryptPasswordEncoder()
        val authService = authService(userRepository, passwordEncoder)
        Mockito.`when`(userRepository.findByEmail("user@example.com")).thenReturn(
            User(
                id = 1L,
                email = "user@example.com",
                nickname = "loot_user",
                passwordHash = passwordEncoder.encode("Password123!"),
                status = UserStatus.WITHDRAWN,
            )
        )

        shouldThrow<InvalidLoginException> {
            authService.login(
                LoginRequest(
                    email = "user@example.com",
                    password = "Password123!",
                )
            )
        }.message shouldBe "Invalid email or password"
    }
})

private fun authService(
    userRepository: UserRepository,
    passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
): AuthService =
    AuthService(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder,
        jwtAccessTokenProvider = JwtAccessTokenProvider(
            authProperties = AuthProperties(
                secret = "test-secret-that-is-long-enough",
                issuer = "lootwise-test",
                accessTokenExpiration = Duration.ofMinutes(15),
            ),
            clock = Clock.fixed(Instant.parse("2026-04-30T00:00:00Z"), ZoneOffset.UTC),
            objectMapper = ObjectMapper(),
        ),
    )
