package server.server.business.dto.finance;

import jakarta.validation.constraints.*;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalRequest(
        @NotBlank(message = "Goal name is required")
        @Size(max = 200)
        String name,

        @NotNull
        @DecimalMin(value = "0.01", message = "Target amount must be positive")
        BigDecimal targetAmount,

        @NotBlank
        @ValidCurrency
        String currency,

        @Future(message = "Deadline must be in the future")
        LocalDate deadline
) {}
