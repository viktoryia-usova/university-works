package server.server.business.mapper.shared;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.shared.SharedSheetResponse;
import server.server.business.entity.shared.SharedSheet;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring", uses = SharedSheetMemberMapper.class)
public abstract class SharedSheetMapper implements BaseReadOnlyMapper<SharedSheet, SharedSheetResponse> {

    @Override
    @Mapping(target = "creatorProfileId", source = "creator.id")
    @Mapping(target = "creatorEmail",     source = "creator.account.username")
    @Mapping(target = "members",          source = "members")
    public abstract SharedSheetResponse toDto(SharedSheet entity);

    @Override
    public SharedSheetResponse toDtoWithRelations(SharedSheet entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public SharedSheetResponse toDtoWithRelations(SharedSheet entity) {
        return toDto(entity);
    }
}
