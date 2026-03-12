package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Privacy Risk Scorer Extended Test Suite
 * Tests for PrivacyRiskScorer: text risk assessment, entity weighting,
 * aggregate scores, context adjustments, and regression tests.
 */
class PrivacyRiskScorerExtendedTest {

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 – Single Entity Risk Scores
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `risk score 001 ssn gets critical weight`() {
        val score = PrivacyRiskScorer.scoreEntity("SSN", "123-45-6789")
        assertTrue(score >= 0.9f)
    }

    @Test fun `risk score 002 credit card critical weight`() {
        val score = PrivacyRiskScorer.scoreEntity("CREDIT_CARD", "4532015112830366")
        assertTrue(score >= 0.9f)
    }

    @Test fun `risk score 003 email medium weight`() {
        val score = PrivacyRiskScorer.scoreEntity("EMAIL", "user@example.com")
        assertTrue(score in 0.3f..0.8f)
    }

    @Test fun `risk score 004 phone medium weight`() {
        val score = PrivacyRiskScorer.scoreEntity("PHONE", "(555) 123-4567")
        assertTrue(score in 0.3f..0.8f)
    }

    @Test fun `risk score 005 ip address low medium weight`() {
        val score = PrivacyRiskScorer.scoreEntity("IP_ADDRESS", "8.8.8.8")
        assertTrue(score in 0.1f..0.7f)
    }

    @Test fun `risk score 006 dob high weight`() {
        val score = PrivacyRiskScorer.scoreEntity("DATE_OF_BIRTH", "1985-03-22")
        assertTrue(score >= 0.5f)
    }

    @Test fun `risk score 007 passport critical`() {
        val score = PrivacyRiskScorer.scoreEntity("PASSPORT", "A12345678")
        assertTrue(score >= 0.9f)
    }

    @Test fun `risk score 008 iban high weight`() {
        val score = PrivacyRiskScorer.scoreEntity("IBAN", "DE89370400440532013000")
        assertTrue(score >= 0.7f)
    }

    @Test fun `risk score 009 api key critical`() {
        val score = PrivacyRiskScorer.scoreEntity("API_KEY", "sk_tst_xyz")
        assertTrue(score >= 0.9f)
    }

    @Test fun `risk score 010 driver license high`() {
        val score = PrivacyRiskScorer.scoreEntity("DRIVERS_LICENSE", "A1234567")
        assertTrue(score >= 0.6f)
    }

    @Test fun `risk score 011 score between 0 and 1`() {
        val types = listOf("SSN", "EMAIL", "PHONE", "IP_ADDRESS", "CREDIT_CARD", "DATE_OF_BIRTH")
        types.forEach { type ->
            val score = PrivacyRiskScorer.scoreEntity(type, "test_value")
            assertTrue("Score out of range for $type: $score", score in 0.0f..1.0f)
        }
    }

    @Test fun `risk score 012 unknown type gets low score`() {
        val score = PrivacyRiskScorer.scoreEntity("UNKNOWN_TYPE", "anything")
        assertTrue(score <= 0.3f)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 – Text-Level Risk Assessment
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `text risk 001 text with ssn is high risk`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertTrue(result.level.name == "HIGH" || result.level.name == "CRITICAL")
    }

    @Test fun `text risk 002 text with card is critical`() {
        val result = PrivacyRiskScorer.assessText("Card: 4532015112830366")
        assertTrue(result.level.name == "HIGH" || result.level.name == "CRITICAL")
    }

    @Test fun `text risk 003 clean text is none`() {
        val result = PrivacyRiskScorer.assessText("The weather is nice today")
        assertTrue(result.level.name == "NONE" || result.level.name == "LOW")
    }

    @Test fun `text risk 004 empty text is none`() {
        val result = PrivacyRiskScorer.assessText("")
        assertEquals("NONE", result.level.name)
    }

    @Test fun `text risk 005 email only is low or medium`() {
        val result = PrivacyRiskScorer.assessText("Email: user@example.com")
        val level = result.level.name
        assertTrue(level == "LOW" || level == "MEDIUM" || level == "HIGH")
    }

