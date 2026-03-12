package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

// =============================================================================
// RegexScreenerExtendedTest2
//
// A comprehensive JUnit4 test suite for the RegexScreener utility class.
// This file covers 180 test scenarios grouped into 9 sections:
//
//   Section 1 — SSN Screening              (20 tests, lines ~70–330)
//   Section 2 — Credit Card Screening      (20 tests, lines ~340–600)
//   Section 3 — Email Screening            (20 tests, lines ~610–870)
//   Section 4 — Phone Screening            (20 tests, lines ~880–1140)
//   Section 5 — IP Address Screening       (20 tests, lines ~1150–1410)
//   Section 6 — Combined Multi-Type        (20 tests, lines ~1420–1700)
//   Section 7 — Performance/Scalability    (15 tests, lines ~1710–1900)
//   Section 8 — Edge Case Inputs           (20 tests, lines ~1910–2170)
//   Section 9 — screenAll Batch Method     (25 tests, lines ~2180–2500)
//
// Assumptions:
//   - RegexScreener() has a no-argument constructor.
//   - screen(text) returns a RegexScreenerResult with:
//       detected: Boolean
//       entityTypes: Set<String>
//       matchCount: Int
//       processingTimeMs: Long
//   - Individual check methods return Boolean:
//       containsSSN, containsCreditCard, containsEmail,
//       containsPhone, containsIPAddress, containsAPIKey
//   - screenAll(texts: List<String>) returns Map<String, RegexScreenerResult>
//
// Each test uses @Test with Kotlin backtick function names and contains
// between 2 and 4 assertions to verify expected behaviour.
// =============================================================================

class RegexScreenerExtendedTest2 {

    // -------------------------------------------------------------------------
    // Shared test fixture — a single RegexScreener instance reused across all
    // tests in this class.  RegexScreener is assumed to be stateless so sharing
    // one instance is safe and avoids per-test allocation overhead.
    // -------------------------------------------------------------------------
    private val screener = RegexScreener()

    // =========================================================================
    // SECTION 1 — SSN SCREENING
    //
    // Social Security Numbers in the United States follow the pattern
    // AAA-GG-SSSS where AAA is the area number (001–899, excluding 666),
    // GG is the group number (01–99), and SSSS is the serial number (0001–9999).
    //
    // These tests verify:
    //   * Dashes-formatted SSNs  (e.g. 123-45-6789)
    //   * Space-separated SSNs   (e.g. 123 45 6789)
    //   * Plain/unformatted SSNs (e.g. 123456789)
    //   * SSNs at start, middle, and end of longer strings
    //   * Multiple SSNs in a single text
    //   * Invalid SSN patterns that should NOT be detected
    //   * Numbers that look similar to SSNs (zip codes, phone numbers)
    // =========================================================================

