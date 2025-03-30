package com.wtb.livesystem.config.app.model

import com.wtb.livesystem.streamer.config.app.StreamerConfig
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
    val user: User
) {
    fun updateLastModified() {
        lastUpdatedAt = Instant.now()
    }
}
