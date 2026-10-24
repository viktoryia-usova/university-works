package server.server.business.dto.shared;

import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
        @NotNull(message = "Profile ID of the contact to invite is required")
        Long profileId
) {}
