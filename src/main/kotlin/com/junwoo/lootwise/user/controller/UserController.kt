package com.junwoo.lootwise.user.controller

import com.junwoo.lootwise.auth.service.JwtPrincipal
import com.junwoo.lootwise.user.dto.UserMeResponse
import com.junwoo.lootwise.user.service.UserQueryService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userQueryService: UserQueryService,
) {
    @GetMapping("/me")
    fun getMe(
        @AuthenticationPrincipal
        principal: JwtPrincipal,
    ): UserMeResponse = userQueryService.getMe(principal.userId)
}
