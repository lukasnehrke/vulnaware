package dev.lukasnehrke.vulnaware.bom.repository;

import dev.lukasnehrke.vulnaware.bom.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, String> {
    //List<Package> findForLookup(@Param("bomId") Long bomId);
}
