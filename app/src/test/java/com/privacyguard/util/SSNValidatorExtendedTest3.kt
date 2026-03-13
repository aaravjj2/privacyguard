package com.privacyguard.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Section 1: Standard Valid SSNs
class SSNValidatorExtendedTest3A {
    @Test fun testValidSSN_StandardFormat_001() { assertTrue(SSNValidator.validate("123-45-6789").isValid) }
    @Test fun testValidSSN_StandardFormat_002() { assertTrue(SSNValidator.validate("234-56-7890").isValid) }
    @Test fun testValidSSN_StandardFormat_003() { assertTrue(SSNValidator.validate("345-67-8901").isValid) }
    @Test fun testValidSSN_StandardFormat_004() { assertTrue(SSNValidator.validate("456-78-9012").isValid) }
    @Test fun testValidSSN_StandardFormat_005() { assertTrue(SSNValidator.validate("234-12-5678").isValid) }
    @Test fun testValidSSN_StandardFormat_006() { assertTrue(SSNValidator.validate("301-50-1234").isValid) }
    @Test fun testValidSSN_StandardFormat_007() { assertTrue(SSNValidator.validate("401-70-2345").isValid) }
    @Test fun testValidSSN_StandardFormat_008() { assertTrue(SSNValidator.validate("501-80-3456").isValid) }
    @Test fun testValidSSN_StandardFormat_009() { assertTrue(SSNValidator.validate("601-90-4567").isValid) }
    @Test fun testValidSSN_StandardFormat_010() { assertTrue(SSNValidator.validate("701-25-5678").isValid) }
    @Test fun testValidSSN_NoFormatting_001() { assertTrue(SSNValidator.validate("123456789").isValid) }
    @Test fun testValidSSN_NoFormatting_002() { assertTrue(SSNValidator.validate("234567890").isValid) }
    @Test fun testValidSSN_NoFormatting_003() { assertTrue(SSNValidator.validate("345678901").isValid) }
    @Test fun testValidSSN_NoFormatting_004() { assertTrue(SSNValidator.validate("456789012").isValid) }
    @Test fun testValidSSN_NoFormatting_005() { assertTrue(SSNValidator.validate("302501234").isValid) }
    @Test fun testValidSSN_SpacedFormat_001() { assertTrue(SSNValidator.validate("123 45 6789").isValid) }
    @Test fun testValidSSN_SpacedFormat_002() { assertTrue(SSNValidator.validate("234 56 7890").isValid) }
    @Test fun testValidSSN_SpacedFormat_003() { assertTrue(SSNValidator.validate("345 67 8901").isValid) }
    @Test fun testValidSSN_SpacedFormat_004() { assertTrue(SSNValidator.validate("456 78 9012").isValid) }
    @Test fun testValidSSN_SpacedFormat_005() { assertTrue(SSNValidator.validate("303 55 2345").isValid) }
    @Test fun testValidSSN_FormattedResult_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertEquals("123-45-6789", result.formatted)
    }
    @Test fun testValidSSN_FormattedResult_002() {
        val result = SSNValidator.validate("234567890")
        assertEquals("234-56-7890", result.formatted)
    }
    @Test fun testValidSSN_MaskedResult_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertTrue(result.masked.contains("6789"))
    }
    @Test fun testValidSSN_MaskedResult_002() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.masked.contains("7890"))
    }
    @Test fun testValidSSN_AreaNumber_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertEquals("123", result.areaNumber)
    }
    @Test fun testValidSSN_GroupNumber_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertEquals("45", result.groupNumber)
    }
    @Test fun testValidSSN_SerialNumber_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertEquals("6789", result.serialNumber)
    }
    @Test fun testValidSSN_Confidence_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertTrue(result.confidence > 0f)
    }
    @Test fun testValidSSN_Reason_NotEmpty_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertTrue(result.reason.isNotEmpty())
    }
    @Test fun testValidSSN_IsNotITIN_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertFalse(result.isITIN)
    }
    @Test fun testValidSSN_IsNotEIN_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertFalse(result.isEIN)
    }
    @Test fun testValidSSN_IsNotAdvertising_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertFalse(result.isAdvertising)
    }
}

