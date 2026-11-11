package com.hun.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// [ @Configuration ] This class contains configuration/settings for my application, and Spring should manage the objects
// (beans) defined here.
@Configuration
// [ @EnableWebSecurity ] Enable Spring Security's web security features for this application.
@EnableWebSecurity
public class SecurityConfig {

    // [ @Bean ] Create this object and put it into the Spring container so I can use it elsewhere.
    // [ PasswordEncoder ] is used to securely hash passwords before storing or comparing them.
    // [ BCryptPasswordEncoder ] is the actual password-hashing algorithm implementation you are using for [ PasswordEncoder ].
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // [ UserDetailsService ] is used to find/load a user's information when they log in.
    // [ UserDetailsService ] tells Spring Security: “Find this username and give me that user's security information.
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Create users with proper dependency injection

        // [ UserDetails ] represents the user's security information for Spring Security.
        // [ UserDetails ] = information about a user that Spring Security needs to authenticate and authorize them.
        // [ User ] It is used to create a user object that Spring Security can use for login and authorization.
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN", "USER")
                .build();

        // [ InMemoryUserDetailsManager ] is used to store and retrieve users in memory for Spring Security.
        // Store these users in the application's memory so Spring Security can find them during login.
        return new InMemoryUserDetailsManager(user, admin);
    }

    // [ AuthenticationSuccessHandler ] is used to decide what happens after a user successfully logs in.
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {

            // Redirect based on user role
            // [ authentication ] is used to represent the currently logged-in user's authentication information.
            // [ getAuthorities() ] is used to get the roles/permissions of the currently logged-in user.
            // [ grantedAuthority ] represents one role or permission that a user has in Spring Security.
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                // [ sendRedirect() ] is used to send the user's browser to another URL/page.
                // Tell the browser to go to /admin
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/home"); // or whatever your user home page is
            }
        };
    }

    // [ SecurityFilterChain ] is used to configure how Spring Security protects your application.
    // you are telling Spring Security what to do with incoming HTTP requests.
    // [ HttpSecurity ] is the object you use to configure Spring Security's security rules for HTTP requests.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // [ http ] is the HttpSecurity object that Spring gives to your method.
        http
                // [ csrf() ] protection helps prevent another website from tricking a user's browser into sending
                // unwanted requests to your application while the user is logged in.
                // [ AbstractHttpConfigurer ] is a Spring Security helper class used to configure or disable a security
                // feature in [ HttpSecurity ].
                .csrf(AbstractHttpConfigurer::disable) // Note: Enable CSRF in production!

                // [ authorizeHttpRequests() ] is used to define who is allowed to access which URLs in your application.
                // [ auth ] is just a variable name representing the object Spring Security gives you to configure
                // authorization rules.
                .authorizeHttpRequests(auth -> auth

                        // Public resources - no authentication required
                        // [ requestMatchers() ] is used to choose the URL/path that a security rule applies to.
                        // Only users with ADMIN role can access URLs matching /admin/**.
                        // [ permitAll() ] is used to allow everyone to access a URL without logging in.
                        .requestMatchers("/", "/home", "/login", "/register", "/error").permitAll()
                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**",
                                "/webjars/**", "/favicon.ico").permitAll()

                        // Admin-only endpoints
                        // [ hasRole() ] is used to allow access only to users who have a specific role.
                        // Only users with the ADMIN role can access /admin/**.
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // User and admin accessible endpoints
                        .requestMatchers("/user/**", "/cv", "/index", "/profile/**").hasAnyRole("USER", "ADMIN")

                        // All other requests require authentication
                        // [ authenticated() ] is used to allow access only to users who are logged in.
                        .anyRequest().authenticated()
                )

                // [ formLogin() ] is used to configure form-based login in Spring Security.
                .formLogin(form -> form
                        .loginPage("/login")

                        // [ successHandler() ] is used to define what happens after the user logs in successfully.
                        // After successful login, run customSuccessHandler().
                        // [ customSuccessHandler() ] is your own method that creates a custom [ AuthenticationSuccessHandler ].
                        // it is called custom because you created the handler yourself instead of using Spring Security's
                        // default behavior.
                        .successHandler(customSuccessHandler()) // Use custom success handler

                        // [ failureUrl() ] is used to tell Spring Security where to send the user when login fails.
                        // If the username or password is incorrect, redirect the user back to /login?error=true.
                        .failureUrl("/login?error=true")
                        .usernameParameter("username") // Default, but explicit
                        .passwordParameter("password") // Default, but explicit
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                )
                .sessionManagement(session -> session
                        .maximumSessions(1) // Limit to one session per user
                        .maxSessionsPreventsLogin(false) // Allow new login to invalidate old session
                );

        return http.build();
    }
}