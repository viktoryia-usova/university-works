package server.server.common.facade;

import org.springframework.transaction.annotation.Transactional;
import server.server.common.entity.classes.BaseEntity;
import server.server.common.mapper.BaseCreateOnlyEntityMapper;
import server.server.common.service.common.BaseCRUDService;
@Transactional
public abstract class BaseCreateOnlyFacade<
        Entity extends BaseEntity,
        ID,
        Mapper extends BaseCreateOnlyEntityMapper<Entity, ResponseDTO, CreateDTO>,
        Service extends BaseCRUDService<Entity, ID, ?>,
        ResponseDTO,
        CreateDTO
        >
        extends BaseReadOnlyFacade<Entity, ID, Mapper, Service, ResponseDTO> {
    public BaseCreateOnlyFacade(Service service, Mapper mapper) {
        super(service, mapper);
    }

    public ResponseDTO create(CreateDTO dto) {
        Entity entity = mapper.toEntity(dto);
        resolveRelationsOnCreate(entity, dto);
        entity = service.create(entity);
        return mapper.toDto(entity);
    }

    protected void resolveRelationsOnCreate(Entity entity, CreateDTO dto) {}
}
