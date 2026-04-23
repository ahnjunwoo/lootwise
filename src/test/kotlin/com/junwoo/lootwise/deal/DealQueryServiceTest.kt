package com.junwoo.lootwise.deal

import com.junwoo.lootwise.deal.service.DealQueryService
import com.junwoo.lootwise.deal.dto.DealSearchRequest
import com.junwoo.lootwise.deal.dto.DealSort
import com.junwoo.lootwise.steam.domain.SteamGame
import com.junwoo.lootwise.steam.domain.SteamPriceSnapshot
import com.junwoo.lootwise.steam.domain.SteamReviewSummary
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDateTime

class DealQueryServiceTest : FunSpec({
    test("discounted games are sorted by discount percent then total reviews") {
        val now = LocalDateTime.of(2026, 4, 16, 12, 0)
        val snapshots = listOf(
            SteamPriceSnapshot(
                id = 1L,
                appId = 10L,
                originalPrice = BigDecimal("30.00"),
                finalPrice = BigDecimal("15.00"),
                discountPercent = 50,
                collectedAt = now,
            ),
            SteamPriceSnapshot(
                id = 2L,
                appId = 20L,
                originalPrice = BigDecimal("40.00"),
                finalPrice = BigDecimal("20.00"),
                discountPercent = 50,
                collectedAt = now,
            ),
            SteamPriceSnapshot(
                id = 3L,
                appId = 30L,
                originalPrice = BigDecimal("50.00"),
                finalPrice = BigDecimal("30.00"),
                discountPercent = 40,
                collectedAt = now,
            ),
        )
        val gamesByAppId = listOf(
            steamGame(appId = 10L, name = "Game A"),
            steamGame(appId = 20L, name = "Game B"),
            steamGame(appId = 30L, name = "Game C"),
        ).associateBy { it.appId }
        val reviewsByAppId = listOf(
            reviewSummary(appId = 10L, totalReviews = 100),
            reviewSummary(appId = 20L, totalReviews = 300),
            reviewSummary(appId = 30L, totalReviews = 200),
        ).associateBy { it.appId }

        val result = DealQueryService.buildTopDeals(
            snapshots = snapshots,
            gamesByAppId = gamesByAppId,
            reviewsByAppId = reviewsByAppId,
            limit = 10,
        )

        result.map { it.appId } shouldBe listOf(20L, 10L, 30L)
        result.first().reviewScoreDescKo shouldBe "매우 긍정적"
    }

    test("non-discounted games are excluded") {
        val now = LocalDateTime.of(2026, 4, 16, 12, 0)
        val snapshots = listOf(
            SteamPriceSnapshot(
                id = 1L,
                appId = 10L,
                originalPrice = BigDecimal("30.00"),
                finalPrice = BigDecimal("15.00"),
                discountPercent = 50,
                collectedAt = now,
            ),
            SteamPriceSnapshot(
                id = 2L,
                appId = 20L,
                originalPrice = BigDecimal("20.00"),
                finalPrice = BigDecimal("20.00"),
                discountPercent = 0,
                collectedAt = now,
            ),
        )
        val gamesByAppId = listOf(
            steamGame(appId = 10L, name = "Game A"),
            steamGame(appId = 20L, name = "Game B"),
        ).associateBy { it.appId }

        val result = DealQueryService.buildTopDeals(
            snapshots = snapshots,
            gamesByAppId = gamesByAppId,
            reviewsByAppId = emptyMap(),
            limit = 10,
        )

        result shouldHaveSize 1
        result.first().appId shouldBe 10L
    }

    test("deals can be filtered by keyword discount price and review score") {
        val now = LocalDateTime.of(2026, 4, 16, 12, 0)
        val snapshots = listOf(
            SteamPriceSnapshot(
                id = 1L,
                appId = 10L,
                originalPrice = BigDecimal("30.00"),
                finalPrice = BigDecimal("12.00"),
                discountPercent = 60,
                collectedAt = now,
            ),
            SteamPriceSnapshot(
                id = 2L,
                appId = 20L,
                originalPrice = BigDecimal("40.00"),
                finalPrice = BigDecimal("18.00"),
                discountPercent = 55,
                collectedAt = now,
            ),
            SteamPriceSnapshot(
                id = 3L,
                appId = 30L,
                originalPrice = BigDecimal("50.00"),
                finalPrice = BigDecimal("45.00"),
                discountPercent = 10,
                collectedAt = now,
            ),
        )
        val gamesByAppId = listOf(
            steamGame(appId = 10L, name = "Dark Quest"),
            steamGame(appId = 20L, name = "Bright Quest"),
            steamGame(appId = 30L, name = "Dark Racing"),
        ).associateBy { it.appId }
        val reviewsByAppId = listOf(
            reviewSummary(appId = 10L, totalReviews = 100, reviewScore = 9),
            reviewSummary(appId = 20L, totalReviews = 300, reviewScore = 7),
            reviewSummary(appId = 30L, totalReviews = 200, reviewScore = 9),
        ).associateBy { it.appId }

        val result = DealQueryService.buildTopDeals(
            snapshots = snapshots,
            gamesByAppId = gamesByAppId,
            reviewsByAppId = reviewsByAppId,
            request = DealSearchRequest(
                keyword = "dark",
                minDiscountPercent = 50,
                maxFinalPrice = BigDecimal("20.00"),
                minReviewScore = 8,
                limit = 10,
            ),
        )

        result shouldHaveSize 1
        result.first().appId shouldBe 10L
    }

    test("deals can be sorted by price ascending") {
        val now = LocalDateTime.of(2026, 4, 16, 12, 0)
        val snapshots = listOf(
            SteamPriceSnapshot(
                id = 1L,
                appId = 10L,
                originalPrice = BigDecimal("30.00"),
                finalPrice = BigDecimal("15.00"),
                discountPercent = 50,
                collectedAt = now,
            ),
            SteamPriceSnapshot(
                id = 2L,
                appId = 20L,
                originalPrice = BigDecimal("40.00"),
                finalPrice = BigDecimal("8.00"),
                discountPercent = 40,
                collectedAt = now,
            ),
        )
        val gamesByAppId = listOf(
            steamGame(appId = 10L, name = "Game A"),
            steamGame(appId = 20L, name = "Game B"),
        ).associateBy { it.appId }

        val result = DealQueryService.buildTopDeals(
            snapshots = snapshots,
            gamesByAppId = gamesByAppId,
            reviewsByAppId = emptyMap(),
            request = DealSearchRequest(sort = DealSort.PRICE_ASC, limit = 10),
        )

        result.map { it.appId } shouldBe listOf(20L, 10L)
    }
})

private fun steamGame(appId: Long, name: String): SteamGame =
    SteamGame(
        id = appId,
        appId = appId,
        name = name,
        steamUrl = "https://store.steampowered.com/app/$appId",
        capsuleImageUrl = null,
        isActive = true,
        createdAt = LocalDateTime.of(2026, 4, 16, 0, 0),
        updatedAt = LocalDateTime.of(2026, 4, 16, 0, 0),
    )

private fun reviewSummary(
    appId: Long,
    totalReviews: Int,
    reviewScore: Int = 9,
): SteamReviewSummary =
    SteamReviewSummary(
        id = appId,
        appId = appId,
        totalReviews = totalReviews,
        totalPositive = totalReviews,
        totalNegative = 0,
        reviewScore = reviewScore,
        reviewScoreDesc = "Very Positive",
        updatedAt = LocalDateTime.of(2026, 4, 16, 0, 0),
    )
