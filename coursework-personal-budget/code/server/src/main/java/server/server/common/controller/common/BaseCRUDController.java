package server.server.common.controller.common;

import server.server.common.entity.classes.BaseEntity;
import server.server.common.facade.BaseCRUDFacade;

public abstract class BaseCRUDController<
        Entity extends BaseEntity,
        ID,
        ResponseDTO,
        CreateDTO,
        UpdateDTO,
        Facade extends BaseCRUDFacade<Entity, ID, ?, ?, ResponseDTO, CreateDTO, UpdateDTO>>
        extends BaseReadOnlyController<Entity, ID, ResponseDTO, Facade>
{
    public BaseCRUDController(Facade facade) {
        super(facade);
    }

    protected ResponseDTO doCreate(CreateDTO dto) {
        return facade.create(dto);
    }

    protected ResponseDTO doUpdate(ID id, UpdateDTO dto) {
        return facade.update(id, dto);
    }
}
