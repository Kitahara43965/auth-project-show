package com.example.myauthapp.audit.log.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

public class AuditLogCreateDTO {

    private final LocalDateTime occurredAt;
    private final LocalDateTime createdAt;
    private final AuditEventType auditEventType;
    private final AuditResult auditResult;
    private final Long actorId;

    // 通常作成用
    public AuditLogCreateDTO(
        AuditEventType auditEventType,
        AuditResult auditResult,
        Long actorId
    ) {
        this(null, null, auditEventType, auditResult, actorId);
    }

    // 日時指定用（テストなど）
    public AuditLogCreateDTO(
        LocalDateTime occurredAt,
        LocalDateTime createdAt,
        AuditEventType auditEventType,
        AuditResult auditResult,
        Long actorId
    ) {
        this.occurredAt = occurredAt;
        this.createdAt = createdAt;

        this.auditEventType = Objects.requireNonNull(
            auditEventType,
            "auditEventType must not be null"
        );
        this.auditResult = Objects.requireNonNull(
            auditResult,
            "auditResult must not be null"
        );
        this.actorId = actorId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AuditEventType getAuditEventType() {
        return auditEventType;
    }

    public AuditResult getAuditResult() {
        return auditResult;
    }

    public Long getActorId() {
        return actorId;
    }
}