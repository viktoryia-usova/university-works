package server.server.business.mapper.finance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.finance.TransactionResponse;
import server.server.business.entity.finance.Transaction;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper implements BaseReadOnlyMapper<Transaction, TransactionResponse> {

    @Override
    @Mapping(target = "categoryId",    source = "category.id")
    @Mapping(target = "categoryName",  source = "category.name")
    @Mapping(target = "categoryColor", source = "category.color")
    public abstract TransactionResponse toDto(Transaction entity);

    @Override
    public TransactionResponse toDtoWithRelations(Transaction entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public TransactionResponse toDtoWithRelations(Transaction entity) {
        return toDto(entity);
    }
}
