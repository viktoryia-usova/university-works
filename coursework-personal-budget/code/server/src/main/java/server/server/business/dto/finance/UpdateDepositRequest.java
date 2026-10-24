package server.server.business.dto.finance;

import jakarta.validation.constraints.*;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateDepositRequest(
        @NotBlank @Size(max = 200) String name,
        @DecimalMin("0") @DecimalMax("100") BigDecimal interestRate,
        LocalDate endDate
) {}
