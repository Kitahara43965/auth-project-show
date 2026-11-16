
package com.example.myauthapp.audit.log.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

public class AuditLogDTO {

    private final Long id;
    private final LocalDateTime occurredAt;
    private final LocalDateTime createdAt;
    private final AuditEventType auditEventType;
    private final AuditResult auditResult;
    private final Long actorId;
    private final Long subjectId;
    private final String clientId;
    private final String ipAddress;
    private final String userAgent;
    private final String requestId;
    private final String metadata;

    public AuditLogDTO(
        Long id,
        LocalDateTime occurredAt,
        LocalDateTime createdAt,
        AuditEventType auditEventType,
        AuditResult auditResult,
        Long actorId,
        Long subjectId,
        String clientId,
        String ipAddress,
        String userAgent,
        String requestId,
        String metadata
    ) {
        this.id = Objects.requireNonNull(
            id, "id must not be null"
        );
        this.occurredAt = Objects.requireNonNull(
            occurredAt, "occurredAt must not be null"
        );
        this.createdAt = Objects.requireNonNull(
            createdAt, "createdAt must not be null"
        );
        this.auditEventType = Objects.requireNonNull(
            auditEventType, "auditEventType must not be null"
        );
        this.auditResult = Objects.requireNonNull(
            auditResult, "auditResult must not be null"
        );
        this.actorId = actorId;
        this.subjectId = subjectId;
        this.clientId = clientId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.requestId = requestId;
        this.metadata = metadata;
    }

    public Long getId() { return id; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public AuditEventType getAuditEventType() { return auditEventType; }
    public AuditResult getAuditResult() { return auditResult; }
    public Long getActorId() { return actorId; }
    public Long getSubjectId() { return subjectId; }
    public String getClientId() { return clientId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getRequestId() { return requestId; }
    public String getMetadata() { return metadata; }
}
