package com.example.myauthapp.audit.log.factory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.sql.Types;

import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.log.dto.AuditLogCreateDTO;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;

@Component
public class TestAuditLogPartitionDefaultInsertRecordFactory {

    public TestPartitionDefaultInsertRecord create(
        LocalDateTime occurredAt,
        LocalDateTime createdAt,
        AuditEventType auditEventType,
        AuditResult auditResult,
        Long actorId
    ) {
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(auditEventType, "auditEventType must not be null");
        Objects.requireNonNull(auditResult, "auditResult must not be null");

        AuditLogCreateDTO auditLogCreateDTO = new AuditLogCreateDTO(
            occurredAt,
            createdAt,
            auditEventType,
            auditResult,
            actorId
        );

        List<String> columnNames = List.of(
            "occurred_at",
            "created_at",
            "audit_event_type",
            "audit_result",
            "actor_id",
            "subject_id",
            "client_id",
            "ip_address",
            "user_agent",
            "request_id",
            "metadata"
        );

        PreparedStatementSetter parameterBinder =
            new PreparedStatementSetter() {

                @Override
                public void setValues(
                    PreparedStatement preparedStatement
                ) throws SQLException {

                    preparedStatement.setObject(
                        1, auditLogCreateDTO.getOccurredAt()
                    );
                    preparedStatement.setObject(
                        2, auditLogCreateDTO.getCreatedAt()
                    );
                    preparedStatement.setString(
                        3, auditLogCreateDTO.getAuditEventType().name()
                    );
                    preparedStatement.setString(
                        4, auditLogCreateDTO.getAuditResult().name()
                    );
                    preparedStatement.setObject(
                        5, auditLogCreateDTO.getActorId()
                    );

                    preparedStatement.setNull(6, Types.BIGINT);
                    preparedStatement.setNull(7, Types.VARCHAR);
                    preparedStatement.setNull(8, Types.VARCHAR);
                    preparedStatement.setNull(9, Types.VARCHAR);
                    preparedStatement.setNull(10, Types.VARCHAR);
                    preparedStatement.setNull(11, Types.OTHER);
                }
            };

        return new TestPartitionDefaultInsertRecord(
            columnNames,
            parameterBinder,
            occurredAt
        );
    }
}