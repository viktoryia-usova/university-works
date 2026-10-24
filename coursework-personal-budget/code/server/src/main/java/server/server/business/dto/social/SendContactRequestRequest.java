package server.server.business.dto.social;

import jakarta.validation.constraints.NotBlank;

public record SendContactRequestRequest(
        @NotBlank(message = "Search query is required (email, phone, or friend code)")
        String query
) {}
