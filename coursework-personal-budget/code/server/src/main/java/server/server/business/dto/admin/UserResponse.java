package server.server.business.dto.admin;

import server.server.common.entity.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        Role role,
        Boolean active,
        LocalDateTime createdAt,
        // Profile info (nullable for ADMIN role)
        Long profileId,
        String firstName,
        String lastName,
        String phone,
        String friendCode
) {}
