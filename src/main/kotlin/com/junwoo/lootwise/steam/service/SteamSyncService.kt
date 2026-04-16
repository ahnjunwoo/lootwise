package com.junwoo.lootwise.steam.service

import com.junwoo.lootwise.deal.domain.Deal
import com.junwoo.lootwise.deal.repository.DealRepository
import com.junwoo.lootwise.steam.client.SteamStoreClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SteamSyncService(
    private val steamStoreClient: SteamStoreClient,
    private val dealRepository: DealRepository,
) {
    @Transactional
    fun collectLatestDeals() {
        val deals = steamStoreClient.fetchDiscountedApps()
            .map { app ->
                Deal(
                    steamAppId = app.appId,
                    title = app.name,
                    originalPrice = app.originalPrice,
                    discountedPrice = app.discountedPrice,
                    discountPercent = app.discountPercent,
                    currency = app.currency,
                    dealUrl = app.storeUrl,
                )
            }

        dealRepository.saveAll(deals)
    }
}
