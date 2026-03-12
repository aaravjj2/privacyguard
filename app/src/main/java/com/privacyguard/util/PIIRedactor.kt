package com.privacyguard.util

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

/**
 * Comprehensive PII redaction utility for PrivacyGuard.
 *
 * Supports multiple redaction modes and can redact SSNs, credit cards, emails,
 * phone numbers, API keys, and IP addresses in a piece of text. Also supports
 * reversible tokenization via a token map so that redacted text can later be
 * restored.
 *
 * Usage:
 * ```kotlin
 * val config = PIIRedactor.RedactionConfig(defaultMode = PIIRedactor.RedactionMode.MASK)
 * val result = PIIRedactor.redactAll("My SSN is 123-45-6789", config)
 * println(result.redactedText) // "My SSN is ***-**-6789"
 * ```
 */
object PIIRedactor {

    // -------------------------------------------------------------------------
    // Enums and configuration data classes
    // -------------------------------------------------------------------------

    /**
     * The strategy used to replace a detected PII entity.
     */
    enum class RedactionMode {
        /** Replace with masked characters preserving format (e.g. ***-**-6789) */
        MASK,
        /** Replace with a generic placeholder label (e.g. [SSN REDACTED]) */
        REPLACE,
        /** Replace with a SHA-256 hex digest of the original value */
        HASH,
        /** Replace with an AES-128-ECB Base64 ciphertext of the original value */
        ENCRYPT,
        /** Remove entirely from the text */
        REMOVE,
        /** Replace with a reversible token (e.g. [SSN_1]) and store the mapping */
        TOKENIZE
    }

    /**
     * Per-entity-type redaction configuration.
     *
     * @param defaultMode              The mode applied to entities not listed in [overrides]
     * @param overrides                Per-entity-type mode overrides
     * @param encryptionKey            16-byte AES key used when mode is [RedactionMode.ENCRYPT]
     * @param preserveFormat           Whether masked values should preserve the original format (e.g. dashes in SSNs)
     * @param hashAlgorithm            Digest algorithm name (e.g. "SHA-256", "SHA-1", "MD5")
     * @param customReplacements       Map from entity type to custom replacement string (used in REPLACE mode)
     */
    data class RedactionConfig(
        val defaultMode: RedactionMode = RedactionMode.MASK,
        val overrides: Map<String, RedactionMode> = emptyMap(),
        val encryptionKey: ByteArray = ByteArray(16) { 0x00 },
        val preserveFormat: Boolean = true,
        val hashAlgorithm: String = "SHA-256",
        val customReplacements: Map<String, String> = emptyMap()
    )

    /**
     * Statistics about a redaction operation.
     *
     * @param originalLength   Length of the original text
     * @param redactedLength   Length of the redacted text
     * @param entitiesRedacted Total number of PII entities that were redacted
     * @param redactionRatePercent   Percentage of original characters that were redacted/replaced
     */
    data class RedactionStats(
        val originalLength: Int,
        val redactedLength: Int,
        val entitiesRedacted: Int,
        val redactionRatePercent: Float
    )

    /**
     * Result of a redaction operation.
     *
     * @param redactedText Text with all PII replaced according to the config
     * @param tokenMap     Mapping from token (e.g. "[SSN_1]") to original value — populated only in TOKENIZE mode
     * @param stats        Redaction statistics
     */
    data class RedactionResult(
        val redactedText: String,
        val tokenMap: Map<String, String>,
        val stats: RedactionStats
    )

    // -------------------------------------------------------------------------
    // Patterns (mirrors subset from PIIValidator for standalone use)
    // -------------------------------------------------------------------------

    private val SSN_PATTERN = Regex("""(?<!\d)\d{3}[-\s]\d{2}[-\s]\d{4}(?!\d)|\b\d{9}\b""")
    private val CARD_PATTERN = Regex("""(?:\d{4}[-\s]?){3}\d{4}""")
    private val EMAIL_PATTERN = Regex("""[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""")
    private val PHONE_PATTERN = Regex("""(?:\+?\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}""")
    private val IPV4_PATTERN = Regex("""(?<!\d)(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})(?!\d)""")
    private val API_KEY_GENERIC = Regex("""(?i)(api[_\-]?key|secret|token|password|passwd)\s*[:=]\s*([A-Za-z0-9_\-./+]{20,})""")

