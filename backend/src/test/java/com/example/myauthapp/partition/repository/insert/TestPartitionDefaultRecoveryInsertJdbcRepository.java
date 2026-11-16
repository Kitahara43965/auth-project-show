package com.example.myauthapp.partition.repository.insert;

import java.util.Objects;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.resolver.PartitionNamingResolver;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.config.PartitionProperties;

@Repository
public class TestPartitionDefaultRecoveryInsertJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionNamingResolver partitionNamingResolver;
    private final PartitionProperties partitionProperties;

    public TestPartitionDefaultRecoveryInsertJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionNamingResolver partitionNamingResolver,
        PartitionProperties partitionProperties
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionNamingResolver = partitionNamingResolver;
        this.partitionProperties = partitionProperties;
    }

    public void insertIntoDefaultPartition(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            testPartitionDefaultInsertRecord,
            "testPartitionDefaultInsertRecord must not be null"
        );

        List<String> columnNames = 
            testPartitionDefaultInsertRecord.getColumnNames();
        PreparedStatementSetter parameterBinder = 
            testPartitionDefaultInsertRecord.getParameterBinder();
        String defaultPartitionName = 
            partitionNamingResolver.getDefaultPartitionName(
                partitionTablePropertiesContext
            );

        String placeholders = String.join(
            ", ",
            Collections.nCopies(
                columnNames.size(),
                "?"
            )
        );

        String sql = """
            INSERT INTO %s (%s)
            VALUES (%s)
            """.formatted(
                defaultPartitionName,
                String.join(", ", columnNames),
                placeholders
            );

        auditJdbcTemplate.update(
            sql,
            parameterBinder
        );
    }

}