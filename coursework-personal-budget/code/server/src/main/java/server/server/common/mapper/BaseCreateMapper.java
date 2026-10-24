package server.server.common.mapper;

import org.mapstruct.Mapping;
import server.server.common.entity.classes.BaseEntity;

public interface BaseCreateMapper<Entity extends BaseEntity, DTO> {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Entity toEntity(DTO dto);
}
