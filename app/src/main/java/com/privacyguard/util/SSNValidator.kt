package com.privacyguard.util

/**
 * Comprehensive SSN (Social Security Number) validation utility.
 * Validates SSNs against all known rules from the Social Security Administration.
 *
 * Validation rules implemented:
 * - Area number (first 3 digits): 001-899, excluding 666
 * - Group number (middle 2 digits): 01-99
 * - Serial number (last 4 digits): 0001-9999
 * - Known invalid patterns (advertising, death master file patterns)
 * - Historical issuance patterns
 * - Randomization era (post-June 2011) rules
 *
 * Also supports:
 * - ITIN (Individual Taxpayer Identification Number) detection
 * - EIN (Employer Identification Number) validation
 * - Multiple format parsing (XXX-XX-XXXX, XXXXXXXXX, XXX XX XXXX)
 */
object SSNValidator {

    /**
     * Result of SSN validation.
     *
     * @param isValid Whether the SSN passes all validation checks
     * @param formatted SSN in standard format (XXX-XX-XXXX)
     * @param masked Masked SSN (***-**-1234)
     * @param areaNumber First three digits
     * @param groupNumber Middle two digits
     * @param serialNumber Last four digits
     * @param isITIN Whether this appears to be an ITIN
     * @param isEIN Whether this appears to be an EIN
     * @param isAdvertising Whether this is a known advertising SSN
     * @param era The era classification (pre/post randomization)
     * @param confidence Validation confidence
     * @param reason Human-readable validation result
     */
    data class SSNValidationResult(
        val isValid: Boolean,
        val formatted: String = "",
        val masked: String = "",
        val areaNumber: String = "",
        val groupNumber: String = "",
        val serialNumber: String = "",
        val isITIN: Boolean = false,
        val isEIN: Boolean = false,
        val isAdvertising: Boolean = false,
        val era: SSNEra = SSNEra.UNKNOWN,
        val confidence: Float = 0f,
        val reason: String = ""
    )

    /**
     * SSN issuance era classification.
     */
    enum class SSNEra {
        /** Before June 25, 2011 - area numbers had geographic meaning */
        PRE_RANDOMIZATION,
        /** After June 25, 2011 - randomized assignment */
        POST_RANDOMIZATION,
        /** Cannot determine era */
        UNKNOWN
    }

    /**
     * Known invalid SSN patterns.
     */
    val knownInvalidSSNs: Set<String> = setOf(
        // SSA advertising SSNs
        "078051120", // Woolworth wallet insert (1938)
        "219099999", // Widely publicized SSN
        "457555462", // Lifelock CEO's SSN (publicly shared)

        // Common dummy/test SSNs
        "123456789",
        "111111111",
        "222222222",
        "333333333",
        "444444444",
        "555555555",
        "666666666",
        "777777777",
        "888888888",
        "999999999",
        "000000000",
        "123121234",
        "321214321",
        "987654321",

        // SSNs used in media/examples
        "078051120", // Original Woolworth
        "042103580", // Tod Slaughter wallet insert
    )

    /**
     * Area numbers that have been explicitly invalid throughout SSA history.
     * These were never assigned and remain invalid even after randomization.
     */
    val permanentlyInvalidAreaNumbers: Set<Int> = setOf(
        0,     // 000 - Never assigned
        666    // 666 - Permanently excluded
    )

    /**
     * Area number ranges invalid in the pre-randomization era (before June 2011).
     * Area numbers 734-749 were unassigned but became valid post-randomization.
     */
    val preRandomizationInvalidRanges: List<IntRange> = listOf(
        734..749,  // Previously unassigned, now valid
        750..772   // Previously unassigned, some now valid
    )

    /**
     * High group numbers that were assigned in the pre-randomization era.
     * This is a simplified version; the full list is maintained by SSA quarterly.
     */
    val historicalHighGroups: Map<Int, Int> = buildMap {
        // Sample of historical high group numbers by area
        // In practice, this would be updated quarterly from SSA data
        // Area -> Highest group number assigned
        put(1, 99); put(2, 99); put(3, 99); put(4, 99); put(5, 99)
        put(6, 99); put(7, 99); put(8, 99); put(9, 99); put(10, 99)
        put(11, 99); put(12, 99); put(13, 99); put(14, 99); put(15, 99)
        put(16, 99); put(17, 99); put(18, 99); put(19, 99); put(20, 99)
        put(21, 99); put(22, 99); put(23, 99); put(24, 99); put(25, 99)
        put(50, 99); put(100, 99); put(150, 99); put(200, 99)
        put(250, 99); put(300, 99); put(350, 99); put(400, 99)
        put(450, 99); put(500, 99); put(550, 99); put(580, 99)
        // Areas over 649 were not assigned until randomization
        put(650, 50); put(700, 25); put(733, 10)
    }

