package server.server.business.dto.resource;

import java.time.LocalDateTime;
import java.util.List;

public record FinancialResourceResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String title,
        String content,
        List<AttachmentResponse> attachments,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isFavorite
) {}
