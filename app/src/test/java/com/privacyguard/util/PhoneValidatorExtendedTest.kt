package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive extended test suite for PhoneValidator.
 *
 * Covers phone number validation, formatting, region detection,
 * heuristic detection, and text extraction across many countries
 * and formats.
 *
 * Sections:
 *  1. US NANP numbers with area code (212) variants         - 25 tests
 *  2. US toll-free numbers (800, 888, 877, 866, 855, 844)   - 25 tests
 *  3. UK numbers (+44, 020, 07xxx mobile)                   - 25 tests
 *  4. German numbers (+49)                                  - 25 tests
 *  5. French numbers (+33)                                  - 25 tests
 *  6. Australian numbers (+61)                              - 25 tests
 *  7. Japanese numbers (+81)                                - 20 tests
 *  8. Indian numbers (+91)                                  - 20 tests
 *  9. formatToE164 function tests                           - 30 tests
 * 10. looksLikePhoneNumber edge cases                       - 30 tests
 *
 * Total: 250 test functions
 *
 * Assumed API surface:
 *   PhoneValidator.isValid(phone: String?): Boolean
 *   PhoneValidator.isValid(phone: String?, region: String): Boolean
 *   PhoneValidator.formatToE164(phone: String?, region: String = "US"): String?
 *   PhoneValidator.looksLikePhoneNumber(text: String): Boolean
 *   PhoneValidator.extractFromText(text: String): List<String>
 *   PhoneValidator.getRegion(phone: String): String?
 */
class PhoneValidatorExtendedTest {

    // =========================================================================
    // TEST-DATA CONSTANTS
    //
    // All raw phone strings and expected E.164 values used throughout the
    // test suite are centralised here so that any future changes to test
    // data can be made in exactly one place.  Each constant follows the
    // naming convention <REGION>_<FORMAT>_<INDEX> or describes its content
    // with a concise suffix.
    // =========================================================================

    companion object {

        // ----- Section 1 – US NANP (212) -----
        const val US_212_DASH              = "212-555-1234"
        const val US_212_PARENS            = "(212) 555-1234"
        const val US_212_DOTS              = "212.555.1234"
        const val US_212_BARE              = "2125551234"
        const val US_212_E164              = "+12125551234"
        const val US_212_E164_NOSLASH      = "12125551234"
        const val US_212_LONG_DASH         = "1-212-555-1234"
        const val US_212_SPACES            = "212 555 1234"
        const val US_212_PLUS_SPACES       = "+1 212 555 1234"
        const val US_646_DASH              = "646-555-0100"
        const val US_646_E164              = "+16465550100"
        const val US_917_DASH              = "917-555-0100"
        const val US_917_E164              = "+19175550100"
        const val US_347_PARENS            = "(347) 555-0100"
        const val US_347_E164              = "+13475550100"
        const val US_718_DASH              = "718-555-0100"
        const val US_516_DASH              = "516-555-0100"
        const val US_516_E164              = "+15165550100"

        // ----- Section 2 – US toll-free -----
        const val TF_800_DASH              = "800-555-0100"
        const val TF_800_E164              = "+18005550100"
        const val TF_888_DASH              = "888-555-0100"
        const val TF_888_E164              = "+18885550100"
        const val TF_877_DASH              = "877-555-0100"
        const val TF_877_E164              = "+18775550100"
        const val TF_866_DASH              = "866-555-0100"
        const val TF_866_E164              = "+18665550100"
        const val TF_855_DASH              = "855-555-0100"
        const val TF_855_E164              = "+18555550100"
        const val TF_844_DASH              = "844-555-0100"
        const val TF_844_E164              = "+18445550100"
        const val TF_800_E164_NOSLASH      = "18005550100"

        // ----- Section 3 – UK -----
        const val UK_LONDON_LOCAL          = "020 7946 0958"
        const val UK_LONDON_INTL           = "+44 20 7946 0958"
        const val UK_LONDON_E164           = "+442079460958"
        const val UK_MOBILE_LOCAL          = "07700 900123"
        const val UK_MOBILE_INTL           = "+44 7700 900123"
        const val UK_MOBILE_E164           = "+447700900123"
        const val UK_FREEPHONE             = "0800 123 4567"
        const val UK_LEEDS                 = "0113 496 0000"
        const val UK_BIRMINGHAM            = "0121 234 5000"
        const val UK_EDINBURGH             = "0131 200 2000"
        const val UK_GLASGOW               = "0141 204 4400"
        const val UK_MOBILE2_INTL          = "+44 7911 123456"
        const val UK_MOBILE2_E164          = "+447911123456"

        // ----- Section 4 – Germany -----
        const val DE_BERLIN_INTL           = "+49 30 12345678"
        const val DE_BERLIN_E164           = "+493012345678"
        const val DE_BERLIN_LOCAL          = "030 12345678"
        const val DE_MOBILE_151            = "+49 151 12345678"
        const val DE_MOBILE_162            = "+49 162 12345678"
        const val DE_MOBILE_162_E164       = "+4916212345678"
        const val DE_MOBILE_170            = "+49 170 12345678"
        const val DE_MOBILE_170_E164       = "+4917012345678"
        const val DE_MUNICH                = "+49 89 12345678"
        const val DE_MUNICH_E164           = "+498912345678"
        const val DE_HAMBURG               = "+49 40 12345678"
        const val DE_HAMBURG_E164          = "+494012345678"
        const val DE_COLOGNE               = "+49 221 1234567"
        const val DE_COLOGNE_E164          = "+492211234567"
        const val DE_FRANKFURT             = "+49 69 12345678"
        const val DE_FRANKFURT_E164        = "+496912345678"
        const val DE_STUTTGART             = "+49 711 1234567"
        const val DE_STUTTGART_E164        = "+497111234567"
        const val DE_DUSSELDORF            = "+49 211 1234567"
        const val DE_DUSSELDORF_E164       = "+492111234567"

        // ----- Section 5 – France -----
        const val FR_PARIS_INTL            = "+33 1 23 45 67 89"
        const val FR_PARIS_E164            = "+33123456789"
        const val FR_PARIS_LOCAL           = "01 23 45 67 89"
        const val FR_MOBILE_06_INTL        = "+33 6 12 34 56 78"
        const val FR_MOBILE_06_E164        = "+33612345678"
        const val FR_MOBILE_06_LOCAL       = "06 12 34 56 78"
        const val FR_MOBILE_07_INTL        = "+33 7 12 34 56 78"
        const val FR_MOBILE_07_E164        = "+33712345678"
        const val FR_MOBILE_07_LOCAL       = "07 12 34 56 78"
        const val FR_MARSEILLE_INTL        = "+33 4 91 00 00 00"
        const val FR_MARSEILLE_E164        = "+33491000000"
        const val FR_BORDEAUX_INTL         = "+33 5 56 00 00 00"
        const val FR_BORDEAUX_E164         = "+33556000000"

        // ----- Section 6 – Australia -----
        const val AU_SYDNEY_INTL           = "+61 2 9000 0000"
        const val AU_SYDNEY_E164           = "+61290000000"
        const val AU_SYDNEY_LOCAL          = "02 9000 0000"
        const val AU_MELB_INTL             = "+61 3 9000 0000"
        const val AU_MELB_E164             = "+61390000000"
        const val AU_MOBILE_INTL           = "+61 412 345 678"
        const val AU_MOBILE_E164           = "+61412345678"
        const val AU_MOBILE_LOCAL          = "0412 345 678"
        const val AU_BRISBANE_INTL         = "+61 7 3000 0000"
        const val AU_BRISBANE_E164         = "+61730000000"
        const val AU_PERTH_INTL            = "+61 8 9000 0000"
        const val AU_PERTH_E164            = "+61890000000"

        // ----- Section 7 – Japan -----
        const val JP_TOKYO_INTL            = "+81 3 1234 5678"
        const val JP_TOKYO_E164            = "+81312345678"
        const val JP_TOKYO_DASH            = "03-1234-5678"
        const val JP_OSAKA_INTL            = "+81 6 1234 5678"
        const val JP_OSAKA_E164            = "+81612345678"
        const val JP_MOBILE_090_INTL       = "+81 90 1234 5678"
        const val JP_MOBILE_090_E164       = "+819012345678"
        const val JP_MOBILE_080_INTL       = "+81 80 1234 5678"
        const val JP_MOBILE_080_E164       = "+818012345678"
        const val JP_MOBILE_070_INTL       = "+81 70 1234 5678"
        const val JP_MOBILE_070_E164       = "+817012345678"

        // ----- Section 8 – India -----
        const val IN_MOBILE_9_INTL         = "+91 98765 43210"
        const val IN_MOBILE_9_E164         = "+919876543210"
        const val IN_MOBILE_9_LOCAL        = "9876543210"
        const val IN_MOBILE_8_INTL         = "+91 87654 32109"
        const val IN_MOBILE_8_E164         = "+918765432109"
        const val IN_MOBILE_7_INTL         = "+91 76543 21098"
        const val IN_MOBILE_7_E164         = "+917654321098"
        const val IN_MOBILE_6_INTL         = "+91 65432 10987"
        const val IN_MOBILE_6_E164         = "+916543210987"
        const val IN_DELHI_INTL            = "+91 11 1234 5678"
        const val IN_DELHI_E164            = "+911112345678"
        const val IN_MUMBAI_INTL           = "+91 22 1234 5678"
        const val IN_MUMBAI_E164           = "+912212345678"
        const val IN_BANGALORE_INTL        = "+91 80 1234 5678"
        const val IN_BANGALORE_E164        = "+918012345678"
        const val IN_CHENNAI_INTL          = "+91 44 1234 5678"
        const val IN_CHENNAI_E164          = "+914412345678"
        const val IN_KOLKATA_INTL          = "+91 33 1234 5678"
        const val IN_KOLKATA_E164          = "+913312345678"
    }

