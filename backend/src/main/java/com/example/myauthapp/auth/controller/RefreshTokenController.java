package com.example.myauthapp.auth.controller;

import com.example.myauthapp.auth.cookie.enums.CookieType;
import com.example.myauthapp.auth.cookie.reader.CookieReader;
import com.example.myauthapp.auth.cookie.service.CookieService;
import com.example.myauthapp.auth.service.AccessTokenJwtService;
import com.example.myauthapp.auth.token.service.RefreshTokenService;
import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.auth.token.entity.RefreshToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    ){
        String stringRefreshToken =
            cookieReader.getTokenFromCookie(
                httpServletRequest,
                CookieType.REFRESH_TOKEN
            );
        RefreshToken refreshToken =
            refreshTokenService.validateRefreshToken(
                stringRefreshToken
            );
        User user = refreshToken.getUser();
        String stringNewRefreshToken =
            refreshTokenService.rotateRefreshToken(refreshToken);
        String stringNewAccessToken =
            accessTokenJwtService.generateAccessToken(user);

        cookieService.addTokenCookie(
            httpServletResponse,
            stringNewRefreshToken,
            CookieType.REFRESH_TOKEN
        );
        cookieService.addTokenCookie(
            httpServletResponse,
            stringNewAccessToken,
            CookieType.ACCESS_TOKEN
        );

        return ResponseEntity.ok().build();
    }
}