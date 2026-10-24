package server.server.business.mapper.finance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.finance.GoalResponse;
import server.server.business.entity.finance.Goal;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public abstract class GoalMapper implements BaseReadOnlyMapper<Goal, GoalResponse> {

    @Override
    @Mapping(target = "progressPercent", expression = "java(computeProgress(entity))")
    public abstract GoalResponse toDto(Goal entity);

    protected BigDecimal computeProgress(Goal entity) {
        if (entity.getTargetAmount() == null || entity.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return entity.getCurrentAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(entity.getTargetAmount(), 2, RoundingMode.HALF_UP);
    }

    @Override
    public GoalResponse toDtoWithRelations(Goal entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public GoalResponse toDtoWithRelations(Goal entity) {
        return toDto(entity);
    }
}
