package com.junwoo.lootwise.batch.scheduler

import com.junwoo.lootwise.steam.service.SteamSyncService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DealCollectionScheduler(
    private val steamSyncService: SteamSyncService,
) {
    @Scheduled(cron = "\${steam.batch.collect-cron}")
    fun collectDeals() {
        steamSyncService.collectLatestDeals()
    }
}
