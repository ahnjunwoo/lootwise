package com.junwoo.lootwise.deal.controller

import com.junwoo.lootwise.deal.dto.DealDetailResponse
import com.junwoo.lootwise.deal.dto.DealSearchRequest
import com.junwoo.lootwise.deal.dto.DealSort
import com.junwoo.lootwise.deal.dto.DealSummaryResponse
import com.junwoo.lootwise.deal.service.DealQueryService
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated

@Validated
@RestController
@RequestMapping("/api/v1/deals")
class DealController(
    private val dealQueryService: DealQueryService,
) {
    @GetMapping
    fun getTopDeals(
        @RequestParam(required = false)
        keyword: String?,
        @RequestParam(required = false)
        @Min(1)
        @Max(100)
        minDiscountPercent: Int?,
        @RequestParam(required = false)
        @DecimalMin("0.00")
        maxFinalPrice: BigDecimal?,
        @RequestParam(required = false)
        @Min(0)
        @Max(100)
        minReviewScore: Int?,
        @RequestParam(required = false)
        reviewScoreDesc: String?,
        @RequestParam(defaultValue = "DISCOUNT_DESC")
        sort: DealSort,
        @RequestParam(defaultValue = "20")
        @Min(1)
        @Max(100)
        limit: Int,
    ): List<DealSummaryResponse> =
        dealQueryService.getTopDeals(
            DealSearchRequest(
                keyword = keyword,
                minDiscountPercent = minDiscountPercent,
                maxFinalPrice = maxFinalPrice,
                minReviewScore = minReviewScore,
                reviewScoreDesc = reviewScoreDesc,
                sort = sort,
                limit = limit,
            )
        )

    @GetMapping("/{appId}")
    fun getDealDetail(
        @PathVariable
        @Positive
        appId: Long,
    ): DealDetailResponse = dealQueryService.getDealDetail(appId)
}
