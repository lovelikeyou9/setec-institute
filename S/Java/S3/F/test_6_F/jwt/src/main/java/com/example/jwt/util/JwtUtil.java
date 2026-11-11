package com.example.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // [ SecretKey ] is a Java object that represents the secret cryptographic key used to sign and
    // verify your JWT.
    private final SecretKey secretKey;

    // [ EXPIRATION ] defines how long your JWT token will be valid.
    private final long EXPIRATION = 1000 * 60 * 60; // 1 hour

    public JwtUtil(
            @Value("${jwt.secret}") String secret) {

        // [ Keys ] is a JJWT helper class that helps you create cryptographic keys for signing
        // and verifying JWTs.
        // [ hmacShaKeyFor ] is used to create a SecretKey from your secret bytes for HMAC-based
        // algorithms such as HS256.
        // Convert my secret text into a cryptographic SecretKey that can be used to sign and verify JWTs.
        // [ StandardCharsets ] is a Java class that provides standard character encodings, such as UTF-8.
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }


    // Generate JWT
    public String generateToken(String username) {

        // [ Jwts ] is a JJWT library class used to create and read JWT tokens.
        // [ setSubject() ] is used to put the main identity of the user into the JWT.
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION
                        )
                )
                // [ signWith() ] is used to digitally sign the JWT using your secret key.
                // "Use this secret key and HS256 algorithm to create a signature for this JWT."
                .signWith(
                        secretKey,
                        // [ SignatureAlgorithm ] tells the JWT library which algorithm to use
                        // when signing the JWT.
                        SignatureAlgorithm.HS256
                )
                // [ compact() ] tells the JWT library which algorithm to use when signing the JWT.
                .compact();
    }


    // Extract username
    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    // Extract expiration
    public Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }


    // Extract claim
    public <T> T extractClaim(
            String token,

            // [ Function ] is a Java interface used to take one value as input and return another value
            // as output.
            Function<Claims, T> claimsResolver) {

        return claimsResolver.apply(
                extractAllClaims(token)
        );
    }


    // Extract all claims
    private Claims extractAllClaims(String token) {

        // [ Jwts ] is a class from the JJWT library that helps you work with JWT tokens.
        // [ parserBuilder() ] is used to prepare a JWT parser that can read and verify an existing JWT.
        return Jwts.parserBuilder()
                // [ setSigningKey ] tells the JWT parser which secret key to use to verify the JWT's signature.
                .setSigningKey(secretKey)
                .build()
                // [ parseClaimsJws ] is used to read and verify a JWT token.
                .parseClaimsJws(token)
                .getBody();
    }


    // Check expiration
    public Boolean isTokenExpired(String token) {

        // [ before() ] compares two dates/times.
        // “Is this date earlier than the other date?”
        return extractExpiration(token)
                .before(new Date());
    }


    // Validate token
    public Boolean validateToken(
            String token,
            String username) {

        final String extractedUsername =
                extractUsername(token);

        // [ isTokenExpired() ] Its purpose is to check whether the JWT token has expired.
        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }
}