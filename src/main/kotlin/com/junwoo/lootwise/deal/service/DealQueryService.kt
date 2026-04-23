package com.junwoo.lootwise.deal.service

import com.junwoo.lootwise.deal.dto.DealDetailResponse
import com.junwoo.lootwise.deal.dto.DealSearchRequest
import com.junwoo.lootwise.deal.dto.DealSort
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
        return getTopDeals(DealSearchRequest(limit = limit))
    }

    @Transactional(readOnly = true)
    fun getTopDeals(request: DealSearchRequest): List<DealSummaryResponse> {
        require(request.limit > 0) { "limit must be greater than 0" }

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
            request = request,
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
            reviewScoreDescKo = ReviewScoreDescriptionTranslator.toKorean(reviewSummary?.reviewScoreDesc),
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
            reviewScoreDescKo = ReviewScoreDescriptionTranslator.toKorean(reviewSummary?.reviewScoreDesc),
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
            buildTopDeals(
                snapshots = snapshots,
                gamesByAppId = gamesByAppId,
                reviewsByAppId = reviewsByAppId,
                request = DealSearchRequest(limit = limit),
            )

        internal fun buildTopDeals(
            snapshots: List<SteamPriceSnapshot>,
            gamesByAppId: Map<Long, SteamGame>,
            reviewsByAppId: Map<Long, SteamReviewSummary>,
            request: DealSearchRequest,
        ): List<DealSummaryResponse> =
            snapshots
                .asSequence()
                .filter { it.discountPercent > 0 }
                .filter { snapshot ->
                    request.minDiscountPercent?.let { snapshot.discountPercent >= it } ?: true
                }
                .filter { snapshot ->
                    request.maxFinalPrice?.let { maxPrice ->
                        snapshot.finalPrice?.let { it <= maxPrice } ?: false
                    } ?: true
                }
                .mapNotNull { snapshot ->
                    val game = gamesByAppId[snapshot.appId] ?: return@mapNotNull null
                    if (!matchesKeyword(game, request.keyword)) {
                        return@mapNotNull null
                    }
                    val reviewSummary = reviewsByAppId[snapshot.appId]
                    if (!matchesReviewScore(reviewSummary, request.minReviewScore)) {
                        return@mapNotNull null
                    }
                    if (!matchesReviewScoreDesc(reviewSummary, request.reviewScoreDesc)) {
                        return@mapNotNull null
                    }
                    DealSummaryResponse(
                        appId = game.appId,
                        name = game.name,
                        originalPrice = snapshot.originalPrice,
                        finalPrice = snapshot.finalPrice ?: return@mapNotNull null,
                        discountPercent = snapshot.discountPercent,
                        reviewScoreDesc = reviewSummary?.reviewScoreDesc,
                        reviewScoreDescKo = ReviewScoreDescriptionTranslator.toKorean(reviewSummary?.reviewScoreDesc),
                        steamUrl = game.steamUrl,
                        capsuleImageUrl = game.capsuleImageUrl,
                    )
                }
                .sortedWith(dealComparator(request.sort, reviewsByAppId, snapshots))
                .take(request.limit)
                .toList()

        private fun matchesKeyword(game: SteamGame, keyword: String?): Boolean {
            val normalizedKeyword = keyword?.trim()?.takeIf { it.isNotEmpty() } ?: return true
            return game.name.contains(normalizedKeyword, ignoreCase = true)
        }

        private fun matchesReviewScore(
            reviewSummary: SteamReviewSummary?,
            minReviewScore: Int?,
        ): Boolean =
            minReviewScore?.let { minScore ->
                reviewSummary?.reviewScore?.let { it >= minScore } ?: false
            } ?: true

        private fun matchesReviewScoreDesc(
            reviewSummary: SteamReviewSummary?,
            reviewScoreDesc: String?,
        ): Boolean {
            val normalizedDesc = reviewScoreDesc?.trim()?.takeIf { it.isNotEmpty() } ?: return true
            return reviewSummary?.reviewScoreDesc?.contains(normalizedDesc, ignoreCase = true) ?: false
        }

        private fun dealComparator(
            sort: DealSort,
            reviewsByAppId: Map<Long, SteamReviewSummary>,
            snapshots: List<SteamPriceSnapshot>,
        ): Comparator<DealSummaryResponse> =
            when (sort) {
                DealSort.DISCOUNT_DESC ->
                    compareByDescending<DealSummaryResponse> { it.discountPercent }
                        .thenByDescending { reviewsByAppId[it.appId]?.totalReviews ?: 0 }
                DealSort.PRICE_ASC ->
                    compareBy<DealSummaryResponse> { it.finalPrice }
                        .thenByDescending { it.discountPercent }
                DealSort.REVIEW_COUNT_DESC ->
                    compareByDescending<DealSummaryResponse> { reviewsByAppId[it.appId]?.totalReviews ?: 0 }
                        .thenByDescending { it.discountPercent }
                DealSort.REVIEW_SCORE_DESC ->
                    compareByDescending<DealSummaryResponse> { reviewsByAppId[it.appId]?.reviewScore ?: -1 }
                        .thenByDescending { reviewsByAppId[it.appId]?.totalReviews ?: 0 }
                DealSort.LATEST_DESC ->
                    compareByDescending<DealSummaryResponse> { response ->
                        snapshots.firstOrNull { it.appId == response.appId }?.collectedAt
                    }
            }
    }
}
