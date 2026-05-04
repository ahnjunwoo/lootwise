package com.junwoo.lootwise.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.auth.config.AuthProperties
import com.junwoo.lootwise.auth.config.SecurityConfig
import com.junwoo.lootwise.auth.service.JwtAccessTokenProvider
import com.junwoo.lootwise.user.domain.UserRole
import io.kotest.core.spec.style.FunSpec
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mock.web.MockServletContext
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

class SecurityConfigTest : FunSpec({
    val webApplicationContext = AnnotationConfigWebApplicationContext().apply {
        servletContext = MockServletContext()
        register(SecurityConfig::class.java, TestSecurityConfig::class.java)
        refresh()
    }
    val mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity())
        .build()
    val jwtAccessTokenProvider = webApplicationContext.getBean(JwtAccessTokenProvider::class.java)

    afterSpec {
        webApplicationContext.close()
    }

    test("permits public deal endpoint without token") {
        mockMvc.get("/api/v1/deals/public-test")
            .andExpect {
                status { isOk() }
            }
    }

    test("permits docs endpoint without token") {
        mockMvc.get("/docs/openapi.yaml")
            .andExpect {
                status { isOk() }
            }
    }

    test("requires authentication for protected endpoint") {
        mockMvc.get("/api/v1/users/me")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    test("allows protected endpoint with valid bearer token") {
        val token = jwtAccessTokenProvider.issue(userId = 10L, role = UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isOk() }
        }
    }
})

@Configuration
@EnableWebSecurity
@EnableWebMvc
class TestSecurityConfig {
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().findAndRegisterModules()

    @Bean
    fun authProperties(): AuthProperties =
        AuthProperties(
            secret = "test-secret-that-is-long-enough",
            issuer = "lootwise-test",
            accessTokenExpiration = Duration.ofMinutes(15),
        )

    @Bean
    fun clock(): Clock =
        Clock.fixed(Instant.parse("2026-04-30T00:00:00Z"), ZoneOffset.UTC)

    @Bean
    fun jwtAccessTokenProvider(
        authProperties: AuthProperties,
        clock: Clock,
        objectMapper: ObjectMapper,
    ): JwtAccessTokenProvider =
        JwtAccessTokenProvider(authProperties, clock, objectMapper)

    @Bean
    fun testController(): TestSecurityController = TestSecurityController()
}

@RestController
class TestSecurityController {
    @GetMapping("/docs/openapi.yaml")
    fun docs(): String = "openapi: 3.0.3"

    @GetMapping("/api/v1/deals/public-test")
    fun publicDeals(): String = "public"

    @GetMapping("/api/v1/users/me")
    fun me(): String = "me"
}
