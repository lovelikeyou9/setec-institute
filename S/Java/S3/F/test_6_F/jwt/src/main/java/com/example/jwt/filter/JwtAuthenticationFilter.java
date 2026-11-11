package com.example.jwt.filter;

import com.example.jwt.service.CustomUserDetailsService;
import com.example.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// [ OncePerRequestFilter ] is a Spring class that helps you create a filter that runs once for each
// HTTP request.
// "My JwtAuthenticationFilter is a filter that should execute once per request."
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // [ HttpServletRequest ] represents the HTTP request coming from the client to your Spring Boot application.
    // [ HttpServletResponse ] represents the HTTP response that your Spring Boot server sends back
    // to the client.
    // [ FilterChain ] is used to continue the HTTP request to the next filter in Spring Security.
    // "My filter is finished. Continue processing this request."
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // [ getServletPath() ] is used to get the URL path of the current HTTP request.
        // [ startsWith() ] is a Java String method used to check whether a string begins
        // with a specific text.
        // [ doFilter() ] is used to pass the current HTTP request and response to the next filter
        // in the filter chain.
        if (request.getServletPath().startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // [ getHeader() ] is used to read a value from an HTTP request header.
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                System.out.println("Invalid token: " + e.getMessage());
            }
        }

        // [ SecurityContextHolder ] is used to store and access the authentication information of
        // the current user.
        // "This request has been authenticated as this user."
        // [ getContext() ] gets the current Spring Security context.
        // [ getAuthentication() ] is used to get the authentication information of the current request/user.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // [ UserDetails ] is a Spring Security interface that represents a user's security information.
            // Find this user and give me the information Spring Security needs to authenticate them.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                // [ UsernamePasswordAuthenticationToken ] is a Spring Security object used to represent
                // an authenticated user.
                // "Spring Security, this is the user who has been authenticated, and these are their
                // permissions."
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // [ SecurityContextHolder ] is used to store and access the security information of
                // the current request/user.
                // SecurityContextHolder = where Spring Security keeps "who is logged in?"
                // [ setAuthentication ] is used to tell Spring Security who the currently authenticated user is.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
