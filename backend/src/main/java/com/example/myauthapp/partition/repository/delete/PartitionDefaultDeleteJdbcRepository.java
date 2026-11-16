package com.example.myauthapp.partition.repository.delete;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.resolver.PartitionNamingResolver;

@Repository
public class PartitionDefaultDeleteJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionNamingResolver partitionNamingResolver;

    public PartitionDefaultDeleteJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionNamingResolver partitionNamingResolver
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionNamingResolver = partitionNamingResolver;
    }


    public void deleteDefaultPartitionRows(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        Objects.requireNonNull(partitionTablePropertiesContext, "partitionTablePropertiesContext must not be null");
        Objects.requireNonNull(partitionRangeDTO, "partitionRangeDTO must not be null");

        String defaultPartitionName =
            Objects.requireNonNull(
                partitionNamingResolver.getDefaultPartitionName(partitionTablePropertiesContext),
                "defaultPartitionName must not be null"
            );

        String columnName =
            Objects.requireNonNull(
                partitionTablePropertiesContext.getPartitionLocalDateTimeColumnName(),
                "partitionLocalDateTimeColumnName must not be null"
            );

        auditJdbcTemplate.update(
            """
            DELETE FROM %s
            WHERE %s >= ?
            AND %s < ?
            """.formatted(
                defaultPartitionName,
                columnName,
                columnName
            ),
            partitionRangeDTO.getRangeStartAt(),
            partitionRangeDTO.getRangeEndAt()
        );
    }
    
}
