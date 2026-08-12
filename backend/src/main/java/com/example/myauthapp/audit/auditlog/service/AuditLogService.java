package com.example.myauthapp.audit.auditlog.service;
import com.example.myauthapp.audit.auditlog.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.auditlog.entity.AuditLog;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(
        AuditLogRepository auditLogRepository
    ) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(
            AuditEventType auditEventType,
            Long actorId,
            AuditResult auditResult
    ) {
        AuditLog auditLog =
            new AuditLog(
                    auditEventType,
                    actorId,
                    auditResult
            );

        auditLogRepository.save(auditLog);
    }
}