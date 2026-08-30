package com.recoverx.dto;

import com.recoverx.service.Decision;

public record DecisionDTO(
        String diagnosis,
        double confidence,
        String recommendedAction,
        String reasonText,
        String priority
) {
    public static DecisionDTO from(Decision d) {
        return new DecisionDTO(
                d.diagnosis(),
                d.confidence(),
                d.recommendedAction().name(),
                d.reasonText(),
                d.priority()
        );
    }
}
