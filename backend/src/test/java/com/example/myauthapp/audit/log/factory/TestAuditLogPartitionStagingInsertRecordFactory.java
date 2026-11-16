package com.example.myauthapp.audit.log.factory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.log.dto.AuditLogCreateDTO;
import com.example.myauthapp.audit.log.mapper.AuditLogRecordDataMapper;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.partition.dto.TestPartitionStagingInsertRecord;

import tools.jackson.databind.node.ObjectNode;

@Component
public class TestAuditLogPartitionStagingInsertRecordFactory {

    private final AuditLogRecordDataMapper auditLogRecordDataMapper;

    public TestAuditLogPartitionStagingInsertRecordFactory(
        AuditLogRecordDataMapper auditLogRecordDataMapper
    ) {
        this.auditLogRecordDataMapper = auditLogRecordDataMapper;
    }

    public TestPartitionStagingInsertRecord create(
        Long recordId,
        LocalDateTime occurredAt,
        LocalDateTime createdAt,
        AuditEventType auditEventType,
        AuditResult auditResult,
        Long actorId
    ) {
        Objects.requireNonNull(recordId, "recordId must not be null");
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
            "record_data"
        );

        PreparedStatementSetter parameterBinder =
            preparedStatement -> {
                ObjectNode recordData =
                    (ObjectNode) auditLogRecordDataMapper.toRecordData(
                        auditLogCreateDTO
                    );

                recordData.put("id", recordId);

                PGobject recordDataObject = new PGobject();
                recordDataObject.setType("jsonb");
                recordDataObject.setValue(recordData.toString());

                preparedStatement.setObject(1, recordDataObject);
            };

        return new TestPartitionStagingInsertRecord(
            recordId,
            columnNames,
            parameterBinder,
            occurredAt
        );
    }
}