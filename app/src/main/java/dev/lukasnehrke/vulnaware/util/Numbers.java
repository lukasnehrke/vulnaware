package dev.lukasnehrke.vulnaware.util;

public final class Numbers {

    private Numbers() {}

    public static boolean isLong(final String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
