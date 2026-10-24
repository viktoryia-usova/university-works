package server.server.business.mapper.profile;

import org.mapstruct.Mapper;
import server.server.business.dto.profile.PrivacySettingsResponse;
import server.server.business.entity.profile.PrivacySettings;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class PrivacySettingsMapper implements BaseReadOnlyMapper<PrivacySettings, PrivacySettingsResponse> {

    @Override
    public abstract PrivacySettingsResponse toDto(PrivacySettings entity);

    @Override
    public PrivacySettingsResponse toDtoWithRelations(PrivacySettings entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public PrivacySettingsResponse toDtoWithRelations(PrivacySettings entity) {
        return toDto(entity);
    }
}
