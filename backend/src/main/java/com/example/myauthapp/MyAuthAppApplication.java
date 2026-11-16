package com.example.myauthapp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.example.myauthapp.token.accesstoken.config.AccessTokenProperties;
import com.example.myauthapp.token.refreshtoken.config.RefreshTokenProperties;
import com.example.myauthapp.token.refreshtoken.config.RefreshTokenCleanupProperties;
import com.example.myauthapp.audit.log.config.AuditLogPartitionTableProperties;
import com.example.myauthapp.audit.log.config.AuditLogPartitionOperationProperties;
import com.example.myauthapp.audit.log.config.AuditLogPartitionCommandProperties;
import com.example.myauthapp.audit.log.config.AuditLogPartitionMaintenanceProperties;
import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.token.cookie.config.CookieProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({
    AccessTokenProperties.class,
    RefreshTokenProperties.class,
    RefreshTokenCleanupProperties.class,
    AuditLogPartitionTableProperties.class,
    AuditLogPartitionOperationProperties.class,
    AuditLogPartitionCommandProperties.class,
    AuditLogPartitionMaintenanceProperties.class,
    PartitionProperties.class,
    CookieProperties.class
})

public class MyAuthAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAuthAppApplication.class, args);
    }
}