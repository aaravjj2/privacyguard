package com.privacyguard.util

import java.util.regex.Pattern

/**
 * Comprehensive PII validation orchestrator for PrivacyGuard.
 *
 * Combines all individual validators (SSN, CreditCard, Email, Phone, APIKey)
 * and adds additional validators for IP addresses, dates of birth, passport numbers,
 * driver licenses, and bank account numbers.
 *
 * Usage:
 * ```kotlin
 * val report = PIIValidator.validateAll("My SSN is 123-45-6789 and card 4111111111111111")
 * println("Risk: ${report.riskScore}, Found: ${report.results.size} entities")
 * ```
 */
object PIIValidator {

    // -------------------------------------------------------------------------
    // Data classes
    // -------------------------------------------------------------------------

    /**
     * Confidence level of a PII detection result.
     */
    enum class PIIConfidence {
        /** High confidence — format matches exactly and passes all validation checks */
        HIGH,
        /** Medium confidence — format matches but some checks are uncertain */
        MEDIUM,
        /** Low confidence — heuristic match, may be a false positive */
        LOW,
        /** Uncertain — pattern present but validation inconclusive */
        UNCERTAIN
    }

    /**
     * A single validated PII entity found in text.
     *
     * @param entityType  Human-readable type label (e.g. "SSN", "CREDIT_CARD", "EMAIL")
     * @param value       The original text value found
     * @param maskedValue A privacy-safe masked representation
     * @param confidence  Validation confidence level
     * @param validationDetails  Human-readable explanation of the validation result
     * @param startIndex  Character offset of the start of the match in the source text
     * @param endIndex    Character offset immediately after the end of the match
     */
    data class PIIValidationResult(
        val entityType: String,
        val value: String,
        val maskedValue: String,
        val confidence: PIIConfidence,
        val validationDetails: String,
        val startIndex: Int = -1,
        val endIndex: Int = -1
    )

    /**
     * Aggregated report of all PII found in a block of text.
     *
     * @param results     All individually validated PII entities
     * @param riskScore   Composite risk score from 0.0 (no risk) to 1.0 (maximum risk)
     * @param summary     Human-readable summary of findings
     * @param textLength  Length of the input text analysed
     * @param timestamp   Unix epoch milliseconds when the report was generated
     */
    data class PIIValidationReport(
        val results: List<PIIValidationResult>,
        val riskScore: Float,
        val summary: String,
        val textLength: Int,
        val timestamp: Long = System.currentTimeMillis()
    )

    // -------------------------------------------------------------------------
    // IPv4 / IPv6 patterns
    // -------------------------------------------------------------------------

    /** Matches a standard dotted-decimal IPv4 address (not range-validated). */
    private val IPV4_PATTERN = Pattern.compile(
        """(?<!\d)(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})(?!\d)"""
    )

    /** Matches a full 128-bit IPv6 address in any of the standard representations. */
    private val IPV6_PATTERN = Pattern.compile(
        """(?i)(?:[0-9a-f]{1,4}:){7}[0-9a-f]{1,4}""" +
        """|(?:[0-9a-f]{1,4}:){1,7}:""" +
        """|:(?::[0-9a-f]{1,4}){1,7}""" +
        """|(?:[0-9a-f]{1,4}:){1,6}:[0-9a-f]{1,4}""" +
        """|::(?:ffff(?::0{1,4})?:)?(?:25[0-5]|2[0-4]\d|[01]?\d\d?)(?:\.(?:25[0-5]|2[0-4]\d|[01]?\d\d?)){3}"""
    )

    // -------------------------------------------------------------------------
    // Date-of-Birth patterns
    // -------------------------------------------------------------------------

    /** MM/DD/YYYY */
    private val DOB_MDY_SLASH = Pattern.compile(
        """(?<!\d)(0[1-9]|1[0-2])/(0[1-9]|[12]\d|3[01])/(19|20)\d{2}(?!\d)"""
    )

