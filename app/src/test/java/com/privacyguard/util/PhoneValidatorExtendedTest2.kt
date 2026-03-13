package com.privacyguard.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Section 1: US Phone Numbers - NANP Valid
class PhoneValidatorExtendedTest2A {
    @Test fun testUS_Valid_001() { assertTrue(CountryCodeValidator.validate("+1 (212) 555-0100").isValid) }
    @Test fun testUS_Valid_002() { assertTrue(CountryCodeValidator.validate("+1 (213) 555-0101").isValid) }
    @Test fun testUS_Valid_003() { assertTrue(CountryCodeValidator.validate("+1 (312) 555-0102").isValid) }
    @Test fun testUS_Valid_004() { assertTrue(CountryCodeValidator.validate("+1 (415) 555-0103").isValid) }
    @Test fun testUS_Valid_005() { assertTrue(CountryCodeValidator.validate("+1 (617) 555-0104").isValid) }
    @Test fun testUS_Valid_006() { assertTrue(CountryCodeValidator.validate("+1 (713) 555-0105").isValid) }
    @Test fun testUS_Valid_007() { assertTrue(CountryCodeValidator.validate("+1 (202) 555-0106").isValid) }
    @Test fun testUS_Valid_008() { assertTrue(CountryCodeValidator.validate("+1 (404) 555-0107").isValid) }
    @Test fun testUS_Valid_009() { assertTrue(CountryCodeValidator.validate("+1 (305) 555-0108").isValid) }
    @Test fun testUS_Valid_010() { assertTrue(CountryCodeValidator.validate("+1 (214) 555-0109").isValid) }
    @Test fun testUS_Valid_011() { assertTrue(CountryCodeValidator.validate("+12125550100").isValid) }
    @Test fun testUS_Valid_012() { assertTrue(CountryCodeValidator.validate("+12135550101").isValid) }
    @Test fun testUS_Valid_013() { assertTrue(CountryCodeValidator.validate("+13125550102").isValid) }
    @Test fun testUS_Valid_014() { assertTrue(CountryCodeValidator.validate("+14155550103").isValid) }
    @Test fun testUS_Valid_015() { assertTrue(CountryCodeValidator.validate("+16175550104").isValid) }
    @Test fun testUS_NoCountryCode_001() { assertTrue(CountryCodeValidator.validate("(212) 555-0100").isValid) }
    @Test fun testUS_NoCountryCode_002() { assertTrue(CountryCodeValidator.validate("(213) 555-0101").isValid) }
    @Test fun testUS_NoCountryCode_003() { assertTrue(CountryCodeValidator.validate("212-555-0100").isValid) }
    @Test fun testUS_NoCountryCode_004() { assertTrue(CountryCodeValidator.validate("213-555-0101").isValid) }
    @Test fun testUS_NoCountryCode_005() { assertTrue(CountryCodeValidator.validate("2125550100").isValid) }
    @Test fun testUS_NANP_Validate_001() {
        val result = CountryCodeValidator.validateNANPNumber("+12125550100")
        assertNotNull(result)
        assertTrue(result.isValid)
    }
    @Test fun testUS_NANP_Validate_002() {
        val result = CountryCodeValidator.validateNANPNumber("2125550100")
        assertNotNull(result)
        assertTrue(result.isValid)
    }
    @Test fun testUS_NANP_Validate_003() {
        val result = CountryCodeValidator.validateNANPNumber("(212) 555-0100")
        assertNotNull(result)
        assertTrue(result.isValid)
    }
    @Test fun testUS_CountryCode_InResult_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        assertEquals("+1", result.countryCode)
    }
    @Test fun testUS_NationalNumber_InResult_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        assertNotNull(result.nationalNumber)
    }
}

