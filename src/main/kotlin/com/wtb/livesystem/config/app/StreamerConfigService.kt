package com.wtb.livesystem.config.app

import com.wtb.livesystem.core.streamer.StreamerDefine.Companion.fromJson
import com.wtb.livesystem.streamer.config.app.StreamerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StreamerConfigService {
    @Autowired
    private val configRepository: StreamerConfigRepository? = null

    @Autowired
    private val appRepository: AppRepository? = null

    fun createConfig(appId: Long, configName: String?, jsonConfig: String): StreamerConfig {
        val app = appRepository!!.findById(appId)
            .orElseThrow { RuntimeException("App not found") }

        // 验证JSON格式
        try {
            fromJson(jsonConfig)
        } catch (e: Exception) {
            throw RuntimeException("Invalid JSON configuration")
        }

        val config = StreamerConfig()
        config.app = app
        config.configName = configName
        config.jsonConfig = jsonConfig

        return configRepository!!.save(config)
    }

    fun updateConfig(configId: Long, jsonConfig: String): StreamerConfig {
        val config = configRepository!!.findById(configId)
            .orElseThrow { RuntimeException("Config not found") }

        // 验证JSON格式
        try {
            fromJson(jsonConfig)
        } catch (e: Exception) {
            throw RuntimeException("Invalid JSON configuration")
        }

        config.jsonConfig = jsonConfig
        return configRepository.save(config)
    }

    fun findByAppId(appId: Long): List<StreamerConfig> {
        return configRepository!!.findByAppId(appId)
    }

    fun findById(id: Long): StreamerConfig {
        return configRepository!!.findById(id)
            .orElseThrow { RuntimeException("Config not found") }
    }
}


