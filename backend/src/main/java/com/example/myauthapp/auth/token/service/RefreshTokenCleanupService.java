package com.example.myauthapp.auth.token.service;

import com.example.myauthapp.auth.token.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.myauthapp.auth.config.RefreshTokenCleanupProperties;

import java.time.LocalDateTime;

@Service
public class RefreshTokenCleanupService {


    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenCleanupProperties refreshTokenCleanupProperties;

    public RefreshTokenCleanupService(
        RefreshTokenRepository refreshTokenRepository,
        RefreshTokenCleanupProperties refreshTokenCleanupProperties
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenCleanupProperties = refreshTokenCleanupProperties;
    }


    @Transactional
    public void cleanupExpiredRefreshTokens() {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        refreshTokenRepository.deleteByExpiresAtBefore(localDateTimeNow);
    }

    @Transactional
    public void cleanupOldRefreshTokens() {
        LocalDateTime localDateTimeThreshold =
            LocalDateTime.now()
                .minus(
                    refreshTokenCleanupProperties.getRetentionPeriod()
                );

        refreshTokenRepository.deleteByCreatedAtBefore(localDateTimeThreshold);
    }

}