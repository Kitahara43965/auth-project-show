package com.example.myauthapp.audit.log.config;

import java.util.Objects;

import com.example.myauthapp.partition.config.PartitionCommandProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("audit.logs.partition.command")
public class AuditLogPartitionCommandProperties implements PartitionCommandProperties {

    private final String partitionRecoveryCommandText;

    public AuditLogPartitionCommandProperties(
        String partitionRecoveryCommandText
    ) {
        this.partitionRecoveryCommandText = 
            Objects.requireNonNull(
                partitionRecoveryCommandText,
                "partitionRecoveryCommandText must not be null"
            );
    }

    public String getPartitionRecoveryCommandText(){
        return partitionRecoveryCommandText;
    }
}