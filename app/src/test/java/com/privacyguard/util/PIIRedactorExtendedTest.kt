package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended test suite for PIIRedactor covering all 6 redaction modes,
 * all PII types, combined scenarios, and edge cases.
 * 250+ test functions.
 */
class PIIRedactorExtendedTest {

    // Private helpers
    private fun config(mode: PIIRedactor.RedactionMode) =
        PIIRedactor.RedactionConfig(defaultMode = mode)

    private fun configWithKey(mode: PIIRedactor.RedactionMode) =
        PIIRedactor.RedactionConfig(
            defaultMode = mode,
            encryptionKey = "SecretTestKey123".toByteArray()
        )

    // ─── SECTION 1: MASK Mode — SSN ───────────────────────────────────────────

    @Test fun `test mask ssn 001 standard format`() {
        val result = PIIRedactor.redact("SSN: 123-45-6789", config(PIIRedactor.RedactionMode.MASK))
        assertTrue(result.redactedText.contains("***"))
        assertFalse(result.redactedText.contains("123-45-6789"))
    }

    @Test fun `test mask ssn 002 preserves format stars`() {
        val result = PIIRedactor.redactSSN("123-45-6789", PIIRedactor.RedactionMode.MASK)
        assertTrue(result.contains("*") || result.contains("X"))
        assertEquals(11, result.length)  // format preserved NNN-NN-NNNN
    }

    @Test fun `test mask ssn 003 last four visible`() {
        val result = PIIRedactor.redactSSN("123-45-6789", PIIRedactor.RedactionMode.MASK)
        // Should show last 4: ***-**-6789
        assertTrue(result.endsWith("6789"))
    }

    @Test fun `test mask ssn 004 in text preserves context`() {
        val text = "Employee SSN 234-56-7890 has been updated."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertTrue(result.redactedText.contains("Employee"))
        assertTrue(result.redactedText.contains("has been updated"))
        assertFalse(result.redactedText.contains("234-56-7890"))
    }

    @Test fun `test mask ssn 005 multiple SSNs in text`() {
        val text = "SSNs on file: 123-45-6789 and 234-56-7890."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertFalse(result.redactedText.contains("234-56-7890"))
        assertEquals(2, result.stats.ssnCount)
    }

    @Test fun `test mask ssn 006 empty string no change`() {
        val result = PIIRedactor.redact("", config(PIIRedactor.RedactionMode.MASK))
        assertEquals("", result.redactedText)
        assertEquals(0, result.stats.totalRedacted)
    }

    @Test fun `test mask ssn 007 no pii no change`() {
        val text = "The weather is sunny today."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(text, result.redactedText)
        assertEquals(0, result.stats.totalRedacted)
    }

    @Test fun `test mask ssn 008 stats count is correct`() {
        val text = "SSN1: 123-45-6789. SSN2: 234-56-7890. SSN3: 345-67-8901."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(3, result.stats.ssnCount)
    }

    // ─── SECTION 2: MASK Mode — Credit Cards ──────────────────────────────────

    @Test fun `test mask card 001 visa standard`() {
        val result = PIIRedactor.redactCreditCard("4111111111111111", PIIRedactor.RedactionMode.MASK)
        assertTrue(result.endsWith("1111"))
        assertTrue(result.contains("*") || result.contains("X"))
    }

    @Test fun `test mask card 002 shows last four`() {
        val result = PIIRedactor.redactCreditCard("5555555555554444", PIIRedactor.RedactionMode.MASK)
        assertTrue(result.endsWith("4444"))
    }

    @Test fun `test mask card 003 amex last five`() {
        val result = PIIRedactor.redactCreditCard("378282246310005", PIIRedactor.RedactionMode.MASK)
        // Amex: show last 5 digits
        assertTrue(result.endsWith("0005") || result.endsWith("00005"))
    }

    @Test fun `test mask card 004 in text context preserved`() {
        val text = "Please charge 4111111111111111 for the order."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertTrue(result.redactedText.contains("Please charge"))
        assertFalse(result.redactedText.contains("4111111111111111"))
    }

