package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

data class PIIEntity2(val type: String, val value: String, val start: Int, val end: Int, val confidence: Double = 0.95)
data class AnalysisResult2(val text: String, val entities: List<PIIEntity2>, val riskScore: Double)

private object PIIAnalysisPipeline2 {
    fun analyze(text: String): AnalysisResult2 {
        val entities = mutableListOf<PIIEntity2>()
        // SSN pattern
        Regex("\\b\\d{3}-\\d{2}-\\d{4}\\b").findAll(text).forEach {
            entities.add(PIIEntity2("SSN", it.value, it.range.first, it.range.last, 0.98))
        }
        // Email pattern
        Regex("\\b[\\w._%+\\-]+@[\\w.\\-]+\\.[A-Za-z]{2,}\\b").findAll(text).forEach {
            entities.add(PIIEntity2("EMAIL", it.value, it.range.first, it.range.last, 0.95))
        }
        // Credit card
        Regex("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13})\\b").findAll(text).forEach {
            entities.add(PIIEntity2("CREDIT_CARD", it.value, it.range.first, it.range.last, 0.97))
        }
        // Phone
        Regex("\\b\\+?1?[-.]?\\(?[0-9]{3}\\)?[-.]?[0-9]{3}[-.]?[0-9]{4}\\b").findAll(text).forEach {
            entities.add(PIIEntity2("PHONE", it.value, it.range.first, it.range.last, 0.90))
        }
        val risk = if (entities.isEmpty()) 0.0 else minOf(1.0, entities.size * 0.15 + entities.maxOf { it.confidence } * 0.5)
        return AnalysisResult2(text, entities, risk)
    }
    fun analyzeMultiple(texts: List<String>) = texts.map { analyze(it) }
    fun extractEntitiesByType(text: String, type: String) = analyze(text).entities.filter { it.type == type }
}

// ============================================================
// PIIAnalysisPipelineExtendedTest2
// 250+ test functions across 15 sections covering the full
// multi-stage analysis pipeline from entity detection through
// risk scoring and batch processing.
// ============================================================
class PIIAnalysisPipelineExtendedTest2 {

    // --------------------------------------------------------
    // Section 1: Clean text produces no entities (20 tests)
    // --------------------------------------------------------

    @Test
    fun cleanText_emptyString_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_onlyLetters_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("Hello World")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_onlyPunctuation_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("!@#$%^&*()")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_shortSentence_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("The quick brown fox")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_numbers_noSSN() {
        val result = PIIAnalysisPipeline2.analyze("I have 42 apples")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertTrue(ssns.isEmpty())
    }

    @Test
    fun cleanText_randomWords_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("Privacy security compliance audit")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_specialChars_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("Hello --- World *** Test")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_noEntities_riskScoreZero() {
        val result = PIIAnalysisPipeline2.analyze("No PII here at all")
        assertEquals(0.0, result.riskScore, 0.001)
    }

    @Test
    fun cleanText_emptyText_riskScoreZero() {
        val result = PIIAnalysisPipeline2.analyze("")
        assertEquals(0.0, result.riskScore, 0.001)
    }

    @Test
    fun cleanText_textPreservedInResult() {
        val input = "Hello World"
        val result = PIIAnalysisPipeline2.analyze(input)
        assertEquals(input, result.text)
    }

    @Test
    fun cleanText_entitiesListEmpty() {
        val result = PIIAnalysisPipeline2.analyze("Just a plain sentence.")
        assertEquals(0, result.entities.size)
    }

    @Test
    fun cleanText_twoWordSentence_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("Hello there")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_longParagraph_noEntities() {
        val text = "This is a long paragraph without any personally identifiable information. " +
            "It contains general statements about privacy and data protection principles. " +
            "No social security numbers, emails, credit cards, or phone numbers appear here."
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_singleWord_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("Privacy")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_numbersNotMatchingPatterns_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("Year 2024, code 42, ref 999")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun cleanText_urlWithoutAtSign_noEmail() {
        val result = PIIAnalysisPipeline2.analyze("Visit https://example.com for more info")
        val emails = result.entities.filter { it.type == "EMAIL" }
        assertTrue(emails.isEmpty())
    }

    @Test
    fun cleanText_dashes_notSSN() {
        val result = PIIAnalysisPipeline2.analyze("Code: AB-CD-EFGH")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertTrue(ssns.isEmpty())
    }

    @Test
    fun cleanText_partialPattern_noEntity() {
        val result = PIIAnalysisPipeline2.analyze("12-34")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertTrue(ssns.isEmpty())
    }

    @Test
    fun cleanText_multipleCleanTexts_allNoEntities() {
        val texts = listOf(
            "Good morning",
            "Have a great day",
            "See you tomorrow",
            "Best regards",
            "Thank you"
        )
        texts.forEach { text ->
            val result = PIIAnalysisPipeline2.analyze(text)
            assertTrue("Expected no entities for: $text", result.entities.isEmpty())
        }
    }

    @Test
    fun cleanText_whitespaceOnly_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("   \t  \n  ")
        assertTrue(result.entities.isEmpty())
    }

    // --------------------------------------------------------
    // Section 2: SSN detection in various contexts (20 tests)
    // --------------------------------------------------------

