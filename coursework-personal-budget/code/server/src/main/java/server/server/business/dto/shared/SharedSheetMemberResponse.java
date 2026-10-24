package server.server.business.dto.shared;

import java.time.LocalDateTime;

public record SharedSheetMemberResponse(
        Long memberId,
        Long profileId,
        String email,
        String firstName,
        String lastName,
        LocalDateTime joinedAt
) {}
