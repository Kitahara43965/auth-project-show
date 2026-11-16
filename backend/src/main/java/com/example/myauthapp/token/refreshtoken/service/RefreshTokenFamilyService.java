
package com.example.myauthapp.token.refreshtoken.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyCreateDTO;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;
import com.example.myauthapp.token.refreshtoken.repository.insert.RefreshTokenFamilyInsertJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.discovery.RefreshTokenFamilyDiscoveryJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.revoke.RefreshTokenFamilyRevokeJdbcRepository;

@Service
public class RefreshTokenFamilyService {

    private final RefreshTokenFamilyInsertJdbcRepository
        refreshTokenFamilyInsertJdbcRepository;

    private final RefreshTokenFamilyDiscoveryJdbcRepository
        refreshTokenFamilyDiscoveryJdbcRepository;

    private final RefreshTokenFamilyRevokeJdbcRepository
        refreshTokenFamilyRevokeJdbcRepository;

    public RefreshTokenFamilyService(
        RefreshTokenFamilyInsertJdbcRepository refreshTokenFamilyInsertJdbcRepository,
        RefreshTokenFamilyDiscoveryJdbcRepository refreshTokenFamilyDiscoveryJdbcRepository,
        RefreshTokenFamilyRevokeJdbcRepository refreshTokenFamilyRevokeJdbcRepository
    ) {
        this.refreshTokenFamilyInsertJdbcRepository =
            refreshTokenFamilyInsertJdbcRepository;
        this.refreshTokenFamilyDiscoveryJdbcRepository =
            refreshTokenFamilyDiscoveryJdbcRepository;
        this.refreshTokenFamilyRevokeJdbcRepository =
            refreshTokenFamilyRevokeJdbcRepository;
    }

    @Transactional(value = "userTransactionManager")
    public RefreshTokenFamilyDTO createRefreshTokenFamilyByUserId(Long userId) {

        Objects.requireNonNull(userId, "userId is null");

        RefreshTokenFamilyCreateDTO refreshTokenFamilyCreateDTO =
            new RefreshTokenFamilyCreateDTO(
                UUID.randomUUID(),
                userId
            );

        refreshTokenFamilyInsertJdbcRepository
            .insertRefreshTokenFamilyByRefreshTokenFamilyCreateDTO(
                refreshTokenFamilyCreateDTO
            );

        return refreshTokenFamilyDiscoveryJdbcRepository
            .findRefreshTokenFamilyDTOById(
                refreshTokenFamilyCreateDTO.getId()
            );
    }

    @Transactional(
        value = "userTransactionManager",
        propagation = Propagation.REQUIRES_NEW
    )
    public void revokeRefreshTokenFamily(UUID refreshTokenFamilyId) {

        Objects.requireNonNull(
            refreshTokenFamilyId,
            "refreshTokenFamilyId is null"
        );

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository
                .findRefreshTokenFamilyDTOById(
                    refreshTokenFamilyId
                );

        refreshTokenFamilyRevokeJdbcRepository
            .revokeRefreshTokenFamilyById(
                refreshTokenFamilyDTO.getId()
            );
    }
}