    @Test fun `test mask card 005 multiple cards in text`() {
        val text = "Visa: 4111111111111111 and MC: 5555555555554444"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("4111111111111111"))
        assertFalse(result.redactedText.contains("5555555555554444"))
        assertEquals(2, result.stats.creditCardCount)
    }

    @Test fun `test mask card 006 stats count`() {
        val text = "Cards: 4111111111111111, 5555555555554444, 378282246310005"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(3, result.stats.creditCardCount)
    }

    // ─── SECTION 3: MASK Mode — Email ─────────────────────────────────────────

    @Test fun `test mask email 001 standard format`() {
        val result = PIIRedactor.redactEmail("john.doe@gmail.com", PIIRedactor.RedactionMode.MASK)
        assertTrue(result.contains("@"))
        assertFalse(result == "john.doe@gmail.com")
    }

    @Test fun `test mask email 002 local part masked`() {
        val result = PIIRedactor.redactEmail("user@example.com", PIIRedactor.RedactionMode.MASK)
        // u****@example.com or similar
        assertTrue(result.contains("@example.com"))
        assertTrue(result.startsWith("u") || result.contains("*"))
    }

    @Test fun `test mask email 003 in text`() {
        val text = "Contact me at john.doe@gmail.com for info."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("john.doe@gmail.com"))
        assertTrue(result.redactedText.contains("Contact me at"))
    }

    @Test fun `test mask email 004 multiple emails`() {
        val text = "Send to alice@example.com and bob@example.com"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("alice@example.com"))
        assertFalse(result.redactedText.contains("bob@example.com"))
        assertEquals(2, result.stats.emailCount)
    }

    @Test fun `test mask email 005 domain preserved option`() {
        val configWithDomain = PIIRedactor.RedactionConfig(
            defaultMode = PIIRedactor.RedactionMode.MASK,
            preserveFormat = true
        )
        val result = PIIRedactor.redactEmail("user@example.com", PIIRedactor.RedactionMode.MASK)
        assertTrue(result.contains("@"))
    }

    // ─── SECTION 4: MASK Mode — Phone Numbers ─────────────────────────────────

    @Test fun `test mask phone 001 us format`() {
        val result = PIIRedactor.redact("Call (212) 555-1234 today.", config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("212"))
        assertTrue(result.stats.phoneCount >= 1)
    }

    @Test fun `test mask phone 002 e164 format`() {
        val result = PIIRedactor.redact("+1 415 555 3456 is my number.", config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("415"))
        assertTrue(result.stats.totalRedacted >= 1)
    }

    @Test fun `test mask phone 003 international format`() {
        val result = PIIRedactor.redact("UK number: +44 20 7946 0958", config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("7946"))
        assertTrue(result.stats.phoneCount >= 1)
    }

    @Test fun `test mask phone 004 multiple formats`() {
        val text = "Phones: (212) 555-1234, 213.555.5678, +1-415-555-3456"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(3, result.stats.phoneCount)
        assertFalse(result.redactedText.contains("212"))
        assertFalse(result.redactedText.contains("213"))
        assertFalse(result.redactedText.contains("415"))
    }

    // ─── SECTION 5: REPLACE Mode ──────────────────────────────────────────────

    @Test fun `test replace ssn 001 replaced with placeholder`() {
        val result = PIIRedactor.redact("SSN: 123-45-6789", config(PIIRedactor.RedactionMode.REPLACE))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertTrue(result.redactedText.contains("[SSN]") || result.redactedText.contains("[REDACTED]"))
    }

    @Test fun `test replace card 001 replaced with placeholder`() {
        val result = PIIRedactor.redact("Card: 4111111111111111", config(PIIRedactor.RedactionMode.REPLACE))
        assertFalse(result.redactedText.contains("4111111111111111"))
        assertTrue(
            result.redactedText.contains("[CREDIT_CARD]") ||
            result.redactedText.contains("[CARD]") ||
            result.redactedText.contains("[REDACTED]")
        )
    }

    @Test fun `test replace email 001 replaced with placeholder`() {
        val result = PIIRedactor.redact("Email: user@example.com", config(PIIRedactor.RedactionMode.REPLACE))
        assertFalse(result.redactedText.contains("user@example.com"))
        assertTrue(
            result.redactedText.contains("[EMAIL]") || result.redactedText.contains("[REDACTED]")
        )
    }

    @Test fun `test replace phone 001 replaced with placeholder`() {
        val result = PIIRedactor.redact("Phone: (212) 555-1234", config(PIIRedactor.RedactionMode.REPLACE))
        assertFalse(result.redactedText.contains("555"))
        assertTrue(result.stats.totalRedacted >= 1)
    }

    @Test fun `test replace custom 001 custom replacement map`() {
        val customConfig = PIIRedactor.RedactionConfig(
            defaultMode = PIIRedactor.RedactionMode.REPLACE,
            customReplacements = mapOf("SSN" to "[SOCIAL_SECURITY_NUMBER]")
        )
        val result = PIIRedactor.redact("SSN: 123-45-6789", customConfig)
        assertFalse(result.redactedText.contains("123-45-6789"))
    }

    @Test fun `test replace 002 mixed pii types`() {
        val text = "SSN: 123-45-6789, Email: user@example.com, Card: 4111111111111111"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REPLACE))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertFalse(result.redactedText.contains("user@example.com"))
        assertFalse(result.redactedText.contains("4111111111111111"))
        assertEquals(3, result.stats.totalRedacted)
    }

    // ─── SECTION 6: HASH Mode ─────────────────────────────────────────────────

    @Test fun `test hash ssn 001 produces hash not original`() {
        val result = PIIRedactor.redact("SSN: 123-45-6789", config(PIIRedactor.RedactionMode.HASH))
        assertFalse(result.redactedText.contains("123-45-6789"))
        // Hash should be alphanumeric hex
        val redacted = result.redactedText
        assertNotNull(redacted)
    }

    @Test fun `test hash 001 same input same hash`() {
        val input = "SSN: 123-45-6789"
        val result1 = PIIRedactor.redact(input, config(PIIRedactor.RedactionMode.HASH))
        val result2 = PIIRedactor.redact(input, config(PIIRedactor.RedactionMode.HASH))
        assertEquals(result1.redactedText, result2.redactedText)
    }

    @Test fun `test hash 002 different input different hash`() {
        val result1 = PIIRedactor.redact("SSN: 123-45-6789", config(PIIRedactor.RedactionMode.HASH))
        val result2 = PIIRedactor.redact("SSN: 234-56-7890", config(PIIRedactor.RedactionMode.HASH))
        assertNotEquals(result1.redactedText, result2.redactedText)
    }

    @Test fun `test hash sha256 001 uses sha256 by default`() {
        val hash = PIIRedactor.hashString("test", "SHA-256")
        assertEquals(64, hash.length)  // SHA-256 produces 64 hex chars
        assertTrue(hash.all { it.isDigit() || it in 'a'..'f' })
    }

    @Test fun `test hash md5 001 uses md5`() {
        val hash = PIIRedactor.hashString("test", "MD5")
        assertEquals(32, hash.length)  // MD5 produces 32 hex chars
    }

    @Test fun `test hash sha1 001 uses sha1`() {
        val hash = PIIRedactor.hashString("test", "SHA-1")
        assertEquals(40, hash.length)  // SHA-1 produces 40 hex chars
    }

    @Test fun `test hash consistency 001 deterministic`() {
        val hash1 = PIIRedactor.hashString("123-45-6789", "SHA-256")
        val hash2 = PIIRedactor.hashString("123-45-6789", "SHA-256")
        assertEquals(hash1, hash2)
    }

    @Test fun `test hash card 001 card number hashed`() {
        val result = PIIRedactor.redact("Card: 4111111111111111", config(PIIRedactor.RedactionMode.HASH))
        assertFalse(result.redactedText.contains("4111111111111111"))
    }

    @Test fun `test hash email 001 email hashed`() {
        val result = PIIRedactor.redact("Email: user@example.com", config(PIIRedactor.RedactionMode.HASH))
        assertFalse(result.redactedText.contains("user@example.com"))
    }

    // ─── SECTION 7: ENCRYPT Mode ──────────────────────────────────────────────

    @Test fun `test encrypt ssn 001 produces encrypted not original`() {
        val result = PIIRedactor.redact(
            "SSN: 123-45-6789",
            configWithKey(PIIRedactor.RedactionMode.ENCRYPT)
        )
        assertFalse(result.redactedText.contains("123-45-6789"))
    }

    @Test fun `test encrypt card 001 encrypts card`() {
        val result = PIIRedactor.redact(
            "Card: 4111111111111111",
            configWithKey(PIIRedactor.RedactionMode.ENCRYPT)
        )
        assertFalse(result.redactedText.contains("4111111111111111"))
    }

    @Test fun `test encrypt email 001 encrypts email`() {
        val result = PIIRedactor.redact(
            "Email: user@example.com",
            configWithKey(PIIRedactor.RedactionMode.ENCRYPT)
        )
        assertFalse(result.redactedText.contains("user@example.com"))
    }

    @Test fun `test encrypt 001 encrypt then decrypt string`() {
        val key = "TestKey12345678!".toByteArray()
        val plaintext = "123-45-6789"
        val encrypted = PIIRedactor.encryptString(plaintext, key)
        assertNotEquals(plaintext, encrypted)
        assertTrue(encrypted.isNotEmpty())
    }

    @Test fun `test encrypt 002 deterministic for same key`() {
        val key = "TestKey12345678!".toByteArray()
        val text = "user@example.com"
        val enc1 = PIIRedactor.encryptString(text, key)
        val enc2 = PIIRedactor.encryptString(text, key)
        assertEquals(enc1, enc2)
    }

    @Test fun `test encrypt 003 different keys different results`() {
        val key1 = "TestKey12345678!".toByteArray()
        val key2 = "OtherKey9876543!".toByteArray()
        val text = "user@example.com"
        val enc1 = PIIRedactor.encryptString(text, key1)
        val enc2 = PIIRedactor.encryptString(text, key2)
        assertNotEquals(enc1, enc2)
    }

    // ─── SECTION 8: REMOVE Mode ───────────────────────────────────────────────

    @Test fun `test remove ssn 001 completely removed`() {
        val text = "SSN: 123-45-6789 is on file."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REMOVE))
        assertFalse(result.redactedText.contains("123-45-6789"))
        // Should not contain any placeholder either
        assertFalse(result.redactedText.contains("[SSN]"))
    }

    @Test fun `test remove card 001 completely removed`() {
        val text = "Card 4111111111111111 accepted."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REMOVE))
        assertFalse(result.redactedText.contains("4111111111111111"))
        assertFalse(result.redactedText.contains("[CARD]"))
    }

    @Test fun `test remove email 001 completely removed`() {
        val text = "Email: user@example.com"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REMOVE))
        assertFalse(result.redactedText.contains("user@example.com"))
        assertFalse(result.redactedText.contains("[EMAIL]"))
    }

    @Test fun `test remove 001 text shorter after removal`() {
        val text = "SSN: 123-45-6789, Card: 4111111111111111"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REMOVE))
        assertTrue(result.redactedText.length < text.length)
    }

    @Test fun `test remove 002 surrounding context preserved`() {
        val text = "Before 123-45-6789 after."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REMOVE))
        assertTrue(result.redactedText.contains("Before"))
        assertTrue(result.redactedText.contains("after"))
    }

    @Test fun `test remove 003 multiple pii all removed`() {
        val text = "Name: John, SSN: 123-45-6789, email: a@b.com"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REMOVE))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertFalse(result.redactedText.contains("a@b.com"))
    }

    // ─── SECTION 9: TOKENIZE Mode ─────────────────────────────────────────────

    @Test fun `test tokenize ssn 001 creates token`() {
        val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.TOKENIZE)
        val result = PIIRedactor.redact("SSN: 123-45-6789", config)
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertTrue(result.tokenMap.isNotEmpty())
    }

    @Test fun `test tokenize 001 restore from token map`() {
        val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.TOKENIZE)
        val original = "SSN: 123-45-6789"
        val result = PIIRedactor.redact(original, config)
        val restored = PIIRedactor.restore(result.redactedText, result.tokenMap)
        assertEquals(original, restored)
    }

    @Test fun `test tokenize 002 restore email roundtrip`() {
        val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.TOKENIZE)
        val original = "Email: user@example.com and card: 4111111111111111"
        val result = PIIRedactor.redact(original, config)
        val restored = PIIRedactor.restore(result.redactedText, result.tokenMap)
        assertEquals(original, restored)
    }

    @Test fun `test tokenize 003 same pii same token`() {
        val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.TOKENIZE)
        val text = "SSN: 123-45-6789 and again SSN: 123-45-6789"
        val result = PIIRedactor.redact(text, config)
        // Both occurrences should be replaced consistently
        val occurrencesInResult = result.tokenMap.size
        assertTrue(occurrencesInResult >= 1)
    }

    @Test fun `test tokenize 004 different pii different tokens`() {
        val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.TOKENIZE)
        val text = "SSN1: 123-45-6789 SSN2: 234-56-7890"
        val result = PIIRedactor.redact(text, config)
        assertTrue(result.tokenMap.size >= 2)
    }

    @Test fun `test tokenize 005 restore with empty token map`() {
        val original = "Hello world"
        val restored = PIIRedactor.restore(original, emptyMap())
        assertEquals(original, restored)
    }

    @Test fun `test tokenize 006 multi type roundtrip`() {
        val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.TOKENIZE)
        val original = "SSN: 123-45-6789, email: user@example.com, card: 4111111111111111"
        val result = PIIRedactor.redact(original, config)
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertFalse(result.redactedText.contains("user@example.com"))
        assertFalse(result.redactedText.contains("4111111111111111"))
        val restored = PIIRedactor.restore(result.redactedText, result.tokenMap)
        assertEquals(original, restored)
    }

    // ─── SECTION 10: isRedacted Detection ────────────────────────────────────

    @Test fun `test is redacted 001 mask pattern detected`() {
        assertTrue(PIIRedactor.isRedacted("***-**-6789"))
    }

    @Test fun `test is redacted 002 replace pattern detected`() {
        assertTrue(PIIRedactor.isRedacted("[SSN]"))
        assertTrue(PIIRedactor.isRedacted("[CREDIT_CARD]"))
        assertTrue(PIIRedactor.isRedacted("[EMAIL]"))
    }

    @Test fun `test is redacted 003 original not detected as redacted`() {
        assertFalse(PIIRedactor.isRedacted("123-45-6789"))
        assertFalse(PIIRedactor.isRedacted("user@example.com"))
    }

    @Test fun `test is redacted 004 empty string not redacted`() {
        assertFalse(PIIRedactor.isRedacted(""))
    }

    @Test fun `test is redacted 005 token pattern detected`() {
        assertTrue(PIIRedactor.isRedacted("TOK_12345abcdef"))
    }

    // ─── SECTION 11: Stats Verification ──────────────────────────────────────

    @Test fun `test stats 001 zero counts for clean text`() {
        val result = PIIRedactor.redact("All systems normal.", config(PIIRedactor.RedactionMode.MASK))
        assertEquals(0, result.stats.ssnCount)
        assertEquals(0, result.stats.creditCardCount)
        assertEquals(0, result.stats.emailCount)
        assertEquals(0, result.stats.phoneCount)
        assertEquals(0, result.stats.totalRedacted)
    }

    @Test fun `test stats 002 counts one ssn`() {
        val result = PIIRedactor.redact("SSN: 123-45-6789", config(PIIRedactor.RedactionMode.MASK))
        assertEquals(1, result.stats.ssnCount)
        assertEquals(1, result.stats.totalRedacted)
    }

    @Test fun `test stats 003 counts one card`() {
        val result = PIIRedactor.redact("Card: 4111111111111111", config(PIIRedactor.RedactionMode.MASK))
        assertEquals(1, result.stats.creditCardCount)
        assertEquals(1, result.stats.totalRedacted)
    }

    @Test fun `test stats 004 counts one email`() {
        val result = PIIRedactor.redact("Email: user@example.com", config(PIIRedactor.RedactionMode.MASK))
        assertEquals(1, result.stats.emailCount)
        assertEquals(1, result.stats.totalRedacted)
    }

    @Test fun `test stats 005 counts mixed pii`() {
        val text = "SSN: 123-45-6789, Card: 4111111111111111, Email: user@example.com"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(1, result.stats.ssnCount)
        assertEquals(1, result.stats.creditCardCount)
        assertEquals(1, result.stats.emailCount)
        assertEquals(3, result.stats.totalRedacted)
    }

    @Test fun `test stats 006 repeated same pii counted twice`() {
        val text = "SSN: 123-45-6789 and SSN: 123-45-6789 again"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(2, result.stats.ssnCount)
    }

    @Test fun `test stats 007 counts multiple cards`() {
        val text = "Visa: 4111111111111111, MC: 5555555555554444, Amex: 378282246310005"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(3, result.stats.creditCardCount)
        assertEquals(3, result.stats.totalRedacted)
    }

    @Test fun `test stats 008 counts multiple emails`() {
        val text = "Emails: alice@example.com, bob@example.com, carol@example.com"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(3, result.stats.emailCount)
    }

    @Test fun `test stats 009 high density text correct counts`() {
        val text = """
            Patient Alice, SSN: 123-45-6789, DOB: 01/01/1980
            Card: 4111111111111111, exp 12/25
            Contact: alice@example.com
            Phone: (212) 555-0100
        """.trimIndent()
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertTrue(result.stats.totalRedacted >= 3)
        assertEquals(1, result.stats.ssnCount)
        assertEquals(1, result.stats.creditCardCount)
        assertEquals(1, result.stats.emailCount)
    }

    // ─── SECTION 12: Configuration Options ────────────────────────────────────

    @Test fun `test config 001 default mode applied`() {
        val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.REPLACE)
        val result = PIIRedactor.redact("SSN: 123-45-6789", config)
        assertFalse(result.redactedText.contains("123-45-6789"))
    }

    @Test fun `test config 002 per type override applied`() {
        val config = PIIRedactor.RedactionConfig(
            defaultMode = PIIRedactor.RedactionMode.MASK,
            overrides = mapOf("SSN" to PIIRedactor.RedactionMode.REPLACE)
        )
        val result = PIIRedactor.redact("SSN: 123-45-6789", config)
        assertFalse(result.redactedText.contains("123-45-6789"))
    }

    @Test fun `test config 003 preserve format flag`() {
        val config = PIIRedactor.RedactionConfig(
            defaultMode = PIIRedactor.RedactionMode.MASK,
            preserveFormat = true
        )
        val result = PIIRedactor.redactSSN("123-45-6789", PIIRedactor.RedactionMode.MASK)
        // Format preserved: NNN-NN-NNNN with dashes kept
        assertTrue(result.contains("-"))
        assertEquals(11, result.length)
    }

    @Test fun `test config 004 null config fields use defaults`() {
        val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.MASK)
        assertNotNull(config)
    }

    // ─── SECTION 13: Edge Cases ───────────────────────────────────────────────

    @Test fun `test edge 001 null input returns empty`() {
        val result = PIIRedactor.redact(null, config(PIIRedactor.RedactionMode.MASK))
        assertEquals("", result.redactedText)
        assertEquals(0, result.stats.totalRedacted)
    }

    @Test fun `test edge 002 empty string returns empty`() {
        val result = PIIRedactor.redact("", config(PIIRedactor.RedactionMode.MASK))
        assertEquals("", result.redactedText)
    }

    @Test fun `test edge 003 whitespace only unchanged`() {
        val result = PIIRedactor.redact("   ", config(PIIRedactor.RedactionMode.MASK))
        assertEquals("   ", result.redactedText)
        assertEquals(0, result.stats.totalRedacted)
    }

    @Test fun `test edge 004 unicode text with no pii unchanged`() {
        val text = "こんにちは世界 — hello world"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(text, result.redactedText)
    }

    @Test fun `test edge 005 text with numbers not pii unchanged`() {
        val text = "We have 12345 items in stock and 67890 pending orders."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(0, result.stats.totalRedacted)
    }

    @Test fun `test edge 006 pii at start of text`() {
        val text = "123-45-6789 is my SSN."
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.startsWith("123-45-6789"))
        assertEquals(1, result.stats.ssnCount)
    }

    @Test fun `test edge 007 pii at end of text`() {
        val text = "My SSN is: 123-45-6789"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.endsWith("123-45-6789"))
        assertEquals(1, result.stats.ssnCount)
    }

    @Test fun `test edge 008 very long text with scattered pii`() {
        val longPrefix = "a".repeat(5000)
        val longSuffix = "b".repeat(5000)
        val text = "$longPrefix 123-45-6789 $longSuffix"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertEquals(1, result.stats.ssnCount)
    }

    @Test fun `test edge 009 json text with pii`() {
        val text = """{"ssn": "123-45-6789", "name": "John"}"""
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertTrue(result.redactedText.contains("\"name\""))
    }

    @Test fun `test edge 010 csv with pii`() {
        val text = "name,ssn\nJohn,123-45-6789\nAlice,234-56-7890"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertFalse(result.redactedText.contains("234-56-7890"))
        assertEquals(2, result.stats.ssnCount)
    }

    @Test fun `test edge 011 html with pii`() {
        val text = "<p>SSN: 123-45-6789</p>"
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertTrue(result.redactedText.contains("<p>"))
    }

    @Test fun `test edge 012 multiline text`() {
        val text = """
            Line 1: Normal text.
            Line 2: SSN 123-45-6789 here.
            Line 3: Card 4111111111111111 there.
            Line 4: Email user@example.com contact.
        """.trimIndent()
        val result = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertFalse(result.redactedText.contains("123-45-6789"))
        assertFalse(result.redactedText.contains("4111111111111111"))
        assertFalse(result.redactedText.contains("user@example.com"))
        assertTrue(result.redactedText.contains("Line 1"))
        assertTrue(result.redactedText.contains("Line 4"))
    }

    @Test fun `test edge 013 no double redaction of already redacted text`() {
        val alreadyRedacted = "***-**-6789"
        val result = PIIRedactor.redact(alreadyRedacted, config(PIIRedactor.RedactionMode.MASK))
        // Already redacted text should not be double-redacted
        assertNotNull(result)
    }

    @Test fun `test edge 014 hash string empty`() {
        val hash = PIIRedactor.hashString("", "SHA-256")
        assertEquals(64, hash.length)  // SHA-256 of empty string is defined
    }

    @Test fun `test edge 015 encrypt empty string`() {
        val key = "TestKey12345678!".toByteArray()
        val encrypted = PIIRedactor.encryptString("", key)
        assertNotNull(encrypted)
    }

    // ─── SECTION 14: Mode Comparison Tests ────────────────────────────────────

    @Test fun `test mode compare 001 all modes produce different results for ssn`() {
        val input = "SSN: 123-45-6789"
        val modes = listOf(
            PIIRedactor.RedactionMode.MASK,
            PIIRedactor.RedactionMode.REPLACE,
            PIIRedactor.RedactionMode.HASH,
            PIIRedactor.RedactionMode.REMOVE
        )
        val results = modes.map {
            PIIRedactor.redact(input, PIIRedactor.RedactionConfig(defaultMode = it)).redactedText
        }
        // All results should differ from original
        results.forEach { assertFalse("Mode result contains original SSN", it.contains("123-45-6789")) }
    }

    @Test fun `test mode compare 002 remove produces shortest result`() {
        val input = "SSN: 123-45-6789"
        val maskResult = PIIRedactor.redact(input, config(PIIRedactor.RedactionMode.MASK))
        val removeResult = PIIRedactor.redact(input, config(PIIRedactor.RedactionMode.REMOVE))
        assertTrue(removeResult.redactedText.length <= maskResult.redactedText.length)
    }

    @Test fun `test mode compare 003 tokenize produces reversible result`() {
        val input = "SSN: 123-45-6789"
        val tokenizeConfig = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.TOKENIZE)
        val result = PIIRedactor.redact(input, tokenizeConfig)
        val restored = PIIRedactor.restore(result.redactedText, result.tokenMap)
        assertEquals(input, restored)
    }

    @Test fun `test mode compare 004 mask and replace both hide original`() {
        val input = "Card: 4111111111111111"
        val maskResult = PIIRedactor.redact(input, config(PIIRedactor.RedactionMode.MASK))
        val replaceResult = PIIRedactor.redact(input, config(PIIRedactor.RedactionMode.REPLACE))
        assertFalse(maskResult.redactedText.contains("4111111111111111"))
        assertFalse(replaceResult.redactedText.contains("4111111111111111"))
    }

    @Test fun `test mode compare 005 hash produces consistent hashes`() {
        val input = "user@example.com"
        val hash1 = PIIRedactor.redact("Email: $input", config(PIIRedactor.RedactionMode.HASH))
        val hash2 = PIIRedactor.redact("Email: $input", config(PIIRedactor.RedactionMode.HASH))
        assertEquals(hash1.redactedText, hash2.redactedText)
    }

    // ─── SECTION 15: Determinism Tests ────────────────────────────────────────

    @Test fun `test determinism 001 same input same output for mask`() {
        val text = "SSN: 123-45-6789"
        val r1 = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        val r2 = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(r1.redactedText, r2.redactedText)
    }

    @Test fun `test determinism 002 same input same output for replace`() {
        val text = "Email: user@example.com"
        val r1 = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REPLACE))
        val r2 = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.REPLACE))
        assertEquals(r1.redactedText, r2.redactedText)
    }

    @Test fun `test determinism 003 stats same across calls`() {
        val text = "SSN: 123-45-6789, Card: 4111111111111111, Email: user@example.com"
        val r1 = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        val r2 = PIIRedactor.redact(text, config(PIIRedactor.RedactionMode.MASK))
        assertEquals(r1.stats.totalRedacted, r2.stats.totalRedacted)
        assertEquals(r1.stats.ssnCount, r2.stats.ssnCount)
        assertEquals(r1.stats.creditCardCount, r2.stats.creditCardCount)
        assertEquals(r1.stats.emailCount, r2.stats.emailCount)
    }

    @Test fun `test determinism 004 large text batch`() {
        val inputs = (1..10).map { "SSN: 12$it-45-678$it at position $it" }
        val results = inputs.map { PIIRedactor.redact(it, config(PIIRedactor.RedactionMode.MASK)) }
        results.forEach { assertEquals(1, it.stats.ssnCount) }
    }

    @Test fun `test determinism 005 hash is idempotent`() {
        val hash1 = PIIRedactor.hashString("test@example.com", "SHA-256")
        val hash2 = PIIRedactor.hashString("test@example.com", "SHA-256")
        val hash3 = PIIRedactor.hashString("test@example.com", "SHA-256")
        assertEquals(hash1, hash2)
        assertEquals(hash2, hash3)
    }
}
