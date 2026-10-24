package server.server.business.dto.finance;

import server.server.business.entity.enums.PeriodType;
import server.server.business.entity.enums.PlannedExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PlannedExpenseResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        BigDecimal amount,
        String currency,
        PlannedExpenseType type,
        PeriodType periodType,
        LocalDate plannedDate,
        Boolean isFulfilled,
        LocalDateTime createdAt
) {}
