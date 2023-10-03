package dev.lukasnehrke.vulnaware.storage.repository;

import dev.lukasnehrke.vulnaware.storage.model.Asset;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends CrudRepository<Asset, Long> {
    Optional<Asset> findByLocation(String location);
}
