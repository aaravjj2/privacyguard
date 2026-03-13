package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

/**
 * SSNValidatorExtendedTest2
 *
 * Comprehensive test suite for Social Security Number (SSN) validation.
 * Covers all 20 test sections:
 *   Section 1-4: Valid area ranges 001-399 (40 tests each)
 *   Section 5-7: Valid area ranges 400-665 (30 tests each)
 *   Section 8-9: ITIN validation 900-999
 *   Section 10: Invalid area 000
 *   Section 11: Invalid area 666
 *   Section 12: Invalid areas 900-999 as regular SSN
 *   Section 13: Invalid group 00
 *   Section 14: Invalid serial 0000
 *   Section 15-17: Format variants
 *   Section 18: Sequential/fake SSNs
 *   Section 19: Text extraction
 *   Section 20: Edge cases
 *
 * Total: 300+ JUnit4 test methods
 */
class SSNValidatorExtendedTest2 {

    // =========================================================================
    // Private helper functions (validator stubs)
    // =========================================================================

    /**
     * Validates a Social Security Number string.
     * Accepts formats: "NNN-GG-SSSS", "NNN GG SSSS", or "NNNGGSSSS"
     * Rules enforced:
     *   - Must be exactly 9 digits after stripping dashes and spaces
     *   - Area (first 3 digits) cannot be 000
     *   - Area cannot be 666
     *   - Area cannot be >= 900 (those are ITIN territory)
     *   - Group (digits 4-5) cannot be 00
     *   - Serial (digits 6-9) cannot be 0000
     */
    private fun isValidSSN(ssn: String): Boolean {
        val digits = ssn.replace("-", "").replace(" ", "")
        if (digits.length != 9) return false
        if (!digits.all { it.isDigit() }) return false
        val area = digits.substring(0, 3).toInt()
        val group = digits.substring(3, 5).toInt()
        val serial = digits.substring(5).toInt()
        if (area == 0 || area == 666 || area >= 900) return false
        if (group == 0) return false
        if (serial == 0) return false
        return true
    }

    /**
     * Validates an Individual Taxpayer Identification Number (ITIN).
     * ITINs have area 900-999, non-zero group, non-zero serial.
     */
    private fun isValidITIN(itin: String): Boolean {
        val digits = itin.replace("-", "").replace(" ", "")
        if (digits.length != 9) return false
        if (!digits.all { it.isDigit() }) return false
        val area = digits.substring(0, 3).toInt()
        val group = digits.substring(3, 5).toInt()
        val serial = digits.substring(5).toInt()
        if (area < 900 || area > 999) return false
        if (group == 0) return false
        if (serial == 0) return false
        return true
    }

    /**
     * Extracts SSNs from a block of text using pattern NNN-NN-NNNN with word boundaries.
     */
    private fun extractSSNsFromText(text: String): List<String> {
        val pattern = Regex("\\b(\\d{3}[-]\\d{2}[-]\\d{4})\\b")
        return pattern.findAll(text).map { it.value }.toList()
    }

    /**
     * Masks an SSN so only the last 4 digits are visible: ***-**-NNNN
     */
    private fun maskSSN(ssn: String): String {
        val digits = ssn.replace("-", "").replace(" ", "")
        if (digits.length != 9) return ssn
        return "***-**-${digits.substring(5)}"
    }

    /**
     * Formats 9 raw digits into standard SSN format NNN-GG-SSSS.
     */
    private fun formatSSN(input: String): String {
        val clean = input.replace("-", "").replace(" ", "")
        if (clean.length != 9) return input
        return "${clean.substring(0, 3)}-${clean.substring(3, 5)}-${clean.substring(5)}"
    }

    /**
     * Checks if an SSN is a well-known sequential/fake number used in media.
     */
    private fun isSequentialFakeSSN(ssn: String): Boolean {
        val knownFakes = setOf("123456789", "219099999", "078051120", "457555462")
        val digits = ssn.replace("-", "").replace(" ", "")
        return digits in knownFakes
    }

    /**
     * Returns the count of valid SSNs in a list.
     */
    private fun countValidSSNs(list: List<String>): Int = list.count { isValidSSN(it) }

    /**
     * Returns the count of invalid SSNs in a list.
     */
    private fun countInvalidSSNs(list: List<String>): Int = list.count { !isValidSSN(it) }

    /**
     * Filters and returns only structurally valid SSNs from a list.
     */
    private fun filterValid(list: List<String>): List<String> = list.filter { isValidSSN(it) }

    /**
     * Filters and returns only structurally invalid SSNs from a list.
     */
    private fun filterInvalid(list: List<String>): List<String> = list.filter { !isValidSSN(it) }

    // =========================================================================
    // SECTION 1 — Valid SSNs: area range 001–099  (40 tests)
    // =========================================================================

    @Test
    fun `section1 test01 area 001 group 01 serial 0001 all minimum valid`() {
        val ssn = "001-01-0001"
        assertTrue("SSN $ssn with minimum valid components should pass", isValidSSN(ssn))
        assertEquals("Area extracted correctly", 1, ssn.replace("-", "").substring(0, 3).toInt())
    }

    @Test
    fun `section1 test02 area 001 group 01 serial 9999 max serial valid`() {
        val ssn = "001-01-9999"
        assertTrue("SSN $ssn with maximum serial should be valid", isValidSSN(ssn))
        val masked = maskSSN(ssn)
        assertEquals("Masked should show last 4", "***-**-9999", masked)
    }

    @Test
    fun `section1 test03 area 001 group 99 serial 9999 max group and serial valid`() {
        val ssn = "001-99-9999"
        assertTrue("SSN $ssn with max group and serial should be valid", isValidSSN(ssn))
        assertNotNull("Non-null result expected", isValidSSN(ssn))
    }

    @Test
    fun `section1 test04 area 005 group 05 serial 0505 area 005 valid`() {
        val ssn = "005-05-0505"
        assertTrue(isValidSSN(ssn))
        assertEquals("Format round-trip", ssn, formatSSN(ssn.replace("-", "")))
    }

    @Test
    fun `section1 test05 area 007 group 07 serial 0007`() {
        assertTrue(isValidSSN("007-07-0007"))
    }

    @Test
    fun `section1 test06 area 009 group 09 serial 0009`() {
        assertTrue(isValidSSN("009-09-0009"))
    }

    @Test
    fun `section1 test07 area 010 group 10 serial 0010`() {
        val ssn = "010-10-0010"
        assertTrue(isValidSSN(ssn))
        val digits = ssn.replace("-", "")
        assertEquals(9, digits.length)
    }

    @Test
    fun `section1 test08 area 011 group 11 serial 0011`() {
        assertTrue(isValidSSN("011-11-0011"))
    }

    @Test
    fun `section1 test09 area 015 group 15 serial 0015`() {
        assertTrue(isValidSSN("015-15-0015"))
    }

    @Test
    fun `section1 test10 area 019 group 19 serial 0019`() {
        assertTrue(isValidSSN("019-19-0019"))
    }

    @Test
    fun `section1 test11 area 020 group 20 serial 0020`() {
        assertTrue(isValidSSN("020-20-0020"))
    }

    @Test
    fun `section1 test12 area 025 group 25 serial 0025`() {
        val ssn = "025-25-0025"
        assertTrue(isValidSSN(ssn))
        assertFalse("ITIN check should fail for area 025", isValidITIN(ssn))
    }

    @Test
    fun `section1 test13 area 030 group 30 serial 0030`() {
        assertTrue(isValidSSN("030-30-0030"))
    }

    @Test
    fun `section1 test14 area 035 group 35 serial 0035`() {
        assertTrue(isValidSSN("035-35-0035"))
    }

    @Test
    fun `section1 test15 area 040 group 40 serial 0040`() {
        assertTrue(isValidSSN("040-40-0040"))
    }

    @Test
    fun `section1 test16 area 045 group 45 serial 0045`() {
        assertTrue(isValidSSN("045-45-0045"))
    }

    @Test
    fun `section1 test17 area 050 group 50 serial 0050`() {
        assertTrue(isValidSSN("050-50-0050"))
    }

    @Test
    fun `section1 test18 area 055 group 55 serial 0055`() {
        val ssn = "055-55-0055"
        assertTrue(isValidSSN(ssn))
        val noFmt = ssn.replace("-", "")
        assertEquals("Strip dashes yields 9 digits", 9, noFmt.length)
    }

    @Test
    fun `section1 test19 area 060 group 60 serial 0060`() {
        assertTrue(isValidSSN("060-60-0060"))
    }

    @Test
    fun `section1 test20 area 065 group 65 serial 0065`() {
        assertTrue(isValidSSN("065-65-0065"))
    }

    @Test
    fun `section1 test21 area 070 group 70 serial 0070`() {
        assertTrue(isValidSSN("070-70-0070"))
    }

    @Test
    fun `section1 test22 area 075 group 75 serial 0075`() {
        assertTrue(isValidSSN("075-75-0075"))
    }

    @Test
    fun `section1 test23 area 080 group 80 serial 0080`() {
        assertTrue(isValidSSN("080-80-0080"))
    }

    @Test
    fun `section1 test24 area 085 group 85 serial 0085`() {
        assertTrue(isValidSSN("085-85-0085"))
    }

    @Test
    fun `section1 test25 area 090 group 90 serial 0090`() {
        assertTrue(isValidSSN("090-90-0090"))
    }

    @Test
    fun `section1 test26 area 095 group 95 serial 0095`() {
        assertTrue(isValidSSN("095-95-0095"))
    }

    @Test
    fun `section1 test27 area 099 group 99 serial 9999 near boundary`() {
        val ssn = "099-99-9999"
        assertTrue(isValidSSN(ssn))
    }

    @Test
    fun `section1 test28 area 001 group 50 serial 5050 mid values`() {
        assertTrue(isValidSSN("001-50-5050"))
    }

    @Test
    fun `section1 test29 area 099 group 01 serial 0099`() {
        assertTrue(isValidSSN("099-01-0099"))
    }

    @Test
    fun `section1 test30 area 002 group 02 serial 0202`() {
        val ssn = "002-02-0202"
        assertTrue(isValidSSN(ssn))
        assertEquals("***-**-0202", maskSSN(ssn))
    }

    @Test
    fun `section1 test31 area 003 group 03 serial 0303`() {
        assertTrue(isValidSSN("003-03-0303"))
    }

    @Test
    fun `section1 test32 area 004 group 04 serial 0404`() {
        assertTrue(isValidSSN("004-04-0404"))
    }

    @Test
    fun `section1 test33 area 006 group 06 serial 0606`() {
        assertTrue(isValidSSN("006-06-0606"))
    }

    @Test
    fun `section1 test34 area 008 group 08 serial 0808`() {
        assertTrue(isValidSSN("008-08-0808"))
    }

    @Test
    fun `section1 test35 area 012 group 12 serial 1212`() {
        assertTrue(isValidSSN("012-12-1212"))
    }

    @Test
    fun `section1 test36 area 013 group 13 serial 1313`() {
        assertTrue(isValidSSN("013-13-1313"))
    }

    @Test
    fun `section1 test37 area 014 group 14 serial 1414`() {
        assertTrue(isValidSSN("014-14-1414"))
    }

    @Test
    fun `section1 test38 batch area 001 to 030 all structurally valid`() {
        val ssns = (1..30).map { "${it.toString().padStart(3, '0')}-01-0001" }
        val invalidOnes = ssns.filter { !isValidSSN(it) }
        assertEquals("No invalid SSNs in area 001-030", 0, invalidOnes.size)
    }

    @Test
    fun `section1 test39 none of area 001-099 are ITIN`() {
        val ssns = (1..99 step 5).map { "${it.toString().padStart(3, '0')}-01-0001" }
        assertTrue("No area 001-099 should pass ITIN validation", ssns.none { isValidITIN(it) })
    }

