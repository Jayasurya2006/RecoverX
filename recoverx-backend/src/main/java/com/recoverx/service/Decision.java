package com.recoverx.service;

import com.recoverx.model.RecommendedAction;

/**
 * The AI agent never returns free text - it returns a structured decision the
 * policy gate can reason about deterministically. successProbability is an
 * internal simulation parameter (stands in for "how likely this action is to
 * work") and is intentionally not exposed on the public DTO.
 */
public record Decision(
        String diagnosis,
        double confidence,
        RecommendedAction recommendedAction,
        String reasonText,
        String priority,
        double successProbability
) {
}
