package server.server.common.mapper;

import server.server.common.entity.classes.BaseEntity;

public interface BaseCreateOnlyEntityMapper<Entity extends BaseEntity, ResponseDTO, CreateDTO>
    extends BaseCreateMapper<Entity, CreateDTO>, BaseReadOnlyMapper<Entity, ResponseDTO>
{
}
