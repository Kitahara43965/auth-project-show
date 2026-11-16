package com.example.myauthapp.token.cookie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token.cookie")
public class CookieProperties {

    private String sameSite;
    private boolean secure;
    private long accessTokenMaxAge;
    private long refreshTokenMaxAge;


    public String getSameSite() {
        return sameSite;
    }

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }


    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }


    public long getAccessTokenMaxAge() {
        return accessTokenMaxAge;
    }

    public void setAccessTokenMaxAge(long accessTokenMaxAge) {
        this.accessTokenMaxAge = accessTokenMaxAge;
    }


    public long getRefreshTokenMaxAge() {
        return refreshTokenMaxAge;
    }

    public void setRefreshTokenMaxAge(long refreshTokenMaxAge) {
        this.refreshTokenMaxAge = refreshTokenMaxAge;
    }
}