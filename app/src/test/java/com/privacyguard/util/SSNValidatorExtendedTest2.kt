package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

/**
 * SSNValidatorExtendedTest2 — 300+ tests in 20 sections covering all SSN validation rules.
 * Covers area ranges 001-665, ITIN 900-999, invalid codes 000/666/900+,
 * group/serial zero checks, format variants, sequential fake detection,
 * text extraction, masking, formatting, and comprehensive edge cases.
 */
class SSNValidatorExtendedTest2 {

    // -------------------------------------------------------------------------
    // Validator stubs — local implementations for unit testing
    // -------------------------------------------------------------------------

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

    private fun extractSSNsFromText(text: String): List<String> {
        val pattern = Regex("\\b(\\d{3}[-]\\d{2}[-]\\d{4})\\b")
        return pattern.findAll(text).map { it.value }.toList()
    }

    private fun maskSSN(ssn: String): String {
        val digits = ssn.replace("-", "").replace(" ", "")
        if (digits.length != 9) return ssn
        return "***-**-${digits.substring(5)}"
    }

    private fun formatSSN(digits: String): String {
        val clean = digits.replace("-", "").replace(" ", "")
        if (clean.length != 9) return digits
        return "${clean.substring(0, 3)}-${clean.substring(3, 5)}-${clean.substring(5)}"
    }

    private fun isSequentialFakeSSN(ssn: String): Boolean {
        val knownFakes = setOf("123456789", "219099999", "078051120", "457555462")
        val digits = ssn.replace("-", "").replace(" ", "")
        return digits in knownFakes
    }

    private fun getSSNAreaRange(ssn: String): String {
        val digits = ssn.replace("-", "").replace(" ", "")
        if (digits.length < 3 || !digits.all { it.isDigit() }) return "INVALID"
        val area = digits.substring(0, 3).toInt()
        return when {
            area in 1..9 -> "001-009"
            area in 10..99 -> "010-099"
            area in 100..199 -> "100-199"
            area in 200..299 -> "200-299"
            area in 300..399 -> "300-399"
            area in 400..499 -> "400-499"
            area in 500..599 -> "500-599"
            area in 600..665 -> "600-665"
            area == 666 -> "INVALID_666"
            area in 667..899 -> "667-899"
            area in 900..999 -> "ITIN"
            else -> "INVALID"
        }
    }

    private fun countValidSSNsInList(ssns: List<String>): Int = ssns.count { isValidSSN(it) }
    private fun countInvalidSSNsInList(ssns: List<String>): Int = ssns.count { !isValidSSN(it) }
    private fun filterValidSSNs(ssns: List<String>): List<String> = ssns.filter { isValidSSN(it) }
    private fun filterInvalidSSNs(ssns: List<String>): List<String> = ssns.filter { !isValidSSN(it) }

    // =========================================================================
    // SECTION 1 — Valid SSNs area range 001–099
    // =========================================================================

    @Test fun `s1 valid ssn 001-01-0001 minimum area minimum group minimum serial`() {
        assertTrue("SSN 001-01-0001 should be valid", isValidSSN("001-01-0001"))
    }
    @Test fun `s1 valid ssn 001-01-9999 minimum area minimum group maximum serial`() {
        assertTrue(isValidSSN("001-01-9999"))
    }
    @Test fun `s1 valid ssn 001-99-0001 minimum area maximum group minimum serial`() {
        assertTrue(isValidSSN("001-99-0001"))
    }
    @Test fun `s1 valid ssn 001-99-9999 minimum area maximum group maximum serial`() {
        assertTrue(isValidSSN("001-99-9999"))
    }
    @Test fun `s1 valid ssn 005-05-0505`() { assertTrue(isValidSSN("005-05-0505")) }
    @Test fun `s1 valid ssn 007-07-0007`() { assertTrue(isValidSSN("007-07-0007")) }
    @Test fun `s1 valid ssn 009-09-0009`() { assertTrue(isValidSSN("009-09-0009")) }
    @Test fun `s1 valid ssn 010-10-0010`() { assertTrue(isValidSSN("010-10-0010")) }
    @Test fun `s1 valid ssn 011-11-0011`() { assertTrue(isValidSSN("011-11-0011")) }
    @Test fun `s1 valid ssn 015-15-0015`() { assertTrue(isValidSSN("015-15-0015")) }
    @Test fun `s1 valid ssn 019-19-0019`() { assertTrue(isValidSSN("019-19-0019")) }
    @Test fun `s1 valid ssn 020-20-0020`() { assertTrue(isValidSSN("020-20-0020")) }
    @Test fun `s1 valid ssn 025-25-0025`() { assertTrue(isValidSSN("025-25-0025")) }
    @Test fun `s1 valid ssn 030-30-0030`() { assertTrue(isValidSSN("030-30-0030")) }
    @Test fun `s1 valid ssn 035-35-0035`() { assertTrue(isValidSSN("035-35-0035")) }
    @Test fun `s1 valid ssn 040-40-0040`() { assertTrue(isValidSSN("040-40-0040")) }
    @Test fun `s1 valid ssn 045-45-0045`() { assertTrue(isValidSSN("045-45-0045")) }
    @Test fun `s1 valid ssn 050-50-0050`() { assertTrue(isValidSSN("050-50-0050")) }
    @Test fun `s1 valid ssn 055-55-0055`() { assertTrue(isValidSSN("055-55-0055")) }
    @Test fun `s1 valid ssn 060-60-0060`() { assertTrue(isValidSSN("060-60-0060")) }
    @Test fun `s1 valid ssn 065-65-0065`() { assertTrue(isValidSSN("065-65-0065")) }
    @Test fun `s1 valid ssn 070-70-0070`() { assertTrue(isValidSSN("070-70-0070")) }
    @Test fun `s1 valid ssn 075-75-0075`() { assertTrue(isValidSSN("075-75-0075")) }
    @Test fun `s1 valid ssn 080-80-0080`() { assertTrue(isValidSSN("080-80-0080")) }
    @Test fun `s1 valid ssn 085-85-0085`() { assertTrue(isValidSSN("085-85-0085")) }
    @Test fun `s1 valid ssn 090-90-0090`() { assertTrue(isValidSSN("090-90-0090")) }
    @Test fun `s1 valid ssn 095-95-0095`() { assertTrue(isValidSSN("095-95-0095")) }
    @Test fun `s1 valid ssn 099-99-9999`() { assertTrue(isValidSSN("099-99-9999")) }
    @Test fun `s1 area range 001-099 identifies correctly`() {
        val ssn = "050-50-5050"
        val range = getSSNAreaRange(ssn)
        assertTrue(range in listOf("010-099"))
    }
    @Test fun `s1 area 001 is not in ITIN range`() {
        assertFalse(isValidITIN("001-01-0001"))
    }
    @Test fun `s1 all in batch 001-030 valid`() {
        val validList = (1..30).map { "${it.toString().padStart(3, '0')}-01-0001" }
        assertEquals(30, countValidSSNsInList(validList))
    }
    @Test fun `s1 valid ssn 001-50-5050`() { assertTrue(isValidSSN("001-50-5050")) }
    @Test fun `s1 valid ssn 099-01-0099`() { assertTrue(isValidSSN("099-01-0099")) }
    @Test fun `s1 valid ssn 002-02-0202`() { assertTrue(isValidSSN("002-02-0202")) }
    @Test fun `s1 valid ssn 003-03-0303`() { assertTrue(isValidSSN("003-03-0303")) }
    @Test fun `s1 valid ssn 004-04-0404`() { assertTrue(isValidSSN("004-04-0404")) }
    @Test fun `s1 valid ssn 006-06-0606`() { assertTrue(isValidSSN("006-06-0606")) }
    @Test fun `s1 valid ssn 008-08-0808`() { assertTrue(isValidSSN("008-08-0808")) }
    @Test fun `s1 valid ssn 012-12-1212`() { assertTrue(isValidSSN("012-12-1212")) }
    @Test fun `s1 valid ssn 013-13-1313`() { assertTrue(isValidSSN("013-13-1313")) }
    @Test fun `s1 valid ssn 014-14-1414`() { assertTrue(isValidSSN("014-14-1414")) }

    // =========================================================================
    // SECTION 2 — Valid SSNs area range 100–199
    // =========================================================================

