package com.example.myauthapp.auth.service;

import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.auth.error.AuthError;
import com.example.myauthapp.auth.result.AuthResult;
import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.common.dto.ErrorResponse;
import com.example.myauthapp.auth.exception.AuthException;
import com.example.myauthapp.auth.token.service.RefreshTokenService;
import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import com.example.myauthapp.auth.token.service.RefreshTokenFamilyService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;
import java.util.UUID;

import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.auditlog.service.AuditLogService;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenJwtService accessTokenJwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenFamilyService refreshTokenFamilyService;
    private final AuditLogService auditLogService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AccessTokenJwtService accessTokenJwtService,
        RefreshTokenService refreshTokenService,
        RefreshTokenFamilyService refreshTokenFamilyService,
        AuditLogService auditLogService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenJwtService = accessTokenJwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenFamilyService = refreshTokenFamilyService;
        this.auditLogService = auditLogService;
    }

    private AuthResult createAuthResult(User user){
        String stringAccessToken = accessTokenJwtService.generateAccessToken(user);
        RefreshTokenFamily refreshTokenFamily =
            refreshTokenFamilyService.createRefreshTokenFamily(user);
        String stringRefreshToken = refreshTokenService.createRefreshToken(user,refreshTokenFamily);
        Long userId = user.getId();

        return new AuthResult(
            userId,
            stringAccessToken,
            stringRefreshToken
        );
    }

    @Transactional
    public AuthResult register(RegisterRequest req) {
        String name = req.getName();
        String email = req.getEmail();
        String password = req.getPassword();
        AuthError authError = null;
        User user = null;
        String hashedPassword = null;
        boolean existsByEmail = false;

        hashedPassword = passwordEncoder.encode(password);
        user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setEmailVerifiedAt(null);

        if (userRepository.existsByEmail(email)) {
            if(authError == null){
                authError = AuthError.EMAIL_ALREADY_EXISTS;
            }//authError

            auditLogService.record(
                AuditEventType.REGISTER,
                null,
                AuditResult.FAILURE
            );

        } else {

            try {
                user = userRepository.save(user);

                auditLogService.record(
                    AuditEventType.REGISTER,
                    user.getId(),
                    AuditResult.SUCCESS
                );

            } catch (DataIntegrityViolationException e) {
                if(authError == null){
                    authError = AuthError.DATA_INTEGRITY_VIOLATION;
                }//authError

                auditLogService.record(
                    AuditEventType.REGISTER,
                    null,
                    AuditResult.FAILURE
                );
            }
        }

        if(authError == null){
            return createAuthResult(user);
        }else{//authError
            throw new AuthException(authError);
        }//httpStatus

    }//register


    @Transactional
    public AuthResult login(LoginRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();
        AuthError authError = null;
        Optional<User> optionalUser = null;
        User user = null;

        if(email != null){
            optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();

                if (passwordEncoder.matches(password,user.getPassword())) {
                    auditLogService.record(
                        AuditEventType.LOGIN,
                        user.getId(),
                        AuditResult.SUCCESS
                    );
                }else{
                    if(authError == null){
                        authError = AuthError.INVALID_CREDENTIALS;
                    }//authError

                    auditLogService.record(
                        AuditEventType.LOGIN,
                        user.getId(),
                        AuditResult.FAILURE
                    );
                }
            } else {
                if(authError == null){
                    authError = AuthError.INVALID_CREDENTIALS;
                }//authError

                auditLogService.record(
                    AuditEventType.LOGIN,
                    null,
                    AuditResult.FAILURE
                );
            }
        }else{//email
            if(authError == null){
                authError = AuthError.AUTH_NO_EMAIL;
            }//authError

            auditLogService.record(
                AuditEventType.LOGIN,
                null,
                AuditResult.FAILURE
            );
        }//email

        if(authError == null){
            return createAuthResult(user);
        }else{//authError
            throw new AuthException(authError);
        }//authError
    }//login

}