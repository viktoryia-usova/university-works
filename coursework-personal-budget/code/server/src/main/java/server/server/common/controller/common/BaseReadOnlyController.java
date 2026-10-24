package server.server.common.controller.common;

import lombok.AllArgsConstructor;
import server.server.common.dto.util.MessageResponse;
import server.server.common.entity.classes.BaseEntity;
import server.server.common.facade.BaseReadOnlyFacade;

import java.util.List;

@AllArgsConstructor
public abstract class BaseReadOnlyController<
        Entity extends BaseEntity,
        ID,
        ResponseDTO,
        Facade extends BaseReadOnlyFacade<Entity, ID, ?, ?, ResponseDTO>>
{
    protected final Facade facade;

    protected ResponseDTO doFindById(ID id) {
        return facade.findById(id);
    }
    protected ResponseDTO doFindByIdWithRelations(ID id) {
        return facade.findByIdWithRelations(id);
    }
    protected List<ResponseDTO> doFindAll() {
        return facade.findAll();
    }
    protected List<ResponseDTO> doFindAllWithRelations() {
        return facade.findAllWithRelations();
    }
    protected MessageResponse doDeleteById(ID id){
        facade.deleteById(id);
        return new MessageResponse("Entity successfully deleted");
    }
    protected List<ResponseDTO> doFindAllByAccountId(Long accountId) {
        return facade.findAllByAccountId(accountId);
    }
    protected ResponseDTO doFindByAccountId(Long accountId) {
        return facade.findByAccountId(accountId);
    }
}
