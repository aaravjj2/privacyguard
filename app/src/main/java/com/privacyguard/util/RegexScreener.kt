package com.privacyguard.util

import com.privacyguard.ml.EntityType
import com.privacyguard.ml.PIIEntity

/**
 * Regex-based pre-screener that runs before the ML model to quick-filter
 * text that is obviously not PII, reducing unnecessary inference calls.
 * Also performs Luhn check validation for credit card numbers.
 */
object RegexScreener {

    // Credit card: 13-19 digit numbers, optionally with spaces/dashes
    private val CREDIT_CARD_REGEX = Regex(
        """(?<!\d)(\d[\s-]?){13,19}(?!\d)"""
    )

    // SSN: XXX-XX-XXXX pattern
    private val SSN_REGEX = Regex(
        """\b\d{3}[-.\s]?\d{2}[-.\s]?\d{4}\b"""
    )

    // Email: standard email pattern
    private val EMAIL_REGEX = Regex(
        """\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b"""
    )

    // Phone: various US/international patterns
    private val PHONE_REGEX = Regex(
        """\b(?:\+?1[-.\s]?)?(?:\(\d{3}\)|\d{3})[-.\s]?\d{3}[-.\s]?\d{4}\b"""
    )

    // API Key patterns: common prefixes and formats
    private val API_KEY_REGEX = Regex(
        """\b(?:sk[-_](?:live|test|prod)|api[-_]?key|AKIA|ghp_xxx|gho_|ghs_|ghr_|glpat-|xox[bpars]-)[A-Za-z0-9_\-]{10,}\b"""
    )

    // Generic long hex/base64 strings that look like secrets
    private val SECRET_REGEX = Regex(
        """\b[A-Za-z0-9+/=_\-]{32,}\b"""
    )

    // Known false positive domains
    private val FALSE_POSITIVE_EMAILS = setOf(
        "example.com", "test.com", "test.test", "example.org",
        "localhost", "placeholder.com"
    )

    /**
     * Pre-screen text for potential PII patterns.
     * Returns true if the text likely contains PII and should go through ML inference.
     */
    fun containsPotentialPII(text: String): Boolean {
        if (text.isBlank()) return false

        return CREDIT_CARD_REGEX.containsMatchIn(text) ||
               SSN_REGEX.containsMatchIn(text) ||
               EMAIL_REGEX.containsMatchIn(text) ||
               PHONE_REGEX.containsMatchIn(text) ||
               API_KEY_REGEX.containsMatchIn(text)
    }

    /**
     * Extract regex-detected entities (used as a fallback when ML model is unavailable).
     */
    fun extractEntities(text: String): List<PIIEntity> {
        val entities = mutableListOf<PIIEntity>()

        // Credit cards
        CREDIT_CARD_REGEX.findAll(text).forEach { match ->
            val digitsOnly = match.value.replace(Regex("[\\s-]"), "")
            if (digitsOnly.length in 13..19 && luhnCheck(digitsOnly)) {
                entities.add(
                    PIIEntity(
                        entityType = EntityType.CREDIT_CARD,
                        confidence = 0.95f,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        rawText = match.value
                    )
                )
            }
        }

        // SSNs
        SSN_REGEX.findAll(text).forEach { match ->
            val digitsOnly = match.value.replace(Regex("[\\-. ]"), "")
            if (isValidSSN(digitsOnly)) {
                entities.add(
                    PIIEntity(
                        entityType = EntityType.SSN,
                        confidence = 0.93f,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        rawText = match.value
                    )
                )
            }
        }

        // Emails
        EMAIL_REGEX.findAll(text).forEach { match ->
            val email = match.value
            val domain = email.substringAfter("@").lowercase()
            if (domain !in FALSE_POSITIVE_EMAILS) {
                entities.add(
                    PIIEntity(
                        entityType = EntityType.EMAIL,
                        confidence = 0.97f,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        rawText = match.value
                    )
                )
            }
        }

        // Phone numbers
        PHONE_REGEX.findAll(text).forEach { match ->
            val digitsOnly = match.value.replace(Regex("[^\\d]"), "")
            if (digitsOnly.length >= 10) {
                entities.add(
                    PIIEntity(
                        entityType = EntityType.PHONE,
                        confidence = 0.90f,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        rawText = match.value
                    )
                )
            }
        }

        // API keys
        API_KEY_REGEX.findAll(text).forEach { match ->
            entities.add(
                PIIEntity(
                    entityType = EntityType.API_KEY,
                    confidence = 0.92f,
                    startIndex = match.range.first,
                    endIndex = match.range.last + 1,
                    rawText = match.value
                )
            )
        }

        return entities
    }

    /**
     * Luhn algorithm check for credit card number validation.
     * Returns true if the number passes the Luhn check (valid credit card format).
     */
    fun luhnCheck(number: String): Boolean {
        val digits = number.filter { it.isDigit() }
        if (digits.length < 13 || digits.length > 19) return false

        var sum = 0
        var alternate = false

        for (i in digits.length - 1 downTo 0) {
            var n = digits[i].digitToInt()
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }

        return sum % 10 == 0
    }

    /**
     * Basic SSN validation: not all zeros in any group, not a known invalid prefix.
     */
    private fun isValidSSN(digits: String): Boolean {
        if (digits.length != 9) return false
        val area = digits.substring(0, 3).toIntOrNull() ?: return false
        val group = digits.substring(3, 5).toIntOrNull() ?: return false
        val serial = digits.substring(5, 9).toIntOrNull() ?: return false

        // Invalid patterns
        if (area == 0 || group == 0 || serial == 0) return false
        if (area == 666) return false
        if (area >= 900) return false

        return true
    }
}
