package dev.lukasnehrke.vulnaware.activity.repository;

import dev.lukasnehrke.vulnaware.activity.model.Activity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByProjectId(Long id, Pageable pageable);
}
