package com.recoverx.dto;

import com.recoverx.model.Transaction;

import java.math.BigDecimal;

public record TransactionDTO(
        String externalId,
        BigDecimal amount,
        String status,
        String failureReason,
        String recoveryStatus,
        int attempts,
        int batchNo
) {
    public static TransactionDTO from(Transaction t) {
        return new TransactionDTO(
                t.getExternalId(),
                t.getAmount(),
                t.getStatus().name(),
                t.getFailureReason(),
                t.getRecoveryStatus().name(),
                t.getAttempts(),
                t.getBatchNo()
        );
    }
}
