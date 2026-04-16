package com.junwoo.lootwise.steam.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "steam_review_summary")
class SteamReviewSummary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "app_id", nullable = false, unique = true)
    val appId: Long,
    @Column(name = "total_reviews", nullable = false)
    val totalReviews: Int,
    @Column(name = "total_positive", nullable = false)
    val totalPositive: Int,
    @Column(name = "total_negative", nullable = false)
    val totalNegative: Int,
    @Column(name = "review_score")
    val reviewScore: Int? = null,
    @Column(name = "review_score_desc")
    val reviewScoreDesc: String? = null,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime,
)
