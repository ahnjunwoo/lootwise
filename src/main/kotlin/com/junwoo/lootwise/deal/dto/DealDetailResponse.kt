package com.junwoo.lootwise.deal.dto

import java.math.BigDecimal

data class DealDetailResponse(
    val appId: Long,
    val name: String,
    val originalPrice: BigDecimal?,
    val finalPrice: BigDecimal,
    val discountPercent: Int,
    val reviewScoreDesc: String?,
    val steamUrl: String,
    val capsuleImageUrl: String?,
)
