package com.example.myauthapp.user.dto;

import java.util.Objects;
import java.time.LocalDateTime;

public class UserDTO {

    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final LocalDateTime emailVerifiedAt;
    private final LocalDateTime createdAt;

    public UserDTO(
        Long id,
        String email,
        String password,
        String name,
        LocalDateTime emailVerifiedAt,
        LocalDateTime createdAt
    ) {
        this.id = 
            Objects.requireNonNull(
                id,
                "id must not be null"
            );
        this.email = 
            Objects.requireNonNull(
                email,
                "email must not be null"
            );
        this.password = 
            Objects.requireNonNull(
                password,
                "password must not be null"
            );
        this.name = 
            Objects.requireNonNull(
                name,
                "name must not be null"
            );
        this.emailVerifiedAt = emailVerifiedAt;
        this.createdAt = 
            Objects.requireNonNull(
                createdAt,
                "createdAt must not be null"
            );
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
}