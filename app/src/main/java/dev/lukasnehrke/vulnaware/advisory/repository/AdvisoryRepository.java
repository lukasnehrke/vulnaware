package dev.lukasnehrke.vulnaware.advisory.repository;

import dev.lukasnehrke.vulnaware.advisory.model.Advisory;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvisoryRepository extends JpaRepository<Advisory, String> {
    @Query(
        value = "SELECT DISTINCT a " +
        "FROM Advisory a INNER JOIN a.affectedPackages ap " +
        "INNER JOIN Component c ON " +
        "c.osvEcosystem = ap.osvEcosystem AND c.osvName = ap.osvName " +
        "OR (c.cpeVendor = ap.cpeVendor AND c.cpeProduct = ap.cpeProduct AND (ap.cpeVersion IS NULL OR c.cpeVersion = ap.cpeVersion))" +
        "WHERE c.bom.id = :bomId AND a.withdrawn = false"
    )
    List<Advisory> selectByBom(@Param("bomId") Long bomId);

    @Query(
        value = "SELECT DISTINCT a " +
        "FROM Advisory a INNER JOIN a.affectedPackages ap " +
        "INNER JOIN Component c ON c.osvEcosystem = ap.osvEcosystem AND c.osvName = ap.osvName " +
        "WHERE c.bom.id = :bomId AND a.withdrawn = false AND a.updatedAt > :date"
    )
    List<Advisory> selectByBomAndDate(@Param("bomId") Long bomId, @Param("date") Date date);
}
