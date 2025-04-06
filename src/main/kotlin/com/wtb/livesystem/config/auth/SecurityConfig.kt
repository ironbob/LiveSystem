package com.wtb.livesystem.config.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsServiceImpl,
    private val passwordEncoder: PasswordEncoder,
) {
    val logger = LoggerFactory.getLogger(SecurityConfig::class.java)
    @Bean
    fun loginRequestLoggingFilter(): LoginRequestLoggingFilter {
        return LoginRequestLoggingFilter()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                "/h2-console/**",
                "/ws/**",
                "/webjars/**",
                "/css/**",
                "/js/**"
            )
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .addFilterBefore(loginRequestLoggingFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/register/**").permitAll()
                    .requestMatchers("/ws/**","/ws/*/**").permitAll()
                    .requestMatchers("/index").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/users").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login").successHandler { request, response, authentication ->
                        logger.info("登录成功---------------")
                        response.sendRedirect("/apps")
                    }.failureHandler{request, response, authentication ->
                        logger.info("登录失败---------------")
//                        response.sendRedirect("/apps")
                    }
                    .defaultSuccessUrl("/apps", true)
                    .failureUrl("/login.html?error=true")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login.html?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
            }
        return http.build()
    }

//    override fun configure(web: WebSecurity) {
//        web.ignoring().antMatchers(
//            "/h2-console/**",
//            "/webjars/**",
//            "/css/**",
//            "/js/**"
//        )
//    }
    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }


    @Bean
    fun authenticationProvider(userDetailsService: UserDetailsService, passwordEncoder: PasswordEncoder): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder)
        return authProvider
    }

}

@Component
class LoginRequestLoggingFilter : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // 仅处理 POST 请求，以防其他请求也进入此过滤器
        if (request.method == "POST" && request.requestURI == "/login") {
            logger.info("提交的表单数据: ${request.parameterMap.toString()}")
        }
        filterChain.doFilter(request, response)
    }
}

