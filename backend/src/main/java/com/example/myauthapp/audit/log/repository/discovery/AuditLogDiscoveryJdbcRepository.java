package com.example.myauthapp.audit.log.repository.discovery;

import java.util.Optional;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.audit.log.dto.AuditLogDTO;
import com.example.myauthapp.audit.log.dto.AuditLogDTODiscoverySql;

import com.example.myauthapp.audit.log.error.AuditLogAuditError;
import com.example.myauthapp.web.audit.exception.AuditException;
import com.example.myauthapp.audit.log.config.AuditLogPartitionTableProperties;

@Repository
public class AuditLogDiscoveryJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final AuditLogPartitionTableProperties
        auditLogPartitionTableProperties;

    public AuditLogDiscoveryJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        AuditLogPartitionTableProperties auditLogPartitionTableProperties
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.auditLogPartitionTableProperties = auditLogPartitionTableProperties;
    }

    public Optional<AuditLogDTO> findOptionalAuditLogDTOByActorIdOrderByOccurredAtDesc(
        Long actorId
    ) {
        Objects.requireNonNull(
            actorId,
            "actorId must not be null"
        );

        String sql = """
            SELECT
                %s
            FROM %s
            WHERE actor_id = ?
            ORDER BY occurred_at DESC, id DESC
            LIMIT 1
            """.formatted(
                AuditLogDTODiscoverySql.COLUMN_NAMES,
                auditLogPartitionTableProperties.getParentTableName()
            );

        return auditJdbcTemplate.query(
            sql,
            AuditLogDTODiscoverySql.ROW_MAPPER,
            actorId
        ).stream().findFirst();
    }

    public AuditLogDTO findAuditLogDTOByOptionalAuditLogDTO(
        Optional<AuditLogDTO> optionalAuditLogDTO
    ){
        AuditLogDTO auditLogDTO = optionalAuditLogDTO.orElseThrow(
            () -> new AuditException(
                AuditLogAuditError.NO_AUDIT_LOGS_ARE_FOUND
            )
        );

        return auditLogDTO;
    }//findUserDTO

    public AuditLogDTO findAuditLogDTOByActorIdOrderByOccurredAtDesc(
        Long actorId
    ) {
        Optional<AuditLogDTO> optionalAuditLogDTO = findOptionalAuditLogDTOByActorIdOrderByOccurredAtDesc(
            actorId
        );
        return findAuditLogDTOByOptionalAuditLogDTO(optionalAuditLogDTO);
    }

    public long countAuditLogRowCount() {

        String sql = 
            "SELECT COUNT(*) FROM "
             + auditLogPartitionTableProperties.getParentTableName();

        return auditJdbcTemplate.queryForObject(
            sql, Long.class
        );
    }




}