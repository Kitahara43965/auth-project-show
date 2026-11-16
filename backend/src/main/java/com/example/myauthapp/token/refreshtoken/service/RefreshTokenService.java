package com.example.myauthapp.token.refreshtoken.service;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.example.myauthapp.token.tokenhash.service.TokenHashService;
import com.example.myauthapp.token.jwttoken.service.RefreshTokenJwtService;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenTokenError;
import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenCreateDTO;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;
import com.example.myauthapp.token.refreshtoken.config.RefreshTokenProperties;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTOGroup;
import com.example.myauthapp.token.refreshtoken.dto.RotateRefreshTokenResult;
import com.example.myauthapp.token.refreshtoken.repository.discovery
    .RefreshTokenFamilyDiscoveryJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.insert
    .RefreshTokenInsertJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.discovery
    .RefreshTokenDiscoveryJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.revoke
    .RefreshTokenRevokeJdbcRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenJwtService refreshTokenJwtService;
    private final TokenHashService tokenHashService;
    private final RefreshTokenProperties refreshTokenProperties;
    private final RefreshTokenFamilyService refreshTokenFamilyService;
    private final RefreshTokenFamilyDiscoveryJdbcRepository
        refreshTokenFamilyDiscoveryJdbcRepository;
    private final RefreshTokenInsertJdbcRepository
        refreshTokenInsertJdbcRepository;
    private final RefreshTokenDiscoveryJdbcRepository
        refreshTokenDiscoveryJdbcRepository;
    private final RefreshTokenRevokeJdbcRepository
        refreshTokenRevokeJdbcRepository;

    public RefreshTokenService(
        RefreshTokenJwtService refreshTokenJwtService,
        TokenHashService tokenHashService,
        RefreshTokenProperties refreshTokenProperties,
        RefreshTokenFamilyService refreshTokenFamilyService,
        RefreshTokenFamilyDiscoveryJdbcRepository
            refreshTokenFamilyDiscoveryJdbcRepository,
        RefreshTokenInsertJdbcRepository
            refreshTokenInsertJdbcRepository,
        RefreshTokenDiscoveryJdbcRepository
            refreshTokenDiscoveryJdbcRepository,
        RefreshTokenRevokeJdbcRepository
            refreshTokenRevokeJdbcRepository
    ) {
        this.refreshTokenJwtService = refreshTokenJwtService;
        this.tokenHashService = tokenHashService;
        this.refreshTokenProperties = refreshTokenProperties;
        this.refreshTokenFamilyService = refreshTokenFamilyService;
        this.refreshTokenFamilyDiscoveryJdbcRepository =
            refreshTokenFamilyDiscoveryJdbcRepository;
        this.refreshTokenInsertJdbcRepository =
            refreshTokenInsertJdbcRepository;
        this.refreshTokenDiscoveryJdbcRepository =
            refreshTokenDiscoveryJdbcRepository;
        this.refreshTokenRevokeJdbcRepository =
            refreshTokenRevokeJdbcRepository;
    }

    @Transactional(value="userTransactionManager")
    public RefreshTokenDTOGroup createRefreshTokenByRefreshTokenFamilyDTO(
        RefreshTokenFamilyDTO refreshTokenFamilyDTO
    ) {
        Objects.requireNonNull(
            refreshTokenFamilyDTO, 
            "refreshTokenFamilyDTO is null"
        );

        Long userId = refreshTokenFamilyDTO.getUserId();

        Objects.requireNonNull(
            userId, 
            "userId is null"
        );

        String stringRefreshToken = refreshTokenJwtService.generateStringRefreshToken(userId);
        String tokenHash = tokenHashService.generateTokenHash(stringRefreshToken);

        RefreshTokenCreateDTO refreshTokenCreateDTO =
            new RefreshTokenCreateDTO(
                tokenHash,
                LocalDateTime.now().plus(
                    Duration.ofMillis(refreshTokenProperties.getExpiration())
                ),
                refreshTokenFamilyDTO.getId()
            );

        refreshTokenInsertJdbcRepository
            .insertRefreshTokenDTOByRefreshTokenCreateDTO(
                refreshTokenCreateDTO
            );

        RefreshTokenDTO refreshTokenDTO =
            refreshTokenDiscoveryJdbcRepository.findRefreshTokenDTOByTokenHash(
                tokenHash
            );

        return new RefreshTokenDTOGroup(
            stringRefreshToken,
            refreshTokenDTO
        );
            
    }//createRefreshTokenByRefreshTokenFamilyDTO

    public RefreshTokenDTO getRefreshTokenDTOByStringRefreshToken(String stringRefreshToken){
        Objects.requireNonNull(
            stringRefreshToken, 
            "stringRefreshToken is null"
        );

        String tokenHash = tokenHashService.generateTokenHash(stringRefreshToken);
        RefreshTokenDTO refreshTokenDTO =
            refreshTokenDiscoveryJdbcRepository.findRefreshTokenDTOByTokenHash(
                tokenHash
            );

        return refreshTokenDTO;
    }


    public RefreshTokenDTO validateRefreshToken(
        String stringRefreshToken
    ) {
        Objects.requireNonNull(
            stringRefreshToken, 
            "stringRefreshToken is null"
        );

        RefreshTokenDTO refreshTokenDTO = getRefreshTokenDTOByStringRefreshToken(stringRefreshToken);

        Objects.requireNonNull(
            refreshTokenDTO, 
            "refreshTokenDTO is null"
        );

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository
                .findRefreshTokenFamilyDTOByRefreshTokenId(
                    refreshTokenDTO.getId()
                );

        Objects.requireNonNull(
            refreshTokenFamilyDTO, 
            "refreshTokenFamilyDTO is null"
        );

        if(refreshTokenDTO.isRevoked()) {
            refreshTokenFamilyService.revokeRefreshTokenFamily(refreshTokenFamilyDTO.getId());
            
            throw new TokenException(
                RefreshTokenTokenError.REFRESH_TOKEN_REUSE_IS_DETECTED
            );
        }

        if(refreshTokenDTO.isExpired()) {
            throw new TokenException(
                RefreshTokenTokenError.REFRESH_TOKEN_IS_EXPIRED
            );
        }

        return refreshTokenDTO;

    }//validateRefreshToken

    @Transactional(value="userTransactionManager")
    public RotateRefreshTokenResult rotateRefreshToken(String stringRefreshToken){

        RefreshTokenDTO refreshTokenDTO = validateRefreshToken(stringRefreshToken);

        refreshTokenRevokeJdbcRepository.revokeRefreshTokenById(
            refreshTokenDTO.getId()
        );

        RefreshTokenFamilyDTO newRefreshTokenFamilyDTO =
            refreshTokenFamilyDiscoveryJdbcRepository
                .findRefreshTokenFamilyDTOByRefreshTokenId(
                    refreshTokenDTO.getId()
                );

        RefreshTokenDTOGroup newRefreshTokenDTOGroup = 
            createRefreshTokenByRefreshTokenFamilyDTO(
                newRefreshTokenFamilyDTO
            );

        String newStringRefreshToken = 
            newRefreshTokenDTOGroup.getStringRefreshToken();
        RefreshTokenDTO newRefreshTokenDTO = 
            newRefreshTokenDTOGroup.getRefreshTokenDTOByStringRefreshToken();

        RotateRefreshTokenResult newRotateRefreshTokenResult =
            new RotateRefreshTokenResult(
                newRefreshTokenFamilyDTO.getId(),
                newRefreshTokenFamilyDTO.getUserId(),
                newRefreshTokenDTO.getExpiresAt(),
                newRefreshTokenDTO.isRevoked(),
                newStringRefreshToken
            );

        return newRotateRefreshTokenResult;

    }//rotateRefreshToken


}