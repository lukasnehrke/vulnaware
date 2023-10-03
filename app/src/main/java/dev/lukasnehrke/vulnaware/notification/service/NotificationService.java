package dev.lukasnehrke.vulnaware.notification.service;

import dev.lukasnehrke.vulnaware.mail.service.MailService;
import dev.lukasnehrke.vulnaware.notification.model.Notification;
import dev.lukasnehrke.vulnaware.notification.repository.NotificationRepository;
import dev.lukasnehrke.vulnaware.user.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notifications;
    private final MailService mailService;

    public Page<Notification> getNotifications(final User user, final Pageable pageable) {
        return this.notifications.findAllByUser(user, pageable);
    }

    @PostAuthorize("returnObject.user.id == principal.id")
    public Notification getNotification(final Long id) {
        return this.notifications.findById(id).orElseThrow();
    }

    @Transactional
    public void save(final Notification notification) {
        this.notifications.save(notification);

        final var mail = notification.toMail();
        if (mail != null) {
            logger.info("Sending mail '{}' to user '{}'", notification.getType(), notification.getUser().getEmail());
            mailService.sendMail(notification.getUser(), mail);
        }
    }
}
