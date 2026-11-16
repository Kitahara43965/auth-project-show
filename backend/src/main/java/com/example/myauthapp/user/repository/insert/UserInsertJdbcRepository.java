package com.example.myauthapp.user.repository.insert;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.user.dto.UserCreateDTO;
import com.example.myauthapp.user.dto.UserDTO;


@Repository
public class UserInsertJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public UserInsertJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public UserDTO insertUserByUserCreateDTO(UserCreateDTO userCreateDTO) {

        Objects.requireNonNull(
            userCreateDTO,
            "userCreateDTO must not be null"
        );

        String sql = """
            INSERT INTO users (
                email,
                password,
                name
            )
            VALUES (?, ?, ?)
            RETURNING id, created_at
        """;

        return userJdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new UserDTO(
                rs.getLong("id"),
                userCreateDTO.getEmail(),
                userCreateDTO.getPassword(),
                userCreateDTO.getName(),
                null,
                rs.getTimestamp("created_at").toLocalDateTime()
            ),
            userCreateDTO.getEmail(),
            userCreateDTO.getPassword(),
            userCreateDTO.getName()
        );
    }

    
}