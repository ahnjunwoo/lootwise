package com.junwoo.lootwise.steam.client

import com.junwoo.lootwise.steam.dto.SteamAppDetailsResponse
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StubSteamStoreClient : SteamStoreClient {
    override fun fetchDiscountedApps(): List<SteamAppDetailsResponse> =
        listOf(
            SteamAppDetailsResponse(
                appId = 570L,
                name = "Dota 2 Starter Pack",
                originalPrice = BigDecimal("19.99"),
                discountedPrice = BigDecimal("9.99"),
                discountPercent = 50,
                currency = "USD",
                storeUrl = "https://store.steampowered.com/app/570",
            ),
        )
}
