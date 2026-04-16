package com.junwoo.lootwise.deal.repository

import com.junwoo.lootwise.deal.domain.Deal
import org.springframework.data.jpa.repository.JpaRepository

interface DealRepository : JpaRepository<Deal, Long> {
    fun findTop20ByOrderByDiscountPercentDescCollectedAtDesc(): List<Deal>
}
