package com.example.myauthapp.auth.dto;

public class AuthResponse {

    private Long userId;

    public AuthResponse(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}