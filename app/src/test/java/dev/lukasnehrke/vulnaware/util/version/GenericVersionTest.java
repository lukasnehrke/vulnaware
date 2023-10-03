package dev.lukasnehrke.vulnaware.util.version;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class GenericVersionTest {

    @Test
    public void testNumber() {
        assertTrue(new GenericVersion("100").isEqual("100"));
        assertTrue(new GenericVersion("101").isAfter("100"));
        assertFalse(new GenericVersion("100").isAfter("101"));
    }

    @Test
    public void testSemver() {
        assertTrue(new GenericVersion("1.2.3").isEqual("1.2.3"));
        assertTrue(new GenericVersion("1.2.3").isAfter("1.2.2"));
        assertFalse(new GenericVersion("1.2.2").isAfter("1.2.3"));

        assertTrue(new GenericVersion("1.2.3-alpha").isEqual("1.2.3-alpha"));
        assertTrue(new GenericVersion("1.2.3-alpha").isAfter("1.2.2-alpha"));
        assertFalse(new GenericVersion("1.2.2-alpha").isAfter("1.2.3-alpha"));

        assertTrue(new GenericVersion("1.2.3-beta").isAfter("1.2.3-alpha"));
        assertFalse(new GenericVersion("1.2.3-alpha").isAfter("1.2.3-beta"));
    }

    @Test
    public void testDates() {
        assertTrue(new GenericVersion("2021-01-01").isEqual("2021-01-01"));
        assertTrue(new GenericVersion("2021-01-02").isAfter("2021-01-01"));
        assertFalse(new GenericVersion("2021-01-01").isAfter("2021-01-02"));
    }
}
