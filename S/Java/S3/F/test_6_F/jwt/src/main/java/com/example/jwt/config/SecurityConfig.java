package com.example.jwt.config;

import com.example.jwt.filter.JwtAuthenticationFilter;
import com.example.jwt.service.CustomUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// [ @Configuration ] This class contains configuration/settings for my application, including Beans
// I need to create.
// [ @EnableWebSecurity ] Enable Spring Security's web security features for this application.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    // [ SecurityFilterChain ] is used to control and protect HTTP requests in your Spring Boot application.
    // [ SecurityFilterChain ] the rules that tell Spring Security what to do with each incoming request.
    // [ HttpSecurity ] is the object you use to configure the security rules for HTTP requests.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // [ csrf ] It is a security protection that prevents another website from tricking
                // a user's browser into sending unwanted requests to your application.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // ✅ public endpoints
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                // [ UsernamePasswordAuthenticationFilter ] is a Spring Security filter that handles
                // username/password login authentication.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // apply JWT filter

        return http.build();
    }

    // [ BCryptPasswordEncoder ] is used to securely hash user passwords before storing them in the database.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // [ AuthenticationManager ] is the component that checks whether a user's login credentials are correct.
    // [ AuthenticationConfiguration ] is a Spring Security class that provides the configured
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

