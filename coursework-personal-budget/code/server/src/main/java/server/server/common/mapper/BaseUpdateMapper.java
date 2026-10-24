package server.server.common.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import server.server.common.entity.classes.BaseEntity;

public interface BaseUpdateMapper<Entity extends BaseEntity, DTO> {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(DTO dto, @MappingTarget Entity entity);
}
