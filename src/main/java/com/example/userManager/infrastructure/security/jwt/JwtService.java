package com.example.userManager.infrastructure.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    private static final long TOKEN_VALIDITY = 1000 * 60 * 60 * 10;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(UUID userId, String email, String username) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("username", username);

        return generateToken(userId, claims);
    }

    private String generateToken(UUID userId, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }
}
