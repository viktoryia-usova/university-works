package server.server.business.dto.shared;

import server.server.business.entity.enums.SharedSheetStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SharedSheetResponse(
        Long id,
        Long creatorProfileId,
        String creatorEmail,
        String name,
        String description,
        SharedSheetStatus status,
        LocalDateTime completedAt,
        List<SharedSheetMemberResponse> members,
        LocalDateTime createdAt
) {}
