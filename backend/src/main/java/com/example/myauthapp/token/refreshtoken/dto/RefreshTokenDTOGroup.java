package com.example.myauthapp.token.refreshtoken.dto;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;

public class RefreshTokenDTOGroup {

    private final String stringRefreshToken;
    private final RefreshTokenDTO refreshTokenDTO;

    public RefreshTokenDTOGroup(
        String stringRefreshToken,
        RefreshTokenDTO refreshTokenDTO
    ) {
        this.stringRefreshToken = stringRefreshToken;
        this.refreshTokenDTO = refreshTokenDTO;
    }

    public String getStringRefreshToken() {
        return stringRefreshToken;
    }

    public RefreshTokenDTO getRefreshTokenDTOByStringRefreshToken() {
        return refreshTokenDTO;
    }
}