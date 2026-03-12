package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended Bank Account Validator Test Suite
 * Covers ABA routing numbers, IBAN validation, SWIFT/BIC codes, US account numbers,
 * bank account masking/extraction, and edge cases.
 */
class BankAccountValidatorExtendedTest {

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 – Valid ABA Routing Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `aba valid 001 021000021 JPMorgan Chase NY`() = assertTrue(BankAccountValidator.isValidABA("021000021"))
    @Test fun `aba valid 002 021200025 Citibank NY`() = assertTrue(BankAccountValidator.isValidABA("021200025"))
    @Test fun `aba valid 003 026009593 Bank of America NY`() = assertTrue(BankAccountValidator.isValidABA("026009593"))
    @Test fun `aba valid 004 021300077 HSBC Bank NY`() = assertTrue(BankAccountValidator.isValidABA("021300077"))
    @Test fun `aba valid 005 031100209 Wells Fargo PA`() = assertTrue(BankAccountValidator.isValidABA("031100209"))
    @Test fun `aba valid 006 044000037 JPMorgan Chase OH`() = assertTrue(BankAccountValidator.isValidABA("044000037"))
    @Test fun `aba valid 007 061000104 SunTrust Bank GA`() = assertTrue(BankAccountValidator.isValidABA("061000104"))
    @Test fun `aba valid 008 071000013 JPMorgan Chase IL`() = assertTrue(BankAccountValidator.isValidABA("071000013"))
    @Test fun `aba valid 009 091000019 US Bank MN`() = assertTrue(BankAccountValidator.isValidABA("091000019"))
    @Test fun `aba valid 010 111000025 Bank of America TX`() = assertTrue(BankAccountValidator.isValidABA("111000025"))
    @Test fun `aba valid 011 122000661 Bank of America CA`() = assertTrue(BankAccountValidator.isValidABA("122000661"))
    @Test fun `aba valid 012 325070760 Boeing Employees CU`() = assertTrue(BankAccountValidator.isValidABA("325070760"))
    @Test fun `aba valid 013 307070115 Colorado State Bank`() = assertTrue(BankAccountValidator.isValidABA("307070115"))
    @Test fun `aba valid 014 101200453 First National Bank KS`() = assertTrue(BankAccountValidator.isValidABA("101200453"))
    @Test fun `aba valid 015 053000219 BB&T NC`() = assertTrue(BankAccountValidator.isValidABA("053000219"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 – Invalid ABA Routing Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `aba invalid 001 000000000 all zeros`() = assertFalse(BankAccountValidator.isValidABA("000000000"))
    @Test fun `aba invalid 002 999999999 all nines`() = assertFalse(BankAccountValidator.isValidABA("999999999"))
    @Test fun `aba invalid 003 123456789 wrong checksum`() = assertFalse(BankAccountValidator.isValidABA("123456789"))
    @Test fun `aba invalid 004 12345678 too short`() = assertFalse(BankAccountValidator.isValidABA("12345678"))
    @Test fun `aba invalid 005 1234567890 too long`() = assertFalse(BankAccountValidator.isValidABA("1234567890"))
    @Test fun `aba invalid 006 empty string`() = assertFalse(BankAccountValidator.isValidABA(""))
    @Test fun `aba invalid 007 letters ABCDEFGHI`() = assertFalse(BankAccountValidator.isValidABA("ABCDEFGHI"))
    @Test fun `aba invalid 008 81 prefix invalid`() = assertFalse(BankAccountValidator.isValidABA("810000120"))
    @Test fun `aba invalid 009 90 prefix invalid`() = assertFalse(BankAccountValidator.isValidABA("900000000"))
    @Test fun `aba invalid 010 dashes in number`() = assertFalse(BankAccountValidator.isValidABA("021-000-021"))
    @Test fun `aba invalid 011 spaces around number`() = assertFalse(BankAccountValidator.isValidABA(" 021000021 "))
    @Test fun `aba invalid 012 null chars`() = assertFalse(BankAccountValidator.isValidABA("\u0000"))
    @Test fun `aba invalid 013 checksum off by one 021000022`() = assertFalse(BankAccountValidator.isValidABA("021000022"))
    @Test fun `aba invalid 014 all same digit 111111111`() = assertFalse(BankAccountValidator.isValidABA("111111111"))
    @Test fun `aba invalid 015 starts with 50 invalid prefix`() = assertFalse(BankAccountValidator.isValidABA("500000000"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 – ABA Checksum Verification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `aba checksum 001 021000021 checksum correct`() {
        val routing = "021000021"
        val d = routing.map { it.digitToInt() }
        val checksum = (3*(d[0]+d[3]+d[6]) + 7*(d[1]+d[4]+d[7]) + (d[2]+d[5]+d[8])) % 10
        assertEquals(0, checksum)
    }

    @Test fun `aba checksum 002 026009593 checksum correct`() {
        val routing = "026009593"
        val d = routing.map { it.digitToInt() }
        val checksum = (3*(d[0]+d[3]+d[6]) + 7*(d[1]+d[4]+d[7]) + (d[2]+d[5]+d[8])) % 10
        assertEquals(0, checksum)
    }

    @Test fun `aba checksum 003 driver function validates`() {
        val validRouting = listOf("021000021", "026009593", "044000037", "071000013", "121000248")
        validRouting.forEach { r ->
            val d = r.map { it.digitToInt() }
            val ok = (3*(d[0]+d[3]+d[6]) + 7*(d[1]+d[4]+d[7]) + (d[2]+d[5]+d[8])) % 10 == 0
            assertTrue("Checksum failed for $r", ok)
        }
    }

    @Test fun `aba checksum 004 invalid routing fails checksum`() {
        val invalidRouting = "021000022"
        val d = invalidRouting.map { it.digitToInt() }
        val checksum = (3*(d[0]+d[3]+d[6]) + 7*(d[1]+d[4]+d[7]) + (d[2]+d[5]+d[8])) % 10
        assertNotEquals(0, checksum)
    }

    @Test fun `aba checksum 005 validator uses aba checksum`() {
        assertTrue(BankAccountValidator.isValidABA("021000021"))
        assertFalse(BankAccountValidator.isValidABA("021000022"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 – IBAN Validation – Valid
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `iban valid 001 DE89370400440532013000 Germany`() = assertTrue(BankAccountValidator.isValidIBAN("DE89370400440532013000"))
    @Test fun `iban valid 002 GB29NWBK60161331926819 UK`() = assertTrue(BankAccountValidator.isValidIBAN("GB29NWBK60161331926819"))
    @Test fun `iban valid 003 FR7630006000011234567890189 France`() = assertTrue(BankAccountValidator.isValidIBAN("FR7630006000011234567890189"))
    @Test fun `iban valid 004 NL91ABNA0417164300 Netherlands`() = assertTrue(BankAccountValidator.isValidIBAN("NL91ABNA0417164300"))
    @Test fun `iban valid 005 BE68539007547034 Belgium`() = assertTrue(BankAccountValidator.isValidIBAN("BE68539007547034"))
    @Test fun `iban valid 006 ES9121000418450200051332 Spain`() = assertTrue(BankAccountValidator.isValidIBAN("ES9121000418450200051332"))
    @Test fun `iban valid 007 IT60X0542811101000000123456 Italy`() = assertTrue(BankAccountValidator.isValidIBAN("IT60X0542811101000000123456"))
    @Test fun `iban valid 008 CH9300762011623852957 Switzerland`() = assertTrue(BankAccountValidator.isValidIBAN("CH9300762011623852957"))
    @Test fun `iban valid 009 AT611904300234573201 Austria`() = assertTrue(BankAccountValidator.isValidIBAN("AT611904300234573201"))
    @Test fun `iban valid 010 SE4550000000058398257466 Sweden`() = assertTrue(BankAccountValidator.isValidIBAN("SE4550000000058398257466"))
    @Test fun `iban valid 011 NO9386011117947 Norway`() = assertTrue(BankAccountValidator.isValidIBAN("NO9386011117947"))
    @Test fun `iban valid 012 DK5000400440116243 Denmark`() = assertTrue(BankAccountValidator.isValidIBAN("DK5000400440116243"))
    @Test fun `iban valid 013 PL61109010140000071219812874 Poland`() = assertTrue(BankAccountValidator.isValidIBAN("PL61109010140000071219812874"))
    @Test fun `iban valid 014 PT50000201231234567890154 Portugal`() = assertTrue(BankAccountValidator.isValidIBAN("PT50000201231234567890154"))
    @Test fun `iban valid 015 FI2112345600000785 Finland`() = assertTrue(BankAccountValidator.isValidIBAN("FI2112345600000785"))
    @Test fun `iban valid 016 GR1601101250000000012300695 Greece`() = assertTrue(BankAccountValidator.isValidIBAN("GR1601101250000000012300695"))
    @Test fun `iban valid 017 LU280019400644750000 Luxembourg`() = assertTrue(BankAccountValidator.isValidIBAN("LU280019400644750000"))
    @Test fun `iban valid 018 CZ6508000000192000145399 Czech Republic`() = assertTrue(BankAccountValidator.isValidIBAN("CZ6508000000192000145399"))
    @Test fun `iban valid 019 HU42117730161111101800000000 Hungary`() = assertTrue(BankAccountValidator.isValidIBAN("HU42117730161111101800000000"))
    @Test fun `iban valid 020 RO49AAAA1B31007593840000 Romania`() = assertTrue(BankAccountValidator.isValidIBAN("RO49AAAA1B31007593840000"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 – IBAN Validation – Invalid
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `iban invalid 001 empty string`() = assertFalse(BankAccountValidator.isValidIBAN(""))
    @Test fun `iban invalid 002 too short`() = assertFalse(BankAccountValidator.isValidIBAN("DE89"))
    @Test fun `iban invalid 003 wrong check digits DE00`() = assertFalse(BankAccountValidator.isValidIBAN("DE00370400440532013000"))
    @Test fun `iban invalid 004 invalid country code XX`() = assertFalse(BankAccountValidator.isValidIBAN("XX89370400440532013000"))
    @Test fun `iban invalid 005 letters in numeric part`() = assertFalse(BankAccountValidator.isValidIBAN("DE89ABCDEF440532013000"))
    @Test fun `iban invalid 006 too long random`() = assertFalse(BankAccountValidator.isValidIBAN("DE89370400440532013000999999"))
    @Test fun `iban invalid 007 with spaces`() = assertFalse(BankAccountValidator.isValidIBAN("DE89 3704 0044 0532 0130 00"))
    @Test fun `iban invalid 008 with dashes`() = assertFalse(BankAccountValidator.isValidIBAN("DE89-3704-0044-0532-0130-00"))
    @Test fun `iban invalid 009 check digit 99`() = assertFalse(BankAccountValidator.isValidIBAN("GB99NWBK60161331926819"))
    @Test fun `iban invalid 010 all zeros`() = assertFalse(BankAccountValidator.isValidIBAN("DE00000000000000000000"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 – IBAN Normalization and Formatting
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `iban normalize 001 remove spaces`() {
        val normalized = BankAccountValidator.normalizeIBAN("DE89 3704 0044 0532 0130 00")
        assertEquals("DE89370400440532013000", normalized)
    }

    @Test fun `iban normalize 002 uppercase`() {
        val normalized = BankAccountValidator.normalizeIBAN("de89370400440532013000")
        assertEquals("DE89370400440532013000", normalized)
    }

    @Test fun `iban normalize 003 already normalized unchanged`() {
        val iban = "DE89370400440532013000"
        assertEquals(iban, BankAccountValidator.normalizeIBAN(iban))
    }

    @Test fun `iban format 001 print format groups of 4`() {
        val formatted = BankAccountValidator.formatIBAN("DE89370400440532013000")
        assertEquals("DE89 3704 0044 0532 0130 00", formatted)
    }

    @Test fun `iban format 002 uk iban formatted`() {
        val formatted = BankAccountValidator.formatIBAN("GB29NWBK60161331926819")
        assertEquals("GB29 NWBK 6016 1331 9268 19", formatted)
    }

    @Test fun `iban country 001 get country from DE iban`() {
        assertEquals("DE", BankAccountValidator.getIBANCountry("DE89370400440532013000"))
    }

    @Test fun `iban country 002 get country from GB iban`() {
        assertEquals("GB", BankAccountValidator.getIBANCountry("GB29NWBK60161331926819"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 – SWIFT/BIC Validation
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `swift valid 001 DEUTDEDB SWIFT 8 chars`() = assertTrue(BankAccountValidator.isValidSWIFT("DEUTDEDB"))
    @Test fun `swift valid 002 DEUTDEDBFRA SWIFT 11 chars`() = assertTrue(BankAccountValidator.isValidSWIFT("DEUTDEDBFRA"))
    @Test fun `swift valid 003 CHASUS33 JPMorgan`() = assertTrue(BankAccountValidator.isValidSWIFT("CHASUS33"))
    @Test fun `swift valid 004 BOFAUS3N BofA`() = assertTrue(BankAccountValidator.isValidSWIFT("BOFAUS3N"))
    @Test fun `swift valid 005 ICICINBB ICICI India`() = assertTrue(BankAccountValidator.isValidSWIFT("ICICINBB"))
    @Test fun `swift valid 006 CITIUS33 Citibank`() = assertTrue(BankAccountValidator.isValidSWIFT("CITIUS33"))
    @Test fun `swift valid 007 HSBCGB2L HSBC UK`() = assertTrue(BankAccountValidator.isValidSWIFT("HSBCGB2L"))
    @Test fun `swift valid 008 BNPAFRPP BNP France`() = assertTrue(BankAccountValidator.isValidSWIFT("BNPAFRPP"))
    @Test fun `swift valid 009 GEBABEBB ING Belgium`() = assertTrue(BankAccountValidator.isValidSWIFT("GEBABEBB"))
    @Test fun `swift valid 010 UNCRIT2B Unicredit Italy`() = assertTrue(BankAccountValidator.isValidSWIFT("UNCRIT2B"))

    @Test fun `swift invalid 001 too short 4 chars`() = assertFalse(BankAccountValidator.isValidSWIFT("DEUT"))
    @Test fun `swift invalid 002 too long 12 chars`() = assertFalse(BankAccountValidator.isValidSWIFT("DEUTDEDBFRAXX"))
    @Test fun `swift invalid 003 empty string`() = assertFalse(BankAccountValidator.isValidSWIFT(""))
    @Test fun `swift invalid 004 lowercase`() = assertFalse(BankAccountValidator.isValidSWIFT("deutdedb"))
    @Test fun `swift invalid 005 invalid country 11`() = assertFalse(BankAccountValidator.isValidSWIFT("DEUT11DB"))
    @Test fun `swift invalid 006 special chars`() = assertFalse(BankAccountValidator.isValidSWIFT("DEUT-DB!"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 – US Bank Account Number Validation
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `usaccount valid 001 8 digit account`() = assertTrue(BankAccountValidator.isValidUSAccountNumber("12345678"))
    @Test fun `usaccount valid 002 10 digit account`() = assertTrue(BankAccountValidator.isValidUSAccountNumber("1234567890"))
    @Test fun `usaccount valid 003 12 digit account`() = assertTrue(BankAccountValidator.isValidUSAccountNumber("123456789012"))
    @Test fun `usaccount valid 004 17 digit account max`() = assertTrue(BankAccountValidator.isValidUSAccountNumber("12345678901234567"))
    @Test fun `usaccount valid 005 6 digit account min`() = assertTrue(BankAccountValidator.isValidUSAccountNumber("123456"))
    @Test fun `usaccount valid 006 9 digit account`() = assertTrue(BankAccountValidator.isValidUSAccountNumber("123456789"))
    @Test fun `usaccount valid 007 11 digit account`() = assertTrue(BankAccountValidator.isValidUSAccountNumber("12345678901"))

    @Test fun `usaccount invalid 001 too short 5 digits`() = assertFalse(BankAccountValidator.isValidUSAccountNumber("12345"))
    @Test fun `usaccount invalid 002 too long 18 digits`() = assertFalse(BankAccountValidator.isValidUSAccountNumber("123456789012345678"))
    @Test fun `usaccount invalid 003 empty string`() = assertFalse(BankAccountValidator.isValidUSAccountNumber(""))
    @Test fun `usaccount invalid 004 with letters`() = assertFalse(BankAccountValidator.isValidUSAccountNumber("1234ABCD"))
    @Test fun `usaccount invalid 005 with dashes`() = assertFalse(BankAccountValidator.isValidUSAccountNumber("1234-5678"))
    @Test fun `usaccount invalid 006 with spaces`() = assertFalse(BankAccountValidator.isValidUSAccountNumber("1234 5678"))
    @Test fun `usaccount invalid 007 all zeros`() = assertFalse(BankAccountValidator.isValidUSAccountNumber("00000000"))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 9 – Masking Bank Account Numbers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `mask aba 001 shows last 4 digits`() {
        val masked = BankAccountValidator.maskABA("021000021")
        assertTrue(masked.endsWith("0021") || masked.endsWith("021"))
    }

    @Test fun `mask aba 002 hides first digits`() {
        val masked = BankAccountValidator.maskABA("021000021")
        assertFalse(masked.contains("021000"))
    }

    @Test fun `mask iban 001 shows last 4 chars`() {
        val masked = BankAccountValidator.maskIBAN("DE89370400440532013000")
        assertTrue(masked.takeLast(4) == "3000")
    }

    @Test fun `mask iban 002 country code preserved`() {
        val masked = BankAccountValidator.maskIBAN("DE89370400440532013000")
        assertTrue(masked.startsWith("DE") || masked.startsWith("****"))
    }

    @Test fun `mask account 001 shows last 4 of us account`() {
        val masked = BankAccountValidator.maskUSAccount("1234567890")
        assertTrue(masked.endsWith("7890"))
    }

    @Test fun `mask account 002 mask format uses asterisks`() {
        val masked = BankAccountValidator.maskUSAccount("1234567890")
        assertTrue(masked.contains("*") || masked.contains("X"))
    }

    @Test fun `mask account 003 full mask hides all`() {
        val masked = BankAccountValidator.maskFull("1234567890")
        assertFalse(masked.contains("1234"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 10 – Extraction from Text
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `extract aba 001 routing number in text`() {
        val text = "Please transfer funds using routing number 021000021"
        val results = BankAccountValidator.extractABAFromText(text)
        assertTrue(results.contains("021000021"))
    }

    @Test fun `extract aba 002 multiple routing numbers`() {
        val text = "Bank 1: 021000021 and Bank 2: 026009593"
        val results = BankAccountValidator.extractABAFromText(text)
        assertEquals(2, results.size)
    }

    @Test fun `extract aba 003 no routing in text`() {
        val text = "No routing numbers mentioned here"
        val results = BankAccountValidator.extractABAFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test fun `extract iban 001 iban in text`() {
        val text = "Please send to IBAN DE89370400440532013000 thank you"
        val results = BankAccountValidator.extractIBANFromText(text)
        assertTrue(results.contains("DE89370400440532013000"))
    }

    @Test fun `extract iban 002 multiple ibans in text`() {
        val text = "Sender: DE89370400440532013000 Receiver: GB29NWBK60161331926819"
        val results = BankAccountValidator.extractIBANFromText(text)
        assertEquals(2, results.size)
    }

    @Test fun `extract iban 003 no iban in text`() {
        val text = "No bank account information here at all"
        val results = BankAccountValidator.extractIBANFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test fun `extract iban 004 iban in json`() {
        val text = """{"iban": "DE89370400440532013000", "amount": 1000}"""
        val results = BankAccountValidator.extractIBANFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `extract iban 005 formatted iban with spaces`() {
        val text = "IBAN: DE89 3704 0044 0532 0130 00"
        val results = BankAccountValidator.extractIBANFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `extract swift 001 swift code in text`() {
        val text = "Bank SWIFT code: CHASUS33 for international transfers"
        val results = BankAccountValidator.extractSWIFTFromText(text)
        assertTrue(results.contains("CHASUS33"))
    }

    @Test fun `extract swift 002 multiple swift codes`() {
        val text = "Sender SWIFT: CHASUS33 Correspondent: DEUTDEDB"
        val results = BankAccountValidator.extractSWIFTFromText(text)
        assertEquals(2, results.size)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 11 – Risk Level Assessment
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `risk aba 001 valid aba high risk`() {
        val risk = BankAccountValidator.getABARiskLevel("021000021")
        assertNotNull(risk)
        assertTrue(risk.name == "HIGH" || risk.name == "CRITICAL")
    }

    @Test fun `risk aba 002 invalid aba no risk`() {
        val risk = BankAccountValidator.getABARiskLevel("000000000")
        assertTrue(risk.name == "NONE" || risk.name == "LOW")
    }

    @Test fun `risk iban 001 valid iban high risk`() {
        val risk = BankAccountValidator.getIBANRiskLevel("DE89370400440532013000")
        assertNotNull(risk)
        assertTrue(risk.name == "HIGH" || risk.name == "CRITICAL")
    }

    @Test fun `risk combined 001 text with routing and account high risk`() {
        val text = "Routing: 021000021, Account: 1234567890"
        val risk = BankAccountValidator.assessTextRisk(text)
        assertTrue(risk.name == "HIGH" || risk.name == "CRITICAL")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 12 – Text Contains Bank Info
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `contains 001 text with aba returns true`() = assertTrue(BankAccountValidator.textContainsBankInfo("Routing: 021000021"))
    @Test fun `contains 002 text with iban returns true`() = assertTrue(BankAccountValidator.textContainsBankInfo("IBAN: DE89370400440532013000"))
    @Test fun `contains 003 text with swift returns true`() = assertTrue(BankAccountValidator.textContainsBankInfo("SWIFT: CHASUS33"))
    @Test fun `contains 004 clean text returns false`() = assertFalse(BankAccountValidator.textContainsBankInfo("Nothing financial here"))
    @Test fun `contains 005 empty text returns false`() = assertFalse(BankAccountValidator.textContainsBankInfo(""))

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 13 – Edge Cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `edge 001 aba with leading zeros 021000021`() = assertTrue(BankAccountValidator.isValidABA("021000021"))
    @Test fun `edge 002 iban lowercase normalized`() = assertTrue(BankAccountValidator.isValidIBAN(BankAccountValidator.normalizeIBAN("de89370400440532013000")))
    @Test fun `edge 003 aba single digit repeated fails`() = assertFalse(BankAccountValidator.isValidABA("111111111"))
    @Test fun `edge 004 iban single char repeated fails`() = assertFalse(BankAccountValidator.isValidIBAN("AAAAAAAAAAAAAAAAAAAAAA"))
    @Test fun `edge 005 very long string aba`() = assertFalse(BankAccountValidator.isValidABA("0".repeat(50)))
    @Test fun `edge 006 very long string iban`() = assertFalse(BankAccountValidator.isValidIBAN("D".repeat(100)))
    @Test fun `edge 007 aba with null byte`() = assertFalse(BankAccountValidator.isValidABA("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000"))
    @Test fun `edge 008 iban with unicode`() = assertFalse(BankAccountValidator.isValidIBAN("DE\u0089\u0037\u0040\u0044\u0045\u0032\u0130\u0300"))
    @Test fun `edge 009 swift with numbers only`() = assertFalse(BankAccountValidator.isValidSWIFT("12345678"))
    @Test fun `edge 010 null-safe aba check`() {
        val result = runCatching { BankAccountValidator.isValidABA("") }
        assertTrue(result.isSuccess)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 14 – Batch Processing
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `batch 001 validate list of abas`() {
        val validABAs = listOf("021000021", "026009593", "044000037", "071000013", "122000661")
        val results = validABAs.map { BankAccountValidator.isValidABA(it) }
        assertTrue(results.all { it })
    }

    @Test fun `batch 002 validate list of ibans`() {
        val validIBANs = listOf(
            "DE89370400440532013000",
            "GB29NWBK60161331926819",
            "NL91ABNA0417164300",
            "BE68539007547034"
        )
        val results = validIBANs.map { BankAccountValidator.isValidIBAN(it) }
        assertTrue(results.all { it })
    }

    @Test fun `batch 003 extract all ibans from multi-line text`() {
        val text = buildString {
            appendLine("Wire 1: DE89370400440532013000")
            appendLine("Wire 2: GB29NWBK60161331926819")
            appendLine("Wire 3: NL91ABNA0417164300")
        }
        val results = BankAccountValidator.extractIBANFromText(text)
        assertTrue(results.size >= 3)
    }

    @Test fun `batch 004 mixed valid and invalid abas`() {
        val abas = listOf("021000021", "000000000", "026009593", "123456789")
        val validCount = abas.count { BankAccountValidator.isValidABA(it) }
        assertEquals(2, validCount)
    }

    @Test fun `batch 005 extract abas from financial document`() {
        val text = """
            WIRE TRANSFER INSTRUCTION
            From: Chase Bank 021000021
            To: Wells Fargo 121000248
            Amount: $1,500.00
        """.trimIndent()
        val results = BankAccountValidator.extractABAFromText(text)
        assertTrue(results.size >= 2)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 15 – Redaction
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `redact aba 001 aba redacted in text`() {
        val text = "Routing: 021000021"
        val redacted = BankAccountValidator.redactInText(text)
        assertFalse(redacted.contains("021000021"))
    }

    @Test fun `redact iban 001 iban redacted in text`() {
        val text = "IBAN: DE89370400440532013000"
        val redacted = BankAccountValidator.redactInText(text)
        assertFalse(redacted.contains("DE89370400440532013000"))
    }

    @Test fun `redact swift 001 swift redacted in text`() {
        val text = "SWIFT: CHASUS33"
        val redacted = BankAccountValidator.redactInText(text)
        assertFalse(redacted.contains("CHASUS33"))
    }

    @Test fun `redact 001 no bank info text unchanged`() {
        val text = "No bank information here"
        val redacted = BankAccountValidator.redactInText(text)
        assertEquals(text, redacted)
    }

    @Test fun `redact 002 redacted text has placeholder`() {
        val text = "Routing: 021000021"
        val redacted = BankAccountValidator.redactInText(text)
        assertTrue(redacted.contains("[ABA]") || redacted.contains("***") || redacted.contains("REDACTED"))
    }

    @Test fun `redact 003 preserve surrounding context`() {
        val text = "Sender name: John, Routing: 021000021, Amount: $500"
        val redacted = BankAccountValidator.redactInText(text)
        assertTrue(redacted.contains("Sender name: John"))
        assertTrue(redacted.contains("Amount: \$500"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 16 – IBAN Country Length Requirements
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `iban length 001 DE is 22 chars`() {
        val iban = "DE89370400440532013000"
        assertEquals(22, iban.length)
        assertTrue(BankAccountValidator.isValidIBAN(iban))
    }

    @Test fun `iban length 002 GB is 22 chars`() {
        val iban = "GB29NWBK60161331926819"
        assertEquals(22, iban.length)
        assertTrue(BankAccountValidator.isValidIBAN(iban))
    }

    @Test fun `iban length 003 NO is 15 chars`() {
        val iban = "NO9386011117947"
        assertEquals(15, iban.length)
        assertTrue(BankAccountValidator.isValidIBAN(iban))
    }

    @Test fun `iban length 004 wrong length for country fails`() {
        // DE should be 22, not 20
        assertFalse(BankAccountValidator.isValidIBAN("DE89370400440532013"))
    }

    @Test fun `iban length 005 expected length lookup`() {
        assertEquals(22, BankAccountValidator.expectedIBANLength("DE"))
        assertEquals(22, BankAccountValidator.expectedIBANLength("GB"))
        assertEquals(15, BankAccountValidator.expectedIBANLength("NO"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 17 – IBAN Checksum Verification (MOD-97)
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `iban mod97 001 DE89 passes mod97`() {
        assertTrue(BankAccountValidator.verifyIBANChecksum("DE89370400440532013000"))
    }

    @Test fun `iban mod97 002 GB29 passes mod97`() {
        assertTrue(BankAccountValidator.verifyIBANChecksum("GB29NWBK60161331926819"))
    }

    @Test fun `iban mod97 003 DE00 fails mod97`() {
        assertFalse(BankAccountValidator.verifyIBANChecksum("DE00370400440532013000"))
    }

    @Test fun `iban mod97 004 valid nl iban passes mod97`() {
        assertTrue(BankAccountValidator.verifyIBANChecksum("NL91ABNA0417164300"))
    }

    @Test fun `iban mod97 005 check digit 01 always invalid`() {
        assertFalse(BankAccountValidator.verifyIBANChecksum("DE01370400440532013000"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 18 – Account Type Classification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `classify 001 aba routing identified as routing`() {
        val type = BankAccountValidator.classifyBankNumber("021000021")
        assertEquals("ABA_ROUTING", type)
    }

    @Test fun `classify 002 iban identified as iban`() {
        val type = BankAccountValidator.classifyBankNumber("DE89370400440532013000")
        assertEquals("IBAN", type)
    }

    @Test fun `classify 003 swift identified as swift`() {
        val type = BankAccountValidator.classifyBankNumber("CHASUS33")
        assertEquals("SWIFT_BIC", type)
    }

    @Test fun `classify 004 us account number identified`() {
        val type = BankAccountValidator.classifyBankNumber("1234567890")
        assertEquals("US_ACCOUNT", type)
    }

    @Test fun `classify 005 unknown type`() {
        val type = BankAccountValidator.classifyBankNumber("NOTABANKNUMBER")
        assertEquals("UNKNOWN", type)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 19 – Stress/Performance
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `stress 001 validate 1000 abas no crash`() {
        val abas = listOf("021000021", "026009593", "044000037", "071000013", "122000661")
        repeat(200) {
            abas.forEach { aba -> BankAccountValidator.isValidABA(aba) }
        }
    }

    @Test fun `stress 002 validate 1000 ibans no crash`() {
        val ibans = listOf("DE89370400440532013000", "GB29NWBK60161331926819", "NL91ABNA0417164300")
        repeat(333) {
            ibans.forEach { iban -> BankAccountValidator.isValidIBAN(iban) }
        }
    }

    @Test fun `stress 003 extract from 10000 char text`() {
        val text = buildString {
            repeat(100) { append("Routing 021000021 for transfer. ") }
        }
        val results = BankAccountValidator.extractABAFromText(text)
        assertTrue(results.isNotEmpty())
    }

    @Test fun `stress 004 null aba check no throw`() {
        repeat(100) {
            assertFalse(BankAccountValidator.isValidABA(""))
        }
    }

    @Test fun `stress 005 batch validate 100 mixed`() {
        val numbers = (1..100).map { if (it % 2 == 0) "021000021" else "000000000" }
        val validCount = numbers.count { BankAccountValidator.isValidABA(it) }
        assertEquals(50, validCount)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 20 – isPII and Confidence
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `pii 001 valid aba is pii`() = assertTrue(BankAccountValidator.isPII("021000021"))
    @Test fun `pii 002 invalid aba not pii`() = assertFalse(BankAccountValidator.isPII("000000000"))
    @Test fun `pii 003 valid iban is pii`() = assertTrue(BankAccountValidator.isPII("DE89370400440532013000"))
    @Test fun `pii 004 empty string not pii`() = assertFalse(BankAccountValidator.isPII(""))
    @Test fun `pii 005 swift is pii`() = assertTrue(BankAccountValidator.isPII("CHASUS33"))

    @Test fun `confidence 001 valid aba high confidence`() {
        val score = BankAccountValidator.confidenceScore("021000021")
        assertTrue(score >= 0.9f)
    }

    @Test fun `confidence 002 invalid aba zero confidence`() {
        val score = BankAccountValidator.confidenceScore("000000000")
        assertEquals(0.0f, score, 0.01f)
    }

    @Test fun `confidence 003 score between 0 and 1`() {
        val score = BankAccountValidator.confidenceScore("021000021")
        assertTrue(score in 0.0f..1.0f)
    }

    @Test fun `confidence 004 iban context boosts confidence`() {
        val score = BankAccountValidator.confidenceScoreInContext("IBAN: DE89370400440532013000", "DE89370400440532013000")
        assertTrue(score >= 0.95f)
    }

    @Test fun `confidence 005 aba labeled context high confidence`() {
        val score = BankAccountValidator.confidenceScoreInContext("Routing: 021000021", "021000021")
        assertTrue(score >= 0.95f)
    }

} // end class BankAccountValidatorExtendedTest
