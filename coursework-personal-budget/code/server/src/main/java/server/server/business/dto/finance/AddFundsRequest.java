package server.server.business.dto.finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddFundsRequest(
        @NotNull
        @DecimalMin(value = "0.0001", message = "Amount must be positive")
        BigDecimal amount
) {}
