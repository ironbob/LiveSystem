package com.wtb.livesystem.execute

import com.wtb.livesystem.config.app.AppRepository
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

@Service
class AppExecutionService(val appRepository: AppRepository) {

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
         appRepository.findById(appId).getOrNull()?.let {
            val appInstance = AppInstance(it)
            runningApps[appId] = appInstance
            appInstance.start()
            return true;
        }
        return false
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
