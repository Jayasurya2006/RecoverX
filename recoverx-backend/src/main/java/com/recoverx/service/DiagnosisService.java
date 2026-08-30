package com.recoverx.service;

import com.recoverx.model.RecommendedAction;
import com.recoverx.model.Transaction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DiagnosisService {

    private final ChatClient chatClient;

    public DiagnosisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public Decision diagnose(Transaction t) {

        String reason = t.getFailureReason();

        String prompt = """
                You are the AI diagnosis engine for RecoverX,
                an intelligent payment recovery system.

                Analyze this failed payment.

                Payment ID: %s
                Amount: %.2f
                Failure reason: %s

                Choose exactly ONE diagnosis:
                temporary_failure
                card_expired
                insufficient_funds
                unrecognized_failure

                Choose exactly ONE action:
                RETRY
                RETRY_LATER
                REQUEST_UPDATE
                ESCALATE

                Confidence must be between 0.0 and 1.0.

                IMPORTANT:
                Return ONLY one single line.
                Do NOT use Markdown.
                Do NOT use code blocks.
                Do NOT add any extra text.

                Required format:
                diagnosis|confidence|action|explanation

                Example:
                temporary_failure|0.92|RETRY|The payment failure appears temporary.
                """.formatted(
                t.getExternalId(),
                t.getAmount(),
                reason
        );

        try {

            String response = chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            System.out.println("AI RESPONSE: " + response);

            return parseResponse(response);

        } catch (Exception e) {

            System.out.println("AI ERROR: " + e.getMessage());

            /*
             * OpenAI unavailable / no credits / API error.
             *
             * Instead of automatically escalating every payment,
             * use a deterministic safety fallback based on the
             * known payment failure reason.
             */
            System.out.println(
                    "AI FALLBACK: Using deterministic recovery rules for "
                            + t.getExternalId()
            );

            return fallbackByFailureReason(t);
        }
    }

    private Decision parseResponse(String response) {

        if (response == null || response.isBlank()) {
            return fallbackByFailureReason(null);
        }

        String cleaned = response
                .trim()
                .replace("```text", "")
                .replace("```", "")
                .replace("`", "")
                .trim();

        String selectedLine = null;

        for (String line : cleaned.split("\\R")) {

            line = line.trim();

            if (line.split("\\|").length >= 4) {
                selectedLine = line;
                break;
            }
        }

        if (selectedLine == null) {
            return fallbackByFailureReason(null);
        }

        String[] parts = selectedLine.split("\\|", 4);

        if (parts.length != 4) {
            return fallbackByFailureReason(null);
        }

        try {

            String diagnosis = parts[0]
                    .trim()
                    .toLowerCase();

            double confidence = Double.parseDouble(
                    parts[1].trim()
            );

            String actionText = parts[2]
                    .trim()
                    .toUpperCase();

            RecommendedAction action =
                    RecommendedAction.valueOf(actionText);

            String explanation = parts[3].trim();

            // Validate diagnosis.
            if (!diagnosis.equals("temporary_failure")
                    && !diagnosis.equals("card_expired")
                    && !diagnosis.equals("insufficient_funds")
                    && !diagnosis.equals("unrecognized_failure")) {

                return fallbackByFailureReason(null);
            }

            // Safety clamp.
            confidence = Math.max(
                    0.0,
                    Math.min(1.0, confidence)
            );

            String risk;

            if (confidence >= 0.85) {
                risk = "LOW";
            } else if (confidence >= 0.60) {
                risk = "MEDIUM";
            } else {
                risk = "HIGH";
            }

            return new Decision(
                    diagnosis,
                    confidence,
                    action,
                    explanation,
                    risk,
                    0.0
            );

        } catch (Exception e) {

            System.out.println(
                    "AI PARSE ERROR: " + e.getMessage()
            );

            return fallbackByFailureReason(null);
        }
    }

    /**
     * Deterministic fallback used when the AI API is unavailable.
     *
     * These rules are intentionally conservative:
     *
     * BANK_TIMEOUT  -> RETRY
     * NETWORK_ISSUE -> RETRY
     * CARD_EXPIRED  -> REQUEST_UPDATE
     * INSUFFICIENT_FUNDS -> ESCALATE
     * Unknown -> ESCALATE
     */
    private Decision fallbackByFailureReason(Transaction t) {

        if (t == null || t.getFailureReason() == null) {

            return new Decision(
                    "unrecognized_failure",
                    0.0,
                    RecommendedAction.ESCALATE,
                    "AI unavailable and failure reason could not be safely determined. Routed to human review.",
                    "HIGH",
                    0.0
            );
        }

        String reason = t.getFailureReason()
                .trim()
                .toUpperCase();

        switch (reason) {

            case "BANK_TIMEOUT":

                return new Decision(
                        "temporary_failure",
                        0.95,
                        RecommendedAction.RETRY,
                        "AI unavailable. Deterministic policy identified a bank timeout as a temporary failure.",
                        "LOW",
                        0.80
                );

            case "NETWORK_ISSUE":

                return new Decision(
                        "temporary_failure",
                        0.95,
                        RecommendedAction.RETRY,
                        "AI unavailable. Deterministic policy identified a network issue as a temporary failure.",
                        "LOW",
                        0.80
                );

            case "CARD_EXPIRED":

                return new Decision(
                        "card_expired",
                        0.99,
                        RecommendedAction.REQUEST_UPDATE,
                        "AI unavailable. Deterministic policy identified an expired card. Customer payment-method update is required.",
                        "LOW",
                        0.75
                );

            case "INSUFFICIENT_FUNDS":

                return new Decision(
                        "insufficient_funds",
                        0.99,
                        RecommendedAction.ESCALATE,
                        "AI unavailable. Deterministic policy identified insufficient funds. Automatic retry is blocked and routed to human review.",
                        "LOW",
                        0.0
                );

            default:

                return new Decision(
                        "unrecognized_failure",
                        0.0,
                        RecommendedAction.ESCALATE,
                        "AI unavailable and the failure pattern is not recognized by the deterministic recovery policy.",
                        "HIGH",
                        0.0
                );
        }
    }
}

