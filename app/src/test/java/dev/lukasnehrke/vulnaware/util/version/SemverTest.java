package dev.lukasnehrke.vulnaware.util.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public final class SemverTest {

    private final Semver s1 = new Semver("1.2.3");
    private final Semver s2 = new Semver("19.0.0-beta.1+exp.sha.5114f85");

    @Test
    public void testParsing() {
        assertEquals(new Semver("v1.0.0"), new Semver("1.0.0"));

        assertEquals(1, s1.getMajor());
        assertEquals(2, s1.getMinor());
        assertEquals(3, s1.getPatch());

        assertEquals(19, s2.getMajor());
        assertEquals(0, s2.getMinor());
        assertEquals(0, s2.getPatch());
        assertEquals("beta.1", s2.getPreRelease());
        assertEquals("exp.sha.5114f85", s2.getBuild());
    }

    @Test
    public void testComparison() {
        assertFalse(s1.isAfter("1.2.3"));

        assertTrue(s1.isAfter("1.2.0"));
        assertTrue(s1.isAfter("1.0.2"));
        assertTrue(s1.isAfter("1.2.2-beta"));
        assertTrue(s1.isAfter("1.2.2-beta.1"));

        assertTrue(s1.isBefore("1.2.4"));
        assertTrue(s1.isAfter("1.2.3-alpha"));

        assertTrue(s2.isAfter("19.0.0-alpha"));
        assertTrue(s2.isBefore("19.0.0"));
    }
}
