package server.server.business.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Decorator pattern: wraps the base {@link EmailNotificationService} with structured logging.
 * Marked {@code @Primary} so Spring injects this wherever {@link NotificationService} is required.
 */
@Component
@Primary
public class NotificationDecorator implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationDecorator.class);

    private final NotificationService delegate;

    public NotificationDecorator(@Qualifier("emailNotificationService") NotificationService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void sendGoalReached(String toEmail, String goalName) {
        log.info("Sending GOAL_REACHED notification to={} goal='{}'", toEmail, goalName);
        try {
            delegate.sendGoalReached(toEmail, goalName);
            log.info("GOAL_REACHED notification sent successfully to={}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send GOAL_REACHED notification to={}: {}", toEmail, ex.getMessage());
        }
    }

    @Override
    public void sendBudgetExceeded(String toEmail, String expenseName) {
        log.info("Sending BUDGET_EXCEEDED notification to={} expense='{}'", toEmail, expenseName);
        try {
            delegate.sendBudgetExceeded(toEmail, expenseName);
            log.info("BUDGET_EXCEEDED notification sent successfully to={}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send BUDGET_EXCEEDED notification to={}: {}", toEmail, ex.getMessage());
        }
    }

    @Override
    public void sendContactRequest(String toEmail, String requesterName) {
        log.info("Sending CONTACT_REQUEST notification to={} from='{}'", toEmail, requesterName);
        try {
            delegate.sendContactRequest(toEmail, requesterName);
            log.info("CONTACT_REQUEST notification sent successfully to={}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send CONTACT_REQUEST notification to={}: {}", toEmail, ex.getMessage());
        }
    }

    @Override
    public void sendSharedTransaction(String toEmail, String sheetName, String authorName) {
        log.info("Sending SHARED_TRANSACTION notification to={} sheet='{}' author='{}'", toEmail, sheetName, authorName);
        try {
            delegate.sendSharedTransaction(toEmail, sheetName, authorName);
            log.info("SHARED_TRANSACTION notification sent successfully to={}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send SHARED_TRANSACTION notification to={}: {}", toEmail, ex.getMessage());
        }
    }

    @Override
    public void sendDepositMatured(String toEmail, String depositName) {
        log.info("Sending DEPOSIT_MATURED notification to={} deposit='{}'", toEmail, depositName);
        try {
            delegate.sendDepositMatured(toEmail, depositName);
            log.info("DEPOSIT_MATURED notification sent successfully to={}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send DEPOSIT_MATURED notification to={}: {}", toEmail, ex.getMessage());
        }
    }
}
