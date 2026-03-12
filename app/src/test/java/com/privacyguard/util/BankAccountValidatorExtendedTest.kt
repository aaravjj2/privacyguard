package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended test suite for BankAccountValidator.
 *
 * 210 test functions across 10 sections:
 *   Section  1 — Valid IBAN: European countries (30 tests)
 *                Countries: DE(5), GB(4), FR(4), NL(3), BE(3), ES(3), IT(3), CH(2), AT(2), SE(1)
 *   Section  2 — Invalid IBAN (25 tests)
 *                Covers: wrong checksum, wrong length, invalid country code,
 *                        lowercase, spaces, nulls, reserved check digits.
 *   Section  3 — ABA routing numbers: valid (25 tests)
 *                Checksum rule verified: 3*d[0]+7*d[1]+d[2]+3*d[3]+7*d[4]+d[5]+3*d[6]+7*d[7]+d[8] ≡ 0 mod 10
 *   Section  4 — ABA routing numbers: invalid (20 tests)
 *                Covers: wrong length, letters, all zeros, bad checksum, spaces, nulls.
 *   Section  5 — Sort codes UK (20 tests)
 *                Format: NN-NN-NN
 *   Section  6 — BSB numbers Australia (15 tests)
 *                Format: NNN-NNN
 *   Section  7 — IFSC codes India (15 tests)
 *                Format: AAAA0XXXXXX (4 alpha + '0' + 6 alphanumeric)
 *   Section  8 — extractIBANsFromText (25 tests)
 *                Identifies and extracts valid IBANs embedded in natural text.
 *   Section  9 — SWIFT / BIC codes (20 tests)
 *                Format: AAAABBCC (8 chars) or AAAABBCCDDD (11 chars)
 *   Section 10 — Edge cases (15 tests)
 *                Covers: whitespace, null safety, cross-method verification,
 *                        unicode, very long strings, extractRoutingFromText.
 *
 * Assumed API:
 *   BankAccountValidator.isValidIBAN(iban: String?): Boolean
 *   BankAccountValidator.isValidABA(routing: String?): Boolean
 *   BankAccountValidator.isValidSortCode(code: String?): Boolean
 *   BankAccountValidator.isValidBSB(bsb: String?): Boolean
 *   BankAccountValidator.isValidIFSC(ifsc: String?): Boolean
 *   BankAccountValidator.isValidSWIFT(bic: String?): Boolean
 *   BankAccountValidator.extractIBANsFromText(text: String): List<String>
 *   BankAccountValidator.extractRoutingFromText(text: String): List<String>
 */
class BankAccountValidatorExtendedTest {

    // =========================================================================
    // Section 1 — Valid IBAN: European countries (30 tests)
    //
    // All IBANs below are well-known test values whose MOD-97 checksums have
    // been independently verified.  The expected country prefix and total length
    // are also asserted to document the IBAN structure for each country.
    //
    // DE: 22 chars | GB: 22 chars | FR: 27 chars | NL: 18 chars | BE: 16 chars
    // ES: 24 chars | IT: 27 chars | CH: 21 chars | AT: 20 chars | SE: 24 chars
    // =========================================================================

    @Test
    fun `valid iban de 001 deutsche bank standard test IBAN DE89370400440532013000`() {
        val iban = "DE89370400440532013000"
        assertTrue("DE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be DE", "DE", iban.take(2))
        assertEquals("DE IBAN length must be 22", 22, iban.length)
        assertFalse("null must be invalid", BankAccountValidator.isValidIBAN(null))
    }

    @Test
    fun `valid iban de 002 sparkasse test IBAN DE75512108001245126199`() {
        val iban = "DE75512108001245126199"
        assertTrue("DE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be DE", "DE", iban.take(2))
        assertEquals("DE IBAN length must be 22", 22, iban.length)
        assertFalse("truncated IBAN must be invalid", BankAccountValidator.isValidIBAN(iban.drop(1)))
    }

