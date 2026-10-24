package server.server.business.repository.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.enums.CategoryType;
import server.server.business.entity.finance.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findByProfile_IdAndDeletedFalse(Long profileId, Pageable pageable);
    Page<Category> findByProfile_IdAndTypeAndDeletedFalse(Long profileId, CategoryType type, Pageable pageable);
    List<Category> findByProfile_IdAndDeletedFalse(Long profileId);
    boolean existsByProfile_IdAndNameIgnoreCaseAndTypeAndDeletedFalse(Long profileId, String name, CategoryType type);
}
