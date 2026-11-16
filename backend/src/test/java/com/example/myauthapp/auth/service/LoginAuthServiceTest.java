package com.example.myauthapp.auth.service;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.BeforeEach;

import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.result.AuthResult;
import com.example.myauthapp.auth.error.FieldAuthError;
import com.example.myauthapp.web.auth.exception.AuthException;
import com.example.myauthapp.user.factory.TestUserDTOFactory;
import com.example.myauthapp.auth.factory.TestRegisterRequestFactory;
import com.example.myauthapp.audit.log.dto.AuditLogDTO;
import com.example.myauthapp.audit.log.repository.discovery.AuditLogDiscoveryJdbcRepository;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;
import com.example.myauthapp.auth.util.TestUserUtils;
import com.example.myauthapp.user.dto.UserDTO;

@ActiveProfiles("test")
@SpringBootTest
class LoginAuthServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private AuthService authService;

    @Autowired
    private TestUserDTOFactory testUserDTOFactory;

    @Autowired
    private TestRegisterRequestFactory testRegisterRequestFactory;

    @Autowired
    private AuditLogDiscoveryJdbcRepository auditLogDiscoveryJdbcRepository;

    @Autowired
    private UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
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
        AuthException authException =
            assertThrows(
                AuthException.class,
                () -> authService.login(loginRequest)
            );

        // then
        assertThat(authException.getAuthError())
            .isEqualTo(FieldAuthError.INVALID_CREDENTIALS);
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
        AuthException authException =
            assertThrows(
                AuthException.class,
                () -> authService.login(loginRequest)
            );

        // then
        assertThat(authException.getAuthError())
            .isEqualTo(FieldAuthError.INVALID_CREDENTIALS);
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
        AuthException authException =
            assertThrows(
                AuthException.class,
                () -> authService.login(loginRequest)
            );

        // then
        assertThat(authException.getAuthError())
            .isEqualTo(FieldAuthError.AUTH_NO_EMAIL);
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
        AuditLogDTO auditLogDTO =
            auditLogDiscoveryJdbcRepository
                .findAuditLogDTOByActorIdOrderByOccurredAtDesc(userId);

        assertThat(auditLogDTO.getAuditEventType())
            .isEqualTo(AuditEventType.LOGIN);

        assertThat(auditLogDTO.getAuditResult())
            .isEqualTo(AuditResult.SUCCESS);

        assertThat(auditLogDTO.getActorId())
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
        UserDTO userDTO = userDiscoveryJdbcRepository.findUserDTOById(userId);

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
        AuditLogDTO auditLogDTO =
            auditLogDiscoveryJdbcRepository
                .findAuditLogDTOByActorIdOrderByOccurredAtDesc(userDTO.getId());

        assertThat(auditLogDTO.getAuditEventType())
            .isEqualTo(AuditEventType.LOGIN);

        assertThat(auditLogDTO.getAuditResult())
            .isEqualTo(AuditResult.FAILURE);

        assertThat(auditLogDTO.getActorId())
            .isEqualTo(userDTO.getId());
    }
}