package server.server.business.repository.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.enums.GoalStatus;
import server.server.business.entity.finance.Goal;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Page<Goal> findByProfile_IdAndDeletedFalse(Long profileId, Pageable pageable);
    Page<Goal> findByProfile_IdAndStatusAndDeletedFalse(Long profileId, GoalStatus status, Pageable pageable);
}
