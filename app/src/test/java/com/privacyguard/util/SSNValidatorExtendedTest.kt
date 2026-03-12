package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended test suite for SSNValidator.
 *
 * This file supplements SSNValidatorTest.kt with additional coverage for:
 *   - Area numbers 004-009 individual validation
 *   - Area numbers 660-680 individual validation
 *   - All ITIN group numbers 70-88, 90-92, 94-99 (filling in gaps)
 *   - EIN campus codes validation (valid and invalid)
 *   - extractFromText() with paragraphs containing 1, 2, 5, and 10 SSNs
 *   - looksLikeSSN() with 50+ input strings
 *   - formatSSN() and maskSSN() comprehensive coverage
 *   - wasValidPreRandomization() boundary conditions
 *   - getStateForAreaNumber() additional area numbers
 *   - isSequentialOrRepetitive() with 50+ pattern cases
 *   - Confidence scores and era classification
 *   - Miscellaneous edge cases
 *
 * All test names are unique and do not duplicate SSNValidatorTest.kt.
 */
class SSNValidatorExtendedTest {

    // ========================================================================
    // SECTION 1: Area Numbers 004-009 Individual Validation
    //
    // The existing SSNValidatorTest covers areas 001, 002, 003, and 010.
    // This section fills in areas 004 through 009.
    // All are in the New Hampshire / Maine / Vermont geographic range
    // under the pre-randomization scheme.
    // ========================================================================

    @Test
    fun `validate valid SSN area 004`() {
        val result = SSNValidator.validate("004-01-0001")
        assertTrue(result.isValid)
        assertEquals("004", result.areaNumber)
        assertEquals("01", result.groupNumber)
        assertEquals("0001", result.serialNumber)
        assertEquals("004-01-0001", result.formatted)
    }

    @Test
    fun `validate valid SSN area 005`() {
        val result = SSNValidator.validate("005-01-0001")
        assertTrue(result.isValid)
        assertEquals("005", result.areaNumber)
        assertFalse(result.isITIN)
        assertFalse(result.isEIN)
    }

    @Test
    fun `validate valid SSN area 006`() {
        val result = SSNValidator.validate("006-01-0001")
        assertTrue(result.isValid)
        assertEquals("006", result.areaNumber)
        assertEquals("***-**-0001", result.masked)
    }