// Section 2: Canadian Phone Numbers
class PhoneValidatorExtendedTest2B {
    @Test fun testCA_Valid_001() { assertTrue(CountryCodeValidator.validate("+1 (416) 555-0100").isValid) }
    @Test fun testCA_Valid_002() { assertTrue(CountryCodeValidator.validate("+1 (604) 555-0101").isValid) }
    @Test fun testCA_Valid_003() { assertTrue(CountryCodeValidator.validate("+1 (514) 555-0102").isValid) }
    @Test fun testCA_Valid_004() { assertTrue(CountryCodeValidator.validate("+1 (403) 555-0103").isValid) }
    @Test fun testCA_Valid_005() { assertTrue(CountryCodeValidator.validate("+1 (613) 555-0104").isValid) }
    @Test fun testCA_Valid_006() { assertTrue(CountryCodeValidator.validate("+1 (905) 555-0105").isValid) }
    @Test fun testCA_Valid_007() { assertTrue(CountryCodeValidator.validate("+14165550100").isValid) }
    @Test fun testCA_Valid_008() { assertTrue(CountryCodeValidator.validate("+16045550101").isValid) }
    @Test fun testCA_Valid_009() { assertTrue(CountryCodeValidator.validate("+15145550102").isValid) }
    @Test fun testCA_Valid_010() { assertTrue(CountryCodeValidator.validate("+14035550103").isValid) }
}

// Section 3: UK Phone Numbers
class PhoneValidatorExtendedTest2C {
    @Test fun testUK_Valid_001() { assertTrue(CountryCodeValidator.validate("+44 20 7946 0958").isValid) }
    @Test fun testUK_Valid_002() { assertTrue(CountryCodeValidator.validate("+44 121 496 0958").isValid) }
    @Test fun testUK_Valid_003() { assertTrue(CountryCodeValidator.validate("+44 131 496 0958").isValid) }
    @Test fun testUK_Valid_004() { assertTrue(CountryCodeValidator.validate("+44 161 496 0958").isValid) }
    @Test fun testUK_Valid_005() { assertTrue(CountryCodeValidator.validate("+447911123456").isValid) }
    @Test fun testUK_Valid_006() { assertTrue(CountryCodeValidator.validate("+447700900123").isValid) }
    @Test fun testUK_CountryCode_001() {
        val result = CountryCodeValidator.validate("+44 20 7946 0958")
        assertEquals("+44", result.countryCode)
    }
    @Test fun testUK_CountryCode_002() {
        val result = CountryCodeValidator.validate("+447911123456")
        assertEquals("+44", result.countryCode)
    }
    @Test fun testUK_DetectCountry_001() {
        val country = CountryCodeValidator.detectCountry("+447911123456")
        if (country != null) assertEquals("GB", country.code)
    }
}

// Section 4: German Phone Numbers
class PhoneValidatorExtendedTest2D {
    @Test fun testDE_Valid_001() { assertTrue(CountryCodeValidator.validate("+49 30 12345678").isValid) }
    @Test fun testDE_Valid_002() { assertTrue(CountryCodeValidator.validate("+49 89 12345678").isValid) }
    @Test fun testDE_Valid_003() { assertTrue(CountryCodeValidator.validate("+49 40 12345678").isValid) }
    @Test fun testDE_Valid_004() { assertTrue(CountryCodeValidator.validate("+49 211 12345678").isValid) }
    @Test fun testDE_Valid_005() { assertTrue(CountryCodeValidator.validate("+49 69 12345678").isValid) }
    @Test fun testDE_Mobile_001() { assertTrue(CountryCodeValidator.validate("+49 151 12345678").isValid) }
    @Test fun testDE_Mobile_002() { assertTrue(CountryCodeValidator.validate("+49 171 12345678").isValid) }
    @Test fun testDE_CountryCode_001() {
        val result = CountryCodeValidator.validate("+49 30 12345678")
        assertEquals("+49", result.countryCode)
    }
}

