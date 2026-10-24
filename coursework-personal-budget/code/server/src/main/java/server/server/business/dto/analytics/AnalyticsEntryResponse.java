package server.server.business.dto.analytics;

import java.math.BigDecimal;

public record AnalyticsEntryResponse(
        String label,       // category name OR "YYYY-MM"
        BigDecimal amountByn,
        Double percent      // share of total, nullable for time-series
) {}
