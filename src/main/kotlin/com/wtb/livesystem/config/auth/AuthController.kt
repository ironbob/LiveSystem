package com.wtb.livesystem.config.auth

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthController {

    @GetMapping("/login")
    fun showLoginPage(): String {
        println("显示登录界面")
        return "login"
    }

    @GetMapping("index")
    fun home(): String {
        return "index"
    }

    @GetMapping("/register")
    fun showRegisterPage(): String {
        println("显示注册界面")
        return "register"
    }
}

data class LoginCredentials(
    val username: String,
    val password: String
)

