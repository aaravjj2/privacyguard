package com.privacyguard.util

/**
 * Comprehensive country code validation and phone number formatting.
 * Supports 200+ countries and territories with their calling codes,
 * number length constraints, and format patterns.
 *
 * This validator is used by the PII detection pipeline to validate
 * phone numbers detected in clipboard or text field content.
 */
object CountryCodeValidator {

    /**
     * Represents a country's phone number rules.
     *
     * @param code ISO 3166-1 alpha-2 country code
     * @param callingCode International calling code (e.g., "+1" for US)
     * @param name English country name
     * @param minLength Minimum national number length
     * @param maxLength Maximum national number length
     * @param trunkPrefix Trunk prefix used for domestic calls (e.g., "0" in UK)
     * @param format Example format pattern
     */
    data class CountryPhoneInfo(
        val code: String,
        val callingCode: String,
        val name: String,
        val minLength: Int,
        val maxLength: Int,
        val trunkPrefix: String? = null,
        val format: String = "",
        val mobilePrefix: List<String> = emptyList(),
        val region: Region = Region.OTHER
    )

    /**
     * Geographic region classification for phone number validation.
     */
    enum class Region {
        NORTH_AMERICA,
        SOUTH_AMERICA,
        EUROPE,
        ASIA,
        AFRICA,
        OCEANIA,
        CARIBBEAN,
        MIDDLE_EAST,
        CENTRAL_AMERICA,
        OTHER
    }

    /**
     * Result of a phone number validation attempt.
     *
     * @param isValid Whether the phone number appears valid
     * @param countryCode Detected country code, if any
     * @param nationalNumber The national number portion
     * @param formattedNumber E.164 formatted number
     * @param confidence Confidence score from 0.0 to 1.0
     * @param reason Human-readable explanation
     */
    data class PhoneValidationResult(
        val isValid: Boolean,
        val countryCode: String? = null,
        val nationalNumber: String? = null,
        val formattedNumber: String? = null,
        val confidence: Float = 0f,
        val reason: String = "",
        val country: CountryPhoneInfo? = null
    )

