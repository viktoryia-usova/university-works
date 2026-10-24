package server.server.business.repository.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.resource.FinancialResource;

public interface FinancialResourceRepository extends JpaRepository<FinancialResource, Long> {
    Page<FinancialResource> findAllByDeletedFalse(Pageable pageable);
    Page<FinancialResource> findByResourceCategory_IdAndDeletedFalse(Long categoryId, Pageable pageable);
    Page<FinancialResource> findByTitleContainingIgnoreCaseAndDeletedFalse(String title, Pageable pageable);
    Page<FinancialResource> findByResourceCategory_IdAndTitleContainingIgnoreCaseAndDeletedFalse(
            Long categoryId, String title, Pageable pageable);
}
