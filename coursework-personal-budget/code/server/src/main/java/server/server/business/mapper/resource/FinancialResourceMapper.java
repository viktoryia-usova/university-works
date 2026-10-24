package server.server.business.mapper.resource;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.resource.FinancialResourceResponse;
import server.server.business.entity.resource.FinancialResource;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring", uses = AttachmentMapper.class)
public abstract class FinancialResourceMapper implements BaseReadOnlyMapper<FinancialResource, FinancialResourceResponse> {

    @Override
    @Mapping(target = "categoryId",   source = "resourceCategory.id")
    @Mapping(target = "categoryName", source = "resourceCategory.name")
    @Mapping(target = "attachments",  source = "attachments")
    @Mapping(target = "isFavorite",   constant = "false")
    public abstract FinancialResourceResponse toDto(FinancialResource entity);

    /** Maps with an explicit favorite flag — avoids an extra repository call when the caller already knows. */
    public FinancialResourceResponse toDto(FinancialResource entity, boolean isFavorite) {
        FinancialResourceResponse dto = toDto(entity);
        return new FinancialResourceResponse(
                dto.id(), dto.categoryId(), dto.categoryName(),
                dto.title(), dto.content(), dto.attachments(),
                dto.createdAt(), dto.updatedAt(), isFavorite
        );
    }

    @Override
    public FinancialResourceResponse toDtoWithRelations(FinancialResource entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public FinancialResourceResponse toDtoWithRelations(FinancialResource entity) {
        return toDto(entity);
    }
}
