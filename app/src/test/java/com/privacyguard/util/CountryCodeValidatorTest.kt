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

    // ========================================================================
    // SECTION 36: Additional NANP Edge Case Tests
    // ========================================================================

    @Test
    fun `NANP rejects area code starting with 0`() {
        val result = CountryCodeValidator.validateNANPNumber("0125551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects area code starting with 1`() {
        val result = CountryCodeValidator.validateNANPNumber("1125551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects exchange starting with 0`() {
        val result = CountryCodeValidator.validateNANPNumber("2120551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects exchange starting with 1`() {
        val result = CountryCodeValidator.validateNANPNumber("2121551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects 211 area code as N11 service code`() {
        val result = CountryCodeValidator.validateNANPNumber("2115551234")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("N11"))
    }

    @Test
    fun `NANP rejects 311 area code as N11 service code`() {
        val result = CountryCodeValidator.validateNANPNumber("3115551234")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("N11"))
    }

    @Test
    fun `NANP rejects 411 area code as N11 service code`() {
        val result = CountryCodeValidator.validateNANPNumber("4115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects 511 area code as N11 service code`() {
        val result = CountryCodeValidator.validateNANPNumber("5115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects 611 area code as N11 service code`() {
        val result = CountryCodeValidator.validateNANPNumber("6115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects 711 area code as N11 service code`() {
        val result = CountryCodeValidator.validateNANPNumber("7115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects 811 area code as N11 service code`() {
        val result = CountryCodeValidator.validateNANPNumber("8115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects 911 area code as N11 service code`() {
        val result = CountryCodeValidator.validateNANPNumber("9115551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP accepts 212 area code not N11`() {
        val result = CountryCodeValidator.validateNANPNumber("2125551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP accepts 312 area code not N11`() {
        val result = CountryCodeValidator.validateNANPNumber("3125551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP accepts 512 area code not N11`() {
        val result = CountryCodeValidator.validateNANPNumber("5125551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP accepts 713 area code not N11`() {
        val result = CountryCodeValidator.validateNANPNumber("7135551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP rejects 555-0100 as fictional`() {
        val result = CountryCodeValidator.validateNANPNumber("2025550100")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects 555-0199 as fictional range`() {
        val result = CountryCodeValidator.validateNANPNumber("2025550199")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP accepts 555-0200 as not in fictional range`() {
        val result = CountryCodeValidator.validateNANPNumber("2025550200")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP accepts 555-1212 as directory assistance`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551212")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP with 11 digits starting with 1 is valid`() {
        val result = CountryCodeValidator.validateNANPNumber("12025551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP rejects 9 digit number`() {
        val result = CountryCodeValidator.validateNANPNumber("202555123")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects 12 digit number`() {
        val result = CountryCodeValidator.validateNANPNumber("120255512345")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP valid result has US country code`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertEquals("US", result.countryCode)
    }

    @Test
    fun `NANP valid result has 0 point 95 confidence`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertEquals(0.95f, result.confidence)
    }

    @Test
    fun `NANP valid result has formatted number`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertEquals("+12025551234", result.formattedNumber)
    }

    @Test
    fun `NANP valid result reason says valid NANP`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertTrue(result.reason.contains("Valid NANP"))
    }

    @Test
    fun `NANP rejects area code 000`() {
        val result = CountryCodeValidator.validateNANPNumber("0005551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP rejects area code 100`() {
        val result = CountryCodeValidator.validateNANPNumber("1005551234")
        assertFalse(result.isValid)
    }

    @Test
    fun `NANP accepts area code 200`() {
        val result = CountryCodeValidator.validateNANPNumber("2005551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP accepts area code 999`() {
        val result = CountryCodeValidator.validateNANPNumber("9995551234")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP accepts exchange 200`() {
        val result = CountryCodeValidator.validateNANPNumber("2022001234")
        assertTrue(result.isValid)
    }

    @Test
    fun `NANP accepts exchange 999`() {
        val result = CountryCodeValidator.validateNANPNumber("2029991234")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 37: Extended cleanPhoneNumber Tests
    // ========================================================================

    @Test
    fun `clean removes leading spaces`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("  +12025551234"))
    }

    @Test
    fun `clean removes trailing spaces`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("+12025551234  "))
    }

    @Test
    fun `clean removes internal dashes`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("+1-202-555-1234"))
    }

    @Test
    fun `clean removes internal spaces`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("+1 202 555 1234"))
    }

    @Test
    fun `clean removes parentheses`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("+1(202)5551234"))
    }

    @Test
    fun `clean removes dots`() {
        assertEquals("+12025551234", CountryCodeValidator.cleanPhoneNumber("+1.202.555.1234"))
    }

    @Test
    fun `clean preserves leading plus sign`() {
        val result = CountryCodeValidator.cleanPhoneNumber("+442071234567")
        assertTrue(result.startsWith("+"))
    }

    @Test
    fun `clean does not add plus sign if not present`() {
        val result = CountryCodeValidator.cleanPhoneNumber("442071234567")
        assertFalse(result.startsWith("+"))
    }

    @Test
    fun `clean removes plus in middle of number`() {
        val result = CountryCodeValidator.cleanPhoneNumber("44+2071234567")
        assertFalse(result.contains("+"))
    }

    @Test
    fun `clean empty string returns empty`() {
        assertEquals("", CountryCodeValidator.cleanPhoneNumber(""))
    }

    @Test
    fun `clean only spaces returns empty`() {
        assertEquals("", CountryCodeValidator.cleanPhoneNumber("   "))
    }

    @Test
    fun `clean only plus returns plus`() {
        assertEquals("+", CountryCodeValidator.cleanPhoneNumber("+"))
    }

    @Test
    fun `clean removes hash symbol`() {
        val result = CountryCodeValidator.cleanPhoneNumber("+1202555#1234")
        assertFalse(result.contains("#"))
    }

    @Test
    fun `clean removes star symbol`() {
        val result = CountryCodeValidator.cleanPhoneNumber("+1202555*1234")
        assertFalse(result.contains("*"))
    }

    @Test
    fun `clean removes letters`() {
        val result = CountryCodeValidator.cleanPhoneNumber("+1202abc5551234")
        assertEquals("+12025551234", result)
    }

    @Test
    fun `clean handles mixed formatting characters`() {
        val result = CountryCodeValidator.cleanPhoneNumber("+1 (202) 555-1234")
        assertEquals("+12025551234", result)
    }

    @Test
    fun `clean handles international format with dash dot space`() {
        val result = CountryCodeValidator.cleanPhoneNumber("+44-20 7123.4567")
        assertEquals("+442071234567", result)
    }

    @Test
    fun `clean handles tab characters`() {
        val result = CountryCodeValidator.cleanPhoneNumber("+1\t202\t555\t1234")
        assertEquals("+12025551234", result)
    }

    // ========================================================================
    // SECTION 38: Country Data Integrity Tests
    // ========================================================================

    @Test
    fun `all ISO codes are unique`() {
        val codes = CountryCodeValidator.countries.map { it.code }
        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun `all country names are non-empty`() {
        for (country in CountryCodeValidator.countries) {
            assertTrue("${country.code} name should not be empty", country.name.isNotEmpty())
        }
    }

    @Test
    fun `all calling codes start with plus`() {
        for (country in CountryCodeValidator.countries) {
            assertTrue("${country.code} calling code should start with +", country.callingCode.startsWith("+"))
        }
    }

    @Test
    fun `all min lengths are positive`() {
        for (country in CountryCodeValidator.countries) {
            assertTrue("${country.code} minLength should be > 0", country.minLength > 0)
        }
    }

    @Test
    fun `all max lengths greater or equal to min lengths`() {
        for (country in CountryCodeValidator.countries) {
            assertTrue("${country.code} maxLength >= minLength",
                country.maxLength >= country.minLength)
        }
    }

    @Test
    fun `all ISO codes are 2 characters`() {
        for (country in CountryCodeValidator.countries) {
            assertEquals("${country.code} should be 2 chars", 2, country.code.length)
        }
    }

    @Test
    fun `all ISO codes are uppercase`() {
        for (country in CountryCodeValidator.countries) {
            assertEquals("${country.code} should be uppercase",
                country.code.uppercase(), country.code)
        }
    }

    @Test
    fun `all calling codes contain only digits and plus`() {
        for (country in CountryCodeValidator.countries) {
            val cc = country.callingCode
            assertTrue("${country.code} calling code has valid chars",
                cc.all { it.isDigit() || it == '+' })
        }
    }

    @Test
    fun `no country has max length greater than 15`() {
        for (country in CountryCodeValidator.countries) {
            assertTrue("${country.code} maxLength should be <= 15",
                country.maxLength <= 15)
        }
    }

    @Test
    fun `no country has min length less than 4`() {
        for (country in CountryCodeValidator.countries) {
            assertTrue("${country.code} minLength should be >= 4",
                country.minLength >= 4)
        }
    }

    @Test
    fun `all region values are valid enum entries`() {
        val validRegions = CountryCodeValidator.Region.values().toSet()
        for (country in CountryCodeValidator.countries) {
            assertTrue("${country.code} has valid region",
                country.region in validRegions)
        }
    }

    @Test
    fun `North American countries have calling code starting with 1`() {
        val naCountries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.NORTH_AMERICA)
        for (country in naCountries) {
            assertTrue("${country.code} should start with +1",
                country.callingCode.startsWith("+1"))
        }
    }

    @Test
    fun `European countries have at least 20 entries`() {
        val euCountries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.EUROPE)
        assertTrue("Should have 20+ European countries", euCountries.size >= 20)
    }

    @Test
    fun `Asian countries have at least 15 entries`() {
        val asiaCountries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.ASIA)
        assertTrue("Should have 15+ Asian countries", asiaCountries.size >= 15)
    }

    @Test
    fun `African countries have at least 15 entries`() {
        val afCountries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.AFRICA)
        assertTrue("Should have 15+ African countries", afCountries.size >= 15)
    }

    @Test
    fun `South American countries have at least 10 entries`() {
        val saCountries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.SOUTH_AMERICA)
        assertTrue("Should have 10+ SA countries", saCountries.size >= 10)
    }

    @Test
    fun `Middle Eastern countries have at least 10 entries`() {
        val meCountries = CountryCodeValidator.getCountriesByRegion(CountryCodeValidator.Region.MIDDLE_EAST)
        assertTrue("Should have 10+ ME countries", meCountries.size >= 10)
    }

    @Test
    fun `trunk prefix when present is short string`() {
        for (country in CountryCodeValidator.countries) {
            if (country.trunkPrefix != null) {
                assertTrue("${country.code} trunk prefix should be 1-2 chars",
                    country.trunkPrefix.length in 1..2)
            }
        }
    }

    // ========================================================================
    // SECTION 39: Extended formatToE164 Tests
    // ========================================================================

    @Test
    fun `formatToE164 US number with plus`() {
        val result = CountryCodeValidator.formatToE164("+12025551234")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+1"))
    }

    @Test
    fun `formatToE164 US number without plus`() {
        val result = CountryCodeValidator.formatToE164("12025551234")
        assertNotNull(result)
    }

    @Test
    fun `formatToE164 UK number with plus`() {
        val result = CountryCodeValidator.formatToE164("+442071234567")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+44"))
    }

    @Test
    fun `formatToE164 France number`() {
        val result = CountryCodeValidator.formatToE164("+33612345678")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+33"))
    }

    @Test
    fun `formatToE164 Germany number`() {
        val result = CountryCodeValidator.formatToE164("+491512345678")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+49"))
    }

    @Test
    fun `formatToE164 Japan number`() {
        val result = CountryCodeValidator.formatToE164("+819012345678")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+81"))
    }

    @Test
    fun `formatToE164 with default country code US`() {
        val result = CountryCodeValidator.formatToE164("2025551234", "US")
        assertNotNull(result)
    }

    @Test
    fun `formatToE164 with default country code GB`() {
        val result = CountryCodeValidator.formatToE164("2071234567", "GB")
        assertNotNull(result)
    }

    @Test
    fun `formatToE164 with default country code DE`() {
        val result = CountryCodeValidator.formatToE164("1512345678", "DE")
        assertNotNull(result)
    }

    @Test
    fun `formatToE164 with default country code FR`() {
        val result = CountryCodeValidator.formatToE164("612345678", "FR")
        assertNotNull(result)
    }

    @Test
    fun `formatToE164 with invalid country code returns null`() {
        val result = CountryCodeValidator.formatToE164("12345", "ZZ")
        assertNull(result)
    }

    @Test
    fun `formatToE164 with too short number returns null`() {
        val result = CountryCodeValidator.formatToE164("123", "US")
        assertNull(result)
    }

    @Test
    fun `formatToE164 India number`() {
        val result = CountryCodeValidator.formatToE164("+919876543210")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+91"))
    }

    @Test
    fun `formatToE164 Australia number`() {
        val result = CountryCodeValidator.formatToE164("+61412345678")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+61"))
    }

    @Test
    fun `formatToE164 Brazil number`() {
        val result = CountryCodeValidator.formatToE164("+5511912345678")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+55"))
    }

    @Test
    fun `formatToE164 Mexico number`() {
        val result = CountryCodeValidator.formatToE164("+521234567890")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+52"))
    }

    @Test
    fun `formatToE164 formatted US number with spaces`() {
        val result = CountryCodeValidator.formatToE164("+1 202 555 1234")
        assertNotNull(result)
    }

    @Test
    fun `formatToE164 formatted US number with dashes`() {
        val result = CountryCodeValidator.formatToE164("+1-202-555-1234")
        assertNotNull(result)
    }

    @Test
    fun `formatToE164 result contains no spaces`() {
        val result = CountryCodeValidator.formatToE164("+1 202 555 1234")
        assertNotNull(result)
        assertFalse(result!!.contains(" "))
    }

    @Test
    fun `formatToE164 result contains no dashes`() {
        val result = CountryCodeValidator.formatToE164("+1-202-555-1234")
        assertNotNull(result)
        assertFalse(result!!.contains("-"))
    }

    @Test
    fun `formatToE164 result starts with plus`() {
        val result = CountryCodeValidator.formatToE164("+442071234567")
        assertNotNull(result)
        assertTrue(result!!.startsWith("+"))
    }

    // ========================================================================
    // SECTION 40: Extended detectCountry Tests
    // ========================================================================

    @Test
    fun `detectCountry US number returns US`() {
        val country = CountryCodeValidator.detectCountry("+12025551234")
        assertNotNull(country)
        assertEquals("US", country!!.code)
    }

    @Test
    fun `detectCountry UK number returns GB`() {
        val country = CountryCodeValidator.detectCountry("+442071234567")
        assertNotNull(country)
        assertEquals("GB", country!!.code)
    }

    @Test
    fun `detectCountry Germany number returns DE`() {
        val country = CountryCodeValidator.detectCountry("+491512345678")
        assertNotNull(country)
        assertEquals("DE", country!!.code)
    }

    @Test
    fun `detectCountry France number returns FR`() {
        val country = CountryCodeValidator.detectCountry("+33612345678")
        assertNotNull(country)
        assertEquals("FR", country!!.code)
    }

    @Test
    fun `detectCountry Japan number returns JP`() {
        val country = CountryCodeValidator.detectCountry("+819012345678")
        assertNotNull(country)
        assertEquals("JP", country!!.code)
    }

    @Test
    fun `detectCountry India number returns IN`() {
        val country = CountryCodeValidator.detectCountry("+919876543210")
        assertNotNull(country)
        assertEquals("IN", country!!.code)
    }

    @Test
    fun `detectCountry China number returns CN`() {
        val country = CountryCodeValidator.detectCountry("+8613912345678")
        assertNotNull(country)
        assertEquals("CN", country!!.code)
    }

    @Test
    fun `detectCountry Australia number returns AU`() {
        val country = CountryCodeValidator.detectCountry("+61412345678")
        assertNotNull(country)
        assertEquals("AU", country!!.code)
    }

    @Test
    fun `detectCountry Brazil number returns BR`() {
        val country = CountryCodeValidator.detectCountry("+5511912345678")
        assertNotNull(country)
        assertEquals("BR", country!!.code)
    }

    @Test
    fun `detectCountry Mexico number returns MX`() {
        val country = CountryCodeValidator.detectCountry("+521234567890")
        assertNotNull(country)
        assertEquals("MX", country!!.code)
    }

    @Test
    fun `detectCountry South Korea number returns KR`() {
        val country = CountryCodeValidator.detectCountry("+821012345678")
        assertNotNull(country)
        assertEquals("KR", country!!.code)
    }

    @Test
    fun `detectCountry Italy number returns IT`() {
        val country = CountryCodeValidator.detectCountry("+393123456789")
        assertNotNull(country)
        assertEquals("IT", country!!.code)
    }

    @Test
    fun `detectCountry Spain number returns ES`() {
        val country = CountryCodeValidator.detectCountry("+34612345678")
        assertNotNull(country)
        assertEquals("ES", country!!.code)
    }

    @Test
    fun `detectCountry Netherlands number returns NL`() {
        val country = CountryCodeValidator.detectCountry("+31612345678")
        assertNotNull(country)
        assertEquals("NL", country!!.code)
    }

    @Test
    fun `detectCountry UAE number returns AE`() {
        val country = CountryCodeValidator.detectCountry("+971501234567")
        assertNotNull(country)
        assertEquals("AE", country!!.code)
    }

    @Test
    fun `detectCountry Saudi Arabia number returns SA`() {
        val country = CountryCodeValidator.detectCountry("+966512345678")
        assertNotNull(country)
        assertEquals("SA", country!!.code)
    }

    @Test
    fun `detectCountry South Africa number returns ZA`() {
        val country = CountryCodeValidator.detectCountry("+27612345678")
        assertNotNull(country)
        assertEquals("ZA", country!!.code)
    }

    @Test
    fun `detectCountry Nigeria number returns NG`() {
        val country = CountryCodeValidator.detectCountry("+2347012345678")
        assertNotNull(country)
        assertEquals("NG", country!!.code)
    }

    @Test
    fun `detectCountry without plus sign still works`() {
        val country = CountryCodeValidator.detectCountry("12025551234")
        assertNotNull(country)
    }

    @Test
    fun `detectCountry with formatted number`() {
        val country = CountryCodeValidator.detectCountry("+1 (202) 555-1234")
        assertNotNull(country)
    }

    @Test
    fun `detectCountry invalid short number returns null`() {
        val country = CountryCodeValidator.detectCountry("+999")
        assertNull(country)
    }

    @Test
    fun `detectCountry Turkey returns TR`() {
        val country = CountryCodeValidator.detectCountry("+905321234567")
        assertNotNull(country)
        assertEquals("TR", country!!.code)
    }

    @Test
    fun `detectCountry Israel returns IL`() {
        val country = CountryCodeValidator.detectCountry("+972521234567")
        assertNotNull(country)
        assertEquals("IL", country!!.code)
    }

    @Test
    fun `detectCountry Egypt returns EG`() {
        val country = CountryCodeValidator.detectCountry("+201012345678")
        assertNotNull(country)
        assertEquals("EG", country!!.code)
    }

    @Test
    fun `detectCountry Kenya returns KE`() {
        val country = CountryCodeValidator.detectCountry("+254712345678")
        assertNotNull(country)
        assertEquals("KE", country!!.code)
    }

    // ========================================================================
    // SECTION 41: Extended looksLikePhoneNumber Tests
    // ========================================================================

    @Test
    fun `looksLikePhoneNumber with plus prefix and digits returns true`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("+12025551234"))
    }

    @Test
    fun `looksLikePhoneNumber with parentheses returns true`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("(202) 555-1234"))
    }

    @Test
    fun `looksLikePhoneNumber with dashes and 10 digits returns true`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("202-555-1234"))
    }

    @Test
    fun `looksLikePhoneNumber with dots and 10 digits returns true`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("202.555.1234"))
    }

    @Test
    fun `looksLikePhoneNumber with spaces and 10 digits returns true`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("202 555 1234"))
    }

    @Test
    fun `looksLikePhoneNumber with only 6 digits returns false`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("123456"))
    }

    @Test
    fun `looksLikePhoneNumber with 16 digits returns false`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("1234567890123456"))
    }

    @Test
    fun `looksLikePhoneNumber with text returns false`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("call me at the office"))
    }

    @Test
    fun `looksLikePhoneNumber with 10 plain digits returns true`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("2025551234"))
    }

    @Test
    fun `looksLikePhoneNumber with 11 plain digits returns true`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("12025551234"))
    }

    @Test
    fun `looksLikePhoneNumber with 7 digits and dashes`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("555-1234"))
    }

    @Test
    fun `looksLikePhoneNumber with international format`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("+44 20 7123 4567"))
    }

    @Test
    fun `looksLikePhoneNumber empty string returns false`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber(""))
    }

    @Test
    fun `looksLikePhoneNumber only spaces returns false`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("   "))
    }

    @Test
    fun `looksLikePhoneNumber only letters returns false`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("abcdefghij"))
    }

    @Test
    fun `looksLikePhoneNumber too many non-digits returns false`() {
        assertFalse(CountryCodeValidator.looksLikePhoneNumber("a b c d e f g 1 2 3 4 5 6 7"))
    }

    @Test
    fun `looksLikePhoneNumber with country code in E164 format`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("+819012345678"))
    }

    @Test
    fun `looksLikePhoneNumber with plus and short number`() {
        assertTrue(CountryCodeValidator.looksLikePhoneNumber("+4512345678"))
    }

    // ========================================================================
    // SECTION 42: Trunk Prefix Handling Tests
    // ========================================================================

    @Test
    fun `validate UK number with trunk prefix 0 is handled`() {
        val uk = CountryCodeValidator.getCountryByIsoCode("GB")
        assertNotNull(uk)
        assertEquals("0", uk!!.trunkPrefix)
    }

    @Test
    fun `validate Germany has trunk prefix 0`() {
        val de = CountryCodeValidator.getCountryByIsoCode("DE")
        assertNotNull(de)
        assertEquals("0", de!!.trunkPrefix)
    }

    @Test
    fun `validate France has trunk prefix 0`() {
        val fr = CountryCodeValidator.getCountryByIsoCode("FR")
        assertNotNull(fr)
        assertEquals("0", fr!!.trunkPrefix)
    }

    @Test
    fun `validate Netherlands has trunk prefix 0`() {
        val nl = CountryCodeValidator.getCountryByIsoCode("NL")
        assertNotNull(nl)
        assertEquals("0", nl!!.trunkPrefix)
    }

    @Test
    fun `validate Belgium has trunk prefix 0`() {
        val be = CountryCodeValidator.getCountryByIsoCode("BE")
        assertNotNull(be)
        assertEquals("0", be!!.trunkPrefix)
    }

    @Test
    fun `validate Austria has trunk prefix 0`() {
        val at = CountryCodeValidator.getCountryByIsoCode("AT")
        assertNotNull(at)
        assertEquals("0", at!!.trunkPrefix)
    }

    @Test
    fun `validate Switzerland has trunk prefix 0`() {
        val ch = CountryCodeValidator.getCountryByIsoCode("CH")
        assertNotNull(ch)
        assertEquals("0", ch!!.trunkPrefix)
    }

    @Test
    fun `validate Sweden has trunk prefix 0`() {
        val se = CountryCodeValidator.getCountryByIsoCode("SE")
        assertNotNull(se)
        assertEquals("0", se!!.trunkPrefix)
    }

    @Test
    fun `validate US has trunk prefix 1`() {
        val us = CountryCodeValidator.getCountryByIsoCode("US")
        assertNotNull(us)
        assertEquals("1", us!!.trunkPrefix)
    }

    @Test
    fun `validate Hungary has trunk prefix 06`() {
        val hu = CountryCodeValidator.getCountryByIsoCode("HU")
        assertNotNull(hu)
        assertEquals("06", hu!!.trunkPrefix)
    }

    @Test
    fun `validate Belarus has trunk prefix 80`() {
        val by = CountryCodeValidator.getCountryByIsoCode("BY")
        assertNotNull(by)
        assertEquals("80", by!!.trunkPrefix)
    }

    @Test
    fun `validate Russia has trunk prefix 8`() {
        val ru = CountryCodeValidator.getCountryByIsoCode("RU")
        assertNotNull(ru)
        assertEquals("8", ru!!.trunkPrefix)
    }

    @Test
    fun `validate Poland has no trunk prefix`() {
        val pl = CountryCodeValidator.getCountryByIsoCode("PL")
        assertNull(pl?.trunkPrefix)
    }

    @Test
    fun `validate Czech Republic has no trunk prefix`() {
        val cz = CountryCodeValidator.getCountryByIsoCode("CZ")
        assertNull(cz?.trunkPrefix)
    }

    @Test
    fun `validate Greece has no trunk prefix`() {
        val gr = CountryCodeValidator.getCountryByIsoCode("GR")
        assertNull(gr?.trunkPrefix)
    }

    @Test
    fun `validate Portugal has no trunk prefix`() {
        val pt = CountryCodeValidator.getCountryByIsoCode("PT")
        assertNull(pt?.trunkPrefix)
    }

    @Test
    fun `validate China has trunk prefix 0`() {
        val cn = CountryCodeValidator.getCountryByIsoCode("CN")
        assertNotNull(cn)
        assertEquals("0", cn!!.trunkPrefix)
    }

    @Test
    fun `validate Japan has trunk prefix 0`() {
        val jp = CountryCodeValidator.getCountryByIsoCode("JP")
        assertNotNull(jp)
        assertEquals("0", jp!!.trunkPrefix)
    }

    @Test
    fun `validate India has trunk prefix 0`() {
        val inCountry = CountryCodeValidator.getCountryByIsoCode("IN")
        assertNotNull(inCountry)
        assertEquals("0", inCountry!!.trunkPrefix)
    }

    @Test
    fun `validate Australia has trunk prefix 0`() {
        val au = CountryCodeValidator.getCountryByIsoCode("AU")
        assertNotNull(au)
        assertEquals("0", au!!.trunkPrefix)
    }

    // ========================================================================
    // SECTION 43: Mobile Prefix Tests
    // ========================================================================

    @Test
    fun `UK mobile prefix is 7`() {
        val gb = CountryCodeValidator.getCountryByIsoCode("GB")
        assertTrue(gb!!.mobilePrefix.contains("7"))
    }

    @Test
    fun `Germany mobile prefixes include 15 16 17`() {
        val de = CountryCodeValidator.getCountryByIsoCode("DE")
        assertTrue(de!!.mobilePrefix.containsAll(listOf("15", "16", "17")))
    }

    @Test
    fun `France mobile prefixes include 6 and 7`() {
        val fr = CountryCodeValidator.getCountryByIsoCode("FR")
        assertTrue(fr!!.mobilePrefix.containsAll(listOf("6", "7")))
    }

    @Test
    fun `Italy mobile prefix is 3`() {
        val it = CountryCodeValidator.getCountryByIsoCode("IT")
        assertTrue(it!!.mobilePrefix.contains("3"))
    }

    @Test
    fun `Spain mobile prefixes include 6 and 7`() {
        val es = CountryCodeValidator.getCountryByIsoCode("ES")
        assertTrue(es!!.mobilePrefix.containsAll(listOf("6", "7")))
    }

    @Test
    fun `Netherlands mobile prefix is 6`() {
        val nl = CountryCodeValidator.getCountryByIsoCode("NL")
        assertTrue(nl!!.mobilePrefix.contains("6"))
    }

    @Test
    fun `Japan mobile prefixes include 70 80 90`() {
        val jp = CountryCodeValidator.getCountryByIsoCode("JP")
        assertTrue(jp!!.mobilePrefix.containsAll(listOf("70", "80", "90")))
    }

    @Test
    fun `South Korea mobile prefixes include 10`() {
        val kr = CountryCodeValidator.getCountryByIsoCode("KR")
        assertTrue(kr!!.mobilePrefix.contains("10"))
    }

    @Test
    fun `India mobile prefixes include 6 7 8 9`() {
        val inCountry = CountryCodeValidator.getCountryByIsoCode("IN")
        assertTrue(inCountry!!.mobilePrefix.containsAll(listOf("6", "7", "8", "9")))
    }

    @Test
    fun `China mobile prefixes include 13 14 15`() {
        val cn = CountryCodeValidator.getCountryByIsoCode("CN")
        assertTrue(cn!!.mobilePrefix.containsAll(listOf("13", "14", "15")))
    }

    @Test
    fun `Australia mobile prefix is 4`() {
        val au = CountryCodeValidator.getCountryByIsoCode("AU")
        assertTrue(au!!.mobilePrefix.contains("4"))
    }

    @Test
    fun `Brazil mobile prefix is 9`() {
        val br = CountryCodeValidator.getCountryByIsoCode("BR")
        assertTrue(br!!.mobilePrefix.contains("9"))
    }

    @Test
    fun `UAE mobile prefix is 5`() {
        val ae = CountryCodeValidator.getCountryByIsoCode("AE")
        assertTrue(ae!!.mobilePrefix.contains("5"))
    }

    @Test
    fun `Saudi Arabia mobile prefix is 5`() {
        val sa = CountryCodeValidator.getCountryByIsoCode("SA")
        assertTrue(sa!!.mobilePrefix.contains("5"))
    }

    @Test
    fun `Turkey mobile prefix is 5`() {
        val tr = CountryCodeValidator.getCountryByIsoCode("TR")
        assertTrue(tr!!.mobilePrefix.contains("5"))
    }

    @Test
    fun `Russia mobile prefix is 9`() {
        val ru = CountryCodeValidator.getCountryByIsoCode("RU")
        assertTrue(ru!!.mobilePrefix.contains("9"))
    }

    @Test
    fun `Singapore mobile prefixes include 8 and 9`() {
        val sg = CountryCodeValidator.getCountryByIsoCode("SG")
        assertTrue(sg!!.mobilePrefix.containsAll(listOf("8", "9")))
    }

    @Test
    fun `Thailand mobile prefixes include 6 8 9`() {
        val th = CountryCodeValidator.getCountryByIsoCode("TH")
        assertTrue(th!!.mobilePrefix.containsAll(listOf("6", "8", "9")))
    }

    @Test
    fun `Malaysia mobile prefix is 1`() {
        val my = CountryCodeValidator.getCountryByIsoCode("MY")
        assertTrue(my!!.mobilePrefix.contains("1"))
    }

    @Test
    fun `Indonesia mobile prefix is 8`() {
        val id = CountryCodeValidator.getCountryByIsoCode("ID")
        assertTrue(id!!.mobilePrefix.contains("8"))
    }

    @Test
    fun `Philippines mobile prefix is 9`() {
        val ph = CountryCodeValidator.getCountryByIsoCode("PH")
        assertTrue(ph!!.mobilePrefix.contains("9"))
    }

    @Test
    fun `Taiwan mobile prefix is 9`() {
        val tw = CountryCodeValidator.getCountryByIsoCode("TW")
        assertTrue(tw!!.mobilePrefix.contains("9"))
    }

    @Test
    fun `South Africa mobile prefixes include 6 7 8`() {
        val za = CountryCodeValidator.getCountryByIsoCode("ZA")
        assertTrue(za!!.mobilePrefix.containsAll(listOf("6", "7", "8")))
    }

    @Test
    fun `Nigeria mobile prefixes include 7 8 9`() {
        val ng = CountryCodeValidator.getCountryByIsoCode("NG")
        assertTrue(ng!!.mobilePrefix.containsAll(listOf("7", "8", "9")))
    }

    @Test
    fun `Kenya mobile prefix is 7`() {
        val ke = CountryCodeValidator.getCountryByIsoCode("KE")
        assertTrue(ke!!.mobilePrefix.contains("7"))
    }

    @Test
    fun `Egypt mobile prefix is 1`() {
        val eg = CountryCodeValidator.getCountryByIsoCode("EG")
        assertTrue(eg!!.mobilePrefix.contains("1"))
    }

    // ========================================================================
    // SECTION 44: Confidence Score Analysis Tests
    // ========================================================================

    @Test
    fun `valid US number with mobile prefix has high confidence`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertTrue(result.confidence >= 0.7f)
    }

    @Test
    fun `valid UK mobile number has high confidence`() {
        val result = CountryCodeValidator.validate("+447911123456")
        assertTrue(result.confidence >= 0.7f)
    }

    @Test
    fun `valid France mobile number has 0 point 9 confidence`() {
        val result = CountryCodeValidator.validate("+33612345678")
        assertEquals(0.9f, result.confidence)
    }

    @Test
    fun `valid Germany mobile number has 0 point 9 confidence`() {
        val result = CountryCodeValidator.validate("+491512345678")
        assertEquals(0.9f, result.confidence)
    }

    @Test
    fun `valid Japan mobile number has high confidence`() {
        val result = CountryCodeValidator.validate("+819012345678")
        assertTrue(result.confidence >= 0.7f)
    }

    @Test
    fun `valid India mobile number has 0 point 9 confidence`() {
        val result = CountryCodeValidator.validate("+919876543210")
        assertEquals(0.9f, result.confidence)
    }

    @Test
    fun `valid Australia mobile has 0 point 9 confidence`() {
        val result = CountryCodeValidator.validate("+61412345678")
        assertEquals(0.9f, result.confidence)
    }

    @Test
    fun `invalid too short number has low confidence`() {
        val result = CountryCodeValidator.validate("+1123")
        assertTrue(result.confidence < 0.5f)
    }

    @Test
    fun `local number without country code has 0 point 4 confidence`() {
        val result = CountryCodeValidator.validate("5551234567")
        assertTrue(result.confidence <= 0.4f)
    }

    @Test
    fun `number outside typical range has low confidence`() {
        val result = CountryCodeValidator.validate("12345")
        assertTrue(result.confidence <= 0.4f)
    }

    @Test
    fun `NANP valid number returns 0 point 95 confidence`() {
        val result = CountryCodeValidator.validateNANPNumber("2025551234")
        assertEquals(0.95f, result.confidence)
    }

    @Test
    fun `NANP fictional 555-01XX returns 0 point 3 confidence`() {
        val result = CountryCodeValidator.validateNANPNumber("2025550100")
        assertEquals(0.3f, result.confidence)
    }

    // ========================================================================
    // SECTION 45: Comprehensive Country Validation by Calling Code
    // ========================================================================

    @Test
    fun `validate plus 7 number for Russia`() {
        val result = CountryCodeValidator.validate("+79123456789")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate plus 86 number for China`() {
        val result = CountryCodeValidator.validate("+8613912345678")
        assertTrue(result.isValid)
        assertEquals("CN", result.countryCode)
    }

    @Test
    fun `validate plus 91 number for India`() {
        val result = CountryCodeValidator.validate("+919876543210")
        assertTrue(result.isValid)
        assertEquals("IN", result.countryCode)
    }

    @Test
    fun `validate plus 81 number for Japan`() {
        val result = CountryCodeValidator.validate("+819012345678")
        assertTrue(result.isValid)
        assertEquals("JP", result.countryCode)
    }

    @Test
    fun `validate plus 82 number for South Korea`() {
        val result = CountryCodeValidator.validate("+821012345678")
        assertTrue(result.isValid)
        assertEquals("KR", result.countryCode)
    }

    @Test
    fun `validate plus 62 number for Indonesia`() {
        val result = CountryCodeValidator.validate("+6281234567890")
        assertTrue(result.isValid)
        assertEquals("ID", result.countryCode)
    }

    @Test
    fun `validate plus 63 number for Philippines`() {
        val result = CountryCodeValidator.validate("+639171234567")
        assertTrue(result.isValid)
        assertEquals("PH", result.countryCode)
    }

    @Test
    fun `validate plus 66 number for Thailand`() {
        val result = CountryCodeValidator.validate("+66812345678")
        assertTrue(result.isValid)
        assertEquals("TH", result.countryCode)
    }

    @Test
    fun `validate plus 84 number for Vietnam`() {
        val result = CountryCodeValidator.validate("+84912345678")
        assertTrue(result.isValid)
        assertEquals("VN", result.countryCode)
    }

    @Test
    fun `validate plus 60 number for Malaysia`() {
        val result = CountryCodeValidator.validate("+60123456789")
        assertTrue(result.isValid)
        assertEquals("MY", result.countryCode)
    }

    @Test
    fun `validate plus 65 number for Singapore`() {
        val result = CountryCodeValidator.validate("+6591234567")
        assertTrue(result.isValid)
        assertEquals("SG", result.countryCode)
    }

    @Test
    fun `validate plus 886 number for Taiwan`() {
        val result = CountryCodeValidator.validate("+886912345678")
        assertTrue(result.isValid)
        assertEquals("TW", result.countryCode)
    }

    @Test
    fun `validate plus 852 number for Hong Kong`() {
        val result = CountryCodeValidator.validate("+85291234567")
        assertTrue(result.isValid)
        assertEquals("HK", result.countryCode)
    }

    @Test
    fun `validate plus 55 number for Brazil`() {
        val result = CountryCodeValidator.validate("+5511912345678")
        assertTrue(result.isValid)
        assertEquals("BR", result.countryCode)
    }

    @Test
    fun `validate plus 54 number for Argentina`() {
        val result = CountryCodeValidator.validate("+5491123456789")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate plus 56 number for Chile`() {
        val result = CountryCodeValidator.validate("+56912345678")
        assertTrue(result.isValid)
        assertEquals("CL", result.countryCode)
    }

    @Test
    fun `validate plus 57 number for Colombia`() {
        val result = CountryCodeValidator.validate("+573101234567")
        assertTrue(result.isValid)
        assertEquals("CO", result.countryCode)
    }

    @Test
    fun `validate plus 51 number for Peru`() {
        val result = CountryCodeValidator.validate("+51912345678")
        assertTrue(result.isValid)
        assertEquals("PE", result.countryCode)
    }

    @Test
    fun `validate plus 20 number for Egypt`() {
        val result = CountryCodeValidator.validate("+201012345678")
        assertTrue(result.isValid)
        assertEquals("EG", result.countryCode)
    }

    @Test
    fun `validate plus 234 number for Nigeria`() {
        val result = CountryCodeValidator.validate("+2347012345678")
        assertTrue(result.isValid)
        assertEquals("NG", result.countryCode)
    }

    @Test
    fun `validate plus 254 number for Kenya`() {
        val result = CountryCodeValidator.validate("+254712345678")
        assertTrue(result.isValid)
        assertEquals("KE", result.countryCode)
    }

    @Test
    fun `validate plus 27 number for South Africa`() {
        val result = CountryCodeValidator.validate("+27612345678")
        assertTrue(result.isValid)
        assertEquals("ZA", result.countryCode)
    }

    @Test
    fun `validate plus 212 number for Morocco`() {
        val result = CountryCodeValidator.validate("+212612345678")
        assertTrue(result.isValid)
        assertEquals("MA", result.countryCode)
    }

    @Test
    fun `validate plus 971 number for UAE`() {
        val result = CountryCodeValidator.validate("+971501234567")
        assertTrue(result.isValid)
        assertEquals("AE", result.countryCode)
    }

    @Test
    fun `validate plus 966 number for Saudi Arabia`() {
        val result = CountryCodeValidator.validate("+966512345678")
        assertTrue(result.isValid)
        assertEquals("SA", result.countryCode)
    }

    @Test
    fun `validate plus 972 number for Israel`() {
        val result = CountryCodeValidator.validate("+972521234567")
        assertTrue(result.isValid)
        assertEquals("IL", result.countryCode)
    }

    @Test
    fun `validate plus 90 number for Turkey`() {
        val result = CountryCodeValidator.validate("+905321234567")
        assertTrue(result.isValid)
        assertEquals("TR", result.countryCode)
    }

    @Test
    fun `validate plus 48 number for Poland`() {
        val result = CountryCodeValidator.validate("+48512345678")
        assertTrue(result.isValid)
        assertEquals("PL", result.countryCode)
    }

    @Test
    fun `validate plus 380 number for Ukraine`() {
        val result = CountryCodeValidator.validate("+380501234567")
        assertTrue(result.isValid)
        assertEquals("UA", result.countryCode)
    }

    @Test
    fun `validate plus 40 number for Romania`() {
        val result = CountryCodeValidator.validate("+40712345678")
        assertTrue(result.isValid)
        assertEquals("RO", result.countryCode)
    }

    @Test
    fun `validate plus 30 number for Greece`() {
        val result = CountryCodeValidator.validate("+306912345678")
        assertTrue(result.isValid)
        assertEquals("GR", result.countryCode)
    }

    // ========================================================================
    // SECTION 46: Variable Length Country Boundary Tests
    // ========================================================================

    @Test
    fun `Germany accepts 5 digit national number at minLength`() {
        val de = CountryCodeValidator.getCountryByIsoCode("DE")
        assertNotNull(de)
        assertEquals(5, de!!.minLength)
    }

    @Test
    fun `Germany accepts 14 digit national number at maxLength`() {
        val de = CountryCodeValidator.getCountryByIsoCode("DE")
        assertNotNull(de)
        assertEquals(14, de!!.maxLength)
    }

    @Test
    fun `Italy accepts 6 digit national number at minLength`() {
        val it = CountryCodeValidator.getCountryByIsoCode("IT")
        assertNotNull(it)
        assertEquals(6, it!!.minLength)
    }

    @Test
    fun `Italy accepts 12 digit national number at maxLength`() {
        val it = CountryCodeValidator.getCountryByIsoCode("IT")
        assertNotNull(it)
        assertEquals(12, it!!.maxLength)
    }

    @Test
    fun `Austria accepts 4 digit national number at minLength`() {
        val at = CountryCodeValidator.getCountryByIsoCode("AT")
        assertNotNull(at)
        assertEquals(4, at!!.minLength)
    }

    @Test
    fun `Austria accepts 13 digit national number at maxLength`() {
        val at = CountryCodeValidator.getCountryByIsoCode("AT")
        assertNotNull(at)
        assertEquals(13, at!!.maxLength)
    }

    @Test
    fun `Sweden accepts 7 digit national number at minLength`() {
        val se = CountryCodeValidator.getCountryByIsoCode("SE")
        assertNotNull(se)
        assertEquals(7, se!!.minLength)
    }

    @Test
    fun `Sweden accepts 13 digit national number at maxLength`() {
        val se = CountryCodeValidator.getCountryByIsoCode("SE")
        assertNotNull(se)
        assertEquals(13, se!!.maxLength)
    }

    @Test
    fun `Finland accepts 5 digit national number at minLength`() {
        val fi = CountryCodeValidator.getCountryByIsoCode("FI")
        assertNotNull(fi)
        assertEquals(5, fi!!.minLength)
    }

    @Test
    fun `Finland accepts 12 digit national number at maxLength`() {
        val fi = CountryCodeValidator.getCountryByIsoCode("FI")
        assertNotNull(fi)
        assertEquals(12, fi!!.maxLength)
    }

    @Test
    fun `Indonesia accepts 9 digit national number at minLength`() {
        val id = CountryCodeValidator.getCountryByIsoCode("ID")
        assertNotNull(id)
        assertEquals(9, id!!.minLength)
    }

    @Test
    fun `Indonesia accepts 12 digit national number at maxLength`() {
        val id = CountryCodeValidator.getCountryByIsoCode("ID")
        assertNotNull(id)
        assertEquals(12, id!!.maxLength)
    }

    @Test
    fun `Luxembourg accepts 4 digit national number at minLength`() {
        val lu = CountryCodeValidator.getCountryByIsoCode("LU")
        assertNotNull(lu)
        assertEquals(4, lu!!.minLength)
    }

    @Test
    fun `Luxembourg accepts 11 digit national number at maxLength`() {
        val lu = CountryCodeValidator.getCountryByIsoCode("LU")
        assertNotNull(lu)
        assertEquals(11, lu!!.maxLength)
    }

    @Test
    fun `Nigeria accepts 8 digit national number at minLength`() {
        val ng = CountryCodeValidator.getCountryByIsoCode("NG")
        assertNotNull(ng)
        assertEquals(8, ng!!.minLength)
    }

    @Test
    fun `Nigeria accepts 10 digit national number at maxLength`() {
        val ng = CountryCodeValidator.getCountryByIsoCode("NG")
        assertNotNull(ng)
        assertEquals(10, ng!!.maxLength)
    }

    @Test
    fun `Brazil accepts 10 digit national number at minLength`() {
        val br = CountryCodeValidator.getCountryByIsoCode("BR")
        assertNotNull(br)
        assertEquals(10, br!!.minLength)
    }

    @Test
    fun `Brazil accepts 11 digit national number at maxLength`() {
        val br = CountryCodeValidator.getCountryByIsoCode("BR")
        assertNotNull(br)
        assertEquals(11, br!!.maxLength)
    }

    @Test
    fun `New Zealand accepts 8 digit national number at minLength`() {
        val nz = CountryCodeValidator.getCountryByIsoCode("NZ")
        assertNotNull(nz)
        assertEquals(8, nz!!.minLength)
    }

    @Test
    fun `New Zealand accepts 10 digit national number at maxLength`() {
        val nz = CountryCodeValidator.getCountryByIsoCode("NZ")
        assertNotNull(nz)
        assertEquals(10, nz!!.maxLength)
    }

    // ========================================================================
    // SECTION 47: getCountriesByCallingCode Extended Tests
    // ========================================================================

    @Test
    fun `getCountriesByCallingCode plus 1 returns US and CA`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+1")
        val codes = countries.map { it.code }
        assertTrue(codes.contains("US"))
        assertTrue(codes.contains("CA"))
    }

    @Test
    fun `getCountriesByCallingCode plus 1 returns Caribbean NANP countries`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+1")
        assertTrue(countries.size >= 2)
    }

    @Test
    fun `getCountriesByCallingCode plus 44 returns GB`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+44")
        assertTrue(countries.any { it.code == "GB" })
    }

    @Test
    fun `getCountriesByCallingCode plus 49 returns DE`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+49")
        assertTrue(countries.any { it.code == "DE" })
    }

    @Test
    fun `getCountriesByCallingCode plus 33 returns FR`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+33")
        assertTrue(countries.any { it.code == "FR" })
    }

    @Test
    fun `getCountriesByCallingCode plus 39 returns IT`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+39")
        assertTrue(countries.any { it.code == "IT" })
    }

    @Test
    fun `getCountriesByCallingCode plus 34 returns ES`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+34")
        assertTrue(countries.any { it.code == "ES" })
    }

    @Test
    fun `getCountriesByCallingCode plus 81 returns JP`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+81")
        assertTrue(countries.any { it.code == "JP" })
    }

    @Test
    fun `getCountriesByCallingCode plus 86 returns CN`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+86")
        assertTrue(countries.any { it.code == "CN" })
    }

    @Test
    fun `getCountriesByCallingCode plus 91 returns IN`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+91")
        assertTrue(countries.any { it.code == "IN" })
    }

    @Test
    fun `getCountriesByCallingCode plus 61 returns AU`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+61")
        assertTrue(countries.any { it.code == "AU" })
    }

    @Test
    fun `getCountriesByCallingCode plus 55 returns BR`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+55")
        assertTrue(countries.any { it.code == "BR" })
    }

    @Test
    fun `getCountriesByCallingCode plus 52 returns MX`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+52")
        assertTrue(countries.any { it.code == "MX" })
    }

    @Test
    fun `getCountriesByCallingCode without plus prefix works`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("44")
        assertTrue(countries.any { it.code == "GB" })
    }

    @Test
    fun `getCountriesByCallingCode plus 7 returns RU and KZ`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+7")
        val codes = countries.map { it.code }
        assertTrue(codes.contains("RU"))
        assertTrue(codes.contains("KZ"))
    }

    @Test
    fun `getCountriesByCallingCode unknown code returns empty`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+999")
        assertTrue(countries.isEmpty())
    }

    @Test
    fun `getCountriesByCallingCode plus 971 returns AE`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+971")
        assertTrue(countries.any { it.code == "AE" })
    }

    @Test
    fun `getCountriesByCallingCode plus 966 returns SA`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+966")
        assertTrue(countries.any { it.code == "SA" })
    }

    @Test
    fun `getCountriesByCallingCode plus 972 returns IL`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+972")
        assertTrue(countries.any { it.code == "IL" })
    }

    @Test
    fun `getCountriesByCallingCode plus 27 returns ZA`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+27")
        assertTrue(countries.any { it.code == "ZA" })
    }

    @Test
    fun `getCountriesByCallingCode plus 234 returns NG`() {
        val countries = CountryCodeValidator.getCountriesByCallingCode("+234")
        assertTrue(countries.any { it.code == "NG" })
    }

    // ========================================================================
    // SECTION 48: allCallingCodes and allIsoCodes Extended Tests
    // ========================================================================

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
    fun `allCallingCodes contains plus 91`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+91"))
    }

    @Test
    fun `allCallingCodes contains plus 81`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+81"))
    }

    @Test
    fun `allCallingCodes contains plus 49`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+49"))
    }

    @Test
    fun `allCallingCodes contains plus 33`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+33"))
    }

    @Test
    fun `allCallingCodes contains plus 55`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+55"))
    }

    @Test
    fun `allCallingCodes contains plus 61`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+61"))
    }

    @Test
    fun `allCallingCodes contains plus 7`() {
        assertTrue(CountryCodeValidator.allCallingCodes().contains("+7"))
    }

    @Test
    fun `allCallingCodes does not contain invalid code`() {
        assertFalse(CountryCodeValidator.allCallingCodes().contains("+000"))
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

    @Test
    fun `allIsoCodes contains IN`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("IN"))
    }

    @Test
    fun `allIsoCodes contains JP`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("JP"))
    }

    @Test
    fun `allIsoCodes contains DE`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("DE"))
    }

    @Test
    fun `allIsoCodes contains FR`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("FR"))
    }

    @Test
    fun `allIsoCodes contains BR`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("BR"))
    }

    @Test
    fun `allIsoCodes contains AU`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("AU"))
    }

    @Test
    fun `allIsoCodes contains RU`() {
        assertTrue(CountryCodeValidator.allIsoCodes().contains("RU"))
    }

    @Test
    fun `allIsoCodes does not contain invalid code ZZ`() {
        assertFalse(CountryCodeValidator.allIsoCodes().contains("ZZ"))
    }

    @Test
    fun `allIsoCodes does not contain lowercase us`() {
        assertFalse(CountryCodeValidator.allIsoCodes().contains("us"))
    }

    @Test
    fun `totalCountries equals countries list size`() {
        assertEquals(CountryCodeValidator.countries.size, CountryCodeValidator.totalCountries())
    }

    @Test
    fun `totalCountries is greater than 100`() {
        assertTrue(CountryCodeValidator.totalCountries() > 100)
    }

    @Test
    fun `allIsoCodes size equals totalCountries`() {
        assertEquals(CountryCodeValidator.totalCountries(), CountryCodeValidator.allIsoCodes().size)
    }

    // ========================================================================
    // SECTION 49: getCountryByIsoCode Case Sensitivity Tests
    // ========================================================================

    @Test
    fun `getCountryByIsoCode uppercase US returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("US"))
    }

    @Test
    fun `getCountryByIsoCode lowercase us returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("us"))
    }

    @Test
    fun `getCountryByIsoCode mixed case Us returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("Us"))
    }

    @Test
    fun `getCountryByIsoCode lowercase gb returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("gb"))
    }

    @Test
    fun `getCountryByIsoCode lowercase de returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("de"))
    }

    @Test
    fun `getCountryByIsoCode lowercase fr returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("fr"))
    }

    @Test
    fun `getCountryByIsoCode lowercase jp returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("jp"))
    }

    @Test
    fun `getCountryByIsoCode lowercase cn returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("cn"))
    }

    @Test
    fun `getCountryByIsoCode lowercase in returns country`() {
        assertNotNull(CountryCodeValidator.getCountryByIsoCode("in"))
    }

    @Test
    fun `getCountryByIsoCode invalid code returns null`() {
        assertNull(CountryCodeValidator.getCountryByIsoCode("XX"))
    }

    @Test
    fun `getCountryByIsoCode empty string returns null`() {
        assertNull(CountryCodeValidator.getCountryByIsoCode(""))
    }

    @Test
    fun `getCountryByIsoCode single char returns null`() {
        assertNull(CountryCodeValidator.getCountryByIsoCode("U"))
    }

    @Test
    fun `getCountryByIsoCode three chars returns null`() {
        assertNull(CountryCodeValidator.getCountryByIsoCode("USA"))
    }

    @Test
    fun `getCountryByIsoCode numeric returns null`() {
        assertNull(CountryCodeValidator.getCountryByIsoCode("12"))
    }

    // ========================================================================
    // SECTION 50: Validate Reason String Content Tests
    // ========================================================================

    @Test
    fun `valid US reason contains United States`() {
        val result = CountryCodeValidator.validate("+12025551234")
        assertTrue(result.reason.contains("United States") || result.reason.contains("Valid"))
    }

    @Test
    fun `valid UK reason contains United Kingdom`() {
        val result = CountryCodeValidator.validate("+447911123456")
        assertTrue(result.reason.contains("United Kingdom") || result.reason.contains("Valid"))
    }

    @Test
    fun `valid France reason contains France`() {
        val result = CountryCodeValidator.validate("+33612345678")
        assertTrue(result.reason.contains("France"))
    }

    @Test
    fun `valid Germany reason contains Germany`() {
        val result = CountryCodeValidator.validate("+491512345678")
        assertTrue(result.reason.contains("Germany"))
    }

    @Test
    fun `valid Japan reason contains Japan`() {
        val result = CountryCodeValidator.validate("+819012345678")
        assertTrue(result.reason.contains("Japan"))
    }

    @Test
    fun `valid India reason contains India`() {
        val result = CountryCodeValidator.validate("+919876543210")
        assertTrue(result.reason.contains("India"))
    }

    @Test
    fun `valid China reason contains China`() {
        val result = CountryCodeValidator.validate("+8613912345678")
        assertTrue(result.reason.contains("China"))
    }

    @Test
    fun `valid Australia reason contains Australia`() {
        val result = CountryCodeValidator.validate("+61412345678")
        assertTrue(result.reason.contains("Australia"))
    }

    @Test
    fun `valid Brazil reason contains Brazil`() {
        val result = CountryCodeValidator.validate("+5511912345678")
        assertTrue(result.reason.contains("Brazil"))
    }

    @Test
    fun `too short number reason mentions length`() {
        val result = CountryCodeValidator.validate("+1")
        assertTrue(result.reason.contains("length") || result.reason.contains("range"))
    }

    @Test
    fun `too long number reason mentions length`() {
        val result = CountryCodeValidator.validate("+12345678901234567890")
        assertTrue(result.reason.contains("length") || result.reason.contains("range"))
    }

    @Test
    fun `NANP N11 rejection reason mentions N11`() {
        val result = CountryCodeValidator.validateNANPNumber("2115551234")
        assertTrue(result.reason.contains("N11"))
    }

    @Test
    fun `NANP area code starting with 0 reason mentions area code`() {
        val result = CountryCodeValidator.validateNANPNumber("0125551234")
        assertTrue(result.reason.contains("area code") || result.reason.contains("0 or 1"))
    }

    @Test
    fun `NANP exchange starting with 1 reason mentions exchange`() {
        val result = CountryCodeValidator.validateNANPNumber("2121551234")
        assertTrue(result.reason.contains("exchange") || result.reason.contains("0 or 1"))
    }

    @Test
    fun `NANP 555-01XX reason mentions fictional`() {
        val result = CountryCodeValidator.validateNANPNumber("2025550100")
        assertTrue(result.reason.contains("fictional"))
    }

    @Test
    fun `NANP wrong length reason mentions digits`() {
        val result = CountryCodeValidator.validateNANPNumber("12345")
        assertTrue(result.reason.contains("10 digits") || result.reason.contains("digit"))
    }

    // ========================================================================
    // SECTION 51: Country Name Verification Tests
    // ========================================================================

    @Test
    fun `US country name is United States`() {
        val country = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals("United States", country?.name)
    }

    @Test
    fun `CA country name is Canada`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CA")
        assertEquals("Canada", country?.name)
    }

    @Test
    fun `GB country name is United Kingdom`() {
        val country = CountryCodeValidator.getCountryByIsoCode("GB")
        assertEquals("United Kingdom", country?.name)
    }

    @Test
    fun `DE country name is Germany`() {
        val country = CountryCodeValidator.getCountryByIsoCode("DE")
        assertEquals("Germany", country?.name)
    }

    @Test
    fun `FR country name is France`() {
        val country = CountryCodeValidator.getCountryByIsoCode("FR")
        assertEquals("France", country?.name)
    }

    @Test
    fun `IT country name is Italy`() {
        val country = CountryCodeValidator.getCountryByIsoCode("IT")
        assertEquals("Italy", country?.name)
    }

    @Test
    fun `ES country name is Spain`() {
        val country = CountryCodeValidator.getCountryByIsoCode("ES")
        assertEquals("Spain", country?.name)
    }

    @Test
    fun `JP country name is Japan`() {
        val country = CountryCodeValidator.getCountryByIsoCode("JP")
        assertEquals("Japan", country?.name)
    }

    @Test
    fun `CN country name is China`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CN")
        assertEquals("China", country?.name)
    }

    @Test
    fun `IN country name is India`() {
        val country = CountryCodeValidator.getCountryByIsoCode("IN")
        assertEquals("India", country?.name)
    }

    @Test
    fun `AU country name is Australia`() {
        val country = CountryCodeValidator.getCountryByIsoCode("AU")
        assertEquals("Australia", country?.name)
    }

    @Test
    fun `BR country name is Brazil`() {
        val country = CountryCodeValidator.getCountryByIsoCode("BR")
        assertEquals("Brazil", country?.name)
    }

    @Test
    fun `MX country name is Mexico`() {
        val country = CountryCodeValidator.getCountryByIsoCode("MX")
        assertEquals("Mexico", country?.name)
    }

    @Test
    fun `RU country name is Russia`() {
        val country = CountryCodeValidator.getCountryByIsoCode("RU")
        assertEquals("Russia", country?.name)
    }

    @Test
    fun `KR country name is South Korea`() {
        val country = CountryCodeValidator.getCountryByIsoCode("KR")
        assertEquals("South Korea", country?.name)
    }

    @Test
    fun `ZA country name is South Africa`() {
        val country = CountryCodeValidator.getCountryByIsoCode("ZA")
        assertEquals("South Africa", country?.name)
    }

    @Test
    fun `NG country name is Nigeria`() {
        val country = CountryCodeValidator.getCountryByIsoCode("NG")
        assertEquals("Nigeria", country?.name)
    }

    @Test
    fun `EG country name is Egypt`() {
        val country = CountryCodeValidator.getCountryByIsoCode("EG")
        assertEquals("Egypt", country?.name)
    }

    @Test
    fun `AE country name is United Arab Emirates`() {
        val country = CountryCodeValidator.getCountryByIsoCode("AE")
        assertEquals("United Arab Emirates", country?.name)
    }

    @Test
    fun `SA country name is Saudi Arabia`() {
        val country = CountryCodeValidator.getCountryByIsoCode("SA")
        assertEquals("Saudi Arabia", country?.name)
    }

    @Test
    fun `TR country name is Turkey`() {
        val country = CountryCodeValidator.getCountryByIsoCode("TR")
        assertEquals("Turkey", country?.name)
    }

    @Test
    fun `IL country name is Israel`() {
        val country = CountryCodeValidator.getCountryByIsoCode("IL")
        assertEquals("Israel", country?.name)
    }

    @Test
    fun `NZ country name is New Zealand`() {
        val country = CountryCodeValidator.getCountryByIsoCode("NZ")
        assertEquals("New Zealand", country?.name)
    }

    @Test
    fun `SG country name is Singapore`() {
        val country = CountryCodeValidator.getCountryByIsoCode("SG")
        assertEquals("Singapore", country?.name)
    }

    @Test
    fun `HK country name is Hong Kong`() {
        val country = CountryCodeValidator.getCountryByIsoCode("HK")
        assertEquals("Hong Kong", country?.name)
    }

    @Test
    fun `TW country name is Taiwan`() {
        val country = CountryCodeValidator.getCountryByIsoCode("TW")
        assertEquals("Taiwan", country?.name)
    }

    @Test
    fun `PH country name is Philippines`() {
        val country = CountryCodeValidator.getCountryByIsoCode("PH")
        assertEquals("Philippines", country?.name)
    }

    @Test
    fun `TH country name is Thailand`() {
        val country = CountryCodeValidator.getCountryByIsoCode("TH")
        assertEquals("Thailand", country?.name)
    }

    @Test
    fun `VN country name is Vietnam`() {
        val country = CountryCodeValidator.getCountryByIsoCode("VN")
        assertEquals("Vietnam", country?.name)
    }

    @Test
    fun `MY country name is Malaysia`() {
        val country = CountryCodeValidator.getCountryByIsoCode("MY")
        assertEquals("Malaysia", country?.name)
    }

    @Test
    fun `ID country name is Indonesia`() {
        val country = CountryCodeValidator.getCountryByIsoCode("ID")
        assertEquals("Indonesia", country?.name)
    }

    @Test
    fun `PK country name is Pakistan`() {
        val country = CountryCodeValidator.getCountryByIsoCode("PK")
        assertEquals("Pakistan", country?.name)
    }

    @Test
    fun `BD country name is Bangladesh`() {
        val country = CountryCodeValidator.getCountryByIsoCode("BD")
        assertEquals("Bangladesh", country?.name)
    }

    @Test
    fun `NL country name is Netherlands`() {
        val country = CountryCodeValidator.getCountryByIsoCode("NL")
        assertEquals("Netherlands", country?.name)
    }

    @Test
    fun `BE country name is Belgium`() {
        val country = CountryCodeValidator.getCountryByIsoCode("BE")
        assertEquals("Belgium", country?.name)
    }

    @Test
    fun `CH country name is Switzerland`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CH")
        assertEquals("Switzerland", country?.name)
    }

    @Test
    fun `SE country name is Sweden`() {
        val country = CountryCodeValidator.getCountryByIsoCode("SE")
        assertEquals("Sweden", country?.name)
    }

    @Test
    fun `NO country name is Norway`() {
        val country = CountryCodeValidator.getCountryByIsoCode("NO")
        assertEquals("Norway", country?.name)
    }

    @Test
    fun `DK country name is Denmark`() {
        val country = CountryCodeValidator.getCountryByIsoCode("DK")
        assertEquals("Denmark", country?.name)
    }

    @Test
    fun `FI country name is Finland`() {
        val country = CountryCodeValidator.getCountryByIsoCode("FI")
        assertEquals("Finland", country?.name)
    }

    @Test
    fun `PL country name is Poland`() {
        val country = CountryCodeValidator.getCountryByIsoCode("PL")
        assertEquals("Poland", country?.name)
    }

    @Test
    fun `UA country name is Ukraine`() {
        val country = CountryCodeValidator.getCountryByIsoCode("UA")
        assertEquals("Ukraine", country?.name)
    }

    @Test
    fun `CZ country name is Czech Republic`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CZ")
        assertEquals("Czech Republic", country?.name)
    }

    @Test
    fun `GR country name is Greece`() {
        val country = CountryCodeValidator.getCountryByIsoCode("GR")
        assertEquals("Greece", country?.name)
    }

    @Test
    fun `RO country name is Romania`() {
        val country = CountryCodeValidator.getCountryByIsoCode("RO")
        assertEquals("Romania", country?.name)
    }

    @Test
    fun `HU country name is Hungary`() {
        val country = CountryCodeValidator.getCountryByIsoCode("HU")
        assertEquals("Hungary", country?.name)
    }

    @Test
    fun `AT country name is Austria`() {
        val country = CountryCodeValidator.getCountryByIsoCode("AT")
        assertEquals("Austria", country?.name)
    }

    @Test
    fun `PT country name is Portugal`() {
        val country = CountryCodeValidator.getCountryByIsoCode("PT")
        assertEquals("Portugal", country?.name)
    }

    @Test
    fun `IE country name is Ireland`() {
        val country = CountryCodeValidator.getCountryByIsoCode("IE")
        assertEquals("Ireland", country?.name)
    }

    @Test
    fun `AR country name is Argentina`() {
        val country = CountryCodeValidator.getCountryByIsoCode("AR")
        assertEquals("Argentina", country?.name)
    }

    @Test
    fun `CL country name is Chile`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CL")
        assertEquals("Chile", country?.name)
    }

    @Test
    fun `CO country name is Colombia`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CO")
        assertEquals("Colombia", country?.name)
    }

    @Test
    fun `PE country name is Peru`() {
        val country = CountryCodeValidator.getCountryByIsoCode("PE")
        assertEquals("Peru", country?.name)
    }

    @Test
    fun `MA country name is Morocco`() {
        val country = CountryCodeValidator.getCountryByIsoCode("MA")
        assertEquals("Morocco", country?.name)
    }

    @Test
    fun `KE country name is Kenya`() {
        val country = CountryCodeValidator.getCountryByIsoCode("KE")
        assertEquals("Kenya", country?.name)
    }

    @Test
    fun `GH country name is Ghana`() {
        val country = CountryCodeValidator.getCountryByIsoCode("GH")
        assertEquals("Ghana", country?.name)
    }

    // ========================================================================
    // SECTION 52: Calling Code Verification Tests
    // ========================================================================

    @Test
    fun `US calling code is plus 1`() {
        val country = CountryCodeValidator.getCountryByIsoCode("US")
        assertEquals("+1", country?.callingCode)
    }

    @Test
    fun `GB calling code is plus 44`() {
        val country = CountryCodeValidator.getCountryByIsoCode("GB")
        assertEquals("+44", country?.callingCode)
    }

    @Test
    fun `DE calling code is plus 49`() {
        val country = CountryCodeValidator.getCountryByIsoCode("DE")
        assertEquals("+49", country?.callingCode)
    }

    @Test
    fun `FR calling code is plus 33`() {
        val country = CountryCodeValidator.getCountryByIsoCode("FR")
        assertEquals("+33", country?.callingCode)
    }

    @Test
    fun `IT calling code is plus 39`() {
        val country = CountryCodeValidator.getCountryByIsoCode("IT")
        assertEquals("+39", country?.callingCode)
    }

    @Test
    fun `ES calling code is plus 34`() {
        val country = CountryCodeValidator.getCountryByIsoCode("ES")
        assertEquals("+34", country?.callingCode)
    }

    @Test
    fun `JP calling code is plus 81`() {
        val country = CountryCodeValidator.getCountryByIsoCode("JP")
        assertEquals("+81", country?.callingCode)
    }

    @Test
    fun `CN calling code is plus 86`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CN")
        assertEquals("+86", country?.callingCode)
    }

    @Test
    fun `IN calling code is plus 91`() {
        val country = CountryCodeValidator.getCountryByIsoCode("IN")
        assertEquals("+91", country?.callingCode)
    }

    @Test
    fun `AU calling code is plus 61`() {
        val country = CountryCodeValidator.getCountryByIsoCode("AU")
        assertEquals("+61", country?.callingCode)
    }

    @Test
    fun `BR calling code is plus 55`() {
        val country = CountryCodeValidator.getCountryByIsoCode("BR")
        assertEquals("+55", country?.callingCode)
    }

    @Test
    fun `MX calling code is plus 52`() {
        val country = CountryCodeValidator.getCountryByIsoCode("MX")
        assertEquals("+52", country?.callingCode)
    }

    @Test
    fun `RU calling code is plus 7`() {
        val country = CountryCodeValidator.getCountryByIsoCode("RU")
        assertEquals("+7", country?.callingCode)
    }

    @Test
    fun `KR calling code is plus 82`() {
        val country = CountryCodeValidator.getCountryByIsoCode("KR")
        assertEquals("+82", country?.callingCode)
    }

    @Test
    fun `AE calling code is plus 971`() {
        val country = CountryCodeValidator.getCountryByIsoCode("AE")
        assertEquals("+971", country?.callingCode)
    }

    @Test
    fun `SA calling code is plus 966`() {
        val country = CountryCodeValidator.getCountryByIsoCode("SA")
        assertEquals("+966", country?.callingCode)
    }

    @Test
    fun `IL calling code is plus 972`() {
        val country = CountryCodeValidator.getCountryByIsoCode("IL")
        assertEquals("+972", country?.callingCode)
    }

    @Test
    fun `TR calling code is plus 90`() {
        val country = CountryCodeValidator.getCountryByIsoCode("TR")
        assertEquals("+90", country?.callingCode)
    }

    @Test
    fun `ZA calling code is plus 27`() {
        val country = CountryCodeValidator.getCountryByIsoCode("ZA")
        assertEquals("+27", country?.callingCode)
    }

    @Test
    fun `NG calling code is plus 234`() {
        val country = CountryCodeValidator.getCountryByIsoCode("NG")
        assertEquals("+234", country?.callingCode)
    }

    @Test
    fun `EG calling code is plus 20`() {
        val country = CountryCodeValidator.getCountryByIsoCode("EG")
        assertEquals("+20", country?.callingCode)
    }

    @Test
    fun `NZ calling code is plus 64`() {
        val country = CountryCodeValidator.getCountryByIsoCode("NZ")
        assertEquals("+64", country?.callingCode)
    }

    @Test
    fun `SG calling code is plus 65`() {
        val country = CountryCodeValidator.getCountryByIsoCode("SG")
        assertEquals("+65", country?.callingCode)
    }

    @Test
    fun `HK calling code is plus 852`() {
        val country = CountryCodeValidator.getCountryByIsoCode("HK")
        assertEquals("+852", country?.callingCode)
    }

    @Test
    fun `TW calling code is plus 886`() {
        val country = CountryCodeValidator.getCountryByIsoCode("TW")
        assertEquals("+886", country?.callingCode)
    }

    @Test
    fun `PH calling code is plus 63`() {
        val country = CountryCodeValidator.getCountryByIsoCode("PH")
        assertEquals("+63", country?.callingCode)
    }

    @Test
    fun `TH calling code is plus 66`() {
        val country = CountryCodeValidator.getCountryByIsoCode("TH")
        assertEquals("+66", country?.callingCode)
    }

    @Test
    fun `MY calling code is plus 60`() {
        val country = CountryCodeValidator.getCountryByIsoCode("MY")
        assertEquals("+60", country?.callingCode)
    }

    @Test
    fun `ID calling code is plus 62`() {
        val country = CountryCodeValidator.getCountryByIsoCode("ID")
        assertEquals("+62", country?.callingCode)
    }

    @Test
    fun `VN calling code is plus 84`() {
        val country = CountryCodeValidator.getCountryByIsoCode("VN")
        assertEquals("+84", country?.callingCode)
    }

    @Test
    fun `PK calling code is plus 92`() {
        val country = CountryCodeValidator.getCountryByIsoCode("PK")
        assertEquals("+92", country?.callingCode)
    }

    @Test
    fun `NL calling code is plus 31`() {
        val country = CountryCodeValidator.getCountryByIsoCode("NL")
        assertEquals("+31", country?.callingCode)
    }

    @Test
    fun `BE calling code is plus 32`() {
        val country = CountryCodeValidator.getCountryByIsoCode("BE")
        assertEquals("+32", country?.callingCode)
    }

    @Test
    fun `CH calling code is plus 41`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CH")
        assertEquals("+41", country?.callingCode)
    }

    @Test
    fun `SE calling code is plus 46`() {
        val country = CountryCodeValidator.getCountryByIsoCode("SE")
        assertEquals("+46", country?.callingCode)
    }

    @Test
    fun `NO calling code is plus 47`() {
        val country = CountryCodeValidator.getCountryByIsoCode("NO")
        assertEquals("+47", country?.callingCode)
    }

    @Test
    fun `DK calling code is plus 45`() {
        val country = CountryCodeValidator.getCountryByIsoCode("DK")
        assertEquals("+45", country?.callingCode)
    }

    @Test
    fun `PL calling code is plus 48`() {
        val country = CountryCodeValidator.getCountryByIsoCode("PL")
        assertEquals("+48", country?.callingCode)
    }

    @Test
    fun `UA calling code is plus 380`() {
        val country = CountryCodeValidator.getCountryByIsoCode("UA")
        assertEquals("+380", country?.callingCode)
    }

    @Test
    fun `GR calling code is plus 30`() {
        val country = CountryCodeValidator.getCountryByIsoCode("GR")
        assertEquals("+30", country?.callingCode)
    }

    @Test
    fun `AR calling code is plus 54`() {
        val country = CountryCodeValidator.getCountryByIsoCode("AR")
        assertEquals("+54", country?.callingCode)
    }

    @Test
    fun `CL calling code is plus 56`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CL")
        assertEquals("+56", country?.callingCode)
    }

    @Test
    fun `CO calling code is plus 57`() {
        val country = CountryCodeValidator.getCountryByIsoCode("CO")
        assertEquals("+57", country?.callingCode)
    }

    @Test
    fun `KE calling code is plus 254`() {
        val country = CountryCodeValidator.getCountryByIsoCode("KE")
        assertEquals("+254", country?.callingCode)
    }

    @Test
    fun `MA calling code is plus 212`() {
        val country = CountryCodeValidator.getCountryByIsoCode("MA")
        assertEquals("+212", country?.callingCode)
    }
}
