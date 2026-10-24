package server.server.business.dto.profile;

import jakarta.validation.constraints.NotNull;

public record UpdatePrivacySettingsRequest(
        @NotNull Boolean searchableByEmail,
        @NotNull Boolean searchableByPhone,
        @NotNull Boolean searchableByFriendCode,
        @NotNull Boolean showFullName
) {}
