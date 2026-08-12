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
public class AccessTokenJwtService {

    private SecretKey accessTokenSecretKey;

    private final JwtProperties jwtProperties;

    public AccessTokenJwtService(
            JwtProperties jwtProperties
    ) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        accessTokenSecretKey =
            Keys.hmacShaKeyFor(
                jwtProperties.getAccessTokenSecret().getBytes(StandardCharsets.UTF_8)
            );
    }

    public String generateAccessToken(User user) {

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
                    .claim("name", user.getName())
                    .claim("userId", user.getId())
                    .issuedAt(new Date())
                    .expiration(
                        new Date(
                            System.currentTimeMillis()
                            + jwtProperties.getAccessTokenExpiration()
                        )
                    )
                    .signWith(accessTokenSecretKey)
                    .compact();
        }else{//tokenJwtError
            throw new TokenJwtException(tokenJwtError);
        }//tokenJwtError
    }

    public String extractEmail(String token) {

        if(token == null){
            throw new TokenJwtException(TokenJwtError.TOKEN_JWT_NO_TOKEN);
        }else{//token
            return Jwts.parser()
                    .verifyWith(accessTokenSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }//token
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