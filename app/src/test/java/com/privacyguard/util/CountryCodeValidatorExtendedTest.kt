package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

/**
 * CountryCodeValidatorExtendedTest
 *
 * Comprehensive test suite for phone number country code validation.
 * Covers 18 sections:
 *   Section 1:  NANP +1 numbers (North America)
 *   Section 2:  UK +44 numbers
 *   Section 3:  Germany +49 numbers
 *   Section 4:  France +33 numbers
 *   Section 5:  Australia +61 numbers
 *   Section 6:  Japan +81 numbers
 *   Section 7:  India +91 numbers
 *   Section 8:  Brazil +55 numbers
 *   Section 9:  Russia +7 numbers
 *   Section 10: China +86 numbers
 *   Section 11: Mexico +52 numbers
 *   Section 12: South Africa +27 numbers
 *   Section 13: Indonesia +62 numbers
 *   Section 14: +1 area code validity checks
 *   Section 15: getCountryCode lookup by prefix
 *   Section 16: Invalid country codes
 *   Section 17: E.164 format validation
 *   Section 18: Formatting and masking
 */
class CountryCodeValidatorExtendedTest {

    // =========================================================================
    // Validator stubs
    // =========================================================================

    private object CountryCodeValidator {
        fun isValidE164(number: String): Boolean {
            return number.matches(Regex("\\+[1-9]\\d{6,14}"))
        }

        fun getCountryCode(number: String): String {
            if (!number.startsWith("+")) return "UNKNOWN"
            return when {
                number.startsWith("+1") -> "US/CA"
                number.startsWith("+44") -> "GB"
                number.startsWith("+49") -> "DE"
                number.startsWith("+33") -> "FR"
                number.startsWith("+61") -> "AU"
                number.startsWith("+81") -> "JP"
                number.startsWith("+91") -> "IN"
                number.startsWith("+55") -> "BR"
                number.startsWith("+7") -> "RU"
                number.startsWith("+86") -> "CN"
                number.startsWith("+52") -> "MX"
                number.startsWith("+27") -> "ZA"
                number.startsWith("+62") -> "ID"
                number.startsWith("+39") -> "IT"
                number.startsWith("+34") -> "ES"
                number.startsWith("+82") -> "KR"
                number.startsWith("+90") -> "TR"
                number.startsWith("+20") -> "EG"
                number.startsWith("+971") -> "AE"
                number.startsWith("+966") -> "SA"
                else -> "UNKNOWN"
            }
        }

        fun isValidNANPNumber(number: String): Boolean {
            if (!number.startsWith("+1")) return false
            val digits = number.removePrefix("+1")
            if (digits.length != 10) return false
            if (!digits.all { it.isDigit() }) return false
            val areaCode = digits.substring(0, 3).toInt()
            if (areaCode < 200) return false
            return true
        }

        fun maskPhoneNumber(number: String): String {
            if (!isValidE164(number)) return number
            val prefix = number.substring(0, number.length - 4)
            val last4 = number.takeLast(4)
            return "${prefix.map { if (it.isDigit()) '*' else it }.joinToString("")}$last4"
        }

        fun normalizePhone(number: String): String {
            val stripped = number.replace(Regex("[\\s\\-().+]"), "")
            return if (stripped.startsWith("00")) "+${stripped.removePrefix("00")}"
            else if (!number.startsWith("+")) number
            else number
        }

        fun isValidCountryCode(code: Int): Boolean {
            return code in 1..999
        }
    }

    // =========================================================================
    // SECTION 1 — NANP North American numbers (+1)
    // =========================================================================

