package server.server.business.mapper.social;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.social.ProfileSearchResponse;
import server.server.business.entity.profile.Profile;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class ProfileSearchMapper implements BaseReadOnlyMapper<Profile, ProfileSearchResponse> {

    @Override
    @Mapping(target = "profileId", source = "id")
    @Mapping(target = "email",     source = "account.username")
    public abstract ProfileSearchResponse toDto(Profile entity);

    @Override
    public ProfileSearchResponse toDtoWithRelations(Profile entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public ProfileSearchResponse toDtoWithRelations(Profile entity) {
        return toDto(entity);
    }
}
