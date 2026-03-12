package com.privacyguard.util

/**
 * Comprehensive credit card validation utility.
 * Validates card numbers using Luhn algorithm and IIN (Issuer Identification Number) ranges.
 *
 * Supports all major card networks:
 * - Visa
 * - Mastercard
 * - American Express (AMEX)
 * - Discover
 * - Diners Club
 * - JCB
 * - UnionPay
 * - Maestro
 * - Mir
 * - Elo
 * - Hipercard
 * - Verve
 * - RuPay
 * - Troy
 * - BC Card
 * - InterPayment
 * - InstaPayment
 * - Dankort
 * - UATP
 * - Laser
 * - Solo
 * - Switch
 * - China T-Union
 * - Lankapay
 * - NPS Pridnestrovie
 *
 * Also provides card number formatting, masking, and metadata extraction.
 */
object CreditCardValidator {

    /**
     * Represents a credit card network with its validation rules.
     *
     * @param name Display name of the card network
     * @param shortName Abbreviated name (e.g., "VISA", "MC")
     * @param prefixes List of valid IIN prefix ranges
     * @param validLengths Set of valid card number lengths
     * @param usesLuhn Whether this network uses Luhn checksum
     * @param cvvLength Length of the CVV/CVC code
     * @param spacing Card number display spacing pattern (e.g., [4,4,4,4] for Visa)
     */
    data class CardNetwork(
        val name: String,
        val shortName: String,
        val prefixes: List<PrefixRange>,
        val validLengths: Set<Int>,
        val usesLuhn: Boolean = true,
        val cvvLength: Int = 3,
        val spacing: List<Int> = listOf(4, 4, 4, 4),
        val category: CardCategory = CardCategory.MAJOR
    )

    /**
     * Represents a range of valid IIN prefixes for a card network.
     *
     * @param start Start of prefix range (inclusive)
     * @param end End of prefix range (inclusive)
     * @param length Number of digits in the prefix to check
     */
    data class PrefixRange(
        val start: Long,
        val end: Long,
        val length: Int
    ) {
        constructor(value: Long, length: Int) : this(value, value, length)
    }

    /**
     * Category classification for card networks.
     */
    enum class CardCategory {
        MAJOR,           // Global networks (Visa, MC, Amex, Discover)
        REGIONAL,        // Regional networks (UnionPay, Mir, RuPay, etc.)
        DEPRECATED,      // Discontinued networks (Laser, Solo, Switch)
        SPECIALTY        // Special purpose cards (UATP, etc.)
    }

    /**
     * Result of credit card validation.
     *
     * @param isValid Whether the card number passes all validation checks
     * @param network Detected card network, if any
     * @param luhnValid Whether the number passes Luhn checksum
     * @param lengthValid Whether the number length is valid for detected network
     * @param prefixValid Whether the IIN prefix matches a known network
     * @param formatted Formatted card number with spacing
     * @param masked Masked card number (e.g., "**** **** **** 1234")
     * @param confidence Validation confidence score
     * @param reason Human-readable validation result
     */
    data class CardValidationResult(
        val isValid: Boolean,
        val network: CardNetwork? = null,
        val luhnValid: Boolean = false,
        val lengthValid: Boolean = false,
        val prefixValid: Boolean = false,
        val formatted: String = "",
        val masked: String = "",
        val confidence: Float = 0f,
        val reason: String = ""
    )

