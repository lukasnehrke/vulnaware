package dev.lukasnehrke.vulnaware.bom.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Entity
@Table(name = "va_packages")
public class Package {

    @Id
    private String id;

    @CollectionTable(name = "va_packages_links")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<PackageLink> links;

    @CollectionTable(name = "va_packages_licenses")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> licenses;

    /** The latest version of this package. */
    @Nullable
    @Column(name = "latest_version")
    private String latestVersion;

    /** When the last lookup happened. */
    @Column(name = "last_lookup_at")
    private Date lastLookupAt;

    @Column(name = "invalid", nullable = false)
    private boolean isInvalid;
}
