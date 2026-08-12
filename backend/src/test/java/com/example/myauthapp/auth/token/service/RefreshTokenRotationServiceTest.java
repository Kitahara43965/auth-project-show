package com.example.myauthapp.auth.token.service;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.Duration;

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
import com.example.myauthapp.support.factory.TestRefreshTokenFactory;
import com.example.myauthapp.auth.token.entity.RefreshToken;
import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import com.example.myauthapp.auth.token.error.RefreshTokenError;
import com.example.myauthapp.auth.token.exception.RefreshTokenException;
import com.example.myauthapp.auth.token.repository.RefreshTokenFamilyRepository;
import com.example.myauthapp.auth.token.repository.RefreshTokenRepository;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.auth.config.JwtProperties;
import com.example.myauthapp.auth.token.service.RefreshTokenCleanupService;
import com.example.myauthapp.auth.token.service.RefreshTokenService;
import com.example.myauthapp.auth.config.RefreshTokenCleanupProperties;



@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RefreshTokenRotationServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestUserFactory testUserFactory;

    @Autowired
    private TestRefreshTokenFactory testRefreshTokenFactory;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenFamilyRepository refreshTokenFamilyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.deleteAllDatabase();
    }

    @Test
    void refreshTokenRotationSuccess(){

        // ① Family作成
        User user = testUserFactory.testCreateUser();

        RefreshTokenFamily refreshTokenFamily =
                new RefreshTokenFamily(user);

        refreshTokenFamilyRepository.save(refreshTokenFamily);

        // ② Token作成
        String token =
            refreshTokenService.createRefreshToken(
                user,
                refreshTokenFamily
            );


        RefreshToken refreshToken =
            refreshTokenService.getRefreshToken(token);

        // ③ Rotation
        String newToken =
            refreshTokenService.rotateRefreshToken(
                refreshToken
            );


        // ④ 古いToken確認
        RefreshToken oldToken =
            refreshTokenService.getRefreshToken(token);

        assertTrue(oldToken.isRevoked());


        // ⑤ 新しいToken確認
        RefreshToken currentToken =
            refreshTokenService.getRefreshToken(newToken);

        assertFalse(currentToken.isRevoked());

        assertEquals(
            oldToken.getRefreshTokenFamily().getId(),
            currentToken.getRefreshTokenFamily().getId()
        );

    }

    @Test
    void reuseDetectionTrigger(){

        User user = testUserFactory.testCreateUser();

        RefreshTokenFamily refreshTokenFamily =
            new RefreshTokenFamily(user);

        refreshTokenFamilyRepository.save(refreshTokenFamily);

        String token =
            refreshTokenService.createRefreshToken(
                user,
                refreshTokenFamily
            );

        RefreshToken refreshToken =
            refreshTokenService.getRefreshToken(token);


        // 1回目のrefresh
        refreshTokenService.rotateRefreshToken(refreshToken);


        RefreshToken revokedToken =
            refreshTokenService.getRefreshToken(token);

        RefreshTokenException exception =
            assertThrows(
                RefreshTokenException.class,
                () ->
                    refreshTokenService.rotateRefreshToken(
                        revokedToken
                    )
            );

        assertEquals(
            RefreshTokenError.REUSE_DETECTED,
            exception.getAppError()
        );

        assertTrue(
            refreshTokenFamily.isRevoked()
        );
    }

    @Test
    void oldRefreshTokenCannotBeRotated() {

        User user = testUserFactory.testCreateUser();

        RefreshTokenFamily refreshTokenFamily =
            new RefreshTokenFamily(user);

        long refreshTokenExpiration = jwtProperties.getRefreshTokenExpiration();

        refreshTokenFamilyRepository.save(refreshTokenFamily);


        RefreshToken expiredToken =
            new RefreshToken(
                refreshTokenFamily,
                "expired-hash",
                LocalDateTime.now()
                    .minus(Duration.ofMillis(refreshTokenExpiration))
            );

        refreshTokenRepository.save(expiredToken);

        RefreshTokenException exception =
            assertThrows(
                RefreshTokenException.class,
                () ->
                    refreshTokenService.rotateRefreshToken(
                        expiredToken
                    )
            );


        assertEquals(
            RefreshTokenError.REFRESH_TOKEN_IS_EXPIRED,
            exception.getAppError()
        );
    }

}