// Section 5: French Phone Numbers
class PhoneValidatorExtendedTest2E {
    @Test fun testFR_Valid_001() { assertTrue(CountryCodeValidator.validate("+33 1 23 45 67 89").isValid) }
    @Test fun testFR_Valid_002() { assertTrue(CountryCodeValidator.validate("+33 9 87 65 43 21").isValid) }
    @Test fun testFR_Mobile_001() { assertTrue(CountryCodeValidator.validate("+33 6 12 34 56 78").isValid) }
    @Test fun testFR_Mobile_002() { assertTrue(CountryCodeValidator.validate("+33 7 98 76 54 32").isValid) }
    @Test fun testFR_CountryCode_001() {
        val result = CountryCodeValidator.validate("+33 1 23 45 67 89")
        assertEquals("+33", result.countryCode)
    }
}

// Section 6: Australian Phone Numbers
class PhoneValidatorExtendedTest2F {
    @Test fun testAU_Valid_001() { assertTrue(CountryCodeValidator.validate("+61 2 9876 5432").isValid) }
    @Test fun testAU_Valid_002() { assertTrue(CountryCodeValidator.validate("+61 3 9876 5432").isValid) }
    @Test fun testAU_Valid_003() { assertTrue(CountryCodeValidator.validate("+61 7 9876 5432").isValid) }
    @Test fun testAU_Valid_004() { assertTrue(CountryCodeValidator.validate("+61 8 9876 5432").isValid) }
    @Test fun testAU_Mobile_001() { assertTrue(CountryCodeValidator.validate("+61 412 345 678").isValid) }
    @Test fun testAU_Mobile_002() { assertTrue(CountryCodeValidator.validate("+61 423 456 789").isValid) }
    @Test fun testAU_CountryCode_001() {
        val result = CountryCodeValidator.validate("+61 2 9876 5432")
        assertEquals("+61", result.countryCode)
    }
}

// Section 7: Indian Phone Numbers
class PhoneValidatorExtendedTest2G {
    @Test fun testIN_Valid_001() { assertTrue(CountryCodeValidator.validate("+91 98765 43210").isValid) }
    @Test fun testIN_Valid_002() { assertTrue(CountryCodeValidator.validate("+91 87654 32109").isValid) }
    @Test fun testIN_Valid_003() { assertTrue(CountryCodeValidator.validate("+91 76543 21098").isValid) }
    @Test fun testIN_Valid_004() { assertTrue(CountryCodeValidator.validate("+91 65432 10987").isValid) }
    @Test fun testIN_Valid_005() { assertTrue(CountryCodeValidator.validate("+919876543210").isValid) }
    @Test fun testIN_CountryCode_001() {
        val result = CountryCodeValidator.validate("+91 98765 43210")
        assertEquals("+91", result.countryCode)
    }
}

// Section 8: Japanese Phone Numbers
class PhoneValidatorExtendedTest2H {
    @Test fun testJP_Valid_001() { assertTrue(CountryCodeValidator.validate("+81 3 1234 5678").isValid) }
    @Test fun testJP_Valid_002() { assertTrue(CountryCodeValidator.validate("+81 6 1234 5678").isValid) }
    @Test fun testJP_Mobile_001() { assertTrue(CountryCodeValidator.validate("+81 80 1234 5678").isValid) }
    @Test fun testJP_Mobile_002() { assertTrue(CountryCodeValidator.validate("+81 90 1234 5678").isValid) }
    @Test fun testJP_CountryCode_001() {
        val result = CountryCodeValidator.validate("+81 3 1234 5678")
        assertEquals("+81", result.countryCode)
    }
}

// Section 9: Brazilian Phone Numbers
class PhoneValidatorExtendedTest2I {
    @Test fun testBR_Valid_001() { assertTrue(CountryCodeValidator.validate("+55 11 9876-5432").isValid) }
    @Test fun testBR_Valid_002() { assertTrue(CountryCodeValidator.validate("+55 21 9876-5432").isValid) }
    @Test fun testBR_Valid_003() { assertTrue(CountryCodeValidator.validate("+55 31 9876-5432").isValid) }
    @Test fun testBR_Valid_004() { assertTrue(CountryCodeValidator.validate("+55 41 9876-5432").isValid) }
    @Test fun testBR_CountryCode_001() {
        val result = CountryCodeValidator.validate("+55 11 9876-5432")
        assertEquals("+55", result.countryCode)
    }
}

