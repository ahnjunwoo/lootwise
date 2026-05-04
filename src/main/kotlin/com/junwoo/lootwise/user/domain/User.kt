package com.junwoo.lootwise.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "email", nullable = false, length = 255)
    val email: String,
    @Column(name = "nickname", nullable = false, length = 30)
    var nickname: String,
    @Column(name = "password_hash", nullable = false, length = 100)
    var passwordHash: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    val role: UserRole = UserRole.USER,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    var status: UserStatus = UserStatus.ACTIVE,
    @Column(name = "withdrawn_at")
    var withdrawnAt: LocalDateTime? = null,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
) {
    fun withdraw(withdrawnAt: LocalDateTime) {
        status = UserStatus.WITHDRAWN
        this.withdrawnAt = withdrawnAt
    }
}
