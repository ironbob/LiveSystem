package com.wtb.livesystem.config.auth

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {

    @PostMapping("/login")
    fun login(@RequestBody credentials: LoginCredentials) {
        // Spring Security会自动处理认证
        // 认证成功后会返回200，失败返回401
    }
}

data class LoginCredentials(
    val username: String,
    val password: String
)

