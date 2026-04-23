package com.junwoo.lootwise.deal.dto

import java.math.BigDecimal

data class DealSearchRequest(
    val keyword: String? = null,
    val minDiscountPercent: Int? = null,
    val maxFinalPrice: BigDecimal? = null,
    val minReviewScore: Int? = null,
    val reviewScoreDesc: String? = null,
    val sort: DealSort = DealSort.DISCOUNT_DESC,
    val limit: Int = 20,
)

enum class DealSort {
    DISCOUNT_DESC,
    PRICE_ASC,
    REVIEW_COUNT_DESC,
    REVIEW_SCORE_DESC,
    LATEST_DESC,
}
