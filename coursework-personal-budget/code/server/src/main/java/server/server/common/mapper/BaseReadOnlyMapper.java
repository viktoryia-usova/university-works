package server.server.common.mapper;

import server.server.common.entity.classes.BaseEntity;
import server.server.common.mapper.context.RelationPaths;

public interface BaseReadOnlyMapper<Entity extends BaseEntity, DTO> {

    DTO toDto(Entity entity);

    /** Maps entity with explicitly selected relations. Safe for bidirectional associations. */
    DTO toDtoWithRelations(Entity entity, RelationPaths paths);

    /**
     * Maps entity with a predefined set of relations (implemented per-mapper).
     * Implementations must use {@link RelationPaths#of} with explicit field names
     * to avoid circular recursion on bidirectional associations.
     */
    DTO toDtoWithRelations(Entity entity);
}
