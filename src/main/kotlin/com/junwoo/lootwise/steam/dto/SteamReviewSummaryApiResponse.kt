package com.junwoo.lootwise.steam.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamReviewSummaryApiResponse(
    @param:JsonProperty("success")
    val success: Int? = null,
    @param:JsonProperty("query_summary")
    val querySummary: SteamReviewQuerySummary = SteamReviewQuerySummary(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamReviewQuerySummary(
    @param:JsonProperty("total_reviews")
    val totalReviews: Int = 0,
    @param:JsonProperty("total_positive")
    val totalPositive: Int = 0,
    @param:JsonProperty("total_negative")
    val totalNegative: Int = 0,
    @param:JsonProperty("review_score")
    val reviewScore: Int? = null,
    @param:JsonProperty("review_score_desc")
    val reviewScoreDesc: String? = null,
)
