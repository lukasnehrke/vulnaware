package dev.lukasnehrke.vulnaware.util.version;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class MavenVersion extends Version<MavenVersion> {

    private final ComparableVersion version;

    public MavenVersion(String version) {
        this.version = new ComparableVersion(version);
    }

    @Override
    public MavenVersion createFrom(final String version) {
        return new MavenVersion(version);
    }

    @Override
    public boolean isEqual(final MavenVersion version) {
        return this.version.compareTo(version.version) == 0;
    }

    @Override
    public boolean isAfter(final MavenVersion version) {
        return this.version.compareTo(version.version) > 0;
    }

    @Override
    public String toString() {
        return version.toString();
    }
}
