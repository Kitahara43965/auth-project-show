package com.example.myauthapp.partition.factory;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.provider.PartitionRecoveryTableExpressionProvider;

@Component
public class PartitionRecoveryTableExpressionProviderContextFactory {

    public PartitionRecoveryTableExpressionProviderContext create(
        PartitionRecoveryTableExpressionProvider partitionRecoveryTableExpressionProvider
    ) {
        Objects.requireNonNull(
            partitionRecoveryTableExpressionProvider,
            "partitionRecoveryTableExpressionProvider must not be null"
        );

        return new PartitionRecoveryTableExpressionProviderContext(
            partitionRecoveryTableExpressionProvider.partitionRecoveryTableExpressions()
        );
    }
}