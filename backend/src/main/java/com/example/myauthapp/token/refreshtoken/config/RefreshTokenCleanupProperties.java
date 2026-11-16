package com.example.myauthapp.token.refreshtoken.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token.refresh-token.cleanup")
public class RefreshTokenCleanupProperties {

    private Duration retentionPeriod;

    public Duration getRetentionPeriod() {
        return retentionPeriod;
    }

    public void setRetentionPeriod(Duration retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }
}