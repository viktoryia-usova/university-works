package server.server.business.dto.finance;

import jakarta.validation.constraints.*;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateGoalRequest(
        @NotBlank @Size(max = 200) String name,
        @NotNull @DecimalMin("0.01") BigDecimal targetAmount,
        @NotBlank @ValidCurrency String currency,
        @Future LocalDate deadline
) {}
