package server.server.business.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendGoalReached(String toEmail, String goalName) {
        send(toEmail,
                "Цель достигнута!",
                "Поздравляем! Вы достигли своей цели: \"" + goalName + "\".");
    }

    @Override
    public void sendBudgetExceeded(String toEmail, String expenseName) {
        send(toEmail,
                "Превышение бюджета",
                "Бюджетный лимит \"" + expenseName + "\" превышен в текущем периоде.");
    }

    @Override
    public void sendContactRequest(String toEmail, String requesterName) {
        send(toEmail,
                "Новый запрос в контакты",
                requesterName + " отправил(а) вам запрос на добавление в контакты.");
    }

    @Override
    public void sendSharedTransaction(String toEmail, String sheetName, String authorName) {
        send(toEmail,
                "Новая транзакция в общем листе",
                authorName + " добавил(а) транзакцию в общий лист \"" + sheetName + "\".");
    }

    @Override
    public void sendDepositMatured(String toEmail, String depositName) {
        send(toEmail,
                "Депозит завершён",
                "Срок вашего депозита \"" + depositName + "\" истёк. Начисленный доход добавлен в ваши транзакции.");
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
