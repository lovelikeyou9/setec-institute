package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

// [ @Configuration ] This class contains configuration/settings for my application, and Spring should manage the objects
// (beans) defined here.
@Configuration
// [ @EnableWebSecurity ] Enable Spring Security's web security features for this application.
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    // [ PasswordEncoder ] is used in Spring Security to securely hash passwords before storing or checking them.
    // [ PasswordEncoder ] turns the password into a hash.
    // "123"
    //  ↓
    //PasswordEncoder
    //  ↓
    //BCrypt hash
    //  ↓
    //"$2a$10$..."
    @Bean
    public PasswordEncoder passwordEncoder() {
        // [ BCryptPasswordEncoder() ] is a specific implementation of PasswordEncoder that uses BCrypt to
        // hash passwords securely.
        return new BCryptPasswordEncoder();
    }

    @Bean
    // [ DaoAuthenticationProvider ] is a Spring Security component that authenticates a username and
    // password using a UserDetailsService and PasswordEncoder.
    // [ PasswordEncoder ] is for checking passwords securely.
    // Use this password hashing method to check the password when the user logs in.
    // when user input 123 but in database is had hash password [ $2a$10$... ]
    // then PasswordEncoder is used to check 123 and $2a$10$... is correct or not
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    // [ AuthenticationSuccessHandler ] is used after the user successfully logs in.
    // It lets you decide what to do after login succeeds.
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {

            // [ getAuthorities() ] is used to get the user's roles/permissions after they log in.
            boolean isAdmin = authentication.getAuthorities().stream()
                    // [ grantedAuthority ] represents one role or permission that a user has.
                    // This user has the ROLE_ADMIN authority.
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            // [ sendRedirect() ] is used to send the user's browser to another URL/page.
            if (isAdmin) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/user");
            }
        };
    }

    @Bean
    // [ SecurityFilterChain ] is used to configure how Spring Security protects your application.
    // Which pages are public
    // Which pages require login
    // Which roles can access which pages
    // How login works
    // How logout works
    // CSRF settings
    // [ HttpSecurity ] is used to configure Spring Security's security rules for HTTP requests.
    // the object you use to tell Spring Security what security rules your website should have.
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                // [ AbstractHttpConfigurer ] is used when you want to disable or customize a Spring Security
                // configuration inside [ HttpSecurity ].
                .csrf(AbstractHttpConfigurer::disable)
                // [ authenticationProvider() ] is used to tell Spring Security how to authenticate (log in) the user.
                .authenticationProvider(authenticationProvider)
                // [ authorizeHttpRequests() ] is used to define who is allowed to access each URL in your application.
                .authorizeHttpRequests(auth -> auth
                        // [ requestMatchers() ] is used to choose which URL/request a security rule applies to.
                        // [ permitAll() ] means everyone is allowed to access that URL without logging in.
                        .requestMatchers("/", "/home", "/login", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**",
                                "/webjars/**", "/favicon.ico").permitAll()
                        // [ hasRole() ] is used to allow access only to users who have a specific role.
                        .requestMatchers("/admin/**", "/users/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        // [ .anyRequest() ] is used to apply a security rule to all requests/URLs that were
                        // not already matched by the previous rules.
                        // [ authenticated() ] is used to allow access only to users who have successfully logged in.
                        .anyRequest().authenticated()
                )
                // [ formLogin() ] is used to configure username/password login using a web form in Spring Security.
                .formLogin(form -> form
                        // [ loginPage() ] is used to tell Spring Security which URL is your custom login page.
                        .loginPage("/login")
                        // [ successHandler() ] is used to decide what happens after the user logs in successfully.
                        .successHandler(customSuccessHandler())
                        // [ failureUrl() ] is used to tell Spring Security where to send the user when login fails.
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        // [ logoutRequestMatcher() ] is used to tell Spring Security which request/URL should trigger logout.
                        // [ PathPatternRequestMatcher ] is used to match an HTTP request URL against a URL pattern so
                        // Spring Security can apply a rule to it.
                        // [ withDefaults() ] means use the default configuration.
                        // Create a [ PathPatternRequestMatcher ] using Spring Security's default settings.
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/logout"))
                        // [ logoutSuccessUrl ] is used to tell Spring Security where to send the user after they
                        // successfully log out.
                        .logoutSuccessUrl("/login?logout=true")
                        // [ invalidateHttpSession() ] is used to destroy the user's HTTP session when they log out.
                        .invalidateHttpSession(true)
                        // [ deleteCookies() ] is used to delete specific cookies when the user logs out.
                        .deleteCookies("JSESSIONID")
                        // [ clearAuthentication() ] is used to remove the user's authentication information from
                        // the current security context.
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        // [ accessDeniedPage() ] is used to choose which page to show when a logged-in user
                        // does not have permission to access a page.
                        .accessDeniedPage("/access-denied")
                )
                // [ sessionManagement() ] is used to configure how Spring Security manages the user's login session.
                // One user account can have only 1 active login session at a time.
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        // [ maxSessionsPreventsLogin() ] is used to decide what happens when a user tries to log
                        // in while they already have the maximum number of active sessions.
                        .maxSessionsPreventsLogin(false)
                );

        // [ build() ] is used to create the final SecurityFilterChain from all the security settings
        // you configured in http.
        return http.build();
    }
}