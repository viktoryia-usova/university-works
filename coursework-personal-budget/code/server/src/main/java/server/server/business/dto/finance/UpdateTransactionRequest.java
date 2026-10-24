package server.server.business.dto.finance;

import jakarta.validation.constraints.*;
import server.server.business.entity.enums.TransactionType;
import server.server.common.annotation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(
        Long categoryId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0001", message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @ValidCurrency
        String currency,

        @NotNull(message = "Transaction type is required")
        TransactionType type,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate date,

        @Size(max = 1000, message = "Description must be ≤ 1000 characters")
        String description
) {}
