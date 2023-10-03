package dev.lukasnehrke.vulnaware.bom.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Embeddable
@AllArgsConstructor
public class PackageLink {

    private String label;
    private String url;

    public PackageLink() {}
}
