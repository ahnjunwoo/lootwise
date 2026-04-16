package com.junwoo.lootwise.steam.client

import com.junwoo.lootwise.steam.dto.SteamAppListResponse
import com.junwoo.lootwise.steam.dto.SteamReviewSummaryApiResponse
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class SteamApiClient(
    private val webClientBuilder: WebClient.Builder,
    private val steamApiProperties: SteamApiProperties,
) {
    fun fetchAppList(): SteamAppListResponse? =
        webClientBuilder
            .baseUrl(steamApiProperties.baseUrl)
            .build()
            .get()
            .uri("/api/applist")
            .retrieve()
            .retrieveSafely()
            .bodyToMono<SteamAppListResponse>()
            .timeout(steamApiProperties.timeout)
            .onErrorResume { Mono.empty() }
            .block()

    fun fetchReviewSummary(appId: Long): SteamReviewSummaryApiResponse? =
        webClientBuilder
            .baseUrl(steamApiProperties.reviewsBaseUrl)
            .build()
            .get()
            .uri("/appreviews/{appId}?json=1", appId)
            .retrieve()
            .retrieveSafely()
            .bodyToMono<SteamReviewSummaryApiResponse>()
            .timeout(steamApiProperties.timeout)
            .onErrorResume { Mono.empty() }
            .block()

    private fun WebClient.ResponseSpec.retrieveSafely(): WebClient.ResponseSpec =
        onStatus(HttpStatusCode::isError) { response ->
            response
                .bodyToMono<String>()
                .defaultIfEmpty("")
                .map { body ->
                    SteamApiClientException(
                        statusCode = response.statusCode().value(),
                        responseBody = body,
                    )
                }
        }
}
