package server.server.business.dto.resource;

import java.time.LocalDateTime;

public record FinancialResourceSummaryResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String title,
        int attachmentCount,
        LocalDateTime createdAt
) {}
