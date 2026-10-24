package server.server.business.dto.shared;

import server.server.business.entity.enums.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SharedGoalResponse(
        Long id,
        Long sheetId,
        String name,
        BigDecimal targetAmount,
        String currency,
        BigDecimal currentAmount,
        BigDecimal progressPercent,
        LocalDate deadline,
        GoalStatus status,
        LocalDateTime createdAt
) {}
