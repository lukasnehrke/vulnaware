package dev.lukasnehrke.vulnaware.project.repository;

import dev.lukasnehrke.vulnaware.project.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    List<Project> findAll();
    List<Project> findAllByNameContainsIgnoreCaseOrSlugIgnoreCase(String name, String slug);
    Optional<Project> findBySlug(String slug);

    @Modifying
    @Query("UPDATE Project p SET p.main.id = ?2 WHERE p.id = ?1")
    void setMainBom(Long projectId, Long bomId);
}
