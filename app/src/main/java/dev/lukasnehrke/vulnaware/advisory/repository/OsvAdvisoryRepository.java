package dev.lukasnehrke.vulnaware.advisory.repository;

import dev.lukasnehrke.vulnaware.advisory.model.OsvAdvisory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OsvAdvisoryRepository extends CrudRepository<OsvAdvisory, String> {}