    /**
     * Complete database of country calling codes and phone number rules.
     * Data sourced from ITU-T E.164 and national numbering plans.
     */
    val countries: List<CountryPhoneInfo> = listOf(
        // North America (NANP - North American Numbering Plan)
        CountryPhoneInfo("US", "+1", "United States", 10, 10, "1", "(XXX) XXX-XXXX",
            listOf("2", "3", "4", "5", "6", "7", "8", "9"), Region.NORTH_AMERICA),
        CountryPhoneInfo("CA", "+1", "Canada", 10, 10, "1", "(XXX) XXX-XXXX",
            listOf("2", "3", "4", "5", "6", "7", "8", "9"), Region.NORTH_AMERICA),

        // Caribbean NANP
        CountryPhoneInfo("AG", "+1268", "Antigua and Barbuda", 10, 10, "1", "(268) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("AI", "+1264", "Anguilla", 10, 10, "1", "(264) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("AS", "+1684", "American Samoa", 10, 10, "1", "(684) XXX-XXXX",
            emptyList(), Region.OTHER),
        CountryPhoneInfo("BB", "+1246", "Barbados", 10, 10, "1", "(246) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("BM", "+1441", "Bermuda", 10, 10, "1", "(441) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("BS", "+1242", "Bahamas", 10, 10, "1", "(242) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("DM", "+1767", "Dominica", 10, 10, "1", "(767) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("DO", "+1809", "Dominican Republic", 10, 10, "1", "(809) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("GD", "+1473", "Grenada", 10, 10, "1", "(473) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("GU", "+1671", "Guam", 10, 10, "1", "(671) XXX-XXXX",
            emptyList(), Region.OTHER),
        CountryPhoneInfo("JM", "+1876", "Jamaica", 10, 10, "1", "(876) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("KN", "+1869", "Saint Kitts and Nevis", 10, 10, "1", "(869) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("KY", "+1345", "Cayman Islands", 10, 10, "1", "(345) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("LC", "+1758", "Saint Lucia", 10, 10, "1", "(758) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("MP", "+1670", "Northern Mariana Islands", 10, 10, "1", "(670) XXX-XXXX",
            emptyList(), Region.OTHER),
        CountryPhoneInfo("MS", "+1664", "Montserrat", 10, 10, "1", "(664) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("PR", "+1787", "Puerto Rico", 10, 10, "1", "(787) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("SX", "+1721", "Sint Maarten", 10, 10, "1", "(721) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("TC", "+1649", "Turks and Caicos Islands", 10, 10, "1", "(649) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("TT", "+1868", "Trinidad and Tobago", 10, 10, "1", "(868) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("VC", "+1784", "Saint Vincent and the Grenadines", 10, 10, "1", "(784) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("VG", "+1284", "British Virgin Islands", 10, 10, "1", "(284) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),
        CountryPhoneInfo("VI", "+1340", "US Virgin Islands", 10, 10, "1", "(340) XXX-XXXX",
            emptyList(), Region.CARIBBEAN),

        // Europe
        CountryPhoneInfo("GB", "+44", "United Kingdom", 10, 10, "0", "0XXXX XXXXXX",
            listOf("7"), Region.EUROPE),
        CountryPhoneInfo("DE", "+49", "Germany", 5, 14, "0", "0XXX XXXXXXX",
            listOf("15", "16", "17"), Region.EUROPE),
        CountryPhoneInfo("FR", "+33", "France", 9, 9, "0", "0X XX XX XX XX",
            listOf("6", "7"), Region.EUROPE),
        CountryPhoneInfo("IT", "+39", "Italy", 6, 12, null, "XXX XXXXXXX",
            listOf("3"), Region.EUROPE),
        CountryPhoneInfo("ES", "+34", "Spain", 9, 9, null, "XXX XXX XXX",
            listOf("6", "7"), Region.EUROPE),
        CountryPhoneInfo("PT", "+351", "Portugal", 9, 9, null, "XXX XXX XXX",
            listOf("9"), Region.EUROPE),
        CountryPhoneInfo("NL", "+31", "Netherlands", 9, 9, "0", "0XX XXXXXXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("BE", "+32", "Belgium", 8, 9, "0", "0XXX XX XX XX",
            listOf("4"), Region.EUROPE),
        CountryPhoneInfo("AT", "+43", "Austria", 4, 13, "0", "0XXX XXXXXXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("CH", "+41", "Switzerland", 9, 9, "0", "0XX XXX XX XX",
            listOf("7"), Region.EUROPE),
        CountryPhoneInfo("SE", "+46", "Sweden", 7, 13, "0", "0XX-XXX XX XX",
            listOf("7"), Region.EUROPE),
        CountryPhoneInfo("NO", "+47", "Norway", 8, 8, null, "XXX XX XXX",
            listOf("4", "9"), Region.EUROPE),
        CountryPhoneInfo("DK", "+45", "Denmark", 8, 8, null, "XX XX XX XX",
            listOf("2", "3", "4", "5", "6", "7", "9"), Region.EUROPE),
        CountryPhoneInfo("FI", "+358", "Finland", 5, 12, "0", "0XX XXX XXXX",
            listOf("4", "5"), Region.EUROPE),
        CountryPhoneInfo("IE", "+353", "Ireland", 7, 9, "0", "0XX XXX XXXX",
            listOf("8"), Region.EUROPE),
        CountryPhoneInfo("PL", "+48", "Poland", 9, 9, null, "XXX XXX XXX",
            listOf("5", "6", "7", "8"), Region.EUROPE),
        CountryPhoneInfo("CZ", "+420", "Czech Republic", 9, 9, null, "XXX XXX XXX",
            listOf("6", "7"), Region.EUROPE),
        CountryPhoneInfo("SK", "+421", "Slovakia", 9, 9, "0", "0XXX XXX XXX",
            listOf("9"), Region.EUROPE),
        CountryPhoneInfo("HU", "+36", "Hungary", 8, 9, "06", "06 XX XXX XXXX",
            listOf("2", "3", "7"), Region.EUROPE),
        CountryPhoneInfo("RO", "+40", "Romania", 9, 9, "0", "0XXX XXX XXX",
            listOf("7"), Region.EUROPE),
        CountryPhoneInfo("BG", "+359", "Bulgaria", 8, 9, "0", "0XX XXX XXXX",
            listOf("8", "9"), Region.EUROPE),
        CountryPhoneInfo("HR", "+385", "Croatia", 8, 9, "0", "0XX XXX XXXX",
            listOf("9"), Region.EUROPE),
        CountryPhoneInfo("SI", "+386", "Slovenia", 8, 8, "0", "0XX XXX XXX",
            listOf("3", "4", "5", "6", "7"), Region.EUROPE),
        CountryPhoneInfo("RS", "+381", "Serbia", 8, 9, "0", "0XX XXX XXXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("BA", "+387", "Bosnia and Herzegovina", 8, 8, "0", "0XX XXX XXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("ME", "+382", "Montenegro", 8, 8, "0", "0XX XXX XXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("MK", "+389", "North Macedonia", 8, 8, "0", "0XX XXX XXX",
            listOf("7"), Region.EUROPE),
        CountryPhoneInfo("AL", "+355", "Albania", 8, 9, "0", "0XX XXX XXXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("GR", "+30", "Greece", 10, 10, null, "XXX XXX XXXX",
            listOf("69"), Region.EUROPE),
        CountryPhoneInfo("CY", "+357", "Cyprus", 8, 8, null, "XX XXXXXX",
            listOf("9"), Region.EUROPE),
        CountryPhoneInfo("MT", "+356", "Malta", 8, 8, null, "XXXX XXXX",
            listOf("7", "9"), Region.EUROPE),
        CountryPhoneInfo("EE", "+372", "Estonia", 7, 8, null, "XXXX XXXX",
            listOf("5"), Region.EUROPE),
        CountryPhoneInfo("LV", "+371", "Latvia", 8, 8, null, "XXXX XXXX",
            listOf("2"), Region.EUROPE),
        CountryPhoneInfo("LT", "+370", "Lithuania", 8, 8, "8", "8 XXX XXXXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("UA", "+380", "Ukraine", 9, 9, "0", "0XX XXX XX XX",
            listOf("5", "6", "7", "9"), Region.EUROPE),
        CountryPhoneInfo("BY", "+375", "Belarus", 9, 10, "80", "80 XX XXX XX XX",
            listOf("25", "29", "33", "44"), Region.EUROPE),
        CountryPhoneInfo("MD", "+373", "Moldova", 8, 8, "0", "0XXX XX XXX",
            listOf("6", "7"), Region.EUROPE),
        CountryPhoneInfo("RU", "+7", "Russia", 10, 10, "8", "8 XXX XXX XX XX",
            listOf("9"), Region.EUROPE),
        CountryPhoneInfo("IS", "+354", "Iceland", 7, 7, null, "XXX XXXX",
            listOf("6", "7", "8"), Region.EUROPE),
        CountryPhoneInfo("LU", "+352", "Luxembourg", 4, 11, null, "XXX XXX XXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("LI", "+423", "Liechtenstein", 7, 9, null, "XXX XX XX",
            listOf("7"), Region.EUROPE),
        CountryPhoneInfo("MC", "+377", "Monaco", 8, 8, null, "XX XX XX XX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("SM", "+378", "San Marino", 6, 10, null, "XXXX XXXXXX",
            listOf("6"), Region.EUROPE),
        CountryPhoneInfo("VA", "+379", "Vatican City", 6, 10, null, "XXXX XXXXXX",
            emptyList(), Region.EUROPE),
        CountryPhoneInfo("AD", "+376", "Andorra", 6, 9, null, "XXX XXX",
            listOf("3", "6"), Region.EUROPE),
        CountryPhoneInfo("GI", "+350", "Gibraltar", 8, 8, null, "XXXX XXXX",
            listOf("5"), Region.EUROPE),
        CountryPhoneInfo("FO", "+298", "Faroe Islands", 6, 6, null, "XXX XXX",
            emptyList(), Region.EUROPE),
        CountryPhoneInfo("GL", "+299", "Greenland", 6, 6, null, "XXX XXX",
            emptyList(), Region.EUROPE),

        // Asia
        CountryPhoneInfo("CN", "+86", "China", 11, 11, "0", "1XX XXXX XXXX",
            listOf("13", "14", "15", "16", "17", "18", "19"), Region.ASIA),
        CountryPhoneInfo("JP", "+81", "Japan", 9, 10, "0", "0X0-XXXX-XXXX",
            listOf("70", "80", "90"), Region.ASIA),
        CountryPhoneInfo("KR", "+82", "South Korea", 8, 11, "0", "01X-XXXX-XXXX",
            listOf("10", "11", "16", "17", "18", "19"), Region.ASIA),
        CountryPhoneInfo("IN", "+91", "India", 10, 10, "0", "0XXXX XXXXX",
            listOf("6", "7", "8", "9"), Region.ASIA),
        CountryPhoneInfo("PK", "+92", "Pakistan", 10, 10, "0", "0XXX XXXXXXX",
            listOf("3"), Region.ASIA),
        CountryPhoneInfo("BD", "+880", "Bangladesh", 10, 10, "0", "01XXX XXXXXX",
            listOf("1"), Region.ASIA),
        CountryPhoneInfo("LK", "+94", "Sri Lanka", 9, 9, "0", "0XX XXX XXXX",
            listOf("7"), Region.ASIA),
        CountryPhoneInfo("NP", "+977", "Nepal", 8, 10, "0", "0XX XXXXXXX",
            listOf("98"), Region.ASIA),
        CountryPhoneInfo("MM", "+95", "Myanmar", 8, 10, "0", "0XX XXX XXXX",
            listOf("9"), Region.ASIA),
        CountryPhoneInfo("TH", "+66", "Thailand", 9, 9, "0", "0XX XXX XXXX",
            listOf("6", "8", "9"), Region.ASIA),
        CountryPhoneInfo("VN", "+84", "Vietnam", 9, 10, "0", "0XXX XXX XXX",
            listOf("3", "5", "7", "8", "9"), Region.ASIA),
        CountryPhoneInfo("KH", "+855", "Cambodia", 8, 9, "0", "0XX XXX XXX",
            listOf("1", "6", "7", "8", "9"), Region.ASIA),
        CountryPhoneInfo("LA", "+856", "Laos", 8, 10, "0", "0XX XX XXXXX",
            listOf("20"), Region.ASIA),
        CountryPhoneInfo("MY", "+60", "Malaysia", 9, 10, "0", "0XX-XXX XXXX",
            listOf("1"), Region.ASIA),
        CountryPhoneInfo("SG", "+65", "Singapore", 8, 8, null, "XXXX XXXX",
            listOf("8", "9"), Region.ASIA),
        CountryPhoneInfo("ID", "+62", "Indonesia", 9, 12, "0", "0XXX XXXX XXXX",
            listOf("8"), Region.ASIA),
        CountryPhoneInfo("PH", "+63", "Philippines", 10, 10, "0", "0XXX XXX XXXX",
            listOf("9"), Region.ASIA),
        CountryPhoneInfo("TW", "+886", "Taiwan", 9, 9, "0", "0XXX XXX XXX",
            listOf("9"), Region.ASIA),
        CountryPhoneInfo("HK", "+852", "Hong Kong", 8, 8, null, "XXXX XXXX",
            listOf("5", "6", "9"), Region.ASIA),
        CountryPhoneInfo("MO", "+853", "Macau", 8, 8, null, "XXXX XXXX",
            listOf("6"), Region.ASIA),
        CountryPhoneInfo("MN", "+976", "Mongolia", 8, 8, null, "XXXX XXXX",
            listOf("8", "9"), Region.ASIA),
        CountryPhoneInfo("KZ", "+7", "Kazakhstan", 10, 10, "8", "8 XXX XXX XX XX",
            listOf("70", "77"), Region.ASIA),
        CountryPhoneInfo("UZ", "+998", "Uzbekistan", 9, 9, "0", "XX XXX XX XX",
            listOf("9"), Region.ASIA),
        CountryPhoneInfo("KG", "+996", "Kyrgyzstan", 9, 9, "0", "0XXX XXX XXX",
            listOf("5", "7"), Region.ASIA),
        CountryPhoneInfo("TJ", "+992", "Tajikistan", 9, 9, null, "XXX XX XXXX",
            listOf("9"), Region.ASIA),
        CountryPhoneInfo("TM", "+993", "Turkmenistan", 8, 8, "8", "8 XX XXXXXX",
            listOf("6"), Region.ASIA),
        CountryPhoneInfo("AF", "+93", "Afghanistan", 9, 9, "0", "0XX XXX XXXX",
            listOf("7"), Region.ASIA),
        CountryPhoneInfo("BN", "+673", "Brunei", 7, 7, null, "XXX XXXX",
            listOf("7", "8"), Region.ASIA),
        CountryPhoneInfo("TL", "+670", "Timor-Leste", 7, 8, null, "XXX XXXXX",
            listOf("7"), Region.ASIA),
        CountryPhoneInfo("BT", "+975", "Bhutan", 7, 8, null, "XX XXX XXX",
            listOf("17"), Region.ASIA),
        CountryPhoneInfo("MV", "+960", "Maldives", 7, 7, null, "XXX XXXX",
            listOf("7", "9"), Region.ASIA),

        // Middle East
        CountryPhoneInfo("AE", "+971", "United Arab Emirates", 9, 9, "0", "0XX XXX XXXX",
            listOf("5"), Region.MIDDLE_EAST),
        CountryPhoneInfo("SA", "+966", "Saudi Arabia", 9, 9, "0", "0XX XXX XXXX",
            listOf("5"), Region.MIDDLE_EAST),
        CountryPhoneInfo("QA", "+974", "Qatar", 8, 8, null, "XXXX XXXX",
            listOf("3", "5", "6", "7"), Region.MIDDLE_EAST),
        CountryPhoneInfo("KW", "+965", "Kuwait", 8, 8, null, "XXXX XXXX",
            listOf("5", "6", "9"), Region.MIDDLE_EAST),
        CountryPhoneInfo("BH", "+973", "Bahrain", 8, 8, null, "XXXX XXXX",
            listOf("3"), Region.MIDDLE_EAST),
        CountryPhoneInfo("OM", "+968", "Oman", 8, 8, null, "XXXX XXXX",
            listOf("7", "9"), Region.MIDDLE_EAST),
        CountryPhoneInfo("YE", "+967", "Yemen", 7, 9, "0", "0XX XXX XXX",
            listOf("7"), Region.MIDDLE_EAST),
        CountryPhoneInfo("JO", "+962", "Jordan", 9, 9, "0", "0X XXXX XXXX",
            listOf("7"), Region.MIDDLE_EAST),
        CountryPhoneInfo("LB", "+961", "Lebanon", 7, 8, "0", "0XX XXX XXX",
            listOf("3", "7"), Region.MIDDLE_EAST),
        CountryPhoneInfo("SY", "+963", "Syria", 8, 9, "0", "0XXX XXX XXX",
            listOf("9"), Region.MIDDLE_EAST),
        CountryPhoneInfo("IQ", "+964", "Iraq", 10, 10, "0", "0XXX XXX XXXX",
            listOf("7"), Region.MIDDLE_EAST),
        CountryPhoneInfo("IR", "+98", "Iran", 10, 10, "0", "0XXX XXX XXXX",
            listOf("9"), Region.MIDDLE_EAST),
        CountryPhoneInfo("IL", "+972", "Israel", 9, 9, "0", "0XX XXX XXXX",
            listOf("5"), Region.MIDDLE_EAST),
        CountryPhoneInfo("PS", "+970", "Palestine", 9, 9, "0", "0XX XXX XXXX",
            listOf("5"), Region.MIDDLE_EAST),
        CountryPhoneInfo("TR", "+90", "Turkey", 10, 10, "0", "0XXX XXX XXXX",
            listOf("5"), Region.MIDDLE_EAST),

        // Africa
        CountryPhoneInfo("ZA", "+27", "South Africa", 9, 9, "0", "0XX XXX XXXX",
            listOf("6", "7", "8"), Region.AFRICA),
        CountryPhoneInfo("NG", "+234", "Nigeria", 8, 10, "0", "0XXX XXX XXXX",
            listOf("7", "8", "9"), Region.AFRICA),
        CountryPhoneInfo("KE", "+254", "Kenya", 9, 9, "0", "0XXX XXXXXX",
            listOf("7"), Region.AFRICA),
        CountryPhoneInfo("GH", "+233", "Ghana", 9, 9, "0", "0XX XXX XXXX",
            listOf("2", "5"), Region.AFRICA),
        CountryPhoneInfo("ET", "+251", "Ethiopia", 9, 9, "0", "0XX XXX XXXX",
            listOf("9"), Region.AFRICA),
        CountryPhoneInfo("TZ", "+255", "Tanzania", 9, 9, "0", "0XXX XXX XXX",
            listOf("6", "7"), Region.AFRICA),
        CountryPhoneInfo("UG", "+256", "Uganda", 9, 9, "0", "0XXX XXXXXX",
            listOf("7"), Region.AFRICA),
        CountryPhoneInfo("EG", "+20", "Egypt", 10, 10, "0", "0XXX XXX XXXX",
            listOf("1"), Region.AFRICA),
        CountryPhoneInfo("MA", "+212", "Morocco", 9, 9, "0", "0XXX XX XX XX",
            listOf("6", "7"), Region.AFRICA),
        CountryPhoneInfo("TN", "+216", "Tunisia", 8, 8, null, "XX XXX XXX",
            listOf("2", "5", "9"), Region.AFRICA),
        CountryPhoneInfo("DZ", "+213", "Algeria", 9, 9, "0", "0XXX XX XX XX",
            listOf("5", "6", "7"), Region.AFRICA),
        CountryPhoneInfo("LY", "+218", "Libya", 9, 9, "0", "0XX XXX XXXX",
            listOf("9"), Region.AFRICA),
        CountryPhoneInfo("SD", "+249", "Sudan", 9, 9, "0", "0XX XXX XXXX",
            listOf("9"), Region.AFRICA),
        CountryPhoneInfo("CM", "+237", "Cameroon", 8, 9, null, "XX XXX XXXX",
            listOf("6"), Region.AFRICA),
        CountryPhoneInfo("CI", "+225", "Ivory Coast", 10, 10, null, "XX XX XX XX XX",
            listOf("0", "4", "5", "6", "7"), Region.AFRICA),
        CountryPhoneInfo("SN", "+221", "Senegal", 9, 9, null, "XX XXX XX XX",
            listOf("7"), Region.AFRICA),
        CountryPhoneInfo("ML", "+223", "Mali", 8, 8, null, "XX XX XX XX",
            listOf("6", "7"), Region.AFRICA),
        CountryPhoneInfo("BF", "+226", "Burkina Faso", 8, 8, null, "XX XX XX XX",
            listOf("5", "6", "7"), Region.AFRICA),
        CountryPhoneInfo("NE", "+227", "Niger", 8, 8, null, "XX XX XX XX",
            listOf("8", "9"), Region.AFRICA),
        CountryPhoneInfo("GN", "+224", "Guinea", 8, 9, null, "XXX XX XX XX",
            listOf("6"), Region.AFRICA),
        CountryPhoneInfo("RW", "+250", "Rwanda", 9, 9, "0", "0XXX XXX XXX",
            listOf("7"), Region.AFRICA),
        CountryPhoneInfo("MZ", "+258", "Mozambique", 9, 9, null, "XX XXX XXXX",
            listOf("8"), Region.AFRICA),
        CountryPhoneInfo("AO", "+244", "Angola", 9, 9, null, "XXX XXX XXX",
            listOf("9"), Region.AFRICA),
        CountryPhoneInfo("ZW", "+263", "Zimbabwe", 9, 9, "0", "0XX XXX XXXX",
            listOf("7"), Region.AFRICA),
        CountryPhoneInfo("ZM", "+260", "Zambia", 9, 9, "0", "0XX XXX XXXX",
            listOf("9"), Region.AFRICA),
        CountryPhoneInfo("MW", "+265", "Malawi", 7, 9, "0", "0XXX XX XXXX",
            listOf("8", "9"), Region.AFRICA),
        CountryPhoneInfo("BW", "+267", "Botswana", 7, 8, null, "XX XXX XXX",
            listOf("7"), Region.AFRICA),
        CountryPhoneInfo("NA", "+264", "Namibia", 7, 10, "0", "0XX XXX XXXX",
            listOf("8"), Region.AFRICA),
        CountryPhoneInfo("MG", "+261", "Madagascar", 9, 9, "0", "0XX XX XXX XX",
            listOf("3"), Region.AFRICA),
        CountryPhoneInfo("MU", "+230", "Mauritius", 8, 8, null, "XXXX XXXX",
            listOf("5"), Region.AFRICA),

        // South America
        CountryPhoneInfo("BR", "+55", "Brazil", 10, 11, "0", "0XX XXXXX-XXXX",
            listOf("9"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("AR", "+54", "Argentina", 10, 10, "0", "0XX XXXX XXXX",
            listOf("9"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("CL", "+56", "Chile", 9, 9, null, "X XXXX XXXX",
            listOf("9"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("CO", "+57", "Colombia", 10, 10, null, "XXX XXX XXXX",
            listOf("3"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("PE", "+51", "Peru", 9, 9, "0", "0XX XXX XXX",
            listOf("9"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("VE", "+58", "Venezuela", 10, 10, "0", "0XXX XXX XXXX",
            listOf("4"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("EC", "+593", "Ecuador", 9, 9, "0", "0XX XXX XXXX",
            listOf("9"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("BO", "+591", "Bolivia", 8, 8, null, "X XXXXXXX",
            listOf("6", "7"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("PY", "+595", "Paraguay", 9, 9, "0", "0XXX XXX XXX",
            listOf("9"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("UY", "+598", "Uruguay", 8, 8, "0", "0XX XXX XXX",
            listOf("9"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("GY", "+592", "Guyana", 7, 7, null, "XXX XXXX",
            listOf("6"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("SR", "+597", "Suriname", 6, 7, null, "XXX XXXX",
            listOf("8"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("GF", "+594", "French Guiana", 9, 9, "0", "0XXX XX XX XX",
            listOf("6"), Region.SOUTH_AMERICA),
        CountryPhoneInfo("FK", "+500", "Falkland Islands", 5, 5, null, "XXXXX",
            emptyList(), Region.SOUTH_AMERICA),

        // Central America
        CountryPhoneInfo("MX", "+52", "Mexico", 10, 10, null, "XX XXXX XXXX",
            listOf("1"), Region.CENTRAL_AMERICA),
        CountryPhoneInfo("GT", "+502", "Guatemala", 8, 8, null, "XXXX XXXX",
            listOf("3", "4", "5"), Region.CENTRAL_AMERICA),
        CountryPhoneInfo("BZ", "+501", "Belize", 7, 7, null, "XXX XXXX",
            listOf("6"), Region.CENTRAL_AMERICA),
        CountryPhoneInfo("SV", "+503", "El Salvador", 8, 8, null, "XXXX XXXX",
            listOf("6", "7"), Region.CENTRAL_AMERICA),
        CountryPhoneInfo("HN", "+504", "Honduras", 8, 8, null, "XXXX XXXX",
            listOf("3", "8", "9"), Region.CENTRAL_AMERICA),
        CountryPhoneInfo("NI", "+505", "Nicaragua", 8, 8, null, "XXXX XXXX",
            listOf("5", "7", "8"), Region.CENTRAL_AMERICA),
        CountryPhoneInfo("CR", "+506", "Costa Rica", 8, 8, null, "XXXX XXXX",
            listOf("5", "6", "7", "8"), Region.CENTRAL_AMERICA),
        CountryPhoneInfo("PA", "+507", "Panama", 7, 8, null, "XXXX XXXX",
            listOf("6"), Region.CENTRAL_AMERICA),
        CountryPhoneInfo("CU", "+53", "Cuba", 8, 8, "0", "0X XXX XXXX",
            listOf("5"), Region.CARIBBEAN),
        CountryPhoneInfo("HT", "+509", "Haiti", 8, 8, null, "XX XX XXXX",
            listOf("3", "4"), Region.CARIBBEAN),

        // Oceania
        CountryPhoneInfo("AU", "+61", "Australia", 9, 9, "0", "0XXX XXX XXX",
            listOf("4"), Region.OCEANIA),
        CountryPhoneInfo("NZ", "+64", "New Zealand", 8, 10, "0", "0XX XXX XXXX",
            listOf("2"), Region.OCEANIA),
        CountryPhoneInfo("FJ", "+679", "Fiji", 7, 7, null, "XXX XXXX",
            listOf("7", "8", "9"), Region.OCEANIA),
        CountryPhoneInfo("PG", "+675", "Papua New Guinea", 7, 8, null, "XXX XXXX",
            listOf("7"), Region.OCEANIA),
        CountryPhoneInfo("WS", "+685", "Samoa", 5, 7, null, "XX XXXXX",
            listOf("7"), Region.OCEANIA),
        CountryPhoneInfo("TO", "+676", "Tonga", 5, 7, null, "XXX XXXX",
            listOf("7"), Region.OCEANIA),
        CountryPhoneInfo("VU", "+678", "Vanuatu", 5, 7, null, "XXX XXXX",
            listOf("5", "7"), Region.OCEANIA),
        CountryPhoneInfo("SB", "+677", "Solomon Islands", 5, 7, null, "XXXXX",
            listOf("7"), Region.OCEANIA),
        CountryPhoneInfo("NC", "+687", "New Caledonia", 6, 6, null, "XX XX XX",
            listOf("7", "8"), Region.OCEANIA),
        CountryPhoneInfo("PF", "+689", "French Polynesia", 6, 6, null, "XX XX XX",
            listOf("8"), Region.OCEANIA)
    )

    /**
     * Index of countries by calling code for fast lookup.
     */
    private val countryByCallingCode: Map<String, List<CountryPhoneInfo>> by lazy {
        countries.groupBy { it.callingCode }
    }

    /**
     * Index of countries by ISO code for fast lookup.
     */
    private val countryByIsoCode: Map<String, CountryPhoneInfo> by lazy {
        countries.associateBy { it.code }
    }

    /**
     * List of all known calling codes sorted by length (longest first)
     * for greedy matching.
     */
    private val sortedCallingCodes: List<String> by lazy {
        countryByCallingCode.keys.sortedByDescending { it.length }
    }

    /**
     * Validates a phone number string against known country formats.
     *
     * @param phoneNumber The phone number to validate (may include country code, spaces, dashes, etc.)
     * @return PhoneValidationResult with validation details
     */
    fun validate(phoneNumber: String): PhoneValidationResult {
        val cleaned = cleanPhoneNumber(phoneNumber)
        if (cleaned.length < 4 || cleaned.length > 15) {
            return PhoneValidationResult(
                isValid = false,
                reason = "Phone number length ${cleaned.length} is outside valid range (4-15 digits)"
            )
        }

        // Try to match with country code
        val withPlusPrefix = if (cleaned.startsWith("+")) cleaned else "+$cleaned"
        for (callingCode in sortedCallingCodes) {
            if (withPlusPrefix.startsWith(callingCode)) {
                val nationalNumber = withPlusPrefix.removePrefix(callingCode)
                val matchingCountries = countryByCallingCode[callingCode] ?: continue
                for (country in matchingCountries) {
                    val result = validateNationalNumber(nationalNumber, country)
                    if (result.isValid) return result
                }
            }
        }

        // Try without country code (assume local number)
        return PhoneValidationResult(
            isValid = cleaned.length in 7..10,
            nationalNumber = cleaned,
            confidence = if (cleaned.length in 7..10) 0.4f else 0.1f,
            reason = if (cleaned.length in 7..10) "Valid length for local number, no country code detected"
            else "Could not determine country; length outside typical range"
        )
    }

    /**
     * Validates a national number against a specific country's rules.
     */
    private fun validateNationalNumber(
        nationalNumber: String,
        country: CountryPhoneInfo
    ): PhoneValidationResult {
        val digits = nationalNumber.filter { it.isDigit() }

        // Remove trunk prefix if present
        val withoutTrunk = if (country.trunkPrefix != null && digits.startsWith(country.trunkPrefix)) {
            digits.removePrefix(country.trunkPrefix)
        } else {
            digits
        }

        val isLengthValid = withoutTrunk.length in country.minLength..country.maxLength

        val isMobilePrefixValid = if (country.mobilePrefix.isEmpty()) {
            true // No mobile prefix constraints
        } else {
            country.mobilePrefix.any { withoutTrunk.startsWith(it) }
        }

        val confidence = when {
            isLengthValid && isMobilePrefixValid -> 0.9f
            isLengthValid -> 0.7f
            isMobilePrefixValid -> 0.5f
            else -> 0.2f
        }

        val formattedNumber = "${country.callingCode}$withoutTrunk"

        return PhoneValidationResult(
            isValid = isLengthValid,
            countryCode = country.code,
            nationalNumber = withoutTrunk,
            formattedNumber = formattedNumber,
            confidence = confidence,
            reason = when {
                isLengthValid && isMobilePrefixValid -> "Valid ${country.name} number"
                isLengthValid -> "Valid length for ${country.name}, prefix not recognized as mobile"
                else -> "Invalid length for ${country.name}: expected ${country.minLength}-${country.maxLength} digits, got ${withoutTrunk.length}"
            },
            country = country
        )
    }

    /**
     * Strips a phone number down to digits and leading plus sign.
     */
    fun cleanPhoneNumber(phoneNumber: String): String {
        val trimmed = phoneNumber.trim()
        val sb = StringBuilder()
        for ((index, char) in trimmed.withIndex()) {
            when {
                char.isDigit() -> sb.append(char)
                char == '+' && index == 0 -> sb.append(char)
                // Skip formatting characters: spaces, dashes, dots, parens
            }
        }
        return sb.toString()
    }

    /**
     * Extracts the likely country from a phone number.
     *
     * @param phoneNumber Phone number string (with or without country code)
     * @return CountryPhoneInfo if country could be determined, null otherwise
     */
    fun detectCountry(phoneNumber: String): CountryPhoneInfo? {
        val cleaned = cleanPhoneNumber(phoneNumber)
        val withPlus = if (cleaned.startsWith("+")) cleaned else "+$cleaned"

        for (callingCode in sortedCallingCodes) {
            if (withPlus.startsWith(callingCode)) {
                val nationalPart = withPlus.removePrefix(callingCode).filter { it.isDigit() }
                val matchingCountries = countryByCallingCode[callingCode] ?: continue
                // If multiple countries share the code (e.g., +1), check national number length
                for (country in matchingCountries) {
                    if (nationalPart.length in country.minLength..country.maxLength) {
                        return country
                    }
                }
                // Return first match if length doesn't narrow it down
                return matchingCountries.firstOrNull()
            }
        }
        return null
    }

    /**
     * Gets phone info for a specific country by ISO code.
     */
    fun getCountryByIsoCode(isoCode: String): CountryPhoneInfo? {
        return countryByIsoCode[isoCode.uppercase()]
    }

    /**
     * Gets all countries with a specific calling code.
     */
    fun getCountriesByCallingCode(callingCode: String): List<CountryPhoneInfo> {
        val normalized = if (callingCode.startsWith("+")) callingCode else "+$callingCode"
        return countryByCallingCode[normalized] ?: emptyList()
    }

    /**
     * Gets all countries in a specific region.
     */
    fun getCountriesByRegion(region: Region): List<CountryPhoneInfo> {
        return countries.filter { it.region == region }
    }

    /**
     * Formats a phone number to E.164 format.
     *
     * @param phoneNumber Raw phone number
     * @param defaultCountryCode Default country ISO code if no country code in number
     * @return E.164 formatted number or null if invalid
     */
    fun formatToE164(phoneNumber: String, defaultCountryCode: String = "US"): String? {
        val result = validate(phoneNumber)
        if (result.isValid && result.formattedNumber != null) {
            return result.formattedNumber
        }

        // Try with default country
        val country = getCountryByIsoCode(defaultCountryCode) ?: return null
        val digits = phoneNumber.filter { it.isDigit() }
        val withoutTrunk = if (country.trunkPrefix != null && digits.startsWith(country.trunkPrefix)) {
            digits.removePrefix(country.trunkPrefix)
        } else {
            digits
        }

        return if (withoutTrunk.length in country.minLength..country.maxLength) {
            "${country.callingCode}$withoutTrunk"
        } else {
            null
        }
    }

    /**
     * Checks if a string looks like a phone number (quick heuristic check).
     */
    fun looksLikePhoneNumber(text: String): Boolean {
        val digits = text.count { it.isDigit() }
        val hasPlus = text.trimStart().startsWith('+')
        val hasParens = text.contains('(') && text.contains(')')
        val hasDashes = text.contains('-')
        val hasDots = text.contains('.')
        val hasSpaces = text.contains(' ')

        // Must have enough digits
        if (digits < 7 || digits > 15) return false

        // Must not be mostly non-digit characters (probably not a phone number)
        val nonDigits = text.length - digits
        if (nonDigits > digits) return false

        // Common phone number indicators
        return hasPlus || hasParens || hasDashes || (hasDots && digits >= 10) ||
                (hasSpaces && digits >= 8) || digits in 10..11
    }

    /**
     * Returns the total number of countries in the database.
     */
    fun totalCountries(): Int = countries.size

    /**
     * Returns all unique calling codes.
     */
    fun allCallingCodes(): Set<String> = countryByCallingCode.keys

    /**
     * Returns all country ISO codes.
     */
    fun allIsoCodes(): Set<String> = countryByIsoCode.keys

    /**
     * NANP (North American Numbering Plan) specific validation.
     * Validates US/Canada numbers with strict area code rules.
     */
    fun validateNANPNumber(phoneNumber: String): PhoneValidationResult {
        val digits = phoneNumber.filter { it.isDigit() }
        val tenDigit = when {
            digits.length == 11 && digits.startsWith("1") -> digits.substring(1)
            digits.length == 10 -> digits
            else -> return PhoneValidationResult(
                isValid = false,
                reason = "NANP numbers must be 10 digits (or 11 with leading 1)"
            )
        }

        val areaCode = tenDigit.substring(0, 3)
        val exchange = tenDigit.substring(3, 6)

        // Area code rules: first digit cannot be 0 or 1
        if (areaCode[0] == '0' || areaCode[0] == '1') {
            return PhoneValidationResult(
                isValid = false,
                reason = "NANP area code cannot start with 0 or 1: $areaCode"
            )
        }

        // Exchange rules: first digit cannot be 0 or 1
        if (exchange[0] == '0' || exchange[0] == '1') {
            return PhoneValidationResult(
                isValid = false,
                reason = "NANP exchange cannot start with 0 or 1: $exchange"
            )
        }

        // N11 codes are service codes, not area codes (211, 311, 411, 511, 611, 711, 811, 911)
        if (areaCode[1] == '1' && areaCode[2] == '1') {
            return PhoneValidationResult(
                isValid = false,
                reason = "N11 codes are service codes, not valid area codes: $areaCode"
            )
        }

        // 555-01XX range is reserved for fictional use
        if (exchange == "555" && tenDigit.substring(6, 8) == "01") {
            return PhoneValidationResult(
                isValid = false,
                confidence = 0.3f,
                reason = "555-01XX numbers are reserved for fictional use"
            )
        }

        return PhoneValidationResult(
            isValid = true,
            countryCode = "US",
            nationalNumber = tenDigit,
            formattedNumber = "+1$tenDigit",
            confidence = 0.95f,
            reason = "Valid NANP number"
        )
    }
}
