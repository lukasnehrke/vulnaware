package dev.lukasnehrke.vulnaware.bom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Setter
@Getter
@Embeddable
public class History {

    @Column(name = "components", nullable = false)
    private int components;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date date;
}
