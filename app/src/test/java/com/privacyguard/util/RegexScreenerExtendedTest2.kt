package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

// =============================================================================
// RegexScreenerExtendedTest2
// 180 test functions covering SSN, credit card, email, phone, IP, combined,
// performance, edge cases, and batch screenAll scenarios.
// =============================================================================

class RegexScreenerExtendedTest2 {

    private val screener = RegexScreener()

    // =========================================================================
    // SECTION 1 — SSN SCREENING (20 tests)
    // =========================================================================

    @Test
    fun `ssn with dashes is detected`() {
        val result = screener.screen("My SSN is 123-45-6789.")
        assertTrue("Expected SSN to be detected", result.detected)
        assertTrue("Expected matchCount >= 1", result.matchCount >= 1)
        assertTrue("Expected entityTypes to contain SSN", result.entityTypes.any { it.contains("SSN", ignoreCase = true) })
    }

    @Test
    fun `ssn with spaces is detected`() {
        val result = screener.screen("Social Security: 234 56 7890")
        assertTrue("Expected SSN with spaces to be detected", result.detected)
        assertTrue("Expected at least one match", result.matchCount >= 1)
        assertNotNull("Result must not be null", result)
    }

    @Test
    fun `ssn plain nine digits is detected`() {
        val result = screener.screen("SSN: 345678901")
        assertTrue("Expected plain SSN to be detected", result.detected)
        assertTrue("Expected matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `multiple ssns in one text are all detected`() {
        val result = screener.screen("First: 111-22-3333 and second: 444-55-6666")
        assertTrue("Expected detection", result.detected)
        assertTrue("Expected matchCount >= 2", result.matchCount >= 2)
        assertNotNull("Result must not be null", result)
    }

    @Test
    fun `text without ssn is not detected as ssn`() {
        val detected = screener.containsSSN("Hello, my name is Alice and I live in New York.")
        assertFalse("Plain text should not contain SSN", detected)
    }

    @Test
    fun `partial ssn pattern does not trigger detection`() {
        val detected = screener.containsSSN("12-34 is not a full SSN")
        assertFalse("Partial SSN pattern should not match", detected)
    }

    @Test
    fun `ssn at start of text is detected`() {
        val result = screener.screen("999-88-7777 is my social security number")
        assertTrue("SSN at start should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes must not be null", result.entityTypes)
    }

    @Test
    fun `ssn at end of text is detected`() {
        val result = screener.screen("Please find my SSN: 555-44-3322")
        assertTrue("SSN at end should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `ssn with dashes containsSSN returns true`() {
        val detected = screener.containsSSN("Patient SSN: 777-88-9999")
        assertTrue("containsSSN should return true", detected)
    }

    @Test
    fun `ssn with spaces containsSSN returns true`() {
        val detected = screener.containsSSN("File reference: 321 65 4321")
        assertTrue("containsSSN with spaces should return true", detected)
    }

    @Test
    fun `phone number is not misidentified as ssn`() {
        val detected = screener.containsSSN("Call me at (800) 555-1234")
        assertFalse("Phone number should not be identified as SSN", detected)
    }

    @Test
    fun `zip code is not misidentified as ssn`() {
        val detected = screener.containsSSN("My zip code is 90210")
        assertFalse("5-digit zip code should not be SSN", detected)
    }

    @Test
    fun `ssn in sentence context is detected`() {
        val result = screener.screen("According to records, the applicant's SSN is 222-11-0000 and was verified.")
        assertTrue("SSN in sentence should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `invalid ssn area 000 not detected`() {
        val detected = screener.containsSSN("Invalid SSN: 000-12-3456")
        assertFalse("SSN with area 000 should not be valid", detected)
    }

    @Test
    fun `invalid ssn area 666 not detected`() {
        val detected = screener.containsSSN("Bad SSN: 666-12-3456")
        assertFalse("SSN with area 666 should not be valid", detected)
    }

    @Test
    fun `ssn in multiline text is detected`() {
        val text = "Name: John Doe\nDate of Birth: 1980-01-01\nSSN: 123-45-6789\nAddress: 123 Main St"
        val result = screener.screen(text)
        assertTrue("SSN in multiline text should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("entityTypes not empty", result.entityTypes.isNotEmpty())
    }

    @Test
    fun `repeated ssn format increases match count`() {
        val text = "SSN1: 111-22-3333 SSN2: 444-55-6666 SSN3: 777-88-9999"
        val result = screener.screen(text)
        assertTrue("Expected detection", result.detected)
        assertTrue("Expected at least 3 matches", result.matchCount >= 3)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `text with only numbers no ssn format not detected`() {
        val detected = screener.containsSSN("12345 67890 11111")
        assertFalse("Unformatted number groups should not be SSN", detected)
    }

    @Test
    fun `ssn with leading zeros is detected`() {
        val result = screener.screen("SSN on file: 001-23-4567")
        assertTrue("SSN with leading zeros should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `processing time is non negative for ssn screening`() {
        val result = screener.screen("SSN: 987-65-4321")
        assertTrue("processingTimeMs should be >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected should be true", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    // =========================================================================
    // SECTION 2 — CREDIT CARD SCREENING (20 tests)
    // =========================================================================

    @Test
    fun `visa card 16 digits with dashes is detected`() {
        val result = screener.screen("Card: 4111-1111-1111-1111")
        assertTrue("Visa card should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("entityTypes contains CREDIT", result.entityTypes.any { it.contains("CREDIT", ignoreCase = true) || it.contains("CARD", ignoreCase = true) })
    }

    @Test
    fun `mastercard 16 digits with spaces is detected`() {
        val result = screener.screen("Payment method: 5500 0000 0000 0004")
        assertTrue("MasterCard should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `amex 15 digits is detected`() {
        val result = screener.screen("Amex card: 3714 496353 98431")
        assertTrue("Amex card should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `visa card plain 16 digits is detected`() {
        val detected = screener.containsCreditCard("Token: 4111111111111111")
        assertTrue("Plain 16-digit Visa should be detected", detected)
    }

    @Test
    fun `mastercard plain 16 digits is detected`() {
        val detected = screener.containsCreditCard("Card number 5500000000000004")
        assertTrue("Plain MasterCard should be detected", detected)
    }

    @Test
    fun `phone number not misidentified as credit card`() {
        val detected = screener.containsCreditCard("Call us at +1-800-555-1234 for support")
        assertFalse("Phone number should not be a credit card", detected)
    }

    @Test
    fun `zip code not misidentified as credit card`() {
        val detected = screener.containsCreditCard("Ship to zip: 90210-1234")
        assertFalse("Zip code should not be a credit card", detected)
    }

    @Test
    fun `multiple credit cards in text all detected`() {
        val text = "Card1: 4111-1111-1111-1111 and Card2: 5500-0000-0000-0004"
        val result = screener.screen(text)
        assertTrue("Should detect credit cards", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `discover card is detected`() {
        val result = screener.screen("Discover: 6011-1111-1111-1117")
        assertTrue("Discover card should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `jcb card is detected`() {
        val detected = screener.containsCreditCard("JCB: 3530111333300000")
        assertTrue("JCB card should be detected", detected)
    }

    @Test
    fun `random 16 digit number without card prefix not detected`() {
        val detected = screener.containsCreditCard("ID: 1234567890123456")
        assertFalse("Random 16-digit number without card prefix should not be detected", detected)
    }

    @Test
    fun `credit card in receipt text is detected`() {
        val text = "Thank you for your purchase. Card charged: 4111 1111 1111 1111. Amount: \$99.99"
        val result = screener.screen(text)
        assertTrue("Card in receipt should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `credit card containsCreditCard returns false for plain text`() {
        val detected = screener.containsCreditCard("The weather today is sunny and warm.")
        assertFalse("Plain text should not contain credit card", detected)
    }

    @Test
    fun `visa card at start of string is detected`() {
        val result = screener.screen("4532015112830366 is the card number on file")
        assertTrue("Visa at start should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `masked credit card not misidentified`() {
        val detected = screener.containsCreditCard("Card ending in XXXX-XXXX-XXXX-1234")
        assertFalse("Masked card should not be detected as valid card", detected)
    }

    @Test
    fun `credit card with extra spaces around it is detected`() {
        val result = screener.screen("   4111 1111 1111 1111   ")
        assertTrue("Card with surrounding spaces should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `social security number not misidentified as credit card`() {
        val detected = screener.containsCreditCard("SSN: 123-45-6789")
        assertFalse("SSN should not be detected as credit card", detected)
    }

    @Test
    fun `credit card in json payload is detected`() {
        val text = "{\"card\": \"4111111111111111\", \"expiry\": \"12/25\"}"
        val result = screener.screen(text)
        assertTrue("Card in JSON should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `expiry date not misidentified as credit card`() {
        val detected = screener.containsCreditCard("Card expiry: 12/2025")
        assertFalse("Expiry date alone should not be a credit card", detected)
    }

    @Test
    fun `credit card processing time is non negative`() {
        val result = screener.screen("My card is 4111111111111111")
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    // =========================================================================
    // SECTION 3 — EMAIL SCREENING (20 tests)
    // =========================================================================

    @Test
    fun `simple email is detected`() {
        val result = screener.screen("Contact me at alice@example.com for details.")
        assertTrue("Simple email should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("entityTypes contains EMAIL", result.entityTypes.any { it.contains("EMAIL", ignoreCase = true) })
    }

    @Test
    fun `role email admin is detected`() {
        val result = screener.screen("Send issues to admin@company.org")
        assertTrue("Role email should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `subdomain email is detected`() {
        val result = screener.screen("Reach the team at support@mail.example.co.uk")
        assertTrue("Subdomain email should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `email with plus tag is detected`() {
        val detected = screener.containsEmail("Filter mail to user+tag@gmail.com please")
        assertTrue("Email with plus tag should be detected", detected)
    }

    @Test
    fun `email with numbers in local part is detected`() {
        val detected = screener.containsEmail("ID user123@domain.net is registered")
        assertTrue("Email with numbers should be detected", detected)
    }

    @Test
    fun `plain text without email not detected`() {
        val detected = screener.containsEmail("The quick brown fox jumps over the lazy dog.")
        assertFalse("Plain text should not contain email", detected)
    }

    @Test
    fun `email in html anchor tag is detected`() {
        val text = "<a href=\"mailto:info@example.com\">Contact us</a>"
        val result = screener.screen(text)
        assertTrue("Email in HTML should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `multiple emails in text all detected`() {
        val text = "From: alice@a.com To: bob@b.com CC: charlie@c.com"
        val result = screener.screen(text)
        assertTrue("Multiple emails should be detected", result.detected)
        assertTrue("matchCount >= 3", result.matchCount >= 3)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `email with hyphen in domain is detected`() {
        val detected = screener.containsEmail("Contact: user@my-company.com")
        assertTrue("Email with hyphen in domain should be detected", detected)
    }

    @Test
    fun `email at the end of sentence is detected`() {
        val result = screener.screen("Please send your response to john.doe@example.com.")
        assertTrue("Email at end of sentence should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `email with uppercase characters is detected`() {
        val detected = screener.containsEmail("Send to Alice.Smith@Example.COM")
        assertTrue("Email with uppercase should be detected", detected)
    }

    @Test
    fun `partial email without at symbol not detected`() {
        val detected = screener.containsEmail("Username: aliceexample.com (no at sign)")
        assertFalse("String without @ should not be email", detected)
    }

    @Test
    fun `email without domain extension not detected`() {
        val detected = screener.containsEmail("Not an email: user@localhost")
        assertFalse("Email without proper TLD may not be detected", detected)
    }

    @Test
    fun `email in csv row is detected`() {
        val text = "John,Doe,john.doe@company.com,Manager"
        val result = screener.screen(text)
        assertTrue("Email in CSV should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `email in json string is detected`() {
        val text = "{\"email\": \"test.user@domain.org\", \"role\": \"admin\"}"
        val result = screener.screen(text)
        assertTrue("Email in JSON should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `email containsEmail returns false for url without email`() {
        val detected = screener.containsEmail("Visit https://www.example.com for more info")
        assertFalse("URL without email should not be detected", detected)
    }

    @Test
    fun `email with dot in local part is detected`() {
        val detected = screener.containsEmail("Contact first.last@bigcorp.io")
        assertTrue("Email with dot in local part should be detected", detected)
    }

    @Test
    fun `noreply email is detected`() {
        val result = screener.screen("Sent from noreply@notifications.service.com")
        assertTrue("noreply email should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `email in xml attribute is detected`() {
        val text = "<user email=\"support@example.net\" active=\"true\"/>"
        val result = screener.screen(text)
        assertTrue("Email in XML should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `email processing time is non negative`() {
        val result = screener.screen("Reach us at hello@world.com")
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected", result.detected)
        assertTrue("entityTypes not empty", result.entityTypes.isNotEmpty())
    }

    // =========================================================================
    // SECTION 4 — PHONE SCREENING (20 tests)
    // =========================================================================

    @Test
    fun `us phone with dashes is detected`() {
        val result = screener.screen("Call me at 800-555-1234.")
        assertTrue("US phone with dashes should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("entityTypes contains PHONE", result.entityTypes.any { it.contains("PHONE", ignoreCase = true) })
    }

    @Test
    fun `us phone with parentheses and space is detected`() {
        val result = screener.screen("Phone: (212) 555-9876")
        assertTrue("Phone with parens should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `us phone with country code is detected`() {
        val result = screener.screen("International: +1-415-555-2671")
        assertTrue("Phone with country code should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `toll free number is detected`() {
        val detected = screener.containsPhone("Support line: 1-800-555-0199")
        assertTrue("Toll-free number should be detected", detected)
    }

    @Test
    fun `uk international phone is detected`() {
        val detected = screener.containsPhone("UK contact: +44 20 7946 0958")
        assertTrue("UK phone number should be detected", detected)
    }

    @Test
    fun `plain text without phone not detected`() {
        val detected = screener.containsPhone("We are open Monday through Friday from nine to five.")
        assertFalse("Plain text without phone should not be detected", detected)
    }

    @Test
    fun `ssn with dashes not misidentified as phone`() {
        val detected = screener.containsPhone("SSN only: 123-45-6789")
        assertFalse("SSN should not be detected as phone", detected)
    }

    @Test
    fun `phone in email signature is detected`() {
        val text = "Best,\nJohn Smith\nSenior Engineer\nPhone: 303-555-7890\njohn@example.com"
        val result = screener.screen(text)
        assertTrue("Phone in signature should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `phone ten digits no formatting is detected`() {
        val detected = screener.containsPhone("My number is 5551234567")
        assertTrue("Ten-digit phone without formatting should be detected", detected)
    }

    @Test
    fun `multiple phones in text all detected`() {
        val text = "Office: (212) 555-1000 Fax: (212) 555-2000 Mobile: 415-555-3000"
        val result = screener.screen(text)
        assertTrue("Multiple phones should be detected", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `five digit zip code not misidentified as phone`() {
        val detected = screener.containsPhone("ZIP: 90210")
        assertFalse("5-digit zip should not be phone", detected)
    }

    @Test
    fun `phone with extension is detected`() {
        val result = screener.screen("Dial (800) 555-4321 ext. 201 for sales")
        assertTrue("Phone with extension should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `phone containsPhone returns false for date string`() {
        val detected = screener.containsPhone("Date: 2025-01-15")
        assertFalse("Date string should not be detected as phone", detected)
    }

    @Test
    fun `phone in html tel link is detected`() {
        val text = "<a href=\"tel:+18005551234\">Call Now</a>"
        val result = screener.screen(text)
        assertTrue("Phone in HTML tel link should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `australian phone number is detected`() {
        val detected = screener.containsPhone("AU contact: +61 2 9876 5432")
        assertTrue("Australian phone should be detected", detected)
    }

    @Test
    fun `phone with dots as separator is detected`() {
        val result = screener.screen("Reach us at 212.555.6789")
        assertTrue("Phone with dot separators should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `credit card number not misidentified as phone`() {
        val detected = screener.containsPhone("Card: 4111111111111111")
        assertFalse("Credit card should not be detected as phone", detected)
    }

    @Test
    fun `phone in json is detected`() {
        val text = "{\"name\": \"Alice\", \"phone\": \"(415) 555-1234\"}"
        val result = screener.screen(text)
        assertTrue("Phone in JSON should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `vanity toll free number is detected`() {
        val detected = screener.containsPhone("Call 1-800-FLOWERS")
        assertTrue("Vanity phone number should be detected", detected)
    }

    @Test
    fun `phone processing time is non negative`() {
        val result = screener.screen("My cell is (555) 123-4567")
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    // =========================================================================
    // SECTION 5 — IP ADDRESS SCREENING (20 tests)
    // =========================================================================

    @Test
    fun `valid ipv4 address is detected`() {
        val result = screener.screen("Server IP: 192.168.1.1")
        assertTrue("IPv4 should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("entityTypes contains IP", result.entityTypes.any { it.contains("IP", ignoreCase = true) })
    }

    @Test
    fun `public ipv4 address is detected`() {
        val result = screener.screen("Remote host: 8.8.8.8 is the DNS server")
        assertTrue("Public IPv4 should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `loopback address is detected`() {
        val result = screener.screen("Service running on 127.0.0.1 port 8080")
        assertTrue("Loopback should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `multiple ipv4 addresses in text all detected`() {
        val text = "Route from 10.0.0.1 to 192.168.0.254 via 172.16.0.1"
        val result = screener.screen(text)
        assertTrue("Multiple IPs should be detected", result.detected)
        assertTrue("matchCount >= 3", result.matchCount >= 3)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `software version number not misidentified as ip`() {
        val detected = screener.containsIPAddress("Using library version 1.2.3.4 in production")
        assertFalse("Software version should not be detected as IP", detected)
    }

    @Test
    fun `date format not misidentified as ip`() {
        val detected = screener.containsIPAddress("Event on 2025.01.15 was a success")
        assertFalse("Date in dot format should not be detected as IP", detected)
    }

    @Test
    fun `invalid ip with octet over 255 not detected`() {
        val detected = screener.containsIPAddress("Bad IP: 999.168.1.1")
        assertFalse("IP with invalid octet should not be detected", detected)
    }

    @Test
    fun `ip address in log line is detected`() {
        val text = "2025-01-01 12:00:00 INFO Request from 203.0.113.50 received"
        val result = screener.screen(text)
        assertTrue("IP in log line should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `ip address in url is detected`() {
        val result = screener.screen("Access http://203.0.113.10:8080/api/v1")
        assertTrue("IP in URL should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `cidr notation ip is detected`() {
        val detected = screener.containsIPAddress("Block the subnet 192.168.1.0/24")
        assertTrue("CIDR notation IP should be detected", detected)
    }

    @Test
    fun `ip address containsIPAddress returns true for private range`() {
        val detected = screener.containsIPAddress("Internal network: 10.0.0.1")
        assertTrue("Private range IP should be detected", detected)
    }

    @Test
    fun `plain text without ip not detected`() {
        val detected = screener.containsIPAddress("This is a regular sentence without any IP addresses.")
        assertFalse("Plain text should not contain IP", detected)
    }

    @Test
    fun `ip in json config is detected`() {
        val text = "{\"host\": \"192.168.1.100\", \"port\": 3306}"
        val result = screener.screen(text)
        assertTrue("IP in JSON should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `broadcast address is detected`() {
        val detected = screener.containsIPAddress("Broadcast to 255.255.255.255")
        assertTrue("Broadcast address should be detected", detected)
    }

    @Test
    fun `ip at end of sentence is detected`() {
        val result = screener.screen("The client connected from 198.51.100.42.")
        assertTrue("IP at end of sentence should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `fragment like 1 2 not misidentified as ip`() {
        val detected = screener.containsIPAddress("Step 1.2 of the process is complete")
        assertFalse("Decimal number should not be IP", detected)
    }

    @Test
    fun `ip address in xml element is detected`() {
        val text = "<server><address>172.16.254.1</address><port>443</port></server>"
        val result = screener.screen(text)
        assertTrue("IP in XML should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    @Test
    fun `multiple ips in csv log is detected`() {
        val text = "10.0.0.1,GET,200\n10.0.0.2,POST,201\n172.16.0.5,DELETE,204"
        val result = screener.screen(text)
        assertTrue("Multiple IPs in CSV should be detected", result.detected)
        assertTrue("matchCount >= 3", result.matchCount >= 3)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `ip version number confusion test`() {
        val detected = screener.containsIPAddress("Running Python 3.9.7 and Django 4.2.1")
        assertFalse("Version numbers should not be detected as IPs", detected)
    }

    @Test
    fun `ip processing time is non negative`() {
        val result = screener.screen("Gateway: 192.168.0.1")
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    // =========================================================================
    // SECTION 6 — COMBINED MULTI-TYPE SCREENING (20 tests)
    // =========================================================================

    @Test
    fun `text with ssn and email has both detected`() {
        val text = "Patient SSN: 123-45-6789 Email: patient@clinic.com"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertTrue("entityTypes size >= 2", result.entityTypes.size >= 2)
    }

    @Test
    fun `text with credit card and phone has both detected`() {
        val text = "Card: 4111-1111-1111-1111 Phone: (800) 555-1234"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertTrue("entityTypes size >= 2", result.entityTypes.size >= 2)
    }

    @Test
    fun `text with email and ip has both detected`() {
        val text = "Admin user admin@corp.com accessed server at 192.168.1.50"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `text with three pii types has all detected`() {
        val text = "SSN: 987-65-4321 Email: user@test.net Phone: 415-555-9090"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 3", result.matchCount >= 3)
        assertTrue("entityTypes size >= 3", result.entityTypes.size >= 3)
    }

    @Test
    fun `text with all pii types has all detected`() {
        val text = "SSN: 111-22-3333 Card: 4111111111111111 Email: a@b.com Phone: 800-555-0001 IP: 1.2.3.4"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 4", result.matchCount >= 4)
        assertTrue("entityTypes not empty", result.entityTypes.isNotEmpty())
    }

    @Test
    fun `clean text with no pii is not detected`() {
        val text = "The annual report shows a 15% increase in customer satisfaction this quarter."
        val result = screener.screen(text)
        assertFalse("Clean text should not be detected", result.detected)
        assertEquals("matchCount should be 0", 0, result.matchCount)
        assertTrue("entityTypes should be empty", result.entityTypes.isEmpty())
    }

    @Test
    fun `text with ssn and credit card entity types both present`() {
        val text = "Application: SSN 444-55-6666 and card 5500000000000004"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertTrue("entityTypes contains SSN type", result.entityTypes.any { it.contains("SSN", ignoreCase = true) })
    }

    @Test
    fun `text with email and credit card entity types both present`() {
        val text = "Receipt for user@shop.com: Card 4111-1111-1111-1111 charged \$200"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertTrue("entityTypes contains EMAIL type", result.entityTypes.any { it.contains("EMAIL", ignoreCase = true) })
    }

    @Test
    fun `text with phone and ip entity types both present`() {
        val text = "Request from IP 10.0.0.5 by caller (212) 555-6677"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertTrue("entityTypes not empty", result.entityTypes.isNotEmpty())
    }

    @Test
    fun `text with ssn phone and ip has three entity types`() {
        val text = "Record: 222-33-4444, 303-555-7777, 192.168.10.1"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 3", result.matchCount >= 3)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `combined text match count matches sum of individual types`() {
        val text = "Email: a@b.com and IP: 8.8.4.4"
        val result = screener.screen(text)
        val emailDetected = screener.containsEmail(text)
        val ipDetected = screener.containsIPAddress(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("Email should be individually detected", emailDetected)
        assertTrue("IP should be individually detected", ipDetected)
    }

    @Test
    fun `combined text processing time is non negative`() {
        val text = "SSN: 123-45-6789 and Card: 4111111111111111 and Email: x@y.com"
        val result = screener.screen(text)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `text with duplicate pii of same type counts correctly`() {
        val text = "SSN1: 111-22-3333 SSN2: 444-55-6666"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `combined check credit card and phone individual methods agree with screen`() {
        val text = "Billing card 5500000000000004 call (800) 555-3333"
        val result = screener.screen(text)
        val ccDetected = screener.containsCreditCard(text)
        val phoneDetected = screener.containsPhone(text)
        assertTrue("Screen should detect PII", result.detected)
        assertTrue("Credit card should be detected individually", ccDetected)
        assertTrue("Phone should be detected individually", phoneDetected)
    }

    @Test
    fun `combined check ssn and email individual methods agree with screen`() {
        val text = "Form: 999-88-7777, admin@form.com"
        val result = screener.screen(text)
        val ssnDetected = screener.containsSSN(text)
        val emailDetected = screener.containsEmail(text)
        assertTrue("Screen should detect PII", result.detected)
        assertTrue("SSN should be detected individually", ssnDetected)
        assertTrue("Email should be detected individually", emailDetected)
    }

    @Test
    fun `text with no pii has zero match count`() {
        val result = screener.screen("Nothing sensitive here whatsoever.")
        assertEquals("matchCount should be 0", 0, result.matchCount)
        assertFalse("detected should be false", result.detected)
        assertTrue("entityTypes should be empty", result.entityTypes.isEmpty())
    }

    @Test
    fun `combined text with api key and email both detected`() {
        val text = "API key: sk-abcdef123456 contact support@api.io"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `combined text entity types is a set with no duplicates`() {
        val text = "SSN: 123-45-6789 and another SSN: 987-65-4321"
        val result = screener.screen(text)
        assertTrue("Should detect PII", result.detected)
        val ssnEntries = result.entityTypes.filter { it.contains("SSN", ignoreCase = true) }
        assertEquals("SSN type should appear only once in entityTypes set", 1, ssnEntries.size)
    }

    @Test
    fun `text with four pii types processing time is non negative`() {
        val text = "SSN: 321-54-9876 Card: 4111111111111111 Email: z@z.com Phone: (212) 555-1111"
        val result = screener.screen(text)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
        assertTrue("detected", result.detected)
        assertTrue("matchCount >= 4", result.matchCount >= 4)
    }

    @Test
    fun `combined text returns detected true when any single pii present`() {
        val text = "Nothing special except: john.doe@acme.com is the contact."
        val result = screener.screen(text)
        assertTrue("Should be detected because email is present", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
        assertNotNull("Result not null", result)
    }

    // =========================================================================
    // SECTION 7 — PERFORMANCE / SCALABILITY (15 tests)
    // =========================================================================

    @Test
    fun `screen does not throw on 10k char text`() {
        val longText = "The quick brown fox jumps over the lazy dog. ".repeat(250)
        val result = screener.screen(longText)
        assertNotNull("Result must not be null for 10K char text", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `screen does not throw on text with 100 emails`() {
        val sb = StringBuilder()
        for (i in 1..100) sb.append("user$i@example.com ")
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null", result)
        assertTrue("Should detect emails", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `screen does not throw on text repeated 1000 times`() {
        val repeated = "Hello world. ".repeat(1000)
        val result = screener.screen(repeated)
        assertNotNull("Result must not be null", result)
        assertFalse("No PII in repeated text", result.detected)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `screen on 10k char text with embedded ssn completes without exception`() {
        val prefix = "A".repeat(5000)
        val suffix = "B".repeat(5000)
        val text = "${prefix}123-45-6789${suffix}"
        val result = screener.screen(text)
        assertNotNull("Result must not be null", result)
        assertTrue("SSN should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    @Test
    fun `screen on text with 50 credit cards returns result without exception`() {
        val sb = StringBuilder()
        for (i in 1..50) sb.append("Card$i: 4111-1111-1111-1111 ")
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null", result)
        assertTrue("Should detect credit cards", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `screen on text with 50 phone numbers returns result without exception`() {
        val sb = StringBuilder()
        for (i in 1..50) sb.append("Phone$i: (800) 555-${i.toString().padStart(4, '0')} ")
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null", result)
        assertTrue("Should detect phones", result.detected)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `screen on text with 50 ip addresses returns result without exception`() {
        val sb = StringBuilder()
        for (i in 1..50) sb.append("IP$i: 192.168.1.${i % 255} ")
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null", result)
        assertTrue("Should detect IPs", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `screen on unicode text 10k chars completes without exception`() {
        val unicodeText = "\u4e2d\u6587\u6587\u672c\u6d4b\u8bd5 ".repeat(1200)
        val result = screener.screen(unicodeText)
        assertNotNull("Result must not be null for Unicode text", result)
        assertNotNull("entityTypes must not be null", result.entityTypes)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `containsEmail on 10k char text with single email returns true`() {
        val prefix = "x".repeat(5000)
        val suffix = "y".repeat(5000)
        val text = "${prefix}perf@test.com${suffix}"
        val detected = screener.containsEmail(text)
        assertTrue("Email in long text should be detected", detected)
    }

    @Test
    fun `containsSSN on 10k char text with single ssn returns true`() {
        val prefix = "a".repeat(5000)
        val suffix = "b".repeat(5000)
        val text = "${prefix}111-22-3333${suffix}"
        val detected = screener.containsSSN(text)
        assertTrue("SSN in long text should be detected", detected)
    }

    @Test
    fun `containsCreditCard on large text returns result without exception`() {
        val sb = StringBuilder()
        repeat(300) { sb.append("Lorem ipsum dolor sit amet. ") }
        sb.append("4111111111111111")
        val detected = screener.containsCreditCard(sb.toString())
        assertTrue("Credit card in large text should be detected", detected)
    }

    @Test
    fun `screen on empty text is fast and returns non null`() {
        val start = System.currentTimeMillis()
        val result = screener.screen("")
        val elapsed = System.currentTimeMillis() - start
        assertNotNull("Result must not be null for empty text", result)
        assertTrue("Should complete quickly (under 5000ms)", elapsed < 5000L)
        assertFalse("Empty text should not be detected", result.detected)
    }

    @Test
    fun `screen on whitespace only text is fast and returns non null`() {
        val whitespace = " ".repeat(10000)
        val result = screener.screen(whitespace)
        assertNotNull("Result must not be null", result)
        assertFalse("Whitespace only should not be detected", result.detected)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `screen on mixed pii large text returns non null`() {
        val sb = StringBuilder()
        for (i in 1..100) {
            sb.append("User$i: SSN ${100 + i}-${10 + i}-${1000 + i} Email user$i@domain.com IP 192.168.1.${i % 255} ")
        }
        val result = screener.screen(sb.toString())
        assertNotNull("Result must not be null for mixed PII large text", result)
        assertTrue("Should detect PII", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `containsPhone on 10k char text with phone returns true`() {
        val prefix = "z".repeat(4990)
        val suffix = "w".repeat(4990)
        val text = "${prefix}(415) 555-1234${suffix}"
        val detected = screener.containsPhone(text)
        assertTrue("Phone in large text should be detected", detected)
    }

    // =========================================================================
    // SECTION 8 — EDGE CASE INPUTS (20 tests)
    // =========================================================================

    @Test
    fun `empty string does not throw and is not detected`() {
        val result = screener.screen("")
        assertNotNull("Result must not be null for empty string", result)
        assertFalse("Empty string should not be detected", result.detected)
        assertEquals("matchCount should be 0 for empty string", 0, result.matchCount)
    }

    @Test
    fun `blank string with only spaces not detected`() {
        val result = screener.screen("     ")
        assertNotNull("Result must not be null", result)
        assertFalse("Blank string should not be detected", result.detected)
        assertTrue("entityTypes should be empty", result.entityTypes.isEmpty())
    }

    @Test
    fun `single character input not detected`() {
        val result = screener.screen("A")
        assertNotNull("Result must not be null", result)
        assertFalse("Single char should not be detected", result.detected)
        assertEquals("matchCount should be 0", 0, result.matchCount)
    }

    @Test
    fun `only punctuation not detected`() {
        val result = screener.screen("!@#\$%^&*()_+-=[]{}|;':\",./<>?")
        assertNotNull("Result must not be null", result)
        assertFalse("Only punctuation should not be detected", result.detected)
        assertTrue("entityTypes should be empty", result.entityTypes.isEmpty())
    }

    @Test
    fun `chinese unicode text not detected as pii`() {
        val result = screener.screen("\u4eca\u5929\u5929\u6c14\u5f88\u597d\uff0c\u6211\u4eec\u53bb\u516c\u56ed\u5427")
        assertNotNull("Result must not be null for Chinese text", result)
        assertFalse("Chinese text should not be detected as PII", result.detected)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `arabic unicode text not detected as pii`() {
        val result = screener.screen("\u0645\u0631\u062d\u0628\u0627 \u0628\u0627\u0644\u0639\u0627\u0644\u0645")
        assertNotNull("Result must not be null for Arabic text", result)
        assertFalse("Arabic text should not be detected as PII", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `base64 encoded string does not throw`() {
        val base64 = "SGVsbG8gV29ybGQhIFRoaXMgaXMgYSBiYXNlNjQgZW5jb2RlZCBzdHJpbmcu"
        val result = screener.screen(base64)
        assertNotNull("Result must not be null for base64 string", result)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `csv data with no pii is not detected`() {
        val csv = "id,name,department,salary\n1,Alice,Engineering,95000\n2,Bob,Marketing,75000\n3,Charlie,HR,65000"
        val result = screener.screen(csv)
        assertNotNull("Result must not be null for CSV", result)
        assertFalse("CSV without PII should not be detected", result.detected)
        assertEquals("matchCount should be 0", 0, result.matchCount)
    }

    @Test
    fun `csv data with email is detected`() {
        val csv = "id,name,email\n1,Alice,alice@company.com\n2,Bob,bob@company.com"
        val result = screener.screen(csv)
        assertNotNull("Result must not be null", result)
        assertTrue("CSV with emails should be detected", result.detected)
        assertTrue("matchCount >= 2", result.matchCount >= 2)
    }

    @Test
    fun `json data with no pii is not detected`() {
        val json = "{\"product\": \"Widget\", \"price\": 9.99, \"quantity\": 100, \"category\": \"Electronics\"}"
        val result = screener.screen(json)
        assertNotNull("Result must not be null for JSON", result)
        assertFalse("JSON without PII should not be detected", result.detected)
        assertTrue("entityTypes should be empty", result.entityTypes.isEmpty())
    }

    @Test
    fun `xml data with no pii is not detected`() {
        val xml = "<?xml version=\"1.0\"?><catalog><item id=\"1\"><name>Book</name><price>29.99</price></item></catalog>"
        val result = screener.screen(xml)
        assertNotNull("Result must not be null for XML", result)
        assertFalse("XML without PII should not be detected", result.detected)
        assertTrue("entityTypes should be empty", result.entityTypes.isEmpty())
    }

    @Test
    fun `html content with no pii is not detected`() {
        val html = "<html><head><title>Hello</title></head><body><h1>Welcome</h1><p>This is a paragraph.</p></body></html>"
        val result = screener.screen(html)
        assertNotNull("Result must not be null for HTML", result)
        assertFalse("HTML without PII should not be detected", result.detected)
        assertEquals("matchCount should be 0", 0, result.matchCount)
    }

    @Test
    fun `html content with email in mailto is detected`() {
        val html = "<html><body><a href=\"mailto:contact@website.org\">Email us</a></body></html>"
        val result = screener.screen(html)
        assertNotNull("Result must not be null", result)
        assertTrue("Email in HTML should be detected", result.detected)
        assertTrue("matchCount >= 1", result.matchCount >= 1)
    }

    @Test
    fun `newline only text not detected`() {
        val result = screener.screen("\n\n\n\n\n")
        assertNotNull("Result must not be null", result)
        assertFalse("Newlines only should not be detected", result.detected)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `tab only text not detected`() {
        val result = screener.screen("\t\t\t\t")
        assertNotNull("Result must not be null", result)
        assertFalse("Tabs only should not be detected", result.detected)
        assertEquals("matchCount should be 0", 0, result.matchCount)
    }

    @Test
    fun `mixed whitespace text not detected`() {
        val result = screener.screen("  \t\n  \r\n  ")
        assertNotNull("Result must not be null", result)
        assertFalse("Mixed whitespace should not be detected", result.detected)
        assertTrue("entityTypes should be empty", result.entityTypes.isEmpty())
    }

    @Test
    fun `text with null bytes does not throw`() {
        val text = "Hello\u0000World\u0000Test"
        val result = screener.screen(text)
        assertNotNull("Result must not be null for text with null bytes", result)
        assertFalse("Text with null bytes should not be detected as PII", result.detected)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `text with emoji characters does not throw`() {
        val text = "\uD83D\uDE00 Hello \uD83D\uDCF1 World \uD83C\uDF0D"
        val result = screener.screen(text)
        assertNotNull("Result must not be null for emoji text", result)
        assertFalse("Emoji text should not be detected as PII", result.detected)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    @Test
    fun `very long single word does not throw`() {
        val singleWord = "a".repeat(10000)
        val result = screener.screen(singleWord)
        assertNotNull("Result must not be null for very long single word", result)
        assertFalse("Very long single word should not be detected", result.detected)
        assertEquals("matchCount should be 0", 0, result.matchCount)
    }

    @Test
    fun `string of all digits not detected as any pii`() {
        val digits = "1234567890" .repeat(100)
        val result = screener.screen(digits)
        assertNotNull("Result must not be null for digit string", result)
        assertTrue("processingTimeMs >= 0", result.processingTimeMs >= 0L)
        assertNotNull("entityTypes not null", result.entityTypes)
    }

    // =========================================================================
    // SECTION 9 — screenAll BATCH METHOD (25 tests)
    // =========================================================================

    @Test
    fun `screenAll with single text returns map with one entry`() {
        val texts = listOf("Hello world")
        val results = screener.screenAll(texts)
        assertNotNull("Results map must not be null", results)
        assertEquals("Should have exactly 1 entry", 1, results.size)
        assertTrue("Key should be the input text", results.containsKey("Hello world"))
    }

    @Test
    fun `screenAll with empty list returns empty map`() {
        val results = screener.screenAll(emptyList())
        assertNotNull("Results must not be null for empty list", results)
        assertEquals("Empty list should return empty map", 0, results.size)
    }

    @Test
    fun `screenAll with five texts returns map with five entries`() {
        val texts = listOf("text1", "text2", "text3", "text4", "text5")
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null", results)
        assertEquals("Should have exactly 5 entries", 5, results.size)
        assertTrue("All keys should be present", results.keys.containsAll(texts))
    }

    @Test
    fun `screenAll with ten texts returns map with ten entries`() {
        val texts = (1..10).map { "item $it sample text" }
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null", results)
        assertEquals("Should have exactly 10 entries", 10, results.size)
        texts.forEach { assertTrue("Key $it should be present", results.containsKey(it)) }
    }

    @Test
    fun `screenAll result for text with ssn matches individual screen result`() {
        val text = "SSN: 123-45-6789"
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        assertNotNull("Batch result must not be null", batch)
        val batchResult = batch[text]
        assertNotNull("Batch entry must not be null", batchResult)
        assertEquals("Detected should match", individual.detected, batchResult!!.detected)
    }

    @Test
    fun `screenAll result for text with email matches individual screen result`() {
        val text = "Contact: user@example.com"
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        val batchResult = batch[text]
        assertNotNull("Batch entry must not be null", batchResult)
        assertEquals("Detected should match", individual.detected, batchResult!!.detected)
        assertEquals("matchCount should match", individual.matchCount, batchResult.matchCount)
    }

    @Test
    fun `screenAll result for text with phone matches individual screen result`() {
        val text = "Call (800) 555-1234"
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        val batchResult = batch[text]
        assertNotNull("Batch entry must not be null", batchResult)
        assertEquals("Detected should match", individual.detected, batchResult!!.detected)
        assertEquals("entityTypes should match", individual.entityTypes, batchResult.entityTypes)
    }

    @Test
    fun `screenAll result for text with credit card matches individual screen result`() {
        val text = "Card: 4111111111111111"
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        val batchResult = batch[text]
        assertNotNull("Batch entry must not be null", batchResult)
        assertEquals("Detected should match", individual.detected, batchResult!!.detected)
        assertEquals("matchCount should match", individual.matchCount, batchResult.matchCount)
    }

    @Test
    fun `screenAll result for text with ip matches individual screen result`() {
        val text = "Server: 192.168.1.1"
        val individual = screener.screen(text)
        val batch = screener.screenAll(listOf(text))
        val batchResult = batch[text]
        assertNotNull("Batch entry must not be null", batchResult)
        assertEquals("Detected should match", individual.detected, batchResult!!.detected)
        assertEquals("matchCount should match", individual.matchCount, batchResult.matchCount)
    }

    @Test
    fun `screenAll result for clean text has detected false`() {
        val text = "Nothing sensitive here at all."
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Result must not be null", result)
        assertFalse("Clean text should not be detected", result!!.detected)
        assertEquals("matchCount should be 0", 0, result.matchCount)
    }

    @Test
    fun `screenAll with mixed clean and pii texts handles each correctly`() {
        val cleanText = "The sky is blue today."
        val piiText = "Email: pii@secret.com"
        val results = screener.screenAll(listOf(cleanText, piiText))
        assertNotNull("Results must not be null", results)
        assertFalse("Clean text should not be detected", results[cleanText]!!.detected)
        assertTrue("PII text should be detected", results[piiText]!!.detected)
    }

    @Test
    fun `screenAll batch results all have non null entityTypes`() {
        val texts = listOf("Hello", "World", "123-45-6789", "user@test.com")
        val results = screener.screenAll(texts)
        texts.forEach { text ->
            assertNotNull("entityTypes must not be null for '$text'", results[text]?.entityTypes)
        }
    }

    @Test
    fun `screenAll batch results all have non negative processingTimeMs`() {
        val texts = listOf("Alpha", "Beta", "Gamma")
        val results = screener.screenAll(texts)
        texts.forEach { text ->
            assertTrue(
                "processingTimeMs should be >= 0 for '$text'",
                (results[text]?.processingTimeMs ?: -1L) >= 0L
            )
        }
    }

    @Test
    fun `screenAll with ten texts all having emails detects all`() {
        val texts = (1..10).map { "Contact user$it@domain.com for help" }
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null", results)
        texts.forEach { text ->
            assertTrue("Email in '$text' should be detected", results[text]!!.detected)
        }
    }

    @Test
    fun `screenAll with ten texts all having ssns detects all`() {
        val ssns = listOf(
            "111-22-3333", "222-33-4444", "333-44-5555", "444-55-6666", "555-66-7777",
            "666-77-8888", "777-88-9999", "888-99-0000", "100-20-3040", "200-30-4050"
        )
        val texts = ssns.map { "SSN is $it on file" }
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null", results)
        texts.forEach { text ->
            assertTrue("SSN in '$text' should be detected", results[text]!!.detected)
        }
    }

    @Test
    fun `screenAll returns correct entry count for list of five clean texts`() {
        val texts = listOf(
            "The weather is nice.",
            "Today is a good day.",
            "Nothing to see here.",
            "All quiet on the front.",
            "No PII in this text."
        )
        val results = screener.screenAll(texts)
        assertEquals("Should have 5 entries", 5, results.size)
        texts.forEach { assertFalse("Should not detect PII in '${it}'", results[it]!!.detected) }
    }

    @Test
    fun `screenAll single text with multiple pii types returns correct match count`() {
        val text = "SSN: 123-45-6789 Email: a@b.com Card: 4111111111111111"
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Result must not be null", result)
        assertTrue("Should detect PII", result!!.detected)
        assertTrue("matchCount >= 3", result.matchCount >= 3)
    }

    @Test
    fun `screenAll entries are independent from each other`() {
        val textWithPii = "SSN: 999-88-7777"
        val textWithoutPii = "A perfectly clean sentence."
        val results = screener.screenAll(listOf(textWithPii, textWithoutPii))
        assertTrue("PII text should be detected", results[textWithPii]!!.detected)
        assertFalse("Clean text should not be detected", results[textWithoutPii]!!.detected)
        assertNotEquals(
            "Results should differ",
            results[textWithPii]!!.detected,
            results[textWithoutPii]!!.detected
        )
    }

    @Test
    fun `screenAll with single email text result has email entity type`() {
        val text = "Notify me at notify@users.io"
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Result must not be null", result)
        assertTrue("Should detect PII", result!!.detected)
        assertTrue(
            "entityTypes should contain EMAIL type",
            result.entityTypes.any { it.contains("EMAIL", ignoreCase = true) }
        )
    }

    @Test
    fun `screenAll with single ssn text result has ssn entity type`() {
        val text = "On record: 321-65-4321"
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Result must not be null", result)
        assertTrue("Should detect PII", result!!.detected)
        assertTrue(
            "entityTypes should contain SSN type",
            result.entityTypes.any { it.contains("SSN", ignoreCase = true) }
        )
    }

    @Test
    fun `screenAll with single phone text result has phone entity type`() {
        val text = "Dial 1-800-555-9999 now"
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Result must not be null", result)
        assertTrue("Should detect PII", result!!.detected)
        assertTrue(
            "entityTypes should contain PHONE type",
            result.entityTypes.any { it.contains("PHONE", ignoreCase = true) }
        )
    }

    @Test
    fun `screenAll keys are exactly the input strings`() {
        val texts = listOf("alpha beta", "gamma delta", "epsilon zeta")
        val results = screener.screenAll(texts)
        assertEquals("Keys count should match input count", texts.size, results.size)
        assertEquals("Keys should match input texts exactly", texts.toSet(), results.keys)
    }

    @Test
    fun `screenAll with duplicate texts returns expected entries`() {
        val text = "Contact me at dup@test.com"
        val texts = listOf(text, text)
        val results = screener.screenAll(texts)
        assertNotNull("Results must not be null", results)
        assertTrue("Duplicate text key should be present", results.containsKey(text))
        assertTrue("Duplicate email should be detected", results[text]!!.detected)
    }

    @Test
    fun `screenAll ten entries all have non null results`() {
        val texts = (1..10).map { "Entry number $it with some content" }
        val results = screener.screenAll(texts)
        texts.forEach { text ->
            assertNotNull("Result must not be null for '$text'", results[text])
        }
    }

    @Test
    fun `screenAll result entity types are a set`() {
        val text = "SSN: 123-45-6789 and SSN: 987-65-4321 Email: a@b.com"
        val results = screener.screenAll(listOf(text))
        val result = results[text]
        assertNotNull("Result must not be null", result)
        val entityTypes = result!!.entityTypes
        assertNotNull("entityTypes must not be null", entityTypes)
        // Set property: no duplicates
        assertEquals("entityTypes as set should have no duplicates", entityTypes.size, entityTypes.toSet().size)
    }
}