    @Test
    fun `section1 test40 area 016 group 16 serial 1616`() {
        val ssn = "016-16-1616"
        assertTrue(isValidSSN(ssn))
        val formatted = formatSSN(ssn.replace("-", ""))
        assertEquals(ssn, formatted)
    }

    // =========================================================================
    // SECTION 2 — Valid SSNs: area range 100–199  (40 tests)
    // =========================================================================

    @Test
    fun `section2 test01 area 100 minimum three-digit area valid`() {
        val ssn = "100-01-0001"
        assertTrue(isValidSSN(ssn))
    }

    @Test
    fun `section2 test02 area 100 group 99 serial 9999 max valid`() {
        assertTrue(isValidSSN("100-99-9999"))
    }

    @Test
    fun `section2 test03 area 101 group 11 serial 1111`() {
        assertTrue(isValidSSN("101-11-1111"))
    }

    @Test
    fun `section2 test04 area 102 group 22 serial 2222`() {
        val ssn = "102-22-2222"
        assertTrue(isValidSSN(ssn))
        assertEquals("***-**-2222", maskSSN(ssn))
    }

    @Test
    fun `section2 test05 area 103 group 33 serial 3333`() {
        assertTrue(isValidSSN("103-33-3333"))
    }

    @Test
    fun `section2 test06 area 104 group 44 serial 4444`() {
        assertTrue(isValidSSN("104-44-4444"))
    }

    @Test
    fun `section2 test07 area 105 group 55 serial 5555`() {
        assertTrue(isValidSSN("105-55-5555"))
    }

    @Test
    fun `section2 test08 area 106 group 66 serial 6666`() {
        assertTrue(isValidSSN("106-66-6666"))
    }

    @Test
    fun `section2 test09 area 107 group 77 serial 7777`() {
        assertTrue(isValidSSN("107-77-7777"))
    }

    @Test
    fun `section2 test10 area 108 group 88 serial 8888`() {
        assertTrue(isValidSSN("108-88-8888"))
    }

    @Test
    fun `section2 test11 area 109 group 99 serial 9999`() {
        assertTrue(isValidSSN("109-99-9999"))
    }

    @Test
    fun `section2 test12 area 110 group 10 serial 1010`() {
        assertTrue(isValidSSN("110-10-1010"))
    }

    @Test
    fun `section2 test13 area 115 group 15 serial 1515`() {
        assertTrue(isValidSSN("115-15-1515"))
    }

    @Test
    fun `section2 test14 area 120 group 20 serial 2020`() {
        assertTrue(isValidSSN("120-20-2020"))
    }

    @Test
    fun `section2 test15 area 125 group 25 serial 2525`() {
        assertTrue(isValidSSN("125-25-2525"))
    }

    @Test
    fun `section2 test16 area 130 group 30 serial 3030`() {
        assertTrue(isValidSSN("130-30-3030"))
    }

    @Test
    fun `section2 test17 area 135 group 35 serial 3535`() {
        assertTrue(isValidSSN("135-35-3535"))
    }

    @Test
    fun `section2 test18 area 140 group 40 serial 4040`() {
        assertTrue(isValidSSN("140-40-4040"))
    }

    @Test
    fun `section2 test19 area 145 group 45 serial 4545`() {
        val ssn = "145-45-4545"
        assertTrue(isValidSSN(ssn))
    }

    @Test
    fun `section2 test20 area 150 group 50 serial 5050`() {
        assertTrue(isValidSSN("150-50-5050"))
    }

    @Test
    fun `section2 test21 area 155 group 55 serial 5555`() {
        assertTrue(isValidSSN("155-55-5555"))
    }

    @Test
    fun `section2 test22 area 160 group 60 serial 6060`() {
        assertTrue(isValidSSN("160-60-6060"))
    }

    @Test
    fun `section2 test23 area 165 group 65 serial 6565`() {
        assertTrue(isValidSSN("165-65-6565"))
    }

    @Test
    fun `section2 test24 area 170 group 70 serial 7070`() {
        assertTrue(isValidSSN("170-70-7070"))
    }

    @Test
    fun `section2 test25 area 175 group 75 serial 7575`() {
        assertTrue(isValidSSN("175-75-7575"))
    }

    @Test
    fun `section2 test26 area 180 group 80 serial 8080`() {
        assertTrue(isValidSSN("180-80-8080"))
    }

    @Test
    fun `section2 test27 area 185 group 85 serial 8585`() {
        assertTrue(isValidSSN("185-85-8585"))
    }

    @Test
    fun `section2 test28 area 190 group 90 serial 9090`() {
        assertTrue(isValidSSN("190-90-9090"))
    }

    @Test
    fun `section2 test29 area 195 group 95 serial 9595`() {
        assertTrue(isValidSSN("195-95-9595"))
    }

    @Test
    fun `section2 test30 area 199 group 99 serial 9999`() {
        assertTrue(isValidSSN("199-99-9999"))
    }

    @Test
    fun `section2 test31 area 111 all matching digit 1 valid`() {
        val ssn = "111-11-1111"
        assertTrue(isValidSSN(ssn))
        assertFalse(isSequentialFakeSSN(ssn))
    }

    @Test
    fun `section2 test32 area 122 group 22 serial 2222`() {
        assertTrue(isValidSSN("122-22-2222"))
    }

    @Test
    fun `section2 test33 area 133 group 33 serial 3333`() {
        assertTrue(isValidSSN("133-33-3333"))
    }

    @Test
    fun `section2 test34 area 144 group 44 serial 4444`() {
        assertTrue(isValidSSN("144-44-4444"))
    }

    @Test
    fun `section2 test35 area 155 group 01 serial 0001`() {
        assertTrue(isValidSSN("155-01-0001"))
    }

    @Test
    fun `section2 test36 area 166 group 16 serial 1661`() {
        assertTrue(isValidSSN("166-16-1661"))
    }

    @Test
    fun `section2 test37 area 177 group 17 serial 1771`() {
        assertTrue(isValidSSN("177-17-1771"))
    }

    @Test
    fun `section2 test38 area 188 group 18 serial 1881`() {
        assertTrue(isValidSSN("188-18-1881"))
    }

    @Test
    fun `section2 test39 batch 100-110 all valid`() {
        val batch = (100..110).map { "$it-10-1000" }
        assertEquals("All of 100-110 should be valid", 11, countValidSSNs(batch))
    }

    @Test
    fun `section2 test40 area 160 group 01 serial 0001 minimum within 1xx range`() {
        val ssn = "160-01-0001"
        assertTrue(isValidSSN(ssn))
        assertFalse("Should not be an ITIN", isValidITIN(ssn))
    }

    // =========================================================================
    // SECTION 3 — Valid SSNs: area range 200–299  (40 tests)
    // =========================================================================

    @Test
    fun `section3 test01 area 200 group 01 serial 0001`() {
        assertTrue(isValidSSN("200-01-0001"))
    }

    @Test
    fun `section3 test02 area 200 group 99 serial 9999`() {
        assertTrue(isValidSSN("200-99-9999"))
    }

    @Test
    fun `section3 test03 area 201 group 10 serial 1000`() {
        assertTrue(isValidSSN("201-10-1000"))
    }

    @Test
    fun `section3 test04 area 205 group 05 serial 0505`() {
        assertTrue(isValidSSN("205-05-0505"))
    }

    @Test
    fun `section3 test05 area 210 group 10 serial 1010`() {
        assertTrue(isValidSSN("210-10-1010"))
    }

    @Test
    fun `section3 test06 area 215 group 15 serial 1515`() {
        assertTrue(isValidSSN("215-15-1515"))
    }

    @Test
    fun `section3 test07 area 220 group 20 serial 2020`() {
        assertTrue(isValidSSN("220-20-2020"))
    }

    @Test
    fun `section3 test08 area 222 all digit 2 valid`() {
        val ssn = "222-22-2222"
        assertTrue(isValidSSN(ssn))
        assertEquals("***-**-2222", maskSSN(ssn))
    }

    @Test
    fun `section3 test09 area 225 group 25 serial 2525`() {
        assertTrue(isValidSSN("225-25-2525"))
    }

    @Test
    fun `section3 test10 area 230 group 30 serial 3030`() {
        assertTrue(isValidSSN("230-30-3030"))
    }

    @Test
    fun `section3 test11 area 235 group 35 serial 3535`() {
        assertTrue(isValidSSN("235-35-3535"))
    }

    @Test
    fun `section3 test12 area 240 group 40 serial 4040`() {
        assertTrue(isValidSSN("240-40-4040"))
    }

    @Test
    fun `section3 test13 area 245 group 45 serial 4545`() {
        assertTrue(isValidSSN("245-45-4545"))
    }

    @Test
    fun `section3 test14 area 250 group 50 serial 5050`() {
        assertTrue(isValidSSN("250-50-5050"))
    }

    @Test
    fun `section3 test15 area 255 group 55 serial 5555`() {
        assertTrue(isValidSSN("255-55-5555"))
    }

    @Test
    fun `section3 test16 area 260 group 60 serial 6060`() {
        assertTrue(isValidSSN("260-60-6060"))
    }

    @Test
    fun `section3 test17 area 265 group 65 serial 6565`() {
        assertTrue(isValidSSN("265-65-6565"))
    }

    @Test
    fun `section3 test18 area 270 group 70 serial 7070`() {
        assertTrue(isValidSSN("270-70-7070"))
    }

    @Test
    fun `section3 test19 area 275 group 75 serial 7575`() {
        assertTrue(isValidSSN("275-75-7575"))
    }

    @Test
    fun `section3 test20 area 280 group 80 serial 8080`() {
        assertTrue(isValidSSN("280-80-8080"))
    }

    @Test
    fun `section3 test21 area 285 group 85 serial 8585`() {
        assertTrue(isValidSSN("285-85-8585"))
    }

    @Test
    fun `section3 test22 area 290 group 90 serial 9090`() {
        assertTrue(isValidSSN("290-90-9090"))
    }

    @Test
    fun `section3 test23 area 295 group 95 serial 9595`() {
        assertTrue(isValidSSN("295-95-9595"))
    }

    @Test
    fun `section3 test24 area 299 group 99 serial 9999`() {
        assertTrue(isValidSSN("299-99-9999"))
    }

    @Test
    fun `section3 test25 area 211 group 21 serial 2121`() {
        assertTrue(isValidSSN("211-21-2121"))
    }

    @Test
    fun `section3 test26 area 233 group 33 serial 3333`() {
        assertTrue(isValidSSN("233-33-3333"))
    }

    @Test
    fun `section3 test27 area 244 group 44 serial 4444`() {
        assertTrue(isValidSSN("244-44-4444"))
    }

    @Test
    fun `section3 test28 area 266 group 66 serial 6661`() {
        assertTrue(isValidSSN("266-66-6661"))
    }

    @Test
    fun `section3 test29 area 277 group 77 serial 7771`() {
        assertTrue(isValidSSN("277-77-7771"))
    }

    @Test
    fun `section3 test30 area 288 group 88 serial 8881`() {
        assertTrue(isValidSSN("288-88-8881"))
    }

    @Test
    fun `section3 test31 area 202 group 02 serial 0202`() {
        assertTrue(isValidSSN("202-02-0202"))
    }

    @Test
    fun `section3 test32 area 203 group 03 serial 0303`() {
        assertTrue(isValidSSN("203-03-0303"))
    }

    @Test
    fun `section3 test33 area 204 group 04 serial 0404`() {
        assertTrue(isValidSSN("204-04-0404"))
    }

    @Test
    fun `section3 test34 area 206 group 06 serial 0606`() {
        assertTrue(isValidSSN("206-06-0606"))
    }

    @Test
    fun `section3 test35 area 207 group 07 serial 0707`() {
        assertTrue(isValidSSN("207-07-0707"))
    }

    @Test
    fun `section3 test36 area 208 group 08 serial 0808`() {
        assertTrue(isValidSSN("208-08-0808"))
    }

