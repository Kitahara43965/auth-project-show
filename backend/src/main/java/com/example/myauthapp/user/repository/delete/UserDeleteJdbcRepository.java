package com.example.myauthapp.user.repository.delete;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDeleteJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public UserDeleteJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public void deleteAll() {
        userJdbcTemplate.update("DELETE FROM users");
    }
}