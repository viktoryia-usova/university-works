package server.server.business.mapper.profile;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.profile.ProfileResponse;
import server.server.business.entity.profile.Profile;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class ProfileMapper implements BaseReadOnlyMapper<Profile, ProfileResponse> {

    @Override
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "email",     source = "account.username")
    public abstract ProfileResponse toDto(Profile entity);

    @Override
    public ProfileResponse toDtoWithRelations(Profile entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public ProfileResponse toDtoWithRelations(Profile entity) {
        return toDto(entity);
    }
}
