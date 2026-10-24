package server.server.common.service.common;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import server.server.common.entity.classes.BaseEntity;
import server.server.common.exception.common.EntityNotFoundException;
import server.server.common.util.log.AppLogger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
@Transactional(propagation = Propagation.REQUIRED)
@AllArgsConstructor
public abstract class BaseCRUDService <Type extends BaseEntity, ID , Repository extends JpaRepository<Type, ID>> {
    protected final Repository repository;
    protected final Class<Type> entityClass;

    public Type create(Type entity) {
        AppLogger.debug("Creating " + entityClass.getSimpleName(), entityClass.getSimpleName());
        return repository.save(entity);
    }

    public Type update(Type entity) {
        AppLogger.debug("Updating " + entityClass.getSimpleName(), entityClass.getSimpleName());
        return repository.save(entity);
    }

    public List<Type> findAll() {
        AppLogger.debug("FindAll " + entityClass.getSimpleName(), entityClass.getSimpleName());
        return repository.findAll();
    }

    public Page<Type> findAll(Pageable pageable) {
        AppLogger.debug("FindAll pageable " + entityClass.getSimpleName(), entityClass.getSimpleName());
        return repository.findAll(pageable);
    }

    public Type findById(ID id) {
        AppLogger.debug("FindById " + entityClass.getSimpleName() + " id=" + id, entityClass.getSimpleName());
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("id = " + id, "ID", entityClass));
    }

    public Type findById(ID id, String errorKey) {
        AppLogger.debug("FindById " + entityClass.getSimpleName() + " id=" + id + " (as relation)", entityClass.getSimpleName());
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("id = " + id, errorKey, entityClass));
    }

    public abstract void deleteById(ID id);
}
