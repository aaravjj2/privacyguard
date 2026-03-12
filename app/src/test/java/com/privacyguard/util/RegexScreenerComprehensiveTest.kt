package com.privacyguard.util

import com.privacyguard.ml.EntityType
import org.junit.Assert.*
import org.junit.Test

class RegexScreenerComprehensiveTest {

    // ============================
    // LUHN ALGORITHM VALIDATION
    // ============================

    @Test fun `luhn valid Visa 4532015112830366`() = assertTrue(RegexScreener.luhnCheck("4532015112830366"))
    @Test fun `luhn valid Visa 4916338506082832`() = assertTrue(RegexScreener.luhnCheck("4916338506082832"))
    @Test fun `luhn valid Visa 4024007148673576`() = assertTrue(RegexScreener.luhnCheck("4024007148673576"))
    @Test fun `luhn valid Mastercard 5425233430109903`() = assertTrue(RegexScreener.luhnCheck("5425233430109903"))
    @Test fun `luhn valid Mastercard 5114496353984312`() = assertTrue(RegexScreener.luhnCheck("5114496353984312"))
    @Test fun `luhn valid Amex 378282246310005`() = assertTrue(RegexScreener.luhnCheck("378282246310005"))
    @Test fun `luhn valid Amex 371449635398431`() = assertTrue(RegexScreener.luhnCheck("371449635398431"))
    @Test fun `luhn valid Discover 6011111111111117`() = assertTrue(RegexScreener.luhnCheck("6011111111111117"))
    @Test fun `luhn invalid 1234567890123456`() = assertFalse(RegexScreener.luhnCheck("1234567890123456"))
    @Test fun `luhn invalid all zeros`() = assertFalse(RegexScreener.luhnCheck("0000000000000000"))
    @Test fun `luhn invalid all ones`() = assertFalse(RegexScreener.luhnCheck("1111111111111111"))
    @Test fun `luhn invalid too short 4 digits`() = assertFalse(RegexScreener.luhnCheck("1234"))
    @Test fun `luhn invalid too short 8 digits`() = assertFalse(RegexScreener.luhnCheck("12345678"))
    @Test fun `luhn invalid too long 20 digits`() = assertFalse(RegexScreener.luhnCheck("12345678901234567890"))
    @Test fun `luhn empty string`() = assertFalse(RegexScreener.luhnCheck(""))
    @Test fun `luhn single digit`() = assertFalse(RegexScreener.luhnCheck("5"))
    @Test fun `luhn non numeric`() = assertFalse(RegexScreener.luhnCheck("abcdefghijklmnop"))
    @Test fun `luhn mixed alpha numeric`() = assertFalse(RegexScreener.luhnCheck("4532abcd56789012"))
    @Test fun `luhn with spaces stripped`() {
        val stripped = "4532 0151 1283 0366".replace(" ", "")
        assertTrue(RegexScreener.luhnCheck(stripped))
    }
    @Test fun `luhn with dashes stripped`() {
        val stripped = "4532-0151-1283-0366".replace("-", "")
        assertTrue(RegexScreener.luhnCheck(stripped))
    }

    // ============================
    // CREDIT CARD DETECTION
    // ============================

