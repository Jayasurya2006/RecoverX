package com.recoverx.service;

import com.recoverx.model.RecoveryStatus;
import com.recoverx.model.Transaction;
import com.recoverx.model.TransactionStatus;
import com.recoverx.repository.AuditLogRepository;
import com.recoverx.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Produces synthetic/test-mode transaction data standing in for a merchant's
 * real payment stream. One batch is "active" at a time - generating a new
 * batch clears the previous one, matching the demo dashboard's UX.
 */
@Service
public class TransactionGeneratorService {

    private static final Map<String, Integer> REASON_WEIGHTS = new LinkedHashMap<>();
    static {
        REASON_WEIGHTS.put("BANK_TIMEOUT", 3);
        REASON_WEIGHTS.put("NETWORK_ISSUE", 2);
        REASON_WEIGHTS.put("TEMPORARY_ERROR", 2);
        REASON_WEIGHTS.put("CARD_EXPIRED", 3);
        REASON_WEIGHTS.put("INSUFFICIENT_FUNDS", 2);
        REASON_WEIGHTS.put("ISSUER_DECLINED", 1);
    }

    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final AtomicInteger batchCounter = new AtomicInteger(0);
    private final Random random = new Random();

    public TransactionGeneratorService(TransactionRepository transactionRepository,
                                        AuditLogRepository auditLogRepository) {
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public List<Transaction> generateBatch(int count) {
        auditLogRepository.deleteAllInBatch();
        transactionRepository.deleteAllInBatch();

        int batchNo = batchCounter.incrementAndGet();
        List<Transaction> created = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            BigDecimal amount = BigDecimal.valueOf(Math.round((300 + random.nextDouble() * 4700) / 10.0) * 10);
            boolean failed = random.nextDouble() < 0.62;

            Transaction t = new Transaction();
            t.setExternalId("PAY_" + String.format("%04d", 1000 + batchNo * 100 + i));
            t.setAmount(amount);
            t.setBatchNo(batchNo);
            t.setAttempts(0);
            t.setCreatedAt(Instant.now());

            if (failed) {
                t.setStatus(TransactionStatus.FAILED);
                t.setFailureReason(weightedReason());
                t.setRecoveryStatus(RecoveryStatus.PENDING);
            } else {
                t.setStatus(TransactionStatus.SUCCESS);
                t.setRecoveryStatus(RecoveryStatus.NOT_APPLICABLE);
            }
            created.add(t);
        }

        return transactionRepository.saveAll(created);
    }

    private String weightedReason() {
        int total = REASON_WEIGHTS.values().stream().mapToInt(Integer::intValue).sum();
        int r = random.nextInt(total);
        for (Map.Entry<String, Integer> e : REASON_WEIGHTS.entrySet()) {
            if (r < e.getValue()) {
                return e.getKey();
            }
            r -= e.getValue();
        }
        return "BANK_TIMEOUT";
    }
}