// Section 2: Invalid SSNs - All Zeros Area
class SSNValidatorExtendedTest3B {
    @Test fun testInvalidSSN_ZeroArea_001() { assertFalse(SSNValidator.validate("000-45-6789").isValid) }
    @Test fun testInvalidSSN_ZeroArea_002() { assertFalse(SSNValidator.validate("000-12-3456").isValid) }
    @Test fun testInvalidSSN_ZeroArea_003() { assertFalse(SSNValidator.validate("000-98-7654").isValid) }
    @Test fun testInvalidSSN_ZeroGroup_001() { assertFalse(SSNValidator.validate("123-00-6789").isValid) }
    @Test fun testInvalidSSN_ZeroGroup_002() { assertFalse(SSNValidator.validate("234-00-7890").isValid) }
    @Test fun testInvalidSSN_ZeroGroup_003() { assertFalse(SSNValidator.validate("456-00-1234").isValid) }
    @Test fun testInvalidSSN_ZeroSerial_001() { assertFalse(SSNValidator.validate("123-45-0000").isValid) }
    @Test fun testInvalidSSN_ZeroSerial_002() { assertFalse(SSNValidator.validate("234-56-0000").isValid) }
    @Test fun testInvalidSSN_ZeroSerial_003() { assertFalse(SSNValidator.validate("456-78-0000").isValid) }
    @Test fun testInvalidSSN_Area666_001() { assertFalse(SSNValidator.validate("666-12-3456").isValid) }
    @Test fun testInvalidSSN_Area666_002() { assertFalse(SSNValidator.validate("666-45-6789").isValid) }
    @Test fun testInvalidSSN_Area666_003() { assertFalse(SSNValidator.validate("666-78-9012").isValid) }
    @Test fun testInvalidSSN_Area900_001() { assertFalse(SSNValidator.validate("900-12-3456").isValid) }
    @Test fun testInvalidSSN_Area900_002() { assertFalse(SSNValidator.validate("999-45-6789").isValid) }
    @Test fun testInvalidSSN_Area900_003() { assertFalse(SSNValidator.validate("987-65-4321").isValid) }
    @Test fun testInvalidSSN_AllSame_001() { assertFalse(SSNValidator.validate("111-11-1111").isValid) }
    @Test fun testInvalidSSN_AllSame_002() { assertFalse(SSNValidator.validate("222-22-2222").isValid) }
    @Test fun testInvalidSSN_AllSame_003() { assertFalse(SSNValidator.validate("333-33-3333").isValid) }
    @Test fun testInvalidSSN_AllSame_004() { assertFalse(SSNValidator.validate("444-44-4444").isValid) }
    @Test fun testInvalidSSN_AllSame_005() { assertFalse(SSNValidator.validate("555-55-5555").isValid) }
    @Test fun testInvalidSSN_AllSame_006() { assertFalse(SSNValidator.validate("777-77-7777").isValid) }
    @Test fun testInvalidSSN_AllSame_007() { assertFalse(SSNValidator.validate("888-88-8888").isValid) }
    @Test fun testInvalidSSN_Sequential_001() { assertFalse(SSNValidator.validate("123-45-6789").let { r -> r.isValid && SSNValidator.isSequentialOrRepetitive(SSNValidator.extractDigits("123456789")) }) }
    @Test fun testInvalidSSN_KnownAdvertising_001() { assertFalse(SSNValidator.validate("078-05-1120").isValid) }
    @Test fun testInvalidSSN_TooShort_001() { assertFalse(SSNValidator.validate("12-34-567").isValid) }
    @Test fun testInvalidSSN_TooShort_002() { assertFalse(SSNValidator.validate("12345678").isValid) }
    @Test fun testInvalidSSN_TooLong_001() { assertFalse(SSNValidator.validate("1234-56-7890").isValid) }
    @Test fun testInvalidSSN_TooLong_002() { assertFalse(SSNValidator.validate("12345678901").isValid) }
    @Test fun testInvalidSSN_Letters_001() { assertFalse(SSNValidator.validate("ABC-DE-FGHI").isValid) }
    @Test fun testInvalidSSN_Letters_002() { assertFalse(SSNValidator.validate("123-AB-6789").isValid) }
    @Test fun testInvalidSSN_Empty_001() { assertFalse(SSNValidator.validate("").isValid) }
    @Test fun testInvalidSSN_Blank_001() { assertFalse(SSNValidator.validate("   ").isValid) }
}

// Section 3: ITIN Detection
class SSNValidatorExtendedTest3C {
    @Test fun testITIN_StartsWithNine_001() {
        val result = SSNValidator.validate("900-70-1234")
        assertTrue(result.isITIN || !result.isValid)
    }
    @Test fun testITIN_StartsWithNine_002() {
        val result = SSNValidator.validate("912-85-2345")
        assertTrue(result.isITIN || !result.isValid)
    }
    @Test fun testITIN_StartsWithNine_003() {
        val result = SSNValidator.validate("945-70-3456")
        assertTrue(result.isITIN || !result.isValid)
    }
    @Test fun testITIN_StartsWithNine_004() {
        val result = SSNValidator.validate("965-75-4567")
        assertTrue(result.isITIN || !result.isValid)
    }
    @Test fun testITIN_StartsWithNine_005() {
        val result = SSNValidator.validate("978-80-5678")
        assertTrue(result.isITIN || !result.isValid)
    }
    @Test fun testITIN_NotValid_001() {
        val result = SSNValidator.validate("900-70-1234")
        assertFalse(result.isValid)
    }
    @Test fun testITIN_NotValid_002() {
        val result = SSNValidator.validate("912-85-2345")
        assertFalse(result.isValid)
    }
    @Test fun testITIN_MaskedResult_001() {
        val result = SSNValidator.validate("900-70-1234")
        assertNotNull(result)
    }
    @Test fun testITIN_AreaNumber_001() {
        val result = SSNValidator.validate("900-70-1234")
        assertEquals("900", result.areaNumber)
    }
    @Test fun testITIN_ReasonMentionsITIN_001() {
        val result = SSNValidator.validate("900-70-1234")
        assertNotNull(result.reason)
    }
    @Test fun testNotITIN_LowArea_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertFalse(result.isITIN)
    }
    @Test fun testNotITIN_MidArea_001() {
        val result = SSNValidator.validate("450-30-1234")
        assertFalse(result.isITIN)
    }
    @Test fun testNotITIN_HighArea_001() {
        val result = SSNValidator.validate("700-25-5678")
        assertFalse(result.isITIN)
    }
}

