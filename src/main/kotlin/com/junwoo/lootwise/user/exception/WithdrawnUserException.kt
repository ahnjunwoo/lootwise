package com.junwoo.lootwise.user.exception

class WithdrawnUserException(
    userId: Long,
) : RuntimeException("User is withdrawn: $userId")