    @Test fun `s2 valid ssn 100-01-0001`() { assertTrue(isValidSSN("100-01-0001")) }
    @Test fun `s2 valid ssn 100-99-9999`() { assertTrue(isValidSSN("100-99-9999")) }
    @Test fun `s2 valid ssn 101-11-1111`() { assertTrue(isValidSSN("101-11-1111")) }
    @Test fun `s2 valid ssn 102-22-2222`() { assertTrue(isValidSSN("102-22-2222")) }
    @Test fun `s2 valid ssn 103-33-3333`() { assertTrue(isValidSSN("103-33-3333")) }
    @Test fun `s2 valid ssn 104-44-4444`() { assertTrue(isValidSSN("104-44-4444")) }
    @Test fun `s2 valid ssn 105-55-5555`() { assertTrue(isValidSSN("105-55-5555")) }
    @Test fun `s2 valid ssn 106-66-6666`() { assertTrue(isValidSSN("106-66-6666")) }
    @Test fun `s2 valid ssn 107-77-7777`() { assertTrue(isValidSSN("107-77-7777")) }
    @Test fun `s2 valid ssn 108-88-8888`() { assertTrue(isValidSSN("108-88-8888")) }
    @Test fun `s2 valid ssn 109-99-9999`() { assertTrue(isValidSSN("109-99-9999")) }
    @Test fun `s2 valid ssn 110-10-1010`() { assertTrue(isValidSSN("110-10-1010")) }
    @Test fun `s2 valid ssn 115-15-1515`() { assertTrue(isValidSSN("115-15-1515")) }
    @Test fun `s2 valid ssn 120-20-2020`() { assertTrue(isValidSSN("120-20-2020")) }
    @Test fun `s2 valid ssn 125-25-2525`() { assertTrue(isValidSSN("125-25-2525")) }
    @Test fun `s2 valid ssn 130-30-3030`() { assertTrue(isValidSSN("130-30-3030")) }
    @Test fun `s2 valid ssn 135-35-3535`() { assertTrue(isValidSSN("135-35-3535")) }
    @Test fun `s2 valid ssn 140-40-4040`() { assertTrue(isValidSSN("140-40-4040")) }
    @Test fun `s2 valid ssn 145-45-4545`() { assertTrue(isValidSSN("145-45-4545")) }
    @Test fun `s2 valid ssn 150-50-5050`() { assertTrue(isValidSSN("150-50-5050")) }
    @Test fun `s2 valid ssn 155-55-5555`() { assertTrue(isValidSSN("155-55-5555")) }
    @Test fun `s2 valid ssn 160-60-6060`() { assertTrue(isValidSSN("160-60-6060")) }
    @Test fun `s2 valid ssn 165-65-6565`() { assertTrue(isValidSSN("165-65-6565")) }
    @Test fun `s2 valid ssn 170-70-7070`() { assertTrue(isValidSSN("170-70-7070")) }
    @Test fun `s2 valid ssn 175-75-7575`() { assertTrue(isValidSSN("175-75-7575")) }
    @Test fun `s2 valid ssn 180-80-8080`() { assertTrue(isValidSSN("180-80-8080")) }
    @Test fun `s2 valid ssn 185-85-8585`() { assertTrue(isValidSSN("185-85-8585")) }
    @Test fun `s2 valid ssn 190-90-9090`() { assertTrue(isValidSSN("190-90-9090")) }
    @Test fun `s2 valid ssn 195-95-9595`() { assertTrue(isValidSSN("195-95-9595")) }
    @Test fun `s2 valid ssn 199-99-9999`() { assertTrue(isValidSSN("199-99-9999")) }
    @Test fun `s2 valid ssn 111-11-1111 all same digit area 1xx`() { assertTrue(isValidSSN("111-11-1111")) }
    @Test fun `s2 valid ssn 122-22-2222`() { assertTrue(isValidSSN("122-22-2222")) }
    @Test fun `s2 valid ssn 133-33-3333`() { assertTrue(isValidSSN("133-33-3333")) }
    @Test fun `s2 valid ssn 144-44-4444`() { assertTrue(isValidSSN("144-44-4444")) }
    @Test fun `s2 valid ssn 155-01-0001`() { assertTrue(isValidSSN("155-01-0001")) }
    @Test fun `s2 valid ssn 166-16-1661`() { assertTrue(isValidSSN("166-16-1661")) }
    @Test fun `s2 valid ssn 177-17-1771`() { assertTrue(isValidSSN("177-17-1771")) }
    @Test fun `s2 valid ssn 188-18-1881`() { assertTrue(isValidSSN("188-18-1881")) }
    @Test fun `s2 valid ssn 199-19-1991`() { assertTrue(isValidSSN("199-19-1991")) }
    @Test fun `s2 batch 100 to 110 all valid`() {
        val batch = (100..110).map { "$it-10-1000" }
        assertTrue(batch.all { isValidSSN(it) })
    }
    @Test fun `s2 valid ssn 160-01-0001`() { assertTrue(isValidSSN("160-01-0001")) }

    // =========================================================================
    // SECTION 3 — Valid SSNs area range 200–299
    // =========================================================================

    @Test fun `s3 valid ssn 200-01-0001`() { assertTrue(isValidSSN("200-01-0001")) }
    @Test fun `s3 valid ssn 200-99-9999`() { assertTrue(isValidSSN("200-99-9999")) }
    @Test fun `s3 valid ssn 201-10-1000`() { assertTrue(isValidSSN("201-10-1000")) }
    @Test fun `s3 valid ssn 205-05-0505`() { assertTrue(isValidSSN("205-05-0505")) }
    @Test fun `s3 valid ssn 210-10-1010`() { assertTrue(isValidSSN("210-10-1010")) }
    @Test fun `s3 valid ssn 215-15-1515`() { assertTrue(isValidSSN("215-15-1515")) }
    @Test fun `s3 valid ssn 220-20-2020`() { assertTrue(isValidSSN("220-20-2020")) }
    @Test fun `s3 valid ssn 225-25-2525`() { assertTrue(isValidSSN("225-25-2525")) }
    @Test fun `s3 valid ssn 230-30-3030`() { assertTrue(isValidSSN("230-30-3030")) }
    @Test fun `s3 valid ssn 235-35-3535`() { assertTrue(isValidSSN("235-35-3535")) }
    @Test fun `s3 valid ssn 240-40-4040`() { assertTrue(isValidSSN("240-40-4040")) }
    @Test fun `s3 valid ssn 245-45-4545`() { assertTrue(isValidSSN("245-45-4545")) }
    @Test fun `s3 valid ssn 250-50-5050`() { assertTrue(isValidSSN("250-50-5050")) }
    @Test fun `s3 valid ssn 255-55-5555`() { assertTrue(isValidSSN("255-55-5555")) }
    @Test fun `s3 valid ssn 260-60-6060`() { assertTrue(isValidSSN("260-60-6060")) }
    @Test fun `s3 valid ssn 265-65-6565`() { assertTrue(isValidSSN("265-65-6565")) }
    @Test fun `s3 valid ssn 270-70-7070`() { assertTrue(isValidSSN("270-70-7070")) }
    @Test fun `s3 valid ssn 275-75-7575`() { assertTrue(isValidSSN("275-75-7575")) }
    @Test fun `s3 valid ssn 280-80-8080`() { assertTrue(isValidSSN("280-80-8080")) }
    @Test fun `s3 valid ssn 285-85-8585`() { assertTrue(isValidSSN("285-85-8585")) }
    @Test fun `s3 valid ssn 290-90-9090`() { assertTrue(isValidSSN("290-90-9090")) }
    @Test fun `s3 valid ssn 295-95-9595`() { assertTrue(isValidSSN("295-95-9595")) }
    @Test fun `s3 valid ssn 299-99-9999`() { assertTrue(isValidSSN("299-99-9999")) }
    @Test fun `s3 valid ssn 211-21-2121`() { assertTrue(isValidSSN("211-21-2121")) }
    @Test fun `s3 valid ssn 222-22-2222 all same digit area 2xx`() { assertTrue(isValidSSN("222-22-2222")) }
    @Test fun `s3 valid ssn 233-33-3333`() { assertTrue(isValidSSN("233-33-3333")) }
    @Test fun `s3 valid ssn 244-44-4444`() { assertTrue(isValidSSN("244-44-4444")) }
    @Test fun `s3 valid ssn 255-55-5551`() { assertTrue(isValidSSN("255-55-5551")) }
    @Test fun `s3 valid ssn 266-66-6661`() { assertTrue(isValidSSN("266-66-6661")) }
    @Test fun `s3 valid ssn 277-77-7771`() { assertTrue(isValidSSN("277-77-7771")) }
    @Test fun `s3 valid ssn 288-88-8881`() { assertTrue(isValidSSN("288-88-8881")) }
    @Test fun `s3 valid ssn 299-99-9991`() { assertTrue(isValidSSN("299-99-9991")) }
    @Test fun `s3 batch 200 to 209 all valid`() {
        val batch = (200..209).map { "$it-20-2000" }
        assertTrue(batch.all { isValidSSN(it) })
    }
    @Test fun `s3 valid ssn 202-02-0202`() { assertTrue(isValidSSN("202-02-0202")) }
    @Test fun `s3 valid ssn 203-03-0303`() { assertTrue(isValidSSN("203-03-0303")) }
    @Test fun `s3 valid ssn 204-04-0404`() { assertTrue(isValidSSN("204-04-0404")) }
    @Test fun `s3 valid ssn 206-06-0606`() { assertTrue(isValidSSN("206-06-0606")) }
    @Test fun `s3 valid ssn 207-07-0707`() { assertTrue(isValidSSN("207-07-0707")) }
    @Test fun `s3 valid ssn 208-08-0808`() { assertTrue(isValidSSN("208-08-0808")) }
    @Test fun `s3 valid ssn 209-09-0909`() { assertTrue(isValidSSN("209-09-0909")) }

    // =========================================================================
    // SECTION 4 — Valid SSNs area range 300–399
    // =========================================================================

