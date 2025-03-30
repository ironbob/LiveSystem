package com.wtb.livesystem.config.app

import com.wtb.livesystem.streamer.config.app.StreamerConfig
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StreamerConfigRepository : JpaRepository<StreamerConfig, Long> {
    fun findByAppId(appId: Long): List<StreamerConfig>
    fun findByAppIdAndId(appId: Long, id: Long): Optional<StreamerConfig>
}