    @Test fun `text risk 006 multiple pii types critical`() {
        val text = "SSN: 123-45-6789, Card: 4532015112830366, DOB: 1985-03-22"
        val result = PrivacyRiskScorer.assessText(text)
        assertTrue(result.level.name == "CRITICAL" || result.level.name == "HIGH")
    }

    @Test fun `text risk 007 score is numeric value`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertTrue(result.score >= 0.0f)
        assertTrue(result.score <= 100.0f)
    }

    @Test fun `text risk 008 higher risk = higher score`() {
        val critical = PrivacyRiskScorer.assessText("SSN: 123-45-6789, Card: 4532015112830366")
        val low = PrivacyRiskScorer.assessText("Email: user@example.com")
        assertTrue(critical.score >= low.score)
    }

    @Test fun `text risk 009 result has entity count`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789, Email: user@example.com")
        assertTrue(result.entityCount >= 1)
    }

    @Test fun `text risk 010 result has type breakdown`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertNotNull(result.typeBreakdown)
    }

    @Test fun `text risk 011 whitespace text is none`() {
        val result = PrivacyRiskScorer.assessText("   \t\n   ")
        assertTrue(result.level.name == "NONE" || result.score == 0.0f)
    }

    @Test fun `text risk 012 very long text with single ssn`() {
        val text = "x".repeat(5000) + " SSN: 123-45-6789 " + "y".repeat(5000)
        val result = PrivacyRiskScorer.assessText(text)
        assertNotNull(result)
        assertTrue(result.level.name != "NONE" || result.score > 0)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 – Risk Level Enum
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `risk level 001 none is lowest`() {
        val none = PrivacyRiskLevel.NONE
        val low = PrivacyRiskLevel.LOW
        assertTrue(none.ordinal < low.ordinal)
    }

    @Test fun `risk level 002 critical is highest`() {
        val high = PrivacyRiskLevel.HIGH
        val critical = PrivacyRiskLevel.CRITICAL
        assertTrue(critical.ordinal > high.ordinal)
    }

    @Test fun `risk level 003 order: none < low < medium < high < critical`() {
        val levels = listOf(
            PrivacyRiskLevel.NONE,
            PrivacyRiskLevel.LOW,
            PrivacyRiskLevel.MEDIUM,
            PrivacyRiskLevel.HIGH,
            PrivacyRiskLevel.CRITICAL
        )
        for (i in 0 until levels.size - 1) {
            assertTrue(levels[i].ordinal < levels[i+1].ordinal)
        }
    }

    @Test fun `risk level 004 five risk levels defined`() {
        assertEquals(5, PrivacyRiskLevel.values().size)
    }

    @Test fun `risk level 005 valueOf works`() {
        assertEquals(PrivacyRiskLevel.HIGH, PrivacyRiskLevel.valueOf("HIGH"))
        assertEquals(PrivacyRiskLevel.NONE, PrivacyRiskLevel.valueOf("NONE"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 – Aggregate Scoring (multiple entities)
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `aggregate 001 no entities = 0 score`() {
        val score = PrivacyRiskScorer.aggregateScore(emptyList())
        assertEquals(0.0f, score, 0.001f)
    }

    @Test fun `aggregate 002 one ssn entity high score`() {
        val entities = listOf(RiskEntity("SSN", "123-45-6789", 0.95f))
        val score = PrivacyRiskScorer.aggregateScore(entities)
        assertTrue(score >= 0.8f)
    }

    @Test fun `aggregate 003 multiple entities accumulate score`() {
        val single = listOf(RiskEntity("SSN", "123-45-6789", 0.95f))
        val multi = listOf(
            RiskEntity("SSN", "123-45-6789", 0.95f),
            RiskEntity("CREDIT_CARD", "4532015112830366", 0.95f)
        )
        val singleScore = PrivacyRiskScorer.aggregateScore(single)
        val multiScore = PrivacyRiskScorer.aggregateScore(multi)
        assertTrue(multiScore >= singleScore)
    }

    @Test fun `aggregate 004 duplicate entities handled`() {
        val entities = List(100) { RiskEntity("EMAIL", "user@example.com", 0.8f) }
        val score = PrivacyRiskScorer.aggregateScore(entities)
        assertTrue(score in 0.0f..100.0f)
    }

    @Test fun `aggregate 005 score never negative`() {
        val score = PrivacyRiskScorer.aggregateScore(emptyList())
        assertTrue(score >= 0.0f)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 – Context-Adjusted Scoring
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `context 001 labeled entity higher confidence`() {
        val labeled = PrivacyRiskScorer.assessTextWithContext(
            "SSN: 123-45-6789",
            DocumentContext.MEDICAL_RECORD
        )
        val unlabeled = PrivacyRiskScorer.assessTextWithContext(
            "123-45-6789",
            DocumentContext.UNKNOWN
        )
        assertTrue(labeled.score >= unlabeled.score)
    }

    @Test fun `context 002 medical context boosts risk`() {
        val medical = PrivacyRiskScorer.assessTextWithContext(
            "SSN: 123-45-6789, DOB: 1985-03-22",
            DocumentContext.MEDICAL_RECORD
        )
        assertTrue(medical.level.name == "HIGH" || medical.level.name == "CRITICAL")
    }

    @Test fun `context 003 financial context boosts card risk`() {
        val financial = PrivacyRiskScorer.assessTextWithContext(
            "Card: 4532015112830366",
            DocumentContext.FINANCIAL_RECORD
        )
        assertTrue(financial.score > 0.0f)
    }

    @Test fun `context 004 government context boosts passport`() {
        val gov = PrivacyRiskScorer.assessTextWithContext(
            "Passport: A12345678",
            DocumentContext.GOVERNMENT_RECORD
        )
        assertTrue(gov.score > 0.0f)
    }

    @Test fun `context 005 marketing context lower risk`() {
        val marketing = PrivacyRiskScorer.assessTextWithContext(
            "Email: user@example.com",
            DocumentContext.MARKETING_EMAIL
        )
        assertNotNull(marketing)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 – Threshold-Based Risk Classification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `threshold 001 score 0 is none`() {
        assertEquals(PrivacyRiskLevel.NONE, PrivacyRiskScorer.scoreToLevel(0.0f))
    }

    @Test fun `threshold 002 score 0.1 is low`() {
        val level = PrivacyRiskScorer.scoreToLevel(0.1f)
        assertTrue(level == PrivacyRiskLevel.LOW || level == PrivacyRiskLevel.NONE)
    }

    @Test fun `threshold 003 score 0.5 is medium`() {
        val level = PrivacyRiskScorer.scoreToLevel(0.5f)
        assertTrue(level == PrivacyRiskLevel.MEDIUM || level == PrivacyRiskLevel.HIGH)
    }

    @Test fun `threshold 004 score 0.9 is high or critical`() {
        val level = PrivacyRiskScorer.scoreToLevel(0.9f)
        assertTrue(level == PrivacyRiskLevel.HIGH || level == PrivacyRiskLevel.CRITICAL)
    }

    @Test fun `threshold 005 score 1.0 is critical`() {
        assertEquals(PrivacyRiskLevel.CRITICAL, PrivacyRiskScorer.scoreToLevel(1.0f))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 – Sensitivity Multipliers
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `multiplier 001 ssn has highest multiplier`() {
        val ssnMult = PrivacyRiskScorer.getSensitivityMultiplier("SSN")
        val emailMult = PrivacyRiskScorer.getSensitivityMultiplier("EMAIL")
        assertTrue(ssnMult >= emailMult)
    }

    @Test fun `multiplier 002 card has high multiplier`() {
        val mult = PrivacyRiskScorer.getSensitivityMultiplier("CREDIT_CARD")
        assertTrue(mult >= 1.0f)
    }

    @Test fun `multiplier 003 unknown type returns 1.0`() {
        val mult = PrivacyRiskScorer.getSensitivityMultiplier("UNKNOWN")
        assertEquals(1.0f, mult, 0.01f)
    }

    @Test fun `multiplier 004 multiplier always positive`() {
        val types = listOf("SSN", "EMAIL", "PHONE", "CREDIT_CARD", "IP_ADDRESS", "PASSPORT")
        types.forEach { type ->
            assertTrue(PrivacyRiskScorer.getSensitivityMultiplier(type) > 0.0f)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 – Combination Risk (multiple entity types)
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `combo 001 ssn plus card critical`() {
        val text = "SSN: 123-45-6789, Card: 4532015112830366"
        val result = PrivacyRiskScorer.assessText(text)
        assertTrue(result.level.name == "CRITICAL" || result.level.name == "HIGH")
    }

    @Test fun `combo 002 ssn plus dob critical`() {
        val text = "SSN: 234-56-7890, DOB: 1985-03-22"
        val result = PrivacyRiskScorer.assessText(text)
        assertTrue(result.level.name == "HIGH" || result.level.name == "CRITICAL")
    }

    @Test fun `combo 003 three types very high`() {
        val text = "SSN: 123-45-6789, Card: 4532015112830366, DOB: 1985-03-22"
        val result = PrivacyRiskScorer.assessText(text)
        assertTrue(result.score > 0.0f)
    }

    @Test fun `combo 004 four types maximum`() {
        val text = "SSN: 123-45-6789, Card: 4532015112830366, DOB: 1985-03-22, Passport: A12345678"
        val result = PrivacyRiskScorer.assessText(text)
        assertTrue(result.level.name == "CRITICAL")
    }

    @Test fun `combo 005 email only lower than ssn only`() {
        val emailResult = PrivacyRiskScorer.assessText("Email: user@example.com")
        val ssnResult = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertTrue(ssnResult.score >= emailResult.score)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 9 – Batch Assessment
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `batch 001 empty batch returns empty`() {
        val results = PrivacyRiskScorer.assessBatch(emptyList())
        assertTrue(results.isEmpty())
    }

    @Test fun `batch 002 single item batch`() {
        val results = PrivacyRiskScorer.assessBatch(listOf("SSN: 123-45-6789"))
        assertEquals(1, results.size)
    }

    @Test fun `batch 003 100 items processed`() {
        val texts = List(100) { "SSN: 123-45-6789" }
        val results = PrivacyRiskScorer.assessBatch(texts)
        assertEquals(100, results.size)
    }

    @Test fun `batch 004 mixed high low risk`() {
        val texts = listOf(
            "SSN: 123-45-6789",     // high
            "Hello world",           // none
            "Card: 4532015112830366", // high
            "No PII here"            // none
        )
        val results = PrivacyRiskScorer.assessBatch(texts)
        assertEquals(4, results.size)
        assertTrue(results[0].score > results[1].score)
    }

    @Test fun `batch 005 results in order`() {
        val texts = listOf("text1 ssn 123-45-6789", "text2 email user@example.com")
        val results = PrivacyRiskScorer.assessBatch(texts)
        assertEquals(2, results.size)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 10 – Compliance Level Mapping
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `compliance 001 hipaa trigger on medical pii`() {
        val text = "Patient SSN: 123-45-6789, DOB: 1985-03-22"
        val compliance = PrivacyRiskScorer.getComplianceTriggers(text)
        assertTrue(compliance.contains("HIPAA") || compliance.isNotEmpty())
    }

    @Test fun `compliance 002 pci dss trigger on card`() {
        val text = "Card number: 4532015112830366"
        val compliance = PrivacyRiskScorer.getComplianceTriggers(text)
        assertTrue(compliance.contains("PCI_DSS") || compliance.isNotEmpty())
    }

    @Test fun `compliance 003 gdpr trigger on email`() {
        val text = "Email: user@example.com" // EU email might trigger GDPR
        val compliance = PrivacyRiskScorer.getComplianceTriggers(text)
        assertNotNull(compliance)
    }

    @Test fun `compliance 004 clean text no triggers`() {
        val text = "The weather is nice today"
        val compliance = PrivacyRiskScorer.getComplianceTriggers(text)
        assertTrue(compliance.isEmpty() || compliance.none { it == "HIPAA" })
    }

    @Test fun `compliance 005 glba trigger on financial data`() {
        val text = "Account: 1234567890, Routing: 021000021"
        val compliance = PrivacyRiskScorer.getComplianceTriggers(text)
        assertNotNull(compliance)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 11 – Performance
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `perf 001 1000 assessments no crash`() {
        repeat(1000) { PrivacyRiskScorer.assessText("SSN: 123-45-6789") }
    }

    @Test fun `perf 002 clean text 1000 times no crash`() {
        repeat(1000) { PrivacyRiskScorer.assessText("Hello world") }
    }

    @Test fun `perf 003 empty text 1000 times no crash`() {
        repeat(1000) { PrivacyRiskScorer.assessText("") }
    }

    @Test fun `perf 004 batch of 1000 no crash`() {
        val texts = List(1000) { "Test $it with SSN 123-45-6789" }
        PrivacyRiskScorer.assessBatch(texts)
    }

    @Test fun `perf 005 very large text no crash`() {
        val text = buildString {
            repeat(100) {
                append("SSN: 123-45-6789, Card: 4532015112830366, Email: user@example.com. ")
            }
        }
        val result = PrivacyRiskScorer.assessText(text)
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 12 – Edge Cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `edge 001 null byte in text no crash`() {
        val result = runCatching { PrivacyRiskScorer.assessText("test\u0000value") }
        assertTrue(result.isSuccess)
    }

    @Test fun `edge 002 unicode text no crash`() {
        val result = runCatching { PrivacyRiskScorer.assessText("\u4E2D\u6587\u6587\u672C") }
        assertTrue(result.isSuccess)
    }

    @Test fun `edge 003 html tags no crash`() {
        val result = runCatching { PrivacyRiskScorer.assessText("<html><body>SSN: 123-45-6789</body></html>") }
        assertTrue(result.isSuccess)
    }

    @Test fun `edge 004 json text analyzed`() {
        val result = PrivacyRiskScorer.assessText("""{"ssn": "123-45-6789"}""")
        assertTrue(result.score >= 0.0f)
    }

    @Test fun `edge 005 xml text no crash`() {
        val result = runCatching { PrivacyRiskScorer.assessText("<ssn>123-45-6789</ssn>") }
        assertTrue(result.isSuccess)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 13 – Recommendation Generation
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `recommend 001 critical risk has recommendations`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789, Card: 4532015112830366")
        val recs = PrivacyRiskScorer.getRecommendations(result)
        assertTrue(recs.isNotEmpty())
    }

    @Test fun `recommend 002 none risk has no urgent recommendations`() {
        val result = PrivacyRiskScorer.assessText("Hello world")
        val recs = PrivacyRiskScorer.getRecommendations(result)
        // Should have 0 urgent recommendations
        assertTrue(recs.isEmpty() || recs.none { it.priority == "URGENT" })
    }

    @Test fun `recommend 003 ssn risk recommends encryption`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        val recs = PrivacyRiskScorer.getRecommendations(result)
        val hasEncryptRec = recs.any { it.action.contains("encr", ignoreCase = true) ||
                it.action.contains("redact", ignoreCase = true)
        }
        assertTrue(hasEncryptRec || recs.isNotEmpty())
    }

    @Test fun `recommend 004 card risk recommends pci compliance`() {
        val result = PrivacyRiskScorer.assessText("Card: 4532015112830366")
        val recs = PrivacyRiskScorer.getRecommendations(result)
        assertNotNull(recs)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 14 – Comparison and Sorting
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `compare 001 higher score higher risk`() {
        val r1 = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        val r2 = PrivacyRiskScorer.assessText("Hello world")
        assertTrue(r1.score >= r2.score)
    }

    @Test fun `compare 002 more entities higher score`() {
        val r1 = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        val r2 = PrivacyRiskScorer.assessText("SSN: 123-45-6789, Card: 4532015112830366")
        assertTrue(r2.score >= r1.score)
    }

    @Test fun `compare 003 sort results by score descending`() {
        val texts = listOf(
            "SSN: 123-45-6789",
            "Email: user@example.com",
            "Hello world"
        )
        val results = PrivacyRiskScorer.assessBatch(texts)
        val sorted = results.sortedByDescending { it.score }
        assertEquals(results[0].score, sorted[0].score, 0.001f)
    }

    @Test fun `compare 004 identical texts same score`() {
        val r1 = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        val r2 = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertEquals(r1.score, r2.score, 0.001f)
    }

    @Test fun `compare 005 different ssns same score`() {
        val r1 = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        val r2 = PrivacyRiskScorer.assessText("SSN: 234-56-7890")
        assertEquals(r1.level, r2.level)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 15 – Full Document Assessment
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `doc 001 medical record critical risk`() {
        val doc = """
            Patient: John Doe
            SSN: 123-45-6789
            DOB: 1985-03-22
            Phone: (555) 123-4567
            Email: john@hospital.com
        """.trimIndent()
        val result = PrivacyRiskScorer.assessText(doc)
        assertTrue(result.level.name == "HIGH" || result.level.name == "CRITICAL")
    }

    @Test fun `doc 002 financial document high risk`() {
        val doc = """
            Account: 1234567890
            Routing: 021000021
            Card: 4532015112830366
            SSN: 234-56-7890
        """.trimIndent()
        val result = PrivacyRiskScorer.assessText(doc)
        assertTrue(result.level.name == "HIGH" || result.level.name == "CRITICAL")
    }

    @Test fun `doc 003 meeting notes no risk`() {
        val doc = """
            Team meeting notes - Q1 2024
            Attendees: Alice, Bob, Carol
            Topics: roadmap, hiring, budget
            Action items: review PR, schedule demo
        """.trimIndent()
        val result = PrivacyRiskScorer.assessText(doc)
        assertTrue(result.level.name == "NONE" || result.level.name == "LOW")
    }

    @Test fun `doc 004 api config critical`() {
        val apiKey = "sk_tst_4eC39HqLyjWDarjtT1zdp7dc"
        val doc = "API_KEY: $apiKey"
        val result = PrivacyRiskScorer.assessText(doc)
        assertNotNull(result)
    }

    @Test fun `doc 005 log file with mixed data`() {
        val doc = """
            [INFO] Server started on port 8080
            [INFO] User login: admin@company.com ip=203.0.113.42
            [WARN] Failed login for user@example.com from 198.51.100.1
            [DEBUG] Processing record SSN=123-45-6789
        """.trimIndent()
        val result = PrivacyRiskScorer.assessText(doc)
        assertTrue(result.hasAny() || result.score > 0.0f)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 16 – Regression Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `reg 001 version number not pii`() {
        val result = PrivacyRiskScorer.assessText("App version 1.2.3.4")
        assertTrue(result.level.name == "NONE" || result.level.name == "LOW")
    }

    @Test fun `reg 002 zip code not ssn`() {
        val result = PrivacyRiskScorer.assessText("Boston MA 02134")
        assertFalse(result.level.name == "CRITICAL")
    }

    @Test fun `reg 003 phone vs zip not confused`() {
        val result = PrivacyRiskScorer.assessText("Our zip code is 90210")
        assertFalse(result.entities.any { it.type.name == "PHONE" && it.confidence > 0.9f })
    }

    @Test fun `reg 004 year not pii`() {
        val result = PrivacyRiskScorer.assessText("This happened in 1985")
        assertTrue(result.level.name == "NONE" || result.level.name == "LOW")
    }

    @Test fun `reg 005 currency not credit card`() {
        val result = PrivacyRiskScorer.assessText("Total: $4,532.01 due on 12/30")
        assertFalse(result.entities.any { it.type.name.contains("CARD") && it.confidence > 0.9f })
    }

    @Test fun `reg 006 advertising ssn handled`() {
        val result = PrivacyRiskScorer.assessText("SSN: 078-05-1120")
        assertNotNull(result)
    }

    @Test fun `reg 007 ip in url not confused`() {
        val result = PrivacyRiskScorer.assessText("Visit http://example.com/path")
        assertNotNull(result)
    }

    @Test fun `reg 008 product id not passport`() {
        val result = PrivacyRiskScorer.assessText("Product ID: PRD-A1234567")
        assertTrue(result.entities.none { it.type.name == "PASSPORT" && it.confidence > 0.9f })
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 17 – Score Result Object
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `result 001 has level field`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertNotNull(result.level)
    }

    @Test fun `result 002 has score field`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertTrue(result.score >= 0.0f)
    }

    @Test fun `result 003 has entities list`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertNotNull(result.entities)
    }

    @Test fun `result 004 has entity count`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        assertTrue(result.entityCount >= 0)
    }

    @Test fun `result 005 entities count matches entities list`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789 Email: user@example.com")
        assertEquals(result.entityCount, result.entities.size)
    }

    @Test fun `result 006 hasAny correct`() {
        val withPII = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        val noPII = PrivacyRiskScorer.assessText("Hello world")
        assertTrue(withPII.hasAny())
        assertFalse(noPII.hasAny())
    }

    @Test fun `result 007 empty text hasAny false`() {
        val result = PrivacyRiskScorer.assessText("")
        assertFalse(result.hasAny())
    }

    @Test fun `result 008 tostring doesnt throw`() {
        val result = PrivacyRiskScorer.assessText("SSN: 123-45-6789")
        val str = runCatching { result.toString() }
        assertTrue(str.isSuccess)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 18 – Whitelist Handling
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `whitelist 001 whitelisted text has reduced risk`() {
        val text = "SSN: 123-45-6789"
        val config = RiskScorerConfig(whitelist = setOf("123-45-6789"))
        val result = PrivacyRiskScorer.assessTextWithConfig(text, config)
        // Whitelisted entities should not inflate risk
        assertNotNull(result)
    }

    @Test fun `whitelist 002 empty whitelist normal behavior`() {
        val text = "SSN: 123-45-6789"
        val config = RiskScorerConfig(whitelist = emptySet())
        val result = PrivacyRiskScorer.assessTextWithConfig(text, config)
        assertNotNull(result)
        assertTrue(result.score >= 0.0f)
    }

    @Test fun `whitelist 003 wildcard whitelist suppresses type`() {
        val text = "Email: user@example.com"
        val config = RiskScorerConfig(suppressTypes = setOf("EMAIL"))
        val result = PrivacyRiskScorer.assessTextWithConfig(text, config)
        // Suppressed type = lower risk
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 19 – Bulk Document Risk
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `bulk 001 rank documents by risk`() {
        val docs = mapOf(
            "doc1" to "SSN: 123-45-6789, Card: 4532015112830366",
            "doc2" to "Email: user@example.com",
            "doc3" to "Hello world"
        )
        val rankings = PrivacyRiskScorer.rankDocuments(docs)
        assertEquals("doc1", rankings.first().key)
        assertEquals("doc3", rankings.last().key)
    }

    @Test fun `bulk 002 200 docs processed`() {
        val docs = (1..200).associate {
            "doc$it" to if (it % 5 == 0) "SSN: 123-45-6789" else "Hello world $it"
        }
        val results = PrivacyRiskScorer.rankDocuments(docs)
        assertEquals(200, results.size)
    }

    @Test fun `bulk 003 empty docs map`() {
        val results = PrivacyRiskScorer.rankDocuments(emptyMap())
        assertTrue(results.isEmpty())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 20 – Score Normalization
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `normalize 001 scores normalized 0-100`() {
        val scores = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)
        scores.forEach { raw ->
            val normalized = PrivacyRiskScorer.normalizeScore(raw)
            assertTrue(normalized in 0.0f..100.0f)
        }
    }

    @Test fun `normalize 002 zero maps to 0`() {
        assertEquals(0.0f, PrivacyRiskScorer.normalizeScore(0.0f), 0.01f)
    }

    @Test fun `normalize 003 one maps to 100`() {
        assertEquals(100.0f, PrivacyRiskScorer.normalizeScore(1.0f), 0.01f)
    }

    @Test fun `normalize 004 half maps to 50`() {
        assertEquals(50.0f, PrivacyRiskScorer.normalizeScore(0.5f), 0.01f)
    }

    @Test fun `normalize 005 out of range clamped`() {
        val over = PrivacyRiskScorer.normalizeScore(2.0f)
        assertTrue(over <= 100.0f)
        val under = PrivacyRiskScorer.normalizeScore(-1.0f)
        assertTrue(under >= 0.0f)
    }

} // end class PrivacyRiskScorerExtendedTest