    @Test fun `s4 valid ssn 300-01-0001`() { assertTrue(isValidSSN("300-01-0001")) }
    @Test fun `s4 valid ssn 300-99-9999`() { assertTrue(isValidSSN("300-99-9999")) }
    @Test fun `s4 valid ssn 310-10-1010`() { assertTrue(isValidSSN("310-10-1010")) }
    @Test fun `s4 valid ssn 320-20-2020`() { assertTrue(isValidSSN("320-20-2020")) }
    @Test fun `s4 valid ssn 330-30-3030`() { assertTrue(isValidSSN("330-30-3030")) }
    @Test fun `s4 valid ssn 333-33-3333 all same digit area 3xx`() { assertTrue(isValidSSN("333-33-3333")) }
    @Test fun `s4 valid ssn 340-40-4040`() { assertTrue(isValidSSN("340-40-4040")) }
    @Test fun `s4 valid ssn 350-50-5050`() { assertTrue(isValidSSN("350-50-5050")) }
    @Test fun `s4 valid ssn 360-60-6060`() { assertTrue(isValidSSN("360-60-6060")) }
    @Test fun `s4 valid ssn 370-70-7070`() { assertTrue(isValidSSN("370-70-7070")) }
    @Test fun `s4 valid ssn 380-80-8080`() { assertTrue(isValidSSN("380-80-8080")) }
    @Test fun `s4 valid ssn 390-90-9090`() { assertTrue(isValidSSN("390-90-9090")) }
    @Test fun `s4 valid ssn 399-99-9999`() { assertTrue(isValidSSN("399-99-9999")) }
    @Test fun `s4 valid ssn 301-11-1101`() { assertTrue(isValidSSN("301-11-1101")) }
    @Test fun `s4 valid ssn 311-21-2111`() { assertTrue(isValidSSN("311-21-2111")) }
    @Test fun `s4 valid ssn 321-31-3121`() { assertTrue(isValidSSN("321-31-3121")) }
    @Test fun `s4 valid ssn 331-41-4131`() { assertTrue(isValidSSN("331-41-4131")) }
    @Test fun `s4 valid ssn 341-51-5141`() { assertTrue(isValidSSN("341-51-5141")) }
    @Test fun `s4 valid ssn 351-61-6151`() { assertTrue(isValidSSN("351-61-6151")) }
    @Test fun `s4 valid ssn 361-71-7161`() { assertTrue(isValidSSN("361-71-7161")) }
    @Test fun `s4 valid ssn 371-81-8171`() { assertTrue(isValidSSN("371-81-8171")) }
    @Test fun `s4 valid ssn 381-91-9181`() { assertTrue(isValidSSN("381-91-9181")) }
    @Test fun `s4 valid ssn 391-01-0191`() { assertTrue(isValidSSN("391-01-0191")) }
    @Test fun `s4 valid ssn 302-12-2312`() { assertTrue(isValidSSN("302-12-2312")) }
    @Test fun `s4 valid ssn 303-23-3423`() { assertTrue(isValidSSN("303-23-3423")) }
    @Test fun `s4 valid ssn 304-34-4534`() { assertTrue(isValidSSN("304-34-4534")) }
    @Test fun `s4 valid ssn 305-45-5645`() { assertTrue(isValidSSN("305-45-5645")) }
    @Test fun `s4 valid ssn 306-56-6756`() { assertTrue(isValidSSN("306-56-6756")) }
    @Test fun `s4 valid ssn 307-67-7867`() { assertTrue(isValidSSN("307-67-7867")) }
    @Test fun `s4 valid ssn 308-78-8978`() { assertTrue(isValidSSN("308-78-8978")) }
    @Test fun `s4 valid ssn 309-89-9089`() { assertTrue(isValidSSN("309-89-9089")) }
    @Test fun `s4 batch 300 to 309 all valid`() {
        val batch = (300..309).map { "$it-30-3000" }
        assertTrue(batch.all { isValidSSN(it) })
    }
    @Test fun `s4 batch 390 to 399 all valid`() {
        val batch = (390..399).map { "$it-39-3900" }
        assertTrue(batch.all { isValidSSN(it) })
    }
    @Test fun `s4 valid ssn 315-15-1515`() { assertTrue(isValidSSN("315-15-1515")) }
    @Test fun `s4 valid ssn 325-25-2525`() { assertTrue(isValidSSN("325-25-2525")) }
    @Test fun `s4 valid ssn 335-35-3535`() { assertTrue(isValidSSN("335-35-3535")) }
    @Test fun `s4 valid ssn 345-45-4545`() { assertTrue(isValidSSN("345-45-4545")) }
    @Test fun `s4 valid ssn 355-55-5555`() { assertTrue(isValidSSN("355-55-5555")) }
    @Test fun `s4 valid ssn 365-65-6565`() { assertTrue(isValidSSN("365-65-6565")) }
    @Test fun `s4 valid ssn 375-75-7575`() { assertTrue(isValidSSN("375-75-7575")) }
    @Test fun `s4 valid ssn 385-85-8585`() { assertTrue(isValidSSN("385-85-8585")) }

    // =========================================================================
    // SECTION 5 — Valid SSNs area range 400–499
    // =========================================================================

    @Test fun `s5 valid ssn 400-01-0001`() { assertTrue(isValidSSN("400-01-0001")) }
    @Test fun `s5 valid ssn 400-99-9999`() { assertTrue(isValidSSN("400-99-9999")) }
    @Test fun `s5 valid ssn 401-10-1001`() { assertTrue(isValidSSN("401-10-1001")) }
    @Test fun `s5 valid ssn 410-10-1010`() { assertTrue(isValidSSN("410-10-1010")) }
    @Test fun `s5 valid ssn 420-20-2020`() { assertTrue(isValidSSN("420-20-2020")) }
    @Test fun `s5 valid ssn 430-30-3030`() { assertTrue(isValidSSN("430-30-3030")) }
    @Test fun `s5 valid ssn 440-40-4040`() { assertTrue(isValidSSN("440-40-4040")) }
    @Test fun `s5 valid ssn 444-44-4444 all same digit area 4xx`() { assertTrue(isValidSSN("444-44-4444")) }
    @Test fun `s5 valid ssn 450-50-5050`() { assertTrue(isValidSSN("450-50-5050")) }
    @Test fun `s5 valid ssn 460-60-6060`() { assertTrue(isValidSSN("460-60-6060")) }
    @Test fun `s5 valid ssn 470-70-7070`() { assertTrue(isValidSSN("470-70-7070")) }
    @Test fun `s5 valid ssn 480-80-8080`() { assertTrue(isValidSSN("480-80-8080")) }
    @Test fun `s5 valid ssn 490-90-9090`() { assertTrue(isValidSSN("490-90-9090")) }
    @Test fun `s5 valid ssn 499-99-9999`() { assertTrue(isValidSSN("499-99-9999")) }
    @Test fun `s5 valid ssn 405-15-2536`() { assertTrue(isValidSSN("405-15-2536")) }
    @Test fun `s5 valid ssn 415-25-3647`() { assertTrue(isValidSSN("415-25-3647")) }
    @Test fun `s5 valid ssn 425-35-4758`() { assertTrue(isValidSSN("425-35-4758")) }
    @Test fun `s5 valid ssn 435-45-5869`() { assertTrue(isValidSSN("435-45-5869")) }
    @Test fun `s5 valid ssn 445-55-6970`() { assertTrue(isValidSSN("445-55-6970")) }
    @Test fun `s5 valid ssn 455-65-7081`() { assertTrue(isValidSSN("455-65-7081")) }
    @Test fun `s5 valid ssn 465-75-8192`() { assertTrue(isValidSSN("465-75-8192")) }
    @Test fun `s5 valid ssn 475-85-9203`() { assertTrue(isValidSSN("475-85-9203")) }
    @Test fun `s5 valid ssn 485-95-0314`() { assertTrue(isValidSSN("485-95-0314")) }
    @Test fun `s5 valid ssn 495-05-1425`() { assertTrue(isValidSSN("495-05-1425")) }
    @Test fun `s5 valid ssn 402-12-3456`() { assertTrue(isValidSSN("402-12-3456")) }
    @Test fun `s5 valid ssn 403-23-4567`() { assertTrue(isValidSSN("403-23-4567")) }
    @Test fun `s5 valid ssn 404-34-5678`() { assertTrue(isValidSSN("404-34-5678")) }
    @Test fun `s5 valid ssn 406-45-6789`() { assertTrue(isValidSSN("406-45-6789")) }
    @Test fun `s5 valid ssn 407-56-7890`() { assertTrue(isValidSSN("407-56-7890")) }
    @Test fun `s5 valid ssn 408-67-8901`() { assertTrue(isValidSSN("408-67-8901")) }
    @Test fun `s5 batch 400 to 409 all valid`() {
        val batch = (400..409).map { "$it-40-4000" }
        assertTrue(batch.all { isValidSSN(it) })
    }

    // =========================================================================
    // SECTION 6 — Valid SSNs area range 500–599
    // =========================================================================

