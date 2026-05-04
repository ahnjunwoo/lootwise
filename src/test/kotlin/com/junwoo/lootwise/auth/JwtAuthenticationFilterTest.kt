package com.junwoo.lootwise.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.auth.config.AuthProperties
import com.junwoo.lootwise.auth.security.JwtAuthenticationFilter
import com.junwoo.lootwise.auth.service.JwtAccessTokenProvider
import com.junwoo.lootwise.auth.service.JwtPrincipal
import com.junwoo.lootwise.user.domain.UserRole
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilterTest : FunSpec({
    lateinit var provider: JwtAccessTokenProvider
    lateinit var filter: JwtAuthenticationFilter

    beforeTest {
        provider = JwtAccessTokenProvider(
            authProperties = AuthProperties(
                secret = "test-secret-that-is-long-enough",
                issuer = "lootwise-test",
                accessTokenExpiration = Duration.ofMinutes(15),
            ),
            clock = Clock.fixed(Instant.parse("2026-04-30T00:00:00Z"), ZoneOffset.UTC),
            objectMapper = ObjectMapper().findAndRegisterModules(),
        )
        filter = JwtAuthenticationFilter(provider, ObjectMapper().findAndRegisterModules())
        SecurityContextHolder.clearContext()
    }

    afterTest {
        SecurityContextHolder.clearContext()
    }

    test("stores authentication in security context for valid bearer token") {
        val token = provider.issue(userId = 10L, role = UserRole.USER)
        val request = MockHttpServletRequest("GET", "/api/v1/users/me")
        val response = MockHttpServletResponse()
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer ${token.token}")

        filter.doFilter(request, response, MockFilterChain())

        response.status shouldBe 200
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal as JwtPrincipal
        principal.userId shouldBe 10L
        principal.role shouldBe UserRole.USER
        authentication.authorities.single().authority shouldBe "ROLE_USER"
    }

    test("returns unauthorized for invalid bearer token") {
        val request = MockHttpServletRequest("GET", "/api/v1/users/me")
        val response = MockHttpServletResponse()
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.value")

        filter.doFilter(request, response, MockFilterChain())

        response.status shouldBe 401
        response.contentAsString shouldContain "Invalid or expired access token"
        SecurityContextHolder.getContext().authentication shouldBe null
    }

    test("passes through when authorization header is missing") {
        val request = MockHttpServletRequest("GET", "/api/v1/users/me")
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, MockFilterChain())

        response.status shouldBe 200
        SecurityContextHolder.getContext().authentication shouldBe null
    }
})