    /**
     * Complete database of known card networks with their validation rules.
     */
    val networks: List<CardNetwork> = listOf(
        // ============================================
        // MAJOR GLOBAL NETWORKS
        // ============================================

        CardNetwork(
            name = "Visa",
            shortName = "VISA",
            prefixes = listOf(PrefixRange(4, 1)),
            validLengths = setOf(13, 16, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.MAJOR
        ),

        CardNetwork(
            name = "Mastercard",
            shortName = "MC",
            prefixes = listOf(
                PrefixRange(51, 55, 2),  // Classic range
                PrefixRange(2221, 2720, 4)  // 2-series range (2017+)
            ),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.MAJOR
        ),

        CardNetwork(
            name = "American Express",
            shortName = "AMEX",
            prefixes = listOf(
                PrefixRange(34, 2),
                PrefixRange(37, 2)
            ),
            validLengths = setOf(15),
            cvvLength = 4,
            spacing = listOf(4, 6, 5),
            category = CardCategory.MAJOR
        ),

        CardNetwork(
            name = "Discover",
            shortName = "DISC",
            prefixes = listOf(
                PrefixRange(6011, 6011, 4),
                PrefixRange(644, 649, 3),
                PrefixRange(65, 2),
                PrefixRange(622126, 622925, 6)  // UnionPay co-branded
            ),
            validLengths = setOf(16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.MAJOR
        ),

        // ============================================
        // REGIONAL NETWORKS
        // ============================================

        CardNetwork(
            name = "UnionPay",
            shortName = "UP",
            prefixes = listOf(
                PrefixRange(62, 2),
                PrefixRange(81, 2)
            ),
            validLengths = setOf(16, 17, 18, 19),
            usesLuhn = false,  // Some UnionPay cards don't use Luhn
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "JCB",
            shortName = "JCB",
            prefixes = listOf(PrefixRange(3528, 3589, 4)),
            validLengths = setOf(15, 16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Diners Club Carte Blanche",
            shortName = "DINR",
            prefixes = listOf(PrefixRange(300, 305, 3)),
            validLengths = setOf(14, 15, 16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 6, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Diners Club International",
            shortName = "DINR",
            prefixes = listOf(
                PrefixRange(36, 2),
                PrefixRange(38, 2)
            ),
            validLengths = setOf(14, 15, 16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 6, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Diners Club USA/Canada",
            shortName = "DINR",
            prefixes = listOf(
                PrefixRange(54, 2),
                PrefixRange(55, 2)
            ),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Maestro",
            shortName = "MAES",
            prefixes = listOf(
                PrefixRange(5018, 4),
                PrefixRange(5020, 4),
                PrefixRange(5038, 4),
                PrefixRange(5893, 4),
                PrefixRange(6304, 4),
                PrefixRange(6759, 4),
                PrefixRange(6761, 6763, 4)
            ),
            validLengths = setOf(12, 13, 14, 15, 16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Maestro UK",
            shortName = "MAES",
            prefixes = listOf(
                PrefixRange(6759, 4),
                PrefixRange(676770, 676770, 6),
                PrefixRange(676774, 676774, 6)
            ),
            validLengths = setOf(12, 13, 14, 15, 16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Mir",
            shortName = "MIR",
            prefixes = listOf(PrefixRange(2200, 2204, 4)),
            validLengths = setOf(16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Elo",
            shortName = "ELO",
            prefixes = listOf(
                PrefixRange(401178, 401178, 6),
                PrefixRange(401179, 401179, 6),
                PrefixRange(438935, 438935, 6),
                PrefixRange(457631, 457631, 6),
                PrefixRange(457632, 457632, 6),
                PrefixRange(431274, 431274, 6),
                PrefixRange(451416, 451416, 6),
                PrefixRange(504175, 504175, 6),
                PrefixRange(506699, 506778, 6),
                PrefixRange(509000, 509999, 6),
                PrefixRange(627780, 627780, 6),
                PrefixRange(636297, 636297, 6),
                PrefixRange(636368, 636368, 6),
                PrefixRange(650031, 650033, 6),
                PrefixRange(650035, 650051, 6),
                PrefixRange(650405, 650439, 6),
                PrefixRange(650485, 650538, 6),
                PrefixRange(650541, 650598, 6),
                PrefixRange(650700, 650718, 6),
                PrefixRange(650720, 650727, 6),
                PrefixRange(650901, 650920, 6),
                PrefixRange(651652, 651679, 6),
                PrefixRange(655000, 655019, 6),
                PrefixRange(655021, 655058, 6)
            ),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Hipercard",
            shortName = "HIPE",
            prefixes = listOf(PrefixRange(606282, 606282, 6)),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "RuPay",
            shortName = "RUPY",
            prefixes = listOf(
                PrefixRange(60, 2),
                PrefixRange(65, 2),
                PrefixRange(81, 2),
                PrefixRange(82, 2),
                PrefixRange(508, 3),
                PrefixRange(353, 3),
                PrefixRange(356, 3)
            ),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Troy",
            shortName = "TROY",
            prefixes = listOf(
                PrefixRange(979200, 979289, 6),
                PrefixRange(65, 2)
            ),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Verve",
            shortName = "VERV",
            prefixes = listOf(
                PrefixRange(506099, 506198, 6),
                PrefixRange(650002, 650027, 6)
            ),
            validLengths = setOf(16, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "BC Card",
            shortName = "BC",
            prefixes = listOf(
                PrefixRange(3569, 4),
                PrefixRange(4, 1),
                PrefixRange(94, 2)
            ),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "InterPayment",
            shortName = "INTP",
            prefixes = listOf(PrefixRange(636, 3)),
            validLengths = setOf(16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "InstaPayment",
            shortName = "INST",
            prefixes = listOf(PrefixRange(637, 639, 3)),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "Dankort",
            shortName = "DANK",
            prefixes = listOf(
                PrefixRange(5019, 4),
                PrefixRange(4571, 4)
            ),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "LankaPay",
            shortName = "LNKP",
            prefixes = listOf(PrefixRange(357111, 357111, 6)),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "China T-Union",
            shortName = "TUN",
            prefixes = listOf(PrefixRange(31, 2)),
            validLengths = setOf(19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4, 3),
            category = CardCategory.REGIONAL
        ),

        CardNetwork(
            name = "NPS Pridnestrovie",
            shortName = "NPS",
            prefixes = listOf(
                PrefixRange(6054740, 6054740, 7),
                PrefixRange(6054741, 6054741, 7),
                PrefixRange(6054742, 6054742, 7),
                PrefixRange(6054743, 6054743, 7),
                PrefixRange(6054744, 6054744, 7)
            ),
            validLengths = setOf(16),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.REGIONAL
        ),

        // ============================================
        // SPECIALTY NETWORKS
        // ============================================

        CardNetwork(
            name = "UATP",
            shortName = "UATP",
            prefixes = listOf(PrefixRange(1, 1)),
            validLengths = setOf(15),
            cvvLength = 3,
            spacing = listOf(4, 5, 6),
            category = CardCategory.SPECIALTY
        ),

        // ============================================
        // DEPRECATED / HISTORICAL NETWORKS
        // ============================================

        CardNetwork(
            name = "Laser",
            shortName = "LASR",
            prefixes = listOf(
                PrefixRange(6304, 4),
                PrefixRange(6706, 4),
                PrefixRange(6771, 4),
                PrefixRange(6709, 4)
            ),
            validLengths = setOf(16, 17, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.DEPRECATED
        ),

        CardNetwork(
            name = "Solo",
            shortName = "SOLO",
            prefixes = listOf(
                PrefixRange(6334, 4),
                PrefixRange(6767, 4)
            ),
            validLengths = setOf(16, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.DEPRECATED
        ),

        CardNetwork(
            name = "Switch",
            shortName = "SWCH",
            prefixes = listOf(
                PrefixRange(4903, 4),
                PrefixRange(4905, 4),
                PrefixRange(4911, 4),
                PrefixRange(4936, 4),
                PrefixRange(564182, 564182, 6),
                PrefixRange(633110, 633110, 6),
                PrefixRange(6333, 4),
                PrefixRange(6759, 4)
            ),
            validLengths = setOf(16, 18, 19),
            cvvLength = 3,
            spacing = listOf(4, 4, 4, 4),
            category = CardCategory.DEPRECATED
        )
    )

    /**
     * Pre-computed network lookup optimized for common prefix lengths.
     */
    private val networksByPrefixLength: Map<Int, List<CardNetwork>> by lazy {
        val map = mutableMapOf<Int, MutableList<CardNetwork>>()
        for (network in networks) {
            for (prefix in network.prefixes) {
                map.getOrPut(prefix.length) { mutableListOf() }.add(network)
            }
        }
        map
    }

    /**
     * Validates a credit card number.
     *
     * @param cardNumber The card number to validate (may include spaces, dashes)
     * @return CardValidationResult with full validation details
     */
    fun validate(cardNumber: String): CardValidationResult {
        val digits = extractDigits(cardNumber)

        if (digits.isEmpty()) {
            return CardValidationResult(
                isValid = false,
                reason = "No digits found in input"
            )
        }

        if (digits.length < 8) {
            return CardValidationResult(
                isValid = false,
                reason = "Card number too short: ${digits.length} digits (minimum 8)"
            )
        }

        if (digits.length > 19) {
            return CardValidationResult(
                isValid = false,
                reason = "Card number too long: ${digits.length} digits (maximum 19)"
            )
        }

        // Detect network
        val network = detectNetwork(digits)
        val prefixValid = network != null
        val lengthValid = network?.validLengths?.contains(digits.length) ?: (digits.length in 12..19)
        val luhnValid = luhnCheck(digits)

        val isValid = when {
            network != null -> {
                lengthValid && (if (network.usesLuhn) luhnValid else true)
            }
            else -> luhnValid && digits.length in 12..19
        }

        val confidence = when {
            prefixValid && lengthValid && luhnValid -> 0.99f
            prefixValid && lengthValid -> 0.85f
            prefixValid && luhnValid -> 0.7f
            luhnValid && digits.length == 16 -> 0.6f
            luhnValid -> 0.4f
            else -> 0.1f
        }

        return CardValidationResult(
            isValid = isValid,
            network = network,
            luhnValid = luhnValid,
            lengthValid = lengthValid,
            prefixValid = prefixValid,
            formatted = formatCardNumber(digits, network),
            masked = maskCardNumber(digits, network),
            confidence = confidence,
            reason = buildValidationReason(isValid, network, luhnValid, lengthValid, prefixValid, digits.length)
        )
    }

    /**
     * Detects the card network from a card number.
     *
     * @param digits Clean digit string
     * @return Detected CardNetwork or null
     */
    fun detectNetwork(digits: String): CardNetwork? {
        if (digits.isEmpty()) return null

        // Check from longest prefix to shortest for most specific match
        val prefixLengths = listOf(7, 6, 4, 3, 2, 1)
        for (prefixLen in prefixLengths) {
            if (digits.length < prefixLen) continue
            val prefix = digits.substring(0, prefixLen).toLongOrNull() ?: continue
            val candidateNetworks = networksByPrefixLength[prefixLen] ?: continue
            for (network in candidateNetworks) {
                for (range in network.prefixes) {
                    if (range.length == prefixLen && prefix in range.start..range.end) {
                        return network
                    }
                }
            }
        }
        return null
    }

    /**
     * Performs the Luhn checksum algorithm.
     *
     * The Luhn algorithm (also known as the "modulus 10" algorithm) is used
     * to validate credit card numbers, IMEI numbers, and other identification
     * numbers.
     *
     * Algorithm steps:
     * 1. Starting from the rightmost digit, double every second digit
     * 2. If doubling results in a number > 9, subtract 9
     * 3. Sum all digits
     * 4. If total modulo 10 equals 0, the number is valid
     *
     * @param digits String of digits to validate
     * @return true if the number passes the Luhn check
     */
    fun luhnCheck(digits: String): Boolean {
        if (digits.isEmpty() || !digits.all { it.isDigit() }) return false

        var sum = 0
        var isSecondDigit = false

        for (i in digits.length - 1 downTo 0) {
            var digit = digits[i] - '0'

            if (isSecondDigit) {
                digit *= 2
                if (digit > 9) digit -= 9
            }

            sum += digit
            isSecondDigit = !isSecondDigit
        }

        return sum % 10 == 0
    }

    /**
     * Generates the Luhn check digit for a partial card number.
     *
     * @param partialNumber Card number without the check digit
     * @return The check digit (0-9)
     */
    fun generateLuhnCheckDigit(partialNumber: String): Int {
        val digits = partialNumber.filter { it.isDigit() }
        var sum = 0
        var isSecondDigit = true

        for (i in digits.length - 1 downTo 0) {
            var digit = digits[i] - '0'
            if (isSecondDigit) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            sum += digit
            isSecondDigit = !isSecondDigit
        }

        return (10 - (sum % 10)) % 10
    }

    /**
     * Extracts only digit characters from a card number string.
     */
    fun extractDigits(cardNumber: String): String {
        return cardNumber.filter { it.isDigit() }
    }

    /**
     * Formats a card number with proper spacing for its network.
     *
     * @param digits Clean digit string
     * @param network Detected card network (for spacing pattern)
     * @return Formatted card number string
     */
    fun formatCardNumber(digits: String, network: CardNetwork? = null): String {
        val spacing = network?.spacing ?: listOf(4, 4, 4, 4, 3)
        val sb = StringBuilder()
        var pos = 0

        for ((index, groupSize) in spacing.withIndex()) {
            if (pos >= digits.length) break
            if (index > 0) sb.append(' ')
            val end = minOf(pos + groupSize, digits.length)
            sb.append(digits.substring(pos, end))
            pos = end
        }

        // Append any remaining digits
        if (pos < digits.length) {
            sb.append(' ')
            sb.append(digits.substring(pos))
        }

        return sb.toString()
    }

    /**
     * Masks a card number showing only the last 4 digits.
     *
     * @param digits Clean digit string
     * @param network Detected card network (for spacing)
     * @return Masked card number (e.g., "**** **** **** 1234")
     */
    fun maskCardNumber(digits: String, network: CardNetwork? = null): String {
        if (digits.length <= 4) return digits
        val lastFour = digits.takeLast(4)
        val maskedLength = digits.length - 4
        val maskedDigits = "*".repeat(maskedLength) + lastFour
        return formatCardNumber(maskedDigits, network)
    }

    /**
     * Masks a card number showing first 6 (BIN) and last 4 digits.
     *
     * @param digits Clean digit string
     * @return BIN-masked card number (e.g., "411111** **** 1234")
     */
    fun binMaskCardNumber(digits: String): String {
        if (digits.length <= 10) return maskCardNumber(digits)
        val bin = digits.take(6)
        val lastFour = digits.takeLast(4)
        val middleLength = digits.length - 10
        val masked = bin + "*".repeat(middleLength) + lastFour
        return formatCardNumber(masked)
    }

    /**
     * Checks if a string could potentially be a credit card number.
     * Quick heuristic check without full validation.
     */
    fun looksLikeCreditCard(text: String): Boolean {
        val digits = text.filter { it.isDigit() }
        if (digits.length < 13 || digits.length > 19) return false

        // Check for reasonable digit density
        val nonDigits = text.length - digits.length
        if (nonDigits > digits.length) return false

        // Quick Luhn check
        return luhnCheck(digits)
    }

    /**
     * Extracts all potential credit card numbers from a text string.
     *
     * @param text Input text to scan
     * @return List of CardValidationResult for each potential card found
     */
    fun extractFromText(text: String): List<CardValidationResult> {
        val results = mutableListOf<CardValidationResult>()

        // Pattern: 13-19 consecutive digits, possibly separated by spaces/dashes
        val pattern = Regex("""(?:\d[\s\-]?){13,19}""")
        val matches = pattern.findAll(text)

        for (match in matches) {
            val result = validate(match.value)
            if (result.isValid) {
                results.add(result)
            }
        }

        return results
    }

    /**
     * Returns the BIN (Bank Identification Number) from a card number.
     * The BIN is typically the first 6-8 digits.
     *
     * @param cardNumber Card number (with or without formatting)
     * @param length BIN length to extract (6 or 8)
     * @return BIN string or null if card number is too short
     */
    fun extractBIN(cardNumber: String, length: Int = 6): String? {
        val digits = extractDigits(cardNumber)
        return if (digits.length >= length) digits.take(length) else null
    }

    /**
     * Generates a valid test card number for a given network.
     * Uses known test prefixes and generates valid Luhn check digit.
     *
     * NOTE: These are test numbers only and will not work for real transactions.
     *
     * @param network The card network
     * @return A valid test card number
     */
    fun generateTestCardNumber(network: CardNetwork): String {
        val prefix = network.prefixes.first()
        val targetLength = network.validLengths.first()
        val prefixStr = prefix.start.toString()

        // Pad with zeros to almost target length
        val padded = prefixStr + "0".repeat(targetLength - prefixStr.length - 1)

        // Calculate and append check digit
        val checkDigit = generateLuhnCheckDigit(padded)
        return padded + checkDigit
    }

    /**
     * Well-known test card numbers for development/testing.
     * These numbers pass Luhn but are designated as test numbers
     * by the respective card networks.
     */
    val testCardNumbers: Map<String, List<String>> = mapOf(
        "Visa" to listOf(
            "4111111111111111",
            "4012888888881881",
            "4222222222222",
            "4000056655665556",
            "4242424242424242"
        ),
        "Mastercard" to listOf(
            "5555555555554444",
            "5200828282828210",
            "5105105105105100",
            "2223003122003222",
            "2223000048400011"
        ),
        "American Express" to listOf(
            "378282246310005",
            "371449635398431",
            "378734493671000"
        ),
        "Discover" to listOf(
            "6011111111111117",
            "6011000990139424",
            "6445644564456445"
        ),
        "Diners Club" to listOf(
            "30569309025904",
            "38520000023237",
            "36700102000000"
        ),
        "JCB" to listOf(
            "3530111333300000",
            "3566002020360505",
            "3566111111111113"
        ),
        "UnionPay" to listOf(
            "6200000000000005",
            "6212345678901234",
            "8100000000000000"
        ),
        "Maestro" to listOf(
            "6304000000000000",
            "6759649826438453",
            "6761999999999999"
        )
    )

    /**
     * Checks if a card number is a known test card number.
     */
    fun isTestCardNumber(cardNumber: String): Boolean {
        val digits = extractDigits(cardNumber)
        return testCardNumbers.values.any { numbers -> digits in numbers }
    }

    /**
     * Builds a human-readable validation reason string.
     */
    private fun buildValidationReason(
        isValid: Boolean,
        network: CardNetwork?,
        luhnValid: Boolean,
        lengthValid: Boolean,
        prefixValid: Boolean,
        length: Int
    ): String {
        if (isValid) {
            return when {
                network != null -> "Valid ${network.name} card number ($length digits)"
                else -> "Valid card number ($length digits, Luhn check passed)"
            }
        }

        val reasons = mutableListOf<String>()
        if (!prefixValid) reasons.add("prefix does not match any known card network")
        if (!lengthValid) {
            if (network != null) {
                reasons.add("invalid length $length for ${network.name} (expected ${network.validLengths.joinToString("/")})")
            } else {
                reasons.add("unusual length: $length digits")
            }
        }
        if (!luhnValid) reasons.add("Luhn checksum failed")

        return "Invalid card number: ${reasons.joinToString(", ")}"
    }

    /**
     * Gets all networks of a specific category.
     */
    fun getNetworksByCategory(category: CardCategory): List<CardNetwork> {
        return networks.filter { it.category == category }
    }

    /**
     * Gets a network by its short name.
     */
    fun getNetworkByShortName(shortName: String): CardNetwork? {
        return networks.find { it.shortName.equals(shortName, ignoreCase = true) }
    }

    /**
     * Gets a network by its full name.
     */
    fun getNetworkByName(name: String): CardNetwork? {
        return networks.find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Returns the total number of supported card networks.
     */
    fun totalNetworks(): Int = networks.size

    /**
     * Returns the total number of active (non-deprecated) card networks.
     */
    fun activeNetworks(): Int = networks.count { it.category != CardCategory.DEPRECATED }
}