    // Token counters (reset per redact call)
    private var tokenCounters = mutableMapOf<String, Int>()

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Redacts all known PII types in [text] using the provided [config].
     *
     * @param text   The text to redact
     * @param config Redaction configuration
     * @return A [RedactionResult] with the redacted text, token map, and stats
     */
    fun redact(text: String, config: RedactionConfig): RedactionResult {
        tokenCounters = mutableMapOf()
        val tokenMap = mutableMapOf<String, String>()
        var current = text

        // Redact in order of specificity — more specific patterns first
        current = redactWithMode(current, SSN_PATTERN, "SSN", config, tokenMap)
        current = redactWithMode(current, CARD_PATTERN, "CREDIT_CARD", config, tokenMap)
        current = redactWithMode(current, EMAIL_PATTERN, "EMAIL", config, tokenMap)
        current = redactWithMode(current, PHONE_PATTERN, "PHONE", config, tokenMap)
        current = redactAPIKeyInternal(current, config, tokenMap)
        current = redactWithMode(current, IPV4_PATTERN, "IP_ADDRESS", config, tokenMap)

        val originalRedactedChars = text.zip(current).count { (a, b) -> a != b }
        val rate = if (text.isEmpty()) 0f else originalRedactedChars.toFloat() / text.length * 100f

        return RedactionResult(
            redactedText = current,
            tokenMap = tokenMap,
            stats = RedactionStats(
                originalLength = text.length,
                redactedLength = current.length,
                entitiesRedacted = tokenCounters.values.sum(),
                redactionRatePercent = rate
            )
        )
    }

    /**
     * Redacts SSNs in [text].
     *
     * @param text Source text
     * @param mode Redaction mode to apply
     * @return Text with all SSNs redacted
     */
    fun redactSSN(text: String, mode: RedactionMode = RedactionMode.MASK): String {
        val tokenMap = mutableMapOf<String, String>()
        tokenCounters = mutableMapOf()
        return redactWithMode(text, SSN_PATTERN, "SSN",
            RedactionConfig(defaultMode = mode), tokenMap)
    }

    /**
     * Redacts credit card numbers in [text].
     *
     * @param text Source text
     * @param mode Redaction mode to apply
     * @return Text with all card numbers redacted
     */
    fun redactCreditCard(text: String, mode: RedactionMode = RedactionMode.MASK): String {
        val tokenMap = mutableMapOf<String, String>()
        tokenCounters = mutableMapOf()
        return redactWithMode(text, CARD_PATTERN, "CREDIT_CARD",
            RedactionConfig(defaultMode = mode), tokenMap)
    }

    /**
     * Redacts email addresses in [text].
     *
     * @param text Source text
     * @param mode Redaction mode to apply
     * @return Text with all emails redacted
     */
    fun redactEmail(text: String, mode: RedactionMode = RedactionMode.MASK): String {
        val tokenMap = mutableMapOf<String, String>()
        tokenCounters = mutableMapOf()
        return redactWithMode(text, EMAIL_PATTERN, "EMAIL",
            RedactionConfig(defaultMode = mode), tokenMap)
    }

    /**
     * Redacts phone numbers in [text].
     *
     * @param text Source text
     * @param mode Redaction mode to apply
     * @return Text with all phone numbers redacted
     */
    fun redactPhone(text: String, mode: RedactionMode = RedactionMode.MASK): String {
        val tokenMap = mutableMapOf<String, String>()
        tokenCounters = mutableMapOf()
        return redactWithMode(text, PHONE_PATTERN, "PHONE",
            RedactionConfig(defaultMode = mode), tokenMap)
    }

