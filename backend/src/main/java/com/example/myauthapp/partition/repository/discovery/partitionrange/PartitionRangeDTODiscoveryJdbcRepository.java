package com.example.myauthapp.partition.repository.discovery.partitionrange;

import java.util.Optional;
import java.util.Objects;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.dto.PartitionTableDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.sql.PartitionRangeDTODiscoverySql;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.repository.discovery.partitiontable
    .PartitionTableDTODiscoveryJdbcRepository;

@Repository
public class PartitionRangeDTODiscoveryJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;
    private final PartitionTableDTODiscoveryJdbcRepository
        partitionTableDTODiscoveryJdbcRepository;

    public PartitionRangeDTODiscoveryJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties,
        PartitionTableDTODiscoveryJdbcRepository partitionTableDTODiscoveryJdbcRepository
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
        this.partitionTableDTODiscoveryJdbcRepository =
            partitionTableDTODiscoveryJdbcRepository;
    }

    private Optional<PartitionRangeDTO> queryOptionalSinglePartitionRangeDTO(
        String sql,
        Object... args
    ) {
        List<PartitionRangeDTO> partitionRangeDTOs = 
            partitionJdbcTemplate.query(
                sql,
                PartitionRangeDTODiscoverySql.ROW_MAPPER,
                args
            );

        if (partitionRangeDTOs.size() > 1) {
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .MULTIPLE_PARTITION_RANGE_RECORDS_ARE_FOUND
            );
        }

        return partitionRangeDTOs.stream().findFirst();
    }

    public Optional<PartitionRangeDTO> 
        findOptionalPartitionRangeDTOById(
            Long id
    ) {
        Objects.requireNonNull(
            id,
            "id must not be null"
        );
        String partitionRangeTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionRangeTableName(),
                "partitionRangeTableName must not be null"
            );

        return queryOptionalSinglePartitionRangeDTO(
            """
            SELECT
                %s
            FROM %s
            WHERE id = ?
            """.formatted(
                PartitionRangeDTODiscoverySql.COLUMN_NAMES,
                partitionRangeTableName
            ),
            id
        );
    }

    public PartitionRangeDTO findPartitionRangeDTOById(
        Long id
    ) {
        Objects.requireNonNull(
            id,
            "id must not be null"
        );

        return 
            findOptionalPartitionRangeDTOById(
                id
            )
            .orElseThrow(() -> new PartitionException(
                PartitionRecoveryPartitionError
                    .NO_PARTITION_RANGE_RECORDS_ARE_FOUND
            ));
    }


    @Transactional(
        value = "partitionTransactionManager",
        readOnly = true
    )
    public Optional<PartitionRangeDTO> 
        findOptionalPartitionRangeDTOByPartitionName(
            PartitionTablePropertiesContext partitionTablePropertiesContext,
            String targetPartitionName
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            targetPartitionName,
            "targetPartitionName must not be null"
        );

        PartitionTableDTO partitionTableDTO =
            partitionTableDTODiscoveryJdbcRepository
                .findPartitionTableDTO(
                    partitionTablePropertiesContext
                );

        Long partitionTableId = partitionTableDTO.getId();

        String partitionRangeTableName =
            partitionProperties.getPartitionRangeTableName();

        return queryOptionalSinglePartitionRangeDTO(
            """
            SELECT
                %s
            FROM %s
            WHERE
                partition_table_id = ?
                AND target_partition_name = ?
            """.formatted(
                PartitionRangeDTODiscoverySql.COLUMN_NAMES,
                partitionRangeTableName
            ),
            partitionTableId,
            targetPartitionName
        );
    }

    public PartitionRangeDTO findPartitionRangeDTOByPartitionName(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        String targetPartitionName
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            targetPartitionName,
            "targetPartitionName must not be null"
        );

        return 
            findOptionalPartitionRangeDTOByPartitionName(
                partitionTablePropertiesContext,
                targetPartitionName
            )
            .orElseThrow(() -> new PartitionException(
                PartitionRecoveryPartitionError
                    .NO_PARTITION_RANGE_RECORDS_ARE_FOUND
            ));
    }



    
}