package com.example.myauthapp.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.error.AuthError;
import com.example.myauthapp.auth.exception.AuthException;

import com.example.myauthapp.auth.token.service.RefreshTokenService;
import com.example.myauthapp.auth.token.service.RefreshTokenFamilyService;

import com.example.myauthapp.audit.auditlog.service.AuditLogService;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.support.factory.TestRegisterRequestFactory;
import com.example.myauthapp.support.util.TestUserUtils;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUnitServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccessTokenJwtService accessTokenJwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenFamilyService refreshTokenFamilyService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private TestRegisterRequestFactory testRegisterRequestFactory;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegisterShouldFailWhenDataIntegrityViolationOccurs() {
        UUID uuid = UUID.randomUUID();

        RegisterRequest request = new RegisterRequest(
            TestUserUtils.getName(uuid),
            TestUserUtils.getPassword(uuid),
            TestUserUtils.getEmail(uuid),
            TestUserUtils.getPassword(uuid)
        );

        when(passwordEncoder.encode(request.getPassword()))
            .thenReturn("hashed-password");

        when(userRepository.existsByEmail(request.getEmail()))
            .thenReturn(false);

        when(userRepository.save(any(User.class)))
            .thenThrow(
                new DataIntegrityViolationException("duplicate")
            );

        AuthException exception = assertThrows(
            AuthException.class,
            () -> authService.register(request)
        );

        assertThat(exception.getAppError())
            .isEqualTo(AuthError.DATA_INTEGRITY_VIOLATION);

        verify(auditLogService).record(
            AuditEventType.REGISTER,
            null,
            AuditResult.FAILURE
        );
    }

}