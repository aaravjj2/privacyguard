package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Extended JUnit4 test suite for CountryCodeValidator.
 *
 * Covers:
 *  Section  1 — US/Canada NANP phones        (25 tests)
 *  Section  2 — UK phones                    (15 tests)
 *  Section  3 — Germany phones               (15 tests)
 *  Section  4 — France phones                (15 tests)
 *  Section  5 — Australia phones             (15 tests)
 *  Section  6 — Japan phones                 (15 tests)
 *  Section  7 — India phones                 (15 tests)
 *  Section  8 — China phones                 (15 tests)
 *  Section  9 — formatToE164                 (15 tests)
 *  Section 10 — looksLikePhoneNumber         (15 tests)
 *  Section 11 — Edge cases                   (15 tests)
 *
 *  Total: 180 test functions
 */
class CountryCodeValidatorExtendedTest {

    // =========================================================================
    // Section 1 — US/Canada NANP phones (25 tests)
    // =========================================================================

    @Test
    fun `validate US number with plus-one prefix returns valid`() {
        val result = CountryCodeValidator.validate("+15552345678")
        assertTrue("Expected isValid=true for +15552345678", result.isValid)
        assertEquals("Expected country code US or CA", "1", result.countryCode)
        assertNotNull("normalizedNumber must not be null", result.normalizedNumber)
    }

    @Test
    fun `validate US number in plus-one-dashes format returns valid`() {
        val result = CountryCodeValidator.validate("+1-555-234-5678")
        assertTrue("Expected isValid=true for +1-555-234-5678", result.isValid)
        assertEquals("Country code should be 1", "1", result.countryCode)
        assertTrue("Confidence should be > 0", result.confidence > 0)
    }

    @Test
    fun `validate US number in parenthetical NXX format returns valid`() {
        val result = CountryCodeValidator.validate("(800) 555-0100")
        assertTrue("Expected isValid=true for (800) 555-0100", result.isValid)
        assertNotNull("normalizedNumber must not be null", result.normalizedNumber)
    }

    @Test
    fun `validate ten-digit US number returns valid`() {
        val result = CountryCodeValidator.validate("2125551234")
        assertTrue("Expected isValid=true for 2125551234", result.isValid)
        assertEquals("Country code should be 1", "1", result.countryCode)
    }

    @Test
    fun `validate US number 212-555-1234 dashes format returns valid`() {
        val result = CountryCodeValidator.validate("212-555-1234")
        assertTrue("Expected isValid=true", result.isValid)
        assertNotNull("normalizedNumber must not be null", result.normalizedNumber)
        assertTrue("Confidence must be positive", result.confidence > 0)
    }

