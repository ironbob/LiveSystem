package com.wtb.livesystem.config.auth

import com.wtb.livesystem.config.app.model.User
import java.time.Instant
import jakarta.validation.constraints.NotBlank

// Size 注解
import jakarta.validation.constraints.Size

// 请求DTO
data class RegisterRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 20)
    val username: String,

    @field:NotBlank
    @field:Size(min = 8, max = 40)
    val password: String
)

data class UpdateUserRequest(
    @field:Size(min = 3, max = 20)
    val username: String?,

    @field:Size(min = 8, max = 40)
    val password: String?
)

// 响应DTO
data class UserResponse(
    val id: Long,
    val username: String,
    val createdAt: Instant
) {
    companion object {
        fun fromUser(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                username = user.username,
                createdAt = user.createdAt
            )
        }
    }
}

// 通用API响应格式
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val timestamp: Instant = Instant.now()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(true, data, message)
        }

        fun <T> success(message: String? = null): ApiResponse<T> {
            return ApiResponse(true, null, message)
        }
    }
}

