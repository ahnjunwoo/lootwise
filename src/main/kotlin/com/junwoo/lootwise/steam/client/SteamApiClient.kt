package com.junwoo.lootwise.steam.client

import com.junwoo.lootwise.steam.dto.SteamAppListResponse
import com.junwoo.lootwise.steam.dto.SteamFeaturedCategoriesResponse
import com.junwoo.lootwise.steam.dto.SteamReviewSummaryApiResponse
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SteamApiClient(
    private val restClient: RestClient,
    private val steamApiProperties: SteamApiProperties,
) {
    fun fetchAppList(): SteamAppListResponse? =
        getSafely(
            baseUrl = steamApiProperties.baseUrl,
            path = "/api/applist",
            responseType = SteamAppListResponse::class.java,
        )

    fun fetchFeaturedCategories(): SteamFeaturedCategoriesResponse? =
        getSafely(
            baseUrl = steamApiProperties.baseUrl,
            path = "/api/featuredcategories",
            responseType = SteamFeaturedCategoriesResponse::class.java,
            queryParams = mapOf(
                "cc" to steamApiProperties.countryCode,
                "l" to steamApiProperties.language,
            ),
        )

    fun fetchReviewSummary(appId: Long): SteamReviewSummaryApiResponse? =
        getSafely(
            baseUrl = steamApiProperties.reviewsBaseUrl,
            path = "/appreviews/{appId}",
            responseType = SteamReviewSummaryApiResponse::class.java,
            uriVariables = mapOf("appId" to appId),
            queryParams = mapOf("json" to 1),
        )

    private fun <T> getSafely(
        baseUrl: String,
        path: String,
        responseType: Class<T>,
        uriVariables: Map<String, Any> = emptyMap(),
        queryParams: Map<String, Any> = emptyMap(),
    ): T? =
        runCatching {
            restClient.get()
                .uri("$baseUrl$path") { builder ->
                    queryParams.forEach { (key, value) ->
                        builder.queryParam(key, value)
                    }
                    builder.build(uriVariables)
                }
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, response ->
                    throw SteamApiClientException(
                        statusCode = response.statusCode.value(),
                        responseBody = response.body.readAllBytes().decodeToString(),
                    )
                }
                .body(responseType)
        }.getOrNull()
}
