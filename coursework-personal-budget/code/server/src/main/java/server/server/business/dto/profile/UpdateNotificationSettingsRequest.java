package server.server.business.dto.profile;

import jakarta.validation.constraints.NotNull;

public record UpdateNotificationSettingsRequest(
        @NotNull Boolean onGoalReached,
        @NotNull Boolean onBudgetExceeded,
        @NotNull Boolean onSharedTransaction,
        @NotNull Boolean onContactRequest,
        @NotNull Boolean onDepositMatured
) {}
