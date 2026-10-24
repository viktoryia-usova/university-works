package server.server.business.mapper.profile;

import org.mapstruct.Mapper;
import server.server.business.dto.profile.NotificationSettingsResponse;
import server.server.business.entity.profile.NotificationSettings;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class NotificationSettingsMapper implements BaseReadOnlyMapper<NotificationSettings, NotificationSettingsResponse> {

    @Override
    public abstract NotificationSettingsResponse toDto(NotificationSettings entity);

    @Override
    public NotificationSettingsResponse toDtoWithRelations(NotificationSettings entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public NotificationSettingsResponse toDtoWithRelations(NotificationSettings entity) {
        return toDto(entity);
    }
}