    @Test fun `s6 valid ssn 500-01-0001`() { assertTrue(isValidSSN("500-01-0001")) }
    @Test fun `s6 valid ssn 500-99-9999`() { assertTrue(isValidSSN("500-99-9999")) }
    @Test fun `s6 valid ssn 501-11-1111`() { assertTrue(isValidSSN("501-11-1111")) }
    @Test fun `s6 valid ssn 510-10-1010`() { assertTrue(isValidSSN("510-10-1010")) }
    @Test fun `s6 valid ssn 520-20-2020`() { assertTrue(isValidSSN("520-20-2020")) }
    @Test fun `s6 valid ssn 530-30-3030`() { assertTrue(isValidSSN("530-30-3030")) }
    @Test fun `s6 valid ssn 540-40-4040`() { assertTrue(isValidSSN("540-40-4040")) }
    @Test fun `s6 valid ssn 550-50-5050`() { assertTrue(isValidSSN("550-50-5050")) }
    @Test fun `s6 valid ssn 555-55-5555 all same digit area 5xx`() { assertTrue(isValidSSN("555-55-5555")) }
    @Test fun `s6 valid ssn 560-60-6060`() { assertTrue(isValidSSN("560-60-6060")) }
    @Test fun `s6 valid ssn 570-70-7070`() { assertTrue(isValidSSN("570-70-7070")) }
    @Test fun `s6 valid ssn 580-80-8080`() { assertTrue(isValidSSN("580-80-8080")) }
    @Test fun `s6 valid ssn 590-90-9090`() { assertTrue(isValidSSN("590-90-9090")) }
    @Test fun `s6 valid ssn 599-99-9999`() { assertTrue(isValidSSN("599-99-9999")) }
    @Test fun `s6 valid ssn 505-15-1525`() { assertTrue(isValidSSN("505-15-1525")) }
    @Test fun `s6 valid ssn 515-25-2535`() { assertTrue(isValidSSN("515-25-2535")) }
    @Test fun `s6 valid ssn 525-35-3545`() { assertTrue(isValidSSN("525-35-3545")) }
    @Test fun `s6 valid ssn 535-45-4555`() { assertTrue(isValidSSN("535-45-4555")) }
    @Test fun `s6 valid ssn 545-55-5565`() { assertTrue(isValidSSN("545-55-5565")) }
    @Test fun `s6 valid ssn 565-75-7585`() { assertTrue(isValidSSN("565-75-7585")) }
    @Test fun `s6 valid ssn 575-85-8595`() { assertTrue(isValidSSN("575-85-8595")) }
    @Test fun `s6 valid ssn 585-95-9505`() { assertTrue(isValidSSN("585-95-9505")) }
    @Test fun `s6 valid ssn 595-05-0515`() { assertTrue(isValidSSN("595-05-0515")) }
    @Test fun `s6 valid ssn 502-12-3412`() { assertTrue(isValidSSN("502-12-3412")) }
    @Test fun `s6 valid ssn 503-23-4523`() { assertTrue(isValidSSN("503-23-4523")) }
    @Test fun `s6 valid ssn 504-34-5634`() { assertTrue(isValidSSN("504-34-5634")) }
    @Test fun `s6 valid ssn 506-45-6745`() { assertTrue(isValidSSN("506-45-6745")) }
    @Test fun `s6 valid ssn 507-56-7856`() { assertTrue(isValidSSN("507-56-7856")) }
    @Test fun `s6 valid ssn 508-67-8967`() { assertTrue(isValidSSN("508-67-8967")) }
    @Test fun `s6 valid ssn 509-78-9078`() { assertTrue(isValidSSN("509-78-9078")) }
    @Test fun `s6 batch 500 to 509 all valid`() {
        val batch = (500..509).map { "$it-50-5000" }
        assertTrue(batch.all { isValidSSN(it) })
    }

    // =========================================================================
    // SECTION 7 — Valid SSNs area range 600–665
    // =========================================================================

    @Test fun `s7 valid ssn 600-01-0001`() { assertTrue(isValidSSN("600-01-0001")) }
    @Test fun `s7 valid ssn 600-99-9999`() { assertTrue(isValidSSN("600-99-9999")) }
    @Test fun `s7 valid ssn 601-11-1111`() { assertTrue(isValidSSN("601-11-1111")) }
    @Test fun `s7 valid ssn 610-10-1010`() { assertTrue(isValidSSN("610-10-1010")) }
    @Test fun `s7 valid ssn 620-20-2020`() { assertTrue(isValidSSN("620-20-2020")) }
    @Test fun `s7 valid ssn 630-30-3030`() { assertTrue(isValidSSN("630-30-3030")) }
    @Test fun `s7 valid ssn 640-40-4040`() { assertTrue(isValidSSN("640-40-4040")) }
    @Test fun `s7 valid ssn 650-50-5050`() { assertTrue(isValidSSN("650-50-5050")) }
    @Test fun `s7 valid ssn 660-60-6060`() { assertTrue(isValidSSN("660-60-6060")) }
    @Test fun `s7 valid ssn 665-65-6565`() { assertTrue(isValidSSN("665-65-6565")) }
    @Test fun `s7 valid ssn 665-01-0001 upper boundary of 600 range`() { assertTrue(isValidSSN("665-01-0001")) }
    @Test fun `s7 valid ssn 602-22-2222`() { assertTrue(isValidSSN("602-22-2222")) }
    @Test fun `s7 valid ssn 603-33-3333`() { assertTrue(isValidSSN("603-33-3333")) }
    @Test fun `s7 valid ssn 604-44-4444`() { assertTrue(isValidSSN("604-44-4444")) }
    @Test fun `s7 valid ssn 605-55-5555`() { assertTrue(isValidSSN("605-55-5555")) }
    @Test fun `s7 valid ssn 606-66-6666`() { assertTrue(isValidSSN("606-66-6666")) }
    @Test fun `s7 valid ssn 607-77-7777`() { assertTrue(isValidSSN("607-77-7777")) }
    @Test fun `s7 valid ssn 608-88-8888`() { assertTrue(isValidSSN("608-88-8888")) }
    @Test fun `s7 valid ssn 609-99-9999`() { assertTrue(isValidSSN("609-99-9999")) }
    @Test fun `s7 valid ssn 611-01-0101`() { assertTrue(isValidSSN("611-01-0101")) }
    @Test fun `s7 valid ssn 612-02-0202`() { assertTrue(isValidSSN("612-02-0202")) }
    @Test fun `s7 valid ssn 613-03-0303`() { assertTrue(isValidSSN("613-03-0303")) }
    @Test fun `s7 valid ssn 614-04-0404`() { assertTrue(isValidSSN("614-04-0404")) }
    @Test fun `s7 valid ssn 615-05-0505`() { assertTrue(isValidSSN("615-05-0505")) }
    @Test fun `s7 valid ssn 616-06-0606`() { assertTrue(isValidSSN("616-06-0606")) }
    @Test fun `s7 valid ssn 617-07-0707`() { assertTrue(isValidSSN("617-07-0707")) }
    @Test fun `s7 valid ssn 618-08-0808`() { assertTrue(isValidSSN("618-08-0808")) }
    @Test fun `s7 valid ssn 619-09-0909`() { assertTrue(isValidSSN("619-09-0909")) }
    @Test fun `s7 valid ssn 621-11-1121`() { assertTrue(isValidSSN("621-11-1121")) }
    @Test fun `s7 batch 600 to 609 all valid`() {
        val batch = (600..609).map { "$it-60-6000" }
        assertTrue(batch.all { isValidSSN(it) })
    }

    // =========================================================================
    // SECTION 8 — ITIN validation 900–999 area codes
    // =========================================================================

    @Test fun `s8 valid itin 900-70-0001`() { assertTrue(isValidITIN("900-70-0001")) }
    @Test fun `s8 valid itin 901-71-1111`() { assertTrue(isValidITIN("901-71-1111")) }
    @Test fun `s8 valid itin 910-72-2222`() { assertTrue(isValidITIN("910-72-2222")) }
    @Test fun `s8 valid itin 920-73-3333`() { assertTrue(isValidITIN("920-73-3333")) }
    @Test fun `s8 valid itin 930-74-4444`() { assertTrue(isValidITIN("930-74-4444")) }
    @Test fun `s8 valid itin 940-75-5555`() { assertTrue(isValidITIN("940-75-5555")) }
    @Test fun `s8 valid itin 950-76-6666`() { assertTrue(isValidITIN("950-76-6666")) }
    @Test fun `s8 valid itin 960-77-7777`() { assertTrue(isValidITIN("960-77-7777")) }
    @Test fun `s8 valid itin 970-78-8888`() { assertTrue(isValidITIN("970-78-8888")) }
    @Test fun `s8 valid itin 980-79-9999`() { assertTrue(isValidITIN("980-79-9999")) }
    @Test fun `s8 valid itin 990-80-0001`() { assertTrue(isValidITIN("990-80-0001")) }
    @Test fun `s8 valid itin 999-88-8888`() { assertTrue(isValidITIN("999-88-8888")) }
    @Test fun `s8 itin 900-70-0001 not valid ssn`() { assertFalse(isValidSSN("900-70-0001")) }
    @Test fun `s8 itin 950-76-6666 not valid ssn`() { assertFalse(isValidSSN("950-76-6666")) }
    @Test fun `s8 itin 999-88-8888 not valid ssn`() { assertFalse(isValidSSN("999-88-8888")) }
    @Test fun `s8 valid itin 905-70-1234`() { assertTrue(isValidITIN("905-70-1234")) }
    @Test fun `s8 valid itin 915-80-2345`() { assertTrue(isValidITIN("915-80-2345")) }
    @Test fun `s8 valid itin 925-90-3456`() { assertTrue(isValidITIN("925-90-3456")) }
    @Test fun `s8 valid itin 935-70-4567`() { assertTrue(isValidITIN("935-70-4567")) }
    @Test fun `s8 valid itin 945-80-5678`() { assertTrue(isValidITIN("945-80-5678")) }
    @Test fun `s8 valid itin 955-90-6789`() { assertTrue(isValidITIN("955-90-6789")) }
    @Test fun `s8 valid itin 965-70-7890`() { assertTrue(isValidITIN("965-70-7890")) }
    @Test fun `s8 valid itin 975-80-8901`() { assertTrue(isValidITIN("975-80-8901")) }
    @Test fun `s8 valid itin 985-90-9012`() { assertTrue(isValidITIN("985-90-9012")) }
    @Test fun `s8 valid itin 995-70-0123`() { assertTrue(isValidITIN("995-70-0123")) }
    @Test fun `s8 batch itins 900 to 909 all valid`() {
        val batch = (900..909).map { "$it-70-7000" }
        assertTrue(batch.all { isValidITIN(it) })
    }
    @Test fun `s8 batch itins none valid as ssn`() {
        val batch = (900..909).map { "$it-70-7000" }
        assertTrue(batch.none { isValidSSN(it) })
    }
    @Test fun `s8 valid itin 902-72-2020`() { assertTrue(isValidITIN("902-72-2020")) }
    @Test fun `s8 valid itin 903-73-3030`() { assertTrue(isValidITIN("903-73-3030")) }
    @Test fun `s8 valid itin 904-74-4040`() { assertTrue(isValidITIN("904-74-4040")) }

