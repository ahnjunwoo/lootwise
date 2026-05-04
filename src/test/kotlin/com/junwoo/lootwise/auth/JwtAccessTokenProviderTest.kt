package com.junwoo.lootwise.auth

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.auth.config.AuthProperties
import com.junwoo.lootwise.auth.service.JwtAuthenticationException
import com.junwoo.lootwise.auth.service.JwtAccessTokenProvider
import com.junwoo.lootwise.user.domain.UserRole
import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.Base64

class JwtAccessTokenProviderTest : FunSpec({
    test("issue creates access token with minimal claims") {
        val objectMapper = ObjectMapper()
        val provider = JwtAccessTokenProvider(
            authProperties = AuthProperties(
                secret = "test-secret-that-is-long-enough",
                issuer = "lootwise-test",
                accessTokenExpiration = Duration.ofMinutes(15),
            ),
            clock = Clock.fixed(Instant.parse("2026-04-30T00:00:00Z"), ZoneOffset.UTC),
            objectMapper = objectMapper,
        )

        val token = provider.issue(userId = 10L, role = UserRole.USER)
        val parts = token.token.split(".")
        val payload = objectMapper.readValue(
            Base64.getUrlDecoder().decode(parts[1]),
            object : TypeReference<Map<String, Any>>() {},
        )

        token.expiresInSeconds shouldBe 900L
        payload.keys shouldContainExactlyInAnyOrder listOf("iss", "iat", "exp", "userId", "role")
        payload["iss"] shouldBe "lootwise-test"
        payload["iat"] shouldBe 1777507200
        payload["exp"] shouldBe 1777508100
        payload["userId"] shouldBe 10
        payload["role"] shouldBe "USER"
    }

    test("parse validates signature and returns principal") {
        val provider = jwtProvider()
        val token = provider.issue(userId = 10L, role = UserRole.USER)

        val principal = provider.parse(token.token)

        principal.userId shouldBe 10L
        principal.role shouldBe UserRole.USER
    }

    test("parse rejects tampered signature") {
        val provider = jwtProvider()
        val token = provider.issue(userId = 10L, role = UserRole.USER).token
        val tamperedToken = token.dropLast(1) + "x"

        shouldThrow<JwtAuthenticationException> {
            provider.parse(tamperedToken)
        }
    }

    test("parse rejects expired token") {
        val issuingProvider = jwtProvider(
            clock = Clock.fixed(Instant.parse("2026-04-30T00:00:00Z"), ZoneOffset.UTC),
            expiration = Duration.ofSeconds(1),
        )
        val validatingProvider = jwtProvider(
            clock = Clock.fixed(Instant.parse("2026-04-30T00:00:02Z"), ZoneOffset.UTC),
            expiration = Duration.ofSeconds(1),
        )
        val token = issuingProvider.issue(userId = 10L, role = UserRole.USER)

        shouldThrow<JwtAuthenticationException> {
            validatingProvider.parse(token.token)
        }
    }
})

private fun jwtProvider(
    clock: Clock = Clock.fixed(Instant.parse("2026-04-30T00:00:00Z"), ZoneOffset.UTC),
    expiration: Duration = Duration.ofMinutes(15),
): JwtAccessTokenProvider =
    JwtAccessTokenProvider(
        authProperties = AuthProperties(
            secret = "test-secret-that-is-long-enough",
            issuer = "lootwise-test",
            accessTokenExpiration = expiration,
        ),
        clock = clock,
        objectMapper = ObjectMapper(),
    )
