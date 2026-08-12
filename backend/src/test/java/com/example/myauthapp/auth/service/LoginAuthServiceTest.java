package com.example.myauthapp.auth.service;

import org.junit.jupiter.api.BeforeEach;
import com.example.myauthapp.support.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.result.AuthResult;
import com.example.myauthapp.auth.error.AuthError;
import com.example.myauthapp.auth.exception.AuthException;
import com.example.myauthapp.support.dto.TestUserData;
import com.example.myauthapp.support.factory.TestUserFactory;
import com.example.myauthapp.support.factory.TestRegisterRequestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.example.myauthapp.audit.auditlog.entity.AuditLog;
import com.example.myauthapp.audit.auditlog.repository.AuditLogRepository;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.support.util.TestUserUtils;
import com.example.myauthapp.user.entity.User;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;



import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LoginAuthServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private AuthService authService;

    @Autowired
    private TestUserFactory testUserFactory;

    @Autowired
    private TestRegisterRequestFactory testRegisterRequestFactory;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.deleteAllDatabase();
    }


    @Test
    void loginShouldAuthenticateUser() {

        // given
        RegisterRequest registerRequest =
            testRegisterRequestFactory.create();

        authService.register(registerRequest);

        LoginRequest loginRequest =
            new LoginRequest(
                registerRequest.getEmail(),
                registerRequest.getPassword()
            );

        // when
        AuthResult authResult =
            authService.login(loginRequest);

        // then
        assertThat(authResult)
            .isNotNull();

        assertThat(authResult.getUserId())
            .isNotNull();

        assertThat(authResult.getAccessToken())
            .isNotBlank();

        assertThat(authResult.getRefreshToken())
            .isNotBlank();
    }

    @Test
    void loginShouldFailWhenEmailDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        // given
        LoginRequest loginRequest =
            new LoginRequest(
                TestUserUtils.getEmail(uuid),
                TestUserUtils.getPassword(uuid)
            );

        // when
        AuthException exception =
            assertThrows(
                AuthException.class,
                () -> authService.login(loginRequest)
            );

        // then
        assertThat(exception.getAppError())
            .isEqualTo(AuthError.INVALID_CREDENTIALS);
    }

    @Test
    void loginShouldFailWhenPasswordIsIncorrect() {

        // given
        RegisterRequest registerRequest =
            testRegisterRequestFactory.create();

        authService.register(registerRequest);

        LoginRequest loginRequest =
            new LoginRequest(
                registerRequest.getEmail(),
                "incorrect-password"
            );

        // when
        AuthException exception =
            assertThrows(
                AuthException.class,
                () -> authService.login(loginRequest)
            );

        // then
        assertThat(exception.getAppError())
            .isEqualTo(AuthError.INVALID_CREDENTIALS);
    }

    @Test
    void loginShouldFailWhenEmailIsNull() {

        // given
        LoginRequest loginRequest =
            new LoginRequest(
                null,
                "password"
            );

        // when
        AuthException exception =
            assertThrows(
                AuthException.class,
                () -> authService.login(loginRequest)
            );

        // then
        assertThat(exception.getAppError())
            .isEqualTo(AuthError.AUTH_NO_EMAIL);
    }



    @Test
    void loginShouldCreateAuditLog() {
        // given
        RegisterRequest registerRequest =
            testRegisterRequestFactory.create();

        authService.register(registerRequest);

        LoginRequest loginRequest =
            new LoginRequest(
                registerRequest.getEmail(),
                registerRequest.getPassword()
            );

        // when
        AuthResult authResult =
            authService.login(loginRequest);

        Long userId = authResult.getUserId();

        // then
        AuditLog auditLog =
            auditLogRepository
                .findTopByActorIdOrderByOccurredAtDesc(userId)
                .orElseThrow();

        assertThat(auditLog.getAuditEventType())
            .isEqualTo(AuditEventType.LOGIN);

        assertThat(auditLog.getAuditResult())
            .isEqualTo(AuditResult.SUCCESS);

        assertThat(auditLog.getActorId())
            .isEqualTo(userId);
    }

    @Test
    void loginShouldCreateFailureAuditLogWhenPasswordIsIncorrect() {

        // given
        RegisterRequest registerRequest =
            testRegisterRequestFactory.create();

        AuthResult authResult =
            authService.register(registerRequest);

        Long userId = authResult.getUserId();
        User user = userRepository
            .findById(userId)
            .orElseThrow();

        LoginRequest loginRequest =
            new LoginRequest(
                registerRequest.getEmail(),
                "incorrect-password"
            );

        // when
        assertThrows(
            AuthException.class,
            () -> authService.login(loginRequest)
        );

        // then
        AuditLog auditLog =
            auditLogRepository
                .findTopByActorIdOrderByOccurredAtDesc(user.getId())
                .orElseThrow();

        assertThat(auditLog.getAuditEventType())
            .isEqualTo(AuditEventType.LOGIN);

        assertThat(auditLog.getAuditResult())
            .isEqualTo(AuditResult.FAILURE);

        assertThat(auditLog.getActorId())
            .isEqualTo(user.getId());
    }
}