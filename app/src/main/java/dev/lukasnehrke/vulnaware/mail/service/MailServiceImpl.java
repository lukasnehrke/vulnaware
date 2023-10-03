package dev.lukasnehrke.vulnaware.mail.service;

import dev.lukasnehrke.vulnaware.mail.dto.Mail;
import dev.lukasnehrke.vulnaware.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mail.enabled")
public class MailServiceImpl implements MailService {

    private static final String FROM = "noreply@lukasnehrke.dev";
    private final JavaMailSender mailSender;

    @Async
    public void sendMail(final User user, final Mail mail) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(user.getEmail());
        message.setSubject(mail.subject());
        message.setText(mail.message());
        mailSender.send(message);
    }
}