    @Test
    fun `section3 test37 area 209 group 09 serial 0909`() {
        assertTrue(isValidSSN("209-09-0909"))
    }

    @Test
    fun `section3 test38 batch 200 to 209 all valid`() {
        val batch = (200..209).map { "$it-20-2000" }
        assertEquals(10, countValidSSNs(batch))
    }

    @Test
    fun `section3 test39 batch 290 to 299 all valid`() {
        val batch = (290..299).map { "$it-29-2900" }
        assertEquals(10, countValidSSNs(batch))
    }

    @Test
    fun `section3 test40 area 255 group 55 serial 5551 not sequential fake`() {
        val ssn = "255-55-5551"
        assertTrue(isValidSSN(ssn))
        assertFalse(isSequentialFakeSSN(ssn))
    }

    // =========================================================================
    // SECTION 4 — Valid SSNs: area range 300–399  (40 tests)
    // =========================================================================

    @Test
    fun `section4 test01 area 300 group 01 serial 0001`() {
        assertTrue(isValidSSN("300-01-0001"))
    }

    @Test
    fun `section4 test02 area 300 group 99 serial 9999`() {
        assertTrue(isValidSSN("300-99-9999"))
    }

    @Test
    fun `section4 test03 area 301 group 11 serial 1101`() {
        assertTrue(isValidSSN("301-11-1101"))
    }

    @Test
    fun `section4 test04 area 310 group 10 serial 1010`() {
        assertTrue(isValidSSN("310-10-1010"))
    }

    @Test
    fun `section4 test05 area 320 group 20 serial 2020`() {
        assertTrue(isValidSSN("320-20-2020"))
    }

    @Test
    fun `section4 test06 area 330 group 30 serial 3030`() {
        assertTrue(isValidSSN("330-30-3030"))
    }

    @Test
    fun `section4 test07 area 333 all digit 3 valid`() {
        val ssn = "333-33-3333"
        assertTrue(isValidSSN(ssn))
        assertEquals("***-**-3333", maskSSN(ssn))
    }

    @Test
    fun `section4 test08 area 340 group 40 serial 4040`() {
        assertTrue(isValidSSN("340-40-4040"))
    }

    @Test
    fun `section4 test09 area 350 group 50 serial 5050`() {
        assertTrue(isValidSSN("350-50-5050"))
    }

    @Test
    fun `section4 test10 area 360 group 60 serial 6060`() {
        assertTrue(isValidSSN("360-60-6060"))
    }

    @Test
    fun `section4 test11 area 370 group 70 serial 7070`() {
        assertTrue(isValidSSN("370-70-7070"))
    }

    @Test
    fun `section4 test12 area 380 group 80 serial 8080`() {
        assertTrue(isValidSSN("380-80-8080"))
    }

    @Test
    fun `section4 test13 area 390 group 90 serial 9090`() {
        assertTrue(isValidSSN("390-90-9090"))
    }

    @Test
    fun `section4 test14 area 399 group 99 serial 9999`() {
        assertTrue(isValidSSN("399-99-9999"))
    }

    @Test
    fun `section4 test15 area 311 group 21 serial 2111`() {
        assertTrue(isValidSSN("311-21-2111"))
    }

    @Test
    fun `section4 test16 area 321 group 31 serial 3121`() {
        assertTrue(isValidSSN("321-31-3121"))
    }

    @Test
    fun `section4 test17 area 331 group 41 serial 4131`() {
        assertTrue(isValidSSN("331-41-4131"))
    }

    @Test
    fun `section4 test18 area 341 group 51 serial 5141`() {
        assertTrue(isValidSSN("341-51-5141"))
    }

    @Test
    fun `section4 test19 area 351 group 61 serial 6151`() {
        assertTrue(isValidSSN("351-61-6151"))
    }

    @Test
    fun `section4 test20 area 361 group 71 serial 7161`() {
        assertTrue(isValidSSN("361-71-7161"))
    }

    @Test
    fun `section4 test21 area 371 group 81 serial 8171`() {
        assertTrue(isValidSSN("371-81-8171"))
    }

    @Test
    fun `section4 test22 area 381 group 91 serial 9181`() {
        assertTrue(isValidSSN("381-91-9181"))
    }

    @Test
    fun `section4 test23 area 391 group 01 serial 0191`() {
        assertTrue(isValidSSN("391-01-0191"))
    }

    @Test
    fun `section4 test24 area 302 group 12 serial 2312`() {
        assertTrue(isValidSSN("302-12-2312"))
    }

    @Test
    fun `section4 test25 area 303 group 23 serial 3423`() {
        assertTrue(isValidSSN("303-23-3423"))
    }

    @Test
    fun `section4 test26 area 304 group 34 serial 4534`() {
        assertTrue(isValidSSN("304-34-4534"))
    }

    @Test
    fun `section4 test27 area 305 group 45 serial 5645`() {
        assertTrue(isValidSSN("305-45-5645"))
    }

    @Test
    fun `section4 test28 area 306 group 56 serial 6756`() {
        assertTrue(isValidSSN("306-56-6756"))
    }

    @Test
    fun `section4 test29 area 307 group 67 serial 7867`() {
        assertTrue(isValidSSN("307-67-7867"))
    }

    @Test
    fun `section4 test30 area 308 group 78 serial 8978`() {
        assertTrue(isValidSSN("308-78-8978"))
    }

    @Test
    fun `section4 test31 area 309 group 89 serial 9089`() {
        assertTrue(isValidSSN("309-89-9089"))
    }

    @Test
    fun `section4 test32 area 315 group 15 serial 1515`() {
        assertTrue(isValidSSN("315-15-1515"))
    }

    @Test
    fun `section4 test33 area 325 group 25 serial 2525`() {
        assertTrue(isValidSSN("325-25-2525"))
    }

    @Test
    fun `section4 test34 area 335 group 35 serial 3535`() {
        assertTrue(isValidSSN("335-35-3535"))
    }

    @Test
    fun `section4 test35 area 345 group 45 serial 4545`() {
        assertTrue(isValidSSN("345-45-4545"))
    }

    @Test
    fun `section4 test36 area 355 group 55 serial 5555`() {
        assertTrue(isValidSSN("355-55-5555"))
    }

    @Test
    fun `section4 test37 area 365 group 65 serial 6565`() {
        assertTrue(isValidSSN("365-65-6565"))
    }

    @Test
    fun `section4 test38 area 375 group 75 serial 7575`() {
        assertTrue(isValidSSN("375-75-7575"))
    }

    @Test
    fun `section4 test39 area 385 group 85 serial 8585`() {
        assertTrue(isValidSSN("385-85-8585"))
    }

    @Test
    fun `section4 test40 batch 300-309 all valid`() {
        val batch = (300..309).map { "$it-30-3000" }
        assertEquals(10, countValidSSNs(batch))
    }

    // =========================================================================
    // SECTION 5 — Valid SSNs: area range 400–499  (30 tests)
    // =========================================================================

    @Test
    fun `section5 test01 area 400 group 01 serial 0001`() {
        assertTrue(isValidSSN("400-01-0001"))
    }

    @Test
    fun `section5 test02 area 400 group 99 serial 9999`() {
        assertTrue(isValidSSN("400-99-9999"))
    }

    @Test
    fun `section5 test03 area 410 group 10 serial 1010`() {
        assertTrue(isValidSSN("410-10-1010"))
    }

    @Test
    fun `section5 test04 area 420 group 20 serial 2020`() {
        assertTrue(isValidSSN("420-20-2020"))
    }

    @Test
    fun `section5 test05 area 430 group 30 serial 3030`() {
        assertTrue(isValidSSN("430-30-3030"))
    }

    @Test
    fun `section5 test06 area 440 group 40 serial 4040`() {
        assertTrue(isValidSSN("440-40-4040"))
    }

    @Test
    fun `section5 test07 area 444 all digit 4 valid`() {
        val ssn = "444-44-4444"
        assertTrue(isValidSSN(ssn))
        assertEquals("***-**-4444", maskSSN(ssn))
    }

    @Test
    fun `section5 test08 area 450 group 50 serial 5050`() {
        assertTrue(isValidSSN("450-50-5050"))
    }

    @Test
    fun `section5 test09 area 460 group 60 serial 6060`() {
        assertTrue(isValidSSN("460-60-6060"))
    }

    @Test
    fun `section5 test10 area 470 group 70 serial 7070`() {
        assertTrue(isValidSSN("470-70-7070"))
    }

    @Test
    fun `section5 test11 area 480 group 80 serial 8080`() {
        assertTrue(isValidSSN("480-80-8080"))
    }

    @Test
    fun `section5 test12 area 490 group 90 serial 9090`() {
        assertTrue(isValidSSN("490-90-9090"))
    }

    @Test
    fun `section5 test13 area 499 group 99 serial 9999`() {
        assertTrue(isValidSSN("499-99-9999"))
    }

    @Test
    fun `section5 test14 area 401 group 10 serial 1001`() {
        assertTrue(isValidSSN("401-10-1001"))
    }

    @Test
    fun `section5 test15 area 405 group 15 serial 2536`() {
        assertTrue(isValidSSN("405-15-2536"))
    }

    @Test
    fun `section5 test16 area 415 group 25 serial 3647`() {
        assertTrue(isValidSSN("415-25-3647"))
    }

    @Test
    fun `section5 test17 area 425 group 35 serial 4758`() {
        assertTrue(isValidSSN("425-35-4758"))
    }

    @Test
    fun `section5 test18 area 435 group 45 serial 5869`() {
        assertTrue(isValidSSN("435-45-5869"))
    }

    @Test
    fun `section5 test19 area 445 group 55 serial 6970`() {
        assertTrue(isValidSSN("445-55-6970"))
    }

    @Test
    fun `section5 test20 area 455 group 65 serial 7081`() {
        assertTrue(isValidSSN("455-65-7081"))
    }

    @Test
    fun `section5 test21 area 402 group 12 serial 3456`() {
        assertTrue(isValidSSN("402-12-3456"))
    }

    @Test
    fun `section5 test22 area 403 group 23 serial 4567`() {
        assertTrue(isValidSSN("403-23-4567"))
    }

    @Test
    fun `section5 test23 area 404 group 34 serial 5678`() {
        assertTrue(isValidSSN("404-34-5678"))
    }

    @Test
    fun `section5 test24 area 406 group 45 serial 6789`() {
        assertTrue(isValidSSN("406-45-6789"))
    }

    @Test
    fun `section5 test25 area 407 group 56 serial 7890`() {
        assertTrue(isValidSSN("407-56-7890"))
    }

    @Test
    fun `section5 test26 area 408 group 67 serial 8901`() {
        assertTrue(isValidSSN("408-67-8901"))
    }

    @Test
    fun `section5 test27 area 409 group 78 serial 9012`() {
        assertTrue(isValidSSN("409-78-9012"))
    }

    @Test
    fun `section5 test28 area 411 group 89 serial 0123`() {
        assertTrue(isValidSSN("411-89-0123"))
    }

    @Test
    fun `section5 test29 batch 400 to 409 all valid`() {
        val batch = (400..409).map { "$it-40-4000" }
        assertEquals(10, countValidSSNs(batch))
    }

    @Test
    fun `section5 test30 batch 490 to 499 all valid`() {
        val batch = (490..499).map { "$it-49-4900" }
        assertEquals(10, countValidSSNs(batch))
    }

    // =========================================================================
    // SECTION 6 — Valid SSNs: area range 500–599  (30 tests)
    // =========================================================================

    @Test
    fun `section6 test01 area 500 group 01 serial 0001`() {
        assertTrue(isValidSSN("500-01-0001"))
    }

    @Test
    fun `section6 test02 area 500 group 99 serial 9999`() {
        assertTrue(isValidSSN("500-99-9999"))
    }

