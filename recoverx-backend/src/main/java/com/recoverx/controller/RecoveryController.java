package com.recoverx.controller;

import com.recoverx.dto.MetricsDTO;
import com.recoverx.dto.ProcessResultDTO;
import com.recoverx.dto.TransactionDTO;
import com.recoverx.repository.AuditLogRepository;
import com.recoverx.repository.TransactionRepository;
import com.recoverx.service.MetricsService;
import com.recoverx.service.RecoveryOrchestratorService;
import com.recoverx.service.TransactionGeneratorService;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecoveryController {

    private final TransactionGeneratorService transactionGeneratorService;
    private final RecoveryOrchestratorService recoveryOrchestratorService;
    private final MetricsService metricsService;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    public RecoveryController(TransactionGeneratorService transactionGeneratorService,
                               RecoveryOrchestratorService recoveryOrchestratorService,
                               MetricsService metricsService,
                               TransactionRepository transactionRepository,
                               AuditLogRepository auditLogRepository) {
        this.transactionGeneratorService = transactionGeneratorService;
        this.recoveryOrchestratorService = recoveryOrchestratorService;
        this.metricsService = metricsService;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /** Generates a fresh synthetic batch, replacing any existing one. */
    @PostMapping("/batch")
    public List<TransactionDTO> generateBatch(@RequestParam(defaultValue = "16") int count) {
        return transactionGeneratorService.generateBatch(count).stream()
                .map(TransactionDTO::from)
                .toList();
    }

    @GetMapping("/transactions")
    public List<TransactionDTO> listTransactions() {
        return transactionRepository.findAllByOrderByIdAsc().stream()
                .map(TransactionDTO::from)
                .toList();
    }

    /** Runs one payment through Detect -> Diagnose -> Decide -> Gate -> Execute -> Audit. */
    @PostMapping("/recovery/process/{externalId}")
    public ProcessResultDTO process(@PathVariable String externalId) {
        return recoveryOrchestratorService.processTransaction(externalId);
    }

    @GetMapping("/transactions/{externalId}/audit")
    public List<String> audit(@PathVariable String externalId) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT;
        return auditLogRepository.findByTransactionExternalIdOrderByTimestampAsc(externalId).stream()
                .map(a -> "[" + fmt.format(a.getTimestamp()) + "] (" + a.getStage() + ") " + a.getMessage())
                .toList();
    }

    @GetMapping("/metrics")
    public MetricsDTO metrics() {
        return metricsService.compute();
    }

    @DeleteMapping("/reset")
    public void reset() {
        auditLogRepository.deleteAllInBatch();
        transactionRepository.deleteAllInBatch();
    }
}