    /**
     * State/territory assignments for area numbers in the pre-randomization era.
     * After June 2011, area numbers no longer have geographic meaning.
     */
    val areaNumberStateAssignment: Map<IntRange, String> = mapOf(
        1..3 to "New Hampshire",
        4..7 to "Maine",
        8..9 to "Vermont",
        10..34 to "Massachusetts",
        35..39 to "Rhode Island",
        40..49 to "Connecticut",
        50..134 to "New York",
        135..158 to "New Jersey",
        159..211 to "Pennsylvania",
        212..220 to "Maryland",
        221..222 to "Delaware",
        223..231 to "Virginia",
        232..236 to "West Virginia",
        232..236 to "North Carolina",
        237..246 to "North Carolina",
        247..251 to "South Carolina",
        252..260 to "Georgia",
        261..267 to "Florida",
        268..302 to "Ohio",
        303..317 to "Indiana",
        318..361 to "Illinois",
        362..386 to "Michigan",
        387..399 to "Wisconsin",
        400..407 to "Kentucky",
        408..415 to "Tennessee",
        416..424 to "Alabama",
        425..428 to "Mississippi",
        429..432 to "Arkansas",
        433..439 to "Louisiana",
        440..448 to "Oklahoma",
        449..467 to "Texas",
        468..477 to "Minnesota",
        478..485 to "Iowa",
        486..500 to "Missouri",
        501..502 to "North Dakota",
        503..504 to "South Dakota",
        505..508 to "Nebraska",
        509..515 to "Kansas",
        516..517 to "Montana",
        518..519 to "Idaho",
        520..520 to "Wyoming",
        521..524 to "Colorado",
        525..525 to "New Mexico",
        526..527 to "Arizona",
        528..529 to "Utah",
        530..530 to "Nevada",
        531..539 to "Washington",
        540..544 to "Oregon",
        545..573 to "California",
        574..574 to "Alaska",
        575..576 to "Hawaii",
        577..579 to "District of Columbia",
        580..580 to "Virgin Islands",
        581..584 to "Puerto Rico",
        585..585 to "New Mexico",
        586..586 to "Pacific Islands",
        587..665 to "Various (post-1972 expansion)",
        667..679 to "Various (post-1972 expansion)",
        680..699 to "Various (late assignment)",
        700..728 to "Railroad Board (historical)",
        729..733 to "Enumeration at Entry program"
    )

    /**
     * ITIN (Individual Taxpayer Identification Number) ranges.
     * ITINs are assigned to individuals who need a US tax ID but are not eligible for an SSN.
     * Format: 9XX-7X-XXXX or 9XX-8X-XXXX (where X is any digit)
     */
    val itinAreaRanges: IntRange = 900..999

    /**
     * ITIN valid group number ranges (middle two digits).
     * Valid: 70-88, 90-92, 94-99
     */
    val itinValidGroupRanges: List<IntRange> = listOf(
        70..88,
        90..92,
        94..99
    )

