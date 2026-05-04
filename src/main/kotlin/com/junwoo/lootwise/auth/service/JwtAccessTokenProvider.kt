package com.junwoo.lootwise.auth.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.junwoo.lootwise.auth.config.AuthProperties
import com.junwoo.lootwise.user.domain.UserRole
import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.time.Instant
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.springframework.stereotype.Component

@Component
class JwtAccessTokenProvider(
    private val authProperties: AuthProperties,
    private val clock: Clock,
    private val objectMapper: ObjectMapper,
) {
    private val mapType = object : TypeReference<Map<String, Any>>() {}

    fun issue(userId: Long, role: UserRole): IssuedAccessToken {
        val now = clock.instant()
        val expiresAt = now.plus(authProperties.accessTokenExpiration)
        val header = mapOf(
            "alg" to "HS256",
            "typ" to "JWT",
        )
        val payload = mapOf(
            "iss" to authProperties.issuer,
            "iat" to now.epochSecond,
            "exp" to expiresAt.epochSecond,
            "userId" to userId,
            "role" to role.name,
        )

        val encodedHeader = encodeJson(header)
        val encodedPayload = encodeJson(payload)
        val unsignedToken = "$encodedHeader.$encodedPayload"
        val signature = sign(unsignedToken)

        return IssuedAccessToken(
            token = "$unsignedToken.$signature",
            expiresInSeconds = authProperties.accessTokenExpiration.seconds,
        )
    }

    fun parse(token: String): JwtPrincipal {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw JwtAuthenticationException("Invalid JWT format")
        }

        val unsignedToken = "${parts[0]}.${parts[1]}"
        val expectedSignature = sign(unsignedToken)
        if (!MessageDigest.isEqual(expectedSignature.toByteArray(StandardCharsets.UTF_8), parts[2].toByteArray(StandardCharsets.UTF_8))) {
            throw JwtAuthenticationException("Invalid JWT signature")
        }

        val header = decodeJson(parts[0])
        if (header["alg"] != "HS256" || header["typ"] != "JWT") {
            throw JwtAuthenticationException("Invalid JWT header")
        }

        val payload = decodeJson(parts[1])
        if (payload["iss"] != authProperties.issuer) {
            throw JwtAuthenticationException("Invalid JWT issuer")
        }

        val expiresAt = epochSecond(payload["exp"], "exp")
        if (!expiresAt.isAfter(clock.instant())) {
            throw JwtAuthenticationException("JWT is expired")
        }

        val userId = longClaim(payload["userId"], "userId")
        val role = roleClaim(payload["role"])

        return JwtPrincipal(
            userId = userId,
            role = role,
        )
    }

    private fun encodeJson(value: Map<String, Any>): String =
        base64UrlEncoder.encodeToString(objectMapper.writeValueAsBytes(value))

    private fun decodeJson(value: String): Map<String, Any> =
        try {
            objectMapper.readValue(base64UrlDecoder.decode(value), mapType)
        } catch (exception: Exception) {
            throw JwtAuthenticationException("Invalid JWT payload")
        }

    private fun epochSecond(value: Any?, claimName: String): Instant {
        val epochSecond = longClaim(value, claimName)
        return Instant.ofEpochSecond(epochSecond)
    }

    private fun longClaim(value: Any?, claimName: String): Long =
        when (value) {
            is Number -> value.toLong()
            is String -> value.toLongOrNull()
            else -> null
        } ?: throw JwtAuthenticationException("Missing or invalid JWT claim: $claimName")

    private fun roleClaim(value: Any?): UserRole =
        try {
            UserRole.valueOf(value as? String ?: throw IllegalArgumentException())
        } catch (exception: IllegalArgumentException) {
            throw JwtAuthenticationException("Missing or invalid JWT claim: role")
        }

    private fun sign(unsignedToken: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(authProperties.secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        return base64UrlEncoder.encodeToString(mac.doFinal(unsignedToken.toByteArray(StandardCharsets.UTF_8)))
    }

    companion object {
        private val base64UrlEncoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
        private val base64UrlDecoder: Base64.Decoder = Base64.getUrlDecoder()
    }
}

data class JwtPrincipal(
    val userId: Long,
    val role: UserRole,
)

data class IssuedAccessToken(
    val token: String,
    val expiresInSeconds: Long,
)

class JwtAuthenticationException(
    message: String,
) : RuntimeException(message)
