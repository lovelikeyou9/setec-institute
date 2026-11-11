
// The [ security ] folder is where you put classes that handle application security
// Usually things related to:
// - Login/authentication → who is the user?
// - Authorization → what can the user access?
// - Password handling → PasswordEncoder
// - Roles/permissions → GrantedAuthority
// - Security configuration → SecurityFilterChain
// - Login success/failure → AuthenticationSuccessHandler
// - Logout configuration

// security folder = classes responsible for protecting your application and controlling login,
// authentication, authorization, roles, and access.

package com.example.demo.security;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// [ UserDetailsService ] is used by Spring Security to find/load a user's information, usually from the database,
// when the user tries to log in.
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    // [ UserDetails ] is a Spring Security interface that represents the information about the logged-in user.
    // [ UsernameNotFoundException ] is used in Spring Security when the username cannot be found in the database.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String role = user.getRole() == null ? "user" : user.getRole().trim().toLowerCase();
        String authority = "ROLE_" + role.toUpperCase();

        // [ User ] It creates a Spring Security UserDetails object.
        // [ User ] is Spring Security's User class.
        // [ builder() ] is used to create a Spring Security User object step by step.
        // [ builder() ] starts the Builder pattern, allowing you to set the user's information:
        // So:
        // builder() → starts building the user
        // username() → sets username
        // password() → sets password
        // authorities() → sets roles/permissions
        // build() → creates the final object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                // [ SimpleGrantedAuthority ] is used in Spring Security to represent a user's permission or role.
                .authorities(List.of(new SimpleGrantedAuthority(authority)))
                .build();
    }
}
