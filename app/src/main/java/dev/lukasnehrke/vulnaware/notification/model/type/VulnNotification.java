package dev.lukasnehrke.vulnaware.notification.model.type;

import dev.lukasnehrke.vulnaware.mail.dto.Mail;
import dev.lukasnehrke.vulnaware.notification.model.Notification;
import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.vulnerability.model.Vulnerability;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("vuln")
@EqualsAndHashCode(callSuper = true)
public class VulnNotification extends Notification {

    @ManyToOne
    private Project project;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Vulnerability> vulnerabilities;

    @Transient
    @Override
    public Mail toMail() {
        final String subject = String.format("Project %s has new vulnerabilities", project.getName());
        final String body = String.format(
            """
        Dear %s,

        we detected new vulnerabilities in your project %s.


        """,
            getUser().getEmail(),
            project.getName()
        );

        return new Mail(subject, body);
    }
}
