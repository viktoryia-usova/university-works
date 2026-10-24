package server.server.business.dto.shared;

import java.math.BigDecimal;

public record MemberContributionResponse(
        Long profileId,
        String email,
        String firstName,
        BigDecimal contributedByn,
        BigDecimal spentByn,
        BigDecimal netByn
) {}