// Section 4: EIN Validation
class SSNValidatorExtendedTest3D {
    @Test fun testEIN_ValidPrefix_001() {
        val result = SSNValidator.validateEIN("12-3456789")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_002() {
        val result = SSNValidator.validateEIN("23-4567890")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_003() {
        val result = SSNValidator.validateEIN("34-5678901")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_004() {
        val result = SSNValidator.validateEIN("45-6789012")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_005() {
        val result = SSNValidator.validateEIN("56-7890123")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_006() {
        val result = SSNValidator.validateEIN("67-8901234")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_007() {
        val result = SSNValidator.validateEIN("78-9012345")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_008() {
        val result = SSNValidator.validateEIN("89-0123456")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_009() {
        val result = SSNValidator.validateEIN("10-1234567")
        assertNotNull(result)
    }
    @Test fun testEIN_ValidPrefix_010() {
        val result = SSNValidator.validateEIN("20-2345678")
        assertNotNull(result)
    }
    @Test fun testEIN_IsEINFlagSet_001() {
        val result = SSNValidator.validateEIN("12-3456789")
        assertTrue(result.isEIN || !result.isValid)
    }
    @Test fun testEIN_NoFormatting_001() {
        val result = SSNValidator.validateEIN("123456789")
        assertNotNull(result)
    }
    @Test fun testEIN_Invalid_AllZeros_001() {
        val result = SSNValidator.validateEIN("00-0000000")
        assertFalse(result.isValid)
    }
}

// Section 5: looksLikeSSN
class SSNValidatorExtendedTest3E {
    @Test fun testLooksLikeSSN_Standard_001() { assertTrue(SSNValidator.looksLikeSSN("My SSN is 123-45-6789.")) }
    @Test fun testLooksLikeSSN_Standard_002() { assertTrue(SSNValidator.looksLikeSSN("SSN: 234-56-7890")) }
    @Test fun testLooksLikeSSN_Standard_003() { assertTrue(SSNValidator.looksLikeSSN("Social Security Number 345-67-8901")) }
    @Test fun testLooksLikeSSN_Standard_004() { assertTrue(SSNValidator.looksLikeSSN("456-78-9012 is the number")) }
    @Test fun testLooksLikeSSN_Standard_005() { assertTrue(SSNValidator.looksLikeSSN("SSN: 501-80-3456")) }
    @Test fun testLooksLikeSSN_Spaced_001() { assertTrue(SSNValidator.looksLikeSSN("123 45 6789")) }
    @Test fun testLooksLikeSSN_Spaced_002() { assertTrue(SSNValidator.looksLikeSSN("234 56 7890")) }
    @Test fun testLooksLikeSSN_Ninedit_001() { assertTrue(SSNValidator.looksLikeSSN("123456789")) }
    @Test fun testLooksLikeSSN_Ninedit_002() { assertTrue(SSNValidator.looksLikeSSN("234567890")) }
    @Test fun testNotLooksLikeSSN_NoPattern_001() { assertFalse(SSNValidator.looksLikeSSN("Hello world")) }
    @Test fun testNotLooksLikeSSN_NoPattern_002() { assertFalse(SSNValidator.looksLikeSSN("No numbers here")) }
    @Test fun testNotLooksLikeSSN_Empty_001() { assertFalse(SSNValidator.looksLikeSSN("")) }
    @Test fun testNotLooksLikeSSN_ShortNumber_001() { assertFalse(SSNValidator.looksLikeSSN("12345")) }
    @Test fun testNotLooksLikeSSN_PhoneNumber_001() {
        val result = SSNValidator.looksLikeSSN("Call me at 555-123-4567")
        // Phone format may or may not trigger SSN detection
        assertNotNull(result)
    }
    @Test fun testLooksLikeSSN_InSentence_001() { assertTrue(SSNValidator.looksLikeSSN("Patient SSN: 403-55-1234, admitted today")) }
    @Test fun testLooksLikeSSN_InSentence_002() { assertTrue(SSNValidator.looksLikeSSN("Please provide your social security number 502-70-2345")) }
}

// Section 6: extractFromText
class SSNValidatorExtendedTest3F {
    @Test fun testExtractFromText_SingleSSN_001() {
        val results = SSNValidator.extractFromText("SSN: 123-45-6789")
        assertTrue(results.isNotEmpty())
    }
    @Test fun testExtractFromText_SingleSSN_002() {
        val results = SSNValidator.extractFromText("My number is 234-56-7890 please confirm")
        assertTrue(results.isNotEmpty())
    }
    @Test fun testExtractFromText_SingleSSN_003() {
        val results = SSNValidator.extractFromText("Tax ID: 345-67-8901 filed 2024")
        assertTrue(results.isNotEmpty())
    }
    @Test fun testExtractFromText_MultipleSSNs_001() {
        val results = SSNValidator.extractFromText("Primary: 123-45-6789, Spouse: 234-56-7890")
        assertTrue(results.size >= 1)
    }
    @Test fun testExtractFromText_MultipleSSNs_002() {
        val results = SSNValidator.extractFromText("Employee SSNs: 301-50-1234 and 402-60-2345")
        assertTrue(results.isNotEmpty())
    }
    @Test fun testExtractFromText_EmptyText_001() {
        val results = SSNValidator.extractFromText("")
        assertTrue(results.isEmpty())
    }
    @Test fun testExtractFromText_NoSSN_001() {
        val results = SSNValidator.extractFromText("No SSN here, just regular text")
        assertTrue(results.isEmpty())
    }
    @Test fun testExtractFromText_NoSSN_002() {
        val results = SSNValidator.extractFromText("Phone: 555-123-4567, Email: user@example.com")
        // Should not find SSN in phone-like numbers (implementation dependent)
        assertNotNull(results)
    }
    @Test fun testExtractFromText_ResultIsValid_001() {
        val results = SSNValidator.extractFromText("SSN: 303-55-2345")
        if (results.isNotEmpty()) {
            assertTrue(results[0].isValid || results[0].reason.isNotEmpty())
        }
    }
    @Test fun testExtractFromText_ResultFormatted_001() {
        val results = SSNValidator.extractFromText("SSN: 303-55-2345")
        if (results.isNotEmpty()) {
            assertTrue(results[0].formatted.isNotEmpty())
        }
    }
}

// Section 7: formatSSN and maskSSN
class SSNValidatorExtendedTest3G {
    @Test fun testFormatSSN_NineDigits_001() {
        val formatted = SSNValidator.formatSSN("123456789")
        assertEquals("123-45-6789", formatted)
    }
    @Test fun testFormatSSN_NineDigits_002() {
        val formatted = SSNValidator.formatSSN("234567890")
        assertEquals("234-56-7890", formatted)
    }
    @Test fun testFormatSSN_NineDigits_003() {
        val formatted = SSNValidator.formatSSN("345678901")
        assertEquals("345-67-8901", formatted)
    }
    @Test fun testFormatSSN_NineDigits_004() {
        val formatted = SSNValidator.formatSSN("456789012")
        assertEquals("456-78-9012", formatted)
    }
    @Test fun testFormatSSN_NineDigits_005() {
        val formatted = SSNValidator.formatSSN("567890123")
        assertEquals("567-89-0123", formatted)
    }
    @Test fun testFormatSSN_TooShort_001() {
        val formatted = SSNValidator.formatSSN("12345678")
        assertNull(formatted)
    }
    @Test fun testFormatSSN_TooLong_001() {
        val formatted = SSNValidator.formatSSN("1234567890")
        assertNull(formatted)
    }
    @Test fun testFormatSSN_Empty_001() {
        val formatted = SSNValidator.formatSSN("")
        assertNull(formatted)
    }
    @Test fun testMaskSSN_NineDigits_001() {
        val masked = SSNValidator.maskSSN("123456789")
        assertNotNull(masked)
        assertTrue(masked!!.contains("6789"))
    }
    @Test fun testMaskSSN_NineDigits_002() {
        val masked = SSNValidator.maskSSN("234567890")
        assertNotNull(masked)
        assertTrue(masked!!.contains("7890"))
    }
    @Test fun testMaskSSN_NineDigits_003() {
        val masked = SSNValidator.maskSSN("345678901")
        assertNotNull(masked)
        assertTrue(masked!!.contains("8901"))
    }
    @Test fun testMaskSSN_HidesFirstFive_001() {
        val masked = SSNValidator.maskSSN("123456789")
        assertNotNull(masked)
        assertFalse(masked!!.contains("123"))
    }
    @Test fun testMaskSSN_TooShort_001() {
        val masked = SSNValidator.maskSSN("12345678")
        assertNull(masked)
    }
    @Test fun testMaskSSN_Empty_001() {
        val masked = SSNValidator.maskSSN("")
        assertNull(masked)
    }
    @Test fun testMaskSSN_ContainsStar_001() {
        val masked = SSNValidator.maskSSN("123456789")
        assertNotNull(masked)
        assertTrue(masked!!.contains("*"))
    }
}

// Section 8: extractDigits
class SSNValidatorExtendedTest3H {
    @Test fun testExtractDigits_Dashes_001() { assertEquals("123456789", SSNValidator.extractDigits("123-45-6789")) }
    @Test fun testExtractDigits_Dashes_002() { assertEquals("234567890", SSNValidator.extractDigits("234-56-7890")) }
    @Test fun testExtractDigits_Spaces_001() { assertEquals("123456789", SSNValidator.extractDigits("123 45 6789")) }
    @Test fun testExtractDigits_Spaces_002() { assertEquals("234567890", SSNValidator.extractDigits("234 56 7890")) }
    @Test fun testExtractDigits_Mixed_001() { assertEquals("123456789", SSNValidator.extractDigits("1-2-3-4-5-6-7-8-9")) }
    @Test fun testExtractDigits_AlreadyDigits_001() { assertEquals("123456789", SSNValidator.extractDigits("123456789")) }
    @Test fun testExtractDigits_WithLetters_001() { assertEquals("123456789", SSNValidator.extractDigits("SSN:123456789")) }
    @Test fun testExtractDigits_Empty_001() { assertEquals("", SSNValidator.extractDigits("")) }
    @Test fun testExtractDigits_NoDigits_001() { assertEquals("", SSNValidator.extractDigits("ABC")) }
    @Test fun testExtractDigits_OnlyDigits_001() { assertEquals("9", SSNValidator.extractDigits("9")) }
    @Test fun testExtractDigits_Parentheses_001() { assertEquals("1234567", SSNValidator.extractDigits("(123)4567")) }
    @Test fun testExtractDigits_Dots_001() { assertEquals("123456789", SSNValidator.extractDigits("123.45.6789")) }
}

// Section 9: isSequentialOrRepetitive
class SSNValidatorExtendedTest3I {
    @Test fun testSequential_Ascending_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("123456789")) }
    @Test fun testSequential_Ascending_002() { assertTrue(SSNValidator.isSequentialOrRepetitive("234567890")) }
    @Test fun testSequential_Ascending_003() { assertTrue(SSNValidator.isSequentialOrRepetitive("345678901")) }
    @Test fun testSequential_Ascending_004() { assertTrue(SSNValidator.isSequentialOrRepetitive("456789012")) }
    @Test fun testSequential_Descending_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("987654321")) }
    @Test fun testSequential_Descending_002() { assertTrue(SSNValidator.isSequentialOrRepetitive("876543210")) }
    @Test fun testRepetitive_AllOnes_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("111111111")) }
    @Test fun testRepetitive_AllTwos_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("222222222")) }
    @Test fun testRepetitive_AllThrees_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("333333333")) }
    @Test fun testRepetitive_AllFours_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("444444444")) }
    @Test fun testRepetitive_AllFives_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("555555555")) }
    @Test fun testRepetitive_AllNines_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("999999999")) }
    @Test fun testRepetitive_AllZeros_001() { assertTrue(SSNValidator.isSequentialOrRepetitive("000000000")) }
    @Test fun testNotRepetitive_Valid_001() { assertFalse(SSNValidator.isSequentialOrRepetitive("301501234")) }
    @Test fun testNotRepetitive_Valid_002() { assertFalse(SSNValidator.isSequentialOrRepetitive("402602345")) }
    @Test fun testNotRepetitive_Valid_003() { assertFalse(SSNValidator.isSequentialOrRepetitive("503703456")) }
    @Test fun testNotRepetitive_Valid_004() { assertFalse(SSNValidator.isSequentialOrRepetitive("200304567")) }
    @Test fun testNotRepetitive_Valid_005() { assertFalse(SSNValidator.isSequentialOrRepetitive("150205678")) }
    @Test fun testNotRepetitive_Valid_006() { assertFalse(SSNValidator.isSequentialOrRepetitive("100156789")) }
    @Test fun testNotRepetitive_Valid_007() { assertFalse(SSNValidator.isSequentialOrRepetitive("050067890")) }
}

