package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Passport Validator Extended Test Suite
 * Covers passport number validation for major issuing countries,
 * Machine Readable Zone (MRZ) parsing, expiry checks, and extraction.
 */
class PassportValidatorExtendedTest {

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 – US Passport Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `us passport 001 9 digit number valid`() = assertTrue(PassportValidator.isValid("123456789", "US"))
    @Test fun `us passport 002 letter then 8 digits valid`() = assertTrue(PassportValidator.isValid("A12345678", "US"))
    @Test fun `us passport 003 all digits 9 chars valid`() = assertTrue(PassportValidator.isValid("987654321", "US"))
    @Test fun `us passport 004 too short 8 digits invalid`() = assertFalse(PassportValidator.isValid("12345678", "US"))
    @Test fun `us passport 005 too long 10 chars invalid`() = assertFalse(PassportValidator.isValid("1234567890", "US"))
    @Test fun `us passport 006 empty string invalid`() = assertFalse(PassportValidator.isValid("", "US"))
    @Test fun `us passport 007 lowercase letters normalized`() = assertTrue(PassportValidator.isValid("a12345678", "US"))
    @Test fun `us passport 008 all letters invalid`() = assertFalse(PassportValidator.isValid("ABCDEFGHI", "US"))
    @Test fun `us passport 009 special chars invalid`() = assertFalse(PassportValidator.isValid("12345678!", "US"))
    @Test fun `us passport 010 two letters then 7 digits valid`() = assertTrue(PassportValidator.isValid("AB1234567", "US"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 – UK Passport Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `uk passport 001 9 digit number valid`() = assertTrue(PassportValidator.isValid("123456789", "GB"))
    @Test fun `uk passport 002 letter prefix valid`() = assertTrue(PassportValidator.isValid("A12345678", "GB"))
    @Test fun `uk passport 003 too short invalid`() = assertFalse(PassportValidator.isValid("12345678", "GB"))
    @Test fun `uk passport 004 too long invalid`() = assertFalse(PassportValidator.isValid("1234567890", "GB"))
    @Test fun `uk passport 005 empty invalid`() = assertFalse(PassportValidator.isValid("", "GB"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 – German Passport Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `de passport 001 standard 9 char valid`() = assertTrue(PassportValidator.isValid("C01X00T47", "DE"))
    @Test fun `de passport 002 10 char number valid`() = assertTrue(PassportValidator.isValid("C01X00T471", "DE"))
    @Test fun `de passport 003 too short 8 chars invalid`() = assertFalse(PassportValidator.isValid("C01X00T4", "DE"))
    @Test fun `de passport 004 too long 11 chars invalid`() = assertFalse(PassportValidator.isValid("C01X00T4711", "DE"))
    @Test fun `de passport 005 lowercase valid after normalization`() = assertTrue(PassportValidator.isValid("c01x00t47", "DE"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 – Canadian Passport Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `ca passport 001 standard format valid`() = assertTrue(PassportValidator.isValid("AB123456", "CA"))
    @Test fun `ca passport 002 two letters six digits`() = assertTrue(PassportValidator.isValid("XY654321", "CA"))
    @Test fun `ca passport 003 wrong length invalid`() = assertFalse(PassportValidator.isValid("AB12345", "CA"))
    @Test fun `ca passport 004 no letter prefix invalid`() = assertFalse(PassportValidator.isValid("12345678", "CA"))
    @Test fun `ca passport 005 single letter prefix invalid`() = assertFalse(PassportValidator.isValid("A1234567", "CA"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 – French Passport Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `fr passport 001 two letters then 7 digits valid`() = assertTrue(PassportValidator.isValid("AB1234567", "FR"))
    @Test fun `fr passport 002 standard format`() = assertTrue(PassportValidator.isValid("CS1234567", "FR"))
    @Test fun `fr passport 003 wrong length 8 invalid`() = assertFalse(PassportValidator.isValid("AB123456", "FR"))
    @Test fun `fr passport 004 no letter prefix invalid`() = assertFalse(PassportValidator.isValid("123456789", "FR"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 – Australian Passport Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `au passport 001 letter then 7 digits valid`() = assertTrue(PassportValidator.isValid("A1234567", "AU"))
    @Test fun `au passport 002 standard format`() = assertTrue(PassportValidator.isValid("E5678901", "AU"))
    @Test fun `au passport 003 no letter invalid`() = assertFalse(PassportValidator.isValid("12345678", "AU"))
    @Test fun `au passport 004 two letters invalid`() = assertFalse(PassportValidator.isValid("AB234567", "AU"))
    @Test fun `au passport 005 too long invalid`() = assertFalse(PassportValidator.isValid("A12345678", "AU"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 – Indian Passport Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `in passport 001 letter then 7 digits valid`() = assertTrue(PassportValidator.isValid("A1234567", "IN"))
    @Test fun `in passport 002 standard format`() = assertTrue(PassportValidator.isValid("J5678901", "IN"))
    @Test fun `in passport 003 too short invalid`() = assertFalse(PassportValidator.isValid("A123456", "IN"))
    @Test fun `in passport 004 no leading letter invalid`() = assertFalse(PassportValidator.isValid("12345678", "IN"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 – Japanese Passport Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `jp passport 001 two letters then 7 digits valid`() = assertTrue(PassportValidator.isValid("TK1234567", "JP"))
    @Test fun `jp passport 002 standard format`() = assertTrue(PassportValidator.isValid("MA5678901", "JP"))
    @Test fun `jp passport 003 one letter only invalid`() = assertFalse(PassportValidator.isValid("T1234567", "JP"))
    @Test fun `jp passport 004 three letters invalid`() = assertFalse(PassportValidator.isValid("TKY234567", "JP"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 9 – Generic Passport Validation (no country)
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `generic 001 8 chars alphanumeric valid`() = assertTrue(PassportValidator.isValidAny("A1234567"))
    @Test fun `generic 002 9 chars alphanumeric valid`() = assertTrue(PassportValidator.isValidAny("AB1234567"))
    @Test fun `generic 003 only digits 9 valid`() = assertTrue(PassportValidator.isValidAny("123456789"))
    @Test fun `generic 004 too short invalid`() = assertFalse(PassportValidator.isValidAny("12345"))
    @Test fun `generic 005 too long invalid`() = assertFalse(PassportValidator.isValidAny("12345678901234"))
    @Test fun `generic 006 empty invalid`() = assertFalse(PassportValidator.isValidAny(""))
    @Test fun `generic 007 special chars invalid`() = assertFalse(PassportValidator.isValidAny("A1234@678"))
    @Test fun `generic 008 spaces invalid`() = assertFalse(PassportValidator.isValidAny("A123 5678"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 10 – Passport Masking
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `mask 001 last 4 shown`() {
        val masked = PassportValidator.mask("A12345678")
        assertTrue(masked.takeLast(4) == "5678")
    }

    @Test fun `mask 002 first chars hidden`() {
        val masked = PassportValidator.mask("A12345678")
        assertFalse(masked.startsWith("A123"))
    }

    @Test fun `mask 003 full mask all hidden`() {
        val masked = PassportValidator.maskFull("A12345678")
        assertFalse(masked.contains("A12"))
    }

    @Test fun `mask 004 masked uses asterisks or X`() {
        val masked = PassportValidator.mask("A12345678")
        assertTrue(masked.contains("*") || masked.contains("X") || masked.contains("#"))
    }

    @Test fun `mask 005 get last4 `() {
        val last4 = PassportValidator.getLast4("A12345678")
        assertEquals("5678", last4)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 11 – Extraction from Text
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `extract 001 passport number in sentence`() {
        val text = "Passport number: A12345678 issued 2020"
        val results = PassportValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `extract 002 multiple passports in text`() {
        val text = "Traveler 1: A12345678 Traveler 2: B98765432"
        val results = PassportValidator.extractFromText(text)
        assertTrue(results.size >= 2)
    }

    @Test fun `extract 003 no passport in text`() {
        val text = "No passport numbers in this text at all"
        val results = PassportValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test fun `extract 004 passport in json field`() {
        val text = """{"passport_number": "A12345678", "name": "John"}"""
        val results = PassportValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `extract 005 passport at start of text`() {
        val text = "A12345678 is the passport number"
        val results = PassportValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `extract 006 labeled passport field`() {
        val text = "PASSPORT: B98765432"
        val results = PassportValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `extract 007 empty text returns empty`() {
        val results = PassportValidator.extractFromText("")
        assertTrue(results.isEmpty())
    }

    @Test fun `extract 008 passport in csv`() {
        val text = "John,Doe,A12345678,US,2030-12-31"
        val results = PassportValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 12 – Expiry Validation
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `expiry 001 future date not expired`() {
        assertFalse(PassportValidator.isExpired("2035-12-31"))
    }

    @Test fun `expiry 002 past date expired`() {
        assertTrue(PassportValidator.isExpired("2000-01-01"))
    }

    @Test fun `expiry 003 near future not expired`() {
        assertFalse(PassportValidator.isExpired("2027-06-15"))
    }

    @Test fun `expiry 004 invalid date returns true as safety`() {
        assertTrue(PassportValidator.isExpired("not-a-date"))
    }

    @Test fun `expiry 005 expiring within 6 months warning`() {
        // A passport expiring "soon" should trigger warning
        val expiring = PassportValidator.isExpiringSoon("2027-06-01")
        assertNotNull(expiring)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 13 – MRZ Line Parsing
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `mrz 001 td3 type p`() {
        val mrz = "P<UTOERIKSSON<<ANNA<MARIA<<<<<<<<<<<<<<<<<<<<<"
        assertEquals("P", PassportValidator.getMRZDocumentType(mrz))
    }

    @Test fun `mrz 002 country code extraction`() {
        val mrz = "P<UTOERIKSSON<<ANNA<MARIA<<<<<<<<<<<<<<<<<<<<<"
        assertEquals("UTO", PassportValidator.getMRZCountryCode(mrz))
    }

    @Test fun `mrz 003 surname extraction`() {
        val mrz = "P<UTOERIKSSON<<ANNA<MARIA<<<<<<<<<<<<<<<<<<<<<"
        assertTrue(PassportValidator.getMRZSurname(mrz).contains("ERIKSSON"))
    }

    @Test fun `mrz 004 given names extraction`() {
        val mrz = "P<UTOERIKSSON<<ANNA<MARIA<<<<<<<<<<<<<<<<<<<<<"
        val given = PassportValidator.getMRZGivenNames(mrz)
        assertTrue(given.contains("ANNA") || given.contains("MARIA"))
    }

    @Test fun `mrz 005 invalid mrz handled gracefully`() {
        val result = runCatching { PassportValidator.getMRZDocumentType("INVALID") }
        assertTrue(result.isSuccess || result.exceptionOrNull() is IllegalArgumentException)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 14 – Normalization
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `normalize 001 lowercase to uppercase`() {
        assertEquals("A12345678", PassportValidator.normalize("a12345678"))
    }

    @Test fun `normalize 002 trim whitespace`() {
        assertEquals("A12345678", PassportValidator.normalize("  A12345678  "))
    }

    @Test fun `normalize 003 remove dashes`() {
        assertEquals("A12345678", PassportValidator.normalize("A-1234-5678"))
    }

    @Test fun `normalize 004 remove spaces`() {
        assertEquals("A12345678", PassportValidator.normalize("A1234 5678"))
    }

    @Test fun `normalize 005 already normalized unchanged`() {
        assertEquals("A12345678", PassportValidator.normalize("A12345678"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 15 – isPII and Risk
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `pii 001 valid passport is pii`() = assertTrue(PassportValidator.isPII("A12345678"))
    @Test fun `pii 002 invalid passport not pii`() = assertFalse(PassportValidator.isPII("12345"))
    @Test fun `pii 003 empty string not pii`() = assertFalse(PassportValidator.isPII(""))
    @Test fun `pii 004 passport high risk`() {
        val risk = PassportValidator.getRiskLevel("A12345678")
        assertTrue(risk.name == "HIGH" || risk.name == "CRITICAL")
    }
    @Test fun `pii 005 invalid passport no risk`() {
        val risk = PassportValidator.getRiskLevel("INVALID")
        assertTrue(risk.name == "NONE" || risk.name == "LOW")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 16 – Redaction
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `redact 001 passport number in text redacted`() {
        val text = "Passport: A12345678"
        val redacted = PassportValidator.redactInText(text)
        assertFalse(redacted.contains("A12345678"))
    }

    @Test fun `redact 002 redacted text has placeholder`() {
        val text = "Passport A12345678 issued"
        val redacted = PassportValidator.redactInText(text)
        assertTrue(redacted.contains("[PASSPORT]") || redacted.contains("***") || redacted.contains("REDACTED"))
    }

    @Test fun `redact 003 no passport unchanged`() {
        val text = "No passport here"
        val redacted = PassportValidator.redactInText(text)
        assertEquals(text, redacted)
    }

    @Test fun `redact 004 surrounding text preserved`() {
        val text = "Name: John, Passport: A12345678, Country: US"
        val redacted = PassportValidator.redactInText(text)
        assertTrue(redacted.contains("Name: John"))
        assertTrue(redacted.contains("Country: US"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 17 – Edge Cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `edge 001 null-safe empty string`() {
        val result = runCatching { PassportValidator.isValid("", "US") }
        assertTrue(result.isSuccess)
    }

    @Test fun `edge 002 unicode letters invalid`() = assertFalse(PassportValidator.isValidAny("Ä1234567"))
    @Test fun `edge 003 null byte invalid`() = assertFalse(PassportValidator.isValidAny("\u000012345678"))
    @Test fun `edge 004 very long string invalid`() = assertFalse(PassportValidator.isValidAny("A".repeat(100)))
    @Test fun `edge 005 newline in number invalid`() = assertFalse(PassportValidator.isValidAny("A1234\n5678"))
    @Test fun `edge 006 all zeros invalid`() = assertFalse(PassportValidator.isValidAny("000000000"))
    @Test fun `edge 007 tab in number invalid`() = assertFalse(PassportValidator.isValidAny("A1234\t5678"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 18 – Confidence Scoring
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `confidence 001 labeled passport high confidence`() {
        val score = PassportValidator.confidenceScoreInContext("Passport: A12345678", "A12345678")
        assertTrue(score >= 0.9f)
    }

    @Test fun `confidence 002 unlabeled passport lower confidence`() {
        val score = PassportValidator.confidenceScore("A12345678")
        assertTrue(score >= 0.5f)
    }

    @Test fun `confidence 003 score between 0 and 1`() {
        val score = PassportValidator.confidenceScore("A12345678")
        assertTrue(score in 0.0f..1.0f)
    }

    @Test fun `confidence 004 invalid passport zero confidence`() {
        val score = PassportValidator.confidenceScore("INVALID")
        assertEquals(0.0f, score, 0.01f)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 19 – Bulk Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `bulk 001 20 valid us passports passes`() {
        val passports = (1..20).map { i -> "A${(10000000 + i)}" }
        passports.forEach { assertTrue("Expected valid: $it", PassportValidator.isValidAny(it)) }
    }

    @Test fun `bulk 002 invalid strings fail`() {
        val invalid = listOf("", "12345", "A@B", "XXXX", "!!!!!!!!!")
        invalid.forEach { assertFalse("Expected invalid: $it", PassportValidator.isValidAny(it)) }
    }

    @Test fun `bulk 003 extract passports from batch text`() {
        val texts = (1..10).map { i -> "Passport A${(10000000 + i)} for traveler $i" }
        val combined = texts.joinToString(" | ")
        val results = PassportValidator.extractFromText(combined)
        assertTrue(results.size >= 5)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 20 – Country Code Validation
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `country 001 US accepted`() = assertTrue(PassportValidator.isSupportedCountry("US"))
    @Test fun `country 002 GB accepted`() = assertTrue(PassportValidator.isSupportedCountry("GB"))
    @Test fun `country 003 DE accepted`() = assertTrue(PassportValidator.isSupportedCountry("DE"))
    @Test fun `country 004 FR accepted`() = assertTrue(PassportValidator.isSupportedCountry("FR"))
    @Test fun `country 005 CA accepted`() = assertTrue(PassportValidator.isSupportedCountry("CA"))
    @Test fun `country 006 AU accepted`() = assertTrue(PassportValidator.isSupportedCountry("AU"))
    @Test fun `country 007 JP accepted`() = assertTrue(PassportValidator.isSupportedCountry("JP"))
    @Test fun `country 008 IN accepted`() = assertTrue(PassportValidator.isSupportedCountry("IN"))
    @Test fun `country 009 XX unknown`() = assertFalse(PassportValidator.isSupportedCountry("XX"))
    @Test fun `country 010 empty string invalid`() = assertFalse(PassportValidator.isSupportedCountry(""))

} // end class PassportValidatorExtendedTest
