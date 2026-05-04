package com.junwoo.lootwise.common.config

import com.junwoo.lootwise.auth.config.AuthProperties
import com.junwoo.lootwise.steam.client.SteamApiProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.client.RestClient
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.net.http.HttpClient
import java.time.Clock

@Configuration
@EnableConfigurationProperties(
    AuthProperties::class,
    SteamApiProperties::class,
)
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

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun webMvcConfigurer(): WebMvcConfigurer =
        object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(
                        "http://localhost:3000",
                        "https://lootwise-web.vercel.app",
                    )
                    .allowedMethods("GET", "POST", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(false)
                    .maxAge(3600)
            }

            override fun addViewControllers(registry: ViewControllerRegistry) {
                registry.addRedirectViewController("/docs", "/docs/index.html")
                registry.addViewController("/docs/").setViewName("forward:/docs/index.html")
            }
        }
}