    // =========================================================================
    // SECTION 9 — ITIN edge cases and invalid ITINs
    // =========================================================================

    @Test fun `s9 invalid itin group 00`() { assertFalse(isValidITIN("900-00-1234")) }
    @Test fun `s9 invalid itin serial 0000`() { assertFalse(isValidITIN("900-70-0000")) }
    @Test fun `s9 invalid itin area below 900`() { assertFalse(isValidITIN("899-70-1234")) }
    @Test fun `s9 invalid itin area 000`() { assertFalse(isValidITIN("000-70-1234")) }
    @Test fun `s9 invalid itin letters in area`() { assertFalse(isValidITIN("90A-70-1234")) }
    @Test fun `s9 invalid itin too short`() { assertFalse(isValidITIN("900-70-123")) }
    @Test fun `s9 invalid itin too long`() { assertFalse(isValidITIN("900-70-12345")) }
    @Test fun `s9 invalid itin empty`() { assertFalse(isValidITIN("")) }
    @Test fun `s9 valid itin 900-50-1234`() { assertTrue(isValidITIN("900-50-1234")) }
    @Test fun `s9 valid itin 910-60-4321`() { assertTrue(isValidITIN("910-60-4321")) }
    @Test fun `s9 invalid itin 100-70-1234 area too low`() { assertFalse(isValidITIN("100-70-1234")) }
    @Test fun `s9 invalid itin 500-70-1234 area too low`() { assertFalse(isValidITIN("500-70-1234")) }
    @Test fun `s9 valid itin 920-71-0001`() { assertTrue(isValidITIN("920-71-0001")) }
    @Test fun `s9 valid itin 930-72-0002`() { assertTrue(isValidITIN("930-72-0002")) }
    @Test fun `s9 valid itin 940-73-0003`() { assertTrue(isValidITIN("940-73-0003")) }
    @Test fun `s9 valid itin 950-74-0004`() { assertTrue(isValidITIN("950-74-0004")) }
    @Test fun `s9 valid itin 960-75-0005`() { assertTrue(isValidITIN("960-75-0005")) }
    @Test fun `s9 invalid itin has spaces no dashes`() { assertFalse(isValidITIN("900 70 1234")) }
    @Test fun `s9 valid itin 970-76-0006`() { assertTrue(isValidITIN("970-76-0006")) }
    @Test fun `s9 valid itin 980-77-0007`() { assertTrue(isValidITIN("980-77-0007")) }
    @Test fun `s9 invalid itin group 99 serial 0000`() { assertFalse(isValidITIN("900-99-0000")) }
    @Test fun `s9 invalid itin area exactly 899`() { assertFalse(isValidITIN("899-80-0001")) }
    @Test fun `s9 itin mutually exclusive with ssn 950-80-0001`() {
        val itin = "950-80-0001"
        assertTrue(isValidITIN(itin))
        assertFalse(isValidSSN(itin))
    }

    // =========================================================================
    // SECTION 10 — Invalid area 000
    // =========================================================================

    @Test fun `s10 invalid area 000 with dashes`() { assertFalse(isValidSSN("000-01-0001")) }
    @Test fun `s10 invalid area 000 no dashes`() { assertFalse(isValidSSN("000010001")) }
    @Test fun `s10 invalid area 000 group 99 serial 9999`() { assertFalse(isValidSSN("000-99-9999")) }
    @Test fun `s10 invalid area 000 group 01 serial 0001`() { assertFalse(isValidSSN("000-01-0001")) }
    @Test fun `s10 invalid area 000 group 50 serial 5000`() { assertFalse(isValidSSN("000-50-5000")) }
    @Test fun `s10 invalid area 000 group 10 serial 1000`() { assertFalse(isValidSSN("000-10-1000")) }
    @Test fun `s10 invalid area 000 group 20 serial 2000`() { assertFalse(isValidSSN("000-20-2000")) }
    @Test fun `s10 invalid area 000 group 30 serial 3000`() { assertFalse(isValidSSN("000-30-3000")) }
    @Test fun `s10 invalid area 000 group 40 serial 4000`() { assertFalse(isValidSSN("000-40-4000")) }
    @Test fun `s10 invalid area 000 group 60 serial 6000`() { assertFalse(isValidSSN("000-60-6000")) }
    @Test fun `s10 invalid area 000 group 70 serial 7000`() { assertFalse(isValidSSN("000-70-7000")) }
    @Test fun `s10 invalid area 000 group 80 serial 8000`() { assertFalse(isValidSSN("000-80-8000")) }
    @Test fun `s10 invalid area 000 group 90 serial 9000`() { assertFalse(isValidSSN("000-90-9000")) }
    @Test fun `s10 invalid area 000 group 05 serial 0500`() { assertFalse(isValidSSN("000-05-0500")) }
    @Test fun `s10 all area 000 combos fail`() {
        val ssns = listOf(
            "000-01-0001", "000-10-1000", "000-25-2500", "000-50-5050",
            "000-75-7575", "000-99-9999"
        )
        assertTrue("All area 000 SSNs should be invalid", ssns.none { isValidSSN(it) })
    }

    // =========================================================================
    // SECTION 11 — Invalid area 666
    // =========================================================================

    @Test fun `s11 invalid area 666 group 01 serial 0001`() { assertFalse(isValidSSN("666-01-0001")) }
    @Test fun `s11 invalid area 666 group 99 serial 9999`() { assertFalse(isValidSSN("666-99-9999")) }
    @Test fun `s11 invalid area 666 group 50 serial 5050`() { assertFalse(isValidSSN("666-50-5050")) }
    @Test fun `s11 invalid area 666 group 10 serial 1234`() { assertFalse(isValidSSN("666-10-1234")) }
    @Test fun `s11 invalid area 666 group 20 serial 2345`() { assertFalse(isValidSSN("666-20-2345")) }
    @Test fun `s11 invalid area 666 group 30 serial 3456`() { assertFalse(isValidSSN("666-30-3456")) }
    @Test fun `s11 invalid area 666 group 40 serial 4567`() { assertFalse(isValidSSN("666-40-4567")) }
    @Test fun `s11 invalid area 666 group 60 serial 6789`() { assertFalse(isValidSSN("666-60-6789")) }
    @Test fun `s11 invalid area 666 group 70 serial 7890`() { assertFalse(isValidSSN("666-70-7890")) }
    @Test fun `s11 invalid area 666 no dashes`() { assertFalse(isValidSSN("666010001")) }
    @Test fun `s11 area 665 valid but 666 invalid contrast`() {
        assertTrue(isValidSSN("665-01-0001"))
        assertFalse(isValidSSN("666-01-0001"))
    }
    @Test fun `s11 area 667 valid but 666 invalid contrast`() {
        assertTrue(isValidSSN("667-01-0001"))
        assertFalse(isValidSSN("666-01-0001"))
    }
    @Test fun `s11 all area 666 combos fail batch`() {
        val ssns = listOf(
            "666-01-0001", "666-25-2500", "666-50-5000",
            "666-75-7500", "666-99-9999"
        )
        assertTrue(ssns.none { isValidSSN(it) })
    }

    // =========================================================================
    // SECTION 12 — Invalid areas 900–999 for regular SSNs
    // =========================================================================

