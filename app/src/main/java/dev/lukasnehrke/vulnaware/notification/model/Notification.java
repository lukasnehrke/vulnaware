package dev.lukasnehrke.vulnaware.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import dev.lukasnehrke.vulnaware.mail.dto.Mail;
import dev.lukasnehrke.vulnaware.user.model.User;
import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.Nullable;

@Data
@Entity
@Table(name = "va_notifications")
@Inheritance
@DiscriminatorColumn(name = "type")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /* --- Relationships --- */

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /* --- Columns --- */

    @Column(nullable = false, insertable = false, updatable = false)
    private String type;

    /** If the notification has been read by the user. */
    @Column(nullable = false)
    private boolean read;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Nullable
    @Transient
    public Mail toMail() {
        return null;
    }
}
