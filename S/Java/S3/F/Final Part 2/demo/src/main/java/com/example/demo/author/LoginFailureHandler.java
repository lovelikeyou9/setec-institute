package com.example.demo.author;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Maps login failures to clear query flags for the login page alert.
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String flag = "bad_credentials";
        if (exception instanceof DisabledException) {
            flag = "disabled";
        }
        String redirect = request.getContextPath() + "/login?error="
                + URLEncoder.encode(flag, StandardCharsets.UTF_8);
        response.sendRedirect(redirect);
    }
}
