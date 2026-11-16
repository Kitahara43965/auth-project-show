package com.example.myauthapp.token.refreshtoken.repository.delete;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyCreateDTO;
import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenFamilyTokenError;
import org.springframework.beans.factory.annotation.Qualifier;

@Repository
public class RefreshTokenFamilyDeleteJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenFamilyDeleteJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public void deleteAll() {
        userJdbcTemplate.update("DELETE FROM refresh_token_families");
    }
}