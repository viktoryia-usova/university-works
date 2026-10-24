package server.server.business.notification;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import server.server.business.entity.profile.NotificationSettings;
import server.server.business.entity.profile.Profile;
import server.server.business.event.*;
import server.server.business.repository.profile.NotificationSettingsRepository;

/**
 * Observer pattern: listens to domain events published via {@code ApplicationEventPublisher}
 * and dispatches email notifications based on each user's notification preferences.
 */
@Component
public class NotificationListener {

    private final NotificationService              notificationService;
    private final NotificationSettingsRepository   settingsRepository;

    public NotificationListener(NotificationService notificationService,
                                NotificationSettingsRepository settingsRepository) {
        this.notificationService = notificationService;
        this.settingsRepository  = settingsRepository;
    }

    @Async
    @EventListener
    public void onGoalReached(GoalReachedEvent event) {
        Profile profile = event.getGoal().getProfile();
        settingsRepository.findByProfile_Id(profile.getId())
                .filter(NotificationSettings::getOnGoalReached)
                .ifPresent(s -> notificationService.sendGoalReached(
                        profile.getAccount().getUsername(),
                        event.getGoal().getName()));
    }

    @Async
    @EventListener
    public void onBudgetExceeded(BudgetExceededEvent event) {
        Profile profile = event.getPlannedExpense().getProfile();
        settingsRepository.findByProfile_Id(profile.getId())
                .filter(NotificationSettings::getOnBudgetExceeded)
                .ifPresent(s -> notificationService.sendBudgetExceeded(
                        profile.getAccount().getUsername(),
                        event.getPlannedExpense().getName()));
    }

    @Async
    @EventListener
    public void onContactRequest(ContactRequestEvent event) {
        Profile addressee = event.getContact().getAddressee();
        settingsRepository.findByProfile_Id(addressee.getId())
                .filter(NotificationSettings::getOnContactRequest)
                .ifPresent(s -> {
                    Profile requester = event.getContact().getRequester();
                    notificationService.sendContactRequest(
                            addressee.getAccount().getUsername(),
                            requester.getFirstName() + " " + requester.getLastName());
                });
    }

    @Async
    @EventListener
    public void onSharedTransaction(SharedTransactionAddedEvent event) {
        Profile author = event.getTransaction().getMember().getProfile();
        String authorName = author.getFirstName() + " " + author.getLastName();
        String sheetName  = event.getSheet().getName();

        // Notify all members except the author
        event.getSheet().getMembers().stream()
                .map(m -> m.getProfile())
                .filter(p -> !p.getId().equals(author.getId()))
                .forEach(p -> settingsRepository.findByProfile_Id(p.getId())
                        .filter(NotificationSettings::getOnSharedTransaction)
                        .ifPresent(s -> notificationService.sendSharedTransaction(
                                p.getAccount().getUsername(), sheetName, authorName)));
    }

    @Async
    @EventListener
    public void onDepositMatured(DepositMaturedEvent event) {
        Profile profile = event.getDeposit().getProfile();
        settingsRepository.findByProfile_Id(profile.getId())
                .filter(NotificationSettings::getOnDepositMatured)
                .ifPresent(s -> notificationService.sendDepositMatured(
                        profile.getAccount().getUsername(),
                        event.getDeposit().getName()));
    }
}