    /** YYYY-MM-DD (ISO 8601) */
    private val DOB_ISO = Pattern.compile(
        """(?<!\d)(19|20)\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])(?!\d)"""
    )

    /** DD/MM/YYYY */
    private val DOB_DMY_SLASH = Pattern.compile(
        """(?<!\d)(0[1-9]|[12]\d|3[01])/(0[1-9]|1[0-2])/(19|20)\d{2}(?!\d)"""
    )

    /** DD Month YYYY (e.g. "12 January 1990") */
    private val DOB_DMY_LONG = Pattern.compile(
        """(?<!\d)(0?[1-9]|[12]\d|3[01])\s+""" +
        """(January|February|March|April|May|June|July|August|September|October|November|December)""" +
        """\s+(19|20)\d{2}(?!\d)""",
        Pattern.CASE_INSENSITIVE
    )

    /** Month DD, YYYY (e.g. "January 12, 1990") */
    private val DOB_MDY_LONG = Pattern.compile(
        """(?i)(January|February|March|April|May|June|July|August|September|October|November|December)""" +
        """\s+(0?[1-9]|[12]\d|3[01]),?\s+(19|20)\d{2}"""
    )

    // -------------------------------------------------------------------------
    // Passport number patterns (by issuing country)
    // -------------------------------------------------------------------------

    /** US passport: letter + 8 digits (e.g. A12345678) */
    private val PASSPORT_US = Pattern.compile("""(?<!\w)[A-Z]\d{8}(?!\w)""")

    /** UK passport: 9 digits */
    private val PASSPORT_UK = Pattern.compile("""(?<!\d)\d{9}(?!\d)""")

    /** German passport: C + 8 alphanumeric */
    private val PASSPORT_DE = Pattern.compile("""(?<!\w)C\d{8}(?!\w)""")

    /** Canadian passport: 2 letters + 6 digits */
    private val PASSPORT_CA = Pattern.compile("""(?<!\w)[A-Z]{2}\d{6}(?!\w)""")

    /** Australian passport: 1-2 letters + 7 digits */
    private val PASSPORT_AU = Pattern.compile("""(?<!\w)[A-Z]{1,2}\d{7}(?!\w)""")

    // -------------------------------------------------------------------------
    // US Driver License patterns by state (simplified representative formats)
    // -------------------------------------------------------------------------