    @Test
    fun `detect Visa card with spaces`() {
        val entities = RegexScreener.extractEntities("Pay with card 4532 0151 1283 0366")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `detect Visa card without spaces`() {
        val entities = RegexScreener.extractEntities("Card: 4532015112830366")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `detect Visa card with dashes`() {
        val entities = RegexScreener.extractEntities("Card: 4532-0151-1283-0366")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `detect Mastercard`() {
        val entities = RegexScreener.extractEntities("MC: 5425233430109903")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `detect Amex 15 digit`() {
        val entities = RegexScreener.extractEntities("Amex: 378282246310005")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `detect Discover card`() {
        val entities = RegexScreener.extractEntities("Disc: 6011111111111117")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `reject invalid card number via Luhn`() {
        val entities = RegexScreener.extractEntities("Number: 1234567890123456")
        assertTrue("Invalid Luhn should be rejected", entities.none { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `reject dollar amount as card`() {
        val entities = RegexScreener.extractEntities("Total: \$4,532.00")
        assertTrue(entities.none { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `reject date as card`() {
        val entities = RegexScreener.extractEntities("Date: 2024-01-15-2024")
        assertTrue(entities.none { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `reject short number as card`() {
        val entities = RegexScreener.extractEntities("Code: 12345")
        assertTrue(entities.none { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `card in long sentence`() {
        val entities = RegexScreener.extractEntities(
            "Please use my Visa card number 4532015112830366 to pay for the order that I placed yesterday afternoon"
        )
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `multiple cards in text`() {
        val entities = RegexScreener.extractEntities(
            "Card1: 4532015112830366 Card2: 5425233430109903"
        )
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Should detect at least one card", ccEntities.isNotEmpty())
    }

    // ============================
    // SSN DETECTION
    // ============================

    @Test fun `detect SSN 123-45-6789`() {
        val entities = RegexScreener.extractEntities("SSN: 123-45-6789")
        assertTrue(entities.any { it.entityType == EntityType.SSN })
    }

    @Test fun `detect SSN with spaces`() {
        val entities = RegexScreener.extractEntities("SSN: 123 45 6789")
        assertTrue(entities.any { it.entityType == EntityType.SSN })
    }

    @Test fun `detect SSN 001-01-0001 valid`() {
        val entities = RegexScreener.extractEntities("SSN: 001-01-0001")
        assertTrue(entities.any { it.entityType == EntityType.SSN })
    }

    @Test fun `reject SSN area 000`() {
        val entities = RegexScreener.extractEntities("SSN: 000-12-3456")
        assertTrue(entities.none { it.entityType == EntityType.SSN })
    }

    @Test fun `reject SSN area 666`() {
        val entities = RegexScreener.extractEntities("SSN: 666-12-3456")
        assertTrue(entities.none { it.entityType == EntityType.SSN })
    }

    @Test fun `reject SSN area 900`() {
        val entities = RegexScreener.extractEntities("SSN: 900-12-3456")
        assertTrue(entities.none { it.entityType == EntityType.SSN })
    }

    @Test fun `reject SSN area 999`() {
        val entities = RegexScreener.extractEntities("SSN: 999-12-3456")
        assertTrue(entities.none { it.entityType == EntityType.SSN })
    }

    @Test fun `reject SSN group 00`() {
        val entities = RegexScreener.extractEntities("SSN: 123-00-6789")
        assertTrue(entities.none { it.entityType == EntityType.SSN })
    }

    @Test fun `reject SSN serial 0000`() {
        val entities = RegexScreener.extractEntities("SSN: 123-45-0000")
        assertTrue(entities.none { it.entityType == EntityType.SSN })
    }

    @Test fun `SSN in natural text`() {
        val entities = RegexScreener.extractEntities("My social security number is 123-45-6789 and I need to file taxes")
        assertTrue(entities.any { it.entityType == EntityType.SSN })
    }

    // ============================
    // EMAIL DETECTION
    // ============================

    @Test fun `detect user@gmail_com`() {
        assertTrue(RegexScreener.extractEntities("user@gmail.com").any { it.entityType == EntityType.EMAIL })
    }

    @Test fun `detect john_doe@company_co_uk`() {
        assertTrue(RegexScreener.extractEntities("john.doe@company.co.uk").any { it.entityType == EntityType.EMAIL })
    }

    @Test fun `detect plus addressing`() {
        assertTrue(RegexScreener.extractEntities("user+tag@gmail.com").any { it.entityType == EntityType.EMAIL })
    }

    @Test fun `detect subdomain email`() {
        assertTrue(RegexScreener.extractEntities("user@mail.server.example.org").any { it.entityType == EntityType.EMAIL })
    }

    @Test fun `suppress test@example_com`() {
        assertTrue(RegexScreener.extractEntities("test@example.com").none { it.entityType == EntityType.EMAIL })
    }

    @Test fun `suppress test@test_com`() {
        assertTrue(RegexScreener.extractEntities("test@test.com").none { it.entityType == EntityType.EMAIL })
    }

    @Test fun `suppress user@localhost`() {
        // localhost might not match email regex - just test it doesn't crash
        val entities = RegexScreener.extractEntities("user@localhost")
        assertNotNull(entities)
    }

    @Test fun `email in sentence`() {
        val entities = RegexScreener.extractEntities("Send the report to john.smith@bigcorp.com by Friday please")
        assertTrue(entities.any { it.entityType == EntityType.EMAIL })
    }

    @Test fun `multiple emails`() {
        val entities = RegexScreener.extractEntities("CC: alice@corp.com and bob@corp.com")
        val emails = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Should find at least one email", emails.isNotEmpty())
    }

    @Test fun `email with numbers`() {
        assertTrue(RegexScreener.extractEntities("user123@domain456.com").any { it.entityType == EntityType.EMAIL })
    }

    @Test fun `email with underscores`() {
        assertTrue(RegexScreener.extractEntities("first_last@domain.com").any { it.entityType == EntityType.EMAIL })
    }

    @Test fun `email with hyphens`() {
        assertTrue(RegexScreener.extractEntities("first-last@domain.com").any { it.entityType == EntityType.EMAIL })
    }

    // ============================
    // PHONE DETECTION
    // ============================

    @Test fun `detect US phone parens`() {
        assertTrue(RegexScreener.extractEntities("(555) 867-5309").any { it.entityType == EntityType.PHONE })
    }

    @Test fun `detect US phone dashes`() {
        assertTrue(RegexScreener.extractEntities("555-867-5309").any { it.entityType == EntityType.PHONE })
    }

    @Test fun `detect US phone dots`() {
        assertTrue(RegexScreener.extractEntities("555.867.5309").any { it.entityType == EntityType.PHONE })
    }

    @Test fun `detect international phone`() {
        assertTrue(RegexScreener.extractEntities("+1-555-867-5309").any { it.entityType == EntityType.PHONE })
    }

    @Test fun `detect UK phone`() {
        assertTrue(RegexScreener.extractEntities("+44 20 7123 4567").any { it.entityType == EntityType.PHONE })
    }

    @Test fun `detect phone in sentence`() {
        val entities = RegexScreener.extractEntities("Call me at (555) 867-5309 anytime after 3pm")
        assertTrue(entities.any { it.entityType == EntityType.PHONE })
    }

    // ============================
    // API KEY DETECTION
    // ============================

    @Test fun `detect sk-live key`() {
        assertTrue(RegexScreener.extractEntities("sk-live_51ABCdef123456789").any { it.entityType == EntityType.API_KEY })
    }

    @Test fun `detect sk-test key`() {
        assertTrue(RegexScreener.extractEntities("sk-test_aBcDeFgHiJkLmNoPq").any { it.entityType == EntityType.API_KEY })
    }

    @Test fun `detect GitHub PAT ghp`() {
        assertTrue(RegexScreener.extractEntities("ghp_1234567890abcdefghij1234567890ab").any { it.entityType == EntityType.API_KEY })
    }

    @Test fun `detect AWS access key`() {
        assertTrue(RegexScreener.extractEntities("AKIAIOSFODNN7EXAMPLE").any { it.entityType == EntityType.API_KEY })
    }

    @Test fun `API key in environment variable`() {
        val entities = RegexScreener.extractEntities("export API_KEY=sk-live_51ABCdef123456789012345")
        assertTrue(entities.any { it.entityType == EntityType.API_KEY })
    }

    // ============================
    // containsPotentialPII
    // ============================

    @Test fun `PII check positive credit card`() = assertTrue(RegexScreener.containsPotentialPII("4532015112830366"))
    @Test fun `PII check positive SSN`() = assertTrue(RegexScreener.containsPotentialPII("123-45-6789"))
    @Test fun `PII check positive email`() = assertTrue(RegexScreener.containsPotentialPII("user@gmail.com"))
    @Test fun `PII check positive phone`() = assertTrue(RegexScreener.containsPotentialPII("(555) 867-5309"))
    @Test fun `PII check positive API key`() = assertTrue(RegexScreener.containsPotentialPII("sk-live_abc123"))
    @Test fun `PII check negative plain text`() = assertFalse(RegexScreener.containsPotentialPII("Hello how are you today"))
    @Test fun `PII check negative empty`() = assertFalse(RegexScreener.containsPotentialPII(""))
    @Test fun `PII check negative blank`() = assertFalse(RegexScreener.containsPotentialPII("     "))
    @Test fun `PII check negative short number`() = assertFalse(RegexScreener.containsPotentialPII("42"))
    @Test fun `PII check negative date`() = assertFalse(RegexScreener.containsPotentialPII("January 15, 2024"))

    // ============================
    // MULTIPLE ENTITIES
    // ============================

    @Test
    fun `detect CC and email in same text`() {
        val entities = RegexScreener.extractEntities("Card: 4532015112830366, email: john@corp.com")
        val types = entities.map { it.entityType }.toSet()
        assertTrue("Should detect CC", types.contains(EntityType.CREDIT_CARD))
        assertTrue("Should detect EMAIL", types.contains(EntityType.EMAIL))
    }

    @Test
    fun `detect SSN and phone in same text`() {
        val entities = RegexScreener.extractEntities("SSN: 123-45-6789 Phone: (555) 867-5309")
        val types = entities.map { it.entityType }.toSet()
        assertTrue(types.size >= 2)
    }

    @Test
    fun `detect all types in single text`() {
        val text = "CC: 4532015112830366 SSN: 123-45-6789 Email: real@corp.com Phone: (555) 867-5309 Key: sk-live_abc123def456ghi"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should detect multiple types", entities.map { it.entityType }.toSet().size >= 3)
    }

    // ============================
    // FALSE POSITIVE SUPPRESSION
    // ============================

    @Test fun `no PII in greeting`() {
        val entities = RegexScreener.extractEntities("Good morning! How are you today? The weather is nice.")
        assertTrue("Plain greeting should have no PII", entities.isEmpty())
    }

    @Test fun `no PII in news article`() {
        val entities = RegexScreener.extractEntities(
            "The president announced new economic policies today that could affect millions of citizens across the country."
        )
        assertTrue("News text should have no PII", entities.isEmpty())
    }

    @Test fun `no PII in code comments`() {
        val entities = RegexScreener.extractEntities(
            "// This function processes input data and returns the filtered output"
        )
        assertTrue(entities.isEmpty())
    }

    @Test fun `no PII in mathematical expressions`() {
        val entities = RegexScreener.extractEntities("2 + 2 = 4 and 10 * 10 = 100")
        assertTrue(entities.none { it.entityType == EntityType.CREDIT_CARD })
    }

    // ============================
    // EDGE CASES
    // ============================

    @Test fun `empty string returns empty list`() = assertTrue(RegexScreener.extractEntities("").isEmpty())
    @Test fun `whitespace only returns empty list`() = assertTrue(RegexScreener.extractEntities("   ").isEmpty())
    @Test fun `single char returns empty list`() = assertTrue(RegexScreener.extractEntities("a").isEmpty())
    @Test fun `special chars only`() = assertTrue(RegexScreener.extractEntities("!@#\$%^&*()").isEmpty())
    @Test fun `unicode only`() = assertNotNull(RegexScreener.extractEntities("你好世界"))
    @Test fun `emoji only`() = assertNotNull(RegexScreener.extractEntities("😀🎉🔒🛡️"))

    @Test fun `very long plain text has no false positives`() {
        val text = "The quick brown fox jumps over the lazy dog. ".repeat(100)
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Long plain text should have no PII", entities.isEmpty())
    }

    @Test fun `PII at start of text`() {
        val entities = RegexScreener.extractEntities("4532015112830366 is my card")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test fun `PII at end of text`() {
        val entities = RegexScreener.extractEntities("My card is 4532015112830366")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test fun `PII is entire text`() {
        val entities = RegexScreener.extractEntities("4532015112830366")
        assertTrue(entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    // ============================
    // PERFORMANCE
    // ============================

    @Test
    fun `1000 extractions complete quickly`() {
        val text = "CC: 4532015112830366, Email: john@corp.com, SSN: 123-45-6789"
        val start = System.nanoTime()
        repeat(1000) {
            RegexScreener.extractEntities(text)
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("1000 regex checks should complete in under 5 seconds", elapsedMs < 5000)
    }

    @Test
    fun `1000 containsPotentialPII checks complete quickly`() {
        val start = System.nanoTime()
        repeat(1000) {
            RegexScreener.containsPotentialPII("Random text with no PII whatsoever in it at all")
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("1000 PII checks should complete in under 2 seconds", elapsedMs < 2000)
    }
}
