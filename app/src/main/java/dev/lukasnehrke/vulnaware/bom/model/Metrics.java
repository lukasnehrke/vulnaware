package dev.lukasnehrke.vulnaware.bom.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import java.util.Map;
import lombok.Data;

@Data
@Embeddable
public class Metrics {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "va_boms_ecosystems", joinColumns = @JoinColumn(name = "bom_id"))
    @Enumerated(EnumType.STRING)
    private Map<Ecosystem, Integer> dependencyCount;

    @Column(name = "updates_count", nullable = false)
    private Integer updatesCount = 0;

    @Column(name = "risk_low_count", nullable = false)
    private Integer riskLowCount = 0;

    @Column(name = "risk_medium_count", nullable = false)
    private Integer riskMediumCount = 0;

    @Column(name = "risk_high_count", nullable = false)
    private Integer riskHighCount = 0;

    @Column(name = "risk_critical_count", nullable = false)
    private Integer riskCriticalCount = 0;

    @Transient
    public Integer getTotalDependencies() {
        return dependencyCount.values().stream().mapToInt(Integer::intValue).sum();
    }
}
