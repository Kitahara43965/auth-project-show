package com.example.myauthapp.user.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserCreateDTO {
    private final String email;
    private final String password;
    private final String name;

    public UserCreateDTO(
        String email,
        String password,
        String name
    ) {
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
    
}