package dev.lukasnehrke.vulnaware.bom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import dev.lukasnehrke.vulnaware.bom.dto.ComponentRisk;
import dev.lukasnehrke.vulnaware.vulnerability.model.Vulnerability;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Entity
@Table(name = "va_components")
@JsonView(Component.InternalView.class)
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonView({ PublicView.class, ListView.class })
    private Long id;

    /* --- Relationships --- */

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private Bom bom;

    @JsonProperty("package")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id")
    @JsonView({ PublicView.class, ListView.class })
    private Package pkg;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Component> dependencies;

    @ManyToMany(mappedBy = "components", fetch = FetchType.EAGER)
    private List<Vulnerability> vulnerabilities;

    /* --- Columns --- */

    /** The ecosystem of this package, e.g. "NPM" */
    @Column(name = "ecosystem", nullable = false)
    @JsonView({ PublicView.class, ListView.class })
    private Ecosystem ecosystem;

    /** The name of this package, e.g. "@jvalue/jayvee" */
    @Column(name = "name", nullable = false)
    @JsonView({ PublicView.class, ListView.class })
    private String name;

    /** ID of this component in the original bom. */
    @Nullable
    @Column(name = "bom_ref", nullable = true)
    private String bomRef;

    /** The version identifier for this package, e.g. "1.0.0-SNAPSHOT" */
    @Nullable
    @Column(name = "version")
    @JsonView({ PublicView.class, ListView.class })
    private String version;

    /** True if this is a direct dependency. */
    @Column(name = "direct", nullable = false)
    private boolean direct = false;

    @Nullable
    @Column(name = "purl", length = 2048)
    @JsonView({ PublicView.class, ListView.class })
    private String packageURL;

    @Nullable
    @Column(name = "cpe", length = 2048)
    @JsonView({ PublicView.class, ListView.class })
    private String cpe;

    /** Version to fix current vulnerabilities. */
    @Nullable
    @Column(name = "fixed_version")
    @JsonView({ PublicView.class, ListView.class })
    private String fixedVersion;

    /* -- Columns for vulnerability matching -- */

    @Column(name = "osv_ecosystem")
    private String osvEcosystem;

    @Column(name = "osv_name")
    private String osvName;

    @Column(name = "cpe_vendor")
    private String cpeVendor;

    @Column(name = "cpe_product")
    private String cpeProduct;

    @Column(name = "cpe_version")
    private String cpeVersion;

    @JsonProperty("hasDependencies")
    @JsonView({ ListView.class, PublicView.class })
    public boolean hasDependencies() {
        return this.dependencies != null && !this.dependencies.isEmpty();
    }

    @Nullable
    @Transient
    @JsonView({ ListView.class, PublicView.class })
    public ComponentRisk getRisk() {
        return vulnerabilities
            .stream()
            .filter(v -> v.getAdvisory().getRisk() != null)
            .map(v -> new ComponentRisk(v.getId(), v.getAdvisory().getRisk()))
            .max(ComponentRisk::compareTo)
            .orElse(null);
    }

    public interface PublicView {}

    public interface ListView {}

    public interface InternalView {}
}
