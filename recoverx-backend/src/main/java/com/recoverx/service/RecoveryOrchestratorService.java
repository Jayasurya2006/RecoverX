package com.recoverx.service;

import com.recoverx.dto.DecisionDTO;
import com.recoverx.dto.ProcessResultDTO;
import com.recoverx.dto.TransactionDTO;
import com.recoverx.model.AuditLog;
import com.recoverx.model.RecommendedAction;
import com.recoverx.model.RecoveryStatus;
import com.recoverx.model.Transaction;
import com.recoverx.repository.AuditLogRepository;
import com.recoverx.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecoveryOrchestratorService {

    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final DiagnosisService diagnosisService;
    private final PolicyGateService policyGateService;
    private final RecoveryExecutionService recoveryExecutionService;

    public RecoveryOrchestratorService(TransactionRepository transactionRepository,
                                        AuditLogRepository auditLogRepository,
                                        DiagnosisService diagnosisService,
                                        PolicyGateService policyGateService,
                                        RecoveryExecutionService recoveryExecutionService) {
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
        this.diagnosisService = diagnosisService;
        this.policyGateService = policyGateService;
        this.recoveryExecutionService = recoveryExecutionService;
    }

    @Transactional
    public ProcessResultDTO processTransaction(String externalId) {
        Transaction t = transactionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found: " + externalId));

        // Idempotent: a payment that's already resolved doesn't get reprocessed.
        if (t.getRecoveryStatus() != RecoveryStatus.PENDING) {
            return new ProcessResultDTO(
                    TransactionDTO.from(t),
                    null,
                    List.of("Payment already resolved: " + t.getRecoveryStatus()),
                    true
            );
        }

        List<String> lines = new ArrayList<>();

        // 1. DETECT
        addAudit(t, "DETECT", "Payment failed, reason=" + t.getFailureReason() + ", amount=" + t.getAmount(), lines);

        // 2. DIAGNOSE
        Decision decision = diagnosisService.diagnose(t);
        addAudit(t, "DIAGNOSE", String.format("diagnosis=%s confidence=%.2f", decision.diagnosis(), decision.confidence()), lines);

        // 3. DECIDE
        addAudit(t, "DECIDE", "recommended_action=" + decision.recommendedAction(), lines);

        // 4. GATE
        GateResult gate = policyGateService.check(t, decision);
        addAudit(t, "GATE", gate.allowed() ? "APPROVED" : "BLOCKED: " + gate.reason(), lines);

        if (!gate.allowed()) {
            t.setRecoveryStatus(RecoveryStatus.ESCALATED);
            addAudit(t, "AUDIT", "Escalated to human review", lines);
            transactionRepository.save(t);
            return new ProcessResultDTO(TransactionDTO.from(t), DecisionDTO.from(decision), lines, true);
        }

        // 5. EXECUTE
        boolean resolved;
        if (decision.recommendedAction() == RecommendedAction.REQUEST_UPDATE) {
            resolved = executeRequestUpdate(t, decision, lines);
        } else {
            resolved = executeRetryLoop(t, decision, lines);
        }

        // 6. AUDIT
        addAudit(t, "AUDIT", "Audit entry finalized. Final status: " + t.getRecoveryStatus(), lines);
        transactionRepository.save(t);

        return new ProcessResultDTO(TransactionDTO.from(t), DecisionDTO.from(decision), lines, resolved);
    }

    private boolean executeRequestUpdate(Transaction t, Decision decision, List<String> lines) {
        t.setAttempts(t.getAttempts() + 1);
        addAudit(t, "EXECUTE", "Sent payment-method update request to customer", lines);

        boolean success = recoveryExecutionService.attempt(decision.successProbability());
        if (success) {
            t.setRecoveryStatus(RecoveryStatus.RECOVERED);
            addAudit(t, "EXECUTE", "Customer updated card - payment retried automatically. Result: SUCCESS", lines);
        } else {
            t.setRecoveryStatus(RecoveryStatus.UNRECOVERED);
            addAudit(t, "EXECUTE", "No response from customer within window. Result: UNRECOVERED", lines);
        }
        return true;
    }

    /** Bounded retry loop for RETRY / RETRY_LATER actions - never exceeds PolicyGateService.MAX_RETRIES. */
    private boolean executeRetryLoop(Transaction t, Decision decision, List<String> lines) {
        boolean resolved = false;
        String label = decision.recommendedAction() == RecommendedAction.RETRY_LATER ? "scheduled retry" : "retry";

        while (t.getAttempts() < PolicyGateService.MAX_RETRIES && !resolved) {
            t.setAttempts(t.getAttempts() + 1);
            addAudit(t, "EXECUTE", "Executing " + label + " #" + t.getAttempts(), lines);

            boolean success = recoveryExecutionService.attempt(decision.successProbability());
            if (success) {
                t.setRecoveryStatus(RecoveryStatus.RECOVERED);
                addAudit(t, "EXECUTE", "Result: SUCCESS - " + t.getAmount() + " recovered (attempt " + t.getAttempts() + ")", lines);
                resolved = true;
            } else {
                addAudit(t, "EXECUTE", "Result: FAILED (attempt " + t.getAttempts() + "/" + PolicyGateService.MAX_RETRIES + ")", lines);
            }
        }

        if (!resolved) {
            t.setRecoveryStatus(RecoveryStatus.ESCALATED);
            addAudit(t, "EXECUTE", "Retry limit reached - safety policy stops execution", lines);
        }
        return resolved;
    }

    private void addAudit(Transaction t, String stage, String message, List<String> lines) {
        AuditLog log = new AuditLog();
        log.setTransaction(t);
        log.setStage(stage);
        log.setMessage(message);
        log.setTimestamp(Instant.now());
        auditLogRepository.save(log);
        lines.add("[" + stage + "] " + message);
    }
}