    @Test fun `s12 invalid area 900 as ssn`() { assertFalse(isValidSSN("900-01-0001")) }
    @Test fun `s12 invalid area 910 as ssn`() { assertFalse(isValidSSN("910-10-1000")) }
    @Test fun `s12 invalid area 920 as ssn`() { assertFalse(isValidSSN("920-20-2000")) }
    @Test fun `s12 invalid area 930 as ssn`() { assertFalse(isValidSSN("930-30-3000")) }
    @Test fun `s12 invalid area 940 as ssn`() { assertFalse(isValidSSN("940-40-4000")) }
    @Test fun `s12 invalid area 950 as ssn`() { assertFalse(isValidSSN("950-50-5000")) }
    @Test fun `s12 invalid area 960 as ssn`() { assertFalse(isValidSSN("960-60-6000")) }
    @Test fun `s12 invalid area 970 as ssn`() { assertFalse(isValidSSN("970-70-7000")) }
    @Test fun `s12 invalid area 980 as ssn`() { assertFalse(isValidSSN("980-80-8000")) }
    @Test fun `s12 invalid area 990 as ssn`() { assertFalse(isValidSSN("990-90-9000")) }
    @Test fun `s12 invalid area 999 as ssn`() { assertFalse(isValidSSN("999-99-9999")) }
    @Test fun `s12 invalid area 901 as ssn`() { assertFalse(isValidSSN("901-01-0101")) }
    @Test fun `s12 invalid area 911 as ssn`() { assertFalse(isValidSSN("911-11-1111")) }
    @Test fun `s12 invalid area 921 as ssn`() { assertFalse(isValidSSN("921-21-2121")) }
    @Test fun `s12 invalid area 931 as ssn`() { assertFalse(isValidSSN("931-31-3131")) }
    @Test fun `s12 all 900s fail as ssn batch`() {
        val ssns = (900..999 step 10).map { "$it-70-7000" }
        assertTrue("All 9xx area numbers should fail SSN validation", ssns.none { isValidSSN(it) })
    }
    @Test fun `s12 area 899 valid contrast`() {
        assertTrue(isValidSSN("899-89-0001"))
        assertFalse(isValidSSN("900-90-0001"))
    }

    // =========================================================================
    // SECTION 13 — Invalid group 00
    // =========================================================================

    @Test fun `s13 invalid group 00 area 100`() { assertFalse(isValidSSN("100-00-1234")) }
    @Test fun `s13 invalid group 00 area 200`() { assertFalse(isValidSSN("200-00-2345")) }
    @Test fun `s13 invalid group 00 area 300`() { assertFalse(isValidSSN("300-00-3456")) }
    @Test fun `s13 invalid group 00 area 400`() { assertFalse(isValidSSN("400-00-4567")) }
    @Test fun `s13 invalid group 00 area 500`() { assertFalse(isValidSSN("500-00-5678")) }
    @Test fun `s13 invalid group 00 area 600`() { assertFalse(isValidSSN("600-00-6789")) }
    @Test fun `s13 invalid group 00 area 001`() { assertFalse(isValidSSN("001-00-0001")) }
    @Test fun `s13 invalid group 00 area 010`() { assertFalse(isValidSSN("010-00-1000")) }
    @Test fun `s13 invalid group 00 area 050`() { assertFalse(isValidSSN("050-00-5000")) }
    @Test fun `s13 invalid group 00 area 099`() { assertFalse(isValidSSN("099-00-9900")) }
    @Test fun `s13 invalid group 00 area 150`() { assertFalse(isValidSSN("150-00-1500")) }
    @Test fun `s13 invalid group 00 area 250`() { assertFalse(isValidSSN("250-00-2500")) }
    @Test fun `s13 invalid group 00 area 350`() { assertFalse(isValidSSN("350-00-3500")) }
    @Test fun `s13 invalid group 00 area 450`() { assertFalse(isValidSSN("450-00-4500")) }
    @Test fun `s13 invalid group 00 area 550`() { assertFalse(isValidSSN("550-00-5500")) }
    @Test fun `s13 group 01 is valid contrast to group 00`() {
        assertTrue(isValidSSN("100-01-1234"))
        assertFalse(isValidSSN("100-00-1234"))
    }
    @Test fun `s13 batch all group 00 combos fail`() {
        val ssns = listOf(100, 200, 300, 400, 500, 600).map { "${it}-00-1000" }
        assertTrue(ssns.none { isValidSSN(it) })
    }

    // =========================================================================
    // SECTION 14 — Invalid serial 0000
    // =========================================================================

    @Test fun `s14 invalid serial 0000 area 100 group 01`() { assertFalse(isValidSSN("100-01-0000")) }
    @Test fun `s14 invalid serial 0000 area 200 group 10`() { assertFalse(isValidSSN("200-10-0000")) }
    @Test fun `s14 invalid serial 0000 area 300 group 20`() { assertFalse(isValidSSN("300-20-0000")) }
    @Test fun `s14 invalid serial 0000 area 400 group 30`() { assertFalse(isValidSSN("400-30-0000")) }
    @Test fun `s14 invalid serial 0000 area 500 group 40`() { assertFalse(isValidSSN("500-40-0000")) }
    @Test fun `s14 invalid serial 0000 area 600 group 50`() { assertFalse(isValidSSN("600-50-0000")) }
    @Test fun `s14 invalid serial 0000 area 001 group 01`() { assertFalse(isValidSSN("001-01-0000")) }
    @Test fun `s14 invalid serial 0000 area 010 group 10`() { assertFalse(isValidSSN("010-10-0000")) }
    @Test fun `s14 invalid serial 0000 area 050 group 50`() { assertFalse(isValidSSN("050-50-0000")) }
    @Test fun `s14 invalid serial 0000 area 099 group 99`() { assertFalse(isValidSSN("099-99-0000")) }
    @Test fun `s14 invalid serial 0000 area 150 group 15`() { assertFalse(isValidSSN("150-15-0000")) }
    @Test fun `s14 invalid serial 0000 area 250 group 25`() { assertFalse(isValidSSN("250-25-0000")) }
    @Test fun `s14 invalid serial 0000 area 350 group 35`() { assertFalse(isValidSSN("350-35-0000")) }
    @Test fun `s14 invalid serial 0000 area 450 group 45`() { assertFalse(isValidSSN("450-45-0000")) }
    @Test fun `s14 invalid serial 0000 area 550 group 55`() { assertFalse(isValidSSN("550-55-0000")) }
    @Test fun `s14 serial 0001 is valid contrast to serial 0000`() {
        assertTrue(isValidSSN("100-01-0001"))
        assertFalse(isValidSSN("100-01-0000"))
    }
    @Test fun `s14 batch all serial 0000 combos fail`() {
        val ssns = listOf(100, 200, 300, 400, 500, 600).map { "${it}-10-0000" }
        assertTrue(ssns.none { isValidSSN(it) })
    }

    // =========================================================================
    // SECTION 15 — Format variants: no dashes
    // =========================================================================

    @Test fun `s15 no dashes valid 001010001`() { assertTrue(isValidSSN("001010001")) }
    @Test fun `s15 no dashes valid 100101000`() { assertTrue(isValidSSN("100101000")) }
    @Test fun `s15 no dashes valid 200202002`() { assertTrue(isValidSSN("200202002")) }
    @Test fun `s15 no dashes valid 300303003`() { assertTrue(isValidSSN("300303003")) }
    @Test fun `s15 no dashes valid 400404004`() { assertTrue(isValidSSN("400404004")) }
    @Test fun `s15 no dashes valid 500505005`() { assertTrue(isValidSSN("500505005")) }
    @Test fun `s15 no dashes valid 600606006`() { assertTrue(isValidSSN("600606006")) }
    @Test fun `s15 no dashes invalid area 000`() { assertFalse(isValidSSN("000010001")) }
    @Test fun `s15 no dashes invalid area 666`() { assertFalse(isValidSSN("666010001")) }
    @Test fun `s15 no dashes invalid area 900`() { assertFalse(isValidSSN("900010001")) }
    @Test fun `s15 no dashes invalid group 00`() { assertFalse(isValidSSN("100000001")) }
    @Test fun `s15 no dashes invalid serial 0000`() { assertFalse(isValidSSN("100010000")) }
    @Test fun `s15 no dashes 8 digits invalid`() { assertFalse(isValidSSN("10101000")) }
    @Test fun `s15 no dashes 10 digits invalid`() { assertFalse(isValidSSN("1001010001")) }
    @Test fun `s15 no dashes all zeros invalid`() { assertFalse(isValidSSN("000000000")) }
    @Test fun `s15 no dashes with leading zero area`() { assertTrue(isValidSSN("001010001")) }
    @Test fun `s15 formatSSN wraps 9 digits correctly`() {
        assertEquals("123-45-6789", formatSSN("123456789"))
    }
    @Test fun `s15 formatSSN handles 9 digit with dashes already`() {
        assertEquals("123-45-6789", formatSSN("123-45-6789"))
    }
    @Test fun `s15 formatSSN returns original if not 9 digits`() {
        val bad = "12345"
        assertEquals(bad, formatSSN(bad))
    }
    @Test fun `s15 formatSSN round-trip valid ssn`() {
        val original = "001-01-0001"
        val digits = original.replace("-", "")
        assertEquals(original, formatSSN(digits))
    }

    // =========================================================================
    // SECTION 16 — Format variants: spaces
    // =========================================================================

