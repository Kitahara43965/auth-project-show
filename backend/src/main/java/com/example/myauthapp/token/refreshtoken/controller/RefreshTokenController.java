package com.example.myauthapp.token.refreshtoken.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myauthapp.token.cookie.enums.CookieType;
import com.example.myauthapp.token.cookie.reader.CookieReader;
import com.example.myauthapp.token.cookie.service.CookieService;
import com.example.myauthapp.token.jwttoken.service.AccessTokenJwtService;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenService;
import com.example.myauthapp.token.refreshtoken.dto.RotateRefreshTokenResult;

@RestController
@RequestMapping("/api")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final AccessTokenJwtService accessTokenJwtService;
    private final CookieService cookieService;
    private final CookieReader cookieReader;


    public RefreshTokenController(
        RefreshTokenService refreshTokenService,
        AccessTokenJwtService accessTokenJwtService,
        CookieService cookieService,
        CookieReader cookieReader
    ){
        this.refreshTokenService = refreshTokenService;
        this.accessTokenJwtService = accessTokenJwtService;
        this.cookieService = cookieService;
        this.cookieReader = cookieReader;
    }


    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
        HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse
    ) {
        String stringRefreshToken =
            cookieReader.getTokenFromCookie(
                httpServletRequest,
                CookieType.REFRESH_TOKEN
            );

        RotateRefreshTokenResult newRotateRefreshTokenResult =
            refreshTokenService.rotateRefreshToken(
                stringRefreshToken
            );

        Long userId =
            newRotateRefreshTokenResult.getUserId();

        String newStringRefreshToken =
            newRotateRefreshTokenResult.getStringRefreshToken();

        String newStringAccessToken =
            accessTokenJwtService.generateStringAccessToken(userId);

        cookieService.addTokenCookie(
            httpServletResponse,
            newStringRefreshToken,
            CookieType.REFRESH_TOKEN
        );

        cookieService.addTokenCookie(
            httpServletResponse,
            newStringAccessToken,
            CookieType.ACCESS_TOKEN
        );

        return ResponseEntity.ok().build();
    }
}