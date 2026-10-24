package server.server.business.mapper.finance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.finance.DepositResponse;
import server.server.business.entity.finance.Deposit;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public abstract class DepositMapper implements BaseReadOnlyMapper<Deposit, DepositResponse> {

    @Override
    @Mapping(target = "projectedIncome", expression = "java(computeProjectedIncome(entity))")
    public abstract DepositResponse toDto(Deposit entity);

    /**
     * Simple annual simple-interest calculation: principal × rate% × (days / 365).
     * Returns ZERO for PIGGY_BANK deposits (no end date or interest rate).
     */
    protected BigDecimal computeProjectedIncome(Deposit entity) {
        if (entity.getInterestRate() == null || entity.getEndDate() == null || entity.getStartDate() == null) {
            return BigDecimal.ZERO;
        }
        long days = ChronoUnit.DAYS.between(entity.getStartDate(), entity.getEndDate());
        if (days <= 0) return BigDecimal.ZERO;
        return entity.getInitialAmount()
                .multiply(entity.getInterestRate())
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(days))
                .divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);
    }

    @Override
    public DepositResponse toDtoWithRelations(Deposit entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public DepositResponse toDtoWithRelations(Deposit entity) {
        return toDto(entity);
    }
}