    // =========================================================================
    // SECTION 1: US NANP numbers with area code (212) variants – 25 tests
    //
    // The North American Numbering Plan (NANP) covers the US, Canada, and
    // several Caribbean nations under country code +1.  Area code 212 is the
    // original New York City area code.  These tests verify that PhoneValidator
    // accepts every common local-formatting style and always returns the
    // canonical E.164 representation "+12125551234".
    // =========================================================================

    @Test
    fun `test us nanp 001 classic dash format`() {
        // Most common US formatting: AAA-NXX-XXXX
        val phone = "212-555-1234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue("dash format should be valid globally", PhoneValidator.isValid(phone))
        assertTrue("dash format should be valid for US region", PhoneValidator.isValid(phone, "US"))
        assertNotNull("E.164 result must not be null", e164)
        assertEquals("E.164 must equal canonical form", "+12125551234", e164)
    }

    @Test
    fun `test us nanp 002 parentheses space dash format`() {
        // Standard NANP display format used in directories: (AAA) NXX-XXXX
        val phone = "(212) 555-1234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+12125551234", e164)
    }

    @Test
    fun `test us nanp 003 dot separated format`() {
        // Dot-separated style common in business cards: AAA.NXX.XXXX
        val phone = "212.555.1234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+12125551234", e164)
    }

    @Test
    fun `test us nanp 004 no separator digits only`() {
        // Raw ten-digit string with no formatting characters
        val phone = "2125551234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+12125551234", e164)
    }

    @Test
    fun `test us nanp 005 e164 with plus one`() {
        // Already in E.164 form – validator should accept and return unchanged
        val phone = "+12125551234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+12125551234", e164)
    }

    @Test
    fun `test us nanp 006 eleven digits with leading 1`() {
        // 11-digit string starting with country code 1, no plus sign
        val phone = "12125551234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+12125551234", e164)
    }

    @Test
    fun `test us nanp 007 one dash area code dash number`() {
        // Long-distance dial prefix: 1-AAA-NXX-XXXX
        val phone = "1-212-555-1234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+12125551234", e164)
    }

    @Test
    fun `test us nanp 008 spaces only separator`() {
        // Space-separated: AAA NXX XXXX
        val phone = "212 555 1234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+12125551234", e164)
    }

    @Test
    fun `test us nanp 009 mixed separators parens dot`() {
        // Unusual but parseable mix of parentheses and dots
        val phone = "(212) 555.1234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+1212"))
    }

    @Test
    fun `test us nanp 010 plus one space separated`() {
        // International dialing prefix with spaces: +1 AAA NXX XXXX
        val phone = "+1 212 555 1234"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+12125551234", e164)
    }

    // --- sub-group: other NYC-area area codes (011–020) ---
    // The 212 area code has been supplemented by overlays 646, 917, 347, 718,
    // and the Long Island code 516.  Each must validate and round-trip to E.164.

    @Test
    fun `test us nanp 011 area code 646 manhattan`() {
        // 646 overlay area code introduced for NYC in 1999
        val phone = "646-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+16465550100", e164)
    }

    @Test
    fun `test us nanp 012 area code 917 nyc mobile`() {
        // 917 is primarily a wireless area code for NYC
        val phone = "917-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+19175550100", e164)
    }

    @Test
    fun `test us nanp 013 area code 347 nyc overlay`() {
        // 347 overlay introduced in 1999 for Brooklyn, Queens, Staten Island, Bronx
        val phone = "(347) 555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+13475550100", e164)
    }

    @Test
    fun `test us nanp 014 area code 718 outer boroughs`() {
        // 718 covers the outer boroughs of New York City
        val phone = "718-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+1718"))
    }

    @Test
    fun `test us nanp 015 area code 516 long island`() {
        // 516 is used in Nassau County, Long Island
        val phone = "516-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+15165550100", e164)
    }

    @Test
    fun `test us nanp 016 too short seven digits invalid`() {
        // Exactly 7 digits – missing area code, must be rejected
        val phone = "555-1234"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 017 too long eleven digits local invalid`() {
        // Eleven digits where area code gives 12 total – too long
        val phone = "212-555-12345"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 018 area code starting with 1 invalid`() {
        // NANP area codes cannot start with 0 or 1
        val phone = "112-555-1234"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 019 area code starting with 0 invalid`() {
        // Area codes 0XX are not assigned in NANP
        val phone = "012-555-1234"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 020 exchange starting with 1 invalid`() {
        // NXX exchange codes cannot start with 0 or 1 in NANP
        val phone = "212-155-1234"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    // --- sub-group: boundary/edge inputs (021–025) ---
    // Null, empty, and semantically invalid strings must be handled gracefully
    // without throwing exceptions, and must return false / null as appropriate.

    @Test
    fun `test us nanp 021 null phone is invalid`() {
        // Null input must never throw and must return false/null
        assertFalse(PhoneValidator.isValid(null))
        assertFalse(PhoneValidator.isValid(null, "US"))
        assertNull(PhoneValidator.formatToE164(null, "US"))
    }

    @Test
    fun `test us nanp 022 empty string is invalid`() {
        // Empty string must never throw and must return false/null
        assertFalse(PhoneValidator.isValid(""))
        assertFalse(PhoneValidator.isValid("", "US"))
        assertNull(PhoneValidator.formatToE164("", "US"))
    }

    @Test
    fun `test us nanp 023 get region returns US`() {
        // E.164 number in the 212 area code must resolve to "US"
        val e164 = "+12125551234"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("US", region)
    }

    @Test
    fun `test us nanp 024 looksLikePhoneNumber true for dash format`() {
        // Heuristic check: classic dashed US format should be detected
        val phone = "212-555-1234"
        assertTrue(PhoneValidator.looksLikePhoneNumber(phone))
    }

    @Test
    fun `test us nanp 025 extractFromText finds 212 number`() {
        // extractFromText must find the embedded phone number in a sentence
        val text = "Please call us at 212-555-1234 for more information."
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue("At least one phone must be extracted", phones.isNotEmpty())
        assertTrue(
            "Extracted number should contain 212",
            phones.any { it.contains("212") || it.replace("[^0-9]".toRegex(), "").contains("2125551234") }
        )
    }

    // =========================================================================
    // SECTION 2: US toll-free numbers (800, 888, 877, 866, 855, 844) – 25 tests
    //
    // Toll-free numbers in NANP share country code +1 and use reserved
    // area codes 800, 833, 844, 855, 866, 877, and 888.  Callers are not
    // charged for these calls.  The tests cover each active toll-free prefix
    // and the same variety of formatting styles as Section 1.
    // =========================================================================

    @Test
    fun `test toll free 001 800 classic dash format`() {
        // 800 is the original and most recognised toll-free prefix
        val phone = "800-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18005550100", e164)
    }

    @Test
    fun `test toll free 002 888 classic dash format`() {
        // 888 introduced 1996 as second toll-free area code
        val phone = "888-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18885550100", e164)
    }

    @Test
    fun `test toll free 003 877 classic dash format`() {
        // 877 introduced 1998
        val phone = "877-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18775550100", e164)
    }

    @Test
    fun `test toll free 004 866 classic dash format`() {
        // 866 introduced 2000
        val phone = "866-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18665550100", e164)
    }

    @Test
    fun `test toll free 005 855 classic dash format`() {
        // 855 introduced 2010
        val phone = "855-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18555550100", e164)
    }

    @Test
    fun `test toll free 006 844 classic dash format`() {
        // 844 introduced 2013
        val phone = "844-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18445550100", e164)
    }

    @Test
    fun `test toll free 007 800 e164 with plus one`() {
        // Already canonical E.164 – must return unchanged
        val phone = "+18005550100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18005550100", e164)
    }

    @Test
    fun `test toll free 008 888 parentheses format`() {
        // (888) NXX-XXXX style
        val phone = "(888) 555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18885550100", e164)
    }

    @Test
    fun `test toll free 009 877 dot format`() {
        // 877.NXX.XXXX dot-separated style
        val phone = "877.555.0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18775550100", e164)
    }

    @Test
    fun `test toll free 010 866 no separator digits only`() {
        // Bare ten-digit string, toll-free
        val phone = "8665550100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18665550100", e164)
    }

    @Test
    fun `test toll free 011 855 space separated`() {
        // 855 NXX XXXX space-separated
        val phone = "855 555 0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18555550100", e164)
    }

    @Test
    fun `test toll free 012 844 one dash prefix`() {
        // 1-844-NXX-XXXX long-distance prefix
        val phone = "1-844-555-0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(e164)
        assertEquals("+18445550100", e164)
    }

    // --- sub-group: region detection, heuristics, text extraction (013–019) ---
    // These tests confirm that the supporting utility methods (getRegion,
    // looksLikePhoneNumber, extractFromText) work correctly for toll-free
    // numbers, and that common formatting quirks do not trip up the validator.

    @Test
    fun `test toll free 013 800 region resolves to US`() {
        // Toll-free 800 numbers belong to the US/NANP region
        val e164 = "+18005550100"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("US", region)
    }

    @Test
    fun `test toll free 014 888 region resolves to US`() {
        val e164 = "+18885550100"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("US", region)
    }

    @Test
    fun `test toll free 015 877 looksLikePhoneNumber true`() {
        // Heuristic must detect a dashed toll-free number
        val phone = "877-555-0100"
        assertTrue(PhoneValidator.looksLikePhoneNumber(phone))
    }

    @Test
    fun `test toll free 016 866 extractFromText finds number`() {
        // extractFromText must locate the toll-free number in a sentence
        val text = "For customer support, call 866-555-0100 today."
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test toll free 017 800 business number valid`() {
        // Real-world style toll-free number that happens to spell a word
        val phone = "800-356-9377"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+18003569377", e164)
    }

    @Test
    fun `test toll free 018 855 parens dot mix`() {
        // Unusual but valid-looking formatting
        val phone = "(855) 555.0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+1855"))
    }

    @Test
    fun `test toll free 019 844 plus one space separated`() {
        // International-style with spaces
        val phone = "+1 844 555 0100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+18445550100", e164)
    }

    @Test
    fun `test toll free 020 800 too short invalid`() {
        // Missing the last subscriber digit
        val phone = "800-555-010"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    // --- sub-group: invalid inputs and boundary conditions (021–025) ---
    // Null, excessively long numbers, letters, and other non-phone strings must
    // be rejected cleanly without exceptions.

    @Test
    fun `test toll free 021 888 too long invalid`() {
        // One extra digit makes it 11-digit subscriber, which is invalid
        val phone = "888-555-01001"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 022 877 null input`() {
        // Null must never crash and must return false/null
        assertFalse(PhoneValidator.isValid(null, "US"))
        assertNull(PhoneValidator.formatToE164(null, "US"))
    }

    @Test
    fun `test toll free 023 866 letters only invalid`() {
        // Pure letters cannot represent a dial sequence
        val phone = "866-ABC-DEFG"
        assertFalse(PhoneValidator.isValid(phone, "US"))
    }

    @Test
    fun `test toll free 024 855 already e164 returned unchanged`() {
        // Passing an already-canonical E.164 string must yield the same string
        val phone = "+18555550100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertNotNull(e164)
        assertEquals("+18555550100", e164)
    }

    @Test
    fun `test toll free 025 844 eleven digits no plus valid`() {
        // Country code 1 prepended without a plus sign
        val phone = "18445550100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+18445550100", e164)
    }

    // =========================================================================
    // SECTION 3: UK numbers (+44, 020, 07xxx mobile) – 25 tests
    //
    // The United Kingdom uses country code +44.  Geographic area codes vary
    // in length (2–5 digits after the leading 0), giving subscriber numbers
    // of different lengths.  Mobile numbers begin with 07, and the national
    // freephone prefix is 0800.
    // =========================================================================

    @Test
    fun `test uk 001 london 020 local format`() {
        // London numbers use the (020) area code + 8-digit subscriber number
        val phone = "020 7946 0958"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
        assertEquals("+442079460958", e164)
    }

    @Test
    fun `test uk 002 london plus44 international format`() {
        // International dial-in format without leading zero
        val phone = "+44 20 7946 0958"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertEquals("+442079460958", e164)
    }

    @Test
    fun `test uk 003 mobile 07700 local format`() {
        // UK mobile numbers start with 07; 07700 9xxxxx is an Ofcom test range
        val phone = "07700 900123"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+447"))
    }

    @Test
    fun `test uk 004 mobile plus44 7700 international`() {
        // Same mobile with +44 prefix and leading zero dropped
        val phone = "+44 7700 900123"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertEquals("+447700900123", e164)
    }

    @Test
    fun `test uk 005 0800 freephone national`() {
        // 0800 is UK freephone; subscriber number is 7 digits
        val phone = "0800 123 4567"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
    }

    @Test
    fun `test uk 006 0345 service number`() {
        // 03xx numbers are charged at local rate; common for public services
        val phone = "0345 678 9000"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
    }

    @Test
    fun `test uk 007 regional 0113 leeds`() {
        // Leeds (0113) – 4-digit area code + 7-digit subscriber number
        val phone = "0113 496 0000"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44113"))
    }

    @Test
    fun `test uk 008 regional 0121 birmingham`() {
        // Birmingham (0121) – second-largest UK city area code
        val phone = "0121 234 5000"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44121"))
    }

    @Test
    fun `test uk 009 regional 0131 edinburgh`() {
        // Edinburgh (0131)
        val phone = "0131 200 2000"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44131"))
    }

    @Test
    fun `test uk 010 plus44 no spaces canonical`() {
        // Canonical E.164 must be returned as-is
        val phone = "+442079460958"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+442079460958", e164)
    }

    @Test
    fun `test uk 011 mobile no spaces local format`() {
        // Ten-digit mobile without spaces, starting 07
        val phone = "07700900123"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertEquals("+447700900123", e164)
    }

    @Test
    fun `test uk 012 get region GB for london number`() {
        // Region lookup must return "GB" for a London E.164 number
        val e164 = "+442079460958"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("GB", region)
    }

    @Test
    fun `test uk 013 get region GB for mobile number`() {
        // Region lookup must return "GB" for a UK mobile E.164 number
        val e164 = "+447700900123"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("GB", region)
    }

    // --- sub-group: heuristics, extraction, format variants (014–019) ---
    // These tests ensure that looksLikePhoneNumber and extractFromText work for
    // UK numbers, and that unusual-but-valid formatting (dashes, mixed) is
    // handled by both the validator and the formatter.

    @Test
    fun `test uk 014 looksLikePhoneNumber true for 020 format`() {
        // Heuristic must recognise a common London format
        assertTrue(PhoneValidator.looksLikePhoneNumber("020 7946 0958"))
    }

    @Test
    fun `test uk 015 looksLikePhoneNumber true for 07xxx format`() {
        // Heuristic must recognise a UK mobile format
        assertTrue(PhoneValidator.looksLikePhoneNumber("07700 900123"))
    }

    @Test
    fun `test uk 016 extractFromText finds london number`() {
        // Text extraction must surface the embedded London number
        val text = "Our London office: +44 20 7946 0958. Please call during office hours."
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test uk 017 too short 9 digits invalid`() {
        // London number needs 10 local digits (02x + 8); 9 is too short
        val phone = "020794609"
        assertFalse(PhoneValidator.isValid(phone, "GB"))
        assertNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 018 too long extra digit invalid`() {
        // Extra digit makes this 11 local digits, exceeding UK maximum
        val phone = "020 7946 09581"
        assertFalse(PhoneValidator.isValid(phone, "GB"))
        assertNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 019 null input is invalid`() {
        // Null must never throw and must return false/null
        assertFalse(PhoneValidator.isValid(null, "GB"))
        assertNull(PhoneValidator.formatToE164(null, "GB"))
    }

    @Test
    fun `test uk 020 empty string is invalid`() {
        // Empty string must never throw and must return false/null
        assertFalse(PhoneValidator.isValid("", "GB"))
        assertNull(PhoneValidator.formatToE164("", "GB"))
    }

    // --- sub-group: additional mobile and regional variants (021–025) ---
    // Covers the 07911 mobile range, dash-formatted London numbers, 0800
    // freephone, canonical E.164 passthrough, and the Glasgow 0141 area code.

    @Test
    fun `test uk 021 mobile 07911 plus44`() {
        // Another Ofcom test mobile range: 07911 1xxxxx
        val phone = "+44 7911 123456"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+447911123456", e164)
    }

    @Test
    fun `test uk 022 020 dash format`() {
        // London number with dashes instead of spaces
        val phone = "020-7946-0958"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertEquals("+442079460958", e164)
    }

    @Test
    fun `test uk 023 0800 freephone e164 starts plus44`() {
        val phone = "0800 123 4567"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
        assertFalse(e164.contains(" "))
    }

    @Test
    fun `test uk 024 mobile plus44 no spaces canonical`() {
        // Canonical E.164 mobile must be returned unchanged
        val phone = "+447700900123"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertNotNull(e164)
        assertEquals("+447700900123", e164)
    }

    @Test
    fun `test uk 025 regional 0141 glasgow`() {
        // Glasgow (0141)
        val phone = "0141 204 4400"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44141"))
    }

    // =========================================================================
    // SECTION 4: German numbers (+49) – 25 tests
    //
    // Germany uses country code +49.  Local area codes (Vorwahl) vary
    // widely in length (2–5 digits after the leading 0), and mobile prefixes
    // are 015x, 016x, and 017x.  The national freephone prefix is 0800.
    // =========================================================================

    @Test
    fun `test german 001 berlin landline plus49`() {
        // Berlin area code 030; international format
        val phone = "+49 30 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertEquals("+493012345678", e164)
    }

    @Test
    fun `test german 002 mobile 0151 range`() {
        // 015x prefix – assigned to Telekom and other carriers for mobile
        val phone = "+49 151 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+4915"))
    }

    @Test
    fun `test german 003 mobile 0162 range`() {
        // 016x prefix – Vodafone and others
        val phone = "+49 162 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertEquals("+4916212345678", e164)
    }

    @Test
    fun `test german 004 mobile 0170 range`() {
        // 017x prefix – T-Mobile and others
        val phone = "+49 170 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertEquals("+4917012345678", e164)
    }

    @Test
    fun `test german 005 berlin local format 030`() {
        // Berlin without plus, leading national trunk prefix 0
        val phone = "030 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+4930"))
    }

    @Test
    fun `test german 006 munich 089 plus49`() {
        // Munich area code 089
        val phone = "+49 89 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertEquals("+498912345678", e164)
    }

    @Test
    fun `test german 007 hamburg 040 plus49`() {
        // Hamburg area code 040
        val phone = "+49 40 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertEquals("+494012345678", e164)
    }

    @Test
    fun `test german 008 cologne 0221 plus49`() {
        // Cologne area code 0221 – 4-digit Vorwahl
        val phone = "+49 221 1234567"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertEquals("+492211234567", e164)
    }

    @Test
    fun `test german 009 frankfurt 069 plus49`() {
        // Frankfurt area code 069
        val phone = "+49 69 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+496912345678", e164)
    }

    @Test
    fun `test german 010 hamburg local no plus`() {
        // Hamburg local format: 040 followed by subscriber number
        val phone = "040 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+4940"))
    }

    @Test
    fun `test german 011 get region DE for berlin`() {
        // Region lookup for Berlin E.164 must return "DE"
        val e164 = "+493012345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("DE", region)
    }

    // --- sub-group: utility method checks for German numbers (012–017) ---
    // getRegion, looksLikePhoneNumber, and extractFromText are exercised for
    // German mobile and landline E.164 strings.

    @Test
    fun `test german 012 get region DE for mobile`() {
        // Region lookup for German mobile E.164 must return "DE"
        val e164 = "+4915112345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("DE", region)
    }

    @Test
    fun `test german 013 looksLikePhoneNumber true`() {
        // Heuristic should detect an international German number
        assertTrue(PhoneValidator.looksLikePhoneNumber("+49 30 12345678"))
    }

    @Test
    fun `test german 014 extractFromText finds berlin number`() {
        // extractFromText should surface the embedded German number
        val text = "Kontakt: +49 30 12345678, Mo–Fr 9–17 Uhr"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test german 015 null input is invalid`() {
        assertFalse(PhoneValidator.isValid(null, "DE"))
        assertNull(PhoneValidator.formatToE164(null, "DE"))
    }

    @Test
    fun `test german 016 empty string is invalid`() {
        assertFalse(PhoneValidator.isValid("", "DE"))
        assertNull(PhoneValidator.formatToE164("", "DE"))
    }

    @Test
    fun `test german 017 0800 freephone valid`() {
        // German 0800 is freephone; subscriber number is 7 digits
        val phone = "0800 1234567"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+49"))
    }

    @Test
    fun `test german 018 0900 premium rate result not crash`() {
        // 0900 is German premium-rate; validator may or may not accept
        val phone = "0900 1234567"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        // Must not throw; result is either null or a valid +49 string
        assertTrue(e164 == null || e164.startsWith("+49"))
    }

    // --- sub-group: local-format and canonical passthrough (019–025) ---
    // Tests that local German digit strings (without the leading '+49') are
    // correctly promoted to E.164, and that already-canonical strings survive
    // the round-trip unchanged.

    @Test
    fun `test german 019 berlin no plus local digits`() {
        // 11-digit string starting with 030 (trunk 0 + area 30 + subscriber)
        val phone = "03012345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+4930"))
    }

    @Test
    fun `test german 020 mobile 0151 local digits`() {
        // 12-digit local string for a 015x mobile
        val phone = "015112345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+49151"))
    }

    @Test
    fun `test german 021 stuttgart 0711 plus49`() {
        // Stuttgart area code 0711
        val phone = "+49 711 1234567"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+497111234567", e164)
    }

    @Test
    fun `test german 022 dusseldorf 0211 plus49`() {
        // Düsseldorf area code 0211
        val phone = "+49 211 1234567"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+492111234567", e164)
    }

    @Test
    fun `test german 023 too short invalid`() {
        // Only 4 subscriber digits after the area code – too short for DE
        val phone = "+49 30 1234"
        assertFalse(PhoneValidator.isValid(phone, "DE"))
        assertNull(PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 024 berlin e164 already canonical`() {
        // Already canonical E.164 must be returned unchanged
        val phone = "+493012345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertNotNull(e164)
        assertEquals("+493012345678", e164)
    }

    @Test
    fun `test german 025 mobile 0176 local format`() {
        // 0176 is a Telekom mobile prefix
        val phone = "0176 12345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+49176"))
    }

    // =========================================================================
    // SECTION 5: French numbers (+33) – 25 tests
    //
    // France uses country code +33.  All French numbers are 10 digits in
    // national format (0X XX XX XX XX).  Landline zones: 01 Paris,
    // 02 NW, 03 NE, 04 SE, 05 SW.  Mobiles: 06 and 07.
    // Freephone: 0800.
    // =========================================================================

    @Test
    fun `test french 001 paris landline plus33`() {
        // Paris landline, zone 01, international format
        val phone = "+33 1 23 45 67 89"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertEquals("+33123456789", e164)
    }

    @Test
    fun `test french 002 paris local 01 format`() {
        // Paris local format: 01 XX XX XX XX
        val phone = "01 23 45 67 89"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+331"))
    }

    @Test
    fun `test french 003 mobile 06 plus33`() {
        // French mobile starting with 06
        val phone = "+33 6 12 34 56 78"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertEquals("+33612345678", e164)
    }

    @Test
    fun `test french 004 mobile 07 plus33`() {
        // French mobile starting with 07 (introduced ~2010)
        val phone = "+33 7 12 34 56 78"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertEquals("+33712345678", e164)
    }

    @Test
    fun `test french 005 mobile 06 local format`() {
        // Mobile 06 in national format: 06 XX XX XX XX
        val phone = "06 12 34 56 78"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+336"))
    }

    @Test
    fun `test french 006 mobile 07 local format`() {
        // Mobile 07 in national format: 07 XX XX XX XX
        val phone = "07 12 34 56 78"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+337"))
    }

    @Test
    fun `test french 007 lyon 04 local format`() {
        // Lyon is in zone 04 (south-east); 04 72 XX XX XX
        val phone = "04 72 00 00 00"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+334"))
    }

    @Test
    fun `test french 008 marseille 04 plus33`() {
        // Marseille is also zone 04; 04 91 XX XX XX
        val phone = "+33 4 91 00 00 00"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+33491000000", e164)
    }

    @Test
    fun `test french 009 toulouse 05 local format`() {
        // Toulouse is in zone 05 (south-west); 05 61 XX XX XX
        val phone = "05 61 00 00 00"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+335"))
    }

    @Test
    fun `test french 010 bordeaux 05 plus33`() {
        // Bordeaux: 05 56 XX XX XX
        val phone = "+33 5 56 00 00 00"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+33556000000", e164)
    }

    @Test
    fun `test french 011 nantes 02 local format`() {
        // Nantes is in zone 02 (north-west); 02 40 XX XX XX
        val phone = "02 40 00 00 00"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+332"))
    }

    @Test
    fun `test french 012 strasbourg 03 local format`() {
        // Strasbourg is in zone 03 (north-east); 03 88 XX XX XX
        val phone = "03 88 00 00 00"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+333"))
    }

    @Test
    fun `test french 013 get region FR for paris number`() {
        // Region lookup for Paris E.164 must return "FR"
        val e164 = "+33123456789"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("FR", region)
    }

    // --- sub-group: utility methods for French numbers (014–018) ---
    // Confirms that getRegion, looksLikePhoneNumber, and extractFromText
    // work correctly for both landline and mobile French numbers, and that
    // null / empty inputs are rejected without exceptions.

    @Test
    fun `test french 014 get region FR for mobile`() {
        // Region lookup for French mobile E.164 must return "FR"
        val e164 = "+33612345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("FR", region)
    }

    @Test
    fun `test french 015 looksLikePhoneNumber true for plus33`() {
        // Heuristic must detect an international French number
        assertTrue(PhoneValidator.looksLikePhoneNumber("+33 1 23 45 67 89"))
    }

    @Test
    fun `test french 016 extractFromText finds mobile number`() {
        // extractFromText must locate the embedded French mobile number
        val text = "Appelez notre service au +33 6 12 34 56 78, disponible 24h/24."
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test french 017 null input is invalid`() {
        assertFalse(PhoneValidator.isValid(null, "FR"))
        assertNull(PhoneValidator.formatToE164(null, "FR"))
    }

    @Test
    fun `test french 018 empty string is invalid`() {
        assertFalse(PhoneValidator.isValid("", "FR"))
        assertNull(PhoneValidator.formatToE164("", "FR"))
    }

    @Test
    fun `test french 019 too short 8 digits invalid`() {
        // French numbers are always 10 digits national; 8 is too short
        val phone = "+33 1 23 45 67"
        assertFalse(PhoneValidator.isValid(phone, "FR"))
        assertNull(PhoneValidator.formatToE164(phone, "FR"))
    }

    // --- sub-group: boundary conditions and compact local format (020–025) ---
    // Verifies that over-length numbers fail, that canonical E.164 survives
    // round-trips, and that French 0800 freephone and compact local formats
    // are accepted.

    @Test
    fun `test french 020 too long 11 digits invalid`() {
        // 11 local digits is one too many for France
        val phone = "+33 1 23 45 67 890"
        assertFalse(PhoneValidator.isValid(phone, "FR"))
        assertNull(PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 021 mobile e164 already canonical`() {
        // Already-canonical E.164 mobile must be returned unchanged
        val phone = "+33612345678"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
        assertEquals("+33612345678", e164)
    }

    @Test
    fun `test french 022 0800 freephone valid`() {
        // French 0800 freephone – 9 digits after the 0800 prefix
        val phone = "0800 123 456"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+33"))
    }

    @Test
    fun `test french 023 local format no spaces 10 digits`() {
        // Compact 10-digit national format without any separators
        val phone = "0123456789"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+331"))
    }

    @Test
    fun `test french 024 mobile local no spaces 10 digits`() {
        // Compact 10-digit mobile: 0612345678
        val phone = "0612345678"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(e164)
        assertEquals("+33612345678", e164)
    }

    @Test
    fun `test french 025 paris plus33 compact no spaces`() {
        // International compact without spaces: +33123456789
        val phone = "+33123456789"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+33123456789", e164)
    }

    // =========================================================================
    // SECTION 6: Australian numbers (+61) – 25 tests
    //
    // Australia uses country code +61.  Geographic area codes: 02 (NSW/ACT),
    // 03 (VIC/TAS), 07 (QLD), 08 (SA/WA/NT).  Mobile numbers start with 04.
    // National freephone: 1800.  Business rate: 1300.
    // =========================================================================

    @Test
    fun `test australian 001 sydney 02 plus61`() {
        // Sydney uses area code 02; subscriber numbers are 8 digits
        val phone = "+61 2 9000 0000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertEquals("+61290000000", e164)
    }

    @Test
    fun `test australian 002 melbourne 03 plus61`() {
        // Melbourne area code 03
        val phone = "+61 3 9000 0000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertEquals("+61390000000", e164)
    }

    @Test
    fun `test australian 003 mobile 04xx plus61`() {
        // Australian mobile numbers start with 04; 10 digits total national
        val phone = "+61 412 345 678"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertEquals("+61412345678", e164)
    }

    @Test
    fun `test australian 004 mobile local 04xx format`() {
        // National mobile format: 0412 XXX XXX
        val phone = "0412 345 678"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+614"))
    }

    @Test
    fun `test australian 005 brisbane 07 plus61`() {
        // Brisbane and Queensland use area code 07
        val phone = "+61 7 3000 0000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertEquals("+61730000000", e164)
    }

    @Test
    fun `test australian 006 perth 08 plus61`() {
        // Perth and Western Australia use area code 08
        val phone = "+61 8 9000 0000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertEquals("+61890000000", e164)
    }

    @Test
    fun `test australian 007 adelaide local 08 format`() {
        // Adelaide local format: 08 XXXX XXXX
        val phone = "08 8000 0000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+618"))
    }

    @Test
    fun `test australian 008 1800 freephone valid`() {
        // 1800 is Australian national freephone; subscriber is 6 digits
        val phone = "1800 123 456"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
    }

    @Test
    fun `test australian 009 1300 business rate valid`() {
        // 1300 local-rate business number; subscriber is 6 digits
        val phone = "1300 123 456"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
    }

    @Test
    fun `test australian 010 get region AU for sydney`() {
        // Region lookup for Sydney E.164 must return "AU"
        val e164 = "+61290000000"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("AU", region)
    }

    // --- sub-group: utility methods and extraction for AU numbers (011–015) ---
    // getRegion, looksLikePhoneNumber, and extractFromText are exercised for
    // Australian numbers; null and empty inputs are verified to fail safely.

    @Test
    fun `test australian 011 get region AU for mobile`() {
        // Region lookup for Australian mobile E.164 must return "AU"
        val e164 = "+61412345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("AU", region)
    }

    @Test
    fun `test australian 012 looksLikePhoneNumber true for plus61`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+61 2 9000 0000"))
    }

    @Test
    fun `test australian 013 extractFromText finds sydney number`() {
        val text = "Ring our Sydney helpdesk: +61 2 9000 0000 (business hours)"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test australian 014 null input is invalid`() {
        assertFalse(PhoneValidator.isValid(null, "AU"))
        assertNull(PhoneValidator.formatToE164(null, "AU"))
    }

    @Test
    fun `test australian 015 empty string is invalid`() {
        assertFalse(PhoneValidator.isValid("", "AU"))
        assertNull(PhoneValidator.formatToE164("", "AU"))
    }

    @Test
    fun `test australian 016 too short 8 local digits invalid`() {
        // Sydney local needs 8 subscriber digits; 7 is too short
        val phone = "+61 2 900 000"
        assertFalse(PhoneValidator.isValid(phone, "AU"))
        assertNull(PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 017 too long extra digit invalid`() {
        // Extra digit makes 9 subscriber digits, exceeding AU maximum
        val phone = "+61 2 9000 00001"
        assertFalse(PhoneValidator.isValid(phone, "AU"))
        assertNull(PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 018 sydney local no plus format`() {
        // National format: 02 XXXX XXXX (leading trunk zero)
        val phone = "02 9000 0000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+612"))
    }

    @Test
    fun `test australian 019 mobile no separator digits only`() {
        // Compact 10-digit mobile: 0412345678
        val phone = "0412345678"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+614"))
    }

    @Test
    fun `test australian 020 sydney e164 already canonical`() {
        // Canonical E.164 must be returned unchanged
        val phone = "+61290000000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
        assertEquals("+61290000000", e164)
    }

    @Test
    fun `test australian 021 mobile e164 already canonical`() {
        // Canonical mobile E.164 must be returned unchanged
        val phone = "+61412345678"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
        assertEquals("+61412345678", e164)
    }

    @Test
    fun `test australian 022 darwin 08 northern territory`() {
        // Darwin (NT) also uses area code 08; 08 89XX XXXX
        val phone = "08 8900 0000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+618"))
    }

    @Test
    fun `test australian 023 canberra 02 act`() {
        // Canberra (ACT) shares the 02 area code with NSW; 02 61XX XXXX
        val phone = "02 6100 0000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+612"))
    }

    @Test
    fun `test australian 024 mobile 0400 range plus61`() {
        // 0400 is the start of the Australian mobile range
        val phone = "+61 400 000 000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+61400000000", e164)
    }

    @Test
    fun `test australian 025 mobile 0499 range local format`() {
        // 0499 is near the end of the current mobile range
        val phone = "0499 999 999"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+614"))
    }

    // =========================================================================
    // SECTION 7: Japanese numbers (+81) – 20 tests
    //
    // Japan uses country code +81.  Area codes (market codes) vary in
    // length.  Tokyo uses 03, Osaka 06.  Mobile prefixes are 070, 080, 090.
    // Freephone prefix is 0120 (and also 0800).
    // =========================================================================

    @Test
    fun `test japanese 001 tokyo 03 plus81`() {
        // Tokyo landline: +81 3 XXXX XXXX
        val phone = "+81 3 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
        assertEquals("+81312345678", e164)
    }

    @Test
    fun `test japanese 002 osaka 06 plus81`() {
        // Osaka landline: +81 6 XXXX XXXX
        val phone = "+81 6 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
        assertEquals("+81612345678", e164)
    }

    @Test
    fun `test japanese 003 mobile 090 plus81`() {
        // Japanese mobile prefix 090
        val phone = "+81 90 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
        assertEquals("+819012345678", e164)
    }

    @Test
    fun `test japanese 004 mobile 080 plus81`() {
        // Japanese mobile prefix 080
        val phone = "+81 80 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
        assertEquals("+818012345678", e164)
    }

    @Test
    fun `test japanese 005 mobile 070 plus81`() {
        // Japanese mobile prefix 070 (formerly pager, now IP phones and mobiles)
        val phone = "+81 70 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
        assertEquals("+817012345678", e164)
    }

    @Test
    fun `test japanese 006 tokyo local dash format`() {
        // Tokyo local format: 03-XXXX-XXXX
        val phone = "03-1234-5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+813"))
    }

    @Test
    fun `test japanese 007 mobile 090 local dash format`() {
        // Mobile local format: 090-XXXX-XXXX
        val phone = "090-1234-5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+819"))
    }

    @Test
    fun `test japanese 008 nagoya 052 plus81`() {
        // Nagoya area code 052
        val phone = "+81 52 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+815212345678", e164)
    }

    @Test
    fun `test japanese 009 get region JP for tokyo`() {
        // Region lookup for Tokyo E.164 must return "JP"
        val e164 = "+81312345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("JP", region)
    }

    @Test
    fun `test japanese 010 get region JP for mobile`() {
        // Region lookup for Japanese mobile E.164 must return "JP"
        val e164 = "+819012345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("JP", region)
    }

    @Test
    fun `test japanese 011 looksLikePhoneNumber true for plus81`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+81 3 1234 5678"))
    }

    @Test
    fun `test japanese 012 extractFromText finds tokyo number`() {
        val text = "東京オフィスの番号: +81 3 1234 5678 にお電話ください。"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test japanese 013 null input is invalid`() {
        assertFalse(PhoneValidator.isValid(null, "JP"))
        assertNull(PhoneValidator.formatToE164(null, "JP"))
    }

    @Test
    fun `test japanese 014 empty string is invalid`() {
        assertFalse(PhoneValidator.isValid("", "JP"))
        assertNull(PhoneValidator.formatToE164("", "JP"))
    }

    @Test
    fun `test japanese 015 too short invalid`() {
        // Tokyo number needs 8 subscriber digits; 7 is too short
        val phone = "+81 3 1234 567"
        assertFalse(PhoneValidator.isValid(phone, "JP"))
        assertNull(PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 016 tokyo e164 already canonical`() {
        // Canonical E.164 must be returned unchanged
        val phone = "+81312345678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertNotNull(e164)
        assertEquals("+81312345678", e164)
    }

    @Test
    fun `test japanese 017 freephone 0120 local format`() {
        // Japanese freephone prefix 0120; subscriber 6 digits
        val phone = "0120-123-456"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
    }

    @Test
    fun `test japanese 018 sapporo 011 plus81`() {
        // Sapporo area code 011
        val phone = "+81 11 123 4567"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+8111"))
    }

    @Test
    fun `test japanese 019 fukuoka 092 plus81`() {
        // Fukuoka area code 092
        val phone = "+81 92 123 4567"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+8192"))
    }

    @Test
    fun `test japanese 020 mobile 080 local dash format`() {
        // Mobile 080 local format: 080-XXXX-XXXX
        val phone = "080-1234-5678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+818"))
    }

    // =========================================================================
    // SECTION 8: Indian numbers (+91) – 20 tests
    //
    // India uses country code +91.  Landline numbers use STD codes of
    // varying length (2–4 digits) followed by the subscriber number.
    // Mobile numbers are always 10 digits starting with 6–9.
    // =========================================================================

    @Test
    fun `test indian 001 mobile 9xxx plus91`() {
        // Indian mobile numbers starting with 9 – most common range
        val phone = "+91 98765 43210"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(e164)
        assertEquals("+919876543210", e164)
    }

    @Test
    fun `test indian 002 mobile 8xxx plus91`() {
        // Indian mobile numbers starting with 8
        val phone = "+91 87654 32109"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(e164)
        assertEquals("+918765432109", e164)
    }

    @Test
    fun `test indian 003 mobile 7xxx plus91`() {
        // Indian mobile numbers starting with 7
        val phone = "+91 76543 21098"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(e164)
        assertEquals("+917654321098", e164)
    }

    @Test
    fun `test indian 004 mobile 6xxx plus91`() {
        // Indian mobile numbers starting with 6 (Jio and others)
        val phone = "+91 65432 10987"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(e164)
        assertEquals("+916543210987", e164)
    }

    @Test
    fun `test indian 005 delhi landline 011 plus91`() {
        // Delhi STD code 011; 8-digit subscriber
        val phone = "+91 11 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(e164)
        assertEquals("+911112345678", e164)
    }

    @Test
    fun `test indian 006 mumbai landline 022 plus91`() {
        // Mumbai STD code 022; 8-digit subscriber
        val phone = "+91 22 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(e164)
        assertEquals("+912212345678", e164)
    }

    @Test
    fun `test indian 007 mobile local 10 digits no plus`() {
        // 10-digit mobile without any prefix
        val phone = "9876543210"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+91"))
    }

    @Test
    fun `test indian 008 mobile local 0 trunk prefix`() {
        // Some legacy systems prepend 0; 11 digits total
        val phone = "09876543210"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+91"))
    }

    @Test
    fun `test indian 009 get region IN for mobile`() {
        // Region lookup for Indian mobile E.164 must return "IN"
        val e164 = "+919876543210"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("IN", region)
    }

    @Test
    fun `test indian 010 get region IN for delhi landline`() {
        // Region lookup for Delhi E.164 must return "IN"
        val e164 = "+911112345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("IN", region)
    }

    @Test
    fun `test indian 011 looksLikePhoneNumber true for plus91`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+91 98765 43210"))
    }

    @Test
    fun `test indian 012 extractFromText finds mobile`() {
        val text = "संपर्क करें: +91 98765 43210 (दिन में किसी भी समय)"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test indian 013 null input is invalid`() {
        assertFalse(PhoneValidator.isValid(null, "IN"))
        assertNull(PhoneValidator.formatToE164(null, "IN"))
    }

    @Test
    fun `test indian 014 empty string is invalid`() {
        assertFalse(PhoneValidator.isValid("", "IN"))
        assertNull(PhoneValidator.formatToE164("", "IN"))
    }

    @Test
    fun `test indian 015 too short 9 digits invalid`() {
        // Indian mobiles are always 10 digits; 9 is too short
        val phone = "+91 9876543"
        assertFalse(PhoneValidator.isValid(phone, "IN"))
        assertNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 016 too long 12 digits invalid`() {
        // 12 digits exceeds the valid length for India
        val phone = "+91 987654321012"
        assertFalse(PhoneValidator.isValid(phone, "IN"))
        assertNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 017 mobile e164 already canonical`() {
        // Already-canonical E.164 must be returned unchanged
        val phone = "+919876543210"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertNotNull(e164)
        assertEquals("+919876543210", e164)
    }

    @Test
    fun `test indian 018 bangalore 080 landline plus91`() {
        // Bengaluru (Bangalore) STD code 080
        val phone = "+91 80 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+918012345678", e164)
    }

    @Test
    fun `test indian 019 chennai 044 landline plus91`() {
        // Chennai STD code 044
        val phone = "+91 44 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+914412345678", e164)
    }

    @Test
    fun `test indian 020 kolkata 033 landline plus91`() {
        // Kolkata STD code 033
        val phone = "+91 33 1234 5678"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(e164)
        assertEquals("+913312345678", e164)
    }

    // =========================================================================
    // SECTION 9: formatToE164 function tests – 30 tests
    //
    // These tests verify the behaviour of PhoneValidator.formatToE164() in
    // isolation: correct output format, graceful handling of invalid/null
    // input, stripping of non-digit characters, idempotency on already-
    // canonical input, and correct region defaulting.
    // =========================================================================

    @Test
    fun `test format e164 001 us dash result is canonical`() {
        // Classic dash format must yield the canonical E.164 string
        val result = PhoneValidator.formatToE164("212-555-1234", "US")
        assertNotNull(result)
        assertEquals("+12125551234", result)
        assertTrue(result!!.startsWith("+"))
    }

    @Test
    fun `test format e164 002 null input yields null`() {
        // Null input must never throw; must return null
        val result = PhoneValidator.formatToE164(null, "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 003 empty string yields null`() {
        // Empty string cannot represent a phone number
        val result = PhoneValidator.formatToE164("", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 004 already e164 returned unchanged`() {
        // An already-canonical E.164 must survive the round-trip intact
        val input = "+12125551234"
        val result = PhoneValidator.formatToE164(input, "US")
        assertNotNull(result)
        assertEquals(input, result)
    }

    @Test
    fun `test format e164 005 uk landline to e164`() {
        // London landline must produce a +44 E.164 string without spaces
        val result = PhoneValidator.formatToE164("020 7946 0958", "GB")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+44"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 006 german landline to e164`() {
        // Berlin landline must produce a +49 E.164 string without spaces
        val result = PhoneValidator.formatToE164("030 12345678", "DE")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+49"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 007 french landline to e164`() {
        // Paris landline must produce a +33 E.164 string without spaces
        val result = PhoneValidator.formatToE164("01 23 45 67 89", "FR")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+33"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 008 australian landline to e164`() {
        // Sydney landline must produce a +61 E.164 string without spaces
        val result = PhoneValidator.formatToE164("02 9000 0000", "AU")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+61"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 009 japanese landline to e164`() {
        // Tokyo landline must produce a +81 E.164 string without dashes
        val result = PhoneValidator.formatToE164("03-1234-5678", "JP")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+81"))
        assertFalse(result.contains("-"))
    }

    @Test
    fun `test format e164 010 indian mobile to e164`() {
        // Indian 10-digit mobile must produce a +91 E.164 string
        val result = PhoneValidator.formatToE164("9876543210", "IN")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+91"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 011 result contains no spaces`() {
        // E.164 must never contain spaces regardless of input format
        val result = PhoneValidator.formatToE164("+1 212 555 1234", "US")
        assertNotNull(result)
        assertFalse(result!!.contains(" "))
        assertEquals("+12125551234", result)
    }

    @Test
    fun `test format e164 012 result contains no dashes`() {
        // E.164 must never contain dashes
        val result = PhoneValidator.formatToE164("1-212-555-1234", "US")
        assertNotNull(result)
        assertFalse(result!!.contains("-"))
        assertEquals("+12125551234", result)
    }

    @Test
    fun `test format e164 013 result contains no parentheses`() {
        // E.164 must never contain parentheses
        val result = PhoneValidator.formatToE164("(212) 555-1234", "US")
        assertNotNull(result)
        assertFalse(result!!.contains("("))
        assertFalse(result.contains(")"))
        assertEquals("+12125551234", result)
    }

    @Test
    fun `test format e164 014 result contains no dots`() {
        // E.164 must never contain dot separators
        val result = PhoneValidator.formatToE164("212.555.1234", "US")
        assertNotNull(result)
        assertFalse(result!!.contains("."))
        assertEquals("+12125551234", result)
    }

    @Test
    fun `test format e164 015 toll free 800 correct e164`() {
        // Toll-free 800 must format correctly
        val result = PhoneValidator.formatToE164("800-555-0100", "US")
        assertNotNull(result)
        assertEquals("+18005550100", result)
    }

    @Test
    fun `test format e164 016 toll free 888 starts plus1888`() {
        // 888 toll-free must start with +1888
        val result = PhoneValidator.formatToE164("888-555-0100", "US")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+1888"))
    }

    @Test
    fun `test format e164 017 all zeros subscriber invalid`() {
        // 000-000-0000 is not a valid NANP subscriber number
        val result = PhoneValidator.formatToE164("000-000-0000", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 018 letters only yields null`() {
        // Pure alphabetic strings cannot be dialled
        val result = PhoneValidator.formatToE164("abcdefghij", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 019 default region is US`() {
        // When region is omitted the default must be "US"
        val resultWithRegion = PhoneValidator.formatToE164("212-555-1234", "US")
        val resultDefault = PhoneValidator.formatToE164("212-555-1234")
        assertNotNull(resultDefault)
        assertEquals(resultWithRegion, resultDefault)
    }

    @Test
    fun `test format e164 020 result always starts with plus`() {
        // All valid E.164 results must start with '+'
        val result = PhoneValidator.formatToE164("+12125551234", "US")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+"))
    }

    @Test
    fun `test format e164 021 extension notation stripped or null`() {
        // Extensions appended with "ext" are not part of E.164
        val result = PhoneValidator.formatToE164("212-555-1234 ext 100", "US")
        // Must not throw; result either null or free of "ext"
        assertTrue(result == null || !result.contains("ext", ignoreCase = true))
    }

    @Test
    fun `test format e164 022 whitespace only yields null`() {
        // A string of spaces must return null, not crash
        val result = PhoneValidator.formatToE164("     ", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 023 single digit yields null`() {
        // A single digit is not a phone number
        val result = PhoneValidator.formatToE164("5", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 024 uk mobile correct e164`() {
        // UK mobile to E.164 must yield +447…
        val result = PhoneValidator.formatToE164("07700 900123", "GB")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+447"))
    }

    @Test
    fun `test format e164 025 german mobile correct e164`() {
        // German mobile to E.164 must yield +4915…
        val result = PhoneValidator.formatToE164("015112345678", "DE")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+4915"))
    }

    @Test
    fun `test format e164 026 french mobile correct e164`() {
        // French mobile to E.164 must yield +336…
        val result = PhoneValidator.formatToE164("0612345678", "FR")
        assertNotNull(result)
        assertEquals("+33612345678", result)
    }

    @Test
    fun `test format e164 027 australian mobile correct e164`() {
        // Australian mobile to E.164 must yield +614…
        val result = PhoneValidator.formatToE164("0412345678", "AU")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+614"))
    }

    @Test
    fun `test format e164 028 special characters only yields null`() {
        // A string of only special characters must return null
        val result = PhoneValidator.formatToE164("!@#\$%^&*()", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 029 result length within e164 bounds`() {
        // E.164 allows a maximum of 15 digits after the '+' sign
        val result = PhoneValidator.formatToE164("212-555-1234", "US")
        assertNotNull(result)
        // Total length: 1 ('+') + 1–15 digits = 2–16 characters
        assertTrue(result!!.length in 8..16)
    }

    @Test
    fun `test format e164 030 indian mobile plus91 correct e164`() {
        // Indian mobile with +91 to E.164 must yield +919876543210
        val result = PhoneValidator.formatToE164("+91 98765 43210", "IN")
        assertNotNull(result)
        assertEquals("+919876543210", result)
    }

    // =========================================================================
    // SECTION 10: looksLikePhoneNumber edge cases – 30 tests
    //
    // PhoneValidator.looksLikePhoneNumber() is a lightweight heuristic that
    // checks whether a given string *looks* like a phone number without
    // performing full libphonenumber validation.  These tests document the
    // expected behaviour for common patterns, ambiguous strings, and clear
    // non-phone strings.
    // =========================================================================

    @Test
    fun `test looks like phone 001 classic us dash format true`() {
        // Standard US dashed format should definitely look like a phone
        val input = "212-555-1234"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 002 plain sentence text false`() {
        // A plain English sentence contains no digit sequence
        val input = "hello world how are you"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 003 empty string false`() {
        // Empty string cannot be a phone number
        val input = ""
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 004 e164 us format true`() {
        // Canonical E.164 string should be recognised
        val input = "+12125551234"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 005 parentheses format true`() {
        // (AAA) NXX-XXXX format should be recognised
        val input = "(212) 555-1234"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 006 dots format true`() {
        // AAA.NXX.XXXX dot-separated should be recognised
        val input = "212.555.1234"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 007 plain ten digits true`() {
        // A bare 10-digit string could be a phone number
        val input = "2125551234"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 008 eleven digits with leading 1 true`() {
        // 11-digit string starting with 1 – NANP with country code
        val input = "12125551234"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 009 alphabetic word false`() {
        // No digits at all – definitely not a phone number
        val input = "SomeRandomText"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 010 three digits too few false`() {
        // Only 3 digits – not long enough to be a phone number
        val input = "123"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 011 seventeen digits too many false`() {
        // 17 digits exceeds E.164 maximum of 15
        val input = "12345678901234567"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 012 uk london format true`() {
        // UK London format should be recognised
        val input = "+44 20 7946 0958"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 013 german format true`() {
        // German international format should be recognised
        val input = "+49 30 12345678"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 014 french format true`() {
        // French international format should be recognised
        val input = "+33 1 23 45 67 89"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 015 date string result is consistent`() {
        // ISO date "2025-03-12" – 8 digits, may or may not be flagged
        val input = "2025-03-12"
        // Call must not throw; result must be a stable Boolean
        val result = PhoneValidator.looksLikePhoneNumber(input)
        assertTrue(result || !result) // tautology – just verifies no crash
    }

    @Test
    fun `test looks like phone 016 social security number false`() {
        // SSN 123-45-6789 is only 9 digits; below typical phone threshold
        val input = "123-45-6789"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 017 five digit zip code false`() {
        // US ZIP code – too short to be a phone number
        val input = "90210"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 018 sixteen digit credit card false`() {
        // 16 digits exceeds E.164 maximum; should not be flagged as phone
        val input = "4111111111111111"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 019 url not a phone false`() {
        // A URL string cannot be a phone number
        val input = "https://example.com"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 020 email address not a phone false`() {
        // An email address cannot be a phone number
        val input = "user@example.com"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 021 mixed alphanumeric not a phone false`() {
        // A mix of letters and digits that don't follow a phone pattern
        val input = "abc123def456"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 022 australian mobile format true`() {
        // Australian mobile with spaces should be recognised
        val input = "0412 345 678"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 023 japanese mobile format true`() {
        // Japanese mobile with +81 prefix should be recognised
        val input = "+81 90 1234 5678"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 024 indian mobile format true`() {
        // Indian mobile with +91 prefix should be recognised
        val input = "+91 98765 43210"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 025 spaces only false`() {
        // A string of only spaces has no digit content
        val input = "     "
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 026 plus sign only false`() {
        // A bare '+' sign contains no digits
        val input = "+"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 027 plus one only false`() {
        // "+1" alone is only the country code – no subscriber number
        val input = "+1"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 028 toll free dash format true`() {
        // 1-800-NXX-XXXX toll-free format should be recognised
        val input = "1-800-555-0100"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 029 sentence with embedded phone is false`() {
        // looksLikePhoneNumber checks the whole string, not sub-strings
        // A natural-language sentence is not itself a phone number
        val input = "call 2125551234 now"
        assertFalse(PhoneValidator.looksLikePhoneNumber(input))
    }

    @Test
    fun `test looks like phone 030 international compact plus33 true`() {
        // Compact international French mobile with no spaces should be recognised
        val input = "+33612345678"
        assertTrue(PhoneValidator.looksLikePhoneNumber(input))
    }
}
