package dev.lukasnehrke.vulnaware.util.version;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Semver extends Version<Semver> {

    private Integer major;
    private int minor;
    private int patch;

    @Nullable
    private String preRelease;

    @Nullable
    private String build;

    public Semver(String version) {
        if (version.startsWith("v")) {
            // remove leading "v"
            version = version.substring(1);
        }

        if (version.contains("+")) {
            this.build = version.substring(version.indexOf("+") + 1);
            // remove build metadata
            version = version.substring(0, version.indexOf("+"));
        }

        if (version.contains("-")) {
            // remove pre-release
            this.preRelease = version.substring(version.indexOf("-") + 1);
            version = version.substring(0, version.indexOf("-"));
        }

        final String[] parts = version.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid version: " + version);
        }

        this.major = Integer.parseInt(parts[0]);
        this.minor = Integer.parseInt(parts[1]);
        this.patch = Integer.parseInt(parts[2]);
    }

    @Override
    public boolean isEqual(Semver version) {
        return (
            this.major.equals(version.major) &&
            this.minor == version.minor &&
            this.patch == version.patch &&
            Objects.equals(this.preRelease, version.preRelease)
        );
    }

    @Override
    public boolean isAfter(final Semver version) {
        if (this.major > version.major) return true;
        if (this.major < version.major) return false;
        if (this.minor > version.minor) return true;
        if (this.minor < version.minor) return false;
        if (this.patch > version.patch) return true;
        if (this.patch < version.patch) return false;

        if (this.preRelease != null && version.preRelease != null) {
            return this.preRelease.compareToIgnoreCase(version.preRelease) > 0;
        }

        if (this.preRelease != null) {
            return false;
        }

        return version.preRelease != null;
    }

    @Override
    public Semver createFrom(final String version) {
        return new Semver(version);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch) + (preRelease != null ? "-" + preRelease : "") + (build != null ? "+" + build : "");
    }
}
