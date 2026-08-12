package com.example.myauthapp.auth.controller;

import com.example.myauthapp.auth.cookie.enums.CookieType;
import com.example.myauthapp.auth.cookie.reader.CookieReader;
import com.example.myauthapp.auth.cookie.service.CookieService;
import com.example.myauthapp.auth.token.service.RefreshTokenService;
import com.example.myauthapp.auth.token.service.RefreshTokenFamilyService;
import com.example.myauthapp.auth.token.entity.RefreshToken;
import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.auditlog.service.AuditLogService;

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
    private final AuditLogService auditLogService;

    public LogoutController(
            CookieService cookieService,
            CookieReader cookieReader,
            RefreshTokenService refreshTokenService,
            RefreshTokenFamilyService refreshTokenFamilyService,
            AuditLogService auditLogService
    ) {
        this.cookieService = cookieService;
        this.cookieReader = cookieReader;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenFamilyService = refreshTokenFamilyService;
        this.auditLogService = auditLogService;
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

        RefreshToken refreshToken = null;
        RefreshTokenFamily refreshTokenFamily = null;

        if (stringRefreshToken != null) {

            refreshToken =
                refreshTokenService.getRefreshToken(
                    stringRefreshToken
                );

            refreshTokenFamily =
                refreshToken.getRefreshTokenFamily();

            refreshTokenService.revokeRefreshToken(
                refreshToken
            );

            refreshTokenFamilyService.revokeRefreshTokenFamily(
                refreshTokenFamily
            );

            auditLogService.record(
                AuditEventType.LOGOUT,
                refreshTokenFamily.getUser().getId(),
                AuditResult.SUCCESS
            );

        } else {

            auditLogService.record(
                AuditEventType.LOGOUT,
                null,
                AuditResult.SUCCESS
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