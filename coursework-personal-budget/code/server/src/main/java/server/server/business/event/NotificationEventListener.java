package server.server.business.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import server.server.business.entity.finance.Deposit;
import server.server.business.entity.finance.Goal;
import server.server.business.entity.finance.PlannedExpense;
import server.server.business.service.profile.ProfileService;
import server.server.common.service.util.MailService;
import server.server.common.util.log.AppLogger;

/**
 * Listens to domain events and sends email notifications to users
 * when their notification settings allow it.
 *
 * Implements the Observer pattern via Spring ApplicationEvents.
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private static final String LOG = AppLogger.name(NotificationEventListener.class);

    private final MailService   mailService;
    private final ProfileService profileService;

    @EventListener
    public void onGoalReached(GoalReachedEvent event) {
        Goal goal = event.getGoal();
        try {
            var settings = profileService.getNotificationSettings(goal.getProfile().getId());
            if (!Boolean.TRUE.equals(settings.getOnGoalReached())) return;

            String email   = goal.getProfile().getAccount().getUsername();
            String subject = "Цель накопления достигнута!";
            String text    = "Поздравляем! Ваша цель «" + goal.getName() + "» выполнена.\n"
                           + "Накоплено: " + goal.getCurrentAmount() + " " + goal.getCurrency() + ".\n\n"
                           + "С уважением, команда Личный бюджет";
            mailService.send(subject, text, email);
        } catch (Exception e) {
            AppLogger.warn("Failed to send GoalReached notification: " + e.getMessage(), LOG);
        }
    }

    @EventListener
    public void onBudgetExceeded(BudgetExceededEvent event) {
        PlannedExpense pe = event.getPlannedExpense();
        try {
            var settings = profileService.getNotificationSettings(pe.getProfile().getId());
            if (!Boolean.TRUE.equals(settings.getOnBudgetExceeded())) return;

            String email   = pe.getProfile().getAccount().getUsername();
            String subject = "Лимит бюджета превышен";
            String text    = "Внимание! Лимит по статье «" + pe.getName() + "» превышен.\n"
                           + "Установленный лимит: " + pe.getAmount() + " " + pe.getCurrency() + ".\n"
                           + "Фактически потрачено: " + event.getActualSpent() + " BYN.\n\n"
                           + "С уважением, команда Личный бюджет";
            mailService.send(subject, text, email);
        } catch (Exception e) {
            AppLogger.warn("Failed to send BudgetExceeded notification: " + e.getMessage(), LOG);
        }
    }

    @EventListener
    public void onDepositMatured(DepositMaturedEvent event) {
        Deposit deposit = event.getDeposit();
        try {
            var settings = profileService.getNotificationSettings(deposit.getProfile().getId());
            if (!Boolean.TRUE.equals(settings.getOnDepositMatured())) return;

            String email   = deposit.getProfile().getAccount().getUsername();
            String subject = "Ваш вклад завершён";
            String text    = "Вклад «" + deposit.getName() + "» достиг даты окончания и переведён в статус «Завершён».\n"
                           + "Итоговая сумма: " + deposit.getCurrentAmount() + " " + deposit.getCurrency() + ".\n\n"
                           + "С уважением, команда Личный бюджет";
            mailService.send(subject, text, email);
        } catch (Exception e) {
            AppLogger.warn("Failed to send DepositMatured notification: " + e.getMessage(), LOG);
        }
    }
}
