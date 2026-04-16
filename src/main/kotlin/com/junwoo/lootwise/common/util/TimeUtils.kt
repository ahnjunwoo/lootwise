package com.junwoo.lootwise.common.util

import java.time.Instant
import java.time.temporal.ChronoUnit

object TimeUtils {
    fun minusDays(baseTime: Instant, days: Long): Instant = baseTime.minus(days, ChronoUnit.DAYS)
}
