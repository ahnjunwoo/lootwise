package com.junwoo.lootwise.auth.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.auth.security.JwtAuthenticationFilter
import com.junwoo.lootwise.auth.service.JwtAccessTokenProvider
import com.junwoo.lootwise.common.exception.ApiErrorResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAccessTokenProvider: JwtAccessTokenProvider,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.status = HttpStatus.UNAUTHORIZED.value()
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    objectMapper.writeValue(
                        response.outputStream,
                        ApiErrorResponse(message = "Authentication is required"),
                    )
                }
            }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/auth/signup",
                    "/api/v1/auth/login",
                    "/api/v1/deals/**",
                    "/docs",
                    "/docs/**",
                    "/actuator/health",
                    "/actuator/info",
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtAccessTokenProvider, objectMapper),
                UsernamePasswordAuthenticationFilter::class.java,
            )
            .build()
}
