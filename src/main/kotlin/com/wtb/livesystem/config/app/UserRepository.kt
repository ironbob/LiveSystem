package com.wtb.livesystem.config.app

import com.wtb.livesystem.config.app.model.App
import com.wtb.livesystem.config.app.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.time.LocalDateTime

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByUsernameAndIdNot(username: String, id: Long): Boolean

    @Query("SELECT u.lastActiveAt FROM User u WHERE u.id = :userId")
    fun findUserLastActiveAt(id: Long): Instant?

    fun countById(id: Long): Long
    fun findByIdOrderByCreatedAt(id: Long, pageable: PageRequest): Page<App>
}