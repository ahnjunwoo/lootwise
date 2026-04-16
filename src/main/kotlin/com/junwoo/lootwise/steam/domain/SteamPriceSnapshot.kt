package com.junwoo.lootwise.steam.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "steam_price_snapshot")
class SteamPriceSnapshot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "app_id", nullable = false)
    val appId: Long,
    @Column(name = "original_price", precision = 10, scale = 2)
    val originalPrice: BigDecimal? = null,
    @Column(name = "final_price", precision = 10, scale = 2)
    val finalPrice: BigDecimal? = null,
    @Column(name = "discount_percent", nullable = false)
    val discountPercent: Int,
    @Column(name = "collected_at", nullable = false)
    val collectedAt: LocalDateTime,
)