    @Test
    fun `section6 test03 area 501 group 11 serial 1111`() {
        assertTrue(isValidSSN("501-11-1111"))
    }

    @Test
    fun `section6 test04 area 510 group 10 serial 1010`() {
        assertTrue(isValidSSN("510-10-1010"))
    }

    @Test
    fun `section6 test05 area 520 group 20 serial 2020`() {
        assertTrue(isValidSSN("520-20-2020"))
    }

    @Test
    fun `section6 test06 area 530 group 30 serial 3030`() {
        assertTrue(isValidSSN("530-30-3030"))
    }

    @Test
    fun `section6 test07 area 540 group 40 serial 4040`() {
        assertTrue(isValidSSN("540-40-4040"))
    }

    @Test
    fun `section6 test08 area 550 group 50 serial 5050`() {
        assertTrue(isValidSSN("550-50-5050"))
    }

    @Test
    fun `section6 test09 area 555 all digit 5 valid`() {
        val ssn = "555-55-5555"
        assertTrue(isValidSSN(ssn))
        assertEquals("***-**-5555", maskSSN(ssn))
    }

    @Test
    fun `section6 test10 area 560 group 60 serial 6060`() {
        assertTrue(isValidSSN("560-60-6060"))
    }

    @Test
    fun `section6 test11 area 570 group 70 serial 7070`() {
        assertTrue(isValidSSN("570-70-7070"))
    }

    @Test
    fun `section6 test12 area 580 group 80 serial 8080`() {
        assertTrue(isValidSSN("580-80-8080"))
    }

    @Test
    fun `section6 test13 area 590 group 90 serial 9090`() {
        assertTrue(isValidSSN("590-90-9090"))
    }

    @Test
    fun `section6 test14 area 599 group 99 serial 9999`() {
        assertTrue(isValidSSN("599-99-9999"))
    }

    @Test
    fun `section6 test15 area 505 group 15 serial 1525`() {
        assertTrue(isValidSSN("505-15-1525"))
    }

    @Test
    fun `section6 test16 area 515 group 25 serial 2535`() {
        assertTrue(isValidSSN("515-25-2535"))
    }

    @Test
    fun `section6 test17 area 525 group 35 serial 3545`() {
        assertTrue(isValidSSN("525-35-3545"))
    }

    @Test
    fun `section6 test18 area 535 group 45 serial 4555`() {
        assertTrue(isValidSSN("535-45-4555"))
    }

    @Test
    fun `section6 test19 area 545 group 55 serial 5565`() {
        assertTrue(isValidSSN("545-55-5565"))
    }

    @Test
    fun `section6 test20 area 565 group 75 serial 7585`() {
        assertTrue(isValidSSN("565-75-7585"))
    }

    @Test
    fun `section6 test21 area 575 group 85 serial 8595`() {
        assertTrue(isValidSSN("575-85-8595"))
    }

    @Test
    fun `section6 test22 area 502 group 12 serial 3412`() {
        assertTrue(isValidSSN("502-12-3412"))
    }

    @Test
    fun `section6 test23 area 503 group 23 serial 4523`() {
        assertTrue(isValidSSN("503-23-4523"))
    }

    @Test
    fun `section6 test24 area 504 group 34 serial 5634`() {
        assertTrue(isValidSSN("504-34-5634"))
    }

    @Test
    fun `section6 test25 area 506 group 45 serial 6745`() {
        assertTrue(isValidSSN("506-45-6745"))
    }

    @Test
    fun `section6 test26 area 507 group 56 serial 7856`() {
        assertTrue(isValidSSN("507-56-7856"))
    }

    @Test
    fun `section6 test27 area 508 group 67 serial 8967`() {
        assertTrue(isValidSSN("508-67-8967"))
    }

    @Test
    fun `section6 test28 area 509 group 78 serial 9078`() {
        assertTrue(isValidSSN("509-78-9078"))
    }

    @Test
    fun `section6 test29 batch 500 to 509 all valid`() {
        val batch = (500..509).map { "$it-50-5000" }
        assertEquals(10, countValidSSNs(batch))
    }

    @Test
    fun `section6 test30 batch 590 to 599 all valid`() {
        val batch = (590..599).map { "$it-59-5900" }
        assertEquals(10, countValidSSNs(batch))
    }

    // =========================================================================
    // SECTION 7 — Valid SSNs: area range 600–665  (30 tests)
    // =========================================================================

    @Test
    fun `section7 test01 area 600 group 01 serial 0001`() {
        assertTrue(isValidSSN("600-01-0001"))
    }

    @Test
    fun `section7 test02 area 600 group 99 serial 9999`() {
        assertTrue(isValidSSN("600-99-9999"))
    }

    @Test
    fun `section7 test03 area 601 group 11 serial 1111`() {
        assertTrue(isValidSSN("601-11-1111"))
    }

    @Test
    fun `section7 test04 area 610 group 10 serial 1010`() {
        assertTrue(isValidSSN("610-10-1010"))
    }

    @Test
    fun `section7 test05 area 620 group 20 serial 2020`() {
        assertTrue(isValidSSN("620-20-2020"))
    }

    @Test
    fun `section7 test06 area 630 group 30 serial 3030`() {
        assertTrue(isValidSSN("630-30-3030"))
    }

    @Test
    fun `section7 test07 area 640 group 40 serial 4040`() {
        assertTrue(isValidSSN("640-40-4040"))
    }

    @Test
    fun `section7 test08 area 650 group 50 serial 5050`() {
        assertTrue(isValidSSN("650-50-5050"))
    }

    @Test
    fun `section7 test09 area 660 group 60 serial 6060`() {
        assertTrue(isValidSSN("660-60-6060"))
    }

    @Test
    fun `section7 test10 area 665 group 65 serial 6565 upper boundary`() {
        val ssn = "665-65-6565"
        assertTrue(isValidSSN(ssn))
    }

    @Test
    fun `section7 test11 area 665 group 01 serial 0001 upper boundary minimum values`() {
        assertTrue(isValidSSN("665-01-0001"))
    }

    @Test
    fun `section7 test12 area 602 group 22 serial 2222`() {
        assertTrue(isValidSSN("602-22-2222"))
    }

    @Test
    fun `section7 test13 area 603 group 33 serial 3333`() {
        assertTrue(isValidSSN("603-33-3333"))
    }

    @Test
    fun `section7 test14 area 604 group 44 serial 4444`() {
        assertTrue(isValidSSN("604-44-4444"))
    }

    @Test
    fun `section7 test15 area 605 group 55 serial 5555`() {
        assertTrue(isValidSSN("605-55-5555"))
    }

    @Test
    fun `section7 test16 area 606 group 66 serial 6666`() {
        assertTrue(isValidSSN("606-66-6666"))
    }

    @Test
    fun `section7 test17 area 607 group 77 serial 7777`() {
        assertTrue(isValidSSN("607-77-7777"))
    }

    @Test
    fun `section7 test18 area 608 group 88 serial 8888`() {
        assertTrue(isValidSSN("608-88-8888"))
    }

    @Test
    fun `section7 test19 area 609 group 99 serial 9999`() {
        assertTrue(isValidSSN("609-99-9999"))
    }

    @Test
    fun `section7 test20 area 611 group 01 serial 0101`() {
        assertTrue(isValidSSN("611-01-0101"))
    }

    @Test
    fun `section7 test21 area 612 group 02 serial 0202`() {
        assertTrue(isValidSSN("612-02-0202"))
    }

    @Test
    fun `section7 test22 area 613 group 03 serial 0303`() {
        assertTrue(isValidSSN("613-03-0303"))
    }

    @Test
    fun `section7 test23 area 614 group 04 serial 0404`() {
        assertTrue(isValidSSN("614-04-0404"))
    }

    @Test
    fun `section7 test24 area 615 group 05 serial 0505`() {
        assertTrue(isValidSSN("615-05-0505"))
    }

    @Test
    fun `section7 test25 area 616 group 06 serial 0606`() {
        assertTrue(isValidSSN("616-06-0606"))
    }

    @Test
    fun `section7 test26 area 617 group 07 serial 0707`() {
        assertTrue(isValidSSN("617-07-0707"))
    }

    @Test
    fun `section7 test27 area 618 group 08 serial 0808`() {
        assertTrue(isValidSSN("618-08-0808"))
    }

    @Test
    fun `section7 test28 area 619 group 09 serial 0909`() {
        assertTrue(isValidSSN("619-09-0909"))
    }

    @Test
    fun `section7 test29 batch 600 to 609 all valid`() {
        val batch = (600..609).map { "$it-60-6000" }
        assertEquals(10, countValidSSNs(batch))
    }

    @Test
    fun `section7 test30 batch 655 to 665 all valid`() {
        val batch = (655..665).map { "$it-60-6000" }
        assertEquals(11, countValidSSNs(batch))
    }

    // =========================================================================
    // SECTION 8 — ITIN validation area codes 900–999  (20+ tests)
    // =========================================================================

    @Test
    fun `section8 test01 valid itin 900-70-0001`() {
        assertTrue(isValidITIN("900-70-0001"))
        assertFalse("Should not also be a valid SSN", isValidSSN("900-70-0001"))
    }

    @Test
    fun `section8 test02 valid itin 901-71-1111`() {
        assertTrue(isValidITIN("901-71-1111"))
        assertFalse(isValidSSN("901-71-1111"))
    }

    @Test
    fun `section8 test03 valid itin 910-72-2222`() {
        assertTrue(isValidITIN("910-72-2222"))
        assertFalse(isValidSSN("910-72-2222"))
    }

    @Test
    fun `section8 test04 valid itin 920-73-3333`() {
        assertTrue(isValidITIN("920-73-3333"))
    }

    @Test
    fun `section8 test05 valid itin 930-74-4444`() {
        assertTrue(isValidITIN("930-74-4444"))
    }

    @Test
    fun `section8 test06 valid itin 940-75-5555`() {
        assertTrue(isValidITIN("940-75-5555"))
    }

    @Test
    fun `section8 test07 valid itin 950-76-6666`() {
        assertTrue(isValidITIN("950-76-6666"))
    }

    @Test
    fun `section8 test08 valid itin 960-77-7777`() {
        assertTrue(isValidITIN("960-77-7777"))
    }

    @Test
    fun `section8 test09 valid itin 970-78-8888`() {
        assertTrue(isValidITIN("970-78-8888"))
    }

    @Test
    fun `section8 test10 valid itin 980-79-9999`() {
        assertTrue(isValidITIN("980-79-9999"))
    }

    @Test
    fun `section8 test11 valid itin 990-80-0001`() {
        assertTrue(isValidITIN("990-80-0001"))
    }

    @Test
    fun `section8 test12 valid itin 999-88-8888`() {
        assertTrue(isValidITIN("999-88-8888"))
    }

    @Test
    fun `section8 test13 batch 900 to 909 all valid as ITIN`() {
        val batch = (900..909).map { "$it-70-7000" }
        assertEquals(10, batch.count { isValidITIN(it) })
    }

    @Test
    fun `section8 test14 batch 900 to 909 none valid as SSN`() {
        val batch = (900..909).map { "$it-70-7000" }
        assertEquals(0, countValidSSNs(batch))
    }

    @Test
    fun `section8 test15 valid itin 905-70-1234`() { assertTrue(isValidITIN("905-70-1234")) }

    @Test
    fun `section8 test16 valid itin 915-80-2345`() { assertTrue(isValidITIN("915-80-2345")) }

    @Test
    fun `section8 test17 valid itin 925-90-3456`() { assertTrue(isValidITIN("925-90-3456")) }

    @Test
    fun `section8 test18 valid itin 935-70-4567`() { assertTrue(isValidITIN("935-70-4567")) }

    @Test
    fun `section8 test19 valid itin 945-80-5678`() { assertTrue(isValidITIN("945-80-5678")) }

