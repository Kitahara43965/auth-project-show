
package com.example.myauthapp.token.refreshtoken.repository.discovery;

import java.util.Optional;
import java.util.Objects;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenTokenError;
import com.example.myauthapp.token.refreshtoken.sql.RefreshTokenDTODiscoverySql;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;
import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;


@Repository
public class RefreshTokenUserDiscoveryJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;
    private final UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;

    public RefreshTokenUserDiscoveryJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate,
        UserDiscoveryJdbcRepository userDiscoveryJdbcRepository
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
        this.userDiscoveryJdbcRepository = userDiscoveryJdbcRepository;
    }
    
    public Optional<Long> findOptionalUserIdByTokenHash(
        String tokenHash
    ) {
        Objects.requireNonNull(
            tokenHash,
            "tokenHash must not be null"
        );

        String sql = """
            SELECT rtf.user_id
            FROM refresh_tokens rt
            JOIN refresh_token_families rtf
                ON rt.refresh_token_family_id = rtf.id
            WHERE rt.token_hash = ?
            """;

        List<Long> userIds = userJdbcTemplate.query(
            sql,
            (rs, rowNum) -> rs.getLong("user_id"),
            tokenHash
        );

        return userIds.stream().findFirst();
    }

    public Long findUserIdByTokenHash(
        String tokenHash
    ) {
        Optional<Long> optionalUserId =
            findOptionalUserIdByTokenHash(tokenHash);

        return userDiscoveryJdbcRepository.findUserIdByOptionalUserId(
            optionalUserId
        );
    }

    
}
