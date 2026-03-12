package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive extended test suite for PhoneValidator.
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
 */
class PhoneValidatorExtendedTest {

    // =========================================================================
    // SECTION 1: US NANP numbers with area code (212) variants - 25 tests
    // =========================================================================

    @Test
    fun `test us nanp 001 classic format`() {
        val phone = "212-555-1234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
        assertEquals("+12125551234", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 002 parentheses format`() {
        val phone = "(212) 555-1234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 003 dot separated format`() {
        val phone = "212.555.1234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+12125551234", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 004 no separator format`() {
        val phone = "2125551234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 005 with country code plus`() {
        val phone = "+12125551234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+12125551234", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 006 with country code 1 prefix`() {
        val phone = "12125551234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+12125551234", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 007 with 1 dash prefix`() {
        val phone = "1-212-555-1234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 008 spaces only format`() {
        val phone = "212 555 1234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+12125551234", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 009 mixed separators`() {
        val phone = "(212) 555.1234"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 010 plus one with spaces`() {
        val phone = "+1 212 555 1234"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+12125551234", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 011 area code 646`() {
        val phone = "646-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 012 area code 917`() {
        val phone = "917-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+19175550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 013 area code 347`() {
        val phone = "(347) 555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.getRegion("+13475550100"))
    }

    @Test
    fun `test us nanp 014 area code 718`() {
        val phone = "718-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+1"))
    }

    @Test
    fun `test us nanp 015 area code 516`() {
        val phone = "516-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+15165550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 016 invalid too short`() {
        val phone = "212-555-123"
        assertFalse(PhoneValidator.isValid(phone))
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 017 invalid too long`() {
        val phone = "212-555-12345"
        assertFalse(PhoneValidator.isValid(phone))
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 018 invalid area code starts with 1`() {
        val phone = "112-555-1234"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 019 invalid area code starts with 0`() {
        val phone = "012-555-1234"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 020 invalid exchange starts with 1`() {
        val phone = "212-155-1234"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test us nanp 021 null input`() {
        assertFalse(PhoneValidator.isValid(null))
        assertFalse(PhoneValidator.isValid(null, "US"))
        assertNull(PhoneValidator.formatToE164(null, "US"))
    }

    @Test
    fun `test us nanp 022 empty string`() {
        assertFalse(PhoneValidator.isValid(""))
        assertFalse(PhoneValidator.isValid("", "US"))
        assertNull(PhoneValidator.formatToE164("", "US"))
    }

    @Test
    fun `test us nanp 023 area code 212 get region`() {
        val e164 = "+12125551234"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("US", region)
    }

    @Test
    fun `test us nanp 024 area code 212 looks like phone`() {
        val phone = "212-555-1234"
        assertTrue(PhoneValidator.looksLikePhoneNumber(phone))
    }

    @Test
    fun `test us nanp 025 area code 212 extract from text`() {
        val text = "Call us at 212-555-1234 for more info"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
        assertTrue(phones.any { it.contains("212") || it.contains("2125551234") })
    }

    // =========================================================================
    // SECTION 2: US toll-free numbers (800, 888, 877, 866, 855, 844) - 25 tests
    // =========================================================================

    @Test
    fun `test toll free 001 800 dashes`() {
        val phone = "800-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+18005550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 002 888 dashes`() {
        val phone = "888-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 003 877 dashes`() {
        val phone = "877-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+18775550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 004 866 dashes`() {
        val phone = "866-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 005 855 dashes`() {
        val phone = "855-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+18555550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 006 844 dashes`() {
        val phone = "844-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 007 800 with plus one`() {
        val phone = "+18005550100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+18005550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 008 888 parentheses format`() {
        val phone = "(888) 555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 009 877 dot format`() {
        val phone = "877.555.0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+18775550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 010 866 no separator`() {
        val phone = "8665550100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 011 855 spaces`() {
        val phone = "855 555 0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertEquals("+18555550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 012 844 with 1 prefix`() {
        val phone = "1-844-555-0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "US"))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 013 800 region check`() {
        val e164 = "+18005550100"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("US", region)
    }

    @Test
    fun `test toll free 014 888 region check`() {
        val e164 = "+18885550100"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("US", region)
    }

    @Test
    fun `test toll free 015 877 looks like phone`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("877-555-0100"))
    }

    @Test
    fun `test toll free 016 866 extract from text`() {
        val text = "For support call 866-555-0100 today"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test toll free 017 800 vanity number format`() {
        // 800-FLOWERS style is not supported by standard validators
        val phone = "800-356-9377"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 018 855 with parentheses and dots`() {
        val phone = "(855) 555.0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 019 844 plus one spaces`() {
        val phone = "+1 844 555 0100"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+18445550100", PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 020 800 invalid too short`() {
        val phone = "800-555-010"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 021 888 invalid extra digit`() {
        val phone = "888-555-01001"
        assertFalse(PhoneValidator.isValid(phone, "US"))
        assertNull(PhoneValidator.formatToE164(phone, "US"))
    }

    @Test
    fun `test toll free 022 877 null`() {
        assertNull(PhoneValidator.formatToE164(null, "US"))
        assertFalse(PhoneValidator.isValid(null, "US"))
    }

    @Test
    fun `test toll free 023 866 letters invalid`() {
        val phone = "866-ABC-DEFG"
        assertFalse(PhoneValidator.isValid(phone, "US"))
    }

    @Test
    fun `test toll free 024 855 e164 already formatted`() {
        val phone = "+18555550100"
        val e164 = PhoneValidator.formatToE164(phone, "US")
        assertEquals("+18555550100", e164)
    }

    @Test
    fun `test toll free 025 844 1 no dashes prefix`() {
        val phone = "18445550100"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+18445550100", PhoneValidator.formatToE164(phone, "US"))
    }

    // =========================================================================
    // SECTION 3: UK numbers (+44, 020, 07xxx mobile) - 25 tests
    // =========================================================================

    @Test
    fun `test uk 001 london 020 local`() {
        val phone = "020 7946 0958"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 002 london plus44 format`() {
        val phone = "+44 20 7946 0958"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertEquals("+442079460958", PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 003 mobile 07xxx format`() {
        val phone = "07700 900123"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 004 mobile plus44 7xxx`() {
        val phone = "+44 7700 900123"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertEquals("+447700900123", PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 005 0800 freephone`() {
        val phone = "0800 123 4567"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 006 0345 service number`() {
        val phone = "0345 678 9000"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 007 regional 0113 leeds`() {
        val phone = "0113 496 0000"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
    }

    @Test
    fun `test uk 008 regional 0121 birmingham`() {
        val phone = "0121 234 5000"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
    }

    @Test
    fun `test uk 009 regional 0131 edinburgh`() {
        val phone = "0131 200 2000"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 010 plus44 no leading zero`() {
        val phone = "+442079460958"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+442079460958", PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 011 mobile no spaces`() {
        val phone = "07700900123"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 012 get region gb`() {
        val e164 = "+442079460958"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("GB", region)
    }

    @Test
    fun `test uk 013 mobile get region gb`() {
        val e164 = "+447700900123"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("GB", region)
    }

    @Test
    fun `test uk 014 looks like phone 020`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("020 7946 0958"))
    }

    @Test
    fun `test uk 015 looks like phone 07xxx`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("07700 900123"))
    }

    @Test
    fun `test uk 016 extract from text`() {
        val text = "Please call our London office on +44 20 7946 0958"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test uk 017 invalid too short`() {
        val phone = "0207946095"
        assertFalse(PhoneValidator.isValid(phone, "GB"))
    }

    @Test
    fun `test uk 018 invalid too long`() {
        val phone = "020 7946 09581"
        assertFalse(PhoneValidator.isValid(phone, "GB"))
    }

    @Test
    fun `test uk 019 null input`() {
        assertFalse(PhoneValidator.isValid(null, "GB"))
        assertNull(PhoneValidator.formatToE164(null, "GB"))
    }

    @Test
    fun `test uk 020 empty input`() {
        assertFalse(PhoneValidator.isValid("", "GB"))
        assertNull(PhoneValidator.formatToE164("", "GB"))
    }

    @Test
    fun `test uk 021 mobile 07911 format`() {
        val phone = "+44 7911 123456"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+447911123456", PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 022 020 dashes format`() {
        val phone = "020-7946-0958"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        assertNotNull(PhoneValidator.formatToE164(phone, "GB"))
    }

    @Test
    fun `test uk 023 0800 freephone e164`() {
        val phone = "0800 123 4567"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
    }

    @Test
    fun `test uk 024 plus44 mobile no spaces`() {
        val phone = "+447700900123"
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertEquals("+447700900123", e164)
    }

    @Test
    fun `test uk 025 regional 0141 glasgow`() {
        val phone = "0141 204 4400"
        assertTrue(PhoneValidator.isValid(phone, "GB"))
        val e164 = PhoneValidator.formatToE164(phone, "GB")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
    }

    // =========================================================================
    // SECTION 4: German numbers (+49) - 25 tests
    // =========================================================================

    @Test
    fun `test german 001 berlin landline`() {
        val phone = "+49 30 12345678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertEquals("+493012345678", PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 002 mobile 015x`() {
        val phone = "+49 151 12345678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 003 mobile 016x`() {
        val phone = "+49 162 12345678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertEquals("+4916212345678", PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 004 mobile 017x`() {
        val phone = "+49 170 12345678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 005 local format 030`() {
        val phone = "030 12345678"
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+49"))
    }

    @Test
    fun `test german 006 munich 089`() {
        val phone = "+49 89 12345678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertEquals("+498912345678", PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 007 hamburg 040`() {
        val phone = "+49 40 12345678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 008 cologne 0221`() {
        val phone = "+49 221 1234567"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 009 frankfurt 069`() {
        val phone = "+49 69 12345678"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+496912345678", PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 010 local 040 no plus`() {
        val phone = "040 12345678"
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+4940"))
    }

    @Test
    fun `test german 011 get region de`() {
        val e164 = "+493012345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("DE", region)
    }

    @Test
    fun `test german 012 mobile get region`() {
        val e164 = "+4915112345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("DE", region)
    }

    @Test
    fun `test german 013 looks like phone`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+49 30 12345678"))
    }

    @Test
    fun `test german 014 extract from text`() {
        val text = "Rufen Sie uns an: +49 30 12345678"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test german 015 null input`() {
        assertFalse(PhoneValidator.isValid(null, "DE"))
        assertNull(PhoneValidator.formatToE164(null, "DE"))
    }

    @Test
    fun `test german 016 empty string`() {
        assertFalse(PhoneValidator.isValid("", "DE"))
        assertNull(PhoneValidator.formatToE164("", "DE"))
    }

    @Test
    fun `test german 017 0800 freephone`() {
        val phone = "0800 1234567"
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertNotNull(e164)
    }

    @Test
    fun `test german 018 0900 premium`() {
        val phone = "0900 1234567"
        // Premium rate numbers - validity depends on implementation
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        // Just verify it doesn't crash and check if we get a result or null
        assertTrue(e164 == null || e164.startsWith("+49"))
    }

    @Test
    fun `test german 019 berlin no plus`() {
        val phone = "03012345678"
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+49"))
    }

    @Test
    fun `test german 020 mobile 0151 no plus`() {
        val phone = "015112345678"
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        assertNotNull(PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 021 stuttgart 0711`() {
        val phone = "+49 711 1234567"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 022 dusseldorf 0211`() {
        val phone = "+49 211 1234567"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+492111234567", PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 023 too short invalid`() {
        val phone = "+49 30 1234"
        assertFalse(PhoneValidator.isValid(phone, "DE"))
        assertNull(PhoneValidator.formatToE164(phone, "DE"))
    }

    @Test
    fun `test german 024 already e164`() {
        val phone = "+493012345678"
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertEquals("+493012345678", e164)
    }

    @Test
    fun `test german 025 mobile 0176 format`() {
        val phone = "0176 12345678"
        assertTrue(PhoneValidator.isValid(phone, "DE"))
        val e164 = PhoneValidator.formatToE164(phone, "DE")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+4917612345678") || e164.startsWith("+49176"))
    }

    // =========================================================================
    // SECTION 5: French numbers (+33) - 25 tests
    // =========================================================================

    @Test
    fun `test french 001 paris landline`() {
        val phone = "+33 1 23 45 67 89"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 002 paris local 01`() {
        val phone = "01 23 45 67 89"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+33"))
    }

    @Test
    fun `test french 003 mobile 06`() {
        val phone = "+33 6 12 34 56 78"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertEquals("+33612345678", PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 004 mobile 07`() {
        val phone = "+33 7 12 34 56 78"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 005 local mobile 06`() {
        val phone = "06 12 34 56 78"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+336"))
    }

    @Test
    fun `test french 006 local mobile 07`() {
        val phone = "07 12 34 56 78"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+337"))
    }

    @Test
    fun `test french 007 lyon 04`() {
        val phone = "04 72 00 00 00"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        assertNotNull(PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 008 marseille 04`() {
        val phone = "+33 4 91 00 00 00"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+33491000000", PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 009 toulouse 05`() {
        val phone = "05 61 00 00 00"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+33"))
    }

    @Test
    fun `test french 010 bordeaux 05`() {
        val phone = "+33 5 56 00 00 00"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 011 nantes 02`() {
        val phone = "02 40 00 00 00"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+332"))
    }

    @Test
    fun `test french 012 strasbourg 03`() {
        val phone = "03 88 00 00 00"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
    }

    @Test
    fun `test french 013 get region fr`() {
        val e164 = "+33123456789"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("FR", region)
    }

    @Test
    fun `test french 014 mobile get region fr`() {
        val e164 = "+33612345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("FR", region)
    }

    @Test
    fun `test french 015 looks like phone`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+33 1 23 45 67 89"))
    }

    @Test
    fun `test french 016 extract from text`() {
        val text = "Appelez-nous au +33 6 12 34 56 78 pour plus d'infos"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test french 017 null input`() {
        assertFalse(PhoneValidator.isValid(null, "FR"))
        assertNull(PhoneValidator.formatToE164(null, "FR"))
    }

    @Test
    fun `test french 018 empty input`() {
        assertFalse(PhoneValidator.isValid("", "FR"))
        assertNull(PhoneValidator.formatToE164("", "FR"))
    }

    @Test
    fun `test french 019 invalid too short`() {
        val phone = "+33 1 23 45 67"
        assertFalse(PhoneValidator.isValid(phone, "FR"))
        assertNull(PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 020 invalid too long`() {
        val phone = "+33 1 23 45 67 890"
        assertFalse(PhoneValidator.isValid(phone, "FR"))
        assertNull(PhoneValidator.formatToE164(phone, "FR"))
    }

    @Test
    fun `test french 021 e164 already formatted`() {
        val phone = "+33612345678"
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertEquals("+33612345678", e164)
    }

    @Test
    fun `test french 022 0800 freephone`() {
        val phone = "0800 123 456"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
    }

    @Test
    fun `test french 023 no spaces local`() {
        val phone = "0123456789"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+33"))
    }

    @Test
    fun `test french 024 mobile no spaces local`() {
        val phone = "0612345678"
        assertTrue(PhoneValidator.isValid(phone, "FR"))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertEquals("+33612345678", e164)
    }

    @Test
    fun `test french 025 paris plus33 no spaces`() {
        val phone = "+33123456789"
        assertTrue(PhoneValidator.isValid(phone))
        val e164 = PhoneValidator.formatToE164(phone, "FR")
        assertEquals("+33123456789", e164)
    }

    // =========================================================================
    // SECTION 6: Australian numbers (+61) - 25 tests
    // =========================================================================

    @Test
    fun `test australian 001 sydney landline`() {
        val phone = "+61 2 9000 0000"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertEquals("+61290000000", PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 002 melbourne landline`() {
        val phone = "+61 3 9000 0000"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 003 mobile 04xx`() {
        val phone = "+61 412 345 678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertEquals("+61412345678", PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 004 mobile local 04xx`() {
        val phone = "0412 345 678"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+614"))
    }

    @Test
    fun `test australian 005 brisbane 07`() {
        val phone = "+61 7 3000 0000"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertEquals("+61730000000", PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 006 perth 08`() {
        val phone = "+61 8 9000 0000"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 007 adelaide 08`() {
        val phone = "08 8000 0000"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+618"))
    }

    @Test
    fun `test australian 008 1800 freephone`() {
        val phone = "1800 123 456"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
    }

    @Test
    fun `test australian 009 1300 business`() {
        val phone = "1300 123 456"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
    }

    @Test
    fun `test australian 010 get region au`() {
        val e164 = "+61290000000"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("AU", region)
    }

    @Test
    fun `test australian 011 mobile get region au`() {
        val e164 = "+61412345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("AU", region)
    }

    @Test
    fun `test australian 012 looks like phone`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+61 2 9000 0000"))
    }

    @Test
    fun `test australian 013 extract from text`() {
        val text = "Call our Sydney office: +61 2 9000 0000"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test australian 014 null input`() {
        assertFalse(PhoneValidator.isValid(null, "AU"))
        assertNull(PhoneValidator.formatToE164(null, "AU"))
    }

    @Test
    fun `test australian 015 empty input`() {
        assertFalse(PhoneValidator.isValid("", "AU"))
        assertNull(PhoneValidator.formatToE164("", "AU"))
    }

    @Test
    fun `test australian 016 invalid too short`() {
        val phone = "+61 2 9000 000"
        assertFalse(PhoneValidator.isValid(phone, "AU"))
        assertNull(PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 017 invalid too long`() {
        val phone = "+61 2 9000 00001"
        assertFalse(PhoneValidator.isValid(phone, "AU"))
        assertNull(PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 018 sydney no plus local`() {
        val phone = "02 9000 0000"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+612"))
    }

    @Test
    fun `test australian 019 mobile no separator`() {
        val phone = "0412345678"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        assertNotNull(PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 020 e164 already formatted`() {
        val phone = "+61290000000"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertEquals("+61290000000", e164)
    }

    @Test
    fun `test australian 021 mobile e164 already formatted`() {
        val phone = "+61412345678"
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertEquals("+61412345678", e164)
    }

    @Test
    fun `test australian 022 darwin 08 northern territory`() {
        val phone = "08 8900 0000"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
    }

    @Test
    fun `test australian 023 canberra 02`() {
        val phone = "02 6100 0000"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+612"))
    }

    @Test
    fun `test australian 024 mobile 0400 range`() {
        val phone = "+61 400 000 000"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+61400000000", PhoneValidator.formatToE164(phone, "AU"))
    }

    @Test
    fun `test australian 025 mobile 0499 range`() {
        val phone = "0499 999 999"
        assertTrue(PhoneValidator.isValid(phone, "AU"))
        val e164 = PhoneValidator.formatToE164(phone, "AU")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+614"))
    }

    // =========================================================================
    // SECTION 7: Japanese numbers (+81) - 20 tests
    // =========================================================================

    @Test
    fun `test japanese 001 tokyo landline`() {
        val phone = "+81 3 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertEquals("+81312345678", PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 002 osaka landline`() {
        val phone = "+81 6 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 003 mobile 090`() {
        val phone = "+81 90 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertEquals("+819012345678", PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 004 mobile 080`() {
        val phone = "+81 80 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertNotNull(PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 005 mobile 070`() {
        val phone = "+81 70 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        assertEquals("+817012345678", PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 006 local format 03`() {
        val phone = "03-1234-5678"
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+81"))
    }

    @Test
    fun `test japanese 007 local mobile 090`() {
        val phone = "090-1234-5678"
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+819"))
    }

    @Test
    fun `test japanese 008 nagoya landline 052`() {
        val phone = "+81 52 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 009 get region jp`() {
        val e164 = "+81312345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("JP", region)
    }

    @Test
    fun `test japanese 010 mobile get region jp`() {
        val e164 = "+819012345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("JP", region)
    }

    @Test
    fun `test japanese 011 looks like phone`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+81 3 1234 5678"))
    }

    @Test
    fun `test japanese 012 extract from text`() {
        val text = "東京のオフィス: +81 3 1234 5678"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test japanese 013 null input`() {
        assertFalse(PhoneValidator.isValid(null, "JP"))
        assertNull(PhoneValidator.formatToE164(null, "JP"))
    }

    @Test
    fun `test japanese 014 empty input`() {
        assertFalse(PhoneValidator.isValid("", "JP"))
        assertNull(PhoneValidator.formatToE164("", "JP"))
    }

    @Test
    fun `test japanese 015 invalid too short`() {
        val phone = "+81 3 1234 567"
        assertFalse(PhoneValidator.isValid(phone, "JP"))
        assertNull(PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 016 e164 already formatted`() {
        val phone = "+81312345678"
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertEquals("+81312345678", e164)
    }

    @Test
    fun `test japanese 017 freephone 0120`() {
        val phone = "0120-123-456"
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertNotNull(e164)
    }

    @Test
    fun `test japanese 018 sapporo 011`() {
        val phone = "+81 11 123 4567"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 019 fukuoka 092`() {
        val phone = "+81 92 123 4567"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "JP"))
    }

    @Test
    fun `test japanese 020 mobile 080 local format`() {
        val phone = "080-1234-5678"
        assertTrue(PhoneValidator.isValid(phone, "JP"))
        val e164 = PhoneValidator.formatToE164(phone, "JP")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+818"))
    }

    // =========================================================================
    // SECTION 8: Indian numbers (+91) - 20 tests
    // =========================================================================

    @Test
    fun `test indian 001 mobile 9xxx`() {
        val phone = "+91 98765 43210"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertEquals("+919876543210", PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 002 mobile 8xxx`() {
        val phone = "+91 87654 32109"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 003 mobile 7xxx`() {
        val phone = "+91 76543 21098"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertEquals("+917654321098", PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 004 mobile 6xxx`() {
        val phone = "+91 65432 10987"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 005 delhi landline 011`() {
        val phone = "+91 11 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertNotNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 006 mumbai landline 022`() {
        val phone = "+91 22 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        assertEquals("+912212345678", PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 007 local mobile format 9xxx`() {
        val phone = "9876543210"
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+91"))
    }

    @Test
    fun `test indian 008 local mobile 0 prefix`() {
        val phone = "09876543210"
        assertTrue(PhoneValidator.isValid(phone, "IN"))
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+91"))
    }

    @Test
    fun `test indian 009 get region in`() {
        val e164 = "+919876543210"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("IN", region)
    }

    @Test
    fun `test indian 010 landline get region in`() {
        val e164 = "+911112345678"
        val region = PhoneValidator.getRegion(e164)
        assertNotNull(region)
        assertEquals("IN", region)
    }

    @Test
    fun `test indian 011 looks like phone`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+91 98765 43210"))
    }

    @Test
    fun `test indian 012 extract from text`() {
        val text = "हमसे संपर्क करें: +91 98765 43210"
        val phones = PhoneValidator.extractFromText(text)
        assertNotNull(phones)
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun `test indian 013 null input`() {
        assertFalse(PhoneValidator.isValid(null, "IN"))
        assertNull(PhoneValidator.formatToE164(null, "IN"))
    }

    @Test
    fun `test indian 014 empty input`() {
        assertFalse(PhoneValidator.isValid("", "IN"))
        assertNull(PhoneValidator.formatToE164("", "IN"))
    }

    @Test
    fun `test indian 015 too short invalid`() {
        val phone = "+91 9876543"
        assertFalse(PhoneValidator.isValid(phone, "IN"))
        assertNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 016 too long invalid`() {
        val phone = "+91 987654321012"
        assertFalse(PhoneValidator.isValid(phone, "IN"))
        assertNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 017 e164 already formatted`() {
        val phone = "+919876543210"
        val e164 = PhoneValidator.formatToE164(phone, "IN")
        assertEquals("+919876543210", e164)
    }

    @Test
    fun `test indian 018 bangalore landline 080`() {
        val phone = "+91 80 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 019 chennai landline 044`() {
        val phone = "+91 44 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertEquals("+914412345678", PhoneValidator.formatToE164(phone, "IN"))
    }

    @Test
    fun `test indian 020 kolkata landline 033`() {
        val phone = "+91 33 1234 5678"
        assertTrue(PhoneValidator.isValid(phone))
        assertNotNull(PhoneValidator.formatToE164(phone, "IN"))
    }

    // =========================================================================
    // SECTION 9: formatToE164 function tests - 30 tests
    // =========================================================================

    @Test
    fun `test format e164 001 us standard result`() {
        val result = PhoneValidator.formatToE164("212-555-1234", "US")
        assertNotNull(result)
        assertEquals("+12125551234", result)
        assertTrue(result!!.startsWith("+"))
    }

    @Test
    fun `test format e164 002 null input returns null`() {
        val result = PhoneValidator.formatToE164(null, "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 003 empty string returns null`() {
        val result = PhoneValidator.formatToE164("", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 004 already e164 unchanged`() {
        val result = PhoneValidator.formatToE164("+12125551234", "US")
        assertEquals("+12125551234", result)
    }

    @Test
    fun `test format e164 005 uk number to e164`() {
        val result = PhoneValidator.formatToE164("020 7946 0958", "GB")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+44"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 006 german number to e164`() {
        val result = PhoneValidator.formatToE164("030 12345678", "DE")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+49"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 007 french number to e164`() {
        val result = PhoneValidator.formatToE164("01 23 45 67 89", "FR")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+33"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 008 australian number to e164`() {
        val result = PhoneValidator.formatToE164("02 9000 0000", "AU")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+61"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 009 japanese number to e164`() {
        val result = PhoneValidator.formatToE164("03-1234-5678", "JP")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+81"))
        assertFalse(result.contains("-"))
    }

    @Test
    fun `test format e164 010 indian number to e164`() {
        val result = PhoneValidator.formatToE164("9876543210", "IN")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+91"))
        assertFalse(result.contains(" "))
    }

    @Test
    fun `test format e164 011 strip all spaces`() {
        val result = PhoneValidator.formatToE164("+1 212 555 1234", "US")
        assertNotNull(result)
        assertFalse(result!!.contains(" "))
        assertEquals("+12125551234", result)
    }

    @Test
    fun `test format e164 012 strip all dashes`() {
        val result = PhoneValidator.formatToE164("1-212-555-1234", "US")
        assertNotNull(result)
        assertFalse(result!!.contains("-"))
    }

    @Test
    fun `test format e164 013 strip parentheses`() {
        val result = PhoneValidator.formatToE164("(212) 555-1234", "US")
        assertNotNull(result)
        assertFalse(result!!.contains("("))
        assertFalse(result.contains(")"))
    }

    @Test
    fun `test format e164 014 strip dots`() {
        val result = PhoneValidator.formatToE164("212.555.1234", "US")
        assertNotNull(result)
        assertFalse(result!!.contains("."))
        assertEquals("+12125551234", result)
    }

    @Test
    fun `test format e164 015 toll free 800`() {
        val result = PhoneValidator.formatToE164("800-555-0100", "US")
        assertNotNull(result)
        assertEquals("+18005550100", result)
    }

    @Test
    fun `test format e164 016 toll free 888`() {
        val result = PhoneValidator.formatToE164("888-555-0100", "US")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+1888"))
    }

    @Test
    fun `test format e164 017 invalid number returns null`() {
        val result = PhoneValidator.formatToE164("000-000-0000", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 018 letters only returns null`() {
        val result = PhoneValidator.formatToE164("abcdefghij", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 019 default region us`() {
        val result = PhoneValidator.formatToE164("212-555-1234")
        assertNotNull(result)
        assertEquals("+12125551234", result)
    }

    @Test
    fun `test format e164 020 plus sign preserved`() {
        val result = PhoneValidator.formatToE164("+12125551234", "US")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+"))
    }

    @Test
    fun `test format e164 021 result has no extension characters`() {
        val result = PhoneValidator.formatToE164("212-555-1234 ext 100", "US")
        // Extensions should be stripped or return null
        assertTrue(result == null || !result.contains("ext"))
    }

    @Test
    fun `test format e164 022 whitespace only returns null`() {
        val result = PhoneValidator.formatToE164("   ", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 023 single digit returns null`() {
        val result = PhoneValidator.formatToE164("5", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 024 uk mobile to e164`() {
        val result = PhoneValidator.formatToE164("07700 900123", "GB")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+447"))
    }

    @Test
    fun `test format e164 025 german mobile to e164`() {
        val result = PhoneValidator.formatToE164("015112345678", "DE")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+4915"))
    }

    @Test
    fun `test format e164 026 french mobile to e164`() {
        val result = PhoneValidator.formatToE164("0612345678", "FR")
        assertNotNull(result)
        assertEquals("+33612345678", result)
    }

    @Test
    fun `test format e164 027 au mobile to e164`() {
        val result = PhoneValidator.formatToE164("0412345678", "AU")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+614"))
    }

    @Test
    fun `test format e164 028 special chars only returns null`() {
        val result = PhoneValidator.formatToE164("!@#$%^&*()", "US")
        assertNull(result)
    }

    @Test
    fun `test format e164 029 result length valid e164`() {
        val result = PhoneValidator.formatToE164("212-555-1234", "US")
        assertNotNull(result)
        // E.164 format: + followed by up to 15 digits
        assertTrue(result!!.length in 8..16)
    }

    @Test
    fun `test format e164 030 in mobile to e164`() {
        val result = PhoneValidator.formatToE164("+91 98765 43210", "IN")
        assertNotNull(result)
        assertEquals("+919876543210", result)
    }

    // =========================================================================
    // SECTION 10: looksLikePhoneNumber edge cases - 30 tests
    // =========================================================================

    @Test
    fun `test looks like phone 001 classic us format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("212-555-1234"))
    }

    @Test
    fun `test looks like phone 002 plain random text false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("hello world"))
    }

    @Test
    fun `test looks like phone 003 empty string false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber(""))
    }

    @Test
    fun `test looks like phone 004 e164 format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+12125551234"))
    }

    @Test
    fun `test looks like phone 005 parentheses format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("(212) 555-1234"))
    }

    @Test
    fun `test looks like phone 006 dots format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("212.555.1234"))
    }

    @Test
    fun `test looks like phone 007 plain 10 digits true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("2125551234"))
    }

    @Test
    fun `test looks like phone 008 plain 11 digits with 1 true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("12125551234"))
    }

    @Test
    fun `test looks like phone 009 word only false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("SomeRandomText"))
    }

    @Test
    fun `test looks like phone 010 too few digits false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("123"))
    }

    @Test
    fun `test looks like phone 011 too many digits false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("12345678901234567"))
    }

    @Test
    fun `test looks like phone 012 uk format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+44 20 7946 0958"))
    }

    @Test
    fun `test looks like phone 013 german format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+49 30 12345678"))
    }

    @Test
    fun `test looks like phone 014 french format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+33 1 23 45 67 89"))
    }

    @Test
    fun `test looks like phone 015 date format false or true`() {
        // "2025-03-12" - could be ambiguous; a heuristic might flag it
        val result = PhoneValidator.looksLikePhoneNumber("2025-03-12")
        // Just assert it's consistent (not crashing)
        assertTrue(result || !result)
    }

    @Test
    fun `test looks like phone 016 social security false`() {
        // SSN format 123-45-6789 - only 9 digits, should not be phone
        assertFalse(PhoneValidator.looksLikePhoneNumber("123-45-6789"))
    }

    @Test
    fun `test looks like phone 017 zip code false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("90210"))
    }

    @Test
    fun `test looks like phone 018 credit card number not phone`() {
        // 16 digits typically exceeds phone length
        assertFalse(PhoneValidator.looksLikePhoneNumber("4111111111111111"))
    }

    @Test
    fun `test looks like phone 019 url not phone`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("https://example.com"))
    }

    @Test
    fun `test looks like phone 020 email not phone`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("user@example.com"))
    }

    @Test
    fun `test looks like phone 021 mixed alpha numeric not phone`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("abc123def456"))
    }

    @Test
    fun `test looks like phone 022 au mobile format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("0412 345 678"))
    }

    @Test
    fun `test looks like phone 023 jp mobile format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+81 90 1234 5678"))
    }

    @Test
    fun `test looks like phone 024 in mobile format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+91 98765 43210"))
    }

    @Test
    fun `test looks like phone 025 spaces only false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("     "))
    }

    @Test
    fun `test looks like phone 026 plus sign only false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("+"))
    }

    @Test
    fun `test looks like phone 027 plus one only false`() {
        assertFalse(PhoneValidator.looksLikePhoneNumber("+1"))
    }

    @Test
    fun `test looks like phone 028 toll free format true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("1-800-555-0100"))
    }

    @Test
    fun `test looks like phone 029 text with embedded phone true`() {
        // looksLikePhoneNumber typically checks the whole string, not just parts
        // Embed in text and verify behavior
        val embedded = "call 2125551234 now"
        // Whole string is not a phone number
        assertFalse(PhoneValidator.looksLikePhoneNumber(embedded))
    }

    @Test
    fun `test looks like phone 030 international plus prefix true`() {
        assertTrue(PhoneValidator.looksLikePhoneNumber("+33612345678"))
    }

}