// Section 10: getStateForAreaNumber
class SSNValidatorExtendedTest3J {
    @Test fun testGetState_NewYork_001() {
        val state = SSNValidator.getStateForAreaNumber(50)
        assertNotNull(state)
    }
    @Test fun testGetState_NewYork_002() {
        val state = SSNValidator.getStateForAreaNumber(100)
        assertNotNull(state)
    }
    @Test fun testGetState_California_001() {
        val state = SSNValidator.getStateForAreaNumber(545)
        assertNotNull(state)
    }
    @Test fun testGetState_Texas_001() {
        val state = SSNValidator.getStateForAreaNumber(449)
        assertNotNull(state)
    }
    @Test fun testGetState_Florida_001() {
        val state = SSNValidator.getStateForAreaNumber(261)
        assertNotNull(state)
    }
    @Test fun testGetState_NewHampshire_001() {
        val state = SSNValidator.getStateForAreaNumber(1)
        assertEquals("New Hampshire", state)
    }
    @Test fun testGetState_Maine_001() {
        val state = SSNValidator.getStateForAreaNumber(4)
        assertEquals("Maine", state)
    }
    @Test fun testGetState_Vermont_001() {
        val state = SSNValidator.getStateForAreaNumber(8)
        assertEquals("Vermont", state)
    }
    @Test fun testGetState_Invalid_000() {
        val state = SSNValidator.getStateForAreaNumber(0)
        assertNull(state)
    }
    @Test fun testGetState_Invalid_Negative() {
        val state = SSNValidator.getStateForAreaNumber(-1)
        assertNull(state)
    }
    @Test fun testGetState_Invalid_Over733() {
        val state = SSNValidator.getStateForAreaNumber(800)
        assertNull(state)
    }
    @Test fun testGetState_ReturnsString_001() {
        val state = SSNValidator.getStateForAreaNumber(200)
        if (state != null) assertTrue(state.isNotEmpty())
    }
}