    @Test
    fun `section8 test20 itin and ssn are mutually exclusive for 9xx numbers`() {
        val itins = listOf("900-70-1234", "920-73-3333", "950-76-6666", "999-88-8888")
        assertTrue("All 9xx should be valid ITINs", itins.all { isValidITIN(it) })
        assertTrue("None of 9xx should be valid SSNs", itins.none { isValidSSN(it) })
    }

    // =========================================================================
    // SECTION 9 — ITIN edge cases and invalid ITINs
    // =========================================================================

    @Test
    fun `section9 test01 invalid itin group 00`() {
        assertFalse(isValidITIN("900-00-1234"))
    }

    @Test
    fun `section9 test02 invalid itin serial 0000`() {
        assertFalse(isValidITIN("900-70-0000"))
    }

    @Test
    fun `section9 test03 invalid itin area 899 too low`() {
        assertFalse(isValidITIN("899-70-1234"))
    }

    @Test
    fun `section9 test04 invalid itin area 000`() {
        assertFalse(isValidITIN("000-70-1234"))
    }

    @Test
    fun `section9 test05 invalid itin letters in area`() {
        assertFalse(isValidITIN("90A-70-1234"))
    }

    @Test
    fun `section9 test06 invalid itin too short 8 digits`() {
        assertFalse(isValidITIN("900-70-123"))
    }

    @Test
    fun `section9 test07 invalid itin too long 10 digits`() {
        assertFalse(isValidITIN("900-70-12345"))
    }

    @Test
    fun `section9 test08 invalid itin empty string`() {
        assertFalse(isValidITIN(""))
    }

    @Test
    fun `section9 test09 valid itin 900-50-1234`() {
        assertTrue(isValidITIN("900-50-1234"))
    }

    @Test
    fun `section9 test10 valid itin 910-60-4321`() {
        assertTrue(isValidITIN("910-60-4321"))
    }

    @Test
    fun `section9 test11 invalid itin area 100 too low`() {
        assertFalse(isValidITIN("100-70-1234"))
    }

    @Test
    fun `section9 test12 invalid itin area 500 too low`() {
        assertFalse(isValidITIN("500-70-1234"))
    }

    @Test
    fun `section9 test13 valid itin 920-71-0001`() {
        assertTrue(isValidITIN("920-71-0001"))
    }

    @Test
    fun `section9 test14 valid itin 930-72-0002`() {
        assertTrue(isValidITIN("930-72-0002"))
    }

    @Test
    fun `section9 test15 valid itin 940-73-0003`() {
        assertTrue(isValidITIN("940-73-0003"))
    }

    @Test
    fun `section9 test16 valid itin 950-74-0004`() {
        assertTrue(isValidITIN("950-74-0004"))
    }

    @Test
    fun `section9 test17 valid itin 960-75-0005`() {
        assertTrue(isValidITIN("960-75-0005"))
    }

    @Test
    fun `section9 test18 invalid itin spaces no dashes`() {
        assertFalse(isValidITIN("900 70 1234"))
    }

    @Test
    fun `section9 test19 valid itin 970-76-0006`() {
        assertTrue(isValidITIN("970-76-0006"))
    }

    @Test
    fun `section9 test20 valid itin 980-77-0007`() {
        assertTrue(isValidITIN("980-77-0007"))
    }

    @Test
    fun `section9 test21 itin mutually exclusive with ssn 950-80-0001`() {
        val num = "950-80-0001"
        assertTrue(isValidITIN(num))
        assertFalse(isValidSSN(num))
    }

    @Test
    fun `section9 test22 invalid itin group 99 serial 0000`() {
        assertFalse(isValidITIN("900-99-0000"))
    }

    // =========================================================================
    // SECTION 10 — Invalid area 000  (15 tests)
    // =========================================================================

    @Test
    fun `section10 test01 area 000 group 01 serial 0001 with dashes`() {
        assertFalse("Area 000 is always invalid", isValidSSN("000-01-0001"))
    }

    @Test
    fun `section10 test02 area 000 no dashes`() {
        assertFalse(isValidSSN("000010001"))
    }

    @Test
    fun `section10 test03 area 000 group 99 serial 9999 max values still invalid`() {
        assertFalse(isValidSSN("000-99-9999"))
    }

    @Test
    fun `section10 test04 area 000 group 50 serial 5000`() {
        assertFalse(isValidSSN("000-50-5000"))
    }

    @Test
    fun `section10 test05 area 000 group 10 serial 1000`() {
        assertFalse(isValidSSN("000-10-1000"))
    }

    @Test
    fun `section10 test06 area 000 group 20 serial 2000`() {
        assertFalse(isValidSSN("000-20-2000"))
    }

    @Test
    fun `section10 test07 area 000 group 30 serial 3000`() {
        assertFalse(isValidSSN("000-30-3000"))
    }

    @Test
    fun `section10 test08 area 000 group 40 serial 4000`() {
        assertFalse(isValidSSN("000-40-4000"))
    }

    @Test
    fun `section10 test09 area 000 group 60 serial 6000`() {
        assertFalse(isValidSSN("000-60-6000"))
    }

    @Test
    fun `section10 test10 area 000 group 70 serial 7000`() {
        assertFalse(isValidSSN("000-70-7000"))
    }

    @Test
    fun `section10 test11 area 000 group 80 serial 8000`() {
        assertFalse(isValidSSN("000-80-8000"))
    }

    @Test
    fun `section10 test12 area 000 group 90 serial 9000`() {
        assertFalse(isValidSSN("000-90-9000"))
    }

    @Test
    fun `section10 test13 area 000 group 05 serial 0500`() {
        assertFalse(isValidSSN("000-05-0500"))
    }

    @Test
    fun `section10 test14 contrast area 001 is valid but area 000 is not`() {
        assertTrue(isValidSSN("001-01-0001"))
        assertFalse(isValidSSN("000-01-0001"))
    }

    @Test
    fun `section10 test15 batch all area 000 combos fail`() {
        val ssns = listOf(
            "000-01-0001", "000-10-1000", "000-25-2500",
            "000-50-5050", "000-75-7575", "000-99-9999"
        )
        assertEquals("All area 000 SSNs should be invalid", 0, countValidSSNs(ssns))
    }

    // =========================================================================
    // SECTION 11 — Invalid area 666  (13 tests)
    // =========================================================================

    @Test
    fun `section11 test01 area 666 group 01 serial 0001`() {
        assertFalse(isValidSSN("666-01-0001"))
    }

    @Test
    fun `section11 test02 area 666 group 99 serial 9999`() {
        assertFalse(isValidSSN("666-99-9999"))
    }

    @Test
    fun `section11 test03 area 666 group 50 serial 5050`() {
        assertFalse(isValidSSN("666-50-5050"))
    }

    @Test
    fun `section11 test04 area 666 group 10 serial 1234`() {
        assertFalse(isValidSSN("666-10-1234"))
    }

    @Test
    fun `section11 test05 area 666 group 20 serial 2345`() {
        assertFalse(isValidSSN("666-20-2345"))
    }

    @Test
    fun `section11 test06 area 666 group 30 serial 3456`() {
        assertFalse(isValidSSN("666-30-3456"))
    }

    @Test
    fun `section11 test07 area 666 group 40 serial 4567`() {
        assertFalse(isValidSSN("666-40-4567"))
    }

    @Test
    fun `section11 test08 area 666 group 60 serial 6789`() {
        assertFalse(isValidSSN("666-60-6789"))
    }

    @Test
    fun `section11 test09 area 666 group 70 serial 7890`() {
        assertFalse(isValidSSN("666-70-7890"))
    }

    @Test
    fun `section11 test10 area 666 no dashes`() {
        assertFalse(isValidSSN("666010001"))
    }

    @Test
    fun `section11 test11 contrast area 665 valid but 666 invalid`() {
        assertTrue(isValidSSN("665-01-0001"))
        assertFalse(isValidSSN("666-01-0001"))
    }

    @Test
    fun `section11 test12 contrast area 667 valid but 666 invalid`() {
        assertTrue(isValidSSN("667-01-0001"))
        assertFalse(isValidSSN("666-01-0001"))
    }

    @Test
    fun `section11 test13 batch all area 666 combos fail`() {
        val ssns = listOf(
            "666-01-0001", "666-25-2500", "666-50-5000",
            "666-75-7500", "666-99-9999"
        )
        assertEquals(0, countValidSSNs(ssns))
    }

    // =========================================================================
    // SECTION 12 — Invalid areas 900–999 as regular SSNs  (17 tests)
    // =========================================================================

    @Test
    fun `section12 test01 area 900 invalid as ssn`() { assertFalse(isValidSSN("900-01-0001")) }

    @Test
    fun `section12 test02 area 910 invalid as ssn`() { assertFalse(isValidSSN("910-10-1000")) }

    @Test
    fun `section12 test03 area 920 invalid as ssn`() { assertFalse(isValidSSN("920-20-2000")) }

    @Test
    fun `section12 test04 area 930 invalid as ssn`() { assertFalse(isValidSSN("930-30-3000")) }

    @Test
    fun `section12 test05 area 940 invalid as ssn`() { assertFalse(isValidSSN("940-40-4000")) }

    @Test
    fun `section12 test06 area 950 invalid as ssn`() { assertFalse(isValidSSN("950-50-5000")) }

    @Test
    fun `section12 test07 area 960 invalid as ssn`() { assertFalse(isValidSSN("960-60-6000")) }

    @Test
    fun `section12 test08 area 970 invalid as ssn`() { assertFalse(isValidSSN("970-70-7000")) }

    @Test
    fun `section12 test09 area 980 invalid as ssn`() { assertFalse(isValidSSN("980-80-8000")) }

    @Test
    fun `section12 test10 area 990 invalid as ssn`() { assertFalse(isValidSSN("990-90-9000")) }

    @Test
    fun `section12 test11 area 999 invalid as ssn`() { assertFalse(isValidSSN("999-99-9999")) }

    @Test
    fun `section12 test12 area 901 invalid as ssn`() { assertFalse(isValidSSN("901-01-0101")) }

    @Test
    fun `section12 test13 area 911 invalid as ssn`() { assertFalse(isValidSSN("911-11-1111")) }

    @Test
    fun `section12 test14 area 921 invalid as ssn`() { assertFalse(isValidSSN("921-21-2121")) }

    @Test
    fun `section12 test15 area 931 invalid as ssn`() { assertFalse(isValidSSN("931-31-3131")) }

    @Test
    fun `section12 test16 batch all 9xx fail as ssn`() {
        val ssns = (900..999 step 10).map { "$it-70-7000" }
        assertEquals(0, countValidSSNs(ssns))
    }

    @Test
    fun `section12 test17 area 899 valid but 900 not as ssn`() {
        assertTrue(isValidSSN("899-89-0001"))
        assertFalse(isValidSSN("900-90-0001"))
    }

    // =========================================================================
    // SECTION 13 — Invalid group 00  (17 tests)
    // =========================================================================

    @Test
    fun `section13 test01 invalid group 00 area 100`() { assertFalse(isValidSSN("100-00-1234")) }

    @Test
    fun `section13 test02 invalid group 00 area 200`() { assertFalse(isValidSSN("200-00-2345")) }

    @Test
    fun `section13 test03 invalid group 00 area 300`() { assertFalse(isValidSSN("300-00-3456")) }

    @Test
    fun `section13 test04 invalid group 00 area 400`() { assertFalse(isValidSSN("400-00-4567")) }

    @Test
    fun `section13 test05 invalid group 00 area 500`() { assertFalse(isValidSSN("500-00-5678")) }

    @Test
    fun `section13 test06 invalid group 00 area 600`() { assertFalse(isValidSSN("600-00-6789")) }

    @Test
    fun `section13 test07 invalid group 00 area 001`() { assertFalse(isValidSSN("001-00-0001")) }

