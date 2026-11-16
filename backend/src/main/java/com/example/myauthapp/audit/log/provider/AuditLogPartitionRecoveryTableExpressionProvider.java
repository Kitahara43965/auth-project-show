package com.example.myauthapp.audit.log.provider;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.provider.PartitionRecoveryTableExpressionProvider;
import com.example.myauthapp.partition.dto.PartitionRecoveryTableExpression;

@Component
public class AuditLogPartitionRecoveryTableExpressionProvider
        implements PartitionRecoveryTableExpressionProvider {

    @Override
    public List<PartitionRecoveryTableExpression> partitionRecoveryTableExpressions() {
        return List.of(
            new PartitionRecoveryTableExpression(
                "id",
                "id",
                "(record_data ->> 'id')::BIGINT"
            ),
            new PartitionRecoveryTableExpression(
                "occurred_at",
                "occurred_at",
                "(record_data ->> 'occurred_at')::timestamp"
            ),
            new PartitionRecoveryTableExpression(
                "created_at",
                "created_at",
                "(record_data ->> 'created_at')::timestamp"
            ),
            new PartitionRecoveryTableExpression(
                "audit_event_type",
                "audit_event_type",
                "record_data ->> 'audit_event_type'"
            ),
            new PartitionRecoveryTableExpression(
                "audit_result",
                "audit_result",
                "record_data ->> 'audit_result'"
            ),
            new PartitionRecoveryTableExpression(
                "actor_id",
                "actor_id",
                "(record_data ->> 'actor_id')::BIGINT"
            ),
            new PartitionRecoveryTableExpression(
                "subject_id",
                "subject_id",
                "(record_data ->> 'subject_id')::BIGINT"
            ),
            new PartitionRecoveryTableExpression(
                "client_id",
                "client_id",
                "record_data ->> 'client_id'"
            ),
            new PartitionRecoveryTableExpression(
                "ip_address",
                "ip_address",
                "record_data ->> 'ip_address'"
            ),
            new PartitionRecoveryTableExpression(
                "user_agent",
                "user_agent",
                "record_data ->> 'user_agent'"
            ),
            new PartitionRecoveryTableExpression(
                "request_id",
                "request_id",
                "record_data ->> 'request_id'"
            ),
            new PartitionRecoveryTableExpression(
                "metadata",
                "metadata",
                "record_data -> 'metadata'"
            )
        );
    }

}
