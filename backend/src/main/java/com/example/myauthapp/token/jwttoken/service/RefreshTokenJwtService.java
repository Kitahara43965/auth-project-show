package com.example.myauthapp.token.jwttoken.service;

import com.example.myauthapp.token.refreshtoken.config.RefreshTokenProperties;
import com.example.myauthapp.token.jwttoken.error.JwtTokenError;
import com.example.myauthapp.web.token.exception.TokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.Objects;
import javax.crypto.SecretKey;

@Service
public class RefreshTokenJwtService {

    private SecretKey refreshTokenSecretKey;

    private final RefreshTokenProperties refreshTokenProperties;

    public RefreshTokenJwtService(
            RefreshTokenProperties refreshTokenProperties
    ) {
        this.refreshTokenProperties = refreshTokenProperties;
    }

    @PostConstruct
    public void init() {

        refreshTokenSecretKey =
            Keys.hmacShaKeyFor(
                refreshTokenProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
            );
    }

    public String generateStringRefreshToken(Long userId) {

        Objects.requireNonNull(userId, "userId is null");

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(
                    new Date(
                        System.currentTimeMillis()
                        + refreshTokenProperties.getExpiration()
                    )
                )
                .signWith(refreshTokenSecretKey)
                .compact();
    }


    public boolean isValid(String token) {

        boolean isValidResult = false;

        try {

            if(token == null){
                isValidResult = false;
            }else{//token
                Jwts.parser()
                        .verifyWith(refreshTokenSecretKey)
                        .build()
                        .parseSignedClaims(token);
                isValidResult = true;
            }//token

        } catch (Exception e) {

            isValidResult = false;
        }

        return isValidResult;
    }
}