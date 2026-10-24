package server.server.business.repository.shared;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.enums.GoalStatus;
import server.server.business.entity.shared.SharedGoal;

import java.util.List;

public interface SharedGoalRepository extends JpaRepository<SharedGoal, Long> {
    Page<SharedGoal> findBySheet_IdAndDeletedFalse(Long sheetId, Pageable pageable);
    List<SharedGoal> findBySheet_IdAndStatusAndDeletedFalse(Long sheetId, GoalStatus status);
}
