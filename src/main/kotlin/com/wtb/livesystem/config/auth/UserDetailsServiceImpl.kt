package com.wtb.livesystem.config.auth

import com.wtb.livesystem.config.app.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : UserDetailsService {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found")
        logger.info("[loadUserByUsername] :$user")
        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            emptyList() // 无角色权限
        )
    }
}