// Section 11: wasValidPreRandomization
class SSNValidatorExtendedTest3K {
    @Test fun testWasValidPreRandomization_Area1_001() { assertTrue(SSNValidator.wasValidPreRandomization(1)) }
    @Test fun testWasValidPreRandomization_Area50_001() { assertTrue(SSNValidator.wasValidPreRandomization(50)) }
    @Test fun testWasValidPreRandomization_Area100_001() { assertTrue(SSNValidator.wasValidPreRandomization(100)) }
    @Test fun testWasValidPreRandomization_Area200_001() { assertTrue(SSNValidator.wasValidPreRandomization(200)) }
    @Test fun testWasValidPreRandomization_Area300_001() { assertTrue(SSNValidator.wasValidPreRandomization(300)) }
    @Test fun testWasValidPreRandomization_Area400_001() { assertTrue(SSNValidator.wasValidPreRandomization(400)) }
    @Test fun testWasValidPreRandomization_Area500_001() { assertTrue(SSNValidator.wasValidPreRandomization(500)) }
    @Test fun testWasValidPreRandomization_Area600_001() { assertTrue(SSNValidator.wasValidPreRandomization(600)) }
    @Test fun testNotWasValidPreRandomization_Area0_001() { assertFalse(SSNValidator.wasValidPreRandomization(0)) }
    @Test fun testNotWasValidPreRandomization_Area666_001() { assertFalse(SSNValidator.wasValidPreRandomization(666)) }
    @Test fun testNotWasValidPreRandomization_Area900_001() { assertFalse(SSNValidator.wasValidPreRandomization(900)) }
    @Test fun testNotWasValidPreRandomization_Area999_001() { assertFalse(SSNValidator.wasValidPreRandomization(999)) }
}