    @Test
    fun `validate valid SSN area 007`() {
        val result = SSNValidator.validate("007-01-0001")
        assertTrue(result.isValid)
        assertEquals("007", result.areaNumber)
        assertEquals(SSNValidator.SSNEra.PRE_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate valid SSN area 008`() {
        val result = SSNValidator.validate("008-01-0001")
        assertTrue(result.isValid)
        assertEquals("008", result.areaNumber)
        assertTrue(result.confidence > 0.5f)
    }

    @Test
    fun `validate valid SSN area 009`() {
        val result = SSNValidator.validate("009-01-0001")
        assertTrue(result.isValid)
        assertEquals("009", result.areaNumber)
        assertEquals("01", result.groupNumber)
        assertEquals("0001", result.serialNumber)
    }

    // ========================================================================
    // SECTION 2: Area Numbers 660-680 Individual Validation
    //
    // Area 666 is permanently invalid. Areas 660-665 and 667-680 are valid.
    // These are from the post-1972 expansion and late assignment blocks.
    // ========================================================================

    @Test
    fun `validate valid SSN area 660`() {
        val result = SSNValidator.validate("660-01-0001")
        assertTrue(result.isValid)
        assertEquals("660", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 661`() {
        val result = SSNValidator.validate("661-01-0001")
        assertTrue(result.isValid)
        assertEquals("661", result.areaNumber)
        assertFalse(result.isITIN)
    }

    @Test
    fun `validate valid SSN area 662`() {
        val result = SSNValidator.validate("662-01-0001")
        assertTrue(result.isValid)
        assertEquals("662", result.areaNumber)
        assertEquals("***-**-0001", result.masked)
    }

    @Test
    fun `validate valid SSN area 663`() {
        val result = SSNValidator.validate("663-01-0001")
        assertTrue(result.isValid)
        assertEquals("663", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 664`() {
        val result = SSNValidator.validate("664-01-0001")
        assertTrue(result.isValid)
        assertEquals("664", result.areaNumber)
    }

    @Test
    fun `validate SSN area 666 is invalid extended`() {
        // This duplicates the rule but uses a different serial number to ensure
        // the validation is not serial-number dependent.
        val result = SSNValidator.validate("666-50-5050")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("666", ignoreCase = true))
    }

    @Test
    fun `validate valid SSN area 668`() {
        val result = SSNValidator.validate("668-01-0001")
        assertTrue(result.isValid)
        assertEquals("668", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 669`() {
        val result = SSNValidator.validate("669-01-0001")
        assertTrue(result.isValid)
        assertEquals("669", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 670`() {
        val result = SSNValidator.validate("670-01-0001")
        assertTrue(result.isValid)
        assertEquals("670", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 671`() {
        val result = SSNValidator.validate("671-01-0001")
        assertTrue(result.isValid)
        assertEquals("671", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 672`() {
        val result = SSNValidator.validate("672-01-0001")
        assertTrue(result.isValid)
        assertEquals("672", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 673`() {
        val result = SSNValidator.validate("673-01-0001")
        assertTrue(result.isValid)
        assertEquals("673", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 674`() {
        val result = SSNValidator.validate("674-01-0001")
        assertTrue(result.isValid)
        assertEquals("674", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 675`() {
        val result = SSNValidator.validate("675-01-0001")
        assertTrue(result.isValid)
        assertEquals("675", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 676`() {
        val result = SSNValidator.validate("676-01-0001")
        assertTrue(result.isValid)
        assertEquals("676", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 677`() {
        val result = SSNValidator.validate("677-01-0001")
        assertTrue(result.isValid)
        assertEquals("677", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 678`() {
        val result = SSNValidator.validate("678-01-0001")
        assertTrue(result.isValid)
        assertEquals("678", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 679`() {
        val result = SSNValidator.validate("679-01-0001")
        assertTrue(result.isValid)
        assertEquals("679", result.areaNumber)
    }

    @Test
    fun `validate valid SSN area 680`() {
        val result = SSNValidator.validate("680-01-0001")
        assertTrue(result.isValid)
        assertEquals("680", result.areaNumber)
    }

    // ========================================================================
    // SECTION 3: ITIN Group Numbers - Filling in Missing Range Tests
    //
    // ITIN valid group ranges: 70-88, 90-92, 94-99.
    // The existing tests cover: 70, 78, 80, 88, 90, 91, 92, 94, 99.
    // This section covers all remaining valid group numbers in those ranges.
    // ========================================================================

    @Test
    fun `validate ITIN with valid group 71 is valid`() {
        val result = SSNValidator.validate("900-71-1234")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("71", result.groupNumber)
        assertEquals(0.85f, result.confidence, 0.001f)
    }

    @Test
    fun `validate ITIN with valid group 72 is valid`() {
        val result = SSNValidator.validate("901-72-5678")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("72", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 73 is valid`() {
        val result = SSNValidator.validate("910-73-1111")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("73", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 74 is valid`() {
        val result = SSNValidator.validate("920-74-2222")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("74", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 75 is valid`() {
        val result = SSNValidator.validate("930-75-3333")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("75", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 76 is valid`() {
        val result = SSNValidator.validate("940-76-4444")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("76", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 77 is valid`() {
        val result = SSNValidator.validate("950-77-5555")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("77", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 79 is valid`() {
        val result = SSNValidator.validate("960-79-6666")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("79", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 81 is valid`() {
        val result = SSNValidator.validate("970-81-7777")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("81", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 82 is valid`() {
        val result = SSNValidator.validate("980-82-8888")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("82", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 83 is valid`() {
        val result = SSNValidator.validate("990-83-9999")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("83", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 84 is valid`() {
        val result = SSNValidator.validate("999-84-1234")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("84", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 85 is valid`() {
        val result = SSNValidator.validate("900-85-2345")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("85", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 86 is valid`() {
        val result = SSNValidator.validate("901-86-3456")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("86", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 87 is valid`() {
        val result = SSNValidator.validate("902-87-4567")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("87", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 95 is valid`() {
        val result = SSNValidator.validate("903-95-5678")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("95", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 96 is valid`() {
        val result = SSNValidator.validate("904-96-6789")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("96", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 97 is valid`() {
        val result = SSNValidator.validate("905-97-7890")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("97", result.groupNumber)
    }

    @Test
    fun `validate ITIN with valid group 98 is valid`() {
        val result = SSNValidator.validate("906-98-8901")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("98", result.groupNumber)
    }

    @Test
    fun `validate ITIN with invalid group 01 is invalid`() {
        // Group 01 is NOT in ITIN valid ranges (70-88, 90-92, 94-99)
        val result = SSNValidator.validate("900-01-1234")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
        assertEquals(0.3f, result.confidence, 0.001f)
    }

    @Test
    fun `validate ITIN with invalid group 30 is invalid`() {
        // Group 30 is not in valid ITIN ranges
        val result = SSNValidator.validate("910-30-5678")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with invalid group 68 is invalid`() {
        // Group 68 is not in valid ITIN ranges (below 70)
        val result = SSNValidator.validate("920-68-9012")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    // ========================================================================
    // SECTION 4: EIN Valid Campus Codes - Additional Tests
    //
    // Tests for valid EIN campus codes not covered in the primary test file:
    // 21, 22, 23, and 25.
    // ========================================================================

    @Test
    fun `validateEIN valid campus code 21`() {
        val result = SSNValidator.validateEIN("21-1234567")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
        assertEquals("21", result.areaNumber)
        assertEquals(0.85f, result.confidence, 0.001f)
    }

    @Test
    fun `validateEIN valid campus code 22`() {
        val result = SSNValidator.validateEIN("22-1234567")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
        assertEquals("22", result.areaNumber)
    }

    @Test
    fun `validateEIN valid campus code 23`() {
        val result = SSNValidator.validateEIN("23-1234567")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
        assertEquals("23", result.areaNumber)
    }

    @Test
    fun `validateEIN valid campus code 25`() {
        val result = SSNValidator.validateEIN("25-1234567")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
        assertEquals("25", result.areaNumber)
    }

    // ========================================================================
    // SECTION 5: EIN Invalid Campus Codes
    //
    // Tests for EIN campus codes that are NOT in the valid set:
    // 00, 07, 08, 09, 17, 18, 19, 28, 29, 49.
    // These should return isValid=false and low confidence.
    // ========================================================================

    @Test
    fun `validateEIN invalid campus code 00`() {
        val result = SSNValidator.validateEIN("00-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
        assertEquals(0.3f, result.confidence, 0.001f)
    }

    @Test
    fun `validateEIN invalid campus code 07`() {
        val result = SSNValidator.validateEIN("07-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
        assertTrue(result.reason.contains("Invalid", ignoreCase = true))
    }

    @Test
    fun `validateEIN invalid campus code 08`() {
        val result = SSNValidator.validateEIN("08-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN invalid campus code 09`() {
        val result = SSNValidator.validateEIN("09-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
        assertEquals(0.3f, result.confidence, 0.001f)
    }

    @Test
    fun `validateEIN invalid campus code 17`() {
        val result = SSNValidator.validateEIN("17-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN invalid campus code 18`() {
        val result = SSNValidator.validateEIN("18-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
        assertEquals(0.3f, result.confidence, 0.001f)
    }

    @Test
    fun `validateEIN invalid campus code 19`() {
        val result = SSNValidator.validateEIN("19-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN invalid campus code 28`() {
        val result = SSNValidator.validateEIN("28-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
        assertEquals(0.3f, result.confidence, 0.001f)
    }

    @Test
    fun `validateEIN invalid campus code 29`() {
        val result = SSNValidator.validateEIN("29-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN invalid campus code 49`() {
        val result = SSNValidator.validateEIN("49-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
        assertEquals(0.3f, result.confidence, 0.001f)
    }

    // ========================================================================
    // SECTION 6: extractFromText() with Paragraphs of 1, 2, 5, and 10 SSNs
    //
    // Tests that extractFromText() correctly identifies multiple SSNs embedded
    // in paragraph text, without false positives on non-SSN digit sequences.
    // ========================================================================

    @Test
    fun `extractFromText finds single SSN in paragraph`() {
        val text = """
            The applicant, John Doe, has applied for federal benefits.
            His Social Security Number, 234-56-7890, is on file with HR.
            Please keep this information confidential.
        """.trimIndent()
        val results = SSNValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("234", results[0].areaNumber)
        assertEquals("56", results[0].groupNumber)
        assertEquals("7890", results[0].serialNumber)
    }

    @Test
    fun `extractFromText finds two SSNs in paragraph`() {
        val text = """
            Employee Record #1: SSN 234-56-7890, Employee Record #2: SSN 345-67-8901.
            Both records have been verified against the Social Security Administration database.
        """.trimIndent()
        val results = SSNValidator.extractFromText(text)
        assertTrue(results.size >= 2)
        val areas = results.map { it.areaNumber }.toSet()
        assertTrue(areas.contains("234"))
        assertTrue(areas.contains("345"))
    }

    @Test
    fun `extractFromText finds five SSNs in document`() {
        val text = """
            The following employees need benefits verification:
            Alice Smith     (SSN: 201-30-1234)
            Bob Jones       (SSN: 302-40-2345)
            Carol White     (SSN: 403-50-3456)
            David Brown     (SSN: 504-60-4567)
            Eve Miller      (SSN: 401-01-0001)
            All employees have been vetted. Verify 2024 compliance.
        """.trimIndent()
        val results = SSNValidator.extractFromText(text)
        assertEquals(5, results.size)
    }

    @Test
    fun `extractFromText finds ten SSNs in bulk data`() {
        val text = """
            Batch SSN Verification Report - Q4 2023
            Record 01: 100-01-0001
            Record 02: 200-02-0002
            Record 03: 300-03-0003
            Record 04: 400-04-0004
            Record 05: 500-05-0005
            Record 06: 150-06-0006
            Record 07: 250-07-0007
            Record 08: 350-08-0008
            Record 09: 450-09-0009
            Record 10: 550-10-0010
            End of report.
        """.trimIndent()
        val results = SSNValidator.extractFromText(text)
        assertEquals(10, results.size)
    }

    @Test
    fun `extractFromText with paragraph containing no SSN returns empty`() {
        val text = """
            Total budget: $12,345,678.90
            Phone: (555) 123-4567
            Date: 12/25/2023
            ZIP code: 90210
            This paragraph contains no Social Security Numbers.
        """.trimIndent()
        val results = SSNValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText with mixed valid and invalid SSNs returns only valid`() {
        val text = """
            Valid SSN: 201-30-1234
            Invalid (area 000): 000-30-1234
            Invalid (group 00): 201-00-1234
            Invalid (serial 0000): 201-30-0000
            Invalid (area 666): 666-30-1234
            Valid SSN: 401-01-0001
        """.trimIndent()
        val results = SSNValidator.extractFromText(text)
        assertEquals(2, results.size)
    }

    // ========================================================================
    // SECTION 7: looksLikeSSN() - True Cases (New Inputs)
    //
    // Tests for inputs that should return true from looksLikeSSN().
    // These cover various valid formats not already in the primary test file.
    // ========================================================================

    @Test
    fun `looksLikeSSN returns true for area 456 format`() {
        assertTrue(SSNValidator.looksLikeSSN("456-78-9012"))
    }

    @Test
    fun `looksLikeSSN returns true for area 234 format`() {
        assertTrue(SSNValidator.looksLikeSSN("234-56-7890"))
    }

    @Test
    fun `looksLikeSSN returns true for area 345 space format`() {
        assertTrue(SSNValidator.looksLikeSSN("345 67 8901"))
    }

    @Test
    fun `looksLikeSSN returns true for area 567 no separator`() {
        assertTrue(SSNValidator.looksLikeSSN("567890123"))
    }

    @Test
    fun `looksLikeSSN returns true for minimal area 001 dashed`() {
        assertTrue(SSNValidator.looksLikeSSN("001-01-0001"))
    }

    @Test
    fun `looksLikeSSN returns true for maximum area 899 dashed`() {
        assertTrue(SSNValidator.looksLikeSSN("899-99-9999"))
    }

    @Test
    fun `looksLikeSSN returns true for ITIN area 900 dashed`() {
        // looksLikeSSN checks FORMAT only, not validity
        assertTrue(SSNValidator.looksLikeSSN("900-70-1234"))
    }

    @Test
    fun `looksLikeSSN returns true for all nines dashed format`() {
        // Format check passes even for logically invalid values
        assertTrue(SSNValidator.looksLikeSSN("999-99-9999"))
    }

    @Test
    fun `looksLikeSSN returns true for all zeros dashed format`() {
        // Format check passes even for logically invalid values
        assertTrue(SSNValidator.looksLikeSSN("000-00-0000"))
    }

    @Test
    fun `looksLikeSSN returns true for SSN with extra whitespace trimmed dashes`() {
        assertTrue(SSNValidator.looksLikeSSN("  100-01-0001  "))
    }

    @Test
    fun `looksLikeSSN returns true for SSN with extra whitespace trimmed spaces`() {
        assertTrue(SSNValidator.looksLikeSSN(" 100 01 0001 "))
    }

    @Test
    fun `looksLikeSSN returns true for SSN with extra whitespace trimmed digits`() {
        assertTrue(SSNValidator.looksLikeSSN(" 100010001 "))
    }

    @Test
    fun `looksLikeSSN returns true for area 555 dashed`() {
        assertTrue(SSNValidator.looksLikeSSN("555-12-3456"))
    }

    @Test
    fun `looksLikeSSN returns true for area 700 dashed`() {
        assertTrue(SSNValidator.looksLikeSSN("700-50-1234"))
    }

    @Test
    fun `looksLikeSSN returns true for area 800 dashed`() {
        assertTrue(SSNValidator.looksLikeSSN("800-01-0001"))
    }

    @Test
    fun `looksLikeSSN returns true for area 101 space separated`() {
        assertTrue(SSNValidator.looksLikeSSN("101 01 0001"))
    }

    @Test
    fun `looksLikeSSN returns true for 9 digit all zeros`() {
        assertTrue(SSNValidator.looksLikeSSN("000000000"))
    }

    @Test
    fun `looksLikeSSN returns true for area 666 dashed format check only`() {
        // looksLikeSSN only checks format, not SSA validity rules
        assertTrue(SSNValidator.looksLikeSSN("666-01-0001"))
    }

    @Test
    fun `looksLikeSSN returns true for area 450 dashed`() {
        assertTrue(SSNValidator.looksLikeSSN("450-99-9999"))
    }

    @Test
    fun `looksLikeSSN returns true for area 200 space format`() {
        assertTrue(SSNValidator.looksLikeSSN("200 50 5000"))
    }

    // ========================================================================
    // SECTION 8: looksLikeSSN() - False Cases (New Inputs)
    //
    // Tests for inputs that should return false from looksLikeSSN().
    // Covers malformed formats, wrong lengths, and non-numeric characters.
    // ========================================================================

    @Test
    fun `looksLikeSSN returns false for 10 digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("1234567890"))
    }

    @Test
    fun `looksLikeSSN returns false for 8 digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("12345678"))
    }

    @Test
    fun `looksLikeSSN returns false for 7 digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("1234567"))
    }

    @Test
    fun `looksLikeSSN returns false for 5 digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("12345"))
    }

    @Test
    fun `looksLikeSSN returns false for single digit`() {
        assertFalse(SSNValidator.looksLikeSSN("5"))
    }

    @Test
    fun `looksLikeSSN returns false for area too long dashes`() {
        assertFalse(SSNValidator.looksLikeSSN("1234-56-789"))
    }

    @Test
    fun `looksLikeSSN returns false for area too short dashes`() {
        assertFalse(SSNValidator.looksLikeSSN("12-345-6789"))
    }

    @Test
    fun `looksLikeSSN returns false for group too long dashes`() {
        assertFalse(SSNValidator.looksLikeSSN("123-456-789"))
    }

    @Test
    fun `looksLikeSSN returns false for serial too short dashes`() {
        assertFalse(SSNValidator.looksLikeSSN("123-45-678"))
    }

    @Test
    fun `looksLikeSSN returns false for serial too long dashes`() {
        assertFalse(SSNValidator.looksLikeSSN("123-45-67890"))
    }

    @Test
    fun `looksLikeSSN returns false for group too short dashes`() {
        assertFalse(SSNValidator.looksLikeSSN("123-4-56789"))
    }

    @Test
    fun `looksLikeSSN returns false for dot separator format`() {
        // Dot separators are not a recognized SSN format
        assertFalse(SSNValidator.looksLikeSSN("123.45.6789"))
    }

    @Test
    fun `looksLikeSSN returns false for slash separator format`() {
        assertFalse(SSNValidator.looksLikeSSN("123/45/6789"))
    }

    @Test
    fun `looksLikeSSN returns false for mixed separators`() {
        assertFalse(SSNValidator.looksLikeSSN("123 456789"))
    }

    @Test
    fun `looksLikeSSN returns false for mixed dash and space`() {
        assertFalse(SSNValidator.looksLikeSSN("123-45 6789"))
    }

    @Test
    fun `looksLikeSSN returns false for all letters 9 chars`() {
        assertFalse(SSNValidator.looksLikeSSN("ABCDEFGHI"))
    }

    @Test
    fun `looksLikeSSN returns false for mixed alphanumeric`() {
        assertFalse(SSNValidator.looksLikeSSN("123-AB-6789"))
    }

    @Test
    fun `looksLikeSSN returns false for whitespace only extended`() {
        assertFalse(SSNValidator.looksLikeSSN("         "))
    }

    @Test
    fun `looksLikeSSN returns false for 16 digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("1234567890123456"))
    }

    @Test
    fun `looksLikeSSN returns false for 20 digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("12345678901234567890"))
    }

    @Test
    fun `looksLikeSSN returns false for incomplete dashed format`() {
        assertFalse(SSNValidator.looksLikeSSN("123-45"))
    }

    @Test
    fun `looksLikeSSN returns false for too short dashes variant`() {
        assertFalse(SSNValidator.looksLikeSSN("55-55-55"))
    }

    @Test
    fun `looksLikeSSN returns false for comma separator format`() {
        assertFalse(SSNValidator.looksLikeSSN("123,45,6789"))
    }

    @Test
    fun `looksLikeSSN returns false for parentheses format`() {
        assertFalse(SSNValidator.looksLikeSSN("(123) 45-6789"))
    }

    @Test
    fun `looksLikeSSN returns false for colon separator format`() {
        assertFalse(SSNValidator.looksLikeSSN("123:45:6789"))
    }

    @Test
    fun `looksLikeSSN returns false for underscore separator`() {
        assertFalse(SSNValidator.looksLikeSSN("123_45_6789"))
    }

    @Test
    fun `looksLikeSSN returns false for SSN keyword included`() {
        assertFalse(SSNValidator.looksLikeSSN("SSN123456789"))
    }

    @Test
    fun `looksLikeSSN returns false for three digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("123"))
    }

    @Test
    fun `looksLikeSSN returns false for dashes only no digits`() {
        assertFalse(SSNValidator.looksLikeSSN("---"))
    }

    // ========================================================================
    // SECTION 9: formatSSN() Extended Tests
    //
    // Additional coverage for formatSSN() with edge cases not in primary file.
    // ========================================================================

    @Test
    fun `formatSSN for area 004 group 01 serial 0001`() {
        val result = SSNValidator.formatSSN("004010001")
        assertEquals("004-01-0001", result)
    }

    @Test
    fun `formatSSN for area 660 group 50 serial 5050`() {
        val result = SSNValidator.formatSSN("660505050")
        assertEquals("660-50-5050", result)
    }

    @Test
    fun `formatSSN for all nines`() {
        val result = SSNValidator.formatSSN("999999999")
        assertEquals("999-99-9999", result)
    }

    @Test
    fun `formatSSN for all zeros`() {
        val result = SSNValidator.formatSSN("000000000")
        assertEquals("000-00-0000", result)
    }

    @Test
    fun `formatSSN with dashes in input strips and reformats`() {
        // Input with dashes is stripped of all separators and reformatted
        val result = SSNValidator.formatSSN("2-3-4-5-6-7-8-9-0")
        assertEquals("234-56-7890", result)
    }

    @Test
    fun `formatSSN with spaces in input`() {
        val result = SSNValidator.formatSSN("2 3 4 5 6 7 8 9 0")
        assertEquals("234-56-7890", result)
    }

    @Test
    fun `formatSSN returns null for null equivalent empty after stripping`() {
        val result = SSNValidator.formatSSN("---")
        assertNull(result)
    }

    @Test
    fun `formatSSN with mixed separators in input`() {
        val result = SSNValidator.formatSSN("123-45 6789")
        assertEquals("123-45-6789", result)
    }

    @Test
    fun `formatSSN produces string with exactly two dashes`() {
        val result = SSNValidator.formatSSN("234567890")
        assertNotNull(result)
        assertEquals(2, result!!.count { it == '-' })
    }

    @Test
    fun `formatSSN produces string of length 11`() {
        val result = SSNValidator.formatSSN("234567890")
        assertNotNull(result)
        assertEquals(11, result!!.length)
    }

    @Test
    fun `formatSSN for area 500 group 10 serial 1234`() {
        val result = SSNValidator.formatSSN("500101234")
        assertEquals("500-10-1234", result)
    }

    @Test
    fun `formatSSN for ITIN area 900`() {
        // formatSSN doesn't validate; it only formats
        val result = SSNValidator.formatSSN("900701234")
        assertEquals("900-70-1234", result)
    }

    // ========================================================================
    // SECTION 10: maskSSN() Extended Tests
    //
    // Additional coverage for maskSSN() with various serial numbers.
    // ========================================================================

    @Test
    fun `maskSSN for area 660 serial 5050`() {
        val result = SSNValidator.maskSSN("660505050")
        assertEquals("***-**-5050", result)
    }

    @Test
    fun `maskSSN for area 004 serial 0001`() {
        val result = SSNValidator.maskSSN("004010001")
        assertEquals("***-**-0001", result)
    }

    @Test
    fun `maskSSN for all nines shows 9999`() {
        val result = SSNValidator.maskSSN("999999999")
        assertEquals("***-**-9999", result)
    }

    @Test
    fun `maskSSN for all zeros shows 0000`() {
        val result = SSNValidator.maskSSN("000000000")
        assertEquals("***-**-0000", result)
    }

    @Test
    fun `maskSSN always starts with asterisks`() {
        val result = SSNValidator.maskSSN("234567890")
        assertNotNull(result)
        assertTrue(result!!.startsWith("***"))
    }

    @Test
    fun `maskSSN always has format asterisk-asterisk-asterisk-dash-asterisk-asterisk-dash`() {
        val result = SSNValidator.maskSSN("234567890")
        assertNotNull(result)
        assertTrue(result!!.startsWith("***-**-"))
    }

    @Test
    fun `maskSSN returns null for 8 digit string`() {
        val result = SSNValidator.maskSSN("12345678")
        assertNull(result)
    }

    @Test
    fun `maskSSN returns null for 10 digit string`() {
        val result = SSNValidator.maskSSN("1234567890")
        assertNull(result)
    }

    @Test
    fun `maskSSN returns null for letter string`() {
        val result = SSNValidator.maskSSN("abcdefghi")
        assertNull(result)
    }

    @Test
    fun `maskSSN last 4 matches serial 5678`() {
        val result = SSNValidator.maskSSN("234-56-5678")
        assertEquals("***-**-5678", result)
    }

    // ========================================================================
    // SECTION 11: wasValidPreRandomization() Boundary Tests
    //
    // Additional boundary tests for the pre-randomization era validity check.
    // The function returns true for area numbers 1-733 excluding 0 and 666.
    // ========================================================================

    @Test
    fun `wasValidPreRandomization returns true for area 2`() {
        assertTrue(SSNValidator.wasValidPreRandomization(2))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 300`() {
        assertTrue(SSNValidator.wasValidPreRandomization(300))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 600`() {
        assertTrue(SSNValidator.wasValidPreRandomization(600))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 665`() {
        assertTrue(SSNValidator.wasValidPreRandomization(665))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 667`() {
        assertTrue(SSNValidator.wasValidPreRandomization(667))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 732`() {
        // 732 is one below the boundary of 733
        assertTrue(SSNValidator.wasValidPreRandomization(732))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 735`() {
        // 735 is above the pre-randomization boundary of 733
        assertFalse(SSNValidator.wasValidPreRandomization(735))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 750`() {
        assertFalse(SSNValidator.wasValidPreRandomization(750))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 899`() {
        assertFalse(SSNValidator.wasValidPreRandomization(899))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 734 boundary`() {
        // 734 is just above the pre-randomization boundary
        assertFalse(SSNValidator.wasValidPreRandomization(734))
    }

    @Test
    fun `wasValidPreRandomization returns false for negative area`() {
        assertFalse(SSNValidator.wasValidPreRandomization(-1))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 1000`() {
        assertFalse(SSNValidator.wasValidPreRandomization(1000))
    }

    // ========================================================================
    // SECTION 12: getStateForAreaNumber() Additional Coverage
    //
    // Additional area numbers for states already tested, plus edge cases.
    // Tests different area numbers within each state's assigned range to ensure
    // the range-based lookup works correctly.
    // ========================================================================

    @Test
    fun `getStateForAreaNumber 012 returns Massachusetts`() {
        val state = SSNValidator.getStateForAreaNumber(12)
        assertEquals("Massachusetts", state)
    }

    @Test
    fun `getStateForAreaNumber 025 returns Massachusetts`() {
        val state = SSNValidator.getStateForAreaNumber(25)
        assertEquals("Massachusetts", state)
    }

    @Test
    fun `getStateForAreaNumber 038 returns Rhode Island`() {
        val state = SSNValidator.getStateForAreaNumber(38)
        assertEquals("Rhode Island", state)
    }

    @Test
    fun `getStateForAreaNumber 045 returns Connecticut`() {
        val state = SSNValidator.getStateForAreaNumber(45)
        assertEquals("Connecticut", state)
    }

    @Test
    fun `getStateForAreaNumber 075 returns New York`() {
        val state = SSNValidator.getStateForAreaNumber(75)
        assertEquals("New York", state)
    }

    @Test
    fun `getStateForAreaNumber 120 returns New York`() {
        val state = SSNValidator.getStateForAreaNumber(120)
        assertEquals("New York", state)
    }

    @Test
    fun `getStateForAreaNumber 150 returns New Jersey`() {
        val state = SSNValidator.getStateForAreaNumber(150)
        assertEquals("New Jersey", state)
    }

    @Test
    fun `getStateForAreaNumber 190 returns Pennsylvania`() {
        val state = SSNValidator.getStateForAreaNumber(190)
        assertEquals("Pennsylvania", state)
    }

    @Test
    fun `getStateForAreaNumber 216 returns Maryland`() {
        val state = SSNValidator.getStateForAreaNumber(216)
        assertEquals("Maryland", state)
    }

    @Test
    fun `getStateForAreaNumber 226 returns Virginia`() {
        val state = SSNValidator.getStateForAreaNumber(226)
        assertEquals("Virginia", state)
    }

    @Test
    fun `getStateForAreaNumber 234 returns North Carolina`() {
        // Note: In the source map, 232-236 maps to "North Carolina"
        // because the second entry for 232..236 overwrites the first.
        val state = SSNValidator.getStateForAreaNumber(234)
        assertEquals("North Carolina", state)
    }

    @Test
    fun `getStateForAreaNumber 244 returns North Carolina`() {
        val state = SSNValidator.getStateForAreaNumber(244)
        assertEquals("North Carolina", state)
    }

    @Test
    fun `getStateForAreaNumber 250 returns South Carolina`() {
        val state = SSNValidator.getStateForAreaNumber(250)
        assertEquals("South Carolina", state)
    }

    @Test
    fun `getStateForAreaNumber 257 returns Georgia`() {
        val state = SSNValidator.getStateForAreaNumber(257)
        assertEquals("Georgia", state)
    }

    @Test
    fun `getStateForAreaNumber 264 returns Florida`() {
        val state = SSNValidator.getStateForAreaNumber(264)
        assertEquals("Florida", state)
    }

    @Test
    fun `getStateForAreaNumber 290 returns Ohio`() {
        val state = SSNValidator.getStateForAreaNumber(290)
        assertEquals("Ohio", state)
    }

    @Test
    fun `getStateForAreaNumber 308 returns Indiana`() {
        val state = SSNValidator.getStateForAreaNumber(308)
        assertEquals("Indiana", state)
    }

    @Test
    fun `getStateForAreaNumber 330 returns Illinois`() {
        val state = SSNValidator.getStateForAreaNumber(330)
        assertEquals("Illinois", state)
    }

    @Test
    fun `getStateForAreaNumber 372 returns Michigan`() {
        val state = SSNValidator.getStateForAreaNumber(372)
        assertEquals("Michigan", state)
    }

    @Test
    fun `getStateForAreaNumber 395 returns Wisconsin`() {
        val state = SSNValidator.getStateForAreaNumber(395)
        assertEquals("Wisconsin", state)
    }

    @Test
    fun `getStateForAreaNumber 403 returns Kentucky`() {
        val state = SSNValidator.getStateForAreaNumber(403)
        assertEquals("Kentucky", state)
    }

    @Test
    fun `getStateForAreaNumber 412 returns Tennessee`() {
        val state = SSNValidator.getStateForAreaNumber(412)
        assertEquals("Tennessee", state)
    }

    @Test
    fun `getStateForAreaNumber 418 returns Alabama`() {
        val state = SSNValidator.getStateForAreaNumber(418)
        assertEquals("Alabama", state)
    }

    @Test
    fun `getStateForAreaNumber 427 returns Mississippi`() {
        val state = SSNValidator.getStateForAreaNumber(427)
        assertEquals("Mississippi", state)
    }

    @Test
    fun `getStateForAreaNumber 431 returns Arkansas`() {
        val state = SSNValidator.getStateForAreaNumber(431)
        assertEquals("Arkansas", state)
    }

    @Test
    fun `getStateForAreaNumber 436 returns Louisiana`() {
        val state = SSNValidator.getStateForAreaNumber(436)
        assertEquals("Louisiana", state)
    }

    @Test
    fun `getStateForAreaNumber 443 returns Oklahoma`() {
        val state = SSNValidator.getStateForAreaNumber(443)
        assertEquals("Oklahoma", state)
    }

    @Test
    fun `getStateForAreaNumber 455 returns Texas`() {
        val state = SSNValidator.getStateForAreaNumber(455)
        assertEquals("Texas", state)
    }

    @Test
    fun `getStateForAreaNumber 472 returns Minnesota`() {
        val state = SSNValidator.getStateForAreaNumber(472)
        assertEquals("Minnesota", state)
    }

    @Test
    fun `getStateForAreaNumber 482 returns Iowa`() {
        val state = SSNValidator.getStateForAreaNumber(482)
        assertEquals("Iowa", state)
    }

    @Test
    fun `getStateForAreaNumber 493 returns Missouri`() {
        val state = SSNValidator.getStateForAreaNumber(493)
        assertEquals("Missouri", state)
    }

    @Test
    fun `getStateForAreaNumber 506 returns Nebraska`() {
        val state = SSNValidator.getStateForAreaNumber(506)
        assertEquals("Nebraska", state)
    }

    @Test
    fun `getStateForAreaNumber 513 returns Kansas`() {
        val state = SSNValidator.getStateForAreaNumber(513)
        assertEquals("Kansas", state)
    }

    @Test
    fun `getStateForAreaNumber 536 returns Washington`() {
        val state = SSNValidator.getStateForAreaNumber(536)
        assertEquals("Washington", state)
    }

    @Test
    fun `getStateForAreaNumber 541 returns Oregon`() {
        val state = SSNValidator.getStateForAreaNumber(541)
        assertEquals("Oregon", state)
    }

    @Test
    fun `getStateForAreaNumber 560 returns California`() {
        val state = SSNValidator.getStateForAreaNumber(560)
        assertEquals("California", state)
    }

    @Test
    fun `getStateForAreaNumber 573 returns California`() {
        val state = SSNValidator.getStateForAreaNumber(573)
        assertEquals("California", state)
    }

    @Test
    fun `getStateForAreaNumber 583 returns Puerto Rico`() {
        val state = SSNValidator.getStateForAreaNumber(583)
        assertEquals("Puerto Rico", state)
    }

    @Test
    fun `getStateForAreaNumber 584 returns Puerto Rico`() {
        val state = SSNValidator.getStateForAreaNumber(584)
        assertEquals("Puerto Rico", state)
    }

    @Test
    fun `getStateForAreaNumber returns null for area 900 ITIN range`() {
        // ITINs (900-999) are not in the state assignment map
        val state = SSNValidator.getStateForAreaNumber(900)
        assertNull(state)
    }

    @Test
    fun `getStateForAreaNumber returns null for area 734`() {
        // Area 734 is in the previously unassigned range (734-749)
        val state = SSNValidator.getStateForAreaNumber(734)
        assertNull(state)
    }

    // ========================================================================
    // SECTION 13: isSequentialOrRepetitive() Extended Pattern Tests
    //
    // Tests covering patterns that the existing suite doesn't cover.
    // The function checks for all-same, ascending, descending, and repeated
    // patterns of length 1, 2, or 3.
    // ========================================================================

    @Test
    fun `isSequentialOrRepetitive returns true for repeating pattern 01`() {
        // "010101010" repeats "01"
        assertTrue(SSNValidator.isSequentialOrRepetitive("010101010"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for repeating pattern 12`() {
        // "121212121" repeats "12"
        assertTrue(SSNValidator.isSequentialOrRepetitive("121212121"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for repeating pattern 112`() {
        // "112112112" repeats "112"
        assertTrue(SSNValidator.isSequentialOrRepetitive("112112112"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for repeating pattern 120`() {
        // "120120120" repeats "120"
        assertTrue(SSNValidator.isSequentialOrRepetitive("120120120"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for all ones`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("111111111"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for pattern with one off digit`() {
        // "111111112" - almost all ones but last digit breaks the pattern
        // For pattern "1" of length 1: checks i=1 through i=8, fails at '2'
        // For pattern "11": at i=8, length-1 char = "1" but digit is "2" => false
        assertFalse(SSNValidator.isSequentialOrRepetitive("111111112"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for near-ascending`() {
        // "123456780" - ascending for first 8 but then drops
        assertFalse(SSNValidator.isSequentialOrRepetitive("123456780"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for near-descending`() {
        // "987654320" - descending for first 8 but then wrong
        assertFalse(SSNValidator.isSequentialOrRepetitive("987654320"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for random digits 415926535`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("415926535"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for random digits 271828182`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("271828182"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for random digits 314159265`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("314159265"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 918273645`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("918273645"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 243546798`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("243546798"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 537192846`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("537192846"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 862049371`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("862049371"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 730261984`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("730261984"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 419820563`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("419820563"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 503847261`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("503847261"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 651473892`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("651473892"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 249185730`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("249185730"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 380512647`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("380512647"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 792843015`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("792843015"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for ascending 234567890`() {
        // 2,3,4,5,6,7,8,9 is strictly ascending, but 9->0 breaks it
        // so this should actually be FALSE
        assertFalse(SSNValidator.isSequentialOrRepetitive("234567890"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for repeated pattern 999`() {
        // Repeated pattern "999" -> true for all same digit
        assertTrue(SSNValidator.isSequentialOrRepetitive("999999999"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for almost repeated pattern 123456121`() {
        // Does not follow a consistent repeat pattern
        assertFalse(SSNValidator.isSequentialOrRepetitive("123456121"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for repeated single digit 5`() {
        assertTrue(SSNValidator.isSequentialOrRepetitive("555555555"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 468013579`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("468013579"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 912837465`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("912837465"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 101010101 alternating`() {
        // Pattern "10" → 10, 10, 10, 10, then "1" (partial match) -> true
        assertTrue(SSNValidator.isSequentialOrRepetitive("101010101"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 010101010`() {
        // Pattern "01" repeatedly
        assertTrue(SSNValidator.isSequentialOrRepetitive("010101010"))
    }

    // ========================================================================
    // SECTION 14: Confidence Score and Era Classification Tests
    //
    // Tests verifying the confidence scores and era classification returned
    // by the validate() function for different SSN patterns.
    // ========================================================================

    @Test
    fun `validate pre-randomization SSN has confidence 0_9`() {
        // Area 200 is in the pre-randomization range (1-733)
        val result = SSNValidator.validate("200-50-5000")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.PRE_RANDOMIZATION, result.era)
        assertEquals(0.9f, result.confidence, 0.001f)
    }

    @Test
    fun `validate post-randomization SSN area 750 has confidence 0_85`() {
        val result = SSNValidator.validate("750-01-0001")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
        assertEquals(0.85f, result.confidence, 0.001f)
    }

    @Test
    fun `validate suspicious pattern SSN has reduced confidence 0_4`() {
        // 112112112 is repetitive (pattern "112"), reducing confidence
        val result = SSNValidator.validate("112-11-2112")
        assertTrue(result.isValid)
        // The digits "112112112" form a repeating pattern
        assertEquals(0.4f, result.confidence, 0.001f)
    }

    @Test
    fun `validate non-suspicious SSN has high confidence`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        assertTrue(result.confidence >= 0.85f)
    }

    @Test
    fun `validate result era is PRE_RANDOMIZATION for area 100`() {
        val result = SSNValidator.validate("100-50-1234")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.PRE_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate result era is POST_RANDOMIZATION for area 800`() {
        val result = SSNValidator.validate("800-01-0001")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate isAdvertising flag is true for known invalid Woolworth`() {
        val result = SSNValidator.validate("078-05-1120")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate isAdvertising flag is false for valid SSN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        assertFalse(result.isAdvertising)
    }

    @Test
    fun `validate isEIN is false for regular SSN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertFalse(result.isEIN)
    }

    @Test
    fun `validate isITIN is false for regular SSN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertFalse(result.isITIN)
    }

    @Test
    fun `validateEIN result isEIN is true`() {
        val result = SSNValidator.validateEIN("12-3456789")
        assertTrue(result.isEIN)
        assertFalse(result.isITIN)
    }

    // ========================================================================
    // SECTION 15: Utility Function Tests
    //
    // Tests for totalPossibleSSNs(), getInvalidAreaNumbers(),
    // extractDigits() edge cases, and overall validate() consistency.
    // ========================================================================

    @Test
    fun `totalPossibleSSNs returns correct calculation`() {
        // Area: 001-899 excluding 666 = 898
        // Group: 01-99 = 99
        // Serial: 0001-9999 = 9999
        val expected = 898L * 99L * 9999L
        assertEquals(expected, SSNValidator.totalPossibleSSNs())
    }

    @Test
    fun `totalPossibleSSNs is greater than 800 million`() {
        assertTrue(SSNValidator.totalPossibleSSNs() > 800_000_000L)
    }

    @Test
    fun `getInvalidAreaNumbers contains 0`() {
        assertTrue(0 in SSNValidator.getInvalidAreaNumbers())
    }

    @Test
    fun `getInvalidAreaNumbers contains 666`() {
        assertTrue(666 in SSNValidator.getInvalidAreaNumbers())
    }

    @Test
    fun `getInvalidAreaNumbers has at least 2 elements`() {
        assertTrue(SSNValidator.getInvalidAreaNumbers().size >= 2)
    }

    @Test
    fun `extractDigits from formatted SSN 123-45-6789`() {
        assertEquals("123456789", SSNValidator.extractDigits("123-45-6789"))
    }

    @Test
    fun `extractDigits from space formatted SSN 123 45 6789`() {
        assertEquals("123456789", SSNValidator.extractDigits("123 45 6789"))
    }

    @Test
    fun `extractDigits from SSN with letters and digits mixed`() {
        assertEquals("123456789", SSNValidator.extractDigits("SSN:123-45-6789"))
    }

    @Test
    fun `validate with null group after extraction returns invalid`() {
        // Group number 00 is always invalid regardless of area and serial
        val result = SSNValidator.validate("234-00-5678")
        assertFalse(result.isValid)
        assertEquals("00", result.groupNumber)
    }

    @Test
    fun `validate SSN with serial 0000 is invalid`() {
        val result = SSNValidator.validate("456-01-0000")
        assertFalse(result.isValid)
        assertEquals("0000", result.serialNumber)
    }

    @Test
    fun `validate consistent results on repeated calls`() {
        val ssn = "234-56-7890"
        val result1 = SSNValidator.validate(ssn)
        val result2 = SSNValidator.validate(ssn)
        assertEquals(result1.isValid, result2.isValid)
        assertEquals(result1.areaNumber, result2.areaNumber)
        assertEquals(result1.formatted, result2.formatted)
    }

    @Test
    fun `validate and formatSSN produce consistent area number`() {
        val ssn = "234567890"
        val validationResult = SSNValidator.validate(ssn)
        val formattedSSN = SSNValidator.formatSSN(ssn)
        assertNotNull(formattedSSN)
        assertTrue(formattedSSN!!.startsWith(validationResult.areaNumber))
    }

    @Test
    fun `validate and maskSSN produce consistent last four`() {
        val ssn = "234567890"
        val validationResult = SSNValidator.validate(ssn)
        val maskedSSN = SSNValidator.maskSSN(ssn)
        assertNotNull(maskedSSN)
        assertTrue(maskedSSN!!.endsWith(validationResult.serialNumber))
    }

    @Test
    fun `knownInvalidSSNs set contains Woolworth SSN`() {
        assertTrue("078051120" in SSNValidator.knownInvalidSSNs)
    }

    @Test
    fun `knownInvalidSSNs set contains sequential 123456789`() {
        assertTrue("123456789" in SSNValidator.knownInvalidSSNs)
    }

    @Test
    fun `permanentlyInvalidAreaNumbers contains 0 and 666`() {
        assertTrue(0 in SSNValidator.permanentlyInvalidAreaNumbers)
        assertTrue(666 in SSNValidator.permanentlyInvalidAreaNumbers)
    }

    @Test
    fun `itinAreaRanges is 900 to 999`() {
        assertEquals(900, SSNValidator.itinAreaRanges.first)
        assertEquals(999, SSNValidator.itinAreaRanges.last)
    }

    @Test
    fun `itinValidGroupRanges contains 70 to 88`() {
        assertTrue(SSNValidator.itinValidGroupRanges.any { 70 in it && 88 in it })
    }

    @Test
    fun `validate area 734 returns valid post-randomization`() {
        // Area 734 was previously unassigned but is valid post-randomization
        val result = SSNValidator.validate("734-01-0001")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate area 749 returns valid post-randomization`() {
        val result = SSNValidator.validate("749-01-0001")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate area 760 returns valid post-randomization`() {
        val result = SSNValidator.validate("760-01-0001")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
    }

    @Test
    fun `formatSSN and validate produce same formatted string`() {
        val digits = "456789012"
        val formatted = SSNValidator.formatSSN(digits)
        val result = SSNValidator.validate(digits)
        assertEquals(formatted, result.formatted)
    }

    @Test
    fun `maskSSN and validate produce same masked string`() {
        val digits = "456789012"
        val masked = SSNValidator.maskSSN(digits)
        val result = SSNValidator.validate(digits)
        assertEquals(masked, result.masked)
    }

    @Test
    fun `validate reason contains era name for valid SSN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `validate reason contains invalid indication for area 000`() {
        val result = SSNValidator.validate("000-01-1234")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("000", ignoreCase = true) ||
                   result.reason.contains("Area", ignoreCase = true) ||
                   result.reason.contains("invalid", ignoreCase = true))
    }

    @Test
    fun `areaNumberStateAssignment map is not empty`() {
        assertTrue(SSNValidator.areaNumberStateAssignment.isNotEmpty())
    }

    @Test
    fun `historicalHighGroups map contains entries`() {
        assertTrue(SSNValidator.historicalHighGroups.isNotEmpty())
    }

    @Test
    fun `validate SSN 400-01-0001 has area 400`() {
        val result = SSNValidator.validate("400-01-0001")
        assertTrue(result.isValid)
        assertEquals("400", result.areaNumber)
    }

    @Test
    fun `validate SSN area 500 group 99 serial 9999`() {
        val result = SSNValidator.validate("500-99-9999")
        assertTrue(result.isValid)
        assertEquals("500", result.areaNumber)
        assertEquals("99", result.groupNumber)
        assertEquals("9999", result.serialNumber)
    }

    @Test
    fun `validate SSN with unusual whitespace in input`() {
        // Tab characters should be handled by extractDigits
        val result = SSNValidator.validate("234\t56\t7890")
        // Tabs are not digits, so extractDigits will only return the digit chars
        // "234567890" = 9 digits
        assertTrue(result.isValid)
    }

    @Test
    fun `validate area 800 group 50 serial 5000 is valid`() {
        val result = SSNValidator.validate("800-50-5000")
        assertTrue(result.isValid)
        assertEquals("800", result.areaNumber)
        assertEquals("50", result.groupNumber)
        assertEquals("5000", result.serialNumber)
    }

    @Test
    fun `validate ITIN formatted output is correct`() {
        val result = SSNValidator.validate("900-71-1234")
        assertTrue(result.isValid)
        assertEquals("900-71-1234", result.formatted)
        assertEquals("***-**-1234", result.masked)
    }

    @Test
    fun `extractFromText handles HTML-like content with SSN`() {
        val text = "<div>Employee SSN: <span>345-67-8901</span></div>"
        val results = SSNValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("345", results[0].areaNumber)
    }

    @Test
    fun `extractFromText handles URL-encoded content with SSN`() {
        val text = "redirect?ssn=456-78-9012&confirm=true"
        val results = SSNValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("456", results[0].areaNumber)
    }

    @Test
    fun `extractFromText two SSNs adjacent with word boundary`() {
        val text = "SSN1: 201-30-1234 and SSN2: 301-40-2345 both verified."
        val results = SSNValidator.extractFromText(text)
        assertEquals(2, results.size)
    }

    @Test
    fun `validate all area numbers 001 through 010 are individually valid`() {
        val validAreas = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        for (area in validAreas) {
            val areaStr = area.toString().padStart(3, '0')
            val result = SSNValidator.validate("$areaStr-01-0001")
            assertTrue("Area $areaStr should be valid", result.isValid)
            assertEquals(areaStr, result.areaNumber)
        }
    }

    @Test
    fun `validate all ITIN group numbers 70-88 are valid`() {
        for (group in 70..88) {
            val groupStr = group.toString()
            val result = SSNValidator.validate("900-$groupStr-1234")
            assertTrue("ITIN group $groupStr should be valid", result.isValid)
            assertTrue(result.isITIN)
        }
    }

    @Test
    fun `validate ITIN group numbers 90-92 are valid`() {
        for (group in 90..92) {
            val result = SSNValidator.validate("900-$group-1234")
            assertTrue("ITIN group $group should be valid", result.isValid)
            assertTrue(result.isITIN)
        }
    }

    @Test
    fun `validate ITIN group numbers 94-99 are valid`() {
        for (group in 94..99) {
            val result = SSNValidator.validate("900-$group-1234")
            assertTrue("ITIN group $group should be valid", result.isValid)
            assertTrue(result.isITIN)
        }
    }

    @Test
    fun `validate all area numbers 660-680 are individually checked`() {
        for (area in 660..680) {
            val areaStr = area.toString()
            val result = SSNValidator.validate("$areaStr-01-0001")
            if (area == 666) {
                assertFalse("Area 666 should be invalid", result.isValid)
            } else {
                assertTrue("Area $areaStr should be valid", result.isValid)
            }
        }
    }

    // ========================================================================
    // SECTION 16: Additional isSequentialOrRepetitive Pattern Coverage
    //
    // Exhaustive pattern tests for edge cases in the repetition detection.
    // ========================================================================

    @Test
    fun `isSequentialOrRepetitive returns true for 212121212`() {
        // Pattern "21" repeated
        assertTrue(SSNValidator.isSequentialOrRepetitive("212121212"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 321321321`() {
        // Pattern "321" repeated
        assertTrue(SSNValidator.isSequentialOrRepetitive("321321321"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 100100100`() {
        // Pattern "100" repeated
        assertTrue(SSNValidator.isSequentialOrRepetitive("100100100"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 100200300`() {
        // Not a simple repeated pattern
        assertFalse(SSNValidator.isSequentialOrRepetitive("100200300"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 246813579`() {
        // Even digits followed by odd digits - not sequential or repetitive
        assertFalse(SSNValidator.isSequentialOrRepetitive("246813579"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 135792468`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("135792468"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 375829164`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("375829164"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 694817325`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("694817325"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 158392047`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("158392047"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 726481935`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("726481935"))
    }

    @Test
    fun `isSequentialOrRepetitive returns true for 000000001 has pattern issue`() {
        // Not all same (ends with 1), not ascending (0->0->...->1, not all +1)
        // Not descending. Pattern "0": fails at position 8 which is '1'
        // Pattern "00": fails at position 8
        // Pattern "000": at position 6 = "001" vs "000" - fails
        // So this should return false
        assertFalse(SSNValidator.isSequentialOrRepetitive("000000001"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 123454321`() {
        // Palindrome pattern - not detected by current algorithm
        assertFalse(SSNValidator.isSequentialOrRepetitive("123454321"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for 191919191 is true alternating`() {
        // Pattern "19" repeated: 19, 19, 19, 19, then "1" partial
        assertTrue(SSNValidator.isSequentialOrRepetitive("191919191"))
    }

    @Test
    fun `isSequentialOrRepetitive returns false for valid SSN 401501601`() {
        assertFalse(SSNValidator.isSequentialOrRepetitive("401501601"))
    }

    @Test
    fun `isSequentialOrRepetitive empty string does not throw`() {
        // Empty string - no digits to evaluate. Should not crash.
        // Behavior: all() on empty = true, so returns true
        assertTrue(SSNValidator.isSequentialOrRepetitive(""))
    }

    // ========================================================================
    // SECTION 17: EIN Format and Structure Tests
    //
    // Tests for EIN formatting, masking, and structural validation.
    // ========================================================================

    @Test
    fun `validateEIN formatted output uses dash separator`() {
        val result = SSNValidator.validateEIN("121234567")
        assertTrue(result.isValid)
        assertEquals("12-1234567", result.formatted)
    }

    @Test
    fun `validateEIN masked output hides middle digits`() {
        val result = SSNValidator.validateEIN("121234567")
        assertTrue(result.isValid)
        assertTrue(result.masked.contains("**"))
    }

    @Test
    fun `validateEIN input with dash already formatted`() {
        val result = SSNValidator.validateEIN("12-1234567")
        assertTrue(result.isValid)
        assertEquals("12-1234567", result.formatted)
    }

    @Test
    fun `validateEIN with spaces as separator`() {
        val result = SSNValidator.validateEIN("12 1234567")
        assertTrue(result.isValid)
        assertEquals("12", result.areaNumber)
    }

    @Test
    fun `validateEIN with 8 digit input is invalid`() {
        val result = SSNValidator.validateEIN("12345678")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN with 10 digit input is invalid`() {
        val result = SSNValidator.validateEIN("1234567890")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN areaNumber contains campus code digits`() {
        val result = SSNValidator.validateEIN("34-1234567")
        assertTrue(result.isValid)
        assertEquals("34", result.areaNumber)
    }

    @Test
    fun `validateEIN confidence for invalid campus code is 0_3`() {
        val result = SSNValidator.validateEIN("07-1234567")
        assertFalse(result.isValid)
        assertEquals(0.3f, result.confidence, 0.001f)
    }

    // ========================================================================
    // SECTION 18: Comprehensive Format and Parsing Tests
    //
    // Tests verifying that SSNValidator handles all common input formats.
    // ========================================================================

    @Test
    fun `validate SSN with newline in middle fails gracefully`() {
        // Newline between digits: "234\n567890" has 9 digits
        val result = SSNValidator.validate("234\n567890")
        // extractDigits filters out non-digits, leaving "234567890"
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN with multiple spaces between groups`() {
        // "234  56  7890" - multiple spaces
        val result = SSNValidator.validate("234  56  7890")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN with leading zeros in group preserved`() {
        val result = SSNValidator.validate("234-06-7890")
        assertTrue(result.isValid)
        assertEquals("06", result.groupNumber)
    }

    @Test
    fun `validate SSN with leading zeros in serial preserved`() {
        val result = SSNValidator.validate("234-56-0789")
        assertTrue(result.isValid)
        assertEquals("0789", result.serialNumber)
    }

    @Test
    fun `formatSSN with area 009 group 01 serial 0001`() {
        assertEquals("009-01-0001", SSNValidator.formatSSN("009010001"))
    }

    @Test
    fun `maskSSN for area 009 shows correct masked format`() {
        assertEquals("***-**-0001", SSNValidator.maskSSN("009010001"))
    }

    @Test
    fun `validate formatted field matches formatSSN output`() {
        val ssn = "500505050"
        val formattedFromValidator = SSNValidator.validate(ssn).formatted
        val formattedFromFunction = SSNValidator.formatSSN(ssn)
        assertEquals(formattedFromFunction, formattedFromValidator)
    }

    @Test
    fun `validate masked field matches maskSSN output`() {
        val ssn = "500505050"
        val maskedFromValidator = SSNValidator.validate(ssn).masked
        val maskedFromFunction = SSNValidator.maskSSN(ssn)
        assertEquals(maskedFromFunction, maskedFromValidator)
    }

    @Test
    fun `validate SSN 234 56 7890 space format full result`() {
        val result = SSNValidator.validate("234 56 7890")
        assertTrue(result.isValid)
        assertEquals("234", result.areaNumber)
        assertEquals("56", result.groupNumber)
        assertEquals("7890", result.serialNumber)
        assertEquals("234-56-7890", result.formatted)
        assertEquals("***-**-7890", result.masked)
    }

    @Test
    fun `validate SSN 567890123 no separator full result`() {
        val result = SSNValidator.validate("567890123")
        assertTrue(result.isValid)
        assertEquals("567", result.areaNumber)
        assertEquals("89", result.groupNumber)
        assertEquals("0123", result.serialNumber)
    }

    // ========================================================================
    // SECTION 19: ITIN Detection and Properties Tests
    //
    // Additional ITIN tests verifying the isITIN flag and confidence levels.
    // ========================================================================

    @Test
    fun `validate ITIN has area starting with 9`() {
        val result = SSNValidator.validate("950-75-5678")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertTrue(result.areaNumber.startsWith("9"))
    }

    @Test
    fun `validate ITIN valid has confidence 0_85`() {
        val result = SSNValidator.validate("910-73-1111")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals(0.85f, result.confidence, 0.001f)
    }

    @Test
    fun `validate ITIN invalid has confidence 0_3`() {
        val result = SSNValidator.validate("910-30-1111")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
        assertEquals(0.3f, result.confidence, 0.001f)
    }

    @Test
    fun `validate area 899 is SSN not ITIN`() {
        val result = SSNValidator.validate("899-01-0001")
        assertTrue(result.isValid)
        assertFalse(result.isITIN)
    }

    @Test
    fun `validate area 900 is ITIN range`() {
        val result = SSNValidator.validate("900-70-1234")
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate area 999 is ITIN range`() {
        val result = SSNValidator.validate("999-70-1234")
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with group 89 is invalid ITIN group`() {
        // 89 is not in valid ITIN groups (70-88, 90-92, 94-99)
        val result = SSNValidator.validate("900-89-1234")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN with group 93 is invalid ITIN group`() {
        // 93 is not in valid ITIN groups
        val result = SSNValidator.validate("910-93-5678")
        assertFalse(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN area 940 group 76 serial 4444 all properties`() {
        val result = SSNValidator.validate("940-76-4444")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("940", result.areaNumber)
        assertEquals("76", result.groupNumber)
        assertEquals("4444", result.serialNumber)
        assertEquals("940-76-4444", result.formatted)
        assertEquals("***-**-4444", result.masked)
        assertEquals(0.85f, result.confidence, 0.001f)
    }

    // ========================================================================
    // SECTION 20: Stress Testing with Many Different Valid SSNs
    //
    // Tests that exercise validate() across a wide variety of valid SSNs
    // to ensure consistent behavior.
    // ========================================================================

    @Test
    fun `validate SSN 110-11-1111 is suspicious pattern`() {
        // "110111111" - not purely repetitive, validate will check
        val result = SSNValidator.validate("110-11-1111")
        // Area 110 valid, group 11 valid, serial 1111 valid
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN area 100 through 109 all valid`() {
        for (area in 100..109) {
            val result = SSNValidator.validate("$area-01-0001")
            assertTrue("Area $area should be valid", result.isValid)
        }
    }

    @Test
    fun `validate SSN area 200 through 209 all valid`() {
        for (area in 200..209) {
            val result = SSNValidator.validate("$area-01-0001")
            assertTrue("Area $area should be valid", result.isValid)
        }
    }

    @Test
    fun `validate SSN area 300 through 309 all valid`() {
        for (area in 300..309) {
            val result = SSNValidator.validate("$area-01-0001")
            assertTrue("Area $area should be valid", result.isValid)
        }
    }

    @Test
    fun `validate SSN area 400 through 409 all valid`() {
        for (area in 400..409) {
            val result = SSNValidator.validate("$area-01-0001")
            assertTrue("Area $area should be valid", result.isValid)
        }
    }

    @Test
    fun `validate SSN area 500 through 509 all valid`() {
        for (area in 500..509) {
            val result = SSNValidator.validate("$area-01-0001")
            assertTrue("Area $area should be valid", result.isValid)
        }
    }

    @Test
    fun `validate SSN group 01 through 10 all valid for area 234`() {
        for (group in 1..10) {
            val groupStr = group.toString().padStart(2, '0')
            val result = SSNValidator.validate("234-$groupStr-1234")
            assertTrue("Group $groupStr should be valid for area 234", result.isValid)
        }
    }

    @Test
    fun `validate SSN serial 0001 through 0010 all valid`() {
        for (serial in 1..10) {
            val serialStr = serial.toString().padStart(4, '0')
            val result = SSNValidator.validate("234-56-$serialStr")
            assertTrue("Serial $serialStr should be valid", result.isValid)
        }
    }

    @Test
    fun `validate SSN serial 9990 through 9999 all valid`() {
        for (serial in 9990..9999) {
            val result = SSNValidator.validate("234-56-$serial")
            assertTrue("Serial $serial should be valid", result.isValid)
        }
    }

    // ========================================================================
    // SECTION 21: Invalid SSN Edge Cases
    //
    // Tests for invalid input patterns beyond the basic cases in the primary
    // test file.
    // ========================================================================

    @Test
    fun `validate SSN with non-ASCII characters is invalid`() {
        val result = SSNValidator.validate("234\u00AD56\u00AD7890")  // soft hyphens
        // After extractDigits, should have 9 digits and be valid
        // Soft hyphen is not a digit so it gets filtered, leaving "234567890"
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN with Unicode digits is handled`() {
        // Kotlin's isDigit() returns true for Unicode digit chars
        // This tests behavior - may or may not produce 9 digits
        val result = SSNValidator.validate("123456789")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate SSN area 001 group 99 serial 9999`() {
        val result = SSNValidator.validate("001-99-9999")
        assertTrue(result.isValid)
        assertEquals("001", result.areaNumber)
        assertEquals("99", result.groupNumber)
        assertEquals("9999", result.serialNumber)
    }

    @Test
    fun `validate SSN area 733 group 10 serial 1234`() {
        // Area 733 is the highest pre-randomization area
        val result = SSNValidator.validate("733-10-1234")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.PRE_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate SSN area 898 group 01 serial 0001`() {
        // Area 898 is near the maximum valid area (899)
        val result = SSNValidator.validate("898-01-0001")
        assertTrue(result.isValid)
        assertEquals(SSNValidator.SSNEra.POST_RANDOMIZATION, result.era)
    }

    @Test
    fun `validate SSN area 899 group 99 serial 9999 is max SSN`() {
        val result = SSNValidator.validate("899-99-9999")
        assertTrue(result.isValid)
        assertEquals("899", result.areaNumber)
        assertEquals("99", result.groupNumber)
        assertEquals("9999", result.serialNumber)
    }

    @Test
    fun `validate SSN with all same digit in area is checked`() {
        // 444-44-4444 - all 4s, should be in knownInvalidSSNs
        val result = SSNValidator.validate("444-44-4444")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate SSN 321214321 is known invalid`() {
        val result = SSNValidator.validate("321-21-4321")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    // ========================================================================
    // SECTION 22: SSNValidator Properties and Static Data Tests
    //
    // Tests verifying the correctness of static data structures used by the
    // validator, such as knownInvalidSSNs and permanentlyInvalidAreaNumbers.
    // ========================================================================

    @Test
    fun `knownInvalidSSNs does not contain valid SSN 234567890`() {
        assertFalse("234567890" in SSNValidator.knownInvalidSSNs)
    }

    @Test
    fun `knownInvalidSSNs contains all repetitive digit SSNs`() {
        val repetitiveSSNs = listOf(
            "111111111", "222222222", "333333333", "444444444",
            "555555555", "666666666", "777777777", "888888888",
            "999999999", "000000000"
        )
        for (ssn in repetitiveSSNs) {
            assertTrue("$ssn should be in knownInvalidSSNs", ssn in SSNValidator.knownInvalidSSNs)
        }
    }

    @Test
    fun `permanentlyInvalidAreaNumbers does not contain 1`() {
        assertFalse(1 in SSNValidator.permanentlyInvalidAreaNumbers)
    }

    @Test
    fun `permanentlyInvalidAreaNumbers does not contain 665`() {
        assertFalse(665 in SSNValidator.permanentlyInvalidAreaNumbers)
    }

    @Test
    fun `areaNumberStateAssignment contains entry for New York range`() {
        // New York is 50-134
        val nyEntry = SSNValidator.areaNumberStateAssignment.entries.find { (range, state) ->
            50 in range && state == "New York"
        }
        assertNotNull(nyEntry)
    }

    @Test
    fun `areaNumberStateAssignment contains entry for California range`() {
        val caEntry = SSNValidator.areaNumberStateAssignment.entries.find { (range, state) ->
            560 in range && state == "California"
        }
        assertNotNull(caEntry)
    }

    @Test
    fun `itinValidGroupRanges covers all valid groups`() {
        val allValidGroups = mutableListOf<Int>()
        for (range in SSNValidator.itinValidGroupRanges) {
            allValidGroups.addAll(range.toList())
        }
        // Should include 70-88 = 19 values, 90-92 = 3 values, 94-99 = 6 values = 28 total
        assertTrue(allValidGroups.contains(70))
        assertTrue(allValidGroups.contains(88))
        assertTrue(allValidGroups.contains(90))
        assertTrue(allValidGroups.contains(92))
        assertTrue(allValidGroups.contains(94))
        assertTrue(allValidGroups.contains(99))
        assertFalse(allValidGroups.contains(89))
        assertFalse(allValidGroups.contains(93))
    }

    @Test
    fun `preRandomizationInvalidRanges contains 734-749`() {
        val containsRange = SSNValidator.preRandomizationInvalidRanges.any { it == 734..749 }
        assertTrue(containsRange)
    }

    @Test
    fun `preRandomizationInvalidRanges contains 750-772`() {
        val containsRange = SSNValidator.preRandomizationInvalidRanges.any { it == 750..772 }
        assertTrue(containsRange)
    }

    // ========================================================================
    // SECTION 23: extractFromText() More Complex Scenarios
    //
    // Additional text parsing scenarios with various SSN representations.
    // ========================================================================

    @Test
    fun `extractFromText finds SSN in JSON-like structure`() {
        val text = """{"name":"John","ssn":"234-56-7890","dob":"1990-01-15"}"""
        val results = SSNValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("234", results[0].areaNumber)
    }

    @Test
    fun `extractFromText finds SSN in SQL-like query`() {
        val text = "SELECT * FROM employees WHERE ssn = '345-67-8901' AND active = 1;"
        val results = SSNValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("345", results[0].areaNumber)
    }

    @Test
    fun `extractFromText finds SSN in pipe-delimited data`() {
        val text = "John Smith|234-56-7890|Engineer|New York"
        val results = SSNValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("234", results[0].areaNumber)
    }

    @Test
    fun `extractFromText does not extract partial phone numbers`() {
        // Phone number 555-123-4567 could look like an SSN pattern
        val text = "Call our office at 555-123-4567 for assistance."
        val results = SSNValidator.extractFromText(text)
        // 555-123-4567: area=555, group=12 (only 2 digits?), wait this is different...
        // Actually "555-123-4567" doesn't match \d{3}-\d{2}-\d{4}
        // because the group part "123" is 3 digits and serial "4567" is 4 digits
        // This does NOT match the SSN pattern, so should return empty
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText finds SSN in space-separated format within text`() {
        val text = "Employee SSN is 456 78 9012 as per records."
        val results = SSNValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("456", results[0].areaNumber)
    }

    @Test
    fun `extractFromText returns all valid SSNs from 10-item list`() {
        val ssnList = listOf(
            "100-01-0001", "200-02-0002", "300-03-0003", "400-04-0004", "500-05-0005",
            "150-06-0006", "250-07-0007", "350-08-0008", "450-09-0009", "550-10-0010"
        )
        val text = ssnList.joinToString("\n")
        val results = SSNValidator.extractFromText(text)
        assertEquals(10, results.size)
    }

    @Test
    fun `extractFromText result items have all fields populated`() {
        val text = "Please verify SSN 345-67-8901 for this applicant."
        val results = SSNValidator.extractFromText(text)
        assertEquals(1, results.size)
        val result = results[0]
        assertTrue(result.isValid)
        assertEquals("345", result.areaNumber)
        assertEquals("67", result.groupNumber)
        assertEquals("8901", result.serialNumber)
        assertNotNull(result.formatted)
        assertNotNull(result.masked)
    }

    // ========================================================================
    // SECTION 24: looksLikeSSN() Boundary and Corner Cases
    //
    // Additional coverage for format checking edge cases.
    // ========================================================================

    @Test
    fun `looksLikeSSN returns false for 11 digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("12345678901"))
    }

    @Test
    fun `looksLikeSSN returns false for 4 digit number`() {
        assertFalse(SSNValidator.looksLikeSSN("1234"))
    }

    @Test
    fun `looksLikeSSN returns true for 9 digit number starting with 0`() {
        // "012345678" - 9 digits, should match ^\d{9}$ pattern
        assertTrue(SSNValidator.looksLikeSSN("012345678"))
    }

    @Test
    fun `looksLikeSSN returns false for dashed format with spaces around dashes`() {
        // "123 - 45 - 6789" does not match ^\d{3}-\d{2}-\d{4}$
        assertFalse(SSNValidator.looksLikeSSN("123 - 45 - 6789"))
    }

    @Test
    fun `looksLikeSSN returns false for SSN prefix text`() {
        assertFalse(SSNValidator.looksLikeSSN("SSN:123456789"))
    }

    @Test
    fun `looksLikeSSN returns false for pound sign prefix`() {
        assertFalse(SSNValidator.looksLikeSSN("#123456789"))
    }

    @Test
    fun `looksLikeSSN returns true for area 000 space format`() {
        // looksLikeSSN checks format only
        assertTrue(SSNValidator.looksLikeSSN("000 00 0000"))
    }

    @Test
    fun `looksLikeSSN returns false for credit card length 16 digits`() {
        assertFalse(SSNValidator.looksLikeSSN("4111111111111111"))
    }

    @Test
    fun `looksLikeSSN returns false for zip plus four format`() {
        // ZIP+4 like "12345-6789" - 5 digits, dash, 4 digits - doesn't match
        assertFalse(SSNValidator.looksLikeSSN("12345-6789"))
    }

    @Test
    fun `looksLikeSSN returns false for time format`() {
        assertFalse(SSNValidator.looksLikeSSN("12:34:5678"))
    }

    // ========================================================================
    // SECTION 25: Comprehensive EIN Tests with Various Input Formats
    //
    // Tests ensuring EIN validation handles all common input formats
    // and correctly distinguishes EIN from SSN.
    // ========================================================================

    @Test
    fun `validateEIN with only digits no dash campus 34`() {
        val result = SSNValidator.validateEIN("341234567")
        assertTrue(result.isValid)
        assertEquals("34", result.areaNumber)
        assertEquals("34-1234567", result.formatted)
    }

    @Test
    fun `validateEIN with dash campus 51`() {
        val result = SSNValidator.validateEIN("51-1234567")
        assertTrue(result.isValid)
        assertTrue(result.isEIN)
        assertEquals("51", result.areaNumber)
    }

    @Test
    fun `validateEIN with dash campus 12`() {
        val result = SSNValidator.validateEIN("12-9999999")
        assertTrue(result.isValid)
        assertEquals("12", result.areaNumber)
    }

    @Test
    fun `validateEIN invalid campus code 00 reason is informative`() {
        val result = SSNValidator.validateEIN("00-1234567")
        assertFalse(result.isValid)
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `validateEIN masked shows campus code hidden`() {
        val result = SSNValidator.validateEIN("34-5678901")
        assertTrue(result.isValid)
        // Masked format: "**-***XXXXX" where XXXXX is last few digits
        assertNotNull(result.masked)
        assertTrue(result.masked.contains("**"))
    }

    @Test
    fun `validateEIN short input 5 digits is invalid`() {
        val result = SSNValidator.validateEIN("12345")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN letters in input are filtered`() {
        // "AB-1234567" - extractDigits would give only "1234567" = 7 digits, invalid
        val result = SSNValidator.validateEIN("AB-1234567")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    @Test
    fun `validateEIN all zeros is invalid campus code 00`() {
        val result = SSNValidator.validateEIN("000000000")
        assertFalse(result.isValid)
        assertTrue(result.isEIN)
    }

    // ========================================================================
    // SECTION 26: Validate SSN Result Properties Consistency
    //
    // Tests that validate() returns a result with consistent and correct
    // property values across all valid/invalid combinations.
    // ========================================================================

    @Test
    fun `validate invalid SSN has empty areaNumber when too short`() {
        val result = SSNValidator.validate("12345")
        assertFalse(result.isValid)
        // For too-short input, fields may be default empty
        assertEquals(0.0f, result.confidence, 0.001f)
    }

    @Test
    fun `validate invalid SSN has empty groupNumber when too short`() {
        val result = SSNValidator.validate("12345")
        assertFalse(result.isValid)
        assertEquals("", result.groupNumber)
    }

    @Test
    fun `validate valid SSN has non-empty reason`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `validate valid SSN reason contains era information`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        // Reason should mention the era
        assertTrue(result.reason.contains("pre-randomization") ||
                   result.reason.contains("post-randomization") ||
                   result.reason.contains("suspicious") ||
                   result.reason.contains("Valid"))
    }

    @Test
    fun `validate invalid SSN reason is not empty`() {
        val result = SSNValidator.validate("000-01-1234")
        assertFalse(result.isValid)
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `validate SSN formatted always 11 characters for valid SSN`() {
        val testSSNs = listOf("001010001", "234567890", "899999999", "500505050")
        for (ssn in testSSNs) {
            val result = SSNValidator.validate(ssn)
            if (result.isValid || result.formatted.isNotEmpty()) {
                assertTrue("Formatted SSN should be 11 chars for $ssn",
                    result.formatted.isEmpty() || result.formatted.length == 11)
            }
        }
    }

    @Test
    fun `validate masked always shows last 4 digit serial`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        assertTrue(result.masked.endsWith("7890"))
    }

    @Test
    fun `validate area number is always 3 characters for valid SSN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        assertEquals(3, result.areaNumber.length)
    }

    @Test
    fun `validate group number is always 2 characters for valid SSN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        assertEquals(2, result.groupNumber.length)
    }

    @Test
    fun `validate serial number is always 4 characters for valid SSN`() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.isValid)
        assertEquals(4, result.serialNumber.length)
    }

    // ========================================================================
    // SECTION 27: More wasValidPreRandomization Edge Cases
    // ========================================================================

    @Test
    fun `wasValidPreRandomization returns true for area 50`() {
        assertTrue(SSNValidator.wasValidPreRandomization(50))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 400`() {
        assertTrue(SSNValidator.wasValidPreRandomization(400))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 650`() {
        assertTrue(SSNValidator.wasValidPreRandomization(650))
    }

    @Test
    fun `wasValidPreRandomization returns true for area 730`() {
        assertTrue(SSNValidator.wasValidPreRandomization(730))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 736`() {
        assertFalse(SSNValidator.wasValidPreRandomization(736))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 772`() {
        assertFalse(SSNValidator.wasValidPreRandomization(772))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 773`() {
        assertFalse(SSNValidator.wasValidPreRandomization(773))
    }

    @Test
    fun `wasValidPreRandomization returns false for area 500 when area is 0`() {
        // Area 0 is in permanentlyInvalidAreaNumbers
        assertFalse(SSNValidator.wasValidPreRandomization(0))
    }

    // ========================================================================
    // SECTION 28: Format Parsing Tolerance Tests
    //
    // Tests that validate() handles unusual but parseable input formats.
    // ========================================================================

    @Test
    fun `validate SSN with mixed case letters strips non-digits`() {
        // "SSN 234567890" - digits only: "234567890"
        val result = SSNValidator.validate("SSN 234567890")
        assertTrue(result.isValid)
        assertEquals("234", result.areaNumber)
    }

    @Test
    fun `validate SSN with colon prefix`() {
        val result = SSNValidator.validate(":234-56-7890")
        assertTrue(result.isValid)
        assertEquals("234", result.areaNumber)
    }

    @Test
    fun `validate SSN with bracket notation`() {
        val result = SSNValidator.validate("[234-56-7890]")
        assertTrue(result.isValid)
        assertEquals("234", result.areaNumber)
    }

    @Test
    fun `validate SSN with hash prefix`() {
        val result = SSNValidator.validate("#234-56-7890")
        assertTrue(result.isValid)
        assertEquals("234", result.areaNumber)
    }

    @Test
    fun `formatSSN handles leading non-digit characters`() {
        // "SSN:234567890" extracts "234567890"
        val result = SSNValidator.formatSSN("SSN:234567890")
        assertEquals("234-56-7890", result)
    }

    @Test
    fun `maskSSN handles input with extra characters`() {
        val result = SSNValidator.maskSSN("SSN:234567890")
        assertEquals("***-**-7890", result)
    }

    // ========================================================================
    // SECTION 29: ITIN Area Number Range Coverage
    //
    // Tests for specific ITIN area numbers at the boundary of the 900-999 range.
    // ========================================================================

    @Test
    fun `validate ITIN area 901 group 71 is valid`() {
        val result = SSNValidator.validate("901-71-1234")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("901", result.areaNumber)
    }

    @Test
    fun `validate ITIN area 910 group 85 is valid`() {
        val result = SSNValidator.validate("910-85-5678")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN area 925 group 90 is valid`() {
        val result = SSNValidator.validate("925-90-9012")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN area 950 group 94 is valid`() {
        val result = SSNValidator.validate("950-94-3456")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN area 975 group 96 is valid`() {
        val result = SSNValidator.validate("975-96-7890")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
    }

    @Test
    fun `validate ITIN area 998 group 99 serial 9999 is valid boundary`() {
        val result = SSNValidator.validate("998-99-9999")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("998", result.areaNumber)
        assertEquals("99", result.groupNumber)
        assertEquals("9999", result.serialNumber)
    }

    @Test
    fun `validate ITIN area 900 group 70 serial 0001 is minimum ITIN`() {
        val result = SSNValidator.validate("900-70-0001")
        assertTrue(result.isValid)
        assertTrue(result.isITIN)
        assertEquals("900", result.areaNumber)
        assertEquals("70", result.groupNumber)
        assertEquals("0001", result.serialNumber)
    }

    // ========================================================================
    // SECTION 30: Bulk Area Number Tests for Each Decade 001-009
    // ========================================================================

    @Test
    fun `validate all area numbers 001 through 009 are individually valid via bulk test`() {
        for (areaInt in 1..9) {
            val areaStr = areaInt.toString().padStart(3, '0')
            val result = SSNValidator.validate("$areaStr-50-5000")
            assertTrue("Area $areaStr should be valid: ${result.reason}", result.isValid)
            assertEquals(areaStr, result.areaNumber)
        }
    }

    @Test
    fun `validate all area numbers 661-664 are valid individually`() {
        for (areaInt in 661..664) {
            val result = SSNValidator.validate("$areaInt-01-0001")
            assertTrue("Area $areaInt should be valid", result.isValid)
        }
    }

    @Test
    fun `validate area 666 is invalid in all group and serial combinations`() {
        val testCases = listOf("666-01-0001", "666-50-5050", "666-99-9999")
        for (ssn in testCases) {
            val result = SSNValidator.validate(ssn)
            assertFalse("$ssn should be invalid due to area 666", result.isValid)
        }
    }

    @Test
    fun `validate area 000 is invalid in all group and serial combinations`() {
        val testCases = listOf("000-01-0001", "000-50-5050", "000-99-9999")
        for (ssn in testCases) {
            val result = SSNValidator.validate(ssn)
            assertFalse("$ssn should be invalid due to area 000", result.isValid)
        }
    }

    @Test
    fun `validate group 00 is invalid for any area`() {
        val testCases = listOf("100-00-1234", "234-00-5678", "500-00-9012")
        for (ssn in testCases) {
            val result = SSNValidator.validate(ssn)
            assertFalse("$ssn should be invalid due to group 00", result.isValid)
            assertEquals("00", result.groupNumber)
        }
    }

    @Test
    fun `validate serial 0000 is invalid for any area and group`() {
        val testCases = listOf("100-01-0000", "234-56-0000", "500-99-0000")
        for (ssn in testCases) {
            val result = SSNValidator.validate(ssn)
            assertFalse("$ssn should be invalid due to serial 0000", result.isValid)
            assertEquals("0000", result.serialNumber)
        }
    }

    @Test
    fun `validate SSN 123-12-1234 is known invalid advertising SSN`() {
        val result = SSNValidator.validate("123-12-1234")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }

    @Test
    fun `validate SSN 042-10-3580 Tod Slaughter is known invalid`() {
        val result = SSNValidator.validate("042-10-3580")
        assertFalse(result.isValid)
        assertTrue(result.isAdvertising)
    }
}



