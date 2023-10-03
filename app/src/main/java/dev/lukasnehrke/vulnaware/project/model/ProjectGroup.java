package dev.lukasnehrke.vulnaware.project.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

/**
 * A {@link ProjectGroup} is a collection of projects.
 * <br>
 * TODO: Not implemented
 */
@Data
@Entity
@Table(name = "va_project_groups")
public class ProjectGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    // --- Relationships ---

    @OneToMany(fetch = FetchType.EAGER)
    private List<Project> projects;

    // --- Columns ---

    @Column(name = "name", nullable = false)
    private String name;
}