    private val DL_PATTERNS: Map<String, Pattern> = mapOf(
        "AL" to Pattern.compile("""(?<!\d)\d{7}(?!\d)"""),
        "AK" to Pattern.compile("""(?<!\d)\d{7}(?!\d)"""),
        "AZ" to Pattern.compile("""(?<!\w)[A-Z]\d{8}(?!\w)|(?<!\d)\d{9}(?!\d)"""),
        "AR" to Pattern.compile("""(?<!\d)\d{9}(?!\d)"""),
        "CA" to Pattern.compile("""(?<!\w)[A-Z]\d{7}(?!\w)"""),
        "CO" to Pattern.compile("""(?<!\d)\d{9}(?!\d)"""),
        "CT" to Pattern.compile("""(?<!\d)\d{9}(?!\d)"""),
        "DE" to Pattern.compile("""(?<!\d)\d{7}(?!\d)"""),
        "FL" to Pattern.compile("""(?<!\w)[A-Z]\d{12}(?!\w)"""),
        "GA" to Pattern.compile("""(?<!\d)\d{9}(?!\d)"""),
        "HI" to Pattern.compile("""(?<!\w)(H\d{8}|\d{9})(?!\w)"""),
        "ID" to Pattern.compile("""(?<!\w)[A-Z]{2}\d{6}[A-Z](?!\w)"""),
        "IL" to Pattern.compile("""(?<!\w)[A-Z]\d{11,12}(?!\w)"""),
        "IN" to Pattern.compile("""(?<!\d)\d{10}(?!\d)"""),
        "IA" to Pattern.compile("""(?<!\w)\d{9}[A-Z]{2}(?!\w)"""),
        "KS" to Pattern.compile("""(?<!\w)[A-Z]\d{8}(?!\w)"""),
        "KY" to Pattern.compile("""(?<!\w)[A-Z]\d{8,9}(?!\w)"""),
        "LA" to Pattern.compile("""(?<!\d)\d{9}(?!\d)"""),
        "ME" to Pattern.compile("""(?<!\d)\d{7}(?!\d)"""),
        "MD" to Pattern.compile("""(?<!\w)[A-Z]\d{12}(?!\w)"""),
        "MA" to Pattern.compile("""(?<!\w)(S\d{8}|\d{9})(?!\w)"""),
        "MI" to Pattern.compile("""(?<!\w)[A-Z]\d{12}(?!\w)"""),
        "MN" to Pattern.compile("""(?<!\w)[A-Z]\d{12}(?!\w)"""),
        "MS" to Pattern.compile("""(?<!\d)\d{9}(?!\d)"""),
        "MO" to Pattern.compile("""(?<!\w)[A-Z]\d{5,9}(?!\w)|\d{9}(?!\d)"""),
        "MT" to Pattern.compile("""(?<!\d)\d{9}(?!\d)"""),
        "NE" to Pattern.compile("""(?<!\w)[A-Z]\d{6,8}(?!\w)"""),
        "NV" to Pattern.compile("""(?<!\d)\d{9,10}(?!\d)"""),
        "NH" to Pattern.compile("""(?<!\d)\d{2}[A-Z]{3}\d{5}(?!\w)"""),
        "NJ" to Pattern.compile("""(?<!\w)[A-Z]\d{14}(?!\w)"""),
        "NM" to Pattern.compile("""(?<!\d)\d{9}(?!\d)"""),
        "NY" to Pattern.compile("""(?<!\w)[A-Z]\d{7}(?!\w)|\d{9}(?!\d)"""),
        "NC" to Pattern.compile("""(?<!\d)\d{1,12}(?!\d)"""),
        "ND" to Pattern.compile("""(?<!\w)[A-Z]{3}\d{6}(?!\w)"""),
        "OH" to Pattern.compile("""(?<!\w)[A-Z]{2}\d{6}(?!\w)"""),
        "OK" to Pattern.compile("""(?<!\w)[A-Z]\d{9}(?!\w)"""),
        "OR" to Pattern.compile("""(?<!\d)\d{7,9}(?!\d)"""),
        "PA" to Pattern.compile("""(?<!\d)\d{8}(?!\d)"""),
        "RI" to Pattern.compile("""(?<!\d)\d{7}(?!\d)"""),
        "SC" to Pattern.compile("""(?<!\d)\d{5,11}(?!\d)"""),
        "SD" to Pattern.compile("""(?<!\d)\d{6,9}(?!\d)"""),
        "TN" to Pattern.compile("""(?<!\d)\d{7,9}(?!\d)"""),
        "TX" to Pattern.compile("""(?<!\d)\d{8}(?!\d)"""),
        "UT" to Pattern.compile("""(?<!\d)\d{4,10}(?!\d)"""),
        "VT" to Pattern.compile("""(?<!\d)\d{8}[A-Z]?(?!\w)"""),
        "VA" to Pattern.compile("""(?<!\w)[A-Z]\d{8,11}(?!\w)|\d{9}(?!\d)"""),
        "WA" to Pattern.compile("""(?<!\w)[A-Z*]{1,7}\d{3}[A-Z0-9]{2}(?!\w)"""),
        "WV" to Pattern.compile("""(?<!\w)[A-Z]\d{6}(?!\w)|\d{7}"""),
        "WI" to Pattern.compile("""(?<!\w)[A-Z]\d{13}(?!\w)"""),
        "WY" to Pattern.compile("""(?<!\d)\d{9,10}(?!\d)""")
    )

