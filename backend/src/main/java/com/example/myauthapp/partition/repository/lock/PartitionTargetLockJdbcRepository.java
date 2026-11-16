package com.example.myauthapp.partition.repository.lock;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.util.PartitionLockKeyUtils;

@Repository
public class PartitionTargetLockJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;

    public PartitionTargetLockJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
    }

    /**
     * Recovery処理が同じpartitionに対して
     * 同時実行されないようにトランザクション単位の
     * Advisory Lockを取得する。
     */
    public void acquirePartitionLock(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        String targetPartitionName = Objects.requireNonNull(
            partitionRangeDTO.getTargetPartitionName(),
            "targetPartitionName must not be null"
        );

        String parentTableName = Objects.requireNonNull(
            partitionTablePropertiesContext.getParentTableName(),
            "parentTableName must not be null"
        );

        String lockKey = PartitionLockKeyUtils.getPartitionLockKey(
            partitionTablePropertiesContext,
            partitionRangeDTO
        );

        auditJdbcTemplate.queryForObject(
            "SELECT pg_advisory_xact_lock(hashtext(?))",
            (rs, rowNum) -> rs.getObject(1),
            lockKey
        );
        
    }
}