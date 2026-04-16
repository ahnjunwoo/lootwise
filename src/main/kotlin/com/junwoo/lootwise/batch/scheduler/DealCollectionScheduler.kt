package com.junwoo.lootwise.batch.scheduler

import com.junwoo.lootwise.steam.service.SteamSyncService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DealCollectionScheduler(
    private val steamSyncService: SteamSyncService,
) {
    @Scheduled(cron = "0 0 */3 * * *")
    fun collectDeals() {
        log.info("Steam deal sync started")
        val result = steamSyncService.sync(emptyList())
        log.info(
            "Steam deal sync finished. gamesSaved={}, priceSnapshotsSaved={}, reviewSummariesSaved={}",
            result.gamesSaved,
            result.priceSnapshotsSaved,
            result.reviewSummariesSaved,
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(DealCollectionScheduler::class.java)
    }
}
