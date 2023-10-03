package dev.lukasnehrke.vulnaware.notification.repository;

import dev.lukasnehrke.vulnaware.notification.model.Notification;
import dev.lukasnehrke.vulnaware.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByUser(User user, Pageable pageable);
    int countAllByUserAndRead(User user, boolean read);
}
