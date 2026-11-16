package com.example.myauthapp.auth.util;

import java.util.UUID;

public final class TestDataUtils {

    private TestDataUtils() {
    }

    public static String getStringUniqueId(UUID uuid) {
        return System.currentTimeMillis() + "-" + uuid;
    }

}