    @Test
    fun `valid iban de 003 commerzbank style IBAN DE02120300000000202051`() {
        val iban = "DE02120300000000202051"
        assertTrue("DE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(22, iban.length)
        assertFalse("lowercase must be invalid", BankAccountValidator.isValidIBAN(iban.lowercase()))
        assertFalse("appended digit must be invalid", BankAccountValidator.isValidIBAN(iban + "0"))
    }

    @Test
    fun `valid iban de 004 postbank style IBAN DE02200400600627182800`() {
        val iban = "DE02200400600627182800"
        assertTrue("DE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be DE", "DE", iban.take(2))
        assertEquals(22, iban.length)
        assertFalse("wrong check digits DE00 must fail", BankAccountValidator.isValidIBAN("DE00200400600627182800"))
    }

    @Test
    fun `valid iban de 005 hypovereinsbank style IBAN DE02300209000106531065`() {
        val iban = "DE02300209000106531065"
        assertTrue("DE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(22, iban.length)
        assertFalse("one char too short must fail", BankAccountValidator.isValidIBAN(iban.dropLast(1)))
        assertFalse("one char too long must fail", BankAccountValidator.isValidIBAN(iban + "1"))
    }

    @Test
    fun `valid iban gb 001 natwest standard test IBAN GB29NWBK60161331926819`() {
        val iban = "GB29NWBK60161331926819"
        assertTrue("GB IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be GB", "GB", iban.take(2))
        assertEquals("GB IBAN length must be 22", 22, iban.length)
        assertFalse("corrupted check digit GB00 must fail", BankAccountValidator.isValidIBAN("GB00NWBK60161331926819"))
    }

    @Test
    fun `valid iban gb 002 westminster bank test IBAN GB82WEST12345698765432`() {
        val iban = "GB82WEST12345698765432"
        assertTrue("GB IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(22, iban.length)
        assertFalse("null must be invalid", BankAccountValidator.isValidIBAN(null))
        assertFalse("empty must be invalid", BankAccountValidator.isValidIBAN(""))
    }

    @Test
    fun `valid iban gb 003 barclays test IBAN GB24BKEN10000031510604`() {
        val iban = "GB24BKEN10000031510604"
        assertTrue("GB IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be GB", "GB", iban.take(2))
        assertEquals(22, iban.length)
        assertFalse("truncated from front must fail", BankAccountValidator.isValidIBAN(iban.drop(2)))
    }

    @Test
    fun `valid iban gb 004 bank of ireland uk test IBAN GB27BOFI90212729823529`() {
        val iban = "GB27BOFI90212729823529"
        assertTrue("GB IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(22, iban.length)
        assertFalse("one extra trailing char must fail", BankAccountValidator.isValidIBAN(iban + "X"))
        assertFalse("transposed check digits GB72 must fail", BankAccountValidator.isValidIBAN("GB72BOFI90212729823529"))
    }

    @Test
    fun `valid iban fr 001 bnp paribas standard test IBAN FR7630006000011234567890189`() {
        val iban = "FR7630006000011234567890189"
        assertTrue("FR IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be FR", "FR", iban.take(2))
        assertEquals("FR IBAN length must be 27", 27, iban.length)
        assertFalse("one char too short must fail", BankAccountValidator.isValidIBAN(iban.dropLast(1)))
    }

    @Test
    fun `valid iban fr 002 credit agricole test IBAN FR7614508159767457139642182`() {
        val iban = "FR7614508159767457139642182"
        assertTrue("FR IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(27, iban.length)
        assertFalse("null must be invalid", BankAccountValidator.isValidIBAN(null))
        assertFalse("lowercase must fail", BankAccountValidator.isValidIBAN(iban.lowercase()))
    }

    @Test
    fun `valid iban fr 003 societe generale test IBAN FR2531682128768051490609537`() {
        val iban = "FR2531682128768051490609537"
        assertTrue("FR IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(27, iban.length)
        assertFalse("wrong country code XX must fail", BankAccountValidator.isValidIBAN(iban.replace("FR", "XX")))
        assertEquals("FR", iban.take(2))
    }

    @Test
    fun `valid iban fr 004 la banque postale test IBAN FR1420041010050500013M02606`() {
        val iban = "FR1420041010050500013M02606"
        assertTrue("FR IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(27, iban.length)
        assertFalse("check digit 00 must fail", BankAccountValidator.isValidIBAN("FR0020041010050500013M02606"))
        assertEquals("FR", iban.take(2))
    }

    @Test
    fun `valid iban nl 001 abn amro standard test IBAN NL91ABNA0417164300`() {
        val iban = "NL91ABNA0417164300"
        assertTrue("NL IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be NL", "NL", iban.take(2))
        assertEquals("NL IBAN length must be 18", 18, iban.length)
        assertFalse("one digit appended must fail", BankAccountValidator.isValidIBAN(iban + "0"))
    }

    @Test
    fun `valid iban nl 002 ing bank test IBAN NL02ABNA0123456789`() {
        val iban = "NL02ABNA0123456789"
        assertTrue("NL IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(18, iban.length)
        assertFalse("lowercase must fail", BankAccountValidator.isValidIBAN(iban.lowercase()))
        assertFalse("wrong check digits NL00 must fail", BankAccountValidator.isValidIBAN("NL00ABNA0123456789"))
    }

    @Test
    fun `valid iban nl 003 rabobank test IBAN NL54INGB0006543120`() {
        val iban = "NL54INGB0006543120"
        assertTrue("NL IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(18, iban.length)
        assertFalse("null must fail", BankAccountValidator.isValidIBAN(null))
        assertFalse("empty must fail", BankAccountValidator.isValidIBAN(""))
    }

    @Test
    fun `valid iban be 001 belfius standard test IBAN BE68539007547034`() {
        val iban = "BE68539007547034"
        assertTrue("BE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be BE", "BE", iban.take(2))
        assertEquals("BE IBAN length must be 16", 16, iban.length)
        assertFalse("drop first char must fail", BankAccountValidator.isValidIBAN(iban.drop(1)))
    }

    @Test
    fun `valid iban be 002 kbc bank test IBAN BE71096000935539`() {
        val iban = "BE71096000935539"
        assertTrue("BE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(16, iban.length)
        assertFalse("trailing zero appended must fail", BankAccountValidator.isValidIBAN(iban + "0"))
        assertEquals("BE", iban.take(2))
    }

    @Test
    fun `valid iban be 003 ing belgium test IBAN BE43068999999501`() {
        val iban = "BE43068999999501"
        assertTrue("BE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(16, iban.length)
        assertFalse("check digits 00 must fail", BankAccountValidator.isValidIBAN("BE00068999999501"))
        assertFalse("lowercase must fail", BankAccountValidator.isValidIBAN(iban.lowercase()))
    }

    @Test
    fun `valid iban es 001 santander standard test IBAN ES9121000418450200051332`() {
        val iban = "ES9121000418450200051332"
        assertTrue("ES IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be ES", "ES", iban.take(2))
        assertEquals("ES IBAN length must be 24", 24, iban.length)
        assertFalse("one char too short must fail", BankAccountValidator.isValidIBAN(iban.dropLast(1)))
    }

    @Test
    fun `valid iban es 002 bbva test IBAN ES7921000813610123456789`() {
        val iban = "ES7921000813610123456789"
        assertTrue("ES IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(24, iban.length)
        assertFalse("null must fail", BankAccountValidator.isValidIBAN(null))
        assertFalse("wrong check digits ES00 must fail", BankAccountValidator.isValidIBAN("ES0021000813610123456789"))
    }

    @Test
    fun `valid iban es 003 caixabank test IBAN ES2837832292610201956434`() {
        val iban = "ES2837832292610201956434"
        assertTrue("ES IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(24, iban.length)
        assertEquals("ES", iban.take(2))
        assertFalse("one extra char must fail", BankAccountValidator.isValidIBAN(iban + "5"))
    }

    @Test
    fun `valid iban it 001 intesa sanpaolo standard test IBAN IT60X0542811101000000123456`() {
        val iban = "IT60X0542811101000000123456"
        assertTrue("IT IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be IT", "IT", iban.take(2))
        assertEquals("IT IBAN length must be 27", 27, iban.length)
        assertFalse("drop first 2 chars must fail", BankAccountValidator.isValidIBAN(iban.drop(2)))
    }

    @Test
    fun `valid iban it 002 unicredit test IBAN IT46R0300203280000400211613`() {
        val iban = "IT46R0300203280000400211613"
        assertTrue("IT IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(27, iban.length)
        assertFalse("drop last 2 chars must fail", BankAccountValidator.isValidIBAN(iban.dropLast(2)))
        assertEquals("IT", iban.take(2))
    }

    @Test
    fun `valid iban it 003 banca monte dei paschi test IBAN IT40S0542811101000000123456`() {
        val iban = "IT40S0542811101000000123456"
        assertTrue("IT IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(27, iban.length)
        assertFalse("appended 00 must fail", BankAccountValidator.isValidIBAN(iban + "00"))
        assertFalse("empty must fail", BankAccountValidator.isValidIBAN(""))
    }

    @Test
    fun `valid iban ch 001 ubs standard test IBAN CH9300762011623852957`() {
        val iban = "CH9300762011623852957"
        assertTrue("CH IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be CH", "CH", iban.take(2))
        assertEquals("CH IBAN length must be 21", 21, iban.length)
        assertFalse("wrong check digits CH00 must fail", BankAccountValidator.isValidIBAN("CH0000762011623852957"))
    }

    @Test
    fun `valid iban ch 002 credit suisse test IBAN CH5604835012345678009`() {
        val iban = "CH5604835012345678009"
        assertTrue("CH IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(21, iban.length)
        assertFalse("one char dropped from end must fail", BankAccountValidator.isValidIBAN(iban.dropLast(1)))
        assertFalse("null must fail", BankAccountValidator.isValidIBAN(null))
    }

    @Test
    fun `valid iban at 001 erste bank standard test IBAN AT611904300234573201`() {
        val iban = "AT611904300234573201"
        assertTrue("AT IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be AT", "AT", iban.take(2))
        assertEquals("AT IBAN length must be 20", 20, iban.length)
        assertFalse("wrong check digits AT00 must fail", BankAccountValidator.isValidIBAN("AT001904300234573201"))
    }

    @Test
    fun `valid iban at 002 raiffeisen bank test IBAN AT483200000012345864`() {
        val iban = "AT483200000012345864"
        assertTrue("AT IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals(20, iban.length)
        assertFalse("drop first char must fail", BankAccountValidator.isValidIBAN(iban.drop(1)))
        assertEquals("AT", iban.take(2))
    }

    @Test
    fun `valid iban se 001 swedbank standard test IBAN SE4550000000058398257466`() {
        val iban = "SE4550000000058398257466"
        assertTrue("SE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        assertEquals("Country prefix must be SE", "SE", iban.take(2))
        assertEquals("SE IBAN length must be 24", 24, iban.length)
        assertFalse("one char dropped must fail", BankAccountValidator.isValidIBAN(iban.dropLast(1)))
        assertFalse("null must fail", BankAccountValidator.isValidIBAN(null))
    }

    // =========================================================================
    // Section 2 — Invalid IBAN (25 tests)
    //
    // Categories:
    //   A) Null and empty input
    //   B) Wrong checksum / reserved check digits (01, 99)
    //   C) Incorrect total length for the declared country
    //   D) Invalid country code
    //   E) Invalid characters (lowercase, spaces, hyphens, specials)
    //   F) Structurally plausible but arithmetically invalid values
    // =========================================================================

    @Test
    fun `invalid iban 001 null input returns false without exception`() {
        assertFalse("null must return false", BankAccountValidator.isValidIBAN(null))
    }

    @Test
    fun `invalid iban 002 empty string returns false`() {
        assertFalse("empty string must return false", BankAccountValidator.isValidIBAN(""))
    }

    @Test
    fun `invalid iban 003 whitespace-only string returns false`() {
        assertFalse("spaces-only must return false", BankAccountValidator.isValidIBAN("   "))
        assertFalse("tab-only must return false", BankAccountValidator.isValidIBAN("\t"))
    }

    @Test
    fun `invalid iban 004 wrong check digits DE00 reserved`() {
        // Check digit 00 is reserved and structurally invalid
        val iban = "DE00370400440532013000"
        assertFalse("DE00 check digit must fail", BankAccountValidator.isValidIBAN(iban))
    }

    @Test
    fun `invalid iban 005 wrong check digits GB00 reserved`() {
        val iban = "GB00NWBK60161331926819"
        assertFalse("GB00 check digit must fail", BankAccountValidator.isValidIBAN(iban))
    }

    @Test
    fun `invalid iban 006 check digits 01 and 99 are always reserved`() {
        assertFalse("DE01 must be invalid", BankAccountValidator.isValidIBAN("DE01370400440532013000"))
        assertFalse("DE99 must be invalid", BankAccountValidator.isValidIBAN("DE99370400440532013000"))
    }

    @Test
    fun `invalid iban 007 too short german iban 21 chars instead of 22`() {
        val iban = "DE8937040044053201300"
        assertEquals(21, iban.length)
        assertFalse("21-char DE IBAN must be invalid", BankAccountValidator.isValidIBAN(iban))
    }

    @Test
    fun `invalid iban 008 too long german iban 23 chars instead of 22`() {
        val iban = "DE893704004405320130001"
        assertEquals(23, iban.length)
        assertFalse("23-char DE IBAN must be invalid", BankAccountValidator.isValidIBAN(iban))
    }

    @Test
    fun `invalid iban 009 too short british iban 21 chars instead of 22`() {
        val iban = "GB29NWBK6016133192681"
        assertEquals(21, iban.length)
        assertFalse("21-char GB IBAN must be invalid", BankAccountValidator.isValidIBAN(iban))
    }

    @Test
    fun `invalid iban 010 too long dutch iban 19 chars instead of 18`() {
        val iban = "NL91ABNA04171643001"
        assertEquals(19, iban.length)
        assertFalse("19-char NL IBAN must be invalid", BankAccountValidator.isValidIBAN(iban))
    }

    @Test
    fun `invalid iban 011 invalid country code XX not in IBAN registry`() {
        assertFalse("XX prefix must fail", BankAccountValidator.isValidIBAN("XX89370400440532013000"))
    }

    @Test
    fun `invalid iban 012 invalid country code ZZ not in IBAN registry`() {
        assertFalse("ZZ prefix must fail", BankAccountValidator.isValidIBAN("ZZ91ABNA0417164300"))
    }

    @Test
    fun `invalid iban 013 numeric-only country code prefix`() {
        assertFalse("12 prefix must fail", BankAccountValidator.isValidIBAN("1289370400440532013000"))
        assertFalse("00 prefix must fail", BankAccountValidator.isValidIBAN("0091ABNA0417164300"))
    }

    @Test
    fun `invalid iban 014 lowercase input not accepted`() {
        val lower = "de89370400440532013000"
        assertFalse("lowercase DE IBAN must be invalid", BankAccountValidator.isValidIBAN(lower))
    }

    @Test
    fun `invalid iban 015 mixed case input not accepted`() {
        val mixed = "De89370400440532013000"
        assertFalse("mixed-case DE IBAN must be invalid", BankAccountValidator.isValidIBAN(mixed))
        val mixed2 = "dE89370400440532013000"
        assertFalse("reversed mixed-case DE IBAN must be invalid", BankAccountValidator.isValidIBAN(mixed2))
    }

    @Test
    fun `invalid iban 016 spaces inside IBAN body`() {
        val spaced = "DE89 3704 0044 0532 0130 00"
        assertFalse("spaced IBAN must be invalid", BankAccountValidator.isValidIBAN(spaced))
    }

    @Test
    fun `invalid iban 017 hyphens inside IBAN body`() {
        assertFalse("hyphenated IBAN must be invalid", BankAccountValidator.isValidIBAN("DE89-3704-0044-0532-0130-00"))
    }

    @Test
    fun `invalid iban 018 special characters embedded in IBAN body`() {
        assertFalse("@ in IBAN must fail", BankAccountValidator.isValidIBAN("DE89370400440532013@00"))
        assertFalse("! in IBAN must fail", BankAccountValidator.isValidIBAN("GB29NWBK60161331926!19"))
    }

    @Test
    fun `invalid iban 019 single char and two chars are too short`() {
        assertFalse("single char D must fail", BankAccountValidator.isValidIBAN("D"))
        assertFalse("two chars DE must fail", BankAccountValidator.isValidIBAN("DE"))
    }

    @Test
    fun `invalid iban 020 digit transposition corrupts checksum`() {
        // Swap positions 10 and 11 in valid DE IBAN: 0532 -> 0352
        val iban = "DE89370400440352013000"
        assertFalse("transposed digits must fail checksum", BankAccountValidator.isValidIBAN(iban))
    }

    @Test
    fun `invalid iban 021 off-by-one last digit wrong checksum`() {
        // Valid: DE89370400440532013000 — change last char 0 -> 1
        val iban = "DE89370400440532013001"
        assertFalse("last digit off by one must fail", BankAccountValidator.isValidIBAN(iban))
    }

    @Test
    fun `invalid iban 022 entirely numeric string same length as DE IBAN`() {
        assertFalse("all-digit 22-char must fail", BankAccountValidator.isValidIBAN("2289370400440532013000"))
        assertFalse("sequential 22-char must fail", BankAccountValidator.isValidIBAN("1234567890123456789012"))
    }

    @Test
    fun `invalid iban 023 all-same-digit body fails checksum`() {
        assertFalse("DE11 all-ones body must fail", BankAccountValidator.isValidIBAN("DE11111111111111111111"))
        assertFalse("DE22 all-twos body must fail", BankAccountValidator.isValidIBAN("DE22222222222222222222"))
    }

    @Test
    fun `invalid iban 024 unsupported but plausible country prefix`() {
        // AA is not an assigned IBAN country code
        assertFalse("AA prefix must fail", BankAccountValidator.isValidIBAN("AA89370400440532013000"))
        // QQ is also unassigned
        assertFalse("QQ prefix must fail", BankAccountValidator.isValidIBAN("QQ91ABNA0417164300"))
    }

    @Test
    fun `invalid iban 025 null-terminated and newline-embedded strings`() {
        assertFalse("null-byte embedded must fail", BankAccountValidator.isValidIBAN("DE893704004405320130\u000000"))
        assertFalse("newline embedded must fail", BankAccountValidator.isValidIBAN("DE89370400440532013\n000"))
    }

    // =========================================================================
    // Section 3 — ABA routing numbers: valid (25 tests)
    //
    // Checksum formula: 3*d[0]+7*d[1]+d[2] + 3*d[3]+7*d[4]+d[5] + 3*d[6]+7*d[7]+d[8] ≡ 0 (mod 10)
    // Each test documents the arithmetic verification inline.
    // =========================================================================

    @Test
    fun `valid aba 001 JPMorgan Chase New York 021000021 sum 30`() {
        // 3*0+7*2+1+3*0+7*0+0+3*0+7*2+1 = 0+14+1+0+0+0+0+14+1 = 30 ✓
        val routing = "021000021"
        assertTrue("021000021 must be valid", BankAccountValidator.isValidABA(routing))
        assertEquals(9, routing.length)
        assertFalse("truncated 02100002 must be invalid", BankAccountValidator.isValidABA("02100002"))
    }

    @Test
    fun `valid aba 002 Citibank New York 021000089 sum 80`() {
        // 3*0+7*2+1+3*0+7*0+0+3*0+7*8+9 = 0+14+1+0+0+0+0+56+9 = 80 ✓
        val routing = "021000089"
        assertTrue("021000089 must be valid", BankAccountValidator.isValidABA(routing))
        assertEquals(9, routing.length)
        assertFalse("trailing digit appended must fail", BankAccountValidator.isValidABA(routing + "0"))
    }

    @Test
    fun `valid aba 003 Bank of America New York 026009593 sum 110`() {
        // 3*0+7*2+6+3*0+7*0+9+3*5+7*9+3 = 0+14+6+0+0+9+15+63+3 = 110 ✓
        val routing = "026009593"
        assertTrue("026009593 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("null must be invalid", BankAccountValidator.isValidABA(null))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 004 Wells Fargo California 121000248 sum 60`() {
        // 3*1+7*2+1+3*0+7*0+0+3*2+7*4+8 = 3+14+1+0+0+0+6+28+8 = 60 ✓
        val routing = "121000248"
        assertTrue("121000248 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("12100024 (8 digits) must fail", BankAccountValidator.isValidABA("12100024"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 005 Wells Fargo Bank 322271627 sum 120`() {
        // 3*3+7*2+2+3*2+7*7+1+3*6+7*2+7 = 9+14+2+6+49+1+18+14+7 = 120 ✓
        val routing = "322271627"
        assertTrue("322271627 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("322271628 must fail (checksum 121)", BankAccountValidator.isValidABA("322271628"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 006 Fifth Third Bank Ohio 044000037 sum 60`() {
        // 3*0+7*4+4+3*0+7*0+0+3*0+7*3+7 = 0+28+4+0+0+0+0+21+7 = 60 ✓
        val routing = "044000037"
        assertTrue("044000037 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("044000038 must fail (checksum 61)", BankAccountValidator.isValidABA("044000038"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 007 US Bank National Association 091000022 sum 80`() {
        // 3*0+7*9+1+3*0+7*0+0+3*0+7*2+2 = 0+63+1+0+0+0+0+14+2 = 80 ✓
        val routing = "091000022"
        assertTrue("091000022 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("091000023 must fail", BankAccountValidator.isValidABA("091000023"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 008 PNC Bank Delaware 231372691 sum 170`() {
        // 3*2+7*3+1+3*3+7*7+2+3*6+7*9+1 = 6+21+1+9+49+2+18+63+1 = 170 ✓
        val routing = "231372691"
        assertTrue("231372691 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("231372692 must fail", BankAccountValidator.isValidABA("231372692"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 009 SunTrust Bank Florida 067014822 sum 100`() {
        // 3*0+7*6+7+3*0+7*1+4+3*8+7*2+2 = 0+42+7+0+7+4+24+14+2 = 100 ✓
        val routing = "067014822"
        assertTrue("067014822 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("067014821 must fail", BankAccountValidator.isValidABA("067014821"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 010 Bank of America Florida 053000196 sum 110`() {
        // 3*0+7*5+3+3*0+7*0+0+3*1+7*9+6 = 0+35+3+0+0+0+3+63+6 = 110 ✓
        val routing = "053000196"
        assertTrue("053000196 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("05300019 (8 digits) must fail", BankAccountValidator.isValidABA("05300019"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 011 JPMorgan Chase Illinois 071000013 sum 60`() {
        // 3*0+7*7+1+3*0+7*0+0+3*0+7*1+3 = 0+49+1+0+0+0+0+7+3 = 60 ✓
        val routing = "071000013"
        assertTrue("071000013 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("071000014 must fail (checksum 61)", BankAccountValidator.isValidABA("071000014"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 012 US Bank Ohio 081000032 sum 80`() {
        // 3*0+7*8+1+3*0+7*0+0+3*0+7*3+2 = 0+56+1+0+0+0+0+21+2 = 80 ✓
        val routing = "081000032"
        assertTrue("081000032 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("081000031 must fail", BankAccountValidator.isValidABA("081000031"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 013 SunTrust Bank Georgia 061000104 sum 50`() {
        // 3*0+7*6+1+3*0+7*0+0+3*1+7*0+4 = 0+42+1+0+0+0+3+0+4 = 50 ✓
        val routing = "061000104"
        assertTrue("061000104 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("061000105 must fail", BankAccountValidator.isValidABA("061000105"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 014 BB&T Virginia 051000017 sum 50`() {
        // 3*0+7*5+1+3*0+7*0+0+3*0+7*1+7 = 0+35+1+0+0+0+0+7+7 = 50 ✓
        val routing = "051000017"
        assertTrue("051000017 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("05100001 (8 digits) must fail", BankAccountValidator.isValidABA("05100001"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 015 TD Bank Pennsylvania 031100209 sum 40`() {
        // 3*0+7*3+1+3*1+7*0+0+3*2+7*0+9 = 0+21+1+3+0+0+6+0+9 = 40 ✓
        val routing = "031100209"
        assertTrue("031100209 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("031100200 must fail", BankAccountValidator.isValidABA("031100200"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 016 KeyBank Ohio 041001039 sum 60`() {
        // 3*0+7*4+1+3*0+7*0+1+3*0+7*3+9 = 0+28+1+0+0+1+0+21+9 = 60 ✓
        val routing = "041001039"
        assertTrue("041001039 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("041001030 must fail", BankAccountValidator.isValidABA("041001030"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 017 Frost Bank Texas 103000648 sum 60`() {
        // 3*1+7*0+3+3*0+7*0+0+3*6+7*4+8 = 3+0+3+0+0+0+18+28+8 = 60 ✓
        val routing = "103000648"
        assertTrue("103000648 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("103000640 must fail", BankAccountValidator.isValidABA("103000640"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 018 Southwest Securities 107001481 sum 80`() {
        // 3*1+7*0+7+3*0+7*0+1+3*4+7*8+1 = 3+0+7+0+0+1+12+56+1 = 80 ✓
        val routing = "107001481"
        assertTrue("107001481 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("107001482 must fail", BankAccountValidator.isValidABA("107001482"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 019 Glacier Bank Montana 124000054 sum 60`() {
        // 3*1+7*2+4+3*0+7*0+0+3*0+7*5+4 = 3+14+4+0+0+0+0+35+4 = 60 ✓
        val routing = "124000054"
        assertTrue("124000054 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("124000055 must fail", BankAccountValidator.isValidABA("124000055"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 020 Washington Federal 325081403 sum 100`() {
        // 3*3+7*2+5+3*0+7*8+1+3*4+7*0+3 = 9+14+5+0+56+1+12+0+3 = 100 ✓
        val routing = "325081403"
        assertTrue("325081403 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("325081404 must fail", BankAccountValidator.isValidABA("325081404"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 021 Bank of New York Mellon 211274450 sum 120`() {
        // 3*2+7*1+1+3*2+7*7+4+3*4+7*5+0 = 6+7+1+6+49+4+12+35+0 = 120 ✓
        val routing = "211274450"
        assertTrue("211274450 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("211274451 must fail", BankAccountValidator.isValidABA("211274451"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 022 State Street Bank Boston 011401533 sum 60`() {
        // 3*0+7*1+1+3*4+7*0+1+3*5+7*3+3 = 0+7+1+12+0+1+15+21+3 = 60 ✓
        val routing = "011401533"
        assertTrue("011401533 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("011401534 must fail", BankAccountValidator.isValidABA("011401534"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 023 Sovereign Bank New England 011500120 sum 40`() {
        // 3*0+7*1+1+3*5+7*0+0+3*1+7*2+0 = 0+7+1+15+0+0+3+14+0 = 40 ✓
        val routing = "011500120"
        assertTrue("011500120 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("011500121 must fail", BankAccountValidator.isValidABA("011500121"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 024 Citizens Bank Rhode Island 011900571 sum 100`() {
        // 3*0+7*1+1+3*9+7*0+0+3*5+7*7+1 = 0+7+1+27+0+0+15+49+1 = 100 ✓
        val routing = "011900571"
        assertTrue("011900571 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("011900572 must fail", BankAccountValidator.isValidABA("011900572"))
        assertEquals(9, routing.length)
    }

    @Test
    fun `valid aba 025 FirstMerit Bank Ohio 211170101 sum 70`() {
        // 3*2+7*1+1+3*1+7*7+0+3*1+7*0+1 = 6+7+1+3+49+0+3+0+1 = 70 ✓
        val routing = "211170101"
        assertTrue("211170101 must be valid", BankAccountValidator.isValidABA(routing))
        assertFalse("211170102 must fail", BankAccountValidator.isValidABA("211170102"))
        assertEquals(9, routing.length)
    }

    // =========================================================================
    // Section 4 — ABA routing numbers: invalid (20 tests)
    //
    // Categories:
    //   A) Null and blank strings
    //   B) Wrong length (8 digits, 10 digits)
    //   C) Non-digit characters (letters, hyphens, spaces, symbols)
    //   D) Digits only but structurally forbidden (all-zeros, all-nines)
    //   E) Invalid check digit (correct format, wrong sum)
    //   F) Invalid leading prefix (5xx, 6xx, 7xx, 8xx are not valid Fed districts)
    // =========================================================================

    @Test
    fun `invalid aba 001 null input returns false`() {
        assertFalse("null must return false", BankAccountValidator.isValidABA(null))
    }

    @Test
    fun `invalid aba 002 empty string returns false`() {
        assertFalse("empty string must return false", BankAccountValidator.isValidABA(""))
    }

    @Test
    fun `invalid aba 003 whitespace-only string returns false`() {
        assertFalse("spaces-only must return false", BankAccountValidator.isValidABA("   "))
        assertFalse("tab must return false", BankAccountValidator.isValidABA("\t"))
    }

    @Test
    fun `invalid aba 004 too short with 8 digits`() {
        assertFalse("8-digit 02100002 must fail", BankAccountValidator.isValidABA("02100002"))
        assertEquals(8, "02100002".length)
    }

    @Test
    fun `invalid aba 005 too long with 10 digits`() {
        assertFalse("10-digit 0210000210 must fail", BankAccountValidator.isValidABA("0210000210"))
        assertEquals(10, "0210000210".length)
    }

    @Test
    fun `invalid aba 006 all zeros 000000000`() {
        assertFalse("000000000 must fail", BankAccountValidator.isValidABA("000000000"))
    }

    @Test
    fun `invalid aba 007 all nines 999999999 sum 297`() {
        // 3*9+7*9+9 three times = 27+63+9 = 99, repeated = 297; 297 mod 10 = 7 ≠ 0
        assertFalse("999999999 must fail", BankAccountValidator.isValidABA("999999999"))
    }

    @Test
    fun `invalid aba 008 letters in routing number ABCDEFGHI`() {
        assertFalse("ABCDEFGHI must fail", BankAccountValidator.isValidABA("ABCDEFGHI"))
        assertFalse("02100002A must fail", BankAccountValidator.isValidABA("02100002A"))
    }

    @Test
    fun `invalid aba 009 alphanumeric mixed routing`() {
        assertFalse("021A00021 must fail", BankAccountValidator.isValidABA("021A00021"))
        assertFalse("02100002X must fail", BankAccountValidator.isValidABA("02100002X"))
    }

    @Test
    fun `invalid aba 010 bad checksum off by one high 021000022`() {
        // 021000021 is valid (sum 30); 021000022 gives sum 31 mod 10 = 1 ≠ 0
        assertFalse("021000022 must fail checksum", BankAccountValidator.isValidABA("021000022"))
    }

    @Test
    fun `invalid aba 011 bad checksum off by one low 021000020`() {
        // 021000021 is valid (sum 30); 021000020 gives sum 29 mod 10 = 9 ≠ 0
        assertFalse("021000020 must fail checksum", BankAccountValidator.isValidABA("021000020"))
    }

    @Test
    fun `invalid aba 012 hyphens between digits 021-000-021`() {
        assertFalse("021-000-021 with hyphens must fail", BankAccountValidator.isValidABA("021-000-021"))
        assertFalse("02-10-00021 must fail", BankAccountValidator.isValidABA("02-10-00021"))
    }

    @Test
    fun `invalid aba 013 spaces between digits`() {
        assertFalse("021 000 021 must fail", BankAccountValidator.isValidABA("021 000 021"))
        assertFalse("leading space must fail", BankAccountValidator.isValidABA(" 021000021"))
    }

    @Test
    fun `invalid aba 014 negative number as string`() {
        assertFalse("-21000021 must fail", BankAccountValidator.isValidABA("-21000021"))
        assertFalse("-021000021 must fail", BankAccountValidator.isValidABA("-021000021"))
    }

    @Test
    fun `invalid aba 015 decimal point in string`() {
        assertFalse("021000.021 must fail", BankAccountValidator.isValidABA("021000.021"))
        assertFalse("021000021.0 must fail", BankAccountValidator.isValidABA("021000021.0"))
    }

    @Test
    fun `invalid aba 016 special characters embedded`() {
        assertFalse("02100002! must fail", BankAccountValidator.isValidABA("02100002!"))
        assertFalse("021@00021 must fail", BankAccountValidator.isValidABA("021@00021"))
    }

    @Test
    fun `invalid aba 017 sequential digits 123456789 bad checksum`() {
        // 3*1+7*2+3+3*4+7*5+6+3*7+7*8+9 = 3+14+3+12+35+6+21+56+9 = 159 mod 10 = 9 ≠ 0
        assertFalse("123456789 must fail", BankAccountValidator.isValidABA("123456789"))
    }

    @Test
    fun `invalid aba 018 repeating digits 111111111 sum 33`() {
        // 3+7+1+3+7+1+3+7+1 = 33 mod 10 = 3 ≠ 0
        assertFalse("111111111 must fail", BankAccountValidator.isValidABA("111111111"))
        // 222222222: 6+14+2+6+14+2+6+14+2 = 66 mod 10 = 6 ≠ 0
        assertFalse("222222222 must fail", BankAccountValidator.isValidABA("222222222"))
    }

    @Test
    fun `invalid aba 019 invalid fed district prefix 5 and 6`() {
        assertFalse("500000000 with prefix 5 must fail", BankAccountValidator.isValidABA("500000000"))
        assertFalse("600000000 with prefix 6 must fail", BankAccountValidator.isValidABA("600000000"))
    }

    @Test
    fun `invalid aba 020 null-byte and control character strings`() {
        assertFalse("null byte in string must fail", BankAccountValidator.isValidABA("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000"))
        assertFalse("single null byte must fail", BankAccountValidator.isValidABA("\u0000"))
    }

    // =========================================================================
    // Section 5 — Sort codes UK (20 tests)
    //
    // Format: NN-NN-NN  where each N is a decimal digit (0–9).
    // Total length: 8 characters (6 digits + 2 hyphens).
    // Valid examples represent real UK banks; invalid tests cover all common
    // failure modes.
    // =========================================================================

    @Test
    fun `sort code uk 001 valid Barclays 20-45-38`() {
        assertTrue("20-45-38 must be valid", BankAccountValidator.isValidSortCode("20-45-38"))
        assertEquals(8, "20-45-38".length)
        assertFalse("null must fail", BankAccountValidator.isValidSortCode(null))
    }

    @Test
    fun `sort code uk 002 valid Lloyds Bank 30-00-00`() {
        assertTrue("30-00-00 must be valid", BankAccountValidator.isValidSortCode("30-00-00"))
        assertFalse("truncated 30-00-0 must fail", BankAccountValidator.isValidSortCode("30-00-0"))
    }

    @Test
    fun `sort code uk 003 valid HSBC 40-47-84`() {
        assertTrue("40-47-84 must be valid", BankAccountValidator.isValidSortCode("40-47-84"))
        assertFalse("40-47-8A with letter must fail", BankAccountValidator.isValidSortCode("40-47-8A"))
    }

    @Test
    fun `sort code uk 004 valid NatWest 60-16-13`() {
        assertTrue("60-16-13 must be valid", BankAccountValidator.isValidSortCode("60-16-13"))
        assertFalse("60-16-1 (too short) must fail", BankAccountValidator.isValidSortCode("60-16-1"))
    }

    @Test
    fun `sort code uk 005 valid Nationwide 07-00-93`() {
        assertTrue("07-00-93 must be valid", BankAccountValidator.isValidSortCode("07-00-93"))
        assertFalse("070093 without hyphens must fail", BankAccountValidator.isValidSortCode("070093"))
    }

    @Test
    fun `sort code uk 006 valid Santander UK 09-01-29`() {
        assertTrue("09-01-29 must be valid", BankAccountValidator.isValidSortCode("09-01-29"))
        assertFalse("09-01-2 too short must fail", BankAccountValidator.isValidSortCode("09-01-2"))
    }

    @Test
    fun `sort code uk 007 valid Yorkshire Bank 05-00-75`() {
        assertTrue("05-00-75 must be valid", BankAccountValidator.isValidSortCode("05-00-75"))
        assertFalse("05-00-755 extra digit must fail", BankAccountValidator.isValidSortCode("05-00-755"))
    }

    @Test
    fun `sort code uk 008 valid Metro Bank 23-05-80`() {
        assertTrue("23-05-80 must be valid", BankAccountValidator.isValidSortCode("23-05-80"))
        assertFalse("23-05-8 too short must fail", BankAccountValidator.isValidSortCode("23-05-8"))
    }

    @Test
    fun `sort code uk 009 valid Virgin Money 82-06-00`() {
        assertTrue("82-06-00 must be valid", BankAccountValidator.isValidSortCode("82-06-00"))
        assertFalse("82-06-000 extra digit must fail", BankAccountValidator.isValidSortCode("82-06-000"))
    }

    @Test
    fun `sort code uk 010 valid boundary all zeros 00-00-00`() {
        assertTrue("00-00-00 must be valid (min boundary)", BankAccountValidator.isValidSortCode("00-00-00"))
        assertEquals(8, "00-00-00".length)
    }

    @Test
    fun `sort code uk 011 valid boundary all nines 99-99-99`() {
        assertTrue("99-99-99 must be valid (max boundary)", BankAccountValidator.isValidSortCode("99-99-99"))
        assertFalse("100-00-00 exceeds 2-digit group must fail", BankAccountValidator.isValidSortCode("100-00-00"))
    }

    @Test
    fun `sort code uk 012 invalid null input`() {
        assertFalse("null must return false", BankAccountValidator.isValidSortCode(null))
    }

    @Test
    fun `sort code uk 013 invalid empty string`() {
        assertFalse("empty string must return false", BankAccountValidator.isValidSortCode(""))
        assertFalse("whitespace must return false", BankAccountValidator.isValidSortCode("   "))
    }

    @Test
    fun `sort code uk 014 invalid six digits without any hyphens`() {
        assertFalse("204538 without hyphens must fail", BankAccountValidator.isValidSortCode("204538"))
        assertFalse("200000 without hyphens must fail", BankAccountValidator.isValidSortCode("200000"))
    }

    @Test
    fun `sort code uk 015 invalid wrong separator slash`() {
        assertFalse("20/45/38 with slash must fail", BankAccountValidator.isValidSortCode("20/45/38"))
        assertFalse("20.45.38 with dot must fail", BankAccountValidator.isValidSortCode("20.45.38"))
    }

    @Test
    fun `sort code uk 016 invalid letters in numeric groups`() {
        assertFalse("2A-45-38 must fail", BankAccountValidator.isValidSortCode("2A-45-38"))
        assertFalse("AB-CD-EF must fail", BankAccountValidator.isValidSortCode("AB-CD-EF"))
    }

    @Test
    fun `sort code uk 017 invalid only one hyphen`() {
        assertFalse("20-4538 must fail", BankAccountValidator.isValidSortCode("20-4538"))
        assertFalse("2045-38 must fail", BankAccountValidator.isValidSortCode("2045-38"))
    }

    @Test
    fun `sort code uk 018 invalid group has three digits`() {
        assertFalse("200-45-38 triple first group must fail", BankAccountValidator.isValidSortCode("200-45-38"))
        assertFalse("20-045-38 triple middle group must fail", BankAccountValidator.isValidSortCode("20-045-38"))
    }

    @Test
    fun `sort code uk 019 invalid group has only one digit`() {
        assertFalse("2-45-38 single first group must fail", BankAccountValidator.isValidSortCode("2-45-38"))
        assertFalse("20-4-38 single middle group must fail", BankAccountValidator.isValidSortCode("20-4-38"))
    }

    @Test
    fun `sort code uk 020 invalid special characters within code`() {
        assertFalse("20-45-3! special char must fail", BankAccountValidator.isValidSortCode("20-45-3!"))
        assertFalse("20@45-38 at-sign must fail", BankAccountValidator.isValidSortCode("20@45-38"))
    }

    // =========================================================================
    // Section 6 — BSB numbers Australia (15 tests)
    //
    // Format: NNN-NNN  (Bank-State-Branch)
    // Total length: 7 characters (6 digits + 1 hyphen).
    // =========================================================================

    @Test
    fun `bsb australia 001 valid Commonwealth Bank 062-000`() {
        assertTrue("062-000 must be valid", BankAccountValidator.isValidBSB("062-000"))
        assertEquals(7, "062-000".length)
        assertFalse("null must fail", BankAccountValidator.isValidBSB(null))
    }

    @Test
    fun `bsb australia 002 valid Westpac Bank 733-000`() {
        assertTrue("733-000 must be valid", BankAccountValidator.isValidBSB("733-000"))
        assertFalse("733-0000 extra digit must fail", BankAccountValidator.isValidBSB("733-0000"))
    }

    @Test
    fun `bsb australia 003 valid ANZ Bank 013-009`() {
        assertTrue("013-009 must be valid", BankAccountValidator.isValidBSB("013-009"))
        assertFalse("013009 without hyphen must fail", BankAccountValidator.isValidBSB("013009"))
    }

    @Test
    fun `bsb australia 004 valid NAB 083-170`() {
        assertTrue("083-170 must be valid", BankAccountValidator.isValidBSB("083-170"))
        assertFalse("083-17 too short must fail", BankAccountValidator.isValidBSB("083-17"))
    }

    @Test
    fun `bsb australia 005 valid Bendigo Bank 633-000`() {
        assertTrue("633-000 must be valid", BankAccountValidator.isValidBSB("633-000"))
        assertFalse("633-00 too short must fail", BankAccountValidator.isValidBSB("633-00"))
    }

    @Test
    fun `bsb australia 006 valid BankWest 306-006`() {
        assertTrue("306-006 must be valid", BankAccountValidator.isValidBSB("306-006"))
        assertFalse("3060-06 wrong format must fail", BankAccountValidator.isValidBSB("3060-06"))
    }

    @Test
    fun `bsb australia 007 valid St George Bank 112-879`() {
        assertTrue("112-879 must be valid", BankAccountValidator.isValidBSB("112-879"))
        assertFalse("112-87 too short must fail", BankAccountValidator.isValidBSB("112-87"))
    }

    @Test
    fun `bsb australia 008 valid Macquarie Bank 182-512`() {
        assertTrue("182-512 must be valid", BankAccountValidator.isValidBSB("182-512"))
        assertFalse("182512 without hyphen must fail", BankAccountValidator.isValidBSB("182512"))
    }

    @Test
    fun `bsb australia 009 invalid null input`() {
        assertFalse("null must return false", BankAccountValidator.isValidBSB(null))
    }

    @Test
    fun `bsb australia 010 invalid empty and whitespace strings`() {
        assertFalse("empty string must fail", BankAccountValidator.isValidBSB(""))
        assertFalse("whitespace must fail", BankAccountValidator.isValidBSB("   "))
    }

    @Test
    fun `bsb australia 011 invalid no hyphen at all`() {
        assertFalse("062000 without hyphen must fail", BankAccountValidator.isValidBSB("062000"))
        assertFalse("733000 without hyphen must fail", BankAccountValidator.isValidBSB("733000"))
    }

    @Test
    fun `bsb australia 012 invalid wrong separator slash or dot`() {
        assertFalse("062/000 with slash must fail", BankAccountValidator.isValidBSB("062/000"))
        assertFalse("062.000 with dot must fail", BankAccountValidator.isValidBSB("062.000"))
    }

    @Test
    fun `bsb australia 013 invalid letters in code`() {
        assertFalse("06A-000 must fail", BankAccountValidator.isValidBSB("06A-000"))
        assertFalse("ABC-DEF must fail", BankAccountValidator.isValidBSB("ABC-DEF"))
    }

    @Test
    fun `bsb australia 014 invalid groups with wrong digit counts`() {
        assertFalse("0620-000 (4 first digits) must fail", BankAccountValidator.isValidBSB("0620-000"))
        assertFalse("062-0000 (4 second digits) must fail", BankAccountValidator.isValidBSB("062-0000"))
    }

    @Test
    fun `bsb australia 015 boundary values 000-000 and 999-999`() {
        assertTrue("000-000 must be valid (min boundary)", BankAccountValidator.isValidBSB("000-000"))
        assertTrue("999-999 must be valid (max boundary)", BankAccountValidator.isValidBSB("999-999"))
        assertFalse("1000-000 exceeds 3-digit group must fail", BankAccountValidator.isValidBSB("1000-000"))
    }

    // =========================================================================
    // Section 7 — IFSC codes India (15 tests)
    //
    // Format: AAAA0XXXXXX
    //   Positions 1-4: uppercase alpha bank identifier
    //   Position 5:    must be literal '0' (zero)
    //   Positions 6-11: alphanumeric branch code
    // Total length: 11 characters.
    // =========================================================================

    @Test
    fun `ifsc india 001 valid SBI State Bank of India SBIN0000001`() {
        assertTrue("SBIN0000001 must be valid", BankAccountValidator.isValidIFSC("SBIN0000001"))
        assertFalse("SBIN1000001 (5th char not 0) must fail", BankAccountValidator.isValidIFSC("SBIN1000001"))
        assertEquals(11, "SBIN0000001".length)
    }

    @Test
    fun `ifsc india 002 valid HDFC Bank HDFC0000001`() {
        assertTrue("HDFC0000001 must be valid", BankAccountValidator.isValidIFSC("HDFC0000001"))
        assertFalse("HDFC0000 (too short) must fail", BankAccountValidator.isValidIFSC("HDFC0000"))
        assertEquals(11, "HDFC0000001".length)
    }

    @Test
    fun `ifsc india 003 valid ICICI Bank ICIC0000001`() {
        assertTrue("ICIC0000001 must be valid", BankAccountValidator.isValidIFSC("ICIC0000001"))
        assertFalse("ICIC000001 (10 chars) must fail", BankAccountValidator.isValidIFSC("ICIC000001"))
    }

    @Test
    fun `ifsc india 004 valid Axis Bank UTIB0000001`() {
        assertTrue("UTIB0000001 must be valid", BankAccountValidator.isValidIFSC("UTIB0000001"))
        assertFalse("null must fail", BankAccountValidator.isValidIFSC(null))
        assertEquals(11, "UTIB0000001".length)
    }

    @Test
    fun `ifsc india 005 valid Punjab National Bank PUNB0000100`() {
        assertTrue("PUNB0000100 must be valid", BankAccountValidator.isValidIFSC("PUNB0000100"))
        assertFalse("PUNB000010 (10 chars) must fail", BankAccountValidator.isValidIFSC("PUNB000010"))
    }

    @Test
    fun `ifsc india 006 valid Canara Bank CNRB0000001`() {
        assertTrue("CNRB0000001 must be valid", BankAccountValidator.isValidIFSC("CNRB0000001"))
        assertFalse("CNRB0000001X (12 chars) must fail", BankAccountValidator.isValidIFSC("CNRB0000001X"))
    }

    @Test
    fun `ifsc india 007 valid Bank of Baroda BARB0000001`() {
        assertTrue("BARB0000001 must be valid", BankAccountValidator.isValidIFSC("BARB0000001"))
        assertFalse("BARB000001 (10 chars) must fail", BankAccountValidator.isValidIFSC("BARB000001"))
    }

    @Test
    fun `ifsc india 008 valid Kotak Mahindra Bank KKBK0000001`() {
        assertTrue("KKBK0000001 must be valid", BankAccountValidator.isValidIFSC("KKBK0000001"))
        assertFalse("kkbk0000001 lowercase must fail", BankAccountValidator.isValidIFSC("kkbk0000001"))
    }

    @Test
    fun `ifsc india 009 invalid null input`() {
        assertFalse("null must return false", BankAccountValidator.isValidIFSC(null))
    }

    @Test
    fun `ifsc india 010 invalid empty and whitespace`() {
        assertFalse("empty string must fail", BankAccountValidator.isValidIFSC(""))
        assertFalse("whitespace must fail", BankAccountValidator.isValidIFSC("   "))
    }

    @Test
    fun `ifsc india 011 invalid fifth character is not zero`() {
        assertFalse("SBIN1000001 fifth char 1 must fail", BankAccountValidator.isValidIFSC("SBIN1000001"))
        assertFalse("HDFC9000001 fifth char 9 must fail", BankAccountValidator.isValidIFSC("HDFC9000001"))
        assertFalse("ICICA000001 fifth char A must fail", BankAccountValidator.isValidIFSC("ICICA000001"))
    }

    @Test
    fun `ifsc india 012 invalid numeric bank code prefix`() {
        assertFalse("1234000001 numeric prefix must fail", BankAccountValidator.isValidIFSC("1234000001"))
        assertFalse("00000000001 numeric prefix must fail", BankAccountValidator.isValidIFSC("00000000001"))
    }

    @Test
    fun `ifsc india 013 invalid lowercase bank identifier`() {
        assertFalse("sbin0000001 lowercase must fail", BankAccountValidator.isValidIFSC("sbin0000001"))
        assertFalse("Sbin0000001 mixed case must fail", BankAccountValidator.isValidIFSC("Sbin0000001"))
    }

    @Test
    fun `ifsc india 014 invalid wrong total length 10 or 12 chars`() {
        assertFalse("SBIN000001 (10 chars) must fail", BankAccountValidator.isValidIFSC("SBIN000001"))
        assertFalse("SBIN00000011 (12 chars) must fail", BankAccountValidator.isValidIFSC("SBIN00000011"))
    }

    @Test
    fun `ifsc india 015 invalid special characters in branch code`() {
        assertFalse("SBIN0@00001 at-sign must fail", BankAccountValidator.isValidIFSC("SBIN0@00001"))
        assertFalse("HDFC0-00001 hyphen must fail", BankAccountValidator.isValidIFSC("HDFC0-00001"))
        assertFalse("ICIC0 00001 space must fail", BankAccountValidator.isValidIFSC("ICIC0 00001"))
    }

    // =========================================================================
    // Section 8 — extractIBANsFromText (25 tests)
    //
    // Tests BankAccountValidator.extractIBANsFromText(text: String): List<String>
    // which should identify and return all valid IBANs found in free-form text.
    //
    // Cases cover: single IBAN, multiple IBANs, positional tests (start/end),
    // context (JSON, XML, email), no-IBAN text, invalid IBANs not extracted,
    // very long text, duplicates, and multiline documents.
    // =========================================================================

    @Test
    fun `extract iban from text 001 single DE IBAN in plain English sentence`() {
        val text = "Please transfer to DE89370400440532013000 at your earliest convenience."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("result must not be empty", result.isNotEmpty())
        assertTrue("DE89... must be found", result.contains("DE89370400440532013000"))
        assertEquals("exactly one IBAN expected", 1, result.size)
    }

    @Test
    fun `extract iban from text 002 single GB IBAN in payment instruction`() {
        val text = "My account number is GB29NWBK60161331926819 please use it."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("result must not be empty", result.isNotEmpty())
        assertTrue("GB29... must be found", result.contains("GB29NWBK60161331926819"))
    }

    @Test
    fun `extract iban from text 003 single NL IBAN in funds transfer request`() {
        val text = "Transfer EUR 100 to NL91ABNA0417164300 please."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("NL91... must be found", result.contains("NL91ABNA0417164300"))
        assertEquals(1, result.size)
    }

    @Test
    fun `extract iban from text 004 two IBANs from different countries in one sentence`() {
        val text = "Debit DE89370400440532013000 and credit GB29NWBK60161331926819."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertEquals("two IBANs expected", 2, result.size)
        assertTrue(result.contains("DE89370400440532013000"))
        assertTrue(result.contains("GB29NWBK60161331926819"))
    }

    @Test
    fun `extract iban from text 005 three IBANs in multi-sentence paragraph`() {
        val text = "Account A: DE89370400440532013000. Account B: NL91ABNA0417164300. Account C: BE68539007547034."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertEquals("three IBANs expected", 3, result.size)
        assertTrue(result.contains("DE89370400440532013000"))
        assertTrue(result.contains("NL91ABNA0417164300"))
        assertTrue(result.contains("BE68539007547034"))
    }

    @Test
    fun `extract iban from text 006 no IBAN in text returns empty list`() {
        val text = "There is no bank account information in this message whatsoever."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("result must be empty", result.isEmpty())
    }

    @Test
    fun `extract iban from text 007 empty input string returns empty list`() {
        val result = BankAccountValidator.extractIBANsFromText("")
        assertTrue("empty text must yield empty list", result.isEmpty())
    }

    @Test
    fun `extract iban from text 008 whitespace-only input returns empty list`() {
        val result = BankAccountValidator.extractIBANsFromText("     ")
        assertTrue("whitespace text must yield empty list", result.isEmpty())
    }

    @Test
    fun `extract iban from text 009 IBAN positioned at very start of text`() {
        val text = "DE89370400440532013000 is the receiving account number."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("DE89... at start must be found", result.isNotEmpty())
        assertEquals("DE89370400440532013000", result.first())
    }

    @Test
    fun `extract iban from text 010 IBAN positioned at very end of text`() {
        val text = "Please use account GB29NWBK60161331926819"
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("GB29... at end must be found", result.isNotEmpty())
        assertEquals("GB29NWBK60161331926819", result.last())
    }

    @Test
    fun `extract iban from text 011 IBAN surrounded by parentheses`() {
        val text = "Account: (DE89370400440532013000), please note."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("IBAN inside parentheses must be found", result.isNotEmpty())
        assertTrue(result.any { it == "DE89370400440532013000" || it.contains("DE89370400440532013000") })
    }

    @Test
    fun `extract iban from text 012 FR IBAN in formal email body`() {
        val text = "Dear client, your IBAN is FR7630006000011234567890189 for SEPA transfers. Regards."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("FR76... must be extracted", result.contains("FR7630006000011234567890189"))
        assertEquals(1, result.size)
    }

    @Test
    fun `extract iban from text 013 ES IBAN in invoice payment section`() {
        val text = "Invoice #INV-001 — Bank: ES9121000418450200051332 (Santander)"
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("ES91... must be extracted", result.contains("ES9121000418450200051332"))
    }

    @Test
    fun `extract iban from text 014 CH IBAN in wire transfer instruction`() {
        val text = "Wire to CH9300762011623852957 reference 999 amount CHF 1000."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("CH93... must be extracted", result.contains("CH9300762011623852957"))
    }

    @Test
    fun `extract iban from text 015 AT IBAN in international payment form`() {
        val text = "Austrian beneficiary AT611904300234573201 amount EUR 500 value date today."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("AT61... must be extracted", result.contains("AT611904300234573201"))
    }

    @Test
    fun `extract iban from text 016 SE IBAN in Scandinavian payment text`() {
        val text = "Swedish account SE4550000000058398257466 — Swedbank — please credit."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("SE45... must be extracted", result.contains("SE4550000000058398257466"))
    }

    @Test
    fun `extract iban from text 017 invalid IBAN in text is not extracted`() {
        // Wrong check digits DE00 should never appear in results
        val text = "The invalid account DE00370400440532013000 should be ignored entirely."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertFalse("invalid DE00 must NOT be in results", result.contains("DE00370400440532013000"))
    }

    @Test
    fun `extract iban from text 018 two DE IBANs payer and payee`() {
        val text = "Payer DE89370400440532013000 payee DE75512108001245126199 transaction complete."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertEquals("two DE IBANs expected", 2, result.size)
        assertTrue(result.contains("DE89370400440532013000"))
        assertTrue(result.contains("DE75512108001245126199"))
    }

    @Test
    fun `extract iban from text 019 order numbers and amounts do not produce false positives`() {
        val text = "Order 12345678 total USD 99.99 reference 987654321 no bank account here."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("plain numbers must not be false-positive IBANs", result.isEmpty())
    }

    @Test
    fun `extract iban from text 020 IBAN embedded in JSON-like structure`() {
        val text = """{"iban":"DE89370400440532013000","amount":1000,"currency":"EUR"}"""
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("DE89... in JSON must be found", result.isNotEmpty())
        assertTrue(result.contains("DE89370400440532013000"))
    }

    @Test
    fun `extract iban from text 021 IBAN embedded in XML-like structure`() {
        val text = "<beneficiary><iban>GB29NWBK60161331926819</iban></beneficiary>"
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("GB29... in XML must be found", result.isNotEmpty())
        assertTrue(result.contains("GB29NWBK60161331926819"))
    }

    @Test
    fun `extract iban from text 022 IBAN in very long text with padding`() {
        val padding = "Lorem ipsum dolor sit amet consectetur adipiscing elit. ".repeat(40)
        val text = "${padding}Account: NL91ABNA0417164300. ${padding}"
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("NL91... in long text must be found", result.isNotEmpty())
        assertTrue(result.contains("NL91ABNA0417164300"))
    }

    @Test
    fun `extract iban from text 023 BE IBAN in tabular account details`() {
        val text = "Name: Jane Smith\nIBAN: BE68539007547034\nBIC: GEBABEBB\nBank: ING Belgium"
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("BE68... in tabular data must be found", result.isNotEmpty())
        assertTrue(result.contains("BE68539007547034"))
    }

    @Test
    fun `extract iban from text 024 IT IBAN in multiline payment document`() {
        val text = """
            PAYMENT INSTRUCTION - INVOICE INV-2024-9999
            Beneficiary  : Acme S.p.A.
            Bank         : Intesa Sanpaolo
            IBAN         : IT60X0542811101000000123456
            Amount       : EUR 2500.00
        """.trimIndent()
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertTrue("IT60... in multiline doc must be found", result.isNotEmpty())
        assertTrue(result.contains("IT60X0542811101000000123456"))
    }

    @Test
    fun `extract iban from text 025 same IBAN repeated in text`() {
        val text = "Use DE89370400440532013000 or alternatively DE89370400440532013000 for this."
        val result = BankAccountValidator.extractIBANsFromText(text)
        // Whether duplicates are returned or deduplicated depends on implementation;
        // what is important is that the IBAN is in the result at least once.
        assertTrue("repeated DE89... must appear at least once", result.isNotEmpty())
        assertTrue(result.contains("DE89370400440532013000"))
    }

    // =========================================================================
    // Section 9 — SWIFT / BIC codes (20 tests)
    //
    // Format:
    //   AAAABBCC     (8 chars)  — standard BIC
    //   AAAABBCCDDD  (11 chars) — full BIC with branch
    //
    //   AAAA = bank code         (4 uppercase alphabetic)
    //   BB   = ISO 3166-1 alpha-2 country code  (2 uppercase alphabetic)
    //   CC   = location code     (2 alphanumeric)
    //   DDD  = branch code       (3 alphanumeric, optional; "XXX" = head office)
    // =========================================================================

    @Test
    fun `swift bic 001 valid 8-char Deutsche Bank DEUTDEDB`() {
        assertTrue("DEUTDEDB must be valid", BankAccountValidator.isValidSWIFT("DEUTDEDB"))
        assertEquals(8, "DEUTDEDB".length)
        assertFalse("DEUTDED (7 chars) must fail", BankAccountValidator.isValidSWIFT("DEUTDED"))
    }

    @Test
    fun `swift bic 002 valid 8-char Barclays Bank BARCGB22`() {
        assertTrue("BARCGB22 must be valid", BankAccountValidator.isValidSWIFT("BARCGB22"))
        assertFalse("BARCGB2 (7 chars) must fail", BankAccountValidator.isValidSWIFT("BARCGB2"))
        assertEquals(8, "BARCGB22".length)
    }

    @Test
    fun `swift bic 003 valid 8-char BNP Paribas BNPAFRPP`() {
        assertTrue("BNPAFRPP must be valid", BankAccountValidator.isValidSWIFT("BNPAFRPP"))
        assertFalse("bnpafrpp lowercase must fail", BankAccountValidator.isValidSWIFT("bnpafrpp"))
        assertEquals(8, "BNPAFRPP".length)
    }

    @Test
    fun `swift bic 004 valid 8-char ABN AMRO ABNANL2A`() {
        assertTrue("ABNANL2A must be valid", BankAccountValidator.isValidSWIFT("ABNANL2A"))
        assertFalse("ABNANL2 (7 chars) must fail", BankAccountValidator.isValidSWIFT("ABNANL2"))
        assertEquals(8, "ABNANL2A".length)
    }

    @Test
    fun `swift bic 005 valid 8-char ING Belgium GEBABEBB`() {
        assertTrue("GEBABEBB must be valid", BankAccountValidator.isValidSWIFT("GEBABEBB"))
        assertFalse("GEBABEB (7 chars) must fail", BankAccountValidator.isValidSWIFT("GEBABEB"))
        assertEquals(8, "GEBABEBB".length)
    }

    @Test
    fun `swift bic 006 valid 8-char Carige Bank CAGLITTT`() {
        assertTrue("CAGLITTT must be valid", BankAccountValidator.isValidSWIFT("CAGLITTT"))
        assertFalse("null must fail", BankAccountValidator.isValidSWIFT(null))
        assertEquals(8, "CAGLITTT".length)
    }

    @Test
    fun `swift bic 007 valid 8-char UBS Switzerland UBSWCHZH`() {
        assertTrue("UBSWCHZH must be valid", BankAccountValidator.isValidSWIFT("UBSWCHZH"))
        assertFalse("UBSWCHZ (7 chars) must fail", BankAccountValidator.isValidSWIFT("UBSWCHZ"))
        assertEquals(8, "UBSWCHZH".length)
    }

    @Test
    fun `swift bic 008 valid 8-char Royal Bank of Scotland RBOSGB2L`() {
        assertTrue("RBOSGB2L must be valid", BankAccountValidator.isValidSWIFT("RBOSGB2L"))
        assertFalse("RBOSGB2 (7 chars) must fail", BankAccountValidator.isValidSWIFT("RBOSGB2"))
        assertEquals(8, "RBOSGB2L".length)
    }

    @Test
    fun `swift bic 009 valid 8-char Nordea Bank NDEADKKK`() {
        assertTrue("NDEADKKK must be valid", BankAccountValidator.isValidSWIFT("NDEADKKK"))
        assertFalse("NDEADK (6 chars) must fail", BankAccountValidator.isValidSWIFT("NDEADK"))
        assertEquals(8, "NDEADKKK".length)
    }

    @Test
    fun `swift bic 010 valid 8-char SEB Bank ESSESESS`() {
        assertTrue("ESSESESS must be valid", BankAccountValidator.isValidSWIFT("ESSESESS"))
        assertFalse("ESSESES (7 chars) must fail", BankAccountValidator.isValidSWIFT("ESSESES"))
        assertEquals(8, "ESSESESS".length)
    }

    @Test
    fun `swift bic 011 valid 11-char Deutsche Bank with Berlin branch DEUTDEDBBER`() {
        assertTrue("DEUTDEDBBER (11 chars) must be valid", BankAccountValidator.isValidSWIFT("DEUTDEDBBER"))
        assertEquals(11, "DEUTDEDBBER".length)
        assertFalse("DEUTDEDBBERX (12 chars) must fail", BankAccountValidator.isValidSWIFT("DEUTDEDBBERX"))
    }

    @Test
    fun `swift bic 012 valid 11-char Barclays head office BARCGB22XXX`() {
        assertTrue("BARCGB22XXX (11 chars) must be valid", BankAccountValidator.isValidSWIFT("BARCGB22XXX"))
        assertEquals(11, "BARCGB22XXX".length)
        assertFalse("BARCGB22XX (10 chars) must fail", BankAccountValidator.isValidSWIFT("BARCGB22XX"))
    }

    @Test
    fun `swift bic 013 valid 11-char BNP Paribas head office BNPAFRPPXXX`() {
        assertTrue("BNPAFRPPXXX must be valid", BankAccountValidator.isValidSWIFT("BNPAFRPPXXX"))
        assertFalse("BNPAFRPPXX (10 chars) must fail", BankAccountValidator.isValidSWIFT("BNPAFRPPXX"))
        assertEquals(11, "BNPAFRPPXXX".length)
    }

    @Test
    fun `swift bic 014 valid 11-char ABN AMRO head office ABNANL2AXXX`() {
        assertTrue("ABNANL2AXXX must be valid", BankAccountValidator.isValidSWIFT("ABNANL2AXXX"))
        assertFalse("ABNANL2AXXXX (12 chars) must fail", BankAccountValidator.isValidSWIFT("ABNANL2AXXXX"))
        assertEquals(11, "ABNANL2AXXX".length)
    }

    @Test
    fun `swift bic 015 invalid null input`() {
        assertFalse("null must return false", BankAccountValidator.isValidSWIFT(null))
    }

    @Test
    fun `swift bic 016 invalid empty and whitespace strings`() {
        assertFalse("empty string must fail", BankAccountValidator.isValidSWIFT(""))
        assertFalse("whitespace must fail", BankAccountValidator.isValidSWIFT("  "))
    }

    @Test
    fun `swift bic 017 invalid lowercase bank code`() {
        assertFalse("deutdedb lowercase must fail", BankAccountValidator.isValidSWIFT("deutdedb"))
        assertFalse("Deutdedb mixed case must fail", BankAccountValidator.isValidSWIFT("Deutdedb"))
    }

    @Test
    fun `swift bic 018 invalid numeric bank code digits in first four positions`() {
        assertFalse("1234DEDB numeric bank code must fail", BankAccountValidator.isValidSWIFT("1234DEDB"))
        assertFalse("DE12DEDB must fail", BankAccountValidator.isValidSWIFT("DE12DEDB"))
    }

    @Test
    fun `swift bic 019 invalid 9-char and 10-char strings`() {
        assertFalse("DEUTDEDBA (9 chars) must fail", BankAccountValidator.isValidSWIFT("DEUTDEDBA"))
        assertFalse("DEUTDEDBAB (10 chars) must fail", BankAccountValidator.isValidSWIFT("DEUTDEDBAB"))
    }

    @Test
    fun `swift bic 020 invalid country code with digits DEUT12DB`() {
        assertFalse("DEUT12DB with numeric country code must fail", BankAccountValidator.isValidSWIFT("DEUT12DB"))
        assertFalse("DEUT1234 must fail", BankAccountValidator.isValidSWIFT("DEUT1234"))
    }

    // =========================================================================
    // Section 10 — Edge cases (15 tests)
    //
    // Covers: whitespace trimming behaviour, unicode characters, null-safety
    // guarantees (no exceptions thrown), very long strings, cross-method
    // correctness (an IBAN is not an ABA; an ABA is not an IBAN), and
    // extractRoutingFromText basic behaviour.
    // =========================================================================

    @Test
    fun `edge case 001 trimmed valid IBAN is valid whitespace version may or may not be`() {
        val iban = "DE89370400440532013000"
        val ibanWithSpaces = "  $iban  "
        // The trimmed form must always be valid
        assertTrue("trimmed DE IBAN must be valid", BankAccountValidator.isValidIBAN(iban))
        // The method contract for whitespace-padded input is implementation-defined;
        // simply verify it does not throw an exception
        val result = runCatching { BankAccountValidator.isValidIBAN(ibanWithSpaces) }
        assertTrue("call with leading/trailing spaces must not throw", result.isSuccess)
    }

    @Test
    fun `edge case 002 sort code without hyphens is always invalid`() {
        assertFalse("204500 (no hyphens) must fail", BankAccountValidator.isValidSortCode("204500"))
        assertFalse("NNNNNN must fail", BankAccountValidator.isValidSortCode("NNNNNN"))
    }

    @Test
    fun `edge case 003 BSB with spaces instead of hyphen is invalid`() {
        assertFalse("062 000 with space separator must fail", BankAccountValidator.isValidBSB("062 000"))
        assertFalse("062  000 double space must fail", BankAccountValidator.isValidBSB("062  000"))
    }

    @Test
    fun `edge case 004 IFSC with non-zero digit as fifth character is invalid`() {
        assertFalse("SBIN5000001 fifth char 5 must fail", BankAccountValidator.isValidIFSC("SBIN5000001"))
        assertFalse("HDFC2000001 fifth char 2 must fail", BankAccountValidator.isValidIFSC("HDFC2000001"))
    }

    @Test
    fun `edge case 005 ABA with tab character is invalid`() {
        assertFalse("tab-prefixed ABA must fail", BankAccountValidator.isValidABA("\t021000021"))
        assertFalse("tab-suffixed ABA must fail", BankAccountValidator.isValidABA("021000021\t"))
    }

    @Test
    fun `edge case 006 SWIFT BIC with embedded space is invalid`() {
        assertFalse("DEUT DEDB with internal space must fail", BankAccountValidator.isValidSWIFT("DEUT DEDB"))
        assertFalse("BARCGB 22 with internal space must fail", BankAccountValidator.isValidSWIFT("BARCGB 22"))
    }

    @Test
    fun `edge case 007 extractIBANsFromText does not return structurally invalid IBANs`() {
        // Both strings have wrong check digits; neither should be returned
        val text = "DEXX370400440532013000 and GB00NWBK60161331926819 are invalid."
        val result = BankAccountValidator.extractIBANsFromText(text)
        assertFalse("DEXX... must NOT be extracted", result.contains("DEXX370400440532013000"))
        assertFalse("GB00... must NOT be extracted", result.contains("GB00NWBK60161331926819"))
    }

    @Test
    fun `edge case 008 extractRoutingFromText returns valid routing number from sentence`() {
        val text = "Please route payment via 021000021 to our account."
        val result = BankAccountValidator.extractRoutingFromText(text)
        assertTrue("021000021 must be found", result.isNotEmpty())
        assertTrue(result.contains("021000021"))
    }

    @Test
    fun `edge case 009 extractRoutingFromText returns empty list when no routing present`() {
        val text = "No routing number whatsoever in this entirely plain sentence."
        val result = BankAccountValidator.extractRoutingFromText(text)
        assertTrue("no routing expected", result.isEmpty())
    }

    @Test
    fun `edge case 010 null IBAN call does not throw exception and returns false`() {
        val result = runCatching { BankAccountValidator.isValidIBAN(null) }
        assertTrue("isValidIBAN(null) must not throw", result.isSuccess)
        assertFalse("isValidIBAN(null) must return false", result.getOrDefault(false))
    }

    @Test
    fun `edge case 011 null ABA call does not throw exception and returns false`() {
        val result = runCatching { BankAccountValidator.isValidABA(null) }
        assertTrue("isValidABA(null) must not throw", result.isSuccess)
        assertFalse("isValidABA(null) must return false", result.getOrDefault(false))
    }

    @Test
    fun `edge case 012 null sort code call does not throw exception and returns false`() {
        val result = runCatching { BankAccountValidator.isValidSortCode(null) }
        assertTrue("isValidSortCode(null) must not throw", result.isSuccess)
        assertFalse("isValidSortCode(null) must return false", result.getOrDefault(false))
    }

    @Test
    fun `edge case 013 null BSB call does not throw exception and returns false`() {
        val result = runCatching { BankAccountValidator.isValidBSB(null) }
        assertTrue("isValidBSB(null) must not throw", result.isSuccess)
        assertFalse("isValidBSB(null) must return false", result.getOrDefault(false))
    }

    @Test
    fun `edge case 014 valid IBAN is not misidentified as ABA routing number`() {
        val iban = "DE89370400440532013000"
        assertTrue("DE IBAN must be valid as IBAN", BankAccountValidator.isValidIBAN(iban))
        assertFalse("DE IBAN must NOT be valid as ABA", BankAccountValidator.isValidABA(iban))
    }

    @Test
    fun `edge case 015 valid ABA routing number is not misidentified as IBAN`() {
        val aba = "021000021"
        assertTrue("ABA must be valid as ABA", BankAccountValidator.isValidABA(aba))
        assertFalse("ABA must NOT be valid as IBAN", BankAccountValidator.isValidIBAN(aba))
        assertFalse("ABA must NOT be valid as SWIFT", BankAccountValidator.isValidSWIFT(aba))
    }

} // end class BankAccountValidatorExtendedTest
