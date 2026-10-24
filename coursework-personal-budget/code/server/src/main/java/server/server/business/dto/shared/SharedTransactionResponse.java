package server.server.business.dto.shared;

import server.server.business.entity.enums.SharedTransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SharedTransactionResponse(
        Long id,
        Long sheetId,
        Long profileId,
        String authorEmail,
        String authorFirstName,
        BigDecimal amount,
        String currency,
        BigDecimal amountByn,
        SharedTransactionType type,
        String description,
        LocalDate date,
        LocalDateTime createdAt
) {}
