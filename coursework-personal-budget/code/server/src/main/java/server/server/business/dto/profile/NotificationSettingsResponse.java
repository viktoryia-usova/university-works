package server.server.business.dto.profile;

public record NotificationSettingsResponse(
        Long id,
        Boolean onGoalReached,
        Boolean onBudgetExceeded,
        Boolean onSharedTransaction,
        Boolean onContactRequest,
        Boolean onDepositMatured
) {}
