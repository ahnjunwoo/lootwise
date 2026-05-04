package com.junwoo.lootwise.user.exception

class UserNotFoundException(
    userId: Long,
) : RuntimeException("User not found: $userId")
