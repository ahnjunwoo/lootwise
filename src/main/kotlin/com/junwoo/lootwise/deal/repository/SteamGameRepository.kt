package com.junwoo.lootwise.deal.repository

import com.junwoo.lootwise.steam.domain.SteamGame
import org.springframework.data.jpa.repository.JpaRepository

interface SteamGameRepository : JpaRepository<SteamGame, Long> {
    fun findByAppId(appId: Long): SteamGame?

    fun findByAppIdIn(appIds: Collection<Long>): List<SteamGame>
}
