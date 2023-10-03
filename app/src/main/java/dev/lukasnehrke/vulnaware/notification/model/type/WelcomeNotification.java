package dev.lukasnehrke.vulnaware.notification.model.type;

import dev.lukasnehrke.vulnaware.mail.dto.Mail;
import dev.lukasnehrke.vulnaware.notification.model.Notification;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("welcome")
public class WelcomeNotification extends Notification {

    @Transient
    @Override
    public Mail toMail() {
        final String subject = "Welcome to VulnAware!";
        final String body = String.format(
            """
        Dear %s,

        thank you for registering! Create a new project to get started.
        """,
            getUser().getName()
        );

        return new Mail(subject, body);
    }
}
