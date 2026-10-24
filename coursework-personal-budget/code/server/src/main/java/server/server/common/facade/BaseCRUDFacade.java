package server.server.common.facade;

import org.springframework.transaction.annotation.Transactional;
import server.server.common.entity.classes.BaseEntity;
import server.server.common.mapper.BaseCRUDEntityMapper;
import server.server.common.service.common.BaseCRUDService;
@Transactional
public abstract class BaseCRUDFacade<
        Entity extends BaseEntity,
        ID,
        Mapper extends BaseCRUDEntityMapper<Entity, ResponseDTO, CreateDTO, UpdateDTO>,
        Service extends BaseCRUDService<Entity, ID, ?>,
        ResponseDTO,
        CreateDTO,
        UpdateDTO>
        extends BaseReadOnlyFacade<Entity, ID, Mapper, Service, ResponseDTO> {

    public BaseCRUDFacade(Service service, Mapper mapper) {
        super(service, mapper);
    }

    public ResponseDTO create(CreateDTO dto) {
        Entity entity = mapper.toEntity(dto);
        resolveRelationsOnCreate(entity, dto);
        entity = service.create(entity);
        return mapper.toDto(entity);
    }

    public ResponseDTO update(ID id, UpdateDTO dto) {
        Entity entity = service.findById(id);
        mapper.updateFromDto(dto, entity);
        resolveRelationsOnUpdate(entity, dto);
        entity = service.update(entity);
        return mapper.toDto(entity);
    }

    protected void resolveRelationsOnCreate(Entity entity, CreateDTO dto) {}

    protected void resolveRelationsOnUpdate(Entity entity, UpdateDTO dto) {}
}
