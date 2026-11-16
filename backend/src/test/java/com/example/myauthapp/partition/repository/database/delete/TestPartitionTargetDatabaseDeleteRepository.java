package com.example.myauthapp.partition.repository.database.delete;

import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.repository.discovery.name.PartitionTargetPartitionNameDiscoveryJdbcRepository;

@Repository
public class TestPartitionTargetDatabaseDeleteRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository;

    public TestPartitionTargetDatabaseDeleteRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionTargetPartitionNameDiscoveryJdbcRepository = partitionTargetPartitionNameDiscoveryJdbcRepository;
    }

    public void deleteAllTargetPartitions(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        List<String> targetPartitionNames =
            partitionTargetPartitionNameDiscoveryJdbcRepository.findAllTargetPartitionNames(
                partitionTablePropertiesContext
            );

        targetPartitionNames.forEach(
            targetPartitionName ->
                auditJdbcTemplate.execute(
                    "DROP TABLE IF EXISTS \"" + targetPartitionName + "\""
                )
        );
    }
}