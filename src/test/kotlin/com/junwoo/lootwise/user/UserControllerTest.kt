package com.junwoo.lootwise.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.auth.config.AuthProperties
import com.junwoo.lootwise.auth.config.SecurityConfig
import com.junwoo.lootwise.auth.service.JwtAccessTokenProvider
import com.junwoo.lootwise.common.exception.GlobalExceptionHandler
import com.junwoo.lootwise.user.controller.UserController
import com.junwoo.lootwise.user.domain.User
import com.junwoo.lootwise.user.domain.UserStatus
import com.junwoo.lootwise.user.repository.UserRepository
import com.junwoo.lootwise.user.service.UserQueryService
import io.kotest.core.spec.style.FunSpec
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.Optional
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mock.web.MockServletContext
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

class UserControllerTest : FunSpec({
    val webApplicationContext = AnnotationConfigWebApplicationContext().apply {
        servletContext = MockServletContext()
        register(SecurityConfig::class.java, UserControllerTestConfig::class.java)
        refresh()
    }
    val mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity())
        .build()
    val userRepository = webApplicationContext.getBean(UserRepository::class.java)
    val jwtAccessTokenProvider = webApplicationContext.getBean(JwtAccessTokenProvider::class.java)

    beforeTest {
        Mockito.reset(userRepository)
    }

    afterSpec {
        webApplicationContext.close()
    }

    test("get me returns authenticated user info without password hash") {
        Mockito.`when`(userRepository.findById(1L)).thenReturn(
            Optional.of(
                User(
                    id = 1L,
                    email = "user@example.com",
                    nickname = "loot_user",
                    passwordHash = "hashed-password",
                )
            )
        )
        val token = jwtAccessTokenProvider.issue(userId = 1L, role = com.junwoo.lootwise.user.domain.UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isOk() }
            jsonPath("$.userId") { value(1) }
            jsonPath("$.email") { value("user@example.com") }
            jsonPath("$.nickname") { value("loot_user") }
            jsonPath("$.role") { value("USER") }
            jsonPath("$.status") { value("ACTIVE") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me requires authentication") {
        mockMvc.get("/api/v1/users/me")
            .andExpect {
                status { isUnauthorized() }
                jsonPath("$.message") { value("Authentication is required") }
                jsonPath("$.passwordHash") { doesNotExist() }
            }
    }

    test("get me rejects invalid token") {
        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer invalid.token.value")
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Invalid or expired access token") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me rejects tampered token") {
        val token = jwtAccessTokenProvider.issue(userId = 1L, role = com.junwoo.lootwise.user.domain.UserRole.USER)
        val tamperedToken = token.token.dropLast(1) + "x"

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer $tamperedToken")
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Invalid or expired access token") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me rejects expired token") {
        val expiredTokenProvider = JwtAccessTokenProvider(
            authProperties = AuthProperties(
                secret = "test-secret-that-is-long-enough",
                issuer = "lootwise-test",
                accessTokenExpiration = Duration.ofSeconds(1),
            ),
            clock = Clock.fixed(Instant.parse("2026-04-29T23:59:00Z"), ZoneOffset.UTC),
            objectMapper = ObjectMapper().findAndRegisterModules(),
        )
        val token = expiredTokenProvider.issue(userId = 1L, role = com.junwoo.lootwise.user.domain.UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Invalid or expired access token") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me returns not found when token user no longer exists") {
        Mockito.`when`(userRepository.findById(1L)).thenReturn(Optional.empty())
        val token = jwtAccessTokenProvider.issue(userId = 1L, role = com.junwoo.lootwise.user.domain.UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message") { value("User not found: 1") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me ignores token role for response and returns persisted user role") {
        Mockito.`when`(userRepository.findById(1L)).thenReturn(
            Optional.of(
                User(
                    id = 1L,
                    email = "user@example.com",
                    nickname = "loot_user",
                    passwordHash = "hashed-password",
                )
            )
        )
        val token = jwtAccessTokenProvider.issue(userId = 1L, role = com.junwoo.lootwise.user.domain.UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isOk() }
            jsonPath("$.role") { value("USER") }
            jsonPath("$.passwordHash") { doesNotExist() }
            jsonPath("$.password") { doesNotExist() }
        }
    }

    test("get me rejects non bearer authorization header as unauthenticated") {
        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Basic invalid")
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Authentication is required") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me rejects blank bearer token as unauthenticated") {
        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ")
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Authentication is required") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me response does not include password field") {
        Mockito.`when`(userRepository.findById(1L)).thenReturn(
            Optional.of(
                User(
                    id = 1L,
                    email = "user@example.com",
                    nickname = "loot_user",
                    passwordHash = "hashed-password",
                )
            )
        )
        val token = jwtAccessTokenProvider.issue(userId = 1L, role = com.junwoo.lootwise.user.domain.UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isOk() }
            jsonPath("$.password") { doesNotExist() }
            jsonPath("$.passwordHash") { doesNotExist() }
            jsonPath("$.userId") { value(1) }
            jsonPath("$.email") { value("user@example.com") }
            jsonPath("$.nickname") { value("loot_user") }
            jsonPath("$.status") { value("ACTIVE") }
            jsonPath("$.role") { value("USER") }
        }
    }

    test("get me returns invalid token response for malformed bearer token") {
        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer malformed")
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Invalid or expired access token") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me is independent per test data") {
        Mockito.`when`(userRepository.findById(2L)).thenReturn(
            Optional.of(
                User(
                    id = 2L,
                    email = "second@example.com",
                    nickname = "second_user",
                    passwordHash = "another-hashed-password",
                )
            )
        )
        val token = jwtAccessTokenProvider.issue(userId = 2L, role = com.junwoo.lootwise.user.domain.UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isOk() }
            jsonPath("$.userId") { value(2) }
            jsonPath("$.email") { value("second@example.com") }
            jsonPath("$.nickname") { value("second_user") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    test("get me returns authenticated user info with exact JSON field names") {
        Mockito.`when`(userRepository.findById(1L)).thenReturn(
            Optional.of(
                User(
                    id = 1L,
                    email = "user@example.com",
                    nickname = "loot_user",
                    passwordHash = "hashed-password",
                )
            )
        )
        val token = jwtAccessTokenProvider.issue(userId = 1L, role = com.junwoo.lootwise.user.domain.UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isOk() }
            jsonPath("$.userId") { value(1) }
            jsonPath("$.email") { value("user@example.com") }
            jsonPath("$.nickname") { value("loot_user") }
            jsonPath("$.role") { value("USER") }
            jsonPath("$.status") { value("ACTIVE") }
        }
    }

    test("get me rejects withdrawn user") {
        Mockito.`when`(userRepository.findById(1L)).thenReturn(
            Optional.of(
                User(
                    id = 1L,
                    email = "user@example.com",
                    nickname = "loot_user",
                    passwordHash = "hashed-password",
                    status = UserStatus.WITHDRAWN,
                )
            )
        )
        val token = jwtAccessTokenProvider.issue(userId = 1L, role = com.junwoo.lootwise.user.domain.UserRole.USER)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer ${token.token}")
        }.andExpect {
            status { isForbidden() }
            jsonPath("$.message") { value("User is withdrawn: 1") }
        }
    }
})

@Configuration
@EnableWebSecurity
@EnableWebMvc
class UserControllerTestConfig {
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
    fun userRepository(): UserRepository = Mockito.mock(UserRepository::class.java)

    @Bean
    fun userQueryService(userRepository: UserRepository): UserQueryService = UserQueryService(userRepository)

    @Bean
    fun userController(userQueryService: UserQueryService): UserController = UserController(userQueryService)

    @Bean
    fun globalExceptionHandler(): GlobalExceptionHandler = GlobalExceptionHandler()
}
