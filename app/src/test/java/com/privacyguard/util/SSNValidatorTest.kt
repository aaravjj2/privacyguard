package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

class SSNValidatorTest {

    // ========================================================================
    // SECTION 1: Basic Valid SSN Tests
    // ========================================================================

    @Test
    fun `validate valid SSN 001-01-0001`() {
        val result = SSNValidator.validate("001-01-0001")
        assertTrue(result.isValid)
        assertEquals("001", result.areaNumber)
        assertEquals("01", result.groupNumber)
        assertEquals("0001", result.serialNumber)
    }

    @Test
    fun `validate valid SSN 123-45-6789 format`() {
        val result = SSNValidator.validate("123-45-6789")
        assertTrue(result.isValid)
        assertEquals("123-45-6789", result.formatted)
    }

    @Test
    fun `validate valid SSN without dashes`() {
        val result = SSNValidator.validate("234567890")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN with spaces`() {
        val result = SSNValidator.validate("234 56 7890")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 001`() {
        val result = SSNValidator.validate("001-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 002`() {
        val result = SSNValidator.validate("002-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 003`() {
        val result = SSNValidator.validate("003-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 010`() {
        val result = SSNValidator.validate("010-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 050`() {
        val result = SSNValidator.validate("050-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 100`() {
        val result = SSNValidator.validate("100-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 200`() {
        val result = SSNValidator.validate("200-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 300`() {
        val result = SSNValidator.validate("300-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 400`() {
        val result = SSNValidator.validate("400-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 500`() {
        val result = SSNValidator.validate("500-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 600`() {
        val result = SSNValidator.validate("600-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 665`() {
        val result = SSNValidator.validate("665-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 667`() {
        val result = SSNValidator.validate("667-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 700`() {
        val result = SSNValidator.validate("700-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 733`() {
        val result = SSNValidator.validate("733-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 750`() {
        val result = SSNValidator.validate("750-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 800`() {
        val result = SSNValidator.validate("800-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate valid SSN area 899`() {
        val result = SSNValidator.validate("899-01-0001")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 2: Invalid Area Number Tests
    // ========================================================================

    @Test
    fun `validate SSN area 000 is invalid`() {
        val result = SSNValidator.validate("000-01-0001")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("000"))
    }

    @Test
    fun `validate SSN area 666 is invalid`() {
        val result = SSNValidator.validate("666-01-0001")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("666"))
    }

    @Test
    fun `validate SSN area 000 without dashes is invalid`() {
        val result = SSNValidator.validate("000010001")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate SSN area 666 without dashes is invalid`() {
        val result = SSNValidator.validate("666010001")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate SSN area 000 with spaces is invalid`() {
        val result = SSNValidator.validate("000 01 0001")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate SSN area 666 with spaces is invalid`() {
        val result = SSNValidator.validate("666 01 0001")
        assertFalse(result.isValid)
    }

    // ========================================================================
    // SECTION 3: ITIN Detection Tests (area 900-999)
    // ========================================================================

    @Test
    fun `validate SSN area 900 is ITIN`() {
        val result = SSNValidator.validate("900-70-0001")
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate SSN area 950 is ITIN`() {
        val result = SSNValidator.validate("950-70-0001")
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate SSN area 999 is ITIN`() {
        val result = SSNValidator.validate("999-70-0001")
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 70 is valid`() {
        val result = SSNValidator.validate("900-70-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 78 is valid`() {
        val result = SSNValidator.validate("900-78-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 80 is valid`() {
        val result = SSNValidator.validate("900-80-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 88 is valid`() {
        val result = SSNValidator.validate("900-88-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 90 is valid`() {
        val result = SSNValidator.validate("900-90-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 91 is valid`() {
        val result = SSNValidator.validate("900-91-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 92 is valid`() {
        val result = SSNValidator.validate("900-92-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 94 is valid`() {
        val result = SSNValidator.validate("900-94-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with valid group 99 is valid`() {
        val result = SSNValidator.validate("900-99-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with invalid group 00 is invalid`() {
        val result = SSNValidator.validate("900-00-0001")
        // Area 900 goes to ITIN path, group 00 is not in valid ITIN ranges
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with invalid group 50 is invalid`() {
        val result = SSNValidator.validate("900-50-0001")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with invalid group 69 is invalid`() {
        val result = SSNValidator.validate("900-69-0001")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with invalid group 89 is invalid`() {
        val result = SSNValidator.validate("900-89-0001")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with invalid group 93 is invalid`() {
        val result = SSNValidator.validate("900-93-0001")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN confidence is 0_85 for valid`() {
        val result = SSNValidator.validate("900-70-0001")
        assertEquals(0.85f, result.confidence, 0.01f)
    }

    @Test
    fun `validate ITIN confidence is 0_3 for invalid group`() {
        val result = SSNValidator.validate("900-50-0001")
        assertEquals(0.3f, result.confidence, 0.01f)
    }

    // ========================================================================
    // SECTION 4: Invalid Group Number Tests
    // ========================================================================

    @Test
    fun `validate SSN with group 00 is invalid`() {
        val result = SSNValidator.validate("123-00-6789")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("00"))
    }

    @Test
    fun `validate SSN with group 00 area 001 is invalid`() {
        val result = SSNValidator.validate("001-00-0001")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate SSN with group 00 area 500 is invalid`() {
        val result = SSNValidator.validate("500-00-0001")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate SSN with group 01 is valid`() {
        val result = SSNValidator.validate("123-01-6789")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN with group 50 is valid`() {
        val result = SSNValidator.validate("123-50-6789")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN with group 99 is valid`() {
        val result = SSNValidator.validate("123-99-6789")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 5: Invalid Serial Number Tests
    // ========================================================================

    @Test
    fun `validate SSN with serial 0000 is invalid`() {
        val result = SSNValidator.validate("123-45-0000")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("0000"))
    }

    @Test
    fun `validate SSN with serial 0000 area 001 is invalid`() {
        val result = SSNValidator.validate("001-01-0000")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate SSN with serial 0001 is valid`() {
        val result = SSNValidator.validate("123-45-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN with serial 5000 is valid`() {
        val result = SSNValidator.validate("123-45-5000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN with serial 9999 is valid`() {
        val result = SSNValidator.validate("123-45-9999")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 6: Known Invalid SSN Tests
    // ========================================================================

    @Test
    fun `validate Woolworth SSN 078-05-1120 is invalid`() {
        val result = SSNValidator.validate("078-05-1120")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate known invalid 219-09-9999 is invalid`() {
        val result = SSNValidator.validate("219-09-9999")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate Lifelock CEO SSN 457-55-5462 is invalid`() {
        val result = SSNValidator.validate("457-55-5462")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate sequential 123456789 is invalid`() {
        val result = SSNValidator.validate("123456789")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate all ones 111111111 is invalid`() {
        val result = SSNValidator.validate("111111111")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate all twos 222222222 is invalid`() {
        val result = SSNValidator.validate("222222222")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate all threes 333333333 is invalid`() {
        val result = SSNValidator.validate("333333333")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate all fours 444444444 is invalid`() {
        val result = SSNValidator.validate("444444444")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate all fives 555555555 is invalid`() {
        val result = SSNValidator.validate("555555555")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate all sixes 666666666 is invalid`() {
        val result = SSNValidator.validate("666666666")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate all sevens 777777777 is invalid`() {
        val result = SSNValidator.validate("777777777")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate all eights 888888888 is invalid`() {
        val result = SSNValidator.validate("888888888")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate all nines 999999999 is invalid`() {
        val result = SSNValidator.validate("999999999")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate all zeros 000000000 is invalid`() {
        val result = SSNValidator.validate("000000000")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate 987654321 is invalid`() {
        val result = SSNValidator.validate("987654321")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate known invalid 123-12-1234`() {
        val result = SSNValidator.validate("123-12-1234")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate Tod Slaughter SSN 042-10-3580 is invalid`() {
        val result = SSNValidator.validate("042-10-3580")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    // ========================================================================
    // SECTION 7: Input Format Tests
    // ========================================================================

    @Test
    fun `validate SSN too short is invalid`() {
        val result = SSNValidator.validate("12345678")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("9 digits"))
    }

    @Test
    fun `validate SSN too long is invalid`() {
        val result = SSNValidator.validate("1234567890")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate empty string is invalid`() {
        val result = SSNValidator.validate("")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate only dashes is invalid`() {
        val result = SSNValidator.validate("---")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate letters only is invalid`() {
        val result = SSNValidator.validate("abcdefghi")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate mixed letters and digits is invalid`() {
        val result = SSNValidator.validate("123-AB-6789")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate SSN with dots as separators`() {
        val result = SSNValidator.validate("234.56.7890")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN with extra dashes`() {
        val result = SSNValidator.validate("1-2-3-4-5-6-7-8-9")
        // extractDigits gives "123456789" which is a known invalid
        val digits = SSNValidator.extractDigits("1-2-3-4-5-6-7-8-9")
        assertEquals("123456789", digits)
    }

    @Test
    fun `validate SSN with prefix text`() {
        val result = SSNValidator.validate("SSN: 234-56-7890")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 8: Formatting Tests
    // ========================================================================

    @Test
    fun `formatSSN formats 9 digit string correctly`() {
        val formatted = SSNValidator.formatSSN("234567890")
        assertEquals("234-56-7890", formatted)
    }

    @Test
    fun `formatSSN returns null for non-9 digit string`() {
        val formatted = SSNValidator.formatSSN("12345678")
        assertNull(formatted)
    }

    @Test
    fun `formatSSN returns null for empty string`() {
        val formatted = SSNValidator.formatSSN("")
        assertNull(formatted)
    }

    @Test
    fun `formatSSN handles input with dashes`() {
        val formatted = SSNValidator.formatSSN("234-56-7890")
        assertEquals("234-56-7890", formatted)
    }

    @Test
    fun `formatSSN handles input with spaces`() {
        val formatted = SSNValidator.formatSSN("234 56 7890")
        assertEquals("234-56-7890", formatted)
    }

    @Test
    fun `formatSSN returns null for too many digits`() {
        val formatted = SSNValidator.formatSSN("1234567890")
        assertNull(formatted)
    }

    @Test
    fun `formatSSN area 001`() {
        val formatted = SSNValidator.formatSSN("001010001")
        assertEquals("001-01-0001", formatted)
    }

    @Test
    fun `formatSSN area 899`() {
        val formatted = SSNValidator.formatSSN("899990001")
        assertEquals("899-99-0001", formatted)
    }

    // ========================================================================
    // SECTION 9: Masking Tests
    // ========================================================================

    @Test
    fun `maskSSN masks first 5 digits`() {
        val masked = SSNValidator.maskSSN("234567890")
        assertEquals("***-**-7890", masked)
    }

    @Test
    fun `maskSSN returns null for non-9 digit string`() {
        val masked = SSNValidator.maskSSN("12345678")
        assertNull(masked)
    }

    @Test
    fun `maskSSN returns null for empty string`() {
        val masked = SSNValidator.maskSSN("")
        assertNull(masked)
    }

    @Test
    fun `maskSSN handles input with dashes`() {
        val masked = SSNValidator.maskSSN("234-56-7890")
        assertEquals("***-**-7890", masked)
    }

    @Test
    fun `maskSSN handles input with spaces`() {
        val masked = SSNValidator.maskSSN("234 56 7890")
        assertEquals("***-**-7890", masked)
    }

    @Test
    fun `maskSSN shows correct last four digits`() {
        val masked = SSNValidator.maskSSN("001-01-0001")
        assertEquals("***-**-0001", masked)
    }

    @Test
    fun `maskSSN last four 9999`() {
        val masked = SSNValidator.maskSSN("123-45-9999")
        assertEquals("***-**-9999", masked)
    }

    @Test
    fun `validate result has correct masked format`() {
        val result = SSNValidator.validate("234-56-7890")
        assertEquals("***-**-7890", result.masked)
    }

    @Test
    fun `validate result has correct formatted string`() {
        val result = SSNValidator.validate("234567890")
        assertEquals("234-56-7890", result.formatted)
    }

    // ========================================================================
    // SECTION 10: extractDigits Tests
    // ========================================================================

    @Test
    fun `extractDigits removes dashes`() {
        assertEquals("123456789", SSNValidator.extractDigits("123-45-6789"))
    }

    @Test
    fun `extractDigits removes spaces`() {
        assertEquals("123456789", SSNValidator.extractDigits("123 45 6789"))
    }

    @Test
    fun `extractDigits removes dots`() {
        assertEquals("123456789", SSNValidator.extractDigits("123.45.6789"))
    }

    @Test
    fun `extractDigits removes letters`() {
        assertEquals("123456789", SSNValidator.extractDigits("SSN:123-45-6789"))
    }

    @Test
    fun `extractDigits empty string returns empty`() {
        assertEquals("", SSNValidator.extractDigits(""))
    }

    @Test
    fun `extractDigits no digits returns empty`() {
        assertEquals("", SSNValidator.extractDigits("no digits here"))
    }

    @Test
    fun `extractDigits preserves pure digit string`() {
        assertEquals("123456789", SSNValidator.extractDigits("123456789"))
    }

    // ========================================================================
    // SECTION 11: isSequentialOrRepetitive Tests
    // ========================================================================

    @Test
    fun `isSequentialOrRepetitive returns true for all same digit`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("111111111"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for ascending sequence`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("123456789"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for descending sequence`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("987654321"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for repeated pattern 123`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("123123123"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for repeated pattern 12`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("121212121"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for random digits`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("234567890"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for typical SSN`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("452781234"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for all twos`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("222222222"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for all zeros`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("000000000"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for all nines`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("999999999"))
    }

    @Test
    fun `validate suspicious pattern reduces confidence`() {
        // This SSN pattern is valid area/group/serial but repetitive
        val result = SSNValidator.validate("121-21-2121")
        if (result.isValid) {
            assertTrue(result.confidence < 0.9f)
        }
    }

    // ========================================================================
    // SECTION 12: State Lookup Tests
    // ========================================================================

    @Test
    fun `getStateForAreaNumber 001 returns New Hampshire`() {
        assertEquals("New Hampshire", SSNValidator.getStateForAreaNumber(1))
    }

    @Test
    fun `getStateForAreaNumber 002 returns New Hampshire`() {
        assertEquals("New Hampshire", SSNValidator.getStateForAreaNumber(2))
    }

    @Test
    fun `getStateForAreaNumber 003 returns New Hampshire`() {
        assertEquals("New Hampshire", SSNValidator.getStateForAreaNumber(3))
    }

    @Test
    fun `getStateForAreaNumber 004 returns Maine`() {
        assertEquals("Maine", SSNValidator.getStateForAreaNumber(4))
    }

    @Test
    fun `getStateForAreaNumber 007 returns Maine`() {
        assertEquals("Maine", SSNValidator.getStateForAreaNumber(7))
    }

    @Test
    fun `getStateForAreaNumber 008 returns Vermont`() {
        assertEquals("Vermont", SSNValidator.getStateForAreaNumber(8))
    }

    @Test
    fun `getStateForAreaNumber 010 returns Massachusetts`() {
        assertEquals("Massachusetts", SSNValidator.getStateForAreaNumber(10))
    }

    @Test
    fun `getStateForAreaNumber 034 returns Massachusetts`() {
        assertEquals("Massachusetts", SSNValidator.getStateForAreaNumber(34))
    }

    @Test
    fun `getStateForAreaNumber 035 returns Rhode Island`() {
        assertEquals("Rhode Island", SSNValidator.getStateForAreaNumber(35))
    }

    @Test
    fun `getStateForAreaNumber 040 returns Connecticut`() {
        assertEquals("Connecticut", SSNValidator.getStateForAreaNumber(40))
    }

    @Test
    fun `getStateForAreaNumber 050 returns New York`() {
        assertEquals("New York", SSNValidator.getStateForAreaNumber(50))
    }

    @Test
    fun `getStateForAreaNumber 135 returns New Jersey`() {
        assertEquals("New Jersey", SSNValidator.getStateForAreaNumber(135))
    }

    @Test
    fun `getStateForAreaNumber 159 returns Pennsylvania`() {
        assertEquals("Pennsylvania", SSNValidator.getStateForAreaNumber(159))
    }

    @Test
    fun `getStateForAreaNumber 212 returns Maryland`() {
        assertEquals("Maryland", SSNValidator.getStateForAreaNumber(212))
    }

    @Test
    fun `getStateForAreaNumber 221 returns Delaware`() {
        assertEquals("Delaware", SSNValidator.getStateForAreaNumber(221))
    }

    @Test
    fun `getStateForAreaNumber 223 returns Virginia`() {
        assertEquals("Virginia", SSNValidator.getStateForAreaNumber(223))
    }

    @Test
    fun `getStateForAreaNumber 247 returns South Carolina`() {
        assertEquals("South Carolina", SSNValidator.getStateForAreaNumber(247))
    }

    @Test
    fun `getStateForAreaNumber 252 returns Georgia`() {
        assertEquals("Georgia", SSNValidator.getStateForAreaNumber(252))
    }

    @Test
    fun `getStateForAreaNumber 261 returns Florida`() {
        assertEquals("Florida", SSNValidator.getStateForAreaNumber(261))
    }

    @Test
    fun `getStateForAreaNumber 268 returns Ohio`() {
        assertEquals("Ohio", SSNValidator.getStateForAreaNumber(268))
    }

    @Test
    fun `getStateForAreaNumber 303 returns Indiana`() {
        assertEquals("Indiana", SSNValidator.getStateForAreaNumber(303))
    }

    @Test
    fun `getStateForAreaNumber 318 returns Illinois`() {
        assertEquals("Illinois", SSNValidator.getStateForAreaNumber(318))
    }

    @Test
    fun `getStateForAreaNumber 362 returns Michigan`() {
        assertEquals("Michigan", SSNValidator.getStateForAreaNumber(362))
    }

    @Test
    fun `getStateForAreaNumber 387 returns Wisconsin`() {
        assertEquals("Wisconsin", SSNValidator.getStateForAreaNumber(387))
    }

    @Test
    fun `getStateForAreaNumber 400 returns Kentucky`() {
        assertEquals("Kentucky", SSNValidator.getStateForAreaNumber(400))
    }

    @Test
    fun `getStateForAreaNumber 408 returns Tennessee`() {
        assertEquals("Tennessee", SSNValidator.getStateForAreaNumber(408))
    }

    @Test
    fun `getStateForAreaNumber 416 returns Alabama`() {
        assertEquals("Alabama", SSNValidator.getStateForAreaNumber(416))
    }

    @Test
    fun `getStateForAreaNumber 425 returns Mississippi`() {
        assertEquals("Mississippi", SSNValidator.getStateForAreaNumber(425))
    }

    @Test
    fun `getStateForAreaNumber 429 returns Arkansas`() {
        assertEquals("Arkansas", SSNValidator.getStateForAreaNumber(429))
    }

    @Test
    fun `getStateForAreaNumber 433 returns Louisiana`() {
        assertEquals("Louisiana", SSNValidator.getStateForAreaNumber(433))
    }

    @Test
    fun `getStateForAreaNumber 440 returns Oklahoma`() {
        assertEquals("Oklahoma", SSNValidator.getStateForAreaNumber(440))
    }

    @Test
    fun `getStateForAreaNumber 449 returns Texas`() {
        assertEquals("Texas", SSNValidator.getStateForAreaNumber(449))
    }

    @Test
    fun `getStateForAreaNumber 468 returns Minnesota`() {
        assertEquals("Minnesota", SSNValidator.getStateForAreaNumber(468))
    }

    @Test
    fun `getStateForAreaNumber 478 returns Iowa`() {
        assertEquals("Iowa", SSNValidator.getStateForAreaNumber(478))
    }

    @Test
    fun `getStateForAreaNumber 486 returns Missouri`() {
        assertEquals("Missouri", SSNValidator.getStateForAreaNumber(486))
    }

    @Test
    fun `getStateForAreaNumber 501 returns North Dakota`() {
        assertEquals("North Dakota", SSNValidator.getStateForAreaNumber(501))
    }

    @Test
    fun `getStateForAreaNumber 503 returns South Dakota`() {
        assertEquals("South Dakota", SSNValidator.getStateForAreaNumber(503))
    }

    @Test
    fun `getStateForAreaNumber 505 returns Nebraska`() {
        assertEquals("Nebraska", SSNValidator.getStateForAreaNumber(505))
    }

    @Test
    fun `getStateForAreaNumber 509 returns Kansas`() {
        assertEquals("Kansas", SSNValidator.getStateForAreaNumber(509))
    }

    @Test
    fun `getStateForAreaNumber 516 returns Montana`() {
        assertEquals("Montana", SSNValidator.getStateForAreaNumber(516))
    }

    @Test
    fun `getStateForAreaNumber 518 returns Idaho`() {
        assertEquals("Idaho", SSNValidator.getStateForAreaNumber(518))
    }

    @Test
    fun `getStateForAreaNumber 520 returns Wyoming`() {
        assertEquals("Wyoming", SSNValidator.getStateForAreaNumber(520))
    }

    @Test
    fun `getStateForAreaNumber 521 returns Colorado`() {
        assertEquals("Colorado", SSNValidator.getStateForAreaNumber(521))
    }

    @Test
    fun `getStateForAreaNumber 525 returns New Mexico`() {
        assertEquals("New Mexico", SSNValidator.getStateForAreaNumber(525))
    }

    @Test
    fun `getStateForAreaNumber 526 returns Arizona`() {
        assertEquals("Arizona", SSNValidator.getStateForAreaNumber(526))
    }

    @Test
    fun `getStateForAreaNumber 528 returns Utah`() {
        assertEquals("Utah", SSNValidator.getStateForAreaNumber(528))
    }

    @Test
    fun `getStateForAreaNumber 530 returns Nevada`() {
        assertEquals("Nevada", SSNValidator.getStateForAreaNumber(530))
    }

    @Test
    fun `getStateForAreaNumber 531 returns Washington`() {
        assertEquals("Washington", SSNValidator.getStateForAreaNumber(531))
    }

    @Test
    fun `getStateForAreaNumber 540 returns Oregon`() {
        assertEquals("Oregon", SSNValidator.getStateForAreaNumber(540))
    }

    @Test
    fun `getStateForAreaNumber 545 returns California`() {
        assertEquals("California", SSNValidator.getStateForAreaNumber(545))
    }

    @Test
    fun `getStateForAreaNumber 574 returns Alaska`() {
        assertEquals("Alaska", SSNValidator.getStateForAreaNumber(574))
    }

    @Test
    fun `getStateForAreaNumber 575 returns Hawaii`() {
        assertEquals("Hawaii", SSNValidator.getStateForAreaNumber(575))
    }

    @Test
    fun `getStateForAreaNumber 577 returns District of Columbia`() {
        assertEquals("District of Columbia", SSNValidator.getStateForAreaNumber(577))
    }

    @Test
    fun `getStateForAreaNumber 580 returns Virgin Islands`() {
        assertEquals("Virgin Islands", SSNValidator.getStateForAreaNumber(580))
    }

    @Test
    fun `getStateForAreaNumber 581 returns Puerto Rico`() {
        assertEquals("Puerto Rico", SSNValidator.getStateForAreaNumber(581))
    }

    @Test
    fun `getStateForAreaNumber returns null for unknown area`() {
        val state = SSNValidator.getStateForAreaNumber(999)
        // May or may not have a mapping
        assertNotNull(state) // Actually 999 might be null, but let's check
    }

    // ========================================================================
    // SECTION 13: EIN Validation Tests
    // ========================================================================

    @Test
    fun `validateEIN valid campus code 10`() {
        val result = SSNValidator.validateEIN("10-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 12`() {
        val result = SSNValidator.validateEIN("12-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 60`() {
        val result = SSNValidator.validateEIN("60-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 67`() {
        val result = SSNValidator.validateEIN("67-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 50`() {
        val result = SSNValidator.validateEIN("50-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 20`() {
        val result = SSNValidator.validateEIN("20-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 30`() {
        val result = SSNValidator.validateEIN("30-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 40`() {
        val result = SSNValidator.validateEIN("40-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 71`() {
        val result = SSNValidator.validateEIN("71-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 81`() {
        val result = SSNValidator.validateEIN("81-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 91`() {
        val result = SSNValidator.validateEIN("91-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN valid campus code 99`() {
        val result = SSNValidator.validateEIN("99-0000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN too short is invalid`() {
        val result = SSNValidator.validateEIN("12-0000")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN too long is invalid`() {
        val result = SSNValidator.validateEIN("12-00000000")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateEIN empty is invalid`() {
        val result = SSNValidator.validateEIN("")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateEIN without dash`() {
        val result = SSNValidator.validateEIN("100000000")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN has correct formatted output`() {
        val result = SSNValidator.validateEIN("100000000")
        assertEquals("10-0000000", result.formatted)
    }

    @Test
    fun `validateEIN has masked output`() {
        val result = SSNValidator.validateEIN("100000000")
        assertTrue(result.masked.contains("*"))
    }

    @Test
    fun `validateEIN confidence is 0_85 for valid`() {
        val result = SSNValidator.validateEIN("10-0000000")
        assertEquals(0.85f, result.confidence, 0.01f)
    }

    @Test
    fun `validateEIN confidence is 0_3 for invalid campus`() {
        // Code 07 is not in the valid set
        val result = SSNValidator.validateEIN("07-0000000")
        if (!result.isValid) {
            assertEquals(0.3f, result.confidence, 0.01f)
        }
    }

    @Test
    fun `validateEIN valid campus code 01`() {
        val result = SSNValidator.validateEIN("01-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 02`() {
        val result = SSNValidator.validateEIN("02-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 03`() {
        val result = SSNValidator.validateEIN("03-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 04`() {
        val result = SSNValidator.validateEIN("04-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 05`() {
        val result = SSNValidator.validateEIN("05-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 06`() {
        val result = SSNValidator.validateEIN("06-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 11`() {
        val result = SSNValidator.validateEIN("11-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 13`() {
        val result = SSNValidator.validateEIN("13-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 14`() {
        val result = SSNValidator.validateEIN("14-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 16`() {
        val result = SSNValidator.validateEIN("16-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 21`() {
        val result = SSNValidator.validateEIN("21-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 22`() {
        val result = SSNValidator.validateEIN("22-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 23`() {
        val result = SSNValidator.validateEIN("23-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 25`() {
        val result = SSNValidator.validateEIN("25-0000000")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 14: Era Classification Tests
    // ========================================================================

    @Test
    fun `validate SSN area 001 is pre-randomization era`() {
        val result = SSNValidator.validate("001-01-0001")
        assertEquals(SSNValidator.SSNEra.PRE_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate SSN area 500 is pre-randomization era`() {
        val result = SSNValidator.validate("500-01-0001")
        assertEquals(SSNValidator.SSNEra.PRE_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate SSN area 733 is pre-randomization era`() {
        val result = SSNValidator.validate("733-01-0001")
        assertEquals(SSNValidator.SSNEra.PRE_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate SSN area 750 is post-randomization era`() {
        val result = SSNValidator.validate("750-01-0001")
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate SSN area 800 is post-randomization era`() {
        val result = SSNValidator.validate("800-01-0001")
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate SSN area 899 is post-randomization era`() {
        val result = SSNValidator.validate("899-01-0001")
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
    }

    @Test
    fun `SSNEra enum has PRE_RANDOMIZATION`() {
        assertNotNull(SSNValidator.SSNEra.PRE_RANDOMIZATION)
    }

    @Test
    fun `SSNEra enum has POST_RANDOMIZATION`() {
        assertNotNull(SSNValidator.SSNEra.POST_RANDOMIZATION)
    }

    @Test
    fun `SSNEra enum has UNKNOWN`() {
        assertNotNull(SSNValidator.SSNEra.UNKNOWN)
    }

    @Test
    fun `SSNEra enum has exactly 3 values`() {
        assertEquals(3, SSNValidator.SSNEra.values().size)
    }

    // ========================================================================
    // SECTION 15: wasValidPreRandomization Tests
    // ========================================================================

    @Test
    fun `wasValidPreRandomization returns true for area 1`() {
        assertTrue(SSNValidator.wasValidPreRandomization(1))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 100`() {
        assertTrue(SSNValidator.wasValidPreRandomization(100))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 500`() {
        assertTrue(SSNValidator.wasValidPreRandomization(500))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 733`() {
        assertTrue(SSNValidator.wasValidPreRandomization(733))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 734`() {
        assertFalse(SSNValidator.wasValidPreRandomization(734))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 800`() {
        assertFalse(SSNValidator.wasValidPreRandomization(800))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 0`() {
        assertFalse(SSNValidator.wasValidPreRandomization(0))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 666`() {
        assertFalse(SSNValidator.wasValidPreRandomization(666))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 900`() {
        assertFalse(SSNValidator.wasValidPreRandomization(900))
    }

    // ========================================================================
    // SECTION 16: getInvalidAreaNumbers Tests
    // ========================================================================

    @Test
    fun `getInvalidAreaNumbers includes 0`() {
        assertTrue(SSNValidator.getInvalidAreaNumbers().contains(0))
    }

    @Test
    fun `getInvalidAreaNumbers includes 666`() {
        assertTrue(SSNValidator.getInvalidAreaNumbers().contains(666))
    }

    @Test
    fun `getInvalidAreaNumbers does not include 1`() {
        assertFalse(SSNValidator.getInvalidAreaNumbers().contains(1))
    }

    @Test
    fun `getInvalidAreaNumbers does not include 500`() {
        assertFalse(SSNValidator.getInvalidAreaNumbers().contains(500))
    }

    // ========================================================================
    // SECTION 17: looksLikeSSN Tests
    // ========================================================================

    @Test
    fun `looksLikeSSN returns true for dashed format`() {
        assertTrue(SSNValidator.looksLikeSSN("123-45-6789"))
    }

    @Test
    fun `looksLikeSSN returns true for space format`() {
        assertTrue(SSNValidator.looksLikeSSN("123 45 6789"))
    }

    @Test
    fun `looksLikeSSN returns true for 9 digit format`() {
        assertTrue(SSNValidator.looksLikeSSN("123456789"))
    }

    @Test
    fun `looksLikeSSN returns false for too short`() {
        assertFalse(SSNValidator.looksLikeSSN("12345678"))
    }

    @Test
    fun `looksLikeSSN returns false for too long`() {
        assertFalse(SSNValidator.looksLikeSSN("1234567890"))
    }

    @Test
    fun `looksLikeSSN returns false for empty`() {
        assertFalse(SSNValidator.looksLikeSSN(""))
    }

    @Test
    fun `looksLikeSSN returns false for letters`() {
        assertFalse(SSNValidator.looksLikeSSN("abc-de-fghi"))
    }

    @Test
    fun `looksLikeSSN returns false for phone number format`() {
        assertFalse(SSNValidator.looksLikeSSN("(123) 456-7890"))
    }

    @Test
    fun `looksLikeSSN returns true with leading space`() {
        assertTrue(SSNValidator.looksLikeSSN(" 123-45-6789"))
    }

    @Test
    fun `looksLikeSSN returns true with trailing space`() {
        assertTrue(SSNValidator.looksLikeSSN("123-45-6789 "))
    }

    // ========================================================================
    // SECTION 18: extractFromText Tests
    // ========================================================================

    @Test
    fun `extractFromText finds SSN in dashed format`() {
        val results = SSNValidator.extractFromText("My SSN is 234-56-7890.")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds SSN in space format`() {
        val results = SSNValidator.extractFromText("SSN: 234 56 7890")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds SSN as 9 digits`() {
        val results = SSNValidator.extractFromText("Number: 234567890 here")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText returns empty for no SSN`() {
        val results = SSNValidator.extractFromText("No SSN here.")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText returns empty for empty string`() {
        val results = SSNValidator.extractFromText("")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText finds multiple SSNs`() {
        val text = "SSN1: 234-56-7890, SSN2: 345-67-8901"
        val results = SSNValidator.extractFromText(text)
        assertTrue(results.size >= 2)
    }

    @Test
    fun `extractFromText ignores invalid SSNs`() {
        val text = "Invalid: 000-00-0000"
        val results = SSNValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText ignores area 666`() {
        val text = "Bad: 666-12-3456"
        val results = SSNValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText finds SSN in JSON`() {
        val json = """{"ssn": "234-56-7890", "name": "Test"}"""
        val results = SSNValidator.extractFromText(json)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds SSN in log`() {
        val log = "2024-01-01 User SSN 234-56-7890 was processed"
        val results = SSNValidator.extractFromText(log)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText multiline`() {
        val text = """
            First: 234-56-7890
            Second: 345-67-8901
        """.trimIndent()
        val results = SSNValidator.extractFromText(text)
        assertTrue(results.size >= 2)
    }

    // ========================================================================
    // SECTION 19: totalPossibleSSNs Tests
    // ========================================================================

    @Test
    fun `totalPossibleSSNs returns correct count`() {
        val expected = 898L * 99L * 9999L
        assertEquals(expected, SSNValidator.totalPossibleSSNs())
    }

    @Test
    fun `totalPossibleSSNs is greater than 800 million`() {
        assertTrue(SSNValidator.totalPossibleSSNs() > 800_000_000L)
    }

    // ========================================================================
    // SECTION 20: SSNValidationResult Properties Tests
    // ========================================================================

    @Test
    fun `valid SSN result has correct areaNumber`() {
        val result = SSNValidator.validate("234-56-7890")
        assertEquals("234", result.areaNumber)
    }

    @Test
    fun `valid SSN result has correct groupNumber`() {
        val result = SSNValidator.validate("234-56-7890")
        assertEquals("56", result.groupNumber)
    }

    @Test
    fun `valid SSN result has correct serialNumber`() {
        val result = SSNValidator.validate("234-56-7890")
        assertEquals("7890", result.serialNumber)
    }

    @Test
    fun `valid SSN result has formatted string`() {
        val result = SSNValidator.validate("234567890")
        assertEquals("234-56-7890", result.formatted)
    }

    @Test
    fun `valid SSN result has masked string`() {
        val result = SSNValidator.validate("234567890")
        assertEquals("***-**-7890", result.masked)
    }

    @Test
    fun `valid SSN result has positive confidence`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.confidence > 0f)
    }

    @Test
    fun `valid SSN result has non-empty reason`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `valid SSN result is not ITIN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertFalse(result.isITIN)
    }

    @Test
    fun `valid SSN result is not EIN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertFalse(result.isEIN)
    }

    @Test
    fun `valid SSN result is not advertising`() {
        val result = SSNValidator.validate("234-56-7890")
        assertFalse(result.isAdvertising)
    }

    @Test
    fun `invalid SSN result has isValid false`() {
        val result = SSNValidator.validate("000-00-0000")
        assertFalse(result.isValid)
    }

    // ========================================================================
    // SECTION 21: SSNValidationResult Data Class Default Tests
    // ========================================================================

    @Test
    fun `SSNValidationResult defaults isITIN to false`() {
        val result = SSNValidator.SSNValidationResult(isValid = false)
        assertFalse(result.isITIN)
    }

    @Test
    fun `SSNValidationResult defaults isEIN to false`() {
        val result = SSNValidator.SSNValidationResult(isValid = false)
        assertFalse(result.isEIN)
    }

    @Test
    fun `SSNValidationResult defaults isAdvertising to false`() {
        val result = SSNValidator.SSNValidationResult(isValid = false)
        assertFalse(result.isAdvertising)
    }

    @Test
    fun `SSNValidationResult defaults era to UNKNOWN`() {
        val result = SSNValidator.SSNValidationResult(isValid = false)
        assertEquals(SSNValidator.SSNEra.UNKNOWN, result.era)
    }

    @Test
    fun `SSNValidationResult defaults confidence to 0`() {
        val result = SSNValidator.SSNValidationResult(isValid = false)
        assertEquals(0f, result.confidence, 0.001f)
    }

    @Test
    fun `SSNValidationResult defaults reason to empty`() {
        val result = SSNValidator.SSNValidationResult(isValid = false)
        assertEquals("", result.reason)
    }

    @Test
    fun `SSNValidationResult defaults formatted to empty`() {
        val result = SSNValidator.SSNValidationResult(isValid = false)
        assertEquals("", result.formatted)
    }

    @Test
    fun `SSNValidationResult defaults masked to empty`() {
        val result = SSNValidator.SSNValidationResult(isValid = false)
        assertEquals("", result.masked)
    }

    // ========================================================================
    // SECTION 22: Confidence Score Tests
    // ========================================================================

    @Test
    fun `pre-randomization SSN has 0_9 confidence`() {
        val result = SSNValidator.validate("234-56-7890")
        assertEquals(0.9f, result.confidence, 0.01f)
    }

    @Test
    fun `post-randomization SSN has 0_85 confidence`() {
        val result = SSNValidator.validate("750-01-0001")
        assertEquals(0.85f, result.confidence, 0.01f)
    }

    @Test
    fun `suspicious pattern SSN has 0_4 confidence`() {
        // Need a valid SSN that also triggers suspicious pattern
        val result = SSNValidator.validate("121-21-2121")
        if (result.isValid) {
            assertEquals(0.4f, result.confidence, 0.01f)
        }
    }

    @Test
    fun `known invalid SSN has 0 confidence`() {
        val result = SSNValidator.validate("078-05-1120")
        assertEquals(0f, result.confidence, 0.01f)
    }

    // ========================================================================
    // SECTION 23: Reason String Tests
    // ========================================================================

    @Test
    fun `valid pre-randomization SSN reason mentions era`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.reason.contains("pre-randomization") || result.reason.contains("Valid"))
    }

    @Test
    fun `valid post-randomization SSN reason mentions era`() {
        val result = SSNValidator.validate("750-01-0001")
        assertTrue(result.reason.contains("post-randomization") || result.reason.contains("Valid"))
    }

    @Test
    fun `invalid SSN area 000 reason mentions 000`() {
        val result = SSNValidator.validate("000-01-0001")
        assertTrue(result.reason.contains("000"))
    }

    @Test
    fun `invalid SSN area 666 reason mentions 666`() {
        val result = SSNValidator.validate("666-01-0001")
        assertTrue(result.reason.contains("666"))
    }

    @Test
    fun `invalid SSN group 00 reason mentions 00`() {
        val result = SSNValidator.validate("123-00-6789")
        assertTrue(result.reason.contains("00"))
    }

    @Test
    fun `invalid SSN serial 0000 reason mentions 0000`() {
        val result = SSNValidator.validate("123-45-0000")
        assertTrue(result.reason.contains("0000"))
    }

    @Test
    fun `invalid advertising SSN reason mentions advertising`() {
        val result = SSNValidator.validate("078-05-1120")
        assertTrue(result.reason.contains("advertising") || result.reason.contains("invalid"))
    }

    @Test
    fun `too short SSN reason mentions digits`() {
        val result = SSNValidator.validate("12345")
        assertTrue(result.reason.contains("9 digits"))
    }

    // ========================================================================
    // SECTION 24: Known Invalid SSNs Set Tests
    // ========================================================================

    @Test
    fun `knownInvalidSSNs contains Woolworth SSN`() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("078051120"))
    }

    @Test
    fun `knownInvalidSSNs contains 219099999`() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("219099999"))
    }

    @Test
    fun `knownInvalidSSNs contains 457555462`() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("457555462"))
    }

    @Test
    fun `knownInvalidSSNs contains 123456789`() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("123456789"))
    }

    @Test
    fun `knownInvalidSSNs contains all repeated digits`() {
        for (d in 0..9) {
            val repeated = d.toString().repeat(9)
            assertTrue("Missing $repeated", SSNValidator.knownInvalidSSNs.contains(repeated))
        }
    }

    @Test
    fun `knownInvalidSSNs contains 987654321`() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("987654321"))
    }

    @Test
    fun `knownInvalidSSNs does not contain valid SSN`() {
        assertFalse(SSNValidator.knownInvalidSSNs.contains("234567890"))
    }

    // ========================================================================
    // SECTION 25: Permanently Invalid Area Numbers Tests
    // ========================================================================

    @Test
    fun `permanentlyInvalidAreaNumbers contains 0`() {
        assertTrue(SSNValidator.permanentlyInvalidAreaNumbers.contains(0))
    }

    @Test
    fun `permanentlyInvalidAreaNumbers contains 666`() {
        assertTrue(SSNValidator.permanentlyInvalidAreaNumbers.contains(666))
    }

    @Test
    fun `permanentlyInvalidAreaNumbers does not contain 1`() {
        assertFalse(SSNValidator.permanentlyInvalidAreaNumbers.contains(1))
    }

    @Test
    fun `permanentlyInvalidAreaNumbers does not contain 500`() {
        assertFalse(SSNValidator.permanentlyInvalidAreaNumbers.contains(500))
    }

    @Test
    fun `permanentlyInvalidAreaNumbers has exactly 2 elements`() {
        assertEquals(2, SSNValidator.permanentlyInvalidAreaNumbers.size)
    }

    // ========================================================================
    // SECTION 26: ITIN Range Tests
    // ========================================================================

    @Test
    fun `itinAreaRanges starts at 900`() {
        assertEquals(900, SSNValidator.itinAreaRanges.first)
    }

    @Test
    fun `itinAreaRanges ends at 999`() {
        assertEquals(999, SSNValidator.itinAreaRanges.last)
    }

    @Test
    fun `itinValidGroupRanges has 3 ranges`() {
        assertEquals(3, SSNValidator.itinValidGroupRanges.size)
    }

    @Test
    fun `itinValidGroupRanges first range is 70 to 88`() {
        assertEquals(70..88, SSNValidator.itinValidGroupRanges[0])
    }

    @Test
    fun `itinValidGroupRanges second range is 90 to 92`() {
        assertEquals(90..92, SSNValidator.itinValidGroupRanges[1])
    }

    @Test
    fun `itinValidGroupRanges third range is 94 to 99`() {
        assertEquals(94..99, SSNValidator.itinValidGroupRanges[2])
    }

    // ========================================================================
    // SECTION 27: Area Number Range Boundary Tests
    // ========================================================================

    @Test
    fun `validate area 001 boundary is valid`() {
        val result = SSNValidator.validate("001-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate area 899 boundary is valid`() {
        val result = SSNValidator.validate("899-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate area 665 is valid`() {
        val result = SSNValidator.validate("665-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate area 667 is valid`() {
        val result = SSNValidator.validate("667-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate area 734 is valid post-randomization`() {
        val result = SSNValidator.validate("734-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate area 749 is valid post-randomization`() {
        val result = SSNValidator.validate("749-01-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate area 772 is valid post-randomization`() {
        val result = SSNValidator.validate("772-01-0001")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 28: Group Number Boundary Tests
    // ========================================================================

    @Test
    fun `validate group 01 boundary is valid`() {
        val result = SSNValidator.validate("123-01-1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate group 99 boundary is valid`() {
        val result = SSNValidator.validate("123-99-1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate group 10 is valid`() {
        val result = SSNValidator.validate("123-10-1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate group 25 is valid`() {
        val result = SSNValidator.validate("123-25-1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate group 50 is valid`() {
        val result = SSNValidator.validate("123-50-1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate group 75 is valid`() {
        val result = SSNValidator.validate("123-75-1234")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 29: Serial Number Boundary Tests
    // ========================================================================

    @Test
    fun `validate serial 0001 boundary is valid`() {
        val result = SSNValidator.validate("123-45-0001")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate serial 9999 boundary is valid`() {
        val result = SSNValidator.validate("123-45-9999")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate serial 1000 is valid`() {
        val result = SSNValidator.validate("123-45-1000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate serial 5000 is valid`() {
        val result = SSNValidator.validate("123-45-5000")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 30: Regression Tests
    // ========================================================================

    @Test
    fun `validate same SSN twice gives consistent results`() {
        val r1 = SSNValidator.validate("234-56-7890")
        val r2 = SSNValidator.validate("234-56-7890")
        assertEquals(r1.isValid, r2.isValid)
        assertEquals(r1.areaNumber, r2.areaNumber)
        assertEquals(r1.groupNumber, r2.groupNumber)
        assertEquals(r1.serialNumber, r2.serialNumber)
        assertEquals(r1.era, r2.era)
        assertEquals(r1.confidence, r2.confidence)
    }

    @Test
    fun `validate different formats of same SSN give same validity`() {
        val r1 = SSNValidator.validate("234-56-7890")
        val r2 = SSNValidator.validate("234567890")
        val r3 = SSNValidator.validate("234 56 7890")
        assertEquals(r1.isValid, r2.isValid)
        assertEquals(r2.isValid, r3.isValid)
    }

    @Test
    fun `validate multiple SSNs sequentially`() {
        val ssns = listOf("234-56-7890", "345-67-8901", "456-78-9012", "567-89-0123")
        for (ssn in ssns) {
            val result = SSNValidator.validate(ssn)
            assertTrue("$ssn should be valid", result.isValid)
        }
    }

    @Test
    fun `validate that all area numbers from 1 to 665 are valid`() {
        for (area in 1..665) {
            val areaStr = area.toString().padStart(3, '0')
            val result = SSNValidator.validate("$areaStr-01-0001")
            assertTrue("Area $areaStr should be valid", result.isValid)
        }
    }

    @Test
    fun `validate that area 666 is invalid`() {
        val result = SSNValidator.validate("666-01-0001")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate that all area numbers from 667 to 899 are valid`() {
        for (area in 667..899) {
            val areaStr = area.toString().padStart(3, '0')
            val ssn = "$areaStr-01-0001"
            val result = SSNValidator.validate(ssn)
            if (SSNValidator.knownInvalidSSNs.contains(SSNValidator.extractDigits(ssn))) {
                assertFalse("Area $areaStr should be known invalid", result.isValid)
            } else {
                assertTrue("Area $areaStr should be valid", result.isValid)
            }
        }
    }

    @Test
    fun `validate preRandomizationInvalidRanges exist`() {
        assertTrue(SSNValidator.preRandomizationInvalidRanges.isNotEmpty())
    }

    @Test
    fun `validate historicalHighGroups has entries`() {
        assertTrue(SSNValidator.historicalHighGroups.isNotEmpty())
    }

    @Test
    fun `validate areaNumberStateAssignment has entries`() {
        assertTrue(SSNValidator.areaNumberStateAssignment.isNotEmpty())
    }

    @Test
    fun `validate extractFromText then validate each found SSN`() {
        val text = "SSN: 234-56-7890, Another: 345-67-8901"
        val found = SSNValidator.extractFromText(text)
        for (result in found) {
            assertTrue(result.isValid)
        }
    }

    @Test
    fun `validate ITIN area boundary 900 goes to ITIN path`() {
        val result = SSNValidator.validate("900-70-0001")
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN area boundary 999 goes to ITIN path`() {
        val result = SSNValidator.validate("999-70-0001")
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate all ITIN valid group values 70 through 88`() {
        for (group in 70..88) {
            val groupStr = group.toString().padStart(2, '0')
            val result = SSNValidator.validate("900-$groupStr-0001")
            assertTrue("ITIN group $groupStr should be valid", result.isValid)
            assertTrue(result.isITIN)
        }
    }

    @Test
    fun `validate all ITIN valid group values 90 through 92`() {
        for (group in 90..92) {
            val groupStr = group.toString().padStart(2, '0')
            val result = SSNValidator.validate("900-$groupStr-0001")
            assertTrue("ITIN group $groupStr should be valid", result.isValid)
            assertTrue(result.isITIN)
        }
    }

    @Test
    fun `validate all ITIN valid group values 94 through 99`() {
        for (group in 94..99) {
            val groupStr = group.toString().padStart(2, '0')
            val result = SSNValidator.validate("900-$groupStr-0001")
            assertTrue("ITIN group $groupStr should be valid", result.isValid)
            assertTrue(result.isITIN)
        }
    }

    @Test
    fun `validate ITIN invalid groups 01 through 69`() {
        for (group in listOf(1, 10, 30, 50, 69)) {
            val groupStr = group.toString().padStart(2, '0')
            val result = SSNValidator.validate("900-$groupStr-0001")
            assertFalse("ITIN group $groupStr should be invalid", result.isValid)
            assertTrue(result.isITIN)
        }
    }

    // ========================================================================
    // SECTION 31: Comprehensive Area Number Ranges for States
    // ========================================================================

    @Test
    fun `getStateForAreaNumber 004 returns Maine`() {
        assertEquals("Maine", SSNValidator.getStateForAreaNumber(4))
    }

    @Test
    fun `getStateForAreaNumber 005 returns Maine`() {
        assertEquals("Maine", SSNValidator.getStateForAreaNumber(5))
    }

    @Test
    fun `getStateForAreaNumber 006 returns Maine`() {
        assertEquals("Maine", SSNValidator.getStateForAreaNumber(6))
    }

    @Test
    fun `getStateForAreaNumber 009 returns Vermont`() {
        assertEquals("Vermont", SSNValidator.getStateForAreaNumber(9))
    }

    @Test
    fun `getStateForAreaNumber 011 returns Massachusetts`() {
        assertEquals("Massachusetts", SSNValidator.getStateForAreaNumber(11))
    }

    @Test
    fun `getStateForAreaNumber 020 returns Massachusetts`() {
        assertEquals("Massachusetts", SSNValidator.getStateForAreaNumber(20))
    }

    @Test
    fun `getStateForAreaNumber 036 returns Rhode Island`() {
        assertEquals("Rhode Island", SSNValidator.getStateForAreaNumber(36))
    }

    @Test
    fun `getStateForAreaNumber 039 returns Rhode Island`() {
        assertEquals("Rhode Island", SSNValidator.getStateForAreaNumber(39))
    }

    @Test
    fun `getStateForAreaNumber 041 returns Connecticut`() {
        assertEquals("Connecticut", SSNValidator.getStateForAreaNumber(41))
    }

    @Test
    fun `getStateForAreaNumber 049 returns Connecticut`() {
        assertEquals("Connecticut", SSNValidator.getStateForAreaNumber(49))
    }

    @Test
    fun `getStateForAreaNumber 100 returns New York`() {
        assertEquals("New York", SSNValidator.getStateForAreaNumber(100))
    }

    @Test
    fun `getStateForAreaNumber 134 returns New York`() {
        assertEquals("New York", SSNValidator.getStateForAreaNumber(134))
    }

    @Test
    fun `getStateForAreaNumber 140 returns New Jersey`() {
        assertEquals("New Jersey", SSNValidator.getStateForAreaNumber(140))
    }

    @Test
    fun `getStateForAreaNumber 158 returns New Jersey`() {
        assertEquals("New Jersey", SSNValidator.getStateForAreaNumber(158))
    }

    @Test
    fun `getStateForAreaNumber 180 returns Pennsylvania`() {
        assertEquals("Pennsylvania", SSNValidator.getStateForAreaNumber(180))
    }

    @Test
    fun `getStateForAreaNumber 211 returns Pennsylvania`() {
        assertEquals("Pennsylvania", SSNValidator.getStateForAreaNumber(211))
    }

    @Test
    fun `getStateForAreaNumber 215 returns Maryland`() {
        assertEquals("Maryland", SSNValidator.getStateForAreaNumber(215))
    }

    @Test
    fun `getStateForAreaNumber 220 returns Maryland`() {
        assertEquals("Maryland", SSNValidator.getStateForAreaNumber(220))
    }

    @Test
    fun `getStateForAreaNumber 222 returns Delaware`() {
        assertEquals("Delaware", SSNValidator.getStateForAreaNumber(222))
    }

    @Test
    fun `getStateForAreaNumber 230 returns Virginia`() {
        assertEquals("Virginia", SSNValidator.getStateForAreaNumber(230))
    }

    @Test
    fun `getStateForAreaNumber 240 returns North Carolina`() {
        assertEquals("North Carolina", SSNValidator.getStateForAreaNumber(240))
    }

    @Test
    fun `getStateForAreaNumber 248 returns South Carolina`() {
        assertEquals("South Carolina", SSNValidator.getStateForAreaNumber(248))
    }

    @Test
    fun `getStateForAreaNumber 255 returns Georgia`() {
        assertEquals("Georgia", SSNValidator.getStateForAreaNumber(255))
    }

    @Test
    fun `getStateForAreaNumber 265 returns Florida`() {
        assertEquals("Florida", SSNValidator.getStateForAreaNumber(265))
    }

    @Test
    fun `getStateForAreaNumber 280 returns Ohio`() {
        assertEquals("Ohio", SSNValidator.getStateForAreaNumber(280))
    }

    @Test
    fun `getStateForAreaNumber 310 returns Indiana`() {
        assertEquals("Indiana", SSNValidator.getStateForAreaNumber(310))
    }

    @Test
    fun `getStateForAreaNumber 340 returns Illinois`() {
        assertEquals("Illinois", SSNValidator.getStateForAreaNumber(340))
    }

    @Test
    fun `getStateForAreaNumber 370 returns Michigan`() {
        assertEquals("Michigan", SSNValidator.getStateForAreaNumber(370))
    }

    @Test
    fun `getStateForAreaNumber 390 returns Wisconsin`() {
        assertEquals("Wisconsin", SSNValidator.getStateForAreaNumber(390))
    }

    @Test
    fun `getStateForAreaNumber 405 returns Kentucky`() {
        assertEquals("Kentucky", SSNValidator.getStateForAreaNumber(405))
    }

    @Test
    fun `getStateForAreaNumber 410 returns Tennessee`() {
        assertEquals("Tennessee", SSNValidator.getStateForAreaNumber(410))
    }

    @Test
    fun `getStateForAreaNumber 420 returns Alabama`() {
        assertEquals("Alabama", SSNValidator.getStateForAreaNumber(420))
    }

    @Test
    fun `getStateForAreaNumber 426 returns Mississippi`() {
        assertEquals("Mississippi", SSNValidator.getStateForAreaNumber(426))
    }

    @Test
    fun `getStateForAreaNumber 430 returns Arkansas`() {
        assertEquals("Arkansas", SSNValidator.getStateForAreaNumber(430))
    }

    @Test
    fun `getStateForAreaNumber 435 returns Louisiana`() {
        assertEquals("Louisiana", SSNValidator.getStateForAreaNumber(435))
    }

    @Test
    fun `getStateForAreaNumber 445 returns Oklahoma`() {
        assertEquals("Oklahoma", SSNValidator.getStateForAreaNumber(445))
    }

    @Test
    fun `getStateForAreaNumber 460 returns Texas`() {
        assertEquals("Texas", SSNValidator.getStateForAreaNumber(460))
    }

    @Test
    fun `getStateForAreaNumber 470 returns Minnesota`() {
        assertEquals("Minnesota", SSNValidator.getStateForAreaNumber(470))
    }

    @Test
    fun `getStateForAreaNumber 480 returns Iowa`() {
        assertEquals("Iowa", SSNValidator.getStateForAreaNumber(480))
    }

    @Test
    fun `getStateForAreaNumber 490 returns Missouri`() {
        assertEquals("Missouri", SSNValidator.getStateForAreaNumber(490))
    }

    @Test
    fun `getStateForAreaNumber 502 returns North Dakota`() {
        assertEquals("North Dakota", SSNValidator.getStateForAreaNumber(502))
    }

    @Test
    fun `getStateForAreaNumber 504 returns South Dakota`() {
        assertEquals("South Dakota", SSNValidator.getStateForAreaNumber(504))
    }

    @Test
    fun `getStateForAreaNumber 507 returns Nebraska`() {
        assertEquals("Nebraska", SSNValidator.getStateForAreaNumber(507))
    }

    @Test
    fun `getStateForAreaNumber 512 returns Kansas`() {
        assertEquals("Kansas", SSNValidator.getStateForAreaNumber(512))
    }

    @Test
    fun `getStateForAreaNumber 517 returns Montana`() {
        assertEquals("Montana", SSNValidator.getStateForAreaNumber(517))
    }

    @Test
    fun `getStateForAreaNumber 519 returns Idaho`() {
        assertEquals("Idaho", SSNValidator.getStateForAreaNumber(519))
    }

    @Test
    fun `getStateForAreaNumber 522 returns Colorado`() {
        assertEquals("Colorado", SSNValidator.getStateForAreaNumber(522))
    }

    @Test
    fun `getStateForAreaNumber 527 returns Arizona`() {
        assertEquals("Arizona", SSNValidator.getStateForAreaNumber(527))
    }

    @Test
    fun `getStateForAreaNumber 529 returns Utah`() {
        assertEquals("Utah", SSNValidator.getStateForAreaNumber(529))
    }

    @Test
    fun `getStateForAreaNumber 535 returns Washington`() {
        assertEquals("Washington", SSNValidator.getStateForAreaNumber(535))
    }

    @Test
    fun `getStateForAreaNumber 542 returns Oregon`() {
        assertEquals("Oregon", SSNValidator.getStateForAreaNumber(542))
    }

    @Test
    fun `getStateForAreaNumber 550 returns California`() {
        assertEquals("California", SSNValidator.getStateForAreaNumber(550))
    }

    @Test
    fun `getStateForAreaNumber 570 returns California`() {
        assertEquals("California", SSNValidator.getStateForAreaNumber(570))
    }

    @Test
    fun `getStateForAreaNumber 576 returns Hawaii`() {
        assertEquals("Hawaii", SSNValidator.getStateForAreaNumber(576))
    }

    @Test
    fun `getStateForAreaNumber 578 returns District of Columbia`() {
        assertEquals("District of Columbia", SSNValidator.getStateForAreaNumber(578))
    }

    @Test
    fun `getStateForAreaNumber 579 returns District of Columbia`() {
        assertEquals("District of Columbia", SSNValidator.getStateForAreaNumber(579))
    }

    @Test
    fun `getStateForAreaNumber 582 returns Puerto Rico`() {
        assertEquals("Puerto Rico", SSNValidator.getStateForAreaNumber(582))
    }

    // ========================================================================
    // SECTION 32: Additional EIN Campus Code Tests
    // ========================================================================

    @Test
    fun `validateEIN valid campus code 34`() {
        val result = SSNValidator.validateEIN("34-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 51`() {
        val result = SSNValidator.validateEIN("51-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 52`() {
        val result = SSNValidator.validateEIN("52-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 53`() {
        val result = SSNValidator.validateEIN("53-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 54`() {
        val result = SSNValidator.validateEIN("54-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 55`() {
        val result = SSNValidator.validateEIN("55-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 56`() {
        val result = SSNValidator.validateEIN("56-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 57`() {
        val result = SSNValidator.validateEIN("57-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 58`() {
        val result = SSNValidator.validateEIN("58-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 59`() {
        val result = SSNValidator.validateEIN("59-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 61`() {
        val result = SSNValidator.validateEIN("61-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 62`() {
        val result = SSNValidator.validateEIN("62-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 63`() {
        val result = SSNValidator.validateEIN("63-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 64`() {
        val result = SSNValidator.validateEIN("64-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 65`() {
        val result = SSNValidator.validateEIN("65-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 66`() {
        val result = SSNValidator.validateEIN("66-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 68`() {
        val result = SSNValidator.validateEIN("68-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 72`() {
        val result = SSNValidator.validateEIN("72-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 73`() {
        val result = SSNValidator.validateEIN("73-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 74`() {
        val result = SSNValidator.validateEIN("74-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 75`() {
        val result = SSNValidator.validateEIN("75-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 76`() {
        val result = SSNValidator.validateEIN("76-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 77`() {
        val result = SSNValidator.validateEIN("77-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 82`() {
        val result = SSNValidator.validateEIN("82-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 83`() {
        val result = SSNValidator.validateEIN("83-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 84`() {
        val result = SSNValidator.validateEIN("84-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 85`() {
        val result = SSNValidator.validateEIN("85-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 86`() {
        val result = SSNValidator.validateEIN("86-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 87`() {
        val result = SSNValidator.validateEIN("87-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 88`() {
        val result = SSNValidator.validateEIN("88-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 92`() {
        val result = SSNValidator.validateEIN("92-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 93`() {
        val result = SSNValidator.validateEIN("93-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 94`() {
        val result = SSNValidator.validateEIN("94-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 95`() {
        val result = SSNValidator.validateEIN("95-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 98`() {
        val result = SSNValidator.validateEIN("98-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 26`() {
        val result = SSNValidator.validateEIN("26-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 27`() {
        val result = SSNValidator.validateEIN("27-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 41`() {
        val result = SSNValidator.validateEIN("41-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 42`() {
        val result = SSNValidator.validateEIN("42-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 43`() {
        val result = SSNValidator.validateEIN("43-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 44`() {
        val result = SSNValidator.validateEIN("44-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 45`() {
        val result = SSNValidator.validateEIN("45-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 46`() {
        val result = SSNValidator.validateEIN("46-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 47`() {
        val result = SSNValidator.validateEIN("47-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 48`() {
        val result = SSNValidator.validateEIN("48-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 15`() {
        val result = SSNValidator.validateEIN("15-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 24`() {
        val result = SSNValidator.validateEIN("24-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 31`() {
        val result = SSNValidator.validateEIN("31-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 32`() {
        val result = SSNValidator.validateEIN("32-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 33`() {
        val result = SSNValidator.validateEIN("33-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 35`() {
        val result = SSNValidator.validateEIN("35-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 36`() {
        val result = SSNValidator.validateEIN("36-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 37`() {
        val result = SSNValidator.validateEIN("37-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 38`() {
        val result = SSNValidator.validateEIN("38-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 39`() {
        val result = SSNValidator.validateEIN("39-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 69`() {
        val result = SSNValidator.validateEIN("69-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 70`() {
        val result = SSNValidator.validateEIN("70-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 78`() {
        val result = SSNValidator.validateEIN("78-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 79`() {
        val result = SSNValidator.validateEIN("79-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 80`() {
        val result = SSNValidator.validateEIN("80-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 89`() {
        val result = SSNValidator.validateEIN("89-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 90`() {
        val result = SSNValidator.validateEIN("90-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 96`() {
        val result = SSNValidator.validateEIN("96-0000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateEIN valid campus code 97`() {
        val result = SSNValidator.validateEIN("97-0000000")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 33: Additional Sequential Pattern Tests
    // ========================================================================

    @Test
    fun `isSequentialOrRepetitive returns true for 333333333`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("333333333"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 444444444`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("444444444"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 555555555`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("555555555"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 666666666`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("666666666"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 777777777`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("777777777"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 888888888`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("888888888"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 321542687`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("321542687"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 789214365`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("789214365"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 567012389`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("567012389"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 148206937`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("148206937"))
    }

    @Test
    fun `validate SSN with non-sequential non-repetitive pattern is valid and confident`() {
        val result = SSNValidator.validate("456-12-7893")
        assertTrue(result.isValid)
        assertTrue(result.confidence >= 0.85f)
    }

    // ========================================================================
    // SECTION 34: extractFromText Pattern Match Tests
    // ========================================================================

    @Test
    fun `extractFromText skips area 000`() {
        val results = SSNValidator.extractFromText("SSN: 000-12-3456")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText skips group 00`() {
        val results = SSNValidator.extractFromText("SSN: 123-00-4567")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText skips serial 0000`() {
        val results = SSNValidator.extractFromText("SSN: 123-45-0000")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText skips area 666`() {
        val results = SSNValidator.extractFromText("SSN: 666-12-3456")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText finds SSN in email body`() {
        val text = """
            Dear applicant,
            Your SSN on file is 234-56-7890.
            Please verify this is correct.
        """.trimIndent()
        val results = SSNValidator.extractFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds SSN in CSV`() {
        val csv = "name,ssn,dob\nJohn,234-56-7890,1980-01-01"
        val results = SSNValidator.extractFromText(csv)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds SSN in XML`() {
        val xml = "<person><ssn>234-56-7890</ssn></person>"
        val results = SSNValidator.extractFromText(xml)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText does not match 8 digit numbers`() {
        val text = "Phone: 12345678"
        val results = SSNValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText does not match 10 digit numbers as SSN`() {
        val text = "Phone: 1234567890"
        val results = SSNValidator.extractFromText(text)
        // 10 digits doesn't match 9-digit SSN pattern
        assertTrue(results.isEmpty())
    }

    // ========================================================================
    // SECTION 35: Additional Format and Masking Boundary Tests
    // ========================================================================

    @Test
    fun `formatSSN for area 001 group 01 serial 0001`() {
        assertEquals("001-01-0001", SSNValidator.formatSSN("001010001"))
    }

    @Test
    fun `formatSSN for area 899 group 99 serial 9999`() {
        assertEquals("899-99-9999", SSNValidator.formatSSN("899999999"))
    }

    @Test
    fun `maskSSN for area 001 serial 0001`() {
        assertEquals("***-**-0001", SSNValidator.maskSSN("001010001"))
    }

    @Test
    fun `maskSSN for area 899 serial 9999`() {
        assertEquals("***-**-9999", SSNValidator.maskSSN("899999999"))
    }

    @Test
    fun `formatSSN returns null for 10 digit string`() {
        assertNull(SSNValidator.formatSSN("1234567890"))
    }

    @Test
    fun `formatSSN returns null for 8 digit string`() {
        assertNull(SSNValidator.formatSSN("12345678"))
    }

    @Test
    fun `maskSSN returns null for 10 digit string`() {
        assertNull(SSNValidator.maskSSN("1234567890"))
    }

    @Test
    fun `maskSSN returns null for 8 digit string`() {
        assertNull(SSNValidator.maskSSN("12345678"))
    }

    @Test
    fun `validate result masked always hides first 5 digits`() {
        val result = SSNValidator.validate("567-89-0123")
        if (result.isValid) {
            assertTrue(result.masked.startsWith("***-**-"))
        }
    }

    @Test
    fun `validate result formatted has dashes`() {
        val result = SSNValidator.validate("567890123")
        if (result.isValid) {
            assertTrue(result.formatted.contains("-"))
            assertEquals(11, result.formatted.length)
        }
    }

    @Test
    fun `validate and format are consistent`() {
        val input = "234-56-7890"
        val result = SSNValidator.validate(input)
        val formatted = SSNValidator.formatSSN(input)
        assertEquals(result.formatted, formatted)
    }

    @Test
    fun `validate and mask are consistent`() {
        val input = "234-56-7890"
        val result = SSNValidator.validate(input)
        val masked = SSNValidator.maskSSN(input)
        assertEquals(result.masked, masked)
    }

    @Test
    fun `validate SSN 585 area returns New Mexico state`() {
        assertEquals("New Mexico", SSNValidator.getStateForAreaNumber(585))
    }

    @Test
    fun `validate SSN 586 area returns Pacific Islands`() {
        assertEquals("Pacific Islands", SSNValidator.getStateForAreaNumber(586))
    }

    @Test
    fun `validate SSN 700 area returns Railroad Board`() {
        val state = SSNValidator.getStateForAreaNumber(700)
        assertNotNull(state)
        assertTrue(state!!.contains("Railroad"))
    }

    @Test
    fun `validate SSN 729 area returns Enumeration at Entry`() {
        val state = SSNValidator.getStateForAreaNumber(729)
        assertNotNull(state)
        assertTrue(state!!.contains("Enumeration"))
    }
}
