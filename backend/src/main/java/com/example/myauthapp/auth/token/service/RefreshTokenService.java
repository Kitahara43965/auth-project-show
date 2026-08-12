package com.example.myauthapp.auth.token.service;

import com.example.myauthapp.auth.token.service.TokenHashService;
import com.example.myauthapp.auth.service.RefreshTokenJwtService;
import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.auth.token.error.RefreshTokenError;
import com.example.myauthapp.auth.token.exception.RefreshTokenException;
import com.example.myauthapp.auth.token.entity.RefreshToken;
import com.example.myauthapp.auth.token.repository.RefreshTokenRepository;
import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import com.example.myauthapp.auth.token.repository.RefreshTokenFamilyRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import com.example.myauthapp.auth.config.JwtProperties;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenJwtService refreshTokenJwtService;
    private final TokenHashService tokenHashService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenFamilyRepository refreshTokenFamilyRepository;
    private final RefreshTokenFamilyService refreshTokenFamilyService;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenJwtService refreshTokenJwtService,
            TokenHashService tokenHashService,
            JwtProperties jwtProperties,
            RefreshTokenFamilyRepository refreshTokenFamilyRepository,
            RefreshTokenFamilyService refreshTokenFamilyService
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenJwtService = refreshTokenJwtService;
        this.tokenHashService = tokenHashService;
        this.jwtProperties = jwtProperties;
        this.refreshTokenFamilyRepository = refreshTokenFamilyRepository;
        this.refreshTokenFamilyService = refreshTokenFamilyService;
    }

    @Transactional
    public String createRefreshToken(User user,RefreshTokenFamily refreshTokenFamily) {
        RefreshTokenError refreshTokenError = null;
        String stringRefreshToken = null;
        String stringTokenHash = null;

        if(user == null){
            if(refreshTokenError == null){
                refreshTokenError = RefreshTokenError.REFRESH_TOKEN_NO_USER;
            }//refreshTokenError
        }else{//user
            stringRefreshToken = refreshTokenJwtService.generateRefreshToken(user);
            stringTokenHash = tokenHashService.createTokenHash(stringRefreshToken);

            RefreshToken refreshToken =
                new RefreshToken(
                    refreshTokenFamily,
                    stringTokenHash,
                    LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()))
                );
            refreshTokenRepository.save(refreshToken);
        }//user

        if(refreshTokenError == null){
            return stringRefreshToken;
        }else{//refreshTokenError
            throw new RefreshTokenException(refreshTokenError);
        }//refreshTokenError
            
    }//createRefreshToken

    public RefreshToken getRefreshToken(String stringRefreshToken){
        RefreshTokenError refreshTokenError = null;
        String stringTokenHash = null;
        Optional<RefreshToken> optionalRefreshToken = null;
        RefreshToken refreshToken = null;

        if(stringRefreshToken == null){
            if(refreshTokenError == null){
                refreshTokenError = RefreshTokenError.STRING_REFRESH_TOKEN_DOES_NOT_EXIST;
            }//refreshTokenError
        }else{//stringRefreshToken
            stringTokenHash = tokenHashService.createTokenHash(stringRefreshToken);
            optionalRefreshToken = refreshTokenRepository.findByTokenHash(stringTokenHash);
            if(optionalRefreshToken.isPresent()){
                refreshToken = optionalRefreshToken.get();
            }else{
                refreshTokenError = RefreshTokenError.REFRESH_TOKEN_DOES_NOT_EXIST;
            }
        }//stringRefreshToken

        if(refreshTokenError == null){
            return refreshToken;
        }else{//refreshTokenError
            throw new RefreshTokenException(refreshTokenError);
        }//refreshTokenError
    }

    public RefreshToken validateRefreshToken(
            String stringRefreshToken
    ) {
        RefreshToken refreshToken = null;
        RefreshTokenError refreshTokenError = null;
        RefreshTokenFamily refreshTokenFamily = null;

        if(stringRefreshToken == null){
            if(refreshTokenError == null){
                refreshTokenError = RefreshTokenError.STRING_REFRESH_TOKEN_DOES_NOT_EXIST;
            }//refreshTokenError
        }else{//stringRefreshToken
            refreshToken = getRefreshToken(stringRefreshToken);
        }//stringRefreshToken

        if(refreshToken == null){
            if(refreshTokenError == null){
                refreshTokenError = RefreshTokenError.REFRESH_TOKEN_DOES_NOT_EXIST;
            }//refreshTokenError
        }else{//refreshToken
            refreshTokenFamily = refreshToken.getRefreshTokenFamily();

            if(refreshToken.isRevoked()) {
                refreshTokenFamilyService.revokeRefreshTokenFamily(refreshTokenFamily);
                if(refreshTokenError == null){
                    refreshTokenError = RefreshTokenError.REUSE_DETECTED;
                }//refreshTokenError
            }

            if(refreshToken.isExpired()) {
                if(refreshTokenError == null){
                    refreshTokenError = RefreshTokenError.REFRESH_TOKEN_IS_EXPIRED;
                }//refreshTokenError
            }
        }//refreshToken

        if(refreshTokenError == null){
            return refreshToken;
        }else{//refreshTokenError
            throw new RefreshTokenException(refreshTokenError);
        }//refreshTokenError
    }//validateRefreshToken

    @Transactional
    public void revokeRefreshToken(RefreshToken refreshToken){
        RefreshTokenError refreshTokenError = null;

        if(refreshToken == null){
            if(refreshTokenError == null){
                refreshTokenError = RefreshTokenError.REFRESH_TOKEN_DOES_NOT_EXIST;
            }//refreshTokenError
        }else{//refreshToken
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
        }//refreshToken

        if(refreshTokenError == null){
            return;
        }else{//refreshTokenError
            throw new RefreshTokenException(refreshTokenError);
        }//refreshTokenError
    }

    @Transactional
    public String rotateRefreshToken(RefreshToken refreshToken){
        User user = null;
        String stringNewRefreshToken = null;
        RefreshTokenFamily refreshTokenFamily = null;
        RefreshTokenError refreshTokenError = null;
        boolean canStringNewRefreshTokenCreated = false;

         if(refreshToken == null){
            if(refreshTokenError == null){
                refreshTokenError = RefreshTokenError.REFRESH_TOKEN_DOES_NOT_EXIST;
            }//refreshTokenError
        }else{//refreshToken
            refreshTokenFamily = refreshToken.getRefreshTokenFamily();

            if(refreshToken.isExpired()){
                if(refreshTokenError == null){
                    refreshTokenError = RefreshTokenError.REFRESH_TOKEN_IS_EXPIRED;
                };
            }

            if(refreshToken.isRevoked()){
                refreshTokenFamilyService.revokeRefreshTokenFamily(refreshTokenFamily);
                if(refreshTokenError == null){
                    refreshTokenError = RefreshTokenError.REUSE_DETECTED;
                }//refreshTokenError
            }else{//refreshToken.isRevoked()
                canStringNewRefreshTokenCreated = true;
            }//refreshToken.isRevoked()
        }//refreshToken

        if(canStringNewRefreshTokenCreated){
            revokeRefreshToken(refreshToken);
            user = refreshToken.getUser();
            stringNewRefreshToken = createRefreshToken(user,refreshTokenFamily);
        }//canStringNewRefreshTokenCreated

        if(refreshTokenError == null){
            return stringNewRefreshToken;
        }else{//refreshTokenError
            throw new RefreshTokenException(refreshTokenError);
        }//refreshTokenError
    }//rotateRefreshToken


}