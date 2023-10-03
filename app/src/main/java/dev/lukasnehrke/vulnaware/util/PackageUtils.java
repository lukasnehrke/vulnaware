package dev.lukasnehrke.vulnaware.util;

import com.github.packageurl.PackageURL;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import dev.lukasnehrke.vulnaware.bom.model.Ecosystem;
import us.springett.parsers.cpe.Cpe;

public final class PackageUtils {

    private PackageUtils() {}

    public static Ecosystem getEcosystemByPackageURL(final PackageURL url) {
        return switch (url.getType()) {
            case "github" -> Ecosystem.GITHUB;
            case "go", "golang" -> Ecosystem.GO;
            case "npm" -> Ecosystem.NPM;
            case "maven" -> Ecosystem.MAVEN;
            case "deb" -> Ecosystem.DEBIAN;
            default -> Ecosystem.OTHER;
        };
    }

    public static String getNameByPackageURL(final PackageURL url) {
        return switch (url.getType()) {
            case "maven" -> url.getNamespace() + ':' + url.getName();
            default -> url.getNamespace() != null ? (url.getNamespace() + '/' + url.getName()) : url.getName();
        };
    }

    public static void setOSVMappingData(final PackageURL purl, final Component component) {
        switch (purl.getType()) {
            case "maven" -> {
                component.setOsvEcosystem("Maven");
                component.setOsvName(purl.getNamespace() + ":" + purl.getName());
            }
            case "npm" -> {
                component.setOsvEcosystem("npm");
                if (purl.getNamespace() == null || purl.getNamespace().isBlank()) {
                    component.setOsvName(purl.getName());
                } else {
                    component.setOsvName(purl.getNamespace() + "/" + purl.getName());
                }
            }
            case "deb" -> {
                component.setOsvEcosystem("Debian");
                component.setOsvName(purl.getName());
            }
        }
    }

    public static void setNvdMappingData(final Cpe cpe, final Component component) {
        component.setCpeVendor(cpe.getVendor());
        component.setCpeProduct(cpe.getProduct());
        component.setCpeVersion(cpe.getVersion());
    }
}
