package server.server.business.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResourceCategoryRequest(
        @NotBlank @Size(max = 100) String name
) {}
