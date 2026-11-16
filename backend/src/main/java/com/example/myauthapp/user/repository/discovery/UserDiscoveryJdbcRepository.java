package com.example.myauthapp.user.repository.discovery;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.user.dto.UserDTO;
import com.example.myauthapp.user.sql.UserDTODiscoverySql;
import com.example.myauthapp.user.error.UserDTOUserError;
import com.example.myauthapp.web.user.exception.UserException;

@Repository
public class UserDiscoveryJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public UserDiscoveryJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public boolean doesUserExistByEmail(String email) {

        Objects.requireNonNull(
            email,
            "email must not be null"
        );

        String sql = """
            SELECT EXISTS (
                SELECT 1
                FROM users
                WHERE email = ?
            )
            """;

        Boolean exists = userJdbcTemplate.queryForObject(
            sql,
            Boolean.class,
            email
        );

        return Boolean.TRUE.equals(exists);

    }

    private Optional<UserDTO> findOptionalUserDTO(
        String columnName,
        Object value
    ) {

        String sql = """
            SELECT
                %s
            FROM users
            WHERE %s = ?
            LIMIT 1
            """.formatted(
                UserDTODiscoverySql.COLUMN_NAMES,
                columnName
            );

        return userJdbcTemplate.query(
            sql,
            UserDTODiscoverySql.ROW_MAPPER,
            value
        ).stream().findFirst();
    }

    public UserDTO findUserDTOByOptionalUserDTO(
        Optional<UserDTO> optionalUserDTO
    ){
        UserDTO userDTO = optionalUserDTO.orElseThrow(
            () -> new UserException(
                UserDTOUserError.NO_USERS_ARE_FOUND
            )
        );

        return userDTO;
    }//findUserDTO

    public Optional<UserDTO> findOptionalUserDTOByEmail(
        String email
    ) {

        Objects.requireNonNull(
            email,
            "email must not be null"
        );

        return findOptionalUserDTO(
            "email",
            email
        );
    }

    public UserDTO findUserDTOByEmail(
        String email
    ) {

        Objects.requireNonNull(
            email,
            "email must not be null"
        );

        Optional<UserDTO> optionalUserDTO =
            findOptionalUserDTOByEmail(email);

        return findUserDTOByOptionalUserDTO(
            optionalUserDTO
        );
    }


    public Optional<UserDTO> findOptionalUserDTOById(
        Long id
    ) {

        Objects.requireNonNull(
            id,
            "id must not be null"
        );

        return findOptionalUserDTO(
            "id",
            id
        );
    }


    public UserDTO findUserDTOById(
        Long id
    ) {

        Objects.requireNonNull(
            id,
            "id must not be null"
        );

        Optional<UserDTO> optionalUserDTO =
            findOptionalUserDTOById(id);

        return findUserDTOByOptionalUserDTO(
            optionalUserDTO
        );
    }

    public Long findUserIdByOptionalUserId(
        Optional<Long> optionalUserId
    ) {
        Objects.requireNonNull(
            optionalUserId,
            "optionalUserId must not be null"
        );

        return optionalUserId.orElseThrow(
            () -> new UserException(
                UserDTOUserError.NO_USER_IDS_ARE_FOUND
            )
        );
    }
    
}