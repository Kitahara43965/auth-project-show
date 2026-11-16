package com.example.myauthapp.audit.log.repository.delete;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.myauthapp.audit.log.config.AuditLogPartitionTableProperties;

@Repository
public class AuditLogDeleteJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final AuditLogPartitionTableProperties
        auditLogPartitionTableProperties;

    public AuditLogDeleteJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        AuditLogPartitionTableProperties
            auditLogPartitionTableProperties
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.auditLogPartitionTableProperties =
            auditLogPartitionTableProperties;
    }

    public void deleteAll() {
        String parentTableName =
            auditLogPartitionTableProperties.getParentTableName();

        auditJdbcTemplate.update("DELETE FROM "+parentTableName);
    }
}