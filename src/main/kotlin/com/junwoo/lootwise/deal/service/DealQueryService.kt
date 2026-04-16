package com.junwoo.lootwise.deal.service

import com.junwoo.lootwise.deal.dto.DealSummaryResponse
import com.junwoo.lootwise.deal.repository.DealRepository
import org.springframework.stereotype.Service

@Service
class DealQueryService(
    private val dealRepository: DealRepository,
) {
    fun getTopDeals(): List<DealSummaryResponse> =
        dealRepository.findTop20ByOrderByDiscountPercentDescCollectedAtDesc()
            .map { deal ->
                DealSummaryResponse(
                    id = deal.id ?: 0L,
                    steamAppId = deal.steamAppId,
                    title = deal.title,
                    originalPrice = deal.originalPrice,
                    discountedPrice = deal.discountedPrice,
                    discountPercent = deal.discountPercent,
                    currency = deal.currency,
                    dealUrl = deal.dealUrl,
                    collectedAt = deal.collectedAt,
                )
            }
}