    /**
     * Redacts IPv4 addresses in [text].
     *
     * @param text Source text
     * @param mode Redaction mode to apply
     * @return Text with all IPv4 addresses redacted
     */
    fun redactIPAddress(text: String, mode: RedactionMode = RedactionMode.MASK): String {
        val tokenMap = mutableMapOf<String, String>()
        tokenCounters = mutableMapOf()
        return redactWithMode(text, IPV4_PATTERN, "IP_ADDRESS",
            RedactionConfig(defaultMode = mode), tokenMap)
    }

    /**
     * Convenience method to redact all PII types with a single [mode].
     *
     * @param text Source text
     * @param mode Redaction mode to apply to all entity types
     * @return Text with all PII redacted
     */
    fun redactAll(text: String, mode: RedactionMode = RedactionMode.MASK): String =
        redact(text, RedactionConfig(defaultMode = mode)).redactedText

    /**
     * Restores a previously tokenized text using the token map from [RedactionResult.tokenMap].
     *
     * @param redacted  The tokenized text (e.g. "My SSN is [SSN_1]")
     * @param tokenMap  The mapping from token to original value
     * @return The original text with tokens replaced back
     */
    fun restore(redacted: String, tokenMap: Map<String, String>): String {
        var result = redacted
        for ((token, original) in tokenMap) {
            result = result.replace(token, original)
        }
        return result
    }

    /**
     * Generates a deterministic token string for an entity type and index.
     * Tokens are in the format [TYPE_N] (e.g. [SSN_1], [CARD_3]).
     *
     * @param entityType  The entity type label (e.g. "SSN", "CREDIT_CARD")
     * @param index       Sequential index (1-based)
     * @return The token string
     */
    fun generateToken(entityType: String, index: Int): String = "[$entityType$index]"

