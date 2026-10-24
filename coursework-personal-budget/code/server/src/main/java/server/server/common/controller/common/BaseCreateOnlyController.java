package server.server.common.controller.common;

import server.server.common.entity.classes.BaseEntity;
import server.server.common.facade.BaseCreateOnlyFacade;

public abstract class BaseCreateOnlyController<
        Entity extends BaseEntity,
        ID,
        ResponseDTO,
        CreateDTO,
        Facade extends BaseCreateOnlyFacade<Entity, ID, ?, ?, ResponseDTO, CreateDTO>>
        extends BaseReadOnlyController<Entity, ID, ResponseDTO, Facade>
{
    public BaseCreateOnlyController(Facade facade) {
        super(facade);
    }

    protected ResponseDTO doCreate(CreateDTO dto) {
        return facade.create(dto);
    }
}
