package com.example.myauthapp.partition.repository.discovery.count;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.resolver.PartitionNamingResolver;

@Repository
public class PartitionTargetCountJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionNamingResolver partitionNamingResolver;

    public PartitionTargetCountJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionNamingResolver partitionNamingResolver
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionNamingResolver = partitionNamingResolver;
    }

    public long countTargetPartitionRowCount(
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

        String targetPartitionName = Objects.requireNonNull(
            partitionRangeDTO.getTargetPartitionName(),
            "targetPartitionName must not be null"
        );

        String partitionLocalDateTimeColumnName = Objects.requireNonNull(
            partitionTablePropertiesContext.getPartitionLocalDateTimeColumnName(),
            "partitionLocalDateTimeColumnName must not be null"
        );

        return auditJdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM %s
            WHERE %s >= ?
            AND %s < ?
            """.formatted(
                targetPartitionName, 
                partitionLocalDateTimeColumnName, 
                partitionLocalDateTimeColumnName
            ),
            Long.class,
            partitionRangeDTO.getRangeStartAt(),
            partitionRangeDTO.getRangeEndAt()
        );
    }


}