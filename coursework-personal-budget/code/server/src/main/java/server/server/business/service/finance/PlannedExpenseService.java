package server.server.business.service.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.PlannedExpenseType;
import server.server.business.entity.finance.PlannedExpense;
import server.server.business.event.BudgetExceededEvent;
import server.server.business.repository.finance.PlannedExpenseRepository;
import server.server.common.exception.BaseKeyException;
import server.server.common.exception.common.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlannedExpenseService {

    private final PlannedExpenseRepository plannedExpenseRepository;
    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;

    public Page<PlannedExpense> findByProfile(Long profileId, PlannedExpenseType type, Pageable pageable) {
        return type != null
                ? plannedExpenseRepository.findByProfile_IdAndTypeAndDeletedFalse(profileId, type, pageable)
                : plannedExpenseRepository.findByProfile_IdAndDeletedFalse(profileId, pageable);
    }

    public PlannedExpense findById(Long id) {
        PlannedExpense pe = plannedExpenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "PLANNED_EXPENSE_NOT_FOUND", PlannedExpense.class));
        if (Boolean.TRUE.equals(pe.getDeleted())) {
            throw new EntityNotFoundException("id=" + id, "PLANNED_EXPENSE_NOT_FOUND", PlannedExpense.class);
        }
        return pe;
    }

    public PlannedExpense save(PlannedExpense expense) {
        return plannedExpenseRepository.save(expense);
    }

    public void delete(PlannedExpense expense) {
        expense.setDeleted(true);
        plannedExpenseRepository.save(expense);
    }

    public PlannedExpense fulfill(PlannedExpense expense) {
        expense.setIsFulfilled(true);
        return plannedExpenseRepository.save(expense);
    }

    /**
     * Checks if any BUDGET_LIMIT for the given category/period was exceeded.
     * Publishes BudgetExceededEvent if so.
     */
    public void checkBudgetLimits(Long profileId, Long categoryId, LocalDate transactionDate) {
        List<PlannedExpense> limits = plannedExpenseRepository
                .findByProfile_IdAndTypeAndDeletedFalse(profileId, PlannedExpenseType.BUDGET_LIMIT);

        limits.stream()
                .filter(pe -> categoryId == null
                        ? pe.getCategory() == null
                        : pe.getCategory() != null && pe.getCategory().getId().equals(categoryId))
                .forEach(pe -> {
                    LocalDate[] range = getPeriodRange(pe, transactionDate);
                    BigDecimal spent = transactionService.sumExpensesForCategoryAndPeriod(
                            profileId,
                            pe.getCategory() != null ? pe.getCategory().getId() : null,
                            range[0], range[1]);

                    if (spent.compareTo(pe.getAmount()) > 0) {
                        eventPublisher.publishEvent(new BudgetExceededEvent(this, pe, spent));
                    }
                });
    }

    private LocalDate[] getPeriodRange(PlannedExpense pe, LocalDate reference) {
        return switch (pe.getPeriodType()) {
            case MONTHLY -> new LocalDate[]{
                    reference.withDayOfMonth(1),
                    reference.withDayOfMonth(reference.lengthOfMonth())
            };
            case QUARTERLY -> {
                int month = reference.getMonthValue();
                int startMonth = ((month - 1) / 3) * 3 + 1;
                LocalDate start = LocalDate.of(reference.getYear(), startMonth, 1);
                yield new LocalDate[]{start, start.plusMonths(3).minusDays(1)};
            }
            case YEARLY -> new LocalDate[]{
                    LocalDate.of(reference.getYear(), 1, 1),
                    LocalDate.of(reference.getYear(), 12, 31)
            };
        };
    }

    public void assertOwnership(PlannedExpense pe, Long profileId) {
        if (!pe.getProfile().getId().equals(profileId)) {
            throw new PlannedExpenseAccessDeniedException();
        }
    }

    public static class PlannedExpenseAccessDeniedException extends BaseKeyException {
        public PlannedExpenseAccessDeniedException() {
            super("You do not own this planned expense", "PLANNED_EXPENSE_ACCESS_DENIED");
        }
    }
}
