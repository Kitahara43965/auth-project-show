package com.example.myauthapp.auth.token.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.example.myauthapp.support.database.cleaner.TestDatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.myauthapp.support.factory.TestUserFactory;
import com.example.myauthapp.auth.token.repository.RefreshTokenFamilyRepository;
import com.example.myauthapp.auth.token.repository.RefreshTokenRepository;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.support.factory.TestUserFactory;
import com.example.myauthapp.auth.token.entity.RefreshToken;
import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import com.example.myauthapp.auth.token.service.RefreshTokenCleanupService;
import com.example.myauthapp.auth.config.RefreshTokenCleanupProperties;
import java.time.LocalDateTime;
import java.time.Duration;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RefreshTokenCleanupServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestUserFactory testUserFactory;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenFamilyRepository refreshTokenFamilyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenCleanupProperties refreshTokenCleanupProperties;

    @Autowired
    private RefreshTokenCleanupService refreshTokenCleanupService;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.deleteAllDatabase();
    }

    @Test
    void deleteExpiredRefreshTokens(){

        User user = testUserFactory.testCreateUser();

        RefreshTokenFamily refreshTokenFamily =
            new RefreshTokenFamily(user);

        refreshTokenFamilyRepository.save(refreshTokenFamily);
        RefreshToken oldRefreshToken =
            new RefreshToken(
                refreshTokenFamily,
                "expired-token-hash",
                LocalDateTime.now()
                .minus(
                    refreshTokenCleanupProperties.getRetentionPeriod()
                )
                .minusDays(1)
            );

        refreshTokenRepository.save(oldRefreshToken);
        refreshTokenCleanupService.cleanupExpiredRefreshTokens();

        assertFalse(
            refreshTokenRepository
                .findById(oldRefreshToken.getId())
                .isPresent()
        );
    }

    @Test
    void deleteOldRefreshTokens(){

        User user = testUserFactory.testCreateUser();

        RefreshTokenFamily refreshTokenFamily =
            new RefreshTokenFamily(user);

        refreshTokenFamilyRepository.save(refreshTokenFamily);
        RefreshToken oldRefreshToken =
            new RefreshToken(
                refreshTokenFamily,
                "expired-token-hash",
                LocalDateTime.now().plusDays(30)
            );

        oldRefreshToken.setCreatedAtForTest(
            LocalDateTime.now()
                .minus(refreshTokenCleanupProperties.getRetentionPeriod())
                .minusDays(1)
        );

        refreshTokenRepository.save(oldRefreshToken);

        refreshTokenCleanupService.cleanupOldRefreshTokens();

        assertFalse(
            refreshTokenRepository
                .findById(oldRefreshToken.getId())
                .isPresent()
        );
    }

}