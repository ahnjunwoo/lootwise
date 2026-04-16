package com.junwoo.lootwise.common.config

import com.junwoo.lootwise.steam.client.SteamApiProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import java.time.Clock

@Configuration
@EnableConfigurationProperties(SteamApiProperties::class)
class AppConfig {
    @Bean
    fun clock(): Clock = Clock.systemUTC()
}
