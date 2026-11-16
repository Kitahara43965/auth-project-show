package com.example.myauthapp.token.refreshtoken.service;

import com.example.myauthapp.token.refreshtoken.repository.delete.RefreshTokenDeleteJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.myauthapp.token.refreshtoken.config.RefreshTokenCleanupProperties;

import java.time.LocalDateTime;

@Service
public class RefreshTokenCleanupService {


    private final RefreshTokenDeleteJdbcRepository refreshTokenDeleteJdbcRepository;
    private final RefreshTokenCleanupProperties refreshTokenCleanupProperties;

    public RefreshTokenCleanupService(
        RefreshTokenDeleteJdbcRepository refreshTokenDeleteJdbcRepository,
        RefreshTokenCleanupProperties refreshTokenCleanupProperties
    ) {
        this.refreshTokenDeleteJdbcRepository = refreshTokenDeleteJdbcRepository;
        this.refreshTokenCleanupProperties = refreshTokenCleanupProperties;
    }


    @Transactional(value="userTransactionManager")
    public void cleanupExpiredRefreshTokens() {
        refreshTokenDeleteJdbcRepository.deleteRefreshTokensByExpiresAtBefore(LocalDateTime.now());
    }

    @Transactional(value="userTransactionManager")
    public void cleanupOldRefreshTokens() {
        LocalDateTime localDateTimeThreshold =
            LocalDateTime.now()
                .minus(
                    refreshTokenCleanupProperties.getRetentionPeriod()
                );

        refreshTokenDeleteJdbcRepository.deleteRefreshTokensByCreatedAtBefore(localDateTimeThreshold);
    }

}