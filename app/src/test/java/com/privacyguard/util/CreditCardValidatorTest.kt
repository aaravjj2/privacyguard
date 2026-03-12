package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

class CreditCardValidatorTest {

    // ========================================================================
    // SECTION 1: Luhn Algorithm Core Tests
    // ========================================================================

    @Test
    fun `luhnCheck returns true for simple valid number 0`() {
        assertTrue(CreditCardValidator.luhnCheck("0"))
    }

    @Test
    fun `luhnCheck returns true for valid two digit 18`() {
        assertTrue(CreditCardValidator.luhnCheck("18"))
    }

    @Test
    fun `luhnCheck returns false for single digit 1`() {
        assertFalse(CreditCardValidator.luhnCheck("1"))
    }

    @Test
    fun `luhnCheck returns true for known valid sequence 79927398713`() {
        assertTrue(CreditCardValidator.luhnCheck("79927398713"))
    }

    @Test
    fun `luhnCheck returns false for known invalid sequence 79927398710`() {
        assertFalse(CreditCardValidator.luhnCheck("79927398710"))
    }

    @Test
    fun `luhnCheck returns false for empty string`() {
        assertFalse(CreditCardValidator.luhnCheck(""))
    }

    @Test
    fun `luhnCheck returns false for non-digit string`() {
        assertFalse(CreditCardValidator.luhnCheck("abcdefgh"))
    }

    @Test
    fun `luhnCheck returns false for mixed alpha and digits`() {
        assertFalse(CreditCardValidator.luhnCheck("411111abc1111111"))
    }

    @Test
    fun `luhnCheck returns true for all zeros of even length`() {
        assertTrue(CreditCardValidator.luhnCheck("00"))
    }

    @Test
    fun `luhnCheck returns true for Visa test number 4111111111111111`() {
        assertTrue(CreditCardValidator.luhnCheck("4111111111111111"))
    }

    @Test
    fun `luhnCheck returns true for Mastercard test 5555555555554444`() {
        assertTrue(CreditCardValidator.luhnCheck("5555555555554444"))
    }

    @Test
    fun `luhnCheck returns true for Amex test 378282246310005`() {
        assertTrue(CreditCardValidator.luhnCheck("378282246310005"))
    }

    @Test
    fun `luhnCheck returns true for Discover test 6011111111111117`() {
        assertTrue(CreditCardValidator.luhnCheck("6011111111111117"))
    }

    @Test
    fun `luhnCheck returns false for Visa with wrong check digit 4111111111111112`() {
        assertFalse(CreditCardValidator.luhnCheck("4111111111111112"))
    }

    @Test
    fun `luhnCheck returns false for Visa with wrong check digit 4111111111111113`() {
        assertFalse(CreditCardValidator.luhnCheck("4111111111111113"))
    }

    @Test
    fun `luhnCheck returns false for all ones 1111111111111111`() {
        assertFalse(CreditCardValidator.luhnCheck("1111111111111111"))
    }

    @Test
    fun `luhnCheck returns false for all nines 9999999999999999`() {
        assertFalse(CreditCardValidator.luhnCheck("9999999999999999"))
    }

    @Test
    fun `luhnCheck returns true for all zeros 0000000000000000`() {
        assertTrue(CreditCardValidator.luhnCheck("0000000000000000"))
    }

    @Test
    fun `luhnCheck handles 19 digit valid number`() {
        val partial = "411111111111111111"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        assertTrue(CreditCardValidator.luhnCheck(partial + check))
    }

    @Test
    fun `luhnCheck handles 13 digit valid number`() {
        val partial = "422222222222"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        assertTrue(CreditCardValidator.luhnCheck(partial + check))
    }

    // ========================================================================
    // SECTION 2: generateLuhnCheckDigit Tests
    // ========================================================================

    @Test
    fun `generateLuhnCheckDigit for Visa prefix produces valid number`() {
        val partial = "411111111111111"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        assertEquals(1, check)
    }

    @Test
    fun `generateLuhnCheckDigit for Mastercard prefix produces valid number`() {
        val partial = "555555555555444"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val full = partial + check
        assertTrue(CreditCardValidator.luhnCheck(full))
    }

    @Test
    fun `generateLuhnCheckDigit for Amex prefix produces valid number`() {
        val partial = "37828224631000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val full = partial + check
        assertTrue(CreditCardValidator.luhnCheck(full))
    }

    @Test
    fun `generateLuhnCheckDigit returns 0 for all zeros`() {
        val partial = "000000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        assertEquals(0, check)
    }

    @Test
    fun `generateLuhnCheckDigit for single digit`() {
        val partial = "7"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val full = partial + check
        assertTrue(CreditCardValidator.luhnCheck(full))
    }

    @Test
    fun `generateLuhnCheckDigit ignores non-digit characters`() {
        val check1 = CreditCardValidator.generateLuhnCheckDigit("4111-1111-1111-111")
        val check2 = CreditCardValidator.generateLuhnCheckDigit("411111111111111")
        assertEquals(check1, check2)
    }

    @Test
    fun `generateLuhnCheckDigit for Discover prefix`() {
        val partial = "601111111111111"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val full = partial + check
        assertTrue(CreditCardValidator.luhnCheck(full))
    }

    @Test
    fun `generateLuhnCheckDigit result is always 0 to 9`() {
        for (i in 0..9) {
            val partial = "41111111111111$i"
            val check = CreditCardValidator.generateLuhnCheckDigit(partial)
            assertTrue("Check digit should be 0-9, got $check", check in 0..9)
        }
    }

    // ========================================================================
    // SECTION 3: extractDigits Tests
    // ========================================================================

    @Test
    fun `extractDigits removes spaces`() {
        assertEquals("4111111111111111", CreditCardValidator.extractDigits("4111 1111 1111 1111"))
    }

    @Test
    fun `extractDigits removes dashes`() {
        assertEquals("4111111111111111", CreditCardValidator.extractDigits("4111-1111-1111-1111"))
    }

    @Test
    fun `extractDigits removes mixed separators`() {
        assertEquals("4111111111111111", CreditCardValidator.extractDigits("4111 1111-1111 1111"))
    }

    @Test
    fun `extractDigits removes all non-digit characters`() {
        assertEquals("4111111111111111", CreditCardValidator.extractDigits("Card: 4111-1111-1111-1111"))
    }

    @Test
    fun `extractDigits returns empty for no digits`() {
        assertEquals("", CreditCardValidator.extractDigits("no digits here"))
    }

    @Test
    fun `extractDigits returns empty for empty string`() {
        assertEquals("", CreditCardValidator.extractDigits(""))
    }

    @Test
    fun `extractDigits preserves pure digit string`() {
        assertEquals("4111111111111111", CreditCardValidator.extractDigits("4111111111111111"))
    }

    @Test
    fun `extractDigits handles dots as separators`() {
        assertEquals("4111111111111111", CreditCardValidator.extractDigits("4111.1111.1111.1111"))
    }

    // ========================================================================
    // SECTION 4: Visa Card Validation Tests
    // ========================================================================

