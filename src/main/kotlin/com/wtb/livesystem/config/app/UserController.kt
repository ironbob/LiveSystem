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
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class UserController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("/register")
    fun registerUser(
        @RequestParam username: String,
        @RequestParam password: String,
        model: Model
    ): String {
        println("用户注册请求--------------->: $username")

        // 检查用户名是否可用
        if (!userService.isUsernameAvailable(username)) {
            println("用户名不可用: $username")
            model.addAttribute("error", "用户名已被占用，请选择其他用户名")
            return "register"  // 重新加载 register.html 并显示错误提示
        }

        // 注册用户
        try {
            val user = userService.registerUser(
                username = username,
                rawPassword = password
            )
            println("用户注册成功: $user")
        } catch (e: Exception) {
            println("注册失败: ${e.message}")
            model.addAttribute("error", "注册失败，请稍后重试")
            return "register"
        }

        // 注册成功 -> 重定向到登录页面
        return "redirect:/login"
    }

//    // 用户登录（由Spring Security处理，此处仅返回用户信息）
//    @PostMapping("/login")
//    fun loginUser(): ResponseEntity<ApiResponse<UserResponse>> {
//        val authentication = SecurityContextHolder.getContext().authentication
//        val user = userService.findByUsername(authentication.name)
//            ?: throw IllegalStateException("User not found")
//
//        return ResponseEntity.ok(
//            ApiResponse.success(
//                data = UserResponse.fromUser(user),
//                message = "Login successful"
//            )
//        )
//    }

    // 检查用户名是否可用
    @GetMapping("/check-username")
    @ResponseBody
    fun checkUsernameAvailability(@RequestParam username: String): ApiResponse<Boolean> {
        val isAvailable = userService.isUsernameAvailable(username)
        return ApiResponse.success(isAvailable)
    }

    // 获取当前用户信息
    @GetMapping("/me")
    @ResponseBody
    fun getCurrentUser(): ResponseEntity<ApiResponse<UserResponse>> {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = userService.findByUsername(username)
            ?: throw IllegalStateException("用户未找到")

        return ResponseEntity.ok(
            ApiResponse.success(
                data = UserResponse.fromUser(user)
            )
        )
    }

    // 更新用户信息
    @PutMapping("/update")
    @ResponseBody
    fun updateCurrentUser(
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val username = SecurityContextHolder.getContext().authentication.name
        val curUser = userService.findByUsername(username)
            ?: throw IllegalStateException("用户未找到")
        val user = userService.updateUserInfo(
            userId = curUser.id,
            newUsername = request.username,
            newRawPassword = request.password?.let { passwordEncoder.encode(it) }
        )

        return ResponseEntity.ok(
            ApiResponse.success(
                data = UserResponse.fromUser(user),
                message = "用户信息更新成功"
            )
        )
    }

    // 用户注销（由 Spring Security 处理）
    @PostMapping("/logout")
    @ResponseBody
    fun logout(): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                message = "退出登录成功"
            )
        )
    }
}

