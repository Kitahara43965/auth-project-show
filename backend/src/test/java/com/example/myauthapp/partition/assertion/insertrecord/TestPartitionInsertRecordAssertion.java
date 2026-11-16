package com.example.myauthapp.partition.assertion.insertrecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.myauthapp.partition.dto.TestPartitionStagingInsertRecord;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;

public final class TestPartitionInsertRecordAssertion {

    private TestPartitionInsertRecordAssertion() {
    }

    public static void assertThatTestPartitionDefaultInsertRecordsHaveIdenticalPartitionLocalDateTimes(
        List<TestPartitionDefaultInsertRecord>
            testPartitionDefaultInsertRecords
    ) {
        Objects.requireNonNull(
            testPartitionDefaultInsertRecords,
            "testPartitionDefaultInsertRecords must not be null"
        );

        LocalDateTime expectedPartitionLocalDateTime = null;

        if(!testPartitionDefaultInsertRecords.isEmpty()){

            expectedPartitionLocalDateTime =
                testPartitionDefaultInsertRecords.get(0)
                    .getPartitionLocalDateTime();

            assertThat(expectedPartitionLocalDateTime)
                .isNotNull();

            for (TestPartitionDefaultInsertRecord record :
                testPartitionDefaultInsertRecords) {

                assertThat(record.getPartitionLocalDateTime())
                    .isEqualTo(expectedPartitionLocalDateTime);
            }
        }//testPartitionDefaultInsertRecords
    }

    public static void assertThatTestPartitionStagingInsertRecordsHaveIdenticalPartitionLocalDateTimes(
        List<TestPartitionStagingInsertRecord>
            testPartitionStagingInsertRecords
    ) {
        Objects.requireNonNull(
            testPartitionStagingInsertRecords,
            "testPartitionStagingInsertRecords must not be null"
        );

        LocalDateTime expectedPartitionLocalDateTime = null;

        if(!testPartitionStagingInsertRecords.isEmpty()){

            expectedPartitionLocalDateTime =
                testPartitionStagingInsertRecords.get(0)
                    .getPartitionLocalDateTime();

            assertThat(expectedPartitionLocalDateTime)
                .isNotNull();

            for (TestPartitionStagingInsertRecord record :
                testPartitionStagingInsertRecords) {

                assertThat(record.getPartitionLocalDateTime())
                    .isEqualTo(expectedPartitionLocalDateTime);
            }
        }//testPartitionStagingInsertRecords
    }
}