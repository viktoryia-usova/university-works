package server.server.business.dto.analytics;

import java.math.BigDecimal;
import java.util.List;

public record AnalyticsSummaryResponse(
        BigDecimal totalIncomeByn,
        BigDecimal totalExpenseByn,
        BigDecimal balanceByn,
        List<AnalyticsEntryResponse> entries
) {}
