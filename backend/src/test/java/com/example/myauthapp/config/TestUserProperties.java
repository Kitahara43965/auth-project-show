package com.example.myauthapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "test.user")
public class TestUserProperties {

    private String namePrefix;
    private String emailPrefix;
    private String emailDomain;
    private String passwordPrefix;

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public String getEmailPrefix() {
        return emailPrefix;
    }

    public void setEmailPrefix(String emailPrefix) {
        this.emailPrefix = emailPrefix;
    }

    public String getEmailDomain() {
        return emailDomain;
    }

    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }
    

    public String getPasswordPrefix() {
        return passwordPrefix;
    }

    public void setPasswordPrefix(String passwordPrefix) {
        this.passwordPrefix = passwordPrefix;
    }
}