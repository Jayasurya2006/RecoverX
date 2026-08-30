package com.recoverx.service;

import com.recoverx.model.RecommendedAction;
import com.recoverx.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;

/**
 * Stands in for the "AI agent" step of the pipeline. Deliberately rule-based
 * and deterministic-in-shape so the demo is reproducible; swap the body of
 * diagnose() for a real model call (e.g. the Anthropic API) without touching
 * anything downstream - the rest of the pipeline only depends on the
 * Decision record, never on how it was produced.
 */
@Service
public class DiagnosisService {

    private static final Set<String> TEMPORARY_REASONS = Set.of("BANK_TIMEOUT", "NETWORK_ISSUE", "TEMPORARY_ERROR");
    private final Random random = new Random();

    public Decision diagnose(Transaction t) {
        String reason = t.getFailureReason();

        if (reason != null && TEMPORARY_REASONS.contains(reason)) {
            return new Decision(
                    "temporary_failure",
                    round(0.86 + random.nextDouble() * 0.10),
                    RecommendedAction.RETRY,
                    "Failure appears transient (" + reason.toLowerCase() + ")",
                    "HIGH",
                    0.68
            );
        }

        if ("CARD_EXPIRED".equals(reason)) {
            return new Decision(
                    "card_expired",
                    round(0.90 + random.nextDouble() * 0.06),
                    RecommendedAction.REQUEST_UPDATE,
                    "Card on file has expired",
                    "MEDIUM",
                    0.58
            );
        }

        if ("INSUFFICIENT_FUNDS".equals(reason)) {
            return new Decision(
                    "insufficient_funds",
                    round(0.78 + random.nextDouble() * 0.10),
                    RecommendedAction.RETRY_LATER,
                    "Funds likely unavailable at time of charge",
                    "MEDIUM",
                    0.50
            );
        }

        // Unrecognized failure pattern - low confidence, straight to human review
        return new Decision(
                "unrecognized_failure",
                round(0.30 + random.nextDouble() * 0.15),
                RecommendedAction.ESCALATE,
                "Failure pattern not recognized with sufficient confidence",
                "HIGH",
                0.0
        );
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
