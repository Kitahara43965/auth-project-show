package com.example.myauthapp.partition.factory;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TestPartitionDataFactory {

    public Long createId() {
        return 100L;
    }

    public UUID createOperationId() {
        return UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
        );
    }

    public Long createPartitionUserId() {
        return 200L;
    }
}