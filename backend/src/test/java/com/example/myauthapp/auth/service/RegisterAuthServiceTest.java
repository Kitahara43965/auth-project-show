package com.example.myauthapp.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.error.AuthError;
import com.example.myauthapp.auth.exception.AuthException;
import com.example.myauthapp.auth.result.AuthResult;

import com.example.myauthapp.audit.auditlog.entity.AuditLog;
import com.example.myauthapp.audit.auditlog.repository.AuditLogRepository;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

import com.example.myauthapp.support.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.support.factory.TestRegisterRequestFactory;
import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.audit.auditlog.service.AuditLogService;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RegisterAuthServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private TestRegisterRequestFactory testRegisterRequestFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.deleteAllDatabase();
    }

    @Test
    void testRegisterShouldFailWhenEmailAlreadyExists() {

        RegisterRequest request =
            testRegisterRequestFactory.create();

        // 1回目
        authService.register(request);

        // 2回目
        AuthException exception = assertThrows(
            AuthException.class,
            () -> authService.register(request)
        );

        assertThat(exception.getAppError())
            .isEqualTo(AuthError.EMAIL_ALREADY_EXISTS);
    }


    @Test
    void testRegisterShouldCreateUser() {

        RegisterRequest registerRequest
             = testRegisterRequestFactory.create();

        // when
        AuthResult authResult =
            authService.register(registerRequest);

        // then
        User user =
            userRepository
                .findByEmail(registerRequest.getEmail())
                .orElseThrow();

        assertThat(user.getId())
            .isNotNull();

        assertThat(user.getEmail())
            .isEqualTo(registerRequest.getEmail());

        assertThat(
            passwordEncoder.matches(
                registerRequest.getPassword(),
                user.getPassword()
            )
        ).isTrue();

        assertThat(user.getName())
            .isEqualTo(registerRequest.getName());

        assertThat(authResult)
            .isNotNull();

        assertThat(authResult.getUserId())
            .isEqualTo(user.getId());

    }

    @Test
    void testRegisterShouldCreateAuditLog() {
        RegisterRequest registerRequest
             = testRegisterRequestFactory.create();

        // when
        AuthResult authResult =
            authService.register(registerRequest);

        // then
        User user =
            userRepository
                .findByEmail(registerRequest.getEmail())
                .orElseThrow();

        AuditLog auditLog =
            auditLogRepository
                .findTopByActorIdOrderByOccurredAtDesc(
                    user.getId()
                )
                .orElseThrow();

        assertThat(auditLog.getAuditEventType())
            .isEqualTo(AuditEventType.REGISTER);

        assertThat(auditLog.getAuditResult())
            .isEqualTo(AuditResult.SUCCESS);

        assertThat(auditLog.getActorId())
            .isEqualTo(user.getId());
    }
}