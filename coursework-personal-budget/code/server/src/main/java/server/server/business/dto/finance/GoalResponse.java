package server.server.business.dto.finance;

import server.server.business.entity.enums.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalResponse(
        Long id,
        String name,
        BigDecimal targetAmount,
        String currency,
        BigDecimal currentAmount,
        BigDecimal progressPercent,
        LocalDate deadline,
        GoalStatus status,
        LocalDateTime createdAt
) {}
