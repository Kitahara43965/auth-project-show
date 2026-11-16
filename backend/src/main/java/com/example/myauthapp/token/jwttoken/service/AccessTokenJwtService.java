package com.example.myauthapp.token.jwttoken.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.Objects;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import com.example.myauthapp.token.accesstoken.config.AccessTokenProperties;
import com.example.myauthapp.token.jwttoken.error.JwtTokenError;
import com.example.myauthapp.web.token.exception.TokenException;

@Service
public class AccessTokenJwtService {

    private SecretKey accessTokenSecretKey;

    private final AccessTokenProperties accessTokenProperties;

    public AccessTokenJwtService(
            AccessTokenProperties accessTokenProperties
    ) {
        this.accessTokenProperties = accessTokenProperties;
    }

    @PostConstruct
    public void init() {
        accessTokenSecretKey =
            Keys.hmacShaKeyFor(
                accessTokenProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
            );
    }
    
    public String generateStringAccessToken(Long userId) {

        Objects.requireNonNull(userId, "userId is null");

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(
                    new Date(
                        System.currentTimeMillis()
                        + accessTokenProperties.getExpiration()
                    )
                )
                .signWith(accessTokenSecretKey)
                .compact();
    }

    public Long extractUserId(String token) {

        if (token == null) {
            throw new TokenException(
                JwtTokenError.JWT_TOKEN_NO_TOKEN
            );
        }

        String subject =
            Jwts.parser()
                .verifyWith(accessTokenSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.valueOf(subject);
    }

    public boolean isValid(String token) {
         boolean isValidResult = false;

        try {

            if(token == null){
                isValidResult = false;
            }else{//token
                Jwts.parser()
                        .verifyWith(accessTokenSecretKey)
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