    // -------------------------------------------------------------------------
    // Bank account patterns
    // -------------------------------------------------------------------------

    /** US ABA routing number: exactly 9 digits, starting with 0-12 or specific ranges */
    private val ABA_ROUTING = Pattern.compile("""(?<!\d)(0[0-9]\d{7}|1[0-2]\d{7}|2[0-1]\d{7}|3[0-2]\d{7})(?!\d)""")

    /** Generic 8-17 digit account number (conservative — used with context) */
    private val BANK_ACCOUNT = Pattern.compile("""(?<!\d)\d{8,17}(?!\d)""")

    /** IBAN: country code + 2 check digits + up to 30 alphanumeric characters */
    private val IBAN_PATTERN = Pattern.compile(
        """(?<!\w)[A-Z]{2}\d{2}[A-Z0-9]{4,30}(?!\w)"""
    )

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Validates all known PII types in the given text and returns a comprehensive report.
     *
     * @param text The text to scan for PII
     * @return A [PIIValidationReport] containing all found entities, risk score, and summary
     */
    fun validateAll(text: String): PIIValidationReport {
        if (text.isBlank()) {
            return PIIValidationReport(
                results = emptyList(),
                riskScore = 0f,
                summary = "No text to analyse",
                textLength = text.length
            )
        }

        val allResults = mutableListOf<PIIValidationResult>()
        allResults += validateSSN(text)
        allResults += validateCreditCard(text)
        allResults += validateEmail(text)
        allResults += validatePhone(text)
        allResults += validateAPIKey(text)
        allResults += validateIPAddress(text)
        allResults += validateDateOfBirth(text)
        allResults += validateBankAccount(text)

        val riskScore = computeRiskScore(PIIValidationReport(allResults, 0f, "", text.length))
        val summary = summarize(PIIValidationReport(allResults, riskScore, "", text.length))

        return PIIValidationReport(
            results = allResults,
            riskScore = riskScore,
            summary = summary,
            textLength = text.length
        )
    }

    /**
     * Finds and validates all SSNs in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for each SSN found
     */
    fun validateSSN(text: String): List<PIIValidationResult> {
        val results = SSNValidator.extractFromText(text)
        return results.map { r ->
            PIIValidationResult(
                entityType = if (r.isITIN) "ITIN" else if (r.isEIN) "EIN" else "SSN",
                value = r.formatted,
                maskedValue = r.masked,
                confidence = when {
                    r.isAdvertising -> PIIConfidence.LOW
                    r.confidence >= 0.85f -> PIIConfidence.HIGH
                    r.confidence >= 0.65f -> PIIConfidence.MEDIUM
                    else -> PIIConfidence.LOW
                },
                validationDetails = r.reason
            )
        }
    }

    /**
     * Finds and validates all credit/debit card numbers in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for each card number found
     */
    fun validateCreditCard(text: String): List<PIIValidationResult> {
        val results = CreditCardValidator.extractFromText(text)
        return results.map { r ->
            PIIValidationResult(
                entityType = "CREDIT_CARD",
                value = r.formattedNumber,
                maskedValue = r.maskedNumber,
                confidence = when {
                    r.luhnValid && r.networkDetected != null -> PIIConfidence.HIGH
                    r.luhnValid -> PIIConfidence.MEDIUM
                    else -> PIIConfidence.LOW
                },
                validationDetails = r.reason
            )
        }
    }

    /**
     * Finds and validates all email addresses in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for each email found
     */
    fun validateEmail(text: String): List<PIIValidationResult> {
        val results = EmailValidator.extractFromText(text)
        return results.map { r ->
            PIIValidationResult(
                entityType = "EMAIL",
                value = r.original,
                maskedValue = maskEmail(r.original),
                confidence = when {
                    r.isValid && !r.isDisposable && !r.isRoleAddress -> PIIConfidence.HIGH
                    r.isValid -> PIIConfidence.MEDIUM
                    else -> PIIConfidence.LOW
                },
                validationDetails = r.reason
            )
        }
    }

