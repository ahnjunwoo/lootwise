package com.junwoo.lootwise.steam.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamAppListResponse(
    @param:JsonProperty("applist")
    val appList: SteamAppListPayload,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamAppListPayload(
    @param:JsonProperty("apps")
    val apps: List<SteamAppDto> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamAppDto(
    @param:JsonProperty("appid")
    val appId: Long,
    @param:JsonProperty("name")
    val name: String,
    @param:JsonProperty("steam_url")
    val steamUrl: String? = null,
    @param:JsonProperty("capsule_image_url")
    val capsuleImageUrl: String? = null,
    @param:JsonProperty("original_price")
    val originalPrice: BigDecimal? = null,
    @param:JsonProperty("final_price")
    val finalPrice: BigDecimal? = null,
    @param:JsonProperty("discount_percent")
    val discountPercent: Int? = null,
)
