package com.junwoo.lootwise.auth.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.common.exception.ApiErrorResponse
import com.junwoo.lootwise.auth.service.JwtAccessTokenProvider
import com.junwoo.lootwise.auth.service.JwtAuthenticationException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtAccessTokenProvider: JwtAccessTokenProvider,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = extractBearerToken(request)
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val principal = jwtAccessTokenProvider.parse(token)
            val authentication = UsernamePasswordAuthenticationToken(
                principal,
                null,
                listOf(SimpleGrantedAuthority("ROLE_${principal.role.name}")),
            )
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
        } catch (exception: JwtAuthenticationException) {
            SecurityContextHolder.clearContext()
            writeUnauthorizedResponse(response)
        }
    }

    private fun extractBearerToken(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null
        }

        return authorizationHeader.removePrefix(BEARER_PREFIX).trim().takeIf { it.isNotBlank() }
    }

    private fun writeUnauthorizedResponse(response: HttpServletResponse) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(
            response.outputStream,
            ApiErrorResponse(message = "Invalid or expired access token"),
        )
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
