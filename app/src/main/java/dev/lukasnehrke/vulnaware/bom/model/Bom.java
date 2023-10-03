package dev.lukasnehrke.vulnaware.bom.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.storage.model.Asset;
import dev.lukasnehrke.vulnaware.vulnerability.model.Vulnerability;
import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

@Getter
@Setter
@Entity
@Table(name = "va_boms", uniqueConstraints = { @UniqueConstraint(columnNames = { "project_id", "tag" }) })
@JsonView(Bom.InternalView.class)
public class Bom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonView(Bom.PublicView.class)
    private Long id;

    /* --- Relationships --- */

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private Project project;

    @OneToMany(mappedBy = "bom", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Component> components;

    @OneToMany(mappedBy = "bom", fetch = FetchType.LAZY)
    private List<Vulnerability> vulnerabilities;

    @Nullable
    @OneToOne(fetch = FetchType.EAGER)
    private Asset asset;

    @Embedded
    @JsonView(Bom.PublicView.class)
    private Metrics metrics;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "va_bom_history", joinColumns = @JoinColumn(name = "bom_id"))
    @JsonView(Bom.PublicView.class)
    private List<History> history;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "va_bom_annotations", joinColumns = @JoinColumn(name = "bom_id"))
    private List<Annotation> annotations = new ArrayList<>();

    /* --- Columns --- */

    /** Unique tag of this bom. */
    @Column(name = "tag", nullable = false)
    @JsonView(Bom.PublicView.class)
    private String tag;

    /** Format of the original bom. */
    @Nullable
    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    @JsonView(Bom.PublicView.class)
    private BomFormat format;

    /** Data format of the original bom. */
    @Nullable
    @Column(name = "data_format")
    @Enumerated(EnumType.STRING)
    @JsonView(Bom.PublicView.class)
    private DataFormat dataFormat;

    /** Format version of the original bom. */
    @Nullable
    @Column(name = "format_version")
    @JsonView(Bom.PublicView.class)
    private String formatVersion;

    @Nullable
    @Column(name = "description", length = 1024)
    @JsonView(Bom.PublicView.class)
    private String description;

    @Column(name = "monitoring", nullable = false)
    @JsonView(Bom.PublicView.class)
    private boolean monitoring = true;

    @Nullable
    @Column(name = "last_analyzed_at")
    private Date lastAnalyzedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;

    public interface PublicView {}

    public interface InternalView {}
}
