package com.example.myauthapp.auth.service;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.error.FieldAuthError;
import com.example.myauthapp.auth.util.TestUserUtils;
import com.example.myauthapp.auth.service.AuthService;
import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;
import com.example.myauthapp.user.repository.insert.UserInsertJdbcRepository;
import com.example.myauthapp.web.auth.exception.AuthException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional(value = "userTransactionManager")
class AuthUnitServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;

    @Autowired
    private UserInsertJdbcRepository userInsertJdbcRepository;

    @Autowired
    private JdbcTemplate auditJdbcTemplate;

    @Test
    void registerShouldFailWhenDataIntegrityViolationOccurs() {

        UUID uuid = UUID.randomUUID();

        RegisterRequest request = new RegisterRequest(
            TestUserUtils.getName(uuid),
            TestUserUtils.getPassword(uuid),
            TestUserUtils.getEmail(uuid),
            TestUserUtils.getPassword(uuid)
        );

        // 1回目：正常登録
        authService.register(request);

        // 2回目：同じメールアドレスで登録
        AuthException authException = assertThrows(
            AuthException.class,
            () -> authService.register(request)
        );

        assertThat(authException.getAuthError())
            .isEqualTo(FieldAuthError.EMAIL_ALREADY_EXISTS);
    }
}