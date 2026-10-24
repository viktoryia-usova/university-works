package server.server.business.dto.social;

import server.server.business.entity.enums.ContactStatus;

import java.time.LocalDateTime;

public record ContactResponse(
        Long id,
        Long contactId,         // the contact relationship ID
        Long profileId,         // the OTHER person's profile id
        String email,
        String firstName,
        String lastName,
        String friendCode,
        ContactStatus status,
        boolean isRequester,    // true if current user sent the request
        LocalDateTime createdAt
) {}