    @Test
    fun `section13 test08 invalid group 00 area 010`() { assertFalse(isValidSSN("010-00-1000")) }

    @Test
    fun `section13 test09 invalid group 00 area 050`() { assertFalse(isValidSSN("050-00-5000")) }

    @Test
    fun `section13 test10 invalid group 00 area 099`() { assertFalse(isValidSSN("099-00-9900")) }

    @Test
    fun `section13 test11 invalid group 00 area 150`() { assertFalse(isValidSSN("150-00-1500")) }

    @Test
    fun `section13 test12 invalid group 00 area 250`() { assertFalse(isValidSSN("250-00-2500")) }

    @Test
    fun `section13 test13 invalid group 00 area 350`() { assertFalse(isValidSSN("350-00-3500")) }

    @Test
    fun `section13 test14 invalid group 00 area 450`() { assertFalse(isValidSSN("450-00-4500")) }

    @Test
    fun `section13 test15 invalid group 00 area 550`() { assertFalse(isValidSSN("550-00-5500")) }

    @Test
    fun `section13 test16 group 01 valid contrast to group 00`() {
        assertTrue(isValidSSN("100-01-1234"))
        assertFalse(isValidSSN("100-00-1234"))
    }

    @Test
    fun `section13 test17 batch group 00 all fail`() {
        val ssns = listOf(100, 200, 300, 400, 500, 600).map { "$it-00-1000" }
        assertEquals(0, countValidSSNs(ssns))
    }

    // =========================================================================
    // SECTION 14 — Invalid serial 0000  (17 tests)
    // =========================================================================

    @Test
    fun `section14 test01 invalid serial 0000 area 100 group 01`() {
        assertFalse(isValidSSN("100-01-0000"))
    }

    @Test
    fun `section14 test02 invalid serial 0000 area 200 group 10`() {
        assertFalse(isValidSSN("200-10-0000"))
    }

    @Test
    fun `section14 test03 invalid serial 0000 area 300 group 20`() {
        assertFalse(isValidSSN("300-20-0000"))
    }

    @Test
    fun `section14 test04 invalid serial 0000 area 400 group 30`() {
        assertFalse(isValidSSN("400-30-0000"))
    }

    @Test
    fun `section14 test05 invalid serial 0000 area 500 group 40`() {
        assertFalse(isValidSSN("500-40-0000"))
    }

    @Test
    fun `section14 test06 invalid serial 0000 area 600 group 50`() {
        assertFalse(isValidSSN("600-50-0000"))
    }

    @Test
    fun `section14 test07 invalid serial 0000 area 001 group 01`() {
        assertFalse(isValidSSN("001-01-0000"))
    }

    @Test
    fun `section14 test08 invalid serial 0000 area 010 group 10`() {
        assertFalse(isValidSSN("010-10-0000"))
    }

    @Test
    fun `section14 test09 invalid serial 0000 area 050 group 50`() {
        assertFalse(isValidSSN("050-50-0000"))
    }

    @Test
    fun `section14 test10 invalid serial 0000 area 099 group 99`() {
        assertFalse(isValidSSN("099-99-0000"))
    }

    @Test
    fun `section14 test11 invalid serial 0000 area 150 group 15`() {
        assertFalse(isValidSSN("150-15-0000"))
    }

    @Test
    fun `section14 test12 invalid serial 0000 area 250 group 25`() {
        assertFalse(isValidSSN("250-25-0000"))
    }

    @Test
    fun `section14 test13 invalid serial 0000 area 350 group 35`() {
        assertFalse(isValidSSN("350-35-0000"))
    }

    @Test
    fun `section14 test14 invalid serial 0000 area 450 group 45`() {
        assertFalse(isValidSSN("450-45-0000"))
    }

    @Test
    fun `section14 test15 invalid serial 0000 area 550 group 55`() {
        assertFalse(isValidSSN("550-55-0000"))
    }

    @Test
    fun `section14 test16 serial 0001 valid contrast to serial 0000`() {
        assertTrue(isValidSSN("100-01-0001"))
        assertFalse(isValidSSN("100-01-0000"))
    }

    @Test
    fun `section14 test17 batch all serial 0000 combos fail`() {
        val ssns = listOf(100, 200, 300, 400, 500, 600).map { "$it-10-0000" }
        assertEquals(0, countValidSSNs(ssns))
    }

    // =========================================================================
    // SECTION 15 — Format variants: no dashes  (20 tests)
    // =========================================================================

    @Test
    fun `section15 test01 no dashes valid 001010001`() {
        assertTrue(isValidSSN("001010001"))
    }

    @Test
    fun `section15 test02 no dashes valid 100101000`() {
        assertTrue(isValidSSN("100101000"))
    }

    @Test
    fun `section15 test03 no dashes valid 200202002`() {
        assertTrue(isValidSSN("200202002"))
    }

    @Test
    fun `section15 test04 no dashes valid 300303003`() {
        assertTrue(isValidSSN("300303003"))
    }

    @Test
    fun `section15 test05 no dashes valid 400404004`() {
        assertTrue(isValidSSN("400404004"))
    }

    @Test
    fun `section15 test06 no dashes valid 500505005`() {
        assertTrue(isValidSSN("500505005"))
    }

    @Test
    fun `section15 test07 no dashes valid 600606006`() {
        assertTrue(isValidSSN("600606006"))
    }

    @Test
    fun `section15 test08 no dashes invalid area 000`() {
        assertFalse(isValidSSN("000010001"))
    }

    @Test
    fun `section15 test09 no dashes invalid area 666`() {
        assertFalse(isValidSSN("666010001"))
    }

    @Test
    fun `section15 test10 no dashes invalid area 900`() {
        assertFalse(isValidSSN("900010001"))
    }

    @Test
    fun `section15 test11 no dashes invalid group 00`() {
        assertFalse(isValidSSN("100000001"))
    }

    @Test
    fun `section15 test12 no dashes invalid serial 0000`() {
        assertFalse(isValidSSN("100010000"))
    }

    @Test
    fun `section15 test13 eight digits invalid`() {
        assertFalse(isValidSSN("10101000"))
    }

    @Test
    fun `section15 test14 ten digits invalid`() {
        assertFalse(isValidSSN("1001010001"))
    }

    @Test
    fun `section15 test15 all zeros invalid`() {
        assertFalse(isValidSSN("000000000"))
    }

    @Test
    fun `section15 test16 formatSSN wraps 9 raw digits correctly`() {
        assertEquals("123-45-6789", formatSSN("123456789"))
    }

    @Test
    fun `section15 test17 formatSSN with dashes already present`() {
        assertEquals("123-45-6789", formatSSN("123-45-6789"))
    }

    @Test
    fun `section15 test18 formatSSN returns original if wrong length`() {
        assertEquals("12345", formatSSN("12345"))
    }

    @Test
    fun `section15 test19 formatSSN round-trip 001-01-0001`() {
        val original = "001-01-0001"
        val digits = original.replace("-", "")
        assertEquals(original, formatSSN(digits))
    }

    @Test
    fun `section15 test20 no dashes valid 665656565`() {
        assertTrue(isValidSSN("665656565"))
    }

    // =========================================================================
    // SECTION 16 — Format variants: spaces  (20 tests)
    // =========================================================================

    @Test
    fun `section16 test01 spaces valid 001 01 0001`() {
        assertTrue(isValidSSN("001 01 0001"))
    }

    @Test
    fun `section16 test02 spaces valid 100 10 1000`() {
        assertTrue(isValidSSN("100 10 1000"))
    }

    @Test
    fun `section16 test03 spaces valid 200 20 2000`() {
        assertTrue(isValidSSN("200 20 2000"))
    }

    @Test
    fun `section16 test04 spaces valid 300 30 3000`() {
        assertTrue(isValidSSN("300 30 3000"))
    }

    @Test
    fun `section16 test05 spaces valid 400 40 4000`() {
        assertTrue(isValidSSN("400 40 4000"))
    }

    @Test
    fun `section16 test06 spaces valid 500 50 5000`() {
        assertTrue(isValidSSN("500 50 5000"))
    }

    @Test
    fun `section16 test07 spaces valid 600 60 6001`() {
        assertTrue(isValidSSN("600 60 6001"))
    }

    @Test
    fun `section16 test08 spaces invalid area 000`() {
        assertFalse(isValidSSN("000 01 0001"))
    }

    @Test
    fun `section16 test09 spaces invalid area 666`() {
        assertFalse(isValidSSN("666 01 0001"))
    }

    @Test
    fun `section16 test10 spaces invalid group 00`() {
        assertFalse(isValidSSN("100 00 1000"))
    }

    @Test
    fun `section16 test11 spaces invalid serial 0000`() {
        assertFalse(isValidSSN("100 01 0000"))
    }

    @Test
    fun `section16 test12 spaces valid 150 15 1501`() {
        assertTrue(isValidSSN("150 15 1501"))
    }

    @Test
    fun `section16 test13 spaces invalid area 900`() {
        assertFalse(isValidSSN("900 70 1234"))
    }

    @Test
    fun `section16 test14 spaces valid 665 65 6501`() {
        assertTrue(isValidSSN("665 65 6501"))
    }

    @Test
    fun `section16 test15 spaces valid area 123 structurally`() {
        assertTrue(isValidSSN("123 45 6789"))
    }

    @Test
    fun `section16 test16 spaces valid 250 25 2525`() {
        assertTrue(isValidSSN("250 25 2525"))
    }

    @Test
    fun `section16 test17 spaces valid 350 35 3535`() {
        assertTrue(isValidSSN("350 35 3535"))
    }

    @Test
    fun `section16 test18 spaces valid 450 45 4545`() {
        assertTrue(isValidSSN("450 45 4545"))
    }

    @Test
    fun `section16 test19 spaces valid 550 55 5551`() {
        assertTrue(isValidSSN("550 55 5551"))
    }

    @Test
    fun `section16 test20 spaces valid 650 65 6501`() {
        assertTrue(isValidSSN("650 65 6501"))
    }

    // =========================================================================
    // SECTION 17 — Partial input and incorrect lengths  (20 tests)
    // =========================================================================

    @Test
    fun `section17 test01 empty string invalid`() {
        assertFalse(isValidSSN(""))
    }

    @Test
    fun `section17 test02 one digit invalid`() {
        assertFalse(isValidSSN("1"))
    }

    @Test
    fun `section17 test03 two digits invalid`() {
        assertFalse(isValidSSN("12"))
    }

    @Test
    fun `section17 test04 three digits invalid`() {
        assertFalse(isValidSSN("123"))
    }

    @Test
    fun `section17 test05 four digits invalid`() {
        assertFalse(isValidSSN("1234"))
    }

    @Test
    fun `section17 test06 five digits invalid`() {
        assertFalse(isValidSSN("12345"))
    }

    @Test
    fun `section17 test07 six digits invalid`() {
        assertFalse(isValidSSN("123456"))
    }

    @Test
    fun `section17 test08 seven digits invalid`() {
        assertFalse(isValidSSN("1234567"))
    }

    @Test
    fun `section17 test09 eight digits invalid`() {
        assertFalse(isValidSSN("12345678"))
    }

    @Test
    fun `section17 test10 ten digits invalid`() {
        assertFalse(isValidSSN("1234567890"))
    }

    @Test
    fun `section17 test11 eleven digits invalid`() {
        assertFalse(isValidSSN("12345678901"))
    }

    @Test
    fun `section17 test12 just dashes invalid`() {
        assertFalse(isValidSSN("---"))
    }

    @Test
    fun `section17 test13 letters mixed invalid`() {
        assertFalse(isValidSSN("ABC-DE-FGHI"))
    }

    @Test
    fun `section17 test14 ssn with letter at end invalid`() {
        assertFalse(isValidSSN("123-45-678A"))
    }

    @Test
    fun `section17 test15 partial dashes 123-45 invalid`() {
        assertFalse(isValidSSN("123-45"))
    }

