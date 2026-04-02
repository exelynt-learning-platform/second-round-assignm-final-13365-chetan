package com.multigenesys.ecommerce.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String DEFAULT_SECRET = "change-this-before-production-change-this-before-production";
    private static final long DEFAULT_EXPIRATION_MS = 86_400_000L;

    @Value("${app.jwt.secret}")
    private String secretValue = DEFAULT_SECRET;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMsValue = DEFAULT_EXPIRATION_MS;

    private static SecretKey secretKey;
    private static long expirationMs = DEFAULT_EXPIRATION_MS;

    @PostConstruct
    void init() {
        expirationMs = expirationMsValue > 0 ? expirationMsValue : DEFAULT_EXPIRATION_MS;
        String secret = (secretValue == null || secretValue.isBlank()) ? DEFAULT_SECRET : secretValue;
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(String email) {
        ensureInitialized();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public static String extractEmail(String token) {
        ensureInitialized();
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    private static void ensureInitialized() {
        if (secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(DEFAULT_SECRET.getBytes(StandardCharsets.UTF_8));
            expirationMs = DEFAULT_EXPIRATION_MS;
        }
    }
}
