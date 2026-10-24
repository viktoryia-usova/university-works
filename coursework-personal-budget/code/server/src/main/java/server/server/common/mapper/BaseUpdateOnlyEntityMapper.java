package server.server.common.mapper;

import server.server.common.entity.classes.BaseEntity;

public interface BaseUpdateOnlyEntityMapper<Entity extends BaseEntity, ResponseDTO, UpdateDTO>
    extends BaseUpdateMapper<Entity, UpdateDTO>, BaseReadOnlyMapper<Entity, ResponseDTO>
{
}
