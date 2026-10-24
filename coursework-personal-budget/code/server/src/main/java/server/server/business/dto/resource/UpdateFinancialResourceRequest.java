package server.server.business.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateFinancialResourceRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 300) String title,
        @NotBlank String content
) {}
