package dev.lukasnehrke.vulnaware.util.version;

import java.util.Optional;
import org.springframework.lang.Nullable;

public abstract class Version<T extends Version<T>> {

    public static Optional<Version<?>> of(final String type, final String ecosystem, final String version) {
        if (type.equals("SEMVER")) {
            return Optional.of(new Semver(version));
        }
        if (type.equals("ECOSYSTEM") && ecosystem.equals("Maven")) {
            return Optional.of(new MavenVersion(version));
        }
        return Optional.of(new GenericVersion(version));
    }

    public abstract T createFrom(String version);

    public abstract boolean isEqual(T version);

    public abstract boolean isAfter(T version);

    public boolean isEqual(@Nullable final String version) {
        if (version == null) return false;
        return isEqual(createFrom(version));
    }

    public boolean isAfterEqual(final T version) {
        return isAfter(version) || isEqual(version);
    }

    @SuppressWarnings("unchecked")
    public boolean isBefore(final T version) {
        return version.isAfter((T) this);
    }

    public boolean isBeforeEqual(final T version) {
        return isBefore(version) || isEqual(version);
    }

    public boolean isBefore(@Nullable final String version) {
        if (version == null) return false;
        if (version.equals("*")) return true;
        return isBefore(createFrom(version));
    }

    public boolean isBeforeEqual(@Nullable final String version) {
        if (version == null) return false;
        if (version.equals("*")) return true;
        return isBeforeEqual(createFrom(version));
    }

    public boolean isAfter(@Nullable final String version) {
        if (version == null) return false;
        if (version.equals("0")) return true;
        return isAfter(createFrom(version));
    }

    public boolean isAfterEqual(@Nullable final String version) {
        if (version == null) return false;
        if (version.equals("0")) return true;
        return isAfterEqual(createFrom(version));
    }
}
