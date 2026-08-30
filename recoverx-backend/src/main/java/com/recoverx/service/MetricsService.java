package com.recoverx.service;

import com.recoverx.dto.MetricsDTO;
import com.recoverx.model.RecoveryStatus;
import com.recoverx.model.Transaction;
import com.recoverx.model.TransactionStatus;
import com.recoverx.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MetricsService {

    private final TransactionRepository transactionRepository;

    public MetricsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public MetricsDTO compute() {
        List<Transaction> failed = transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatus.FAILED)
                .toList();

        BigDecimal atRisk = failed.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal recovered = failed.stream()
                .filter(t -> t.getRecoveryStatus() == RecoveryStatus.RECOVERED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double rate = atRisk.compareTo(BigDecimal.ZERO) > 0
                ? recovered.doubleValue() / atRisk.doubleValue() * 100.0
                : 0.0;

        long recoveredCount = count(failed, RecoveryStatus.RECOVERED);
        long unrecoveredCount = count(failed, RecoveryStatus.UNRECOVERED);
        long escalatedCount = count(failed, RecoveryStatus.ESCALATED);
        long pendingCount = count(failed, RecoveryStatus.PENDING);

        return new MetricsDTO(
                atRisk,
                recovered,
                Math.round(rate * 10) / 10.0,
                recoveredCount,
                unrecoveredCount,
                escalatedCount,
                pendingCount,
                failed.size()
        );
    }

    private long count(List<Transaction> failed, RecoveryStatus status) {
        return failed.stream().filter(t -> t.getRecoveryStatus() == status).count();
    }
}
