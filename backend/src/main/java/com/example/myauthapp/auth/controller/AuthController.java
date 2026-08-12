package com.example.myauthapp.auth.controller;

import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.auth.service.AuthService;
import com.example.myauthapp.auth.cookie.service.CookieService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import com.example.myauthapp.auth.validation.group.ValidationSequence;
import com.example.myauthapp.auth.cookie.enums.CookieType;
import com.example.myauthapp.auth.result.AuthResult;
import com.example.myauthapp.auth.dto.AuthResponse;


@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    public AuthController(
        AuthService authService,
        CookieService cookieService
    ) {
        this.authService = authService;
        this.cookieService = cookieService;
    }

    private void addAuthCookies(
        HttpServletResponse httpServletResponse,
        AuthResult authResult
    ) {

        cookieService.addTokenCookie(
            httpServletResponse,
            authResult.getAccessToken(),
            CookieType.ACCESS_TOKEN
        );

        cookieService.addTokenCookie(
            httpServletResponse,
            authResult.getRefreshToken(),
            CookieType.REFRESH_TOKEN
        );
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Validated(ValidationSequence.class)
            @RequestBody RegisterRequest registerRequest,
            HttpServletResponse httpServletResponse
    ) {
        AuthResult authResult = authService.register(registerRequest);
        AuthResponse authResponse = null;

        addAuthCookies(httpServletResponse,authResult);
        authResponse = new AuthResponse(authResult.getUserId());
        return ResponseEntity.ok(authResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Validated(ValidationSequence.class)
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpServletResponse
    ) {

        AuthResult authResult = authService.login(loginRequest);
        AuthResponse authResponse = null;

        addAuthCookies(httpServletResponse,authResult);
        authResponse = new AuthResponse(authResult.getUserId());
        return ResponseEntity.ok(authResponse);
    }
}