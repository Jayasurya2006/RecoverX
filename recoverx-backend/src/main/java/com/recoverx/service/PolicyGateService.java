package com.recoverx.service;

import com.recoverx.model.RecommendedAction;
import com.recoverx.model.Transaction;
import org.springframework.stereotype.Service;

/**
 * "AI recommends, policy decides." Nothing here is learned or probabilistic -
 * these are hard rules the AI agent cannot override. This is what makes every
 * money-moving action explainable, bounded, and gated.
 */
@Service
public class PolicyGateService {

    public static final int MAX_RETRIES = 2;

    public GateResult check(Transaction t, Decision decision) {
        if (decision.recommendedAction() == RecommendedAction.ESCALATE) {
            return new GateResult(false, "AI diagnosis below confidence threshold - routed to human review");
        }

        if (decision.recommendedAction() == RecommendedAction.RETRY
                || decision.recommendedAction() == RecommendedAction.RETRY_LATER) {
            if (t.getAttempts() >= MAX_RETRIES) {
                return new GateResult(false, "Retry limit (" + MAX_RETRIES + ") reached");
            }
            return new GateResult(true, null);
        }

        if (decision.recommendedAction() == RecommendedAction.REQUEST_UPDATE) {
            // Low-risk, non-monetary action - always allowed once.
            return new GateResult(true, null);
        }

        return new GateResult(false, "Unrecognized action type");
    }
}
