package server.server.business.dto.resource;

import server.server.business.entity.enums.AttachmentType;

public record AttachmentResponse(
        Long id,
        String fileName,
        String fileUrl,
        AttachmentType fileType
) {}
