package dev.lukasnehrke.vulnaware.notification.controller;

import com.fasterxml.jackson.annotation.JsonView;
import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.notification.model.Notification;
import dev.lukasnehrke.vulnaware.notification.repository.NotificationRepository;
import dev.lukasnehrke.vulnaware.notification.service.NotificationService;
import dev.lukasnehrke.vulnaware.security.UserDetailsImpl;
import dev.lukasnehrke.vulnaware.user.CurrentUser;
import dev.lukasnehrke.vulnaware.user.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class NotificationController {

    private final UserRepository users;
    private final NotificationRepository notifications;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    @JsonView(Bom.PublicView.class)
    public ResponseEntity<Page<Notification>> getNotifications(
        @CurrentUser final UserDetailsImpl userDetails,
        @RequestParam(defaultValue = "0") final int page,
        @RequestParam(defaultValue = "10") final int size
    ) {
        final var user = users.getReferenceById(userDetails.getId());
        final var sort = Sort.by("createdAt").descending();
        final var paging = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(notificationService.getNotifications(user, paging));
    }

    @GetMapping("/notifications/unread")
    public ResponseEntity<?> getUnread(@CurrentUser final UserDetailsImpl userDetails) {
        final var user = users.getReferenceById(userDetails.getId());
        final var unread = notifications.countAllByUserAndRead(user, false);

        /* create response */
        final Map<String, Object> response = new HashMap<>();
        response.put("unread", unread);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/notifications/{id}")
    @JsonView(Bom.PublicView.class)
    public ResponseEntity<?> getNotification(@PathVariable final Long id) {
        final var notification = notificationService.getNotification(id);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PutMapping("/notifications/{id}")
    @JsonView(Bom.PublicView.class)
    public ResponseEntity<?> updateNotification(@PathVariable final Long id, @RequestBody final Notification update) {
        var notification = notificationService.getNotification(id);
        notification.setRead(update.isRead());
        notification = notifications.save(notification);

        return new ResponseEntity<>(notification, HttpStatus.OK);
    }
}
