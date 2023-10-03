package dev.lukasnehrke.vulnaware.bom.service;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.PackageURLBuilder;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import dev.lukasnehrke.vulnaware.bom.model.Package;
import dev.lukasnehrke.vulnaware.bom.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParserService {

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);
    private final PackageRepository packageRepository;

    public void createPackage(final Component c) {
        try {
            if (c.getPackageURL() == null) return;
            final var id = transformToId(new PackageURL(c.getPackageURL()));

            final var pkg = packageRepository.findById(id);
            if (pkg.isEmpty()) {
                final var pkgNew = new Package();
                pkgNew.setId(id);
                c.setPkg(packageRepository.save(pkgNew));
            } else {
                c.setPkg(pkg.get());
            }
        } catch (MalformedPackageURLException ex) {
            logger.warn("Invalid package url: {}", c.getPackageURL(), ex);
        }
    }

    private String transformToId(final PackageURL url) throws MalformedPackageURLException {
        return PackageURLBuilder
            .aPackageURL()
            .withType(url.getType())
            .withNamespace(url.getNamespace())
            .withName(url.getName())
            .withVersion(url.getVersion())
            .build()
            .canonicalize();
    }
}
