package com.example.myauthapp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.example.myauthapp.auth.config.JwtProperties;
import com.example.myauthapp.auth.config.RefreshTokenCleanupProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({
    JwtProperties.class,
    RefreshTokenCleanupProperties.class
})

public class MyAuthAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAuthAppApplication.class, args);
    }
}