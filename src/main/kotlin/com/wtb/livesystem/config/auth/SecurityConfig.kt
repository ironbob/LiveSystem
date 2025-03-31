package com.wtb.livesystem.config.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsServiceImpl,
    private val passwordEncoder: PasswordEncoder,
) {

//    @Bean
//    fun webSecurityCustomizer(): WebSecurityCustomizer {
//        return WebSecurityCustomizer { web ->
//            web.ignoring().requestMatchers(
//                "/h2-console/**",
//                "/webjars/**",
//                "/css/**",
//                "/js/**"
//            )
//        }
//    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/register/**").permitAll()
                    .requestMatchers("/index").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/users").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")/*.successHandler { request, response, authentication ->
                        println("登录成功---------------")
                        response.sendRedirect("/apps")
                    }.failureHandler{request, response, authentication ->
                        println("登录失败---------------")
//                        response.sendRedirect("/apps")
                    }*/
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


    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }
}

