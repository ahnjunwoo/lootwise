package com.junwoo.lootwise.steam.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamFeaturedCategoriesResponse(
    @param:JsonProperty("specials")
    val specials: SteamSpecialsCategory = SteamSpecialsCategory(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamSpecialsCategory(
    @param:JsonProperty("items")
    val items: List<SteamSpecialItemDto> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamSpecialItemDto(
    @param:JsonProperty("id")
    val appId: Long,
    @param:JsonProperty("name")
    val name: String,
    @param:JsonProperty("discount_percent")
    val discountPercent: Int = 0,
    @param:JsonProperty("original_price")
    val originalPriceMinorUnit: Long? = null,
    @param:JsonProperty("final_price")
    val finalPriceMinorUnit: Long? = null,
    @param:JsonProperty("large_capsule_image")
    val capsuleImageUrl: String? = null,
)
