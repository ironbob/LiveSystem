package com.wtb.livesystem.config.app.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 20)
    var username: String,

    @Column(nullable = false, length = 60) // BCrypt加密后的固定长度
    var password: String,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var lastActiveAt: Instant = Instant.now(),

    @Column(length = 50)
    val email: String? = null
) {
    // 更新最后活跃时间
    fun updateLastActiveTime() {
        lastActiveAt = Instant.now()
    }
}


