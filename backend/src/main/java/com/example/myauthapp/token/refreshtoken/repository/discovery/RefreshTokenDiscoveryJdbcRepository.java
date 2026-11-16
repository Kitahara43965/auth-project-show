
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

@Repository
public class RefreshTokenDiscoveryJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenDiscoveryJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public Optional<RefreshTokenDTO> findOptionalRefreshTokenDTOById(
        Long id
    ) {
        Objects.requireNonNull(
            id,
            "id must not be null"
        );

        String sql = """
            SELECT %s
            FROM refresh_tokens
            WHERE id = ?
            """.formatted(
                RefreshTokenDTODiscoverySql.COLUMN_NAMES
            );

        return userJdbcTemplate.query(
            sql,
            RefreshTokenDTODiscoverySql.ROW_MAPPER,
            id
        ).stream().findFirst();
    }

    public Optional<RefreshTokenDTO> findOptionalRefreshTokenDTOByTokenHash(
        String tokenHash
    ) {
        Objects.requireNonNull(
            tokenHash,
            "tokenHash must not be null"
        );

        String sql = """
            SELECT %s
            FROM refresh_tokens
            WHERE token_hash = ?
            """.formatted(RefreshTokenDTODiscoverySql.COLUMN_NAMES);

        return userJdbcTemplate.query(
            sql,
            RefreshTokenDTODiscoverySql.ROW_MAPPER,
            tokenHash
        ).stream().findFirst();
    }

    public Optional<RefreshTokenDTO> findOptionalTopRefreshTokenDTOByUserIdOrderByCreatedAtDesc(
        Long userId
    ) {
        Objects.requireNonNull(userId, "userId must not be null");

        String sql = """
            SELECT
                rt.id,
                rt.token_hash,
                rt.expires_at,
                rt.refresh_token_family_id,
                rt.revoked_at,
                rt.created_at
            FROM refresh_tokens rt
            INNER JOIN refresh_token_families rtf
                ON rt.refresh_token_family_id = rtf.id
            WHERE rtf.user_id = ?
            ORDER BY rt.created_at DESC, rt.id DESC
            LIMIT 1
            """;

        return userJdbcTemplate.query(
            sql,
            RefreshTokenDTODiscoverySql.ROW_MAPPER,
            userId
        ).stream().findFirst();
    }

    public RefreshTokenDTO findRefreshTokenDTOByOptionalRefreshTokenDTO(
        Optional<RefreshTokenDTO> optionalRefreshTokenDTO
    ) {
        Objects.requireNonNull(
            optionalRefreshTokenDTO,
            "optionalRefreshTokenDTO must not be null"
        );

        return optionalRefreshTokenDTO.orElseThrow(
            () -> new TokenException(
                RefreshTokenTokenError.NO_REFRESH_TOKENS_ARE_FOUND
            )
        );
    }

    public RefreshTokenDTO findRefreshTokenDTOById(
        Long id
    ) {
        Optional<RefreshTokenDTO> optionalRefreshTokenDTO =
            findOptionalRefreshTokenDTOById(id);

        return findRefreshTokenDTOByOptionalRefreshTokenDTO(
            optionalRefreshTokenDTO
        );
    }

    public RefreshTokenDTO findRefreshTokenDTOByTokenHash(
        String tokenHash
    ) {
        Optional<RefreshTokenDTO> optionalRefreshTokenDTO =
            findOptionalRefreshTokenDTOByTokenHash(tokenHash);

        return findRefreshTokenDTOByOptionalRefreshTokenDTO(
            optionalRefreshTokenDTO
        );
    }

    public RefreshTokenDTO findTopRefreshTokenDTOByUserIdOrderByCreatedAtDesc(
        Long userId
    ) {
        Optional<RefreshTokenDTO> optionalRefreshTokenDTO =
            findOptionalTopRefreshTokenDTOByUserIdOrderByCreatedAtDesc(userId);

        return findRefreshTokenDTOByOptionalRefreshTokenDTO(
            optionalRefreshTokenDTO
        );
    }


}
