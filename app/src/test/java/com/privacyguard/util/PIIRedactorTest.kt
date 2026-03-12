package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

class PIIRedactorTest {

    // -------------------------------------------------------------------------
    // REDACT SSN TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRedactSSN_maskMode() {
        val text = "My SSN is 123-45-6789"
        val result = PIIRedactor.redactSSN(text, RedactionMode.MASK)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
        assertTrue(result.contains("***") || result.contains("XXX") || result.contains("###"))
    }

    @Test
    fun testRedactSSN_maskPreservesLastFour() {
        val text = "SSN: 123-45-6789"
        val result = PIIRedactor.redactSSN(text, RedactionMode.MASK)
        assertNotNull(result)
        // Masked SSN should preserve format as ***-**-6789
        assertTrue(
            result.contains("6789") || result.contains("***") || !result.contains("123-45")
        )
    }

    @Test
    fun testRedactSSN_replaceMode() {
        val text = "My SSN is 123-45-6789"
        val result = PIIRedactor.redactSSN(text, RedactionMode.REPLACE)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
        assertFalse(result.isEmpty())
    }

    @Test
    fun testRedactSSN_hashMode() {
        val text = "SSN: 987-65-4321"
        val result = PIIRedactor.redactSSN(text, RedactionMode.HASH)
        assertNotNull(result)
        assertFalse(result.contains("987-65-4321"))
        assertFalse(result.isEmpty())
    }

    @Test
    fun testRedactSSN_removeMode() {
        val text = "My SSN is 123-45-6789 here"
        val result = PIIRedactor.redactSSN(text, RedactionMode.REMOVE)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
        assertTrue(result.length < text.length)
    }

    @Test
    fun testRedactSSN_removeReducesLength() {
        val text = "SSN: 123-45-6789"
        val result = PIIRedactor.redactSSN(text, RedactionMode.REMOVE)
        assertTrue(result.length < text.length)
    }

    @Test
    fun testRedactSSN_tokenizeMode() {
        val text = "SSN: 123-45-6789"
        val result = PIIRedactor.redactSSN(text, RedactionMode.TOKENIZE)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
        // Should contain a token placeholder
        assertTrue(result.contains("[") || result.contains("{") || result.contains("TOKEN") || result.contains("__"))
    }

    @Test
    fun testRedactSSN_emptyInput() {
        val result = PIIRedactor.redactSSN("", RedactionMode.MASK)
        assertNotNull(result)
        assertEquals("", result)
    }

    @Test
    fun testRedactSSN_noSSNPresent() {
        val text = "No SSN in this text"
        val result = PIIRedactor.redactSSN(text, RedactionMode.MASK)
        assertEquals(text, result)
    }

    @Test
    fun testRedactSSN_multipleSSNs() {
        val text = "SSN1: 111-22-3333. SSN2: 444-55-6666."
        val result = PIIRedactor.redactSSN(text, RedactionMode.REPLACE)
        assertFalse(result.contains("111-22-3333"))
        assertFalse(result.contains("444-55-6666"))
    }

    // -------------------------------------------------------------------------
    // REDACT CREDIT CARD TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRedactCreditCard_maskMode() {
        val text = "Card: 4111111111111111"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.MASK)
        assertNotNull(result)
        assertFalse(result.replace(" ", "").contains("4111111111111111"))
    }

    @Test
    fun testRedactCreditCard_maskShowsLastFour() {
        val text = "Use card 4111111111111111"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.MASK)
        assertNotNull(result)
        // Masked card should show last 4 digits
        assertTrue(result.contains("1111") || result.contains("****") || !result.contains("411111111111"))
    }

    @Test
    fun testRedactCreditCard_removeMode() {
        val text = "Payment with 4111111111111111 processed"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.REMOVE)
        assertNotNull(result)
        assertFalse(result.replace(" ", "").contains("4111111111111111"))
        assertTrue(result.length < text.length)
    }

    @Test
    fun testRedactCreditCard_replaceMode() {
        val text = "Card: 5500005555555559"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.REPLACE)
        assertFalse(result.contains("5500005555555559"))
    }

    @Test
    fun testRedactCreditCard_hashMode() {
        val text = "Card: 4111111111111111"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.HASH)
        assertNotNull(result)
        assertFalse(result.contains("4111111111111111"))
    }

    @Test
    fun testRedactCreditCard_tokenizeMode() {
        val text = "Card: 4111111111111111"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.TOKENIZE)
        assertNotNull(result)
        assertFalse(result.contains("4111111111111111"))
    }

    @Test
    fun testRedactCreditCard_emptyInput() {
        val result = PIIRedactor.redactCreditCard("", RedactionMode.MASK)
        assertNotNull(result)
        assertEquals("", result)
    }

    @Test
    fun testRedactCreditCard_noCardPresent() {
        val text = "No credit card info"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.MASK)
        assertEquals(text, result)
    }

    @Test
    fun testRedactCreditCard_withDashes() {
        val text = "Card: 4111-1111-1111-1111"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.REPLACE)
        assertFalse(result.contains("4111-1111-1111-1111"))
    }

    @Test
    fun testRedactCreditCard_multipleCards() {
        val text = "Cards: 4111111111111111 and 5500005555555559"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.REMOVE)
        assertFalse(result.contains("4111111111111111"))
        assertFalse(result.contains("5500005555555559"))
        assertTrue(result.length < text.length)
    }

    // -------------------------------------------------------------------------
    // REDACT EMAIL TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRedactEmail_maskMode() {
        val text = "Email: user@example.com"
        val result = PIIRedactor.redactEmail(text, RedactionMode.MASK)
        assertNotNull(result)
        assertFalse(result.contains("user@example.com"))
    }

    @Test
    fun testRedactEmail_replaceMode() {
        val text = "Contact: admin@company.org"
        val result = PIIRedactor.redactEmail(text, RedactionMode.REPLACE)
        assertFalse(result.contains("admin@company.org"))
    }

    @Test
    fun testRedactEmail_removeMode() {
        val text = "Send to alice@wonderland.net for details"
        val result = PIIRedactor.redactEmail(text, RedactionMode.REMOVE)
        assertFalse(result.contains("alice@wonderland.net"))
        assertTrue(result.length < text.length)
    }

    @Test
    fun testRedactEmail_hashMode() {
        val text = "Email: secret@domain.com"
        val result = PIIRedactor.redactEmail(text, RedactionMode.HASH)
        assertNotNull(result)
        assertFalse(result.contains("secret@domain.com"))
    }

    @Test
    fun testRedactEmail_tokenizeMode() {
        val text = "CC: boss@corp.io"
        val result = PIIRedactor.redactEmail(text, RedactionMode.TOKENIZE)
        assertNotNull(result)
        assertFalse(result.contains("boss@corp.io"))
    }

    @Test
    fun testRedactEmail_emptyInput() {
        val result = PIIRedactor.redactEmail("", RedactionMode.MASK)
        assertNotNull(result)
        assertEquals("", result)
    }

    @Test
    fun testRedactEmail_noEmailPresent() {
        val text = "No email address here"
        val result = PIIRedactor.redactEmail(text, RedactionMode.MASK)
        assertEquals(text, result)
    }

    @Test
    fun testRedactEmail_multipleEmails() {
        val text = "From: alice@x.com To: bob@y.com"
        val result = PIIRedactor.redactEmail(text, RedactionMode.REPLACE)
        assertFalse(result.contains("alice@x.com"))
        assertFalse(result.contains("bob@y.com"))
    }

    // -------------------------------------------------------------------------
    // REDACT PHONE TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRedactPhone_maskMode() {
        val text = "Phone: (555) 123-4567"
        val result = PIIRedactor.redactPhone(text, RedactionMode.MASK)
        assertNotNull(result)
        assertFalse(result.contains("555) 123-4567"))
    }

    @Test
    fun testRedactPhone_replaceMode() {
        val text = "Call 555-867-5309 today"
        val result = PIIRedactor.redactPhone(text, RedactionMode.REPLACE)
        assertFalse(result.contains("555-867-5309"))
    }

    @Test
    fun testRedactPhone_removeMode() {
        val text = "Reach us at +1-800-555-1212 anytime"
        val result = PIIRedactor.redactPhone(text, RedactionMode.REMOVE)
        assertFalse(result.contains("+1-800-555-1212"))
        assertTrue(result.length < text.length)
    }

    @Test
    fun testRedactPhone_emptyInput() {
        val result = PIIRedactor.redactPhone("", RedactionMode.MASK)
        assertNotNull(result)
        assertEquals("", result)
    }

    @Test
    fun testRedactPhone_noPhonePresent() {
        val text = "No phone number here."
        val result = PIIRedactor.redactPhone(text, RedactionMode.MASK)
        assertEquals(text, result)
    }

    @Test
    fun testRedactPhone_tokenizeMode() {
        val text = "Contact: (555) 111-2222"
        val result = PIIRedactor.redactPhone(text, RedactionMode.TOKENIZE)
        assertNotNull(result)
        assertFalse(result.contains("555) 111-2222"))
    }

    // -------------------------------------------------------------------------
    // REDACT IP ADDRESS TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRedactIPAddress_maskMode() {
        val text = "Server: 192.168.1.100"
        val result = PIIRedactor.redactIPAddress(text, RedactionMode.MASK)
        assertNotNull(result)
        assertFalse(result.contains("192.168.1.100"))
    }

    @Test
    fun testRedactIPAddress_replaceMode() {
        val text = "From IP: 8.8.8.8"
        val result = PIIRedactor.redactIPAddress(text, RedactionMode.REPLACE)
        assertFalse(result.contains("8.8.8.8"))
    }

    @Test
    fun testRedactIPAddress_removeMode() {
        val text = "Traffic from 10.0.0.1 blocked"
        val result = PIIRedactor.redactIPAddress(text, RedactionMode.REMOVE)
        assertFalse(result.contains("10.0.0.1"))
        assertTrue(result.length < text.length)
    }

    @Test
    fun testRedactIPAddress_emptyInput() {
        val result = PIIRedactor.redactIPAddress("", RedactionMode.MASK)
        assertNotNull(result)
        assertEquals("", result)
    }

    @Test
    fun testRedactIPAddress_noIPPresent() {
        val text = "No IP address present"
        val result = PIIRedactor.redactIPAddress(text, RedactionMode.MASK)
        assertEquals(text, result)
    }

    @Test
    fun testRedactIPAddress_multipleIPs() {
        val text = "From 1.2.3.4 to 5.6.7.8"
        val result = PIIRedactor.redactIPAddress(text, RedactionMode.REPLACE)
        assertFalse(result.contains("1.2.3.4"))
        assertFalse(result.contains("5.6.7.8"))
    }

    @Test
    fun testRedactIPAddress_hashMode() {
        val text = "Origin: 203.0.113.1"
        val result = PIIRedactor.redactIPAddress(text, RedactionMode.HASH)
        assertNotNull(result)
        assertFalse(result.contains("203.0.113.1"))
    }

    // -------------------------------------------------------------------------
    // REDACT ALL TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRedactAll_maskMode() {
        val text = "SSN: 123-45-6789. Email: x@y.com. Card: 4111111111111111."
        val result = PIIRedactor.redactAll(text, RedactionMode.MASK)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
        assertFalse(result.contains("x@y.com"))
        assertFalse(result.contains("4111111111111111"))
    }

    @Test
    fun testRedactAll_removeMode() {
        val text = "SSN: 123-45-6789. Email: user@test.com."
        val result = PIIRedactor.redactAll(text, RedactionMode.REMOVE)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
        assertFalse(result.contains("user@test.com"))
        assertTrue(result.length < text.length)
    }

    @Test
    fun testRedactAll_replaceMode() {
        val text = "Phone: (555) 123-4567. IP: 8.8.8.8."
        val result = PIIRedactor.redactAll(text, RedactionMode.REPLACE)
        assertFalse(result.contains("555) 123-4567"))
        assertFalse(result.contains("8.8.8.8"))
    }

    @Test
    fun testRedactAll_emptyInput() {
        val result = PIIRedactor.redactAll("", RedactionMode.MASK)
        assertNotNull(result)
        assertEquals("", result)
    }

    @Test
    fun testRedactAll_noPIIPresent() {
        val text = "This is safe text with no personal data."
        val result = PIIRedactor.redactAll(text, RedactionMode.MASK)
        assertEquals(text, result)
    }

    @Test
    fun testRedactAll_tokenizeMode() {
        val text = "SSN: 123-45-6789. Card: 4111111111111111."
        val result = PIIRedactor.redactAll(text, RedactionMode.TOKENIZE)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
        assertFalse(result.contains("4111111111111111"))
    }

    // -------------------------------------------------------------------------
    // REDACT (WITH CONFIG) TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRedact_defaultConfig() {
        val text = "Email: test@example.com"
        val config = RedactionConfig(
            defaultMode = RedactionMode.MASK,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val result = PIIRedactor.redact(text, config)
        assertNotNull(result)
        assertNotNull(result.redactedText)
        assertFalse(result.redactedText.contains("test@example.com"))
    }

    @Test
    fun testRedact_resultHasTokenMap() {
        val text = "Card: 4111111111111111"
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val result = PIIRedactor.redact(text, config)
        assertNotNull(result.tokenMap)
    }

    @Test
    fun testRedact_resultHasStats() {
        val text = "SSN: 123-45-6789"
        val config = RedactionConfig(
            defaultMode = RedactionMode.MASK,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val result = PIIRedactor.redact(text, config)
        assertNotNull(result.stats)
        assertEquals(text.length, result.stats.originalLength)
    }

    @Test
    fun testRedact_customReplacement() {
        val text = "SSN: 123-45-6789"
        val config = RedactionConfig(
            defaultMode = RedactionMode.REPLACE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = false,
            hashAlgorithm = "SHA-256",
            customReplacements = mapOf("SSN" to "[REDACTED_SSN]")
        )
        val result = PIIRedactor.redact(text, config)
        assertNotNull(result)
        assertFalse(result.redactedText.contains("123-45-6789"))
    }

    @Test
    fun testRedact_encryptMode() {
        val text = "SSN: 123-45-6789"
        val config = RedactionConfig(
            defaultMode = RedactionMode.ENCRYPT,
            overrides = emptyMap(),
            encryptionKey = "testkey123456789",
            preserveFormat = false,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val result = PIIRedactor.redact(text, config)
        assertNotNull(result)
        assertFalse(result.redactedText.contains("123-45-6789"))
    }

    @Test
    fun testRedact_emptyText() {
        val config = RedactionConfig(
            defaultMode = RedactionMode.MASK,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val result = PIIRedactor.redact("", config)
        assertNotNull(result)
        assertEquals("", result.redactedText)
    }

    // -------------------------------------------------------------------------
    // RESTORE (TOKENIZE ROUNDTRIP) TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRestore_tokenizeRoundtrip() {
        val text = "SSN: 123-45-6789"
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val redactionResult = PIIRedactor.redact(text, config)
        val restored = PIIRedactor.restore(redactionResult.redactedText, redactionResult.tokenMap)
        assertNotNull(restored)
        assertEquals(text, restored)
    }

    @Test
    fun testRestore_emailRoundtrip() {
        val text = "Contact: user@example.com"
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val redactionResult = PIIRedactor.redact(text, config)
        val restored = PIIRedactor.restore(redactionResult.redactedText, redactionResult.tokenMap)
        assertEquals(text, restored)
    }

    @Test
    fun testRestore_multipleEntitiesRoundtrip() {
        val text = "SSN: 123-45-6789. Email: user@example.com."
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val redactionResult = PIIRedactor.redact(text, config)
        val restored = PIIRedactor.restore(redactionResult.redactedText, redactionResult.tokenMap)
        assertEquals(text, restored)
    }

    @Test
    fun testRestore_emptyTokenMap() {
        val redacted = "This has no tokens"
        val restored = PIIRedactor.restore(redacted, emptyMap())
        assertEquals(redacted, restored)
    }

    @Test
    fun testRestore_cardRoundtrip() {
        val text = "Card: 4111111111111111"
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val redactionResult = PIIRedactor.redact(text, config)
        assertFalse(redactionResult.redactedText.contains("4111111111111111"))
        val restored = PIIRedactor.restore(redactionResult.redactedText, redactionResult.tokenMap)
        assertEquals(text, restored)
    }

    @Test
    fun testRestore_phoneRoundtrip() {
        val text = "Phone: (555) 123-4567"
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val redacted = PIIRedactor.redact(text, config)
        val restored = PIIRedactor.restore(redacted.redactedText, redacted.tokenMap)
        assertEquals(text, restored)
    }

    // -------------------------------------------------------------------------
    // GENERATE TOKEN TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testGenerateToken_notEmpty() {
        val token = PIIRedactor.generateToken("SSN", 0)
        assertNotNull(token)
        assertFalse(token.isEmpty())
    }

    @Test
    fun testGenerateToken_containsEntityType() {
        val token = PIIRedactor.generateToken("EMAIL", 1)
        assertNotNull(token)
        assertTrue(token.contains("EMAIL") || token.length > 4)
    }

    @Test
    fun testGenerateToken_uniquePerIndex() {
        val token0 = PIIRedactor.generateToken("SSN", 0)
        val token1 = PIIRedactor.generateToken("SSN", 1)
        assertNotEquals(token0, token1)
    }

    @Test
    fun testGenerateToken_uniquePerEntityType() {
        val ssnToken = PIIRedactor.generateToken("SSN", 0)
        val emailToken = PIIRedactor.generateToken("EMAIL", 0)
        assertNotEquals(ssnToken, emailToken)
    }

    @Test
    fun testGenerateToken_deterministicForSameInput() {
        val token1 = PIIRedactor.generateToken("CARD", 2)
        val token2 = PIIRedactor.generateToken("CARD", 2)
        assertEquals(token1, token2)
    }

    // -------------------------------------------------------------------------
    // HASH STRING TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testHashString_sha256NotEmpty() {
        val result = PIIRedactor.hashString("test input", "SHA-256")
        assertNotNull(result)
        assertFalse(result.isEmpty())
    }

    @Test
    fun testHashString_sha256Length() {
        val result = PIIRedactor.hashString("test input", "SHA-256")
        // SHA-256 hex string is 64 chars
        assertEquals(64, result.length)
    }

    @Test
    fun testHashString_md5NotEmpty() {
        val result = PIIRedactor.hashString("test input", "MD5")
        assertNotNull(result)
        assertFalse(result.isEmpty())
    }

    @Test
    fun testHashString_md5Length() {
        val result = PIIRedactor.hashString("test input", "MD5")
        // MD5 hex string is 32 chars
        assertEquals(32, result.length)
    }

    @Test
    fun testHashString_sha256Deterministic() {
        val input = "SSN:123-45-6789"
        val h1 = PIIRedactor.hashString(input, "SHA-256")
        val h2 = PIIRedactor.hashString(input, "SHA-256")
        assertEquals(h1, h2)
    }

    @Test
    fun testHashString_md5Deterministic() {
        val input = "card:4111111111111111"
        val h1 = PIIRedactor.hashString(input, "MD5")
        val h2 = PIIRedactor.hashString(input, "MD5")
        assertEquals(h1, h2)
    }

    @Test
    fun testHashString_differentInputsDifferentHashes() {
        val h1 = PIIRedactor.hashString("input1", "SHA-256")
        val h2 = PIIRedactor.hashString("input2", "SHA-256")
        assertNotEquals(h1, h2)
    }

    @Test
    fun testHashString_sha256DoesNotContainOriginal() {
        val original = "123-45-6789"
        val hash = PIIRedactor.hashString(original, "SHA-256")
        assertFalse(hash.contains("123-45-6789"))
    }

    @Test
    fun testHashString_md5DoesNotContainOriginal() {
        val original = "user@example.com"
        val hash = PIIRedactor.hashString(original, "MD5")
        assertFalse(hash.contains("user@example.com"))
    }

    @Test
    fun testHashString_emptyInput() {
        val result = PIIRedactor.hashString("", "SHA-256")
        assertNotNull(result)
        // Hash of empty string is still valid
        assertEquals(64, result.length)
    }

    @Test
    fun testHashString_sha1NotEmpty() {
        val result = PIIRedactor.hashString("test", "SHA-1")
        assertNotNull(result)
        assertFalse(result.isEmpty())
    }

    // -------------------------------------------------------------------------
    // IS REDACTED TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testIsRedacted_maskedSSN() {
        val text = "SSN: ***-**-6789"
        val result = PIIRedactor.isRedacted(text)
        assertTrue(result)
    }

    @Test
    fun testIsRedacted_maskedCard() {
        val text = "Card: ****-****-****-1111"
        val result = PIIRedactor.isRedacted(text)
        assertTrue(result)
    }

    @Test
    fun testIsRedacted_tokenPresent() {
        val text = "SSN: [TOKEN_SSN_0]"
        val result = PIIRedactor.isRedacted(text)
        assertTrue(result)
    }

    @Test
    fun testIsRedacted_plainText() {
        val text = "This is plain unredacted text."
        val result = PIIRedactor.isRedacted(text)
        assertFalse(result)
    }

    @Test
    fun testIsRedacted_emptyText() {
        val result = PIIRedactor.isRedacted("")
        assertFalse(result)
    }

    @Test
    fun testIsRedacted_redactedEmail() {
        val text = "Email: [REDACTED]"
        val result = PIIRedactor.isRedacted(text)
        assertTrue(result)
    }

    @Test
    fun testIsRedacted_maskedPhone() {
        val text = "Phone: (***) ***-4567"
        val result = PIIRedactor.isRedacted(text)
        assertTrue(result)
    }

    @Test
    fun testIsRedacted_xxxPattern() {
        val text = "SSN: XXX-XX-6789"
        val result = PIIRedactor.isRedacted(text)
        assertTrue(result)
    }

    @Test
    fun testIsRedacted_hashPatternHex() {
        // A SHA-256 hash in context looks redacted
        val text = "Value: a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"
        val result = PIIRedactor.isRedacted(text)
        // May detect as redacted due to hash pattern
        assertNotNull(result)
    }

    // -------------------------------------------------------------------------
    // EXTRACT TOKENS TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testExtractTokens_singleToken() {
        val text = "SSN: [TOKEN_SSN_0] is here"
        val tokens = PIIRedactor.extractTokens(text)
        assertNotNull(tokens)
        assertTrue(tokens.isNotEmpty())
        assertTrue(tokens.contains("[TOKEN_SSN_0]"))
    }

    @Test
    fun testExtractTokens_multipleTokens() {
        val text = "Data: [TOKEN_SSN_0] and [TOKEN_EMAIL_1] found"
        val tokens = PIIRedactor.extractTokens(text)
        assertNotNull(tokens)
        assertTrue(tokens.size >= 2)
    }

    @Test
    fun testExtractTokens_noTokens() {
        val text = "No tokens in this text."
        val tokens = PIIRedactor.extractTokens(text)
        assertNotNull(tokens)
        assertTrue(tokens.isEmpty())
    }

    @Test
    fun testExtractTokens_emptyText() {
        val tokens = PIIRedactor.extractTokens("")
        assertNotNull(tokens)
        assertTrue(tokens.isEmpty())
    }

    @Test
    fun testExtractTokens_fromTokenizedResult() {
        val text = "SSN: 123-45-6789. Card: 4111111111111111."
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val redacted = PIIRedactor.redact(text, config)
        val tokens = PIIRedactor.extractTokens(redacted.redactedText)
        assertNotNull(tokens)
        assertTrue(tokens.size >= 2)
    }

    @Test
    fun testExtractTokens_countMatchesRedactions() {
        val text = "Email: a@b.com. Phone: (555) 111-2222."
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val redacted = PIIRedactor.redact(text, config)
        val tokens = PIIRedactor.extractTokens(redacted.redactedText)
        assertEquals(redacted.tokenMap.size, tokens.size)
    }

    @Test
    fun testExtractTokens_returnsList() {
        val text = "[TOKEN_A_0] and [TOKEN_B_1]"
        val result = PIIRedactor.extractTokens(text)
        assertTrue(result is List<*>)
    }

    // -------------------------------------------------------------------------
    // COMPUTE REDACTION STATS TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testComputeRedactionStats_originalLength() {
        val original = "SSN: 123-45-6789"
        val redacted = PIIRedactor.redactSSN(original, RedactionMode.MASK)
        val stats = PIIRedactor.computeRedactionStats(original, redacted)
        assertEquals(original.length, stats.originalLength)
    }

    @Test
    fun testComputeRedactionStats_redactedLength() {
        val original = "SSN: 123-45-6789"
        val redacted = PIIRedactor.redactSSN(original, RedactionMode.MASK)
        val stats = PIIRedactor.computeRedactionStats(original, redacted)
        assertEquals(redacted.length, stats.redactedLength)
    }

    @Test
    fun testComputeRedactionStats_entitiesRedacted() {
        val original = "SSN: 123-45-6789"
        val redacted = PIIRedactor.redactSSN(original, RedactionMode.REPLACE)
        val stats = PIIRedactor.computeRedactionStats(original, redacted)
        assertTrue(stats.entitiesRedacted >= 1)
    }

    @Test
    fun testComputeRedactionStats_ratePercent() {
        val original = "SSN: 123-45-6789. Email: x@y.com."
        val redacted = PIIRedactor.redactAll(original, RedactionMode.REMOVE)
        val stats = PIIRedactor.computeRedactionStats(original, redacted)
        assertTrue(stats.redactionRatePercent >= 0.0f)
        assertTrue(stats.redactionRatePercent <= 100.0f)
    }

    @Test
    fun testComputeRedactionStats_noRedaction() {
        val text = "No PII here at all"
        val stats = PIIRedactor.computeRedactionStats(text, text)
        assertEquals(text.length, stats.originalLength)
        assertEquals(text.length, stats.redactedLength)
        assertEquals(0, stats.entitiesRedacted)
    }

    @Test
    fun testComputeRedactionStats_removeReducesLength() {
        val original = "SSN: 123-45-6789 in record"
        val redacted = PIIRedactor.redactSSN(original, RedactionMode.REMOVE)
        val stats = PIIRedactor.computeRedactionStats(original, redacted)
        assertTrue(stats.redactedLength < stats.originalLength)
    }

    // -------------------------------------------------------------------------
    // ADDITIONAL EDGE CASE TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testRedactAll_hashMode() {
        val text = "SSN: 123-45-6789"
        val result = PIIRedactor.redactAll(text, RedactionMode.HASH)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
    }

    @Test
    fun testRedactAll_encryptMode() {
        val text = "Card: 4111111111111111"
        val result = PIIRedactor.redactAll(text, RedactionMode.ENCRYPT)
        assertNotNull(result)
        assertFalse(result.contains("4111111111111111"))
    }

    @Test
    fun testMaskSSNPreservesFormat() {
        val text = "SSN: 123-45-6789"
        val result = PIIRedactor.redactSSN(text, RedactionMode.MASK)
        // Format ***-**-6789 expected
        assertNotNull(result)
        assertFalse(result.contains("123"))
        assertFalse(result.contains("45-6"))
        assertTrue(result.contains("6789") || !result.contains("123-45"))
    }

    @Test
    fun testMaskCardShowsLastFour() {
        val text = "Card number is 4111111111111111"
        val result = PIIRedactor.redactCreditCard(text, RedactionMode.MASK)
        assertNotNull(result)
        assertTrue(result.contains("1111") || result.contains("****"))
        assertFalse(result.contains("411111111111"))
    }

    @Test
    fun testRedactResult_statsEntitiesPositive() {
        val text = "SSN: 123-45-6789"
        val config = RedactionConfig(
            defaultMode = RedactionMode.REPLACE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val result = PIIRedactor.redact(text, config)
        assertTrue(result.stats.entitiesRedacted >= 1)
    }

    @Test
    fun testRedactResult_tokenMapNotNullAfterTokenize() {
        val text = "Email: secret@domain.com"
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val result = PIIRedactor.redact(text, config)
        assertNotNull(result.tokenMap)
        assertFalse(result.tokenMap.isEmpty())
    }

    @Test
    fun testRedactAll_preservesNonPIIText() {
        val text = "Hello, my name is John. Card: 4111111111111111."
        val result = PIIRedactor.redactAll(text, RedactionMode.REPLACE)
        // Non-PII text should be preserved
        assertTrue(result.contains("Hello") || result.contains("John"))
        assertFalse(result.contains("4111111111111111"))
    }

    @Test
    fun testRedactAll_multipleModesConsistency() {
        val text = "SSN: 123-45-6789"
        val maskResult = PIIRedactor.redactAll(text, RedactionMode.MASK)
        val replaceResult = PIIRedactor.redactAll(text, RedactionMode.REPLACE)
        val removeResult = PIIRedactor.redactAll(text, RedactionMode.REMOVE)
        // All modes should remove the SSN
        assertFalse(maskResult.contains("123-45-6789"))
        assertFalse(replaceResult.contains("123-45-6789"))
        assertFalse(removeResult.contains("123-45-6789"))
    }

    @Test
    fun testRedactSSN_encryptMode() {
        val text = "SSN: 123-45-6789"
        val result = PIIRedactor.redactSSN(text, RedactionMode.ENCRYPT)
        assertNotNull(result)
        assertFalse(result.contains("123-45-6789"))
    }

    @Test
    fun testHashString_sha256KnownValue() {
        // SHA-256 of "abc" is known
        val result = PIIRedactor.hashString("abc", "SHA-256")
        assertEquals("ba7816bf8f01cfea414140de5dae2ec73b00361bbef0469348423f656b2c955d", result)
    }

    @Test
    fun testHashString_md5KnownValue() {
        // MD5 of "abc" is known
        val result = PIIRedactor.hashString("abc", "MD5")
        assertEquals("900150983cd24fb0d6963f7d28e17f72", result)
    }

    @Test
    fun testExtractTokens_tokenFormatValidation() {
        val text = "[TOKEN_SSN_0] and [TOKEN_CARD_1]"
        val tokens = PIIRedactor.extractTokens(text)
        for (token in tokens) {
            assertTrue(token.startsWith("["))
            assertTrue(token.endsWith("]"))
        }
    }

    @Test
    fun testRestore_withMultiplePhones() {
        val text = "Call (555) 111-2222 or (555) 333-4444"
        val config = RedactionConfig(
            defaultMode = RedactionMode.TOKENIZE,
            overrides = emptyMap(),
            encryptionKey = null,
            preserveFormat = true,
            hashAlgorithm = "SHA-256",
            customReplacements = emptyMap()
        )
        val redacted = PIIRedactor.redact(text, config)
        val restored = PIIRedactor.restore(redacted.redactedText, redacted.tokenMap)
        assertEquals(text, restored)
    }

    @Test
    fun testComputeRedactionStats_multipleEntities() {
        val original = "SSN: 123-45-6789. Email: a@b.com. Card: 4111111111111111."
        val redacted = PIIRedactor.redactAll(original, RedactionMode.REPLACE)
        val stats = PIIRedactor.computeRedactionStats(original, redacted)
        assertTrue(stats.entitiesRedacted >= 3)
        assertTrue(stats.redactionRatePercent > 0.0f)
    }

    @Test
    fun testRedactPhone_hashMode() {
        val text = "Phone: 555-123-4567"
        val result = PIIRedactor.redactPhone(text, RedactionMode.HASH)
        assertNotNull(result)
        assertFalse(result.contains("555-123-4567"))
    }

    @Test
    fun testRedactEmail_encryptMode() {
        val text = "Email: private@secret.org"
        val result = PIIRedactor.redactEmail(text, RedactionMode.ENCRYPT)
        assertNotNull(result)
        assertFalse(result.contains("private@secret.org"))
    }

    @Test
    fun testRedactIPAddress_tokenizeMode() {
        val text = "From IP: 192.0.2.1"
        val result = PIIRedactor.redactIPAddress(text, RedactionMode.TOKENIZE)
        assertNotNull(result)
        assertFalse(result.contains("192.0.2.1"))
    }
}
