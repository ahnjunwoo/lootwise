package com.junwoo.lootwise.deal.repository

import com.junwoo.lootwise.steam.domain.SteamPriceSnapshot
import org.springframework.data.jpa.repository.JpaRepository

interface SteamPriceSnapshotRepository : JpaRepository<SteamPriceSnapshot, Long> {
    fun findByAppId(appId: Long): List<SteamPriceSnapshot>

    fun findByDiscountPercentGreaterThan(discountPercent: Int): List<SteamPriceSnapshot>

    fun findByAppIdIn(appIds: Collection<Long>): List<SteamPriceSnapshot>

    fun findTopByAppIdOrderByCollectedAtDesc(appId: Long): SteamPriceSnapshot?
}