    /**
     * Finds and validates all phone numbers in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for each phone number found
     */
    fun validatePhone(text: String): List<PIIValidationResult> {
        val results = CountryCodeValidator.extractFromText(text)
        return results.map { r ->
            PIIValidationResult(
                entityType = "PHONE",
                value = r.originalInput,
                maskedValue = maskPhone(r.normalizedNumber ?: r.originalInput),
                confidence = when {
                    r.isValid && r.confidence >= 0.85f -> PIIConfidence.HIGH
                    r.isValid -> PIIConfidence.MEDIUM
                    else -> PIIConfidence.LOW
                },
                validationDetails = r.reason
            )
        }
    }

    /**
     * Finds and validates all API keys and secrets in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for each API key found
     */
    fun validateAPIKey(text: String): List<PIIValidationResult> {
        val matches = APIKeyPatterns.scan(text)
        return matches.map { m ->
            val masked = if (m.value.length > 8)
                m.value.take(4) + "*".repeat(m.value.length - 8) + m.value.takeLast(4)
            else
                "*".repeat(m.value.length)
            PIIValidationResult(
                entityType = "API_KEY",
                value = m.value,
                maskedValue = masked,
                confidence = when (m.pattern.severity) {
                    APIKeyPatterns.Severity.CRITICAL -> PIIConfidence.HIGH
                    APIKeyPatterns.Severity.HIGH -> PIIConfidence.HIGH
                    APIKeyPatterns.Severity.MEDIUM -> PIIConfidence.MEDIUM
                    APIKeyPatterns.Severity.LOW -> PIIConfidence.LOW
                },
                validationDetails = "Provider: ${m.pattern.provider}, Severity: ${m.pattern.severity}"
            )
        }
    }

    /**
     * Finds and validates all IP addresses (IPv4 and IPv6) in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for each IP address found
     */
    fun validateIPAddress(text: String): List<PIIValidationResult> {
        val results = mutableListOf<PIIValidationResult>()

        // IPv4
        val ipv4Matcher = IPV4_PATTERN.matcher(text)
        while (ipv4Matcher.find()) {
            val ip = ipv4Matcher.group()
            if (isValidIPv4(ip)) {
                val ipType = classifyIPv4(ip)
                results += PIIValidationResult(
                    entityType = "IP_ADDRESS",
                    value = ip,
                    maskedValue = maskIPv4(ip),
                    confidence = if (ipType == "PUBLIC") PIIConfidence.HIGH else PIIConfidence.LOW,
                    validationDetails = "IPv4 $ipType address",
                    startIndex = ipv4Matcher.start(),
                    endIndex = ipv4Matcher.end()
                )
            }
        }

        // IPv6
        val ipv6Matcher = IPV6_PATTERN.matcher(text)
        while (ipv6Matcher.find()) {
            results += PIIValidationResult(
                entityType = "IP_ADDRESS",
                value = ipv6Matcher.group(),
                maskedValue = "::****:****",
                confidence = PIIConfidence.MEDIUM,
                validationDetails = "IPv6 address",
                startIndex = ipv6Matcher.start(),
                endIndex = ipv6Matcher.end()
            )
        }

        return results
    }

