package server.server.business.service.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.GoalStatus;
import server.server.business.entity.finance.Goal;
import server.server.business.entity.profile.Profile;
import server.server.business.event.GoalReachedEvent;
import server.server.business.repository.finance.GoalRepository;
import server.server.common.exception.BaseKeyException;
import server.server.common.exception.common.EntityNotFoundException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<Goal> findByProfile(Long profileId, GoalStatus status, Pageable pageable) {
        return status != null
                ? goalRepository.findByProfile_IdAndStatusAndDeletedFalse(profileId, status, pageable)
                : goalRepository.findByProfile_IdAndDeletedFalse(profileId, pageable);
    }

    public Goal findById(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "GOAL_NOT_FOUND", Goal.class));
        if (Boolean.TRUE.equals(goal.getDeleted())) {
            throw new EntityNotFoundException("id=" + id, "GOAL_NOT_FOUND", Goal.class);
        }
        return goal;
    }

    public Goal save(Goal goal) {
        return goalRepository.save(goal);
    }

    public void delete(Goal goal) {
        goal.setDeleted(true);
        goalRepository.save(goal);
    }

    /**
     * Adds amount to currentAmount; publishes GoalReachedEvent if target is met.
     * Demonstrates Observer pattern (Spring ApplicationEvents).
     */
    public Goal contribute(Goal goal, BigDecimal amount) {
        BigDecimal newAmount = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(newAmount);

        if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
            goal = goalRepository.save(goal);
            eventPublisher.publishEvent(new GoalReachedEvent(this, goal));
        } else {
            goal = goalRepository.save(goal);
        }
        return goal;
    }

    /**
     * Recalculates goal status after the target amount or accumulated amount changes.
     * <ul>
     *   <li>ACTIVE → COMPLETED when {@code currentAmount >= targetAmount} (fires GoalReachedEvent).</li>
     *   <li>COMPLETED → ACTIVE when {@code currentAmount < targetAmount} (target was raised).</li>
     *   <li>CANCELLED goals are never touched.</li>
     * </ul>
     */
    public void recalculateStatus(Goal goal) {
        if (goal.getStatus() == GoalStatus.CANCELLED) return;
        boolean reached = goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0;
        if (reached && goal.getStatus() == GoalStatus.ACTIVE) {
            goal.setStatus(GoalStatus.COMPLETED);
            eventPublisher.publishEvent(new GoalReachedEvent(this, goal));
        } else if (!reached && goal.getStatus() == GoalStatus.COMPLETED) {
            goal.setStatus(GoalStatus.ACTIVE);
        }
    }

    public void assertOwnership(Goal goal, Long profileId) {
        if (!goal.getProfile().getId().equals(profileId)) {
            throw new GoalAccessDeniedException();
        }
    }

    public static class GoalAccessDeniedException extends BaseKeyException {
        public GoalAccessDeniedException() {
            super("You do not own this goal", "GOAL_ACCESS_DENIED");
        }
    }
}
