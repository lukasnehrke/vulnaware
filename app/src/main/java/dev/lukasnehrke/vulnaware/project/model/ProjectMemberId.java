package dev.lukasnehrke.vulnaware.project.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class ProjectMemberId implements Serializable {

    private Long user;
    private Long project;
}
