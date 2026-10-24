package server.server.business.mapper.shared;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import server.server.business.dto.shared.SharedSheetMemberResponse;
import server.server.business.entity.shared.SharedSheetMember;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class SharedSheetMemberMapper implements BaseReadOnlyMapper<SharedSheetMember, SharedSheetMemberResponse> {

    @Override
    @Mapping(target = "memberId",   source = "id")
    @Mapping(target = "profileId",  source = "profile.id")
    @Mapping(target = "email",      source = "profile.account.username")
    @Mapping(target = "firstName",  source = "profile.firstName")
    @Mapping(target = "lastName",   source = "profile.lastName")
    public abstract SharedSheetMemberResponse toDto(SharedSheetMember entity);

    @Override
    @Named("memberWithRelations")
    public SharedSheetMemberResponse toDtoWithRelations(SharedSheetMember entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    @Named("memberWithRelations")
    public SharedSheetMemberResponse toDtoWithRelations(SharedSheetMember entity) {
        return toDto(entity);
    }
}