    @Test
    fun `section17 test16 whitespace only invalid`() {
        assertFalse(isValidSSN("   "))
    }

    @Test
    fun `section17 test17 slash separated invalid`() {
        assertFalse(isValidSSN("123/45/6789"))
    }

    @Test
    fun `section17 test18 dot separated invalid`() {
        assertFalse(isValidSSN("123.45.6789"))
    }

    @Test
    fun `section17 test19 mixed dash and space yields valid extraction`() {
        assertTrue(isValidSSN("001-01 0001"))
    }

    @Test
    fun `section17 test20 null char in string invalid`() {
        assertFalse(isValidSSN("123\u000045-6789"))
    }

    // =========================================================================
    // SECTION 18 — Sequential and known fake SSNs  (18 tests)
    // =========================================================================

    @Test
    fun `section18 test01 123-45-6789 is known sequential fake`() {
        assertTrue(isSequentialFakeSSN("123-45-6789"))
    }

    @Test
    fun `section18 test02 219-09-9999 is known sequential fake`() {
        assertTrue(isSequentialFakeSSN("219-09-9999"))
    }

    @Test
    fun `section18 test03 078-05-1120 is known sequential fake`() {
        assertTrue(isSequentialFakeSSN("078-05-1120"))
    }

    @Test
    fun `section18 test04 457-55-5462 is known sequential fake`() {
        assertTrue(isSequentialFakeSSN("457-55-5462"))
    }

    @Test
    fun `section18 test05 001-01-0001 is not sequential fake`() {
        assertFalse(isSequentialFakeSSN("001-01-0001"))
    }

    @Test
    fun `section18 test06 200-20-2020 is not sequential fake`() {
        assertFalse(isSequentialFakeSSN("200-20-2020"))
    }

    @Test
    fun `section18 test07 123456789 no dashes is sequential fake`() {
        assertTrue(isSequentialFakeSSN("123456789"))
    }

    @Test
    fun `section18 test08 123-45-6789 is structurally valid despite being fake`() {
        assertTrue(isValidSSN("123-45-6789"))
        assertTrue(isSequentialFakeSSN("123-45-6789"))
    }

    @Test
    fun `section18 test09 219-09-9999 is structurally valid despite being fake`() {
        assertTrue(isValidSSN("219-09-9999"))
        assertTrue(isSequentialFakeSSN("219-09-9999"))
    }

    @Test
    fun `section18 test10 sequential fake count all 4 detected`() {
        val fakes = listOf("123-45-6789", "219-09-9999", "078-05-1120", "457-55-5462")
        assertEquals(4, fakes.count { isSequentialFakeSSN(it) })
    }

    @Test
    fun `section18 test11 bulk check only 2 of 4 are fakes`() {
        val candidates = listOf("001-01-0001", "123-45-6789", "400-40-4040", "219-09-9999")
        assertEquals(2, candidates.count { isSequentialFakeSSN(it) })
    }

    @Test
    fun `section18 test12 111-11-1111 structurally valid not sequential fake`() {
        assertTrue(isValidSSN("111-11-1111"))
        assertFalse(isSequentialFakeSSN("111-11-1111"))
    }

    @Test
    fun `section18 test13 222-22-2222 structurally valid not sequential fake`() {
        assertTrue(isValidSSN("222-22-2222"))
        assertFalse(isSequentialFakeSSN("222-22-2222"))
    }

    @Test
    fun `section18 test14 333-33-3333 structurally valid not sequential fake`() {
        assertTrue(isValidSSN("333-33-3333"))
        assertFalse(isSequentialFakeSSN("333-33-3333"))
    }

    @Test
    fun `section18 test15 444-44-4444 structurally valid not sequential fake`() {
        assertTrue(isValidSSN("444-44-4444"))
        assertFalse(isSequentialFakeSSN("444-44-4444"))
    }

    @Test
    fun `section18 test16 555-55-5555 structurally valid not sequential fake`() {
        assertTrue(isValidSSN("555-55-5555"))
        assertFalse(isSequentialFakeSSN("555-55-5555"))
    }

    @Test
    fun `section18 test17 078051120 no dashes is known fake`() {
        assertTrue(isSequentialFakeSSN("078051120"))
    }

    @Test
    fun `section18 test18 001010001 no dashes not a fake`() {
        assertFalse(isSequentialFakeSSN("001010001"))
    }

    // =========================================================================
    // SECTION 19 — Extract SSNs from text  (30 tests)
    // =========================================================================

    @Test
    fun `section19 test01 extract single ssn from simple sentence`() {
        val text = "Patient SSN is 123-45-6789 as per records."
        val result = extractSSNsFromText(text)
        assertEquals("Should find exactly one SSN", 1, result.size)
        assertEquals("123-45-6789", result[0])
    }

    @Test
    fun `section19 test02 extract two ssns from text`() {
        val text = "Primary 234-56-7890 and secondary 345-67-8901."
        val result = extractSSNsFromText(text)
        assertEquals(2, result.size)
    }

