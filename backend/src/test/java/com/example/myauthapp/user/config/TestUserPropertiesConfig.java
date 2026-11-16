package com.example.myauthapp.user.config;

import com.example.myauthapp.user.config.TestUserProperties;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Component
@EnableConfigurationProperties(TestUserProperties.class)
public class TestUserPropertiesConfig {

    public static String NAME_PREFIX;
    public static String EMAIL_PREFIX;
    public static String EMAIL_DOMAIN;
    public static String PASSWORD_PREFIX;

    public TestUserPropertiesConfig(TestUserProperties testUserProperties) {
        NAME_PREFIX = testUserProperties.getNamePrefix();
        EMAIL_PREFIX = testUserProperties.getEmailPrefix();
        EMAIL_DOMAIN = testUserProperties.getEmailDomain();
        PASSWORD_PREFIX = testUserProperties.getPasswordPrefix();
    }
}