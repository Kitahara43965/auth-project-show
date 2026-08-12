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
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.service.AuthService;

import com.example.myauthapp.auth.token.entity.RefreshToken;
import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import com.example.myauthapp.auth.token.repository.RefreshTokenRepository;
import com.example.myauthapp.auth.token.repository.RefreshTokenFamilyRepository;

import com.example.myauthapp.audit.auditlog.entity.AuditLog;
import com.example.myauthapp.audit.auditlog.repository.AuditLogRepository;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

import com.example.myauthapp.support.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.support.factory.TestRegisterRequestFactory;

import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.auth.cookie.enums.CookieType;
import com.example.myauthapp.support.helper.TestAuthHelper;
import com.example.myauthapp.support.helper.TestCookieHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenFamilyRepository refreshTokenFamilyRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private TestAuthHelper testAuthHelper;

    private TestCookieHelper testCookieHelper;


    @BeforeEach
    void setup() {
        testDatabaseCleaner.deleteAllDatabase();
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
            userRepository
                .findByEmail(registerRequest.getEmail())
                .orElseThrow()
                .getId();


        RefreshToken refreshToken =
            refreshTokenRepository
                .findTopByRefreshTokenFamily_User_IdOrderByCreatedAtDesc(userId)
                .orElseThrow();


        RefreshTokenFamily refreshTokenFamily =
            refreshToken.getRefreshTokenFamily();


        // when

        mockMvc.perform(
            post("/api/logout")
                .cookie(refreshTokenCookie)
        )
        .andExpect(status().isOk());


        // then

        RefreshToken revokedToken =
            refreshTokenRepository
                .findById(refreshToken.getId())
                .orElseThrow();

        assertThat(revokedToken.isRevoked())
            .isTrue();


        RefreshTokenFamily revokedFamily =
            refreshTokenFamilyRepository
                .findById(refreshTokenFamily.getId())
                .orElseThrow();

        assertThat(revokedFamily.isRevoked())
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


        User user =
            userRepository
                .findByEmail(registerRequest.getEmail())
                .orElseThrow();


        // when

        mockMvc.perform(
            post("/api/logout")
                .cookie(refreshTokenCookie)
        )
        .andExpect(status().isOk());


        // then

        AuditLog auditLog =
            auditLogRepository
                .findTopByActorIdOrderByOccurredAtDesc(user.getId())
                .orElseThrow();


        assertThat(auditLog.getAuditEventType())
            .isEqualTo(AuditEventType.LOGOUT);

        assertThat(auditLog.getAuditResult())
            .isEqualTo(AuditResult.SUCCESS);

        assertThat(auditLog.getActorId())
            .isEqualTo(user.getId());
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
