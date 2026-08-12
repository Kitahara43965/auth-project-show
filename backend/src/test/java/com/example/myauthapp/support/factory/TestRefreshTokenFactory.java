package com.example.myauthapp.support.factory;

import com.example.myauthapp.auth.token.entity.RefreshToken;
import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import com.example.myauthapp.auth.token.repository.RefreshTokenFamilyRepository;
import com.example.myauthapp.auth.token.service.RefreshTokenService;
import com.example.myauthapp.user.entity.User;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import com.example.myauthapp.auth.token.repository.RefreshTokenRepository;

@Component
public class TestRefreshTokenFactory {

    private final RefreshTokenFamilyRepository refreshTokenFamilyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    public TestRefreshTokenFactory(
            RefreshTokenFamilyRepository refreshTokenFamilyRepository,
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenService refreshTokenService
    ) {
        this.refreshTokenFamilyRepository = refreshTokenFamilyRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenService = refreshTokenService;
    }

}