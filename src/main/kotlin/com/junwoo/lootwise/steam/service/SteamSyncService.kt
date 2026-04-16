package com.junwoo.lootwise.steam.service

import com.junwoo.lootwise.deal.repository.SteamGameRepository
import com.junwoo.lootwise.deal.repository.SteamPriceSnapshotRepository
import com.junwoo.lootwise.deal.repository.SteamReviewSummaryRepository
import com.junwoo.lootwise.steam.client.SteamApiClient
import com.junwoo.lootwise.steam.domain.SteamGame
import com.junwoo.lootwise.steam.domain.SteamPriceSnapshot
import com.junwoo.lootwise.steam.domain.SteamReviewSummary
import com.junwoo.lootwise.steam.dto.SteamAppDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class SteamSyncService(
    private val steamApiClient: SteamApiClient,
    private val steamGameRepository: SteamGameRepository,
    private val steamPriceSnapshotRepository: SteamPriceSnapshotRepository,
    private val steamReviewSummaryRepository: SteamReviewSummaryRepository,
) {
    fun sync(appIdsForReview: Collection<Long>): SteamSyncResult {
        val apps = steamApiClient.fetchAppList()?.appList?.apps.orEmpty()
        val savedGames = upsertGames(apps)
        val savedSnapshots = savePriceSnapshots(apps)
        val savedReviews = syncReviewSummaries(appIdsForReview)

        log.info(
            "Steam sync completed. gamesSaved={}, priceSnapshotsSaved={}, reviewSummariesSaved={}",
            savedGames,
            savedSnapshots,
            savedReviews,
        )

        return SteamSyncResult(
            gamesSaved = savedGames,
            priceSnapshotsSaved = savedSnapshots,
            reviewSummariesSaved = savedReviews,
        )
    }

    @Transactional
    fun upsertGames(apps: List<SteamAppDto>): Int {
        if (apps.isEmpty()) {
            return 0
        }

        val games = apps.map { app ->
            val existing = steamGameRepository.findByAppId(app.appId)
            toSteamGame(app, existing?.id, existing?.createdAt)
        }

        steamGameRepository.saveAll(games)
        return games.size
    }

    @Transactional
    fun savePriceSnapshots(apps: List<SteamAppDto>): Int {
        val snapshots = apps.mapNotNull(::toPriceSnapshot)
            .filterNot(::isDuplicateSnapshotWithinSameHour)
        if (snapshots.isEmpty()) {
            return 0
        }

        steamPriceSnapshotRepository.saveAll(snapshots)
        return snapshots.size
    }

    fun syncReviewSummaries(appIds: Collection<Long>): Int {
        if (appIds.isEmpty()) {
            return 0
        }

        val summaries = appIds.mapNotNull { appId ->
            val response = steamApiClient.fetchReviewSummary(appId) ?: return@mapNotNull null
            val existing = steamReviewSummaryRepository.findByAppId(appId)
            SteamReviewSummary(
                id = existing?.id,
                appId = appId,
                totalReviews = response.querySummary.totalReviews,
                totalPositive = response.querySummary.totalPositive,
                totalNegative = response.querySummary.totalNegative,
                reviewScore = response.querySummary.reviewScore,
                reviewScoreDesc = response.querySummary.reviewScoreDesc,
                updatedAt = LocalDateTime.now(),
            )
        }

        return saveReviewSummaries(summaries)
    }

    private fun isDuplicateSnapshotWithinSameHour(snapshot: SteamPriceSnapshot): Boolean {
        val latestSnapshot = steamPriceSnapshotRepository.findTopByAppIdOrderByCollectedAtDesc(snapshot.appId) ?: return false

        return latestSnapshot.collectedAt.truncatedTo(ChronoUnit.HOURS) ==
            snapshot.collectedAt.truncatedTo(ChronoUnit.HOURS)
    }

    @Transactional
    fun saveReviewSummaries(summaries: List<SteamReviewSummary>): Int {
        if (summaries.isEmpty()) {
            return 0
        }

        steamReviewSummaryRepository.saveAll(summaries)
        return summaries.size
    }

    private fun toSteamGame(
        app: SteamAppDto,
        id: Long?,
        createdAt: LocalDateTime?,
    ): SteamGame =
        SteamGame(
            id = id,
            appId = app.appId,
            name = app.name,
            steamUrl = app.steamUrl ?: "https://store.steampowered.com/app/${app.appId}",
            capsuleImageUrl = app.capsuleImageUrl,
            isActive = true,
            createdAt = createdAt,
            updatedAt = null,
        )

    private fun toPriceSnapshot(app: SteamAppDto): SteamPriceSnapshot? {
        val finalPrice = app.finalPrice ?: return null

        return SteamPriceSnapshot(
            appId = app.appId,
            originalPrice = app.originalPrice,
            finalPrice = finalPrice,
            discountPercent = app.discountPercent ?: 0,
            collectedAt = LocalDateTime.now(),
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(SteamSyncService::class.java)
    }
}

data class SteamSyncResult(
    val gamesSaved: Int,
    val priceSnapshotsSaved: Int,
    val reviewSummariesSaved: Int,
)
