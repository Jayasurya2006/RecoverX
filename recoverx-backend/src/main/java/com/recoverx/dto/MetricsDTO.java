package com.recoverx.dto;

import java.math.BigDecimal;

public record MetricsDTO(
        BigDecimal revenueAtRisk,
        BigDecimal revenueRecovered,
        double recoveryRatePercent,
        long recoveredCount,
        long unrecoveredCount,
        long escalatedCount,
        long pendingCount,
        long totalFailed
) {
}
