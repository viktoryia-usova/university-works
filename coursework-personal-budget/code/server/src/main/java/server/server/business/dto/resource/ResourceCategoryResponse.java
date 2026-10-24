package server.server.business.dto.resource;

import java.time.LocalDateTime;

public record ResourceCategoryResponse(Long id, String name, LocalDateTime createdAt) {}
