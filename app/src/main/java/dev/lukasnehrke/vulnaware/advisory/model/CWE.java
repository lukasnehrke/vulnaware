package dev.lukasnehrke.vulnaware.advisory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Entity
@Table(name = "va_cwe")
public class CWE {

    @Id
    private String id;

    @Nullable
    @Column(name = "name")
    private String name;
}
