package com.junwoo.lootwise.deal.repository

import com.junwoo.lootwise.steam.domain.SteamReviewSummary
import org.springframework.data.jpa.repository.JpaRepository

interface SteamReviewSummaryRepository : JpaRepository<SteamReviewSummary, Long> {
    fun findByAppId(appId: Long): SteamReviewSummary?

    fun findByAppIdIn(appIds: Collection<Long>): List<SteamReviewSummary>
}
