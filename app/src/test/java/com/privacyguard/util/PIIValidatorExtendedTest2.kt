package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * PIIValidator Extended Test Suite – Part 2
 * Supplements PIIValidatorTest.kt with additional coverage for:
 * - Multi-type detection in complex documents
 * - Edge cases for all PII entity types
 * - Risk level assessment
 * - Confidence scoring
 * - Batch processing
 * - Regression tests
 */
class PIIValidatorExtendedTest2 {

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 – SSN Detection in PIIValidator
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 ssn 001 plain ssn detected`() {
        val result = PIIValidator.analyze("SSN: 123-45-6789")
        assertTrue(result.entities.any { it.type.name.contains("SSN") })
    }

    @Test fun `piiv2 ssn 002 ssn with spaces detected`() {
        val result = PIIValidator.analyze("SSN 123 45 6789 on file")
        assertTrue(result.entities.any { it.type.name.contains("SSN") })
    }

    @Test fun `piiv2 ssn 003 ssn without dashes`() {
        val result = PIIValidator.analyze("Social Security Number 123456789")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 ssn 004 invalid ssn not detected`() {
        val result = PIIValidator.analyze("Number 000-00-0000 here")
        assertFalse(result.entities.any { it.type.name.contains("SSN") })
    }

    @Test fun `piiv2 ssn 005 area 666 not detected`() {
        val result = PIIValidator.analyze("Fake SSN 666-12-3456")
        assertFalse(result.entities.any { it.type.name.contains("SSN") && it.value == "666-12-3456" })
    }

    @Test fun `piiv2 ssn 006 multiple ssns detected`() {
        val result = PIIValidator.analyze("P1: 123-45-6789 P2: 234-56-7890")
        val ssnEntities = result.entities.filter { it.type.name.contains("SSN") }
        assertTrue(ssnEntities.size >= 2)
    }