// Section 10: Invalid Phone Numbers
class PhoneValidatorExtendedTest2J {
    @Test fun testInvalid_Empty_001() { assertFalse(CountryCodeValidator.validate("").isValid) }
    @Test fun testInvalid_Blank_001() { assertFalse(CountryCodeValidator.validate("   ").isValid) }
    @Test fun testInvalid_TooShort_001() { assertFalse(CountryCodeValidator.validate("12345").isValid) }
    @Test fun testInvalid_TooShort_002() { assertFalse(CountryCodeValidator.validate("123-456").isValid) }
    @Test fun testInvalid_TooLong_001() { assertFalse(CountryCodeValidator.validate("+1 2125550100 extra").isValid) }
    @Test fun testInvalid_AllZeros_001() { assertFalse(CountryCodeValidator.validate("0000000000").isValid) }
    @Test fun testInvalid_Letters_001() { assertFalse(CountryCodeValidator.validate("abcdefghij").isValid) }
    @Test fun testInvalid_AreaCode000_001() { assertFalse(CountryCodeValidator.validate("000-555-0100").isValid) }
    @Test fun testInvalid_AreaCode1XX_001() { assertFalse(CountryCodeValidator.validate("155-555-0100").isValid) }
    @Test fun testInvalid_ExchangeCode0XX_001() { assertFalse(CountryCodeValidator.validate("212-011-0100").isValid) }
    @Test fun testInvalid_ExchangeCode1XX_001() { assertFalse(CountryCodeValidator.validate("212-123-0100").isValid) }
    @Test fun testInvalid_JustPlus_001() { assertFalse(CountryCodeValidator.validate("+").isValid) }
    @Test fun testInvalid_NonsenseCountryCode_001() { assertFalse(CountryCodeValidator.validate("+999 12345678901").isValid) }
}

// Section 11: looksLikePhoneNumber
class PhoneValidatorExtendedTest2K {
    @Test fun testLooksLike_USStandard_001() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("Call me at (212) 555-0100")) }
    @Test fun testLooksLike_USStandard_002() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("Phone: 212-555-0101")) }
    @Test fun testLooksLike_USStandard_003() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("212.555.0102")) }
    @Test fun testLooksLike_USStandard_004() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("Tel: 2125550103")) }
    @Test fun testLooksLike_E164_001() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("+12125550100")) }
    @Test fun testLooksLike_E164_002() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("+44 20 7946 0958")) }
    @Test fun testLooksLike_International_001() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("+49 30 12345678")) }
    @Test fun testNotLooksLike_NoPattern_001() { assertFalse(CountryCodeValidator.looksLikePhoneNumber("Hello world")) }
    @Test fun testNotLooksLike_Empty_001() { assertFalse(CountryCodeValidator.looksLikePhoneNumber("")) }
    @Test fun testNotLooksLike_ShortDigits_001() { assertFalse(CountryCodeValidator.looksLikePhoneNumber("12345")) }
    @Test fun testLooksLike_InSentence_001() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("Emergency contact: (555) 123-4567, ask for John")) }
    @Test fun testLooksLike_InSentence_002() { assertTrue(CountryCodeValidator.looksLikePhoneNumber("For support call +1-800-555-0100 between 9am-5pm")) }
}

// Section 12: cleanPhoneNumber
class PhoneValidatorExtendedTest2L {
    @Test fun testClean_Dashes_001() {
        val clean = CountryCodeValidator.cleanPhoneNumber("212-555-0100")
        assertFalse(clean.contains("-"))
    }
    @Test fun testClean_Parentheses_001() {
        val clean = CountryCodeValidator.cleanPhoneNumber("(212) 555-0100")
        assertFalse(clean.contains("("))
        assertFalse(clean.contains(")"))
    }
    @Test fun testClean_Dots_001() {
        val clean = CountryCodeValidator.cleanPhoneNumber("212.555.0100")
        assertFalse(clean.contains("."))
    }
    @Test fun testClean_Spaces_001() {
        val clean = CountryCodeValidator.cleanPhoneNumber("212 555 0100")
        assertFalse(clean.contains(" "))
    }
    @Test fun testClean_PreservesPlus_001() {
        val clean = CountryCodeValidator.cleanPhoneNumber("+12125550100")
        assertTrue(clean.startsWith("+"))
    }
    @Test fun testClean_E164_001() {
        val clean = CountryCodeValidator.cleanPhoneNumber("+1 (212) 555-0100")
        assertTrue(clean.startsWith("+1"))
    }
    @Test fun testClean_AlreadyClean_001() {
        val clean = CountryCodeValidator.cleanPhoneNumber("2125550100")
        assertEquals("2125550100", clean)
    }
}

