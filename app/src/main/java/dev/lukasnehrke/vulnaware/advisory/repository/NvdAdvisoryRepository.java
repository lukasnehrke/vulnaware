package dev.lukasnehrke.vulnaware.advisory.repository;

import dev.lukasnehrke.vulnaware.advisory.model.NvdAdvisory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NvdAdvisoryRepository extends CrudRepository<NvdAdvisory, String> {}
