package com.example.myauthapp.partition.repository.discovery.partitiontable;

import java.util.Objects;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.dto.PartitionTableDTO;
import com.example.myauthapp.partition.sql.PartitionTableDTODiscoverySql;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;

import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;

@Repository
public class PartitionTableDTODiscoveryJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionTableDTODiscoveryJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    private Optional<PartitionTableDTO> queryOptionalSinglePartitionTableDTO(
        String sql,
        Object... args
    ) {
        List<PartitionTableDTO> partitionTableDTOs =
            partitionJdbcTemplate.query(
                sql,
                PartitionTableDTODiscoverySql.ROW_MAPPER,
                args
            );

        if (partitionTableDTOs.size() > 1) {
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .MULTIPLE_PARTITION_TABLE_RECORDS_ARE_FOUND
            );
        }

        return partitionTableDTOs.stream().findFirst();
    }


    public Optional<PartitionTableDTO> findOptionalPartitionTableDTO(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        String parentTableName =
            partitionTablePropertiesContext.getParentTableName();

        String partitionTableTableName =
            partitionProperties.getPartitionTableTableName();

        return queryOptionalSinglePartitionTableDTO(
            """
            SELECT
                %s
            FROM %s
            WHERE table_name = ?
            """.formatted(
                PartitionTableDTODiscoverySql.COLUMN_NAMES,
                partitionTableTableName
            ),
            parentTableName
        );
    }


    public PartitionTableDTO findPartitionTableDTO(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        return findOptionalPartitionTableDTO(
            partitionTablePropertiesContext
        ).orElseThrow(() -> new PartitionException(
            PartitionRecoveryPartitionError
                .NO_PARTITION_TABLE_RECORDS_ARE_FOUND
        ));
    }


}