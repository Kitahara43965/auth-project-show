package com.example.myauthapp.token.refreshtoken.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.myauthapp.token.refreshtoken.service.RefreshTokenFamilyService;
import com.example.myauthapp.token.refreshtoken.repository.insert.RefreshTokenInsertJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.update.RefreshTokenUpdateJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.discovery.RefreshTokenDiscoveryJdbcRepository;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenCreateDTO;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenCleanupService;
import com.example.myauthapp.token.refreshtoken.config.RefreshTokenCleanupProperties;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;
import com.example.myauthapp.user.factory.TestUserDTOFactory;

import java.time.LocalDateTime;
import java.time.Duration;

@SpringBootTest
@ActiveProfiles("test")
class RefreshTokenCleanupServiceTest {

    private final String TOKEN_HASH = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private RefreshTokenInsertJdbcRepository refreshTokenInsertJdbcRepository;

    @Autowired
    private RefreshTokenUpdateJdbcRepository refreshTokenUpdateJdbcRepository;

    @Autowired
    private RefreshTokenDiscoveryJdbcRepository refreshTokenDiscoveryJdbcRepository;

    @Autowired
    private RefreshTokenFamilyService refreshTokenFamilyService;

    @Autowired
    private RefreshTokenCleanupProperties refreshTokenCleanupProperties;

    @Autowired
    private RefreshTokenCleanupService refreshTokenCleanupService;

    @Autowired
    private TestUserDTOFactory testUserDTOFactory;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void deleteExpiredRefreshTokens(){

        Long userId = testUserDTOFactory.create().getId();

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyService.createRefreshTokenFamilyByUserId(userId);

        RefreshTokenCreateDTO refreshTokenCreateDTO =
            new RefreshTokenCreateDTO(
                TOKEN_HASH,
                LocalDateTime.now()
                    .minus(
                        refreshTokenCleanupProperties.getRetentionPeriod()
                    ).minusDays(1),
                refreshTokenFamilyDTO.getId()
            );

        refreshTokenInsertJdbcRepository.insertRefreshTokenDTOByRefreshTokenCreateDTO(
            refreshTokenCreateDTO
        );

        RefreshTokenDTO refreshTokenDTO =
            refreshTokenDiscoveryJdbcRepository.findRefreshTokenDTOByTokenHash(
                refreshTokenCreateDTO.getTokenHash()
            );
        
        refreshTokenCleanupService.cleanupExpiredRefreshTokens();

        Optional<RefreshTokenDTO> newOptionalRefreshTokenDTO =
            refreshTokenDiscoveryJdbcRepository.findOptionalRefreshTokenDTOByTokenHash(
                refreshTokenCreateDTO.getTokenHash()
            );

        assertTrue(newOptionalRefreshTokenDTO.isEmpty());
    }

    @Test
    void deleteOldRefreshTokens(){

        Long userId = testUserDTOFactory.create().getId();

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyService.createRefreshTokenFamilyByUserId(userId);

        RefreshTokenCreateDTO refreshTokenCreateDTO =
            new RefreshTokenCreateDTO(
                TOKEN_HASH,
                LocalDateTime.now().plusDays(30),
                refreshTokenFamilyDTO.getId()
            );

        refreshTokenInsertJdbcRepository.insertRefreshTokenDTOByRefreshTokenCreateDTO(
            refreshTokenCreateDTO
        );

        RefreshTokenDTO refreshTokenDTO =
            refreshTokenDiscoveryJdbcRepository.findRefreshTokenDTOByTokenHash(
                refreshTokenCreateDTO.getTokenHash()
            );

        refreshTokenUpdateJdbcRepository.updateRefreshTokenCreatedAtByIdAndCreatedAt(
            refreshTokenDTO.getId(),
            LocalDateTime.now()
                .minus(refreshTokenCleanupProperties.getRetentionPeriod())
                .minusDays(1)
        );

        refreshTokenCleanupService.cleanupOldRefreshTokens();

        Optional<RefreshTokenDTO> newOptionalRefreshTokenDTO =
            refreshTokenDiscoveryJdbcRepository.findOptionalRefreshTokenDTOByTokenHash(
                refreshTokenCreateDTO.getTokenHash()
            );

        assertTrue(newOptionalRefreshTokenDTO.isEmpty());
    }

}