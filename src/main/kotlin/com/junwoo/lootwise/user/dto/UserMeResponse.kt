package com.junwoo.lootwise.user.dto

import com.junwoo.lootwise.user.domain.UserRole
import com.junwoo.lootwise.user.domain.UserStatus

data class UserMeResponse(
    val userId: Long,
    val email: String,
    val nickname: String,
    val role: UserRole,
    val status: UserStatus,
)
