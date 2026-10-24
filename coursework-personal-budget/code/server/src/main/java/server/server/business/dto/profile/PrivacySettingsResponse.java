package server.server.business.dto.profile;

public record PrivacySettingsResponse(
        Long id,
        Boolean searchableByEmail,
        Boolean searchableByPhone,
        Boolean searchableByFriendCode,
        Boolean showFullName
) {}
