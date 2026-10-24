package server.server.business.dto.shared;

import jakarta.validation.constraints.*;
import server.server.business.entity.enums.SharedTransactionType;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSharedTransactionRequest(
        @NotNull @DecimalMin("0.0001") BigDecimal amount,
        @NotBlank @ValidCurrency String currency,
        @NotNull SharedTransactionType type,
        @NotNull @PastOrPresent LocalDate date,
        @Size(max = 1000) String description
) {}
