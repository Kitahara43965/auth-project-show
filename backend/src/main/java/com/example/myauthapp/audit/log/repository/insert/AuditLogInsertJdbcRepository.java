package com.example.myauthapp.audit.log.repository.insert;

import java.util.Objects;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.audit.log.dto.AuditLogCreateDTO;
import com.example.myauthapp.audit.log.dto.AuditLogDTO;
import com.example.myauthapp.audit.log.config.AuditLogPartitionTableProperties;

@Repository
public class AuditLogInsertJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final AuditLogPartitionTableProperties
        auditLogPartitionTableProperties;

    public AuditLogInsertJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        AuditLogPartitionTableProperties auditLogPartitionTableProperties
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.auditLogPartitionTableProperties =
            auditLogPartitionTableProperties;
    }

    
    public AuditLogDTO insertAuditLogByAuditLogCreateDTO(
        AuditLogCreateDTO auditLogCreateDTO
    ) {
        Objects.requireNonNull(
            auditLogCreateDTO,
            "auditLogCreateDTO must not be null"
        );

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime occurredAt =
            auditLogCreateDTO.getOccurredAt() != null
                ? auditLogCreateDTO.getOccurredAt()
                : now;

        LocalDateTime createdAt =
            auditLogCreateDTO.getCreatedAt() != null
                ? auditLogCreateDTO.getCreatedAt()
                : occurredAt;

        String sql = """
            INSERT INTO %s (
                occurred_at,
                created_at,
                audit_event_type,
                audit_result,
                actor_id
            )
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """.formatted(
                auditLogPartitionTableProperties.getParentTableName()
            );

        Long id = auditJdbcTemplate.queryForObject(
            sql,
            Long.class,
            occurredAt,
            createdAt,
            auditLogCreateDTO.getAuditEventType().name(),
            auditLogCreateDTO.getAuditResult().name(),
            auditLogCreateDTO.getActorId()
        );

        return new AuditLogDTO(
            id,
            occurredAt,
            createdAt,
            auditLogCreateDTO.getAuditEventType(),
            auditLogCreateDTO.getAuditResult(),
            auditLogCreateDTO.getActorId(),
            null,
            null,
            null,
            null,
            null,
            null
        );
    }



    
}