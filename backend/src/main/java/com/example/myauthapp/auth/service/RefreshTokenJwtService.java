package com.example.myauthapp.auth.service;

import com.example.myauthapp.auth.config.JwtProperties;
import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.auth.error.TokenJwtError;
import com.example.myauthapp.auth.exception.TokenJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenJwtService {

    private SecretKey refreshTokenSecretKey;

    private final JwtProperties jwtProperties;

    public RefreshTokenJwtService(
            JwtProperties jwtProperties
    ) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {

        refreshTokenSecretKey =
            Keys.hmacShaKeyFor(
                jwtProperties.getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8)
            );
    }

    public String generateRefreshToken(User user) {
        TokenJwtError tokenJwtError = null;

         if(user == null){
            if(tokenJwtError == null){
                tokenJwtError = TokenJwtError.TOKEN_JWT_NO_USER;
            }//tokenJwtError
        }else{//user
            if(user.getEmail() == null){
                if(tokenJwtError == null){
                    tokenJwtError = TokenJwtError.TOKEN_JWT_NO_EMAIL;
                }//tokenJwtError
            }
        }//user
        
        if(tokenJwtError == null){
            return Jwts.builder()
                    .id(UUID.randomUUID().toString())
                    .subject(user.getEmail())
                    .issuedAt(new Date())
                    .expiration(
                        new Date(
                            System.currentTimeMillis()
                            + jwtProperties.getRefreshTokenExpiration()
                        )
                    )
                    .signWith(refreshTokenSecretKey)
                    .compact();
        }else{//tokenJwtError
            throw new TokenJwtException(tokenJwtError);
        }//tokenJwtError
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