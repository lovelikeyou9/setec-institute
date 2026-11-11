package com.example.jwt.service;

import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;

// [ UserDetailsService ] is a Spring Security interface used to load a user's information from your database.
// "Given this username, find the user's security information."
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // [ UsernameNotFoundException ] is a Spring Security exception used when a username cannot be found.
    // "I searched for this username, but it doesn't exist."
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // [ UserBuilder ] is a helper from Spring Security used to create a UserDetails object.
        // [ withUsername ] is used to set the username when creating a Spring Security UserDetails.
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
        builder.password(user.getPassword());
        builder.roles(user.getRole());
        return builder.build();
    }
}
