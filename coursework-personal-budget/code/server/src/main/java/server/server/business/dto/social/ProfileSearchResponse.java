package server.server.business.dto.social;

public record ProfileSearchResponse(
        Long profileId,
        String email,
        String firstName,
        String lastName,
        String friendCode
) {}