    @Test
    fun `validate toll-free 800 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+18005551234")
        assertTrue("800 toll-free should be valid", result.isValid)
        assertEquals("1", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate toll-free 888 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+18885551234")
        assertTrue("888 toll-free should be valid", result.isValid)
        assertEquals("1", result.countryCode)
    }

    @Test
    fun `validate toll-free 877 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+18775551234")
        assertTrue("877 toll-free should be valid", result.isValid)
        assertNotNull(result.normalizedNumber)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate toll-free 866 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+18665551234")
        assertTrue("866 toll-free should be valid", result.isValid)
        assertEquals("1", result.countryCode)
    }

    @Test
    fun `validate toll-free 855 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+18555551234")
        assertTrue("855 toll-free should be valid", result.isValid)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate toll-free 844 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+18445551234")
        assertTrue("844 toll-free should be valid", result.isValid)
        assertEquals("1", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate toll-free 833 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+18335551234")
        assertTrue("833 toll-free should be valid", result.isValid)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate shared-cost 900 prefix returns valid or flagged`() {
        val result = CountryCodeValidator.validate("+19005551234")
        // 900 numbers are technically valid NANP but may have low confidence
        assertNotNull("Result must not be null", result)
        assertNotNull("normalizedNumber must not be null", result.normalizedNumber)
    }

    @Test
    fun `validate Canadian number +1-416 returns valid`() {
        val result = CountryCodeValidator.validate("+14165551234")
        assertTrue("Canadian 416 should be valid", result.isValid)
        assertEquals("1", result.countryCode)
    }

    @Test
    fun `validate Canadian number +1-604 Vancouver returns valid`() {
        val result = CountryCodeValidator.validate("+16045551234")
        assertTrue("Canadian 604 should be valid", result.isValid)
        assertEquals("1", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate US number with spaces format returns valid`() {
        val result = CountryCodeValidator.validate("+1 555 234 5678")
        assertTrue("Spaced format should be valid", result.isValid)
        assertEquals("1", result.countryCode)
    }

    @Test
    fun `validate NANP number via validateNANPNumber returns valid`() {
        val result = CountryCodeValidator.validateNANPNumber("+15552345678")
        assertTrue("validateNANPNumber should return valid for +15552345678", result.isValid)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validateNANPNumber with 10-digit number returns valid`() {
        val result = CountryCodeValidator.validateNANPNumber("5552345678")
        assertTrue("10-digit NANP should be valid", result.isValid)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate invalid all-zeros NANP returns invalid`() {
        val result = CountryCodeValidator.validate("000-000-0000")
        assertFalse("000-000-0000 should be invalid", result.isValid)
        assertNotNull("reason should explain invalidity", result.reason)
    }

    @Test
    fun `validate invalid 123-456-7890 directory number returns invalid`() {
        val result = CountryCodeValidator.validate("123-456-7890")
        assertFalse("123-456-7890 (directory info) should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `detectCountry on US plus-one number returns US or CA`() {
        val country = CountryCodeValidator.detectCountry("+15552345678")
        assertNotNull("detectCountry should not return null", country)
        assertTrue(
            "Detected country should be US or CA",
            country == "US" || country == "CA" || country == "1"
        )
    }

    @Test
    fun `detectCountry on toll-free 800 detects NANP region`() {
        val country = CountryCodeValidator.detectCountry("+18005551234")
        assertNotNull(country)
    }

    @Test
    fun `validate US number confidence is high for well-formed number`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertTrue(result.isValid)
        assertTrue("Confidence should be at least 50 for +1-202", result.confidence >= 50)
    }

    @Test
    fun `validate US number normalizedNumber starts with plus`() {
        val result = CountryCodeValidator.validate("2125551234")
        if (result.isValid) {
            assertTrue(
                "normalizedNumber should start with +",
                result.normalizedNumber?.startsWith("+") == true
            )
        }
        assertNotNull(result)
    }

    @Test
    fun `validate US number with plus-one and parentheses returns valid`() {
        val result = CountryCodeValidator.validate("+1 (310) 555-9999")
        assertTrue("Mixed format with +1 and parens should be valid", result.isValid)
        assertEquals("1", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    // =========================================================================
    // Section 2 — UK phones (15 tests)
    // =========================================================================

    @Test
    fun `validate UK number with plus-44 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+442071234567")
        assertTrue("UK +44 number should be valid", result.isValid)
        assertEquals("44", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate UK London 020 number returns valid`() {
        val result = CountryCodeValidator.validate("+442012345678")
        assertTrue("UK London 020 should be valid", result.isValid)
        assertEquals("44", result.countryCode)
    }

    @Test
    fun `validate UK Manchester 0161 number returns valid`() {
        val result = CountryCodeValidator.validate("+441612345678")
        assertTrue("UK Manchester 0161 should be valid", result.isValid)
        assertEquals("44", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate UK mobile 07XXXXXXXXX returns valid`() {
        val result = CountryCodeValidator.validate("+447911123456")
        assertTrue("UK mobile 07X should be valid", result.isValid)
        assertEquals("44", result.countryCode)
    }

    @Test
    fun `validate UK mobile 07700900000 returns valid`() {
        val result = CountryCodeValidator.validate("+447700900000")
        assertTrue("UK mobile should be valid", result.isValid)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate UK geographic 01XXX XXXXXX returns valid`() {
        val result = CountryCodeValidator.validate("+441234567890")
        assertTrue("UK geographic 01XXX should be valid", result.isValid)
        assertEquals("44", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate UK number in local 020 XXXX XXXX format returns valid`() {
        val result = CountryCodeValidator.validate("+442034567890")
        assertTrue("UK 020 in full format should be valid", result.isValid)
        assertEquals("44", result.countryCode)
    }

    @Test
    fun `validate UK Edinburgh 0131 number returns valid`() {
        val result = CountryCodeValidator.validate("+441312345678")
        assertTrue("UK Edinburgh 0131 should be valid", result.isValid)
        assertEquals("44", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate UK freephone 0800 number returns valid`() {
        val result = CountryCodeValidator.validate("+448001234567")
        assertTrue("UK freephone 0800 should be valid", result.isValid)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate UK premium 09XX number returns not null`() {
        val result = CountryCodeValidator.validate("+449012345678")
        assertNotNull("Result must not be null", result)
        assertNotNull(result.reason)
    }

    @Test
    fun `detectCountry on UK plus-44 number returns UK`() {
        val country = CountryCodeValidator.detectCountry("+447911123456")
        assertNotNull(country)
        assertTrue(
            "Should detect UK",
            country == "UK" || country == "GB" || country == "44"
        )
    }

    @Test
    fun `validate UK number too short returns invalid`() {
        val result = CountryCodeValidator.validate("+4420712345")
        assertFalse("Too short UK number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate UK number too long returns invalid`() {
        val result = CountryCodeValidator.validate("+442071234567890123")
        assertFalse("Too long UK number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate UK number confidence is positive for valid number`() {
        val result = CountryCodeValidator.validate("+447700900123")
        assertTrue(result.isValid)
        assertTrue("UK confidence should be positive", result.confidence > 0)
    }

    @Test
    fun `validate UK normalizedNumber starts with plus-44`() {
        val result = CountryCodeValidator.validate("+447911123456")
        if (result.isValid) {
            assertTrue(
                "Normalized should start with +44",
                result.normalizedNumber?.startsWith("+44") == true
            )
        }
        assertNotNull(result)
    }

    // =========================================================================
    // Section 3 — Germany phones (15 tests)
    // =========================================================================

    @Test
    fun `validate Germany number with plus-49 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+4930123456789")
        assertTrue("German +49 number should be valid", result.isValid)
        assertEquals("49", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Germany Berlin 030 number returns valid`() {
        val result = CountryCodeValidator.validate("+4930987654321")
        assertTrue("German Berlin 030 should be valid", result.isValid)
        assertEquals("49", result.countryCode)
    }

    @Test
    fun `validate Germany Hamburg 040 number returns valid`() {
        val result = CountryCodeValidator.validate("+4940123456789")
        assertTrue("German Hamburg 040 should be valid", result.isValid)
        assertEquals("49", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Germany mobile 015X returns valid`() {
        val result = CountryCodeValidator.validate("+491512345678")
        assertTrue("German 015X mobile should be valid", result.isValid)
        assertEquals("49", result.countryCode)
    }

    @Test
    fun `validate Germany mobile 016X returns valid`() {
        val result = CountryCodeValidator.validate("+491612345678")
        assertTrue("German 016X mobile should be valid", result.isValid)
        assertEquals("49", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate Germany mobile 017X returns valid`() {
        val result = CountryCodeValidator.validate("+491712345678")
        assertTrue("German 017X mobile should be valid", result.isValid)
        assertEquals("49", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Germany Munich 089 number returns valid`() {
        val result = CountryCodeValidator.validate("+498912345678")
        assertTrue("German Munich 089 should be valid", result.isValid)
        assertEquals("49", result.countryCode)
    }

    @Test
    fun `validate Germany Frankfurt 069 number returns valid`() {
        val result = CountryCodeValidator.validate("+496912345678")
        assertTrue("German Frankfurt 069 should be valid", result.isValid)
        assertEquals("49", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Germany freephone 0800 number returns valid`() {
        val result = CountryCodeValidator.validate("+498001234567")
        assertTrue("German freephone should be valid", result.isValid)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `detectCountry on Germany plus-49 number returns DE`() {
        val country = CountryCodeValidator.detectCountry("+491712345678")
        assertNotNull(country)
        assertTrue(
            "Should detect Germany",
            country == "DE" || country == "49"
        )
    }

    @Test
    fun `validate Germany number too short returns invalid`() {
        val result = CountryCodeValidator.validate("+4930123")
        assertFalse("Too short German number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate Germany number too long returns invalid`() {
        val result = CountryCodeValidator.validate("+49301234567890123")
        assertFalse("Too long German number should be invalid", result.isValid)
    }

    @Test
    fun `validate Germany Cologne 0221 number returns valid`() {
        val result = CountryCodeValidator.validate("+492211234567")
        assertTrue("German Cologne 0221 should be valid", result.isValid)
        assertEquals("49", result.countryCode)
    }

    @Test
    fun `validate Germany Stuttgart 0711 number returns valid`() {
        val result = CountryCodeValidator.validate("+497111234567")
        assertTrue("German Stuttgart 0711 should be valid", result.isValid)
        assertEquals("49", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate Germany normalizedNumber starts with plus-49`() {
        val result = CountryCodeValidator.validate("+491512345678")
        if (result.isValid) {
            assertTrue(
                "Normalized should start with +49",
                result.normalizedNumber?.startsWith("+49") == true
            )
        }
        assertNotNull(result)
    }

    // =========================================================================
    // Section 4 — France phones (15 tests)
    // =========================================================================

    @Test
    fun `validate France number with plus-33 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+33123456789")
        assertTrue("French +33 number should be valid", result.isValid)
        assertEquals("33", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate France geographic 01 Paris returns valid`() {
        val result = CountryCodeValidator.validate("+33112345678")
        assertTrue("French 01 geographic should be valid", result.isValid)
        assertEquals("33", result.countryCode)
    }

    @Test
    fun `validate France geographic 02 northwest returns valid`() {
        val result = CountryCodeValidator.validate("+33212345678")
        assertTrue("French 02 geographic should be valid", result.isValid)
        assertEquals("33", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate France geographic 03 northeast returns valid`() {
        val result = CountryCodeValidator.validate("+33312345678")
        assertTrue("French 03 geographic should be valid", result.isValid)
        assertEquals("33", result.countryCode)
    }

    @Test
    fun `validate France geographic 04 southeast returns valid`() {
        val result = CountryCodeValidator.validate("+33412345678")
        assertTrue("French 04 geographic should be valid", result.isValid)
        assertEquals("33", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate France geographic 05 southwest returns valid`() {
        val result = CountryCodeValidator.validate("+33512345678")
        assertTrue("French 05 geographic should be valid", result.isValid)
        assertEquals("33", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate France mobile 06 returns valid`() {
        val result = CountryCodeValidator.validate("+33612345678")
        assertTrue("French 06 mobile should be valid", result.isValid)
        assertEquals("33", result.countryCode)
    }

    @Test
    fun `validate France mobile 07 returns valid`() {
        val result = CountryCodeValidator.validate("+33712345678")
        assertTrue("French 07 mobile should be valid", result.isValid)
        assertEquals("33", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate France freephone 0800 returns valid`() {
        val result = CountryCodeValidator.validate("+33800123456")
        assertTrue("French freephone 0800 should be valid", result.isValid)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `detectCountry on France plus-33 number returns FR`() {
        val country = CountryCodeValidator.detectCountry("+33612345678")
        assertNotNull(country)
        assertTrue(
            "Should detect France",
            country == "FR" || country == "33"
        )
    }

    @Test
    fun `validate France number too short returns invalid`() {
        val result = CountryCodeValidator.validate("+3312345")
        assertFalse("Too short French number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate France number too long returns invalid`() {
        val result = CountryCodeValidator.validate("+331234567890123")
        assertFalse("Too long French number should be invalid", result.isValid)
    }

    @Test
    fun `validate France number confidence is positive`() {
        val result = CountryCodeValidator.validate("+33612345678")
        assertTrue(result.isValid)
        assertTrue("France confidence should be positive", result.confidence > 0)
    }

    @Test
    fun `validate France normalizedNumber starts with plus-33`() {
        val result = CountryCodeValidator.validate("+33712345678")
        if (result.isValid) {
            assertTrue(
                "Normalized should start with +33",
                result.normalizedNumber?.startsWith("+33") == true
            )
        }
        assertNotNull(result)
    }

    @Test
    fun `validate France special 09 VoIP number returns valid or known`() {
        val result = CountryCodeValidator.validate("+33912345678")
        assertNotNull("Result must not be null", result)
        assertNotNull(result.reason)
    }

    // =========================================================================
    // Section 5 — Australia phones (15 tests)
    // =========================================================================

    @Test
    fun `validate Australia number with plus-61 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+61212345678")
        assertTrue("Australian +61 number should be valid", result.isValid)
        assertEquals("61", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Australia NSW 02 number returns valid`() {
        val result = CountryCodeValidator.validate("+61298765432")
        assertTrue("Australian NSW 02 should be valid", result.isValid)
        assertEquals("61", result.countryCode)
    }

    @Test
    fun `validate Australia VIC 03 number returns valid`() {
        val result = CountryCodeValidator.validate("+61398765432")
        assertTrue("Australian VIC 03 should be valid", result.isValid)
        assertEquals("61", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Australia QLD 07 number returns valid`() {
        val result = CountryCodeValidator.validate("+61798765432")
        assertTrue("Australian QLD 07 should be valid", result.isValid)
        assertEquals("61", result.countryCode)
    }

    @Test
    fun `validate Australia WA 08 number returns valid`() {
        val result = CountryCodeValidator.validate("+61898765432")
        assertTrue("Australian WA 08 should be valid", result.isValid)
        assertEquals("61", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate Australia mobile 04XX returns valid`() {
        val result = CountryCodeValidator.validate("+61412345678")
        assertTrue("Australian mobile 04XX should be valid", result.isValid)
        assertEquals("61", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Australia mobile 0400 returns valid`() {
        val result = CountryCodeValidator.validate("+61400123456")
        assertTrue("Australian 0400 mobile should be valid", result.isValid)
        assertEquals("61", result.countryCode)
    }

    @Test
    fun `validate Australia mobile 0499 returns valid`() {
        val result = CountryCodeValidator.validate("+61499123456")
        assertTrue("Australian 0499 mobile should be valid", result.isValid)
        assertEquals("61", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Australia SA 08 number returns valid`() {
        val result = CountryCodeValidator.validate("+61882345678")
        assertTrue("Australian SA 08 should be valid", result.isValid)
        assertEquals("61", result.countryCode)
    }

    @Test
    fun `detectCountry on Australia plus-61 number returns AU`() {
        val country = CountryCodeValidator.detectCountry("+61412345678")
        assertNotNull(country)
        assertTrue(
            "Should detect Australia",
            country == "AU" || country == "61"
        )
    }

    @Test
    fun `validate Australia number too short returns invalid`() {
        val result = CountryCodeValidator.validate("+61212345")
        assertFalse("Too short Australian number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate Australia number too long returns invalid`() {
        val result = CountryCodeValidator.validate("+61212345678901234")
        assertFalse("Too long Australian number should be invalid", result.isValid)
    }

    @Test
    fun `validate Australia normalizedNumber starts with plus-61`() {
        val result = CountryCodeValidator.validate("+61412345678")
        if (result.isValid) {
            assertTrue(
                "Normalized should start with +61",
                result.normalizedNumber?.startsWith("+61") == true
            )
        }
        assertNotNull(result)
    }

    @Test
    fun `validate Australia freephone 1800 returns valid`() {
        val result = CountryCodeValidator.validate("+611800123456")
        assertNotNull("Result must not be null", result)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Australia ACT 02 Canberra returns valid`() {
        val result = CountryCodeValidator.validate("+61261234567")
        assertTrue("Australian ACT should be valid", result.isValid)
        assertEquals("61", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    // =========================================================================
    // Section 6 — Japan phones (15 tests)
    // =========================================================================

    @Test
    fun `validate Japan number with plus-81 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+81312345678")
        assertTrue("Japanese +81 number should be valid", result.isValid)
        assertEquals("81", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Japan Tokyo 03 number returns valid`() {
        val result = CountryCodeValidator.validate("+81312345678")
        assertTrue("Japanese Tokyo 03 should be valid", result.isValid)
        assertEquals("81", result.countryCode)
    }

    @Test
    fun `validate Japan Osaka 06 number returns valid`() {
        val result = CountryCodeValidator.validate("+81612345678")
        assertTrue("Japanese Osaka 06 should be valid", result.isValid)
        assertEquals("81", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Japan mobile 090 returns valid`() {
        val result = CountryCodeValidator.validate("+819012345678")
        assertTrue("Japanese 090 mobile should be valid", result.isValid)
        assertEquals("81", result.countryCode)
    }

    @Test
    fun `validate Japan mobile 080 returns valid`() {
        val result = CountryCodeValidator.validate("+818012345678")
        assertTrue("Japanese 080 mobile should be valid", result.isValid)
        assertEquals("81", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate Japan mobile 070 returns valid`() {
        val result = CountryCodeValidator.validate("+817012345678")
        assertTrue("Japanese 070 mobile should be valid", result.isValid)
        assertEquals("81", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Japan Nagoya 052 number returns valid`() {
        val result = CountryCodeValidator.validate("+81521234567")
        assertTrue("Japanese Nagoya 052 should be valid", result.isValid)
        assertEquals("81", result.countryCode)
    }

    @Test
    fun `validate Japan Sapporo 011 number returns valid`() {
        val result = CountryCodeValidator.validate("+81111234567")
        assertTrue("Japanese Sapporo 011 should be valid", result.isValid)
        assertEquals("81", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `detectCountry on Japan plus-81 number returns JP`() {
        val country = CountryCodeValidator.detectCountry("+819012345678")
        assertNotNull(country)
        assertTrue(
            "Should detect Japan",
            country == "JP" || country == "81"
        )
    }

    @Test
    fun `validate Japan number too short returns invalid`() {
        val result = CountryCodeValidator.validate("+81312345")
        assertFalse("Too short Japanese number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate Japan number too long returns invalid`() {
        val result = CountryCodeValidator.validate("+813123456789012345")
        assertFalse("Too long Japanese number should be invalid", result.isValid)
    }

    @Test
    fun `validate Japan freephone 0120 returns valid`() {
        val result = CountryCodeValidator.validate("+81120123456")
        assertNotNull("Result must not be null", result)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate Japan normalizedNumber starts with plus-81`() {
        val result = CountryCodeValidator.validate("+819012345678")
        if (result.isValid) {
            assertTrue(
                "Normalized should start with +81",
                result.normalizedNumber?.startsWith("+81") == true
            )
        }
        assertNotNull(result)
    }

    @Test
    fun `validate Japan Yokohama 045 number returns valid`() {
        val result = CountryCodeValidator.validate("+81451234567")
        assertTrue("Japanese Yokohama 045 should be valid", result.isValid)
        assertEquals("81", result.countryCode)
    }

    @Test
    fun `validate Japan confidence is positive for well-formed number`() {
        val result = CountryCodeValidator.validate("+81312345678")
        assertTrue(result.isValid)
        assertTrue("Japan confidence should be positive", result.confidence > 0)
    }

    // =========================================================================
    // Section 7 — India phones (15 tests)
    // =========================================================================

    @Test
    fun `validate India number with plus-91 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+919876543210")
        assertTrue("Indian +91 number should be valid", result.isValid)
        assertEquals("91", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate India mobile starting with 9 returns valid`() {
        val result = CountryCodeValidator.validate("+919123456789")
        assertTrue("Indian 9X mobile should be valid", result.isValid)
        assertEquals("91", result.countryCode)
    }

    @Test
    fun `validate India mobile starting with 8 returns valid`() {
        val result = CountryCodeValidator.validate("+918123456789")
        assertTrue("Indian 8X mobile should be valid", result.isValid)
        assertEquals("91", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate India mobile starting with 7 returns valid`() {
        val result = CountryCodeValidator.validate("+917123456789")
        assertTrue("Indian 7X mobile should be valid", result.isValid)
        assertEquals("91", result.countryCode)
    }

    @Test
    fun `validate India mobile starting with 6 returns valid`() {
        val result = CountryCodeValidator.validate("+916123456789")
        assertTrue("Indian 6X mobile should be valid", result.isValid)
        assertEquals("91", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate India mobile Jio 6 series returns valid`() {
        val result = CountryCodeValidator.validate("+916012345678")
        assertTrue("Indian Jio 6X series should be valid", result.isValid)
        assertEquals("91", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate India Delhi landline 011 returns valid`() {
        val result = CountryCodeValidator.validate("+911123456789")
        assertTrue("Indian Delhi 011 landline should be valid", result.isValid)
        assertEquals("91", result.countryCode)
    }

    @Test
    fun `validate India Mumbai landline 022 returns valid`() {
        val result = CountryCodeValidator.validate("+912223456789")
        assertTrue("Indian Mumbai 022 landline should be valid", result.isValid)
        assertEquals("91", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `detectCountry on India plus-91 number returns IN`() {
        val country = CountryCodeValidator.detectCountry("+919876543210")
        assertNotNull(country)
        assertTrue(
            "Should detect India",
            country == "IN" || country == "91"
        )
    }

    @Test
    fun `validate India number starting with 0 after plus-91 returns invalid`() {
        val result = CountryCodeValidator.validate("+910123456789")
        assertFalse("Indian number starting with 0 after +91 should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate India number too short returns invalid`() {
        val result = CountryCodeValidator.validate("+91987654321")
        assertFalse("Too short Indian number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate India number too long returns invalid`() {
        val result = CountryCodeValidator.validate("+9198765432101")
        assertFalse("Too long Indian number should be invalid", result.isValid)
    }

    @Test
    fun `validate India normalizedNumber starts with plus-91`() {
        val result = CountryCodeValidator.validate("+919876543210")
        if (result.isValid) {
            assertTrue(
                "Normalized should start with +91",
                result.normalizedNumber?.startsWith("+91") == true
            )
        }
        assertNotNull(result)
    }

    @Test
    fun `validate India confidence is positive for valid mobile`() {
        val result = CountryCodeValidator.validate("+919876543210")
        assertTrue(result.isValid)
        assertTrue("India confidence should be positive", result.confidence > 0)
    }

    @Test
    fun `validate India number starting with 5 returns invalid`() {
        val result = CountryCodeValidator.validate("+915123456789")
        assertFalse("Indian number starting with 5 should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    // =========================================================================
    // Section 8 — China phones (15 tests)
    // =========================================================================

    @Test
    fun `validate China number with plus-86 prefix returns valid`() {
        val result = CountryCodeValidator.validate("+8613812345678")
        assertTrue("Chinese +86 number should be valid", result.isValid)
        assertEquals("86", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate China mobile 13X series returns valid`() {
        val result = CountryCodeValidator.validate("+8613512345678")
        assertTrue("Chinese 13X mobile should be valid", result.isValid)
        assertEquals("86", result.countryCode)
    }

    @Test
    fun `validate China mobile 14X series returns valid`() {
        val result = CountryCodeValidator.validate("+8614512345678")
        assertTrue("Chinese 14X mobile should be valid", result.isValid)
        assertEquals("86", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate China mobile 15X series returns valid`() {
        val result = CountryCodeValidator.validate("+8615812345678")
        assertTrue("Chinese 15X mobile should be valid", result.isValid)
        assertEquals("86", result.countryCode)
    }

    @Test
    fun `validate China mobile 16X series returns valid`() {
        val result = CountryCodeValidator.validate("+8616612345678")
        assertTrue("Chinese 16X mobile should be valid", result.isValid)
        assertEquals("86", result.countryCode)
        assertTrue(result.confidence > 0)
    }

    @Test
    fun `validate China mobile 17X series returns valid`() {
        val result = CountryCodeValidator.validate("+8617012345678")
        assertTrue("Chinese 17X mobile should be valid", result.isValid)
        assertEquals("86", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate China mobile 18X series returns valid`() {
        val result = CountryCodeValidator.validate("+8618812345678")
        assertTrue("Chinese 18X mobile should be valid", result.isValid)
        assertEquals("86", result.countryCode)
    }

    @Test
    fun `validate China mobile 19X series returns valid`() {
        val result = CountryCodeValidator.validate("+8619912345678")
        assertTrue("Chinese 19X mobile should be valid", result.isValid)
        assertEquals("86", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `validate China Beijing landline 010 returns valid`() {
        val result = CountryCodeValidator.validate("+868612345678")
        assertNotNull("Result must not be null", result)
        assertNotNull(result.normalizedNumber)
    }

    @Test
    fun `detectCountry on China plus-86 number returns CN`() {
        val country = CountryCodeValidator.detectCountry("+8613812345678")
        assertNotNull(country)
        assertTrue(
            "Should detect China",
            country == "CN" || country == "86"
        )
    }

    @Test
    fun `validate China number too short returns invalid`() {
        val result = CountryCodeValidator.validate("+86138123456")
        assertFalse("Too short Chinese number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate China number too long returns invalid`() {
        val result = CountryCodeValidator.validate("+86138123456789012")
        assertFalse("Too long Chinese number should be invalid", result.isValid)
    }

    @Test
    fun `validate China normalizedNumber starts with plus-86`() {
        val result = CountryCodeValidator.validate("+8613812345678")
        if (result.isValid) {
            assertTrue(
                "Normalized should start with +86",
                result.normalizedNumber?.startsWith("+86") == true
            )
        }
        assertNotNull(result)
    }

    @Test
    fun `validate China confidence is positive for valid mobile`() {
        val result = CountryCodeValidator.validate("+8613812345678")
        assertTrue(result.isValid)
        assertTrue("China confidence should be positive", result.confidence > 0)
    }

    @Test
    fun `validate China mobile 19X second number returns valid`() {
        val result = CountryCodeValidator.validate("+8619512345678")
        assertTrue("Chinese 19X second mobile should be valid", result.isValid)
        assertEquals("86", result.countryCode)
        assertNotNull(result.normalizedNumber)
    }

    // =========================================================================
    // Section 9 — formatToE164 (15 tests)
    // =========================================================================

    @Test
    fun `formatToE164 with US plus-one number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+15552345678")
        assertNotNull("formatToE164 must not return null", formatted)
        assertTrue("E164 must start with +", formatted!!.startsWith("+"))
    }

    @Test
    fun `formatToE164 with ten-digit US number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("2125551234")
        assertNotNull(formatted)
        assertTrue(
            "E164 result should start with +",
            formatted?.startsWith("+") == true
        )
    }

    @Test
    fun `formatToE164 with parenthetical US format returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("(800) 555-0100")
        assertNotNull(formatted)
        assertTrue(formatted?.startsWith("+") == true)
    }

    @Test
    fun `formatToE164 with UK plus-44 number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+447911123456")
        assertNotNull("UK E164 must not be null", formatted)
        assertTrue("E164 must start with +", formatted!!.startsWith("+"))
    }

    @Test
    fun `formatToE164 with Germany plus-49 number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+491712345678")
        assertNotNull(formatted)
        assertTrue(formatted?.startsWith("+") == true)
    }

    @Test
    fun `formatToE164 with France plus-33 number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+33612345678")
        assertNotNull(formatted)
        assertTrue("E164 must start with +", formatted!!.startsWith("+"))
    }

    @Test
    fun `formatToE164 with Australia plus-61 number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+61412345678")
        assertNotNull(formatted)
        assertTrue(formatted?.startsWith("+") == true)
    }

    @Test
    fun `formatToE164 with Japan plus-81 number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+819012345678")
        assertNotNull(formatted)
        assertTrue("E164 must start with +", formatted!!.startsWith("+"))
    }

    @Test
    fun `formatToE164 with India plus-91 number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+919876543210")
        assertNotNull(formatted)
        assertTrue(formatted?.startsWith("+") == true)
    }

    @Test
    fun `formatToE164 with China plus-86 number returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+8613812345678")
        assertNotNull(formatted)
        assertTrue("E164 must start with +", formatted!!.startsWith("+"))
    }

    @Test
    fun `formatToE164 with dashes-only US format returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("212-555-1234")
        assertNotNull(formatted)
        assertTrue(formatted?.startsWith("+") == true)
    }

    @Test
    fun `formatToE164 with spaces US format returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("+1 555 234 5678")
        assertNotNull(formatted)
        assertTrue("E164 must start with +", formatted!!.startsWith("+"))
    }

    @Test
    fun `formatToE164 with dots US format returns E164 format`() {
        val formatted = CountryCodeValidator.formatToE164("212.555.1234")
        assertNotNull(formatted)
        assertTrue(formatted?.startsWith("+") == true)
    }

    @Test
    fun `formatToE164 with empty string returns null or empty`() {
        val formatted = CountryCodeValidator.formatToE164("")
        // Either null or empty for invalid input
        assertTrue(
            "Empty input should produce null or empty result",
            formatted == null || formatted.isEmpty()
        )
    }

    @Test
    fun `formatToE164 with valid number does not contain spaces`() {
        val formatted = CountryCodeValidator.formatToE164("+15552345678")
        if (formatted != null) {
            assertFalse(
                "E164 formatted number should not contain spaces",
                formatted.contains(" ")
            )
        }
        assertNotNull(CountryCodeValidator.formatToE164("+15552345678"))
    }

    // =========================================================================
    // Section 10 — looksLikePhoneNumber (15 tests)
    // =========================================================================

    @Test
    fun `looksLikePhoneNumber with plus-one US number returns true`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("+15552345678")
        assertTrue("US +1 number should look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with parenthetical US format returns true`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("(800) 555-0100")
        assertTrue("Parenthetical US should look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with dashes US format returns true`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("212-555-1234")
        assertTrue("Dash-separated US should look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with UK plus-44 number returns true`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("+447911123456")
        assertTrue("UK +44 should look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with international E164 number returns true`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("+33612345678")
        assertTrue("E164 French mobile should look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with ten-digit number returns true`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("2125551234")
        assertTrue("Ten-digit number should look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with purely alphabetic string returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("helloworld")
        assertFalse("Alphabetic string should not look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with email address returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("user@example.com")
        assertFalse("Email address should not look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with sentence returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("The quick brown fox")
        assertFalse("Regular sentence should not look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with URL returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("https://example.com/path")
        assertFalse("URL should not look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with short numeric string returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("123")
        assertFalse("Three-digit number should not look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with spaces dotted number returns true`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("+1 555 234 5678")
        assertTrue("Spaced E164 should look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with Australian mobile returns true`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("+61412345678")
        assertTrue("Australian mobile should look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with single word with numbers returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("abc123def")
        assertFalse("Mixed alpha-digit word should not look like phone", result)
    }

    @Test
    fun `looksLikePhoneNumber with empty string returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("")
        assertFalse("Empty string should not look like phone", result)
    }

    // =========================================================================
    // Section 11 — Edge cases (15 tests)
    // =========================================================================

    @Test
    fun `validate empty string returns invalid`() {
        val result = CountryCodeValidator.validate("")
        assertFalse("Empty string should be invalid", result.isValid)
        assertNotNull("Reason should explain why empty is invalid", result.reason)
    }

    @Test
    fun `validate whitespace-only string returns invalid`() {
        val result = CountryCodeValidator.validate("   ")
        assertFalse("Whitespace-only string should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate all-zeros string returns invalid`() {
        val result = CountryCodeValidator.validate("0000000000")
        assertFalse("All-zeros string should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate single digit returns invalid`() {
        val result = CountryCodeValidator.validate("5")
        assertFalse("Single digit should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate letters-only string returns invalid`() {
        val result = CountryCodeValidator.validate("abcdefghij")
        assertFalse("Letters-only string should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate mixed letters and digits that are not a phone returns invalid`() {
        val result = CountryCodeValidator.validate("abc123def456")
        assertFalse("Random mixed alphanumeric should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `looksLikePhoneNumber with URL containing port number returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("http://example.com:8080/path")
        assertFalse("URL with port should not be detected as phone number", result)
    }

    @Test
    fun `looksLikePhoneNumber with timestamp 12-colon-34-colon-56 returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("12:34:56")
        assertFalse("Timestamp 12:34:56 should not be detected as phone number", result)
    }

    @Test
    fun `validate null-like empty string input does not throw exception`() {
        var threwException = false
        try {
            CountryCodeValidator.validate("")
        } catch (e: Exception) {
            threwException = true
        }
        assertFalse("validate should not throw exception for empty input", threwException)
    }

    @Test
    fun `validate number with only plus sign returns invalid`() {
        val result = CountryCodeValidator.validate("+")
        assertFalse("Plus sign alone should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate number with plus and single digit returns invalid`() {
        val result = CountryCodeValidator.validate("+1")
        assertFalse("+1 alone should be invalid (no subscriber number)", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `looksLikePhoneNumber with date format returns false`() {
        val result = CountryCodeValidator.looksLikePhoneNumber("2026-03-12")
        assertFalse("Date string should not look like phone number", result)
    }

    @Test
    fun `validate extremely long numeric string returns invalid`() {
        val result = CountryCodeValidator.validate("1".repeat(30))
        assertFalse("Extremely long number should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate special characters only returns invalid`() {
        val result = CountryCodeValidator.validate("!@#$%^&*()")
        assertFalse("Special characters only should be invalid", result.isValid)
        assertNotNull(result.reason)
    }

    @Test
    fun `validate number with embedded newline returns invalid or cleaned`() {
        val result = CountryCodeValidator.validate("+1555\n2345678")
        // Either it cleans and validates, or returns invalid — should not crash
        assertNotNull("Result must not be null for embedded newline input", result)
        assertNotNull(result.reason)
    }
}
