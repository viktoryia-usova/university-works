package server.server.business.mapper.finance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.finance.PlannedExpenseResponse;
import server.server.business.entity.finance.PlannedExpense;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class PlannedExpenseMapper implements BaseReadOnlyMapper<PlannedExpense, PlannedExpenseResponse> {

    @Override
    @Mapping(target = "categoryId",   source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    public abstract PlannedExpenseResponse toDto(PlannedExpense entity);

    @Override
    public PlannedExpenseResponse toDtoWithRelations(PlannedExpense entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public PlannedExpenseResponse toDtoWithRelations(PlannedExpense entity) {
        return toDto(entity);
    }
}
