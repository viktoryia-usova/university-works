package server.server.business.mapper.finance;

import org.mapstruct.Mapper;
import server.server.business.dto.finance.CategoryResponse;
import server.server.business.entity.finance.Category;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper implements BaseReadOnlyMapper<Category, CategoryResponse> {

    @Override
    public abstract CategoryResponse toDto(Category entity);

    @Override
    public CategoryResponse toDtoWithRelations(Category entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public CategoryResponse toDtoWithRelations(Category entity) {
        return toDto(entity);
    }
}
