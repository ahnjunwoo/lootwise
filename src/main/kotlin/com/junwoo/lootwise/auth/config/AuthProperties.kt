package com.junwoo.lootwise.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "auth.jwt")
data class AuthProperties(
    val secret: String,
    val issuer: String = "lootwise",
    val accessTokenExpiration: Duration = Duration.ofMinutes(15),
)
