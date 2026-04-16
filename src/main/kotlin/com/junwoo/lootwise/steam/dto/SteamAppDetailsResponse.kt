package com.junwoo.lootwise.steam.dto

import java.math.BigDecimal

data class SteamAppDetailsResponse(
    val appId: Long,
    val name: String,
    val originalPrice: BigDecimal,
    val discountedPrice: BigDecimal,
    val discountPercent: Int,
    val currency: String,
    val storeUrl: String,
)
