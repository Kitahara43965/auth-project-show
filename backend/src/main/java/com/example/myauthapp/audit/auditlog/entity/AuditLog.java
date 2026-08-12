package com.example.myauthapp.audit.auditlog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_event_type", nullable = false, length = 100)
    private AuditEventType auditEventType;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "request_id")
    private String requestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_result", nullable = false, length = 50)
    private AuditResult auditResult;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    protected AuditLog() {
    }

    public AuditLog(
            AuditEventType auditEventType,
            Long actorId,
            Long subjectId,
            String clientId,
            String ipAddress,
            String userAgent,
            String requestId,
            AuditResult auditResult,
            String metadata
    ) {
        this.auditEventType = auditEventType;
        this.actorId = actorId;
        this.subjectId = subjectId;
        this.clientId = clientId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.requestId = requestId;
        this.auditResult = auditResult;
        this.metadata = metadata;
    }

    public AuditLog(
        AuditEventType auditEventType,
        Long actorId,
        AuditResult auditResult
    ) {
        this.auditEventType = auditEventType;
        this.actorId = actorId;
        this.auditResult = auditResult;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (occurredAt == null) {
            occurredAt = now;
        }

        if (createdAt == null) {
            createdAt = now;
        }
    }

    

    public Long getId() {
        return id;
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

    public Long getActorId() {
        return actorId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getRequestId() {
        return requestId;
    }

    public AuditResult getAuditResult() {
        return auditResult;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}