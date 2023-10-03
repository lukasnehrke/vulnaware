package dev.lukasnehrke.vulnaware.util.version;

public class GenericVersion extends Version<GenericVersion> {

    private final String version;

    public GenericVersion(final String version) {
        this.version = version;
    }

    @Override
    public GenericVersion createFrom(String version) {
        return new GenericVersion(version);
    }

    @Override
    public boolean isEqual(final GenericVersion version) {
        return this.version.equals(version.version);
    }

    @Override
    public boolean isAfter(final GenericVersion version) {
        final String[] thisParts = this.version.split("\\.");
        final String[] otherParts = version.version.split("\\.");
        for (int i = 0; i < Math.min(thisParts.length, otherParts.length); i++) {
            if (isNumeric(thisParts[i]) && isNumeric(otherParts[i])) {
                final int thisPart = Integer.parseInt(thisParts[i]);
                final int otherPart = Integer.parseInt(otherParts[i]);
                if (thisPart < otherPart) {
                    return false;
                } else if (thisPart > otherPart) {
                    return true;
                }
            } else {
                final int result = thisParts[i].compareTo(otherParts[i]);
                if (result < 0) {
                    return false;
                } else if (result > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNumeric(final String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return version;
    }
}
