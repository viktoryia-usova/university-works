package server.server.common.service.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import server.server.common.entity.classes.BaseSoftDeletingEntity;
import server.server.common.util.log.AppLogger;
@Transactional(propagation = Propagation.REQUIRED)
public abstract class BaseCRUDSoftDeletingService<Type extends BaseSoftDeletingEntity, ID , Repository extends JpaRepository<Type, ID>> extends BaseCRUDService<Type, ID, Repository> {
    public BaseCRUDSoftDeletingService(Repository repository, Class<Type> entityClass) {
        super(repository, entityClass);
    }
    @Override
    public void deleteById(ID id) {
        AppLogger.info("Soft deleting " + entityClass.getSimpleName() + " id=" + id, entityClass.getSimpleName());
        Type entity = findById(id);
        entity.setDeleted(true);
        update(entity);
    }
}
