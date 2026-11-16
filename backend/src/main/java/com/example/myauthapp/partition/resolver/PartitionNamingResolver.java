package com.example.myauthapp.partition.resolver; 

import java.time.YearMonth;
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.util.Objects;

import org.springframework.stereotype.Component; 

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionNamePartitionError;

@Component 
public class PartitionNamingResolver {

    private static final DateTimeFormatter MONTHLY_PARTITION_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy_MM");

    public String getDefaultPartitionName(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        
        return partitionTablePropertiesContext.getParentTableName()
                + partitionTablePropertiesContext.getDefaultTableNameSuffix();
    }

    public String getMonthlyPartitionName(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        LocalDateTime startedAt
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            startedAt,
            "startedAt must not be null"
        );

        return partitionTablePropertiesContext.getParentTableName()
            + "_"
            + startedAt.format(MONTHLY_PARTITION_FORMATTER);
    }

    public LocalDateTime getMonthlyPartitionStartedAt(
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

        String prefix = partitionTablePropertiesContext.getParentTableName() + "_";
        String stringYearMonth = null;

        if (targetPartitionName.startsWith(prefix)) {

            stringYearMonth = targetPartitionName.substring(prefix.length());

            return YearMonth
                .parse(stringYearMonth, MONTHLY_PARTITION_FORMATTER)
                .atDay(1)
                .atStartOfDay();

        }else{
            throw new PartitionException(
                PartitionNamePartitionError.PARTITION_NAME_IS_INVALID
            );
        }
    }
}