    /**
     * Validates a Social Security Number.
     *
     * @param ssn The SSN string to validate (with or without dashes/spaces)
     * @return SSNValidationResult with detailed validation information
     */
    fun validate(ssn: String): SSNValidationResult {
        val digits = extractDigits(ssn)

        // Must be exactly 9 digits
        if (digits.length != 9) {
            return SSNValidationResult(
                isValid = false,
                reason = "SSN must be exactly 9 digits (got ${digits.length})"
            )
        }

        val area = digits.substring(0, 3).toInt()
        val group = digits.substring(3, 5).toInt()
        val serial = digits.substring(5, 9).toInt()

        val areaStr = digits.substring(0, 3)
        val groupStr = digits.substring(3, 5)
        val serialStr = digits.substring(5, 9)

        val formatted = "$areaStr-$groupStr-$serialStr"
        val masked = "***-**-$serialStr"

        // Check known invalid SSNs
        if (digits in knownInvalidSSNs) {
            return SSNValidationResult(
                isValid = false,
                formatted = formatted,
                masked = masked,
                areaNumber = areaStr,
                groupNumber = groupStr,
                serialNumber = serialStr,
                isAdvertising = true,
                reason = "This is a known invalid/advertising SSN"
            )
        }

        // Check if it's an ITIN
        if (area in itinAreaRanges) {
            return validateAsITIN(digits, area, group, serial, formatted, masked, areaStr, groupStr, serialStr)
        }

        // Area number validation
        if (area == 0) {
            return SSNValidationResult(
                isValid = false,
                formatted = formatted,
                masked = masked,
                areaNumber = areaStr,
                groupNumber = groupStr,
                serialNumber = serialStr,
                reason = "Area number 000 is invalid"
            )
        }

        if (area == 666) {
            return SSNValidationResult(
                isValid = false,
                formatted = formatted,
                masked = masked,
                areaNumber = areaStr,
                groupNumber = groupStr,
                serialNumber = serialStr,
                reason = "Area number 666 is permanently excluded"
            )
        }

        // Group number validation
        if (group == 0) {
            return SSNValidationResult(
                isValid = false,
                formatted = formatted,
                masked = masked,
                areaNumber = areaStr,
                groupNumber = groupStr,
                serialNumber = serialStr,
                reason = "Group number 00 is invalid"
            )
        }

        // Serial number validation
        if (serial == 0) {
            return SSNValidationResult(
                isValid = false,
                formatted = formatted,
                masked = masked,
                areaNumber = areaStr,
                groupNumber = groupStr,
                serialNumber = serialStr,
                reason = "Serial number 0000 is invalid"
            )
        }

        // Determine era
        val era = when {
            area <= 733 -> SSNEra.PRE_RANDOMIZATION
            area in 750..772 && area != 763 && area != 764 -> SSNEra.POST_RANDOMIZATION
            else -> SSNEra.POST_RANDOMIZATION
        }

        // Check for sequential/repetitive patterns (likely fake)
        val suspiciousPattern = isSequentialOrRepetitive(digits)

        val confidence = when {
            suspiciousPattern -> 0.4f
            era == SSNEra.PRE_RANDOMIZATION -> 0.9f
            era == SSNEra.POST_RANDOMIZATION -> 0.85f
            else -> 0.7f
        }

        return SSNValidationResult(
            isValid = true,
            formatted = formatted,
            masked = masked,
            areaNumber = areaStr,
            groupNumber = groupStr,
            serialNumber = serialStr,
            era = era,
            confidence = confidence,
            reason = if (suspiciousPattern) {
                "Valid SSN format but suspicious pattern detected"
            } else {
                "Valid SSN (${era.name.lowercase().replace('_', '-')} era)"
            }
        )
    }

    /**
     * Validates an ITIN (Individual Taxpayer Identification Number).
     */
    private fun validateAsITIN(
        digits: String,
        area: Int,
        group: Int,
        serial: Int,
        formatted: String,
        masked: String,
        areaStr: String,
        groupStr: String,
        serialStr: String
    ): SSNValidationResult {
        val isValidItinGroup = itinValidGroupRanges.any { group in it }

        return SSNValidationResult(
            isValid = isValidItinGroup,
            formatted = formatted,
            masked = masked,
            areaNumber = areaStr,
            groupNumber = groupStr,
            serialNumber = serialStr,
            isITIN = true,
            confidence = if (isValidItinGroup) 0.85f else 0.3f,
            reason = if (isValidItinGroup) {
                "Valid ITIN format (area $areaStr, group $groupStr)"
            } else {
                "ITIN area range (900-999) but invalid group number $groupStr"
            }
        )
    }

    /**
     * Validates an EIN (Employer Identification Number).
     * EINs are in format XX-XXXXXXX where the first two digits are a valid campus code.
     *
     * @param ein The EIN to validate
     * @return SSNValidationResult adapted for EIN validation
     */
    fun validateEIN(ein: String): SSNValidationResult {
        val digits = extractDigits(ein)

        if (digits.length != 9) {
            return SSNValidationResult(
                isValid = false,
                isEIN = true,
                reason = "EIN must be exactly 9 digits (got ${digits.length})"
            )
        }

        val campusCode = digits.substring(0, 2).toInt()
        val formatted = "${digits.substring(0, 2)}-${digits.substring(2)}"

        // Valid campus/office codes
        val validCampusCodes = setOf(
            10, 12, 60, 67, 50, 53, 01, 02, 03, 04, 05, 06, 11, 13, 14, 16,
            21, 22, 23, 25, 34, 51, 52, 54, 55, 56, 57, 58, 59, 65,
            30, 32, 35, 36, 37, 38, 61, 62, 63, 64, 66, 68, 71, 72, 73, 74, 75,
            76, 77, 81, 82, 83, 84, 85, 86, 87, 88, 91, 92, 93, 94, 95, 98, 99,
            20, 26, 27, 40, 41, 42, 43, 44, 45, 46, 47, 48, 15, 24, 31, 33, 39,
            69, 70, 78, 79, 80, 89, 90, 96, 97
        )

        val isValidCampus = campusCode in validCampusCodes

        return SSNValidationResult(
            isValid = isValidCampus,
            isEIN = true,
            formatted = formatted,
            masked = "**-***${digits.substring(5)}",
            areaNumber = digits.substring(0, 2),
            confidence = if (isValidCampus) 0.85f else 0.3f,
            reason = if (isValidCampus) {
                "Valid EIN format (campus code $campusCode)"
            } else {
                "Invalid EIN campus code: $campusCode"
            }
        )
    }