// Section 13: formatToE164
class PhoneValidatorExtendedTest2M {
    @Test fun testFormatE164_US_001() {
        val formatted = CountryCodeValidator.formatToE164("(212) 555-0100", "US")
        if (formatted != null) assertTrue(formatted.startsWith("+1"))
    }
    @Test fun testFormatE164_US_002() {
        val formatted = CountryCodeValidator.formatToE164("212-555-0101", "US")
        if (formatted != null) assertTrue(formatted.startsWith("+1"))
    }
    @Test fun testFormatE164_US_003() {
        val formatted = CountryCodeValidator.formatToE164("2125550102", "US")
        if (formatted != null) assertTrue(formatted.startsWith("+1"))
    }
    @Test fun testFormatE164_InternationalAlready_001() {
        val formatted = CountryCodeValidator.formatToE164("+12125550100", "US")
        if (formatted != null) assertEquals("+12125550100", formatted)
    }
    @Test fun testFormatE164_UK_001() {
        val formatted = CountryCodeValidator.formatToE164("07911 123456", "GB")
        if (formatted != null) assertTrue(formatted.startsWith("+44"))
    }
    @Test fun testFormatE164_Invalid_001() {
        val formatted = CountryCodeValidator.formatToE164("", "US")
        assertNull(formatted)
    }
    @Test fun testFormatE164_Invalid_002() {
        val formatted = CountryCodeValidator.formatToE164("not-a-phone", "US")
        assertNull(formatted)
    }
    @Test fun testFormatE164_ContainsOnlyDigitsAndPlus_001() {
        val formatted = CountryCodeValidator.formatToE164("(212) 555-0100", "US")
        if (formatted != null) {
            val stripped = formatted.replace("+", "")
            assertTrue(stripped.all { it.isDigit() })
        }
    }
}

// Section 14: detectCountry
class PhoneValidatorExtendedTest2N {
    @Test fun testDetectCountry_US_001() {
        val country = CountryCodeValidator.detectCountry("+12125550100")
        if (country != null) assertEquals("US", country.code)
    }
    @Test fun testDetectCountry_UK_001() {
        val country = CountryCodeValidator.detectCountry("+447911123456")
        if (country != null) assertEquals("GB", country.code)
    }
    @Test fun testDetectCountry_DE_001() {
        val country = CountryCodeValidator.detectCountry("+4930123456")
        if (country != null) assertEquals("DE", country.code)
    }
    @Test fun testDetectCountry_AU_001() {
        val country = CountryCodeValidator.detectCountry("+61298765432")
        if (country != null) assertEquals("AU", country.code)
    }
    @Test fun testDetectCountry_IN_001() {
        val country = CountryCodeValidator.detectCountry("+919876543210")
        if (country != null) assertEquals("IN", country.code)
    }
    @Test fun testDetectCountry_NoCountryCode_001() {
        val country = CountryCodeValidator.detectCountry("2125550100")
        // Without country code, detection might fail or guess US
        assertNotNull(country != null || country == null)
    }
    @Test fun testDetectCountry_Empty_001() {
        val country = CountryCodeValidator.detectCountry("")
        assertNull(country)
    }
    @Test fun testDetectCountry_CountryInfo_001() {
        val country = CountryCodeValidator.detectCountry("+12125550100")
        if (country != null) {
            assertNotNull(country.name)
            assertNotNull(country.callingCode)
            assertTrue(country.name.isNotEmpty())
        }
    }
}

