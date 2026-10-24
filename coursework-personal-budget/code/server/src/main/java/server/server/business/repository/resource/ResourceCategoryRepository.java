package server.server.business.repository.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.resource.ResourceCategory;

import java.util.List;
import java.util.Optional;

public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Long> {
    List<ResourceCategory> findAllByDeletedFalse();
    Optional<ResourceCategory> findByNameIgnoreCaseAndDeletedFalse(String name);
    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
}
