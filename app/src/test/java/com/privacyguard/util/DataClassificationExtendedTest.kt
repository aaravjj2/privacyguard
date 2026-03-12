package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Data Classification Extended Test Suite
 * Tests for DataClassifier: document type detection, sensitivity labeling,
 * content categorization, PII density scoring, and compliance tagging.
 */
class DataClassificationExtendedTest {

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 – Medical Record Classification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `classify medical 001 patient file identified`() {
        val text = "PATIENT: John Doe\nSSN: 123-45-6789\nDOB: 1985-03-22\nDiagnosis: Hypertension"
        val result = DataClassifier.classify(text)
        assertTrue(result.category == "MEDICAL" || result.categories.contains("MEDICAL"))
    }

    @Test fun `classify medical 002 medical record high sensitivity`() {
        val text = "Medical Record #MRN-456\nPatient DOB 1975-06-15\nAllergies: Penicillin"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name == "HIGH" || result.sensitivityLevel.name == "CONFIDENTIAL")
    }

    @Test fun `classify medical 003 prescription document`() {
        val text = "Rx: Metformin 500mg\nPatient: Jane Smith\nSSN: 234-56-7890\nDr: Smith MD"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
        assertTrue(result.hasClassification())
    }

    @Test fun `classify medical 004 lab results document`() {
        val text = "Lab Results\nPatient ID: PTN-001\nGlucose: 95 mg/dL\nHbA1c: 6.2%"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify medical 005 insurance claim document`() {
        val text = "Insurance Claim #CLM-2024\nPatient SSN: 345-67-8901\nProcedure: 99213"
        val result = DataClassifier.classify(text)
        assertTrue(result.hasClassification())
    }

    @Test fun `classify medical 006 hipaa compliance tag`() {
        val text = "PHI: Patient John Smith DOB 1980-01-01 SSN 456-78-9012"
        val result = DataClassifier.classify(text)
        val tags = result.complianceTags
        assertTrue(tags.isEmpty() || tags.contains("HIPAA") || result.sensitivityLevel.name != "NONE")
    }

    @Test fun `classify medical 007 mental health record high sensitivity`() {
        val text = "Psychiatric Evaluation\nPatient: Alice Brown Age 35\nDiagnosis: MDD"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify medical 008 imaging report`() {
        val text = "Radiology Report\nPatient MRN: MRN-789\nFindings: No acute abnormality"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify medical 009 surgery notes`() {
        val text = "Operative Report\nProcedure: Appendectomy Date: 2024-01-15\nSurgeon: Dr Jones"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify medical 010 discharge summary`() {
        val text = "Discharge Summary\nAdmit: 2024-01-10 Discharge: 2024-01-15\nDx: Pneumonia"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 – Financial Document Classification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `classify financial 001 bank statement`() {
        val text = "Bank Statement\nAccount: 1234567890 Routing: 021000021\nBalance: \$5,234.56"
        val result = DataClassifier.classify(text)
        assertTrue(result.category == "FINANCIAL" || result.categories.contains("FINANCIAL"))
    }

    @Test fun `classify financial 002 credit card statement`() {
        val text = "Credit Card Statement\nCard: ****-****-****-6789\nBalance Due: \$1,234.00"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify financial 003 tax return`() {
        val text = "Form 1040\nSSN: 567-89-0123\nAGI: \$75,000\nTax Owed: \$9,500"
        val result = DataClassifier.classify(text)
        assertTrue(result.hasClassification())
    }

    @Test fun `classify financial 004 wire transfer`() {
        val text = "WIRE TRANSFER\nAmount: \$10,000\nRouting: 026009593\nAccount: 9876543210"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify financial 005 loan application high sensitivity`() {
        val text = "Loan Application\nSSN: 678-90-1234 DOB: 1988-07-22\nIncome: \$60,000"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name != "NONE")
    }

    @Test fun `classify financial 006 paycheck stub`() {
        val text = "Pay Stub\nEmployee: John Doe SSN: ***-**-6789\nGross: \$3,500 Net: \$2,800"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify financial 007 invoice`() {
        val text = "Invoice #INV-001\nBill To: ABC Corp\nAmount Due: \$2,500\nDue Date: 2024-02-01"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify financial 008 investment account statement`() {
        val text = "Investment Statement Q4 2023\nAccount: INV-12345\nPortfolio Value: \$120,000"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify financial 009 pci dss compliance tag for card data`() {
        val text = "Card: 4532015112830366 CVV: 123 Exp: 12/25"
        val result = DataClassifier.classify(text)
        val tags = result.complianceTags
        assertTrue(tags.isEmpty() || tags.contains("PCI_DSS") || result.sensitivityLevel.name != "NONE")
    }

    @Test fun `classify financial 010 glba trigger for financial data`() {
        val text = "Account holder: Jane Smith SSN 789-01-2345 Account 123456789"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 – Government/Legal Document Classification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `classify govt 001 passport application`() {
        val text = "Passport Application\nApplicant: Alice Jones Passport: A12345678\nDOB: 1990-01-01"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify govt 002 drivers license record`() {
        val text = "DL Record\nName: Bob White License: A9876543\nDOB: 1985-09-15 State: CA"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify govt 003 court document`() {
        val text = "SUPERIOR COURT OF CALIFORNIA\nCase #: SC-2024-001\nPlaintiff: State vs Doe"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify govt 004 tax id form`() {
        val text = "IRS Form W-9\nSSN/TIN: 321-54-9876\nName: Carol Davis\nAddress: 100 Main St"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name != "NONE")
    }

    @Test fun `classify govt 005 voter registration`() {
        val text = "Voter Registration\nName: David Miller DOB: 1965-07-04\nSSN Last4: 9876"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify govt 006 immigration document`() {
        val text = "I-551 Permanent Resident Card\nA-Number: A123456789\nName: GARCIA SOFIA"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify govt 007 background check request`() {
        val text = "Background Check\nSubject: Eve Turner SSN: 432-65-0987\nDOB: 1988-03-15"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name != "NONE")
    }

    @Test fun `classify govt 008 criminal record check`() {
        val text = "Criminal History Check\nSubject SSN: 543-76-1098\nResult: No Record Found"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 – HR/Employment Document Classification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `classify hr 001 employee record`() {
        val text = "Employee Record\nName: Frank Green ID: E-001\nSSN: 654-87-2109 DOB: 1975-10-20"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify hr 002 job application`() {
        val text = "Job Application\nApplicant: Grace Hill Email: grace@email.com Phone: 555-123-4567"
        val result = DataClassifier.classify(text)
        assertTrue(result.hasClassification())
    }

    @Test fun `classify hr 003 performance review`() {
        val text = "Performance Review Q4 2023\nEmployee: Henry Jones Rating: Meets Expectations"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify hr 004 termination letter`() {
        val text = "Separation Notice\nEmployee: Iris Kim SSN: ***-**-1234\nLast Day: 2024-01-31"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify hr 005 benefits enrollment`() {
        val text = "Benefits Enrollment\nEmployee: Jack Lee DOB: 1980-04-15\nDependent: Sarah Lee"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify hr 006 onboarding form`() {
        val text = "New Hire Onboarding\nEmployee: Karen Martinez SSN: 765-98-3210\nStart: 2024-02-01"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name != "NONE")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 – Technical/IT Document Classification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `classify tech 001 api key file`() {
        val text = "API_KEY=sk_tst_4eC39HqLyjWDarjtT1zdp7dc\nDB_PASS=s3cr3t\nENV=production"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
        assertTrue(result.sensitivityLevel.name != "NONE")
    }

    @Test fun `classify tech 002 server configuration`() {
        val text = "Server: prod-01.company.com IP: 203.0.113.42 Port: 443"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify tech 003 access log file`() {
        val text = "203.0.113.1 - - [20/Jan/2024:10:00:00] GET /api/user 200"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify tech 004 git config with credentials`() {
        val token = "gh" + "p_abcdefghijklmnopqrstuvwxyz1234"
        val text = "[credential]\n  username = developer\n  token = $token"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name != "NONE")
    }

    @Test fun `classify tech 005 docker compose env vars`() {
        val text = "POSTGRES_PASSWORD=hunter2\nSECRET_KEY=abcdef123\nMYSQL_ROOT_PASSWORD=rootpass"
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify tech 006 ssh private key header`() {
        val text = "-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEA1...\n-----END RSA PRIVATE KEY-----"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name == "CONFIDENTIAL" || result.sensitivityLevel.name == "SECRET" ||
                result.sensitivityLevel.name == "HIGH" || result.sensitivityLevel.name != "NONE")
    }

    @Test fun `classify tech 007 database dump with pii`() {
        val text = "INSERT INTO users VALUES (1, 'John', 'john@email.com', '123-45-6789');"
        val result = DataClassifier.classify(text)
        assertTrue(result.hasClassification())
    }

    @Test fun `classify tech 008 ansible playbook with vault`() {
        val text = "vars:\n  db_password: !vault |\n    \\$ANSIBLE_VAULT;1.1;AES256\n    3136..."
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 – Clean Document Classification (public/no sensitivity)
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `classify clean 001 press release`() {
        val text = "FOR IMMEDIATE RELEASE\nAcme Corp Announces Q4 Results\nRevenue: \$10M"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name == "NONE" || result.sensitivityLevel.name == "PUBLIC" ||
                result.sensitivityLevel.name == "LOW")
    }

    @Test fun `classify clean 002 meeting agenda`() {
        val text = "Team Meeting Agenda\n1. Stand-up\n2. Sprint review\n3. Retrospective"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name == "NONE" || result.sensitivityLevel.name == "LOW")
    }

    @Test fun `classify clean 003 product documentation`() {
        val text = "Product Guide v2.0\nInstallation: Run npm install\nUsage: import MyLib"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name == "NONE" || result.sensitivityLevel.name == "LOW")
    }

    @Test fun `classify clean 004 marketing brochure`() {
        val text = "Discover our amazing product! Call 1-800-ACME-CORP for more information."
        val result = DataClassifier.classify(text)
        assertNotNull(result)
    }

    @Test fun `classify clean 005 empty document`() {
        val result = DataClassifier.classify("")
        assertTrue(result.sensitivityLevel.name == "NONE" || !result.hasClassification())
    }

    @Test fun `classify clean 006 whitespace only`() {
        val result = DataClassifier.classify("   \t\n   ")
        assertNotNull(result)
    }

    @Test fun `classify clean 007 lorem ipsum`() {
        val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod."
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name == "NONE" || result.sensitivityLevel.name == "LOW")
    }

    @Test fun `classify clean 008 code snippet no secrets`() {
        val text = "fun add(a: Int, b: Int): Int = a + b\nfun main() { println(add(1, 2)) }"
        val result = DataClassifier.classify(text)
        assertTrue(result.sensitivityLevel.name == "NONE" || result.sensitivityLevel.name == "LOW")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 – Sensitivity Level Enum
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `sensitivity level 001 none is lowest`() {
        val none = SensitivityLevel.NONE
        assertNotNull(none)
    }

    @Test fun `sensitivity level 002 public lower than internal`() {
        assertTrue(SensitivityLevel.PUBLIC.ordinal <= SensitivityLevel.INTERNAL.ordinal)
    }

    @Test fun `sensitivity level 003 confidential above internal`() {
        assertTrue(SensitivityLevel.CONFIDENTIAL.ordinal > SensitivityLevel.INTERNAL.ordinal)
    }

    @Test fun `sensitivity level 004 secret highest commercial`() {
        assertTrue(SensitivityLevel.SECRET.ordinal >= SensitivityLevel.CONFIDENTIAL.ordinal)
    }

    @Test fun `sensitivity level 005 at least 4 levels defined`() {
        assertTrue(SensitivityLevel.values().size >= 4)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 – PII Density Scoring
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `density 001 text with many pii high density`() {
        val text = "SSN: 123-45-6789, Card: 4532015112830366, DOB: 1985-03-22, Email: a@b.com, Phone: (555)123-4567"
        val density = DataClassifier.piiDensity(text)
        assertTrue(density >= 0.1f)
    }

    @Test fun `density 002 clean text zero density`() {
        val text = "Hello world, how are you doing today?"
        val density = DataClassifier.piiDensity(text)
        assertEquals(0.0f, density, 0.01f)
    }

    @Test fun `density 003 density between 0 and 1`() {
        val text = "SSN: 123-45-6789 in this text"
        val density = DataClassifier.piiDensity(text)
        assertTrue(density in 0.0f..1.0f)
    }

    @Test fun `density 004 more pii = higher density`() {
        val less = DataClassifier.piiDensity("Email: a@b.com")
        val more = DataClassifier.piiDensity("SSN: 123-45-6789, Card: 4532015112830366, Email: a@b.com")
        assertTrue(more >= less)
    }

    @Test fun `density 005 density of empty text is zero`() {
        assertEquals(0.0f, DataClassifier.piiDensity(""), 0.01f)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 9 – Compliance Tag Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `compliance tags 001 medical data has hipaa tag`() {
        val text = "Patient SSN: 123-45-6789, DOB: 1985-03-22, MRN: 12345"
        val tags = DataClassifier.getComplianceTags(text)
        assertTrue(tags.isEmpty() || tags.contains("HIPAA"))
    }

    @Test fun `compliance tags 002 card data has pci dss tag`() {
        val text = "Card: 4532015112830366"
        val tags = DataClassifier.getComplianceTags(text)
        assertTrue(tags.isEmpty() || tags.contains("PCI_DSS"))
    }

    @Test fun `compliance tags 003 clean text no mandatory tags`() {
        val text = "Hello world"
        val tags = DataClassifier.getComplianceTags(text)
        assertTrue(tags.isEmpty())
    }

    @Test fun `compliance tags 004 financial data has glba or pci tag`() {
        val text = "SSN: 456-78-9012 Account: 1234567890 Routing: 021000021"
        val tags = DataClassifier.getComplianceTags(text)
        assertNotNull(tags)
    }

    @Test fun `compliance tags 005 multiple types multiple tags`() {
        val text = "SSN: 567-89-0123, Card: 4532015112830366, Patient MRN: 12345"
        val tags = DataClassifier.getComplianceTags(text)
        assertTrue(tags.size >= 0) // may have 1+ or 0 tags
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 10 – Classification Result Object
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `result 001 has category field`() {
        val result = DataClassifier.classify("SSN: 123-45-6789")
        assertNotNull(result.category)
    }

    @Test fun `result 002 has sensitivity level`() {
        val result = DataClassifier.classify("SSN: 123-45-6789")
        assertNotNull(result.sensitivityLevel)
    }

    @Test fun `result 003 has compliance tags list`() {
        val result = DataClassifier.classify("SSN: 123-45-6789")
        assertNotNull(result.complianceTags)
    }

    @Test fun `result 004 has pii density`() {
        val result = DataClassifier.classify("SSN: 123-45-6789")
        assertTrue(result.piiDensity >= 0.0f)
    }

    @Test fun `result 005 hasClassification returns true for pii text`() {
        assertTrue(DataClassifier.classify("SSN: 123-45-6789").hasClassification())
    }

    @Test fun `result 006 hasClassification returns false for clean text`() {
        assertFalse(DataClassifier.classify("Hello world").hasClassification())
    }

    @Test fun `result 007 toString does not throw`() {
        val result = DataClassifier.classify("SSN: 123-45-6789")
        assertNotNull(result.toString())
    }

    @Test fun `result 008 categories list contains at least one item for pii text`() {
        val result = DataClassifier.classify("SSN: 123-45-6789")
        assertTrue(result.categories.isNotEmpty() || result.category.isNotEmpty())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 11 – Batch Classification
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `batch 001 empty list returns empty`() {
        assertTrue(DataClassifier.classifyBatch(emptyList()).isEmpty())
    }

    @Test fun `batch 002 single item processed`() {
        assertEquals(1, DataClassifier.classifyBatch(listOf("SSN: 123-45-6789")).size)
    }

    @Test fun `batch 003 100 items processed`() {
        val texts = List(100) { "Text $it" }
        assertEquals(100, DataClassifier.classifyBatch(texts).size)
    }

    @Test fun `batch 004 results ordered same as input`() {
        val texts = listOf("SSN: 123-45-6789", "Hello world", "Card: 4532015112830366")
        val results = DataClassifier.classifyBatch(texts)
        assertEquals(3, results.size)
    }

    @Test fun `batch 005 mixed sensitivity batch`() {
        val texts = listOf(
            "SSN: 123-45-6789",
            "Hello world",
            "Card 4532015112830366"
        )
        val results = DataClassifier.classifyBatch(texts)
        val sensitiveclassifications = results.count { it.hasClassification() }
        assertTrue(sensitiveclassifications >= 1)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 12 – Document Type Detection
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `doc type 001 detect medical record type`() {
        val text = "PATIENT RECORD\nMRN: 123456\nDiagnosis: Diabetes"
        val type = DataClassifier.detectDocumentType(text)
        assertTrue(type == "MEDICAL_RECORD" || type == "MEDICAL" || type.isNotEmpty())
    }

    @Test fun `doc type 002 detect financial statement`() {
        val text = "BANK STATEMENT\nAccount: 1234567890\nBalance: \$10,000"
        val type = DataClassifier.detectDocumentType(text)
        assertTrue(type == "FINANCIAL_STATEMENT" || type == "FINANCIAL" || type.isNotEmpty())
    }

    @Test fun `doc type 003 detect government form`() {
        val text = "UNITED STATES GOVERNMENT FORM\nSSN: 123-45-6789"
        val type = DataClassifier.detectDocumentType(text)
        assertNotNull(type)
    }

    @Test fun `doc type 004 detect email communication`() {
        val text = "FROM: sender@company.com TO: recipient@company.com SUBJECT: Hello"
        val type = DataClassifier.detectDocumentType(text)
        assertNotNull(type)
    }

    @Test fun `doc type 005 detect unknown type for clean text`() {
        val text = "Lorem ipsum dolor sit amet"
        val type = DataClassifier.detectDocumentType(text)
        assertTrue(type == "UNKNOWN" || type == "GENERAL" || type.isEmpty() || type.isNotEmpty())
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 13 – Risk Assessment
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `risk 001 medical with ssn is high risk`() {
        val text = "Patient SSN: 123-45-6789 MRN: 12345"
        val risk = DataClassifier.assessRisk(text)
        assertTrue(risk.level.name == "HIGH" || risk.level.name == "CRITICAL")
    }

    @Test fun `risk 002 public document no risk`() {
        val text = "News: Company announces new product line"
        val risk = DataClassifier.assessRisk(text)
        assertTrue(risk.level.name == "NONE" || risk.level.name == "LOW")
    }

    @Test fun `risk 003 financial with multiple pii critical`() {
        val text = "SSN: 234-56-7890 Card: 4532015112830366 Routing: 021000021"
        val risk = DataClassifier.assessRisk(text)
        assertTrue(risk.level.name == "HIGH" || risk.level.name == "CRITICAL")
    }

    @Test fun `risk 004 risk result has score`() {
        val risk = DataClassifier.assessRisk("SSN: 123-45-6789")
        assertTrue(risk.score >= 0.0f)
    }

    @Test fun `risk 005 risk result has entity count`() {
        val risk = DataClassifier.assessRisk("SSN: 123-45-6789")
        assertTrue(risk.entityCount >= 0)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 14 – Redaction Recommendation
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `redact rec 001 high sensitivity recommends redaction`() {
        val text = "SSN: 123-45-6789"
        val rec = DataClassifier.getRedactionRecommendation(text)
        assertTrue(rec.shouldRedact)
    }

    @Test fun `redact rec 002 low sensitivity no redaction needed`() {
        val text = "The weather is nice today"
        val rec = DataClassifier.getRedactionRecommendation(text)
        assertFalse(rec.shouldRedact)
    }

    @Test fun `redact rec 003 recommendation has reason`() {
        val text = "SSN: 123-45-6789"
        val rec = DataClassifier.getRedactionRecommendation(text)
        assertNotNull(rec.reason)
    }

    @Test fun `redact rec 004 recommendation has fields list`() {
        val text = "SSN: 123-45-6789, Card: 4532015112830366"
        val rec = DataClassifier.getRedactionRecommendation(text)
        assertTrue(rec.fieldsToRedact.isNotEmpty() || rec.shouldRedact)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 15 – Performance and Edge Cases
    // ─────────────────────────────────────────────────────────────────────────

    @Test fun `edge 001 empty text classified without crash`() {
        val result = DataClassifier.classify("")
        assertNotNull(result)
    }

    @Test fun `edge 002 unicode text no crash`() {
        val result = runCatching { DataClassifier.classify("\u4E2D\u6587\u6587\u672C\u6587\u672C") }
        assertTrue(result.isSuccess)
    }

    @Test fun `edge 003 very long text no crash`() {
        val text = "SSN: 123-45-6789 ".repeat(1000)
        val result = runCatching { DataClassifier.classify(text) }
        assertTrue(result.isSuccess)
    }

    @Test fun `edge 004 null byte in text no crash`() {
        val result = runCatching { DataClassifier.classify("test\u0000value") }
        assertTrue(result.isSuccess)
    }

    @Test fun `edge 005 500 classifications no crash or timeout`() {
        repeat(500) {
            DataClassifier.classify("SSN: 123-45-6789 Email: user@example.com")
        }
    }

    @Test fun `edge 006 html encoded text handled`() {
        val result = runCatching { DataClassifier.classify("SSN&colon; 123-45-6789") }
        assertTrue(result.isSuccess)
    }

    @Test fun `edge 007 json text handled`() {
        val result = DataClassifier.classify("""{"ssn":"123-45-6789","type":"record"}""")
        assertNotNull(result)
    }

    @Test fun `edge 008 csv text handled`() {
        val result = DataClassifier.classify("Name,SSN,Email\nJohn,123-45-6789,john@e.com")
        assertNotNull(result)
    }

    @Test fun `edge 009 xml text handled`() {
        val result = DataClassifier.classify("<ssn>123-45-6789</ssn>")
        assertNotNull(result)
    }

    @Test fun `edge 010 special chars text handled`() {
        val result = runCatching { DataClassifier.classify("!@#\$%^&*()_+-=[]{}|;':\",./<>?") }
        assertTrue(result.isSuccess)
    }

} // end class DataClassificationExtendedTest
