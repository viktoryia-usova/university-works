package server.server.business.mapper.resource;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import server.server.business.dto.resource.AttachmentResponse;
import server.server.business.entity.resource.ResourceAttachment;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class AttachmentMapper implements BaseReadOnlyMapper<ResourceAttachment, AttachmentResponse> {

    @Override
    @Mapping(target = "fileUrl", source = "filePath")
    public abstract AttachmentResponse toDto(ResourceAttachment entity);

    @Override
    @Named("attachmentWithRelations")
    public AttachmentResponse toDtoWithRelations(ResourceAttachment entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    @Named("attachmentWithRelations")
    public AttachmentResponse toDtoWithRelations(ResourceAttachment entity) {
        return toDto(entity);
    }
}