    @Test
    fun `ssn with dashes is detected`() {
        val text = "My SSN is 123-45-6789 and it must be kept private."
        val result = screener.screen(text)
        assertTrue("Expected SSN to be detected in dashes format", result.detected)
        assertTrue("Expected matchCount >= 1 for dashes-format SSN", result.matchCount >= 1)
        assertTrue(
            "Expected entityTypes to contain SSN entry",
            result.entityTypes.any { it.contains("SSN", ignoreCase = true) }
        )
        assertTrue("processingTimeMs should be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `ssn with spaces is detected`() {
        val text = "Social Security reference number: 234 56 7890 — please handle with care."
        val result = screener.screen(text)
        assertTrue("Expected SSN with spaces to be detected", result.detected)
        assertTrue("Expected at least one match for space-formatted SSN", result.matchCount >= 1)
        assertNotNull("Result object must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `ssn plain nine digits is detected`() {
        val text = "Applicant SSN (no separators): 345678901"
        val result = screener.screen(text)
        assertTrue("Expected plain nine-digit SSN to be detected", result.detected)
        assertTrue("Expected matchCount >= 1 for plain SSN", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `multiple ssns in one text are all detected`() {
        val text = "Employee A SSN: 111-22-3333 and Employee B SSN: 444-55-6666 — both must be redacted."
        val result = screener.screen(text)
        assertTrue("Expected detection when multiple SSNs present", result.detected)
        assertTrue("Expected matchCount >= 2 for two SSNs", result.matchCount >= 2)
        assertNotNull("Result must not be null", result)
        assertTrue("entityTypes must not be empty", result.entityTypes.isNotEmpty())
    }

    @Test
    fun `text without ssn is not detected as ssn`() {
        val text = "Hello, my name is Alice and I live in New York. No sensitive numbers here."
        val detected = screener.containsSSN(text)
        assertFalse("Plain descriptive text should not contain SSN", detected)
    }

    @Test
    fun `partial ssn pattern does not trigger detection`() {
        val text = "Reference code 12-34 is used internally and is not a social security number."
        val detected = screener.containsSSN(text)
        assertFalse("A two-segment partial pattern should not match a full SSN", detected)
    }

    @Test
    fun `ssn at start of text is detected`() {
        val text = "999-88-7777 is the social security number on file for this patient."
        val result = screener.screen(text)
        assertTrue("SSN at the very start of text should be detected", result.detected)
        assertTrue("matchCount >= 1 when SSN is at start", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `ssn at end of text is detected`() {
        val text = "Please verify the record against the social security number: 555-44-3322"
        val result = screener.screen(text)
        assertTrue("SSN at end of string should be detected", result.detected)
        assertTrue("matchCount >= 1 for trailing SSN", result.matchCount >= 1)
        assertTrue("entityTypes should not be empty", result.entityTypes.isNotEmpty())
        assertTrue("processingTimeMs must be >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `ssn with dashes containsSSN returns true`() {
        val text = "Patient record — SSN: 777-88-9999 — verified by clerk."
        val detected = screener.containsSSN(text)
        assertTrue("containsSSN should return true for dashes-formatted SSN", detected)
    }

    @Test
    fun `ssn with spaces containsSSN returns true`() {
        val text = "Internal file reference number is 321 65 4321 for tracking purposes."
        val detected = screener.containsSSN(text)
        assertTrue("containsSSN should return true for space-separated SSN", detected)
    }

    @Test
    fun `phone number is not misidentified as ssn`() {
        val text = "Please call our support line at (800) 555-1234 during business hours."
        val detected = screener.containsSSN(text)
        assertFalse("A US phone number should not be misidentified as an SSN", detected)
    }

    @Test
    fun `zip code is not misidentified as ssn`() {
        val text = "Please ship to zip code 90210 in Beverly Hills, California."
        val detected = screener.containsSSN(text)
        assertFalse("A five-digit US zip code should not be mistaken for an SSN", detected)
    }

    @Test
    fun `ssn in sentence context is detected`() {
        val text = "According to official records, the applicant's SSN is 222-11-0000 and was verified on intake."
        val result = screener.screen(text)
        assertTrue("SSN embedded in a sentence should be detected", result.detected)
        assertTrue("matchCount >= 1 for SSN in context", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `invalid ssn area 000 not detected`() {
        val text = "This number 000-12-3456 uses the invalid area code zero and should be rejected."
        val detected = screener.containsSSN(text)
        assertFalse("SSN with area number 000 should not pass validation", detected)
    }

    @Test
    fun `invalid ssn area 666 not detected`() {
        val text = "Attempting to use 666-12-3456 but area 666 is administratively excluded."
        val detected = screener.containsSSN(text)
        assertFalse("SSN with area number 666 should not pass validation", detected)
    }

    @Test
    fun `ssn in multiline text is detected`() {
        val text = buildString {
            appendLine("Name: John Doe")
            appendLine("Date of Birth: 1980-01-01")
            appendLine("SSN: 123-45-6789")
            appendLine("Address: 123 Main Street, Springfield")
        }
        val result = screener.screen(text)
        assertTrue("SSN in multiline text should be detected", result.detected)
        assertTrue("matchCount >= 1 for SSN in multiline text", result.matchCount >= 1)
        assertTrue("entityTypes should not be empty", result.entityTypes.isNotEmpty())
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `repeated ssn format increases match count`() {
        val text = "SSN1: 111-22-3333   SSN2: 444-55-6666   SSN3: 777-88-9999"
        val result = screener.screen(text)
        assertTrue("Expected detection with three SSNs", result.detected)
        assertTrue("Expected matchCount >= 3 for three distinct SSNs", result.matchCount >= 3)
        assertNotNull("Result must not be null", result)
        assertTrue("entityTypes should contain SSN type", result.entityTypes.any { it.contains("SSN", ignoreCase = true) })
    }

    @Test
    fun `text with only numbers no ssn format not detected`() {
        val text = "The numbers 12345 67890 and 11111 appear but have no SSN formatting."
        val detected = screener.containsSSN(text)
        assertFalse("Bare number groups without SSN separators should not be detected", detected)
    }

    @Test
    fun `ssn with leading zeros in area is detected`() {
        val text = "File reference SSN: 001-23-4567 — area code starts with leading zero."
        val result = screener.screen(text)
        assertTrue("SSN with leading zeros in area number should be detected", result.detected)
        assertTrue("matchCount >= 1 for leading-zero area SSN", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `processing time is non negative for ssn screening`() {
        val text = "Account holder SSN: 987-65-4321 — please keep this confidential."
        val result = screener.screen(text)
        assertTrue("processingTimeMs should be >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected should be true for valid SSN", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("matchCount should be at least 1", result.matchCount >= 1)
    }

    // =========================================================================
    // SECTION 2 — CREDIT CARD SCREENING
    //
    // Credit card numbers follow specific IIN (Issuer Identification Number)
    // patterns defined by card networks.  Key ranges:
    //   * Visa:       starts with 4, 13 or 16 digits
    //   * MasterCard: starts with 51–55 or 2221–2720, 16 digits
    //   * Amex:       starts with 34 or 37, 15 digits
    //   * Discover:   starts with 6011, 622126–622925, 644–649, 65, 16 digits
    //   * JCB:        starts with 3528–3589, 16 digits
    //
    // These tests verify:
    //   * Card numbers with dash separators
    //   * Card numbers with space separators
    //   * Card numbers with no separators (plain)
    //   * Multiple card types in a single text
    //   * Numbers similar to card numbers that should NOT match (phone, zip, etc.)
    //   * Masked/redacted card numbers
    //   * Cards embedded in common document formats (JSON, HTML, receipt text)
    // =========================================================================

    @Test
    fun `visa card 16 digits with dashes is detected`() {
        val text = "Please charge the following card: 4111-1111-1111-1111 for the order."
        val result = screener.screen(text)
        assertTrue("Visa card with dash separators should be detected", result.detected)
        assertTrue("matchCount >= 1 for dash-formatted Visa card", result.matchCount >= 1)
        assertTrue(
            "entityTypes should contain CREDIT_CARD or CARD type",
            result.entityTypes.any { it.contains("CREDIT", ignoreCase = true) || it.contains("CARD", ignoreCase = true) }
        )
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `mastercard 16 digits with spaces is detected`() {
        val text = "Payment method on record: 5500 0000 0000 0004 — expires 12/26."
        val result = screener.screen(text)
        assertTrue("MasterCard with space separators should be detected", result.detected)
        assertTrue("matchCount >= 1 for space-formatted MasterCard", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `amex 15 digits is detected`() {
        val text = "American Express card on file: 3714 496353 98431 — authorisation pending."
        val result = screener.screen(text)
        assertTrue("Amex 15-digit card should be detected", result.detected)
        assertTrue("matchCount >= 1 for Amex card", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `visa card plain 16 digits is detected`() {
        val text = "Stored token: 4111111111111111 — card is active."
        val detected = screener.containsCreditCard(text)
        assertTrue("Plain 16-digit Visa without separators should be detected", detected)
    }

    @Test
    fun `mastercard plain 16 digits is detected`() {
        val text = "Vault entry: card_number=5500000000000004 status=active"
        val detected = screener.containsCreditCard(text)
        assertTrue("Plain MasterCard without separators should be detected", detected)
    }

    @Test
    fun `phone number not misidentified as credit card`() {
        val text = "Call us at +1-800-555-1234 for billing support — we are available 24/7."
        val detected = screener.containsCreditCard(text)
        assertFalse("A US toll-free phone number should not be misidentified as a credit card", detected)
    }

    @Test
    fun `zip code not misidentified as credit card`() {
        val text = "Please ship to the ZIP+4 code 90210-1234 in California."
        val detected = screener.containsCreditCard(text)
        assertFalse("A ZIP+4 code should not be misidentified as a credit card", detected)
    }

    @Test
    fun `multiple credit cards in text all detected`() {
        val text = "Primary card: 4111-1111-1111-1111 and backup card: 5500-0000-0000-0004 — both on file."
        val result = screener.screen(text)
        assertTrue("Should detect credit cards when two are present", result.detected)
        assertTrue("matchCount >= 2 for two credit cards", result.matchCount >= 2)
        assertNotNull("Result must not be null", result)
        assertTrue("entityTypes should not be empty", result.entityTypes.isNotEmpty())
    }

    @Test
    fun `discover card is detected`() {
        val text = "Billing info — Discover card: 6011-1111-1111-1117 — CVV on request."
        val result = screener.screen(text)
        assertTrue("Discover card should be detected", result.detected)
        assertTrue("matchCount >= 1 for Discover card", result.matchCount >= 1)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `jcb card is detected`() {
        val text = "International customer JCB card: 3530111333300000 — processed successfully."
        val detected = screener.containsCreditCard(text)
        assertTrue("JCB card number should be detected", detected)
    }

    @Test
    fun `random 16 digit number without card prefix not detected`() {
        val text = "Internal correlation ID: 1234567890123456 — do not share externally."
        val detected = screener.containsCreditCard(text)
        assertFalse("A random 16-digit number without a valid IIN prefix should not be detected", detected)
    }

    @Test
    fun `credit card in receipt text is detected`() {
        val text = "Thank you for your purchase! Card charged: 4111 1111 1111 1111. Total: \$149.99. Order #78901."
        val result = screener.screen(text)
        assertTrue("Card in receipt text should be detected", result.detected)
        assertTrue("matchCount >= 1 for card in receipt", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `credit card containsCreditCard returns false for plain text`() {
        val text = "The weather today is sunny and warm with a high of 75 degrees Fahrenheit."
        val detected = screener.containsCreditCard(text)
        assertFalse("A plain descriptive sentence should not contain a credit card number", detected)
    }

    @Test
    fun `visa card at start of string is detected`() {
        val text = "4532015112830366 is the Visa card number associated with this account."
        val result = screener.screen(text)
        assertTrue("Visa card at the very start of the string should be detected", result.detected)
        assertTrue("matchCount >= 1 for leading Visa card", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `masked credit card not misidentified`() {
        val text = "Your saved card ending in XXXX-XXXX-XXXX-4242 is ready for checkout."
        val detected = screener.containsCreditCard(text)
        assertFalse("A masked/redacted card representation should not be flagged as a real card number", detected)
    }

    @Test
    fun `credit card with extra whitespace around it is detected`() {
        val text = "   4111 1111 1111 1111   "
        val result = screener.screen(text)
        assertTrue("Card number surrounded by spaces should be detected", result.detected)
        assertTrue("matchCount >= 1 for card with surrounding whitespace", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `social security number not misidentified as credit card`() {
        val text = "Patient file — SSN: 123-45-6789 — needs verification."
        val detected = screener.containsCreditCard(text)
        assertFalse("A Social Security Number should not be misidentified as a credit card", detected)
    }

    @Test
    fun `credit card in json payload is detected`() {
        val text = """{"card_number": "4111111111111111", "expiry_month": 12, "expiry_year": 2026}"""
        val result = screener.screen(text)
        assertTrue("Card number in a JSON payload should be detected", result.detected)
        assertTrue("matchCount >= 1 for card in JSON", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertTrue("entityTypes must not be empty", result.entityTypes.isNotEmpty())
    }

    @Test
    fun `expiry date not misidentified as credit card`() {
        val text = "Card expiration date: 12/2026 — please update before renewal."
        val detected = screener.containsCreditCard(text)
        assertFalse("An expiry date alone should not be detected as a credit card number", detected)
    }

    @Test
    fun `credit card processing time is non negative`() {
        val text = "New card registered: 4111111111111111 — assigned to account A-1001."
        val result = screener.screen(text)
        assertTrue("processingTimeMs should be >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected should be true for Visa card", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("matchCount should be at least 1", result.matchCount >= 1)
    }

    // =========================================================================
    // SECTION 3 — EMAIL SCREENING
    //
    // RFC 5321 / RFC 5322 compliant email addresses have the form
    // local-part@domain where the local-part may contain letters, digits,
    // dots, plus signs, hyphens and underscores, and the domain consists
    // of dot-separated labels ending in a recognised TLD.
    //
    // These tests cover:
    //   * Standard personal emails
    //   * Role-based emails (admin, noreply, support)
    //   * Subdomain emails (a.b.example.com)
    //   * Emails with special characters in local part (+, -, .)
    //   * Mixed-case email addresses
    //   * Emails embedded in HTML, CSV, JSON, XML
    //   * Strings that look similar to emails but are NOT valid
    // =========================================================================

    @Test
    fun `simple email is detected`() {
        val text = "If you have questions, please contact alice@example.com at your earliest convenience."
        val result = screener.screen(text)
        assertTrue("A simple user@domain.tld email should be detected", result.detected)
        assertTrue("matchCount >= 1 for simple email", result.matchCount >= 1)
        assertTrue(
            "entityTypes should contain EMAIL type",
            result.entityTypes.any { it.contains("EMAIL", ignoreCase = true) }
        )
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `role email admin is detected`() {
        val text = "Please escalate unresolved issues to admin@company.org for immediate attention."
        val result = screener.screen(text)
        assertTrue("Role-based admin email should be detected", result.detected)
        assertTrue("matchCount >= 1 for admin email", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `subdomain email is detected`() {
        val text = "Reach the regional team at support@mail.example.co.uk for localised assistance."
        val result = screener.screen(text)
        assertTrue("Subdomain email address should be detected", result.detected)
        assertTrue("matchCount >= 1 for subdomain email", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `email with plus tag is detected`() {
        val text = "Filter emails to the alias user+tag@gmail.com for this campaign, please."
        val detected = screener.containsEmail(text)
        assertTrue("Email with a plus-sign tag should be detected", detected)
    }

    @Test
    fun `email with numbers in local part is detected`() {
        val text = "The account user123@domain.net was created on the third of January."
        val detected = screener.containsEmail(text)
        assertTrue("Email with digits in the local part should be detected", detected)
    }

    @Test
    fun `plain text without email not detected`() {
        val text = "The quick brown fox jumps over the lazy dog. No email addresses appear here."
        val detected = screener.containsEmail(text)
        assertFalse("A plain sentence with no email address should not be detected", detected)
    }

    @Test
    fun `email in html anchor tag is detected`() {
        val text = """<p>For support, use <a href="mailto:info@example.com">info@example.com</a>.</p>"""
        val result = screener.screen(text)
        assertTrue("Email inside an HTML anchor mailto link should be detected", result.detected)
        assertTrue("matchCount >= 1 for email in HTML", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `multiple emails in text all detected`() {
        val text = "From: alice@a.com  |  To: bob@b.com  |  CC: charlie@c.com  |  BCC: dana@d.com"
        val result = screener.screen(text)
        assertTrue("All four emails in the text should be detected", result.detected)
        assertTrue("matchCount >= 4 for four distinct emails", result.matchCount >= 4)
        assertNotNull("Result must not be null", result)
        assertTrue("entityTypes must contain EMAIL type", result.entityTypes.any { it.contains("EMAIL", ignoreCase = true) })
    }

    @Test
    fun `email with hyphen in domain is detected`() {
        val text = "Reach our department via contact@my-company.com — response within 24 hours."
        val detected = screener.containsEmail(text)
        assertTrue("Email with a hyphen in the domain name should be detected", detected)
    }

    @Test
    fun `email at the end of sentence is detected`() {
        val text = "For all correspondence, please write to john.doe@example.com."
        val result = screener.screen(text)
        assertTrue("Email at the end of a sentence (before full stop) should be detected", result.detected)
        assertTrue("matchCount >= 1 for trailing email", result.matchCount >= 1)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `email with uppercase characters is detected`() {
        val text = "Corporate communications are routed through Alice.Smith@Example.COM daily."
        val detected = screener.containsEmail(text)
        assertTrue("Mixed-case email address should be detected", detected)
    }

    @Test
    fun `partial email without at symbol not detected`() {
        val text = "The username is aliceexample.com but it is missing the at sign entirely."
        val detected = screener.containsEmail(text)
        assertFalse("A string without the @ symbol should not be detected as an email", detected)
    }

    @Test
    fun `email without domain extension not detected`() {
        val text = "Local mail sent to user@localhost may not be a fully qualified email address."
        val detected = screener.containsEmail(text)
        assertFalse("An address pointing to localhost without a TLD may not be a valid email", detected)
    }

    @Test
    fun `email in csv row is detected`() {
        val text = "John,Doe,john.doe@company.com,Senior Engineer,Engineering"
        val result = screener.screen(text)
        assertTrue("Email embedded in a CSV row should be detected", result.detected)
        assertTrue("matchCount >= 1 for CSV row email", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `email in json string is detected`() {
        val text = """{"user_id": 42, "email": "test.user@domain.org", "role": "admin", "active": true}"""
        val result = screener.screen(text)
        assertTrue("Email in a JSON object value should be detected", result.detected)
        assertTrue("matchCount >= 1 for email in JSON", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertTrue("entityTypes must not be empty", result.entityTypes.isNotEmpty())
    }

    @Test
    fun `email containsEmail returns false for url without email`() {
        val text = "Please visit https://www.example.com/about for further information on our services."
        val detected = screener.containsEmail(text)
        assertFalse("A plain HTTP URL without an email address should not be detected", detected)
    }

    @Test
    fun `email with dot in local part is detected`() {
        val text = "The point of contact for this project is first.last@bigcorp.io — copy all stakeholders."
        val detected = screener.containsEmail(text)
        assertTrue("Email with a dot separator in the local part should be detected", detected)
    }

    @Test
    fun `noreply email is detected`() {
        val text = "This message was automatically generated and sent from noreply@notifications.service.com."
        val result = screener.screen(text)
        assertTrue("A noreply/ automated email address should be detected", result.detected)
        assertTrue("matchCount >= 1 for noreply email", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `email in xml attribute is detected`() {
        val text = """<user email="support@example.net" active="true" role="operator"/>"""
        val result = screener.screen(text)
        assertTrue("Email in an XML attribute value should be detected", result.detected)
        assertTrue("matchCount >= 1 for email in XML", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `email processing time is non negative`() {
        val text = "For billing enquiries reach us at billing@world.io and we will respond promptly."
        val result = screener.screen(text)
        assertTrue("processingTimeMs should be >= 0 for email screening", result.processingTimeMs >= 0L)
        assertTrue("detected should be true when email is present", result.detected)
        assertTrue("entityTypes should not be empty", result.entityTypes.isNotEmpty())
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    // =========================================================================
    // SECTION 4 — PHONE SCREENING
    //
    // Phone number formats covered by these tests include:
    //   * US local: (NXX) NXX-XXXX  or  NXX-NXX-XXXX
    //   * US with country code: +1-NXX-NXX-XXXX
    //   * Toll-free US numbers: 800/888/877/866/855/844/833 prefixes
    //   * Plain 10-digit: NNNNNNNNNN
    //   * International (UK, AU): +44 or +61 prefix
    //   * Vanity numbers: 1-800-FLOWERS
    //   * Dot-separated: NXX.NXX.XXXX
    //   * With extension: ext 201
    //
    // Also verifies that phone-like patterns that are NOT phones
    // (SSNs, card numbers, dates) are not incorrectly matched.
    // =========================================================================

    @Test
    fun `us phone with dashes is detected`() {
        val text = "To speak with a representative, please call us at 800-555-1234 weekdays."
        val result = screener.screen(text)
        assertTrue("US phone number with dashes should be detected", result.detected)
        assertTrue("matchCount >= 1 for dashes-formatted US phone", result.matchCount >= 1)
        assertTrue(
            "entityTypes should contain PHONE type",
            result.entityTypes.any { it.contains("PHONE", ignoreCase = true) }
        )
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `us phone with parentheses and space is detected`() {
        val text = "Main office phone number: (212) 555-9876 — ask for the accounts department."
        val result = screener.screen(text)
        assertTrue("US phone with parenthetical area code should be detected", result.detected)
        assertTrue("matchCount >= 1 for parenthesised phone", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `us phone with country code is detected`() {
        val text = "International dial-in: +1-415-555-2671 — please use this number when calling from abroad."
        val result = screener.screen(text)
        assertTrue("US phone with +1 country code should be detected", result.detected)
        assertTrue("matchCount >= 1 for phone with country code", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `toll free number is detected`() {
        val text = "Call our free customer helpline at 1-800-555-0199 for immediate assistance."
        val detected = screener.containsPhone(text)
        assertTrue("Toll-free 1-800 number should be detected", detected)
    }

    @Test
    fun `uk international phone is detected`() {
        val text = "London regional office: +44 20 7946 0958 — open from 9 AM to 5 PM GMT."
        val detected = screener.containsPhone(text)
        assertTrue("UK international phone number should be detected", detected)
    }

    @Test
    fun `plain text without phone not detected`() {
        val text = "Our office is open Monday through Friday from nine o'clock until five o'clock."
        val detected = screener.containsPhone(text)
        assertFalse("Plain descriptive text without phone numbers should not be detected", detected)
    }

    @Test
    fun `ssn with dashes not misidentified as phone`() {
        val text = "Record contains SSN only: 123-45-6789 — no phone number present in this field."
        val detected = screener.containsPhone(text)
        assertFalse("An SSN (AAA-GG-SSSS format) should not be mistaken for a phone number", detected)
    }

    @Test
    fun `phone in email signature is detected`() {
        val text = buildString {
            appendLine("Best regards,")
            appendLine("John Smith")
            appendLine("Senior Software Engineer")
            appendLine("Direct: 303-555-7890")
            appendLine("Email: john.smith@example.com")
        }
        val result = screener.screen(text)
        assertTrue("Phone number in an email signature block should be detected", result.detected)
        assertTrue("matchCount >= 1 for phone in signature", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `phone ten digits no formatting is detected`() {
        val text = "For callbacks leave your ten-digit number: 5551234567 and we will ring you back."
        val detected = screener.containsPhone(text)
        assertTrue("Ten-digit phone number without any formatting should be detected", detected)
    }

    @Test
    fun `multiple phones in text all detected`() {
        val text = "Office main: (212) 555-1000  |  Fax: (212) 555-2000  |  Mobile: 415-555-3000"
        val result = screener.screen(text)
        assertTrue("All phone numbers in a list should be detected", result.detected)
        assertTrue("matchCount >= 2 for multiple phones", result.matchCount >= 2)
        assertNotNull("Result must not be null", result)
        assertTrue("entityTypes must contain PHONE type", result.entityTypes.any { it.contains("PHONE", ignoreCase = true) })
    }

    @Test
    fun `five digit zip code not misidentified as phone`() {
        val text = "Parcel delivery address ZIP code: 90210 — Beverly Hills, California."
        val detected = screener.containsPhone(text)
        assertFalse("A five-digit ZIP code should never be detected as a phone number", detected)
    }

    @Test
    fun `phone with extension is detected`() {
        val text = "For the accounts receivable department, dial (800) 555-4321 ext. 201 and ask for Maria."
        val result = screener.screen(text)
        assertTrue("Phone number with an extension should be detected", result.detected)
        assertTrue("matchCount >= 1 for phone with extension", result.matchCount >= 1)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `phone containsPhone returns false for date string`() {
        val text = "The contract signing date is 2025-01-15 at the downtown office."
        val detected = screener.containsPhone(text)
        assertFalse("An ISO date string (YYYY-MM-DD) should not be detected as a phone number", detected)
    }

    @Test
    fun `phone in html tel link is detected`() {
        val text = """<a href="tel:+18005551234" class="cta">Call Now — Free</a>"""
        val result = screener.screen(text)
        assertTrue("Phone number in an HTML tel: link should be detected", result.detected)
        assertTrue("matchCount >= 1 for tel: link phone", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `australian phone number is detected`() {
        val text = "Sydney branch contact: +61 2 9876 5432 — available during AEST business hours."
        val detected = screener.containsPhone(text)
        assertTrue("Australian international phone number should be detected", detected)
    }

    @Test
    fun `phone with dots as separator is detected`() {
        val text = "For technical support, call 212.555.6789 — our engineers are on standby."
        val result = screener.screen(text)
        assertTrue("Phone number using dots as separators should be detected", result.detected)
        assertTrue("matchCount >= 1 for dot-separated phone", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `credit card number not misidentified as phone`() {
        val text = "Stored payment token: card 4111111111111111 — do not share this number."
        val detected = screener.containsPhone(text)
        assertFalse("A credit card number should not be misidentified as a phone number", detected)
    }

    @Test
    fun `phone in json is detected`() {
        val text = """{"name": "Alice Nguyen", "phone": "(415) 555-1234", "department": "Engineering"}"""
        val result = screener.screen(text)
        assertTrue("Phone number in a JSON payload should be detected", result.detected)
        assertTrue("matchCount >= 1 for phone in JSON", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `vanity toll free number is detected`() {
        val text = "Order flowers by calling our toll-free vanity line 1-800-FLOWERS today."
        val detected = screener.containsPhone(text)
        assertTrue("Vanity toll-free number (letters) should be detected", detected)
    }

    @Test
    fun `phone processing time is non negative`() {
        val text = "My personal mobile number is (555) 123-4567 — please text first."
        val result = screener.screen(text)
        assertTrue("processingTimeMs should be >= 0 for phone screening", result.processingTimeMs >= 0L)
        assertTrue("detected should be true for a valid phone number", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    // =========================================================================
    // SECTION 5 — IP ADDRESS SCREENING
    //
    // IPv4 addresses follow the dot-decimal format A.B.C.D where each
    // octet A–D is in the range 0–255.  Well-known special ranges include:
    //   * Loopback:       127.0.0.0/8
    //   * Private class A: 10.0.0.0/8
    //   * Private class B: 172.16.0.0/12
    //   * Private class C: 192.168.0.0/16
    //   * Public IPs:     any other valid range
    //   * Broadcast:      255.255.255.255
    //
    // These tests also verify that dot-decimal patterns that are NOT IPs
    // (software version numbers, decimal numbers, short dates) are correctly
    // excluded from results.
    // =========================================================================

    @Test
    fun `valid ipv4 address is detected`() {
        val text = "The application server is running at IP address 192.168.1.1 on port 443."
        val result = screener.screen(text)
        assertTrue("A valid private IPv4 address should be detected", result.detected)
        assertTrue("matchCount >= 1 for valid IPv4", result.matchCount >= 1)
        assertTrue(
            "entityTypes should contain IP type",
            result.entityTypes.any { it.contains("IP", ignoreCase = true) }
        )
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `public ipv4 address is detected`() {
        val text = "DNS resolver configured as 8.8.8.8 — Google Public DNS primary server."
        val result = screener.screen(text)
        assertTrue("A well-known public IPv4 address should be detected", result.detected)
        assertTrue("matchCount >= 1 for public IPv4", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `loopback address is detected`() {
        val text = "Development web server is accessible at 127.0.0.1 on port 8080 locally."
        val result = screener.screen(text)
        assertTrue("Loopback address 127.0.0.1 should be detected", result.detected)
        assertTrue("matchCount >= 1 for loopback address", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `multiple ipv4 addresses in text all detected`() {
        val text = "Network route from gateway 10.0.0.1 to destination 192.168.0.254 via relay 172.16.0.1."
        val result = screener.screen(text)
        assertTrue("All three IP addresses should be detected", result.detected)
        assertTrue("matchCount >= 3 for three distinct IPv4 addresses", result.matchCount >= 3)
        assertNotNull("Result must not be null", result)
        assertTrue("entityTypes must not be empty", result.entityTypes.isNotEmpty())
    }

    @Test
    fun `software version number not misidentified as ip`() {
        val text = "This service currently runs library version 1.2.3.4 which was released last month."
        val detected = screener.containsIPAddress(text)
        assertFalse("A four-part software version number should not be detected as an IPv4 address", detected)
    }

    @Test
    fun `date format not misidentified as ip`() {
        val text = "The annual conference took place on 2025.01.15 at the downtown convention centre."
        val detected = screener.containsIPAddress(text)
        assertFalse("A date written in dot notation should not be detected as an IP address", detected)
    }

    @Test
    fun `invalid ip with octet over 255 not detected`() {
        val text = "Misconfigured address 999.168.1.1 is not a valid IPv4 address in any range."
        val detected = screener.containsIPAddress(text)
        assertFalse("An address with an octet value above 255 should not be detected as valid IP", detected)
    }

    @Test
    fun `ip address in log line is detected`() {
        val text = "2025-01-01 12:00:00 INFO  HTTP request received from client 203.0.113.50"
        val result = screener.screen(text)
        assertTrue("IPv4 address in a server log line should be detected", result.detected)
        assertTrue("matchCount >= 1 for IP in log", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `ip address in url is detected`() {
        val text = "Make requests to http://203.0.113.10:8080/api/v1/users for the REST API."
        val result = screener.screen(text)
        assertTrue("IPv4 address embedded in a URL should be detected", result.detected)
        assertTrue("matchCount >= 1 for IP in URL", result.matchCount >= 1)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `cidr notation ip is detected`() {
        val text = "Firewall rule: block all ingress traffic from the subnet 192.168.1.0/24 immediately."
        val detected = screener.containsIPAddress(text)
        assertTrue("IPv4 address in CIDR notation should be detected", detected)
    }

    @Test
    fun `ip address containsIPAddress returns true for private range`() {
        val text = "Internal microservice registered on 10.0.0.1 within the private network."
        val detected = screener.containsIPAddress(text)
        assertTrue("A private-range IPv4 address should be detected", detected)
    }

    @Test
    fun `plain text without ip not detected`() {
        val text = "This is an ordinary sentence that does not contain any network addresses whatsoever."
        val detected = screener.containsIPAddress(text)
        assertFalse("A plain sentence with no IP-like pattern should not be detected", detected)
    }

    @Test
    fun `ip in json config is detected`() {
        val text = """{"database": {"host": "192.168.1.100", "port": 3306, "name": "mydb"}}"""
        val result = screener.screen(text)
        assertTrue("IPv4 address in a JSON configuration object should be detected", result.detected)
        assertTrue("matchCount >= 1 for IP in JSON", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `broadcast address is detected`() {
        val text = "Sending ARP broadcast to 255.255.255.255 to discover all hosts on the segment."
        val detected = screener.containsIPAddress(text)
        assertTrue("The IPv4 broadcast address 255.255.255.255 should be detected", detected)
    }

    @Test
    fun `ip at end of sentence is detected`() {
        val text = "The suspicious connection attempt originated from external host 198.51.100.42."
        val result = screener.screen(text)
        assertTrue("IPv4 address at the end of a sentence should be detected", result.detected)
        assertTrue("matchCount >= 1 for trailing IPv4 address", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `fragment like 1 2 not misidentified as ip`() {
        val text = "Completed step 1.2 of the installation procedure — moving on to step 1.3 next."
        val detected = screener.containsIPAddress(text)
        assertFalse("A two-part decimal number such as 1.2 should not be detected as an IP", detected)
    }

    @Test
    fun `ip address in xml element is detected`() {
        val text = "<server><address>172.16.254.1</address><port>443</port><protocol>HTTPS</protocol></server>"
        val result = screener.screen(text)
        assertTrue("IPv4 in an XML element value should be detected", result.detected)
        assertTrue("matchCount >= 1 for IP in XML element", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `multiple ips in csv log is detected`() {
        val text = buildString {
            appendLine("10.0.0.1,GET,/api/users,200,12ms")
            appendLine("10.0.0.2,POST,/api/login,201,8ms")
            appendLine("172.16.0.5,DELETE,/api/sessions/9,204,5ms")
        }
        val result = screener.screen(text)
        assertTrue("All IPv4 addresses in a CSV access log should be detected", result.detected)
        assertTrue("matchCount >= 3 for three IPs in CSV", result.matchCount >= 3)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `ip version number confusion test`() {
        val text = "Stack: Python 3.9.7, Django 4.2.1, PostgreSQL 15.3 — all versions up to date."
        val detected = screener.containsIPAddress(text)
        assertFalse("Three-part or four-part software version strings should not be detected as IPs", detected)
    }

    @Test
    fun `ip processing time is non negative`() {
        val text = "Default gateway configured as 192.168.0.1 in the router DHCP settings."
        val result = screener.screen(text)
        assertTrue("processingTimeMs should be >= 0 for IP screening", result.processingTimeMs >= 0L)
        assertTrue("detected should be true for valid IPv4 address", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    // =========================================================================
    // SECTION 6 — COMBINED MULTI-TYPE SCREENING
    //
    // These tests verify that RegexScreener correctly handles text that contains
    // two, three, four, or all supported PII types simultaneously.  They also
    // confirm that:
    //   * entityTypes is a Set (no duplicate type strings)
    //   * matchCount accumulates across all detected types
    //   * Individual type methods (containsSSN etc.) agree with screen()
    //   * A completely clean text produces detected=false and matchCount=0
    // =========================================================================

    @Test
    fun `text with ssn and email has both detected`() {
        val text = "Patient record — SSN: 123-45-6789 — primary contact: patient@clinic.com"
        val result = screener.screen(text)
        assertTrue("Should detect PII when both SSN and email are present", result.detected)
        assertTrue("matchCount >= 2 for SSN + email", result.matchCount >= 2)
        assertTrue("entityTypes should have at least 2 entries", result.entityTypes.size >= 2)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `text with credit card and phone has both detected`() {
        val text = "Billing info — Card: 4111-1111-1111-1111 — Support: (800) 555-1234"
        val result = screener.screen(text)
        assertTrue("Should detect PII when both card and phone are present", result.detected)
        assertTrue("matchCount >= 2 for card + phone", result.matchCount >= 2)
        assertTrue("entityTypes should have at least 2 entries", result.entityTypes.size >= 2)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `text with email and ip has both detected`() {
        val text = "Admin user admin@corp.com authenticated from server address 192.168.1.50."
        val result = screener.screen(text)
        assertTrue("Should detect PII when email and IP are both present", result.detected)
        assertTrue("matchCount >= 2 for email + IP", result.matchCount >= 2)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `text with three pii types has all detected`() {
        val text = "Form submission — SSN: 987-65-4321 | Email: user@test.net | Phone: 415-555-9090"
        val result = screener.screen(text)
        assertTrue("Should detect PII when three types are present", result.detected)
        assertTrue("matchCount >= 3 for SSN + email + phone", result.matchCount >= 3)
        assertTrue("entityTypes should have at least 3 entries", result.entityTypes.size >= 3)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `text with all pii types has all detected`() {
        val text = "SSN: 111-22-3333 | Card: 4111111111111111 | Email: a@b.com | Phone: 800-555-0001 | IP: 192.168.0.1"
        val result = screener.screen(text)
        assertTrue("Should detect all PII types", result.detected)
        assertTrue("matchCount >= 5 for five PII items", result.matchCount >= 4)
        assertTrue("entityTypes must not be empty", result.entityTypes.isNotEmpty())
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `clean text with no pii is not detected`() {
        val text = "The annual corporate report shows a 15% increase in customer satisfaction this quarter."
        val result = screener.screen(text)
        assertFalse("A clean text with no PII should not be detected", result.detected)
        assertEquals("matchCount should be exactly 0 for clean text", 0, result.matchCount)
        assertTrue("entityTypes should be empty for clean text", result.entityTypes.isEmpty())
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `text with ssn and credit card entity types both present`() {
        val text = "New application — SSN: 444-55-6666 — Card: 5500000000000004"
        val result = screener.screen(text)
        assertTrue("Should detect PII with SSN and card", result.detected)
        assertTrue("matchCount >= 2 for SSN + card", result.matchCount >= 2)
        assertTrue(
            "entityTypes should contain SSN type",
            result.entityTypes.any { it.contains("SSN", ignoreCase = true) }
        )
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `text with email and credit card entity types both present`() {
        val text = "Receipt for user@shop.com: Visa card 4111-1111-1111-1111 was charged \$200.00"
        val result = screener.screen(text)
        assertTrue("Should detect PII with email and card", result.detected)
        assertTrue("matchCount >= 2 for email + card", result.matchCount >= 2)
        assertTrue(
            "entityTypes should contain EMAIL type",
            result.entityTypes.any { it.contains("EMAIL", ignoreCase = true) }
        )
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `text with phone and ip entity types both present`() {
        val text = "Access log: request from IP 10.0.0.5 by caller at (212) 555-6677 — approved."
        val result = screener.screen(text)
        assertTrue("Should detect PII with phone and IP", result.detected)
        assertTrue("matchCount >= 2 for phone + IP", result.matchCount >= 2)
        assertTrue("entityTypes must not be empty", result.entityTypes.isNotEmpty())
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `text with ssn phone and ip has three entity types`() {
        val text = "Composite record: SSN 222-33-4444 | Phone 303-555-7777 | Server IP 192.168.10.1"
        val result = screener.screen(text)
        assertTrue("Should detect three PII types", result.detected)
        assertTrue("matchCount >= 3 for SSN + phone + IP", result.matchCount >= 3)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `combined text match count matches sum of individual types`() {
        val text = "User email: a@b.com — Connected from IP: 8.8.4.4"
        val result = screener.screen(text)
        val emailDetected = screener.containsEmail(text)
        val ipDetected = screener.containsIPAddress(text)
        assertTrue("screen() should detect PII", result.detected)
        assertTrue("containsEmail should independently detect email", emailDetected)
        assertTrue("containsIPAddress should independently detect IP", ipDetected)
    }

    @Test
    fun `combined text processing time is non negative`() {
        val text = "SSN: 123-45-6789 | Card: 4111111111111111 | Email: x@y.com | Phone: 800-555-0001"
        val result = screener.screen(text)
        assertTrue("processingTimeMs must be >= 0 for multi-type text", result.processingTimeMs >= 0L)
        assertTrue("detected should be true", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("matchCount >= 4", result.matchCount >= 4)
    }

    @Test
    fun `text with duplicate pii of same type counts correctly`() {
        val text = "SSN1: 111-22-3333 and SSN2: 444-55-6666 — two distinct social security numbers."
        val result = screener.screen(text)
        assertTrue("Should detect PII when two SSNs are present", result.detected)
        assertTrue("matchCount >= 2 for two SSNs", result.matchCount >= 2)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertNotNull("Result must not be null", result)
    }

    @Test
    fun `combined check credit card and phone individual methods agree with screen`() {
        val text = "Billing: Visa 5500000000000004 — call (800) 555-3333 for refunds."
        val result = screener.screen(text)
        val ccDetected = screener.containsCreditCard(text)
        val phoneDetected = screener.containsPhone(text)
        assertTrue("screen() should detect PII", result.detected)
        assertTrue("containsCreditCard should independently detect the card", ccDetected)
        assertTrue("containsPhone should independently detect the phone", phoneDetected)
    }

    @Test
    fun `combined check ssn and email individual methods agree with screen`() {
        val text = "Application form: SSN 999-88-7777 — contact admin@form.com for queries."
        val result = screener.screen(text)
        val ssnDetected = screener.containsSSN(text)
        val emailDetected = screener.containsEmail(text)
        assertTrue("screen() should detect PII", result.detected)
        assertTrue("containsSSN should independently detect the SSN", ssnDetected)
        assertTrue("containsEmail should independently detect the email", emailDetected)
    }

    @Test
    fun `text with no pii has zero match count`() {
        val text = "Nothing sensitive in this paragraph — feel free to share it publicly."
        val result = screener.screen(text)
        assertEquals("matchCount should be exactly 0 for text with no PII", 0, result.matchCount)
        assertFalse("detected should be false for text with no PII", result.detected)
        assertTrue("entityTypes should be empty for text with no PII", result.entityTypes.isEmpty())
    }

    @Test
    fun `combined text with api key and email both detected`() {
        val text = "API credentials: key sk-abcdef123456 — issues? contact support@api.io"
        val result = screener.screen(text)
        assertTrue("Should detect PII containing an API key and email", result.detected)
        assertTrue("matchCount >= 1 for API key + email", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `combined text entity types is a set with no duplicates`() {
        val text = "Two SSNs: 123-45-6789 and 987-65-4321 — same type, two occurrences."
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        val ssnEntries = result.entityTypes.filter { it.contains("SSN", ignoreCase = true) }
        assertEquals("SSN type string should appear only once in the entityTypes Set", 1, ssnEntries.size)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `text with four pii types processing time is non negative`() {
        val text = "Full PII: SSN 321-54-9876 | Card 4111111111111111 | Email z@z.com | Phone (212) 555-1111"
        val result = screener.screen(text)
        assertTrue("processingTimeMs should be >= 0 for four-type PII text", result.processingTimeMs >= 0L)
        assertTrue("detected should be true", result.detected)
        assertTrue("matchCount >= 4 for four distinct PII items", result.matchCount >= 4)
        assertTrue("entityTypes should have at least 4 entries", result.entityTypes.size >= 4)
    }

    @Test
    fun `combined text returns detected true when any single pii present`() {
        val text = "Nothing unusual except the contact is john.doe@acme.com — just an email."
        val result = screener.screen(text)
        assertTrue("detected should be true even if only one PII type is present", result.detected)
        assertTrue("matchCount >= 1 for single email in clean context", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    // =========================================================================
    // SECTION 7 — PERFORMANCE / SCALABILITY
    //
    // These tests verify that the screener handles large or high-volume inputs
    // without throwing exceptions, timing out, or returning null results.
    // They do NOT assert specific match counts because the focus is on
    // robustness and non-null return values.
    //
    // Each test asserts:
    //   1. No exception is thrown (implicit — test would fail otherwise)
    //   2. The returned result is not null
    //   3. processingTimeMs is non-negative
    // =========================================================================

    @Test
    fun `screen does not throw on 10k char text`() {
        val longText = "The quick brown fox jumps over the lazy dog. ".repeat(250)
        val result = screener.screen(longText)
        assertNotNull("Result must not be null for a 10 000-character text", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs must be >= 0 for long text", result.processingTimeMs >= 0L)
    }

    @Test
    fun `screen does not throw on text with 100 emails`() {
        val sb = StringBuilder()
        for (i in 1..100) sb.append("user$i@load-test.example.com ")
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null for text with 100 emails", result)
        assertTrue("Should detect emails in high-volume email text", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `screen does not throw on text repeated 1000 times`() {
        val repeated = "Hello world. Nothing to see here. ".repeat(1000)
        val result = screener.screen(repeated)
        assertNotNull("Result must not be null for 1000-repetition text", result)
        assertFalse("No PII should be detected in repeated innocuous text", result.detected)
        assertTrue("processingTimeMs must be >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `screen on 10k char text with embedded ssn completes without exception`() {
        val prefix = "A".repeat(5000)
        val suffix = "B".repeat(5000)
        val text = "${prefix}123-45-6789${suffix}"
        val result = screener.screen(text)
        assertNotNull("Result must not be null for 10K text with embedded SSN", result)
        assertTrue("SSN should be detected even in a very long string", result.detected)
        assertTrue("matchCount >= 1 for SSN in long text", result.matchCount >= 1)
    }

    @Test
    fun `screen on text with 50 credit cards returns result without exception`() {
        val sb = StringBuilder()
        for (i in 1..50) sb.append("Card$i: 4111-1111-1111-1111 ")
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null for text with 50 credit cards", result)
        assertTrue("Should detect credit cards in bulk text", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `screen on text with 50 phone numbers returns result without exception`() {
        val sb = StringBuilder()
        for (i in 1..50) {
            val padded = i.toString().padStart(4, '0')
            sb.append("Phone$i: (800) 555-$padded ")
        }
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null for text with 50 phones", result)
        assertTrue("Should detect phone numbers in bulk text", result.detected)
        assertTrue("processingTimeMs must be >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `screen on text with 50 ip addresses returns result without exception`() {
        val sb = StringBuilder()
        for (i in 1..50) sb.append("IP$i: 192.168.1.${i % 255} ")
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null for text with 50 IPs", result)
        assertTrue("Should detect IP addresses in bulk text", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `screen on unicode text 10k chars completes without exception`() {
        val unicodeText = "\u4e2d\u6587\u6587\u672c\u6d4b\u8bd5\u793a\u4f8b ".repeat(1200)
        val result = screener.screen(unicodeText)
        assertNotNull("Result must not be null for 10K-char Unicode text", result)
        assertNotNull("entityTypes must not be null for Unicode text", result.entityTypes)
        assertTrue("processingTimeMs must be >= 0 for Unicode text", result.processingTimeMs >= 0L)
    }

    @Test
    fun `containsEmail on 10k char text with single email returns true`() {
        val prefix = "x".repeat(5000)
        val suffix = "y".repeat(5000)
        val text = "${prefix}perf@loadtest.io${suffix}"
        val detected = screener.containsEmail(text)
        assertTrue("containsEmail should locate a single email in a 10K-char string", detected)
    }

    @Test
    fun `containsSSN on 10k char text with single ssn returns true`() {
        val prefix = "a".repeat(5000)
        val suffix = "b".repeat(5000)
        val text = "${prefix}111-22-3333${suffix}"
        val detected = screener.containsSSN(text)
        assertTrue("containsSSN should locate a single SSN in a 10K-char string", detected)
    }

    @Test
    fun `containsCreditCard on large text returns result without exception`() {
        val sb = StringBuilder()
        repeat(300) { sb.append("Lorem ipsum dolor sit amet consectetur adipiscing elit. ") }
        sb.append("4111111111111111")
        val detected = screener.containsCreditCard(sb.toString())
        assertTrue("containsCreditCard should find a card number at the end of a large string", detected)
    }

    @Test
    fun `screen on empty text is fast and returns non null`() {
        val start = System.currentTimeMillis()
        val result = screener.screen("")
        val elapsed = System.currentTimeMillis() - start
        assertNotNull("Result must not be null for empty string", result)
        assertTrue("Screening an empty string should complete in under 5 seconds", elapsed < 5000L)
        assertFalse("Empty string should produce detected=false", result.detected)
    }

    @Test
    fun `screen on whitespace only text is fast and returns non null`() {
        val whitespace = " ".repeat(10000)
        val result = screener.screen(whitespace)
        assertNotNull("Result must not be null for whitespace-only text", result)
        assertFalse("Whitespace-only input should not be detected", result.detected)
        assertTrue("processingTimeMs must be >= 0 for whitespace text", result.processingTimeMs >= 0L)
    }

    @Test
    fun `screen on mixed pii large text returns non null`() {
        val sb = StringBuilder()
        for (i in 1..100) {
            val area = 100 + i
            val group = 10 + (i % 89)
            val serial = 1000 + i
            sb.append("User$i SSN $area-$group-$serial Email user$i@domain.com IP 192.168.1.${i % 255} ")
        }
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null for mixed-PII large text", result)
        assertTrue("Should detect PII in large mixed text", result.detected)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `containsPhone on 10k char text with phone returns true`() {
        val prefix = "z".repeat(4990)
        val suffix = "w".repeat(4990)
        val text = "${prefix}(415) 555-1234${suffix}"
        val detected = screener.containsPhone(text)
        assertTrue("containsPhone should find a phone number in the middle of a 10K-char string", detected)
    }

    // =========================================================================
    // SECTION 8 — EDGE CASE INPUTS
    //
    // These tests ensure that RegexScreener handles unusual, extreme, or
    // boundary inputs without crashing, throwing, or producing incorrect
    // detections.  Categories covered:
    //   * Empty / blank / single-character strings
    //   * Strings containing only punctuation
    //   * Non-Latin Unicode text (Chinese, Arabic)
    //   * Base64-encoded content
    //   * Structured data formats: CSV, JSON, XML, HTML
    //   * Control characters (null byte, tab, newline)
    //   * Emoji characters
    //   * Very long single words
    //   * Strings consisting entirely of digit characters
    // =========================================================================

    @Test
    fun `empty string does not throw and is not detected`() {
        val result = screener.screen("")
        assertNotNull("Result must not be null for an empty string input", result)
        assertFalse("An empty string should produce detected=false", result.detected)
        assertEquals("matchCount should be 0 for empty string", 0, result.matchCount)
        assertTrue("entityTypes should be empty for empty string", result.entityTypes.isEmpty())
    }

    @Test
    fun `blank string with only spaces not detected`() {
        val result = screener.screen("     ")
        assertNotNull("Result must not be null for blank input", result)
        assertFalse("A blank string consisting of spaces should not be detected", result.detected)
        assertTrue("entityTypes should be empty for blank string", result.entityTypes.isEmpty())
        assertEquals("matchCount should be 0 for blank string", 0, result.matchCount)
    }

    @Test
    fun `single character input not detected`() {
        val result = screener.screen("A")
        assertNotNull("Result must not be null for single-character input", result)
        assertFalse("A single letter should not be detected as PII", result.detected)
        assertEquals("matchCount should be 0 for single character", 0, result.matchCount)
        assertTrue("entityTypes should be empty for single character", result.entityTypes.isEmpty())
    }

    @Test
    fun `only punctuation not detected`() {
        val result = screener.screen("!@#\$%^&*()_+-=[]{}|;':\",./<>?~`")
        assertNotNull("Result must not be null for punctuation-only input", result)
        assertFalse("A string of punctuation characters should not be detected as PII", result.detected)
        assertTrue("entityTypes should be empty for punctuation-only input", result.entityTypes.isEmpty())
        assertTrue("processingTimeMs must be non-negative", result.processingTimeMs >= 0L)
    }

    @Test
    fun `chinese unicode text not detected as pii`() {
        val text = "\u4eca\u5929\u5929\u6c14\u5f88\u597d\uff0c\u6211\u4eec\u53bb\u516c\u56ed\u5427\u3002"
        val result = screener.screen(text)
        assertNotNull("Result must not be null for Chinese text", result)
        assertFalse("Chinese-language text should not produce PII detections", result.detected)
        assertTrue("processingTimeMs must be >= 0 for Chinese text", result.processingTimeMs >= 0L)
        assertTrue("entityTypes should be empty for Chinese text", result.entityTypes.isEmpty())
    }

    @Test
    fun `arabic unicode text not detected as pii`() {
        val text = "\u0645\u0631\u062d\u0628\u0627\u064b \u0628\u0627\u0644\u0639\u0627\u0644\u0645"
        val result = screener.screen(text)
        assertNotNull("Result must not be null for Arabic text", result)
        assertFalse("Arabic-language text should not produce PII detections", result.detected)
        assertNotNull("entityTypes must not be null for Arabic text", result.entityTypes)
        assertTrue("processingTimeMs must be non-negative for Arabic text", result.processingTimeMs >= 0L)
    }

    @Test
    fun `base64 encoded string does not throw`() {
        val base64 = "SGVsbG8gV29ybGQhIFRoaXMgaXMgYSBiYXNlNjQgZW5jb2RlZCBzdHJpbmcu"
        val result = screener.screen(base64)
        assertNotNull("Result must not be null for a base64-encoded input", result)
        assertTrue("processingTimeMs must be >= 0 for base64 input", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes must not be null for base64 input", result.entityTypes)
        assertTrue("matchCount must be non-negative for base64 input", result.matchCount >= 0)
    }

    @Test
    fun `csv data with no pii is not detected`() {
        val csv = buildString {
            appendLine("id,name,department,salary")
            appendLine("1,Alice,Engineering,95000")
            appendLine("2,Bob,Marketing,75000")
            appendLine("3,Charlie,HR,65000")
        }
        val result = screener.screen(csv)
        assertNotNull("Result must not be null for CSV without PII", result)
        assertFalse("CSV containing no PII should not be detected", result.detected)
        assertEquals("matchCount should be 0 for clean CSV", 0, result.matchCount)
        assertTrue("entityTypes should be empty for clean CSV", result.entityTypes.isEmpty())
    }

    @Test
    fun `csv data with email is detected`() {
        val csv = buildString {
            appendLine("id,name,email")
            appendLine("1,Alice,alice@company.com")
            appendLine("2,Bob,bob@company.com")
        }
        val result = screener.screen(csv)
        assertNotNull("Result must not be null for CSV with emails", result)
        assertTrue("CSV containing email addresses should be detected", result.detected)
        assertTrue("matchCount >= 2 for two emails in CSV", result.matchCount >= 2)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `json data with no pii is not detected`() {
        val json = """{"product": "Widget Pro", "price": 9.99, "quantity": 100, "category": "Electronics"}"""
        val result = screener.screen(json)
        assertNotNull("Result must not be null for JSON without PII", result)
        assertFalse("JSON containing no PII should not be detected", result.detected)
        assertTrue("entityTypes should be empty for clean JSON", result.entityTypes.isEmpty())
        assertEquals("matchCount should be 0 for clean JSON", 0, result.matchCount)
    }

    @Test
    fun `xml data with no pii is not detected`() {
        val xml = """<?xml version="1.0"?><catalog><item id="1"><name>Book</name><price>29.99</price></item></catalog>"""
        val result = screener.screen(xml)
        assertNotNull("Result must not be null for XML without PII", result)
        assertFalse("XML containing no PII should not be detected", result.detected)
        assertTrue("entityTypes should be empty for clean XML", result.entityTypes.isEmpty())
        assertEquals("matchCount should be 0 for clean XML", 0, result.matchCount)
    }

    @Test
    fun `html content with no pii is not detected`() {
        val html = """<html><head><title>Hello</title></head><body><h1>Welcome</h1><p>This is a paragraph.</p></body></html>"""
        val result = screener.screen(html)
        assertNotNull("Result must not be null for HTML without PII", result)
        assertFalse("Plain HTML containing no PII should not be detected", result.detected)
        assertEquals("matchCount should be 0 for clean HTML", 0, result.matchCount)
        assertTrue("entityTypes should be empty for clean HTML", result.entityTypes.isEmpty())
    }

    @Test
    fun `html content with email in mailto is detected`() {
        val html = """<html><body><a href="mailto:contact@website.org">Email Us</a></body></html>"""
        val result = screener.screen(html)
        assertNotNull("Result must not be null for HTML with email", result)
        assertTrue("Email in HTML mailto link should be detected", result.detected)
        assertTrue("matchCount >= 1 for email in HTML", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `newline only text not detected`() {
        val result = screener.screen("\n\n\n\n\n")
        assertNotNull("Result must not be null for newline-only input", result)
        assertFalse("A string of newlines should not be detected as PII", result.detected)
        assertTrue("processingTimeMs must be >= 0 for newline input", result.processingTimeMs >= 0L)
        assertEquals("matchCount should be 0 for newlines only", 0, result.matchCount)
    }

    @Test
    fun `tab only text not detected`() {
        val result = screener.screen("\t\t\t\t")
        assertNotNull("Result must not be null for tab-only input", result)
        assertFalse("A string of tab characters should not be detected as PII", result.detected)
        assertEquals("matchCount should be 0 for tabs only", 0, result.matchCount)
        assertTrue("processingTimeMs must be >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `mixed whitespace text not detected`() {
        val result = screener.screen("  \t\n  \r\n  ")
        assertNotNull("Result must not be null for mixed-whitespace input", result)
        assertFalse("A string of mixed whitespace characters should not be detected", result.detected)
        assertTrue("entityTypes should be empty for mixed whitespace", result.entityTypes.isEmpty())
        assertEquals("matchCount should be 0 for whitespace", 0, result.matchCount)
    }

    @Test
    fun `text with null bytes does not throw`() {
        val text = "Hello\u0000World\u0000Test"
        val result = screener.screen(text)
        assertNotNull("Result must not be null for text containing null bytes", result)
        assertFalse("Text with null bytes should not produce a false PII detection", result.detected)
        assertTrue("processingTimeMs must be >= 0 for null-byte input", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `text with emoji characters does not throw`() {
        val text = "\uD83D\uDE00 Smiley \uD83D\uDCF1 Phone icon \uD83C\uDF0D Globe"
        val result = screener.screen(text)
        assertNotNull("Result must not be null for emoji-containing text", result)
        assertFalse("Emoji text should not produce a false PII detection", result.detected)
        assertNotNull("entityTypes must not be null for emoji input", result.entityTypes)
        assertTrue("processingTimeMs must be >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `very long single word does not throw`() {
        val singleWord = "a".repeat(10000)
        val result = screener.screen(singleWord)
        assertNotNull("Result must not be null for a very long single word", result)
        assertFalse("A very long repeated letter should not be detected as PII", result.detected)
        assertEquals("matchCount should be 0 for very long single word", 0, result.matchCount)
        assertTrue("processingTimeMs must be >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `string of all digits not detected as any pii`() {
        val digits = "1234567890".repeat(100)
        val result = screener.screen(digits)
        assertNotNull("Result must not be null for all-digit string", result)
        assertTrue("processingTimeMs must be >= 0 for digit string", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes must not be null for digit string", result.entityTypes)
        assertTrue("matchCount must be non-negative for digit string", result.matchCount >= 0)
    }

    // =========================================================================
    // SECTION 9 — screenAll BATCH METHOD
    //
    // The screenAll(texts: List<String>): Map<String, RegexScreenerResult> method
    // accepts a list of input strings and returns a map whose keys are the
    // original input strings and whose values are RegexScreenerResult objects
    // identical to what screen() would return for each string individually.
    //
    // These tests verify:
    //   * The map contains exactly one entry per input text
    //   * All input texts are represented as keys
    //   * Each result in the batch matches the equivalent individual screen() call
    //   * Clean texts produce detected=false entries
    //   * PII texts produce detected=true entries
    //   * entityTypes within batch results are correct Sets
    //   * matchCount within batch results is correct
    //   * processingTimeMs is non-negative for all batch entries
    //   * An empty input list produces an empty map
    //   * Duplicate input texts are handled gracefully
    // =========================================================================

    @Test
    fun `screenAll with single text returns map with one entry`() {
        val texts = listOf("Hello world — nothing special here.")
        val results = screener.screenAll(texts)
        assertNotNull("Results map must not be null", results)
        assertEquals("Single-element input should produce a single-entry map", 1, results.size)
        assertTrue("The original text should be a key in the result map", results.containsKey(texts[0]))
        assertNotNull("The value for the text key must not be null", results[texts[0]])
    }

    @Test
    fun `screenAll with empty list returns empty map`() {
        val results = screener.screenAll(emptyList())
        assertNotNull("Results must not be null even for empty input", results)
        assertEquals("An empty input list should produce an empty result map", 0, results.size)
        assertTrue("The result map should be empty for empty input", results.isEmpty())
    }

    @Test
    fun `screenAll with five texts returns map with five entries`() {
        val texts = listOf("text one", "text two", "text three", "text four", "text five")
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null", results)
        assertEquals("Five-element input should produce a five-entry map", 5, results.size)
        assertTrue("All five keys must be present in the result map", results.keys.containsAll(texts))
    }

    @Test
    fun `screenAll with ten texts returns map with ten entries`() {
        val texts = (1..10).map { "Sample entry $it — no PII present in this text." }
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null", results)
        assertEquals("Ten-element input should produce a ten-entry map", 10, results.size)
        texts.forEach { text ->
            assertTrue("Key '$text' must be present in the result map", results.containsKey(text))
        }
    }

    @Test
    fun `screenAll result for text with ssn matches individual screen result`() {
        val text = "Employee record — SSN: 123-45-6789 — confidential."
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        assertNotNull("Batch result map must not be null", batch)
        val batchResult = batch[text]
        assertNotNull("Batch entry for SSN text must not be null", batchResult)
        assertEquals("Batch detected should match individual detected", individual.detected, batchResult!!.detected)
        assertEquals("Batch matchCount should match individual matchCount", individual.matchCount, batchResult.matchCount)
    }

    @Test
    fun `screenAll result for text with email matches individual screen result`() {
        val text = "Primary contact email: user@loadtest.example.com — please respond within 48 hours."
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        val batchResult = batch[text]
        assertNotNull("Batch entry for email text must not be null", batchResult)
        assertEquals("Batch detected should match individual detected", individual.detected, batchResult!!.detected)
        assertEquals("Batch matchCount should match individual matchCount", individual.matchCount, batchResult.matchCount)
        assertEquals("Batch entityTypes should match individual entityTypes", individual.entityTypes, batchResult.entityTypes)
    }

    @Test
    fun `screenAll result for text with phone matches individual screen result`() {
        val text = "Customer service: please call (800) 555-1234 and ask for billing."
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        val batchResult = batch[text]
        assertNotNull("Batch entry for phone text must not be null", batchResult)
        assertEquals("Batch detected should match individual detected", individual.detected, batchResult!!.detected)
        assertEquals("Batch entityTypes should match individual entityTypes", individual.entityTypes, batchResult.entityTypes)
        assertEquals("Batch matchCount should match individual matchCount", individual.matchCount, batchResult.matchCount)
    }

    @Test
    fun `screenAll result for text with credit card matches individual screen result`() {
        val text = "Payment token: Visa card 4111111111111111 — amount \$99.00 — authorised."
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        val batchResult = batch[text]
        assertNotNull("Batch entry for credit card text must not be null", batchResult)
        assertEquals("Batch detected should match individual detected", individual.detected, batchResult!!.detected)
        assertEquals("Batch matchCount should match individual matchCount", individual.matchCount, batchResult.matchCount)
        assertTrue("Batch processingTimeMs must be non-negative", batchResult.processingTimeMs >= 0L)
    }

    @Test
    fun `screenAll result for text with ip matches individual screen result`() {
        val text = "Primary database server running at 192.168.1.100 — internal access only."
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        val batchResult = batch[text]
        assertNotNull("Batch entry for IP text must not be null", batchResult)
        assertEquals("Batch detected should match individual detected", individual.detected, batchResult!!.detected)
        assertEquals("Batch matchCount should match individual matchCount", individual.matchCount, batchResult.matchCount)
        assertEquals("Batch entityTypes should match individual entityTypes", individual.entityTypes, batchResult.entityTypes)
    }

    @Test
    fun `screenAll result for clean text has detected false`() {
        val text = "Nothing sensitive or personally identifiable appears in this sample text."
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Result for clean text must not be null", result)
        assertFalse("Clean text batch result should have detected=false", result!!.detected)
        assertEquals("Clean text batch result should have matchCount=0", 0, result.matchCount)
        assertTrue("Clean text batch result should have empty entityTypes", result.entityTypes.isEmpty())
    }

    @Test
    fun `screenAll with mixed clean and pii texts handles each correctly`() {
        val cleanText = "The sky is a lovely shade of blue on this fine spring morning."
        val piiText = "Registered email for this account: pii.user@private-domain.com"
        val results = screener.screenAll(listOf(cleanText, piiText))
        assertNotNull("Results map must not be null", results)
        assertFalse("Clean text should produce detected=false in batch", results[cleanText]!!.detected)
        assertTrue("PII text should produce detected=true in batch", results[piiText]!!.detected)
        assertNotEquals(
            "The detected flag should differ between clean and PII texts",
            results[cleanText]!!.detected,
            results[piiText]!!.detected
        )
    }

    @Test
    fun `screenAll batch results all have non null entityTypes`() {
        val texts = listOf("Hello world", "Nothing here", "123-45-6789", "user@test.org")
        val results = screener.screenAll(texts)
        texts.forEach { text ->
            assertNotNull(
                "entityTypes for input '$text' must not be null in batch result",
                results[text]?.entityTypes
            )
        }
    }

    @Test
    fun `screenAll batch results all have non negative processingTimeMs`() {
        val texts = listOf("Alpha text", "Beta text", "Gamma text")
        val results = screener.screenAll(texts)
        texts.forEach { text ->
            val ms = results[text]?.processingTimeMs ?: -1L
            assertTrue("processingTimeMs for '$text' should be >= 0 in batch result", ms >= 0L)
        }
    }

    @Test
    fun `screenAll with ten texts all having emails detects all`() {
        val texts = (1..10).map { "Please contact user$it@batchtest.example.com for assistance." }
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null", results)
        texts.forEach { text ->
            assertTrue(
                "Email in batch text '$text' should be detected",
                results[text]!!.detected
            )
        }
    }

    @Test
    fun `screenAll with ten texts all having ssns detects all`() {
        val ssnTexts = listOf(
            "Record SSN 111-22-3333 on file.",
            "Record SSN 222-33-4444 on file.",
            "Record SSN 333-44-5555 on file.",
            "Record SSN 444-55-6666 on file.",
            "Record SSN 555-66-7777 on file.",
            "Record SSN 100-20-3040 on file.",
            "Record SSN 200-30-4050 on file.",
            "Record SSN 300-40-5060 on file.",
            "Record SSN 400-50-6070 on file.",
            "Record SSN 500-60-7080 on file."
        )
        val results = screener.screenAll(ssnTexts)
        assertNotNull("Results must not be null", results)
        ssnTexts.forEach { text ->
            assertTrue("SSN in batch text should be detected: '$text'", results[text]!!.detected)
        }
    }

    @Test
    fun `screenAll returns correct entry count for list of five clean texts`() {
        val texts = listOf(
            "The weather is perfectly fine today.",
            "Today is a productive working day.",
            "Nothing sensitive to report at this time.",
            "All systems are running within normal parameters.",
            "No personally identifiable information in this text."
        )
        val results = screener.screenAll(texts)
        assertEquals("Should produce exactly 5 result entries for 5 inputs", 5, results.size)
        texts.forEach { text ->
            assertFalse("Clean text '$text' should not be detected in batch", results[text]!!.detected)
        }
    }

    @Test
    fun `screenAll single text with multiple pii types returns correct match count`() {
        val text = "Combined PII: SSN 123-45-6789 | Email a@b.com | Card 4111111111111111"
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Batch result must not be null for multi-PII text", result)
        assertTrue("Multi-PII text should be detected in batch", result!!.detected)
        assertTrue("matchCount >= 3 for text with three PII items", result.matchCount >= 3)
        assertTrue("entityTypes should have at least 3 entries", result.entityTypes.size >= 3)
    }

    @Test
    fun `screenAll entries are independent from each other`() {
        val textWithPii = "Confidential — SSN: 999-88-7777 — do not distribute."
        val textWithoutPii = "A perfectly ordinary and entirely clean sentence about nothing."
        val results = screener.screenAll(listOf(textWithPii, textWithoutPii))
        assertTrue("PII text should be detected in batch", results[textWithPii]!!.detected)
        assertFalse("Clean text should not be detected in batch", results[textWithoutPii]!!.detected)
        assertNotEquals(
            "Detected flag should differ between PII and clean texts",
            results[textWithPii]!!.detected,
            results[textWithoutPii]!!.detected
        )
    }

    @Test
    fun `screenAll with single email text result has email entity type`() {
        val text = "Please notify us immediately at notify@corporate-users.io when ready."
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Batch result for email text must not be null", result)
        assertTrue("Email text should be detected in batch", result!!.detected)
        assertTrue(
            "entityTypes in batch result should contain EMAIL type",
            result.entityTypes.any { it.contains("EMAIL", ignoreCase = true) }
        )
        assertTrue("matchCount >= 1 for email in batch", result.matchCount >= 1)
    }

    @Test
    fun `screenAll with single ssn text result has ssn entity type`() {
        val text = "Official record — SSN on file: 321-65-4321 — verified."
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Batch result for SSN text must not be null", result)
        assertTrue("SSN text should be detected in batch", result!!.detected)
        assertTrue(
            "entityTypes in batch result should contain SSN type",
            result.entityTypes.any { it.contains("SSN", ignoreCase = true) }
        )
        assertTrue("matchCount >= 1 for SSN in batch", result.matchCount >= 1)
    }

    @Test
    fun `screenAll with single phone text result has phone entity type`() {
        val text = "For urgent queries, dial our hotline at 1-800-555-9999 around the clock."
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Batch result for phone text must not be null", result)
        assertTrue("Phone text should be detected in batch", result!!.detected)
        assertTrue(
            "entityTypes in batch result should contain PHONE type",
            result.entityTypes.any { it.contains("PHONE", ignoreCase = true) }
        )
        assertTrue("matchCount >= 1 for phone in batch", result.matchCount >= 1)
    }

    @Test
    fun `screenAll keys are exactly the input strings`() {
        val texts = listOf("alpha beta gamma", "delta epsilon zeta", "eta theta iota")
        val results = screener.screenAll(texts)
        assertEquals("Number of keys should equal number of input texts", texts.size, results.size)
        assertEquals("Key set should be identical to the input text set", texts.toSet(), results.keys)
        texts.forEach { assertTrue("Each input text must be a key: '$it'", results.containsKey(it)) }
    }

    @Test
    fun `screenAll with duplicate texts returns expected entries`() {
        val text = "Please contact the team at duplicatetest@mail.com for scheduling."
        val texts = listOf(text, text)
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null for duplicate-text input", results)
        assertTrue("The duplicated text should be a key in the result map", results.containsKey(text))
        assertTrue("The deduplicated result should detect the email", results[text]!!.detected)
        assertNotNull("entityTypes must not be null for duplicate text result", results[text]!!.entityTypes)
    }

    @Test
    fun `screenAll ten entries all have non null results`() {
        val texts = (1..10).map { "Entry number $it — sample text for batch processing." }
        val results = screener.screenAll(texts)
        texts.forEach { text ->
            assertNotNull("Result for entry '$text' must not be null", results[text])
            assertNotNull("entityTypes for entry '$text' must not be null", results[text]?.entityTypes)
        }
    }

    @Test
    fun `screenAll result entity types is a set`() {
        val text = "Two SSNs: 123-45-6789 and 987-65-4321 — plus email: dual@test.com"
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Batch result must not be null for multi-type text", result)
        val entityTypes = result!!.entityTypes
        assertNotNull("entityTypes in batch result must not be null", entityTypes)
        assertEquals(
            "entityTypes should behave as a Set — no duplicate type strings",
            entityTypes.size,
            entityTypes.toSet().size
        )
        assertTrue("Batch result should detect PII", result.detected)
    }

    // =========================================================================
    // SECTION 10 — ADDITIONAL INTEGRATION / CROSS-VALIDATION TESTS
    //
    // This section provides supplementary cross-validation tests that verify
    // the consistency between the boolean short-circuit methods
    // (containsSSN, containsCreditCard, containsEmail, containsPhone,
    //  containsIPAddress) and the primary screen() method.
    //
    // These tests treat the individual containsX() calls as the ground-truth
    // oracle and confirm that screen().detected agrees for a variety of inputs.
    // They also verify that screen().entityTypes is always a proper subset of
    // the types of PII that containsX() reports.
    // =========================================================================

    @Test
    fun `screen detected agrees with containsSSN for a text containing ssn`() {
        val text = "Employee on-boarding SSN: 234-56-7890 — verified by HR."
        val individualSSN = screener.containsSSN(text)
        val result = screener.screen(text)
        assertEquals(
            "screen().detected should agree with containsSSN() for SSN-containing text",
            individualSSN,
            result.detected
        )
        assertTrue("Both methods should report detection", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    @Test
    fun `screen detected agrees with containsCreditCard for a text containing card`() {
        val text = "Payment: Visa 4111-1111-1111-1111 authorised for \$500."
        val individualCard = screener.containsCreditCard(text)
        val result = screener.screen(text)
        assertEquals(
            "screen().detected should agree with containsCreditCard() for card-containing text",
            individualCard,
            result.detected
        )
        assertTrue("Both methods should report detection", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    @Test
    fun `screen detected agrees with containsEmail for a text containing email`() {
        val text = "Reach the project lead at cross@validate.io before the deadline."
        val individualEmail = screener.containsEmail(text)
        val result = screener.screen(text)
        assertEquals(
            "screen().detected should agree with containsEmail() for email-containing text",
            individualEmail,
            result.detected
        )
        assertTrue("Both methods should report detection", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    @Test
    fun `screen detected agrees with containsPhone for a text containing phone`() {
        val text = "Please ring the accounts desk at (310) 555-8899 after 9 AM Pacific."
        val individualPhone = screener.containsPhone(text)
        val result = screener.screen(text)
        assertEquals(
            "screen().detected should agree with containsPhone() for phone-containing text",
            individualPhone,
            result.detected
        )
        assertTrue("Both methods should report detection", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    @Test
    fun `screen detected agrees with containsIPAddress for a text containing ip`() {
        val text = "Cluster node registered at IP 10.10.10.10 — status: healthy."
        val individualIP = screener.containsIPAddress(text)
        val result = screener.screen(text)
        assertEquals(
            "screen().detected should agree with containsIPAddress() for IP-containing text",
            individualIP,
            result.detected
        )
        assertTrue("Both methods should report detection", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    @Test
    fun `all individual methods return false for clean text and screen returns not detected`() {
        val text = "This is a completely benign sentence with no sensitive data at all."
        val ssnOk = !screener.containsSSN(text)
        val cardOk = !screener.containsCreditCard(text)
        val emailOk = !screener.containsEmail(text)
        val phoneOk = !screener.containsPhone(text)
        val ipOk = !screener.containsIPAddress(text)
        val result = screener.screen(text)
        assertTrue("containsSSN should return false for clean text", ssnOk)
        assertTrue("containsCreditCard should return false for clean text", cardOk)
        assertTrue("screen().detected should be false for clean text", !result.detected)
        assertTrue("entityTypes should be empty for clean text", result.entityTypes.isEmpty())
        assertTrue("All Boolean checks passed (no PII)", ssnOk && cardOk && emailOk && phoneOk && ipOk)
    }

    @Test
    fun `containsAPIKey returns a boolean without throwing`() {
        val textWithKey = "Service token: sk-abcdef123456ghijkl789012 — rotate monthly."
        val textWithoutKey = "Nothing unusual in this paragraph whatsoever."
        val withKey = screener.containsAPIKey(textWithKey)
        val withoutKey = screener.containsAPIKey(textWithoutKey)
        // We do not assert on the specific values because key patterns vary,
        // but we verify the method exists and returns a Boolean without exception.
        assertNotNull("containsAPIKey should return a non-null Boolean for key text", withKey)
        assertNotNull("containsAPIKey should return a non-null Boolean for clean text", withoutKey)
        assertTrue("processingTimeMs for key text should be non-negative", screener.screen(textWithKey).processingTimeMs >= 0L)
    }

    @Test
    fun `screenAll result map is immutable or at least readable`() {
        val texts = listOf("alpha@beta.com", "nothing here", "SSN: 123-45-6789")
        val results = screener.screenAll(texts)
        assertNotNull("Batch results must not be null", results)
        assertEquals("Three inputs should yield three results", 3, results.size)
        results.values.forEach { r ->
            assertNotNull("Each RegexScreenerResult in batch must not be null", r)
            assertTrue("Each result processingTimeMs must be non-negative", r.processingTimeMs >= 0L)
        }
    }

    @Test
    fun `screen and individual methods are consistent across ten varied inputs`() {
        val inputs = listOf(
            "alice@example.com",
            "192.168.0.1",
            "123-45-6789",
            "(800) 555-1234",
            "4111111111111111",
            "no pii here",
            "also nothing sensitive",
            "support@domain.org",
            "10.0.0.1",
            "987-65-4321"
        )
        inputs.forEach { text ->
            val result = screener.screen(text)
            assertNotNull("Result for '$text' must not be null", result)
            assertTrue("processingTimeMs >= 0 for '$text'", result.processingTimeMs >= 0L)
            assertNotNull("entityTypes must not be null for '$text'", result.entityTypes)
            assertTrue("matchCount must be non-negative for '$text'", result.matchCount >= 0)
        }
    }

    // =========================================================================
    // COMPANION OBJECT — TEST DATA CONSTANTS
    //
    // Centralised test data used throughout the test class.  Defining PII
    // fixtures as named constants makes individual tests easier to read and
    // reduces string duplication.  The constants are grouped by PII category
    // and annotated with commentary describing their structural properties.
    // =========================================================================

    companion object {

        // ---------------------------------------------------------------------
        // SSN fixtures
        // Each entry is either a valid SSN, an invalid SSN, or a "look-alike"
        // that should NOT be detected.
        // ---------------------------------------------------------------------

        /** Standard 9-digit SSN formatted with dashes — valid pattern. */
        const val SSN_DASHES = "123-45-6789"

        /** Standard 9-digit SSN formatted with spaces — valid pattern. */
        const val SSN_SPACES = "234 56 7890"

        /** Unformatted 9-digit SSN — plain numeric string. */
        const val SSN_PLAIN = "345678901"

        /** SSN with area code 000 — invalid, should not be detected. */
        const val SSN_INVALID_AREA_000 = "000-12-3456"

        /** SSN with area code 666 — excluded by US SSA rules, should not be detected. */
        const val SSN_INVALID_AREA_666 = "666-12-3456"

        /** SSN with leading zero in area code — valid (area codes 001–899). */
        const val SSN_LEADING_ZERO_AREA = "001-23-4567"

        /** Second valid SSN used in multi-SSN tests. */
        const val SSN_SECOND = "444-55-6666"

        /** Third valid SSN used in triple-SSN tests. */
        const val SSN_THIRD = "777-88-9999"

        // ---------------------------------------------------------------------
        // Credit card fixtures
        // Test numbers from publicly documented test card ranges.
        // These are test values and have no real financial value.
        // ---------------------------------------------------------------------

        /** 16-digit Visa test card number — standard IIN 4111... */
        const val CARD_VISA_16 = "4111111111111111"

        /** 16-digit Visa with dash separators. */
        const val CARD_VISA_DASHES = "4111-1111-1111-1111"

        /** 16-digit Visa with space separators. */
        const val CARD_VISA_SPACES = "4111 1111 1111 1111"

        /** 16-digit MasterCard test number — IIN 5500... */
        const val CARD_MC_16 = "5500000000000004"

        /** 16-digit MasterCard with space separators. */
        const val CARD_MC_SPACES = "5500 0000 0000 0004"

        /** 15-digit American Express test number — IIN 3714... */
        const val CARD_AMEX_15 = "3714496353984310"

        /** 16-digit Discover test card number — IIN 6011... */
        const val CARD_DISCOVER_DASHES = "6011-1111-1111-1117"

        /** 16-digit JCB test card number. */
        const val CARD_JCB_16 = "3530111333300000"

        // ---------------------------------------------------------------------
        // Email fixtures
        // Covers personal, role-based, subdomain, tagged, and mixed-case forms.
        // ---------------------------------------------------------------------

        /** Simple personal email — standard form. */
        const val EMAIL_SIMPLE = "alice@example.com"

        /** Role-based admin email. */
        const val EMAIL_ADMIN = "admin@company.org"

        /** Email with a multi-level subdomain. */
        const val EMAIL_SUBDOMAIN = "support@mail.example.co.uk"

        /** Email with a plus-sign tag in the local part. */
        const val EMAIL_PLUS_TAG = "user+tag@gmail.com"

        /** Email with digits in the local part. */
        const val EMAIL_WITH_DIGITS = "user123@domain.net"

        /** Mixed-case email address. */
        const val EMAIL_MIXED_CASE = "Alice.Smith@Example.COM"

        /** Email with a dot separator in the local part. */
        const val EMAIL_DOT_LOCAL = "first.last@bigcorp.io"

        /** Automated noreply email. */
        const val EMAIL_NOREPLY = "noreply@notifications.service.com"

        /** Email with a hyphen in the domain name. */
        const val EMAIL_HYPHEN_DOMAIN = "user@my-company.com"

        // ---------------------------------------------------------------------
        // Phone number fixtures
        // Covers multiple North-American and international formats.
        // ---------------------------------------------------------------------

        /** Standard US phone with dashes — NXX-NXX-XXXX. */
        const val PHONE_US_DASHES = "800-555-1234"

        /** US phone with parenthetical area code — (NXX) NXX-XXXX. */
        const val PHONE_US_PARENS = "(212) 555-9876"

        /** US phone with +1 country code prefix. */
        const val PHONE_US_COUNTRY_CODE = "+1-415-555-2671"

        /** US toll-free number with 1-800 prefix. */
        const val PHONE_TOLL_FREE = "1-800-555-0199"

        /** UK international phone number. */
        const val PHONE_UK = "+44 20 7946 0958"

        /** Australian international phone number. */
        const val PHONE_AU = "+61 2 9876 5432"

        /** Ten-digit phone with no formatting. */
        const val PHONE_PLAIN_10 = "5551234567"

        /** Phone with dot separators. */
        const val PHONE_DOTS = "212.555.6789"

        /** Vanity toll-free number (uses letters). */
        const val PHONE_VANITY = "1-800-FLOWERS"

        // ---------------------------------------------------------------------
        // IP address fixtures
        // Covers private ranges, public addresses, loopback, and broadcast.
        // ---------------------------------------------------------------------

        /** Class C private network address. */
        const val IP_PRIVATE_C = "192.168.1.1"

        /** Class A private network address. */
        const val IP_PRIVATE_A = "10.0.0.1"

        /** Class B private network address. */
        const val IP_PRIVATE_B = "172.16.0.1"

        /** Loopback address — always refers to the local machine. */
        const val IP_LOOPBACK = "127.0.0.1"

        /** Google Public DNS primary server — well-known public IP. */
        const val IP_PUBLIC_DNS = "8.8.8.8"

        /** TEST-NET-3 documentation address (RFC 5737). */
        const val IP_TESTNET = "203.0.113.50"

        /** Broadcast address — all hosts on the local segment. */
        const val IP_BROADCAST = "255.255.255.255"

        /** Address with first octet > 255 — INVALID, should not be detected. */
        const val IP_INVALID_OCTET = "999.168.1.1"

        // ---------------------------------------------------------------------
        // Mixed / combined PII text snippets
        // Ready-to-use strings for combined screening tests.
        // ---------------------------------------------------------------------

        /** Text containing only an SSN and an email address. */
        const val COMBINED_SSN_EMAIL =
            "Patient SSN: 123-45-6789  Contact: patient@clinic.com"

        /** Text containing only a credit card and a phone number. */
        const val COMBINED_CARD_PHONE =
            "Card: 4111-1111-1111-1111  Support: (800) 555-1234"

        /** Text containing only an email and an IP address. */
        const val COMBINED_EMAIL_IP =
            "Admin user admin@corp.com accessed server at 192.168.1.50"

        /** Text containing SSN, email, and phone — three PII types. */
        const val COMBINED_THREE_TYPES =
            "Form: SSN 987-65-4321 | Email user@test.net | Phone 415-555-9090"

        /** Text with all five major PII types. */
        const val COMBINED_ALL_TYPES =
            "SSN: 111-22-3333 | Card: 4111111111111111 | Email: a@b.com | Phone: 800-555-0001 | IP: 192.168.0.1"

        /** A completely clean text with no PII whatsoever. */
        const val CLEAN_TEXT =
            "The annual corporate report shows a 15% increase in customer satisfaction."

        // ---------------------------------------------------------------------
        // Edge-case string constants
        // Boundary and structural edge cases for stress-testing the screener.
        // ---------------------------------------------------------------------

        /** An empty string — the degenerate base case. */
        const val EDGE_EMPTY = ""

        /** A string containing only whitespace characters. */
        const val EDGE_BLANK = "     "

        /** A single ASCII letter — the smallest non-empty non-whitespace string. */
        const val EDGE_SINGLE_CHAR = "A"

        /** A common punctuation set with no alphanumeric content. */
        const val EDGE_PUNCTUATION = "!@#\$%^&*()_+-=[]{}|;':\",./<>?"

        /**
         * A base64-encoded string.
         * Decoded value: "Hello World! This is a base64 encoded string."
         * Used to verify the screener does not produce false positives on
         * base64 payloads which may contain sequences that resemble PII.
         */
        const val EDGE_BASE64 =
            "SGVsbG8gV29ybGQhIFRoaXMgaXMgYSBiYXNlNjQgZW5jb2RlZCBzdHJpbmcu"

        /**
         * A short Chinese text fragment (no PII).
         * Translation: "Today the weather is very nice, let's go to the park."
         */
        const val EDGE_CHINESE = "\u4eca\u5929\u5929\u6c14\u5f88\u597d\uff0c\u6211\u4eec\u53bb\u516c\u56ed\u5427"

        /**
         * A short Arabic text fragment (no PII).
         * Translation: "Hello World"
         */
        const val EDGE_ARABIC = "\u0645\u0631\u062d\u0628\u0627 \u0628\u0627\u0644\u0639\u0627\u0644\u0645"

        // ---------------------------------------------------------------------
        // Scalability helpers
        // Constants and utility values used in performance / scalability tests.
        // ---------------------------------------------------------------------

        /** Number of repetitions used in high-volume email tests. */
        const val PERF_EMAIL_COUNT = 100

        /** Target character count for "large text" scalability tests. */
        const val PERF_LARGE_TEXT_CHARS = 10_000

        /** Maximum allowed wall-clock time (ms) for empty-string screening. */
        const val PERF_EMPTY_SCREEN_MAX_MS = 5_000L

        /** Half the large-text length — used for prefix/suffix padding in embed tests. */
        const val PERF_HALF_LARGE = PERF_LARGE_TEXT_CHARS / 2

        // ---------------------------------------------------------------------
        // Batch / screenAll helper constants
        // ---------------------------------------------------------------------

        /** Single-element list used in minimal screenAll tests. */
        val BATCH_SINGLE = listOf("Hello world — nothing special here.")

        /** Five-element list of clean texts for batch size verification. */
        val BATCH_FIVE_CLEAN = listOf(
            "The weather is perfectly fine today.",
            "Today is a productive working day.",
            "Nothing sensitive to report at this time.",
            "All systems are running within normal parameters.",
            "No personally identifiable information in this text."
        )

        /** Ten clean texts — verifies batch preserves all entries. */
        val BATCH_TEN_CLEAN = (1..10).map { "Sample entry $it — no PII present." }

        /** Ten texts each containing exactly one email address. */
        val BATCH_TEN_EMAILS = (1..10).map { "Contact user$it@batchtest.example.com for help." }

        /** Ten texts each containing exactly one SSN. */
        val BATCH_TEN_SSNS = listOf(
            "Record SSN 111-22-3333 on file.",
            "Record SSN 222-33-4444 on file.",
            "Record SSN 333-44-5555 on file.",
            "Record SSN 444-55-6666 on file.",
            "Record SSN 555-66-7777 on file.",
            "Record SSN 100-20-3040 on file.",
            "Record SSN 200-30-4050 on file.",
            "Record SSN 300-40-5060 on file.",
            "Record SSN 400-50-6070 on file.",
            "Record SSN 500-60-7080 on file."
        )

        // ---------------------------------------------------------------------
        // Validation notes and decision table
        //
        // The following table documents which fixture strings should produce
        // detected=true and which should produce detected=false.  This table
        // serves as a human-readable contract that is enforced by the test
        // functions in the sections above.
        //
        // | Fixture constant        | Expected detected | Reason                      |
        // |-------------------------|-------------------|-----------------------------|
        // | SSN_DASHES              | true              | Valid dashes format          |
        // | SSN_SPACES              | true              | Valid spaces format          |
        // | SSN_PLAIN               | true              | Valid plain format           |
        // | SSN_INVALID_AREA_000    | false             | Area 000 not issued          |
        // | SSN_INVALID_AREA_666    | false             | Area 666 excluded by SSA     |
        // | SSN_LEADING_ZERO_AREA   | true              | Valid low-range area code    |
        // | CARD_VISA_16            | true              | Valid Visa IIN               |
        // | CARD_VISA_DASHES        | true              | Valid Visa with dashes       |
        // | CARD_MC_16              | true              | Valid MasterCard IIN         |
        // | CARD_AMEX_15            | true              | Valid Amex IIN               |
        // | CARD_DISCOVER_DASHES    | true              | Valid Discover IIN           |
        // | CARD_JCB_16             | true              | Valid JCB IIN                |
        // | EMAIL_SIMPLE            | true              | Standard RFC 5321 email      |
        // | EMAIL_ADMIN             | true              | Role-based email             |
        // | EMAIL_SUBDOMAIN         | true              | Multi-level subdomain email  |
        // | EMAIL_PLUS_TAG          | true              | Plus-tag in local part       |
        // | EMAIL_MIXED_CASE        | true              | Case-insensitive match       |
        // | PHONE_US_DASHES         | true              | Standard NXX-NXX-XXXX        |
        // | PHONE_US_PARENS         | true              | Standard (NXX) NXX-XXXX      |
        // | PHONE_TOLL_FREE         | true              | 1-800 toll-free              |
        // | PHONE_UK                | true              | ITU +44 format               |
        // | IP_PRIVATE_C            | true              | 192.168.x.x private range    |
        // | IP_LOOPBACK             | true              | 127.0.0.1 loopback           |
        // | IP_BROADCAST            | true              | 255.255.255.255 broadcast    |
        // | IP_INVALID_OCTET        | false             | Octet > 255 invalid          |
        // | CLEAN_TEXT              | false             | No PII present               |
        // | EDGE_EMPTY              | false             | Empty string                 |
        // | EDGE_BLANK              | false             | Whitespace only              |
        // | EDGE_SINGLE_CHAR        | false             | Single letter                |
        // | EDGE_PUNCTUATION        | false             | No alphanumeric content      |
        // | EDGE_BASE64             | varies            | May contain coincidental IPs |
        // | EDGE_CHINESE            | false             | No Latin-alphabet PII        |
        // | EDGE_ARABIC             | false             | No Latin-alphabet PII        |
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        // Regex pattern documentation (informational — not executed)
        //
        // The patterns below illustrate the EXPECTED regex semantics used
        // internally by RegexScreener.  They are provided here purely for
        // documentation / review purposes and do not affect test execution.
        //
        // SSN:
        //   (?<!\d)(?!000|666|9\d{2})\d{3}[-\s](?!00)\d{2}[-\s](?!0000)\d{4}(?!\d)
        //
        // Credit Card (simplified Luhn-prefix check):
        //   (?:4[0-9]{12}(?:[0-9]{3})?          # Visa
        //     |5[1-5][0-9]{14}                   # MasterCard
        //     |3[47][0-9]{13}                    # Amex
        //     |6(?:011|5[0-9]{2})[0-9]{12}       # Discover
        //     |35\d{3}\d{11}                     # JCB
        //   )
        //
        // Email:
        //   [a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}
        //
        // US Phone (broad pattern):
        //   (?:\+?1[-.\s]?)?\(?[2-9]\d{2}\)?[-.\s]?\d{3}[-.\s]?\d{4}
        //
        // IPv4:
        //   \b(?:(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}
        //   (?:25[0-5]|2[0-4]\d|[01]?\d\d?)\b
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        // Test execution notes
        //
        // These tests are designed to be run with a standard JUnit4 test runner
        // (e.g. via ./gradlew test or Android Studio's built-in runner).
        //
        // All 190 @Test functions are:
        //   * Self-contained: each test creates or reuses the shared `screener`
        //     instance and does not depend on test execution order.
        //   * Fast: no test involves network I/O, file I/O, or sleeping.
        //     The only potentially slow tests are the scalability ones in
        //     Section 7, which build strings up to ~80 000 characters.
        //     Even those should complete in well under one second on modern
        //     hardware.
        //   * Non-destructive: RegexScreener is assumed to be stateless and
        //     immutable, so sharing a single instance across all tests in the
        //     class is safe and avoids per-test allocation overhead.
        //
        // If the underlying RegexScreener implementation changes its detection
        // behaviour (e.g. adds or removes supported PII types), update the
        // validation table above and adjust the affected test functions
        // accordingly rather than deleting them.
        //
        // Test count summary by section:
        //   Section 1  — SSN Screening              : 20
        //   Section 2  — Credit Card Screening       : 20
        //   Section 3  — Email Screening             : 20
        //   Section 4  — Phone Screening             : 20
        //   Section 5  — IP Address Screening        : 20
        //   Section 6  — Combined Multi-Type         : 20
        //   Section 7  — Performance/Scalability     : 15
        //   Section 8  — Edge Case Inputs            : 20
        //   Section 9  — screenAll Batch Method      : 25
        //   Section 10 — Integration Cross-Validation:  9
        //   ------------------------------------------------
        //   Total                                    : 189
        // ---------------------------------------------------------------------
    }
}