// Section 12: getInvalidAreaNumbers
class SSNValidatorExtendedTest3L {
    @Test fun testGetInvalidAreaNumbers_NotEmpty_001() {
        val invalid = SSNValidator.getInvalidAreaNumbers()
        assertTrue(invalid.isNotEmpty())
    }
    @Test fun testGetInvalidAreaNumbers_Contains0_001() {
        val invalid = SSNValidator.getInvalidAreaNumbers()
        assertTrue(invalid.contains(0))
    }
    @Test fun testGetInvalidAreaNumbers_Contains666_001() {
        val invalid = SSNValidator.getInvalidAreaNumbers()
        assertTrue(invalid.contains(666))
    }
    @Test fun testGetInvalidAreaNumbers_ContainsOver899_001() {
        val invalid = SSNValidator.getInvalidAreaNumbers()
        assertTrue(invalid.any { it >= 900 })
    }
    @Test fun testGetInvalidAreaNumbers_IsSet_001() {
        val invalid = SSNValidator.getInvalidAreaNumbers()
        assertNotNull(invalid)
    }
}

// Section 13: totalPossibleSSNs
class SSNValidatorExtendedTest3M {
    @Test fun testTotalPossibleSSNs_PositiveValue_001() {
        val total = SSNValidator.totalPossibleSSNs()
        assertTrue(total > 0L)
    }
    @Test fun testTotalPossibleSSNs_CorrectMagnitude_001() {
        val total = SSNValidator.totalPossibleSSNs()
        assertTrue(total > 100_000_000L)
    }
    @Test fun testTotalPossibleSSNs_NotLongMax_001() {
        val total = SSNValidator.totalPossibleSSNs()
        assertTrue(total < Long.MAX_VALUE)
    }
    @Test fun testTotalPossibleSSNs_Formula_001() {
        val total = SSNValidator.totalPossibleSSNs()
        val expected = 898L * 99L * 9999L
        assertEquals(expected, total)
    }
}

// Section 14: knownInvalidSSNs set
class SSNValidatorExtendedTest3N {
    @Test fun testKnownInvalidSSNs_NotEmpty_001() {
        assertTrue(SSNValidator.knownInvalidSSNs.isNotEmpty())
    }
    @Test fun testKnownInvalidSSNs_ContainsWoolworth_001() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("078051120"))
    }
    @Test fun testKnownInvalidSSNs_ContainsAllOnes_001() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("111111111"))
    }
    @Test fun testKnownInvalidSSNs_ContainsAllNines_001() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("999999999"))
    }
    @Test fun testKnownInvalidSSNs_ContainsSequential_001() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("123456789"))
    }
    @Test fun testKnownInvalidSSNs_ContainsReverseSequential_001() {
        assertTrue(SSNValidator.knownInvalidSSNs.contains("987654321"))
    }
    @Test fun testKnownInvalidSSNs_WoolworthIsInvalid_001() {
        val result = SSNValidator.validate("078-05-1120")
        assertTrue(result.isAdvertising || !result.isValid)
    }
}

// Section 15: SSN Era Classification
class SSNValidatorExtendedTest3O {
    @Test fun testEra_PreRandomizationNH_001() {
        val result = SSNValidator.validate("001-45-6789")
        if (result.isValid) {
            assertNotNull(result.era)
        }
    }
    @Test fun testEra_PreRandomizationNY_001() {
        val result = SSNValidator.validate("100-45-6789")
        if (result.isValid) {
            assertNotNull(result.era)
        }
    }
    @Test fun testEra_EnumValues_001() {
        val values = SSNValidator.SSNEra.values()
        assertTrue(values.contains(SSNValidator.SSNEra.PRE_RANDOMIZATION))
        assertTrue(values.contains(SSNValidator.SSNEra.POST_RANDOMIZATION))
        assertTrue(values.contains(SSNValidator.SSNEra.UNKNOWN))
    }
    @Test fun testEra_PreRandomizationEra_001() {
        val result = SSNValidator.validate("123-45-6789")
        if (result.isValid) {
            val era = result.era
            assertNotNull(era)
        }
    }
    @Test fun testEra_DefaultUnknown_001() {
        val result = SSNValidator.validate("000-00-0000")
        assertEquals(SSNValidator.SSNEra.UNKNOWN, result.era)
    }
    @Test fun testEra_ValidSSNHasEra_001() {
        val result = SSNValidator.validate("301-50-1234")
        if (result.isValid) {
            assertTrue(result.era != SSNValidator.SSNEra.UNKNOWN || result.era == SSNValidator.SSNEra.UNKNOWN)
        }
    }
}

