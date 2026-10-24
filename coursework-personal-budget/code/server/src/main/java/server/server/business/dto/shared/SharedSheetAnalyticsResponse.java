package server.server.business.dto.shared;

import java.math.BigDecimal;
import java.util.List;

public record SharedSheetAnalyticsResponse(
        Long sheetId,
        String sheetName,
        BigDecimal totalContributedByn,
        BigDecimal totalSpentByn,
        BigDecimal balanceByn,
        BigDecimal equalShareByn,
        int memberCount,
        List<MemberContributionResponse> memberContributions
) {}
