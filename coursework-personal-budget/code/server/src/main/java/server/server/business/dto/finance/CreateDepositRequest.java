package server.server.business.dto.finance;

import jakarta.validation.constraints.*;
import server.server.business.entity.enums.DepositType;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDepositRequest(
        @NotBlank @Size(max = 200) String name,

        @NotNull DepositType type,

        @NotNull @DecimalMin("0.01") BigDecimal initialAmount,

        @NotBlank @ValidCurrency String currency,

        /** Required for DEPOSIT type */
        @DecimalMin("0") @DecimalMax("100") BigDecimal interestRate,

        @NotNull LocalDate startDate,

        /** Required for DEPOSIT type; null for PIGGY_BANK */
        LocalDate endDate
) {}
