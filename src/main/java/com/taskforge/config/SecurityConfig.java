package com.taskforge.config;

import com.taskforge.model.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/","/api/auth/**").permitAll()
//                        .requestMatchers("/api/tasks/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.MANAGER.name(), UserRole.USER.name())
//                        .requestMatchers("/api/users/**").hasRole(UserRole.ADMIN.name())
//                        .anyRequest().authenticated())
                .build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}