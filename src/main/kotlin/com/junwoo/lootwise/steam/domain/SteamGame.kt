package com.junwoo.lootwise.steam.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "steam_game")
class SteamGame(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "app_id", nullable = false, unique = true)
    val appId: Long,
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "steam_url", nullable = false)
    val steamUrl: String,
    @Column(name = "capsule_image_url")
    val capsuleImageUrl: String? = null,
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