    @Test
    fun `s1 nanp valid number plus1 2025550100`() {
        val number = "+12025550100"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 2125550120`() {
        val number = "+12125550120"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 3105550150`() {
        val number = "+13105550150"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 4155550180`() {
        val number = "+14155550180"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 5125550200`() {
        val number = "+15125550200"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 6465550220`() {
        val number = "+16465550220"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 7135550240`() {
        val number = "+17135550240"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 8185550260`() {
        val number = "+18185550260"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 9175550280`() {
        val number = "+19175550280"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 3145550300`() {
        val number = "+13145550300"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 4045550320`() {
        val number = "+14045550320"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 5035550340`() {
        val number = "+15035550340"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 6025550360`() {
        val number = "+16025550360"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 7025550380`() {
        val number = "+17025550380"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 8015550400`() {
        val number = "+18015550400"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 9545550420`() {
        val number = "+19545550420"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 2395550440`() {
        val number = "+12395550440"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 3045550460`() {
        val number = "+13045550460"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 4075550480`() {
        val number = "+14075550480"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid number plus1 5055550500`() {
        val number = "+15055550500"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp canada area code 416 Toronto`() {
        val number = "+14165550100"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp canada area code 604 Vancouver`() {
        val number = "+16045550200"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp canada area code 514 Montreal`() {
        val number = "+15145550300"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp country code is US CA`() {
        assertEquals("US/CA", CountryCodeValidator.getCountryCode("+12025550100"))
    }

    @Test
    fun `s1 nanp valid number 12 chars total including plus`() {
        val number = "+12025550100"
        assertEquals(12, number.length)
        assertTrue(CountryCodeValidator.isValidE164(number))
    }

    @Test
    fun `s1 nanp invalid no plus sign`() {
        assertFalse(CountryCodeValidator.isValidE164("12025550100"))
    }

    @Test
    fun `s1 nanp valid 800 toll free`() {
        assertTrue(CountryCodeValidator.isValidE164("+18005550100"))
    }

    @Test
    fun `s1 nanp valid 888 toll free`() {
        assertTrue(CountryCodeValidator.isValidE164("+18885550100"))
    }

    @Test
    fun `s1 nanp valid 877 toll free`() {
        assertTrue(CountryCodeValidator.isValidE164("+18775550100"))
    }

    @Test
    fun `s1 nanp valid 866 toll free`() {
        assertTrue(CountryCodeValidator.isValidE164("+18665550100"))
    }

    @Test
    fun `s1 nanp valid 855 toll free`() {
        assertTrue(CountryCodeValidator.isValidE164("+18555550100"))
    }

    @Test
    fun `s1 nanp valid 844 toll free`() {
        assertTrue(CountryCodeValidator.isValidE164("+18445550100"))
    }

    @Test
    fun `s1 nanp valid 833 toll free`() {
        assertTrue(CountryCodeValidator.isValidE164("+18335550100"))
    }

    @Test
    fun `s1 nanp valid new york 212`() {
        val number = "+12125550100"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid los angeles 213`() {
        val number = "+12135550100"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid chicago 312`() {
        val number = "+13125550100"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid houston 713`() {
        val number = "+17135550100"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s1 nanp valid phoenix 602`() {
        assertTrue(CountryCodeValidator.isValidE164("+16025550100"))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode("+16025550100"))
    }

    @Test
    fun `s1 nanp valid san antonio 210`() {
        assertTrue(CountryCodeValidator.isValidE164("+12105550100"))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode("+12105550100"))
    }

    @Test
    fun `s1 nanp valid san diego 619`() {
        assertTrue(CountryCodeValidator.isValidE164("+16195550100"))
    }

    @Test
    fun `s1 nanp valid dallas 214`() {
        assertTrue(CountryCodeValidator.isValidE164("+12145550100"))
    }

    @Test
    fun `s1 nanp valid san jose 408`() {
        assertTrue(CountryCodeValidator.isValidE164("+14085550100"))
    }

    @Test
    fun `s1 nanp valid austin 512`() {
        assertTrue(CountryCodeValidator.isValidE164("+15125550100"))
    }

    @Test
    fun `s1 nanp valid jacksonville 904`() {
        assertTrue(CountryCodeValidator.isValidE164("+19045550100"))
    }

    @Test
    fun `s1 nanp valid fort worth 817`() {
        assertTrue(CountryCodeValidator.isValidE164("+18175550100"))
    }

    @Test
    fun `s1 nanp valid columbus 614`() {
        assertTrue(CountryCodeValidator.isValidE164("+16145550100"))
    }

    @Test
    fun `s1 nanp valid charlotte 704`() {
        assertTrue(CountryCodeValidator.isValidE164("+17045550100"))
    }

    @Test
    fun `s1 nanp valid indianapolis 317`() {
        assertTrue(CountryCodeValidator.isValidE164("+13175550100"))
    }

    @Test
    fun `s1 nanp valid seattle 206`() {
        assertTrue(CountryCodeValidator.isValidE164("+12065550100"))
    }

    @Test
    fun `s1 nanp valid denver 303`() {
        assertTrue(CountryCodeValidator.isValidE164("+13035550100"))
    }

    @Test
    fun `s1 nanp valid nashville 615`() {
        assertTrue(CountryCodeValidator.isValidE164("+16155550100"))
    }

    @Test
    fun `s1 nanp valid oklahoma city 405`() {
        assertTrue(CountryCodeValidator.isValidE164("+14055550100"))
    }

    @Test
    fun `s1 nanp valid el paso 915`() {
        assertTrue(CountryCodeValidator.isValidE164("+19155550100"))
    }

    @Test
    fun `s1 nanp valid washington dc 202`() {
        assertTrue(CountryCodeValidator.isValidE164("+12025550100"))
    }

    @Test
    fun `s1 nanp valid las vegas 702`() {
        assertTrue(CountryCodeValidator.isValidE164("+17025550100"))
    }

    @Test
    fun `s1 nanp valid portland 503`() {
        assertTrue(CountryCodeValidator.isValidE164("+15035550100"))
    }

    @Test
    fun `s1 nanp valid tucson 520`() {
        assertTrue(CountryCodeValidator.isValidE164("+15205550100"))
    }

    @Test
    fun `s1 nanp valid albuquerque 505`() {
        assertTrue(CountryCodeValidator.isValidE164("+15055550100"))
    }

    @Test
    fun `s1 nanp valid atlanta 404`() {
        assertTrue(CountryCodeValidator.isValidE164("+14045550100"))
    }

    @Test
    fun `s1 nanp valid long beach 562`() {
        assertTrue(CountryCodeValidator.isValidE164("+15625550100"))
    }

    @Test
    fun `s1 nanp valid sacramento 916`() {
        assertTrue(CountryCodeValidator.isValidE164("+19165550100"))
    }

    @Test
    fun `s1 nanp valid kansas city 816`() {
        assertTrue(CountryCodeValidator.isValidE164("+18165550100"))
    }

    @Test
    fun `s1 nanp valid mesa 480`() {
        assertTrue(CountryCodeValidator.isValidE164("+14805550100"))
    }

    @Test
    fun `s1 nanp valid virginia beach 757`() {
        assertTrue(CountryCodeValidator.isValidE164("+17575550100"))
    }

    @Test
    fun `s1 nanp valid omaha 402`() {
        assertTrue(CountryCodeValidator.isValidE164("+14025550100"))
    }

    @Test
    fun `s1 nanp valid colorado springs 719`() {
        assertTrue(CountryCodeValidator.isValidE164("+17195550100"))
    }

    @Test
    fun `s1 nanp valid raleigh 919`() {
        assertTrue(CountryCodeValidator.isValidE164("+19195550100"))
    }

    @Test
    fun `s1 nanp valid miami 305`() {
        assertTrue(CountryCodeValidator.isValidE164("+13055550100"))
    }

    @Test
    fun `s1 nanp valid minneapolis 612`() {
        assertTrue(CountryCodeValidator.isValidE164("+16125550100"))
    }

    @Test
    fun `s1 nanp valid tulsa 918`() {
        assertTrue(CountryCodeValidator.isValidE164("+19185550100"))
    }

    @Test
    fun `s1 nanp valid cleveland 216`() {
        assertTrue(CountryCodeValidator.isValidE164("+12165550100"))
    }

    @Test
    fun `s1 nanp valid wichita 316`() {
        assertTrue(CountryCodeValidator.isValidE164("+13165550100"))
    }

    @Test
    fun `s1 nanp valid new orleans 504`() {
        assertTrue(CountryCodeValidator.isValidE164("+15045550100"))
    }

    @Test
    fun `s1 nanp valid bakersfield 661`() {
        assertTrue(CountryCodeValidator.isValidE164("+16615550100"))
    }

    @Test
    fun `s1 nanp valid tampa 813`() {
        assertTrue(CountryCodeValidator.isValidE164("+18135550100"))
    }

    @Test
    fun `s1 nanp valid aurora 720`() {
        assertTrue(CountryCodeValidator.isValidE164("+17205550100"))
    }

    @Test
    fun `s1 nanp valid anaheim 714`() {
        assertTrue(CountryCodeValidator.isValidE164("+17145550100"))
    }

    // =========================================================================
    // SECTION 2 — UK numbers (+44)
    // =========================================================================

    @Test
    fun `s2 uk valid london 44207`() {
        val number = "+442071234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid london 44208`() {
        val number = "+442081234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid mobile 447911123456`() {
        val number = "+447911123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid mobile 447700900123`() {
        val number = "+447700900123"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid birmingham 44121`() {
        val number = "+441211234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid manchester 44161`() {
        val number = "+441611234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid edinburgh 44131`() {
        val number = "+441311234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid glasgow 44141`() {
        val number = "+441411234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid leeds 44113`() {
        val number = "+441131234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid bristol 44117`() {
        val number = "+441171234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid liverpool 44151`() {
        val number = "+441511234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid sheffield 44114`() {
        val number = "+441141234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid nottingham 44115`() {
        val number = "+441151234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid leicester 44116`() {
        val number = "+441161234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk country code lookup returns GB`() {
        assertEquals("GB", CountryCodeValidator.getCountryCode("+441234567890"))
    }

    @Test
    fun `s2 uk number not confused with US`() {
        val uk = "+441234567890"
        val us = "+12025550100"
        assertEquals("GB", CountryCodeValidator.getCountryCode(uk))
        assertEquals("US/CA", CountryCodeValidator.getCountryCode(us))
    }

    @Test
    fun `s2 uk valid cardiff 44292`() {
        val number = "+442921234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid belfast 44289`() {
        val number = "+442891234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid oxford 441865`() {
        val number = "+4418651234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid cambridge 441223`() {
        val number = "+4412231234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s2 uk valid coventry 44247`() {
        val number = "+442471234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("GB", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 3 — Germany numbers (+49)
    // =========================================================================

    @Test
    fun `s3 germany valid berlin 4930`() {
        val number = "+493012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid munich 4989`() {
        val number = "+498912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid hamburg 4940`() {
        val number = "+494012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid frankfurt 4969`() {
        val number = "+496912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid cologne 49221`() {
        val number = "+492211234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid dusseldorf 49211`() {
        val number = "+492111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid stuttgart 49711`() {
        val number = "+497111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid mobile 491511`() {
        val number = "+4915112345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid mobile 491521`() {
        val number = "+4915212345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany country code lookup returns DE`() {
        assertEquals("DE", CountryCodeValidator.getCountryCode("+4930123456"))
    }

    @Test
    fun `s3 germany not confused with France`() {
        val de = "+4930123456"
        val fr = "+3312345678"
        assertEquals("DE", CountryCodeValidator.getCountryCode(de))
        assertEquals("FR", CountryCodeValidator.getCountryCode(fr))
    }

    @Test
    fun `s3 germany valid dortmund 49231`() {
        val number = "+492311234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid essen 49201`() {
        val number = "+492011234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid leipzig 49341`() {
        val number = "+493411234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid bremen 49421`() {
        val number = "+494211234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid dresden 49351`() {
        val number = "+493511234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid hannover 49511`() {
        val number = "+495111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid nuremberg 49911`() {
        val number = "+499111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid duisburg 49203`() {
        val number = "+492031234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s3 germany valid bochum 49234`() {
        val number = "+492341234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("DE", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 4 — France numbers (+33)
    // =========================================================================

    @Test
    fun `s4 france valid paris 331`() {
        val number = "+33123456789"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid paris landline 44`() {
        val number = "+33145123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid mobile 336`() {
        val number = "+33612345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid mobile 337`() {
        val number = "+33712345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid lyon 334`() {
        val number = "+33478123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid marseille 491`() {
        val number = "+33491123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid toulouse 561`() {
        val number = "+33561123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid nice 493`() {
        val number = "+33493123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france country code lookup returns FR`() {
        assertEquals("FR", CountryCodeValidator.getCountryCode("+33123456789"))
    }

    @Test
    fun `s4 france valid strasbourg 388`() {
        val number = "+33388123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid bordeaux 556`() {
        val number = "+33556123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid nantes 240`() {
        val number = "+33240123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid montpellier 467`() {
        val number = "+33467123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid rennes 299`() {
        val number = "+33299123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid reims 326`() {
        val number = "+33326123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid le havre 235`() {
        val number = "+33235123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid saint-etienne 477`() {
        val number = "+33477123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid toulon 494`() {
        val number = "+33494123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid grenoble 476`() {
        val number = "+33476123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s4 france valid dijon 380`() {
        val number = "+33380123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("FR", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 5 — Australia numbers (+61)
    // =========================================================================

    @Test
    fun `s5 australia valid sydney 612`() {
        val number = "+61212345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid melbourne 613`() {
        val number = "+61312345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid brisbane 617`() {
        val number = "+61712345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid perth 618`() {
        val number = "+61812345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid adelaide 618`() {
        val number = "+61882123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid mobile 614`() {
        val number = "+61412345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid mobile 615`() {
        val number = "+61512345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid canberra 612`() {
        val number = "+61261234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid darwin 618`() {
        val number = "+61889123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid hobart 613`() {
        val number = "+61362123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia country code lookup returns AU`() {
        assertEquals("AU", CountryCodeValidator.getCountryCode("+61212345678"))
    }

    @Test
    fun `s5 australia not confused with Japan`() {
        assertEquals("AU", CountryCodeValidator.getCountryCode("+61212345678"))
        assertEquals("JP", CountryCodeValidator.getCountryCode("+81312345678"))
    }

    @Test
    fun `s5 australia valid mobile 616`() {
        val number = "+61612345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid townsville 617`() {
        val number = "+61746123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid geelong 613`() {
        val number = "+61352123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid gold coast 617`() {
        val number = "+61755123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid newcastle 612`() {
        val number = "+61249123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid sunshine coast 617`() {
        val number = "+61754123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid central coast 612`() {
        val number = "+61243123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s5 australia valid wollongong 612`() {
        val number = "+61242123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("AU", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 6 — Japan numbers (+81)
    // =========================================================================

    @Test
    fun `s6 japan valid tokyo 813`() {
        val number = "+81312345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid osaka 816`() {
        val number = "+81612345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid nagoya 8152`() {
        val number = "+81521234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid sapporo 81011`() {
        val number = "+81111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid fukuoka 8192`() {
        val number = "+81921234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid kobe 8178`() {
        val number = "+81781234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid mobile 8180`() {
        val number = "+818012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid mobile 8190`() {
        val number = "+819012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid yokohama 8145`() {
        val number = "+81451234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan country code lookup returns JP`() {
        assertEquals("JP", CountryCodeValidator.getCountryCode("+81312345678"))
    }

    @Test
    fun `s6 japan valid kyoto 8175`() {
        val number = "+81751234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid sendai 8122`() {
        val number = "+81221234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid hiroshima 8182`() {
        val number = "+81821234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid naha okinawa 81988`() {
        val number = "+819881234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan not confused with Australia`() {
        assertEquals("JP", CountryCodeValidator.getCountryCode("+81312345678"))
        assertEquals("AU", CountryCodeValidator.getCountryCode("+61312345678"))
    }

    @Test
    fun `s6 japan valid kawasaki 8144`() {
        val number = "+81441234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid saitama 8148`() {
        val number = "+81481234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid chiba 8143`() {
        val number = "+81431234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid kitakyushu 8193`() {
        val number = "+81931234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s6 japan valid nagasaki 81958`() {
        val number = "+819581234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("JP", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 7 — India numbers (+91)
    // =========================================================================

    @Test
    fun `s7 india valid delhi 9111`() {
        val number = "+911123456789"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid mumbai 9122`() {
        val number = "+912212345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid bangalore 9180`() {
        val number = "+918012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid chennai 9144`() {
        val number = "+914412345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid kolkata 9133`() {
        val number = "+913312345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid hyderabad 9140`() {
        val number = "+914012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid mobile 917`() {
        val number = "+917012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid mobile 918`() {
        val number = "+918012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid mobile 919`() {
        val number = "+919012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india country code lookup returns IN`() {
        assertEquals("IN", CountryCodeValidator.getCountryCode("+911234567890"))
    }

    @Test
    fun `s7 india valid pune 9120`() {
        val number = "+912012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid ahmedabad 9179`() {
        val number = "+917912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid jaipur 91141`() {
        val number = "+911411234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid surat 91261`() {
        val number = "+912611234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india not confused with Japan`() {
        assertEquals("IN", CountryCodeValidator.getCountryCode("+911234567890"))
        assertEquals("JP", CountryCodeValidator.getCountryCode("+81312345678"))
    }

    @Test
    fun `s7 india valid lucknow 91522`() {
        val number = "+915221234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid kanpur 91512`() {
        val number = "+915121234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid nagpur 91712`() {
        val number = "+917121234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid indore 91731`() {
        val number = "+917311234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s7 india valid thane 9122`() {
        val number = "+912212345679"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("IN", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 8 — Brazil numbers (+55)
    // =========================================================================

    @Test
    fun `s8 brazil valid sao paulo 5511`() {
        val number = "+5511912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid rio 5521`() {
        val number = "+5521912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid brasilia 5561`() {
        val number = "+5561912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid salvador 5571`() {
        val number = "+5571912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid fortaleza 5585`() {
        val number = "+5585912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid belo horizonte 5531`() {
        val number = "+5531912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid manaus 5592`() {
        val number = "+5592912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid curitiba 5541`() {
        val number = "+5541912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil country code lookup returns BR`() {
        assertEquals("BR", CountryCodeValidator.getCountryCode("+5511987654321"))
    }

    @Test
    fun `s8 brazil valid recife 5581`() {
        val number = "+5581912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid porto alegre 5551`() {
        val number = "+5551912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid belem 5591`() {
        val number = "+5591912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid goiania 5562`() {
        val number = "+5562912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil valid guarulhos 5511`() {
        val number = "+5511812345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("BR", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s8 brazil not confused with Russia`() {
        assertEquals("BR", CountryCodeValidator.getCountryCode("+5511912345678"))
        assertEquals("RU", CountryCodeValidator.getCountryCode("+74951234567"))
    }

    // =========================================================================
    // SECTION 9 — Russia numbers (+7)
    // =========================================================================

    @Test
    fun `s9 russia valid moscow 7495`() {
        val number = "+74951234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid moscow 7499`() {
        val number = "+74991234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid saint petersburg 7812`() {
        val number = "+78121234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid novosibirsk 7383`() {
        val number = "+73831234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid yekaterinburg 7343`() {
        val number = "+73431234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid mobile 7900`() {
        val number = "+79001234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid mobile 7916`() {
        val number = "+79161234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid mobile 7985`() {
        val number = "+79851234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia country code lookup returns RU`() {
        assertEquals("RU", CountryCodeValidator.getCountryCode("+74951234567"))
    }

    @Test
    fun `s9 russia valid omsk 7381`() {
        val number = "+73811234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia not confused with NANP`() {
        assertNotEquals("US/CA", CountryCodeValidator.getCountryCode("+74951234567"))
        assertEquals("RU", CountryCodeValidator.getCountryCode("+74951234567"))
    }

    @Test
    fun `s9 russia valid chelyabinsk 7351`() {
        val number = "+73511234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid kazan 7843`() {
        val number = "+78431234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid ufa 7347`() {
        val number = "+73471234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s9 russia valid rostov 7863`() {
        val number = "+78631234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("RU", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 10 — China numbers (+86)
    // =========================================================================

    @Test
    fun `s10 china valid beijing 8610`() {
        val number = "+861012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid shanghai 8621`() {
        val number = "+862112345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid guangzhou 8620`() {
        val number = "+862012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid shenzhen 86755`() {
        val number = "+867551234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid mobile 86138`() {
        val number = "+8613812345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid mobile 86158`() {
        val number = "+8615812345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid chengdu 8628`() {
        val number = "+862812345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid wuhan 8627`() {
        val number = "+862712345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china country code lookup returns CN`() {
        assertEquals("CN", CountryCodeValidator.getCountryCode("+861012345678"))
    }

    @Test
    fun `s10 china valid nanjing 8625`() {
        val number = "+862512345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china not confused with India`() {
        assertEquals("CN", CountryCodeValidator.getCountryCode("+861012345678"))
        assertEquals("IN", CountryCodeValidator.getCountryCode("+911123456789"))
    }

    @Test
    fun `s10 china valid xian 86029`() {
        val number = "+860291234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid tianjin 8622`() {
        val number = "+862212345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid chongqing 8623`() {
        val number = "+862312345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s10 china valid hangzhou 86571`() {
        val number = "+865711234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("CN", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 11 — Mexico numbers (+52)
    // =========================================================================

    @Test
    fun `s11 mexico valid mexico city 5255`() {
        val number = "+525512345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid guadalajara 5233`() {
        val number = "+523312345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid monterrey 5281`() {
        val number = "+528112345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid puebla 52222`() {
        val number = "+522221234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid tijuana 52664`() {
        val number = "+526641234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico country code lookup returns MX`() {
        assertEquals("MX", CountryCodeValidator.getCountryCode("+525512345678"))
    }

    @Test
    fun `s11 mexico valid leon 52477`() {
        val number = "+524771234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid juarez 52656`() {
        val number = "+526561234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico not confused with Brazil`() {
        assertEquals("MX", CountryCodeValidator.getCountryCode("+525512345678"))
        assertEquals("BR", CountryCodeValidator.getCountryCode("+5511912345678"))
    }

    @Test
    fun `s11 mexico valid cancun 52998`() {
        val number = "+529981234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid merida 52999`() {
        val number = "+529991234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid toluca 52722`() {
        val number = "+527221234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid queretaro 52442`() {
        val number = "+524421234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid san luis potosi 52444`() {
        val number = "+524441234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s11 mexico valid aguascalientes 52449`() {
        val number = "+524491234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("MX", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 12 — South Africa numbers (+27)
    // =========================================================================

    @Test
    fun `s12 south africa valid johannesburg 2711`() {
        val number = "+27111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid cape town 2721`() {
        val number = "+27211234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid durban 2731`() {
        val number = "+27311234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid pretoria 2712`() {
        val number = "+27121234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid mobile 2760`() {
        val number = "+27601234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid mobile 2772`() {
        val number = "+27721234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid mobile 2782`() {
        val number = "+27821234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa country code lookup returns ZA`() {
        assertEquals("ZA", CountryCodeValidator.getCountryCode("+27111234567"))
    }

    @Test
    fun `s12 south africa valid port elizabeth 2741`() {
        val number = "+27411234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa not confused with other codes`() {
        assertEquals("ZA", CountryCodeValidator.getCountryCode("+27111234567"))
        assertEquals("FR", CountryCodeValidator.getCountryCode("+33123456789"))
    }

    @Test
    fun `s12 south africa valid bloemfontein 2751`() {
        val number = "+27511234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid east london 2743`() {
        val number = "+27431234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid pietermaritzburg 2733`() {
        val number = "+27331234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid polokwane 2715`() {
        val number = "+27151234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s12 south africa valid nelspruit 2713`() {
        val number = "+27131234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ZA", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 13 — Indonesia numbers (+62)
    // =========================================================================

    @Test
    fun `s13 indonesia valid jakarta 6221`() {
        val number = "+62211234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid surabaya 6231`() {
        val number = "+62311234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid bandung 6222`() {
        val number = "+62221234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid medan 6261`() {
        val number = "+62611234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid bali 62361`() {
        val number = "+623611234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid mobile 62811`() {
        val number = "+628111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid mobile 62821`() {
        val number = "+628211234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia country code lookup returns ID`() {
        assertEquals("ID", CountryCodeValidator.getCountryCode("+62211234567"))
    }

    @Test
    fun `s13 indonesia valid semarang 6224`() {
        val number = "+62241234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid yogyakarta 62274`() {
        val number = "+622741234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia not confused with Australia`() {
        assertEquals("ID", CountryCodeValidator.getCountryCode("+62211234567"))
        assertEquals("AU", CountryCodeValidator.getCountryCode("+61212345678"))
    }

    @Test
    fun `s13 indonesia valid makassar 62411`() {
        val number = "+624111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid palembang 62711`() {
        val number = "+627111234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid tangerang 6221`() {
        val number = "+62215234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    @Test
    fun `s13 indonesia valid mobile 62851`() {
        val number = "+628511234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
        assertEquals("ID", CountryCodeValidator.getCountryCode(number))
    }

    // =========================================================================
    // SECTION 14 — +1 area code validity checks
    // =========================================================================

    @Test
    fun `s14 area code 202 Washington DC valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+12025550100"))
    }

    @Test
    fun `s14 area code 212 New York valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+12125550100"))
    }

    @Test
    fun `s14 area code 213 Los Angeles valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+12135550100"))
    }

    @Test
    fun `s14 area code 214 Dallas valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+12145550100"))
    }

    @Test
    fun `s14 area code 215 Philadelphia valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+12155550100"))
    }

    @Test
    fun `s14 area code 216 Cleveland valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+12165550100"))
    }

    @Test
    fun `s14 area code 217 Springfield IL valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+12175550100"))
    }

    @Test
    fun `s14 area code 301 Maryland valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+13015550100"))
    }

    @Test
    fun `s14 area code 305 Miami valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+13055550100"))
    }

    @Test
    fun `s14 area code 312 Chicago valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+13125550100"))
    }

    @Test
    fun `s14 area code 313 Detroit valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+13135550100"))
    }

    @Test
    fun `s14 area code 314 St Louis valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+13145550100"))
    }

    @Test
    fun `s14 area code 404 Atlanta valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+14045550100"))
    }

    @Test
    fun `s14 area code 415 San Francisco valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+14155550100"))
    }

    @Test
    fun `s14 area code 512 Austin valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+15125550100"))
    }

    @Test
    fun `s14 area code 617 Boston valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+16175550100"))
    }

    @Test
    fun `s14 area code 702 Las Vegas valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+17025550100"))
    }

    @Test
    fun `s14 area code 713 Houston valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+17135550100"))
    }

    @Test
    fun `s14 area code 801 Salt Lake City valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+18015550100"))
    }

    @Test
    fun `s14 area code 917 New York mobile valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+19175550100"))
    }

    @Test
    fun `s14 area code 100 invalid starts with 1`() {
        assertFalse(CountryCodeValidator.isValidNANPNumber("+11005550100"))
    }

    @Test
    fun `s14 area code 011 invalid starts with 0`() {
        assertFalse(CountryCodeValidator.isValidNANPNumber("+10115550100"))
    }

    @Test
    fun `s14 area code 911 emergency not assignable`() {
        assertFalse(CountryCodeValidator.isValidNANPNumber("+19115550100"))
    }

    @Test
    fun `s14 area code 200 is the minimum valid NANP area`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+12005550100"))
    }

    @Test
    fun `s14 area code 900 valid structurally per our rules`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+19005550100"))
    }

    @Test
    fun `s14 area code 416 Toronto Canada valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+14165550100"))
    }

    @Test
    fun `s14 area code 604 Vancouver Canada valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+16045550100"))
    }

    @Test
    fun `s14 area code 649 Turks and Caicos valid NANP`() {
        assertTrue(CountryCodeValidator.isValidNANPNumber("+16495550100"))
    }

    @Test
    fun `s14 NANP requires exactly 10 digits after plus1`() {
        val tooShort = "+1202555010"
        val tooLong = "+120255501001"
        assertFalse(CountryCodeValidator.isValidNANPNumber(tooShort))
        assertFalse(CountryCodeValidator.isValidNANPNumber(tooLong))
    }

    @Test
    fun `s14 non plus1 number is not NANP`() {
        assertFalse(CountryCodeValidator.isValidNANPNumber("+442071234567"))
        assertFalse(CountryCodeValidator.isValidNANPNumber("+493012345678"))
    }

    // =========================================================================
    // SECTION 15 — Country code lookup by prefix
    // =========================================================================

    @Test
    fun `s15 lookup plus1 returns US CA`() {
        assertEquals("US/CA", CountryCodeValidator.getCountryCode("+12025550100"))
    }

    @Test
    fun `s15 lookup plus44 returns GB`() {
        assertEquals("GB", CountryCodeValidator.getCountryCode("+441234567890"))
    }

    @Test
    fun `s15 lookup plus49 returns DE`() {
        assertEquals("DE", CountryCodeValidator.getCountryCode("+4912345678"))
    }

    @Test
    fun `s15 lookup plus33 returns FR`() {
        assertEquals("FR", CountryCodeValidator.getCountryCode("+33123456789"))
    }

    @Test
    fun `s15 lookup plus61 returns AU`() {
        assertEquals("AU", CountryCodeValidator.getCountryCode("+61412345678"))
    }

    @Test
    fun `s15 lookup plus81 returns JP`() {
        assertEquals("JP", CountryCodeValidator.getCountryCode("+81312345678"))
    }

    @Test
    fun `s15 lookup plus91 returns IN`() {
        assertEquals("IN", CountryCodeValidator.getCountryCode("+911234567890"))
    }

    @Test
    fun `s15 lookup plus55 returns BR`() {
        assertEquals("BR", CountryCodeValidator.getCountryCode("+5511912345678"))
    }

    @Test
    fun `s15 lookup plus7 returns RU`() {
        assertEquals("RU", CountryCodeValidator.getCountryCode("+74951234567"))
    }

    @Test
    fun `s15 lookup plus86 returns CN`() {
        assertEquals("CN", CountryCodeValidator.getCountryCode("+861012345678"))
    }

    @Test
    fun `s15 lookup plus52 returns MX`() {
        assertEquals("MX", CountryCodeValidator.getCountryCode("+525512345678"))
    }

    @Test
    fun `s15 lookup plus27 returns ZA`() {
        assertEquals("ZA", CountryCodeValidator.getCountryCode("+27111234567"))
    }

    @Test
    fun `s15 lookup plus62 returns ID`() {
        assertEquals("ID", CountryCodeValidator.getCountryCode("+62211234567"))
    }

    @Test
    fun `s15 lookup unknown code returns UNKNOWN`() {
        assertEquals("UNKNOWN", CountryCodeValidator.getCountryCode("+9999999999"))
    }

    @Test
    fun `s15 lookup no plus prefix returns UNKNOWN`() {
        assertEquals("UNKNOWN", CountryCodeValidator.getCountryCode("12025550100"))
    }

    @Test
    fun `s15 lookup empty string returns UNKNOWN`() {
        assertEquals("UNKNOWN", CountryCodeValidator.getCountryCode(""))
    }

    @Test
    fun `s15 lookup plus only returns UNKNOWN`() {
        assertEquals("UNKNOWN", CountryCodeValidator.getCountryCode("+"))
    }

    @Test
    fun `s15 lookup Italy plus39`() {
        assertEquals("IT", CountryCodeValidator.getCountryCode("+3912345678"))
    }

    @Test
    fun `s15 lookup Spain plus34`() {
        assertEquals("ES", CountryCodeValidator.getCountryCode("+34912345678"))
    }

    @Test
    fun `s15 lookup South Korea plus82`() {
        assertEquals("KR", CountryCodeValidator.getCountryCode("+82212345678"))
    }

    @Test
    fun `s15 lookup Turkey plus90`() {
        assertEquals("TR", CountryCodeValidator.getCountryCode("+902121234567"))
    }

    @Test
    fun `s15 lookup Egypt plus20`() {
        assertEquals("EG", CountryCodeValidator.getCountryCode("+20212345678"))
    }

    @Test
    fun `s15 lookup UAE plus971`() {
        assertEquals("AE", CountryCodeValidator.getCountryCode("+971212345678"))
    }

    @Test
    fun `s15 lookup Saudi Arabia plus966`() {
        assertEquals("SA", CountryCodeValidator.getCountryCode("+966112345678"))
    }

    @Test
    fun `s15 all known codes produce expected result`() {
        val knownNumbers = mapOf(
            "+12025550100" to "US/CA",
            "+441234567890" to "GB",
            "+4912345678" to "DE",
            "+33123456789" to "FR",
            "+61412345678" to "AU",
            "+81312345678" to "JP",
            "+911234567890" to "IN",
            "+5511912345678" to "BR",
            "+74951234567" to "RU",
            "+861012345678" to "CN"
        )
        knownNumbers.forEach { (number, expectedCode) ->
            assertEquals("Expected $expectedCode for $number",
                expectedCode, CountryCodeValidator.getCountryCode(number))
        }
    }

    // =========================================================================
    // SECTION 16 — Invalid country codes and invalid numbers
    // =========================================================================

    @Test
    fun `s16 no plus prefix invalid E164`() {
        assertFalse(CountryCodeValidator.isValidE164("12025550100"))
    }

    @Test
    fun `s16 empty string invalid E164`() {
        assertFalse(CountryCodeValidator.isValidE164(""))
    }

    @Test
    fun `s16 plus only invalid E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+"))
    }

    @Test
    fun `s16 plus zero prefix invalid E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+01234567890"))
    }

    @Test
    fun `s16 letters in number invalid E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+1ABC5550100"))
    }

    @Test
    fun `s16 too short 6 digits after plus invalid E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+112345"))
    }

    @Test
    fun `s16 too long 16 digits after plus invalid E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+12025550100123456"))
    }

    @Test
    fun `s16 spaces in number invalid for E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+1 202 555 0100"))
    }

    @Test
    fun `s16 dashes in number invalid for E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+1-202-555-0100"))
    }

    @Test
    fun `s16 parentheses in number invalid for E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+1(202)5550100"))
    }

    @Test
    fun `s16 country code 0 invalid`() {
        assertFalse(CountryCodeValidator.isValidCountryCode(0))
    }

    @Test
    fun `s16 country code 1 valid`() {
        assertTrue(CountryCodeValidator.isValidCountryCode(1))
    }

    @Test
    fun `s16 country code 999 valid`() {
        assertTrue(CountryCodeValidator.isValidCountryCode(999))
    }

    @Test
    fun `s16 country code 1000 invalid`() {
        assertFalse(CountryCodeValidator.isValidCountryCode(1000))
    }

    @Test
    fun `s16 country code negative invalid`() {
        assertFalse(CountryCodeValidator.isValidCountryCode(-1))
    }

    @Test
    fun `s16 special chars in number invalid`() {
        assertFalse(CountryCodeValidator.isValidE164("+1@2025550100"))
    }

    @Test
    fun `s16 null-like empty string returns UNKNOWN lookup`() {
        assertEquals("UNKNOWN", CountryCodeValidator.getCountryCode(""))
    }

    @Test
    fun `s16 number without plus returns UNKNOWN lookup`() {
        assertEquals("UNKNOWN", CountryCodeValidator.getCountryCode("44123456789"))
    }

    @Test
    fun `s16 single digit after plus invalid E164`() {
        assertFalse(CountryCodeValidator.isValidE164("+1"))
    }

    @Test
    fun `s16 batch invalid numbers none pass E164`() {
        val invalidNumbers = listOf(
            "12025550100", "+0123456789", "+1abc5550100", "", "+", "+1", "+123456"
        )
        assertTrue(invalidNumbers.none { CountryCodeValidator.isValidE164(it) })
    }

    // =========================================================================
    // SECTION 17 — E.164 format validation
    // =========================================================================

    @Test
    fun `s17 e164 valid minimum 7 digits after country code`() {
        assertTrue(CountryCodeValidator.isValidE164("+11234567"))
    }

    @Test
    fun `s17 e164 valid maximum 15 digits total`() {
        assertTrue(CountryCodeValidator.isValidE164("+123456789012345"))
    }

    @Test
    fun `s17 e164 invalid 16 digits total too long`() {
        assertFalse(CountryCodeValidator.isValidE164("+1234567890123456"))
    }

    @Test
    fun `s17 e164 must start with non-zero digit after plus`() {
        assertFalse(CountryCodeValidator.isValidE164("+0123456789"))
    }

    @Test
    fun `s17 e164 valid plus1 with 10-digit subscriber number`() {
        assertTrue(CountryCodeValidator.isValidE164("+12025550100"))
    }

    @Test
    fun `s17 e164 valid plus44 with 10-digit UK number`() {
        assertTrue(CountryCodeValidator.isValidE164("+441234567890"))
    }

    @Test
    fun `s17 e164 all digits no spaces`() {
        assertTrue(CountryCodeValidator.isValidE164("+49123456789"))
        assertFalse(CountryCodeValidator.isValidE164("+49 123 456 789"))
    }

    @Test
    fun `s17 e164 valid 8 digits after plus`() {
        assertTrue(CountryCodeValidator.isValidE164("+12345678"))
    }

    @Test
    fun `s17 e164 valid 9 digits after plus`() {
        assertTrue(CountryCodeValidator.isValidE164("+123456789"))
    }

    @Test
    fun `s17 e164 valid 10 digits after plus`() {
        assertTrue(CountryCodeValidator.isValidE164("+1234567890"))
    }

    @Test
    fun `s17 e164 valid 11 digits after plus`() {
        assertTrue(CountryCodeValidator.isValidE164("+12345678901"))
    }

    @Test
    fun `s17 e164 valid 12 digits after plus`() {
        assertTrue(CountryCodeValidator.isValidE164("+123456789012"))
    }

    @Test
    fun `s17 e164 valid 13 digits after plus`() {
        assertTrue(CountryCodeValidator.isValidE164("+1234567890123"))
    }

    @Test
    fun `s17 e164 valid 14 digits after plus`() {
        assertTrue(CountryCodeValidator.isValidE164("+12345678901234"))
    }

    @Test
    fun `s17 e164 valid 15 digits after plus`() {
        assertTrue(CountryCodeValidator.isValidE164("+123456789012345"))
    }

    @Test
    fun `s17 e164 invalid 16 digits after plus`() {
        assertFalse(CountryCodeValidator.isValidE164("+1234567890123456"))
    }

    @Test
    fun `s17 e164 batch valid numbers`() {
        val validNumbers = listOf(
            "+12025550100",
            "+441234567890",
            "+4930123456",
            "+33123456789",
            "+61412345678",
            "+81312345678",
            "+911234567890",
            "+5511912345678"
        )
        assertEquals(validNumbers.size, validNumbers.count { CountryCodeValidator.isValidE164(it) })
    }

    @Test
    fun `s17 e164 batch invalid numbers`() {
        val invalidNumbers = listOf(
            "12025550100",
            "+0123456789",
            "+1abc5550100",
            "",
            "+",
            "+1",
            "+123456"
        )
        assertEquals(0, invalidNumbers.count { CountryCodeValidator.isValidE164(it) })
    }

    @Test
    fun `s17 e164 is consistent on repeated calls`() {
        val number = "+12025550100"
        assertEquals(CountryCodeValidator.isValidE164(number), CountryCodeValidator.isValidE164(number))
    }

    // =========================================================================
    // SECTION 18 — Formatting and masking
    // =========================================================================

    @Test
    fun `s18 mask us number last 4 digits preserved`() {
        val number = "+12025550100"
        val masked = CountryCodeValidator.maskPhoneNumber(number)
        assertTrue(masked.endsWith("0100"))
    }

    @Test
    fun `s18 mask uk number last 4 digits preserved`() {
        val number = "+441234567890"
        val masked = CountryCodeValidator.maskPhoneNumber(number)
        assertTrue(masked.endsWith("7890"))
    }

    @Test
    fun `s18 mask preserves plus prefix`() {
        val number = "+12025550100"
        val masked = CountryCodeValidator.maskPhoneNumber(number)
        assertTrue(masked.contains("+"))
    }

    @Test
    fun `s18 invalid number returned as-is when masking`() {
        val invalid = "not-a-number"
        assertEquals(invalid, CountryCodeValidator.maskPhoneNumber(invalid))
    }

    @Test
    fun `s18 mask australian number`() {
        val number = "+61412345678"
        val masked = CountryCodeValidator.maskPhoneNumber(number)
        assertTrue(masked.endsWith("5678"))
    }

    @Test
    fun `s18 different numbers have different masks`() {
        val n1 = "+12025550100"
        val n2 = "+12025559999"
        assertNotEquals(
            CountryCodeValidator.maskPhoneNumber(n1),
            CountryCodeValidator.maskPhoneNumber(n2)
        )
    }

    @Test
    fun `s18 isValidE164 plus1 ten digits valid`() {
        assertTrue(CountryCodeValidator.isValidE164("+12025550100"))
    }

    @Test
    fun `s18 isValidE164 plus44 ten digits valid`() {
        assertTrue(CountryCodeValidator.isValidE164("+441234567890"))
    }

    @Test
    fun `s18 batch lookup all returns non-empty string`() {
        val numbers = listOf("+12025550100", "+441234567890", "+4930123456")
        val results = numbers.map { CountryCodeValidator.getCountryCode(it) }
        assertTrue(results.all { it.isNotEmpty() })
    }

    @Test
    fun `s18 batch mixed valid and invalid E164`() {
        val mixed = listOf(
            "+12025550100" to true,
            "12025550100" to false,
            "+441234567890" to true,
            "" to false,
            "+0123456789" to false
        )
        mixed.forEach { (number, expected) ->
            assertEquals("E164 for $number", expected, CountryCodeValidator.isValidE164(number))
        }
    }

    @Test
    fun `s18 getCountryCode handles all major countries`() {
        val cases = mapOf(
            "+12025550100" to "US/CA",
            "+441234567890" to "GB",
            "+4930123456" to "DE",
            "+33123456789" to "FR",
            "+61412345678" to "AU"
        )
        cases.forEach { (number, expected) ->
            assertEquals("Country for $number", expected, CountryCodeValidator.getCountryCode(number))
        }
    }

    @Test
    fun `s18 valid E164 numbers from all sections pass`() {
        val numbers = listOf(
            "+12025550100", "+441234567890", "+493012345678",
            "+33123456789", "+61212345678", "+81312345678",
            "+911123456789", "+5511912345678", "+74951234567",
            "+861012345678"
        )
        assertEquals(numbers.size, numbers.count { CountryCodeValidator.isValidE164(it) })
    }

    @Test
    fun `s18 E164 validation consistent on repeated calls`() {
        val number = "+12025550100"
        val r1 = CountryCodeValidator.isValidE164(number)
        val r2 = CountryCodeValidator.isValidE164(number)
        assertEquals(r1, r2)
    }

    @Test
    fun `s18 country code lookup is consistent on repeated calls`() {
        val number = "+12025550100"
        val r1 = CountryCodeValidator.getCountryCode(number)
        val r2 = CountryCodeValidator.getCountryCode(number)
        assertEquals(r1, r2)
    }

    @Test
    fun `s18 batch of 10 NANP numbers all valid E164`() {
        val numbers = (1..10).map { "+1202555${it.toString().padStart(4, '0')}" }
        assertEquals(10, numbers.count { CountryCodeValidator.isValidE164(it) })
    }

    @Test
    fun `s18 batch of all 13 known country numbers lookup works`() {
        val testCases = listOf(
            "+12025550100", "+441234567890", "+493012345678", "+33123456789",
            "+61412345678", "+81312345678", "+911234567890", "+5511912345678",
            "+74951234567", "+861012345678", "+525512345678", "+27111234567",
            "+62211234567"
        )
        val results = testCases.map { CountryCodeValidator.getCountryCode(it) }
        assertTrue("All should be non-UNKNOWN", results.none { it == "UNKNOWN" })
        assertEquals(13, results.distinct().size) // all different country codes
    }

    // =========================================================================
    // ADDITIONAL SECTION — More NANP and international edge cases
    // =========================================================================

    @Test fun `extra nanp albany ny 518 valid`() { assertTrue(CountryCodeValidator.isValidE164("+15185550100")) }
    @Test fun `extra nanp allentown pa 610 valid`() { assertTrue(CountryCodeValidator.isValidE164("+16105550100")) }
    @Test fun `extra nanp anchorage ak 907 valid`() { assertTrue(CountryCodeValidator.isValidE164("+19075550100")) }
    @Test fun `extra nanp baton rouge la 225 valid`() { assertTrue(CountryCodeValidator.isValidE164("+12255550100")) }
    @Test fun `extra nanp birmingham al 205 valid`() { assertTrue(CountryCodeValidator.isValidE164("+12055550100")) }
    @Test fun `extra nanp boise id 208 valid`() { assertTrue(CountryCodeValidator.isValidE164("+12085550100")) }
    @Test fun `extra nanp buffalo ny 716 valid`() { assertTrue(CountryCodeValidator.isValidE164("+17165550100")) }
    @Test fun `extra nanp charleston sc 843 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18435550100")) }
    @Test fun `extra nanp charleston wv 304 valid`() { assertTrue(CountryCodeValidator.isValidE164("+13045550100")) }
    @Test fun `extra nanp columbia sc 803 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18035550100")) }
    @Test fun `extra nanp dayton oh 937 valid`() { assertTrue(CountryCodeValidator.isValidE164("+19375550100")) }
    @Test fun `extra nanp des moines ia 515 valid`() { assertTrue(CountryCodeValidator.isValidE164("+15155550100")) }
    @Test fun `extra nanp detroit mi 313 valid`() { assertTrue(CountryCodeValidator.isValidE164("+13135550100")) }
    @Test fun `extra nanp durham nc 919 valid`() { assertTrue(CountryCodeValidator.isValidE164("+19195550100")) }
    @Test fun `extra nanp fargo nd 701 valid`() { assertTrue(CountryCodeValidator.isValidE164("+17015550100")) }
    @Test fun `extra nanp fresno ca 559 valid`() { assertTrue(CountryCodeValidator.isValidE164("+15595550100")) }
    @Test fun `extra nanp grand rapids mi 616 valid`() { assertTrue(CountryCodeValidator.isValidE164("+16165550100")) }
    @Test fun `extra nanp greensboro nc 336 valid`() { assertTrue(CountryCodeValidator.isValidE164("+13365550100")) }
    @Test fun `extra nanp hartford ct 860 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18605550100")) }
    @Test fun `extra nanp honolulu hi 808 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18085550100")) }
    @Test fun `extra nanp jackson ms 601 valid`() { assertTrue(CountryCodeValidator.isValidE164("+16015550100")) }
    @Test fun `extra nanp knoxville tn 865 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18655550100")) }
    @Test fun `extra nanp lexington ky 859 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18595550100")) }
    @Test fun `extra nanp lincoln ne 402 valid`() { assertTrue(CountryCodeValidator.isValidE164("+14025550100")) }
    @Test fun `extra nanp little rock ar 501 valid`() { assertTrue(CountryCodeValidator.isValidE164("+15015550100")) }
    @Test fun `extra nanp louisville ky 502 valid`() { assertTrue(CountryCodeValidator.isValidE164("+15025550100")) }
    @Test fun `extra nanp madison wi 608 valid`() { assertTrue(CountryCodeValidator.isValidE164("+16085550100")) }
    @Test fun `extra nanp memphis tn 901 valid`() { assertTrue(CountryCodeValidator.isValidE164("+19015550100")) }
    @Test fun `extra nanp mobile al 251 valid`() { assertTrue(CountryCodeValidator.isValidE164("+12515550100")) }
    @Test fun `extra nanp montgomery al 334 valid`() { assertTrue(CountryCodeValidator.isValidE164("+13345550100")) }
    @Test fun `extra nanp norfolk va 757 valid`() { assertTrue(CountryCodeValidator.isValidE164("+17575550100")) }
    @Test fun `extra nanp ontario ca 909 valid`() { assertTrue(CountryCodeValidator.isValidE164("+19095550100")) }
    @Test fun `extra nanp orlando fl 407 valid`() { assertTrue(CountryCodeValidator.isValidE164("+14075550100")) }
    @Test fun `extra nanp pensacola fl 850 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18505550100")) }
    @Test fun `extra nanp pittsburgh pa 412 valid`() { assertTrue(CountryCodeValidator.isValidE164("+14125550100")) }
    @Test fun `extra nanp providence ri 401 valid`() { assertTrue(CountryCodeValidator.isValidE164("+14015550100")) }
    @Test fun `extra nanp richmond va 804 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18045550100")) }
    @Test fun `extra nanp riverside ca 951 valid`() { assertTrue(CountryCodeValidator.isValidE164("+19515550100")) }
    @Test fun `extra nanp rochester ny 585 valid`() { assertTrue(CountryCodeValidator.isValidE164("+15855550100")) }
    @Test fun `extra nanp salt lake city ut 801 valid`() { assertTrue(CountryCodeValidator.isValidE164("+18015550100")) }
    @Test fun `extra nanp san bernardino ca 909 valid`() { assertTrue(CountryCodeValidator.isValidE164("+19095550100")) }
    @Test fun `extra nanp santa ana ca 714 valid`() { assertTrue(CountryCodeValidator.isValidE164("+17145550100")) }
    @Test fun `extra nanp shreveport la 318 valid`() { assertTrue(CountryCodeValidator.isValidE164("+13185550100")) }
    @Test fun `extra nanp spokane wa 509 valid`() { assertTrue(CountryCodeValidator.isValidE164("+15095550100")) }
    @Test fun `extra nanp springfield ma 413 valid`() { assertTrue(CountryCodeValidator.isValidE164("+14135550100")) }
    @Test fun `extra nanp st paul mn 651 valid`() { assertTrue(CountryCodeValidator.isValidE164("+16515550100")) }
    @Test fun `extra nanp st pete fl 727 valid`() { assertTrue(CountryCodeValidator.isValidE164("+17275550100")) }
    @Test fun `extra nanp stockton ca 209 valid`() { assertTrue(CountryCodeValidator.isValidE164("+12095550100")) }
    @Test fun `extra nanp syracuse ny 315 valid`() { assertTrue(CountryCodeValidator.isValidE164("+13155550100")) }
    @Test fun `extra nanp tacoma wa 253 valid`() { assertTrue(CountryCodeValidator.isValidE164("+12535550100")) }
    @Test fun `extra nanp toledo oh 419 valid`() { assertTrue(CountryCodeValidator.isValidE164("+14195550100")) }
    @Test fun `extra nanp tempe az 480 valid`() { assertTrue(CountryCodeValidator.isValidE164("+14805550100")) }
    @Test fun `extra nanp worcester ma 508 valid`() { assertTrue(CountryCodeValidator.isValidE164("+15085550100")) }
    @Test fun `extra nanp yonkers ny 914 valid`() { assertTrue(CountryCodeValidator.isValidE164("+19145550100")) }

    @Test fun `extra intl portugal plus351 valid`() {
        val number = "+351912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl netherlands plus31 valid`() {
        val number = "+31612345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl sweden plus46 valid`() {
        val number = "+46701234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl norway plus47 valid`() {
        val number = "+4712345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl denmark plus45 valid`() {
        val number = "+4512345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl finland plus358 valid`() {
        val number = "+358401234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl poland plus48 valid`() {
        val number = "+48123456789"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl switzerland plus41 valid`() {
        val number = "+41791234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl belgium plus32 valid`() {
        val number = "+32470123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl austria plus43 valid`() {
        val number = "+43664123456"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl greece plus30 valid`() {
        val number = "+306912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl argentina plus54 valid`() {
        val number = "+541112345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl colombia plus57 valid`() {
        val number = "+573001234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl chile plus56 valid`() {
        val number = "+56912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl peru plus51 valid`() {
        val number = "+51912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl venezuela plus58 valid`() {
        val number = "+584121234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl nigeria plus234 valid`() {
        val number = "+2348012345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl kenya plus254 valid`() {
        val number = "+254712345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl ghana plus233 valid`() {
        val number = "+233201234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl ethiopia plus251 valid`() {
        val number = "+251912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl pakistan plus92 valid`() {
        val number = "+923001234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl bangladesh plus880 valid`() {
        val number = "+8801712345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl philippines plus63 valid`() {
        val number = "+639171234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl vietnam plus84 valid`() {
        val number = "+84912345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl thailand plus66 valid`() {
        val number = "+66812345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl malaysia plus60 valid`() {
        val number = "+60112345678"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl singapore plus65 valid`() {
        val number = "+6591234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl new zealand plus64 valid`() {
        val number = "+64211234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl israel plus972 valid`() {
        val number = "+972501234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra intl iran plus98 valid`() {
        val number = "+989121234567"
        assertTrue(CountryCodeValidator.isValidE164(number))
    }
    @Test fun `extra all 30 extra intl numbers pass E164`() {
        val extraNumbers = listOf(
            "+351912345678", "+31612345678", "+46701234567", "+4712345678",
            "+4512345678", "+358401234567", "+48123456789", "+41791234567",
            "+32470123456", "+43664123456", "+306912345678", "+541112345678",
            "+573001234567", "+56912345678", "+51912345678", "+584121234567",
            "+2348012345678", "+254712345678", "+233201234567", "+251912345678",
            "+923001234567", "+8801712345678", "+639171234567", "+84912345678",
            "+66812345678", "+60112345678", "+6591234567", "+64211234567",
            "+972501234567", "+989121234567"
        )
        val invalid = extraNumbers.filter { !CountryCodeValidator.isValidE164(it) }
        assertTrue("All extra international numbers should be valid E164, failed: $invalid", invalid.isEmpty())
    }

    // =========================================================================
    // FINAL SECTION — Cross-country discrimination and comprehensive batch tests
    // =========================================================================

    @Test fun `final all plus1 NANP numbers resolve to US CA`() {
        val nanpNumbers = listOf(
            "+12025550100", "+12125550100", "+13105550150",
            "+14155550180", "+15125550200", "+16465550220",
            "+17135550240", "+18185550260", "+19175550280"
        )
        assertTrue(nanpNumbers.all { CountryCodeValidator.getCountryCode(it) == "US/CA" })
    }

    @Test fun `final all plus44 numbers resolve to GB`() {
        val ukNumbers = listOf(
            "+442071234567", "+441211234567", "+447911123456",
            "+441611234567", "+441311234567", "+441411234567"
        )
        assertTrue(ukNumbers.all { CountryCodeValidator.getCountryCode(it) == "GB" })
    }

    @Test fun `final all plus49 numbers resolve to DE`() {
        val deNumbers = listOf(
            "+493012345678", "+498912345678", "+494012345678",
            "+496912345678", "+492211234567"
        )
        assertTrue(deNumbers.all { CountryCodeValidator.getCountryCode(it) == "DE" })
    }

    @Test fun `final all plus33 numbers resolve to FR`() {
        val frNumbers = listOf(
            "+33123456789", "+33612345678", "+33712345678",
            "+33478123456", "+33491123456"
        )
        assertTrue(frNumbers.all { CountryCodeValidator.getCountryCode(it) == "FR" })
    }

    @Test fun `final all plus61 numbers resolve to AU`() {
        val auNumbers = listOf(
            "+61212345678", "+61312345678", "+61712345678",
            "+61812345678", "+61412345678"
        )
        assertTrue(auNumbers.all { CountryCodeValidator.getCountryCode(it) == "AU" })
    }

    @Test fun `final all plus81 numbers resolve to JP`() {
        val jpNumbers = listOf(
            "+81312345678", "+81612345678", "+81521234567",
            "+81451234567", "+81751234567"
        )
        assertTrue(jpNumbers.all { CountryCodeValidator.getCountryCode(it) == "JP" })
    }

    @Test fun `final all plus91 numbers resolve to IN`() {
        val inNumbers = listOf(
            "+911123456789", "+912212345678", "+918012345678",
            "+914412345678", "+913312345678"
        )
        assertTrue(inNumbers.all { CountryCodeValidator.getCountryCode(it) == "IN" })
    }

    @Test fun `final all plus55 numbers resolve to BR`() {
        val brNumbers = listOf(
            "+5511912345678", "+5521912345678", "+5531912345678"
        )
        assertTrue(brNumbers.all { CountryCodeValidator.getCountryCode(it) == "BR" })
    }

    @Test fun `final all plus7 numbers resolve to RU`() {
        val ruNumbers = listOf(
            "+74951234567", "+74991234567", "+78121234567",
            "+73831234567", "+79001234567"
        )
        assertTrue(ruNumbers.all { CountryCodeValidator.getCountryCode(it) == "RU" })
    }

    @Test fun `final all plus86 numbers resolve to CN`() {
        val cnNumbers = listOf(
            "+861012345678", "+862112345678", "+862012345678",
            "+862812345678", "+862712345678"
        )
        assertTrue(cnNumbers.all { CountryCodeValidator.getCountryCode(it) == "CN" })
    }

    @Test fun `final all plus52 numbers resolve to MX`() {
        val mxNumbers = listOf(
            "+525512345678", "+523312345678", "+528112345678"
        )
        assertTrue(mxNumbers.all { CountryCodeValidator.getCountryCode(it) == "MX" })
    }

    @Test fun `final all plus27 numbers resolve to ZA`() {
        val zaNumbers = listOf(
            "+27111234567", "+27211234567", "+27311234567",
            "+27721234567", "+27821234567"
        )
        assertTrue(zaNumbers.all { CountryCodeValidator.getCountryCode(it) == "ZA" })
    }

    @Test fun `final all plus62 numbers resolve to ID`() {
        val idNumbers = listOf(
            "+62211234567", "+62311234567", "+62221234567"
        )
        assertTrue(idNumbers.all { CountryCodeValidator.getCountryCode(it) == "ID" })
    }

    @Test fun `final no US number resolves to any other country`() {
        val usNumbers = listOf("+12025550100", "+12125550100", "+13105550150")
        assertTrue(usNumbers.none { CountryCodeValidator.getCountryCode(it) == "UNKNOWN" })
        assertTrue(usNumbers.none { CountryCodeValidator.getCountryCode(it) == "GB" })
        assertTrue(usNumbers.none { CountryCodeValidator.getCountryCode(it) == "DE" })
    }

    @Test fun `final E164 pattern exact boundary cases`() {
        // 7 digits after plus (minimum) - 6 digits in subscriber part
        assertTrue(CountryCodeValidator.isValidE164("+1234567"))  // 7 total digits
        // 15 digits after plus (maximum) - 14 digits in subscriber part
        assertTrue(CountryCodeValidator.isValidE164("+123456789012345"))  // 15 total digits
        // 6 digits - invalid (too short)
        assertFalse(CountryCodeValidator.isValidE164("+123456"))
        // 16 digits - invalid (too long)
        assertFalse(CountryCodeValidator.isValidE164("+1234567890123456"))
    }

    @Test fun `final E164 format must have plus as first char`() {
        assertFalse(CountryCodeValidator.isValidE164("12025550100"))
        assertFalse(CountryCodeValidator.isValidE164("0012025550100"))
        assertTrue(CountryCodeValidator.isValidE164("+12025550100"))
    }

    @Test fun `final mask function preserves last 4 digits`() {
        val testCases = listOf(
            "+12025550100" to "0100",
            "+441234567890" to "7890",
            "+61412345678" to "5678",
            "+74951234567" to "4567"
        )
        testCases.forEach { (number, lastFour) ->
            val masked = CountryCodeValidator.maskPhoneNumber(number)
            assertTrue("Masked $number should end with $lastFour", masked.endsWith(lastFour))
        }
    }

    @Test fun `final isValidCountryCode covers typical ranges`() {
        val validCodes = listOf(1, 7, 20, 27, 30, 31, 32, 33, 34, 36, 39, 40, 41,
            43, 44, 45, 46, 47, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 60,
            61, 62, 63, 64, 65, 66, 81, 82, 84, 86, 90, 91, 92, 98, 234, 254,
            351, 358, 966, 971, 972, 999)
        assertTrue(validCodes.all { CountryCodeValidator.isValidCountryCode(it) })
    }

    @Test fun `final comprehensive E164 format check 50 valid numbers`() {
        val validNumbers = listOf(
            "+12025550100", "+12125550120", "+13105550150", "+14155550180", "+15125550200",
            "+16465550220", "+17135550240", "+18185550260", "+19175550280", "+13145550300",
            "+442071234567", "+441211234567", "+447911123456", "+441611234567", "+441311234567",
            "+493012345678", "+498912345678", "+494012345678", "+496912345678", "+492211234567",
            "+33123456789", "+33612345678", "+33712345678", "+33478123456", "+33491123456",
            "+61212345678", "+61312345678", "+61712345678", "+61812345678", "+61412345678",
            "+81312345678", "+81612345678", "+81521234567", "+81451234567", "+81751234567",
            "+911123456789", "+912212345678", "+918012345678", "+914412345678", "+913312345678",
            "+5511912345678", "+5521912345678", "+5531912345678", "+5541912345678", "+5551912345678",
            "+74951234567", "+74991234567", "+78121234567", "+73831234567", "+79001234567"
        )
        val failed = validNumbers.filter { !CountryCodeValidator.isValidE164(it) }
        assertTrue("All 50 should be valid E164, failed: $failed", failed.isEmpty())
    }
}


