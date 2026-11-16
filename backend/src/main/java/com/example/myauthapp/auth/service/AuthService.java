package com.example.myauthapp.auth.service;

import java.util.Optional;
import java.util.UUID;
import java.util.Objects;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.token.jwttoken.service.AccessTokenJwtService;
import com.example.myauthapp.auth.error.FieldAuthError;
import com.example.myauthapp.auth.result.AuthResult;
import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.user.dto.UserDTO;
import com.example.myauthapp.web.auth.exception.AuthException;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenService;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;
import com.example.myauthapp.token.refreshtoken.service.RefreshTokenFamilyService;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.log.service.save.AuditLogSaveService;
import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;
import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;
import com.example.myauthapp.user.repository.insert.UserInsertJdbcRepository;
import com.example.myauthapp.user.dto.UserCreateDTO;
import com.example.myauthapp.token.refreshtoken.repository.discovery
    .RefreshTokenFamilyDiscoveryJdbcRepository;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AccessTokenJwtService accessTokenJwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenFamilyService refreshTokenFamilyService;
    private final AuditLogSaveService auditLogSaveService;
    private final UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;
    private final UserInsertJdbcRepository userInsertJdbcRepository;

    public AuthService(
        PasswordEncoder passwordEncoder,
        AccessTokenJwtService accessTokenJwtService,
        RefreshTokenService refreshTokenService,
        RefreshTokenFamilyService refreshTokenFamilyService,
        AuditLogSaveService auditLogSaveService,
        UserDiscoveryJdbcRepository userDiscoveryJdbcRepository,
        UserInsertJdbcRepository userInsertJdbcRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.accessTokenJwtService = accessTokenJwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenFamilyService = refreshTokenFamilyService;
        this.auditLogSaveService = auditLogSaveService;
        this.userDiscoveryJdbcRepository = userDiscoveryJdbcRepository;
        this.userInsertJdbcRepository = userInsertJdbcRepository;
    }

    private AuthResult createAuthResult(Long userId){
        Objects.requireNonNull(
            userId,
            "userId must not be null"
        );

        RefreshTokenFamilyDTO refreshTokenFamilyDTO =
            refreshTokenFamilyService.createRefreshTokenFamilyByUserId(userId);
        String stringAccessToken = accessTokenJwtService.generateStringAccessToken(userId);
        String stringRefreshToken
         = refreshTokenService
            .createRefreshTokenByRefreshTokenFamilyDTO(refreshTokenFamilyDTO)
            .getStringRefreshToken();

        return new AuthResult(
            userId,
            stringAccessToken,
            stringRefreshToken
        );
    }

    @Transactional(value="userTransactionManager")
    public AuthResult register(RegisterRequest req) {
        String name = req.getName();
        String email = req.getEmail();
        String password = req.getPassword();

        FieldAuthError fieldAuthError = null;
        String hashedPassword = null;
        boolean existsByEmail = false;
        hashedPassword = passwordEncoder.encode(password);

        UserCreateDTO userCreateDTO = new UserCreateDTO(
            email,
            hashedPassword,
            name
        );

        boolean doesUserExistByEmail =
            userDiscoveryJdbcRepository.doesUserExistByEmail(email);

        if (doesUserExistByEmail) {

            auditLogSaveService.record(
                AuditEventType.REGISTER,
                AuditResult.FAILURE,
                null
            );

            throw new AuthException(
                FieldAuthError.EMAIL_ALREADY_EXISTS
            );

        }//doesUserExistByEmail

        UserDTO userDTO = null;
        Long userId = null;

        try {
            userDTO = userInsertJdbcRepository.insertUserByUserCreateDTO(userCreateDTO);
            userId = userDTO.getId();

            auditLogSaveService.record(
                AuditEventType.REGISTER,
                AuditResult.SUCCESS,
                userId
            );

        } catch (DataIntegrityViolationException e) {

            auditLogSaveService.record(
                AuditEventType.REGISTER,
                AuditResult.FAILURE,
                null
            );

            throw new AuthException(
                FieldAuthError.DATA_INTEGRITY_VIOLATION
            );
        }

        return createAuthResult(userId);

    }//register


    @Transactional(value="userTransactionManager")
    public AuthResult login(LoginRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        if(email == null){
            auditLogSaveService.record(
                AuditEventType.LOGIN,
                AuditResult.FAILURE,
                null
            );

            throw new AuthException(
                FieldAuthError.AUTH_NO_EMAIL
            );
        }//email

        Optional<UserDTO> optionalUserDTO = userDiscoveryJdbcRepository
            .findOptionalUserDTOByEmail(email);

        if (!optionalUserDTO.isPresent()) {
            auditLogSaveService.record(
                AuditEventType.LOGIN,
                AuditResult.FAILURE,
                null
            );

            throw new AuthException(
                FieldAuthError.INVALID_CREDENTIALS
            );
        }//optionalUserDTO.isPresent()

        UserDTO userDTO = userDiscoveryJdbcRepository
            .findUserDTOByOptionalUserDTO(optionalUserDTO);

        if (!passwordEncoder.matches(password,userDTO.getPassword())) {
            auditLogSaveService.record(
                AuditEventType.LOGIN,
                AuditResult.FAILURE,
                userDTO.getId()
            );

            throw new AuthException(FieldAuthError.INVALID_CREDENTIALS);
        }

        auditLogSaveService.record(
            AuditEventType.LOGIN,
            AuditResult.SUCCESS,
            userDTO.getId()
        );
            
        return createAuthResult(userDTO.getId());

    }//login

}