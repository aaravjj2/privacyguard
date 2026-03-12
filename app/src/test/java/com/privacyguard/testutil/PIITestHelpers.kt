package com.privacyguard.testutil

import java.util.regex.Pattern

/**
 * Utility helpers for PII detection tests.
 * Provides pre-built data collections and helper functions.
 */
object PIITestHelpers {

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 1: Valid SSN data lists
    // ═══════════════════════════════════════════════════════════════════════════

    fun allValidSSNs(): List<String> = listOf(
        "001-01-0001", "001-01-0002", "001-01-0003", "001-01-0100", "001-01-9999",
        "001-99-0001", "001-99-9999", "010-10-1010", "020-20-2020", "030-30-3030",
        "040-40-4040", "050-50-5050", "060-60-6060", "070-70-7070", "080-80-8080",
        "090-90-9090", "100-10-1001", "110-20-2002", "120-30-3003", "130-40-4004",
        "140-50-5005", "150-60-6006", "160-70-7007", "170-80-8008", "180-90-9009",
        "190-10-0010", "200-20-0020", "210-30-0030", "220-40-0040", "230-50-0050",
        "240-60-0060", "250-70-0070", "260-80-0080", "270-90-0090", "280-01-0001",
        "290-02-0002", "300-03-0003", "310-04-0004", "320-05-0005", "330-06-0006",
        "340-07-0007", "350-08-0008", "360-09-0009", "370-11-1111", "380-12-2222",
        "390-13-3333", "400-14-4444", "410-15-5555", "420-16-6666", "430-17-7777",
        "440-18-8888", "450-19-9999", "460-21-1234", "470-22-2345", "480-23-3456",
        "490-24-4567", "500-25-5678", "510-26-6789", "520-27-7890", "530-28-8901",
        "540-29-9012", "550-31-0123", "560-32-1234", "570-33-2345", "580-34-3456",
        "590-35-4567", "600-36-5678", "002-02-0002", "003-03-0003", "004-04-0004",
        "005-05-0005", "006-06-0006", "007-07-0007", "008-08-0008", "009-09-0009",
        "011-11-1111", "022-22-2222", "033-33-3333", "044-44-4444", "055-55-5555"
    )