    /**
     * Computes redaction statistics by comparing [original] and [redacted] texts.
     *
     * @param original  The original unredacted text
     * @param redacted  The redacted text
     * @return A [RedactionStats] object
     */
    fun computeRedactionStats(original: String, redacted: String): RedactionStats {
        val changed = original.zip(redacted).count { (a, b) -> a != b } +
                      Math.abs(original.length - redacted.length)
        val rate = if (original.isEmpty()) 0f else changed.toFloat() / original.length * 100f
        return RedactionStats(
            originalLength = original.length,
            redactedLength = redacted.length,
            entitiesRedacted = -1, // unknown without tracking
            redactionRatePercent = rate.coerceIn(0f, 100f)
        )
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun redactWithMode(
        text: String,
        pattern: Regex,
        entityType: String,
        config: RedactionConfig,
        tokenMap: MutableMap<String, String>
    ): String {
        val mode = config.overrides[entityType] ?: config.defaultMode
        return pattern.replace(text) { match ->
            val value = match.value
            val count = (tokenCounters[entityType] ?: 0) + 1
            tokenCounters[entityType] = count
            replaceValue(value, entityType, count, mode, config, tokenMap)
        }
    }

    private fun redactAPIKeyInternal(
        text: String,
        config: RedactionConfig,
        tokenMap: MutableMap<String, String>
    ): String {
        val mode = config.overrides["API_KEY"] ?: config.defaultMode
        return API_KEY_GENERIC.replace(text) { match ->
            val keyName = match.groupValues[1]
            val keyValue = match.groupValues[2]
            val count = (tokenCounters["API_KEY"] ?: 0) + 1
            tokenCounters["API_KEY"] = count
            val replacement = replaceValue(keyValue, "API_KEY", count, mode, config, tokenMap)
            "$keyName=${match.value[match.value.indexOf('=')].let { "=" }}$replacement"
                .let { match.value.replace(keyValue, replacement) }
        }
    }

    private fun replaceValue(
        value: String,
        entityType: String,
        count: Int,
        mode: RedactionMode,
        config: RedactionConfig,
        tokenMap: MutableMap<String, String>
    ): String {
        return when (mode) {
            RedactionMode.MASK -> maskValue(value, entityType, config.preserveFormat)
            RedactionMode.REPLACE -> config.customReplacements[entityType] ?: "[$entityType REDACTED]"
            RedactionMode.HASH -> hashString(value, config.hashAlgorithm)
            RedactionMode.ENCRYPT -> encryptString(value, config.encryptionKey)
            RedactionMode.REMOVE -> ""
            RedactionMode.TOKENIZE -> {
                val token = generateToken(entityType, count)
                tokenMap[token] = value
                token
            }
        }
    }

    /**
     * Masks a value based on its entity type, optionally preserving format delimiters.
     */
    private fun maskValue(value: String, entityType: String, preserveFormat: Boolean): String {
        return when (entityType) {
            "SSN"         -> maskSSNInternal(value, preserveFormat)
            "CREDIT_CARD" -> maskCardInternal(value, preserveFormat)
            "EMAIL"       -> maskEmailInternal(value)
            "PHONE"       -> maskPhoneInternal(value)
            "IP_ADDRESS"  -> maskIPv4Internal(value)
            "API_KEY"     -> maskAPIKeyInternal(value)
            else          -> "*".repeat(value.length)
        }
    }

    /**
     * Masks an SSN, preserving format delimiters if [preserveFormat] is true.
     * Examples:
     * - "123-45-6789" → "***-**-6789"
     * - "123456789"   → "*****6789"
     */
    private fun maskSSNInternal(ssn: String, preserveFormat: Boolean): String {
        if (preserveFormat) {
            val hasDash = ssn.contains('-')
            val hasSpace = ssn.contains(' ')
            val digits = ssn.filter { it.isDigit() }
            if (digits.length != 9) return "*".repeat(ssn.length)
            val last4 = digits.takeLast(4)
            return when {
                hasDash   -> "***-**-$last4"
                hasSpace  -> "*** ** $last4"
                else      -> "*****$last4"
            }
        }
        return "*".repeat(ssn.length)
    }

    /**
     * Masks a credit card number, preserving format delimiters if [preserveFormat] is true.
     * Examples:
     * - "4111 1111 1111 1111" → "**** **** **** 1111"
     * - "4111111111111111"    → "************1111"
     */
    private fun maskCardInternal(card: String, preserveFormat: Boolean): String {
        if (preserveFormat) {
            val digits = card.filter { it.isDigit() }
            if (digits.length < 12) return "*".repeat(card.length)
            val last4 = digits.takeLast(4)
            val hasDash  = card.contains('-')
            val hasSpace = card.contains(' ')
            val maskedDigits = "*".repeat(digits.length - 4) + last4
            return when {
                hasSpace -> maskedDigits.chunked(4).joinToString(" ")
                hasDash  -> maskedDigits.chunked(4).joinToString("-")
                else     -> maskedDigits
            }
        }
        return "*".repeat(card.length)
    }

    /**
     * Masks an email address showing only the first character of the local part and the domain.
     * Example: "johndoe@example.com" → "j*****@example.com"
     */
    private fun maskEmailInternal(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 0) return "*".repeat(email.length)
        val local = email.substring(0, atIndex)
        val domain = email.substring(atIndex)
        val masked = when {
            local.length <= 1 -> "*"
            local.length <= 3 -> local.first() + "*".repeat(local.length - 1)
            else -> local.first() + "*".repeat(local.length - 2) + local.last()
        }
        return "$masked$domain"
    }

    /**
     * Masks a phone number preserving only the last 4 digits.
     * Example: "+1 (555) 867-5309" → "*** *** ***-5309"
     */
    private fun maskPhoneInternal(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        if (digits.length < 4) return "*".repeat(phone.length)
        val last4 = digits.takeLast(4)
        // Preserve length and separator positions
        var digitIdx = 0
        return buildString {
            for (ch in phone) {
                if (ch.isDigit()) {
                    val globalIdx = digits.length - phone.count { it.isDigit() } + digitIdx
                    if (globalIdx >= digits.length - 4) append(ch) else append('*')
                    digitIdx++
                } else {
                    append(ch)
                }
            }
        }
    }

