package com.example.myauthapp.token.refreshtoken.service;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenTokenError;
import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.config.RefreshTokenProperties;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenCleanupService;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenService;
import com.example.myauthapp.token.refreshtoken.config.RefreshTokenCleanupProperties;
import com.example.myauthapp.user.factory.TestUserDTOFactory;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenFamilyService;
import com.example.myauthapp.token.refreshtoken.repository.discovery
    .RefreshTokenFamilyDiscoveryJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.update
    .RefreshTokenUpdateJdbcRepository;


@SpringBootTest
@ActiveProfiles("test")
class RefreshTokenRotationServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenProperties refreshTokenProperties;

    @Autowired
    private RefreshTokenFamilyService refreshTokenFamilyService;

    @Autowired
    private RefreshTokenFamilyDiscoveryJdbcRepository
        refreshTokenFamilyDiscoveryJdbcRepository;

    @Autowired
    private RefreshTokenUpdateJdbcRepository
        refreshTokenUpdateJdbcRepository;

    @Autowired
    private TestUserDTOFactory testUserDTOFactory;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void refreshTokenRotationSuccess() {

        // ① Family作成
        Long userId = testUserDTOFactory.create().getId();

        RefreshTokenFamilyDTO oldRefreshTokenFamilyDTO =
            refreshTokenFamilyService.createRefreshTokenFamilyByUserId(userId);

        // ② Token作成
        String oldStringRefreshToken =
            refreshTokenService.createRefreshTokenByRefreshTokenFamilyDTO(
                oldRefreshTokenFamilyDTO
            ).getStringRefreshToken();

        // ③ Rotation
        String newStringRefreshToken =
            refreshTokenService
                .rotateRefreshToken(oldStringRefreshToken)
                .getStringRefreshToken();

        // ④ 古いToken確認
        RefreshTokenDTO oldRefreshTokenDTO =
            refreshTokenService.getRefreshTokenDTOByStringRefreshToken(
                oldStringRefreshToken
            );

        assertTrue(oldRefreshTokenDTO.isRevoked());

        // ⑤ 新しいToken確認
        RefreshTokenDTO newRefreshTokenDTO =
            refreshTokenService.getRefreshTokenDTOByStringRefreshToken(
                newStringRefreshToken
            );

        assertFalse(newRefreshTokenDTO.isRevoked());

        RefreshTokenFamilyDTO currentRefreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository.findRefreshTokenFamilyDTOByRefreshTokenId(
                oldRefreshTokenDTO.getId()
            );

        RefreshTokenFamilyDTO newRefreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository.findRefreshTokenFamilyDTOByRefreshTokenId(
                newRefreshTokenDTO.getId()
            );

        assertEquals(
            currentRefreshTokenFamilyDTO.getId(),
            newRefreshTokenFamilyDTO.getId()
        );
    }

    @Test
    void reuseDetectionTrigger() {

        Long userId = testUserDTOFactory.create().getId();

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyService.createRefreshTokenFamilyByUserId(userId);

        String stringRefreshToken =
            refreshTokenService.createRefreshTokenByRefreshTokenFamilyDTO(
                refreshTokenFamilyDTO
            ).getStringRefreshToken();

        // ① 1回目のrefresh
        refreshTokenService.rotateRefreshToken(stringRefreshToken);

        // ② 同じ古いTokenをもう一度使用
        TokenException exception =
            assertThrows(
                TokenException.class,
                () ->
                    refreshTokenService.rotateRefreshToken(stringRefreshToken)
            );

        assertEquals(
            RefreshTokenTokenError.REFRESH_TOKEN_REUSE_IS_DETECTED,
            exception.getTokenError()
        );

        RefreshTokenFamilyDTO savedRefreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository.findRefreshTokenFamilyDTOById(
                refreshTokenFamilyDTO.getId()
            );

        assertTrue(savedRefreshTokenFamilyDTO.isRevoked());
    }

    @Test
    void oldRefreshTokenCannotBeRotated() {

        Long userId = testUserDTOFactory.create().getId();

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyService.createRefreshTokenFamilyByUserId(userId);

        String stringRefreshToken =
            refreshTokenService.createRefreshTokenByRefreshTokenFamilyDTO(
                refreshTokenFamilyDTO
            ).getStringRefreshToken();

        RefreshTokenDTO refreshTokenDTO =
            refreshTokenService.getRefreshTokenDTOByStringRefreshToken(stringRefreshToken);

        refreshTokenUpdateJdbcRepository.
            updateRefreshTokenExpiresAtByIdAndExpiresAt(
                refreshTokenDTO.getId(),
                LocalDateTime.now().minusSeconds(1)
            );

        TokenException exception =
            assertThrows(
                TokenException.class,
                () ->
                    refreshTokenService.rotateRefreshToken(stringRefreshToken)
            );

        assertEquals(
            RefreshTokenTokenError.REFRESH_TOKEN_IS_EXPIRED,
            exception.getTokenError()
        );
    }

    @Test
    void rotatedRefreshTokenCanBeUsed() {

        Long userId = testUserDTOFactory.create().getId();

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyService.createRefreshTokenFamilyByUserId(userId);

        String oldStringRefreshToken =
            refreshTokenService.createRefreshTokenByRefreshTokenFamilyDTO(
                refreshTokenFamilyDTO
            ).getStringRefreshToken();

        String newStringRefreshToken =
            refreshTokenService
                .rotateRefreshToken(oldStringRefreshToken)
                .getStringRefreshToken();

        // 新しいTokenは正常にRotationできる
        String newestStringRefreshToken =
            refreshTokenService
                .rotateRefreshToken(newStringRefreshToken)
                .getStringRefreshToken();

        assertNotNull(newestStringRefreshToken);
    }

    @Test
    void rotatedOldTokenCannotBeUsedAgain() {

        Long userId = testUserDTOFactory.create().getId();

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyService.createRefreshTokenFamilyByUserId(userId);

        String oldStringRefreshToken =
            refreshTokenService.createRefreshTokenByRefreshTokenFamilyDTO(
                refreshTokenFamilyDTO
            ).getStringRefreshToken();

        String newStringRefreshToken =
            refreshTokenService
                .rotateRefreshToken(oldStringRefreshToken)
                .getStringRefreshToken();

        assertNotNull(newStringRefreshToken);

        TokenException exception =
            assertThrows(
                TokenException.class,
                () ->
                    refreshTokenService.rotateRefreshToken(oldStringRefreshToken)
            );

        assertEquals(
            RefreshTokenTokenError.REFRESH_TOKEN_REUSE_IS_DETECTED,
            exception.getTokenError()
        );

        RefreshTokenFamilyDTO savedRefreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository.findRefreshTokenFamilyDTOById(
                refreshTokenFamilyDTO.getId()
            );

        assertTrue(savedRefreshTokenFamilyDTO.isRevoked());
    }
}