    fun allInvalidSSNs(): List<String> = listOf(
        "000-01-0001",  // area 000 forbidden
        "666-01-0001",  // area 666 forbidden
        "900-01-0001",  // area 900+ forbidden
        "999-01-0001",  // area 999 forbidden
        "123-00-4567",  // group 00 forbidden
        "123-45-0000",  // serial 0000 forbidden
        "12345678",     // wrong format
        "123456789",    // no dashes
        "123-456-789",  // wrong grouping
        "1234-5-6789",  // wrong grouping
        "abc-de-fghi",  // letters
        "12-345-6789",  // wrong grouping
        "",             // empty
        "   ",          // whitespace
        "078-05-1120",  // Woolworth SSN
        "219-09-9999",  // test SSN from ads
        "987-65-4320",  // used in ads
        "700-00-0001",  // area 700+ unassigned
        "800-00-0001",  // area 800+
        "1234-56-789",  // too many in area
        "12-345678",    // malformed
        "123-4-56789",  // malformed
        "000-00-0000",  // everything zero
        "999-99-9999",  // everything nine
        "---"           // only dashes
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 2: Credit Card data lists
    // ═══════════════════════════════════════════════════════════════════════════

    fun allValidCards(): List<String> = listOf(
        // Visa
        "4111111111111111", "4012888888881881", "4222222222222",
        "4539578763621486", "4916338506082832", "4532015112830366",
        "4929490369015736", "4716751691420004", "4485904394748950",
        "4556737586899855", "4024007103939509", "4026278463897620",
        "4508751079308350", "4844532262435032", "4913614280213862",
        // Mastercard
        "5105105105105100", "5555555555554444", "5500005555555559",
        "5425233430109903", "5200828282828210", "2221000000000009",
        "2720999999999996", "2500000000000001", "5301250070000191",
        "5100290029002909", "5211476012005955", "5311561559145891",
        "5411111111111115", "5511111111111116",
        // Amex
        "378282246310005", "371449635398431", "378734493671000",
        "340000000000009", "370000000000002",
        // Discover
        "6011111111111117", "6011000990139424", "6500000000000002",
        "6444444444444444", "6221260000000000",
        // JCB
        "3528000000000007", "3589000000000006", "3530000000000005",
        "3566000020000410",
        // Diners
        "30569309025904", "36148900647913", "38000000000006",
        "30000000000004", "30500000000003",
        // Additional Visa
        "4444444444444448", "4000000000000002", "4000000000000010",
        "4000000000000028", "4000000000000036", "4000000000000044",
        "4000000000000051", "4000000000000069", "4000000000000077",
        "4000000000000085", "4000000000000093", "4000000000000101",
        "4000000000000119", "4000000000000127", "4000000000000135"
    )

    fun allInvalidCards(): List<String> = listOf(
        "0000000000000000",   // all zeros
        "1111111111111111",   // luhn fail
        "4111111111111112",   // luhn fail
        "5105105105105101",   // luhn fail
        "411111111111",       // too short
        "41111111111111111",  // too long
        "4111111111111a11",   // letters
        "",                   // empty
        "1234567890123456",   // unknown prefix
        "9876543210987654",   // unknown prefix
        "6011000000000",      // too short for discover
        "37828224631000",     // too short for amex
        "305693090259",       // too short for diners
        "35280000000000",     // too short for jcb
        "2221000000000010",   // luhn fail
        "4000",               // way too short
        "123456789012345",    // no valid prefix
        "   ",                // whitespace
        "-",                  // dash
        "0"                   // single digit
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 3: Email data lists
    // ═══════════════════════════════════════════════════════════════════════════

    fun allValidEmails(): List<String> = listOf(
        "user@example.com", "user.name@example.com", "user+tag@example.com",
        "user-name@example.com", "user_name@example.com", "user123@example.com",
        "123user@example.com", "USER@EXAMPLE.COM", "User.Name@Example.Com",
        "user@subdomain.example.com", "user@sub.sub.example.com",
        "user@example.co.uk", "user@example.com.au", "user@example.io",
        "user@example.ai", "user@example.app", "user@example.dev",
        "user@example.gov", "user@example.edu", "user@example.org",
        "user@example.net", "user@example.mil", "first.last@company.com",
        "john.doe@gmail.com", "jane.smith@yahoo.com", "bob.jones@outlook.com",
        "alice.wonder@hotmail.com", "charlie.brown@icloud.com",
        "david.miller@protonmail.com", "test+filter@gmail.com",
        "user.name+tag+sorting@example.com", "x@example.com",
        "example-indeed@strange-example.com", "example@s.example",
        "1234567890@example.com", "email@example-one.com",
        "_______@example.com", "email@example.name", "email@example.museum",
        "user@gmail.com", "user@yahoo.com", "user@hotmail.com",
        "user@outlook.com", "user@icloud.com", "user@mail.com",
        "user@protonmail.com", "user@fastmail.com", "user@me.com",
        "user@live.com", "alex.johnson@company.co.uk",
        "test.email.with+symbol@example.com", "id-with-dash@example.com",
        "a@b.c", "test123@test123.test123", "admin+filter@my-domain.org",
        "sales.team@global-corp.net", "hr_department@bigcorp.co",
        "dev.team+releases@company.tech", "newsletter@weekly.updates.com",
        "auto-reply@mailer.domain.net", "user@example.travel",
        "user@example.photography", "user@example.technology",
        "user@example.engineering", "user@example.consulting",
        "user@xn--nxasmq6b.com", "team@very-long-company-name-here.com",
        "shortened@co.uk", "user@example.info", "user@example.us",
        "user@example.ca", "user@example.au", "user@example.nz",
        "user@example.sg", "user@example.in", "user@example.jp",
        "user@example.kr", "user@example.cn", "user@example.hk",
        "user@example.tw", "user@example.br", "user@example.mx"
    )

    fun allInvalidEmails(): List<String> = listOf(
        "plainaddress", "@missinglocal.com", "missingdomain@",
        "missingat.com", "two@@example.com", ".user@example.com",
        "user.@example.com", "user..name@example.com", "user@.com",
        "user@exam ple.com", "", "user@", "@", "user@[256.256.256.256]",
        "user@example..com", "missing-dot@com", "user@-domain.com",
        "user@domain-.com", "user@.domain.com", "user@domain.c",
        "toolong" + "a".repeat(250) + "@example.com",
        "user@domain", "user@domain.", "@domain.com", "user name@domain.com",
        "user\t@domain.com", "user\n@domain.com", "user\r@domain.com",
        "user\u0000@domain.com", "user<tag>@domain.com"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 4: Phone number data lists
    // ═══════════════════════════════════════════════════════════════════════════

    fun allValidPhones(): List<String> = listOf(
        "(212) 555-1234", "(213) 555-5678", "(312) 555-9012",
        "(415) 555-3456", "(617) 555-7890", "(713) 555-1234",
        "(305) 555-5678", "(404) 555-9012", "(206) 555-3456",
        "(702) 555-7890", "212-555-1234", "213.555.5678",
        "3125559012", "+1-415-555-3456", "+1 617 555 7890",
        "+12125551234", "1-213-555-5678", "(800) 555-1212",
        "(888) 555-0100", "(877) 555-0199",
        "+44 20 7946 0958", "+44 161 999 8888", "+44 7700 900001",
        "020 7946 0958", "07700 900001",
        "+49 030 12345678", "+49 089 98765432", "+49 0211 1234567",
        "+33 1 23 45 67 89", "+33 6 12 34 56 78",
        "+61 2 9876 5432", "+61 3 9876 5432", "+61 412 345 678",
        "+81 3-1234-5678", "+81 80-1234-5678",
        "+91 98765 43210", "+91 11 2345 6789",
        "+86 138 0013 8000", "+86 10 1234 5678",
        "+55 11 98765-4321", "+55 21 3456-7890",
        "+34 91 234 56 78", "+34 612 345 678",
        "+52 55 1234 5678", "+1 604 555 1234",
        "+12125551234", "+447700900001", "+4930123456",
        "+33123456789", "+61298765432",
        "+7 495 123-45-67", "+39 02 1234 5678", "+31 20 123 4567",
        "+41 44 123 45 67", "+46 8 123 456 78", "+47 22 12 34 56",
        "+45 32 12 34 56", "+48 22 123 45 67", "+32 2 123 45 67",
        "+43 1 123 4567", "+351 21 123 4567", "+30 21 0123 4567"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 5: IP address data lists
    // ═══════════════════════════════════════════════════════════════════════════

    fun allPublicIPs(): List<String> = listOf(
        "8.8.8.8", "8.8.4.4", "1.1.1.1", "1.0.0.1",
        "208.67.222.222", "208.67.220.220", "9.9.9.9",
        "149.112.112.112", "64.6.64.6", "64.6.65.6",
        "216.58.192.14", "172.217.0.0", "13.32.0.0",
        "52.0.0.0", "104.16.0.0", "31.13.92.36",
        "157.240.0.1", "199.59.148.1", "205.251.242.0",
        "198.41.128.4", "198.51.100.1", "203.0.113.1",
        "130.211.0.0", "107.21.0.0", "54.84.0.0",
        "185.220.100.1", "91.108.4.1", "149.154.160.1",
        "184.168.131.241", "166.62.28.1"
    )

    fun allPrivateIPs(): List<String> = listOf(
        "10.0.0.1", "10.0.0.254", "10.1.2.3", "10.255.255.254",
        "172.16.0.1", "172.31.255.254", "192.168.0.1",
        "192.168.1.1", "192.168.255.254", "192.168.100.100",
        "127.0.0.1", "127.0.0.2", "127.255.255.254",
        "169.254.0.1", "169.254.169.254", "100.64.0.1",
        "100.127.255.254", "198.18.0.1", "198.19.255.254",
        "192.0.2.1"  // TEST-NET
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 6: Wrapping helpers for natural text
    // ═══════════════════════════════════════════════════════════════════════════

    fun textContainingSSN(ssn: String): String =
        "The applicant's Social Security Number is $ssn as per the official records."

    fun textContainingSsn2(ssn: String): String =
        "For verification, please provide your SSN: $ssn when calling customer service."

    fun textContainingSsn3(ssn: String): String =
        "Employee SSN $ssn has been updated in the HR system effective immediately."

    fun textContainingCard(card: String): String =
        "Please confirm the payment by charging credit card number $card for this order."

    fun textContainingCard2(card: String): String =
        "The transaction was declined for card $card. Please contact your bank."

    fun textContainingCard3(card: String): String =
        "Authorize card number $card expiring 12/25 for the total amount of $149.99."

    fun textContainingEmail(email: String): String =
        "The confirmation email was sent to $email. Please check your inbox."

    fun textContainingEmail2(email: String): String =
        "User registered with the email address $email on this platform."

    fun textContainingEmail3(email: String): String =
        "Please update your contact details. Your current email $email is on file."

    fun textContainingPhone(phone: String): String =
        "You can reach the customer at $phone between 9 AM and 5 PM Monday to Friday."

    fun textContainingPhone2(phone: String): String =
        "The callback number provided was $phone for the open support ticket."

    fun textContainingPhone3(phone: String): String =
        "Two-factor authentication code will be sent to $phone via SMS."

    fun textContainingIP(ip: String): String =
        "Suspicious login detected from IP address $ip at 03:17 AM UTC."

    fun textContainingIP2(ip: String): String =
        "The server at $ip is not responding to health checks."

    fun textContainingIBAN(iban: String): String =
        "Please wire the payment to the following IBAN: $iban by end of business day."

    fun multiPIIText(ssn: String, card: String, email: String): String =
        "Application form - SSN: $ssn, Credit Card: $card, Contact: $email"

    fun multiPIIText2(ssn: String, phone: String, ip: String): String =
        "Audit log entry: User SSN=$ssn, Phone=$phone, AccessIP=$ip"

    fun multiPIIText3(card: String, email: String, phone: String): String =
        "Customer profile: Card=$card, Email=$email, Mobile=$phone"

    fun highDensityPIIText(ssn: String, card: String, email: String, phone: String): String = """
        Customer Application Reference: #REF2024001

        Personal Information:
        - Social Security Number: $ssn
        - Date of Birth: June 15, 1990
        - Email Address: $email
        - Phone Number: $phone

        Payment Details:
        - Card Number: $card
        - Expiration: 12/2025
        - CVV: 123

        Please keep this information confidential.
    """.trimIndent()

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 7: Algorithmic helpers
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Implements the Luhn algorithm for credit card validation.
     */
    fun luhnCheck(number: String): Boolean {
        val digits = number.filter { it.isDigit() }
        if (digits.isEmpty()) return false
        var sum = 0
        var alternate = false
        for (i in digits.length - 1 downTo 0) {
            var n = digits[i] - '0'
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
     * Validates SSN format: NNN-NN-NNNN where:
     * - Area (NNN): 001-899, excluding 666
     * - Group (NN): 01-99
     * - Serial (NNNN): 0001-9999
     */
    fun isValidSSNFormat(s: String): Boolean {
        val ssnPattern = Pattern.compile("""^(?!000|666|9\d{2})\d{3}-(?!00)\d{2}-(?!0000)\d{4}$""")
        return ssnPattern.matcher(s).matches()
    }

    /**
     * Validates basic email format using regex.
     */
    fun isValidEmailFormat(s: String): Boolean {
        val emailPattern = Pattern.compile(
            """^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$"""
        )
        return emailPattern.matcher(s).matches()
    }

    /**
     * Validates IPv4 format: four octets 0-255 separated by dots.
     */
    fun isValidIPv4(s: String): Boolean {
        val ipPattern = Pattern.compile(
            """^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"""
        )
        return ipPattern.matcher(s).matches()
    }

    /**
     * Validates ABA routing number using checksum.
     * Checksum: 3*(d1+d4+d7) + 7*(d2+d5+d8) + (d3+d6+d9) must be divisible by 10.
     */
    fun isValidABA(routing: String): Boolean {
        if (routing.length != 9) return false
        val d = routing.map { it - '0' }
        if (d.any { it < 0 }) return false
        val checksum = 3 * (d[0] + d[3] + d[6]) +
                       7 * (d[1] + d[4] + d[7]) +
                       (d[2] + d[5] + d[8])
        return checksum != 0 && checksum % 10 == 0
    }

    /**
     * Validates IBAN MOD-97-10 checksum.
     * Returns true if the IBAN passes the modulo 97 check.
     */
    fun isValidIBANChecksum(iban: String): Boolean {
        val cleaned = iban.replace("\\s".toRegex(), "").uppercase()
        if (cleaned.length < 5) return false
        val rearranged = cleaned.substring(4) + cleaned.substring(0, 4)
        val numericString = rearranged.map { c ->
            if (c.isLetter()) (c - 'A' + 10).toString() else c.toString()
        }.joinToString("")
        var remainder = 0L
        for (char in numericString) {
            remainder = (remainder * 10 + (char - '0')) % 97
        }
        return remainder == 1L
    }

    /**
     * Returns a deterministic string of given length (not truly random, for testing).
     */
    fun randomAlphanumeric(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val sb = StringBuilder()
        for (i in 0 until length) {
            sb.append(chars[(i * 7 + 13) % chars.length])
        }
        return sb.toString()
    }

    /**
     * Returns a string repeated to fill a given length.
     */
    fun repeated(pattern: String, totalLength: Int): String {
        if (pattern.isEmpty()) return ""
        val sb = StringBuilder()
        while (sb.length < totalLength) sb.append(pattern)
        return sb.substring(0, totalLength)
    }

    /**
     * Checks if text likely contains PII by looking for common patterns.
     */
    fun containsAnyPII(text: String): Boolean {
        return SSN_PATTERN.matcher(text).find() ||
               CARD_PATTERN.matcher(text).find() ||
               EMAIL_PATTERN.matcher(text).find() ||
               PHONE_PATTERN.matcher(text).find() ||
               IP_PATTERN.matcher(text).find()
    }

    /**
     * Extracts a list of SSN-like strings from text.
     */
    fun extractSSNs(text: String): List<String> {
        val matcher = SSN_PATTERN.matcher(text)
        val results = mutableListOf<String>()
        while (matcher.find()) results.add(matcher.group())
        return results
    }

    /**
     * Extracts email addresses from text.
     */
    fun extractEmails(text: String): List<String> {
        val matcher = EMAIL_PATTERN.matcher(text)
        val results = mutableListOf<String>()
        while (matcher.find()) results.add(matcher.group())
        return results
    }

    /**
     * Extracts IPv4 addresses from text.
     */
    fun extractIPs(text: String): List<String> {
        val matcher = IP_PATTERN.matcher(text)
        val results = mutableListOf<String>()
        while (matcher.find()) results.add(matcher.group())
        return results
    }

    /**
     * Returns a sample medical text with embedded PII.
     */
    fun medicalTextWithPII(
        patientName: String = "Jane Doe",
        ssn: String = "123-45-6789",
        dob: String = "01/01/1980",
        mrn: String = "MRN-12345678"
    ): String = """
        PATIENT RECORD — CONFIDENTIAL
        Patient Name: $patientName
        Date of Birth: $dob
        Social Security Number: $ssn
        Medical Record Number: $mrn

        Chief Complaint: Patient presents with chest pain.
        Duration: 2 hours.

        Medications: Lisinopril 10mg daily, Metformin 500mg twice daily.
        Allergies: Penicillin.

        Assessment: Acute coronary syndrome suspected. Ordered EKG and troponin levels.

        Physician: Dr. A. Smith, MD
        Date: 2024-01-15
        Time: 14:32
    """.trimIndent()

    /**
     * Returns a sample financial text with embedded PII.
     */
    fun financialTextWithPII(
        name: String = "John Smith",
        card: String = "4111111111111111",
        iban: String = "DE89370400440532013000",
        ssn: String = "234-56-7890",
        email: String = "john.smith@email.com"
    ): String = """
        FINANCIAL SERVICES APPLICATION

        Applicant: $name
        Tax ID (SSN): $ssn
        Email: $email

        Payment Method: Credit Card
        Card Number: $card
        Expiry: 12/2025

        Bank Account (IBAN): $iban

        Loan Amount Requested: $50,000
        Purpose: Home renovation

        Signature: ___________________   Date: __________
    """.trimIndent()

    /**
     * Returns a sample HR text with embedded PII.
     */
    fun hrTextWithPII(
        name: String = "Alice Johnson",
        ssn: String = "345-67-8901",
        email: String = "alice@company.com",
        phone: String = "(617) 555-0100",
        dob: String = "1985-06-15"
    ): String = """
        EMPLOYEE ONBOARDING FORM — HR CONFIDENTIAL

        Full Name: $name
        Social Security Number: $ssn
        Date of Birth: $dob
        Personal Email: $email
        Emergency Phone: $phone

        Department: Engineering
        Manager: Bob Wilson
        Start Date: 2024-02-01

        Benefits Selection:
        - Health Insurance: PPO Plan
        - Dental Insurance: Basic Plan
        - 401k Contribution: 6%
    """.trimIndent()

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 8: Compiled regex patterns
    // ═══════════════════════════════════════════════════════════════════════════

    val SSN_PATTERN: Pattern = Pattern.compile(
        """(?<!\d)(?!000|666|9\d{2})\d{3}-(?!00)\d{2}-(?!0000)\d{4}(?!\d)"""
    )

    val CARD_PATTERN: Pattern = Pattern.compile(
        """(?:4[0-9]{12}(?:[0-9]{3,6})?|5[1-5][0-9]{14}|2[2-7][0-9]{14}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|6(?:011|5[0-9]{2})[0-9]{12}|(?:2131|1800|35\d{3})\d{11})"""
    )

    val EMAIL_PATTERN: Pattern = Pattern.compile(
        """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"""
    )

    val PHONE_PATTERN: Pattern = Pattern.compile(
        """(?:\+?1[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}"""
    )

    val IP_PATTERN: Pattern = Pattern.compile(
        """(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"""
    )

    val IBAN_PATTERN: Pattern = Pattern.compile(
        """[A-Z]{2}\d{2}[A-Z0-9]{4,30}"""
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 9: Common assertion helpers
    // ═══════════════════════════════════════════════════════════════════════════

    data class PIICheckResult(
        val hasSSN: Boolean,
        val hasCard: Boolean,
        val hasEmail: Boolean,
        val hasPhone: Boolean,
        val hasIP: Boolean,
        val hasIBAN: Boolean
    ) {
        val hasAnyPII: Boolean get() = hasSSN || hasCard || hasEmail || hasPhone || hasIP || hasIBAN
        val piiCount: Int get() = listOf(hasSSN, hasCard, hasEmail, hasPhone, hasIP, hasIBAN).count { it }
    }

    fun checkTextForPII(text: String): PIICheckResult {
        return PIICheckResult(
            hasSSN = SSN_PATTERN.matcher(text).find(),
            hasCard = CARD_PATTERN.matcher(text).find(),
            hasEmail = EMAIL_PATTERN.matcher(text).find(),
            hasPhone = PHONE_PATTERN.matcher(text).find(),
            hasIP = IP_PATTERN.matcher(text).find(),
            hasIBAN = IBAN_PATTERN.matcher(text).find()
        )
    }

    /**
     * Generates a list of SSNs by area code for testing.
     */
    fun ssnsByArea(area: Int, count: Int = 5): List<String> {
        require(area in 1..699 && area != 666) { "Invalid area: $area" }
        return (1..count).map { i ->
            "%03d-%02d-%04d".format(area, (i % 99) + 1, (i % 9999) + 1)
        }
    }

    /**
     * Generates test email addresses for a given domain.
     */
    fun emailsForDomain(domain: String, count: Int = 5): List<String> {
        val usernames = listOf("alice", "bob", "charlie", "diana", "eve",
            "frank", "grace", "henry", "iris", "jack")
        return (0 until minOf(count, usernames.size)).map { "${usernames[it]}@$domain" }
    }

    /**
     * Generates text fragments with varying PII density.
     */
    fun generateParagraphWithNoPII(): String = """
        The quarterly report shows strong performance across all business units.
        Revenue increased by 12% compared to the same period last year, driven by
        higher demand in the enterprise segment. Operating expenses remained stable
        at approximately 65% of revenue, resulting in improved margins. The board
        has approved additional investment in research and development initiatives.
    """.trimIndent()

    fun generateParagraphWithLowPII(email: String): String = """
        Please send your feedback to our team. You can reach us by email at
        $email or fill out the web form on our website. We appreciate all
        customer feedback and strive to respond within two business days.
        Thank you for being a valued customer.
    """.trimIndent()

    fun generateParagraphWithMediumPII(email: String, phone: String): String = """
        Your account has been created. Your username is the email address you
        provided: $email. For account recovery or urgent support, please call
        $phone and have your account ID ready. You should receive a
        verification email shortly. If you don't see it, check your spam folder.
    """.trimIndent()

    fun generateParagraphWithHighPII(ssn: String, card: String, email: String, phone: String): String = """
        CONFIDENTIAL - DO NOT DISTRIBUTE

        Customer service representative created the following account on behalf of the customer:

        - SSN on file: $ssn
        - Primary payment method: Card ending $card
        - Contact email: $email
        - Contact phone: $phone
        - Account created: 2024-01-15 14:32:00 UTC

        Please shred this document after verification.
    """.trimIndent()
}
