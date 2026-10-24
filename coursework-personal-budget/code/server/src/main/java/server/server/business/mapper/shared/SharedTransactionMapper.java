package server.server.business.mapper.shared;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.shared.SharedTransactionResponse;
import server.server.business.entity.shared.SharedTransaction;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class SharedTransactionMapper implements BaseReadOnlyMapper<SharedTransaction, SharedTransactionResponse> {

    @Override
    @Mapping(target = "sheetId",        source = "member.sheet.id")
    @Mapping(target = "profileId",      source = "member.profile.id")
    @Mapping(target = "authorEmail",    source = "member.profile.account.username")
    @Mapping(target = "authorFirstName",source = "member.profile.firstName")
    public abstract SharedTransactionResponse toDto(SharedTransaction entity);

    @Override
    public SharedTransactionResponse toDtoWithRelations(SharedTransaction entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public SharedTransactionResponse toDtoWithRelations(SharedTransaction entity) {
        return toDto(entity);
    }
}
