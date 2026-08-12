package com.example.myauthapp.audit.auditlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.myauthapp.audit.auditlog.entity.AuditLog;
import java.util.Optional;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

        Optional<AuditLog> findTopByActorIdOrderByOccurredAtDesc(Long actorId);
}