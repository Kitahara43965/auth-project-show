package com.example.myauthapp.support.util;

import com.example.myauthapp.support.util.TestDataUtils;
import com.example.myauthapp.config.TestUserConfig;
import java.util.UUID;

public final class TestUserUtils {

    public static String getName(UUID uuid){
        return TestUserConfig.NAME_PREFIX
            + TestDataUtils.getStringUniqueId(uuid);
    }

    public static String getEmail(UUID uuid){
        return TestUserConfig.EMAIL_PREFIX 
            + TestDataUtils.getStringUniqueId(uuid)
            + "@"
            + TestUserConfig.EMAIL_DOMAIN;
    }

    public static String getPassword(UUID uuid){
        return TestUserConfig.PASSWORD_PREFIX 
            + TestDataUtils.getStringUniqueId(uuid);
    }
}