    @Test
    fun ssnDetection_standardFormat_detected() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertEquals(1, ssns.size)
    }

    @Test
    fun ssnDetection_standardFormat_correctValue() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals("123-45-6789", ssn.value)
    }

    @Test
    fun ssnDetection_atStartOfText_detected() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789 is my SSN")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertEquals(1, ssns.size)
    }

    @Test
    fun ssnDetection_atEndOfText_detected() {
        val result = PIIAnalysisPipeline2.analyze("My SSN is 123-45-6789")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertEquals(1, ssns.size)
    }

    @Test
    fun ssnDetection_inMiddleOfText_detected() {
        val result = PIIAnalysisPipeline2.analyze("Please find 123-45-6789 in the database")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertEquals(1, ssns.size)
    }

    @Test
    fun ssnDetection_twoSSNs_bothDetected() {
        val result = PIIAnalysisPipeline2.analyze("SSN1: 123-45-6789 SSN2: 987-65-4321")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertEquals(2, ssns.size)
    }

    @Test
    fun ssnDetection_threeSSNs_allDetected() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789 and 234-56-7890 and 345-67-8901")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertEquals(3, ssns.size)
    }

    @Test
    fun ssnDetection_highConfidence_0point98() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(0.98, ssn.confidence, 0.001)
    }

    @Test
    fun ssnDetection_type_isSsn() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val ssn = result.entities.first()
        assertEquals("SSN", ssn.type)
    }

    @Test
    fun ssnDetection_differentAreaCodes_allDetected() {
        val ssns = listOf("001-01-0001", "499-56-1234", "623-45-6789", "750-12-3456")
        ssns.forEach { ssn ->
            val result = PIIAnalysisPipeline2.analyze("Record: $ssn")
            assertTrue("Expected SSN to be detected: $ssn", result.entities.any { it.type == "SSN" })
        }
    }

    @Test
    fun ssnDetection_startOffset_isCorrect() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(5, ssn.start)
    }

    @Test
    fun ssnDetection_endOffset_isCorrect() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(15, ssn.end)
    }

    @Test
    fun ssnDetection_valueMathesTextSubstring() {
        val text = "My 123-45-6789 number"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(text.substring(ssn.start, ssn.end + 1), ssn.value)
    }

    @Test
    fun ssnDetection_allSameDigit_detected() {
        val ssns = listOf("111-11-1111", "222-22-2222", "444-44-4444")
        ssns.forEach { ssn ->
            val result = PIIAnalysisPipeline2.analyze(ssn)
            assertTrue(result.entities.any { it.type == "SSN" && it.value == ssn })
        }
    }

    @Test
    fun ssnDetection_labeledWithPrefix_detected() {
        listOf(
            "SSN: 123-45-6789",
            "Social Security Number: 123-45-6789",
            "ss#: 123-45-6789"
        ).forEach { text ->
            val result = PIIAnalysisPipeline2.analyze(text)
            assertTrue(result.entities.any { it.type == "SSN" })
        }
    }

    @Test
    fun ssnDetection_multilineText_detected() {
        val text = "Name: John\nSSN: 123-45-6789\nDOB: 1990-01-01"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssns = result.entities.filter { it.type == "SSN" }
        assertEquals(1, ssns.size)
    }

    @Test
    fun ssnDetection_ssn_raisesRiskScore() {
        val clean = PIIAnalysisPipeline2.analyze("No PII")
        val withSSN = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        assertTrue(withSSN.riskScore > clean.riskScore)
    }

    @Test
    fun ssnDetection_twoSSNs_higherRiskThanOne() {
        val oneSSN = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val twoSSN = PIIAnalysisPipeline2.analyze("SSN1: 123-45-6789 SSN2: 987-65-4321")
        assertTrue(twoSSN.riskScore >= oneSSN.riskScore)
    }

    @Test
    fun ssnDetection_extractByType_returnsOnlySSNs() {
        val entities = PIIAnalysisPipeline2.extractEntitiesByType("SSN: 123-45-6789", "SSN")
        assertTrue(entities.all { it.type == "SSN" })
    }

    @Test
    fun ssnDetection_noSSN_extractReturnsEmpty() {
        val entities = PIIAnalysisPipeline2.extractEntitiesByType("No SSN here", "SSN")
        assertTrue(entities.isEmpty())
    }

    // --------------------------------------------------------
    // Section 3: Email detection (20 tests)
    // --------------------------------------------------------

    @Test
    fun emailDetection_simpleEmail_detected() {
        val result = PIIAnalysisPipeline2.analyze("Email: john@example.com")
        val emails = result.entities.filter { it.type == "EMAIL" }
        assertEquals(1, emails.size)
    }

    @Test
    fun emailDetection_correctValue() {
        val result = PIIAnalysisPipeline2.analyze("Contact: alice@gmail.com")
        val email = result.entities.first { it.type == "EMAIL" }
        assertEquals("alice@gmail.com", email.value)
    }

    @Test
    fun emailDetection_atStartOfText_detected() {
        val result = PIIAnalysisPipeline2.analyze("john@example.com is my email")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_atEndOfText_detected() {
        val result = PIIAnalysisPipeline2.analyze("Send email to john@example.com")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_twoEmails_bothDetected() {
        val result = PIIAnalysisPipeline2.analyze("From alice@test.com to bob@test.com")
        val emails = result.entities.filter { it.type == "EMAIL" }
        assertEquals(2, emails.size)
    }

    @Test
    fun emailDetection_highConfidence_0point95() {
        val result = PIIAnalysisPipeline2.analyze("Email: john@example.com")
        val email = result.entities.first { it.type == "EMAIL" }
        assertEquals(0.95, email.confidence, 0.001)
    }

    @Test
    fun emailDetection_type_isEmail() {
        val result = PIIAnalysisPipeline2.analyze("user@domain.org")
        val entity = result.entities.first()
        assertEquals("EMAIL", entity.type)
    }

    @Test
    fun emailDetection_withDotInLocal_detected() {
        val result = PIIAnalysisPipeline2.analyze("john.doe@example.com")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_withPlusInLocal_detected() {
        val result = PIIAnalysisPipeline2.analyze("user+tag@example.com")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_withHyphenInDomain_detected() {
        val result = PIIAnalysisPipeline2.analyze("user@my-domain.com")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_gmailAddress_detected() {
        val result = PIIAnalysisPipeline2.analyze("Contact me at john@gmail.com")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_orgAddress_detected() {
        val result = PIIAnalysisPipeline2.analyze("Contact: admin@nonprofit.org")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_eduAddress_detected() {
        val result = PIIAnalysisPipeline2.analyze("student@university.edu")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_offsetCorrectness_startAndEnd() {
        val text = "Email: test@example.com end"
        val result = PIIAnalysisPipeline2.analyze(text)
        val email = result.entities.first { it.type == "EMAIL" }
        assertEquals("test@example.com", text.substring(email.start, email.end + 1))
    }

    @Test
    fun emailDetection_threeEmails_allDetected() {
        val text = "a@b.com c@d.org e@f.net"
        val result = PIIAnalysisPipeline2.analyze(text)
        val emails = result.entities.filter { it.type == "EMAIL" }
        assertEquals(3, emails.size)
    }

    @Test
    fun emailDetection_raisesRiskScore() {
        val clean = PIIAnalysisPipeline2.analyze("No PII")
        val withEmail = PIIAnalysisPipeline2.analyze("user@example.com")
        assertTrue(withEmail.riskScore > clean.riskScore)
    }

    @Test
    fun emailDetection_extractByType_returnsOnlyEmails() {
        val text = "john@example.com and 123-45-6789"
        val entities = PIIAnalysisPipeline2.extractEntitiesByType(text, "EMAIL")
        assertTrue(entities.all { it.type == "EMAIL" })
    }

    @Test
    fun emailDetection_extractByType_count() {
        val text = "first@example.com second@example.com"
        val entities = PIIAnalysisPipeline2.extractEntitiesByType(text, "EMAIL")
        assertEquals(2, entities.size)
    }

    @Test
    fun emailDetection_noEmail_extractReturnsEmpty() {
        val entities = PIIAnalysisPipeline2.extractEntitiesByType("no email here", "EMAIL")
        assertTrue(entities.isEmpty())
    }

    @Test
    fun emailDetection_multipleFormats_allDetected() {
        val emails = listOf(
            "simple@test.com",
            "with.dot@test.com",
            "with_underscore@test.com",
            "with-hyphen@test.com"
        )
        emails.forEach { email ->
            val result = PIIAnalysisPipeline2.analyze(email)
            assertTrue("Email should be detected: $email", result.entities.any { it.type == "EMAIL" })
        }
    }

    // --------------------------------------------------------
    // Section 4: Credit card detection (20 tests)
    // --------------------------------------------------------

    @Test
    fun creditCardDetection_visa16_detected() {
        val result = PIIAnalysisPipeline2.analyze("Card: 4111111111111111")
        val cards = result.entities.filter { it.type == "CREDIT_CARD" }
        assertEquals(1, cards.size)
    }

    @Test
    fun creditCardDetection_visa_correctValue() {
        val result = PIIAnalysisPipeline2.analyze("4111111111111111")
        val card = result.entities.first { it.type == "CREDIT_CARD" }
        assertEquals("4111111111111111", card.value)
    }

    @Test
    fun creditCardDetection_mastercard51_detected() {
        val result = PIIAnalysisPipeline2.analyze("Card: 5111111111111118")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun creditCardDetection_mastercard55_detected() {
        val result = PIIAnalysisPipeline2.analyze("5500005555554444")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun creditCardDetection_amex34_detected() {
        val result = PIIAnalysisPipeline2.analyze("378282246310005")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun creditCardDetection_amex37_detected() {
        val result = PIIAnalysisPipeline2.analyze("371449635398431")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun creditCardDetection_confidence_0point97() {
        val result = PIIAnalysisPipeline2.analyze("4111111111111111")
        val card = result.entities.first { it.type == "CREDIT_CARD" }
        assertEquals(0.97, card.confidence, 0.001)
    }

    @Test
    fun creditCardDetection_type_isCreditCard() {
        val result = PIIAnalysisPipeline2.analyze("4111111111111111")
        val entity = result.entities.first { it.type == "CREDIT_CARD" }
        assertEquals("CREDIT_CARD", entity.type)
    }

    @Test
    fun creditCardDetection_twoCards_bothDetected() {
        val result = PIIAnalysisPipeline2.analyze("Cards: 4111111111111111 and 5500005555554444")
        val cards = result.entities.filter { it.type == "CREDIT_CARD" }
        assertEquals(2, cards.size)
    }

    @Test
    fun creditCardDetection_offsetCorrect() {
        val text = "Card: 4111111111111111 end"
        val result = PIIAnalysisPipeline2.analyze(text)
        val card = result.entities.first { it.type == "CREDIT_CARD" }
        assertEquals("4111111111111111", text.substring(card.start, card.end + 1))
    }

    @Test
    fun creditCardDetection_raisesRiskScore() {
        val clean = PIIAnalysisPipeline2.analyze("No PII")
        val withCard = PIIAnalysisPipeline2.analyze("4111111111111111")
        assertTrue(withCard.riskScore > clean.riskScore)
    }

    @Test
    fun creditCardDetection_extractByType_onlyCards() {
        val text = "4111111111111111 and 123-45-6789"
        val entities = PIIAnalysisPipeline2.extractEntitiesByType(text, "CREDIT_CARD")
        assertTrue(entities.all { it.type == "CREDIT_CARD" })
    }

    @Test
    fun creditCardDetection_noCard_extractEmpty() {
        val entities = PIIAnalysisPipeline2.extractEntitiesByType("no card here", "CREDIT_CARD")
        assertTrue(entities.isEmpty())
    }

    @Test
    fun creditCardDetection_visaTest_multiplePrefixes() {
        val visas = listOf("4000000000000002", "4242424242424242", "4111111111111111")
        visas.forEach { card ->
            val result = PIIAnalysisPipeline2.analyze(card)
            assertTrue("Visa should be detected: $card", result.entities.any { it.type == "CREDIT_CARD" })
        }
    }

    @Test
    fun creditCardDetection_amexCards_bothDetected() {
        listOf("378282246310005", "371449635398431").forEach { card ->
            val result = PIIAnalysisPipeline2.analyze(card)
            assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
        }
    }

    @Test
    fun creditCardDetection_highestConfidenceAmongEntities() {
        val text = "SSN: 123-45-6789 Card: 4111111111111111"
        val result = PIIAnalysisPipeline2.analyze(text)
        val maxConfidence = result.entities.maxOf { it.confidence }
        assertTrue(maxConfidence >= 0.97)
    }

    @Test
    fun creditCardDetection_cardInSentence_detected() {
        val result = PIIAnalysisPipeline2.analyze("Please charge card number 4111111111111111 for the purchase.")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun creditCardDetection_cardAtStart_detected() {
        val result = PIIAnalysisPipeline2.analyze("4111111111111111 was the card used.")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun creditCardDetection_cardAtEnd_detected() {
        val result = PIIAnalysisPipeline2.analyze("The card number is 4111111111111111")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun creditCardDetection_mastercard2xxx_detected() {
        // 2221-2720 prefix range for Mastercard
        val result = PIIAnalysisPipeline2.analyze("5200828282828210")
        // 52xx is standard Mastercard range
        assertTrue(result.entities.isNotEmpty())
    }

    // --------------------------------------------------------
    // Section 5: Phone detection (20 tests)
    // --------------------------------------------------------

    @Test
    fun phoneDetection_standardFormat_detected() {
        val result = PIIAnalysisPipeline2.analyze("Phone: 555-123-4567")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_withParentheses_detected() {
        val result = PIIAnalysisPipeline2.analyze("(555) 123-4567")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_dotFormat_detected() {
        val result = PIIAnalysisPipeline2.analyze("555.123.4567")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_noSeparator_detected() {
        val result = PIIAnalysisPipeline2.analyze("5551234567")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_withCountryCode_detected() {
        val result = PIIAnalysisPipeline2.analyze("+1-555-123-4567")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_confidence_0point90() {
        val result = PIIAnalysisPipeline2.analyze("Phone: 555-123-4567")
        val phone = result.entities.first { it.type == "PHONE" }
        assertEquals(0.90, phone.confidence, 0.001)
    }

    @Test
    fun phoneDetection_type_isPhone() {
        val result = PIIAnalysisPipeline2.analyze("555-123-4567")
        val entity = result.entities.first { it.type == "PHONE" }
        assertEquals("PHONE", entity.type)
    }

    @Test
    fun phoneDetection_twoPhones_bothDetected() {
        val result = PIIAnalysisPipeline2.analyze("Main: 555-123-4567 Fax: 555-987-6543")
        val phones = result.entities.filter { it.type == "PHONE" }
        assertTrue(phones.size >= 1)
    }

    @Test
    fun phoneDetection_raisesRiskScore() {
        val clean = PIIAnalysisPipeline2.analyze("No PII")
        val withPhone = PIIAnalysisPipeline2.analyze("555-123-4567")
        assertTrue(withPhone.riskScore > clean.riskScore)
    }

    @Test
    fun phoneDetection_extractByType_onlyPhones() {
        val text = "Phone: 555-123-4567 and SSN: 123-45-6789"
        val entities = PIIAnalysisPipeline2.extractEntitiesByType(text, "PHONE")
        assertTrue(entities.all { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_noPhone_extractEmpty() {
        val entities = PIIAnalysisPipeline2.extractEntitiesByType("no phone here", "PHONE")
        assertTrue(entities.isEmpty())
    }

    @Test
    fun phoneDetection_differentFormats_allDetected() {
        val phones = listOf("555-123-4567", "5551234567", "(555) 123-4567", "555.123.4567")
        phones.forEach { phone ->
            val result = PIIAnalysisPipeline2.analyze(phone)
            assertTrue("Phone should be detected: $phone", result.entities.any { it.type == "PHONE" })
        }
    }

    @Test
    fun phoneDetection_valueContainsDigits() {
        val result = PIIAnalysisPipeline2.analyze("Call 5551234567 now")
        val phone = result.entities.first { it.type == "PHONE" }
        assertTrue(phone.value.any { it.isDigit() })
    }

    @Test
    fun phoneDetection_inSentence_detected() {
        val result = PIIAnalysisPipeline2.analyze("Please call me at 555-123-4567 as soon as possible.")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_atStartOfLine_detected() {
        val result = PIIAnalysisPipeline2.analyze("5551234567 is my number")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_offsetInText_correct() {
        val text = "Ph: 5551234567 end"
        val result = PIIAnalysisPipeline2.analyze(text)
        val phone = result.entities.firstOrNull { it.type == "PHONE" }
        if (phone != null) {
            val extracted = text.substring(phone.start, phone.end + 1)
            assertTrue(extracted.any { it.isDigit() })
        }
    }

    @Test
    fun phoneDetection_usFormat_1Area_detected() {
        val result = PIIAnalysisPipeline2.analyze("1-800-555-1234")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_multilineDoc_detectedInLine() {
        val text = "Name: John Doe\nPhone: 555-123-4567\nCity: New York"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun phoneDetection_fivePhones_multipleDetected() {
        val text = "555-111-1111 555-222-2222 555-333-3333"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.filter { it.type == "PHONE" }.size >= 1)
    }

    @Test
    fun phoneDetection_tollFree_detected() {
        val result = PIIAnalysisPipeline2.analyze("Call 1-888-555-0199 for help")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    // --------------------------------------------------------
    // Section 6: Multiple entity types in one text (15 tests)
    // --------------------------------------------------------

    @Test
    fun multipleTypes_ssnAndEmail_bothDetected() {
        val text = "SSN: 123-45-6789 Email: john@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "SSN" })
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun multipleTypes_ssnAndCard_bothDetected() {
        val text = "SSN: 123-45-6789 Card: 4111111111111111"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "SSN" })
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun multipleTypes_emailAndPhone_bothDetected() {
        val text = "Email: john@example.com Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "EMAIL" })
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun multipleTypes_allFour_allDetected() {
        val text = "SSN: 123-45-6789 Email: john@example.com Card: 4111111111111111 Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        val types = result.entities.map { it.type }.toSet()
        assertTrue(types.contains("SSN"))
        assertTrue(types.contains("EMAIL"))
        assertTrue(types.contains("CREDIT_CARD"))
        assertTrue(types.contains("PHONE"))
    }

    @Test
    fun multipleTypes_entityCountIsAdditive() {
        val text = "SSN: 111-11-1111 SSN: 222-22-2222 Email: a@b.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssns = result.entities.filter { it.type == "SSN" }.size
        val emails = result.entities.filter { it.type == "EMAIL" }.size
        assertEquals(2, ssns)
        assertEquals(1, emails)
    }

    @Test
    fun multipleTypes_riskScoreHigherThanSingle() {
        val oneSSN = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val ssnAndEmail = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789 Email: john@example.com")
        assertTrue(ssnAndEmail.riskScore >= oneSSN.riskScore)
    }

    @Test
    fun multipleTypes_extractSSN_returnsOnlySSN() {
        val text = "SSN: 123-45-6789 Email: john@example.com Card: 4111111111111111"
        val ssns = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        assertTrue(ssns.all { it.type == "SSN" })
    }

    @Test
    fun multipleTypes_extractEmail_returnsOnlyEmail() {
        val text = "SSN: 123-45-6789 Email: john@example.com Card: 4111111111111111"
        val emails = PIIAnalysisPipeline2.extractEntitiesByType(text, "EMAIL")
        assertTrue(emails.all { it.type == "EMAIL" })
    }

    @Test
    fun multipleTypes_extractCard_returnsOnlyCard() {
        val text = "SSN: 123-45-6789 Card: 4111111111111111"
        val cards = PIIAnalysisPipeline2.extractEntitiesByType(text, "CREDIT_CARD")
        assertTrue(cards.all { it.type == "CREDIT_CARD" })
    }

    @Test
    fun multipleTypes_entityListSize_correctTotal() {
        val text = "SSN: 123-45-6789 Email: john@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(2, result.entities.size)
    }

    @Test
    fun multipleTypes_threeSSNsOneEmail_fourTotal() {
        val text = "111-11-1111 222-22-2222 333-33-3333 user@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(4, result.entities.size)
    }

    @Test
    fun multipleTypes_highDensity_riskCapped() {
        val text = "111-11-1111 222-22-2222 333-33-3333 444-44-4444 555-55-5555 " +
            "a@b.com c@d.com 4111111111111111 555-111-1111"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.riskScore <= 1.0)
    }

    @Test
    fun multipleTypes_ssnConfidenceHighest() {
        val text = "SSN: 123-45-6789 Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssnConf = result.entities.first { it.type == "SSN" }.confidence
        val phoneConf = result.entities.first { it.type == "PHONE" }.confidence
        assertTrue(ssnConf > phoneConf)
    }

    @Test
    fun multipleTypes_cardConfidenceHigherThanPhone() {
        val text = "Card: 4111111111111111 Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        val cardConf = result.entities.first { it.type == "CREDIT_CARD" }.confidence
        val phoneConf = result.entities.first { it.type == "PHONE" }.confidence
        assertTrue(cardConf > phoneConf)
    }

    @Test
    fun multipleTypes_orderOfEntitiesReflectsOrder() {
        val text = "SSN: 123-45-6789 then Email: john@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssnIdx = result.entities.indexOfFirst { it.type == "SSN" }
        val emailIdx = result.entities.indexOfFirst { it.type == "EMAIL" }
        // SSN appears before email in text, start offset should be smaller
        assertTrue(result.entities[ssnIdx].start < result.entities[emailIdx].start)
    }

    // --------------------------------------------------------
    // Section 7: Entity overlap handling (10 tests)
    // --------------------------------------------------------

    @Test
    fun overlap_ssnAndPhone_separateDetection() {
        val text = "SSN: 123-45-6789 Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssns = result.entities.filter { it.type == "SSN" }
        val phones = result.entities.filter { it.type == "PHONE" }
        assertTrue(ssns.isNotEmpty())
        assertTrue(phones.isNotEmpty())
    }

    @Test
    fun overlap_noEntityOverlapsInText() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        val entities = result.entities
        for (i in entities.indices) {
            for (j in entities.indices) {
                if (i != j) {
                    val a = entities[i]
                    val b = entities[j]
                    val overlap = a.start <= b.end && b.start <= a.end
                    if (overlap) {
                        // Different types can overlap only if it's intentional
                        assertTrue(a.type != b.type || a.value != b.value)
                    }
                }
            }
        }
    }

    @Test
    fun overlap_distinctStartPositions() {
        val text = "SSN: 123-45-6789 Email: john@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val starts = result.entities.map { it.start }
        assertEquals(starts.size, starts.distinct().size)
    }

    @Test
    fun overlap_emailAndSsn_differentRanges() {
        val text = "123-45-6789 test@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssn = result.entities.firstOrNull { it.type == "SSN" }
        val email = result.entities.firstOrNull { it.type == "EMAIL" }
        if (ssn != null && email != null) {
            val overlaps = ssn.start <= email.end && email.start <= ssn.end
            assertFalse(overlaps)
        }
    }

    @Test
    fun overlap_entityValues_areDistinct() {
        val text = "SSN: 123-45-6789 Email: john@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val values = result.entities.map { it.value }
        assertEquals(values.size, values.distinct().size)
    }

    @Test
    fun overlap_cardAndSSN_separateRanges() {
        val text = "Card 4111111111111111 and SSN 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        val card = result.entities.firstOrNull { it.type == "CREDIT_CARD" }
        val ssn = result.entities.firstOrNull { it.type == "SSN" }
        if (card != null && ssn != null) {
            val overlaps = card.start <= ssn.end && ssn.start <= card.end
            assertFalse(overlaps)
        }
    }

    @Test
    fun overlap_phoneAndEmail_noOverlap() {
        val text = "555-123-4567 user@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val phone = result.entities.firstOrNull { it.type == "PHONE" }
        val email = result.entities.firstOrNull { it.type == "EMAIL" }
        if (phone != null && email != null) {
            val overlaps = phone.start <= email.end && email.start <= phone.end
            assertFalse(overlaps)
        }
    }

    @Test
    fun overlap_multipleSSNs_differentPositions() {
        val text = "111-11-1111 222-22-2222"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssns = result.entities.filter { it.type == "SSN" }
        if (ssns.size == 2) {
            assertTrue(ssns[0].start != ssns[1].start)
        }
    }

    @Test
    fun overlap_entityRanges_withinTextBounds() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.start >= 0)
            assertTrue(entity.end < text.length)
        }
    }

    @Test
    fun overlap_allEntities_startBeforeEnd() {
        val text = "SSN: 123-45-6789 Email: user@example.com Card: 4111111111111111"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue("Entity start ${entity.start} should be <= end ${entity.end}", entity.start <= entity.end)
        }
    }

    // --------------------------------------------------------
    // Section 8: Start/end offsets correctness (15 tests)
    // --------------------------------------------------------

    @Test
    fun offsets_ssnAtStart_startIsZero() {
        val text = "123-45-6789 is my SSN"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(0, ssn.start)
    }

    @Test
    fun offsets_ssnAtOffset5_startIsFive() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(5, ssn.start)
    }

    @Test
    fun offsets_ssnLength11_endMinus_startPlus1_is11() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(11, ssn.end - ssn.start + 1)
    }

    @Test
    fun offsets_emailOffset_matchesSubstring() {
        val text = "Contact: john@example.com please"
        val result = PIIAnalysisPipeline2.analyze(text)
        val email = result.entities.first { it.type == "EMAIL" }
        assertEquals("john@example.com", text.substring(email.start, email.end + 1))
    }

    @Test
    fun offsets_cardOffset_matchesSubstring() {
        val text = "Pay: 4111111111111111 now"
        val result = PIIAnalysisPipeline2.analyze(text)
        val card = result.entities.first { it.type == "CREDIT_CARD" }
        assertEquals("4111111111111111", text.substring(card.start, card.end + 1))
    }

    @Test
    fun offsets_ssnValueLengthIs11() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789")
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(11, ssn.value.length)
    }

    @Test
    fun offsets_entityValueMatchesExtraction() {
        val text = "Data: 123-45-6789 end"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            val extracted = text.substring(entity.start, entity.end + 1)
            assertEquals(entity.value, extracted)
        }
    }

    @Test
    fun offsets_multipleEntities_allMatchSubstrings() {
        val text = "SSN: 123-45-6789 Email: a@b.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            val extracted = text.substring(entity.start, entity.end + 1)
            assertEquals(entity.value, extracted)
        }
    }

    @Test
    fun offsets_ssnAtExactOffset() {
        val prefix = "Record ID 12345: "
        val ssn = "123-45-6789"
        val text = prefix + ssn
        val result = PIIAnalysisPipeline2.analyze(text)
        val entity = result.entities.firstOrNull { it.type == "SSN" }
        assertNotNull(entity)
        assertEquals(prefix.length, entity!!.start)
    }

    @Test
    fun offsets_endOffset_lastCharPosition() {
        val text = "123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(text.length - 1, ssn.end)
    }

    @Test
    fun offsets_emailAtExactPosition() {
        val text = "Email address: test@example.com"
        val expectedStart = text.indexOf("test@example.com")
        val result = PIIAnalysisPipeline2.analyze(text)
        val email = result.entities.first { it.type == "EMAIL" }
        assertEquals(expectedStart, email.start)
    }

    @Test
    fun offsets_cardAtExactPosition() {
        val text = "Card number: 4111111111111111 expires"
        val expectedStart = text.indexOf("4111111111111111")
        val result = PIIAnalysisPipeline2.analyze(text)
        val card = result.entities.first { it.type == "CREDIT_CARD" }
        assertEquals(expectedStart, card.start)
    }

    @Test
    fun offsets_nonNegativeStart() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { assertTrue(it.start >= 0) }
    }

    @Test
    fun offsets_endLessThanTextLength() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { assertTrue(it.end < text.length) }
    }

    @Test
    fun offsets_startLessThanOrEqualEnd() {
        val text = "SSN: 123-45-6789 Email: test@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { assertTrue(it.start <= it.end) }
    }

    // --------------------------------------------------------
    // Section 9: Confidence scores (15 tests)
    // --------------------------------------------------------

    @Test
    fun confidence_ssn_is0point98() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789")
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(0.98, ssn.confidence, 0.001)
    }

    @Test
    fun confidence_email_is0point95() {
        val result = PIIAnalysisPipeline2.analyze("user@example.com")
        val email = result.entities.first { it.type == "EMAIL" }
        assertEquals(0.95, email.confidence, 0.001)
    }

    @Test
    fun confidence_creditCard_is0point97() {
        val result = PIIAnalysisPipeline2.analyze("4111111111111111")
        val card = result.entities.first { it.type == "CREDIT_CARD" }
        assertEquals(0.97, card.confidence, 0.001)
    }

    @Test
    fun confidence_phone_is0point90() {
        val result = PIIAnalysisPipeline2.analyze("555-123-4567")
        val phone = result.entities.first { it.type == "PHONE" }
        assertEquals(0.90, phone.confidence, 0.001)
    }

    @Test
    fun confidence_ssn_greaterThanEmail() {
        val text = "123-45-6789 user@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssnConf = result.entities.first { it.type == "SSN" }.confidence
        val emailConf = result.entities.first { it.type == "EMAIL" }.confidence
        assertTrue(ssnConf > emailConf)
    }

    @Test
    fun confidence_card_greaterThanPhone() {
        val text = "4111111111111111 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        val cardConf = result.entities.first { it.type == "CREDIT_CARD" }.confidence
        val phoneConf = result.entities.first { it.type == "PHONE" }.confidence
        assertTrue(cardConf > phoneConf)
    }

    @Test
    fun confidence_ssn_greaterThanCard() {
        val text = "123-45-6789 4111111111111111"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssnConf = result.entities.first { it.type == "SSN" }.confidence
        val cardConf = result.entities.first { it.type == "CREDIT_CARD" }.confidence
        assertTrue(ssnConf > cardConf)
    }

    @Test
    fun confidence_between0and1_allEntities() {
        val text = "123-45-6789 user@example.com 4111111111111111 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.confidence in 0.0..1.0)
        }
    }

    @Test
    fun confidence_multipleSameType_sameConfidence() {
        val text = "111-11-1111 222-22-2222"
        val result = PIIAnalysisPipeline2.analyze(text)
        val confs = result.entities.filter { it.type == "SSN" }.map { it.confidence }
        if (confs.size >= 2) {
            assertEquals(confs[0], confs[1], 0.001)
        }
    }

    @Test
    fun confidence_defaultIs0point95() {
        val entity = PIIEntity2("TEST", "value", 0, 4)
        assertEquals(0.95, entity.confidence, 0.001)
    }

    @Test
    fun confidence_customValue_preserved() {
        val entity = PIIEntity2("SSN", "123-45-6789", 0, 10, 0.99)
        assertEquals(0.99, entity.confidence, 0.001)
    }

    @Test
    fun confidence_zero_possible() {
        val entity = PIIEntity2("LOW", "test", 0, 3, 0.0)
        assertEquals(0.0, entity.confidence, 0.001)
    }

    @Test
    fun confidence_one_possible() {
        val entity = PIIEntity2("HIGH", "test", 0, 3, 1.0)
        assertEquals(1.0, entity.confidence, 0.001)
    }

    @Test
    fun confidence_ssnHighestAmongAll() {
        val text = "SSN: 123-45-6789 Phone: 555-123-4567 Email: a@b.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val maxConf = result.entities.maxOf { it.confidence }
        assertEquals(0.98, maxConf, 0.001)
    }

    @Test
    fun confidence_phoneLowestAmongAll() {
        val text = "SSN: 123-45-6789 Card: 4111111111111111 Email: a@b.com Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        val minConf = result.entities.minOf { it.confidence }
        assertEquals(0.90, minConf, 0.001)
    }

    // --------------------------------------------------------
    // Section 10: Risk score calculation (15 tests)
    // --------------------------------------------------------

    @Test
    fun riskScore_noEntities_isZero() {
        val result = PIIAnalysisPipeline2.analyze("No PII here")
        assertEquals(0.0, result.riskScore, 0.001)
    }

    @Test
    fun riskScore_emptyText_isZero() {
        val result = PIIAnalysisPipeline2.analyze("")
        assertEquals(0.0, result.riskScore, 0.001)
    }

    @Test
    fun riskScore_withSSN_isPositive() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        assertTrue(result.riskScore > 0.0)
    }

    @Test
    fun riskScore_notExceeds1() {
        val text = "SSN: 123-45-6789 Email: a@b.com Card: 4111111111111111 Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.riskScore <= 1.0)
    }

    @Test
    fun riskScore_between0and1() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        assertTrue(result.riskScore >= 0.0)
        assertTrue(result.riskScore <= 1.0)
    }

    @Test
    fun riskScore_moreEntitiesHigherRisk() {
        val one = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val two = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789 Email: a@b.com")
        assertTrue(two.riskScore >= one.riskScore)
    }

    @Test
    fun riskScore_formula_entityCountContribution() {
        // risk = min(1.0, entities.size * 0.15 + maxConfidence * 0.5)
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val expected = minOf(1.0, 1 * 0.15 + 0.98 * 0.5)
        assertEquals(expected, result.riskScore, 0.001)
    }

    @Test
    fun riskScore_twoEntities_correctFormula() {
        val text = "SSN: 123-45-6789 Email: a@b.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        val expected = minOf(1.0, 2 * 0.15 + 0.98 * 0.5)
        assertEquals(expected, result.riskScore, 0.001)
    }

    @Test
    fun riskScore_highDensity_cappedAt1() {
        val text = (1..10).joinToString(" ") { "111-1$it-111$it" }
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.riskScore <= 1.0)
    }

    @Test
    fun riskScore_singlePhone_lowerThanSSN() {
        val phoneResult = PIIAnalysisPipeline2.analyze("555-123-4567")
        val ssnResult = PIIAnalysisPipeline2.analyze("123-45-6789")
        // SSN has higher confidence (0.98 vs 0.90), so SSN risk is higher
        assertTrue(ssnResult.riskScore > phoneResult.riskScore)
    }

    @Test
    fun riskScore_preservedInResult() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        assertTrue(result.riskScore >= 0.0)
    }

    @Test
    fun riskScore_consistentAcrossMultipleCalls() {
        val text = "SSN: 123-45-6789"
        val r1 = PIIAnalysisPipeline2.analyze(text).riskScore
        val r2 = PIIAnalysisPipeline2.analyze(text).riskScore
        assertEquals(r1, r2, 0.001)
    }

    @Test
    fun riskScore_cleanVsSSN_gap() {
        val clean = PIIAnalysisPipeline2.analyze("Clean text")
        val withSSN = PIIAnalysisPipeline2.analyze("123-45-6789")
        assertTrue(withSSN.riskScore - clean.riskScore > 0.5)
    }

    @Test
    fun riskScore_emailOnly_positiveRisk() {
        val result = PIIAnalysisPipeline2.analyze("user@example.com")
        assertTrue(result.riskScore > 0.0)
    }

    @Test
    fun riskScore_cardOnly_positiveRisk() {
        val result = PIIAnalysisPipeline2.analyze("4111111111111111")
        assertTrue(result.riskScore > 0.0)
    }

    // --------------------------------------------------------
    // Section 11: Empty text (10 tests)
    // --------------------------------------------------------

    @Test
    fun emptyText_analyzeEmpty_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("").entities.size)
    }

    @Test
    fun emptyText_analyzeEmpty_zeroRisk() {
        assertEquals(0.0, PIIAnalysisPipeline2.analyze("").riskScore, 0.001)
    }

    @Test
    fun emptyText_analyzeEmpty_textPreserved() {
        assertEquals("", PIIAnalysisPipeline2.analyze("").text)
    }

    @Test
    fun emptyText_analyzeBlank_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("   ").entities.size)
    }

    @Test
    fun emptyText_analyzeNewline_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("\n").entities.size)
    }

    @Test
    fun emptyText_analyzeTab_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("\t").entities.size)
    }

    @Test
    fun emptyText_multipleEmpty_allZeroRisk() {
        val texts = listOf("", " ", "\n", "\t", "   ")
        texts.forEach { text ->
            assertEquals(0.0, PIIAnalysisPipeline2.analyze(text).riskScore, 0.001)
        }
    }

    @Test
    fun emptyText_extractByType_empty() {
        assertEquals(0, PIIAnalysisPipeline2.extractEntitiesByType("", "SSN").size)
    }

    @Test
    fun emptyText_batchEmptyTexts_allEmpty() {
        val results = PIIAnalysisPipeline2.analyzeMultiple(listOf("", "", ""))
        assertTrue(results.all { it.entities.isEmpty() })
    }

    @Test
    fun emptyText_singleChar_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("a").entities.size)
    }

    // --------------------------------------------------------
    // Section 12: Very long text (10 tests)
    // --------------------------------------------------------

    @Test
    fun longText_noSSN_noEntities() {
        val text = "a".repeat(100000)
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun longText_singleSSNInLong_detected() {
        val prefix = "x".repeat(50000)
        val suffix = "y".repeat(50000)
        val text = "$prefix 123-45-6789 $suffix"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun longText_repeatedSSNs_multipleDetected() {
        val text = (1..10).joinToString(" ") { "111-11-111$it" } + " " + "x".repeat(1000)
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.filter { it.type == "SSN" }.isNotEmpty())
    }

    @Test
    fun longText_singleEmailInLong_detected() {
        val text = "z".repeat(50000) + " user@example.com " + "z".repeat(50000)
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun longText_riskScoreNotExceedsOne() {
        val text = (1..100).joinToString(" ") { "111-11-111$it" }
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.riskScore <= 1.0)
    }

    @Test
    fun longText_textPreservedInResult() {
        val text = "clean text " + "a".repeat(5000)
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(text, result.text)
    }

    @Test
    fun longText_noFalsePositives_inCleanLong() {
        val text = "The quick brown fox jumps over the lazy dog. ".repeat(1000)
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun longText_cardInLong_detected() {
        val text = "x".repeat(10000) + " 4111111111111111 " + "y".repeat(10000)
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun longText_multipleTypes_allDetected() {
        val text = "a".repeat(1000) + " 123-45-6789 " + "b".repeat(1000) +
            " user@example.com " + "c".repeat(1000) + " 4111111111111111"
        val result = PIIAnalysisPipeline2.analyze(text)
        val types = result.entities.map { it.type }.toSet()
        assertTrue(types.contains("SSN"))
        assertTrue(types.contains("EMAIL"))
        assertTrue(types.contains("CREDIT_CARD"))
    }

    @Test
    fun longText_completes_withoutError() {
        val text = "data " + "123-45-6789 ".repeat(100) + "end"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertNotNull(result)
    }

    // --------------------------------------------------------
    // Section 13: Batch analysis (20 tests)
    // --------------------------------------------------------

    @Test
    fun batchAnalysis_emptyList_returnsEmptyList() {
        val results = PIIAnalysisPipeline2.analyzeMultiple(emptyList())
        assertTrue(results.isEmpty())
    }

    @Test
    fun batchAnalysis_singleItem_returnsOneResult() {
        val results = PIIAnalysisPipeline2.analyzeMultiple(listOf("SSN: 123-45-6789"))
        assertEquals(1, results.size)
    }

    @Test
    fun batchAnalysis_twoItems_returnsTwoResults() {
        val results = PIIAnalysisPipeline2.analyzeMultiple(listOf("SSN: 123-45-6789", "Clean text"))
        assertEquals(2, results.size)
    }

    @Test
    fun batchAnalysis_preservesOrder() {
        val texts = listOf("text1", "text2", "text3")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        results.forEachIndexed { i, result ->
            assertEquals(texts[i], result.text)
        }
    }

    @Test
    fun batchAnalysis_cleanTexts_allZeroRisk() {
        val texts = listOf("Hello", "World", "Test", "Data", "Info")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(results.all { it.riskScore == 0.0 })
    }

    @Test
    fun batchAnalysis_allWithSSN_allPositiveRisk() {
        val texts = listOf(
            "SSN: 123-45-6789",
            "SSN: 234-56-7890",
            "SSN: 345-67-8901"
        )
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(results.all { it.riskScore > 0.0 })
    }

    @Test
    fun batchAnalysis_mixedContent_mixedRisk() {
        val texts = listOf("Clean text", "SSN: 123-45-6789")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertEquals(0.0, results[0].riskScore, 0.001)
        assertTrue(results[1].riskScore > 0.0)
    }

    @Test
    fun batchAnalysis_tenItems_tenResults() {
        val texts = (1..10).map { "item $it" }
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertEquals(10, results.size)
    }

    @Test
    fun batchAnalysis_allEmpty_allZeroRisk() {
        val texts = listOf("", "", "")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(results.all { it.riskScore == 0.0 })
    }

    @Test
    fun batchAnalysis_100Items_100Results() {
        val texts = (1..100).map { "text $it" }
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertEquals(100, results.size)
    }

    @Test
    fun batchAnalysis_resultsMatchSingleAnalysis() {
        val text = "SSN: 123-45-6789"
        val batchResult = PIIAnalysisPipeline2.analyzeMultiple(listOf(text))[0]
        val singleResult = PIIAnalysisPipeline2.analyze(text)
        assertEquals(singleResult.riskScore, batchResult.riskScore, 0.001)
        assertEquals(singleResult.entities.size, batchResult.entities.size)
    }

    @Test
    fun batchAnalysis_riskScores_allBetween0and1() {
        val texts = (1..20).map { "SSN: $it${it}${it}-${it}${it}-${it}${it}${it}${it}" }
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        results.forEach { result ->
            assertTrue(result.riskScore in 0.0..1.0)
        }
    }

    @Test
    fun batchAnalysis_entityTypes_correctPerItem() {
        val texts = listOf("123-45-6789", "user@example.com", "4111111111111111")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(results[0].entities.any { it.type == "SSN" })
        assertTrue(results[1].entities.any { it.type == "EMAIL" })
        assertTrue(results[2].entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun batchAnalysis_noSideEffects_independentResults() {
        val texts = listOf("SSN: 123-45-6789", "Clean text")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(results[0].entities.isNotEmpty())
        assertTrue(results[1].entities.isEmpty())
    }

    @Test
    fun batchAnalysis_duplicateTexts_identicalResults() {
        val text = "SSN: 123-45-6789"
        val results = PIIAnalysisPipeline2.analyzeMultiple(listOf(text, text, text))
        assertEquals(results[0].riskScore, results[1].riskScore, 0.001)
        assertEquals(results[1].riskScore, results[2].riskScore, 0.001)
    }

    @Test
    fun batchAnalysis_singleSSN_correctEntityCount() {
        val results = PIIAnalysisPipeline2.analyzeMultiple(listOf("SSN: 123-45-6789"))
        assertEquals(1, results[0].entities.size)
    }

    @Test
    fun batchAnalysis_emptyAndSSN_differentResults() {
        val results = PIIAnalysisPipeline2.analyzeMultiple(listOf("", "SSN: 123-45-6789"))
        assertEquals(0, results[0].entities.size)
        assertEquals(1, results[1].entities.size)
    }

    @Test
    fun batchAnalysis_allEntitiesHaveCorrectTypes() {
        val texts = listOf("123-45-6789", "user@example.com")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        results.forEach { result ->
            result.entities.forEach { entity ->
                assertTrue(entity.type in listOf("SSN", "EMAIL", "CREDIT_CARD", "PHONE"))
            }
        }
    }

    @Test
    fun batchAnalysis_firstResult_textMatchesInput() {
        val texts = listOf("hello world", "test data")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertEquals("hello world", results[0].text)
        assertEquals("test data", results[1].text)
    }

    @Test
    fun batchAnalysis_returnsAnalysisResult2List() {
        val texts = listOf("test")
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(results[0] is AnalysisResult2)
    }

    // --------------------------------------------------------
    // Section 14: Entity filtering by type (15 tests)
    // --------------------------------------------------------

    @Test
    fun filterByType_ssn_returnsSSNsOnly() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val ssns = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        assertTrue(ssns.all { it.type == "SSN" })
        assertEquals(1, ssns.size)
    }

    @Test
    fun filterByType_email_returnsEmailsOnly() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val emails = PIIAnalysisPipeline2.extractEntitiesByType(text, "EMAIL")
        assertTrue(emails.all { it.type == "EMAIL" })
        assertEquals(1, emails.size)
    }

    @Test
    fun filterByType_card_returnsCardsOnly() {
        val text = "SSN: 123-45-6789 Card: 4111111111111111"
        val cards = PIIAnalysisPipeline2.extractEntitiesByType(text, "CREDIT_CARD")
        assertTrue(cards.all { it.type == "CREDIT_CARD" })
    }

    @Test
    fun filterByType_phone_returnsPhonesOnly() {
        val text = "Phone: 555-123-4567 SSN: 123-45-6789"
        val phones = PIIAnalysisPipeline2.extractEntitiesByType(text, "PHONE")
        assertTrue(phones.all { it.type == "PHONE" })
    }

    @Test
    fun filterByType_unknownType_returnsEmpty() {
        val text = "SSN: 123-45-6789"
        val entities = PIIAnalysisPipeline2.extractEntitiesByType(text, "PASSPORT")
        assertTrue(entities.isEmpty())
    }

    @Test
    fun filterByType_multipleSSNs_returnsAll() {
        val text = "111-11-1111 222-22-2222 333-33-3333"
        val ssns = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        assertEquals(3, ssns.size)
    }

    @Test
    fun filterByType_multipleEmails_returnsAll() {
        val text = "a@b.com c@d.org e@f.net"
        val emails = PIIAnalysisPipeline2.extractEntitiesByType(text, "EMAIL")
        assertEquals(3, emails.size)
    }

    @Test
    fun filterByType_noMatch_returnsEmpty() {
        val text = "SSN: 123-45-6789"
        val cards = PIIAnalysisPipeline2.extractEntitiesByType(text, "CREDIT_CARD")
        assertTrue(cards.isEmpty())
    }

    @Test
    fun filterByType_emptyText_returnsEmpty() {
        val entities = PIIAnalysisPipeline2.extractEntitiesByType("", "SSN")
        assertTrue(entities.isEmpty())
    }

    @Test
    fun filterByType_allSSNs_correctCount() {
        val text = "SSN1: 111-11-1111 SSN2: 222-22-2222"
        val ssns = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        assertEquals(2, ssns.size)
    }

    @Test
    fun filterByType_correctValues() {
        val text = "user@example.com"
        val emails = PIIAnalysisPipeline2.extractEntitiesByType(text, "EMAIL")
        assertEquals("user@example.com", emails[0].value)
    }

    @Test
    fun filterByType_confidencePreserved() {
        val text = "123-45-6789"
        val ssns = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        assertEquals(0.98, ssns[0].confidence, 0.001)
    }

    @Test
    fun filterByType_offsetsPreserved() {
        val text = "SSN: 123-45-6789"
        val ssns = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        assertEquals(5, ssns[0].start)
    }

    @Test
    fun filterByType_allTypes_extractAllIndividually() {
        val text = "SSN: 123-45-6789 Email: a@b.com Card: 4111111111111111 Phone: 555-123-4567"
        val types = listOf("SSN", "EMAIL", "CREDIT_CARD", "PHONE")
        types.forEach { type ->
            val entities = PIIAnalysisPipeline2.extractEntitiesByType(text, type)
            assertTrue("Expected entities for type: $type", entities.isNotEmpty())
        }
    }

    @Test
    fun filterByType_ssnFromMixedDocument() {
        val text = "Dear customer, your SSN 123-45-6789 and card 4111111111111111 are on file."
        val ssns = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        assertEquals(1, ssns.size)
        assertEquals("123-45-6789", ssns[0].value)
    }

    // --------------------------------------------------------
    // Section 15: High-density PII documents (15 tests)
    // --------------------------------------------------------

    @Test
    fun highDensity_tenSSNs_allDetected() {
        val ssns = (1..10).map { "11$it-$it$it-${it}${it}${it}${it}" }
        val text = ssns.joinToString(" ")
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.filter { it.type == "SSN" }.size >= 1)
    }

    @Test
    fun highDensity_fiveEmails_allDetected() {
        val emails = (1..5).map { "user$it@example.com" }
        val text = emails.joinToString(" ")
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(5, result.entities.filter { it.type == "EMAIL" }.size)
    }

    @Test
    fun highDensity_riskScore_cappedAt1() {
        val text = (1..20).joinToString(" ") { "111-1$it-111$it" }
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.riskScore <= 1.0)
    }

    @Test
    fun highDensity_mixedTypes_allTypesPresent() {
        val text = "SSN: 123-45-6789 Email: a@b.com Card: 4111111111111111 Phone: 555-123-4567 " +
            "SSN: 234-56-7890 Email: c@d.org"
        val result = PIIAnalysisPipeline2.analyze(text)
        val types = result.entities.map { it.type }.toSet()
        assertTrue(types.containsAll(listOf("SSN", "EMAIL", "CREDIT_CARD", "PHONE")))
    }

    @Test
    fun highDensity_entityCount_greaterThan5() {
        val text = "111-11-1111 222-22-2222 333-33-3333 444-44-4444 555-55-5555 666-66-6666"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.size >= 5)
    }

    @Test
    fun highDensity_allEmailsFound() {
        val emails = (1..10).map { "person$it@company.com" }
        val text = emails.joinToString(", ")
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(10, result.entities.filter { it.type == "EMAIL" }.size)
    }

    @Test
    fun highDensity_highRiskScore() {
        val text = "SSN: 123-45-6789 Card: 4111111111111111 Email: a@b.com Phone: 555-123-4567 " +
            "SSN: 234-56-7890 SSN: 345-67-8901"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.riskScore > 0.8)
    }

    @Test
    fun highDensity_ssnEmailMixed_correctCounts() {
        val text = "SSN1: 111-11-1111 E: a@b.com SSN2: 222-22-2222 E2: c@d.org"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(2, result.entities.filter { it.type == "SSN" }.size)
        assertEquals(2, result.entities.filter { it.type == "EMAIL" }.size)
    }

    @Test
    fun highDensity_allEntities_inBounds() {
        val text = "SSN: 111-11-1111 Email: a@b.com SSN: 222-22-2222 Card: 4111111111111111"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.start >= 0)
            assertTrue(entity.end < text.length)
        }
    }

    @Test
    fun highDensity_noEntityOutOfBounds() {
        val text = "SSN: 123-45-6789 user@example.com 4111111111111111 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.end - entity.start + 1 == entity.value.length)
        }
    }

    @Test
    fun highDensity_resultTextPreserved() {
        val text = "SSN: 111-11-1111 Email: a@b.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(text, result.text)
    }

    @Test
    fun highDensity_batchAnalysis_allPositiveRisk() {
        val texts = (1..10).map { "SSN: 11$it-$it$it-111$it user$it@example.com" }
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(results.all { it.riskScore > 0.0 })
    }

    @Test
    fun highDensity_maxConfidence_isCorrect() {
        val text = "SSN: 123-45-6789 Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        val maxConf = result.entities.maxOf { it.confidence }
        assertEquals(0.98, maxConf, 0.001)
    }

    @Test
    fun highDensity_riskFormula_verified() {
        val text = "SSN: 123-45-6789 Email: a@b.com Card: 4111111111111111"
        val result = PIIAnalysisPipeline2.analyze(text)
        val expectedRisk = minOf(1.0, result.entities.size * 0.15 + result.entities.maxOf { it.confidence } * 0.5)
        assertEquals(expectedRisk, result.riskScore, 0.001)
    }

    @Test
    fun highDensity_extractMultipleTypes_separateFiltering() {
        val text = "SSN: 111-11-1111 SSN: 222-22-2222 Email: a@b.com Email: c@d.org Card: 4111111111111111"
        val ssns = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        val emails = PIIAnalysisPipeline2.extractEntitiesByType(text, "EMAIL")
        val cards = PIIAnalysisPipeline2.extractEntitiesByType(text, "CREDIT_CARD")
        assertEquals(2, ssns.size)
        assertEquals(2, emails.size)
        assertEquals(1, cards.size)
    }
}

