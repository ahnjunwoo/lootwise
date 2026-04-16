package com.junwoo.lootwise.steam.client

import com.junwoo.lootwise.steam.dto.SteamAppDetailsResponse

interface SteamStoreClient {
    fun fetchDiscountedApps(): List<SteamAppDetailsResponse>
}
