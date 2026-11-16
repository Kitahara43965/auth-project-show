
package com.example.myauthapp.token.refreshtoken.repository.discovery;

import java.util.Optional;
import java.util.Objects;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenFamilyTokenError;
import com.example.myauthapp.token.refreshtoken.sql.RefreshTokenFamilyDTODiscoverySql;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;

@Repository
public class RefreshTokenFamilyDiscoveryJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenFamilyDiscoveryJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public Optional<RefreshTokenFamilyDTO> findOptionalRefreshTokenFamilyDTOByRefreshTokenId(
        Long refreshTokenId
    ) {
        Objects.requireNonNull(
            refreshTokenId,
            "refreshTokenId must not be null"
        );

        String sql = """
            SELECT
                %s
            FROM refresh_tokens rt
            INNER JOIN refresh_token_families rtf
                ON rt.refresh_token_family_id = rtf.id
            WHERE rt.id = ?
            """.formatted(
                RefreshTokenFamilyDTODiscoverySql.COLUMN_NAMES
                    .lines()
                    .map(String::trim)
                    .filter(column -> !column.isEmpty())
                    .map(column -> "rtf." + column.replaceAll(",$", ""))
                    .collect(java.util.stream.Collectors.joining(",\n"))
            );

        return userJdbcTemplate.query(
            sql,
            RefreshTokenFamilyDTODiscoverySql.ROW_MAPPER,
            refreshTokenId
        ).stream().findFirst();
    }

    public Optional<RefreshTokenFamilyDTO> findOptionalRefreshTokenFamilyDTOById(
        UUID refreshTokenFamilyId
    ) {
        Objects.requireNonNull(
            refreshTokenFamilyId,
            "refreshTokenFamilyId must not be null"
        );

        String sql =
            """
            SELECT
                %s
            FROM refresh_token_families
            WHERE id = ?
            """.formatted(RefreshTokenFamilyDTODiscoverySql.COLUMN_NAMES);

        return userJdbcTemplate.query(
            sql,
            RefreshTokenFamilyDTODiscoverySql.ROW_MAPPER,
            refreshTokenFamilyId
        ).stream().findFirst();
    }

    public RefreshTokenFamilyDTO findRefreshTokenFamilyDTOByOptionalRefreshTokenFamilyDTO(
        Optional<RefreshTokenFamilyDTO> optionalRefreshTokenFamilyDTO
    ){
        Objects.requireNonNull(
            optionalRefreshTokenFamilyDTO,
            "optionalRefreshTokenFamilyDTO must not be null"
        );
        RefreshTokenFamilyDTO refreshTokenFamilyDTO = optionalRefreshTokenFamilyDTO.orElseThrow(
            () -> new TokenException(
                RefreshTokenFamilyTokenError.NO_REFRESH_TOKEN_FAMILIES_ARE_FOUND
            )
        );

        return refreshTokenFamilyDTO;
    }//findRefreshTokenFamilyDTO


    public RefreshTokenFamilyDTO findRefreshTokenFamilyDTOByRefreshTokenId(
        Long refreshTokenId
    ) {
        Objects.requireNonNull(
            refreshTokenId,
            "refreshTokenId must not be null"
        );
        Optional<RefreshTokenFamilyDTO> optionalRefreshTokenFamilyDTO =
            findOptionalRefreshTokenFamilyDTOByRefreshTokenId(refreshTokenId);
        return findRefreshTokenFamilyDTOByOptionalRefreshTokenFamilyDTO(
            optionalRefreshTokenFamilyDTO
        );
    }

    public RefreshTokenFamilyDTO findRefreshTokenFamilyDTOById(
        UUID refreshTokenFamilyId
    ) {
        Objects.requireNonNull(
            refreshTokenFamilyId,
            "refreshTokenFamilyId must not be null"
        );
        Optional<RefreshTokenFamilyDTO> optionalRefreshTokenFamilyDTO =
            findOptionalRefreshTokenFamilyDTOById(refreshTokenFamilyId);
        return findRefreshTokenFamilyDTOByOptionalRefreshTokenFamilyDTO(
            optionalRefreshTokenFamilyDTO
        );
    }

}
