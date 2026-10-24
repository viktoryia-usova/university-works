package server.server.business.dto.profile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProfileResponse(
        Long id,
        Long accountId,
        String email,
        String firstName,
        String lastName,
        String phone,
        LocalDate birthDate,
        String friendCode,
        String defaultCurrency,
        LocalDateTime createdAt
) {}
