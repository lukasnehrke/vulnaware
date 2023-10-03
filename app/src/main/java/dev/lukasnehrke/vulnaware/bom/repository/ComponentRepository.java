package dev.lukasnehrke.vulnaware.bom.repository;

import dev.lukasnehrke.vulnaware.bom.model.Component;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.juli.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ComponentRepository extends JpaRepository<Component, Log>, JpaSpecificationExecutor<Component> {
    Optional<Component> findById(Long id);

    @Query("SELECT c FROM Component c") //TODO
    List<Component> getComponentsForLookup(Date date, Pageable pageable);
}
