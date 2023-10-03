package dev.lukasnehrke.vulnaware.mail.service;

import dev.lukasnehrke.vulnaware.mail.dto.Mail;
import dev.lukasnehrke.vulnaware.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "mail.enabled", havingValue = "false", matchIfMissing = true)
public class DummyMailService implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(DummyMailService.class);

    @Override
    public void sendMail(User user, Mail mail) {
        logger.info("--> DummyMail to {} with subject '{}'", user.getEmail(), mail.subject());
    }
}
