package server.server.business.mapper.resource;

import org.mapstruct.Mapper;
import server.server.business.dto.resource.ResourceCategoryResponse;
import server.server.business.entity.resource.ResourceCategory;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class ResourceCategoryMapper implements BaseReadOnlyMapper<ResourceCategory, ResourceCategoryResponse> {

    @Override
    public abstract ResourceCategoryResponse toDto(ResourceCategory entity);

    @Override
    public ResourceCategoryResponse toDtoWithRelations(ResourceCategory entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public ResourceCategoryResponse toDtoWithRelations(ResourceCategory entity) {
        return toDto(entity);
    }
}
