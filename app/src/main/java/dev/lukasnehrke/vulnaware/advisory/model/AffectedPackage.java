package dev.lukasnehrke.vulnaware.advisory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AffectedPackage {

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
}
