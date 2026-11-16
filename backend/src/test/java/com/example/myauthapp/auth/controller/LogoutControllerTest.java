package com.example.myauthapp.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.service.AuthService;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;
import com.example.myauthapp.token.refreshtoken.repository.discovery
    .RefreshTokenFamilyDiscoveryJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.discovery.RefreshTokenDiscoveryJdbcRepository;

import com.example.myauthapp.audit.log.dto.AuditLogDTO;
import com.example.myauthapp.audit.log.repository.discovery.AuditLogDiscoveryJdbcRepository;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.auth.factory.TestRegisterRequestFactory;

import com.example.myauthapp.user.dto.UserDTO;
import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;
import com.example.myauthapp.token.cookie.enums.CookieType;
import com.example.myauthapp.auth.helper.TestAuthHelper;
import com.example.myauthapp.token.cookie.helper.TestCookieHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestRegisterRequestFactory testRegisterRequestFactory;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;

    @Autowired
    private RefreshTokenDiscoveryJdbcRepository refreshTokenDiscoveryJdbcRepository;

    @Autowired
    private RefreshTokenFamilyDiscoveryJdbcRepository
        refreshTokenFamilyDiscoveryJdbcRepository;

    @Autowired
    private AuditLogDiscoveryJdbcRepository auditLogDiscoveryJdbcRepository;

    private TestAuthHelper testAuthHelper;

    private TestCookieHelper testCookieHelper;


    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
        testAuthHelper = new TestAuthHelper(mockMvc);
        testCookieHelper = new TestCookieHelper();
    }


    @Test
    void logoutShouldRevokeRefreshTokenAndFamily() throws Exception {

        // given

        RegisterRequest registerRequest =
            testRegisterRequestFactory.create();

        authService.register(registerRequest);

        LoginRequest loginRequest =
            new LoginRequest(
                registerRequest.getEmail(),
                registerRequest.getPassword()
            );

        MockHttpServletResponse mockHttpServletResponse =
            testAuthHelper.login(loginRequest);


        Cookie refreshTokenCookie =
           TestCookieHelper.getTokenCookie(
                mockHttpServletResponse,
                CookieType.REFRESH_TOKEN
           );


        Long userId =
            userDiscoveryJdbcRepository
                .findUserDTOByEmail(registerRequest.getEmail())
                .getId();


        RefreshTokenDTO refreshTokenDTO =
            refreshTokenDiscoveryJdbcRepository
                .findTopRefreshTokenDTOByUserIdOrderByCreatedAtDesc(userId);


        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository
                .findRefreshTokenFamilyDTOByRefreshTokenId(
                    refreshTokenDTO.getId()
                );

        // when

        mockMvc.perform(
            post("/api/logout")
                .cookie(refreshTokenCookie)
        )
        .andExpect(status().isOk());


        // then

        RefreshTokenDTO revokedRefreshTokenDTO =
            refreshTokenDiscoveryJdbcRepository
                .findRefreshTokenDTOById(refreshTokenDTO.getId());

        assertThat(revokedRefreshTokenDTO.isRevoked())
            .isTrue();

        RefreshTokenFamilyDTO revokedRefreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository
                .findRefreshTokenFamilyDTOById(
                    refreshTokenFamilyDTO.getId()
                );

        assertThat(revokedRefreshTokenFamilyDTO.isRevoked())
            .isTrue();
    }


    @Test
    void logoutShouldCreateAuditLog() throws Exception {

        // given

        RegisterRequest registerRequest =
            testRegisterRequestFactory.create();

        authService.register(registerRequest);

        LoginRequest loginRequest =
            new LoginRequest(
                registerRequest.getEmail(),
                registerRequest.getPassword()
            );


        MockHttpServletResponse mockHttpServletResponse =
            testAuthHelper.login(loginRequest);


        Cookie refreshTokenCookie =
           TestCookieHelper.getTokenCookie(
                mockHttpServletResponse,
                CookieType.REFRESH_TOKEN
           );


        UserDTO userDTO =
            userDiscoveryJdbcRepository
                .findUserDTOByEmail(registerRequest.getEmail());


        // when

        mockMvc.perform(
            post("/api/logout")
                .cookie(refreshTokenCookie)
        )
        .andExpect(status().isOk());


        // then

        AuditLogDTO auditLogDTO =
            auditLogDiscoveryJdbcRepository
                .findAuditLogDTOByActorIdOrderByOccurredAtDesc(userDTO.getId());


        assertThat(auditLogDTO.getAuditEventType())
            .isEqualTo(AuditEventType.LOGOUT);

        assertThat(auditLogDTO.getAuditResult())
            .isEqualTo(AuditResult.SUCCESS);

        assertThat(auditLogDTO.getActorId())
            .isEqualTo(userDTO.getId());
    }


    @Test
    void logoutShouldDeleteAuthenticationCookies() throws Exception {

        // given

        RegisterRequest registerRequest =
            testRegisterRequestFactory.create();

        authService.register(registerRequest);


        LoginRequest loginRequest =
            new LoginRequest(
                registerRequest.getEmail(),
                registerRequest.getPassword()
            );


        MockHttpServletResponse mockHttpServletResponse =
            testAuthHelper.login(loginRequest);


        Cookie refreshTokenCookie =
            Arrays.stream(mockHttpServletResponse.getCookies())
                .filter(cookie ->
                    cookie.getName().equals(CookieType.REFRESH_TOKEN.getName())
                )
                .findFirst()
                .orElseThrow();


        // when

        MockHttpServletResponse logoutResponse =
            mockMvc.perform(
                post("/api/logout")
                    .cookie(refreshTokenCookie)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();


        // then

        Cookie accessTokenCookie =
            logoutResponse.getCookie(CookieType.ACCESS_TOKEN.getName());

        Cookie deletedRefreshTokenCookie =
            logoutResponse.getCookie(CookieType.REFRESH_TOKEN.getName());


        assertThat(accessTokenCookie)
            .isNotNull();

        assertThat(deletedRefreshTokenCookie)
            .isNotNull();


        assertThat(accessTokenCookie.getMaxAge())
            .isZero();

        assertThat(deletedRefreshTokenCookie.getMaxAge())
            .isZero();
    }
}
