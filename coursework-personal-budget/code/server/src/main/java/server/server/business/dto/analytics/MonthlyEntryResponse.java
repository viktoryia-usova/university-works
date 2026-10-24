package server.server.business.dto.analytics;

import java.math.BigDecimal;

public record MonthlyEntryResponse(
        int year,
        int month,
        String label,
        BigDecimal incomeByn,
        BigDecimal expenseByn,
        BigDecimal balanceByn
) {}
