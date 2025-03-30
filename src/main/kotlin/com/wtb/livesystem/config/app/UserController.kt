package com.wtb.livesystem.config.app

import com.wtb.livesystem.config.app.model.User
import com.wtb.livesystem.config.auth.ApiResponse
import com.wtb.livesystem.config.auth.RegisterRequest
import com.wtb.livesystem.config.auth.UpdateUserRequest
import com.wtb.livesystem.config.auth.UserResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) {

    // 用户注册
    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.registerUser(
            username = request.username,
            rawPassword = passwordEncoder.encode(request.password)
        )
        return ResponseEntity.ok(
            ApiResponse.success(
                data = UserResponse.fromUser(user),
                message = "User registered successfully"
            )
        )
    }

    // 用户登录（由Spring Security处理，此处仅返回用户信息）
    @PostMapping("/login")
    fun loginUser(): ResponseEntity<ApiResponse<UserResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService.findByUsername(authentication.name)
            ?: throw IllegalStateException("User not found")

        return ResponseEntity.ok(
            ApiResponse.success(
                data = UserResponse.fromUser(user),
                message = "Login successful"
            )
        )
    }

    // 获取当前用户信息
    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<ApiResponse<UserResponse>> {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = userService.findByUsername(username)
            ?: throw IllegalStateException("User not found")

        return ResponseEntity.ok(
            ApiResponse.success(
                data = UserResponse.fromUser(user)
            )
        )
    }

    // 更新用户信息
    @PutMapping("/update")
    fun updateCurrentUser(
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val username = SecurityContextHolder.getContext().authentication.name
        val curUser = userService.findByUsername(username)
            ?: throw IllegalStateException("User not found")
        val user = userService.updateUserInfo(
            userId = curUser.id,
            newUsername = request.username,
            newRawPassword = request.password?.let { passwordEncoder.encode(it) }
        )

        return ResponseEntity.ok(
            ApiResponse.success(
                data = UserResponse.fromUser(user),
                message = "User updated successfully"
            )
        )
    }

    // 用户注销（由Spring Security处理，此处仅返回成功消息）
    @PostMapping("/logout")
    fun logout(): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                message = "Logout successful"
            )
        )
    }

    // 检查用户名是否可用
    @GetMapping("/check-username")
    fun checkUsernameAvailability(@RequestParam username: String): ResponseEntity<ApiResponse<Boolean>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                data = userService.isUsernameAvailable(username)
            )
        )
    }
}