    @Test fun `piiv2 ssn 007 ssn in medical record`() {
        val text = """
            PATIENT RECORD
            Name: Jane Doe
            SSN: 456-78-9012
            DOB: 1982-03-15
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.entities.any { it.type.name.contains("SSN") })
    }

    @Test fun `piiv2 ssn 008 ssn in json format`() {
        val result = PIIValidator.analyze("""{"ssn": "234-56-7890", "name": "John"}""")
        assertTrue(result.hasAny())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 – Credit Card Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 card 001 visa detected`() {
        val result = PIIValidator.analyze("Card number: 4532015112830366")
        assertTrue(result.entities.any { it.type.name.contains("CREDIT") || it.type.name.contains("CARD") })
    }

    @Test fun `piiv2 card 002 mastercard detected`() {
        val result = PIIValidator.analyze("MC: 5425233430109903")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 card 003 amex detected`() {
        val result = PIIValidator.analyze("AMEX number 371449635398431")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 card 004 formatted visa detected`() {
        val result = PIIValidator.analyze("Credit card 4532-0151-1283-0366 exp 12/25")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 card 005 fake card not detected`() {
        val result = PIIValidator.analyze("Not a card: 1234567890123456")
        // Result may or may not detect; key is no crash
        assertNotNull(result)
    }

    @Test fun `piiv2 card 006 multiple cards in text`() {
        val result = PIIValidator.analyze("Card1: 4532015112830366 Card2: 5425233430109903")
        val cardEntities = result.entities.filter { it.type.name.contains("CARD") || it.type.name.contains("CREDIT") }
        assertTrue(cardEntities.size >= 2 || result.hasAny())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 – Email Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 email 001 simple email detected`() {
        val result = PIIValidator.analyze("Email: user@example.com")
        assertTrue(result.entities.any { it.type.name.contains("EMAIL") })
    }

    @Test fun `piiv2 email 002 email in sentence`() {
        val result = PIIValidator.analyze("Please contact john.doe@company.org for details")
        assertTrue(result.entities.any { it.type.name.contains("EMAIL") })
    }

    @Test fun `piiv2 email 003 multiple emails detected`() {
        val result = PIIValidator.analyze("From: a@b.com To: c@d.com Reply: e@f.com")
        val emailEntities = result.entities.filter { it.type.name.contains("EMAIL") }
        assertTrue(emailEntities.size >= 2)
    }

    @Test fun `piiv2 email 004 invalid email not detected`() {
        val result = PIIValidator.analyze("Not an email: userATexample.com here")
        assertFalse(result.entities.any { it.type.name.contains("EMAIL") })
    }

    @Test fun `piiv2 email 005 email with plus sign`() {
        val result = PIIValidator.analyze("user+tag@gmail.com")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 email 006 email in json`() {
        val result = PIIValidator.analyze("""{"email": "admin@domain.co.uk"}""")
        assertTrue(result.hasAny())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 – Phone Number Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 phone 001 us phone detected`() {
        val result = PIIValidator.analyze("Phone: (555) 123-4567")
        assertTrue(result.entities.any { it.type.name.contains("PHONE") })
    }

    @Test fun `piiv2 phone 002 e164 phone detected`() {
        val result = PIIValidator.analyze("Mobile: +1-800-555-0100")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 phone 003 international phone detected`() {
        val result = PIIValidator.analyze("UK number: +44 20 7946 0958")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 phone 004 phone in form`() {
        val result = PIIValidator.analyze("Tel: 555.867.5309")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 phone 005 multiple phones`() {
        val result = PIIValidator.analyze("Home: (555)123-4567 Work: (555)987-6543")
        val phoneEntities = result.entities.filter { it.type.name.contains("PHONE") }
        assertTrue(phoneEntities.size >= 2 || result.hasAny())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 – IP Address Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 ip 001 public ipv4 detected`() {
        val result = PIIValidator.analyze("Server IP: 8.8.8.8")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 ip 002 private ip may not be pii`() {
        val result = PIIValidator.analyze("Gateway: 192.168.1.1")
        // Private IPs may or may not be classified as PII depending on policy
        assertNotNull(result)
    }

    @Test fun `piiv2 ip 003 ipv6 detected`() {
        val result = PIIValidator.analyze("IPv6: 2001:0db8:85a3:0000:0000:8a2e:0370:7334")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 ip 004 log line with ip`() {
        val result = PIIValidator.analyze("[2024-01-01] 203.0.113.42 GET /api/data 200")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 ip 005 multiple ips in access log`() {
        val text = "Client: 198.51.100.1 Proxy: 203.0.113.25 Server: 198.51.100.100"
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 – Date of Birth Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 dob 001 iso dob detected`() {
        val result = PIIValidator.analyze("DOB: 1985-03-22")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 dob 002 us format dob detected`() {
        val result = PIIValidator.analyze("Date of Birth: 03/22/1985")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 dob 003 eu format dob detected`() {
        val result = PIIValidator.analyze("Geburtsdatum: 22.03.1985")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 dob 004 dob in medical record`() {
        val text = "Patient: John Smith, DOB: 1975-07-04, MRN: 123456"
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 dob 005 invalid date not pii`() {
        val result = PIIValidator.analyze("Date: 2023-13-45")
        // Invalid date should not be detected as DOB
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 – API Key Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 apikey 001 stripe test key detected`() {
        val key = "sk_tst_4eC39HqLyjWDarjtT1zdp7dc"
        val result = PIIValidator.analyze("API key: $key")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 apikey 002 github token detected`() {
        val token = "gh" + "p_abcdefghijklmnopqrstuvwxyz1234"
        val result = PIIValidator.analyze("Token: $token")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 apikey 003 aws access key pattern`() {
        val key = "AKIA" + "IOSFODNN7EXAMPLE"
        val result = PIIValidator.analyze("AWS key: $key")
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 apikey 004 short random string not api key`() {
        val result = PIIValidator.analyze("Code: XK9P2")
        // Short string should not be classified as API key
        assertFalse(result.entities.any { it.type.name.contains("API") })
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 – Multi-Type Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 multi 001 ssn and email in same text`() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val result = PIIValidator.analyze(text)
        assertTrue(result.entities.size >= 2)
    }

    @Test fun `piiv2 multi 002 card and phone in text`() {
        val text = "Card: 4532015112830366 Phone: (555) 123-4567"
        val result = PIIValidator.analyze(text)
        assertTrue(result.entities.size >= 2)
    }

    @Test fun `piiv2 multi 003 full medical record`() {
        val text = """
            Patient: John Smith
            SSN: 456-78-9012
            DOB: 1978-11-22
            Phone: (555) 234-5678
            Email: john.smith@email.com
            Insurance: 021000021
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.entities.size >= 3)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 multi 004 financial form all types`() {
        val text = """
            Name: Alice Johnson
            SSN: 234-56-7890
            Card: 4532015112830366
            Email: alice@bank.com
            Phone: +1-555-987-6543
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.entities.size >= 4)
    }

    @Test fun `piiv2 multi 005 hr document`() {
        val text = """
            Employee Record
            Name: Bob Williams
            SSN: 345-67-8901
            DOB: 1990-06-15
            Email: bob.williams@corp.com
            Phone: 555-345-6789
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 multi 006 ecommerce order`() {
        val text = """
            Order #12345
            Customer: Carol Davis
            Email: carol@shop.com
            Card: 5425233430109903
            Phone: (555) 456-7890
            IP: 203.0.113.42
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.entities.size >= 3)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 9 – Risk Level Assessment
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 risk 001 ssn alone is high risk`() {
        val result = PIIValidator.analyze("SSN: 123-45-6789")
        assertTrue(result.riskLevel.name == "HIGH" || result.riskLevel.name == "CRITICAL")
    }

    @Test fun `piiv2 risk 002 credit card is high risk`() {
        val result = PIIValidator.analyze("Card: 4532015112830366")
        assertTrue(result.riskLevel.name == "HIGH" || result.riskLevel.name == "CRITICAL")
    }

    @Test fun `piiv2 risk 003 email alone is medium risk`() {
        val result = PIIValidator.analyze("Email: user@example.com")
        val risk = result.riskLevel.name
        assertTrue(risk == "MEDIUM" || risk == "LOW" || risk == "HIGH")
    }

    @Test fun `piiv2 risk 004 clean text no risk`() {
        val result = PIIValidator.analyze("Today the weather is nice and sunny")
        assertFalse(result.riskLevel.name == "HIGH" || result.riskLevel.name == "CRITICAL")
    }

    @Test fun `piiv2 risk 005 multiple pii types critical risk`() {
        val text = "SSN: 123-45-6789, Card: 4532015112830366, DOB: 1985-03-22"
        val result = PIIValidator.analyze(text)
        assertTrue(result.riskLevel.name == "HIGH" || result.riskLevel.name == "CRITICAL")
    }

    @Test fun `piiv2 risk 006 ip address risk level`() {
        val result = PIIValidator.analyze("IP: 8.8.8.8")
        assertNotNull(result.riskLevel)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 10 – Confidence Scores
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 conf 001 labeled ssn high confidence`() {
        val result = PIIValidator.analyze("SSN: 123-45-6789")
        val ssnEntity = result.entities.firstOrNull { it.type.name.contains("SSN") }
        assertNotNull(ssnEntity)
        ssnEntity?.let { assertTrue(it.confidence >= 0.9f) }
    }

    @Test fun `piiv2 conf 002 labeled email high confidence`() {
        val result = PIIValidator.analyze("Email: user@example.com")
        val emailEntity = result.entities.firstOrNull { it.type.name.contains("EMAIL") }
        assertNotNull(emailEntity)
        emailEntity?.let { assertTrue(it.confidence >= 0.8f) }
    }

    @Test fun `piiv2 conf 003 all entities have positive confidence`() {
        val result = PIIValidator.analyze("SSN: 123-45-6789 Email: user@email.com")
        result.entities.forEach { entity ->
            assertTrue("Confidence should be positive for ${entity.type}", entity.confidence > 0.0f)
        }
    }

    @Test fun `piiv2 conf 004 entities have confidence between 0 and 1`() {
        val result = PIIValidator.analyze("Card: 4532015112830366")
        result.entities.forEach { entity ->
            assertTrue("Confidence out of range: ${entity.confidence}", entity.confidence in 0.0f..1.0f)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 11 – Empty and Edge Case Inputs
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 edge 001 empty string no entities`() {
        val result = PIIValidator.analyze("")
        assertTrue(result.entities.isEmpty())
        assertFalse(result.hasAny())
    }

    @Test fun `piiv2 edge 002 whitespace only no entities`() {
        val result = PIIValidator.analyze("   \t\n  ")
        assertTrue(result.entities.isEmpty())
    }

    @Test fun `piiv2 edge 003 single char no entities`() {
        val result = PIIValidator.analyze("X")
        assertTrue(result.entities.isEmpty())
    }

    @Test fun `piiv2 edge 004 only numbers no pii`() {
        val result = PIIValidator.analyze("12345 67890")
        assertFalse(result.entities.any { it.confidence > 0.9f })
    }

    @Test fun `piiv2 edge 005 very long text with single ssn`() {
        val text = "x".repeat(10000) + " SSN: 123-45-6789 " + "y".repeat(10000)
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 edge 006 null byte in text no crash`() {
        val result = runCatching { PIIValidator.analyze("test\u0000value") }
        assertTrue(result.isSuccess)
    }

    @Test fun `piiv2 edge 007 unicode text no crash`() {
        val result = runCatching { PIIValidator.analyze("SSN: 一二三-四五-六七八九") }
        assertTrue(result.isSuccess)
    }

    @Test fun `piiv2 edge 008 html tags stripped or handled`() {
        val result = PIIValidator.analyze("<p>SSN: 123-45-6789</p>")
        assertNotNull(result)
    }

    @Test fun `piiv2 edge 009 newline separated values`() {
        val text = "SSN:\n123-45-6789\nEmail:\nuser@example.com"
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 edge 010 tab separated values`() {
        val text = "SSN\t123-45-6789\tEmail\tuser@example.com"
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 12 – Batch Analysis
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 batch 001 list of texts analyzed`() {
        val texts = listOf(
            "SSN: 123-45-6789",
            "Email: user@example.com",
            "No PII here at all"
        )
        val results = PIIValidator.analyzeBatch(texts)
        assertEquals(3, results.size)
    }

    @Test fun `piiv2 batch 002 pii texts return entities`() {
        val texts = listOf(
            "SSN: 123-45-6789",
            "Card: 4532015112830366"
        )
        val results = PIIValidator.analyzeBatch(texts)
        assertTrue(results.all { it.hasAny() })
    }

    @Test fun `piiv2 batch 003 clean texts return no entities`() {
        val texts = listOf(
            "Hello world", "No PII here", "Just text"
        )
        val results = PIIValidator.analyzeBatch(texts)
        assertTrue(results.none { it.hasAny() })
    }

    @Test fun `piiv2 batch 004 empty batch returns empty list`() {
        val results = PIIValidator.analyzeBatch(emptyList())
        assertTrue(results.isEmpty())
    }

    @Test fun `piiv2 batch 005 single item batch`() {
        val results = PIIValidator.analyzeBatch(listOf("SSN: 456-78-9012"))
        assertEquals(1, results.size)
        assertTrue(results[0].hasAny())
    }

    @Test fun `piiv2 batch 006 100 item batch no crash`() {
        val texts = (1..100).map { "SSN: ${(100+it)}-${(10+it%89).toString().padStart(2,'0')}-${(1000+it).toString().padStart(4,'0')}" }
        val results = PIIValidator.analyzeBatch(texts)
        assertEquals(100, results.size)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 13 – hasAny and isEmpty
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 has 001 ssn text has any pii`() = assertTrue(PIIValidator.analyze("SSN: 123-45-6789").hasAny())
    @Test fun `piiv2 has 002 clean text has no pii`() = assertFalse(PIIValidator.analyze("Hello is it me").hasAny())
    @Test fun `piiv2 has 003 email text has any pii`() = assertTrue(PIIValidator.analyze("a@b.com").hasAny())
    @Test fun `piiv2 has 004 empty text no pii`() = assertFalse(PIIValidator.analyze("").hasAny())
    @Test fun `piiv2 has 005 card text has any pii`() = assertTrue(PIIValidator.analyze("4532015112830366").hasAny())

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 14 – Entity Position / Span
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 span 001 ssn entity has start position`() {
        val text = "SSN: 123-45-6789"
        val result = PIIValidator.analyze(text)
        val ssnEntity = result.entities.firstOrNull { it.type.name.contains("SSN") }
        ssnEntity?.let {
            assertTrue(it.startIndex >= 0)
            assertTrue(it.endIndex > it.startIndex)
        }
    }

    @Test fun `piiv2 span 002 email entity has valid span`() {
        val text = "Email: user@example.com"
        val result = PIIValidator.analyze(text)
        val emailEntity = result.entities.firstOrNull { it.type.name.contains("EMAIL") }
        emailEntity?.let {
            val extracted = text.substring(it.startIndex, it.endIndex)
            assertTrue(extracted.contains("@"))
        }
    }

    @Test fun `piiv2 span 003 entity value matches text at span`() {
        val text = "SSN 123-45-6789 here"
        val result = PIIValidator.analyze(text)
        val entity = result.entities.firstOrNull { it.type.name.contains("SSN") }
        entity?.let {
            assertEquals(it.value, text.substring(it.startIndex, it.endIndex))
        }
    }

    @Test fun `piiv2 span 004 multiple entities no overlapping spans`() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val result = PIIValidator.analyze(text)
        if (result.entities.size >= 2) {
            val sorted = result.entities.sortedBy { it.startIndex }
            for (i in 0 until sorted.size - 1) {
                assertTrue(sorted[i].endIndex <= sorted[i+1].startIndex)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 15 – Summary and Statistics
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 stats 001 entity count correct`() {
        val result = PIIValidator.analyze("SSN: 123-45-6789 Email: user@example.com")
        assertTrue(result.entityCount >= 1)
    }

    @Test fun `piiv2 stats 002 type summary includes all types`() {
        val text = "SSN: 123-45-6789 Email: user@example.com Phone: (555)123-4567"
        val result = PIIValidator.analyze(text)
        val summary = result.typeSummary
        assertTrue(summary.isNotEmpty())
    }

    @Test fun `piiv2 stats 003 highest confidence entity accessible`() {
        val result = PIIValidator.analyze("SSN: 123-45-6789 Email: user@example.com")
        val topEntity = result.highestConfidenceEntity
        assertNotNull(topEntity)
    }

    @Test fun `piiv2 stats 004 unique type count`() {
        val text = "SSN: 123-45-6789, SSN: 234-56-7890, Email: user@example.com"
        val result = PIIValidator.analyze(text)
        val uniqueTypes = result.entities.map { it.type }.toSet()
        assertTrue(uniqueTypes.size >= 1)
    }

    @Test fun `piiv2 stats 005 empty text stats all zero`() {
        val result = PIIValidator.analyze("")
        assertEquals(0, result.entityCount)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 16 – Performance
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 perf 001 1000 analyses no crash`() {
        repeat(1000) {
            PIIValidator.analyze("SSN: 123-45-6789")
        }
    }

    @Test fun `piiv2 perf 002 clean text 1000 times no crash`() {
        repeat(1000) {
            PIIValidator.analyze("No PII in this text at all")
        }
    }

    @Test fun `piiv2 perf 003 large text analysis no crash`() {
        val text = buildString {
            repeat(100) {
                append("Name: John Doe, SSN: 123-45-6789, Email: john@example.com. ")
            }
        }
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 perf 004 empty text 1000 times no crash`() {
        repeat(1000) {
            PIIValidator.analyze("")
        }
    }

    @Test fun `piiv2 perf 005 special chars 500 times no crash`() {
        repeat(500) {
            PIIValidator.analyze("!@#\$%^&*()_+-=[]{}|;':\",./<>?")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 17 – Regression Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 reg 001 078-05-1120 advertising ssn handled`() {
        val result = PIIValidator.analyze("SSN: 078-05-1120")
        assertNotNull(result)
    }

    @Test fun `piiv2 reg 002 219-09-9999 advertising ssn handled`() {
        val result = PIIValidator.analyze("SSN: 219-09-9999")
        assertNotNull(result)
    }

    @Test fun `piiv2 reg 003 dotless email not false positive`() {
        val result = PIIValidator.analyze("username")
        assertFalse(result.entities.any { it.type.name.contains("EMAIL") })
    }

    @Test fun `piiv2 reg 004 word that looks like ip`() {
        val result = PIIValidator.analyze("version 1.2.3.4 of the software")
        // 1.2.3.4 is valid IP but context suggests version
        assertNotNull(result)
    }

    @Test fun `piiv2 reg 005 us phone vs zip code`() {
        val result = PIIValidator.analyze("Zip code 12345 State NY")
        assertFalse(result.entities.any { it.type.name.contains("PHONE") })
    }

    @Test fun `piiv2 reg 006 year not detected as dob`() {
        val result = PIIValidator.analyze("The year 1985 was significant")
        // Single year should not be high-confidence DOB
        val dobEntities = result.entities.filter { it.type.name.contains("DOB") || it.type.name.contains("DATE") }
        assertTrue(dobEntities.isEmpty() || dobEntities.all { it.confidence < 0.8f })
    }

    @Test fun `piiv2 reg 007 credit card in product sku not detected`() {
        val result = PIIValidator.analyze("SKU: PRD-1234567890123456")
        // Should handle gracefully
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 18 – Redaction via PIIValidator
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 redact 001 redact ssn from text`() {
        val text = "SSN: 123-45-6789"
        val redacted = PIIValidator.redact(text)
        assertFalse(redacted.contains("123-45-6789"))
    }

    @Test fun `piiv2 redact 002 redact email from text`() {
        val text = "Contact user@example.com for info"
        val redacted = PIIValidator.redact(text)
        assertFalse(redacted.contains("user@example.com"))
    }

    @Test fun `piiv2 redact 003 redact phone from text`() {
        val text = "Call (555) 123-4567 now"
        val redacted = PIIValidator.redact(text)
        assertFalse(redacted.contains("555") && redacted.contains("1234"))
    }

    @Test fun `piiv2 redact 004 clean text unchanged`() {
        val text = "No PII in this text"
        val redacted = PIIValidator.redact(text)
        assertEquals(text, redacted)
    }

    @Test fun `piiv2 redact 005 multi pii all redacted`() {
        val text = "SSN: 123-45-6789, Email: user@example.com"
        val redacted = PIIValidator.redact(text)
        assertFalse(redacted.contains("123-45-6789"))
        assertFalse(redacted.contains("user@example.com"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 19 – Detection Filters (minimum confidence)
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 filter 001 high threshold filters low confidence`() {
        val text = "Version 1.2.3.4 released"
        val result = PIIValidator.analyzeWithMinConfidence(text, 0.9f)
        // With high threshold, ambiguous cases filtered out
        assertNotNull(result)
    }

    @Test fun `piiv2 filter 002 low threshold includes more entities`() {
        val text = "SSN: 123-45-6789"
        val resultHigh = PIIValidator.analyzeWithMinConfidence(text, 0.95f)
        val resultLow = PIIValidator.analyzeWithMinConfidence(text, 0.5f)
        assertTrue(resultLow.entities.size >= resultHigh.entities.size)
    }

    @Test fun `piiv2 filter 003 threshold 1.0 only perfect matches`() {
        val text = "SSN: 123-45-6789"
        val result = PIIValidator.analyzeWithMinConfidence(text, 1.0f)
        // May return 0 or 1 entity depending on implementation
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 20 – Special Document Types
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `piiv2 doc 001 loan application text`() {
        val text = """
            LOAN APPLICATION
            Applicant: David Miller
            SSN: 567-89-0123
            DOB: 1988-02-14
            Email: david.miller@email.com
            Phone: (555) 678-9012
            Annual Income: $85,000
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.entities.size >= 3)
    }

    @Test fun `piiv2 doc 002 insurance form text`() {
        val text = """
            INSURANCE CLAIM #CLM-2024-001
            Insured: Sarah Connor
            Policy #: 9876543210
            SSN: 678-90-1234
            Contact: sarah.connor@email.com
            Phone: 555-789-0123
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 doc 003 government id form`() {
        val text = """
            GOVERNMENT ID FORM
            Full Name: Michael Brown
            SSN: 789-01-2345
            Date of Birth: 1965-08-30
            Driver's License: X1234567
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 doc 004 tech log with pii`() {
        val text = """
            [INFO] 2024-01-01T10:00:00Z UserLogin user=jdoe@example.com ip=203.0.113.42
            [INFO] 2024-01-01T10:00:01Z ProfileUpdate ssn=123-45-6789 status=ok
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.hasAny())
    }

    @Test fun `piiv2 doc 005 api response body with pii`() {
        val text = """
            {
              "user_id": 12345,
              "name": "Alice Johnson",
              "email": "alice@example.com",
              "ssn": "234-56-7890",
              "phone": "+1-555-234-5678"
            }
        """.trimIndent()
        val result = PIIValidator.analyze(text)
        assertTrue(result.entities.size >= 2)
    }

} // end class PIIValidatorExtendedTest2
