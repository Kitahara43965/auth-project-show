package com.example.myauthapp.support.database.cleaner;

import com.example.myauthapp.audit.auditlog.repository.AuditLogRepository;
import com.example.myauthapp.auth.token.repository.RefreshTokenFamilyRepository;
import com.example.myauthapp.auth.token.repository.RefreshTokenRepository;
import com.example.myauthapp.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestDatabaseCleaner {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenFamilyRepository refreshTokenFamilyRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public TestDatabaseCleaner(
        RefreshTokenRepository refreshTokenRepository,
        RefreshTokenFamilyRepository refreshTokenFamilyRepository,
        AuditLogRepository auditLogRepository,
        UserRepository userRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenFamilyRepository = refreshTokenFamilyRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void deleteAllDatabase() {
        refreshTokenRepository.deleteAll();
        refreshTokenFamilyRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
    }
}