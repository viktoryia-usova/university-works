package server.server.common.facade;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import server.server.common.entity.classes.BaseEntity;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;
import server.server.common.service.common.AccountQueryable;
import server.server.common.service.common.AccountSingleQueryable;
import server.server.common.service.common.BaseCRUDService;

import java.util.List;

@AllArgsConstructor
@Transactional
public abstract class BaseReadOnlyFacade<
        Entity extends BaseEntity,
        ID,
        Mapper extends BaseReadOnlyMapper<Entity, ResponseDTO>,
        Service extends BaseCRUDService<Entity, ID, ?>,
        ResponseDTO
        > {

    protected final Service service;
    protected final Mapper mapper;

    public ResponseDTO findById(ID id) {
        Entity entity = service.findById(id);
        return mapper.toDto(entity);
    }

    public ResponseDTO findByIdWithRelations(ID id) {
        Entity entity = service.findById(id);
        resolveRelationsOnResponse(entity);
        return mapper.toDtoWithRelations(entity);
    }

    public ResponseDTO findByIdWithRelations(ID id, RelationPaths paths) {
        Entity entity = service.findById(id);
        resolveRelationsOnResponse(entity);
        return mapper.toDtoWithRelations(entity, paths);
    }

    public List<ResponseDTO> findAll() {
        return service.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<ResponseDTO> findAllWithRelations() {
        return service.findAll().stream()
                .map(entity -> { resolveRelationsOnResponse(entity); return mapper.toDtoWithRelations(entity); })
                .toList();
    }

    public List<ResponseDTO> findAllWithRelations(RelationPaths paths) {
        return service.findAll().stream()
                .map(entity -> { resolveRelationsOnResponse(entity); return mapper.toDtoWithRelations(entity, paths); })
                .toList();
    }

    @SuppressWarnings("unchecked")
    public List<ResponseDTO> findAllByAccountId(Long accountId) {
        if (!(service instanceof AccountQueryable<?>)) {
            throw new UnsupportedOperationException(
                    service.getClass().getSimpleName() + " does not implement AccountQueryable");
        }
        return ((AccountQueryable<Entity>) service).findAllByAccountId(accountId).stream()
                .map(entity -> { resolveRelationsOnResponse(entity); return mapper.toDtoWithRelations(entity); })
                .toList();
    }

    @SuppressWarnings("unchecked")
    public ResponseDTO findByAccountId(Long accountId) {
        if (!(service instanceof AccountSingleQueryable<?>)) {
            throw new UnsupportedOperationException(
                    service.getClass().getSimpleName() + " does not implement AccountSingleQueryable");
        }
        Entity entity = ((AccountSingleQueryable<Entity>) service).findByAccountId(accountId);
        resolveRelationsOnResponse(entity);
        return mapper.toDto(entity);
    }

    public void resolveRelationsOnResponse(Entity entity) {}

    public void deleteById(ID id) {
        service.deleteById(id);
    }
}