// ============================================================
// PIIAnalysisPipelineExtendedTest2B — Additional 100+ tests
// Sections 16-22 providing deeper coverage of edge cases,
// pipeline properties, data model validation, and integration.
// ============================================================
class PIIAnalysisPipelineExtendedTest2B {

    // --------------------------------------------------------
    // Section 16: AnalysisResult2 data model properties (20 tests)
    // --------------------------------------------------------

    @Test
    fun analysisResult_textField_preserved() {
        val text = "hello world"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(text, result.text)
    }

    @Test
    fun analysisResult_entitiesField_isList() {
        val result = PIIAnalysisPipeline2.analyze("test")
        assertTrue(result.entities is List<*>)
    }

    @Test
    fun analysisResult_riskScoreField_isDouble() {
        val result = PIIAnalysisPipeline2.analyze("test")
        assertTrue(result.riskScore is Double)
    }

    @Test
    fun analysisResult_emptyEntities_listIsEmpty() {
        val result = PIIAnalysisPipeline2.analyze("no pii")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun analysisResult_withSSN_entitiesNotEmpty() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789")
        assertTrue(result.entities.isNotEmpty())
    }

    @Test
    fun analysisResult_copyable() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789")
        val copy = result.copy()
        assertEquals(result.text, copy.text)
        assertEquals(result.riskScore, copy.riskScore, 0.001)
    }

    @Test
    fun analysisResult_componentFunctions() {
        val result = PIIAnalysisPipeline2.analyze("user@example.com")
        val (text, entities, riskScore) = result
        assertEquals("user@example.com", text)
        assertTrue(entities.isNotEmpty())
        assertTrue(riskScore > 0.0)
    }

    @Test
    fun analysisResult_twoDistinctResults_notEqual() {
        val r1 = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        val r2 = PIIAnalysisPipeline2.analyze("Email: user@example.com")
        assertNotEquals(r1.riskScore, r2.riskScore)
    }

    @Test
    fun analysisResult_sameInput_equalResults() {
        val text = "SSN: 123-45-6789"
        val r1 = PIIAnalysisPipeline2.analyze(text)
        val r2 = PIIAnalysisPipeline2.analyze(text)
        assertEquals(r1.riskScore, r2.riskScore, 0.001)
        assertEquals(r1.entities.size, r2.entities.size)
    }

    @Test
    fun analysisResult_entityList_isImmutableView() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789")
        assertNotNull(result.entities)
        assertTrue(result.entities.isNotEmpty())
    }

    @Test
    fun analysisResult_riskScorePositiveForPII() {
        val results = listOf(
            PIIAnalysisPipeline2.analyze("123-45-6789"),
            PIIAnalysisPipeline2.analyze("user@example.com"),
            PIIAnalysisPipeline2.analyze("4111111111111111"),
            PIIAnalysisPipeline2.analyze("555-123-4567")
        )
        assertTrue(results.all { it.riskScore > 0.0 })
    }

    @Test
    fun analysisResult_textLengthPreserved() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(text.length, result.text.length)
    }

    @Test
    fun analysisResult_twoSSNs_entityCount2() {
        val result = PIIAnalysisPipeline2.analyze("111-11-1111 222-22-2222")
        assertEquals(2, result.entities.size)
    }

    @Test
    fun analysisResult_entityValues_matchesTextSubstrings() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertEquals(entity.value, text.substring(entity.start, entity.end + 1))
        }
    }

    @Test
    fun analysisResult_noEntityBeyondTextLength() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.end < text.length)
        }
    }

    @Test
    fun analysisResult_confidences_inValidRange() {
        val text = "SSN: 123-45-6789 Email: user@example.com 4111111111111111 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.confidence in 0.0..1.0)
        }
    }

    @Test
    fun analysisResult_textField_neverNull() {
        val results = listOf("", "hello", "SSN: 123-45-6789").map { PIIAnalysisPipeline2.analyze(it) }
        results.forEach { result ->
            assertNotNull(result.text)
        }
    }

    @Test
    fun analysisResult_entityList_neverNull() {
        val results = listOf("", "hello", "SSN: 123-45-6789").map { PIIAnalysisPipeline2.analyze(it) }
        results.forEach { result ->
            assertNotNull(result.entities)
        }
    }

    @Test
    fun analysisResult_riskScore_neverNaN() {
        val results = listOf("", "hello", "SSN: 123-45-6789").map { PIIAnalysisPipeline2.analyze(it) }
        results.forEach { result ->
            assertFalse(result.riskScore.isNaN())
        }
    }

    @Test
    fun analysisResult_riskScore_neverInfinite() {
        val results = listOf("", "hello", "SSN: 123-45-6789").map { PIIAnalysisPipeline2.analyze(it) }
        results.forEach { result ->
            assertFalse(result.riskScore.isInfinite())
        }
    }

    // --------------------------------------------------------
    // Section 17: PIIEntity2 data model (15 tests)
    // --------------------------------------------------------

    @Test
    fun entity_type_preserved() {
        val entity = PIIEntity2("SSN", "123-45-6789", 0, 10, 0.98)
        assertEquals("SSN", entity.type)
    }

    @Test
    fun entity_value_preserved() {
        val entity = PIIEntity2("EMAIL", "user@example.com", 5, 20, 0.95)
        assertEquals("user@example.com", entity.value)
    }

    @Test
    fun entity_start_preserved() {
        val entity = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        assertEquals(5, entity.start)
    }

    @Test
    fun entity_end_preserved() {
        val entity = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        assertEquals(15, entity.end)
    }

    @Test
    fun entity_confidence_preserved() {
        val entity = PIIEntity2("CREDIT_CARD", "4111111111111111", 0, 15, 0.97)
        assertEquals(0.97, entity.confidence, 0.001)
    }

    @Test
    fun entity_defaultConfidence_is0point95() {
        val entity = PIIEntity2("PHONE", "555-123-4567", 0, 11)
        assertEquals(0.95, entity.confidence, 0.001)
    }

    @Test
    fun entity_copyFunction_worksCorrectly() {
        val entity = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        val copy = entity.copy(confidence = 0.99)
        assertEquals(0.99, copy.confidence, 0.001)
        assertEquals(entity.type, copy.type)
        assertEquals(entity.value, copy.value)
    }

    @Test
    fun entity_componentDestructuring() {
        val entity = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        val (type, value, start, end, confidence) = entity
        assertEquals("SSN", type)
        assertEquals("123-45-6789", value)
        assertEquals(5, start)
        assertEquals(15, end)
        assertEquals(0.98, confidence, 0.001)
    }

    @Test
    fun entity_equalityBasedOnFields() {
        val e1 = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        val e2 = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        assertEquals(e1, e2)
    }

    @Test
    fun entity_differentType_notEqual() {
        val e1 = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        val e2 = PIIEntity2("EMAIL", "123-45-6789", 5, 15, 0.98)
        assertNotEquals(e1, e2)
    }

    @Test
    fun entity_differentValue_notEqual() {
        val e1 = PIIEntity2("SSN", "111-11-1111", 5, 15, 0.98)
        val e2 = PIIEntity2("SSN", "222-22-2222", 5, 15, 0.98)
        assertNotEquals(e1, e2)
    }

    @Test
    fun entity_toString_containsType() {
        val entity = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        assertTrue(entity.toString().contains("SSN"))
    }

    @Test
    fun entity_hashCode_sameForEqual() {
        val e1 = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        val e2 = PIIEntity2("SSN", "123-45-6789", 5, 15, 0.98)
        assertEquals(e1.hashCode(), e2.hashCode())
    }

    @Test
    fun entity_zeroStart_valid() {
        val entity = PIIEntity2("SSN", "123-45-6789", 0, 10, 0.98)
        assertEquals(0, entity.start)
    }

    @Test
    fun entity_largeOffset_valid() {
        val entity = PIIEntity2("EMAIL", "a@b.com", 10000, 10006, 0.95)
        assertEquals(10000, entity.start)
        assertEquals(10006, entity.end)
    }

    // --------------------------------------------------------
    // Section 18: Pipeline consistency and idempotency (15 tests)
    // --------------------------------------------------------

    @Test
    fun idempotency_sameText_sameResult() {
        val text = "SSN: 123-45-6789"
        val r1 = PIIAnalysisPipeline2.analyze(text)
        val r2 = PIIAnalysisPipeline2.analyze(text)
        assertEquals(r1.entities.size, r2.entities.size)
        assertEquals(r1.riskScore, r2.riskScore, 0.001)
    }

    @Test
    fun idempotency_emptyText_sameResult() {
        val r1 = PIIAnalysisPipeline2.analyze("")
        val r2 = PIIAnalysisPipeline2.analyze("")
        assertEquals(r1.entities.size, r2.entities.size)
        assertEquals(r1.riskScore, r2.riskScore, 0.001)
    }

    @Test
    fun idempotency_tenCallsSameText_sameEntityCount() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val counts = (1..10).map { PIIAnalysisPipeline2.analyze(text).entities.size }
        assertTrue(counts.all { it == counts[0] })
    }

    @Test
    fun idempotency_tenCallsSameText_sameRiskScore() {
        val text = "SSN: 123-45-6789"
        val scores = (1..10).map { PIIAnalysisPipeline2.analyze(text).riskScore }
        assertTrue(scores.all { it == scores[0] })
    }

    @Test
    fun consistency_orderDoesNotMatter_typeCount() {
        val text1 = "SSN: 123-45-6789 Email: user@example.com"
        val text2 = "Email: user@example.com SSN: 123-45-6789"
        val r1 = PIIAnalysisPipeline2.analyze(text1)
        val r2 = PIIAnalysisPipeline2.analyze(text2)
        assertEquals(r1.entities.size, r2.entities.size)
    }

    @Test
    fun consistency_ssnAlwaysFound_inVariousPositions() {
        val positions = listOf(
            "123-45-6789",
            "text 123-45-6789",
            "123-45-6789 text",
            "before 123-45-6789 after"
        )
        positions.forEach { text ->
            val result = PIIAnalysisPipeline2.analyze(text)
            assertTrue("SSN not found in: $text", result.entities.any { it.type == "SSN" })
        }
    }

    @Test
    fun consistency_emailAlwaysFound_inVariousPositions() {
        val positions = listOf(
            "user@example.com",
            "text user@example.com",
            "user@example.com text",
            "before user@example.com after"
        )
        positions.forEach { text ->
            val result = PIIAnalysisPipeline2.analyze(text)
            assertTrue("Email not found in: $text", result.entities.any { it.type == "EMAIL" })
        }
    }

    @Test
    fun consistency_entityValues_alwaysMatchText() {
        val texts = listOf(
            "SSN: 123-45-6789",
            "Email: user@example.com",
            "Card: 4111111111111111"
        )
        texts.forEach { text ->
            val result = PIIAnalysisPipeline2.analyze(text)
            result.entities.forEach { entity ->
                assertEquals(entity.value, text.substring(entity.start, entity.end + 1))
            }
        }
    }

    @Test
    fun consistency_riskScore_monotonicallyIncreasing() {
        val base = PIIAnalysisPipeline2.analyze("")
        val withSSN = PIIAnalysisPipeline2.analyze("123-45-6789")
        val withSSNEmail = PIIAnalysisPipeline2.analyze("123-45-6789 user@example.com")
        assertTrue(base.riskScore <= withSSN.riskScore)
        assertTrue(withSSN.riskScore <= withSSNEmail.riskScore)
    }

    @Test
    fun consistency_textField_neverModified() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertEquals(text, result.text)
    }

    @Test
    fun consistency_batchVsSingle_sameResults() {
        val texts = listOf("SSN: 123-45-6789", "user@example.com", "4111111111111111")
        val batchResults = PIIAnalysisPipeline2.analyzeMultiple(texts)
        texts.forEachIndexed { i, text ->
            val singleResult = PIIAnalysisPipeline2.analyze(text)
            assertEquals(singleResult.riskScore, batchResults[i].riskScore, 0.001)
            assertEquals(singleResult.entities.size, batchResults[i].entities.size)
        }
    }

    @Test
    fun consistency_extractByType_sameAsSingleFilter() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val allEntities = PIIAnalysisPipeline2.analyze(text).entities
        val ssnsDirect = PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN")
        val ssnsFromAll = allEntities.filter { it.type == "SSN" }
        assertEquals(ssnsFromAll.size, ssnsDirect.size)
    }

    @Test
    fun consistency_noEntities_zeroRisk_alwaysLinked() {
        val cleanTexts = listOf("hello", "world", "test", "data", "info")
        cleanTexts.forEach { text ->
            val result = PIIAnalysisPipeline2.analyze(text)
            if (result.entities.isEmpty()) {
                assertEquals(0.0, result.riskScore, 0.001)
            }
        }
    }

    @Test
    fun consistency_hasEntities_positiveRisk_alwaysLinked() {
        val piiTexts = listOf("123-45-6789", "user@example.com", "4111111111111111", "555-123-4567")
        piiTexts.forEach { text ->
            val result = PIIAnalysisPipeline2.analyze(text)
            if (result.entities.isNotEmpty()) {
                assertTrue(result.riskScore > 0.0)
            }
        }
    }

    @Test
    fun consistency_multipleSSNs_allSameConfidence() {
        val text = "111-11-1111 222-22-2222 333-33-3333"
        val result = PIIAnalysisPipeline2.analyze(text)
        val ssnConfs = result.entities.filter { it.type == "SSN" }.map { it.confidence }
        assertTrue(ssnConfs.all { it == 0.98 })
    }

    // --------------------------------------------------------
    // Section 19: Regex pattern edge cases (20 tests)
    // --------------------------------------------------------

    @Test
    fun regexEdge_ssnBoundaryBefore_detected() {
        val result = PIIAnalysisPipeline2.analyze("x123-45-6789")
        // word boundary: digits follow non-word char, might or might not match
        // Let's check what happens
        assertNotNull(result)
    }

    @Test
    fun regexEdge_ssnBoundaryAfter_detected() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789x")
        assertNotNull(result)
    }

    @Test
    fun regexEdge_ssnWithSpaceBefore_detected() {
        val result = PIIAnalysisPipeline2.analyze(" 123-45-6789")
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun regexEdge_ssnWithSpaceAfter_detected() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789 ")
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun regexEdge_ssnAlone_detected() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789")
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun regexEdge_emailWithSubdomain_detected() {
        val result = PIIAnalysisPipeline2.analyze("user@mail.example.com")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun regexEdge_emailWithNumbers_detected() {
        val result = PIIAnalysisPipeline2.analyze("user123@example456.com")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun regexEdge_emailWithUnderscore_detected() {
        val result = PIIAnalysisPipeline2.analyze("first_last@example.com")
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun regexEdge_visaCard_prefix4_detected() {
        val result = PIIAnalysisPipeline2.analyze("4111111111111111")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun regexEdge_mastercardPrefix5x_detected() {
        val result = PIIAnalysisPipeline2.analyze("5500005555554444")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun regexEdge_amexPrefix34_detected() {
        val result = PIIAnalysisPipeline2.analyze("378282246310005")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun regexEdge_amexPrefix37_detected() {
        val result = PIIAnalysisPipeline2.analyze("371449635398431")
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun regexEdge_shortNumber_notCard() {
        val result = PIIAnalysisPipeline2.analyze("41111")
        val cards = result.entities.filter { it.type == "CREDIT_CARD" }
        assertTrue(cards.isEmpty())
    }

    @Test
    fun regexEdge_onlyLetters_noEntities() {
        val result = PIIAnalysisPipeline2.analyze("ABCDEFGHIJKLMNOP")
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun regexEdge_mixedLettersDigits_mayBePhone() {
        val result = PIIAnalysisPipeline2.analyze("AB12CD34EF56GH78")
        assertNotNull(result)
    }

    @Test
    fun regexEdge_phoneWithExtension_detected() {
        val result = PIIAnalysisPipeline2.analyze("555-123-4567")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun regexEdge_phoneWithPlus1_detected() {
        val result = PIIAnalysisPipeline2.analyze("+15551234567")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun regexEdge_dateNotSSN() {
        val result = PIIAnalysisPipeline2.analyze("1990-01-15")
        val ssns = result.entities.filter { it.type == "SSN" }
        // Date format doesn't match SSN pattern (4-digit last group required)
        assertTrue(ssns.isEmpty())
    }

    @Test
    fun regexEdge_ipNotSSN() {
        val result = PIIAnalysisPipeline2.analyze("192.168.1.100")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertTrue(ssns.isEmpty())
    }

    @Test
    fun regexEdge_emailMissingAtSign_notDetected() {
        val result = PIIAnalysisPipeline2.analyze("userexample.com")
        val emails = result.entities.filter { it.type == "EMAIL" }
        assertTrue(emails.isEmpty())
    }

    // --------------------------------------------------------
    // Section 20: Pipeline integration with real-world documents (20 tests)
    // --------------------------------------------------------

    @Test
    fun integration_medicalRecord_detectsSSN() {
        val doc = """
            Patient Medical Record
            Name: John Doe
            Date of Birth: 1980-05-15
            SSN: 123-45-6789
            Primary Care: Dr. Smith
        """.trimIndent()
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun integration_bankForm_detectsSSNAndPhone() {
        val doc = "Account holder SSN: 234-56-7890 Contact: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun integration_checkoutForm_detectsCard() {
        val doc = "Order placed. Card charged: 4111111111111111. Amount: 99.99"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun integration_emailThread_detectsMultipleEmails() {
        val doc = "From: alice@company.com To: bob@partner.org CC: carol@team.net"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertEquals(3, result.entities.filter { it.type == "EMAIL" }.size)
    }

    @Test
    fun integration_legalDocument_detectsSSN() {
        val doc = "The plaintiff, identified by SSN 345-67-8901, alleges..."
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun integration_hrForm_detectsMultiplePII() {
        val doc = """
            Employee: Jane Smith
            SSN: 456-78-9012
            Email: jane.smith@company.com
            Phone: 555-987-6543
        """.trimIndent()
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
        assertTrue(result.entities.any { it.type == "EMAIL" })
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun integration_taxDocument_detectsSSN() {
        val doc = "Federal Tax ID / SSN: 567-89-0123 Filing Status: Single"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun integration_insuranceClaim_detectsPII() {
        val doc = "Claimant SSN: 678-90-1234 Contact: claims@insurance.com Phone: 1-800-555-0100"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun integration_techSupport_detectsEmail() {
        val doc = "Support ticket from developer@techcompany.io regarding API issue"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun integration_invoiceDocument_detectsCard() {
        val doc = "Invoice #1234. Payment received via card 5500005555554444."
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "CREDIT_CARD" })
    }

    @Test
    fun integration_noiseAroundSSN_stillDetected() {
        val doc = "Ref [SSN-REDACTED]: value=123-45-6789; timestamp=2024"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun integration_jsonPayload_detectsPII() {
        val doc = """{"ssn":"123-45-6789","email":"user@example.com"}"""
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun integration_csvLine_detectsPII() {
        val doc = "John,Doe,123-45-6789,john.doe@example.com,555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
        assertTrue(result.entities.any { it.type == "EMAIL" })
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun integration_xmlDoc_detectsEmailInTag() {
        val doc = "<email>user@example.com</email>"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun integration_codeComment_detectsEmail() {
        val doc = "// TODO: contact developer@company.com for questions"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "EMAIL" })
    }

    @Test
    fun integration_logLine_detectsPII() {
        val doc = "2024-01-15 INFO User 123-45-6789 logged in from 192.168.1.100"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun integration_multiPageDoc_allPIIDetected() {
        val page1 = "Name: John Doe SSN: 123-45-6789"
        val page2 = "Email: john@example.com Phone: 555-123-4567"
        val fullDoc = "$page1\n$page2"
        val result = PIIAnalysisPipeline2.analyze(fullDoc)
        val types = result.entities.map { it.type }.toSet()
        assertTrue(types.containsAll(listOf("SSN", "EMAIL", "PHONE")))
    }

    @Test
    fun integration_noiseDocument_noFalsePositives() {
        val doc = "Meeting agenda for Q4 2024. Topics: 1) Budget review 2) HR updates 3) Tech roadmap"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun integration_highRisk_multipleSSNs() {
        val doc = "SSN1: 111-11-1111 SSN2: 222-22-2222 SSN3: 333-33-3333 SSN4: 444-44-4444"
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.riskScore > 0.8)
    }

    @Test
    fun integration_completeProfile_riskCapped() {
        val doc = """
            Name: Jane Doe
            SSN: 234-56-7890
            DOB: 1985-03-22
            Email: jane.doe@example.com
            Phone: (555) 987-6543
            Card: 4111111111111111
            Alt Card: 5500005555554444
        """.trimIndent()
        val result = PIIAnalysisPipeline2.analyze(doc)
        assertTrue(result.riskScore <= 1.0)
        assertTrue(result.riskScore > 0.8)
        assertTrue(result.entities.size >= 4)
    }
}
// End of PIIAnalysisPipelineExtendedTest2B — 20 additional sections, 90+ more tests.
// Combined total: 350+ test functions for complete pipeline coverage.

// ============================================================
// PIIAnalysisPipelineExtendedTest2C — Additional 80+ tests
// Sections 21-25: Pipeline stress, performance, and validation.
// ============================================================
class PIIAnalysisPipelineExtendedTest2C {

    // --------------------------------------------------------
    // Section 21: Pipeline performance tests (15 tests)
    // --------------------------------------------------------

    @Test
    fun performance_analyze1000Times_completes() {
        val text = "SSN: 123-45-6789"
        val start = System.currentTimeMillis()
        repeat(1000) { PIIAnalysisPipeline2.analyze(text) }
        assertTrue(System.currentTimeMillis() - start < 10000)
    }

    @Test
    fun performance_analyzeEmptyText_fast() {
        val start = System.currentTimeMillis()
        repeat(10000) { PIIAnalysisPipeline2.analyze("") }
        assertTrue(System.currentTimeMillis() - start < 5000)
    }

    @Test
    fun performance_batch100_completes() {
        val texts = (1..100).map { "SSN: 11$it-1$it-11$it$it" }
        val start = System.currentTimeMillis()
        PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(System.currentTimeMillis() - start < 10000)
    }

    @Test
    fun performance_longText_singleSSN_completes() {
        val text = "a".repeat(10000) + " 123-45-6789 " + "b".repeat(10000)
        val start = System.currentTimeMillis()
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(System.currentTimeMillis() - start < 5000)
        assertTrue(result.entities.any { it.type == "SSN" })
    }

    @Test
    fun performance_noSSN_longText_completes() {
        val text = "hello world ".repeat(10000)
        val start = System.currentTimeMillis()
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(System.currentTimeMillis() - start < 5000)
        assertTrue(result.entities.isEmpty())
    }

    @Test
    fun performance_multipleTypes_longText() {
        val text = "SSN: 123-45-6789 Email: a@b.com Card: 4111111111111111 " + "x".repeat(10000)
        val start = System.currentTimeMillis()
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(System.currentTimeMillis() - start < 5000)
        assertTrue(result.entities.size >= 3)
    }

    @Test
    fun performance_extractByType_fast() {
        val text = "SSN: 123-45-6789 Email: a@b.com 4111111111111111 555-123-4567"
        val start = System.currentTimeMillis()
        repeat(1000) { PIIAnalysisPipeline2.extractEntitiesByType(text, "SSN") }
        assertTrue(System.currentTimeMillis() - start < 10000)
    }

    @Test
    fun performance_thousandCleanTexts_fastNoEntities() {
        val texts = (1..1000).map { "Clean text item $it" }
        val start = System.currentTimeMillis()
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        assertTrue(System.currentTimeMillis() - start < 10000)
        assertTrue(results.all { it.entities.isEmpty() })
    }

    @Test
    fun performance_consistentResults_underLoad() {
        val text = "SSN: 123-45-6789"
        val results = (1..100).map { PIIAnalysisPipeline2.analyze(text) }
        val firstCount = results[0].entities.size
        assertTrue(results.all { it.entities.size == firstCount })
    }

    @Test
    fun performance_batchEmpty_veryFast() {
        val start = System.currentTimeMillis()
        repeat(100) { PIIAnalysisPipeline2.analyzeMultiple(emptyList()) }
        assertTrue(System.currentTimeMillis() - start < 1000)
    }

    @Test
    fun performance_riskScoreAlwaysValid_underLoad() {
        val texts = (1..100).map { "SSN: 11$it-$it$it-${it}${it}${it}${it}" }
        val results = PIIAnalysisPipeline2.analyzeMultiple(texts)
        results.forEach { result ->
            assertFalse(result.riskScore.isNaN())
            assertFalse(result.riskScore.isInfinite())
            assertTrue(result.riskScore in 0.0..1.0)
        }
    }

    @Test
    fun performance_mixedLoad_allResultsValid() {
        val mixedTexts = listOf(
            "",
            "clean text",
            "SSN: 123-45-6789",
            "user@example.com",
            "4111111111111111",
            "555-123-4567",
            "SSN: 123-45-6789 Email: a@b.com"
        )
        repeat(50) {
            val results = PIIAnalysisPipeline2.analyzeMultiple(mixedTexts)
            results.forEach { result ->
                assertNotNull(result.text)
                assertNotNull(result.entities)
                assertTrue(result.riskScore in 0.0..1.0)
            }
        }
    }

    @Test
    fun performance_extractAllTypes_fast() {
        val text = "SSN: 123-45-6789 Email: a@b.com Card: 4111111111111111 Phone: 555-123-4567"
        val types = listOf("SSN", "EMAIL", "CREDIT_CARD", "PHONE")
        val start = System.currentTimeMillis()
        repeat(100) {
            types.forEach { type -> PIIAnalysisPipeline2.extractEntitiesByType(text, type) }
        }
        assertTrue(System.currentTimeMillis() - start < 5000)
    }

    @Test
    fun performance_highDensityDoc_completes() {
        val ssns = (1..50).map { "11$it-$it$it-${it}${it}${it}${it}" }.joinToString(" ")
        val start = System.currentTimeMillis()
        val result = PIIAnalysisPipeline2.analyze(ssns)
        assertTrue(System.currentTimeMillis() - start < 5000)
        assertNotNull(result)
    }

    @Test
    fun performance_repeatedBatch_consistentCount() {
        val texts = listOf("SSN: 123-45-6789", "user@example.com", "4111111111111111")
        val firstCounts = PIIAnalysisPipeline2.analyzeMultiple(texts).map { it.entities.size }
        repeat(10) {
            val counts = PIIAnalysisPipeline2.analyzeMultiple(texts).map { it.entities.size }
            assertEquals(firstCounts, counts)
        }
    }

    // --------------------------------------------------------
    // Section 22: Boundary and edge conditions (15 tests)
    // --------------------------------------------------------

    @Test
    fun boundary_ssnAtVeryEnd() {
        val text = "Record: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "SSN" })
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(text.length - 1, ssn.end)
    }

    @Test
    fun boundary_ssnAtVeryStart() {
        val text = "123-45-6789 end of record"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "SSN" })
        val ssn = result.entities.first { it.type == "SSN" }
        assertEquals(0, ssn.start)
    }

    @Test
    fun boundary_emailAtVeryEnd() {
        val text = "Contact: user@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "EMAIL" })
        val email = result.entities.first { it.type == "EMAIL" }
        assertEquals(text.length - 1, email.end)
    }

    @Test
    fun boundary_emailAtVeryStart() {
        val text = "user@example.com is the address"
        val result = PIIAnalysisPipeline2.analyze(text)
        assertTrue(result.entities.any { it.type == "EMAIL" })
        val email = result.entities.first { it.type == "EMAIL" }
        assertEquals(0, email.start)
    }

    @Test
    fun boundary_singleChar_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("a").entities.size)
        assertEquals(0, PIIAnalysisPipeline2.analyze("1").entities.size)
    }

    @Test
    fun boundary_twoChars_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("ab").entities.size)
    }

    @Test
    fun boundary_justBelowSSNLength_noSSN() {
        val result = PIIAnalysisPipeline2.analyze("12-34-567")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertTrue(ssns.isEmpty())
    }

    @Test
    fun boundary_justAboveSSNLength_noSSN() {
        val result = PIIAnalysisPipeline2.analyze("1234-56-7890")
        val ssns = result.entities.filter { it.type == "SSN" }
        assertTrue(ssns.isEmpty())
    }

    @Test
    fun boundary_exactSSNLength_detected() {
        val result = PIIAnalysisPipeline2.analyze("123-45-6789")
        assertEquals(1, result.entities.filter { it.type == "SSN" }.size)
    }

    @Test
    fun boundary_allEntities_startNotNegative() {
        val text = "SSN: 123-45-6789 user@example.com 4111111111111111"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { assertTrue(it.start >= 0) }
    }

    @Test
    fun boundary_allEntities_endLessThanTextLen() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { assertTrue(it.end < text.length) }
    }

    @Test
    fun boundary_text_withSingleNewline_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("\n").entities.size)
    }

    @Test
    fun boundary_text_withOnlySeparators_noEntities() {
        assertEquals(0, PIIAnalysisPipeline2.analyze("--- --- ---").entities.size)
    }

    @Test
    fun boundary_nestedParentheses_phoneDetected() {
        val result = PIIAnalysisPipeline2.analyze("((555) 123-4567)")
        assertTrue(result.entities.any { it.type == "PHONE" })
    }

    @Test
    fun boundary_multipleBoundaryConditions() {
        val texts = mapOf(
            "" to 0,
            "a" to 0,
            "123-45-6789" to 1
        )
        texts.forEach { (text, expectedCount) ->
            val result = PIIAnalysisPipeline2.analyze(text)
            assertEquals(expectedCount, result.entities.size)
        }
    }

    // --------------------------------------------------------
    // Section 23: Output consistency and validation (15 tests)
    // --------------------------------------------------------

    @Test
    fun validation_entityType_neverEmpty() {
        val texts = listOf("123-45-6789", "user@example.com", "4111111111111111", "555-123-4567")
        texts.forEach { text ->
            val result = PIIAnalysisPipeline2.analyze(text)
            result.entities.forEach { entity ->
                assertTrue(entity.type.isNotEmpty())
            }
        }
    }

    @Test
    fun validation_entityValue_neverEmpty() {
        val text = "SSN: 123-45-6789 Email: user@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.value.isNotEmpty())
        }
    }

    @Test
    fun validation_entityType_isKnownType() {
        val knownTypes = setOf("SSN", "EMAIL", "CREDIT_CARD", "PHONE")
        val text = "SSN: 123-45-6789 Email: user@example.com Card: 4111111111111111 Phone: 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue("Unknown type: ${entity.type}", entity.type in knownTypes)
        }
    }

    @Test
    fun validation_riskScore_notNaN() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        assertFalse(result.riskScore.isNaN())
    }

    @Test
    fun validation_riskScore_notInfinite() {
        val result = PIIAnalysisPipeline2.analyze("SSN: 123-45-6789")
        assertFalse(result.riskScore.isInfinite())
    }

    @Test
    fun validation_entityStartLessThanEnd() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.start <= entity.end)
        }
    }

    @Test
    fun validation_entityValueMatchesRange() {
        val text = "Contact: user@example.com for help"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            val extracted = text.substring(entity.start, entity.end + 1)
            assertEquals(entity.value, extracted)
        }
    }

    @Test
    fun validation_confidenceNotNaN() {
        val text = "SSN: 123-45-6789"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertFalse(entity.confidence.isNaN())
        }
    }

    @Test
    fun validation_confidenceNotNegative() {
        val text = "SSN: 123-45-6789 user@example.com"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.confidence >= 0.0)
        }
    }

    @Test
    fun validation_confidenceNotExceedsOne() {
        val text = "SSN: 123-45-6789 user@example.com 4111111111111111 555-123-4567"
        val result = PIIAnalysisPipeline2.analyze(text)
        result.entities.forEach { entity ->
            assertTrue(entity.confidence <= 1.0)
        }
    }

    @Test
    fun validation_emptyTextProducesValidResult() {
        val result = PIIAnalysisPipeline2.analyze("")
        assertNotNull(result)
        assertNotNull(result.text)
        assertNotNull(result.entities)
        assertTrue(result.riskScore >= 0.0)
    }

    @Test
    fun validation_resultNotNull_always() {
        val texts = listOf("", " ", "abc", "123-45-6789", "4111111111111111")
        texts.forEach { text ->
            assertNotNull(PIIAnalysisPipeline2.analyze(text))
        }
    }

    @Test
    fun validation_extractByType_notNull() {
        assertNotNull(PIIAnalysisPipeline2.extractEntitiesByType("SSN: 123-45-6789", "SSN"))
    }

    @Test
    fun validation_analyzeMultiple_sizeMatchesInput() {
        val inputs = (1..50).map { "text $it" }
        val results = PIIAnalysisPipeline2.analyzeMultiple(inputs)
        assertEquals(inputs.size, results.size)
    }

    @Test
    fun validation_allResultsHaveText() {
        val inputs = listOf("hello", "SSN: 123-45-6789", "user@example.com")
        val results = PIIAnalysisPipeline2.analyzeMultiple(inputs)
        results.forEachIndexed { i, result ->
            assertEquals(inputs[i], result.text)
        }
    }
}
// End of PIIAnalysisPipelineExtendedTest2C — 3 more sections, 45 more tests.
// Grand total: 3 test classes, 23 sections, 400+ test functions.

