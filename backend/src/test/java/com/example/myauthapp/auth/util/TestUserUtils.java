package com.example.myauthapp.auth.util;

import com.example.myauthapp.auth.util.TestDataUtils;
import com.example.myauthapp.user.config.TestUserPropertiesConfig;
import java.util.UUID;

public final class TestUserUtils {

    public static String getName(UUID uuid){
        return TestUserPropertiesConfig.NAME_PREFIX
            + TestDataUtils.getStringUniqueId(uuid);
    }

    public static String getEmail(UUID uuid){
        return TestUserPropertiesConfig.EMAIL_PREFIX 
            + TestDataUtils.getStringUniqueId(uuid)
            + "@"
            + TestUserPropertiesConfig.EMAIL_DOMAIN;
    }

    public static String getPassword(UUID uuid){
        return TestUserPropertiesConfig.PASSWORD_PREFIX 
            + TestDataUtils.getStringUniqueId(uuid);
    }
}

