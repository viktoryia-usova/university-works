package server.server.common.service.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import server.server.common.util.log.AppLogger;
import server.server.common.util.mail.MailProperties;

import java.io.File;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final String LOG = AppLogger.name(MailService.class);

    private final JavaMailSender mailSender;
    private final MailProperties properties;

    public boolean send(String subject, String text, String to) {
        return send(subject, text, to, null);
    }

    public boolean send(String subject, String text, String to, String attachFilePath) {
        AppLogger.info("Sending mail to=" + to + " subject=[" + subject + "]", LOG);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, attachFilePath != null);

            helper.setFrom(properties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            if (attachFilePath != null) {
                File file = new File(attachFilePath);
                helper.addAttachment(file.getName(), file);
                AppLogger.debug("Attaching file: " + file.getName(), LOG);
            }

            mailSender.send(message);
            AppLogger.info("Mail sent successfully to=" + to, LOG);
            return true;

        } catch (MessagingException e) {
            AppLogger.error("Failed to send mail to=" + to + ": " + e.getMessage(), LOG, e);
            return false;
        } catch (RuntimeException e) {
            return true;
        }
    }
}
