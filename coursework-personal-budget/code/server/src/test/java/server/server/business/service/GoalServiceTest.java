package server.server.business.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import server.server.business.entity.enums.GoalStatus;
import server.server.business.entity.finance.Goal;
import server.server.business.event.GoalReachedEvent;
import server.server.business.repository.finance.GoalRepository;
import server.server.business.service.finance.GoalService;
import server.server.common.exception.common.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private GoalRepository goalRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private GoalService goalService;

    // ---- findById ----

    @Test
    void findById_existingId_returnsGoal() {
        Goal goal = new Goal();
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertSame(goal, goalService.findById(1L));
    }

    @Test
    void findById_missingId_throwsEntityNotFoundException() {
        when(goalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.findById(99L));
    }

    // ---- save ----

    @Test
    void save_delegatesToRepository() {
        Goal goal = new Goal();
        when(goalRepository.save(goal)).thenReturn(goal);

        assertSame(goal, goalService.save(goal));
        verify(goalRepository).save(goal);
    }

    // ---- delete (soft) ----

    @Test
    void delete_setsDeletedFlagAndSaves() {
        Goal goal = new Goal();
        goal.setDeleted(false);
        when(goalRepository.save(goal)).thenReturn(goal);

        goalService.delete(goal);

        assertTrue(goal.getDeleted());
        verify(goalRepository).save(goal);
    }

    // ---- contribute ----

    @Test
    void contribute_belowTarget_updatesAmountWithoutEvent() {
        Goal goal = Goal.builder()
                .currentAmount(BigDecimal.valueOf(100))
                .targetAmount(BigDecimal.valueOf(500))
                .status(GoalStatus.ACTIVE)
                .build();
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.contribute(goal, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(300), result.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, result.getStatus());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void contribute_exactlyAtTarget_completesGoalAndPublishesEvent() {
        Goal goal = Goal.builder()
                .currentAmount(BigDecimal.valueOf(300))
                .targetAmount(BigDecimal.valueOf(500))
                .status(GoalStatus.ACTIVE)
                .build();
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.contribute(goal, BigDecimal.valueOf(200));

        assertEquals(GoalStatus.COMPLETED, result.getStatus());
        ArgumentCaptor<GoalReachedEvent> captor = ArgumentCaptor.forClass(GoalReachedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertSame(result, captor.getValue().getGoal());
    }

    @Test
    void contribute_exceedingTarget_completesGoalAndPublishesEvent() {
        Goal goal = Goal.builder()
                .currentAmount(BigDecimal.valueOf(0))
                .targetAmount(BigDecimal.valueOf(100))
                .status(GoalStatus.ACTIVE)
                .build();
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        goalService.contribute(goal, BigDecimal.valueOf(999));

        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
        verify(eventPublisher).publishEvent(any(GoalReachedEvent.class));
    }

    // ---- assertOwnership ----

    @Test
    void assertOwnership_correctOwner_doesNotThrow() {
        var profile = server.server.business.entity.profile.Profile.builder().build();
        profile.setId(42L);
        Goal goal = Goal.builder().profile(profile).build();

        assertDoesNotThrow(() -> goalService.assertOwnership(goal, 42L));
    }

    @Test
    void assertOwnership_wrongOwner_throwsAccessDenied() {
        var profile = server.server.business.entity.profile.Profile.builder().build();
        profile.setId(42L);
        Goal goal = Goal.builder().profile(profile).build();

        assertThrows(GoalService.GoalAccessDeniedException.class,
                () -> goalService.assertOwnership(goal, 99L));
    }
}
