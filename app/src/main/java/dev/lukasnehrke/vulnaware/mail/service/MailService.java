package dev.lukasnehrke.vulnaware.mail.service;

import dev.lukasnehrke.vulnaware.mail.dto.Mail;
import dev.lukasnehrke.vulnaware.user.model.User;

public interface MailService {
    void sendMail(final User user, final Mail mail);
}
