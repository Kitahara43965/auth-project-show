package com.example.myauthapp.auth.controller;

import com.example.myauthapp.token.cookie.enums.CookieType;
import com.example.myauthapp.token.cookie.reader.CookieReader;
import com.example.myauthapp.token.cookie.service.CookieService;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenService;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenFamilyService;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;
import com.example.myauthapp.token.refreshtoken.repository.discovery
    .RefreshTokenFamilyDiscoveryJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.revoke
    .RefreshTokenRevokeJdbcRepository;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.log.service.save.AuditLogSaveService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class LogoutController {

    private final CookieService cookieService;
    private final CookieReader cookieReader;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenFamilyService refreshTokenFamilyService;
    private final RefreshTokenFamilyDiscoveryJdbcRepository
        refreshTokenFamilyDiscoveryJdbcRepository;
    private final RefreshTokenRevokeJdbcRepository
        refreshTokenRevokeJdbcRepository;
    private final AuditLogSaveService auditLogSaveService;

    public LogoutController(
        CookieService cookieService,
        CookieReader cookieReader,
        RefreshTokenService refreshTokenService,
        RefreshTokenFamilyService refreshTokenFamilyService,
        RefreshTokenFamilyDiscoveryJdbcRepository
            refreshTokenFamilyDiscoveryJdbcRepository,
        RefreshTokenRevokeJdbcRepository
            refreshTokenRevokeJdbcRepository,
        AuditLogSaveService auditLogSaveService
    ) {
        this.cookieService = cookieService;
        this.cookieReader = cookieReader;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenFamilyService = refreshTokenFamilyService;
        this.refreshTokenFamilyDiscoveryJdbcRepository =
            refreshTokenFamilyDiscoveryJdbcRepository;
        this.refreshTokenRevokeJdbcRepository =
            refreshTokenRevokeJdbcRepository;
        this.auditLogSaveService = auditLogSaveService;
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        String stringRefreshToken =
            cookieReader.getTokenFromCookie(
                request,
                CookieType.REFRESH_TOKEN
            );

        RefreshTokenDTO refreshTokenDTO = null;
        RefreshTokenFamilyDTO refreshTokenFamilyDTO = null;

        if (stringRefreshToken != null) {

            refreshTokenDTO =
                refreshTokenService.getRefreshTokenDTOByStringRefreshToken(
                    stringRefreshToken
                );

            refreshTokenFamilyDTO =
                refreshTokenFamilyDiscoveryJdbcRepository
                    .findRefreshTokenFamilyDTOByRefreshTokenId(
                        refreshTokenDTO.getId()
                    );

            refreshTokenRevokeJdbcRepository.revokeRefreshTokenById(
                refreshTokenDTO.getId()
            );

            refreshTokenFamilyService.revokeRefreshTokenFamily(
                refreshTokenFamilyDTO.getId()
            );

            auditLogSaveService.record(
                AuditEventType.LOGOUT,
                AuditResult.SUCCESS,
                refreshTokenFamilyDTO.getUserId()
            );

        } else {

            auditLogSaveService.record(
                AuditEventType.LOGOUT,
                AuditResult.SUCCESS,
                null
            );
        }

        cookieService.deleteTokenCookie(
            response,
            CookieType.ACCESS_TOKEN
        );

        cookieService.deleteTokenCookie(
            response,
            CookieType.REFRESH_TOKEN
        );

        return ResponseEntity.ok().build();
    }
}