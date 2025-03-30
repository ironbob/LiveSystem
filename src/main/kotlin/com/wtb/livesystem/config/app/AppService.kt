package com.wtb.livesystem.config.app

import com.wtb.livesystem.config.app.model.App
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
@Service
class AppService(
    private val appRepository: AppRepository,
    private val userRepository: UserRepository
) {

    // 创建新App（新增description参数）
    @Transactional
    fun createApp(name: String, description: String, username: String): App {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found")

        val app = App(
            name = name,
            description = description,
            user = user
        )
        return appRepository.save(app)
    }

    // 根据用户名查找用户的所有App（修改为使用username查询）
    fun findByUsername(username: String): List<App> {
        return appRepository.findByUserUsernameOrderByCreatedAtDesc(username)
    }

    // 根据ID查找App（保持不变）
    fun findById(id: Long): App {
        return appRepository.findById(id)
            .orElseThrow { IllegalArgumentException("App not found") }
    }

    // 更新App信息（新增description更新）
    @Transactional
    fun updateApp(id: Long, newName: String, newDescription: String): App {
        val app = findById(id).apply {
            name = newName
            description = newDescription
            updateLastModified()
        }
        return appRepository.save(app)
    }

    // 删除App（保持不变）
    @Transactional
    fun deleteApp(id: Long) {
        appRepository.deleteById(id)
    }

    // 验证用户是否有权访问该App（优化实现）
    fun validateUserOwnership(appId: Long, username: String): Boolean {
        val userId = userRepository.findByUsername(username)?.id ?: return false
        return appRepository.existsByIdAndUserId(appId, userId)
    }

    // 新增：获取用户最近修改的App
    fun findRecentlyUpdated(username: String, limit: Int): List<App> {
        return appRepository.findByUserUsernameOrderByLastUpdatedAtDesc(
            username,
            PageRequest.of(0, limit)
        ).content
    }
}

