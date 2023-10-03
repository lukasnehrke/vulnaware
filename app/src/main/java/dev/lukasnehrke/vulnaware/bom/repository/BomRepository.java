package dev.lukasnehrke.vulnaware.bom.repository;

import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.project.model.Project;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BomRepository extends CrudRepository<Bom, Long> {
    Page<Bom> findAllByProject(Project project, Pageable pageable);
    Page<Bom> findAllByProjectAndTagContainingIgnoreCase(Project project, String tag, Pageable pageable);
    Optional<Bom> findByProjectAndTag(Project project, String tag);
    Page<Bom> findAllByMonitoring(boolean monitoring, Pageable pageable);
}
