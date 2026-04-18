package com.junwoo.lootwise.steam.client

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "steam.api")
data class SteamApiProperties(
    val baseUrl: String,
    val reviewsBaseUrl: String,
    val countryCode: String = "KR",
    val language: String = "koreana",
    val reviewSyncLimit: Int = 20,
    val timeout: Duration = Duration.ofSeconds(5),
)
