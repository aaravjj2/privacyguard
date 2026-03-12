package com.privacyguard.ml

import org.junit.Assert.*
import org.junit.Test

class SeverityTest {

    @Test
    fun `severity values exist`() {
        assertEquals(3, Severity.entries.size)
        assertNotNull(Severity.CRITICAL)
        assertNotNull(Severity.HIGH)
        assertNotNull(Severity.MEDIUM)
    }

    @Test
    fun `severity ordering is CRITICAL highest`() {
        assertTrue(Severity.CRITICAL.ordinal < Severity.HIGH.ordinal)
        assertTrue(Severity.HIGH.ordinal < Severity.MEDIUM.ordinal)
    }

    @Test
    fun `severity display names are set`() {
        assertEquals("Critical", Severity.CRITICAL.displayName)
        assertEquals("High", Severity.HIGH.displayName)
        assertEquals("Medium", Severity.MEDIUM.displayName)
    }

    @Test
    fun `severity colors are non-zero`() {
        assertTrue(Severity.CRITICAL.colorHex != 0L)
        assertTrue(Severity.HIGH.colorHex != 0L)
        assertTrue(Severity.MEDIUM.colorHex != 0L)
    }
}
