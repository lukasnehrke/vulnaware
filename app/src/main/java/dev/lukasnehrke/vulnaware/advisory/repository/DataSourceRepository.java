package dev.lukasnehrke.vulnaware.advisory.repository;

import dev.lukasnehrke.vulnaware.advisory.model.DataSource;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, String> {
    Optional<DataSource> getDataSourceById(String id);
}
