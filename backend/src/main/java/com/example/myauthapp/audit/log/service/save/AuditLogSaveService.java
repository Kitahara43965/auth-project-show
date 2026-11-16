package com.example.myauthapp.audit.log.service.save;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.stereotype.Service;

import com.example.myauthapp.audit.log.repository.insert.AuditLogInsertJdbcRepository;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.log.dto.AuditLogCreateDTO;

@Service
public class AuditLogSaveService {

    private final AuditLogInsertJdbcRepository auditLogInsertJdbcRepository;

    public AuditLogSaveService(
        AuditLogInsertJdbcRepository auditLogInsertJdbcRepository
    ) {
        this.auditLogInsertJdbcRepository = auditLogInsertJdbcRepository;
    }

    @Transactional(
        value="auditTransactionManager",
        propagation = Propagation.REQUIRES_NEW
    )
    public void record(
        AuditEventType auditEventType,
        AuditResult auditResult,
        Long actorId
    ) {
        
        AuditLogCreateDTO auditLogCreateDTO =
            new AuditLogCreateDTO(
                auditEventType,
                auditResult,
                actorId
            );

        auditLogInsertJdbcRepository.insertAuditLogByAuditLogCreateDTO(
            auditLogCreateDTO
        );

    }
}