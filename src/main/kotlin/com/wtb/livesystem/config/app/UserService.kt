package com.wtb.livesystem.config.app
import com.wtb.livesystem.config.app.model.User
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val appRepository: AppRepository
) {

    // 注册新用户
    fun registerUser(username: String, rawPassword: String): User {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(rawPassword.length >= 6) { "Password must be at least 6 characters" }

        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }

        val user = User(
            username = username,
            password = passwordEncoder.encode(rawPassword)
        )
        return userRepository.save(user)
    }

    // 根据用户名查找用户
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    // 验证用户凭据
    fun authenticate(username: String, rawPassword: String): User {
        val user = findByUsername(username)
            ?: throw IllegalArgumentException("User not found")

        if (!passwordEncoder.matches(rawPassword, user.password)) {
            throw IllegalArgumentException("Invalid password")
        }

        return user
    }

    // 更新用户信息
    fun updateUserInfo(userId: Long, newUsername: String? = null, newRawPassword: String? = null): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        newUsername?.let {
            require(it.isNotBlank()) { "Username cannot be blank" }
            if (userRepository.existsByUsernameAndIdNot(it, userId)) {
                throw IllegalArgumentException("Username already taken")
            }
            user.username = it
        }

        newRawPassword?.let {
            require(it.length >= 8) { "Password must be at least 8 characters" }
            user.password = passwordEncoder.encode(it)
        }

        return userRepository.save(user)
    }

    // 获取用户的应用统计信息
    fun getUserStats(userId: Long): Map<String, Any> {
        val appCount = appRepository.countByUserId(userId)
        val recentApps = appRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 5))

        return mapOf(
            "totalApps" to appRepository.countByUserId(userId),
            "recentApps" to appRepository.findByUserIdOrderByCreatedAtDesc(
                userId,
                PageRequest.of(0, 5)
            ).content, // 提取实际内容列表
            "lastActive" to (userRepository.findUserLastActiveAt(userId)?.toString() ?: "Never")
        )
    }

        // 检查用户名是否可用
    fun isUsernameAvailable(username: String): Boolean {
        return !userRepository.existsByUsername(username)
    }

    // 为测试数据生成随机用户
    fun generateTestUsers(count: Int): List<User> {
        return (1..count).map {
            User(
                username = "testuser_${UUID.randomUUID().toString().substring(0, 8)}",
                password = passwordEncoder.encode("Test1234!")
            ).also { userRepository.save(it) }
        }
    }
}