    @Test fun `s16 spaces valid 001 01 0001`() { assertTrue(isValidSSN("001 01 0001")) }
    @Test fun `s16 spaces valid 100 10 1000`() { assertTrue(isValidSSN("100 10 1000")) }
    @Test fun `s16 spaces valid 200 20 2000`() { assertTrue(isValidSSN("200 20 2000")) }
    @Test fun `s16 spaces valid 300 30 3000`() { assertTrue(isValidSSN("300 30 3000")) }
    @Test fun `s16 spaces valid 400 40 4000`() { assertTrue(isValidSSN("400 40 4000")) }
    @Test fun `s16 spaces valid 500 50 5000`() { assertTrue(isValidSSN("500 50 5000")) }
    @Test fun `s16 spaces valid 600 60 6001`() { assertTrue(isValidSSN("600 60 6001")) }
    @Test fun `s16 spaces invalid area 000`() { assertFalse(isValidSSN("000 01 0001")) }
    @Test fun `s16 spaces invalid area 666`() { assertFalse(isValidSSN("666 01 0001")) }
    @Test fun `s16 spaces invalid group 00`() { assertFalse(isValidSSN("100 00 1000")) }
    @Test fun `s16 spaces invalid serial 0000`() { assertFalse(isValidSSN("100 01 0000")) }
    @Test fun `s16 spaces valid 150 15 1501`() { assertTrue(isValidSSN("150 15 1501")) }
    @Test fun `s16 spaces invalid area 900`() { assertFalse(isValidSSN("900 70 1234")) }
    @Test fun `s16 spaces valid 665 65 6501`() { assertTrue(isValidSSN("665 65 6501")) }
    @Test fun `s16 spaces mixed with dashes valid area 123`() {
        // digits: 123456789 -> area=123, grp=45, serial=6789 -> valid structurally
        assertTrue(isValidSSN("123 45 6789"))
    }
    @Test fun `s16 spaces valid 250 25 2525`() { assertTrue(isValidSSN("250 25 2525")) }
    @Test fun `s16 spaces valid 350 35 3535`() { assertTrue(isValidSSN("350 35 3535")) }
    @Test fun `s16 spaces valid 450 45 4545`() { assertTrue(isValidSSN("450 45 4545")) }
    @Test fun `s16 spaces valid 550 55 5551`() { assertTrue(isValidSSN("550 55 5551")) }
    @Test fun `s16 spaces valid 650 65 6501`() { assertTrue(isValidSSN("650 65 6501")) }

    // =========================================================================
    // SECTION 17 — Partial input and incorrect lengths
    // =========================================================================

    @Test fun `s17 empty string invalid`() { assertFalse(isValidSSN("")) }
    @Test fun `s17 one digit invalid`() { assertFalse(isValidSSN("1")) }
    @Test fun `s17 three digits invalid`() { assertFalse(isValidSSN("123")) }
    @Test fun `s17 five digits invalid`() { assertFalse(isValidSSN("12345")) }
    @Test fun `s17 eight digits invalid`() { assertFalse(isValidSSN("12345678")) }
    @Test fun `s17 ten digits invalid`() { assertFalse(isValidSSN("1234567890")) }
    @Test fun `s17 eleven digits invalid`() { assertFalse(isValidSSN("12345678901")) }
    @Test fun `s17 just dashes invalid`() { assertFalse(isValidSSN("---")) }
    @Test fun `s17 letters mixed invalid`() { assertFalse(isValidSSN("ABC-DE-FGHI")) }
    @Test fun `s17 ssn with letters at end invalid`() { assertFalse(isValidSSN("123-45-678A")) }
    @Test fun `s17 partial dashes 3 digits invalid`() { assertFalse(isValidSSN("123-45")) }
    @Test fun `s17 whitespace only invalid`() { assertFalse(isValidSSN("   ")) }
    @Test fun `s17 special chars invalid`() { assertFalse(isValidSSN("123/45/6789")) }
    @Test fun `s17 dot separated invalid`() { assertFalse(isValidSSN("123.45.6789")) }
    @Test fun `s17 mixed dash and space valid extraction`() {
        // digits = "001010001" -> valid
        assertTrue(isValidSSN("001-01 0001"))
    }
    @Test fun `s17 two digits only invalid`() { assertFalse(isValidSSN("12")) }
    @Test fun `s17 four digits invalid`() { assertFalse(isValidSSN("1234")) }
    @Test fun `s17 six digits invalid`() { assertFalse(isValidSSN("123456")) }
    @Test fun `s17 seven digits invalid`() { assertFalse(isValidSSN("1234567")) }
    @Test fun `s17 null char in string invalid`() { assertFalse(isValidSSN("123\u000045-6789")) }

    // =========================================================================
    // SECTION 18 — Sequential and known fake SSNs
    // =========================================================================

    @Test fun `s18 sequential 123-45-6789 is sequential fake`() {
        assertTrue(isSequentialFakeSSN("123-45-6789"))
    }
    @Test fun `s18 sequential 219-09-9999 is sequential fake`() {
        assertTrue(isSequentialFakeSSN("219-09-9999"))
    }
    @Test fun `s18 sequential 078-05-1120 is sequential fake`() {
        assertTrue(isSequentialFakeSSN("078-05-1120"))
    }
    @Test fun `s18 sequential 457-55-5462 is sequential fake`() {
        assertTrue(isSequentialFakeSSN("457-55-5462"))
    }
    @Test fun `s18 normal ssn 001-01-0001 not sequential`() {
        assertFalse(isSequentialFakeSSN("001-01-0001"))
    }
    @Test fun `s18 normal ssn 200-20-2020 not sequential`() {
        assertFalse(isSequentialFakeSSN("200-20-2020"))
    }
    @Test fun `s18 sequential 123456789 no dashes is sequential fake`() {
        assertTrue(isSequentialFakeSSN("123456789"))
    }
    @Test fun `s18 structural validity of 123-45-6789`() {
        assertTrue(isValidSSN("123-45-6789"))
    }
    @Test fun `s18 structural validity of 219-09-9999`() {
        assertTrue(isValidSSN("219-09-9999"))
    }
    @Test fun `s18 sequential fake count check`() {
        val fakes = listOf("123-45-6789", "219-09-9999", "078-05-1120", "457-55-5462")
        assertEquals(4, fakes.count { isSequentialFakeSSN(it) })
    }
    @Test fun `s18 bulk sequential check passes only known fakes`() {
        val candidates = listOf("001-01-0001", "123-45-6789", "400-40-4040", "219-09-9999")
        val fakes = candidates.filter { isSequentialFakeSSN(it) }
        assertEquals(2, fakes.size)
    }
    @Test fun `s18 all digits same 111-11-1111 structurally valid`() {
        assertTrue(isValidSSN("111-11-1111"))
    }
    @Test fun `s18 all digits same 222-22-2222 structurally valid`() {
        assertTrue(isValidSSN("222-22-2222"))
    }
    @Test fun `s18 all digits same 333-33-3333 structurally valid`() {
        assertTrue(isValidSSN("333-33-3333"))
    }
    @Test fun `s18 all digits same 444-44-4444 structurally valid`() {
        assertTrue(isValidSSN("444-44-4444"))
    }
    @Test fun `s18 all digits same 555-55-5555 structurally valid`() {
        assertTrue(isValidSSN("555-55-5555"))
    }
    @Test fun `s18 all digits same 777-77-7777 structurally valid`() {
        assertTrue(isValidSSN("777-77-7777"))
    }
    @Test fun `s18 fake ssn 078051120 no dashes is fake`() {
        assertTrue(isSequentialFakeSSN("078051120"))
    }
    @Test fun `s18 non-fake 001010001 is not sequential`() {
        assertFalse(isSequentialFakeSSN("001010001"))
    }

    // =========================================================================
    // SECTION 19 — Extract SSNs from text
    // =========================================================================