    /**
     * Masks an IPv4 address replacing the last two octets with asterisks.
     * Example: "192.168.1.100" → "192.168.*.*"
     */
    private fun maskIPv4Internal(ip: String): String {
        val parts = ip.split(".")
        return if (parts.size == 4) "${parts[0]}.${parts[1]}.*.*" else "*".repeat(ip.length)
    }

    /**
     * Masks an API key showing only the first 4 and last 4 characters.
     * Example: "sk_live_AbCdEfGhIjKlMnOp" → "sk_l*************MnOp"
     */
    private fun maskAPIKeyInternal(key: String): String {
        if (key.length <= 8) return "*".repeat(key.length)
        return key.take(4) + "*".repeat(key.length - 8) + key.takeLast(4)
    }

    /**
     * Computes the SHA-256 (or other) hex digest of a string.
     *
     * @param input     The string to hash
     * @param algorithm The MessageDigest algorithm name (e.g. "SHA-256")
     * @return Hex-encoded digest string
     */
    fun hashString(input: String, algorithm: String = "SHA-256"): String {
        val digest = MessageDigest.getInstance(algorithm)
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Encrypts a string using AES-128-ECB and returns a Base64-encoded ciphertext.
     *
     * Note: ECB mode is intentionally simple here as this is for local tokenisation
     * and the key should be stored securely in Android Keystore.
     *
     * @param input The string to encrypt
     * @param key   16-byte AES key
     * @return Base64 ciphertext or "[ENCRYPT_ERROR]" if encryption fails
     */
    fun encryptString(input: String, key: ByteArray): String {
        return try {
            val keySpec = SecretKeySpec(key.copyOf(16), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encrypted = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            "[ENCRYPT_ERROR]"
        }
    }

    /**
     * Decrypts a Base64-encoded AES-128-ECB ciphertext back to the original string.
     *
     * @param ciphertext Base64-encoded ciphertext
     * @param key        16-byte AES key (must match the key used for [encryptString])
     * @return Original plaintext or "[DECRYPT_ERROR]" if decryption fails
     */
    fun decryptString(ciphertext: String, key: ByteArray): String {
        return try {
            val keySpec = SecretKeySpec(key.copyOf(16), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val bytes = Base64.decode(ciphertext, Base64.NO_WRAP)
            String(cipher.doFinal(bytes), Charsets.UTF_8)
        } catch (e: Exception) {
            "[DECRYPT_ERROR]"
        }
    }

    /**
     * Returns true if [text] contains anything that looks like a tokenized placeholder.
     * Useful for detecting whether text has already been redacted.
     */
    fun isRedacted(text: String): Boolean =
        Regex("""\[(?:SSN|CREDIT_CARD|EMAIL|PHONE|API_KEY|IP_ADDRESS|BANK_IBAN|BANK_ROUTING|PASSPORT|DATE_OF_BIRTH)[\s_]\d+\]""")
            .containsMatchIn(text) ||
        text.contains("***-**-") ||
        text.contains("[SSN REDACTED]") ||
        text.contains("[EMAIL REDACTED]")

    /**
     * Returns a list of all tokens found in a tokenized text (e.g. ["[SSN_1]", "[EMAIL_2]"]).
     */
    fun extractTokens(redactedText: String): List<String> =
        Regex("""\[[A-Z_]+\d+\]""").findAll(redactedText).map { it.value }.toList()

    /**
     * Returns the number of entities redacted per type in the [RedactionResult].
     * Derived from the [RedactionResult.tokenMap] when TOKENIZE mode is used.
     */
    fun countByType(result: RedactionResult): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        for (token in result.tokenMap.keys) {
            // Token format: [TYPE_N]
            val typeMatch = Regex("""\[([A-Z_]+)\d+\]""").find(token)
            val type = typeMatch?.groupValues?.get(1) ?: "UNKNOWN"
            counts[type] = (counts[type] ?: 0) + 1
        }
        return counts
    }
}
