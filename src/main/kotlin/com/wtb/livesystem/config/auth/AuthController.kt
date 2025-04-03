package com.wtb.livesystem.config.auth

import com.wtb.livesystem.config.app.UserController
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AuthController {
    val logger = LoggerFactory.getLogger(AuthController::class.java)
    @GetMapping("/login")
    fun showLoginPage(): String {
        logger.info("显示登录界面")
        return "login"
    }

    @PostMapping("/login")
    fun login(@RequestParam username: String, @RequestParam password: String): String {
        logger.info("登录请求 Received login request: username=$username, password=$password")
        // 处理登录逻辑
        return "redirect:/apps"
    }

    @GetMapping("index")
    fun home(): String {
        return "index"
    }

    @GetMapping("/register")
    fun showRegisterPage(): String {
        logger.info("显示注册界面")
        return "register"
    }
}

data class LoginCredentials(
    val username: String,
    val password: String
)

