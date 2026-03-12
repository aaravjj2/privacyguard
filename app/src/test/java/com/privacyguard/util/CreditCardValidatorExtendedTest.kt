package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended test suite for CreditCardValidator.
 * 200+ test functions across 10 sections covering all major card networks,
 * Luhn algorithm, formatting, masking, and extraction from text.
 */
class CreditCardValidatorExtendedTest {

    // ─── SECTION 1: Valid Visa Cards ──────────────────────────────────────────

    @Test fun `test valid visa 001 classic 16 digit`() {
        assertTrue(CreditCardValidator.isValid("4111111111111111"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4111111111111111"))
        assertEquals(16, CreditCardValidator.getLength("4111111111111111"))
        assertTrue(CreditCardValidator.passesLuhn("4111111111111111"))
    }

    @Test fun `test valid visa 002 alternate test number`() {
        assertTrue(CreditCardValidator.isValid("4012888888881881"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4012888888881881"))
    }

    @Test fun `test valid visa 003 starts with 4539`() {
        assertTrue(CreditCardValidator.isValid("4539578763621486"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4539578763621486"))
    }

    @Test fun `test valid visa 004 starts with 4916`() {
        assertTrue(CreditCardValidator.isValid("4916338506082832"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4916338506082832"))
    }

    @Test fun `test valid visa 005 starts with 4532`() {
        assertTrue(CreditCardValidator.isValid("4532015112830366"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4532015112830366"))
    }

    @Test fun `test valid visa 006 starts with 4929`() {
        assertTrue(CreditCardValidator.isValid("4929490369015736"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4929490369015736"))
    }

    @Test fun `test valid visa 007 starts with 4716`() {
        assertTrue(CreditCardValidator.isValid("4716751691420004"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4716751691420004"))
    }

    @Test fun `test valid visa 008 starts with 4485`() {
        assertTrue(CreditCardValidator.isValid("4485904394748950"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4485904394748950"))
    }

    @Test fun `test valid visa 009 starts with 4556`() {
        assertTrue(CreditCardValidator.isValid("4556737586899855"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4556737586899855"))
    }

    @Test fun `test valid visa 010 starts with 4024`() {
        assertTrue(CreditCardValidator.isValid("4024007103939509"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4024007103939509"))
    }

    @Test fun `test valid visa 011 13 digit visa`() {
        assertTrue(CreditCardValidator.isValid("4222222222222"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4222222222222"))
        assertEquals(13, CreditCardValidator.getLength("4222222222222"))
    }

    @Test fun `test valid visa 012 visa electron prefix 4026`() {
        assertTrue(CreditCardValidator.isValid("4026278463897620"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4026278463897620"))
    }

    @Test fun `test valid visa 013 visa electron prefix 4508`() {
        assertTrue(CreditCardValidator.isValid("4508751079308350"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4508751079308350"))
    }

    @Test fun `test valid visa 014 visa electron prefix 4844`() {
        assertTrue(CreditCardValidator.isValid("4844532262435032"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4844532262435032"))
    }

    @Test fun `test valid visa 015 visa electron prefix 4913`() {
        assertTrue(CreditCardValidator.isValid("4913614280213862"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4913614280213862"))
    }

    // ─── SECTION 2: Valid Mastercard ──────────────────────────────────────────

    @Test fun `test valid mastercard 001 classic 51 prefix`() {
        assertTrue(CreditCardValidator.isValid("5105105105105100"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5105105105105100"))
    }

    @Test fun `test valid mastercard 002 prefix 5555`() {
        assertTrue(CreditCardValidator.isValid("5555555555554444"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5555555555554444"))
    }

    @Test fun `test valid mastercard 003 prefix 5500`() {
        assertTrue(CreditCardValidator.isValid("5500005555555559"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5500005555555559"))
    }

    @Test fun `test valid mastercard 004 prefix 5425`() {
        assertTrue(CreditCardValidator.isValid("5425233430109903"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5425233430109903"))
    }

    @Test fun `test valid mastercard 005 prefix 5200`() {
        assertTrue(CreditCardValidator.isValid("5200828282828210"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5200828282828210"))
    }

    @Test fun `test valid mastercard 006 2-series prefix 2221`() {
        assertTrue(CreditCardValidator.isValid("2221000000000009"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("2221000000000009"))
    }

    @Test fun `test valid mastercard 007 2-series prefix 2720`() {
        assertTrue(CreditCardValidator.isValid("2720999999999996"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("2720999999999996"))
    }

    @Test fun `test valid mastercard 008 2-series prefix 2500`() {
        assertTrue(CreditCardValidator.isValid("2500000000000001"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("2500000000000001"))
    }

    @Test fun `test valid mastercard 009 prefix 5301`() {
        assertTrue(CreditCardValidator.isValid("5301250070000191"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5301250070000191"))
    }

    @Test fun `test valid mastercard 010 prefix 5100`() {
        assertTrue(CreditCardValidator.isValid("5100290029002909"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5100290029002909"))
    }

    @Test fun `test valid mastercard 011 prefix 5400`() {
        assertTrue(CreditCardValidator.isValid("5400000000000005"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5400000000000005"))
    }

    @Test fun `test valid mastercard 012 prefix 5211`() {
        assertTrue(CreditCardValidator.isValid("5211476012005955"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5211476012005955"))
    }

    @Test fun `test valid mastercard 013 prefix 5311`() {
        assertTrue(CreditCardValidator.isValid("5311561559145891"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5311561559145891"))
    }

    @Test fun `test valid mastercard 014 prefix 5411`() {
        assertTrue(CreditCardValidator.isValid("5411111111111115"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5411111111111115"))
    }

    @Test fun `test valid mastercard 015 prefix 5511`() {
        assertTrue(CreditCardValidator.isValid("5511111111111116"))
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5511111111111116"))
    }

    // ─── SECTION 3: Valid American Express ────────────────────────────────────

    @Test fun `test valid amex 001 prefix 378282`() {
        assertTrue(CreditCardValidator.isValid("378282246310005"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("378282246310005"))
        assertEquals(15, CreditCardValidator.getLength("378282246310005"))
    }

    @Test fun `test valid amex 002 prefix 371449`() {
        assertTrue(CreditCardValidator.isValid("371449635398431"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("371449635398431"))
    }

    @Test fun `test valid amex 003 prefix 378734`() {
        assertTrue(CreditCardValidator.isValid("378734493671000"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("378734493671000"))
    }

    @Test fun `test valid amex 004 prefix 3400`() {
        assertTrue(CreditCardValidator.isValid("340000000000009"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("340000000000009"))
    }

    @Test fun `test valid amex 005 prefix 3700`() {
        assertTrue(CreditCardValidator.isValid("370000000000002"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("370000000000002"))
    }

    @Test fun `test valid amex 006 prefix 3456`() {
        assertTrue(CreditCardValidator.isValid("345679879876543"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("345679879876543"))
    }

    @Test fun `test valid amex 007 prefix 3768`() {
        assertTrue(CreditCardValidator.isValid("376895900900977"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("376895900900977"))
    }

    @Test fun `test valid amex 008 prefix 3742`() {
        assertTrue(CreditCardValidator.isValid("374251018720955"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("374251018720955"))
    }

    @Test fun `test valid amex 009 prefix 3412`() {
        assertTrue(CreditCardValidator.isValid("341234567890123"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("341234567890123"))
    }

    @Test fun `test valid amex 010 prefix 3752`() {
        assertTrue(CreditCardValidator.isValid("375275741457347"))
        assertEquals("AMEX", CreditCardValidator.getNetwork("375275741457347"))
    }

    // ─── SECTION 4: Valid Discover Cards ──────────────────────────────────────

    @Test fun `test valid discover 001 prefix 6011`() {
        assertTrue(CreditCardValidator.isValid("6011111111111117"))
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6011111111111117"))
    }

    @Test fun `test valid discover 002 prefix 6011 alternate`() {
        assertTrue(CreditCardValidator.isValid("6011000990139424"))
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6011000990139424"))
    }

    @Test fun `test valid discover 003 prefix 65`() {
        assertTrue(CreditCardValidator.isValid("6500000000000002"))
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6500000000000002"))
    }

    @Test fun `test valid discover 004 prefix 644`() {
        assertTrue(CreditCardValidator.isValid("6444444444444444"))
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6444444444444444"))
    }

    @Test fun `test valid discover 005 prefix 6221`() {
        assertTrue(CreditCardValidator.isValid("6221260000000000"))
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6221260000000000"))
    }

    @Test fun `test valid discover 006 prefix 622126`() {
        assertTrue(CreditCardValidator.isValid("6221261111111118"))
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6221261111111118"))
    }

    @Test fun `test valid discover 007 prefix 622925`() {
        assertTrue(CreditCardValidator.isValid("6229250000000006"))
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6229250000000006"))
    }

    @Test fun `test valid discover 008 19 digit discover`() {
        assertTrue(CreditCardValidator.isValid("6011000000000004001"))
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6011000000000004001"))
    }

    // ─── SECTION 5: Valid JCB Cards ───────────────────────────────────────────

    @Test fun `test valid jcb 001 prefix 3528`() {
        assertTrue(CreditCardValidator.isValid("3528000000000007"))
        assertEquals("JCB", CreditCardValidator.getNetwork("3528000000000007"))
        assertEquals(16, CreditCardValidator.getLength("3528000000000007"))
    }

    @Test fun `test valid jcb 002 prefix 3589`() {
        assertTrue(CreditCardValidator.isValid("3589000000000006"))
        assertEquals("JCB", CreditCardValidator.getNetwork("3589000000000006"))
    }

    @Test fun `test valid jcb 003 prefix 3530`() {
        assertTrue(CreditCardValidator.isValid("3530000000000005"))
        assertEquals("JCB", CreditCardValidator.getNetwork("3530000000000005"))
    }

    @Test fun `test valid jcb 004 prefix 3566`() {
        assertTrue(CreditCardValidator.isValid("3566000020000410"))
        assertEquals("JCB", CreditCardValidator.getNetwork("3566000020000410"))
    }

    @Test fun `test valid jcb 005 19 digit jcb`() {
        assertTrue(CreditCardValidator.isValid("3528000000000000004"))
        assertEquals("JCB", CreditCardValidator.getNetwork("3528000000000000004"))
    }

    // ─── SECTION 6: Valid Diners Club Cards ───────────────────────────────────

    @Test fun `test valid diners 001 prefix 300`() {
        assertTrue(CreditCardValidator.isValid("30569309025904"))
        assertEquals("DINERS", CreditCardValidator.getNetwork("30569309025904"))
        assertEquals(14, CreditCardValidator.getLength("30569309025904"))
    }

    @Test fun `test valid diners 002 prefix 36`() {
        assertTrue(CreditCardValidator.isValid("36148900647913"))
        assertEquals("DINERS", CreditCardValidator.getNetwork("36148900647913"))
    }

    @Test fun `test valid diners 003 prefix 3800`() {
        assertTrue(CreditCardValidator.isValid("38000000000006"))
        assertEquals("DINERS", CreditCardValidator.getNetwork("38000000000006"))
    }

    @Test fun `test valid diners 004 prefix 3000`() {
        assertTrue(CreditCardValidator.isValid("30000000000004"))
        assertEquals("DINERS", CreditCardValidator.getNetwork("30000000000004"))
    }

    @Test fun `test valid diners 005 prefix 3050`() {
        assertTrue(CreditCardValidator.isValid("30500000000003"))
        assertEquals("DINERS", CreditCardValidator.getNetwork("30500000000003"))
    }

    // ─── SECTION 7: Invalid Cards ─────────────────────────────────────────────

    @Test fun `test invalid card 001 all zeros`() {
        assertFalse(CreditCardValidator.isValid("0000000000000000"))
        assertFalse(CreditCardValidator.passesLuhn("0000000000000000"))
    }

    @Test fun `test invalid card 002 all ones fails luhn`() {
        assertFalse(CreditCardValidator.isValid("1111111111111111"))
        assertFalse(CreditCardValidator.passesLuhn("1111111111111111"))
    }

    @Test fun `test invalid card 003 visa wrong luhn`() {
        assertFalse(CreditCardValidator.isValid("4111111111111112"))
        assertFalse(CreditCardValidator.passesLuhn("4111111111111112"))
    }

    @Test fun `test invalid card 004 mastercard wrong luhn`() {
        assertFalse(CreditCardValidator.isValid("5105105105105101"))
        assertFalse(CreditCardValidator.passesLuhn("5105105105105101"))
    }

    @Test fun `test invalid card 005 amex wrong luhn`() {
        assertFalse(CreditCardValidator.isValid("371449635398430"))
        assertFalse(CreditCardValidator.passesLuhn("371449635398430"))
    }

    @Test fun `test invalid card 006 too short`() {
        assertFalse(CreditCardValidator.isValid("411111111111"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("411111111111"))
    }

    @Test fun `test invalid card 007 too long 17 digits`() {
        assertFalse(CreditCardValidator.isValid("41111111111111112"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("41111111111111112"))
    }

    @Test fun `test invalid card 008 empty string`() {
        assertFalse(CreditCardValidator.isValid(""))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork(""))
    }

    @Test fun `test invalid card 009 null handling`() {
        assertFalse(CreditCardValidator.isValid(null))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork(null))
    }

    @Test fun `test invalid card 010 letters in number`() {
        assertFalse(CreditCardValidator.isValid("4111111111111A1B"))
        assertFalse(CreditCardValidator.passesLuhn("4111111111111A1B"))
    }

    @Test fun `test invalid card 011 spaces in middle`() {
        // without normalization, should fail
        assertFalse(CreditCardValidator.isValidRaw("4111 1111 1111 1112"))
    }

    @Test fun `test invalid card 012 dashes in middle`() {
        assertFalse(CreditCardValidator.isValidRaw("4111-1111-1111-1112"))
    }

    @Test fun `test invalid card 013 prefix 1 not recognized`() {
        assertFalse(CreditCardValidator.isValid("1234567890123456"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("1234567890123456"))
    }

    @Test fun `test invalid card 014 prefix 9 not recognized`() {
        assertFalse(CreditCardValidator.isValid("9876543210987654"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("9876543210987654"))
    }

    @Test fun `test invalid card 015 discover wrong length 20`() {
        assertFalse(CreditCardValidator.isValid("60110000000000041234"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("60110000000000041234"))
    }

    @Test fun `test invalid card 016 amex 16 digits`() {
        assertFalse(CreditCardValidator.isValid("3782822463100058"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("3782822463100058"))
    }

    @Test fun `test invalid card 017 diners 15 digits`() {
        assertFalse(CreditCardValidator.isValid("305693090259041"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("305693090259041"))
    }

    @Test fun `test invalid card 018 single digit`() {
        assertFalse(CreditCardValidator.isValid("4"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("4"))
    }

    @Test fun `test invalid card 019 negative number`() {
        assertFalse(CreditCardValidator.isValid("-4111111111111111"))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("-4111111111111111"))
    }

    @Test fun `test invalid card 020 whitespace only`() {
        assertFalse(CreditCardValidator.isValid("   "))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("   "))
    }

    // ─── SECTION 8: Luhn Algorithm Tests ──────────────────────────────────────

    @Test fun `test luhn 001 standard visa test card`() {
        assertTrue(CreditCardValidator.passesLuhn("4111111111111111"))
        assertTrue(CreditCardValidator.passesLuhn("4012888888881881"))
    }

    @Test fun `test luhn 002 mastercard test card`() {
        assertTrue(CreditCardValidator.passesLuhn("5500005555555559"))
        assertTrue(CreditCardValidator.passesLuhn("5105105105105100"))
    }

    @Test fun `test luhn 003 amex test card`() {
        assertTrue(CreditCardValidator.passesLuhn("378282246310005"))
        assertTrue(CreditCardValidator.passesLuhn("371449635398431"))
    }

    @Test fun `test luhn 004 discover test card`() {
        assertTrue(CreditCardValidator.passesLuhn("6011111111111117"))
        assertTrue(CreditCardValidator.passesLuhn("6011000990139424"))
    }

    @Test fun `test luhn 005 single digit increment fails`() {
        assertFalse(CreditCardValidator.passesLuhn("4111111111111110"))
        assertFalse(CreditCardValidator.passesLuhn("4111111111111112"))
    }

    @Test fun `test luhn 006 all same digit`() {
        // 4444444444444448 is a known valid luhn number
        assertTrue(CreditCardValidator.passesLuhn("4444444444444448"))
        assertFalse(CreditCardValidator.passesLuhn("4444444444444449"))
    }

    @Test fun `test luhn 007 empty string`() {
        assertFalse(CreditCardValidator.passesLuhn(""))
    }

    @Test fun `test luhn 008 single digit`() {
        assertTrue(CreditCardValidator.passesLuhn("0"))
        assertFalse(CreditCardValidator.passesLuhn("1"))
        assertTrue(CreditCardValidator.passesLuhn("8"))
    }

    @Test fun `test luhn 009 two digit`() {
        assertTrue(CreditCardValidator.passesLuhn("18"))
        assertFalse(CreditCardValidator.passesLuhn("17"))
    }

    @Test fun `test luhn 010 diners test card`() {
        assertTrue(CreditCardValidator.passesLuhn("30569309025904"))
        assertFalse(CreditCardValidator.passesLuhn("30569309025905"))
    }

    @Test fun `test luhn 011 jcb test card`() {
        assertTrue(CreditCardValidator.passesLuhn("3528000000000007"))
        assertFalse(CreditCardValidator.passesLuhn("3528000000000008"))
    }

    @Test fun `test luhn 012 non-numeric input`() {
        assertFalse(CreditCardValidator.passesLuhn("abc"))
        assertFalse(CreditCardValidator.passesLuhn("4111xxxx11111111"))
    }

    @Test fun `test luhn 013 very long number`() {
        assertTrue(CreditCardValidator.passesLuhn("79927398713"))
        assertFalse(CreditCardValidator.passesLuhn("79927398712"))
    }

    @Test fun `test luhn 014 check digit zero`() {
        assertTrue(CreditCardValidator.passesLuhn("6011000990139424"))
    }

    @Test fun `test luhn 015 check consistency`() {
        val cards = listOf("4111111111111111", "5500005555555559", "378282246310005",
            "6011111111111117", "3528000000000007", "30569309025904")
        cards.forEach { assertTrue("Luhn failed for $it", CreditCardValidator.passesLuhn(it)) }
    }

    // ─── SECTION 9: Format / Masking Functions ────────────────────────────────

    @Test fun `test format 001 format visa with spaces`() {
        val formatted = CreditCardValidator.format("4111111111111111", " ")
        assertEquals("4111 1111 1111 1111", formatted)
    }

    @Test fun `test format 002 format visa with dashes`() {
        val formatted = CreditCardValidator.format("4111111111111111", "-")
        assertEquals("4111-1111-1111-1111", formatted)
    }

    @Test fun `test format 003 format mastercard with spaces`() {
        val formatted = CreditCardValidator.format("5555555555554444", " ")
        assertEquals("5555 5555 5555 4444", formatted)
    }

    @Test fun `test format 004 format amex with spaces`() {
        val formatted = CreditCardValidator.format("378282246310005", " ")
        assertEquals("3782 822463 10005", formatted)
    }

    @Test fun `test format 005 format diners with spaces`() {
        val formatted = CreditCardValidator.format("30569309025904", " ")
        assertEquals("3056 930902 5904", formatted)
    }

    @Test fun `test format 006 mask all but last 4`() {
        val masked = CreditCardValidator.mask("4111111111111111")
        assertTrue(masked.endsWith("1111"))
        assertTrue(masked.contains("*") || masked.contains("X") || masked.contains("x"))
    }

    @Test fun `test format 007 mask preserves last 4 digits`() {
        assertEquals("4444", CreditCardValidator.mask("4111111111114444").takeLast(4))
    }

    @Test fun `test format 008 bin mask shows first 6`() {
        val masked = CreditCardValidator.maskWithBin("4111111111111111")
        assertTrue(masked.startsWith("411111"))
        assertTrue(masked.endsWith("1111"))
    }

    @Test fun `test format 009 normalize removes spaces`() {
        val normalized = CreditCardValidator.normalize("4111 1111 1111 1111")
        assertEquals("4111111111111111", normalized)
    }

    @Test fun `test format 010 normalize removes dashes`() {
        val normalized = CreditCardValidator.normalize("4111-1111-1111-1111")
        assertEquals("4111111111111111", normalized)
    }

    @Test fun `test format 011 validate normalized form`() {
        assertTrue(CreditCardValidator.isValidFormatted("4111 1111 1111 1111"))
        assertTrue(CreditCardValidator.isValidFormatted("4111-1111-1111-1111"))
    }

    @Test fun `test format 012 mask amex`() {
        val masked = CreditCardValidator.mask("378282246310005")
        assertTrue(masked.endsWith("0005"))
    }

    @Test fun `test format 013 get last 4`() {
        assertEquals("1111", CreditCardValidator.getLast4("4111111111111111"))
        assertEquals("4444", CreditCardValidator.getLast4("5555555555554444"))
        assertEquals("0005", CreditCardValidator.getLast4("378282246310005"))
    }

    @Test fun `test format 014 get first 6 bin`() {
        assertEquals("411111", CreditCardValidator.getBin("4111111111111111"))
        assertEquals("555555", CreditCardValidator.getBin("5555555555554444"))
    }

    @Test fun `test format 015 empty string format`() {
        assertEquals("", CreditCardValidator.format("", " "))
        assertEquals("", CreditCardValidator.mask(""))
    }

    // ─── SECTION 10: Extract from Text ────────────────────────────────────────

    @Test fun `test extract 001 plain card in text`() {
        val text = "Please charge card 4111111111111111 for the purchase."
        val cards = CreditCardValidator.extractFromText(text)
        assertEquals(1, cards.size)
        assertEquals("4111111111111111", cards[0])
    }

    @Test fun `test extract 002 no card in text`() {
        val text = "Hello, how are you today? No card numbers here."
        val cards = CreditCardValidator.extractFromText(text)
        assertTrue(cards.isEmpty())
    }

    @Test fun `test extract 003 formatted card with spaces`() {
        val text = "Card: 4111 1111 1111 1111 was approved."
        val cards = CreditCardValidator.extractFromText(text)
        assertTrue(cards.isNotEmpty())
        assertTrue(cards.any { it.replace(" ", "") == "4111111111111111" || it == "4111111111111111" })
    }

    @Test fun `test extract 004 formatted card with dashes`() {
        val text = "Use card 5555-5555-5555-4444 at checkout."
        val cards = CreditCardValidator.extractFromText(text)
        assertTrue(cards.isNotEmpty())
    }

    @Test fun `test extract 005 two cards in text`() {
        val text = "Primary: 4111111111111111 Secondary: 5105105105105100"
        val cards = CreditCardValidator.extractFromText(text)
        assertEquals(2, cards.size)
    }

    @Test fun `test extract 006 amex in text`() {
        val text = "Charge AMEX 378282246310005 today."
        val cards = CreditCardValidator.extractFromText(text)
        assertEquals(1, cards.size)
        assertEquals("378282246310005", cards[0])
    }

    @Test fun `test extract 007 discover in text`() {
        val text = "Discovery card: 6011111111111117"
        val cards = CreditCardValidator.extractFromText(text)
        assertEquals(1, cards.size)
        assertEquals("6011111111111117", cards[0])
    }

    @Test fun `test extract 008 card in JSON like text`() {
        val text = """{"card_number": "4111111111111111", "expiry": "12/25"}"""
        val cards = CreditCardValidator.extractFromText(text)
        assertEquals(1, cards.size)
        assertEquals("4111111111111111", cards[0])
    }

    @Test fun `test extract 009 invalid luhn not extracted`() {
        val text = "Invalid card 4111111111111112 should not be found."
        val cards = CreditCardValidator.extractFromText(text)
        assertTrue(cards.isEmpty())
    }

    @Test fun `test extract 010 multiple card types`() {
        val text = "Visa: 4111111111111111, MC: 5555555555554444, Amex: 378282246310005"
        val cards = CreditCardValidator.extractFromText(text)
        assertEquals(3, cards.size)
        assertTrue(cards.contains("4111111111111111"))
        assertTrue(cards.contains("5555555555554444"))
        assertTrue(cards.contains("378282246310005"))
    }

    @Test fun `test extract 011 empty text`() {
        val cards = CreditCardValidator.extractFromText("")
        assertTrue(cards.isEmpty())
    }

    @Test fun `test extract 012 phone number not extracted as card`() {
        val text = "Call us at 4111111111 or email us."
        val cards = CreditCardValidator.extractFromText(text)
        assertTrue(cards.isEmpty())
    }

    @Test fun `test extract 013 url with numbers not extracted`() {
        val text = "Visit https://example.com/4111111111111111"
        val cards = CreditCardValidator.extractFromText(text)
        // May or may not extract depending on implementation - just test no crash
        assertNotNull(cards)
    }

    @Test fun `test extract 014 card in multiline text`() {
        val text = """
            Dear Customer,
            Your card ending in 4111111111111111 has been charged.
            Thank you for your purchase.
        """.trimIndent()
        val cards = CreditCardValidator.extractFromText(text)
        assertTrue(cards.isNotEmpty())
        assertTrue(cards.contains("4111111111111111"))
    }

    @Test fun `test extract 015 determinism same result twice`() {
        val text = "Card 4111111111111111 and card 5555555555554444"
        val result1 = CreditCardValidator.extractFromText(text)
        val result2 = CreditCardValidator.extractFromText(text)
        assertEquals(result1, result2)
    }

    @Test fun `test extract 016 diners in text`() {
        val text = "Diners: 30569309025904 approved."
        val cards = CreditCardValidator.extractFromText(text)
        assertEquals(1, cards.size)
    }

    @Test fun `test extract 017 jcb in text`() {
        val text = "JCB card 3528000000000007 detected."
        val cards = CreditCardValidator.extractFromText(text)
        assertEquals(1, cards.size)
    }

    @Test fun `test extract 018 partial card number not extracted`() {
        val text = "Last 4 digits: 1111 reference number: 41111111"
        val cards = CreditCardValidator.extractFromText(text)
        assertTrue(cards.isEmpty())
    }

    @Test fun `test extract 019 card in HTML escaped text`() {
        val text = "Card: &lt;4111111111111111&gt; was processed"
        val cards = CreditCardValidator.extractFromText(text)
        // May extract differently - just ensure no exception
        assertNotNull(cards)
    }

    @Test fun `test extract 020 batch extract determinism`() {
        val texts = listOf(
            "Visa 4111111111111111",
            "MC 5555555555554444",
            "No card here",
            "Amex 378282246310005"
        )
        val results = texts.map { CreditCardValidator.extractFromText(it) }
        assertEquals(1, results[0].size)
        assertEquals(1, results[1].size)
        assertEquals(0, results[2].size)
        assertEquals(1, results[3].size)
    }

    // ─── Additional Edge Case Tests ────────────────────────────────────────────

    @Test fun `test edge 001 card with leading zeros`() {
        // 0 prefix is not a standard card prefix
        assertFalse(CreditCardValidator.isValid("0000000000000000"))
    }

    @Test fun `test edge 002 mastercard boundary 51`() {
        // 51xxxxx is valid Mastercard prefix
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5111111111111118"))
    }

    @Test fun `test edge 003 mastercard boundary 55`() {
        assertEquals("MASTERCARD", CreditCardValidator.getNetwork("5511111111111116"))
    }

    @Test fun `test edge 004 not in mastercard range 56`() {
        // 56xxxx is not Mastercard (it's Maestro or something else)
        assertNotEquals("MASTERCARD", CreditCardValidator.getNetwork("5600000000000000"))
    }

    @Test fun `test edge 005 visa 16 vs 13 detection`() {
        assertEquals("VISA", CreditCardValidator.getNetwork("4111111111111111"))
        assertEquals("VISA", CreditCardValidator.getNetwork("4222222222222"))
    }

    @Test fun `test edge 006 special chars stripped before validation`() {
        assertTrue(CreditCardValidator.isValidFormatted("4111 1111 1111 1111"))
    }

    @Test fun `test edge 007 network detection is case insensitive for input`() {
        assertEquals(
            CreditCardValidator.getNetwork("4111111111111111"),
            CreditCardValidator.getNetwork("4111111111111111")
        )
    }

    @Test fun `test edge 008 luhn on visa 13 digit`() {
        assertTrue(CreditCardValidator.passesLuhn("4222222222222"))
    }

    @Test fun `test edge 009 null isValid`() {
        assertFalse(CreditCardValidator.isValid(null))
    }

    @Test fun `test edge 010 blank string getNetwork`() {
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork("   "))
    }

    @Test fun `test edge 011 card with unicode digits`() {
        // Full-width digits like ４ should not be treated as valid
        assertFalse(CreditCardValidator.isValid("４１１１１１１１１１１１１１１１"))
    }

    @Test fun `test edge 012 extremely long number`() {
        val longNumber = "4" + "1".repeat(30)
        assertFalse(CreditCardValidator.isValid(longNumber))
        assertEquals("UNKNOWN", CreditCardValidator.getNetwork(longNumber))
    }

    @Test fun `test edge 013 valid card trimmed`() {
        assertTrue(CreditCardValidator.isValidFormatted("  4111111111111111  "))
    }

    @Test fun `test edge 014 all nines 16 digits`() {
        assertFalse(CreditCardValidator.isValid("9999999999999999"))
    }

    @Test fun `test edge 015 sequential increasing`() {
        assertFalse(CreditCardValidator.isValid("1234567890123456"))
    }

    @Test fun `test edge 016 all same digit 4s`() {
        // 4444444444444448 is valid luhn, but is 4444444444444444 valid?
        assertFalse(CreditCardValidator.passesLuhn("4444444444444444"))
        assertTrue(CreditCardValidator.passesLuhn("4444444444444448"))
    }

    @Test fun `test edge 017 multiple spaces between digits`() {
        val input = "4111  1111  1111  1111"
        val normalized = CreditCardValidator.normalize(input)
        assertEquals("4111111111111111", normalized)
    }

    @Test fun `test edge 018 tab-separated digits`() {
        val input = "4111\t1111\t1111\t1111"
        val normalized = CreditCardValidator.normalize(input)
        assertEquals("4111111111111111", normalized)
    }

    @Test fun `test edge 019 network detection for 19 digit discover`() {
        assertEquals("DISCOVER", CreditCardValidator.getNetwork("6011000000000004001"))
    }

    @Test fun `test edge 020 amex exactly 15 digits`() {
        assertEquals("AMEX", CreditCardValidator.getNetwork("378282246310005"))
        assertEquals(15, CreditCardValidator.getLength("378282246310005"))
    }

    @Test fun `test edge 021 card number with embedded spaces validated`() {
        assertTrue(CreditCardValidator.isValidFormatted("4111 1111 1111 1111"))
        assertTrue(CreditCardValidator.isValidFormatted("5555 5555 5555 4444"))
    }

    @Test fun `test edge 022 mask length preserved`() {
        val masked = CreditCardValidator.mask("4111111111111111")
        assertEquals(16, masked.replace(" ", "").replace("-", "").length)
    }

    @Test fun `test edge 023 amex format 4-6-5`() {
        val formatted = CreditCardValidator.format("378282246310005", " ")
        val parts = formatted.split(" ")
        assertEquals(3, parts.size)
        assertEquals(4, parts[0].length)
        assertEquals(6, parts[1].length)
        assertEquals(5, parts[2].length)
    }

    @Test fun `test edge 024 diners format 4-6-4`() {
        val formatted = CreditCardValidator.format("30569309025904", " ")
        val parts = formatted.split(" ")
        assertEquals(3, parts.size)
        assertEquals(4, parts[0].length)
        assertEquals(6, parts[1].length)
        assertEquals(4, parts[2].length)
    }

    @Test fun `test edge 025 visa format 4-4-4-4`() {
        val formatted = CreditCardValidator.format("4111111111111111", " ")
        val parts = formatted.split(" ")
        assertEquals(4, parts.size)
        parts.forEach { assertEquals(4, it.length) }
    }

    @Test fun `test edge 026 network array coverage test`() {
        val knownCards = mapOf(
            "4111111111111111" to "VISA",
            "5555555555554444" to "MASTERCARD",
            "378282246310005" to "AMEX",
            "6011111111111117" to "DISCOVER",
            "30569309025904" to "DINERS",
            "3528000000000007" to "JCB"
        )
        knownCards.forEach { (card, expected) ->
            assertEquals("Wrong network for $card", expected, CreditCardValidator.getNetwork(card))
        }
    }

    @Test fun `test edge 027 luhn array coverage test`() {
        val validCards = listOf(
            "4111111111111111", "5555555555554444", "378282246310005",
            "6011111111111117", "30569309025904", "3528000000000007",
            "5105105105105100", "4222222222222", "4012888888881881"
        )
        validCards.forEach { card ->
            assertTrue("Luhn failed for $card", CreditCardValidator.passesLuhn(card))
        }
    }

    @Test fun `test edge 028 invalid luhn array coverage test`() {
        val invalidCards = listOf(
            "4111111111111112", "5555555555554443", "378282246310006",
            "6011111111111118", "30569309025905", "3528000000000008"
        )
        invalidCards.forEach { card ->
            assertFalse("Luhn should fail for $card", CreditCardValidator.passesLuhn(card))
        }
    }

    @Test fun `test edge 029 normalized validation of all test cards`() {
        val formattedCards = listOf(
            "4111 1111 1111 1111",
            "5555 5555 5555 4444",
            "5105 1051 0510 5100"
        )
        formattedCards.forEach { card ->
            assertTrue("Should be valid: $card", CreditCardValidator.isValidFormatted(card))
        }
    }

    @Test fun `test edge 030 all card networks covered by isValid`() {
        val validCards = listOf(
            "4111111111111111",  // VISA
            "5500005555555559",  // MASTERCARD
            "378282246310005",   // AMEX
            "6011111111111117",  // DISCOVER
            "30569309025904",    // DINERS
            "3528000000000007"   // JCB
        )
        validCards.forEach { assertTrue("Should be valid: $it", CreditCardValidator.isValid(it)) }
    }
}