    @Test
    fun `validate Visa test card 4111111111111111 is valid`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertTrue(result.isValid)
        assertEquals("Visa", result.network?.name)
        assertTrue(result.luhnValid)
        assertTrue(result.lengthValid)
        assertTrue(result.prefixValid)
    }

    @Test
    fun `validate Visa test card 4012888888881881 is valid`() {
        val result = CreditCardValidator.validate("4012888888881881")
        assertTrue(result.isValid)
        assertEquals("Visa", result.network?.name)
    }

    @Test
    fun `validate Visa 13-digit test card 4222222222222 is valid`() {
        val result = CreditCardValidator.validate("4222222222222")
        assertTrue(result.isValid)
        assertEquals("Visa", result.network?.name)
    }

    @Test
    fun `validate Visa test card 4000056655665556 is valid`() {
        val result = CreditCardValidator.validate("4000056655665556")
        assertTrue(result.isValid)
        assertEquals("Visa", result.network?.name)
    }

    @Test
    fun `validate Visa test card 4242424242424242 is valid`() {
        val result = CreditCardValidator.validate("4242424242424242")
        assertTrue(result.isValid)
        assertEquals("Visa", result.network?.name)
    }

    @Test
    fun `validate Visa with spaces is valid`() {
        val result = CreditCardValidator.validate("4111 1111 1111 1111")
        assertTrue(result.isValid)
        assertEquals("Visa", result.network?.name)
    }

    @Test
    fun `validate Visa with dashes is valid`() {
        val result = CreditCardValidator.validate("4111-1111-1111-1111")
        assertTrue(result.isValid)
        assertEquals("Visa", result.network?.name)
    }

    @Test
    fun `validate Visa network short name is VISA`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertEquals("VISA", result.network?.shortName)
    }

    @Test
    fun `validate Visa has cvv length 3`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertEquals(3, result.network?.cvvLength)
    }

    @Test
    fun `validate Visa has major category`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertEquals(CreditCardValidator.CardCategory.MAJOR, result.network?.category)
    }

    @Test
    fun `validate Visa formatted has correct spacing`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertEquals("4111 1111 1111 1111", result.formatted)
    }

    @Test
    fun `validate Visa masked shows last four digits`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertEquals("**** **** **** 1111", result.masked)
    }

    @Test
    fun `validate Visa confidence is high`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertTrue(result.confidence >= 0.95f)
    }

    @Test
    fun `validate Visa with invalid Luhn fails`() {
        val result = CreditCardValidator.validate("4111111111111112")
        assertFalse(result.isValid)
        assertFalse(result.luhnValid)
    }

    // ========================================================================
    // SECTION 5: Mastercard Validation Tests
    // ========================================================================

    @Test
    fun `validate Mastercard test card 5555555555554444 is valid`() {
        val result = CreditCardValidator.validate("5555555555554444")
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard test card 5200828282828210 is valid`() {
        val result = CreditCardValidator.validate("5200828282828210")
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard test card 5105105105105100 is valid`() {
        val result = CreditCardValidator.validate("5105105105105100")
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard 2-series test 2223003122003222 is valid`() {
        val result = CreditCardValidator.validate("2223003122003222")
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard 2-series test 2223000048400011 is valid`() {
        val result = CreditCardValidator.validate("2223000048400011")
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard prefix 51 is valid`() {
        val partial = "510000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard prefix 52 is valid`() {
        val partial = "520000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard prefix 53 is valid`() {
        val partial = "530000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard prefix 54 is valid`() {
        val partial = "540000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Mastercard prefix 55 is valid`() {
        val partial = "550000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Mastercard 2-series lower bound 2221 is valid`() {
        val partial = "222100000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard 2-series upper bound 2720 is valid`() {
        val partial = "272000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Mastercard", result.network?.name)
    }

    @Test
    fun `validate Mastercard network short name is MC`() {
        val result = CreditCardValidator.validate("5555555555554444")
        assertEquals("MC", result.network?.shortName)
    }

    @Test
    fun `validate Mastercard has cvv length 3`() {
        val result = CreditCardValidator.validate("5555555555554444")
        assertEquals(3, result.network?.cvvLength)
    }

    @Test
    fun `validate Mastercard has major category`() {
        val result = CreditCardValidator.validate("5555555555554444")
        assertEquals(CreditCardValidator.CardCategory.MAJOR, result.network?.category)
    }

    @Test
    fun `validate Mastercard formatted has correct spacing`() {
        val result = CreditCardValidator.validate("5555555555554444")
        assertEquals("5555 5555 5555 4444", result.formatted)
    }

    @Test
    fun `validate Mastercard valid lengths is exactly 16`() {
        val result = CreditCardValidator.validate("5555555555554444")
        assertEquals(setOf(16), result.network?.validLengths)
    }

    // ========================================================================
    // SECTION 6: American Express Validation Tests
    // ========================================================================

    @Test
    fun `validate Amex test card 378282246310005 is valid`() {
        val result = CreditCardValidator.validate("378282246310005")
        assertTrue(result.isValid)
        assertEquals("American Express", result.network?.name)
    }

    @Test
    fun `validate Amex test card 371449635398431 is valid`() {
        val result = CreditCardValidator.validate("371449635398431")
        assertTrue(result.isValid)
        assertEquals("American Express", result.network?.name)
    }

    @Test
    fun `validate Amex test card 378734493671000 is valid`() {
        val result = CreditCardValidator.validate("378734493671000")
        assertTrue(result.isValid)
        assertEquals("American Express", result.network?.name)
    }

    @Test
    fun `validate Amex prefix 34 is valid`() {
        val partial = "34000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("American Express", result.network?.name)
    }

    @Test
    fun `validate Amex prefix 37 is valid`() {
        val partial = "37000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("American Express", result.network?.name)
    }

    @Test
    fun `validate Amex network short name is AMEX`() {
        val result = CreditCardValidator.validate("378282246310005")
        assertEquals("AMEX", result.network?.shortName)
    }

    @Test
    fun `validate Amex has cvv length 4`() {
        val result = CreditCardValidator.validate("378282246310005")
        assertEquals(4, result.network?.cvvLength)
    }

    @Test
    fun `validate Amex has 15 digit valid length`() {
        val result = CreditCardValidator.validate("378282246310005")
        assertEquals(setOf(15), result.network?.validLengths)
    }

    @Test
    fun `validate Amex formatted with 4-6-5 spacing`() {
        val result = CreditCardValidator.validate("378282246310005")
        assertEquals("3782 822463 10005", result.formatted)
    }

    @Test
    fun `validate Amex masked with 4-6-5 spacing`() {
        val result = CreditCardValidator.validate("378282246310005")
        assertTrue(result.masked.endsWith("0005"))
    }

    @Test
    fun `validate Amex has major category`() {
        val result = CreditCardValidator.validate("378282246310005")
        assertEquals(CreditCardValidator.CardCategory.MAJOR, result.network?.category)
    }

    @Test
    fun `validate Amex 16 digits is invalid length`() {
        val partial = "370000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val full = partial + check
        val result = CreditCardValidator.validate(full)
        assertFalse(result.isValid)
    }

    // ========================================================================
    // SECTION 7: Discover Card Validation Tests
    // ========================================================================

    @Test
    fun `validate Discover test card 6011111111111117 is valid`() {
        val result = CreditCardValidator.validate("6011111111111117")
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover test card 6011000990139424 is valid`() {
        val result = CreditCardValidator.validate("6011000990139424")
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover prefix 6011 is valid`() {
        val partial = "601100000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover prefix 644 is valid`() {
        val partial = "644000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover prefix 645 is valid`() {
        val partial = "645000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover prefix 649 is valid`() {
        val partial = "649000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover prefix 65 is valid`() {
        val partial = "650000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Discover network short name is DISC`() {
        val result = CreditCardValidator.validate("6011111111111117")
        assertEquals("DISC", result.network?.shortName)
    }

    @Test
    fun `validate Discover has cvv length 3`() {
        val result = CreditCardValidator.validate("6011111111111117")
        assertEquals(3, result.network?.cvvLength)
    }

    @Test
    fun `validate Discover has major category`() {
        val result = CreditCardValidator.validate("6011111111111117")
        assertEquals(CreditCardValidator.CardCategory.MAJOR, result.network?.category)
    }

    @Test
    fun `validate Discover supports lengths 16 through 19`() {
        val result = CreditCardValidator.validate("6011111111111117")
        assertTrue(result.network?.validLengths?.contains(16) == true)
        assertTrue(result.network?.validLengths?.contains(17) == true)
        assertTrue(result.network?.validLengths?.contains(18) == true)
        assertTrue(result.network?.validLengths?.contains(19) == true)
    }

    // ========================================================================
    // SECTION 8: JCB Validation Tests
    // ========================================================================

    @Test
    fun `validate JCB test card 3530111333300000 is valid`() {
        val result = CreditCardValidator.validate("3530111333300000")
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB test card 3566002020360505 is valid`() {
        val result = CreditCardValidator.validate("3566002020360505")
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB test card 3566111111111113 is valid`() {
        val result = CreditCardValidator.validate("3566111111111113")
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB prefix lower bound 3528 is valid`() {
        val partial = "352800000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB prefix upper bound 3589 is valid`() {
        val partial = "358900000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB network short name is JCB`() {
        val result = CreditCardValidator.validate("3530111333300000")
        assertEquals("JCB", result.network?.shortName)
    }

    @Test
    fun `validate JCB has regional category`() {
        val result = CreditCardValidator.validate("3530111333300000")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, result.network?.category)
    }

    @Test
    fun `validate JCB supports length 16`() {
        val result = CreditCardValidator.validate("3530111333300000")
        assertTrue(result.network?.validLengths?.contains(16) == true)
    }

    // ========================================================================
    // SECTION 9: Diners Club Validation Tests
    // ========================================================================

    @Test
    fun `validate Diners Club test card 30569309025904 is valid`() {
        val result = CreditCardValidator.validate("30569309025904")
        assertTrue(result.isValid)
        assertTrue(result.network?.name?.startsWith("Diners") == true)
    }

    @Test
    fun `validate Diners Club test card 38520000023237 is valid`() {
        val result = CreditCardValidator.validate("38520000023237")
        assertTrue(result.isValid)
        assertTrue(result.network?.name?.startsWith("Diners") == true)
    }

    @Test
    fun `validate Diners Club test card 36700102000000 is valid`() {
        val result = CreditCardValidator.validate("36700102000000")
        assertTrue(result.isValid)
        assertTrue(result.network?.name?.startsWith("Diners") == true)
    }

    @Test
    fun `validate Diners Club Carte Blanche prefix 300 is valid`() {
        val partial = "3000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertTrue(result.network?.name?.contains("Diners") == true)
    }

    @Test
    fun `validate Diners Club International prefix 36 is valid`() {
        val partial = "3600000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Diners Club International prefix 38 is valid`() {
        val partial = "3800000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Diners Club has short name DINR`() {
        val result = CreditCardValidator.validate("30569309025904")
        assertEquals("DINR", result.network?.shortName)
    }

    // ========================================================================
    // SECTION 10: UnionPay Validation Tests
    // ========================================================================

    @Test
    fun `validate UnionPay test card 6200000000000005 is valid`() {
        val result = CreditCardValidator.validate("6200000000000005")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate UnionPay test card 6212345678901234 is valid`() {
        val result = CreditCardValidator.validate("6212345678901234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate UnionPay prefix 62 detected`() {
        val result = CreditCardValidator.validate("6200000000000005")
        assertNotNull(result.network)
    }

    @Test
    fun `validate UnionPay does not use Luhn`() {
        val network = CreditCardValidator.getNetworkByName("UnionPay")
        assertNotNull(network)
        assertFalse(network!!.usesLuhn)
    }

    @Test
    fun `validate UnionPay has regional category`() {
        val network = CreditCardValidator.getNetworkByName("UnionPay")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    @Test
    fun `validate UnionPay network short name is UP`() {
        val network = CreditCardValidator.getNetworkByName("UnionPay")
        assertEquals("UP", network?.shortName)
    }

    @Test
    fun `validate UnionPay supports multiple lengths`() {
        val network = CreditCardValidator.getNetworkByName("UnionPay")
        assertTrue(network?.validLengths?.containsAll(setOf(16, 17, 18, 19)) == true)
    }

    // ========================================================================
    // SECTION 11: Maestro Validation Tests
    // ========================================================================

    @Test
    fun `validate Maestro test card 6304000000000000 is valid`() {
        val result = CreditCardValidator.validate("6304000000000000")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Maestro test card 6759649826438453 is valid`() {
        val result = CreditCardValidator.validate("6759649826438453")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Maestro prefix 5018 detected`() {
        val partial = "501800000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Maestro prefix 5020 detected`() {
        val partial = "502000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Maestro prefix 5038 detected`() {
        val partial = "503800000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Maestro supports 12 to 19 digit lengths`() {
        val network = CreditCardValidator.getNetworkByName("Maestro")
        assertNotNull(network)
        assertTrue(network!!.validLengths.contains(12))
        assertTrue(network.validLengths.contains(19))
    }

    @Test
    fun `validate Maestro has regional category`() {
        val network = CreditCardValidator.getNetworkByName("Maestro")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    @Test
    fun `validate Maestro has short name MAES`() {
        val network = CreditCardValidator.getNetworkByName("Maestro")
        assertEquals("MAES", network?.shortName)
    }

    // ========================================================================
    // SECTION 12: Mir Validation Tests
    // ========================================================================

    @Test
    fun `validate Mir prefix 2200 detected`() {
        val partial = "220000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Mir", result.network?.name)
    }

    @Test
    fun `validate Mir prefix 2204 detected`() {
        val partial = "220400000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Mir", result.network?.name)
    }

    @Test
    fun `validate Mir has regional category`() {
        val network = CreditCardValidator.getNetworkByName("Mir")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    @Test
    fun `validate Mir short name is MIR`() {
        val network = CreditCardValidator.getNetworkByName("Mir")
        assertEquals("MIR", network?.shortName)
    }

    @Test
    fun `validate Mir supports lengths 16 through 19`() {
        val network = CreditCardValidator.getNetworkByName("Mir")
        assertTrue(network?.validLengths?.containsAll(setOf(16, 17, 18, 19)) == true)
    }

    // ========================================================================
    // SECTION 13: Elo Validation Tests
    // ========================================================================

    @Test
    fun `validate Elo prefix 401178 detected`() {
        val partial = "401178000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Elo", result.network?.name)
    }

    @Test
    fun `validate Elo prefix 438935 detected`() {
        val partial = "438935000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Elo", result.network?.name)
    }

    @Test
    fun `validate Elo prefix 504175 detected`() {
        val partial = "504175000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Elo", result.network?.name)
    }

    @Test
    fun `validate Elo prefix 636297 detected`() {
        val partial = "636297000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Elo", result.network?.name)
    }

    @Test
    fun `validate Elo has regional category`() {
        val network = CreditCardValidator.getNetworkByName("Elo")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    @Test
    fun `validate Elo short name is ELO`() {
        val network = CreditCardValidator.getNetworkByName("Elo")
        assertEquals("ELO", network?.shortName)
    }

    @Test
    fun `validate Elo valid length is 16`() {
        val network = CreditCardValidator.getNetworkByName("Elo")
        assertEquals(setOf(16), network?.validLengths)
    }

    // ========================================================================
    // SECTION 14: Hipercard Validation Tests
    // ========================================================================

    @Test
    fun `validate Hipercard prefix 606282 detected`() {
        val partial = "606282000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Hipercard", result.network?.name)
    }

    @Test
    fun `validate Hipercard short name is HIPE`() {
        val network = CreditCardValidator.getNetworkByName("Hipercard")
        assertEquals("HIPE", network?.shortName)
    }

    @Test
    fun `validate Hipercard has regional category`() {
        val network = CreditCardValidator.getNetworkByName("Hipercard")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    // ========================================================================
    // SECTION 15: Troy Validation Tests
    // ========================================================================

    @Test
    fun `validate Troy prefix 979200 detected`() {
        val partial = "979200000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Troy", result.network?.name)
    }

    @Test
    fun `validate Troy short name is TROY`() {
        val network = CreditCardValidator.getNetworkByName("Troy")
        assertEquals("TROY", network?.shortName)
    }

    @Test
    fun `validate Troy has regional category`() {
        val network = CreditCardValidator.getNetworkByName("Troy")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    // ========================================================================
    // SECTION 16: Verve Validation Tests
    // ========================================================================

    @Test
    fun `validate Verve prefix 506099 detected`() {
        val partial = "506099000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Verve", result.network?.name)
    }

    @Test
    fun `validate Verve short name is VERV`() {
        val network = CreditCardValidator.getNetworkByName("Verve")
        assertEquals("VERV", network?.shortName)
    }

    @Test
    fun `validate Verve supports lengths 16, 18, 19`() {
        val network = CreditCardValidator.getNetworkByName("Verve")
        assertTrue(network?.validLengths?.containsAll(setOf(16, 18, 19)) == true)
    }

    // ========================================================================
    // SECTION 17: UATP Validation Tests
    // ========================================================================

    @Test
    fun `validate UATP prefix 1 detected`() {
        val partial = "10000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("UATP", result.network?.name)
    }

    @Test
    fun `validate UATP short name is UATP`() {
        val network = CreditCardValidator.getNetworkByName("UATP")
        assertEquals("UATP", network?.shortName)
    }

    @Test
    fun `validate UATP has specialty category`() {
        val network = CreditCardValidator.getNetworkByName("UATP")
        assertEquals(CreditCardValidator.CardCategory.SPECIALTY, network?.category)
    }

    @Test
    fun `validate UATP valid length is 15`() {
        val network = CreditCardValidator.getNetworkByName("UATP")
        assertEquals(setOf(15), network?.validLengths)
    }

    // ========================================================================
    // SECTION 18: Deprecated Networks Tests (Laser, Solo, Switch)
    // ========================================================================

    @Test
    fun `validate Laser prefix 6304 detected`() {
        val network = CreditCardValidator.getNetworkByName("Laser")
        assertNotNull(network)
        assertEquals(CreditCardValidator.CardCategory.DEPRECATED, network?.category)
    }

    @Test
    fun `validate Laser prefix 6706 in network definition`() {
        val network = CreditCardValidator.getNetworkByName("Laser")
        assertNotNull(network)
        val has6706 = network!!.prefixes.any { it.start == 6706L }
        assertTrue(has6706)
    }

    @Test
    fun `validate Laser prefix 6771 in network definition`() {
        val network = CreditCardValidator.getNetworkByName("Laser")
        assertNotNull(network)
        val has6771 = network!!.prefixes.any { it.start == 6771L }
        assertTrue(has6771)
    }

    @Test
    fun `validate Solo has deprecated category`() {
        val network = CreditCardValidator.getNetworkByName("Solo")
        assertEquals(CreditCardValidator.CardCategory.DEPRECATED, network?.category)
    }

    @Test
    fun `validate Solo prefix 6334 in definition`() {
        val network = CreditCardValidator.getNetworkByName("Solo")
        val has6334 = network!!.prefixes.any { it.start == 6334L }
        assertTrue(has6334)
    }

    @Test
    fun `validate Solo prefix 6767 in definition`() {
        val network = CreditCardValidator.getNetworkByName("Solo")
        val has6767 = network!!.prefixes.any { it.start == 6767L }
        assertTrue(has6767)
    }

    @Test
    fun `validate Solo supports lengths 16, 18, 19`() {
        val network = CreditCardValidator.getNetworkByName("Solo")
        assertTrue(network?.validLengths?.containsAll(setOf(16, 18, 19)) == true)
    }

    @Test
    fun `validate Switch has deprecated category`() {
        val network = CreditCardValidator.getNetworkByName("Switch")
        assertEquals(CreditCardValidator.CardCategory.DEPRECATED, network?.category)
    }

    @Test
    fun `validate Switch short name is SWCH`() {
        val network = CreditCardValidator.getNetworkByName("Switch")
        assertEquals("SWCH", network?.shortName)
    }

    @Test
    fun `validate Switch prefix 4903 in definition`() {
        val network = CreditCardValidator.getNetworkByName("Switch")
        val has4903 = network!!.prefixes.any { it.start == 4903L }
        assertTrue(has4903)
    }

    // ========================================================================
    // SECTION 19: Dankort Validation Tests
    // ========================================================================

    @Test
    fun `validate Dankort prefix 5019 detected`() {
        val partial = "501900000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Dankort short name is DANK`() {
        val network = CreditCardValidator.getNetworkByName("Dankort")
        assertEquals("DANK", network?.shortName)
    }

    @Test
    fun `validate Dankort has regional category`() {
        val network = CreditCardValidator.getNetworkByName("Dankort")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    // ========================================================================
    // SECTION 20: InterPayment and InstaPayment Tests
    // ========================================================================

    @Test
    fun `validate InterPayment prefix 636 exists`() {
        val network = CreditCardValidator.getNetworkByName("InterPayment")
        assertNotNull(network)
        assertEquals("INTP", network?.shortName)
    }

    @Test
    fun `validate InterPayment supports lengths 16 through 19`() {
        val network = CreditCardValidator.getNetworkByName("InterPayment")
        assertTrue(network?.validLengths?.containsAll(setOf(16, 17, 18, 19)) == true)
    }

    @Test
    fun `validate InstaPayment prefix 637 exists`() {
        val network = CreditCardValidator.getNetworkByName("InstaPayment")
        assertNotNull(network)
        assertEquals("INST", network?.shortName)
    }

    @Test
    fun `validate InstaPayment valid length is 16`() {
        val network = CreditCardValidator.getNetworkByName("InstaPayment")
        assertEquals(setOf(16), network?.validLengths)
    }

    // ========================================================================
    // SECTION 21: LankaPay, China T-Union, NPS Pridnestrovie Tests
    // ========================================================================

    @Test
    fun `validate LankaPay prefix 357111 exists`() {
        val network = CreditCardValidator.getNetworkByName("LankaPay")
        assertNotNull(network)
        assertEquals("LNKP", network?.shortName)
    }

    @Test
    fun `validate China T-Union prefix 31 exists`() {
        val network = CreditCardValidator.getNetworkByName("China T-Union")
        assertNotNull(network)
        assertEquals("TUN", network?.shortName)
    }

    @Test
    fun `validate China T-Union valid length is 19`() {
        val network = CreditCardValidator.getNetworkByName("China T-Union")
        assertEquals(setOf(19), network?.validLengths)
    }

    @Test
    fun `validate NPS Pridnestrovie prefix exists`() {
        val network = CreditCardValidator.getNetworkByName("NPS Pridnestrovie")
        assertNotNull(network)
        assertEquals("NPS", network?.shortName)
    }

    // ========================================================================
    // SECTION 22: BC Card and RuPay Tests
    // ========================================================================

    @Test
    fun `validate BC Card exists in networks`() {
        val network = CreditCardValidator.getNetworkByName("BC Card")
        assertNotNull(network)
        assertEquals("BC", network?.shortName)
    }

    @Test
    fun `validate RuPay exists in networks`() {
        val network = CreditCardValidator.getNetworkByName("RuPay")
        assertNotNull(network)
        assertEquals("RUPY", network?.shortName)
    }

    @Test
    fun `validate RuPay has regional category`() {
        val network = CreditCardValidator.getNetworkByName("RuPay")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    // ========================================================================
    // SECTION 23: Invalid Input Validation Tests
    // ========================================================================

    @Test
    fun `validate empty string is invalid`() {
        val result = CreditCardValidator.validate("")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("No digits"))
    }

    @Test
    fun `validate whitespace only is invalid`() {
        val result = CreditCardValidator.validate("   ")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate letters only is invalid`() {
        val result = CreditCardValidator.validate("abcdefghijklmnop")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate single digit is invalid`() {
        val result = CreditCardValidator.validate("4")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("too short"))
    }

    @Test
    fun `validate 7 digits is too short`() {
        val result = CreditCardValidator.validate("4111111")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("too short"))
    }

    @Test
    fun `validate 20 digits is too long`() {
        val result = CreditCardValidator.validate("41111111111111111111")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("too long"))
    }

    @Test
    fun `validate 25 digits is too long`() {
        val result = CreditCardValidator.validate("4111111111111111111111111")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate all zeros 16 digits passes Luhn but has no network`() {
        val result = CreditCardValidator.validate("0000000000000000")
        assertTrue(result.luhnValid)
    }

    @Test
    fun `validate special characters only is invalid`() {
        val result = CreditCardValidator.validate("!@#$%^&*()")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate null-like empty is invalid`() {
        val result = CreditCardValidator.validate("")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate number with letters mixed is handled`() {
        val result = CreditCardValidator.validate("4111abcd11111111")
        // extractDigits would produce "4111111111" which is only 10 digits - may be invalid
        assertNotNull(result)
    }

    @Test
    fun `validate valid Luhn but unknown prefix`() {
        // Use a number that passes Luhn but starts with an unusual prefix
        val partial = "990000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.luhnValid)
    }

    @Test
    fun `validate 8 digits is minimum accepted length`() {
        val partial = "4111111"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        // 8 digits is accepted but may not be valid for Visa
        assertNotNull(result)
    }

    // ========================================================================
    // SECTION 24: Card Number Formatting Tests
    // ========================================================================

    @Test
    fun `formatCardNumber Visa style 4-4-4-4`() {
        val formatted = CreditCardValidator.formatCardNumber("4111111111111111")
        assertEquals("4111 1111 1111 1111", formatted)
    }

    @Test
    fun `formatCardNumber with explicit Visa network`() {
        val visa = CreditCardValidator.getNetworkByName("Visa")
        val formatted = CreditCardValidator.formatCardNumber("4111111111111111", visa)
        assertEquals("4111 1111 1111 1111", formatted)
    }

    @Test
    fun `formatCardNumber Amex style 4-6-5`() {
        val amex = CreditCardValidator.getNetworkByName("American Express")
        val formatted = CreditCardValidator.formatCardNumber("378282246310005", amex)
        assertEquals("3782 822463 10005", formatted)
    }

    @Test
    fun `formatCardNumber with null network uses default spacing`() {
        val formatted = CreditCardValidator.formatCardNumber("1234567890123456")
        assertEquals("1234 5678 9012 3456", formatted)
    }

    @Test
    fun `formatCardNumber short number`() {
        val formatted = CreditCardValidator.formatCardNumber("1234")
        assertEquals("1234", formatted)
    }

    @Test
    fun `formatCardNumber empty string`() {
        val formatted = CreditCardValidator.formatCardNumber("")
        assertEquals("", formatted)
    }

    @Test
    fun `formatCardNumber 13 digit Visa`() {
        val visa = CreditCardValidator.getNetworkByName("Visa")
        val formatted = CreditCardValidator.formatCardNumber("4222222222222", visa)
        assertEquals("4222 2222 2222 2", formatted)
    }

    @Test
    fun `formatCardNumber 19 digit number`() {
        val formatted = CreditCardValidator.formatCardNumber("4111111111111111111")
        assertEquals("4111 1111 1111 1111 111", formatted)
    }

    // ========================================================================
    // SECTION 25: Card Number Masking Tests
    // ========================================================================

    @Test
    fun `maskCardNumber shows only last 4 for Visa`() {
        val masked = CreditCardValidator.maskCardNumber("4111111111111111")
        assertTrue(masked.endsWith("1111"))
        assertTrue(masked.contains("*"))
    }

    @Test
    fun `maskCardNumber for 16 digit number`() {
        val masked = CreditCardValidator.maskCardNumber("4111111111111111")
        assertEquals("**** **** **** 1111", masked)
    }

    @Test
    fun `maskCardNumber for Amex 15 digit number`() {
        val amex = CreditCardValidator.getNetworkByName("American Express")
        val masked = CreditCardValidator.maskCardNumber("378282246310005", amex)
        assertTrue(masked.endsWith("0005"))
        assertTrue(masked.contains("*"))
    }

    @Test
    fun `maskCardNumber for 4 or fewer digits returns digits`() {
        val masked = CreditCardValidator.maskCardNumber("1234")
        assertEquals("1234", masked)
    }

    @Test
    fun `maskCardNumber for 5 digit number masks first digit`() {
        val masked = CreditCardValidator.maskCardNumber("12345")
        assertTrue(masked.endsWith("2345"))
        assertTrue(masked.contains("*"))
    }

    @Test
    fun `maskCardNumber for 13 digit number`() {
        val masked = CreditCardValidator.maskCardNumber("4222222222222")
        assertTrue(masked.endsWith("2222"))
    }

    @Test
    fun `maskCardNumber with network spacing`() {
        val visa = CreditCardValidator.getNetworkByName("Visa")
        val masked = CreditCardValidator.maskCardNumber("4111111111111111", visa)
        assertEquals("**** **** **** 1111", masked)
    }

    // ========================================================================
    // SECTION 26: BIN Masking Tests
    // ========================================================================

    @Test
    fun `binMaskCardNumber shows first 6 and last 4`() {
        val masked = CreditCardValidator.binMaskCardNumber("4111111111111111")
        assertTrue(masked.startsWith("4111 11"))
        assertTrue(masked.endsWith("1111"))
        assertTrue(masked.contains("*"))
    }

    @Test
    fun `binMaskCardNumber for 10 or fewer digits falls back to regular mask`() {
        val masked = CreditCardValidator.binMaskCardNumber("1234567890")
        assertTrue(masked.endsWith("7890"))
    }

    @Test
    fun `binMaskCardNumber for 11 digit number shows BIN and last 4`() {
        val masked = CreditCardValidator.binMaskCardNumber("12345678901")
        assertTrue(masked.startsWith("1234"))
    }

    @Test
    fun `binMaskCardNumber preserves BIN for 16 digit number`() {
        val masked = CreditCardValidator.binMaskCardNumber("5555555555554444")
        // First 6: 555555, last 4: 4444, middle 6 masked
        assertTrue(masked.contains("5555 55"))
        assertTrue(masked.endsWith("4444"))
    }

    // ========================================================================
    // SECTION 27: BIN Extraction Tests
    // ========================================================================

    @Test
    fun `extractBIN returns first 6 digits by default`() {
        val bin = CreditCardValidator.extractBIN("4111111111111111")
        assertEquals("411111", bin)
    }

    @Test
    fun `extractBIN returns first 8 digits when requested`() {
        val bin = CreditCardValidator.extractBIN("4111111111111111", 8)
        assertEquals("41111111", bin)
    }

    @Test
    fun `extractBIN strips formatting`() {
        val bin = CreditCardValidator.extractBIN("4111 1111 1111 1111")
        assertEquals("411111", bin)
    }

    @Test
    fun `extractBIN returns null for too short number`() {
        val bin = CreditCardValidator.extractBIN("41111")
        assertNull(bin)
    }

    @Test
    fun `extractBIN returns null for empty string`() {
        val bin = CreditCardValidator.extractBIN("")
        assertNull(bin)
    }

    @Test
    fun `extractBIN for Amex card`() {
        val bin = CreditCardValidator.extractBIN("378282246310005")
        assertEquals("378282", bin)
    }

    @Test
    fun `extractBIN for Mastercard`() {
        val bin = CreditCardValidator.extractBIN("5555555555554444")
        assertEquals("555555", bin)
    }

    @Test
    fun `extractBIN for Discover card`() {
        val bin = CreditCardValidator.extractBIN("6011111111111117")
        assertEquals("601111", bin)
    }

    @Test
    fun `extractBIN with 8-digit request for Amex`() {
        val bin = CreditCardValidator.extractBIN("378282246310005", 8)
        assertEquals("37828224", bin)
    }

    @Test
    fun `extractBIN returns null when number shorter than requested length`() {
        val bin = CreditCardValidator.extractBIN("41111", 8)
        assertNull(bin)
    }

    // ========================================================================
    // SECTION 28: Network Detection Tests
    // ========================================================================

    @Test
    fun `detectNetwork returns Visa for prefix 4`() {
        val network = CreditCardValidator.detectNetwork("4111111111111111")
        assertEquals("Visa", network?.name)
    }

    @Test
    fun `detectNetwork returns Mastercard for prefix 51`() {
        val network = CreditCardValidator.detectNetwork("5100000000000000")
        assertEquals("Mastercard", network?.name)
    }

    @Test
    fun `detectNetwork returns Mastercard for prefix 55`() {
        val network = CreditCardValidator.detectNetwork("5500000000000000")
        assertEquals("Mastercard", network?.name)
    }

    @Test
    fun `detectNetwork returns Amex for prefix 34`() {
        val network = CreditCardValidator.detectNetwork("340000000000000")
        assertEquals("American Express", network?.name)
    }

    @Test
    fun `detectNetwork returns Amex for prefix 37`() {
        val network = CreditCardValidator.detectNetwork("370000000000000")
        assertEquals("American Express", network?.name)
    }

    @Test
    fun `detectNetwork returns Discover for prefix 6011`() {
        val network = CreditCardValidator.detectNetwork("6011000000000000")
        assertEquals("Discover", network?.name)
    }

    @Test
    fun `detectNetwork returns JCB for prefix 3528`() {
        val network = CreditCardValidator.detectNetwork("3528000000000000")
        assertEquals("JCB", network?.name)
    }

    @Test
    fun `detectNetwork returns JCB for prefix 3589`() {
        val network = CreditCardValidator.detectNetwork("3589000000000000")
        assertEquals("JCB", network?.name)
    }

    @Test
    fun `detectNetwork returns null for empty string`() {
        val network = CreditCardValidator.detectNetwork("")
        assertNull(network)
    }

    @Test
    fun `detectNetwork returns Mir for prefix 2200`() {
        val network = CreditCardValidator.detectNetwork("2200000000000000")
        assertEquals("Mir", network?.name)
    }

    @Test
    fun `detectNetwork returns Elo for prefix 401178`() {
        val network = CreditCardValidator.detectNetwork("4011780000000000")
        assertEquals("Elo", network?.name)
    }

    @Test
    fun `detectNetwork returns Hipercard for prefix 606282`() {
        val network = CreditCardValidator.detectNetwork("6062820000000000")
        assertEquals("Hipercard", network?.name)
    }

    @Test
    fun `detectNetwork returns Troy for prefix 979200`() {
        val network = CreditCardValidator.detectNetwork("9792000000000000")
        assertEquals("Troy", network?.name)
    }

    @Test
    fun `detectNetwork returns Verve for prefix 506099`() {
        val network = CreditCardValidator.detectNetwork("5060990000000000")
        assertEquals("Verve", network?.name)
    }

    @Test
    fun `detectNetwork returns NPS Pridnestrovie for prefix 6054740`() {
        val network = CreditCardValidator.detectNetwork("6054740000000000")
        assertEquals("NPS Pridnestrovie", network?.name)
    }

    @Test
    fun `detectNetwork returns LankaPay for prefix 357111`() {
        val network = CreditCardValidator.detectNetwork("3571110000000000")
        assertEquals("LankaPay", network?.name)
    }

    @Test
    fun `detectNetwork prefers more specific match over generic`() {
        // 401178 is Elo, not generic Visa
        val network = CreditCardValidator.detectNetwork("4011780000000000")
        assertEquals("Elo", network?.name)
    }

    @Test
    fun `detectNetwork for Mastercard 2-series 2221`() {
        val network = CreditCardValidator.detectNetwork("2221000000000000")
        assertEquals("Mastercard", network?.name)
    }

    @Test
    fun `detectNetwork for Mastercard 2-series 2500`() {
        val network = CreditCardValidator.detectNetwork("2500000000000000")
        assertEquals("Mastercard", network?.name)
    }

    @Test
    fun `detectNetwork for Mastercard 2-series 2720`() {
        val network = CreditCardValidator.detectNetwork("2720000000000000")
        assertEquals("Mastercard", network?.name)
    }

    // ========================================================================
    // SECTION 29: Test Card Number Checks
    // ========================================================================

    @Test
    fun `isTestCardNumber returns true for Visa test card`() {
        assertTrue(CreditCardValidator.isTestCardNumber("4111111111111111"))
    }

    @Test
    fun `isTestCardNumber returns true for Visa test with spaces`() {
        assertTrue(CreditCardValidator.isTestCardNumber("4111 1111 1111 1111"))
    }

    @Test
    fun `isTestCardNumber returns true for Visa test with dashes`() {
        assertTrue(CreditCardValidator.isTestCardNumber("4111-1111-1111-1111"))
    }

    @Test
    fun `isTestCardNumber returns true for Mastercard test card`() {
        assertTrue(CreditCardValidator.isTestCardNumber("5555555555554444"))
    }

    @Test
    fun `isTestCardNumber returns true for Amex test card`() {
        assertTrue(CreditCardValidator.isTestCardNumber("378282246310005"))
    }

    @Test
    fun `isTestCardNumber returns true for Discover test card`() {
        assertTrue(CreditCardValidator.isTestCardNumber("6011111111111117"))
    }

    @Test
    fun `isTestCardNumber returns true for JCB test card`() {
        assertTrue(CreditCardValidator.isTestCardNumber("3530111333300000"))
    }

    @Test
    fun `isTestCardNumber returns true for Diners test card`() {
        assertTrue(CreditCardValidator.isTestCardNumber("30569309025904"))
    }

    @Test
    fun `isTestCardNumber returns true for UnionPay test card`() {
        assertTrue(CreditCardValidator.isTestCardNumber("6200000000000005"))
    }

    @Test
    fun `isTestCardNumber returns true for Maestro test card`() {
        assertTrue(CreditCardValidator.isTestCardNumber("6304000000000000"))
    }

    @Test
    fun `isTestCardNumber returns false for random valid card`() {
        val partial = "411111111111000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        assertFalse(CreditCardValidator.isTestCardNumber(partial + check))
    }

    @Test
    fun `isTestCardNumber returns false for empty string`() {
        assertFalse(CreditCardValidator.isTestCardNumber(""))
    }

    @Test
    fun `isTestCardNumber returns true for all Visa test cards`() {
        val visaCards = CreditCardValidator.testCardNumbers["Visa"]!!
        for (card in visaCards) {
            assertTrue("Failed for $card", CreditCardValidator.isTestCardNumber(card))
        }
    }

    @Test
    fun `isTestCardNumber returns true for all Mastercard test cards`() {
        val mcCards = CreditCardValidator.testCardNumbers["Mastercard"]!!
        for (card in mcCards) {
            assertTrue("Failed for $card", CreditCardValidator.isTestCardNumber(card))
        }
    }

    @Test
    fun `isTestCardNumber returns true for all Amex test cards`() {
        val amexCards = CreditCardValidator.testCardNumbers["American Express"]!!
        for (card in amexCards) {
            assertTrue("Failed for $card", CreditCardValidator.isTestCardNumber(card))
        }
    }

    @Test
    fun `isTestCardNumber returns true for all Discover test cards`() {
        val discCards = CreditCardValidator.testCardNumbers["Discover"]!!
        for (card in discCards) {
            assertTrue("Failed for $card", CreditCardValidator.isTestCardNumber(card))
        }
    }

    // ========================================================================
    // SECTION 30: generateTestCardNumber Tests
    // ========================================================================

    @Test
    fun `generateTestCardNumber for Visa produces valid number`() {
        val visa = CreditCardValidator.getNetworkByName("Visa")!!
        val testCard = CreditCardValidator.generateTestCardNumber(visa)
        val result = CreditCardValidator.validate(testCard)
        assertTrue(result.isValid)
    }

    @Test
    fun `generateTestCardNumber for Mastercard produces valid number`() {
        val mc = CreditCardValidator.getNetworkByName("Mastercard")!!
        val testCard = CreditCardValidator.generateTestCardNumber(mc)
        val result = CreditCardValidator.validate(testCard)
        assertTrue(result.isValid)
    }

    @Test
    fun `generateTestCardNumber for Amex produces valid number`() {
        val amex = CreditCardValidator.getNetworkByName("American Express")!!
        val testCard = CreditCardValidator.generateTestCardNumber(amex)
        val result = CreditCardValidator.validate(testCard)
        assertTrue(result.isValid)
    }

    @Test
    fun `generateTestCardNumber for Discover produces valid number`() {
        val disc = CreditCardValidator.getNetworkByName("Discover")!!
        val testCard = CreditCardValidator.generateTestCardNumber(disc)
        val result = CreditCardValidator.validate(testCard)
        assertTrue(result.isValid)
    }

    @Test
    fun `generateTestCardNumber for JCB produces valid number`() {
        val jcb = CreditCardValidator.getNetworkByName("JCB")!!
        val testCard = CreditCardValidator.generateTestCardNumber(jcb)
        val result = CreditCardValidator.validate(testCard)
        assertTrue(result.isValid)
    }

    @Test
    fun `generateTestCardNumber for Mir produces valid number`() {
        val mir = CreditCardValidator.getNetworkByName("Mir")!!
        val testCard = CreditCardValidator.generateTestCardNumber(mir)
        val result = CreditCardValidator.validate(testCard)
        assertTrue(result.isValid)
    }

    @Test
    fun `generateTestCardNumber passes Luhn check`() {
        val visa = CreditCardValidator.getNetworkByName("Visa")!!
        val testCard = CreditCardValidator.generateTestCardNumber(visa)
        assertTrue(CreditCardValidator.luhnCheck(testCard))
    }

    @Test
    fun `generateTestCardNumber has correct length`() {
        val visa = CreditCardValidator.getNetworkByName("Visa")!!
        val testCard = CreditCardValidator.generateTestCardNumber(visa)
        assertTrue(testCard.length in visa.validLengths)
    }

    // ========================================================================
    // SECTION 31: looksLikeCreditCard Tests
    // ========================================================================

    @Test
    fun `looksLikeCreditCard returns true for valid Visa`() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("4111111111111111"))
    }

    @Test
    fun `looksLikeCreditCard returns true for formatted Visa`() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("4111 1111 1111 1111"))
    }

    @Test
    fun `looksLikeCreditCard returns true for valid Mastercard`() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("5555555555554444"))
    }

    @Test
    fun `looksLikeCreditCard returns false for too short`() {
        assertFalse(CreditCardValidator.looksLikeCreditCard("411111"))
    }

    @Test
    fun `looksLikeCreditCard returns false for too long`() {
        assertFalse(CreditCardValidator.looksLikeCreditCard("41111111111111111111"))
    }

    @Test
    fun `looksLikeCreditCard returns false for failing Luhn`() {
        assertFalse(CreditCardValidator.looksLikeCreditCard("4111111111111112"))
    }

    @Test
    fun `looksLikeCreditCard returns false for too many non-digits`() {
        assertFalse(CreditCardValidator.looksLikeCreditCard("Card number is 4111-1111-1111-1111 here"))
    }

    @Test
    fun `looksLikeCreditCard returns false for empty string`() {
        assertFalse(CreditCardValidator.looksLikeCreditCard(""))
    }

    @Test
    fun `looksLikeCreditCard returns false for alphabetic text`() {
        assertFalse(CreditCardValidator.looksLikeCreditCard("not a credit card number"))
    }

    @Test
    fun `looksLikeCreditCard returns true for Amex test card`() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("378282246310005"))
    }

    @Test
    fun `looksLikeCreditCard returns true for Discover test card`() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("6011111111111117"))
    }

    @Test
    fun `looksLikeCreditCard returns true for 13-digit Visa`() {
        assertTrue(CreditCardValidator.looksLikeCreditCard("4222222222222"))
    }

    // ========================================================================
    // SECTION 32: extractFromText Tests
    // ========================================================================

    @Test
    fun `extractFromText finds single Visa in text`() {
        val results = CreditCardValidator.extractFromText("My card is 4111111111111111.")
        assertTrue(results.isNotEmpty())
        assertEquals("Visa", results[0].network?.name)
    }

    @Test
    fun `extractFromText finds Visa with spaces`() {
        val results = CreditCardValidator.extractFromText("Card: 4111 1111 1111 1111")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds Visa with dashes`() {
        val results = CreditCardValidator.extractFromText("Card: 4111-1111-1111-1111")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds multiple cards`() {
        val text = "Visa: 4111111111111111 MC: 5555555555554444"
        val results = CreditCardValidator.extractFromText(text)
        assertTrue(results.size >= 2)
    }

    @Test
    fun `extractFromText ignores invalid numbers`() {
        val results = CreditCardValidator.extractFromText("Not a card: 1234567890123456")
        // This may or may not find results depending on Luhn
        assertNotNull(results)
    }

    @Test
    fun `extractFromText returns empty for no cards`() {
        val results = CreditCardValidator.extractFromText("No credit cards here at all.")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText returns empty for empty text`() {
        val results = CreditCardValidator.extractFromText("")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText finds Amex in text`() {
        val results = CreditCardValidator.extractFromText("Amex: 378282246310005 end")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds Discover in text`() {
        val results = CreditCardValidator.extractFromText("Pay with 6011111111111117")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText handles multiline text`() {
        val text = """
            First card: 4111111111111111
            Second card: 5555555555554444
            Third card: 378282246310005
        """.trimIndent()
        val results = CreditCardValidator.extractFromText(text)
        assertTrue(results.size >= 2)
    }

    @Test
    fun `extractFromText skips short digit sequences`() {
        val results = CreditCardValidator.extractFromText("Phone: 555-1234 Zip: 90210")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText finds card in JSON payload`() {
        val json = """{"card_number": "4111111111111111", "exp": "12/25"}"""
        val results = CreditCardValidator.extractFromText(json)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds card in URL parameter`() {
        val url = "https://example.com/pay?cc=4111111111111111&exp=1225"
        val results = CreditCardValidator.extractFromText(url)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds card in log line`() {
        val log = "2024-01-01 INFO Payment processed with card 4111111111111111"
        val results = CreditCardValidator.extractFromText(log)
        assertTrue(results.isNotEmpty())
    }

    // ========================================================================
    // SECTION 33: Network Category Queries
    // ========================================================================

    @Test
    fun `getNetworksByCategory MAJOR returns Visa MC Amex Discover`() {
        val major = CreditCardValidator.getNetworksByCategory(CreditCardValidator.CardCategory.MAJOR)
        assertTrue(major.size >= 4)
        assertTrue(major.any { it.name == "Visa" })
        assertTrue(major.any { it.name == "Mastercard" })
        assertTrue(major.any { it.name == "American Express" })
        assertTrue(major.any { it.name == "Discover" })
    }

    @Test
    fun `getNetworksByCategory REGIONAL returns multiple networks`() {
        val regional = CreditCardValidator.getNetworksByCategory(CreditCardValidator.CardCategory.REGIONAL)
        assertTrue(regional.size >= 10)
    }

    @Test
    fun `getNetworksByCategory DEPRECATED returns Laser Solo Switch`() {
        val deprecated = CreditCardValidator.getNetworksByCategory(CreditCardValidator.CardCategory.DEPRECATED)
        assertTrue(deprecated.size >= 3)
        assertTrue(deprecated.any { it.name == "Laser" })
        assertTrue(deprecated.any { it.name == "Solo" })
        assertTrue(deprecated.any { it.name == "Switch" })
    }

    @Test
    fun `getNetworksByCategory SPECIALTY includes UATP`() {
        val specialty = CreditCardValidator.getNetworksByCategory(CreditCardValidator.CardCategory.SPECIALTY)
        assertTrue(specialty.any { it.name == "UATP" })
    }

    // ========================================================================
    // SECTION 34: Network Lookup by Name and Short Name
    // ========================================================================

    @Test
    fun `getNetworkByName Visa returns correct network`() {
        val network = CreditCardValidator.getNetworkByName("Visa")
        assertNotNull(network)
        assertEquals("Visa", network?.name)
    }

    @Test
    fun `getNetworkByName is case insensitive`() {
        val network = CreditCardValidator.getNetworkByName("visa")
        assertNotNull(network)
        assertEquals("Visa", network?.name)
    }

    @Test
    fun `getNetworkByName VISA uppercase`() {
        val network = CreditCardValidator.getNetworkByName("VISA")
        assertNotNull(network)
    }

    @Test
    fun `getNetworkByName returns null for unknown network`() {
        val network = CreditCardValidator.getNetworkByName("FakeNetwork")
        assertNull(network)
    }

    @Test
    fun `getNetworkByShortName VISA returns Visa`() {
        val network = CreditCardValidator.getNetworkByShortName("VISA")
        assertNotNull(network)
        assertEquals("Visa", network?.name)
    }

    @Test
    fun `getNetworkByShortName MC returns Mastercard`() {
        val network = CreditCardValidator.getNetworkByShortName("MC")
        assertNotNull(network)
        assertEquals("Mastercard", network?.name)
    }

    @Test
    fun `getNetworkByShortName AMEX returns American Express`() {
        val network = CreditCardValidator.getNetworkByShortName("AMEX")
        assertNotNull(network)
        assertEquals("American Express", network?.name)
    }

    @Test
    fun `getNetworkByShortName DISC returns Discover`() {
        val network = CreditCardValidator.getNetworkByShortName("DISC")
        assertNotNull(network)
        assertEquals("Discover", network?.name)
    }

    @Test
    fun `getNetworkByShortName is case insensitive`() {
        val network = CreditCardValidator.getNetworkByShortName("visa")
        assertNotNull(network)
    }

    @Test
    fun `getNetworkByShortName returns null for unknown`() {
        val network = CreditCardValidator.getNetworkByShortName("FAKE")
        assertNull(network)
    }

    // ========================================================================
    // SECTION 35: totalNetworks and activeNetworks Tests
    // ========================================================================

    @Test
    fun `totalNetworks returns at least 20`() {
        assertTrue(CreditCardValidator.totalNetworks() >= 20)
    }

    @Test
    fun `activeNetworks is fewer than totalNetworks`() {
        assertTrue(CreditCardValidator.activeNetworks() < CreditCardValidator.totalNetworks())
    }

    @Test
    fun `activeNetworks excludes deprecated`() {
        val deprecated = CreditCardValidator.getNetworksByCategory(CreditCardValidator.CardCategory.DEPRECATED)
        assertEquals(
            CreditCardValidator.totalNetworks() - deprecated.size,
            CreditCardValidator.activeNetworks()
        )
    }

    // ========================================================================
    // SECTION 36: Confidence Score Tests
    // ========================================================================

    @Test
    fun `confidence is 0_99 for valid Visa with prefix Luhn and length`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertEquals(0.99f, result.confidence, 0.01f)
    }

    @Test
    fun `confidence is lower when Luhn fails`() {
        val result = CreditCardValidator.validate("4111111111111112")
        assertTrue(result.confidence < 0.5f)
    }

    @Test
    fun `confidence is lower for unknown prefix`() {
        // Build a number with unknown prefix but valid Luhn
        val partial = "990000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.confidence < 0.99f)
    }

    @Test
    fun `confidence is 0_99 for valid Mastercard`() {
        val result = CreditCardValidator.validate("5555555555554444")
        assertEquals(0.99f, result.confidence, 0.01f)
    }

    @Test
    fun `confidence is 0_99 for valid Amex`() {
        val result = CreditCardValidator.validate("378282246310005")
        assertEquals(0.99f, result.confidence, 0.01f)
    }

    // ========================================================================
    // SECTION 37: Validation Reason String Tests
    // ========================================================================

    @Test
    fun `reason for valid Visa mentions Visa name`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertTrue(result.reason.contains("Visa"))
    }

    @Test
    fun `reason for valid Mastercard mentions Mastercard name`() {
        val result = CreditCardValidator.validate("5555555555554444")
        assertTrue(result.reason.contains("Mastercard"))
    }

    @Test
    fun `reason for invalid Luhn mentions Luhn`() {
        val result = CreditCardValidator.validate("4111111111111112")
        assertTrue(result.reason.contains("Luhn"))
    }

    @Test
    fun `reason for too short mentions too short`() {
        val result = CreditCardValidator.validate("4111")
        assertTrue(result.reason.contains("short"))
    }

    @Test
    fun `reason for too long mentions too long`() {
        val result = CreditCardValidator.validate("41111111111111111111")
        assertTrue(result.reason.contains("long"))
    }

    @Test
    fun `reason for empty mentions no digits`() {
        val result = CreditCardValidator.validate("")
        assertTrue(result.reason.contains("No digits"))
    }

    @Test
    fun `reason for valid card mentions digit count`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertTrue(result.reason.contains("16"))
    }

    // ========================================================================
    // SECTION 38: CardValidationResult Properties Tests
    // ========================================================================

    @Test
    fun `valid card result has all boolean flags true except isValid when Luhn fails`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertTrue(result.isValid)
        assertTrue(result.luhnValid)
        assertTrue(result.lengthValid)
        assertTrue(result.prefixValid)
    }

    @Test
    fun `invalid card result has isValid false`() {
        val result = CreditCardValidator.validate("4111111111111112")
        assertFalse(result.isValid)
    }

    @Test
    fun `valid card result has non-empty formatted string`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertTrue(result.formatted.isNotEmpty())
    }

    @Test
    fun `valid card result has non-empty masked string`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertTrue(result.masked.isNotEmpty())
    }

    @Test
    fun `valid card result has non-null network`() {
        val result = CreditCardValidator.validate("4111111111111111")
        assertNotNull(result.network)
    }

    @Test
    fun `invalid card result for empty has null network`() {
        val result = CreditCardValidator.validate("")
        assertNull(result.network)
    }

    // ========================================================================
    // SECTION 39: CardNetwork Data Class Tests
    // ========================================================================

    @Test
    fun `CardNetwork equals same data`() {
        val n1 = CreditCardValidator.CardNetwork(
            name = "Test", shortName = "T",
            prefixes = listOf(CreditCardValidator.PrefixRange(4, 1)),
            validLengths = setOf(16)
        )
        val n2 = CreditCardValidator.CardNetwork(
            name = "Test", shortName = "T",
            prefixes = listOf(CreditCardValidator.PrefixRange(4, 1)),
            validLengths = setOf(16)
        )
        assertEquals(n1, n2)
    }

    @Test
    fun `CardNetwork not equal different name`() {
        val n1 = CreditCardValidator.CardNetwork(
            name = "Test1", shortName = "T",
            prefixes = listOf(CreditCardValidator.PrefixRange(4, 1)),
            validLengths = setOf(16)
        )
        val n2 = CreditCardValidator.CardNetwork(
            name = "Test2", shortName = "T",
            prefixes = listOf(CreditCardValidator.PrefixRange(4, 1)),
            validLengths = setOf(16)
        )
        assertNotEquals(n1, n2)
    }

    @Test
    fun `PrefixRange single value constructor sets start equal to end`() {
        val range = CreditCardValidator.PrefixRange(42, 2)
        assertEquals(42L, range.start)
        assertEquals(42L, range.end)
        assertEquals(2, range.length)
    }

    @Test
    fun `PrefixRange range constructor has different start and end`() {
        val range = CreditCardValidator.PrefixRange(51, 55, 2)
        assertEquals(51L, range.start)
        assertEquals(55L, range.end)
        assertEquals(2, range.length)
    }

    // ========================================================================
    // SECTION 40: CardCategory Enum Tests
    // ========================================================================

    @Test
    fun `CardCategory has MAJOR value`() {
        assertNotNull(CreditCardValidator.CardCategory.MAJOR)
    }

    @Test
    fun `CardCategory has REGIONAL value`() {
        assertNotNull(CreditCardValidator.CardCategory.REGIONAL)
    }

    @Test
    fun `CardCategory has DEPRECATED value`() {
        assertNotNull(CreditCardValidator.CardCategory.DEPRECATED)
    }

    @Test
    fun `CardCategory has SPECIALTY value`() {
        assertNotNull(CreditCardValidator.CardCategory.SPECIALTY)
    }

    @Test
    fun `CardCategory values count is 4`() {
        assertEquals(4, CreditCardValidator.CardCategory.values().size)
    }

    // ========================================================================
    // SECTION 41: Edge Cases and Boundary Tests
    // ========================================================================

    @Test
    fun `validate handles card number with leading zeros`() {
        val result = CreditCardValidator.validate("0000000000000000")
        // All zeros passes Luhn but likely no known network
        assertTrue(result.luhnValid)
    }

    @Test
    fun `validate handles card number at exact max length 19`() {
        val partial = "411111111111111111"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertNotNull(result)
        assertEquals(19, CreditCardValidator.extractDigits(partial + check).length)
    }

    @Test
    fun `validate handles card number at exact min length 8`() {
        val partial = "4111111"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertNotNull(result)
    }

    @Test
    fun `validate card with only spaces between digits`() {
        val result = CreditCardValidator.validate("4 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate card with tabs between digit groups`() {
        val result = CreditCardValidator.validate("4111\t1111\t1111\t1111")
        // tabs are non-digit, so extractDigits strips them
        assertNotNull(result)
    }

    @Test
    fun `validate card with newline in number`() {
        val result = CreditCardValidator.validate("41111111\n11111111")
        assertNotNull(result)
    }

    @Test
    fun `validate card with parentheses`() {
        val result = CreditCardValidator.validate("(4111) 1111 1111 1111")
        assertNotNull(result)
    }

    @Test
    fun `validate card with prefix text is handled`() {
        val result = CreditCardValidator.validate("CC:4111111111111111")
        assertNotNull(result)
    }

    @Test
    fun `validate extremely long string with embedded card`() {
        val padding = "a".repeat(100)
        val result = CreditCardValidator.validate(padding + "4111111111111111" + padding)
        // extractDigits gets only the 16 digits, works fine
        assertNotNull(result)
    }

    @Test
    fun `validate unicode characters in input`() {
        val result = CreditCardValidator.validate("\u00A04111111111111111")
        assertNotNull(result)
    }

    @Test
    fun `validate repeated validate calls produce consistent results`() {
        val result1 = CreditCardValidator.validate("4111111111111111")
        val result2 = CreditCardValidator.validate("4111111111111111")
        assertEquals(result1.isValid, result2.isValid)
        assertEquals(result1.network?.name, result2.network?.name)
        assertEquals(result1.confidence, result2.confidence)
    }

    // ========================================================================
    // SECTION 42: Additional Visa Variants
    // ========================================================================

    @Test
    fun `validate Visa 19 digit card`() {
        val partial = "411111111111111111"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Visa", result.network?.name)
    }

    @Test
    fun `validate Visa prefix 40 is valid`() {
        val partial = "400000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 41 is valid`() {
        val partial = "410000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 42 is valid`() {
        val partial = "420000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 43 is valid`() {
        val partial = "430000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 44 is valid`() {
        val partial = "440000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 45 is valid`() {
        val partial = "450000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 46 is valid`() {
        val partial = "460000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 47 is valid`() {
        val partial = "470000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 48 is valid`() {
        val partial = "480000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Visa prefix 49 is valid`() {
        val partial = "490000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 43: Additional Mastercard 2-series boundary tests
    // ========================================================================

    @Test
    fun `validate Mastercard 2-series 2222 is valid`() {
        val partial = "222200000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Mastercard 2-series 2300 is valid`() {
        val partial = "230000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Mastercard 2-series 2400 is valid`() {
        val partial = "240000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Mastercard 2-series 2600 is valid`() {
        val partial = "260000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Mastercard 2-series 2700 is valid`() {
        val partial = "270000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Mastercard 2-series 2719 is valid`() {
        val partial = "271900000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 44: Test Card Numbers Map Structure Tests
    // ========================================================================

    @Test
    fun `testCardNumbers map has Visa key`() {
        assertTrue(CreditCardValidator.testCardNumbers.containsKey("Visa"))
    }

    @Test
    fun `testCardNumbers map has Mastercard key`() {
        assertTrue(CreditCardValidator.testCardNumbers.containsKey("Mastercard"))
    }

    @Test
    fun `testCardNumbers map has American Express key`() {
        assertTrue(CreditCardValidator.testCardNumbers.containsKey("American Express"))
    }

    @Test
    fun `testCardNumbers map has Discover key`() {
        assertTrue(CreditCardValidator.testCardNumbers.containsKey("Discover"))
    }

    @Test
    fun `testCardNumbers map has Diners Club key`() {
        assertTrue(CreditCardValidator.testCardNumbers.containsKey("Diners Club"))
    }

    @Test
    fun `testCardNumbers map has JCB key`() {
        assertTrue(CreditCardValidator.testCardNumbers.containsKey("JCB"))
    }

    @Test
    fun `testCardNumbers map has UnionPay key`() {
        assertTrue(CreditCardValidator.testCardNumbers.containsKey("UnionPay"))
    }

    @Test
    fun `testCardNumbers map has Maestro key`() {
        assertTrue(CreditCardValidator.testCardNumbers.containsKey("Maestro"))
    }

    @Test
    fun `all Visa test card numbers pass Luhn`() {
        for (card in CreditCardValidator.testCardNumbers["Visa"]!!) {
            assertTrue("Card $card should pass Luhn", CreditCardValidator.luhnCheck(card))
        }
    }

    @Test
    fun `all Mastercard test card numbers pass Luhn`() {
        for (card in CreditCardValidator.testCardNumbers["Mastercard"]!!) {
            assertTrue("Card $card should pass Luhn", CreditCardValidator.luhnCheck(card))
        }
    }

    @Test
    fun `all Amex test card numbers pass Luhn`() {
        for (card in CreditCardValidator.testCardNumbers["American Express"]!!) {
            assertTrue("Card $card should pass Luhn", CreditCardValidator.luhnCheck(card))
        }
    }

    @Test
    fun `all Discover test card numbers pass Luhn`() {
        for (card in CreditCardValidator.testCardNumbers["Discover"]!!) {
            assertTrue("Card $card should pass Luhn", CreditCardValidator.luhnCheck(card))
        }
    }

    @Test
    fun `all Diners Club test card numbers pass Luhn`() {
        for (card in CreditCardValidator.testCardNumbers["Diners Club"]!!) {
            assertTrue("Card $card should pass Luhn", CreditCardValidator.luhnCheck(card))
        }
    }

    @Test
    fun `all JCB test card numbers pass Luhn`() {
        for (card in CreditCardValidator.testCardNumbers["JCB"]!!) {
            assertTrue("Card $card should pass Luhn", CreditCardValidator.luhnCheck(card))
        }
    }

    @Test
    fun `all Maestro test card numbers pass Luhn`() {
        for (card in CreditCardValidator.testCardNumbers["Maestro"]!!) {
            assertTrue("Card $card should pass Luhn", CreditCardValidator.luhnCheck(card))
        }
    }

    // ========================================================================
    // SECTION 45: Comprehensive Network List Tests
    // ========================================================================

    @Test
    fun `networks list contains Visa`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Visa" })
    }

    @Test
    fun `networks list contains Mastercard`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Mastercard" })
    }

    @Test
    fun `networks list contains American Express`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "American Express" })
    }

    @Test
    fun `networks list contains Discover`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Discover" })
    }

    @Test
    fun `networks list contains JCB`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "JCB" })
    }

    @Test
    fun `networks list contains Diners Club Carte Blanche`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Diners Club Carte Blanche" })
    }

    @Test
    fun `networks list contains Diners Club International`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Diners Club International" })
    }

    @Test
    fun `networks list contains UnionPay`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "UnionPay" })
    }

    @Test
    fun `networks list contains Maestro`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Maestro" })
    }

    @Test
    fun `networks list contains Maestro UK`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Maestro UK" })
    }

    @Test
    fun `networks list contains Mir`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Mir" })
    }

    @Test
    fun `networks list contains Elo`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Elo" })
    }

    @Test
    fun `networks list contains Hipercard`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Hipercard" })
    }

    @Test
    fun `networks list contains RuPay`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "RuPay" })
    }

    @Test
    fun `networks list contains Troy`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Troy" })
    }

    @Test
    fun `networks list contains Verve`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Verve" })
    }

    @Test
    fun `networks list contains BC Card`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "BC Card" })
    }

    @Test
    fun `networks list contains InterPayment`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "InterPayment" })
    }

    @Test
    fun `networks list contains InstaPayment`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "InstaPayment" })
    }

    @Test
    fun `networks list contains Dankort`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Dankort" })
    }

    @Test
    fun `networks list contains LankaPay`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "LankaPay" })
    }

    @Test
    fun `networks list contains China T-Union`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "China T-Union" })
    }

    @Test
    fun `networks list contains NPS Pridnestrovie`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "NPS Pridnestrovie" })
    }

    @Test
    fun `networks list contains UATP`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "UATP" })
    }

    @Test
    fun `networks list contains Laser`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Laser" })
    }

    @Test
    fun `networks list contains Solo`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Solo" })
    }

    @Test
    fun `networks list contains Switch`() {
        assertTrue(CreditCardValidator.networks.any { it.name == "Switch" })
    }

    // ========================================================================
    // SECTION 46: All networks use Luhn except UnionPay
    // ========================================================================

    @Test
    fun `Visa uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("Visa")
        assertTrue(network!!.usesLuhn)
    }

    @Test
    fun `Mastercard uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("Mastercard")
        assertTrue(network!!.usesLuhn)
    }

    @Test
    fun `American Express uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("American Express")
        assertTrue(network!!.usesLuhn)
    }

    @Test
    fun `Discover uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("Discover")
        assertTrue(network!!.usesLuhn)
    }

    @Test
    fun `JCB uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("JCB")
        assertTrue(network!!.usesLuhn)
    }

    @Test
    fun `UnionPay does not use Luhn`() {
        val network = CreditCardValidator.getNetworkByName("UnionPay")
        assertFalse(network!!.usesLuhn)
    }

    @Test
    fun `Maestro uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("Maestro")
        assertTrue(network!!.usesLuhn)
    }

    @Test
    fun `Mir uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("Mir")
        assertTrue(network!!.usesLuhn)
    }

    @Test
    fun `Elo uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("Elo")
        assertTrue(network!!.usesLuhn)
    }

    @Test
    fun `UATP uses Luhn`() {
        val network = CreditCardValidator.getNetworkByName("UATP")
        assertTrue(network!!.usesLuhn)
    }

    // ========================================================================
    // SECTION 47: Spacing Pattern Tests
    // ========================================================================

    @Test
    fun `Visa spacing is 4-4-4-4`() {
        val network = CreditCardValidator.getNetworkByName("Visa")
        assertEquals(listOf(4, 4, 4, 4), network?.spacing)
    }

    @Test
    fun `Amex spacing is 4-6-5`() {
        val network = CreditCardValidator.getNetworkByName("American Express")
        assertEquals(listOf(4, 6, 5), network?.spacing)
    }

    @Test
    fun `Diners Club Carte Blanche spacing is 4-6-4`() {
        val network = CreditCardValidator.getNetworkByName("Diners Club Carte Blanche")
        assertEquals(listOf(4, 6, 4), network?.spacing)
    }

    @Test
    fun `UATP spacing is 4-5-6`() {
        val network = CreditCardValidator.getNetworkByName("UATP")
        assertEquals(listOf(4, 5, 6), network?.spacing)
    }

    @Test
    fun `China T-Union spacing is 4-4-4-4-3`() {
        val network = CreditCardValidator.getNetworkByName("China T-Union")
        assertEquals(listOf(4, 4, 4, 4, 3), network?.spacing)
    }

    // ========================================================================
    // SECTION 48: Luhn Algorithm with all-same-digit strings
    // ========================================================================

    @Test
    fun `luhnCheck for 16 twos`() {
        assertFalse(CreditCardValidator.luhnCheck("2222222222222222"))
    }

    @Test
    fun `luhnCheck for 16 threes`() {
        assertFalse(CreditCardValidator.luhnCheck("3333333333333333"))
    }

    @Test
    fun `luhnCheck for 16 fours`() {
        assertFalse(CreditCardValidator.luhnCheck("4444444444444444"))
    }

    @Test
    fun `luhnCheck for 16 fives`() {
        assertFalse(CreditCardValidator.luhnCheck("5555555555555555"))
    }

    @Test
    fun `luhnCheck for 16 sixes`() {
        assertFalse(CreditCardValidator.luhnCheck("6666666666666666"))
    }

    @Test
    fun `luhnCheck for 16 sevens`() {
        assertFalse(CreditCardValidator.luhnCheck("7777777777777777"))
    }

    @Test
    fun `luhnCheck for 16 eights`() {
        assertFalse(CreditCardValidator.luhnCheck("8888888888888888"))
    }

    // ========================================================================
    // SECTION 49: Additional extractFromText with various formats
    // ========================================================================

    @Test
    fun `extractFromText finds card in email body`() {
        val email = """
            Dear customer,
            Your payment of $100 was charged to card ending in 1111.
            Full card: 4111111111111111
            Thank you.
        """.trimIndent()
        val results = CreditCardValidator.extractFromText(email)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds card in CSV data`() {
        val csv = "name,card,amount\nJohn,4111111111111111,50.00"
        val results = CreditCardValidator.extractFromText(csv)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds card in XML data`() {
        val xml = "<payment><card>4111111111111111</card></payment>"
        val results = CreditCardValidator.extractFromText(xml)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText handles text with phone numbers without false positive`() {
        val text = "Call us at 1-800-555-1234 for support."
        val results = CreditCardValidator.extractFromText(text)
        // Phone numbers should not match as cards (too short or fail Luhn)
        for (result in results) {
            assertTrue(result.isValid)
        }
    }

    @Test
    fun `extractFromText handles text with SSN without false positive`() {
        val text = "SSN: 123-45-6789"
        val results = CreditCardValidator.extractFromText(text)
        // SSN is 9 digits, typically won't match 13-19 digit pattern
        assertTrue(results.isEmpty())
    }

    // ========================================================================
    // SECTION 50: Diners Club USA/Canada Tests
    // ========================================================================

    @Test
    fun `validate Diners Club USA Canada prefix 54 detected`() {
        val partial = "540000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Diners Club USA Canada valid length is 16`() {
        val network = CreditCardValidator.getNetworkByName("Diners Club USA/Canada")
        assertEquals(setOf(16), network?.validLengths)
    }

    // ========================================================================
    // SECTION 51: Maestro UK Tests
    // ========================================================================

    @Test
    fun `validate Maestro UK exists in networks`() {
        val network = CreditCardValidator.getNetworkByName("Maestro UK")
        assertNotNull(network)
    }

    @Test
    fun `validate Maestro UK short name is MAES`() {
        val network = CreditCardValidator.getNetworkByName("Maestro UK")
        assertEquals("MAES", network?.shortName)
    }

    @Test
    fun `validate Maestro UK has regional category`() {
        val network = CreditCardValidator.getNetworkByName("Maestro UK")
        assertEquals(CreditCardValidator.CardCategory.REGIONAL, network?.category)
    }

    @Test
    fun `validate Maestro UK supports 12 to 19 digits`() {
        val network = CreditCardValidator.getNetworkByName("Maestro UK")
        assertTrue(network?.validLengths?.contains(12) == true)
        assertTrue(network?.validLengths?.contains(19) == true)
    }

    // ========================================================================
    // SECTION 52: Regression and Integration-Style Tests
    // ========================================================================

    @Test
    fun `validate and then format round trip for Visa`() {
        val original = "4111111111111111"
        val result = CreditCardValidator.validate(original)
        assertTrue(result.isValid)
        val digits = CreditCardValidator.extractDigits(result.formatted)
        assertEquals(original, digits)
    }

    @Test
    fun `validate and then format round trip for Amex`() {
        val original = "378282246310005"
        val result = CreditCardValidator.validate(original)
        assertTrue(result.isValid)
        val digits = CreditCardValidator.extractDigits(result.formatted)
        assertEquals(original, digits)
    }

    @Test
    fun `validate multiple different card types sequentially`() {
        val cards = listOf(
            "4111111111111111" to "Visa",
            "5555555555554444" to "Mastercard",
            "378282246310005" to "American Express",
            "6011111111111117" to "Discover",
            "3530111333300000" to "JCB"
        )
        for ((card, expectedNetwork) in cards) {
            val result = CreditCardValidator.validate(card)
            assertTrue("$card should be valid", result.isValid)
            assertEquals("$card should be $expectedNetwork", expectedNetwork, result.network?.name)
        }
    }

    @Test
    fun `validate same card number twice gives identical results`() {
        val card = "5555555555554444"
        val r1 = CreditCardValidator.validate(card)
        val r2 = CreditCardValidator.validate(card)
        assertEquals(r1.isValid, r2.isValid)
        assertEquals(r1.network?.name, r2.network?.name)
        assertEquals(r1.luhnValid, r2.luhnValid)
        assertEquals(r1.lengthValid, r2.lengthValid)
        assertEquals(r1.prefixValid, r2.prefixValid)
        assertEquals(r1.formatted, r2.formatted)
        assertEquals(r1.masked, r2.masked)
        assertEquals(r1.confidence, r2.confidence)
    }

    @Test
    fun `validate formatted and unformatted give same validity`() {
        val unformatted = "4111111111111111"
        val formatted = "4111 1111 1111 1111"
        val r1 = CreditCardValidator.validate(unformatted)
        val r2 = CreditCardValidator.validate(formatted)
        assertEquals(r1.isValid, r2.isValid)
        assertEquals(r1.network?.name, r2.network?.name)
    }

    @Test
    fun `extractFromText then validate each found card`() {
        val text = "Cards: 4111111111111111, 5555555555554444"
        val found = CreditCardValidator.extractFromText(text)
        for (result in found) {
            assertTrue(result.isValid)
            assertNotNull(result.network)
        }
    }

    @Test
    fun `validate with Elo prefix 457631`() {
        val partial = "457631000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Elo", result.network?.name)
    }

    @Test
    fun `validate with Elo prefix 509000`() {
        val partial = "509000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Elo", result.network?.name)
    }

    @Test
    fun `validate with Elo prefix 627780`() {
        val partial = "627780000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Elo", result.network?.name)
    }

    @Test
    fun `validate with Elo prefix 636368`() {
        val partial = "636368000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Elo", result.network?.name)
    }

    @Test
    fun `validate Discover UnionPay co-branded prefix 622126`() {
        val partial = "622126000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover UnionPay co-branded prefix 622925`() {
        val partial = "622925000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover prefix 646 is valid`() {
        val partial = "646000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover prefix 647 is valid`() {
        val partial = "647000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate Discover prefix 648 is valid`() {
        val partial = "648000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("Discover", result.network?.name)
    }

    @Test
    fun `validate JCB prefix 3540 is valid`() {
        val partial = "354000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB prefix 3550 is valid`() {
        val partial = "355000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB prefix 3560 is valid`() {
        val partial = "356000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB prefix 3570 is valid`() {
        val partial = "357000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate JCB prefix 3580 is valid`() {
        val partial = "358000000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
        assertEquals("JCB", result.network?.name)
    }

    @Test
    fun `validate Diners Club Carte Blanche prefix 301 is valid`() {
        val partial = "3010000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Diners Club Carte Blanche prefix 302 is valid`() {
        val partial = "3020000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Diners Club Carte Blanche prefix 303 is valid`() {
        val partial = "3030000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Diners Club Carte Blanche prefix 304 is valid`() {
        val partial = "3040000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Diners Club Carte Blanche prefix 305 is valid`() {
        val partial = "3050000000000"
        val check = CreditCardValidator.generateLuhnCheckDigit(partial)
        val result = CreditCardValidator.validate(partial + check)
        assertTrue(result.isValid)
    }
}