    /**
     * Detects dates of birth in the given text using multiple date format patterns.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for each DOB-like date found
     */
    fun validateDateOfBirth(text: String): List<PIIValidationResult> {
        val results = mutableListOf<PIIValidationResult>()
        val patterns = listOf(DOB_ISO, DOB_MDY_SLASH, DOB_DMY_SLASH, DOB_MDY_LONG, DOB_DMY_LONG)
        val formatLabels = listOf("ISO YYYY-MM-DD", "MM/DD/YYYY", "DD/MM/YYYY", "Month DD, YYYY", "DD Month YYYY")
        val seen = mutableSetOf<String>()

        for ((pattern, label) in patterns.zip(formatLabels)) {
            val m = pattern.matcher(text)
            while (m.find()) {
                val value = m.group()
                if (value !in seen) {
                    seen += value
                    results += PIIValidationResult(
                        entityType = "DATE_OF_BIRTH",
                        value = value,
                        maskedValue = "**/**/****",
                        confidence = PIIConfidence.MEDIUM,
                        validationDetails = "Date in $label format — possible DOB",
                        startIndex = m.start(),
                        endIndex = m.end()
                    )
                }
            }
        }
        return results
    }

    /**
     * Detects passport numbers from multiple countries in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for passport numbers found
     */
    fun validatePassportNumber(text: String): List<PIIValidationResult> {
        val results = mutableListOf<PIIValidationResult>()
        val patterns = mapOf(
            "US" to PASSPORT_US,
            "UK" to PASSPORT_UK,
            "DE" to PASSPORT_DE,
            "CA" to PASSPORT_CA,
            "AU" to PASSPORT_AU
        )
        for ((country, pattern) in patterns) {
            val m = pattern.matcher(text)
            while (m.find()) {
                val value = m.group()
                val masked = value.take(2) + "*".repeat(value.length - 2)
                results += PIIValidationResult(
                    entityType = "PASSPORT",
                    value = value,
                    maskedValue = masked,
                    confidence = PIIConfidence.MEDIUM,
                    validationDetails = "Possible $country passport number format",
                    startIndex = m.start(),
                    endIndex = m.end()
                )
            }
        }
        return results
    }

    /**
     * Detects US driver license numbers by state format in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for driver license numbers found
     */
    fun validateDriverLicense(text: String): List<PIIValidationResult> {
        val results = mutableListOf<PIIValidationResult>()
        for ((state, pattern) in DL_PATTERNS) {
            val m = pattern.matcher(text)
            while (m.find()) {
                val value = m.group()
                results += PIIValidationResult(
                    entityType = "DRIVER_LICENSE",
                    value = value,
                    maskedValue = value.take(1) + "*".repeat(value.length - 1),
                    confidence = PIIConfidence.LOW,
                    validationDetails = "Possible $state driver license format",
                    startIndex = m.start(),
                    endIndex = m.end()
                )
            }
        }
        return results
    }

    /**
     * Detects bank account numbers (IBAN and US ABA routing numbers) in the given text.
     *
     * @param text Source text to search
     * @return List of [PIIValidationResult] for bank account numbers found
     */
    fun validateBankAccount(text: String): List<PIIValidationResult> {
        val results = mutableListOf<PIIValidationResult>()

        // IBAN
        val ibanMatcher = IBAN_PATTERN.matcher(text)
        while (ibanMatcher.find()) {
            val value = ibanMatcher.group()
            if (isValidIBAN(value)) {
                results += PIIValidationResult(
                    entityType = "BANK_IBAN",
                    value = value,
                    maskedValue = value.take(4) + " **** **** " + value.takeLast(4),
                    confidence = PIIConfidence.HIGH,
                    validationDetails = "Valid IBAN (country: ${value.take(2)}, checksum verified)",
                    startIndex = ibanMatcher.start(),
                    endIndex = ibanMatcher.end()
                )
            }
        }

        // US ABA Routing Number
        val abaMatcher = ABA_ROUTING.matcher(text)
        while (abaMatcher.find()) {
            val value = abaMatcher.group()
            if (isValidABARoutingNumber(value)) {
                results += PIIValidationResult(
                    entityType = "BANK_ROUTING",
                    value = value,
                    maskedValue = "***" + value.drop(3),
                    confidence = PIIConfidence.HIGH,
                    validationDetails = "Valid ABA routing number (checksum verified)",
                    startIndex = abaMatcher.start(),
                    endIndex = abaMatcher.end()
                )
            }
        }

        return results
    }

