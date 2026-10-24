package server.server.business.service.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.GoalStatus;
import server.server.business.entity.shared.SharedGoal;
import server.server.business.repository.shared.SharedGoalRepository;
import server.server.common.exception.common.EntityNotFoundException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class SharedGoalService {

    private final SharedGoalRepository sharedGoalRepository;

    public Page<SharedGoal> findBySheet(Long sheetId, Pageable pageable) {
        return sharedGoalRepository.findBySheet_IdAndDeletedFalse(sheetId, pageable);
    }

    public SharedGoal findById(Long id) {
        SharedGoal goal = sharedGoalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "SHARED_GOAL_NOT_FOUND", SharedGoal.class));
        if (Boolean.TRUE.equals(goal.getDeleted())) {
            throw new EntityNotFoundException("id=" + id, "SHARED_GOAL_NOT_FOUND", SharedGoal.class);
        }
        return goal;
    }

    public SharedGoal save(SharedGoal goal) {
        return sharedGoalRepository.save(goal);
    }

    public void delete(SharedGoal goal) {
        goal.setDeleted(true);
        sharedGoalRepository.save(goal);
    }

    public SharedGoal contribute(SharedGoal goal, BigDecimal amount) {
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }
        return sharedGoalRepository.save(goal);
    }
}
