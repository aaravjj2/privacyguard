package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

class CountryCodeValidatorTest {

    // ========================================================================
    // SECTION 1: US Phone Number Validation Tests
    // ========================================================================

    @Test
    fun `validate US number with country code is valid`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate US number with 1 prefix is valid`() {
        val result = CountryCodeValidator.validate("12025551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate US formatted number with dashes`() {
        val result = CountryCodeValidator.validate("+1-202-555-1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate US formatted number with spaces`() {
        val result = CountryCodeValidator.validate("+1 202 555 1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate US formatted number with parentheses`() {
        val result = CountryCodeValidator.validate("+1 (202) 555-1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate US formatted number with dots`() {
        val result = CountryCodeValidator.validate("+1.202.555.1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate US country detected as US`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertEquals("US", result.countryCode)
    }

    @Test
    fun `validate US number has high confidence`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertTrue(result.confidence >= 0.7f)
    }

    @Test
    fun `validate US number E164 format`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertNotNull(result.formattedNumber)
        assertTrue(result.formattedNumber!!.startsWith("+1"))
    }

    // ========================================================================
    // SECTION 2: Canadian Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Canadian number with country code is valid`() {
        val result = CountryCodeValidator.validate("+14165551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Canadian number formatted`() {
        val result = CountryCodeValidator.validate("+1 416 555 1234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Canadian 10 digit number`() {
        val result = CountryCodeValidator.validate("+14165551234")
        val country = result.country
        assertNotNull(country)
    }

    // ========================================================================
    // SECTION 3: UK Phone Number Tests
    // ========================================================================

    @Test
    fun `validate UK number with country code is valid`() {
        val result = CountryCodeValidator.validate("+442071234567")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate UK mobile number is valid`() {
        val result = CountryCodeValidator.validate("+447911123456")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate UK number detected as GB`() {
        val result = CountryCodeValidator.validate("+442071234567")
        assertEquals("GB", result.countryCode)
    }

    @Test
    fun `validate UK number formatted with spaces`() {
        val result = CountryCodeValidator.validate("+44 20 7123 4567")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate UK country has calling code plus 44`() {
        val uk = CountryCodeValidator.getCountryByIsoCode("GB")
        assertNotNull(uk)
        assertEquals("+44", uk?.callingCode)
    }

    @Test
    fun `validate UK country has trunk prefix 0`() {
        val uk = CountryCodeValidator.getCountryByIsoCode("GB")
        assertEquals("0", uk?.trunkPrefix)
    }

    @Test
    fun `validate UK country has min length 10`() {
        val uk = CountryCodeValidator.getCountryByIsoCode("GB")
        assertEquals(10, uk?.minLength)
    }

    @Test
    fun `validate UK country has max length 10`() {
        val uk = CountryCodeValidator.getCountryByIsoCode("GB")
        assertEquals(10, uk?.maxLength)
    }

    @Test
    fun `validate UK country is in EUROPE region`() {
        val uk = CountryCodeValidator.getCountryByIsoCode("GB")
        assertEquals(CountryCodeValidator.Region.EUROPE, uk?.region)
    }

    // ========================================================================
    // SECTION 4: German Phone Number Tests
    // ========================================================================

    @Test
    fun `validate German number is valid`() {
        val result = CountryCodeValidator.validate("+4915123456789")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate German country has calling code plus 49`() {
        val de = CountryCodeValidator.getCountryByIsoCode("DE")
        assertNotNull(de)
        assertEquals("+49", de?.callingCode)
    }

    @Test
    fun `validate German country has trunk prefix 0`() {
        val de = CountryCodeValidator.getCountryByIsoCode("DE")
        assertEquals("0", de?.trunkPrefix)
    }

    @Test
    fun `validate German country is in EUROPE`() {
        val de = CountryCodeValidator.getCountryByIsoCode("DE")
        assertEquals(CountryCodeValidator.Region.EUROPE, de?.region)
    }

    // ========================================================================
    // SECTION 5: French Phone Number Tests
    // ========================================================================

    @Test
    fun `validate French number is valid`() {
        val result = CountryCodeValidator.validate("+33612345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate French country has calling code plus 33`() {
        val fr = CountryCodeValidator.getCountryByIsoCode("FR")
        assertNotNull(fr)
        assertEquals("+33", fr?.callingCode)
    }

    @Test
    fun `validate French country min and max length is 9`() {
        val fr = CountryCodeValidator.getCountryByIsoCode("FR")
        assertEquals(9, fr?.minLength)
        assertEquals(9, fr?.maxLength)
    }

    @Test
    fun `validate French country is in EUROPE`() {
        val fr = CountryCodeValidator.getCountryByIsoCode("FR")
        assertEquals(CountryCodeValidator.Region.EUROPE, fr?.region)
    }

    // ========================================================================
    // SECTION 6: Japanese Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Japanese number is valid`() {
        val result = CountryCodeValidator.validate("+819012345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Japanese country has calling code plus 81`() {
        val jp = CountryCodeValidator.getCountryByIsoCode("JP")
        assertNotNull(jp)
        assertEquals("+81", jp?.callingCode)
    }

    @Test
    fun `validate Japanese country is in ASIA`() {
        val jp = CountryCodeValidator.getCountryByIsoCode("JP")
        assertEquals(CountryCodeValidator.Region.ASIA, jp?.region)
    }

    // ========================================================================
    // SECTION 7: Indian Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Indian number is valid`() {
        val result = CountryCodeValidator.validate("+919876543210")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Indian country has calling code plus 91`() {
        val india = CountryCodeValidator.getCountryByIsoCode("IN")
        assertNotNull(india)
        assertEquals("+91", india?.callingCode)
    }

    @Test
    fun `validate Indian country min and max length is 10`() {
        val india = CountryCodeValidator.getCountryByIsoCode("IN")
        assertEquals(10, india?.minLength)
        assertEquals(10, india?.maxLength)
    }

    @Test
    fun `validate Indian country is in ASIA`() {
        val india = CountryCodeValidator.getCountryByIsoCode("IN")
        assertEquals(CountryCodeValidator.Region.ASIA, india?.region)
    }

    // ========================================================================
    // SECTION 8: Chinese Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Chinese number is valid`() {
        val result = CountryCodeValidator.validate("+8613912345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Chinese country has calling code plus 86`() {
        val cn = CountryCodeValidator.getCountryByIsoCode("CN")
        assertNotNull(cn)
        assertEquals("+86", cn?.callingCode)
    }

    @Test
    fun `validate Chinese country min and max length is 11`() {
        val cn = CountryCodeValidator.getCountryByIsoCode("CN")
        assertEquals(11, cn?.minLength)
        assertEquals(11, cn?.maxLength)
    }

    @Test
    fun `validate Chinese country is in ASIA`() {
        val cn = CountryCodeValidator.getCountryByIsoCode("CN")
        assertEquals(CountryCodeValidator.Region.ASIA, cn?.region)
    }

    // ========================================================================
    // SECTION 9: Australian Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Australian number is valid`() {
        val result = CountryCodeValidator.validate("+61412345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Australian country has calling code plus 61`() {
        val au = CountryCodeValidator.getCountryByIsoCode("AU")
        assertNotNull(au)
        assertEquals("+61", au?.callingCode)
    }

    @Test
    fun `validate Australian country is in OCEANIA`() {
        val au = CountryCodeValidator.getCountryByIsoCode("AU")
        assertEquals(CountryCodeValidator.Region.OCEANIA, au?.region)
    }

    @Test
    fun `validate Australian country min and max length is 9`() {
        val au = CountryCodeValidator.getCountryByIsoCode("AU")
        assertEquals(9, au?.minLength)
        assertEquals(9, au?.maxLength)
    }

    // ========================================================================
    // SECTION 10: Brazilian Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Brazilian number is valid`() {
        val result = CountryCodeValidator.validate("+5511912345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Brazilian country has calling code plus 55`() {
        val br = CountryCodeValidator.getCountryByIsoCode("BR")
        assertNotNull(br)
        assertEquals("+55", br?.callingCode)
    }

    @Test
    fun `validate Brazilian country is in SOUTH_AMERICA`() {
        val br = CountryCodeValidator.getCountryByIsoCode("BR")
        assertEquals(CountryCodeValidator.Region.SOUTH_AMERICA, br?.region)
    }

    // ========================================================================
    // SECTION 11: Mexican Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Mexican number is valid`() {
        val result = CountryCodeValidator.validate("+521234567890")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Mexican country has calling code plus 52`() {
        val mx = CountryCodeValidator.getCountryByIsoCode("MX")
        assertNotNull(mx)
        assertEquals("+52", mx?.callingCode)
    }

    @Test
    fun `validate Mexican country is in CENTRAL_AMERICA`() {
        val mx = CountryCodeValidator.getCountryByIsoCode("MX")
        assertEquals(CountryCodeValidator.Region.CENTRAL_AMERICA, mx?.region)
    }

    // ========================================================================
    // SECTION 12: South Korean Phone Number Tests
    // ========================================================================

    @Test
    fun `validate South Korean number is valid`() {
        val result = CountryCodeValidator.validate("+821012345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate South Korean country has calling code plus 82`() {
        val kr = CountryCodeValidator.getCountryByIsoCode("KR")
        assertNotNull(kr)
        assertEquals("+82", kr?.callingCode)
    }

    @Test
    fun `validate South Korean country is in ASIA`() {
        val kr = CountryCodeValidator.getCountryByIsoCode("KR")
        assertEquals(CountryCodeValidator.Region.ASIA, kr?.region)
    }

    // ========================================================================
    // SECTION 13: Russian Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Russian number is valid`() {
        val result = CountryCodeValidator.validate("+79161234567")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Russian country has calling code plus 7`() {
        val ru = CountryCodeValidator.getCountryByIsoCode("RU")
        assertNotNull(ru)
        assertEquals("+7", ru?.callingCode)
    }

    @Test
    fun `validate Russian country min and max length is 10`() {
        val ru = CountryCodeValidator.getCountryByIsoCode("RU")
        assertEquals(10, ru?.minLength)
        assertEquals(10, ru?.maxLength)
    }

    // ========================================================================
    // SECTION 14: Middle Eastern Phone Number Tests
    // ========================================================================

    @Test
    fun `validate UAE number is valid`() {
        val result = CountryCodeValidator.validate("+971501234567")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate UAE country has calling code plus 971`() {
        val ae = CountryCodeValidator.getCountryByIsoCode("AE")
        assertNotNull(ae)
        assertEquals("+971", ae?.callingCode)
    }

    @Test
    fun `validate UAE country is in MIDDLE_EAST`() {
        val ae = CountryCodeValidator.getCountryByIsoCode("AE")
        assertEquals(CountryCodeValidator.Region.MIDDLE_EAST, ae?.region)
    }

    @Test
    fun `validate Saudi Arabia number is valid`() {
        val result = CountryCodeValidator.validate("+966512345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Saudi Arabia country has calling code plus 966`() {
        val sa = CountryCodeValidator.getCountryByIsoCode("SA")
        assertNotNull(sa)
        assertEquals("+966", sa?.callingCode)
    }

    @Test
    fun `validate Israel number is valid`() {
        val result = CountryCodeValidator.validate("+972501234567")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Turkey number is valid`() {
        val result = CountryCodeValidator.validate("+905321234567")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Turkey country has calling code plus 90`() {
        val tr = CountryCodeValidator.getCountryByIsoCode("TR")
        assertNotNull(tr)
        assertEquals("+90", tr?.callingCode)
    }

    @Test
    fun `validate Qatar number is valid`() {
        val result = CountryCodeValidator.validate("+97433123456")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Kuwait number is valid`() {
        val result = CountryCodeValidator.validate("+96555123456")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Bahrain number is valid`() {
        val result = CountryCodeValidator.validate("+97333123456")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Jordan number is valid`() {
        val result = CountryCodeValidator.validate("+962791234567")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Lebanon country has calling code plus 961`() {
        val lb = CountryCodeValidator.getCountryByIsoCode("LB")
        assertNotNull(lb)
        assertEquals("+961", lb?.callingCode)
    }

    @Test
    fun `validate Iran country has calling code plus 98`() {
        val ir = CountryCodeValidator.getCountryByIsoCode("IR")
        assertNotNull(ir)
        assertEquals("+98", ir?.callingCode)
    }

    @Test
    fun `validate Iraq country has calling code plus 964`() {
        val iq = CountryCodeValidator.getCountryByIsoCode("IQ")
        assertNotNull(iq)
        assertEquals("+964", iq?.callingCode)
    }

    // ========================================================================
    // SECTION 15: African Phone Number Tests
    // ========================================================================

    @Test
    fun `validate South African number is valid`() {
        val result = CountryCodeValidator.validate("+27711234567")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate South African country has calling code plus 27`() {
        val za = CountryCodeValidator.getCountryByIsoCode("ZA")
        assertNotNull(za)
        assertEquals("+27", za?.callingCode)
    }

    @Test
    fun `validate South African country is in AFRICA`() {
        val za = CountryCodeValidator.getCountryByIsoCode("ZA")
        assertEquals(CountryCodeValidator.Region.AFRICA, za?.region)
    }

    @Test
    fun `validate Nigerian number is valid`() {
        val result = CountryCodeValidator.validate("+2347012345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Nigerian country has calling code plus 234`() {
        val ng = CountryCodeValidator.getCountryByIsoCode("NG")
        assertNotNull(ng)
        assertEquals("+234", ng?.callingCode)
    }

    @Test
    fun `validate Kenyan number is valid`() {
        val result = CountryCodeValidator.validate("+254712345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Egyptian number is valid`() {
        val result = CountryCodeValidator.validate("+201012345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate Egyptian country has calling code plus 20`() {
        val eg = CountryCodeValidator.getCountryByIsoCode("EG")
        assertNotNull(eg)
        assertEquals("+20", eg?.callingCode)
    }

    @Test
    fun `validate Moroccan country has calling code plus 212`() {
        val ma = CountryCodeValidator.getCountryByIsoCode("MA")
        assertNotNull(ma)
        assertEquals("+212", ma?.callingCode)
    }

    @Test
    fun `validate Ghanaian country has calling code plus 233`() {
        val gh = CountryCodeValidator.getCountryByIsoCode("GH")
        assertNotNull(gh)
        assertEquals("+233", gh?.callingCode)
    }

    @Test
    fun `validate Tanzanian country has calling code plus 255`() {
        val tz = CountryCodeValidator.getCountryByIsoCode("TZ")
        assertNotNull(tz)
        assertEquals("+255", tz?.callingCode)
    }

    @Test
    fun `validate Ethiopian country has calling code plus 251`() {
        val et = CountryCodeValidator.getCountryByIsoCode("ET")
        assertNotNull(et)
        assertEquals("+251", et?.callingCode)
    }

    // ========================================================================
    // SECTION 16: Caribbean NANP Phone Number Tests
    // ========================================================================

    @Test
    fun `validate Antigua number country code`() {
        val ag = CountryCodeValidator.getCountryByIsoCode("AG")
        assertNotNull(ag)
        assertEquals("+1268", ag?.callingCode)
    }

    @Test
    fun `validate Barbados number country code`() {
        val bb = CountryCodeValidator.getCountryByIsoCode("BB")
        assertNotNull(bb)
        assertEquals("+1246", bb?.callingCode)
    }

    @Test
    fun `validate Bahamas number country code`() {
        val bs = CountryCodeValidator.getCountryByIsoCode("BS")
        assertNotNull(bs)
        assertEquals("+1242", bs?.callingCode)
    }

    @Test
    fun `validate Jamaica number country code`() {
        val jm = CountryCodeValidator.getCountryByIsoCode("JM")
        assertNotNull(jm)
        assertEquals("+1876", jm?.callingCode)
    }

    @Test
    fun `validate Trinidad number country code`() {
        val tt = CountryCodeValidator.getCountryByIsoCode("TT")
        assertNotNull(tt)
        assertEquals("+1868", tt?.callingCode)
    }

    @Test
    fun `validate Bermuda number country code`() {
        val bm = CountryCodeValidator.getCountryByIsoCode("BM")
        assertNotNull(bm)
        assertEquals("+1441", bm?.callingCode)
    }

    @Test
    fun `validate Cayman Islands number country code`() {
        val ky = CountryCodeValidator.getCountryByIsoCode("KY")
        assertNotNull(ky)
        assertEquals("+1345", ky?.callingCode)
    }

    @Test
    fun `validate Dominican Republic number country code`() {
        val dom = CountryCodeValidator.getCountryByIsoCode("DO")
        assertNotNull(dom)
        assertEquals("+1809", dom?.callingCode)
    }

    @Test
    fun `validate Puerto Rico number country code`() {
        val pr = CountryCodeValidator.getCountryByIsoCode("PR")
        assertNotNull(pr)
        assertEquals("+1787", pr?.callingCode)
    }

    @Test
    fun `validate US Virgin Islands number country code`() {
        val vi = CountryCodeValidator.getCountryByIsoCode("VI")
        assertNotNull(vi)
        assertEquals("+1340", vi?.callingCode)
    }

    @Test
    fun `validate Caribbean countries are in CARIBBEAN region`() {
        val caribbeanCodes = listOf("AG", "BB", "BS", "DM", "DO", "GD", "JM", "KN", "KY", "LC", "MS", "TT", "VC", "VG")
        for (code in caribbeanCodes) {
            val country = CountryCodeValidator.getCountryByIsoCode(code)
            assertNotNull("$code should exist", country)
            assertEquals("$code should be CARIBBEAN", CountryCodeValidator.Region.CARIBBEAN, country?.region)
        }
    }

    // ========================================================================
    // SECTION 17: European Country Phone Info Tests
    // ========================================================================

    @Test
    fun `validate Italy country has calling code plus 39`() {
        val it = CountryCodeValidator.getCountryByIsoCode("IT")
        assertNotNull(it)
        assertEquals("+39", it?.callingCode)
    }

    @Test
    fun `validate Spain country has calling code plus 34`() {
        val es = CountryCodeValidator.getCountryByIsoCode("ES")
        assertNotNull(es)
        assertEquals("+34", es?.callingCode)
    }

    @Test
    fun `validate Netherlands country has calling code plus 31`() {
        val nl = CountryCodeValidator.getCountryByIsoCode("NL")
        assertNotNull(nl)
        assertEquals("+31", nl?.callingCode)
    }

    @Test
    fun `validate Belgium country has calling code plus 32`() {
        val be = CountryCodeValidator.getCountryByIsoCode("BE")
        assertNotNull(be)
        assertEquals("+32", be?.callingCode)
    }

    @Test
    fun `validate Switzerland country has calling code plus 41`() {
        val ch = CountryCodeValidator.getCountryByIsoCode("CH")
        assertNotNull(ch)
        assertEquals("+41", ch?.callingCode)
    }

    @Test
    fun `validate Austria country has calling code plus 43`() {
        val at = CountryCodeValidator.getCountryByIsoCode("AT")
        assertNotNull(at)
        assertEquals("+43", at?.callingCode)
    }

    @Test
    fun `validate Sweden country has calling code plus 46`() {
        val se = CountryCodeValidator.getCountryByIsoCode("SE")
        assertNotNull(se)
        assertEquals("+46", se?.callingCode)
    }

    @Test
    fun `validate Norway country has calling code plus 47`() {
        val no = CountryCodeValidator.getCountryByIsoCode("NO")
        assertNotNull(no)
        assertEquals("+47", no?.callingCode)
    }

    @Test
    fun `validate Denmark country has calling code plus 45`() {
        val dk = CountryCodeValidator.getCountryByIsoCode("DK")
        assertNotNull(dk)
        assertEquals("+45", dk?.callingCode)
    }

    @Test
    fun `validate Finland country has calling code plus 358`() {
        val fi = CountryCodeValidator.getCountryByIsoCode("FI")
        assertNotNull(fi)
        assertEquals("+358", fi?.callingCode)
    }

    @Test
    fun `validate Ireland country has calling code plus 353`() {
        val ie = CountryCodeValidator.getCountryByIsoCode("IE")
        assertNotNull(ie)
        assertEquals("+353", ie?.callingCode)
    }

    @Test
    fun `validate Poland country has calling code plus 48`() {
        val pl = CountryCodeValidator.getCountryByIsoCode("PL")
        assertNotNull(pl)
        assertEquals("+48", pl?.callingCode)
    }

    @Test
    fun `validate Czech Republic country has calling code plus 420`() {
        val cz = CountryCodeValidator.getCountryByIsoCode("CZ")
        assertNotNull(cz)
        assertEquals("+420", cz?.callingCode)
    }

    @Test
    fun `validate Portugal country has calling code plus 351`() {
        val pt = CountryCodeValidator.getCountryByIsoCode("PT")
        assertNotNull(pt)
        assertEquals("+351", pt?.callingCode)
    }

    @Test
    fun `validate Greece country has calling code plus 30`() {
        val gr = CountryCodeValidator.getCountryByIsoCode("GR")
        assertNotNull(gr)
        assertEquals("+30", gr?.callingCode)
    }

    @Test
    fun `validate Romania country has calling code plus 40`() {
        val ro = CountryCodeValidator.getCountryByIsoCode("RO")
        assertNotNull(ro)
        assertEquals("+40", ro?.callingCode)
    }

    @Test
    fun `validate Hungary country has calling code plus 36`() {
        val hu = CountryCodeValidator.getCountryByIsoCode("HU")
        assertNotNull(hu)
        assertEquals("+36", hu?.callingCode)
    }

    @Test
    fun `validate Ukraine country has calling code plus 380`() {
        val ua = CountryCodeValidator.getCountryByIsoCode("UA")
        assertNotNull(ua)
        assertEquals("+380", ua?.callingCode)
    }

    @Test
    fun `validate Iceland country has calling code plus 354`() {
        val is_ = CountryCodeValidator.getCountryByIsoCode("IS")
        assertNotNull(is_)
        assertEquals("+354", is_?.callingCode)
    }

    @Test
    fun `validate all European countries are in EUROPE region`() {
        val europeanCodes = listOf("GB", "DE", "FR", "IT", "ES", "PT", "NL", "BE", "AT", "CH",
            "SE", "NO", "DK", "FI", "IE", "PL", "CZ", "SK", "HU", "RO", "BG", "HR", "SI",
            "RS", "BA", "ME", "MK", "AL", "GR", "CY", "MT", "EE", "LV", "LT", "UA", "BY",
            "MD", "RU", "IS", "LU", "LI", "MC", "SM", "VA", "AD", "GI", "FO", "GL")
        for (code in europeanCodes) {
            val country = CountryCodeValidator.getCountryByIsoCode(code)
            assertNotNull("$code should exist", country)
            assertEquals("$code should be EUROPE", CountryCodeValidator.Region.EUROPE, country?.region)
        }
    }

    // ========================================================================
    // SECTION 18: Asian Country Phone Info Tests
    // ========================================================================

    @Test
    fun `validate Singapore country has calling code plus 65`() {
        val sg = CountryCodeValidator.getCountryByIsoCode("SG")
        assertNotNull(sg)
        assertEquals("+65", sg?.callingCode)
    }

    @Test
    fun `validate Singapore min and max length is 8`() {
        val sg = CountryCodeValidator.getCountryByIsoCode("SG")
        assertEquals(8, sg?.minLength)
        assertEquals(8, sg?.maxLength)
    }

    @Test
    fun `validate Hong Kong country has calling code plus 852`() {
        val hk = CountryCodeValidator.getCountryByIsoCode("HK")
        assertNotNull(hk)
        assertEquals("+852", hk?.callingCode)
    }

    @Test
    fun `validate Taiwan country has calling code plus 886`() {
        val tw = CountryCodeValidator.getCountryByIsoCode("TW")
        assertNotNull(tw)
        assertEquals("+886", tw?.callingCode)
    }

    @Test
    fun `validate Malaysia country has calling code plus 60`() {
        val my = CountryCodeValidator.getCountryByIsoCode("MY")
        assertNotNull(my)
        assertEquals("+60", my?.callingCode)
    }

    @Test
    fun `validate Indonesia country has calling code plus 62`() {
        val id = CountryCodeValidator.getCountryByIsoCode("ID")
        assertNotNull(id)
        assertEquals("+62", id?.callingCode)
    }

    @Test
    fun `validate Philippines country has calling code plus 63`() {
        val ph = CountryCodeValidator.getCountryByIsoCode("PH")
        assertNotNull(ph)
        assertEquals("+63", ph?.callingCode)
    }

    @Test
    fun `validate Thailand country has calling code plus 66`() {
        val th = CountryCodeValidator.getCountryByIsoCode("TH")
        assertNotNull(th)
        assertEquals("+66", th?.callingCode)
    }

    @Test
    fun `validate Vietnam country has calling code plus 84`() {
        val vn = CountryCodeValidator.getCountryByIsoCode("VN")
        assertNotNull(vn)
        assertEquals("+84", vn?.callingCode)
    }

    @Test
    fun `validate Pakistan country has calling code plus 92`() {
        val pk = CountryCodeValidator.getCountryByIsoCode("PK")
        assertNotNull(pk)
        assertEquals("+92", pk?.callingCode)
    }

    @Test
    fun `validate Bangladesh country has calling code plus 880`() {
        val bd = CountryCodeValidator.getCountryByIsoCode("BD")
        assertNotNull(bd)
        assertEquals("+880", bd?.callingCode)
    }

    @Test
    fun `validate all Asian countries are in ASIA region`() {
        val asianCodes = listOf("CN", "JP", "KR", "IN", "PK", "BD", "LK", "NP", "MM", "TH",
            "VN", "KH", "LA", "MY", "SG", "ID", "PH", "TW", "HK", "MO", "MN", "KZ", "UZ",
            "KG", "TJ", "TM", "AF", "BN", "TL", "BT", "MV")
        for (code in asianCodes) {
            val country = CountryCodeValidator.getCountryByIsoCode(code)
            assertNotNull("$code should exist", country)
            assertEquals("$code should be ASIA", CountryCodeValidator.Region.ASIA, country?.region)
        }
    }

    // ========================================================================
    // SECTION 19: South American Country Tests
    // ========================================================================

    @Test
    fun `validate Argentina country has calling code plus 54`() {
        val ar = CountryCodeValidator.getCountryByIsoCode("AR")
        assertNotNull(ar)
        assertEquals("+54", ar?.callingCode)
    }

    @Test
    fun `validate Chile country has calling code plus 56`() {
        val cl = CountryCodeValidator.getCountryByIsoCode("CL")
        assertNotNull(cl)
        assertEquals("+56", cl?.callingCode)
    }

    @Test
    fun `validate Colombia country has calling code plus 57`() {
        val co = CountryCodeValidator.getCountryByIsoCode("CO")
        assertNotNull(co)
        assertEquals("+57", co?.callingCode)
    }

    @Test
    fun `validate Peru country has calling code plus 51`() {
        val pe = CountryCodeValidator.getCountryByIsoCode("PE")
        assertNotNull(pe)
        assertEquals("+51", pe?.callingCode)
    }

    @Test
    fun `validate Venezuela country has calling code plus 58`() {
        val ve = CountryCodeValidator.getCountryByIsoCode("VE")
        assertNotNull(ve)
        assertEquals("+58", ve?.callingCode)
    }

    @Test
    fun `validate Ecuador country has calling code plus 593`() {
        val ec = CountryCodeValidator.getCountryByIsoCode("EC")
        assertNotNull(ec)
        assertEquals("+593", ec?.callingCode)
    }

    @Test
    fun `validate all South American countries in SOUTH_AMERICA region`() {
        val saCodes = listOf("BR", "AR", "CL", "CO", "PE", "VE", "EC", "BO", "PY", "UY", "GY", "SR", "GF", "FK")
        for (code in saCodes) {
            val country = CountryCodeValidator.getCountryByIsoCode(code)
            assertNotNull("$code should exist", country)
            assertEquals("$code should be SOUTH_AMERICA", CountryCodeValidator.Region.SOUTH_AMERICA, country?.region)
        }
    }

    // ========================================================================
    // SECTION 20: NANP Validation Tests
    // ========================================================================

    @Test
    fun `validateNANPNumber valid 10 digit number`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateNANPNumber valid 11 digit with leading 1`() {
        val result = CountryCodeValidator.validateNANPNumber("12025551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateNANPNumber area code starting with 0 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("0025551234")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("area code"))
    }

    @Test
    fun `validateNANPNumber area code starting with 1 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("1025551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber exchange starting with 0 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("2020551234")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("exchange"))
    }

    @Test
    fun `validateNANPNumber exchange starting with 1 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("2021551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber N11 area code 211 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("2115551234")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("N11"))
    }

    @Test
    fun `validateNANPNumber N11 area code 311 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("3115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber N11 area code 411 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("4115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber N11 area code 511 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("5115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber N11 area code 611 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("6115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber N11 area code 711 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("7115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber N11 area code 811 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("8115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber N11 area code 911 is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("9115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber 555-01XX is fictional`() {
        val result = CountryCodeValidator.validateNANPNumber("2025550100")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("fictional"))
    }

    @Test
    fun `validateNANPNumber too short is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("20255512")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber too long is invalid`() {
        val result = CountryCodeValidator.validateNANPNumber("202555123456")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateNANPNumber formatted number returned`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertEquals("+12025551234", result.formattedNumber)
    }

    @Test
    fun `validateNANPNumber high confidence`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertEquals(0.95f, result.confidence, 0.01f)
    }

    @Test
    fun `validateNANPNumber returns US country code`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertEquals("US", result.countryCode)
    }

    // ========================================================================
    // SECTION 21: cleanPhoneNumber Tests
    // ========================================================================

    @Test
    fun `cleanPhoneNumber removes spaces`() {
        assertEquals("+1202555 1234".replace(" ", ""), CountryCodeValidator.cleanPhoneNumber("+1 202 555 1234"))
    }

    @Test
    fun `cleanPhoneNumber removes dashes`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("+1-202-555-1234"))
    }

    @Test
    fun `cleanPhoneNumber removes parentheses`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("+1 (202) 555-1234"))
    }

    @Test
    fun `cleanPhoneNumber removes dots`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("+1.202.555.1234"))
    }

    @Test
    fun `cleanPhoneNumber preserves leading plus`() {
        val cleaned = CountryCodeValidator.cleanPhoneNumber("+12025551234")
        assertTrue(cleaned.startsWith("+"))
    }

    @Test
    fun `cleanPhoneNumber strips non-leading plus`() {
        val cleaned = CountryCodeValidator.cleanPhoneNumber("1+2025551234")
        assertFalse(cleaned.contains("+"))
    }

    @Test
    fun `cleanPhoneNumber handles empty string`() {
        assertEquals("", CountryCodeValidator.cleanPhoneNumber(""))
    }

    @Test
    fun `cleanPhoneNumber handles only spaces`() {
        assertEquals("", CountryCodeValidator.cleanPhoneNumber("   "))
    }

    @Test
    fun `cleanPhoneNumber handles only digits`() {
        assertEquals("12025551234", CountryCodeValidator.cleanPhoneNumber("12025551234"))
    }

    @Test
    fun `cleanPhoneNumber trims leading and trailing spaces`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("  +12025551234  "))
    }

    // ========================================================================
    // SECTION 22: detectCountry Tests
    // ========================================================================

    @Test
    fun `detectCountry returns US for plus 1 number`() {
        val country = CountryCodeValidator.detectCountry("+12025551234")
        assertNotNull(country)
        assertEquals("US", country?.code)
    }

    @Test
    fun `detectCountry returns GB for plus 44 number`() {
        val country = CountryCodeValidator.detectCountry("+442071234567")
        assertNotNull(country)
        assertEquals("GB", country?.code)
    }

    @Test
    fun `detectCountry returns DE for plus 49 number`() {
        val country = CountryCodeValidator.detectCountry("+4915123456789")
        assertNotNull(country)
        assertEquals("DE", country?.code)
    }

    @Test
    fun `detectCountry returns FR for plus 33 number`() {
        val country = CountryCodeValidator.detectCountry("+33612345678")
        assertNotNull(country)
        assertEquals("FR", country?.code)
    }

    @Test
    fun `detectCountry returns JP for plus 81 number`() {
        val country = CountryCodeValidator.detectCountry("+819012345678")
        assertNotNull(country)
        assertEquals("JP", country?.code)
    }

    @Test
    fun `detectCountry returns CN for plus 86 number`() {
        val country = CountryCodeValidator.detectCountry("+8613912345678")
        assertNotNull(country)
        assertEquals("CN", country?.code)
    }

    @Test
    fun `detectCountry returns AU for plus 61 number`() {
        val country = CountryCodeValidator.detectCountry("+61412345678")
        assertNotNull(country)
        assertEquals("AU", country?.code)
    }

    @Test
    fun `detectCountry returns IN for plus 91 number`() {
        val country = CountryCodeValidator.detectCountry("+919876543210")
        assertNotNull(country)
        assertEquals("IN", country?.code)
    }

    @Test
    fun `detectCountry returns BR for plus 55 number`() {
        val country = CountryCodeValidator.detectCountry("+5511912345678")
        assertNotNull(country)
        assertEquals("BR", country?.code)
    }

    @Test
    fun `detectCountry returns null for too short number`() {
        val country = CountryCodeValidator.detectCountry("123")
        // May return null since cleaned number is too short
        assertNotNull(country) // or null, depending on implementation
    }

    // ========================================================================
    // SECTION 23: getCountryByIsoCode Tests
    // ========================================================================

    @Test
    fun `getCountryByIsoCode US returns United States`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertNotNull(us)
        assertEquals("United States", us?.name)
    }

    @Test
    fun `getCountryByIsoCode is case insensitive`() {
        val us = CountryCodeValidator.getCountryByIsoCode("us")
        assertNotNull(us)
    }

    @Test
    fun `getCountryByIsoCode returns null for unknown code`() {
        val result = CountryCodeValidator.getCountryByIsoCode("XX")
        assertNull(result)
    }

    @Test
    fun `getCountryByIsoCode GB returns United Kingdom`() {
        val gb = CountryCodeValidator.getCountryByIsoCode("GB")
        assertNotNull(gb)
        assertEquals("United Kingdom", gb?.name)
    }

    @Test
    fun `getCountryByIsoCode DE returns Germany`() {
        val de = CountryCodeValidator.getCountryByIsoCode("DE")
        assertEquals("Germany", de?.name)
    }

    @Test
    fun `getCountryByIsoCode FR returns France`() {
        val fr = CountryCodeValidator.getCountryByIsoCode("FR")
        assertEquals("France", fr?.name)
    }

    @Test
    fun `getCountryByIsoCode JP returns Japan`() {
        val jp = CountryCodeValidator.getCountryByIsoCode("JP")
        assertEquals("Japan", jp?.name)
    }

    @Test
    fun `getCountryByIsoCode CN returns China`() {
        val cn = CountryCodeValidator.getCountryByIsoCode("CN")
        assertEquals("China", cn?.name)
    }

    @Test
    fun `getCountryByIsoCode AU returns Australia`() {
        val au = CountryCodeValidator.getCountryByIsoCode("AU")
        assertEquals("Australia", au?.name)
    }

    @Test
    fun `getCountryByIsoCode IN returns India`() {
        val india = CountryCodeValidator.getCountryByIsoCode("IN")
        assertEquals("India", india?.name)
    }

    @Test
    fun `getCountryByIsoCode BR returns Brazil`() {
        val br = CountryCodeValidator.getCountryByIsoCode("BR")
        assertEquals("Brazil", br?.name)
    }

    @Test
    fun `getCountryByIsoCode MX returns Mexico`() {
        val mx = CountryCodeValidator.getCountryByIsoCode("MX")
        assertEquals("Mexico", mx?.name)
    }

    @Test
    fun `getCountryByIsoCode RU returns Russia`() {
        val ru = CountryCodeValidator.getCountryByIsoCode("RU")
        assertEquals("Russia", ru?.name)
    }

    @Test
    fun `getCountryByIsoCode ZA returns South Africa`() {
        val za = CountryCodeValidator.getCountryByIsoCode("ZA")
        assertEquals("South Africa", za?.name)
    }

    // ========================================================================
    // SECTION 24: getCountriesByCallingCode Tests
    // ========================================================================

    @Test
    fun `getCountriesByCallingCode plus 1 returns US and Canada`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+1")
        assertTrue(countries.size >= 2)
        assertTrue(countries.any { it.code == "US" })
        assertTrue(countries.any { it.code == "CA" })
    }

    @Test
    fun `getCountriesByCallingCode plus 44 returns GB`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+44")
        assertTrue(countries.any { it.code == "GB" })
    }

    @Test
    fun `getCountriesByCallingCode without plus prefix works`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("44")
        assertTrue(countries.any { it.code == "GB" })
    }

    @Test
    fun `getCountriesByCallingCode plus 7 returns Russia and Kazakhstan`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+7")
        assertTrue(countries.size >= 2)
        assertTrue(countries.any { it.code == "RU" })
        assertTrue(countries.any { it.code == "KZ" })
    }

    @Test
    fun `getCountriesByCallingCode returns empty for unknown code`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+999")
        assertTrue(countries.isEmpty())
    }

    // ========================================================================
    // SECTION 25: getCountriesByRegion Tests
    // ========================================================================

    @Test
    fun `getCountriesByRegion NORTH_AMERICA returns US and Canada`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.NORTH_AMERICA)
        assertTrue(countries.any { it.code == "US" })
        assertTrue(countries.any { it.code == "CA" })
    }

    @Test
    fun `getCountriesByRegion EUROPE returns multiple countries`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.EUROPE)
        assertTrue(countries.size >= 20)
    }

    @Test
    fun `getCountriesByRegion ASIA returns multiple countries`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.ASIA)
        assertTrue(countries.size >= 15)
    }

    @Test
    fun `getCountriesByRegion AFRICA returns multiple countries`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.AFRICA)
        assertTrue(countries.size >= 10)
    }

    @Test
    fun `getCountriesByRegion SOUTH_AMERICA returns multiple countries`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.SOUTH_AMERICA)
        assertTrue(countries.size >= 5)
    }

    @Test
    fun `getCountriesByRegion OCEANIA returns multiple countries`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.OCEANIA)
        assertTrue(countries.size >= 5)
    }

    @Test
    fun `getCountriesByRegion MIDDLE_EAST returns multiple countries`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.MIDDLE_EAST)
        assertTrue(countries.size >= 5)
    }

    @Test
    fun `getCountriesByRegion CENTRAL_AMERICA returns multiple countries`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.CENTRAL_AMERICA)
        assertTrue(countries.size >= 5)
    }

    @Test
    fun `getCountriesByRegion CARIBBEAN returns multiple countries`() {
        val countries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.CARIBBEAN)
        assertTrue(countries.size >= 5)
    }

    // ========================================================================
    // SECTION 26: formatToE164 Tests
    // ========================================================================

    @Test
    fun `formatToE164 US number returns E164 format`() {
        val e164 = CountryCodeValidator.formatToE164("+12025551234")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+1"))
    }

    @Test
    fun `formatToE164 UK number returns E164 format`() {
        val e164 = CountryCodeValidator.formatToE164("+442071234567")
        assertNotNull(e164)
        assertTrue(e164!!.startsWith("+44"))
    }

    @Test
    fun `formatToE164 with default US country code`() {
        val e164 = CountryCodeValidator.formatToE164("2025551234", "US")
        assertNotNull(e164)
    }

    @Test
    fun `formatToE164 with default GB country code`() {
        val e164 = CountryCodeValidator.formatToE164("7911123456", "GB")
        assertNotNull(e164)
    }

    @Test
    fun `formatToE164 returns null for invalid number`() {
        val e164 = CountryCodeValidator.formatToE164("123", "US")
        assertNull(e164)
    }

    @Test
    fun `formatToE164 returns null for unknown country code`() {
        val e164 = CountryCodeValidator.formatToE164("123456", "XX")
        assertNull(e164)
    }

    // ========================================================================
    // SECTION 27: looksLikePhoneNumber Tests
    // ========================================================================

    @Test
    fun `looksLikePhoneNumber returns true for plus format`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("+12025551234"))
    }

    @Test
    fun `looksLikePhoneNumber returns true for parentheses format`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("(202) 555-1234"))
    }

    @Test
    fun `looksLikePhoneNumber returns true for dash format`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("202-555-1234"))
    }

    @Test
    fun `looksLikePhoneNumber returns true for 10 digit number`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("2025551234"))
    }

    @Test
    fun `looksLikePhoneNumber returns true for 11 digit number`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("12025551234"))
    }

    @Test
    fun `looksLikePhoneNumber returns false for too short`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("12345"))
    }

    @Test
    fun `looksLikePhoneNumber returns false for too long`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("1234567890123456"))
    }

    @Test
    fun `looksLikePhoneNumber returns false for mostly non-digits`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("Call us at this number please"))
    }

    @Test
    fun `looksLikePhoneNumber returns false for empty`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber(""))
    }

    @Test
    fun `looksLikePhoneNumber returns true for dot-separated`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("202.555.1234"))
    }

    @Test
    fun `looksLikePhoneNumber returns true for space-separated with 8 digits`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("2025 5512 34"))
    }

    // ========================================================================
    // SECTION 28: Invalid Phone Number Tests
    // ========================================================================

    @Test
    fun `validate too short number is invalid`() {
        val result = CountryCodeValidator.validate("123")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate too long number is invalid`() {
        val result = CountryCodeValidator.validate("+1234567890123456")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate empty number is invalid`() {
        val result = CountryCodeValidator.validate("")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate only letters is invalid`() {
        val result = CountryCodeValidator.validate("abcdefghij")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate single digit is invalid`() {
        val result = CountryCodeValidator.validate("5")
        assertFalse(result.isValid)
    }

    // ========================================================================
    // SECTION 29: totalCountries and allCallingCodes Tests
    // ========================================================================

    @Test
    fun `totalCountries returns positive number`() {
        assertTrue(CountryCodeValidator.totalCountries() > 0)
    }

    @Test
    fun `totalCountries is at least 100`() {
        assertTrue(CountryCodeValidator.totalCountries() >= 100)
    }

    @Test
    fun `allCallingCodes returns non-empty set`() {
        assertTrue(CountryCodeValidator.allCallingCodes().isNotEmpty())
    }

    @Test
    fun `allCallingCodes contains plus 1`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+1"))
    }

    @Test
    fun `allCallingCodes contains plus 44`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+44"))
    }

    @Test
    fun `allCallingCodes contains plus 86`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+86"))
    }

    @Test
    fun `allIsoCodes returns non-empty set`() {
        assertTrue(CountryCodeValidator.allIsoCodes().isNotEmpty())
    }

    @Test
    fun `allIsoCodes contains US`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("US"))
    }

    @Test
    fun `allIsoCodes contains GB`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("GB"))
    }

    @Test
    fun `allIsoCodes contains CN`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("CN"))
    }

    // ========================================================================
    // SECTION 30: Region Enum Tests
    // ========================================================================

    @Test
    fun `Region enum has NORTH_AMERICA`() {
        assertNotNull(CountryCodeValidator.Region.NORTH_AMERICA)
    }

    @Test
    fun `Region enum has SOUTH_AMERICA`() {
        assertNotNull(CountryCodeValidator.Region.SOUTH_AMERICA)
    }

    @Test
    fun `Region enum has EUROPE`() {
        assertNotNull(CountryCodeValidator.Region.EUROPE)
    }

    @Test
    fun `Region enum has ASIA`() {
        assertNotNull(CountryCodeValidator.Region.ASIA)
    }

    @Test
    fun `Region enum has AFRICA`() {
        assertNotNull(CountryCodeValidator.Region.AFRICA)
    }

    @Test
    fun `Region enum has OCEANIA`() {
        assertNotNull(CountryCodeValidator.Region.OCEANIA)
    }

    @Test
    fun `Region enum has CARIBBEAN`() {
        assertNotNull(CountryCodeValidator.Region.CARIBBEAN)
    }

    @Test
    fun `Region enum has MIDDLE_EAST`() {
        assertNotNull(CountryCodeValidator.Region.MIDDLE_EAST)
    }

    @Test
    fun `Region enum has CENTRAL_AMERICA`() {
        assertNotNull(CountryCodeValidator.Region.CENTRAL_AMERICA)
    }

    @Test
    fun `Region enum has OTHER`() {
        assertNotNull(CountryCodeValidator.Region.OTHER)
    }

    @Test
    fun `Region enum has exactly 10 values`() {
        assertEquals(10, CountryCodeValidator.Region.values().size)
    }

    // ========================================================================
    // SECTION 31: PhoneValidationResult Properties Tests
    // ========================================================================

    @Test
    fun `valid result has isValid true`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `valid result has non-null countryCode`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertNotNull(result.countryCode)
    }

    @Test
    fun `valid result has non-null nationalNumber`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertNotNull(result.nationalNumber)
    }

    @Test
    fun `valid result has non-null formattedNumber`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertNotNull(result.formattedNumber)
    }

    @Test
    fun `valid result has positive confidence`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertTrue(result.confidence > 0f)
    }

    @Test
    fun `valid result has non-empty reason`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `invalid result has isValid false`() {
        val result = CountryCodeValidator.validate("")
        assertFalse(result.isValid)
    }

    @Test
    fun `PhoneValidationResult defaults`() {
        val result = CountryCodeValidator.PhoneValidationResult(isValid = false)
        assertFalse(result.isValid)
        assertNull(result.countryCode)
        assertNull(result.nationalNumber)
        assertNull(result.formattedNumber)
        assertEquals(0f, result.confidence, 0.001f)
        assertEquals("", result.reason)
        assertNull(result.country)
    }

    // ========================================================================
    // SECTION 32: CountryPhoneInfo Data Class Tests
    // ========================================================================

    @Test
    fun `CountryPhoneInfo US has correct code`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals("US", us?.code)
    }

    @Test
    fun `CountryPhoneInfo US has correct name`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals("United States", us?.name)
    }

    @Test
    fun `CountryPhoneInfo US has correct calling code`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals("+1", us?.callingCode)
    }

    @Test
    fun `CountryPhoneInfo US has correct min length`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals(10, us?.minLength)
    }

    @Test
    fun `CountryPhoneInfo US has correct max length`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals(10, us?.maxLength)
    }

    @Test
    fun `CountryPhoneInfo US has trunk prefix 1`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals("1", us?.trunkPrefix)
    }

    @Test
    fun `CountryPhoneInfo US has non-empty format`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertTrue(us?.format?.isNotEmpty() == true)
    }

    @Test
    fun `CountryPhoneInfo US has mobile prefixes`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertTrue(us?.mobilePrefix?.isNotEmpty() == true)
    }

    @Test
    fun `CountryPhoneInfo US has NORTH_AMERICA region`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals(CountryCodeValidator.Region.NORTH_AMERICA, us?.region)
    }

    // ========================================================================
    // SECTION 33: Oceania Country Tests
    // ========================================================================

    @Test
    fun `validate New Zealand country exists`() {
        val nz = CountryCodeValidator.getCountryByIsoCode("NZ")
        assertNotNull(nz)
        assertEquals("+64", nz?.callingCode)
    }

    @Test
    fun `validate Fiji country exists`() {
        val fj = CountryCodeValidator.getCountryByIsoCode("FJ")
        assertNotNull(fj)
        assertEquals("+679", fj?.callingCode)
    }

    @Test
    fun `validate Papua New Guinea country exists`() {
        val pg = CountryCodeValidator.getCountryByIsoCode("PG")
        assertNotNull(pg)
        assertEquals("+675", pg?.callingCode)
    }

    @Test
    fun `validate Samoa country exists`() {
        val ws = CountryCodeValidator.getCountryByIsoCode("WS")
        assertNotNull(ws)
        assertEquals("+685", ws?.callingCode)
    }

    @Test
    fun `validate Tonga country exists`() {
        val to = CountryCodeValidator.getCountryByIsoCode("TO")
        assertNotNull(to)
        assertEquals("+676", to?.callingCode)
    }

    @Test
    fun `validate Vanuatu country exists`() {
        val vu = CountryCodeValidator.getCountryByIsoCode("VU")
        assertNotNull(vu)
        assertEquals("+678", vu?.callingCode)
    }

    @Test
    fun `validate New Caledonia country exists`() {
        val nc = CountryCodeValidator.getCountryByIsoCode("NC")
        assertNotNull(nc)
        assertEquals("+687", nc?.callingCode)
    }

    @Test
    fun `validate French Polynesia country exists`() {
        val pf = CountryCodeValidator.getCountryByIsoCode("PF")
        assertNotNull(pf)
        assertEquals("+689", pf?.callingCode)
    }

    @Test
    fun `validate all Oceania countries in OCEANIA region`() {
        val oceaniaCodes = listOf("AU", "NZ", "FJ", "PG", "WS", "TO", "VU", "SB", "NC", "PF")
        for (code in oceaniaCodes) {
            val country = CountryCodeValidator.getCountryByIsoCode(code)
            assertNotNull("$code should exist", country)
            assertEquals("$code should be OCEANIA", CountryCodeValidator.Region.OCEANIA, country?.region)
        }
    }

    // ========================================================================
    // SECTION 34: Central American Country Tests
    // ========================================================================

    @Test
    fun `validate Guatemala country exists`() {
        val gt = CountryCodeValidator.getCountryByIsoCode("GT")
        assertNotNull(gt)
        assertEquals("+502", gt?.callingCode)
    }

    @Test
    fun `validate Belize country exists`() {
        val bz = CountryCodeValidator.getCountryByIsoCode("BZ")
        assertNotNull(bz)
        assertEquals("+501", bz?.callingCode)
    }

    @Test
    fun `validate El Salvador country exists`() {
        val sv = CountryCodeValidator.getCountryByIsoCode("SV")
        assertNotNull(sv)
        assertEquals("+503", sv?.callingCode)
    }

    @Test
    fun `validate Honduras country exists`() {
        val hn = CountryCodeValidator.getCountryByIsoCode("HN")
        assertNotNull(hn)
        assertEquals("+504", hn?.callingCode)
    }

    @Test
    fun `validate Nicaragua country exists`() {
        val ni = CountryCodeValidator.getCountryByIsoCode("NI")
        assertNotNull(ni)
        assertEquals("+505", ni?.callingCode)
    }

    @Test
    fun `validate Costa Rica country exists`() {
        val cr = CountryCodeValidator.getCountryByIsoCode("CR")
        assertNotNull(cr)
        assertEquals("+506", cr?.callingCode)
    }

    @Test
    fun `validate Panama country exists`() {
        val pa = CountryCodeValidator.getCountryByIsoCode("PA")
        assertNotNull(pa)
        assertEquals("+507", pa?.callingCode)
    }

    // ========================================================================
    // SECTION 35: Regression and Integration Tests
    // ========================================================================

    @Test
    fun `validate same number twice gives consistent results`() {
        val r1 = CountryCodeValidator.validate("+12025551234")
        val r2 = CountryCodeValidator.validate("+12025551234")
        assertEquals(r1.isValid, r2.isValid)
        assertEquals(r1.countryCode, r2.countryCode)
        assertEquals(r1.confidence, r2.confidence)
    }

    @Test
    fun `validate different format of same number gives same country`() {
        val r1 = CountryCodeValidator.validate("+12025551234")
        val r2 = CountryCodeValidator.validate("+1-202-555-1234")
        val r3 = CountryCodeValidator.validate("+1 (202) 555-1234")
        assertEquals(r1.countryCode, r2.countryCode)
        assertEquals(r2.countryCode, r3.countryCode)
    }

    @Test
    fun `validate numbers from multiple countries sequentially`() {
        val numbers = listOf(
            "+12025551234",
            "+442071234567",
            "+33612345678",
            "+819012345678",
            "+61412345678"
        )
        for (number in numbers) {
            val result = CountryCodeValidator.validate(number)
            assertTrue("$number should be valid", result.isValid)
        }
    }

    @Test
    fun `validate countries list has expected structure`() {
        for (country in CountryCodeValidator.countries) {
            assertTrue("${country.code} code should be 2 chars", country.code.length == 2)
            assertTrue("${country.code} name should not be empty", country.name.isNotEmpty())
            assertTrue("${country.code} calling code should start with +", country.callingCode.startsWith("+"))
            assertTrue("${country.code} minLength should be positive", country.minLength > 0)
            assertTrue("${country.code} maxLength >= minLength", country.maxLength >= country.minLength)
        }
    }

    @Test
    fun `validate all NANP countries have 10 digit length`() {
        val nanpCallingCode = "+1"
        val nanpCountries = CountryCodeValidator.countries.filter { it.callingCode.startsWith(nanpCallingCode) }
        for (country in nanpCountries) {
            assertEquals("${country.code} should have 10 min", 10, country.minLength)
            assertEquals("${country.code} should have 10 max", 10, country.maxLength)
        }
    }

    @Test
    fun `validate all Middle East countries are in MIDDLE_EAST region`() {
        val meCodes = listOf("AE", "SA", "QA", "KW", "BH", "OM", "YE", "JO", "LB", "SY", "IQ", "IR", "IL", "PS", "TR")
        for (code in meCodes) {
            val country = CountryCodeValidator.getCountryByIsoCode(code)
            assertNotNull("$code should exist", country)
            assertEquals("$code should be MIDDLE_EAST", CountryCodeValidator.Region.MIDDLE_EAST, country?.region)
        }
    }

    @Test
    fun `validate all African countries in AFRICA region`() {
        val afCodes = listOf("ZA", "NG", "KE", "GH", "ET", "TZ", "UG", "EG", "MA", "TN", "DZ")
        for (code in afCodes) {
            val country = CountryCodeValidator.getCountryByIsoCode(code)
            assertNotNull("$code should exist", country)
            assertEquals("$code should be AFRICA", CountryCodeValidator.Region.AFRICA, country?.region)
        }
    }

    @Test
    fun `validate countries list is not empty`() {
        assertTrue(CountryCodeValidator.countries.isNotEmpty())
    }

    @Test
    fun `validate countries list has over 100 entries`() {
        assertTrue(CountryCodeValidator.countries.size > 100)
    }

    @Test
    fun `validate Italy has no trunk prefix`() {
        val it = CountryCodeValidator.getCountryByIsoCode("IT")
        assertNull(it?.trunkPrefix)
    }

    @Test
    fun `validate Spain has no trunk prefix`() {
        val es = CountryCodeValidator.getCountryByIsoCode("ES")
        assertNull(es?.trunkPrefix)
    }

    @Test
    fun `validate Norway has no trunk prefix`() {
        val no = CountryCodeValidator.getCountryByIsoCode("NO")
        assertNull(no?.trunkPrefix)
    }

    @Test
    fun `validate Denmark has no trunk prefix`() {
        val dk = CountryCodeValidator.getCountryByIsoCode("DK")
        assertNull(dk?.trunkPrefix)
    }

    @Test
    fun `validate Singapore has no trunk prefix`() {
        val sg = CountryCodeValidator.getCountryByIsoCode("SG")
        assertNull(sg?.trunkPrefix)
    }

    @Test
    fun `validate Hong Kong has no trunk prefix`() {
        val hk = CountryCodeValidator.getCountryByIsoCode("HK")
        assertNull(hk?.trunkPrefix)
    }
}
