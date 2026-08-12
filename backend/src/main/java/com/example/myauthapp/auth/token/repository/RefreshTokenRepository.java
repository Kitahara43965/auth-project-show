package com.example.myauthapp.auth.token.repository;

import com.example.myauthapp.auth.token.entity.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(
        String tokenHash
    );

    Optional<RefreshToken>
        findTopByRefreshTokenFamily_User_IdOrderByCreatedAtDesc(
            Long userId
        );


    int deleteByExpiresAtBefore(
        LocalDateTime date
    );

    int deleteByCreatedAtBefore(
        LocalDateTime date
    );

}