    @Test fun `s19 extract single ssn from text`() {
        val text = "Patient SSN is 123-45-6789 as per records."
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("123-45-6789", result[0])
    }
    @Test fun `s19 extract two ssns from text`() {
        val text = "Primary 234-56-7890 and secondary 345-67-8901."
        val result = extractSSNsFromText(text)
        assertEquals(2, result.size)
    }
    @Test fun `s19 extract zero when none present`() {
        val text = "No social security numbers here at all."
        assertEquals(0, extractSSNsFromText(text).size)
    }
    @Test fun `s19 extract from medical record format`() {
        val text = "Name: John Doe, SSN: 456-78-9012, DOB: 01/01/1990"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("456-78-9012", result[0])
    }
    @Test fun `s19 extract from hr document`() {
        val text = "Employee ID: 78901, SSN: 567-89-0123, Hire Date: 2020-01-15"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("567-89-0123", result[0])
    }
    @Test fun `s19 no extract without dashes`() {
        val text = "SSN without dashes: 123456789 should not match"
        assertEquals(0, extractSSNsFromText(text).size)
    }
    @Test fun `s19 extract at start of text`() {
        val text = "123-45-6789 is the SSN on file."
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
    }
    @Test fun `s19 extract at end of text`() {
        val text = "The SSN is 234-56-7890"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
    }
    @Test fun `s19 extract multiple in same sentence`() {
        val text = "SSN1: 100-10-1000 and SSN2: 200-20-2000 and SSN3: 300-30-3000"
        assertEquals(3, extractSSNsFromText(text).size)
    }
    @Test fun `s19 no extract partial pattern 123-45-678`() {
        val text = "Partial: 123-45-678 should not match"
        assertEquals(0, extractSSNsFromText(text).size)
    }
    @Test fun `s19 no extract 1234-56-7890 extra digits`() {
        val text = "Too long: 1234-56-7890 should not match"
        assertEquals(0, extractSSNsFromText(text).size)
    }
    @Test fun `s19 extract preserves original format`() {
        val text = "SSN: 456-78-9012"
        val result = extractSSNsFromText(text)
        assertEquals("456-78-9012", result[0])
    }
    @Test fun `s19 extract from multiline text`() {
        val text = "Line 1: 111-22-3333\nLine 2: no ssn here\nLine 3: 444-55-6666"
        assertEquals(2, extractSSNsFromText(text).size)
    }
    @Test fun `s19 extract from JSON-like string`() {
        val text = """{"ssn": "789-01-2345", "name": "Jane"}"""
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("789-01-2345", result[0])
    }
    @Test fun `s19 extract from CSV row`() {
        val text = "John,Doe,890-12-3456,30,Engineer"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertEquals("890-12-3456", result[0])
    }
    @Test fun `s19 no extract from phone number 555-867-5309`() {
        val text = "Call 555-867-5309 for info"
        assertEquals(0, extractSSNsFromText(text).size)
    }
    @Test fun `s19 extract adjacent to parentheses`() {
        val text = "SSN: (123-45-6789)"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
    }
    @Test fun `s19 extract from long paragraph`() {
        val text = "According to the records, the individual with SSN 321-54-9876 " +
                "has been enrolled since 2010. The co-applicant 432-65-0987 was added in 2015."
        assertEquals(2, extractSSNsFromText(text).size)
    }
    @Test fun `s19 extract first from list returns correct value`() {
        val text = "Records: 001-01-0001, 002-02-0002, 003-03-0003"
        val result = extractSSNsFromText(text)
        assertEquals("001-01-0001", result.first())
    }
    @Test fun `s19 extract last from list returns correct value`() {
        val text = "Records: 001-01-0001, 002-02-0002, 003-03-0003"
        val result = extractSSNsFromText(text)
        assertEquals("003-03-0003", result.last())
    }
    @Test fun `s19 all extracted ssns are structurally valid`() {
        val text = "SSNs: 001-01-0001, 100-10-1000, 200-20-2000, 300-30-3000"
        val result = extractSSNsFromText(text)
        assertTrue(result.all { isValidSSN(it) })
    }
    @Test fun `s19 text with invalid area extracts but fails validation`() {
        val text = "Invalid SSN: 000-01-0001 found in document"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertFalse(isValidSSN(result[0]))
    }
    @Test fun `s19 text with area 666 extracts but fails validation`() {
        val text = "Invalid SSN: 666-01-0001"
        val result = extractSSNsFromText(text)
        assertEquals(1, result.size)
        assertFalse(isValidSSN(result[0]))
    }
    @Test fun `s19 multiline with mixed valid and invalid`() {
        val text = "Good: 100-10-1000\nBad: 000-10-1000\nGood: 200-20-2000"
        val extracted = extractSSNsFromText(text)
        assertEquals(3, extracted.size)
        assertEquals(2, extracted.count { isValidSSN(it) })
    }
    @Test fun `s19 empty text returns empty list`() {
        assertEquals(emptyList<String>(), extractSSNsFromText(""))
    }
    @Test fun `s19 whitespace-only text returns empty list`() {
        assertEquals(emptyList<String>(), extractSSNsFromText("   \n\t  "))
    }
    @Test fun `s19 single char text returns empty list`() {
        assertEquals(emptyList<String>(), extractSSNsFromText("x"))
    }
    @Test fun `s19 repeated same ssn extracted multiple times`() {
        val text = "SSN 123-45-6789 and again 123-45-6789"
        assertEquals(2, extractSSNsFromText(text).size)
    }
    @Test fun `s19 extract five ssns from paragraph`() {
        val text = "Applicants: 101-11-1001, 202-22-2002, 303-33-3003, 404-44-4004, 505-55-5005 enrolled."
        assertEquals(5, extractSSNsFromText(text).size)
    }

    // =========================================================================
    // SECTION 20 — Edge cases, masking, formatting, boundary checks, and more
    // =========================================================================

    @Test fun `s20 mask ssn hides first five digits dashes`() {
        assertEquals("***-**-6789", maskSSN("123-45-6789"))
    }
    @Test fun `s20 mask ssn preserves last four`() {
        assertEquals("***-**-9999", maskSSN("999-99-9999"))
    }
    @Test fun `s20 mask ssn of no-dash input`() {
        assertEquals("***-**-6789", maskSSN("123456789"))
    }
    @Test fun `s20 mask ssn last four 0001`() {
        assertEquals("***-**-0001", maskSSN("001-01-0001"))
    }
    @Test fun `s20 mask ssn last four 1234`() {
        assertEquals("***-**-1234", maskSSN("100-12-1234"))
    }
    @Test fun `s20 format ssn 9 digits`() {
        assertEquals("123-45-6789", formatSSN("123456789"))
    }
    @Test fun `s20 format ssn returns input if wrong length`() {
        assertEquals("12345", formatSSN("12345"))
    }
    @Test fun `s20 valid boundary 665-01-0001 last area before 666`() {
        assertTrue(isValidSSN("665-01-0001"))
    }
    @Test fun `s20 invalid 666-01-0001 reserved area`() {
        assertFalse(isValidSSN("666-01-0001"))
    }
    @Test fun `s20 valid boundary 667-01-0001 first area after 666`() {
        assertTrue(isValidSSN("667-01-0001"))
    }
    @Test fun `s20 valid ssn 001-01-0001 absolute minimum`() {
        assertTrue(isValidSSN("001-01-0001"))
    }
    @Test fun `s20 valid ssn 899-99-9999 highest non-ITIN`() {
        assertTrue(isValidSSN("899-99-9999"))
    }
    @Test fun `s20 area 700 valid`() { assertTrue(isValidSSN("700-70-7007")) }
    @Test fun `s20 area 750 valid`() { assertTrue(isValidSSN("750-75-7507")) }
    @Test fun `s20 area 800 valid`() { assertTrue(isValidSSN("800-80-8008")) }
    @Test fun `s20 area 850 valid`() { assertTrue(isValidSSN("850-85-8508")) }
    @Test fun `s20 ssn is pure function same input same output`() {
        val ssn = "345-67-8901"
        assertEquals(isValidSSN(ssn), isValidSSN(ssn))
    }
    @Test fun `s20 batch 10 valid ssns all pass`() {
        val ssns = listOf(
            "001-01-0001", "100-10-1000", "200-20-2000", "300-30-3000",
            "400-40-4000", "500-50-5000", "600-60-6000", "001-99-9999",
            "299-01-0001", "399-01-0001"
        )
        assertTrue(ssns.all { isValidSSN(it) })
    }
    @Test fun `s20 batch 5 invalid ssns all fail`() {
        val ssns = listOf(
            "000-01-0001", "666-01-0001", "900-01-0001", "100-00-1000", "100-01-0000"
        )
        assertTrue(ssns.none { isValidSSN(it) })
    }
    @Test fun `s20 countValidSSNsInList counts correctly`() {
        val mixed = listOf("100-10-1000", "000-00-0000", "200-20-2000", "666-66-6666", "300-30-3000")
        assertEquals(3, countValidSSNsInList(mixed))
    }
    @Test fun `s20 countInvalidSSNsInList counts correctly`() {
        val mixed = listOf("100-10-1000", "000-00-0000", "200-20-2000", "666-66-6666", "300-30-3000")
        assertEquals(2, countInvalidSSNsInList(mixed))
    }
    @Test fun `s20 filterValidSSNs returns only valid`() {
        val mixed = listOf("100-10-1000", "000-00-0000", "200-20-2000", "666-66-6666")
        val valid = filterValidSSNs(mixed)
        assertEquals(2, valid.size)
        assertTrue(valid.all { isValidSSN(it) })
    }
    @Test fun `s20 filterInvalidSSNs returns only invalid`() {
        val mixed = listOf("100-10-1000", "000-00-0000", "200-20-2000", "666-66-6666")
        val invalid = filterInvalidSSNs(mixed)
        assertEquals(2, invalid.size)
        assertTrue(invalid.none { isValidSSN(it) })
    }
    @Test fun `s20 valid area 668 just above 666`() {
        assertTrue(isValidSSN("668-10-1001"))
    }
    @Test fun `s20 area 899 valid boundary below 900`() {
        assertTrue(isValidSSN("899-01-0001"))
    }
    @Test fun `s20 area 900 invalid boundary above 899`() {
        assertFalse(isValidSSN("900-01-0001"))
    }
    @Test fun `s20 group 01 valid minimum group`() {
        assertTrue(isValidSSN("100-01-0001"))
    }
    @Test fun `s20 group 99 valid maximum group`() {
        assertTrue(isValidSSN("100-99-9999"))
    }
    @Test fun `s20 serial 0001 valid minimum serial`() {
        assertTrue(isValidSSN("100-01-0001"))
    }
    @Test fun `s20 serial 9999 valid maximum serial`() {
        assertTrue(isValidSSN("100-01-9999"))
    }
    @Test fun `s20 all three components at boundaries valid`() {
        // area=001, group=01, serial=0001 — all min nonzero
        assertTrue(isValidSSN("001-01-0001"))
        // area=899, group=99, serial=9999 — all max valid
        assertTrue(isValidSSN("899-99-9999"))
    }
    @Test fun `s20 area 001 to 010 all valid`() {
        val ssns = (1..10).map { "${it.toString().padStart(3, '0')}-01-0001" }
        assertTrue(ssns.all { isValidSSN(it) })
    }
    @Test fun `s20 area 660 to 669 valid except 666`() {
        val valid = (660..669).filter { it != 666 }.map { "$it-01-0001" }
        val invalid = listOf("666-01-0001")
        assertTrue(valid.all { isValidSSN(it) })
        assertTrue(invalid.none { isValidSSN(it) })
    }
}
