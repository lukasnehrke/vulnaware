package dev.lukasnehrke.vulnaware.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

@Data
@Entity
@Table(name = "va_advisories")
@Inheritance
@DiscriminatorColumn(name = "type")
public abstract class Advisory {

    @Id
    private String id;

    /* --- Relationships --- */

    @Nullable
    @ManyToOne(fetch = FetchType.EAGER)
    private CWE cwe;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(
        name = "va_advisories_affected_packages",
        indexes = { @Index(columnList = "osv_ecosystem, osv_name"), @Index(columnList = "cpe_vendor, cpe_product, cpe_version") }
    )
    private Set<AffectedPackage> affectedPackages;

    /* --- Columns --- */

    @Column(name = "type", nullable = false, insertable = false, updatable = false)
    private String type;

    @Column(name = "withdrawn", nullable = false)
    private boolean withdrawn;

    @Nullable
    @Column(name = "risk")
    private Double risk;

    @Nullable
    @Column(name = "summary")
    private String summary;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    public abstract List<Component> isAffected(final List<Component> components);
}
