package server.server.business.mapper.shared;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.shared.SharedGoalResponse;
import server.server.business.entity.shared.SharedGoal;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public abstract class SharedGoalMapper implements BaseReadOnlyMapper<SharedGoal, SharedGoalResponse> {

    @Override
    @Mapping(target = "sheetId",        source = "sheet.id")
    @Mapping(target = "progressPercent", expression = "java(computeProgress(entity))")
    public abstract SharedGoalResponse toDto(SharedGoal entity);

    protected BigDecimal computeProgress(SharedGoal entity) {
        if (entity.getTargetAmount() == null || entity.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return entity.getCurrentAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(entity.getTargetAmount(), 2, RoundingMode.HALF_UP);
    }

    @Override
    public SharedGoalResponse toDtoWithRelations(SharedGoal entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public SharedGoalResponse toDtoWithRelations(SharedGoal entity) {
        return toDto(entity);
    }
}
