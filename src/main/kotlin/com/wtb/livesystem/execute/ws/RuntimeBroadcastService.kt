package com.wtb.livesystem.execute.ws

import com.wtb.livesystem.execute.AppExecutionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RuntimeBroadcastService(val appExecutionService: AppExecutionService) {
    @Autowired
    private val handler: AppRuntimeWebSocketHandler? = null


    @Scheduled(fixedRate = 1000)
    fun broadcastAllRunningTimes() {
        val apps = appExecutionService.queryRunningAppMap();
        for ((appId, instance) in apps) {
            if (instance.getStartTime() != null) {
                val duration = instance.getRunningDuration()
                handler!!.broadcast(appId, duration)
            }
        }
    }
}