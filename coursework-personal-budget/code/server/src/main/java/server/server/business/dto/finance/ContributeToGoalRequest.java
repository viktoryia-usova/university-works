package server.server.business.dto.finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;

public record ContributeToGoalRequest(
        @NotNull
        @DecimalMin(value = "0.0001", message = "Contribution must be positive")
        BigDecimal amount,

        @NotBlank
        @ValidCurrency
        String currency
) {}