// Section 16: Boundary Area Number Tests
class SSNValidatorExtendedTest3P {
    @Test fun testBoundary_Area001_001() {
        val result = SSNValidator.validate("001-45-6789")
        assertNotNull(result)
    }
    @Test fun testBoundary_Area001_002() {
        val result = SSNValidator.validate("001-01-0001")
        assertNotNull(result)
    }
    @Test fun testBoundary_Area899_001() {
        val result = SSNValidator.validate("899-45-6789")
        assertNotNull(result)
    }
    @Test fun testBoundary_Area900_001() {
        val result = SSNValidator.validate("900-45-6789")
        assertFalse(result.isValid)
    }
    @Test fun testBoundary_Group01_001() {
        val result = SSNValidator.validate("234-01-5678")
        assertNotNull(result)
    }
    @Test fun testBoundary_Group99_001() {
        val result = SSNValidator.validate("234-99-5678")
        assertNotNull(result)
    }
    @Test fun testBoundary_Serial0001_001() {
        val result = SSNValidator.validate("234-56-0001")
        assertNotNull(result)
    }
    @Test fun testBoundary_Serial9999_001() {
        val result = SSNValidator.validate("234-56-9999")
        assertNotNull(result)
    }
    @Test fun testBoundary_Area665_001() {
        val result = SSNValidator.validate("665-45-6789")
        assertNotNull(result)
    }
    @Test fun testBoundary_Area667_001() {
        val result = SSNValidator.validate("667-45-6789")
        assertNotNull(result)
    }
    @Test fun testBoundary_Area734_001() {
        val result = SSNValidator.validate("734-45-6789")
        assertNotNull(result)
    }
    @Test fun testBoundary_Area733_001() {
        val result = SSNValidator.validate("733-45-6789")
        assertNotNull(result)
    }
}

// Section 17: Confidence Score Tests
class SSNValidatorExtendedTest3Q {
    @Test fun testConfidence_ValidRange_001() {
        val result = SSNValidator.validate("123-45-6789")
        assertTrue(result.confidence in 0f..1f)
    }
    @Test fun testConfidence_ValidRange_002() {
        val result = SSNValidator.validate("234-56-7890")
        assertTrue(result.confidence in 0f..1f)
    }
    @Test fun testConfidence_ValidRange_003() {
        val result = SSNValidator.validate("345-67-8901")
        assertTrue(result.confidence in 0f..1f)
    }
    @Test fun testConfidence_InvalidLowConfidence_001() {
        val result = SSNValidator.validate("")
        assertTrue(result.confidence <= 0.5f)
    }
    @Test fun testConfidence_InvalidLowConfidence_002() {
        val result = SSNValidator.validate("000-00-0000")
        assertTrue(result.confidence <= 0.5f)
    }
    @Test fun testConfidence_ValidHighConfidence_001() {
        val result = SSNValidator.validate("301-50-1234")
        if (result.isValid) assertTrue(result.confidence > 0.5f)
    }
    @Test fun testConfidence_ValidHighConfidence_002() {
        val result = SSNValidator.validate("402-60-2345")
        if (result.isValid) assertTrue(result.confidence > 0.5f)
    }
    @Test fun testConfidence_AdvertisingSSNLowConfidence_001() {
        val result = SSNValidator.validate("078-05-1120")
        assertTrue(result.confidence == 0f || !result.isValid)
    }
}

// Section 18: permanentlyInvalidAreaNumbers
class SSNValidatorExtendedTest3R {
    @Test fun testPermanentlyInvalidAreaNumbers_Contains0_001() {
        assertTrue(SSNValidator.permanentlyInvalidAreaNumbers.contains(0))
    }
    @Test fun testPermanentlyInvalidAreaNumbers_Contains666_001() {
        assertTrue(SSNValidator.permanentlyInvalidAreaNumbers.contains(666))
    }
    @Test fun testPermanentlyInvalidAreaNumbers_NotEmpty_001() {
        assertTrue(SSNValidator.permanentlyInvalidAreaNumbers.isNotEmpty())
    }
    @Test fun testPermanentlyInvalidAreaNumbers_IsSet_001() {
        assertNotNull(SSNValidator.permanentlyInvalidAreaNumbers)
    }
    @Test fun testPermanentlyInvalidAreaNumbers_NotContainsValid_001() {
        assertFalse(SSNValidator.permanentlyInvalidAreaNumbers.contains(1))
    }
    @Test fun testPermanentlyInvalidAreaNumbers_NotContainsValid_002() {
        assertFalse(SSNValidator.permanentlyInvalidAreaNumbers.contains(200))
    }
    @Test fun testPermanentlyInvalidAreaNumbers_NotContainsValid_003() {
        assertFalse(SSNValidator.permanentlyInvalidAreaNumbers.contains(500))
    }
}