    /**
     * Computes a composite risk score from 0.0 to 1.0 based on the types, counts and
     * confidence levels of all PII entities found in the report.
     *
     * Scoring weights:
     * - Every HIGH-confidence entity contributes more than MEDIUM or LOW
     * - Sensitive types (SSN, CREDIT_CARD, API_KEY) are weighted more than EMAIL or PHONE
     * - The final score is clamped to [0, 1]
     *
     * @param report The [PIIValidationReport] to score (riskScore field is ignored)
     * @return A float in [0.0, 1.0]
     */
    fun computeRiskScore(report: PIIValidationReport): Float {
        if (report.results.isEmpty()) return 0f

        val typeWeights = mapOf(
            "SSN"          to 1.0f,
            "ITIN"         to 0.9f,
            "EIN"          to 0.7f,
            "CREDIT_CARD"  to 1.0f,
            "API_KEY"      to 0.95f,
            "BANK_IBAN"    to 0.9f,
            "BANK_ROUTING" to 0.8f,
            "PASSPORT"     to 0.85f,
            "DRIVER_LICENSE" to 0.7f,
            "DATE_OF_BIRTH" to 0.5f,
            "EMAIL"        to 0.4f,
            "PHONE"        to 0.4f,
            "IP_ADDRESS"   to 0.3f
        )

        var total = 0f
        for (result in report.results) {
            val typeWeight = typeWeights[result.entityType] ?: 0.3f
            val confWeight = when (result.confidence) {
                PIIConfidence.HIGH      -> 1.0f
                PIIConfidence.MEDIUM    -> 0.7f
                PIIConfidence.LOW       -> 0.4f
                PIIConfidence.UNCERTAIN -> 0.2f
            }
            total += typeWeight * confWeight
        }

        // Scale: each entity adds to the score, saturating toward 1.0
        // Use 1 - e^(-k*total) so the score grows fast and saturates
        val k = 0.5f
        return (1f - Math.exp((-k * total).toDouble())).toFloat().coerceIn(0f, 1f)
    }

    /**
     * Returns a human-readable summary of the validation report.
     *
     * @param report The report to summarise
     * @return A multi-line string describing what was found
     */
    fun summarize(report: PIIValidationReport): String {
        if (report.results.isEmpty()) return "No PII detected in ${report.textLength}-character text."

        val counts = report.results.groupBy { it.entityType }.mapValues { it.value.size }
        val parts = counts.entries.sortedByDescending { it.value }.map { (type, count) ->
            "$count $type${if (count > 1) "s" else ""}"
        }

        val riskLabel = when {
            report.riskScore >= 0.8f -> "CRITICAL"
            report.riskScore >= 0.6f -> "HIGH"
            report.riskScore >= 0.4f -> "MEDIUM"
            report.riskScore >= 0.2f -> "LOW"
            else -> "MINIMAL"
        }

        return "Risk: $riskLabel (${(report.riskScore * 100).toInt()}%) — Found: ${parts.joinToString(", ")} in ${report.textLength}-character text."
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun maskEmail(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 0) return "****"
        val local = email.substring(0, atIndex)
        val domain = email.substring(atIndex)
        val masked = if (local.length <= 2) "*".repeat(local.length) else local.first() + "*".repeat(local.length - 2) + local.last()
        return "$masked$domain"
    }

