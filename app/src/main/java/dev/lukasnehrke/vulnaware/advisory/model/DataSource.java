package dev.lukasnehrke.vulnaware.advisory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Entity
@Table(name = "va_data_sources")
public class DataSource {

    @Id
    private String id;

    @Nullable
    @Column(name = "cursor")
    private Integer cursor;

    @Nullable
    @Column(name = "etag")
    private String etag;

    @Nullable
    @Column(name = "last_synchronized")
    private OffsetDateTime lastSynchronized;
}
