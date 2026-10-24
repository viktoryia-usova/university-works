package server.server.business.repository.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.enums.PlannedExpenseType;
import server.server.business.entity.finance.PlannedExpense;

import java.util.List;

public interface PlannedExpenseRepository extends JpaRepository<PlannedExpense, Long> {
    Page<PlannedExpense> findByProfile_IdAndDeletedFalse(Long profileId, Pageable pageable);
    Page<PlannedExpense> findByProfile_IdAndTypeAndDeletedFalse(Long profileId, PlannedExpenseType type, Pageable pageable);
    List<PlannedExpense> findByProfile_IdAndTypeAndDeletedFalse(Long profileId, PlannedExpenseType type);
}
