package server.server.business.notification;

public interface NotificationService {
    void sendGoalReached(String toEmail, String goalName);
    void sendBudgetExceeded(String toEmail, String expenseName);
    void sendContactRequest(String toEmail, String requesterName);
    void sendSharedTransaction(String toEmail, String sheetName, String authorName);
    void sendDepositMatured(String toEmail, String depositName);
}
