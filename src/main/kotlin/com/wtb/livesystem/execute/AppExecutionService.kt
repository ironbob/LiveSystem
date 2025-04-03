package com.wtb.livesystem.execute

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class AppExecutionService {

    private val runningApps: MutableMap<Long, AppInstance> = ConcurrentHashMap()

    fun queryRunningAppList(): List<AppInstance> {
        return runningApps.values.toList()
    }

    fun queryRunningAppMap(): Map<Long, AppInstance> {
        return runningApps
    }

    fun startApp(appId: Long): Boolean {
        if (runningApps.containsKey(appId)) {
            return false // 应用已经在运行
        }
        val appInstance = AppInstance(appId)
        runningApps[appId] = appInstance
        appInstance.start()
        return true
    }

    fun stopApp(appId: Long): Boolean {
        return runningApps.remove(appId)?.let {
            it.stop()
            true
        } ?: false
    }

    fun getNextSpeech(appId: Long): String? {
        return runningApps[appId]?.getNextSpeech()
    }
}
