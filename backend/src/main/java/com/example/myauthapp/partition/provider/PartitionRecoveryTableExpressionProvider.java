package com.example.myauthapp.partition.provider; 

import java.util.List;

import com.example.myauthapp.partition.dto.PartitionRecoveryTableExpression;

public interface PartitionRecoveryTableExpressionProvider {

    List<PartitionRecoveryTableExpression> partitionRecoveryTableExpressions();

}