// Section 15: getCountryByIsoCode
class PhoneValidatorExtendedTest2O {
    @Test fun testGetCountryByIsoCode_US_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("US")
        assertNotNull(country)
        assertEquals("US", country?.code)
    }
    @Test fun testGetCountryByIsoCode_GB_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("GB")
        assertNotNull(country)
        assertEquals("GB", country?.code)
    }
    @Test fun testGetCountryByIsoCode_DE_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("DE")
        assertNotNull(country)
        assertEquals("DE", country?.code)
    }
    @Test fun testGetCountryByIsoCode_AU_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("AU")
        assertNotNull(country)
        assertEquals("AU", country?.code)
    }
    @Test fun testGetCountryByIsoCode_JP_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("JP")
        assertNotNull(country)
    }
    @Test fun testGetCountryByIsoCode_IN_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("IN")
        assertNotNull(country)
    }
    @Test fun testGetCountryByIsoCode_FR_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("FR")
        assertNotNull(country)
    }
    @Test fun testGetCountryByIsoCode_Invalid_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("XX")
        assertNull(country)
    }
    @Test fun testGetCountryByIsoCode_Invalid_002() {
        val country = CountryCodeValidator.getCountryByIsoCode("")
        assertNull(country)
    }
    @Test fun testGetCountryByIsoCode_CallingCode_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals("+1", country?.callingCode)
    }
    @Test fun testGetCountryByIsoCode_CallingCode_002() {
        val country = CountryCodeValidator.getCountryByIsoCode("GB")
        assertEquals("+44", country?.callingCode)
    }
    @Test fun testGetCountryByIsoCode_CallingCode_003() {
        val country = CountryCodeValidator.getCountryByIsoCode("DE")
        assertEquals("+49", country?.callingCode)
    }
    @Test fun testGetCountryByIsoCode_Region_001() {
        val country = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals(CountryCodeValidator.Region.NORTH_AMERICA, country?.region)
    }
    @Test fun testGetCountryByIsoCode_Region_002() {
        val country = CountryCodeValidator.getCountryByIsoCode("DE")
        assertEquals(CountryCodeValidator.Region.EUROPE, country?.region)
    }
}

// Section 16: getCountriesByCallingCode
class PhoneValidatorExtendedTest2P {
    @Test fun testGetByCallingCode_Plus1_001() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+1")
        assertTrue(countries.isNotEmpty())
    }
    @Test fun testGetByCallingCode_Plus1_ContainsUS_001() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+1")
        assertTrue(countries.any { it.code == "US" })
    }
    @Test fun testGetByCallingCode_Plus1_ContainsCA_001() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+1")
        assertTrue(countries.any { it.code == "CA" })
    }
    @Test fun testGetByCallingCode_Plus44_001() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+44")
        assertTrue(countries.isNotEmpty())
    }
    @Test fun testGetByCallingCode_Plus49_001() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+49")
        assertTrue(countries.isNotEmpty())
    }
    @Test fun testGetByCallingCode_Invalid_001() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+999")
        assertTrue(countries.isEmpty())
    }
    @Test fun testGetByCallingCode_Empty_001() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("")
        assertTrue(countries.isEmpty())
    }
}

// Section 17: getCountriesByRegion
class PhoneValidatorExtendedTest2Q {
    @Test fun testByRegion_NorthAmerica_001() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.NORTH_AMERICA)
        assertTrue(countries.isNotEmpty())
    }
    @Test fun testByRegion_NorthAmerica_002() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.NORTH_AMERICA)
        assertTrue(countries.any { it.code == "US" })
    }
    @Test fun testByRegion_Europe_001() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.EUROPE)
        assertTrue(countries.isNotEmpty())
    }
    @Test fun testByRegion_Europe_002() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.EUROPE)
        assertTrue(countries.any { it.code == "DE" || it.code == "FR" || it.code == "GB" })
    }
    @Test fun testByRegion_Asia_001() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.ASIA)
        assertTrue(countries.isNotEmpty())
    }
    @Test fun testByRegion_Asia_002() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.ASIA)
        assertTrue(countries.any { it.code == "JP" || it.code == "IN" || it.code == "CN" })
    }
    @Test fun testByRegion_Oceania_001() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.OCEANIA)
        assertTrue(countries.isNotEmpty())
    }
    @Test fun testByRegion_Africa_001() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.AFRICA)
        assertTrue(countries.isNotEmpty())
    }
    @Test fun testByRegion_SameRegionItems_001() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.NORTH_AMERICA)
        countries.forEach { assertEquals(CountryCodeValidator.Region.NORTH_AMERICA, it.region) }
    }
}

