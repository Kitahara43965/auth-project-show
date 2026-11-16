package com.example.myauthapp.token.cookie.enums;

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