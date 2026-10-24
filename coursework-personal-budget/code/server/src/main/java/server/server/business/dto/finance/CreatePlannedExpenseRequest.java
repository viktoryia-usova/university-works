package server.server.business.dto.finance;

import jakarta.validation.constraints.*;
import server.server.business.entity.enums.PeriodType;
import server.server.business.entity.enums.PlannedExpenseType;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePlannedExpenseRequest(
        Long categoryId,

        @NotBlank @Size(max = 200)
        String name,

        @NotNull @DecimalMin("0.01")
        BigDecimal amount,

        @NotBlank @ValidCurrency
        String currency,

        @NotNull
        PlannedExpenseType type,

        /** Required for BUDGET_LIMIT */
        PeriodType periodType,

        /** Required for PLANNED_PURCHASE */
        LocalDate plannedDate
) {}