    @Test
    fun `section19 test03 extract zero when none present`() {
        val text = "No social security numbers here at all."
        assertEquals(0, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test04 extract from medical record format`() {
        val text = "Name: John Doe, SSN: 456-78-9012, DOB: 01/01/1990"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("456-78-9012", result[0])
    }

    @Test
    fun `section19 test05 extract from hr document`() {
        val text = "Employee ID: 78901, SSN: 567-89-0123, Hire Date: 2020-01-15"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("567-89-0123", result[0])
    }

    @Test
    fun `section19 test06 no extract without dashes`() {
        val text = "SSN without dashes: 123456789 should not match"
        assertEquals(0, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test07 extract at start of text`() {
        val text = "123-45-6789 is the SSN on file."
        assertEquals(1, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test08 extract at end of text`() {
        val text = "The SSN is 234-56-7890"
        assertEquals(1, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test09 extract three ssns from same sentence`() {
        val text = "SSN1: 100-10-1000 and SSN2: 200-20-2000 and SSN3: 300-30-3000"
        assertEquals(3, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test10 no extract partial pattern 123-45-678`() {
        val text = "Partial: 123-45-678 should not match"
        assertEquals(0, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test11 no extract 1234-56-7890 extra area digits`() {
        val text = "Too long: 1234-56-7890 should not match"
        assertEquals(0, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test12 extract preserves original dash format`() {
        val text = "SSN: 456-78-9012"
        val result = extractSSNsFromText(text)
        assertEquals("456-78-9012", result[0])
    }

    @Test
    fun `section19 test13 extract from multiline text`() {
        val text = "Line 1: 111-22-3333\nLine 2: no ssn here\nLine 3: 444-55-6666"
        assertEquals(2, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test14 extract from JSON-like string`() {
        val text = """{"ssn": "789-01-2345", "name": "Jane"}"""
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("789-01-2345", result[0])
    }

    @Test
    fun `section19 test15 extract from CSV row`() {
        val text = "John,Doe,890-12-3456,30,Engineer"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("890-12-3456", result[0])
    }

    @Test
    fun `section19 test16 phone number 555-867-5309 does not match SSN pattern`() {
        val text = "Call 555-867-5309 for info"
        assertEquals("Phone number pattern differs from SSN", 0, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test17 extract adjacent to parentheses`() {
        val text = "SSN: (123-45-6789)"
        assertEquals(1, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test18 extract two from long paragraph`() {
        val text = "Records show that 321-54-9876 was enrolled in 2010 " +
                "and co-applicant 432-65-0987 was added later."
        assertEquals(2, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test19 first extracted from list correct`() {
        val text = "Records: 001-01-0001, 002-02-0002, 003-03-0003"
        assertEquals("001-01-0001", extractSSNsFromText(text).first())
    }

    @Test
    fun `section19 test20 last extracted from list correct`() {
        val text = "Records: 001-01-0001, 002-02-0002, 003-03-0003"
        assertEquals("003-03-0003", extractSSNsFromText(text).last())
    }

    @Test
    fun `section19 test21 all extracted are structurally valid`() {
        val text = "SSNs: 001-01-0001, 100-10-1000, 200-20-2000, 300-30-3000"
        val result = extractSSNsFromText(text)
        assertTrue(result.all { isValidSSN(it) })
    }

    @Test
    fun `section19 test22 invalid area extracted but fails validation`() {
        val text = "Found: 000-01-0001 in document"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertFalse(isValidSSN(result[0]))
    }

    @Test
    fun `section19 test23 area 666 extracted but fails validation`() {
        val text = "Record: 666-01-0001"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertFalse(isValidSSN(result[0]))
    }

    @Test
    fun `section19 test24 multiline 2 valid 1 invalid`() {
        val text = "Good: 100-10-1000\nBad: 000-10-1000\nGood: 200-20-2000"
        val extracted = extractSSNsFromText(text)
        assertEquals(3, extracted.size)
        assertEquals(2, extracted.count { isValidSSN(it) })
    }

    @Test
    fun `section19 test25 empty text returns empty list`() {
        assertEquals(emptyList<String>(), extractSSNsFromText(""))
    }

    @Test
    fun `section19 test26 whitespace-only text returns empty list`() {
        assertEquals(emptyList<String>(), extractSSNsFromText("   \n\t  "))
    }

    @Test
    fun `section19 test27 single char text returns empty list`() {
        assertEquals(emptyList<String>(), extractSSNsFromText("x"))
    }

    @Test
    fun `section19 test28 repeated ssn extracted twice`() {
        val text = "SSN 123-45-6789 and again 123-45-6789"
        assertEquals(2, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test29 five ssns in one sentence`() {
        val text = "Applicants: 101-11-1001, 202-22-2002, 303-33-3003, 404-44-4004, 505-55-5005."
        assertEquals(5, extractSSNsFromText(text).size)
    }

    @Test
    fun `section19 test30 surrounded by digits no word boundary no match`() {
        val text = "X123-45-6789Y should not match due to word boundary"
        assertEquals(0, extractSSNsFromText(text).size)
    }

    // =========================================================================
    // SECTION 20 — Edge cases, masking, formatting, boundaries, and helpers
    // =========================================================================

    @Test
    fun `section20 test01 maskSSN hides first five digits shows last four`() {
        assertEquals("***-**-6789", maskSSN("123-45-6789"))
    }

    @Test
    fun `section20 test02 maskSSN preserves last four digits 9999`() {
        assertEquals("***-**-9999", maskSSN("999-99-9999"))
    }

    @Test
    fun `section20 test03 maskSSN handles raw 9-digit input`() {
        assertEquals("***-**-6789", maskSSN("123456789"))
    }

    @Test
    fun `section20 test04 maskSSN last four 0001`() {
        assertEquals("***-**-0001", maskSSN("001-01-0001"))
    }

    @Test
    fun `section20 test05 maskSSN last four 1234`() {
        assertEquals("***-**-1234", maskSSN("100-12-1234"))
    }

    @Test
    fun `section20 test06 formatSSN produces NNN-GG-SSSS from 9 raw digits`() {
        assertEquals("123-45-6789", formatSSN("123456789"))
    }

    @Test
    fun `section20 test07 formatSSN returns original when not 9 digits`() {
        assertEquals("12345", formatSSN("12345"))
    }

    @Test
    fun `section20 test08 area 665 last valid area before reserved 666`() {
        assertTrue(isValidSSN("665-01-0001"))
        assertFalse(isValidSSN("666-01-0001"))
    }

    @Test
    fun `section20 test09 area 667 first valid area after reserved 666`() {
        assertTrue(isValidSSN("667-01-0001"))
    }

    @Test
    fun `section20 test10 area 001 group 01 serial 0001 absolute minimum valid`() {
        assertTrue(isValidSSN("001-01-0001"))
    }

    @Test
    fun `section20 test11 area 899 group 99 serial 9999 highest non-ITIN valid`() {
        assertTrue(isValidSSN("899-99-9999"))
    }

    @Test
    fun `section20 test12 area 700 valid`() {
        assertTrue(isValidSSN("700-70-7007"))
    }

    @Test
    fun `section20 test13 area 750 valid`() {
        assertTrue(isValidSSN("750-75-7507"))
    }

    @Test
    fun `section20 test14 area 800 valid`() {
        assertTrue(isValidSSN("800-80-8008"))
    }

    @Test
    fun `section20 test15 area 850 valid`() {
        assertTrue(isValidSSN("850-85-8508"))
    }

    @Test
    fun `section20 test16 isValidSSN is pure function same in same out`() {
        val ssn = "345-67-8901"
        val r1 = isValidSSN(ssn)
        val r2 = isValidSSN(ssn)
        assertEquals(r1, r2)
    }

    @Test
    fun `section20 test17 batch 10 valid ssns all pass`() {
        val ssns = listOf(
            "001-01-0001", "100-10-1000", "200-20-2000", "300-30-3000",
            "400-40-4000", "500-50-5000", "600-60-6000", "001-99-9999",
            "299-01-0001", "399-01-0001"
        )
        assertEquals(10, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test18 batch 5 invalid ssns all fail`() {
        val ssns = listOf(
            "000-01-0001", "666-01-0001", "900-01-0001", "100-00-1000", "100-01-0000"
        )
        assertEquals(0, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test19 countInvalidSSNs works correctly`() {
        val mixed = listOf("100-10-1000", "000-00-0000", "200-20-2000", "666-66-6666", "300-30-3000")
        assertEquals(2, countInvalidSSNs(mixed))
    }

    @Test
    fun `section20 test20 filterValid returns only valid ssns`() {
        val mixed = listOf("100-10-1000", "000-00-0000", "200-20-2000", "666-66-6666")
        val valid = filterValid(mixed)
        assertEquals(2, valid.size)
        assertTrue(valid.all { isValidSSN(it) })
    }

    @Test
    fun `section20 test21 filterInvalid returns only invalid ssns`() {
        val mixed = listOf("100-10-1000", "000-00-0000", "200-20-2000", "666-66-6666")
        val invalid = filterInvalid(mixed)
        assertEquals(2, invalid.size)
        assertTrue(invalid.none { isValidSSN(it) })
    }

    @Test
    fun `section20 test22 area 668 is valid just above 666`() {
        assertTrue(isValidSSN("668-10-1001"))
    }

    @Test
    fun `section20 test23 area 899 valid boundary just below 900`() {
        assertTrue(isValidSSN("899-01-0001"))
    }

    @Test
    fun `section20 test24 area 900 invalid boundary just at ITIN start`() {
        assertFalse(isValidSSN("900-01-0001"))
    }

    @Test
    fun `section20 test25 group 01 valid as minimum non-zero group`() {
        assertTrue(isValidSSN("100-01-0001"))
    }

    @Test
    fun `section20 test26 group 99 valid as maximum group`() {
        assertTrue(isValidSSN("100-99-9999"))
    }

    @Test
    fun `section20 test27 serial 0001 valid as minimum non-zero serial`() {
        assertTrue(isValidSSN("100-01-0001"))
    }

    @Test
    fun `section20 test28 serial 9999 valid as maximum serial`() {
        assertTrue(isValidSSN("100-01-9999"))
    }

    @Test
    fun `section20 test29 area 001 to 010 all structurally valid`() {
        val ssns = (1..10).map { "${it.toString().padStart(3, '0')}-01-0001" }
        assertEquals(10, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test30 area 660 to 669 valid except 666`() {
        val valid = (660..669).filter { it != 666 }.map { "$it-01-0001" }
        val invalid = listOf("666-01-0001")
        assertEquals(9, countValidSSNs(valid))
        assertEquals(0, countValidSSNs(invalid))
    }

    // =========================================================================
    // SECTION 20 CONTINUED — Additional boundary and batch tests
    // =========================================================================

    @Test
    fun `section20 test31 area 001 minimum valid area all components minimum`() {
        val ssn = "001-01-0001"
        assertTrue(isValidSSN(ssn))
        val masked = maskSSN(ssn)
        assertTrue("Masked should start with ***", masked.startsWith("***"))
        assertEquals("***-**-0001", masked)
    }

    @Test
    fun `section20 test32 area 100 to 199 all ten-step boundaries valid`() {
        val ssns = (100..199 step 10).map { "$it-10-1000" }
        assertEquals("All 100-199 step-10 should be valid", ssns.size, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test33 area 200 to 299 all ten-step boundaries valid`() {
        val ssns = (200..299 step 10).map { "$it-20-2000" }
        assertEquals(ssns.size, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test34 area 300 to 399 all ten-step boundaries valid`() {
        val ssns = (300..399 step 10).map { "$it-30-3000" }
        assertEquals(ssns.size, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test35 area 400 to 499 all ten-step boundaries valid`() {
        val ssns = (400..499 step 10).map { "$it-40-4000" }
        assertEquals(ssns.size, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test36 area 500 to 599 all ten-step boundaries valid`() {
        val ssns = (500..599 step 10).map { "$it-50-5000" }
        assertEquals(ssns.size, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test37 area 600 to 665 all five-step boundaries valid`() {
        val ssns = (600..665 step 5).map { "$it-60-6000" }
        assertEquals(ssns.size, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test38 serial range 0001 to 0010 all valid for area 100 group 10`() {
        val ssns = (1..10).map { "100-10-${it.toString().padStart(4, '0')}" }
        assertEquals(10, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test39 serial range 9990 to 9999 all valid for area 200 group 20`() {
        val ssns = (9990..9999).map { "200-20-$it" }
        assertEquals(10, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test40 group range 01 to 10 all valid for area 300 serial 3000`() {
        val ssns = (1..10).map { "300-${it.toString().padStart(2, '0')}-3000" }
        assertEquals(10, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test41 group range 90 to 99 all valid for area 400 serial 4000`() {
        val ssns = (90..99).map { "400-$it-4000" }
        assertEquals(10, countValidSSNs(ssns))
    }

    @Test
    fun `section20 test42 extract and validate from realistic text block`() {
        val text = """
            HR DOCUMENT — CONFIDENTIAL
            Employee: Jane Smith
            Social Security: 234-56-7890
            Department: Engineering
            Salary: $95,000
            Start Date: 2021-03-15
            Emergency Contact SSN: 345-67-8901
        """.trimIndent()
        val ssns = extractSSNsFromText(text)
        assertEquals("Should extract 2 SSNs from HR document", 2, ssns.size)
        assertTrue("All extracted SSNs should be structurally valid", ssns.all { isValidSSN(it) })
    }

    @Test
    fun `section20 test43 extract and validate from medical note`() {
        val text = """
            Patient Record ID: MR-2023-00451
            Patient Name: Robert Chen
            SSN: 567-89-0123
            Date of Birth: 04/12/1975
            Insurance ID: INS-992-1234
            Physician: Dr. Emily Watson
            Diagnosis Code: J45.909
        """.trimIndent()
        val ssns = extractSSNsFromText(text)
        assertEquals(1, ssns.size)
        assertEquals("567-89-0123", ssns[0])
        assertTrue(isValidSSN(ssns[0]))
    }

    @Test
    fun `section20 test44 mask multiple ssns extracted from text`() {
        val text = "Primary: 100-10-1000, Secondary: 200-20-2000"
        val ssns = extractSSNsFromText(text)
        val masked = ssns.map { maskSSN(it) }
        assertEquals(2, masked.size)
        assertTrue(masked.all { it.startsWith("***-**-") })
        assertEquals("***-**-1000", masked[0])
        assertEquals("***-**-2000", masked[1])
    }

    @Test
    fun `section20 test45 format then validate round trip`() {
        val rawDigits = "400401000"
        val formatted = formatSSN(rawDigits)
        assertEquals("400-40-1000", formatted)
        assertTrue("Formatted SSN should be valid", isValidSSN(formatted))
    }

    @Test
    fun `section20 test46 validate ssn with leading zeros in area`() {
        val ssn = "007-07-0777"
        assertTrue(isValidSSN(ssn))
        assertEquals("007-07-0777", formatSSN(ssn.replace("-", "")))
    }

    @Test
    fun `section20 test47 area 111 group 11 serial 1111 consistent repeated digit pattern`() {
        val ssn = "111-11-1111"
        assertTrue(isValidSSN(ssn))
        assertFalse("Not a known sequential fake", isSequentialFakeSSN(ssn))
        assertEquals("***-**-1111", maskSSN(ssn))
    }

    @Test
    fun `section20 test48 countValidSSNs on empty list`() {
        assertEquals(0, countValidSSNs(emptyList()))
    }

    @Test
    fun `section20 test49 countInvalidSSNs on empty list`() {
        assertEquals(0, countInvalidSSNs(emptyList()))
    }

    @Test
    fun `section20 test50 filterValid on empty list returns empty`() {
        assertEquals(emptyList<String>(), filterValid(emptyList()))
    }

    @Test
    fun `section20 test51 filterInvalid on empty list returns empty`() {
        assertEquals(emptyList<String>(), filterInvalid(emptyList()))
    }

    @Test
    fun `section20 test52 area 898 valid`() {
        assertTrue(isValidSSN("898-98-9898"))
    }

    @Test
    fun `section20 test53 area 897 valid`() {
        assertTrue(isValidSSN("897-97-9797"))
    }

    @Test
    fun `section20 test54 area 667 valid`() {
        assertTrue(isValidSSN("667-67-6767"))
    }

    @Test
    fun `section20 test55 area 668 valid`() {
        assertTrue(isValidSSN("668-68-6868"))
    }

    @Test
    fun `section20 test56 area 700 valid`() {
        assertTrue(isValidSSN("700-70-7070"))
    }

    @Test
    fun `section20 test57 area 800 valid`() {
        assertTrue(isValidSSN("800-80-8080"))
    }

    @Test
    fun `section20 test58 mixed valid and invalid batch statistics`() {
        val batch = listOf(
            "100-10-1000",   // valid
            "000-10-1000",   // invalid - area 000
            "200-20-2000",   // valid
            "666-20-2000",   // invalid - area 666
            "300-30-3000",   // valid
            "900-30-3000",   // invalid - area 900
            "400-00-4000",   // invalid - group 00
            "500-50-5000",   // valid
            "600-60-0000",   // invalid - serial 0000
            "700-70-7007"    // valid
        )
        assertEquals(5, countValidSSNs(batch))
        assertEquals(5, countInvalidSSNs(batch))
    }

    @Test
    fun `section20 test59 all six invalid conditions represented in one batch`() {
        val invalid = listOf(
            "000-10-1000",  // area 000
            "666-10-1000",  // area 666
            "900-10-1000",  // area 900+
            "100-00-1000",  // group 00
            "100-10-0000",  // serial 0000
            "12345678",     // too short
            "1234567890"    // too long
        )
        assertEquals("All should be invalid", 0, countValidSSNs(invalid))
    }

    @Test
    fun `section20 test60 real-world batch test of 20 ssns`() {
        val validSSNs = listOf(
            "001-01-0001", "010-10-1010", "050-50-5050", "099-99-9999",
            "100-10-1000", "150-50-5001", "199-99-9999", "200-20-2020",
            "250-50-5001", "299-99-9999", "300-30-3030", "350-50-5001",
            "399-99-9999", "400-40-4040", "450-50-5001", "499-99-9999",
            "500-50-5050", "550-50-5001", "599-99-9999", "600-60-6060"
        )
        val invalidSSNs = listOf(
            "000-01-0001", "666-01-0001", "900-01-0001", "999-99-9999",
            "100-00-1000", "200-20-0000", "300-00-0000"
        )
        assertEquals(20, countValidSSNs(validSSNs))
        assertEquals(0, countValidSSNs(invalidSSNs))
        assertEquals(7, countInvalidSSNs(invalidSSNs))
    }
}

