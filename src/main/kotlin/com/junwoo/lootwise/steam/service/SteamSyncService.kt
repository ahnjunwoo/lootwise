package com.junwoo.lootwise.steam.service

import com.junwoo.lootwise.deal.repository.SteamGameRepository
import com.junwoo.lootwise.deal.repository.SteamPriceSnapshotRepository
import com.junwoo.lootwise.deal.repository.SteamReviewSummaryRepository
import com.junwoo.lootwise.steam.client.SteamApiClient
import com.junwoo.lootwise.steam.client.SteamApiProperties
import com.junwoo.lootwise.steam.domain.SteamGame
import com.junwoo.lootwise.steam.domain.SteamPriceSnapshot
import com.junwoo.lootwise.steam.domain.SteamReviewSummary
import com.junwoo.lootwise.steam.dto.SteamAppDto
import com.junwoo.lootwise.steam.dto.SteamSpecialItemDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class SteamSyncService(
    private val steamApiClient: SteamApiClient,
    private val steamApiProperties: SteamApiProperties,
    private val steamGameRepository: SteamGameRepository,
    private val steamPriceSnapshotRepository: SteamPriceSnapshotRepository,
    private val steamReviewSummaryRepository: SteamReviewSummaryRepository,
) {
    fun sync(appIdsForReview: Collection<Long>): SteamSyncResult {
        val appListApps = steamApiClient.fetchAppList()?.appList?.apps.orEmpty()
        val featuredSpecialItems = steamApiClient.fetchFeaturedCategories()?.specials?.items.orEmpty()
        val apps = mergeApps(appListApps, featuredSpecialItems)
        val savedGames = upsertGames(apps)
        val savedSnapshots = savePriceSnapshots(apps)
        val reviewAppIds = resolveReviewAppIds(appIdsForReview, apps)
        val savedReviews = syncReviewSummaries(reviewAppIds)

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
        val snapshots = apps.asSequence()
            .mapNotNull(::toPriceSnapshot)
            .filterNot(::isDuplicateSnapshotWithinSameHour)
            .toList()
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

    private fun mergeApps(
        appListApps: List<SteamAppDto>,
        featuredSpecialItems: List<SteamSpecialItemDto>,
    ): List<SteamAppDto> {
        val appListById = appListApps.associateBy { it.appId }
        val mergedSpecials = featuredSpecialItems.map { special ->
            val appListApp = appListById[special.appId]
            SteamAppDto(
                appId = special.appId,
                name = special.name.ifBlank { appListApp?.name.orEmpty() },
                steamUrl = "https://store.steampowered.com/app/${special.appId}",
                capsuleImageUrl = special.capsuleImageUrl ?: appListApp?.capsuleImageUrl,
                originalPrice = special.originalPriceMinorUnit?.toPriceAmount(),
                finalPrice = special.finalPriceMinorUnit?.toPriceAmount(),
                discountPercent = special.discountPercent,
            )
        }

        val mergedById = mergedSpecials.associateBy { it.appId }.toMutableMap()
        appListApps.forEach { app ->
            mergedById.putIfAbsent(app.appId, app)
        }

        return mergedById.values.toList()
    }

    private fun resolveReviewAppIds(
        requestedAppIds: Collection<Long>,
        apps: List<SteamAppDto>,
    ): List<Long> {
        if (requestedAppIds.isNotEmpty()) {
            return requestedAppIds.distinct()
        }

        return apps.asSequence()
            .filter { (it.discountPercent ?: 0) > 0 }
            .sortedByDescending { it.discountPercent ?: 0 }
            .map { it.appId }
            .take(steamApiProperties.reviewSyncLimit)
            .toList()
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
        val discountPercent = app.discountPercent ?: 0
        if (discountPercent <= 0) {
            return null
        }

        return SteamPriceSnapshot(
            appId = app.appId,
            originalPrice = app.originalPrice,
            finalPrice = finalPrice,
            discountPercent = discountPercent,
            collectedAt = LocalDateTime.now(),
        )
    }

    private fun Long.toPriceAmount(): BigDecimal =
        BigDecimal.valueOf(this)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)

    companion object {
        private val log = LoggerFactory.getLogger(SteamSyncService::class.java)
    }
}

data class SteamSyncResult(
    val gamesSaved: Int,
    val priceSnapshotsSaved: Int,
    val reviewSummariesSaved: Int,
)