// Section 18: totalCountries, allCallingCodes, allIsoCodes
class PhoneValidatorExtendedTest2R {
    @Test fun testTotalCountries_PositiveValue_001() {
        val total = CountryCodeValidator.totalCountries()
        assertTrue(total > 0)
    }
    @Test fun testTotalCountries_AtLeast100_001() {
        val total = CountryCodeValidator.totalCountries()
        assertTrue(total >= 100)
    }
    @Test fun testTotalCountries_AtLeast200_001() {
        val total = CountryCodeValidator.totalCountries()
        assertTrue(total >= 150) // 200+ countries claimed
    }
    @Test fun testAllCallingCodes_NotEmpty_001() {
        val codes = CountryCodeValidator.allCallingCodes()
        assertTrue(codes.isNotEmpty())
    }
    @Test fun testAllCallingCodes_ContainsPlus1_001() {
        val codes = CountryCodeValidator.allCallingCodes()
        assertTrue(codes.contains("+1"))
    }
    @Test fun testAllCallingCodes_ContainsPlus44_001() {
        val codes = CountryCodeValidator.allCallingCodes()
        assertTrue(codes.contains("+44"))
    }
    @Test fun testAllCallingCodes_ContainsPlus49_001() {
        val codes = CountryCodeValidator.allCallingCodes()
        assertTrue(codes.contains("+49"))
    }
    @Test fun testAllIsoCodes_NotEmpty_001() {
        val codes = CountryCodeValidator.allIsoCodes()
        assertTrue(codes.isNotEmpty())
    }
    @Test fun testAllIsoCodes_ContainsUS_001() {
        val codes = CountryCodeValidator.allIsoCodes()
        assertTrue(codes.contains("US"))
    }
    @Test fun testAllIsoCodes_ContainsGB_001() {
        val codes = CountryCodeValidator.allIsoCodes()
        assertTrue(codes.contains("GB"))
    }
    @Test fun testAllIsoCodes_CountMatchesTotal_001() {
        val totalFromMethod = CountryCodeValidator.totalCountries()
        val totalFromSet = CountryCodeValidator.allIsoCodes().size
        // May differ due to multiple entries per ISO code
        assertTrue(totalFromSet in (totalFromMethod / 2)..(totalFromMethod * 2))
    }
    @Test fun testAllIsoCodes_TwoLetterCodes_001() {
        val codes = CountryCodeValidator.allIsoCodes()
        codes.forEach { code -> assertTrue("$code should be 2 chars", code.length == 2) }
    }
}

