package com.example.myauthapp.audit.log.mapper;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.audit.log.dto.AuditLogCreateDTO;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Component
public class AuditLogRecordDataMapper {

    private final ObjectMapper objectMapper;

    public AuditLogRecordDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode toRecordData(
        AuditLogCreateDTO auditLogCreateDTO
    ) {
        Objects.requireNonNull(
            auditLogCreateDTO,
            "auditLogCreateDTO must not be null"
        );

        ObjectNode json = objectMapper.createObjectNode();

        putNullable(json, "id", null);
        putNullable(json, "occurred_at", auditLogCreateDTO.getOccurredAt());
        putNullable(json, "created_at", auditLogCreateDTO.getCreatedAt());

        putNullable(
            json,
            "audit_event_type",
            auditLogCreateDTO.getAuditEventType().name()
        );

        putNullable(
            json,
            "audit_result",
            auditLogCreateDTO.getAuditResult().name()
        );

        putNullable(json, "actor_id", auditLogCreateDTO.getActorId());
        putNullable(json, "subject_id", null);
        putNullable(json, "client_id", null);
        putNullable(json, "ip_address", null);
        putNullable(json, "user_agent", null);
        putNullable(json, "request_id", null);
        putNullable(json, "metadata", null);

        return json;
    }

    private void putNullable(
        ObjectNode json,
        String fieldName,
        Object value
    ) {
        if (value == null) {
            json.putNull(fieldName);
        } else {
            json.set(
                fieldName,
                objectMapper.valueToTree(value)
            );
        }
    }
}