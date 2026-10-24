package server.server.business.dto.finance;

import server.server.business.entity.enums.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String color,
        CategoryType type,
        LocalDateTime createdAt
) {}
