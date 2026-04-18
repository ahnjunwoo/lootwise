package com.junwoo.lootwise.batch.scheduler

import com.junwoo.lootwise.steam.service.SteamSyncService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StartupDealSyncRunner(
    private val steamSyncService: SteamSyncService,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun runInitialSync() {
        log.info("Initial Steam deal sync started")
        val result = steamSyncService.sync(emptyList())
        log.info(
            "Initial Steam deal sync finished. gamesSaved={}, priceSnapshotsSaved={}, reviewSummariesSaved={}",
            result.gamesSaved,
            result.priceSnapshotsSaved,
            result.reviewSummariesSaved,
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(StartupDealSyncRunner::class.java)
    }
}
