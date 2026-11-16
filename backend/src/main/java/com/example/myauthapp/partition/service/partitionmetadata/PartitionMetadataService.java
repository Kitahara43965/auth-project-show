package com.example.myauthapp.partition.service.partitionmetadata;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionTableDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.discovery.partitionrange.PartitionRangeDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitiontable.PartitionTableDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.partitiontable.PartitionTableJdbcRepository;
import com.example.myauthapp.partition.repository.partitionrange.PartitionRangeJdbcRepository;
import com.example.myauthapp.partition.resolver.PartitionNamingResolver;
import com.example.myauthapp.partition.util.PartitionTimeUtils;

@Service
public class PartitionMetadataService {

    private final PartitionTableDTODiscoveryJdbcRepository
        partitionTableDTODiscoveryJdbcRepository;

    private final PartitionRangeDTODiscoveryJdbcRepository
        partitionRangeDTODiscoveryJdbcRepository;

    private final PartitionTableJdbcRepository
        partitionTableJdbcRepository;

    private final PartitionRangeJdbcRepository partitionRangeJdbcRepository;

    private final PartitionNamingResolver partitionNamingResolver;

    public PartitionMetadataService(
        PartitionTableDTODiscoveryJdbcRepository
            partitionTableDTODiscoveryJdbcRepository,
        PartitionRangeDTODiscoveryJdbcRepository
            partitionRangeDTODiscoveryJdbcRepository,
        PartitionTableJdbcRepository
            partitionTableJdbcRepository,
        PartitionRangeJdbcRepository partitionRangeJdbcRepository,
        PartitionNamingResolver partitionNamingResolver
    ) {
        this.partitionTableDTODiscoveryJdbcRepository =
            partitionTableDTODiscoveryJdbcRepository;
        this.partitionRangeDTODiscoveryJdbcRepository =
            partitionRangeDTODiscoveryJdbcRepository;
        this.partitionTableJdbcRepository =
            partitionTableJdbcRepository;
        this.partitionRangeJdbcRepository =
            partitionRangeJdbcRepository;
        this.partitionNamingResolver =
            partitionNamingResolver;
    }


    private PartitionTableDTO ensurePartitionTableDTO(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionOperationPropertiesContext partitionOperationPropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionOperationPropertiesContext,
            "partitionOperationPropertiesContext must not be null"
        );

        Optional<PartitionTableDTO> optionalPartitionTableDTO =
            partitionTableDTODiscoveryJdbcRepository
                .findOptionalPartitionTableDTO(
                    partitionTablePropertiesContext
                );

        return optionalPartitionTableDTO.orElseGet(() -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime createdAt = now;
            LocalDateTime updatedAt = now;

            return partitionTableJdbcRepository.insert(
                createdAt,
                updatedAt,
                partitionTablePropertiesContext.getParentTableName(),
                partitionOperationPropertiesContext
                    .getPartitionOperationStrategy(),
                partitionTablePropertiesContext
                    .getPartitionLocalDateTimeColumnName()
            );
        });
    }

    private PartitionRangeDTO ensurePartitionRangeDTO(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionTableDTO partitionTableDTO,
        LocalDateTime localDateTime
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionTableDTO,
            "partitionTableDTO must not be null"
        );

        Objects.requireNonNull(
            localDateTime,
            "localDateTime must not be null"
        );

        LocalDateTime rangeStartAt =
            PartitionTimeUtils.getMonthStart(localDateTime);

        LocalDateTime rangeEndAt =
            PartitionTimeUtils.getNextMonthStart(localDateTime);

        String targetPartitionName =
            partitionNamingResolver.getMonthlyPartitionName(
                partitionTablePropertiesContext,
                rangeStartAt
            );

        return partitionRangeDTODiscoveryJdbcRepository
            .findOptionalPartitionRangeDTOByPartitionName(
                partitionTablePropertiesContext,
                targetPartitionName
            )
            .orElseGet(() ->
                partitionRangeJdbcRepository.insert(
                    partitionTableDTO.getId(),
                    LocalDateTime.now(),
                    rangeStartAt,
                    rangeEndAt,
                    targetPartitionName
                )
            );
    }

    

    @Transactional(transactionManager = "partitionTransactionManager")
    public PartitionRangeDTO ensurePartition(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionOperationPropertiesContext partitionOperationPropertiesContext,
        LocalDateTime targetMonth
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionOperationPropertiesContext,
            "partitionOperationPropertiesContext must not be null"
        );

        Objects.requireNonNull(
            targetMonth,
            "targetMonth must not be null"
        );

        PartitionTableDTO partitionTableDTO =
            ensurePartitionTableDTO(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext
            );

        PartitionRangeDTO partitionRangeDTO =
            ensurePartitionRangeDTO(
                partitionTablePropertiesContext,
                partitionTableDTO,
                targetMonth
            );

        return partitionRangeDTO;
    }
}