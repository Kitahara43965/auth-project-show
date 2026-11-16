package com.example.myauthapp.partition.repository.discovery.name;

import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.resolver.PartitionNamingResolver;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;

@Repository
public class PartitionTargetPartitionNameDiscoveryJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionNamingResolver partitionNamingResolver;

    public PartitionTargetPartitionNameDiscoveryJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionNamingResolver partitionNamingResolver
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionNamingResolver = partitionNamingResolver;
    }

    public List<String> findAllTargetPartitionNames(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        String defaultPartitionName =
            Objects.requireNonNull(
                partitionNamingResolver.getDefaultPartitionName(
                    partitionTablePropertiesContext
                ),
                "defaultPartitionName must not be null"
            );

        return auditJdbcTemplate.query(
            """
            SELECT
                child.relname AS target_partition_name
            FROM pg_inherits
            JOIN pg_class parent
                ON pg_inherits.inhparent = parent.oid
            JOIN pg_namespace parent_schema
                ON parent.relnamespace = parent_schema.oid
            JOIN pg_class child
                ON pg_inherits.inhrelid = child.oid
            JOIN pg_namespace child_schema
                ON child.relnamespace = child_schema.oid
            WHERE
                parent.relname = ?
                AND parent_schema.nspname = ?
                AND child_schema.nspname = ?
                AND child.relname <> ?
            ORDER BY child.relname
            """,
            (resultSet, rowNumber) ->
                resultSet.getString("target_partition_name"),
            partitionTablePropertiesContext.getParentTableName(),
            partitionTablePropertiesContext.getSchemaName(),
            partitionTablePropertiesContext.getSchemaName(),
            defaultPartitionName
        );
    }

    public boolean doesTargetPartitionExist(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        String targetPartitionName =
            partitionRangeDTO.getTargetPartitionName();

        Long partitionCount = auditJdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM pg_inherits
            JOIN pg_class parent
                ON pg_inherits.inhparent = parent.oid
            JOIN pg_class child
                ON pg_inherits.inhrelid = child.oid
            JOIN pg_namespace parent_schema
                ON parent.relnamespace = parent_schema.oid
            JOIN pg_namespace child_schema
                ON child.relnamespace = child_schema.oid
            WHERE
                parent.relname = ?
                AND parent_schema.nspname = ?
                AND child.relname = ?
                AND child_schema.nspname = ?
            """,
            Long.class,
            partitionTablePropertiesContext.getParentTableName(),
            partitionTablePropertiesContext.getSchemaName(),
            targetPartitionName,
            partitionTablePropertiesContext.getSchemaName()
        );

        return partitionCount != null && partitionCount > 0;
    }
}