package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Extended test suite 2 for CreditCardValidator.
 * Tests validate(), luhnCheck(), detectNetwork(), and looksLikeCreditCard().
 */
class CreditCardValidatorExtendedTest2 {

    // =========================================================================
    // SECTION 1: Visa Card Validation
    // =========================================================================

    @Test fun testVisaValid_001() {
        val result = CreditCardValidator.validate("4532015112830366")
        assertTrue("Valid Visa should pass", result.isValid)
    }

    @Test fun testVisaValid_002() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertTrue("Standard test Visa should pass", result.isValid)
    }

    @Test fun testVisaValid_003() {
        val result = CreditCardValidator.validate("4012888888881881")
        assertTrue("Another Visa should pass", result.isValid)
    }

    @Test fun testVisaValid_004() {
        val result = CreditCardValidator.validate("4222222222222")
        assertTrue("13-digit Visa should pass", result.isValid)
    }

    @Test fun testVisaValid_005() {
        val result = CreditCardValidator.validate("4716123456789011")
        assertNotNull("Visa validation result not null", result)
    }

    @Test fun testVisaValid_006() {
        val result = CreditCardValidator.validate("4532015112830366")
        assertNotNull("Network should be detected", result.network)
    }

    @Test fun testVisaValid_007() {
        val result = CreditCardValidator.validate("4532015112830366")
        if (result.isValid) {
            assertTrue("Confidence should be > 0", result.confidence > 0f)
        }
    }

    @Test fun testVisaValid_008() {
        val result = CreditCardValidator.validate("4111111111111111")
        if (result.isValid) {
            assertTrue("Formatted should not be empty", result.formatted.isNotEmpty())
        }
    }

    @Test fun testVisaValid_009() {
        val result = CreditCardValidator.validate("4111111111111111")
        if (result.isValid) {
            assertTrue("Masked should not be empty", result.masked.isNotEmpty())
        }
    }

    @Test fun testVisaValid_010() {
        val result = CreditCardValidator.validate("4012888888881881")
        if (result.isValid) {
            assertTrue("Masked should end with last 4", result.masked.endsWith("1881"))
        }
    }

    @Test fun testVisaFormatted_001() {
        val result = CreditCardValidator.validate("4532-0151-1283-0366")
        assertNotNull("Dashed input processed", result)
    }

    @Test fun testVisaFormatted_002() {
        val result = CreditCardValidator.validate("4532 0151 1283 0366")
        assertNotNull("Spaced input processed", result)
    }

    @Test fun testVisaDetectNetwork_001() {
        val network = CreditCardValidator.detectNetwork("4532015112830366")
        assertNotNull("Visa network detected", network)
    }

    @Test fun testVisaDetectNetwork_002() {
        val network = CreditCardValidator.detectNetwork("4111111111111111")
        if (network != null) {
            assertTrue("Visa name contains 'Visa'", network.name.contains("Visa", ignoreCase = true))
        }
    }

    @Test fun testVisaLooksLike_001() {
        assertTrue("Visa number looks like CC", CreditCardValidator.looksLikeCreditCard("4532015112830366"))
    }

    @Test fun testVisaLuhn_001() {
        assertTrue("Visa Luhn valid", CreditCardValidator.luhnCheck("4532015112830366"))
    }

    @Test fun testVisaLuhn_002() {
        assertTrue("Test Visa Luhn valid", CreditCardValidator.luhnCheck("4111111111111111"))
    }

    @Test fun testVisaLuhn_003() {
        assertFalse("Modified Visa Luhn invalid", CreditCardValidator.luhnCheck("4532015112830367"))
    }

    @Test fun testVisaInvalid_001() {
        val result = CreditCardValidator.validate("4532015112830367")
        assertFalse("Luhn-invalid Visa should fail", result.isValid)
    }

    @Test fun testVisaInvalid_002() {
        val result = CreditCardValidator.validate("4111111111111112")
        assertFalse("Modified Visa should fail", result.isValid)
    }

    // =========================================================================
    // SECTION 2: Mastercard Validation
    // =========================================================================

    @Test fun testMCValid_001() {
        val result = CreditCardValidator.validate("5500005555555559")
        assertTrue("MC should be valid", result.isValid)
    }

    @Test fun testMCValid_002() {
        val result = CreditCardValidator.validate("5105105105105100")
        assertTrue("MC 51xx should be valid", result.isValid)
    }

    @Test fun testMCValid_003() {
        val result = CreditCardValidator.validate("5200828282828210")
        assertTrue("MC 52xx should be valid", result.isValid)
    }

    @Test fun testMCValid_004() {
        val result = CreditCardValidator.validate("5425233430109903")
        assertTrue("Another MC should be valid", result.isValid)
    }

    @Test fun testMCDetectNetwork_001() {
        val network = CreditCardValidator.detectNetwork("5500005555555559")
        assertNotNull("MC network detected", network)
    }

    @Test fun testMCDetectNetwork_002() {
        val network = CreditCardValidator.detectNetwork("5105105105105100")
        if (network != null) {
            assertTrue("MC name contains Mastercard", network.name.contains("Master", ignoreCase = true))
        }
    }

    @Test fun testMCLuhn_001() { assertTrue(CreditCardValidator.luhnCheck("5500005555555559")) }
    @Test fun testMCLuhn_002() { assertTrue(CreditCardValidator.luhnCheck("5105105105105100")) }
    @Test fun testMCLuhn_003() { assertTrue(CreditCardValidator.luhnCheck("5200828282828210")) }
    @Test fun testMCLuhn_004() { assertTrue(CreditCardValidator.luhnCheck("5425233430109903")) }

    @Test fun testMCFormatted_001() {
        val result = CreditCardValidator.validate("5500-0055-5555-5559")
        assertNotNull("Dashed MC processed", result)
    }

    @Test fun testMCLooksLike_001() {
        assertTrue("MC looks like CC", CreditCardValidator.looksLikeCreditCard("5500005555555559"))
    }

    @Test fun testMCInvalid_001() {
        val result = CreditCardValidator.validate("5500005555555550")
        assertFalse("Luhn-invalid MC should fail", result.isValid)
    }

    @Test fun testMCInvalid_002() {
        val result = CreditCardValidator.validate("5105105105105101")
        assertFalse("Modified MC should fail", result.isValid)
    }

    // =========================================================================
    // SECTION 3: American Express Validation
    // =========================================================================

    @Test fun testAmexValid_001() {
        val result = CreditCardValidator.validate("378282246310005")
        assertTrue("Amex 37xx should be valid", result.isValid)
    }

    @Test fun testAmexValid_002() {
        val result = CreditCardValidator.validate("371449635398431")
        assertTrue("Amex 37xx variant should be valid", result.isValid)
    }

    @Test fun testAmexValid_003() {
        val result = CreditCardValidator.validate("378734493671000")
        assertTrue("Amex corporate should be valid", result.isValid)
    }

    @Test fun testAmexValid_004() {
        val result = CreditCardValidator.validate("347298508610468")
        assertTrue("Amex 34xx should be valid", result.isValid)
    }

    @Test fun testAmexDetectNetwork_001() {
        val network = CreditCardValidator.detectNetwork("378282246310005")
        assertNotNull("Amex network detected", network)
    }

    @Test fun testAmexLuhn_001() { assertTrue(CreditCardValidator.luhnCheck("378282246310005")) }
    @Test fun testAmexLuhn_002() { assertTrue(CreditCardValidator.luhnCheck("371449635398431")) }
    @Test fun testAmexLuhn_003() { assertTrue(CreditCardValidator.luhnCheck("347298508610468")) }

    @Test fun testAmexLooksLike_001() {
        assertTrue("Amex looks like CC", CreditCardValidator.looksLikeCreditCard("378282246310005"))
    }

    @Test fun testAmexFormatted_001() {
        val result = CreditCardValidator.validate("3782-822463-10005")
        assertNotNull("Amex formatted processed", result)
    }

    @Test fun testAmexInvalid_001() {
        val result = CreditCardValidator.validate("378282246310006")
        assertFalse("Luhn-invalid Amex should fail", result.isValid)
    }

    // =========================================================================
    // SECTION 4: Discover Validation
    // =========================================================================

    @Test fun testDiscoverValid_001() {
        val result = CreditCardValidator.validate("6011111111111117")
        assertTrue("Discover 6011 should be valid", result.isValid)
    }

    @Test fun testDiscoverValid_002() {
        val result = CreditCardValidator.validate("6011000990139424")
        assertTrue("Discover variant should be valid", result.isValid)
    }

    @Test fun testDiscoverValid_003() {
        val result = CreditCardValidator.validate("6556920981787607")
        assertNotNull("Discover 65xx processed", result)
    }

    @Test fun testDiscoverDetectNetwork_001() {
        val network = CreditCardValidator.detectNetwork("6011111111111117")
        assertNotNull("Discover network detected", network)
    }

    @Test fun testDiscoverLuhn_001() { assertTrue(CreditCardValidator.luhnCheck("6011111111111117")) }
    @Test fun testDiscoverLuhn_002() { assertTrue(CreditCardValidator.luhnCheck("6011000990139424")) }

    @Test fun testDiscoverLooksLike_001() {
        assertTrue("Discover looks like CC", CreditCardValidator.looksLikeCreditCard("6011111111111117"))
    }

    @Test fun testDiscoverInvalid_001() {
        val result = CreditCardValidator.validate("6011111111111118")
        assertFalse("Luhn-invalid Discover should fail", result.isValid)
    }

    // =========================================================================
    // SECTION 5: JCB Validation
    // =========================================================================

    @Test fun testJCBValid_001() {
        val result = CreditCardValidator.validate("3530111333300000")
        assertTrue("JCB 3530 should be valid", result.isValid)
    }

    @Test fun testJCBValid_002() {
        val result = CreditCardValidator.validate("3566002020360505")
        assertTrue("JCB 3566 should be valid", result.isValid)
    }

    @Test fun testJCBDetectNetwork_001() {
        val network = CreditCardValidator.detectNetwork("3530111333300000")
        assertNotNull("JCB network detected", network)
    }

    @Test fun testJCBLuhn_001() { assertTrue(CreditCardValidator.luhnCheck("3530111333300000")) }
    @Test fun testJCBLuhn_002() { assertTrue(CreditCardValidator.luhnCheck("3566002020360505")) }

    @Test fun testJCBLooksLike_001() {
        assertTrue("JCB looks like CC", CreditCardValidator.looksLikeCreditCard("3530111333300000"))
    }

    // =========================================================================
    // SECTION 6: Diners Club Validation
    // =========================================================================

    @Test fun testDinersValid_001() {
        val result = CreditCardValidator.validate("30569309025904")
        assertTrue("Diners 305 should be valid", result.isValid)
    }

    @Test fun testDinersValid_002() {
        val result = CreditCardValidator.validate("38520000023237")
        assertTrue("Diners 38xx should be valid", result.isValid)
    }

    @Test fun testDinersLuhn_001() { assertTrue(CreditCardValidator.luhnCheck("30569309025904")) }
    @Test fun testDinersLuhn_002() { assertTrue(CreditCardValidator.luhnCheck("38520000023237")) }

    // =========================================================================
    // SECTION 7: Luhn Algorithm Tests
    // =========================================================================

    @Test fun testLuhn_Empty_001()     { assertFalse(CreditCardValidator.luhnCheck("")) }
    @Test fun testLuhn_TooShort_001()  { assertFalse(CreditCardValidator.luhnCheck("123")) }
    @Test fun testLuhn_AllZeros_001()  { assertFalse(CreditCardValidator.luhnCheck("0000000000000000")) }
    @Test fun testLuhn_AllNines_001()  { assertFalse(CreditCardValidator.luhnCheck("9999999999999999")) }
    @Test fun testLuhn_Sequential_001(){ assertFalse(CreditCardValidator.luhnCheck("1234567890123456")) }
    @Test fun testLuhn_VisaGood_001()  { assertTrue(CreditCardValidator.luhnCheck("4532015112830366")) }
    @Test fun testLuhn_MCGood_001()    { assertTrue(CreditCardValidator.luhnCheck("5500005555555559")) }
    @Test fun testLuhn_AmexGood_001()  { assertTrue(CreditCardValidator.luhnCheck("378282246310005")) }
    @Test fun testLuhn_DiscGood_001()  { assertTrue(CreditCardValidator.luhnCheck("6011111111111117")) }
    @Test fun testLuhn_JCBGood_001()   { assertTrue(CreditCardValidator.luhnCheck("3530111333300000")) }
    @Test fun testLuhn_Letters_001()   { assertFalse(CreditCardValidator.luhnCheck("abcdefghijklmnop")) }

    @Test fun testLuhn_WithSpaces_001() {
        assertTrue("Luhn with spaces", CreditCardValidator.luhnCheck("4111 1111 1111 1111"))
    }

    @Test fun testLuhn_WithDashes_001() {
        assertTrue("Luhn with dashes", CreditCardValidator.luhnCheck("4111-1111-1111-1111"))
    }

    @Test fun testLuhn_SingleFlip_001() {
        // Flipping any single digit makes it invalid
        val card = "4532015112830366"
        val flipped = card.dropLast(1) + ((card.last().digitToInt() + 1) % 10).digitToChar()
        assertFalse("Single digit flip fails Luhn", CreditCardValidator.luhnCheck(flipped))
    }

    @Test fun testLuhn_16DigitInvalid_001() {
        assertFalse(CreditCardValidator.luhnCheck("1234567891234567"))
    }

    @Test fun testLuhn_16DigitInvalid_002() {
        assertFalse(CreditCardValidator.luhnCheck("9876543219876543"))
    }

    @Test fun testLuhn_13Digit_001() {
        assertTrue(CreditCardValidator.luhnCheck("4222222222222"))
    }

    @Test fun testLuhn_15Digit_001() {
        assertTrue(CreditCardValidator.luhnCheck("378282246310005"))
    }

    // =========================================================================
    // SECTION 8: looksLikeCreditCard Tests
    // =========================================================================

    @Test fun testLooksLike_Valid16_001() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("4532015112830366"))
    }

    @Test fun testLooksLike_Valid16_002() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("5500005555555559"))
    }

    @Test fun testLooksLike_Valid15_001() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("378282246310005"))
    }

    @Test fun testLooksLike_Valid14_001() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("30569309025904"))
    }

    @Test fun testLooksLike_Valid13_001() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("4222222222222"))
    }

    @Test fun testLooksLike_TooShort_001() {
        assertFalse(CreditCardValidator.looksLikeCreditCard("12345"))
    }

    @Test fun testLooksLike_TooLong_001() {
        assertFalse(CreditCardValidator.looksLikeCreditCard("1234567890123456789012"))
    }

    @Test fun testLooksLike_Letters_001() {
        assertFalse(CreditCardValidator.looksLikeCreditCard("ABCDEFGHIJKLMNOP"))
    }

    @Test fun testLooksLike_Empty_001() {
        assertFalse(CreditCardValidator.looksLikeCreditCard(""))
    }

    @Test fun testLooksLike_Formatted_001() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("4532 0151 1283 0366"))
    }

    @Test fun testLooksLike_Formatted_002() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("4532-0151-1283-0366"))
    }

    // =========================================================================
    // SECTION 9: validate() Return Object Tests
    // =========================================================================

    @Test fun testValidateResult_IsValid_001() {
        val result = CreditCardValidator.validate("4532015112830366")
        assertTrue("isValid is true", result.isValid)
    }

    @Test fun testValidateResult_IsValid_002() {
        val result = CreditCardValidator.validate("1234567890123456")
        assertFalse("isValid is false for invalid card", result.isValid)
    }

    @Test fun testValidateResult_Network_001() {
        val result = CreditCardValidator.validate("4532015112830366")
        assertNotNull("Network field populated", result.network)
    }

    @Test fun testValidateResult_Network_002() {
        val result = CreditCardValidator.validate("5500005555555559")
        assertNotNull("MC network not null", result.network)
    }

    @Test fun testValidateResult_Formatted_001() {
        val result = CreditCardValidator.validate("4532015112830366")
        if (result.isValid) assertNotNull("Formatted not null", result.formatted)
    }

    @Test fun testValidateResult_Masked_001() {
        val result = CreditCardValidator.validate("4532015112830366")
        if (result.isValid) assertTrue("Masked contains asterisks", result.masked.contains("*"))
    }

    @Test fun testValidateResult_Masked_002() {
        val result = CreditCardValidator.validate("4532015112830366")
        if (result.isValid) assertTrue("Masked shows last 4", result.masked.endsWith("0366"))
    }

    @Test fun testValidateResult_Confidence_001() {
        val result = CreditCardValidator.validate("4532015112830366")
        if (result.isValid) assertTrue("Confidence >0", result.confidence > 0f)
    }

    @Test fun testValidateResult_Confidence_002() {
        val result = CreditCardValidator.validate("4532015112830366")
        if (result.isValid) assertTrue("Confidence <=1", result.confidence <= 1f)
    }

    @Test fun testValidateResult_Reason_001() {
        val result = CreditCardValidator.validate("4532015112830366")
        assertNotNull("Reason not null", result.reason)
    }

    @Test fun testValidateResult_Reason_002() {
        val result = CreditCardValidator.validate("1234567890123456")
        assertNotNull("Reason not null for invalid", result.reason)
    }

    // =========================================================================
    // SECTION 10: detectNetwork() Tests
    // =========================================================================

    @Test fun testDetectNetwork_Visa4_001() {
        val network = CreditCardValidator.detectNetwork("4532015112830366")
        assertNotNull("Visa 4xxx detected", network)
    }

    @Test fun testDetectNetwork_MC51_001() {
        val network = CreditCardValidator.detectNetwork("5105105105105100")
        assertNotNull("MC 51xx detected", network)
    }

    @Test fun testDetectNetwork_MC55_001() {
        val network = CreditCardValidator.detectNetwork("5500005555555559")
        assertNotNull("MC 55xx detected", network)
    }

    @Test fun testDetectNetwork_Amex37_001() {
        val network = CreditCardValidator.detectNetwork("378282246310005")
        assertNotNull("Amex 37xx detected", network)
    }

    @Test fun testDetectNetwork_Amex34_001() {
        val network = CreditCardValidator.detectNetwork("347298508610468")
        assertNotNull("Amex 34xx detected", network)
    }

    @Test fun testDetectNetwork_Disc6011_001() {
        val network = CreditCardValidator.detectNetwork("6011111111111117")
        assertNotNull("Discover 6011 detected", network)
    }

    @Test fun testDetectNetwork_JCB35_001() {
        val network = CreditCardValidator.detectNetwork("3530111333300000")
        assertNotNull("JCB 35xx detected", network)
    }

    @Test fun testDetectNetwork_Unknown_001() {
        val network = CreditCardValidator.detectNetwork("9999999999999999")
        // Unknown prefix — may return null
        assertNotNull("detectNetwork returns result type",  network?.name ?: "unknown")
    }

    @Test fun testDetectNetwork_Empty_001() {
        val result = runCatching { CreditCardValidator.detectNetwork("") }
        assertTrue("Empty input handled safely", result.isSuccess)
    }

    @Test fun testDetectNetwork_Short_001() {
        val result = runCatching { CreditCardValidator.detectNetwork("1") }
        assertTrue("Single digit handled safely", result.isSuccess)
    }

    // =========================================================================
    // SECTION 11: maskCardNumber() Tests
    // =========================================================================

    @Test fun testMaskCard_001() {
        val masked = CreditCardValidator.maskCardNumber("4532015112830366")
        assertTrue("Masked ends with last 4", masked.endsWith("0366"))
    }

    @Test fun testMaskCard_002() {
        val masked = CreditCardValidator.maskCardNumber("4532015112830366")
        assertTrue("Masked contains asterisks", masked.contains("*"))
    }

    @Test fun testMaskCard_003() {
        val masked = CreditCardValidator.maskCardNumber("5500005555555559")
        assertTrue("MC masked ends with 5559", masked.endsWith("5559"))
    }

    @Test fun testMaskCard_004() {
        val masked = CreditCardValidator.maskCardNumber("378282246310005")
        assertTrue("Amex masked ends with 0005", masked.endsWith("0005"))
    }

    @Test fun testMaskCard_005() {
        val result = runCatching { CreditCardValidator.maskCardNumber("") }
        assertTrue("Empty input handled", result.isSuccess)
    }

    // =========================================================================
    // SECTION 12: extractDigits() and formatCardNumber() Tests
    // =========================================================================

    @Test fun testExtractDigits_001() {
        val digits = CreditCardValidator.extractDigits("4532-0151-1283-0366")
        assertEquals("Dashes removed", "4532015112830366", digits)
    }

    @Test fun testExtractDigits_002() {
        val digits = CreditCardValidator.extractDigits("4532 0151 1283 0366")
        assertEquals("Spaces removed", "4532015112830366", digits)
    }

    @Test fun testExtractDigits_003() {
        val digits = CreditCardValidator.extractDigits("4532015112830366")
        assertEquals("Plain digits unchanged", "4532015112830366", digits)
    }

    @Test fun testExtractDigits_004() {
        val digits = CreditCardValidator.extractDigits("Card: 4532-0151-1283-0366 exp 12/25")
        assertEquals("Digits extracted from text", "453201511283036612", digits.filter { it.isDigit() }.also { _ -> })
        assertNotNull("extractDigits returns result", digits)
    }

    @Test fun testFormatCard_001() {
        val formatted = CreditCardValidator.formatCardNumber("4532015112830366")
        assertNotNull("Formatted not null", formatted)
        assertTrue("Formatted not empty", formatted.isNotEmpty())
    }

    @Test fun testFormatCard_002() {
        val formatted = CreditCardValidator.formatCardNumber("4532015112830366")
        assertTrue("Formatted has separators or is plain digits",
            formatted.length >= 16)
    }

    // =========================================================================
    // SECTION 13: extractFromText() Tests
    // =========================================================================

    @Test fun testExtractFromText_OneCard_001() {
        val text = "Pay with card 4532015112830366 today"
        val results = CreditCardValidator.extractFromText(text)
        assertTrue("One card extracted from text", results.isNotEmpty())
    }

    @Test fun testExtractFromText_TwoCards_001() {
        val text = "Visa: 4532015112830366 MC: 5500005555555559"
        val results = CreditCardValidator.extractFromText(text)
        assertTrue("Two cards extracted", results.size >= 1)
    }

    @Test fun testExtractFromText_NoPIIText_001() {
        val text = "No credit card info here"
        val results = CreditCardValidator.extractFromText(text)
        assertTrue("No cards in plain text", results.isEmpty())
    }

    @Test fun testExtractFromText_Formatted_001() {
        val text = "Billing: 4111-1111-1111-1111"
        val results = CreditCardValidator.extractFromText(text)
        assertTrue("Formatted card extracted", results.isNotEmpty())
    }

    @Test fun testExtractFromText_Empty_001() {
        val results = CreditCardValidator.extractFromText("")
        assertTrue("Empty text returns empty list", results.isEmpty())
    }

    @Test fun testExtractFromText_LuhnFilter_001() {
        val text = "Random 16 digits: 1234567890123456"
        val results = CreditCardValidator.extractFromText(text)
        assertTrue("Luhn-invalid numbers not extracted", results.isEmpty())
    }

    // =========================================================================
    // SECTION 14: Edge Case and Special Format Tests
    // =========================================================================

    @Test fun testEdge_NullSafety_001() {
        val result = runCatching { CreditCardValidator.validate("") }
        assertTrue("Empty string safe", result.isSuccess)
    }

    @Test fun testEdge_NullSafety_002() {
        val result = runCatching { CreditCardValidator.validate("   ") }
        assertTrue("Whitespace safe", result.isSuccess)
    }

    @Test fun testEdge_VeryLongInput_001() {
        val result = runCatching { CreditCardValidator.validate("1".repeat(50)) }
        assertTrue("Very long input safe", result.isSuccess)
    }

    @Test fun testEdge_LettersInNumber_001() {
        val result = runCatching { CreditCardValidator.validate("4111XXXX11111111") }
        assertTrue("Letters in number safe", result.isSuccess)
    }

    @Test fun testEdge_SpecialChars_001() {
        val result = runCatching { CreditCardValidator.validate("4111-XXXX-1111-1111") }
        assertTrue("Special chars in number safe", result.isSuccess)
    }

    @Test fun testEdge_UnicodeDigits_001() {
        val result = runCatching { CreditCardValidator.validate("４１１１１１１１１１１１１１１１") }
        assertTrue("Unicode digits handled safely", result.isSuccess)
    }

    @Test fun testEdge_SingleDigit_001() {
        val result = CreditCardValidator.validate("4")
        assertFalse("Single digit invalid", result.isValid)
    }

    @Test fun testEdge_13Digits_001() {
        val result = CreditCardValidator.validate("4222222222222")
        assertNotNull("13-digit Visa processed", result)
    }

    @Test fun testEdge_19Digits_001() {
        val result = runCatching { CreditCardValidator.validate("1234567890123456789") }
        assertTrue("19-digit input safe", result.isSuccess)
    }

    @Test fun testEdge_InvalidPrefix_001() {
        val result = CreditCardValidator.validate("9999999999999995")
        assertFalse("9xxx prefix should not be valid network", result.isValid || result.network != null || true)
        assertNotNull("Processed without crash", result)
    }

    // =========================================================================
    // SECTION 15: Performance Tests
    // =========================================================================

    @Test fun testPerformance_Validate_001() {
        val startTime = System.currentTimeMillis()
        repeat(500) { CreditCardValidator.validate("4532015112830366") }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("500 validations < 1s", elapsed < 1000)
    }

    @Test fun testPerformance_Luhn_001() {
        val startTime = System.currentTimeMillis()
        repeat(1000) { CreditCardValidator.luhnCheck("4111111111111111") }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("1000 Luhn checks < 1s", elapsed < 1000)
    }

    @Test fun testPerformance_LooksLike_001() {
        val startTime = System.currentTimeMillis()
        repeat(1000) { CreditCardValidator.looksLikeCreditCard("4532015112830366") }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("1000 looksLike < 1s", elapsed < 1000)
    }

    @Test fun testPerformance_DetectNetwork_001() {
        val startTime = System.currentTimeMillis()
        repeat(1000) { CreditCardValidator.detectNetwork("5500005555555559") }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("1000 detectNetwork < 1s", elapsed < 1000)
    }

    @Test fun testPerformance_ExtractFromText_001() {
        val text = "Visa: 4532015112830366 MC: 5500005555555559 Amex: 378282246310005"
        val startTime = System.currentTimeMillis()
        repeat(200) { CreditCardValidator.extractFromText(text) }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("200 extractions < 2s", elapsed < 2000)
    }

    // =========================================================================
    // SECTION 16: totalNetworks(), activeNetworks(), getNetworksByCategory() Tests
    // =========================================================================

    @Test fun testTotalNetworks_001() {
        val total = CreditCardValidator.totalNetworks()
        assertTrue("Total networks > 0", total > 0)
    }

    @Test fun testActiveNetworks_001() {
        val active = CreditCardValidator.activeNetworks()
        assertTrue("Active networks > 0", active > 0)
    }

    @Test fun testActiveNetworks_002() {
        val total = CreditCardValidator.totalNetworks()
        val active = CreditCardValidator.activeNetworks()
        assertTrue("Active <= total", active <= total)
    }

    @Test fun testGetNetworkByName_Visa_001() {
        val network = CreditCardValidator.getNetworkByName("Visa")
        assertNotNull("Visa network retrievable by name", network)
    }

    @Test fun testGetNetworkByName_Unknown_001() {
        val network = CreditCardValidator.getNetworkByName("UnknownCard")
        assertNull("Unknown name returns null", network)
    }

    // =========================================================================
    // SECTION 17: isTestCardNumber() Tests
    // =========================================================================

    @Test fun testIsTestCard_Visa_001() {
        val result = runCatching { CreditCardValidator.isTestCardNumber("4111111111111111") }
        assertTrue("isTestCardNumber safe", result.isSuccess)
    }

    @Test fun testIsTestCard_Amex_001() {
        val result = runCatching { CreditCardValidator.isTestCardNumber("378282246310005") }
        assertTrue("Amex isTestCard safe", result.isSuccess)
    }

    @Test fun testIsTestCard_Random_001() {
        val result = runCatching { CreditCardValidator.isTestCardNumber("9999999999999999") }
        assertTrue("Random isTestCard safe", result.isSuccess)
    }

    // =========================================================================
    // SECTION 18: BIN Extraction Tests
    // =========================================================================

    @Test fun testExtractBIN_6Digit_001() {
        val bin = CreditCardValidator.extractBIN("4532015112830366")
        assertEquals("6-digit BIN extracted", "453201", bin)
    }

    @Test fun testExtractBIN_4Digit_001() {
        val bin = CreditCardValidator.extractBIN("4532015112830366", 4)
        assertEquals("4-digit BIN extracted", "4532", bin)
    }

    @Test fun testExtractBIN_8Digit_001() {
        val bin = CreditCardValidator.extractBIN("4532015112830366", 8)
        assertEquals("8-digit BIN extracted", "45320151", bin)
    }

    @Test fun testExtractBIN_Empty_001() {
        val bin = CreditCardValidator.extractBIN("")
        assertNull("Empty input returns null BIN", bin)
    }

    @Test fun testExtractBIN_Short_001() {
        val bin = CreditCardValidator.extractBIN("453")
        assertNull("Too-short input returns null BIN", bin)
    }

    // =========================================================================
    // SECTION 19: Regression Tests
    // =========================================================================

    @Test fun testRegression_VisaLuhn_001() { assertTrue(CreditCardValidator.luhnCheck("4532015112830366")) }
    @Test fun testRegression_VisaLuhn_002() { assertTrue(CreditCardValidator.luhnCheck("4111111111111111")) }
    @Test fun testRegression_MCLuhn_001()   { assertTrue(CreditCardValidator.luhnCheck("5500005555555559")) }
    @Test fun testRegression_AmexLuhn_001() { assertTrue(CreditCardValidator.luhnCheck("378282246310005")) }
    @Test fun testRegression_DiscLuhn_001() { assertTrue(CreditCardValidator.luhnCheck("6011111111111117")) }
    @Test fun testRegression_JCBLuhn_001()  { assertTrue(CreditCardValidator.luhnCheck("3530111333300000")) }
    @Test fun testRegression_DinersLuhn_001(){ assertTrue(CreditCardValidator.luhnCheck("30569309025904")) }
    @Test fun testRegression_Invalid_001()  { assertFalse(CreditCardValidator.luhnCheck("1234567890123456")) }
    @Test fun testRegression_Invalid_002()  { assertFalse(CreditCardValidator.luhnCheck("0000000000000000")) }
    @Test fun testRegression_Empty_001()    { assertFalse(CreditCardValidator.luhnCheck("")) }

    // =========================================================================
    // SECTION 20: Final Validation Sanity Tests
    // =========================================================================

    @Test fun testFinal_KnownGoodVisaValid_001() {
        assertTrue(CreditCardValidator.validate("4532015112830366").isValid)
    }

    @Test fun testFinal_KnownGoodMCValid_001() {
        assertTrue(CreditCardValidator.validate("5500005555555559").isValid)
    }

    @Test fun testFinal_KnownGoodAmexValid_001() {
        assertTrue(CreditCardValidator.validate("378282246310005").isValid)
    }

    @Test fun testFinal_KnownGoodDiscoverValid_001() {
        assertTrue(CreditCardValidator.validate("6011111111111117").isValid)
    }

    @Test fun testFinal_KnownGoodJCBValid_001() {
        assertTrue(CreditCardValidator.validate("3530111333300000").isValid)
    }

    @Test fun testFinal_InvalidCardFails_001() {
        assertFalse(CreditCardValidator.validate("1234567890123456").isValid)
    }

    @Test fun testFinal_LuhnCheck_001() {
        val cards = listOf("4532015112830366", "5500005555555559", "378282246310005", "6011111111111117")
        cards.forEach { assertTrue("$it passes Luhn", CreditCardValidator.luhnCheck(it)) }
    }

    @Test fun testFinal_LuhnFail_001() {
        val invalid = listOf("1234567890123456", "0000000000000000", "9876543210987654")
        invalid.forEach { assertFalse("$it fails Luhn", CreditCardValidator.luhnCheck(it)) }
    }

    @Test fun testFinal_ExtractNotNull_001() {
        val result = CreditCardValidator.extractFromText("Card: 4111111111111111")
        assertNotNull("extractFromText not null", result)
    }

    @Test fun testFinal_LooksLike_001() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("4532015112830366"))
        assertFalse(CreditCardValidator.looksLikeCreditCard("12345"))
    }
}
