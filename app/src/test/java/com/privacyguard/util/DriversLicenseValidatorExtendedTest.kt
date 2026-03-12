package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Driver's License Validator Extended Test Suite
 * Covers US state driver's license formats, international licenses,
 * masking, extraction, expiry, and edge cases.
 */
class DriversLicenseValidatorExtendedTest {

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 – US State Driver's Licenses – Valid Formats
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `dl us 001 california format A1234567 letter then 7 digits`() = assertTrue(DriversLicenseValidator.isValid("A1234567", "CA"))
    @Test fun `dl us 002 california format B9999999`() = assertTrue(DriversLicenseValidator.isValid("B9999999", "CA"))
    @Test fun `dl us 003 texas 8 digits`() = assertTrue(DriversLicenseValidator.isValid("12345678", "TX"))
    @Test fun `dl us 004 texas 7 digits valid`() = assertTrue(DriversLicenseValidator.isValid("1234567", "TX"))
    @Test fun `dl us 005 new york letter then 7 digits`() = assertTrue(DriversLicenseValidator.isValid("A1234567", "NY"))
    @Test fun `dl us 006 new york 9 digits`() = assertTrue(DriversLicenseValidator.isValid("123456789", "NY"))
    @Test fun `dl us 007 florida letter then 12 digits`() = assertTrue(DriversLicenseValidator.isValid("A123456789012", "FL"))
    @Test fun `dl us 008 illinois letter then 11 digits`() = assertTrue(DriversLicenseValidator.isValid("A12345678901", "IL"))
    @Test fun `dl us 009 ohio 2 letters then 6 digits`() = assertTrue(DriversLicenseValidator.isValid("AB123456", "OH"))
    @Test fun `dl us 010 michigan letter then 12 digits`() = assertTrue(DriversLicenseValidator.isValid("A123456789012", "MI"))
    @Test fun `dl us 011 georgia 9 digits`() = assertTrue(DriversLicenseValidator.isValid("123456789", "GA"))
    @Test fun `dl us 012 virginia letter then 8 digits`() = assertTrue(DriversLicenseValidator.isValid("A12345678", "VA"))
    @Test fun `dl us 013 washington 7 digits alpha`() = assertTrue(DriversLicenseValidator.isValid("AB1234567", "WA"))
    @Test fun `dl us 014 arizona 9 digits`() = assertTrue(DriversLicenseValidator.isValid("D12345678", "AZ"))
    @Test fun `dl us 015 colorado 2 digits then 5 digits`() = assertTrue(DriversLicenseValidator.isValid("1234567", "CO"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 – US State Driver's Licenses – Invalid Formats
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `dl us invalid 001 empty string`() = assertFalse(DriversLicenseValidator.isValid("", "CA"))
    @Test fun `dl us invalid 002 too short`() = assertFalse(DriversLicenseValidator.isValid("A123", "CA"))
    @Test fun `dl us invalid 003 too long`() = assertFalse(DriversLicenseValidator.isValid("A12345678901234567", "CA"))
    @Test fun `dl us invalid 004 special chars`() = assertFalse(DriversLicenseValidator.isValid("A123@567", "CA"))
    @Test fun `dl us invalid 005 all zeros`() = assertFalse(DriversLicenseValidator.isValid("00000000", "CA"))
    @Test fun `dl us invalid 006 spaces in number`() = assertFalse(DriversLicenseValidator.isValid("A123 567", "CA"))
    @Test fun `dl us invalid 007 dashes in number`() = assertFalse(DriversLicenseValidator.isValid("A123-567", "CA"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 – UK Driver's Licenses
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `dl uk 001 standard format 18 chars`() = assertTrue(DriversLicenseValidator.isValid("MORGA657054SM9IJ", "GB"))
    @Test fun `dl uk 002 standard format surname 5`() = assertTrue(DriversLicenseValidator.isValid("SMITH123456AB1CD", "GB"))
    @Test fun `dl uk 003 too short 15 chars invalid`() = assertFalse(DriversLicenseValidator.isValid("MORGA65705", "GB"))
    @Test fun `dl uk 004 empty invalid`() = assertFalse(DriversLicenseValidator.isValid("", "GB"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 – German Driver's Licenses
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `dl de 001 standard format B072RRE2I3`() = assertTrue(DriversLicenseValidator.isValid("B072RRE2I3", "DE"))
    @Test fun `dl de 002 10 alphanumeric chars valid`() = assertTrue(DriversLicenseValidator.isValid("A123456789", "DE"))
    @Test fun `dl de 003 too short invalid`() = assertFalse(DriversLicenseValidator.isValid("A12345", "DE"))
    @Test fun `dl de 004 too long invalid`() = assertFalse(DriversLicenseValidator.isValid("A12345678901", "DE"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 – Canadian Driver's Licenses
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `dl ca 001 ontario 14 15 digits`() = assertTrue(DriversLicenseValidator.isValid("A1234-56789-01234", "CA"))
    @Test fun `dl ca 002 bc 7 digits`() = assertTrue(DriversLicenseValidator.isValid("1234567", "CA-BC"))
    @Test fun `dl ca 003 alberta 9 digits`() = assertTrue(DriversLicenseValidator.isValid("123456789", "CA-AB"))
    @Test fun `dl ca 004 quebec letter then 12 digits`() = assertTrue(DriversLicenseValidator.isValid("A123456789012", "CA-QC"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 – Australian Driver's Licenses
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `dl au 001 nsw format 8-10 chars`() = assertTrue(DriversLicenseValidator.isValid("12345678", "AU-NSW"))
    @Test fun `dl au 002 victoria 8 digits`() = assertTrue(DriversLicenseValidator.isValid("12345678", "AU-VIC"))
    @Test fun `dl au 003 queensland 8-9 digits`() = assertTrue(DriversLicenseValidator.isValid("123456789", "AU-QLD"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 – Generic Driver's License Validation
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `dl generic 001 7 alphanumeric valid`() = assertTrue(DriversLicenseValidator.isValidAny("A123456"))
    @Test fun `dl generic 002 8 alphanumeric valid`() = assertTrue(DriversLicenseValidator.isValidAny("A1234567"))
    @Test fun `dl generic 003 9 alphanumeric valid`() = assertTrue(DriversLicenseValidator.isValidAny("A12345678"))
    @Test fun `dl generic 004 10 alphanumeric valid`() = assertTrue(DriversLicenseValidator.isValidAny("A123456789"))
    @Test fun `dl generic 005 12 alphanumeric valid`() = assertTrue(DriversLicenseValidator.isValidAny("A12345678901"))
    @Test fun `dl generic 006 too short 5 chars invalid`() = assertFalse(DriversLicenseValidator.isValidAny("A1234"))
    @Test fun `dl generic 007 too long 20 chars invalid`() = assertFalse(DriversLicenseValidator.isValidAny("A1234567890123456789"))
    @Test fun `dl generic 008 empty string invalid`() = assertFalse(DriversLicenseValidator.isValidAny(""))
    @Test fun `dl generic 009 special chars invalid`() = assertFalse(DriversLicenseValidator.isValidAny("A123@567"))
    @Test fun `dl generic 010 spaces invalid`() = assertFalse(DriversLicenseValidator.isValidAny("A123 567"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 – Masking
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `mask 001 shows last 4`() {
        val masked = DriversLicenseValidator.mask("A1234567")
        assertTrue(masked.takeLast(4) == "4567")
    }

    @Test fun `mask 002 first chars hidden`() {
        val masked = DriversLicenseValidator.mask("A1234567")
        assertFalse(masked.startsWith("A123"))
    }

    @Test fun `mask 003 full mask hides all`() {
        val masked = DriversLicenseValidator.maskFull("A1234567")
        assertFalse(masked.contains("A12"))
    }

    @Test fun `mask 004 uses asterisks`() {
        val masked = DriversLicenseValidator.mask("A1234567")
        assertTrue(masked.contains("*") || masked.contains("X"))
    }

    @Test fun `mask 005 get last4`() {
        assertEquals("4567", DriversLicenseValidator.getLast4("A1234567"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 9 – Extraction
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `extract 001 dl in sentence`() {
        val text = "Driver's license: A1234567 California"
        val results = DriversLicenseValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `extract 002 dl in form`() {
        val text = "License #: A12345678"
        val results = DriversLicenseValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `extract 003 no dl in text`() {
        val text = "No license number here"
        val results = DriversLicenseValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test fun `extract 004 multiple dls in text`() {
        val text = "Driver 1: A1234567, Driver 2: B9876543"
        val results = DriversLicenseValidator.extractFromText(text)
        assertTrue(results.size >= 2)
    }

    @Test fun `extract 005 dl in json`() {
        val text = """{"dl_number": "A1234567", "state": "CA"}"""
        val results = DriversLicenseValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 10 – State Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `state 001 CA abbreviation recognized`() = assertTrue(DriversLicenseValidator.isSupportedState("CA"))
    @Test fun `state 002 TX abbreviation recognized`() = assertTrue(DriversLicenseValidator.isSupportedState("TX"))
    @Test fun `state 003 NY abbreviation recognized`() = assertTrue(DriversLicenseValidator.isSupportedState("NY"))
    @Test fun `state 004 FL abbreviation recognized`() = assertTrue(DriversLicenseValidator.isSupportedState("FL"))
    @Test fun `state 005 IL abbreviation recognized`() = assertTrue(DriversLicenseValidator.isSupportedState("IL"))
    @Test fun `state 006 OH recognized`() = assertTrue(DriversLicenseValidator.isSupportedState("OH"))
    @Test fun `state 007 MI recognized`() = assertTrue(DriversLicenseValidator.isSupportedState("MI"))
    @Test fun `state 008 GA recognized`() = assertTrue(DriversLicenseValidator.isSupportedState("GA"))
    @Test fun `state 009 XX not recognized`() = assertFalse(DriversLicenseValidator.isSupportedState("XX"))
    @Test fun `state 010 empty string not recognized`() = assertFalse(DriversLicenseValidator.isSupportedState(""))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 11 – isPII and Risk
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `pii 001 valid dl is pii`() = assertTrue(DriversLicenseValidator.isPII("A1234567"))
    @Test fun `pii 002 invalid dl not pii`() = assertFalse(DriversLicenseValidator.isPII("A123"))
    @Test fun `pii 003 empty string not pii`() = assertFalse(DriversLicenseValidator.isPII(""))
    @Test fun `pii 004 valid dl high risk`() {
        val risk = DriversLicenseValidator.getRiskLevel("A1234567")
        assertTrue(risk.name == "HIGH" || risk.name == "CRITICAL")
    }
    @Test fun `pii 005 invalid dl no risk`() {
        val risk = DriversLicenseValidator.getRiskLevel("A123")
        assertTrue(risk.name == "NONE" || risk.name == "LOW")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 12 – Normalization
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `normalize 001 lowercase to uppercase`() {
        assertEquals("A1234567", DriversLicenseValidator.normalize("a1234567"))
    }

    @Test fun `normalize 002 trim spaces`() {
        assertEquals("A1234567", DriversLicenseValidator.normalize("  A1234567  "))
    }

    @Test fun `normalize 003 remove dashes`() {
        assertEquals("A1234567", DriversLicenseValidator.normalize("A-1234-567"))
    }

    @Test fun `normalize 004 already normalized`() {
        assertEquals("A1234567", DriversLicenseValidator.normalize("A1234567"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 13 – Redaction
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `redact 001 dl in text redacted`() {
        val text = "License: A1234567"
        val redacted = DriversLicenseValidator.redactInText(text)
        assertFalse(redacted.contains("A1234567"))
    }

    @Test fun `redact 002 placeholder present after redaction`() {
        val text = "DL: A1234567"
        val redacted = DriversLicenseValidator.redactInText(text)
        assertTrue(redacted.contains("[DL]") || redacted.contains("***") || redacted.contains("REDACTED"))
    }

    @Test fun `redact 003 no dl unchanged`() {
        val text = "No license here"
        assertEquals(text, DriversLicenseValidator.redactInText(text))
    }

    @Test fun `redact 004 surrounding text preserved`() {
        val text = "Name: John, License: A1234567, DOB: 1985-03-22"
        val redacted = DriversLicenseValidator.redactInText(text)
        assertTrue(redacted.contains("Name: John"))
        assertTrue(redacted.contains("DOB: 1985-03-22"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 14 – Edge Cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `edge 001 unicode in number invalid`() = assertFalse(DriversLicenseValidator.isValidAny("A\u1234567"))
    @Test fun `edge 002 null byte invalid`() = assertFalse(DriversLicenseValidator.isValidAny("A\u00001234"))
    @Test fun `edge 003 very long string invalid`() = assertFalse(DriversLicenseValidator.isValidAny("A".repeat(50)))
    @Test fun `edge 004 single char invalid`() = assertFalse(DriversLicenseValidator.isValidAny("A"))
    @Test fun `edge 005 no crash on empty`() {
        val result = runCatching { DriversLicenseValidator.isValidAny("") }
        assertTrue(result.isSuccess)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 15 – Confidence Scoring
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `confidence 001 labeled dl high confidence`() {
        val score = DriversLicenseValidator.confidenceScoreInContext("License: A1234567", "A1234567")
        assertTrue(score >= 0.85f)
    }

    @Test fun `confidence 002 unlabeled moderate confidence`() {
        val score = DriversLicenseValidator.confidenceScore("A1234567")
        assertTrue(score >= 0.5f)
    }

    @Test fun `confidence 003 invalid zero confidence`() {
        val score = DriversLicenseValidator.confidenceScore("INVALID")
        assertEquals(0.0f, score, 0.01f)
    }

    @Test fun `confidence 004 score in range 0 to 1`() {
        assertTrue(DriversLicenseValidator.confidenceScore("A1234567") in 0.0f..1.0f)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 16 – Bulk Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `bulk 001 50 ca format licenses all valid`() {
        (1..50).map { i -> "${('A'.code + i % 26).toChar()}${(1000000 + i)}" }
            .forEach { assertTrue(DriversLicenseValidator.isValidAny(it)) }
    }

    @Test fun `bulk 002 invalid strings all fail`() {
        listOf("", "X", "12", "XY3", "@!#", "   ").forEach {
            assertFalse(DriversLicenseValidator.isValidAny(it))
        }
    }

    @Test fun `bulk 003 extract from batch text`() {
        val text = (1..10).joinToString(" ") { "License A${(1000000 + it)}" }
        val results = DriversLicenseValidator.extractFromText(text)
        assertTrue(results.size >= 5)
    }

} // end class DriversLicenseValidatorExtendedTest
