package server.server.common.controller.common;

import server.server.common.entity.classes.BaseEntity;
import server.server.common.facade.BaseUpdateOnlyFacade;

public abstract class BaseUpdateOnlyController<
        Entity extends BaseEntity,
        ID,
        ResponseDTO,
        UpdateDTO,
        Facade extends BaseUpdateOnlyFacade<Entity, ID, ?, ?, ResponseDTO, UpdateDTO>>
        extends BaseReadOnlyController<Entity, ID, ResponseDTO, Facade>
{
    public BaseUpdateOnlyController(Facade facade) {
        super(facade);
    }

    protected ResponseDTO doUpdate(ID id, UpdateDTO dto) {
        return facade.update(id, dto);
    }
}
