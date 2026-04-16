package com.junwoo.lootwise.deal.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "deals")
class Deal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val steamAppId: Long,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false, precision = 10, scale = 2)
    val originalPrice: BigDecimal,
    @Column(nullable = false, precision = 10, scale = 2)
    val discountedPrice: BigDecimal,
    @Column(nullable = false)
    val discountPercent: Int,
    @Column(nullable = false)
    val currency: String,
    @Column(nullable = false)
    val dealUrl: String,
    @Column(nullable = false)
    val collectedAt: Instant = Instant.now(),
)
