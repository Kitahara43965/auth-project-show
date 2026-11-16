package com.example.myauthapp.partition.config;

import java.time.LocalDateTime;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "test.partition.recovery-failure")
public class TestPartitionRecoveryFailureProperties {

    private LocalDateTime occurredAtBefore;
    private String recoveryFailureInducingTableName;
    private String deleteDefaultFailureTriggerName;
    private String deleteDefaultFailureFunctionName;
    private String deleteStagingFailureTriggerName;
    private String deleteStagingFailureFunctionName;

    public LocalDateTime getOccurredAtBefore() {
        return occurredAtBefore;
    }

    public void setOccurredAtBefore(LocalDateTime occurredAtBefore) {
        this.occurredAtBefore = occurredAtBefore;
    }

    public String getRecoveryFailureInducingTableName() {
        return recoveryFailureInducingTableName;
    }

    public void setRecoveryFailureInducingTableName(
        String recoveryFailureInducingTableName
    ) {
        this.recoveryFailureInducingTableName =
            recoveryFailureInducingTableName;
    }

    public String getDeleteDefaultFailureTriggerName() {
        return deleteDefaultFailureTriggerName;
    }

    public void setDeleteDefaultFailureTriggerName(
        String deleteDefaultFailureTriggerName
    ) {
        this.deleteDefaultFailureTriggerName =
            deleteDefaultFailureTriggerName;
    }

    public String getDeleteDefaultFailureFunctionName() {
        return deleteDefaultFailureFunctionName;
    }

    public void setDeleteDefaultFailureFunctionName(
        String deleteDefaultFailureFunctionName
    ) {
        this.deleteDefaultFailureFunctionName =
            deleteDefaultFailureFunctionName;
    }

    public String getDeleteStagingFailureTriggerName() {
        return deleteStagingFailureTriggerName;
    }

    public void setDeleteStagingFailureTriggerName(
        String deleteStagingFailureTriggerName
    ) {
        this.deleteStagingFailureTriggerName =
            deleteStagingFailureTriggerName;
    }

    public String getDeleteStagingFailureFunctionName() {
        return deleteStagingFailureFunctionName;
    }

    public void setDeleteStagingFailureFunctionName(
        String deleteStagingFailureFunctionName
    ) {
        this.deleteStagingFailureFunctionName =
            deleteStagingFailureFunctionName;
    }
}