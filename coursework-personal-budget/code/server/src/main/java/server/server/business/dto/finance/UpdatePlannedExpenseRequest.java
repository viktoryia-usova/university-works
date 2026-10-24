package server.server.business.dto.finance;

import jakarta.validation.constraints.*;
import server.server.business.entity.enums.PeriodType;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdatePlannedExpenseRequest(
        Long categoryId,
        @NotBlank @Size(max = 200) String name,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @ValidCurrency String currency,
        PeriodType periodType,
        LocalDate plannedDate,
        Boolean isFulfilled
) {}
