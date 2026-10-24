package server.server.business.dto.finance;

import server.server.business.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String categoryColor,
        BigDecimal amount,
        String currency,
        BigDecimal exchangeRateToByn,
        BigDecimal amountByn,
        TransactionType type,
        LocalDate date,
        String description,
        LocalDateTime createdAt
) {}
