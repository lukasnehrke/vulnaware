package dev.lukasnehrke.vulnaware.bom.model;

import dev.lukasnehrke.vulnaware.storage.model.Asset;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Embeddable
public class Annotation {

    @Column(name = "label", nullable = false)
    private String label;

    @OneToOne(fetch = FetchType.EAGER)
    private Asset asset;
}