    private fun maskPhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return if (digits.length >= 4) "*".repeat(digits.length - 4) + digits.takeLast(4) else "****"
    }

    private fun maskIPv4(ip: String): String {
        val parts = ip.split(".")
        return if (parts.size == 4) "${parts[0]}.${parts[1]}.*.*" else ip
    }

    private fun isValidIPv4(ip: String): Boolean {
        val parts = ip.split(".")
        if (parts.size != 4) return false
        return parts.all { part ->
            val n = part.toIntOrNull() ?: return false
            n in 0..255
        }
    }

    private fun classifyIPv4(ip: String): String {
        val parts = ip.split(".").map { it.toIntOrNull() ?: 0 }
        if (parts.size != 4) return "UNKNOWN"
        return when {
            parts[0] == 10 -> "PRIVATE"
            parts[0] == 172 && parts[1] in 16..31 -> "PRIVATE"
            parts[0] == 192 && parts[1] == 168 -> "PRIVATE"
            parts[0] == 127 -> "LOOPBACK"
            parts[0] == 169 && parts[1] == 254 -> "LINK_LOCAL"
            parts[0] in 224..239 -> "MULTICAST"
            parts[0] == 255 -> "BROADCAST"
            else -> "PUBLIC"
        }
    }

    /**
     * Validates an IBAN using the ISO 7064 MOD-97-10 algorithm.
     * 1. Move the first 4 characters to the end.
     * 2. Replace each letter with its numeric equivalent (A=10, B=11, ..., Z=35).
     * 3. Compute the remainder of the resulting number divided by 97.
     * 4. A valid IBAN has a remainder of 1.
     */
    fun isValidIBAN(iban: String): Boolean {
        val clean = iban.replace(" ", "").uppercase()
        if (clean.length < 5) return false
        val rearranged = clean.drop(4) + clean.take(4)
        val numeric = StringBuilder()
        for (ch in rearranged) {
            if (ch.isLetter()) numeric.append(ch - 'A' + 10)
            else if (ch.isDigit()) numeric.append(ch)
            else return false
        }
        var remainder = 0L
        for (ch in numeric) {
            remainder = (remainder * 10 + ch.digitToInt()) % 97
        }
        return remainder == 1L
    }

    /**
     * Validates a US ABA routing number using the standard checksum formula:
     * 3*(d1+d4+d7) + 7*(d2+d5+d8) + (d3+d6+d9) must be divisible by 10.
     */
    fun isValidABARoutingNumber(routing: String): Boolean {
        if (routing.length != 9 || !routing.all { it.isDigit() }) return false
        val d = routing.map { it.digitToInt() }
        val checksum = 3 * (d[0] + d[3] + d[6]) + 7 * (d[1] + d[4] + d[7]) + (d[2] + d[5] + d[8])
        return checksum % 10 == 0
    }

    /**
     * Returns the total number of results of a given entity type in a report.
     */
    fun countByType(report: PIIValidationReport, entityType: String): Int =
        report.results.count { it.entityType == entityType }

    /**
     * Returns only the HIGH-confidence results from a report.
     */
    fun highConfidenceOnly(report: PIIValidationReport): List<PIIValidationResult> =
        report.results.filter { it.confidence == PIIConfidence.HIGH }

    /**
     * Returns true if the report contains any critical entity types (SSN, CREDIT_CARD, API_KEY).
     */
    fun hasCriticalPII(report: PIIValidationReport): Boolean =
        report.results.any { it.entityType in setOf("SSN", "CREDIT_CARD", "API_KEY", "BANK_IBAN", "ITIN") }

    /**
     * Returns a deduplicated list of entity types found in the report.
     */
    fun entityTypesFound(report: PIIValidationReport): Set<String> =
        report.results.map { it.entityType }.toSet()

    /**
     * Checks whether a piece of text contains any detectable PII (quick heuristic).
     * Returns true as soon as the first entity is found.
     */
    fun containsPII(text: String): Boolean {
        if (text.isBlank()) return false
        return SSNValidator.extractFromText(text).isNotEmpty() ||
               CreditCardValidator.extractFromText(text).isNotEmpty() ||
               EmailValidator.extractFromText(text).isNotEmpty() ||
               APIKeyPatterns.containsAPIKey(text) ||
               validateIPAddress(text).any { it.confidence == PIIConfidence.HIGH }
    }
}
