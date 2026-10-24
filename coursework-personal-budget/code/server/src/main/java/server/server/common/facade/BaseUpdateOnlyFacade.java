package server.server.common.facade;

import org.springframework.transaction.annotation.Transactional;
import server.server.common.entity.classes.BaseEntity;
import server.server.common.mapper.BaseUpdateOnlyEntityMapper;
import server.server.common.service.common.BaseCRUDService;
@Transactional
public abstract class BaseUpdateOnlyFacade<
        Entity extends BaseEntity,
        ID,
        Mapper extends BaseUpdateOnlyEntityMapper<Entity, ResponseDTO, UpdateDTO>,
        Service extends BaseCRUDService<Entity, ID, ?>,
        ResponseDTO,
        UpdateDTO>
        extends BaseReadOnlyFacade<Entity, ID, Mapper, Service, ResponseDTO> {

    public BaseUpdateOnlyFacade(Service service, Mapper mapper) {
        super(service, mapper);
    }

    public ResponseDTO update(ID id, UpdateDTO dto) {
        Entity entity = service.findById(id);
        mapper.updateFromDto(dto, entity);
        resolveRelationsOnUpdate(entity, dto);
        entity = service.update(entity);
        return mapper.toDto(entity);
    }

    protected void resolveRelationsOnUpdate(Entity entity, UpdateDTO dto) {}
}
