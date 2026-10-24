package server.server.common.mapper;

import server.server.common.entity.classes.BaseEntity;

public interface BaseCRUDEntityMapper<Entity extends BaseEntity, ResponseDTO, CreateDTO, UpdateDTO>
    extends BaseCreateMapper<Entity, CreateDTO>, BaseUpdateMapper<Entity, UpdateDTO>, BaseReadOnlyMapper<Entity, ResponseDTO>
{
}
