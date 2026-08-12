package com.example.myauthapp.auth.result;

public class AuthResult {

    private final Long userId;
    private final String accessToken;
    private final String refreshToken;

    public AuthResult(
            Long userId,
            String accessToken,
            String refreshToken
    ) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}