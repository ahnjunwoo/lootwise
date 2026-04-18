package com.junwoo.lootwise.common.config

import com.junwoo.lootwise.steam.client.SteamApiProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.http.HttpClient
import java.time.Clock

@Configuration
@EnableConfigurationProperties(SteamApiProperties::class)
class AppConfig {
    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean
    fun restClient(): RestClient {
        val httpClient = HttpClient.newBuilder().build()
        return RestClient.builder()
            .requestFactory(JdkClientHttpRequestFactory(httpClient))
            .build()
    }
}
