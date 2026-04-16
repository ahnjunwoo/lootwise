package com.junwoo.lootwise.deal.dto

import java.math.BigDecimal
import java.time.Instant

data class DealSummaryResponse(
    val id: Long,
    val steamAppId: Long,
    val title: String,
    val originalPrice: BigDecimal,
    val discountedPrice: BigDecimal,
    val discountPercent: Int,
    val currency: String,
    val dealUrl: String,
    val collectedAt: Instant,
)
