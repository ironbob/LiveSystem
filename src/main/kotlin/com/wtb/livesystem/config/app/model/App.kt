package com.wtb.livesystem.config.app.model

import com.wtb.livesystem.core.LiveConfig
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "apps")
data class App(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var lastUpdatedAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @OneToOne(mappedBy = "app", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var liveConfig: LiveConfig? = null // 直播配置 (可选)
) {
    fun updateLastModified() {
        lastUpdatedAt = Instant.now()
    }

    fun initializeLiveConfig() {
        if (liveConfig == null) {
            liveConfig = LiveConfig(app = this)
        }
    }
}

