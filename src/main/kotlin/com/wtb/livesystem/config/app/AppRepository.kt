package com.wtb.livesystem.config.app

import com.wtb.livesystem.config.app.model.App
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.*

interface AppRepository : JpaRepository<App, Long> {
    fun findByUserId(userId: Long): List<App>
    fun findByUserUsername(username: String): List<App>
    fun findByIdAndUserId(id: Long, userId: Long): Optional<App>
    // 统计用户拥有的应用数量
    fun countByUserId(userId: Long): Long

    // 分页查询用户最近创建的应用（按创建时间降序）
    fun findByUserIdOrderByCreatedAtDesc(
        userId: Long,
        pageable: PageRequest
    ): Page<App>

    // 可选：使用JPQL查询最后活跃时间
    @Query("""
        SELECT MAX(a.lastUpdatedAt) 
        FROM App a 
        WHERE a.user.id = :userId
    """)
    fun findLastActiveTimeByUser(userId: Long): Instant?

    // 按用户名查找并排序
    fun findByUserUsernameOrderByCreatedAtDesc(username: String): List<App>

    // 按用户ID和AppID联合查询
    fun existsByIdAndUserId(id: Long, userId: Long): Boolean

    // 分页查询最近更新的App
    fun findByUserUsernameOrderByLastUpdatedAtDesc(
        username: String,
        pageable: PageRequest
    ): Page<App>

    // 其他可能需要的查询...
    fun countByUserUsername(username: String): Long
}