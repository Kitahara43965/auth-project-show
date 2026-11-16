package com.example.myauthapp.audit.log.service.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.error.FieldAuthError;
import com.example.myauthapp.web.auth.exception.AuthException;
import com.example.myauthapp.auth.result.AuthResult;

import com.example.myauthapp.audit.log.dto.AuditLogDTO;
import com.example.myauthapp.audit.log.repository.discovery.AuditLogDiscoveryJdbcRepository;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.user.factory.TestUserDTOFactory;

import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.auth.factory.TestRegisterRequestFactory;
import com.example.myauthapp.user.dto.UserDTO;
import com.example.myauthapp.auth.service.AuthService;
import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;


import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class RegisterAuthServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private AuthService authService;


    @Autowired
    private AuditLogDiscoveryJdbcRepository auditLogDiscoveryJdbcRepository;

    @Autowired
    private TestRegisterRequestFactory testRegisterRequestFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestUserDTOFactory testUserDTOFactory;

    @Autowired
    private UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void testRegisterShouldFailWhenEmailAlreadyExists() {

        RegisterRequest request =
            testRegisterRequestFactory.create();

        // 1回目
        authService.register(request);

        // 1回目のregisterが戻った直後に確認
        boolean exists = userDiscoveryJdbcRepository
            .doesUserExistByEmail(request.getEmail());
            
        // 2回目
        AuthException authException = assertThrows(
            AuthException.class,
            () -> authService.register(request)
        );

        assertThat(authException.getAuthError())
            .isEqualTo(FieldAuthError.EMAIL_ALREADY_EXISTS);
    }


    @Test
    void testRegisterShouldCreateUser() {

        RegisterRequest registerRequest
             = testRegisterRequestFactory.create();

        // when
        AuthResult authResult =
            authService.register(registerRequest);

        // then
        UserDTO userDTO =
            userDiscoveryJdbcRepository
                .findUserDTOByEmail(registerRequest.getEmail());

        assertThat(userDTO.getId())
            .isNotNull();

        assertThat(userDTO.getEmail())
            .isEqualTo(registerRequest.getEmail());

        assertThat(
            passwordEncoder.matches(
                registerRequest.getPassword(),
                userDTO.getPassword()
            )
        ).isTrue();

        assertThat(userDTO.getName())
            .isEqualTo(registerRequest.getName());

        assertThat(authResult)
            .isNotNull();

        assertThat(authResult.getUserId())
            .isEqualTo(userDTO.getId());

    }

    @Test
    void testRegisterShouldCreateAuditLog() {
        RegisterRequest registerRequest
             = testRegisterRequestFactory.create();

        // when
        AuthResult authResult =
            authService.register(registerRequest);

        // then
        UserDTO userDTO =
            userDiscoveryJdbcRepository
                .findUserDTOByEmail(registerRequest.getEmail());

        AuditLogDTO auditLogDTO =
            auditLogDiscoveryJdbcRepository
                .findAuditLogDTOByActorIdOrderByOccurredAtDesc(
                    userDTO.getId()
                );

        assertThat(auditLogDTO.getAuditEventType())
            .isEqualTo(AuditEventType.REGISTER);

        assertThat(auditLogDTO.getAuditResult())
            .isEqualTo(AuditResult.SUCCESS);

        assertThat(auditLogDTO.getActorId())
            .isEqualTo(userDTO.getId());
    }
}