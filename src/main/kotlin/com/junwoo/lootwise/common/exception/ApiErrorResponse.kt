package com.junwoo.lootwise.common.exception

import java.time.Instant

data class ApiErrorResponse(
    val message: String,
    val timestamp: Instant = Instant.now(),
)
