package com.junwoo.lootwise.deal.service

import com.junwoo.lootwise.deal.dto.DealDetailResponse
import com.junwoo.lootwise.deal.dto.DealSummaryResponse
import com.junwoo.lootwise.deal.repository.SteamGameRepository
import com.junwoo.lootwise.deal.repository.SteamPriceSnapshotRepository
import com.junwoo.lootwise.deal.repository.SteamReviewSummaryRepository
import com.junwoo.lootwise.steam.domain.SteamGame
import com.junwoo.lootwise.steam.domain.SteamPriceSnapshot
import com.junwoo.lootwise.steam.domain.SteamReviewSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DealQueryService(
    private val steamGameRepository: SteamGameRepository,
    private val steamPriceSnapshotRepository: SteamPriceSnapshotRepository,
    private val steamReviewSummaryRepository: SteamReviewSummaryRepository,
) {
    @Transactional(readOnly = true)
    fun getTopDeals(limit: Int): List<DealSummaryResponse> {
        require(limit > 0) { "limit must be greater than 0" }

        val latestDiscountedSnapshots = findLatestDiscountedSnapshots()
        if (latestDiscountedSnapshots.isEmpty()) {
            return emptyList()
        }

        val appIds = latestDiscountedSnapshots.map { it.appId }
        val gamesByAppId = steamGameRepository.findByAppIdIn(appIds).associateBy { it.appId }
        val reviewsByAppId = steamReviewSummaryRepository.findByAppIdIn(appIds).associateBy { it.appId }

        return buildTopDeals(
            snapshots = latestDiscountedSnapshots,
            gamesByAppId = gamesByAppId,
            reviewsByAppId = reviewsByAppId,
            limit = limit,
        )
    }

    @Transactional(readOnly = true)
    fun getDealDetail(appId: Long): DealDetailResponse {
        val game = steamGameRepository.findByAppId(appId)
            ?: throw NoSuchElementException("Deal game not found for appId=$appId")
        val snapshot = steamPriceSnapshotRepository.findTopByAppIdOrderByCollectedAtDesc(appId)
            ?.takeIf { it.discountPercent > 0 }
            ?: throw NoSuchElementException("Discounted deal not found for appId=$appId")
        val reviewSummary = steamReviewSummaryRepository.findByAppId(appId)

        return toDetailResponse(
            game = game,
            snapshot = snapshot,
            reviewSummary = reviewSummary,
        )
    }

    private fun findLatestDiscountedSnapshots(): List<SteamPriceSnapshot> =
        steamPriceSnapshotRepository.findByDiscountPercentGreaterThan(0)
            .groupBy { it.appId }
            .mapNotNull { (_, snapshots) ->
                snapshots.maxByOrNull { it.collectedAt }
            }

    private fun toSummaryResponse(
        game: SteamGame,
        snapshot: SteamPriceSnapshot,
        reviewSummary: SteamReviewSummary?,
    ): DealSummaryResponse =
        DealSummaryResponse(
            appId = game.appId,
            name = game.name,
            originalPrice = snapshot.originalPrice,
            finalPrice = snapshot.finalPrice ?: throw IllegalStateException("finalPrice is required"),
            discountPercent = snapshot.discountPercent,
            reviewScoreDesc = reviewSummary?.reviewScoreDesc,
            steamUrl = game.steamUrl,
            capsuleImageUrl = game.capsuleImageUrl,
        )

    private fun toDetailResponse(
        game: SteamGame,
        snapshot: SteamPriceSnapshot,
        reviewSummary: SteamReviewSummary?,
    ): DealDetailResponse =
        DealDetailResponse(
            appId = game.appId,
            name = game.name,
            originalPrice = snapshot.originalPrice,
            finalPrice = snapshot.finalPrice ?: throw IllegalStateException("finalPrice is required"),
            discountPercent = snapshot.discountPercent,
            reviewScoreDesc = reviewSummary?.reviewScoreDesc,
            steamUrl = game.steamUrl,
            capsuleImageUrl = game.capsuleImageUrl,
        )

    companion object {
        internal fun buildTopDeals(
            snapshots: List<SteamPriceSnapshot>,
            gamesByAppId: Map<Long, SteamGame>,
            reviewsByAppId: Map<Long, SteamReviewSummary>,
            limit: Int,
        ): List<DealSummaryResponse> =
            snapshots
                .asSequence()
                .filter { it.discountPercent > 0 }
                .mapNotNull { snapshot ->
                    val game = gamesByAppId[snapshot.appId] ?: return@mapNotNull null
                    DealSummaryResponse(
                        appId = game.appId,
                        name = game.name,
                        originalPrice = snapshot.originalPrice,
                        finalPrice = snapshot.finalPrice ?: return@mapNotNull null,
                        discountPercent = snapshot.discountPercent,
                        reviewScoreDesc = reviewsByAppId[snapshot.appId]?.reviewScoreDesc,
                        steamUrl = game.steamUrl,
                        capsuleImageUrl = game.capsuleImageUrl,
                    )
                }
                .sortedWith(
                    compareByDescending<DealSummaryResponse> { it.discountPercent }
                        .thenByDescending { reviewsByAppId[it.appId]?.totalReviews ?: 0 }
                )
                .take(limit)
                .toList()
    }
}