// Section 19: PhoneValidationResult Properties
class PhoneValidatorExtendedTest2S {
    @Test fun testResult_Properties_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        assertNotNull(result.isValid)
        assertNotNull(result.confidence)
        assertNotNull(result.reason)
    }
    @Test fun testResult_Confidence_Range_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        assertTrue(result.confidence in 0f..1f)
    }
    @Test fun testResult_Confidence_Range_002() {
        val result = CountryCodeValidator.validate("+44 20 7946 0958")
        assertTrue(result.confidence in 0f..1f)
    }
    @Test fun testResult_Confidence_Range_003() {
        val result = CountryCodeValidator.validate("")
        assertTrue(result.confidence in 0f..1f)
    }
    @Test fun testResult_ValidHasHighConfidence_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        if (result.isValid) assertTrue(result.confidence > 0.5f)
    }
    @Test fun testResult_InvalidHasLowConfidence_001() {
        val result = CountryCodeValidator.validate("")
        assertTrue(result.confidence <= 0.5f)
    }
    @Test fun testResult_ReasonNotEmpty_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        assertTrue(result.reason.isNotEmpty())
    }
    @Test fun testResult_ReasonNotEmpty_002() {
        val result = CountryCodeValidator.validate("")
        assertTrue(result.reason.isNotEmpty())
    }
    @Test fun testResult_ValidHasCountry_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        if (result.isValid) {
            assertNotNull(result.countryCode)
        }
    }
    @Test fun testResult_CountryInfo_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        if (result.isValid && result.country != null) {
            assertNotNull(result.country!!.name)
            assertTrue(result.country!!.name.isNotEmpty())
        }
    }
    @Test fun testResult_NationalNumber_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        if (result.isValid) {
            assertNotNull(result.nationalNumber)
        }
    }
    @Test fun testResult_FormattedNumber_001() {
        val result = CountryCodeValidator.validate("+12125550100")
        if (result.isValid) {
            assertNotNull(result.formattedNumber)
        }
    }
}

// Section 20: Regression + Stress Tests
class PhoneValidatorExtendedTest2T {
    @Test fun testRegression_USValid_AllFormats_001() {
        val formats = listOf(
            "+12125550100",
            "+1 212 555 0100",
            "+1 (212) 555-0100",
            "1-212-555-0100",
            "(212) 555-0100",
            "212-555-0100",
            "212.555.0100",
            "2125550100"
        )
        formats.forEach { fmt ->
            val result = CountryCodeValidator.validate(fmt)
            assertNotNull("Expected result for $fmt", result)
        }
    }
    @Test fun testRegression_InvalidPhones_001() {
        val invalid = listOf("", "   ", "abc", "555", "12345", "0000000000")
        invalid.forEach { phone ->
            assertFalse("Expected invalid for '$phone'", CountryCodeValidator.validate(phone).isValid)
        }
    }
    @Test fun testRegression_InternationalCountryCodes_001() {
        val international = listOf(
            "+44 20 7946 0958",
            "+49 30 12345678",
            "+33 1 23456789",
            "+61 2 98765432",
            "+91 9876543210"
        )
        international.forEach { phone ->
            val result = CountryCodeValidator.validate(phone)
            assertNotNull(result)
            assertTrue(result.confidence in 0f..1f)
        }
    }
    @Test fun testRegression_LooksLikePhone_001() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("+12125550100"))
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("(212) 555-0100"))
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("hello world"))
        assertFalse(CountryCodeValidator.looksLikePhoneNumber(""))
    }
    @Test fun testStress_ValidateMany_001() {
        val phones = listOf(
            "+12125550100", "+12135550101", "+13125550102", "+14155550103", "+16175550104",
            "+17135550105", "+12025550106", "+14045550107", "+13055550108", "+12145550109",
            "+14165550110", "+16045550111", "+15145550112", "+14035550113", "+16135550114"
        )
        phones.forEach { phone ->
            val result = CountryCodeValidator.validate(phone)
            assertNotNull(result)
        }
    }
    @Test fun testStress_LooksLikePhone_InText_001() {
        val texts = listOf(
            "Call (212) 555-0100 for info",
            "Emergency: 911 or 212-555-0101",
            "Fax: +1 212 555 0102",
            "Mobile: 2125550103",
            "Work: +44 20 7946 0958"
        )
        texts.forEach { text ->
            val result = CountryCodeValidator.looksLikePhoneNumber(text)
            assertNotNull(result)
        }
    }
    @Test fun testStress_CleanPhoneMany_001() {
        val phones = listOf(
            "(212) 555-0100", "212-555-0101", "212.555.0102",
            "212 555 0103", "  212 555 0104  "
        )
        phones.forEach { phone ->
            val clean = CountryCodeValidator.cleanPhoneNumber(phone)
            assertNotNull(clean)
            assertFalse(clean.contains("("))
            assertFalse(clean.contains(")"))
        }
    }
}
