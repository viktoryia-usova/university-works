package server.server.business.dto.finance;

import server.server.business.entity.enums.DepositStatus;
import server.server.business.entity.enums.DepositType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DepositResponse(
        Long id,
        String name,
        DepositType type,
        BigDecimal initialAmount,
        String currency,
        BigDecimal interestRate,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal currentAmount,
        BigDecimal projectedIncome,
        DepositStatus status,
        LocalDateTime createdAt
) {}
