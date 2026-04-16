package com.junwoo.lootwise.deal.controller

import com.junwoo.lootwise.deal.dto.DealSummaryResponse
import com.junwoo.lootwise.deal.service.DealQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/deals")
class DealController(
    private val dealQueryService: DealQueryService,
) {
    @GetMapping
    fun getTopDeals(): List<DealSummaryResponse> = dealQueryService.getTopDeals()
}
