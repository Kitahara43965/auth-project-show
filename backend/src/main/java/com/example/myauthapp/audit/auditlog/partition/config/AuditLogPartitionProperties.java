package com.example.myauthapp.audit.auditlog.partition.config;

import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit.logs.partition")
public record AuditLogPartitionProperties(
        String prefix
) {

    public String getDefaultPartitionName() {
        return prefix + "_default";
    }

    public String getPartitionName(LocalDateTime occurredAt) {
        Objects.requireNonNull(
                occurredAt,
                "occurredAt must not be null"
        );

        return prefix
                + "_"
                + occurredAt.getYear()
                + "_"
                + String.format(
                        "%02d",
                        occurredAt.getMonthValue()
                );
    }
}