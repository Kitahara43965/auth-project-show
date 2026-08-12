package com.example.myauthapp.auth.cookie.enums;
import java.time.Duration;

public enum CookieType {

    ACCESS_TOKEN(
        "ACCESS_TOKEN"
    ),

    REFRESH_TOKEN(
        "REFRESH_TOKEN"
    );

    private final String name;

    CookieType(
        String name
    ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}