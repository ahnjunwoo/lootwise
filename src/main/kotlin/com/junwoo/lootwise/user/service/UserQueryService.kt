package com.junwoo.lootwise.user.service

import com.junwoo.lootwise.user.domain.UserStatus
import com.junwoo.lootwise.user.dto.UserMeResponse
import com.junwoo.lootwise.user.exception.UserNotFoundException
import com.junwoo.lootwise.user.exception.WithdrawnUserException
import com.junwoo.lootwise.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserQueryService(
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = true)
    fun getMe(userId: Long): UserMeResponse {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException(userId) }
        if (user.status == UserStatus.WITHDRAWN) {
            throw WithdrawnUserException(userId)
        }

        return UserMeResponse(
            userId = requireNotNull(user.id) { "User id must not be null" },
            email = user.email,
            nickname = user.nickname,
            role = user.role,
            status = user.status,
        )
    }
}