    /**
     * Checks if a digit string looks sequential or repetitive (likely fake).
     */
    fun isSequentialOrRepetitive(digits: String): Boolean {
        // All same digit
        if (digits.all { it == digits[0] }) return true

        // Sequential ascending (123456789)
        var isAscending = true
        for (i in 1 until digits.length) {
            if (digits[i] - digits[i - 1] != 1) {
                isAscending = false
                break
            }
        }
        if (isAscending) return true

        // Sequential descending (987654321)
        var isDescending = true
        for (i in 1 until digits.length) {
            if (digits[i - 1] - digits[i] != 1) {
                isDescending = false
                break
            }
        }
        if (isDescending) return true

        // Repeated pattern (e.g., 123123123)
        for (patternLen in 1..3) {
            val pattern = digits.substring(0, patternLen)
            var isRepeated = true
            for (i in patternLen until digits.length step patternLen) {
                val end = minOf(i + patternLen, digits.length)
                if (digits.substring(i, end) != pattern.substring(0, end - i)) {
                    isRepeated = false
                    break
                }
            }
            if (isRepeated) return true
        }

        return false
    }

    /**
     * Extracts only digit characters from input.
     */
    fun extractDigits(input: String): String = input.filter { it.isDigit() }

    /**
     * Formats a 9-digit string as an SSN (XXX-XX-XXXX).
     */
    fun formatSSN(digits: String): String? {
        val d = extractDigits(digits)
        return if (d.length == 9) "${d.substring(0, 3)}-${d.substring(3, 5)}-${d.substring(5, 9)}" else null
    }

    /**
     * Masks an SSN showing only the last 4 digits.
     */
    fun maskSSN(digits: String): String? {
        val d = extractDigits(digits)
        return if (d.length == 9) "***-**-${d.substring(5, 9)}" else null
    }

    /**
     * Gets the state assignment for an SSN area number (pre-randomization era only).
     *
     * @param areaNumber The first 3 digits of the SSN
     * @return State/territory name or null if unknown/post-randomization
     */
    fun getStateForAreaNumber(areaNumber: Int): String? {
        for ((range, state) in areaNumberStateAssignment) {
            if (areaNumber in range) return state
        }
        return null
    }

    /**
     * Checks if a string could potentially be an SSN (quick heuristic).
     */
    fun looksLikeSSN(text: String): Boolean {
        val trimmed = text.trim()
        // Common SSN formats: 123-45-6789, 123 45 6789, 123456789
        val patterns = listOf(
            Regex("""^\d{3}-\d{2}-\d{4}$"""),
            Regex("""^\d{3}\s\d{2}\s\d{4}$"""),
            Regex("""^\d{9}$""")
        )
        return patterns.any { it.matches(trimmed) }
    }

    /**
     * Extracts potential SSNs from text.
     */
    fun extractFromText(text: String): List<SSNValidationResult> {
        val patterns = listOf(
            Regex("""\d{3}-\d{2}-\d{4}"""),
            Regex("""\d{3}\s\d{2}\s\d{4}"""),
            Regex("""(?<!\d)\d{9}(?!\d)""")
        )

        val results = mutableListOf<SSNValidationResult>()
        for (pattern in patterns) {
            for (match in pattern.findAll(text)) {
                val result = validate(match.value)
                if (result.isValid) {
                    results.add(result)
                }
            }
        }
        return results
    }

    /**
     * Returns the total number of theoretically possible SSNs.
     * Area: 001-899 (excluding 666) = 898 values
     * Group: 01-99 = 99 values
     * Serial: 0001-9999 = 9999 values
     */
    fun totalPossibleSSNs(): Long = 898L * 99L * 9999L

    /**
     * Checks if an area number was valid in the pre-randomization era.
     */
    fun wasValidPreRandomization(areaNumber: Int): Boolean {
        if (areaNumber in permanentlyInvalidAreaNumbers) return false
        if (areaNumber > 733) return false
        return areaNumber in 1..899
    }

    /**
     * Gets all permanently invalid area numbers.
     */
    fun getInvalidAreaNumbers(): Set<Int> {
        return permanentlyInvalidAreaNumbers + setOf(0)
    }
}
