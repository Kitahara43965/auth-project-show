package com.example.myauthapp.partition.service.discovery.time;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.Comparator;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.resolver.PartitionNamingResolver;
import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.repository.discovery.time
    .PartitionDefaultTimeDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.time
    .PartitionStagingTimeDiscoveryJdbcRepository;

@Repository
public class PartitionTimeDiscoveryJdbcService {

    private final PartitionNamingResolver partitionNamingResolver;
    private final PartitionProperties partitionProperties;
    private final PartitionDefaultTimeDiscoveryJdbcRepository
        partitionDefaultTimeDiscoveryJdbcRepository;
    private final PartitionStagingTimeDiscoveryJdbcRepository
        partitionStagingTimeDiscoveryJdbcRepository;

    public PartitionTimeDiscoveryJdbcService(
        PartitionNamingResolver partitionNamingResolver,
        PartitionProperties partitionProperties,
        PartitionDefaultTimeDiscoveryJdbcRepository
            partitionDefaultTimeDiscoveryJdbcRepository,
        PartitionStagingTimeDiscoveryJdbcRepository
            partitionStagingTimeDiscoveryJdbcRepository
    ) {
        this.partitionNamingResolver = partitionNamingResolver;
        this.partitionProperties = partitionProperties;
        this.partitionDefaultTimeDiscoveryJdbcRepository =
            partitionDefaultTimeDiscoveryJdbcRepository;
        this.partitionStagingTimeDiscoveryJdbcRepository =
            partitionStagingTimeDiscoveryJdbcRepository;
    }

    public List<LocalDateTime> findRecoveryTargetMonths(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        String defaultPartitionName =
            partitionNamingResolver.getDefaultPartitionName(partitionTablePropertiesContext);

        String partitionStagingTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionStagingTableName(),
                "partitionStagingTableName must not be null"
            );

        String localDateTimeColumnName =
            Objects.requireNonNull(
                partitionTablePropertiesContext.getPartitionLocalDateTimeColumnName(),
                "partitionLocalDateTimeColumnName must not be null"
            );

        List<LocalDateTime> defaultPartitionMonths =
            partitionDefaultTimeDiscoveryJdbcRepository
                .findTargetMonthsFromDefaultPartition(
                    defaultPartitionName,
                    localDateTimeColumnName
                );

        List<LocalDateTime> stagingMonths =
            partitionStagingTimeDiscoveryJdbcRepository
                .findTargetMonthsFromStaging(
                    partitionStagingTableName,
                    localDateTimeColumnName
                );

        return Stream.concat(
            defaultPartitionMonths.stream(),
            stagingMonths.stream()
        )
        .distinct()
        .sorted(Comparator.naturalOrder())
        .toList();
    }
}