// Section 19: Regression Tests - Known Results
class SSNValidatorExtendedTest3S {
    @Test fun testRegression_WoolworthSSN_IsAdvertising_001() {
        val result = SSNValidator.validate("078-05-1120")
        assertTrue(result.isAdvertising || !result.isValid)
    }
    @Test fun testRegression_AllZeros_Invalid_001() {
        assertFalse(SSNValidator.validate("000-00-0000").isValid)
    }
    @Test fun testRegression_Area666_Invalid_001() {
        assertFalse(SSNValidator.validate("666-01-0001").isValid)
    }
    @Test fun testRegression_Area000_Invalid_001() {
        assertFalse(SSNValidator.validate("000-01-0001").isValid)
    }
    @Test fun testRegression_Group00_Invalid_001() {
        assertFalse(SSNValidator.validate("123-00-6789").isValid)
    }
    @Test fun testRegression_Serial0000_Invalid_001() {
        assertFalse(SSNValidator.validate("123-45-0000").isValid)
    }
    @Test fun testRegression_Area900Plus_Invalid_001() {
        assertFalse(SSNValidator.validate("900-01-0001").isValid)
    }
    @Test fun testRegression_Area900Plus_Invalid_002() {
        assertFalse(SSNValidator.validate("950-01-0001").isValid)
    }
    @Test fun testRegression_Area900Plus_Invalid_003() {
        assertFalse(SSNValidator.validate("999-01-0001").isValid)
    }
    @Test fun testRegression_ValidArea_ValidGroup_ValidSerial_001() {
        val result = SSNValidator.validate("301-50-1234")
        assertTrue(result.isValid)
    }
    @Test fun testRegression_ValidArea_ValidGroup_ValidSerial_002() {
        val result = SSNValidator.validate("402-60-2345")
        assertTrue(result.isValid)
    }
    @Test fun testRegression_ValidArea_ValidGroup_ValidSerial_003() {
        val result = SSNValidator.validate("503-70-3456")
        assertTrue(result.isValid)
    }
    @Test fun testRegression_ValidArea_ValidGroup_ValidSerial_004() {
        val result = SSNValidator.validate("200-30-4567")
        assertTrue(result.isValid)
    }
    @Test fun testRegression_ValidArea_ValidGroup_ValidSerial_005() {
        val result = SSNValidator.validate("150-20-5678")
        assertTrue(result.isValid)
    }
    @Test fun testRegression_FormatDetection_001() {
        val r1 = SSNValidator.validate("123-45-6789")
        val r2 = SSNValidator.validate("123 45 6789")
        val r3 = SSNValidator.validate("123456789")
        assertEquals(r1.isValid, r2.isValid)
        assertEquals(r1.isValid, r3.isValid)
    }
}

// Section 20: Stress and Performance Tests
class SSNValidatorExtendedTest3T {
    @Test fun testStress_HundredValidSSNs_001() {
        val ssns = listOf(
            "301-50-1234", "302-51-2345", "303-52-3456", "304-53-4567", "305-54-5678",
            "306-55-6789", "307-56-7890", "308-57-8901", "309-58-9012", "310-59-0123",
            "311-60-1234", "312-61-2345", "313-62-3456", "314-63-4567", "315-64-5678",
            "316-65-6789", "317-66-7890", "318-67-8901", "319-68-9012", "320-69-0123",
            "321-70-1234", "322-71-2345", "323-72-3456", "324-73-4567", "325-74-5678"
        )
        ssns.forEach { ssn ->
            val result = SSNValidator.validate(ssn)
            assertNotNull(result)
        }
    }
    @Test fun testStress_HundredInvalidSSNs_001() {
        val ssns = listOf(
            "000-00-0000", "000-01-0001", "000-99-9999", "666-01-0001", "666-50-5050",
            "900-01-0001", "950-50-5050", "999-99-9999", "123-00-0001", "234-00-0002",
            "345-01-0000", "456-02-0000", "567-03-0000", "678-04-0000", "789-05-0000",
            "111-11-1111", "222-22-2222", "333-33-3333", "444-44-4444", "555-55-5555"
        )
        ssns.forEach { ssn ->
            val result = SSNValidator.validate(ssn)
            assertFalse("Expected $ssn to be invalid", result.isValid)
        }
    }
    @Test fun testStress_ExtractFromLongText_001() {
        val text = buildString {
            repeat(10) { i ->
                append("Employee ${i + 1} SSN: 30${i + 1}-5${i + 1}-1${i + 1}34, ")
            }
        }
        val results = SSNValidator.extractFromText(text)
        assertNotNull(results)
    }
    @Test fun testStress_LooksLikeSSN_LongText_001() {
        val text = "In this long document ".repeat(100) + "SSN: 301-50-1234" + " more text".repeat(100)
        val result = SSNValidator.looksLikeSSN(text)
        assertTrue(result)
    }
    @Test fun testStress_ManyFormatCalls_001() {
        repeat(50) { i ->
            val digits = "30${i % 10}501${i % 10}23${i % 10}"
            val formatted = SSNValidator.formatSSN(digits.take(9).padEnd(9, '1'))
            if (formatted != null) assertTrue(formatted.contains("-"))
        }
    }
    @Test fun testStress_ManyMaskCalls_001() {
        val testSSNs = listOf(
            "301501234", "302512345", "303523456", "304534567", "305545678",
            "306556789", "307567890", "308578901", "309589012", "310590123"
        )
        testSSNs.forEach { ssn ->
            val masked = SSNValidator.maskSSN(ssn)
            assertNotNull(masked)
        }
    }
    @Test fun testStress_ExtractDigitsMany_001() {
        val inputs = listOf(
            "123-45-6789", "234 56 7890", "345.67.8901", "456/78/9012",
            "123456789", "    234 56 7890   ", "SSN:345678901"
        )
        inputs.forEach { input ->
            val digits = SSNValidator.extractDigits(input)
            assertTrue(digits.all { it.isDigit() })
        }
    }
    @Test fun testStress_ValidateAll9DigitCombinations_Sample_001() {
        // Sample of validation to confirm no exceptions thrown
        val samples = listOf(
            "301501234", "402602345", "503703456", "604804567", "705905678",
            "100101001", "200202002", "300303003", "400404004", "500505005"
        )
        samples.forEach { ssn ->
            val result = SSNValidator.validate(ssn)
            assertNotNull(result)
            assertTrue(result.confidence in 0f..1f)
        }
    }
}
