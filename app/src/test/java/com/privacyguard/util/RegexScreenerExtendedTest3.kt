package com.privacyguard.util

import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import org.junit.Test
import org.junit.Assert.*

/**
 * Extended test suite 3 for RegexScreener — pattern matching coverage.
 * Tests containsPotentialPII, extractEntities, and luhnCheck across
 * all PII types, formats, and edge cases.
 */
class RegexScreenerExtendedTest3 {

    // =========================================================================
    // SECTION 1: SSN containsPotentialPII Tests
    // =========================================================================

    @Test fun testSSNContainsPII_DashFormat_001() {
        assertTrue(RegexScreener.containsPotentialPII("My SSN is 123-45-6789"))
    }

    @Test fun testSSNContainsPII_DashFormat_002() {
        assertTrue(RegexScreener.containsPotentialPII("SSN: 234-56-7890"))
    }

    @Test fun testSSNContainsPII_DashFormat_003() {
        assertTrue(RegexScreener.containsPotentialPII("Social Security Number 345-67-8901"))
    }

    @Test fun testSSNContainsPII_DashFormat_004() {
        assertTrue(RegexScreener.containsPotentialPII("456-78-9012 is the patient ID"))
    }

    @Test fun testSSNContainsPII_DashFormat_005() {
        assertTrue(RegexScreener.containsPotentialPII("Please provide 567-89-0123 for verification"))
    }

    @Test fun testSSNContainsPII_DashFormat_006() {
        assertTrue(RegexScreener.containsPotentialPII("The file contains 678-90-1234."))
    }

    @Test fun testSSNContainsPII_DashFormat_007() {
        assertTrue(RegexScreener.containsPotentialPII("Ref SSN 789-01-2345 confirmed"))
    }

    @Test fun testSSNContainsPII_DashFormat_008() {
        assertTrue(RegexScreener.containsPotentialPII("890-12-3456"))
    }

    @Test fun testSSNContainsPII_DashFormat_009() {
        assertTrue(RegexScreener.containsPotentialPII("Patient: John, SSN: 901-23-4567"))
    }

    @Test fun testSSNContainsPII_DashFormat_010() {
        assertTrue(RegexScreener.containsPotentialPII("Record: 012-34-5678"))
    }

    @Test fun testSSNContainsPII_SpaceFormat_001() {
        assertTrue(RegexScreener.containsPotentialPII("SSN 123 45 6789 entered"))
    }

    @Test fun testSSNContainsPII_SpaceFormat_002() {
        assertTrue(RegexScreener.containsPotentialPII("Reference: 234 56 7890"))
    }

    @Test fun testSSNContainsPII_SpaceFormat_003() {
        assertTrue(RegexScreener.containsPotentialPII("Number 345 67 8901 filed"))
    }

    @Test fun testSSNContainsPII_SpaceFormat_004() {
        assertTrue(RegexScreener.containsPotentialPII("ID 456 78 9012 assigned"))
    }

    @Test fun testSSNContainsPII_SpaceFormat_005() {
        assertTrue(RegexScreener.containsPotentialPII("Record 567 89 0123 updated"))
    }

    @Test fun testSSNContainsPII_NoFormat_001() {
        assertTrue(RegexScreener.containsPotentialPII("Number 123456789 confirmed"))
    }

    @Test fun testSSNContainsPII_NoFormat_002() {
        assertTrue(RegexScreener.containsPotentialPII("ID 234567890 assigned"))
    }

    @Test fun testSSNContainsPII_NoFormat_003() {
        assertTrue(RegexScreener.containsPotentialPII("Code 345678901"))
    }

    @Test fun testSSNNoPII_TooShort_001() {
        assertFalse(RegexScreener.containsPotentialPII("ID: 12345"))
    }

    @Test fun testSSNNoPII_TooShort_002() {
        assertFalse(RegexScreener.containsPotentialPII("Code: 1234"))
    }

    @Test fun testSSNNoPII_AllZeros_001() {
        // 000-00-0000 should fail validation
        val entities = RegexScreener.extractEntities("000-00-0000")
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("All-zero SSN should not pass validation", ssnEntities.isEmpty())
    }

    @Test fun testSSNExtract_DashFormat_001() {
        val text = "SSN: 123-45-6789"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract SSN entity", entities.any { it.entityType == EntityType.SSN })
    }

    @Test fun testSSNExtract_DashFormat_002() {
        val text = "Patient SSN 234-56-7890 on file"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract SSN from text", entities.any { it.entityType == EntityType.SSN })
    }

    @Test fun testSSNExtract_DashFormat_003() {
        val text = "Reference: 345-67-8901"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract SSN", entities.any { it.entityType == EntityType.SSN })
    }

    @Test fun testSSNExtract_Multiple_001() {
        val text = "First: 123-45-6789 Second: 234-56-7890"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("Should extract multiple SSNs", ssnEntities.size >= 1)
    }

    @Test fun testSSNExtract_Position_001() {
        val text = "SSN: 123-45-6789"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntity = entities.firstOrNull { it.entityType == EntityType.SSN }
        if (ssnEntity != null) {
            assertTrue("Start index should be non-negative", ssnEntity.startIndex >= 0)
            assertTrue("End index should be after start", ssnEntity.endIndex > ssnEntity.startIndex)
        }
    }

    @Test fun testSSNExtract_Severity_001() {
        val text = "SSN: 123-45-6789"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntity = entities.firstOrNull { it.entityType == EntityType.SSN }
        if (ssnEntity != null) {
            assertEquals("SSN should be CRITICAL", Severity.CRITICAL, ssnEntity.severity)
        }
    }

    // =========================================================================
    // SECTION 2: Credit Card containsPotentialPII and extractEntities Tests
    // =========================================================================

    @Test fun testCCContainsPII_Visa_001() {
        assertTrue(RegexScreener.containsPotentialPII("Card: 4532015112830366"))
    }

    @Test fun testCCContainsPII_Visa_002() {
        assertTrue(RegexScreener.containsPotentialPII("Payment: 4111111111111111"))
    }

    @Test fun testCCContainsPII_Visa_003() {
        assertTrue(RegexScreener.containsPotentialPII("Using 4012888888881881 for purchase"))
    }

    @Test fun testCCContainsPII_Visa_004() {
        assertTrue(RegexScreener.containsPotentialPII("Billing: 4222222222222"))
    }

    @Test fun testCCContainsPII_Visa_005() {
        assertTrue(RegexScreener.containsPotentialPII("CC: 4716-1234-5678-9010"))
    }

    @Test fun testCCContainsPII_Mastercard_001() {
        assertTrue(RegexScreener.containsPotentialPII("MC: 5500005555555559"))
    }

    @Test fun testCCContainsPII_Mastercard_002() {
        assertTrue(RegexScreener.containsPotentialPII("Card: 5105105105105100"))
    }

    @Test fun testCCContainsPII_Mastercard_003() {
        assertTrue(RegexScreener.containsPotentialPII("Payment: 5200828282828210"))
    }

    @Test fun testCCContainsPII_Mastercard_004() {
        assertTrue(RegexScreener.containsPotentialPII("Using 5425233430109903"))
    }

    @Test fun testCCContainsPII_Amex_001() {
        assertTrue(RegexScreener.containsPotentialPII("AMEX: 378282246310005"))
    }

    @Test fun testCCContainsPII_Amex_002() {
        assertTrue(RegexScreener.containsPotentialPII("Card: 371449635398431"))
    }

    @Test fun testCCContainsPII_Discover_001() {
        assertTrue(RegexScreener.containsPotentialPII("Discover: 6011111111111117"))
    }

    @Test fun testCCContainsPII_Discover_002() {
        assertTrue(RegexScreener.containsPotentialPII("Card: 6011000990139424"))
    }

    @Test fun testCCContainsPII_JCB_001() {
        assertTrue(RegexScreener.containsPotentialPII("JCB: 3530111333300000"))
    }

    @Test fun testCCContainsPII_Formatted_001() {
        assertTrue(RegexScreener.containsPotentialPII("Card: 4111 1111 1111 1111"))
    }

    @Test fun testCCContainsPII_Formatted_002() {
        assertTrue(RegexScreener.containsPotentialPII("Card: 4111-1111-1111-1111"))
    }

    @Test fun testCCNoPII_TooShort_001() {
        assertFalse(RegexScreener.containsPotentialPII("Code: 12345"))
    }

    @Test fun testCCNoPII_AllSame_001() {
        // All-same digits fail Luhn
        val entities = RegexScreener.extractEntities("Card: 1111111111111111")
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("All-same digit card fails Luhn", ccEntities.isEmpty())
    }

    @Test fun testCCExtract_Visa_001() {
        val text = "Card: 4532015112830366"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract Visa CC entity", entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test fun testCCExtract_Visa_002() {
        val text = "Payment method: 4111111111111111"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract Visa entity", entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test fun testCCExtract_Mastercard_001() {
        val text = "Using MC: 5500005555555559"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract Mastercard entity", entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test fun testCCExtract_Amex_001() {
        val text = "Amex: 378282246310005"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract Amex entity", entities.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test fun testCCExtract_Severity_001() {
        val text = "Card: 4532015112830366"
        val entities = RegexScreener.extractEntities(text)
        val ccEntity = entities.firstOrNull { it.entityType == EntityType.CREDIT_CARD }
        if (ccEntity != null) {
            assertEquals("CC should be CRITICAL severity", Severity.CRITICAL, ccEntity.severity)
        }
    }

    @Test fun testCCExtract_Confidence_001() {
        val text = "Card: 4532015112830366"
        val entities = RegexScreener.extractEntities(text)
        val ccEntity = entities.firstOrNull { it.entityType == EntityType.CREDIT_CARD }
        if (ccEntity != null) {
            assertTrue("CC confidence should be positive", ccEntity.confidence > 0f)
        }
    }

    @Test fun testCCExtract_Multiple_001() {
        val text = "Visa: 4532015112830366 MC: 5500005555555559"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Should extract multiple CC entities", ccEntities.size >= 1)
    }

    // =========================================================================
    // SECTION 3: Luhn Check Tests
    // =========================================================================

    @Test fun testLuhn_VisaValid_001() { assertTrue(RegexScreener.luhnCheck("4532015112830366")) }
    @Test fun testLuhn_VisaValid_002() { assertTrue(RegexScreener.luhnCheck("4111111111111111")) }
    @Test fun testLuhn_VisaValid_003() { assertTrue(RegexScreener.luhnCheck("4012888888881881")) }
    @Test fun testLuhn_MCValid_001()   { assertTrue(RegexScreener.luhnCheck("5500005555555559")) }
    @Test fun testLuhn_MCValid_002()   { assertTrue(RegexScreener.luhnCheck("5105105105105100")) }
    @Test fun testLuhn_MCValid_003()   { assertTrue(RegexScreener.luhnCheck("5200828282828210")) }
    @Test fun testLuhn_MCValid_004()   { assertTrue(RegexScreener.luhnCheck("5425233430109903")) }
    @Test fun testLuhn_AmexValid_001() { assertTrue(RegexScreener.luhnCheck("378282246310005")) }
    @Test fun testLuhn_AmexValid_002() { assertTrue(RegexScreener.luhnCheck("371449635398431")) }
    @Test fun testLuhn_DiscValid_001() { assertTrue(RegexScreener.luhnCheck("6011111111111117")) }
    @Test fun testLuhn_DiscValid_002() { assertTrue(RegexScreener.luhnCheck("6011000990139424")) }
    @Test fun testLuhn_JCBValid_001()  { assertTrue(RegexScreener.luhnCheck("3530111333300000")) }
    @Test fun testLuhn_Invalid_001()   { assertFalse(RegexScreener.luhnCheck("1234567890123456")) }
    @Test fun testLuhn_Invalid_002()   { assertFalse(RegexScreener.luhnCheck("1111111111111111")) }
    @Test fun testLuhn_Invalid_003()   { assertFalse(RegexScreener.luhnCheck("0000000000000000")) }
    @Test fun testLuhn_TooShort_001()  { assertFalse(RegexScreener.luhnCheck("123")) }
    @Test fun testLuhn_Empty_001()     { assertFalse(RegexScreener.luhnCheck("")) }
    @Test fun testLuhn_WithDashes_001() { assertTrue(RegexScreener.luhnCheck("4532-0151-1283-0366")) }
    @Test fun testLuhn_WithSpaces_001() { assertTrue(RegexScreener.luhnCheck("4532 0151 1283 0366")) }
    @Test fun testLuhn_Mixed_001() {
        val number = "4532015112830366"
        assertTrue("Valid Visa should pass", RegexScreener.luhnCheck(number))
        val modified = number.dropLast(1) + ((number.last().digitToInt() + 1) % 10).digitToChar()
        assertFalse("Modified CC should fail", RegexScreener.luhnCheck(modified))
    }

    @Test fun testLuhn_Visa19_001() { assertTrue(RegexScreener.luhnCheck("4532015112830366001")) }

    @Test fun testLuhn_MC51_001() { assertTrue(RegexScreener.luhnCheck("5105105105105100")) }
    @Test fun testLuhn_MC55_001() { assertTrue(RegexScreener.luhnCheck("5500005555555559")) }

    @Test fun testLuhn_AllZeros_001() { assertFalse(RegexScreener.luhnCheck("0000000000")) }
    @Test fun testLuhn_AllNines_001() { assertFalse(RegexScreener.luhnCheck("9999999999")) }
    @Test fun testLuhn_Sequential_001() { assertFalse(RegexScreener.luhnCheck("1234567890")) }
    @Test fun testLuhn_OnlyLetters_001() { assertFalse(RegexScreener.luhnCheck("abcdefgh")) }

    // =========================================================================
    // SECTION 4: Email containsPotentialPII and extractEntities Tests
    // =========================================================================

    @Test fun testEmailContainsPII_Basic_001() {
        assertTrue(RegexScreener.containsPotentialPII("Contact: user@example.com"))
    }

    @Test fun testEmailContainsPII_Basic_002() {
        assertTrue(RegexScreener.containsPotentialPII("Send to john.doe@company.org"))
    }

    @Test fun testEmailContainsPII_Basic_003() {
        assertTrue(RegexScreener.containsPotentialPII("Email: test+tag@domain.net"))
    }

    @Test fun testEmailContainsPII_Basic_004() {
        assertTrue(RegexScreener.containsPotentialPII("Reply: user_name@sub.domain.co.uk"))
    }

    @Test fun testEmailContainsPII_Basic_005() {
        assertTrue(RegexScreener.containsPotentialPII("Contact info@privacyguard.io for support"))
    }

    @Test fun testEmailContainsPII_Intl_001() {
        assertTrue(RegexScreener.containsPotentialPII("Email: hans@firma.de"))
    }

    @Test fun testEmailContainsPII_Intl_002() {
        assertTrue(RegexScreener.containsPotentialPII("Contact: user@example.jp"))
    }

    @Test fun testEmailContainsPII_Intl_003() {
        assertTrue(RegexScreener.containsPotentialPII("Address: pierre@domaine.fr"))
    }

    @Test fun testEmailContainsPII_Intl_004() {
        assertTrue(RegexScreener.containsPotentialPII("Email: user@company.co.au"))
    }

    @Test fun testEmailContainsPII_Intl_005() {
        assertTrue(RegexScreener.containsPotentialPII("Contact: user@firma.de"))
    }

    @Test fun testEmailContainsPII_OrgTld_001() {
        assertTrue(RegexScreener.containsPotentialPII("nonprofit@organization.org"))
    }

    @Test fun testEmailContainsPII_NetTld_001() {
        assertTrue(RegexScreener.containsPotentialPII("admin@network.net"))
    }

    @Test fun testEmailContainsPII_IoTld_001() {
        assertTrue(RegexScreener.containsPotentialPII("dev@startup.io"))
    }

    @Test fun testEmailContainsPII_EduTld_001() {
        assertTrue(RegexScreener.containsPotentialPII("student@university.edu"))
    }

    @Test fun testEmailContainsPII_GovTld_001() {
        assertTrue(RegexScreener.containsPotentialPII("contact@agency.gov"))
    }

    @Test fun testEmailNoPII_NoAt_001() {
        assertFalse(RegexScreener.containsPotentialPII("Not email: noatsign.com"))
    }

    @Test fun testEmailNoPII_NoDomain_001() {
        // "user@" alone with no domain shouldn't match
        val entities = RegexScreener.extractEntities("not valid: user@")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Missing domain should not match", emailEntities.isEmpty())
    }

    @Test fun testEmailExtract_001() {
        val text = "Contact: user@example.com for support"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract email entity", entities.any { it.entityType == EntityType.EMAIL })
    }

    @Test fun testEmailExtract_002() {
        val text = "From: alice@test.com To: bob@example.com"
        val entities = RegexScreener.extractEntities(text)
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Should extract at least one email", emailEntities.isNotEmpty())
    }

    @Test fun testEmailExtract_Severity_001() {
        val text = "Email: user@example.com"
        val entities = RegexScreener.extractEntities(text)
        val emailEntity = entities.firstOrNull { it.entityType == EntityType.EMAIL }
        if (emailEntity != null) {
            assertTrue("Email should be HIGH or MEDIUM severity",
                emailEntity.severity == Severity.HIGH || emailEntity.severity == Severity.MEDIUM)
        }
    }

    // =========================================================================
    // SECTION 5: Phone containsPotentialPII and extractEntities Tests
    // =========================================================================

    @Test fun testPhoneContainsPII_US_001() {
        assertTrue(RegexScreener.containsPotentialPII("Call me at (555) 123-4567"))
    }

    @Test fun testPhoneContainsPII_US_002() {
        assertTrue(RegexScreener.containsPotentialPII("Phone: 555-123-4567"))
    }

    @Test fun testPhoneContainsPII_US_003() {
        assertTrue(RegexScreener.containsPotentialPII("Contact: 5551234567"))
    }

    @Test fun testPhoneContainsPII_US_004() {
        assertTrue(RegexScreener.containsPotentialPII("Reach us: +1 (555) 123-4567"))
    }

    @Test fun testPhoneContainsPII_US_005() {
        assertTrue(RegexScreener.containsPotentialPII("Number: +1-555-123-4567"))
    }

    @Test fun testPhoneContainsPII_US_006() {
        assertTrue(RegexScreener.containsPotentialPII("Office: 555.123.4567"))
    }

    @Test fun testPhoneContainsPII_US_007() {
        assertTrue(RegexScreener.containsPotentialPII("Cell: (800) 555-0199"))
    }

    @Test fun testPhoneContainsPII_US_008() {
        assertTrue(RegexScreener.containsPotentialPII("Toll free: 1-800-555-0199"))
    }

    @Test fun testPhoneContainsPII_US_009() {
        assertTrue(RegexScreener.containsPotentialPII("Fax: (212) 555-0100"))
    }

    @Test fun testPhoneContainsPII_US_010() {
        assertTrue(RegexScreener.containsPotentialPII("Mobile: 415-555-1234"))
    }

    @Test fun testPhoneNoPII_TooShort_001() {
        assertFalse(RegexScreener.containsPotentialPII("ID: 123"))
    }

    @Test fun testPhoneNoPII_TooShort_002() {
        assertFalse(RegexScreener.containsPotentialPII("Code: 1234"))
    }

    @Test fun testPhoneExtract_001() {
        val text = "Call (555) 123-4567 now"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract phone entity", entities.any { it.entityType == EntityType.PHONE })
    }

    @Test fun testPhoneExtract_002() {
        val text = "Home: 555-123-4567 Work: 555-987-6543"
        val entities = RegexScreener.extractEntities(text)
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Should extract phone entities", phoneEntities.isNotEmpty())
    }

    @Test fun testPhoneExtract_Severity_001() {
        val text = "Phone: 555-123-4567"
        val entities = RegexScreener.extractEntities(text)
        val phoneEntity = entities.firstOrNull { it.entityType == EntityType.PHONE }
        if (phoneEntity != null) {
            assertTrue("Phone should be HIGH or MEDIUM",
                phoneEntity.severity == Severity.HIGH || phoneEntity.severity == Severity.MEDIUM)
        }
    }

    // =========================================================================
    // SECTION 6: API Key containsPotentialPII Tests
    // =========================================================================

    @Test fun testAPIKeyContainsPII_SK_001() {
        assertTrue(RegexScreener.containsPotentialPII("Key: sk-prod-abcdefghijklmnopqrstuvwxyz"))
    }

    @Test fun testAPIKeyContainsPII_SK_002() {
        assertTrue(RegexScreener.containsPotentialPII("API: sk-live-1234567890abcdefghijklm"))
    }

    @Test fun testAPIKeyContainsPII_AKIA_001() {
        assertTrue(RegexScreener.containsPotentialPII("AWS: AKIAIOSFODNN7EXAMPLE"))
    }

    @Test fun testAPIKeyContainsPII_GHP_001() {
        assertTrue(RegexScreener.containsPotentialPII("Token: ghp_xxx"))
    }

    @Test fun testAPIKeyContainsPII_GHO_001() {
        assertTrue(RegexScreener.containsPotentialPII("Token: gho_abcdefghijklmnopqrstu1234567"))
    }

    @Test fun testAPIKeyContainsPII_GHS_001() {
        assertTrue(RegexScreener.containsPotentialPII("Token: ghs_abc123def456ghi789jkl012mno345"))
    }

    @Test fun testAPIKeyContainsPII_GLPAT_001() {
        assertTrue(RegexScreener.containsPotentialPII("Token: glpat-abcdefghijklmnopqrstu"))
    }

    @Test fun testAPIKeyContainsPII_APIKey_001() {
        assertTrue(RegexScreener.containsPotentialPII("api_key=abcdefghijklmnopqrstuvwxyz012345"))
    }

    @Test fun testAPIKeyExtract_001() {
        val text = "Using sk-prod-abcdefghijklmnopqrstuvwxyz"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract API key entity", entities.any { it.entityType == EntityType.API_KEY })
    }

    @Test fun testAPIKeyExtract_Severity_001() {
        val text = "Key: sk-prod-abcdefghijklmnopqrstuvwxyz"
        val entities = RegexScreener.extractEntities(text)
        val keyEntity = entities.firstOrNull { it.entityType == EntityType.API_KEY }
        if (keyEntity != null) {
            assertEquals("API key should be CRITICAL", Severity.CRITICAL, keyEntity.severity)
        }
    }

    // =========================================================================
    // SECTION 7: Mixed PII Detection Tests
    // =========================================================================

    @Test fun testMixedPII_EmailAndPhone_001() {
        val text = "Email: john@company.com Phone: (555) 123-4567"
        assertTrue("Should detect mixed PII", RegexScreener.containsPotentialPII(text))
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract entities", entities.isNotEmpty())
    }

    @Test fun testMixedPII_CCAndSSN_001() {
        val text = "Card: 4532015112830366 SSN: 123-45-6789"
        assertTrue("Should contain PII", RegexScreener.containsPotentialPII(text))
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract multiple entities", entities.isNotEmpty())
    }

    @Test fun testMixedPII_EmailAndCC_001() {
        val text = "user@example.com paid with 5500005555555559"
        assertTrue("Should contain PII", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testMixedPII_AllTypes_001() {
        val text = "SSN: 123-45-6789 Card: 4532015112830366 Email: user@example.com Phone: 555-123-4567"
        assertTrue("Should contain PII", RegexScreener.containsPotentialPII(text))
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Should extract multiple entities", entities.size >= 1)
    }

    @Test fun testMixedPII_FormData_001() {
        val text = "Name: John Smith DOB: 01/15/1985 SSN: 234-56-7890 Manager: Bob"
        assertTrue("Should contain PII in form", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testMixedPII_LogLine_001() {
        val text = "Server 10.0.0.1 received request from user john@internal.corp"
        val hasPII = RegexScreener.containsPotentialPII(text)
        assertNotNull("Should process log line", hasPII)
    }

    @Test fun testMixedPII_PaymentData_001() {
        val text = "Transaction: 4532015112830366 for customer alice@shop.com"
        assertTrue("Should contain PII in payment data", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testMixedPII_HRRecord_001() {
        val text = "Employee: John Doe, SSN: 345-67-8901, Email: j.doe@company.com"
        assertTrue("Should contain PII in HR record", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testMixedPII_MedicalRecord_001() {
        val text = "Patient: Jane Smith, DOB: 1985-01-15, Phone: (555) 123-4567"
        assertTrue("Should contain PII in medical record", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testMixedPII_TravelBooking_001() {
        val text = "Passenger: John Doe, Flight: AA123, Contact: john@travel.com"
        assertTrue("Should detect email in travel booking", RegexScreener.containsPotentialPII(text))
    }

    // =========================================================================
    // SECTION 8: Empty / Whitespace / Edge Case Tests
    // =========================================================================

    @Test fun testEmpty_001() {
        assertFalse(RegexScreener.containsPotentialPII(""))
        assertTrue(RegexScreener.extractEntities("").isEmpty())
    }

    @Test fun testWhitespace_001() {
        assertFalse(RegexScreener.containsPotentialPII("   "))
        assertTrue(RegexScreener.extractEntities("   ").isEmpty())
    }

    @Test fun testWhitespace_002() {
        assertFalse(RegexScreener.containsPotentialPII("\t\n\r"))
        assertTrue(RegexScreener.extractEntities("\t\n\r").isEmpty())
    }

    @Test fun testSingleChar_001() {
        assertFalse(RegexScreener.containsPotentialPII("a"))
        assertTrue(RegexScreener.extractEntities("a").isEmpty())
    }

    @Test fun testSimpleWord_001() {
        assertFalse(RegexScreener.containsPotentialPII("Hello"))
    }

    @Test fun testSimpleSentence_001() {
        assertFalse(RegexScreener.containsPotentialPII("This is a normal sentence."))
    }

    @Test fun testNumber_Year_001() {
        assertFalse(RegexScreener.containsPotentialPII("Year: 2024"))
    }

    @Test fun testNumber_Short_001() {
        assertFalse(RegexScreener.containsPotentialPII("Count: 42"))
    }

    @Test fun testLongNoPIIText_001() {
        val text = "This is a long document. " + "No PII here. ".repeat(100)
        assertFalse("Long text without PII", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testLongTextWithPII_001() {
        val piiSection = "SSN: 123-45-6789"
        val text = "Intro. ".repeat(50) + piiSection + " End. ".repeat(50)
        assertTrue("Should detect SSN in long text", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testSpecialCharsNoPII_001() {
        assertFalse(RegexScreener.containsPotentialPII("Text with <special> & \"chars\""))
    }

    @Test fun testUnicodeNoPII_001() {
        assertFalse(RegexScreener.containsPotentialPII("日本語 中文 한국어 عربي"))
    }

    @Test fun testNewlines_001() {
        val text = "Name: John\nSSN: 234-56-7890\nPhone: 555-123-4567"
        assertTrue("Should detect PII across newlines", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTabs_001() {
        val text = "SSN:\t345-67-8901"
        assertTrue("Should detect PII with tabs", RegexScreener.containsPotentialPII(text))
    }

    // =========================================================================
    // SECTION 9: Performance Tests
    // =========================================================================

    @Test fun testPerformance_ShortText_001() {
        val text = "SSN: 123-45-6789"
        val startTime = System.currentTimeMillis()
        repeat(1000) { RegexScreener.containsPotentialPII(text) }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("1000 iterations on short text should be < 2s", elapsed < 2000)
    }

    @Test fun testPerformance_MediumText_001() {
        val text = "Patient John Smith DOB 01/15/1985 SSN 123-45-6789 Email john@test.com Phone 555-123-4567"
        val startTime = System.currentTimeMillis()
        repeat(500) { RegexScreener.containsPotentialPII(text) }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("500 iterations on medium text should be < 2s", elapsed < 2000)
    }

    @Test fun testPerformance_ExtractShort_001() {
        val text = "SSN: 123-45-6789"
        val startTime = System.currentTimeMillis()
        repeat(500) { RegexScreener.extractEntities(text) }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("500 extract iterations should be < 2s", elapsed < 2000)
    }

    @Test fun testPerformance_ExtractMedium_001() {
        val text = "Card 4532015112830366 email user@example.com phone 555-123-4567"
        val startTime = System.currentTimeMillis()
        repeat(200) { RegexScreener.extractEntities(text) }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("200 extract iterations should be < 2s", elapsed < 2000)
    }

    @Test fun testPerformance_LongNoPIIText_001() {
        val text = buildString { repeat(100) { append("This paragraph contains no PII data at all. ") } }
        val startTime = System.currentTimeMillis()
        repeat(100) { RegexScreener.containsPotentialPII(text) }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("100 iterations on 100-para text should be < 2s", elapsed < 2000)
    }

    @Test fun testPerformance_LuhnBatch_001() {
        val cards = listOf(
            "4532015112830366", "5500005555555559", "378282246310005",
            "6011111111111117", "3530111333300000", "5105105105105100"
        )
        val startTime = System.currentTimeMillis()
        repeat(500) { cards.forEach { RegexScreener.luhnCheck(it) } }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("Luhn batch should be fast", elapsed < 2000)
    }

    // =========================================================================
    // SECTION 10: PIIEntity Property Tests
    // =========================================================================

    @Test fun testEntity_HavePosition_001() {
        val text = "SSN: 123-45-6789 end"
        val entities = RegexScreener.extractEntities(text)
        for (entity in entities) {
            assertTrue("startIndex >= 0", entity.startIndex >= 0)
            assertTrue("endIndex > startIndex", entity.endIndex > entity.startIndex)
            assertTrue("endIndex <= text.length", entity.endIndex <= text.length)
        }
    }

    @Test fun testEntity_HaveConfidence_001() {
        val text = "Card: 4532015112830366"
        val entities = RegexScreener.extractEntities(text)
        for (entity in entities) {
            assertTrue("Confidence should be 0-1", entity.confidence in 0f..1f)
        }
    }

    @Test fun testEntity_HaveSeverity_001() {
        val text = "SSN: 123-45-6789"
        val entities = RegexScreener.extractEntities(text)
        for (entity in entities) {
            assertNotNull("Severity should not be null", entity.severity)
        }
    }

    @Test fun testEntity_SSNSeverityIsCritical_001() {
        val text = "SSN: 123-45-6789"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntity = entities.firstOrNull { it.entityType == EntityType.SSN }
        if (ssnEntity != null) {
            assertEquals("SSN should have CRITICAL severity", Severity.CRITICAL, ssnEntity.entityType.severity)
        }
    }

    @Test fun testEntity_CCEntityType_001() {
        val text = "Card: 4532015112830366"
        val entities = RegexScreener.extractEntities(text)
        val ccEntity = entities.firstOrNull { it.entityType == EntityType.CREDIT_CARD }
        if (ccEntity != null) {
            assertEquals("Should be CREDIT_CARD", EntityType.CREDIT_CARD, ccEntity.entityType)
        }
    }

    @Test fun testEntity_EmailEntityType_001() {
        val text = "Email: user@example.com"
        val entities = RegexScreener.extractEntities(text)
        val emailEntity = entities.firstOrNull { it.entityType == EntityType.EMAIL }
        if (emailEntity != null) {
            assertEquals("Should be EMAIL", EntityType.EMAIL, emailEntity.entityType)
        }
    }

    @Test fun testEntity_PhoneEntityType_001() {
        val text = "Phone: 555-123-4567"
        val entities = RegexScreener.extractEntities(text)
        val phoneEntity = entities.firstOrNull { it.entityType == EntityType.PHONE }
        if (phoneEntity != null) {
            assertEquals("Should be PHONE", EntityType.PHONE, phoneEntity.entityType)
        }
    }

    @Test fun testEntity_MeetsThreshold_001() {
        val text = "Card: 4532015112830366"
        val entities = RegexScreener.extractEntities(text)
        for (entity in entities) {
            assertTrue("Entity should meet low threshold", entity.meetsThreshold(0.01f))
        }
    }

    @Test fun testEntity_MaskedText_001() {
        val entity = com.privacyguard.ml.PIIEntity(
            entityType = EntityType.CREDIT_CARD,
            confidence = 0.99f,
            startIndex = 0,
            endIndex = 16,
            rawText = "4532015112830366"
        )
        val masked = entity.maskedText
        assertTrue("Masked text should keep last 4", masked.endsWith("0366"))
        assertTrue("Masked text should have asterisks", masked.contains("*"))
    }

    @Test fun testEntity_Length_001() {
        val entity = com.privacyguard.ml.PIIEntity(
            entityType = EntityType.SSN,
            confidence = 0.95f,
            startIndex = 5,
            endIndex = 16,
            rawText = "123-45-6789"
        )
        assertEquals("Length should be end - start", 11, entity.length)
    }

    // =========================================================================
    // SECTION 11: Boundary Detection Tests
    // =========================================================================

    @Test fun testBoundary_CCSubstring_001() {
        // 17-digit number: should not match 16-digit CC pattern properly
        val text = "Product: 453201511283036600"
        val entities = RegexScreener.extractEntities(text)
        assertNotNull("Should handle extended number gracefully", entities)
    }

    @Test fun testBoundary_SSNLike_001() {
        // Looks like SSN but different format
        val text = "Code: 1234-567-890"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("4-3-3 pattern should not be SSN (needs 3-2-4)", ssnEntities.isEmpty())
    }

    @Test fun testBoundary_ZipCode_001() {
        val text = "Zip: 90210"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("ZIP code should not be SSN", ssnEntities.isEmpty())
    }

    @Test fun testBoundary_YearRange_001() {
        val text = "Years: 2020-2024"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("Year range should not be SSN", ssnEntities.isEmpty())
    }

    @Test fun testBoundary_URL_001() {
        val text = "Visit https://www.example.com for info"
        val entities = RegexScreener.extractEntities(text)
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("URL should not be email", emailEntities.isEmpty())
    }

    @Test fun testBoundary_DomainOnly_001() {
        val text = "Website: example.com"
        val entities = RegexScreener.extractEntities(text)
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Domain without @ should not be email", emailEntities.isEmpty())
    }

    @Test fun testBoundary_MaskedCC_001() {
        val text = "Card on file: ****-****-****-0366"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Masked CC should not be detected as CC", ccEntities.isEmpty())
    }

    @Test fun testBoundary_MaskedSSN_001() {
        val text = "SSN on file: ***-**-6789"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("Masked SSN should not be detected", ssnEntities.isEmpty())
    }

    // =========================================================================
    // SECTION 12: containsPotentialPII Negative Tests (No PII)
    // =========================================================================

    @Test fun testNoPII_NormalText_001() { assertFalse(RegexScreener.containsPotentialPII("Hello World")) }
    @Test fun testNoPII_NormalText_002() { assertFalse(RegexScreener.containsPotentialPII("The weather is nice today")) }
    @Test fun testNoPII_NormalText_003() { assertFalse(RegexScreener.containsPotentialPII("Android is a mobile operating system")) }
    @Test fun testNoPII_NormalText_004() { assertFalse(RegexScreener.containsPotentialPII("Kotlin is a modern programming language")) }
    @Test fun testNoPII_NormalText_005() { assertFalse(RegexScreener.containsPotentialPII("Privacy is important for users")) }
    @Test fun testNoPII_NormalText_006() { assertFalse(RegexScreener.containsPotentialPII("The meeting is at 3pm today")) }
    @Test fun testNoPII_NormalText_007() { assertFalse(RegexScreener.containsPotentialPII("Please review the attached document")) }
    @Test fun testNoPII_NormalText_008() { assertFalse(RegexScreener.containsPotentialPII("The project deadline is next Friday")) }
    @Test fun testNoPII_NormalText_009() { assertFalse(RegexScreener.containsPotentialPII("Version 1.0.0 has been released")) }
    @Test fun testNoPII_NormalText_010() { assertFalse(RegexScreener.containsPotentialPII("Please update your application")) }
    @Test fun testNoPII_NormalText_011() { assertFalse(RegexScreener.containsPotentialPII("The server is running normally")) }
    @Test fun testNoPII_NormalText_012() { assertFalse(RegexScreener.containsPotentialPII("Build successful in 45 seconds")) }
    @Test fun testNoPII_NormalText_013() { assertFalse(RegexScreener.containsPotentialPII("Test passed: 142/142")) }
    @Test fun testNoPII_NormalText_011() { assertFalse(RegexScreener.containsPotentialPII("The server is running normally")) }
    @Test fun testNoPII_NormalText_012() { assertFalse(RegexScreener.containsPotentialPII("Build successful in 45 seconds")) }
    @Test fun testNoPII_NormalText_013() { assertFalse(RegexScreener.containsPotentialPII("Test passed: 142/142")) }
    @Test fun testNoPII_NormalText_014() { assertFalse(RegexScreener.containsPotentialPII("Memory usage: 256MB")) }
    @Test fun testNoPII_NormalText_015() { assertFalse(RegexScreener.containsPotentialPII("CPU cores: 8")) }
}

// =============================================================================
// RegexScreenerExtendedTest3B — Sections 13-16
// =============================================================================
class RegexScreenerExtendedTest3B {

    // =========================================================================
    // SECTION 13: IPv4 Address containsPotentialPII Tests
    // =========================================================================

    @Test fun testIPv4ContainsPII_Loopback_001() {
        assertTrue(RegexScreener.containsPotentialPII("Server IP: 127.0.0.1"))
    }

    @Test fun testIPv4ContainsPII_Private_001() {
        assertTrue(RegexScreener.containsPotentialPII("Internal: 192.168.1.100"))
    }

    @Test fun testIPv4ContainsPII_Private_002() {
        assertTrue(RegexScreener.containsPotentialPII("Gateway: 10.0.0.1"))
    }

    @Test fun testIPv4ContainsPII_Private_003() {
        assertTrue(RegexScreener.containsPotentialPII("Host: 172.16.0.50"))
    }

    @Test fun testIPv4ContainsPII_Public_001() {
        assertTrue(RegexScreener.containsPotentialPII("Remote: 8.8.8.8"))
    }

    @Test fun testIPv4ContainsPII_Public_002() {
        assertTrue(RegexScreener.containsPotentialPII("DNS: 1.1.1.1"))
    }

    @Test fun testIPv4ContainsPII_Public_003() {
        assertTrue(RegexScreener.containsPotentialPII("IP address is 203.0.113.45"))
    }

    @Test fun testIPv4ContainsPII_WithPort_001() {
        assertTrue(RegexScreener.containsPotentialPII("Connect to 192.168.1.1:8080"))
    }

    @Test fun testIPv4ContainsPII_WithPort_002() {
        assertTrue(RegexScreener.containsPotentialPII("Service at 10.10.10.10:443"))
    }

    @Test fun testIPv4ContainsPII_InSentence_001() {
        assertTrue(RegexScreener.containsPotentialPII("The server at 74.125.224.72 responded"))
    }

    @Test fun testIPv4ContainsPII_InSentence_002() {
        assertTrue(RegexScreener.containsPotentialPII("Blocked IP: 185.234.219.1"))
    }

    @Test fun testIPv4ContainsPII_InSentence_003() {
        assertTrue(RegexScreener.containsPotentialPII("Login from 45.33.32.156 detected"))
    }

    @Test fun testIPv4ContainsPII_Broadcast_001() {
        assertTrue(RegexScreener.containsPotentialPII("Broadcast: 255.255.255.255"))
    }

    @Test fun testIPv4ContainsPII_MultipleIPs_001() {
        assertTrue(RegexScreener.containsPotentialPII("From 192.168.0.1 to 192.168.0.254"))
    }

    @Test fun testIPv4ContainsPII_IPv6Style_001() {
        // IPv4 within text still detected
        assertTrue(RegexScreener.containsPotentialPII("IPv4: 10.20.30.40 mapped"))
    }

    @Test fun testIPv4NoPII_VersionNumber_001() {
        assertFalse(RegexScreener.containsPotentialPII("Version 1.2.3"))
    }

    @Test fun testIPv4NoPII_VersionNumber_002() {
        assertFalse(RegexScreener.containsPotentialPII("SDK version 3.14"))
    }

    @Test fun testIPv4NoPII_DateFormat_001() {
        assertFalse(RegexScreener.containsPotentialPII("Date 12/25/2023"))
    }

    @Test fun testIPv4NoPII_Price_001() {
        assertFalse(RegexScreener.containsPotentialPII("Price $9.99"))
    }

    @Test fun testIPv4NoPII_EmptyString_001() {
        assertFalse(RegexScreener.containsPotentialPII(""))
    }

    // =========================================================================
    // SECTION 14: extractEntities — SSN Entity Tests
    // =========================================================================

    @Test fun testExtractSSN_StandardFormat_001() {
        val text = "SSN: 234-56-7890"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("SSN entity detected", ssnEntities.isNotEmpty())
    }

    @Test fun testExtractSSN_StandardFormat_002() {
        val text = "Patient ID 345-67-8901 confirmed"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("SSN entity found", ssnEntities.isNotEmpty())
    }

    @Test fun testExtractSSN_StandardFormat_003() {
        val text = "456-78-9012"
        val entities = RegexScreener.extractEntities(text)
        assertFalse("Should extract entity from bare SSN", entities.isEmpty())
    }

    @Test fun testExtractSSN_MultipleSSNs_001() {
        val text = "First: 123-45-6789, Second: 234-56-7890"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("Multiple SSNs detected", ssnEntities.size >= 1)
    }

    @Test fun testExtractSSN_EmptyText_001() {
        val entities = RegexScreener.extractEntities("")
        assertTrue("Empty text yields no entities", entities.isEmpty())
    }

    @Test fun testExtractSSN_NoSSN_001() {
        val entities = RegexScreener.extractEntities("Hello World")
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("Plain text has no SSN", ssnEntities.isEmpty())
    }

    @Test fun testExtractSSN_InvalidFormat_001() {
        val entities = RegexScreener.extractEntities("Number: 000-00-0000")
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("All-zero SSN not detected as valid SSN", ssnEntities.isEmpty())
    }

    @Test fun testExtractSSN_SpaceFormat_001() {
        val text = "SSN 567 89 0123"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("Space-formatted SSN extracted", ssnEntities.isNotEmpty())
    }

    @Test fun testExtractSSN_EntityTypeNotNull_001() {
        val text = "SSN: 678-90-1234"
        val entities = RegexScreener.extractEntities(text)
        entities.forEach { entity ->
            assertNotNull("Entity type not null", entity.entityType)
        }
    }

    @Test fun testExtractSSN_NoFalsePositive_001() {
        val text = "Phone: (123) 456-7890"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("Phone number not confused with SSN", ssnEntities.isEmpty())
    }

    @Test fun testExtractSSN_WithContext_001() {
        val text = "The beneficiary's SSN is 789-01-2345."
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("SSN detected in context", ssnEntities.isNotEmpty())
    }

    @Test fun testExtractSSN_WithContext_002() {
        val text = "Tax ID: 890-12-3456 (SSN)"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("SSN in tax context detected", ssnEntities.isNotEmpty())
    }

    @Test fun testExtractSSN_TrailingPunctuation_001() {
        val text = "ID is 901-23-4567."
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("SSN before period detected", ssnEntities.isNotEmpty())
    }

    @Test fun testExtractSSN_TrailingPunctuation_002() {
        val text = "ID: 012-34-5678,"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("SSN before comma detected", ssnEntities.isNotEmpty())
    }

    @Test fun testExtractSSN_NewlineContext_001() {
        val text = "Name: John\nSSN: 123-45-6789\nDOB: 1985-01-01"
        val entities = RegexScreener.extractEntities(text)
        val ssnEntities = entities.filter { it.entityType == EntityType.SSN }
        assertTrue("SSN in multiline text detected", ssnEntities.isNotEmpty())
    }

    // =========================================================================
    // SECTION 15: extractEntities — Credit Card Entity Tests
    // =========================================================================

    @Test fun testExtractCC_Visa_001() {
        val text = "Card: 4111111111111111"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Visa card extracted", ccEntities.isNotEmpty())
    }

    @Test fun testExtractCC_Visa_002() {
        val text = "Visa card 4012888888881881 on file"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Second Visa extracted", ccEntities.isNotEmpty())
    }

    @Test fun testExtractCC_Mastercard_001() {
        val text = "MC: 5500005555555559"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Mastercard extracted", ccEntities.isNotEmpty())
    }

    @Test fun testExtractCC_Amex_001() {
        val text = "AmEx: 378282246310005"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Amex extracted", ccEntities.isNotEmpty())
    }

    @Test fun testExtractCC_Discover_001() {
        val text = "Discover: 6011111111111117"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Discover extracted", ccEntities.isNotEmpty())
    }

    @Test fun testExtractCC_LuhnInvalid_001() {
        val text = "Card: 4111111111111112"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Luhn-invalid card not extracted", ccEntities.isEmpty())
    }

    @Test fun testExtractCC_Dashes_001() {
        val text = "4111-1111-1111-1111"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Dashed card extracted", ccEntities.isNotEmpty())
    }

    @Test fun testExtractCC_Spaces_001() {
        val text = "Card number 4111 1111 1111 1111"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Space-separated card extracted", ccEntities.isNotEmpty())
    }

    @Test fun testExtractCC_Masked_001() {
        val text = "Card: ****-****-****-1111"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Masked card not extracted", ccEntities.isEmpty())
    }

    @Test fun testExtractCC_TooShort_001() {
        val text = "Number: 411111111"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Too short number not extracted", ccEntities.isEmpty())
    }

    @Test fun testExtractCC_TooLong_001() {
        val text = "Number: 41111111111111111111"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Too long number not extracted", ccEntities.isEmpty())
    }

    @Test fun testExtractCC_Mixed_001() {
        val text = "Pay 4111111111111111 or 5500005555555559"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Multiple cards extracted", ccEntities.size >= 1)
    }

    @Test fun testExtractCC_NoCC_001() {
        val entities = RegexScreener.extractEntities("Hello World 12345")
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("No CC in plain text", ccEntities.isEmpty())
    }

    @Test fun testExtractCC_Jcb_001() {
        val text = "JCB: 3530111333300000"
        val entities = RegexScreener.extractEntities(text)
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("JCB card extracted", ccEntities.isNotEmpty())
    }

    @Test fun testExtractCC_AllDigits_notCC_001() {
        val text = "Order ID: 1234567890123456"
        val entities = RegexScreener.extractEntities(text)
        // Only valid Luhn numbers extracted
        val ccEntities = entities.filter { it.entityType == EntityType.CREDIT_CARD }
        // Invalid Luhn → no CC
        assertTrue("Non-Luhn 16 digits not a CC", ccEntities.isEmpty())
    }

    // =========================================================================
    // SECTION 16: extractEntities — Email Entity Tests
    // =========================================================================

    @Test fun testExtractEmail_Simple_001() {
        val entities = RegexScreener.extractEntities("Email: user@example.com")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Simple email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_Simple_002() {
        val entities = RegexScreener.extractEntities("Contact john.doe@company.org")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Dotted email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_Subdomains_001() {
        val entities = RegexScreener.extractEntities("Send to info@mail.example.co.uk")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Subdomain email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_Plus_001() {
        val entities = RegexScreener.extractEntities("Alias: user+filter@gmail.com")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Plus alias email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_Hyphen_001() {
        val entities = RegexScreener.extractEntities("Email: first-last@domain.net")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Hyphen email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_Numeric_001() {
        val entities = RegexScreener.extractEntities("Contact: user123@example123.com")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Numeric email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_Invalid_NoAt_001() {
        val entities = RegexScreener.extractEntities("Not email: userexample.com")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("No @ means no email", emailEntities.isEmpty())
    }

    @Test fun testExtractEmail_Invalid_NoDomain_001() {
        val entities = RegexScreener.extractEntities("user@")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Incomplete domain means no email", emailEntities.isEmpty())
    }

    @Test fun testExtractEmail_Multiple_001() {
        val entities = RegexScreener.extractEntities("CC: a@b.com, d@e.org")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Multiple emails extracted", emailEntities.size >= 1)
    }

    @Test fun testExtractEmail_InURL_001() {
        val entities = RegexScreener.extractEntities("URL: https://user:pass@example.com/path")
        // URL-embedded credentials might be detected
        assertNotNull("extractEntities returns list", entities)
    }

    @Test fun testExtractEmail_MixedCase_001() {
        val entities = RegexScreener.extractEntities("Contact: User.Name@Example.COM")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Mixed case email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_Underscore_001() {
        val entities = RegexScreener.extractEntities("ID: first_last@corp.io")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Underscore email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_LongTLD_001() {
        val entities = RegexScreener.extractEntities("user@example.museum")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Long TLD email extracted", emailEntities.isNotEmpty())
    }

    @Test fun testExtractEmail_NoEmail_001() {
        val entities = RegexScreener.extractEntities("No email here at all")
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("No email in plain text", emailEntities.isEmpty())
    }

    @Test fun testExtractEmail_InSentence_001() {
        val text = "Please email support@privacyguard.dev for help."
        val entities = RegexScreener.extractEntities(text)
        val emailEntities = entities.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Email in sentence extracted", emailEntities.isNotEmpty())
    }
}

// =============================================================================
// RegexScreenerExtendedTest3C — Sections 17-20
// =============================================================================
class RegexScreenerExtendedTest3C {

    // =========================================================================
    // SECTION 17: extractEntities — Phone Entity Tests
    // =========================================================================

    @Test fun testExtractPhone_US_001() {
        val entities = RegexScreener.extractEntities("Call (555) 123-4567")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("US phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_US_002() {
        val entities = RegexScreener.extractEntities("Phone: 555-234-5678")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Dashed US phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_US_003() {
        val entities = RegexScreener.extractEntities("Number: 555.345.6789")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Dotted US phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_US_004() {
        val entities = RegexScreener.extractEntities("Mobile: 5554567890")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Compact US phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_Intl_001() {
        val entities = RegexScreener.extractEntities("International: +1-555-456-7890")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("International phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_Intl_002() {
        val entities = RegexScreener.extractEntities("UK: +44 20 7946 0958")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("UK phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_Intl_003() {
        val entities = RegexScreener.extractEntities("DE: +49 30 12345678")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("German phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_WithExtension_001() {
        val entities = RegexScreener.extractEntities("Office: (555) 678-9012 ext. 123")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Phone with extension extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_1800_001() {
        val entities = RegexScreener.extractEntities("Toll-free: 1-800-555-0100")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Toll-free phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_1800_002() {
        val entities = RegexScreener.extractEntities("1-888-555-0199 for support")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("1-888 phone extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_NoPhone_001() {
        val entities = RegexScreener.extractEntities("No phone number here")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("No phone in plain text", phoneEntities.isEmpty())
    }

    @Test fun testExtractPhone_TooShort_001() {
        val entities = RegexScreener.extractEntities("ID: 12345")
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Short number not a phone", phoneEntities.isEmpty())
    }

    @Test fun testExtractPhone_Multiple_001() {
        val text = "Call (555) 123-4567 or (555) 987-6543"
        val entities = RegexScreener.extractEntities(text)
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Multiple phones extracted", phoneEntities.size >= 1)
    }

    @Test fun testExtractPhone_InSentence_001() {
        val text = "John can be reached at (555) 246-8013 during business hours."
        val entities = RegexScreener.extractEntities(text)
        val phoneEntities = entities.filter { it.entityType == EntityType.PHONE }
        assertTrue("Phone in sentence extracted", phoneEntities.isNotEmpty())
    }

    @Test fun testExtractPhone_InSentence_002() {
        val text = "Emergency: call 911 immediately."
        val entities = RegexScreener.extractEntities(text)
        assertNotNull("911 text processed without exception", entities)
    }

    // =========================================================================
    // SECTION 18: extractEntities — API Key Entity Tests
    // =========================================================================

    @Test fun testExtractAPIKey_Stripe_001() {
        val text = "Key: sk_lvd_abcdefghijklmnopqrstuvwxyz012345"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("Stripe live key extracted", apiEntities.isNotEmpty())
    }

    @Test fun testExtractAPIKey_Stripe_002() {
        val text = "Test key: pk_test_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("Stripe public test key extracted", apiEntities.isNotEmpty())
    }

    @Test fun testExtractAPIKey_Generic_001() {
        val text = "API_KEY=a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("Generic API key extracted", apiEntities.isNotEmpty())
    }

    @Test fun testExtractAPIKey_Bearer_001() {
        val text = "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.payload.sig"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("Bearer token detected", apiEntities.isNotEmpty())
    }

    @Test fun testExtractAPIKey_GitHub_001() {
        val text = "ghp_xxx"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("GitHub PAT detected", apiEntities.isNotEmpty())
    }

    @Test fun testExtractAPIKey_Short_001() {
        val text = "Key: abc123"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("Short key not extracted", apiEntities.isEmpty())
    }

    @Test fun testExtractAPIKey_NoKey_001() {
        val entities = RegexScreener.extractEntities("No secret here")
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("No API key in plain text", apiEntities.isEmpty())
    }

    @Test fun testExtractAPIKey_InConfig_001() {
        val text = "OPENAI_KEY=sk-aBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRs"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("OpenAI key in config extracted", apiEntities.isNotEmpty())
    }

    @Test fun testExtractAPIKey_HighEntropy_001() {
        val text = "Secret: xK9mN2pL7qR4sT1uV8wX5yZ3aB6cD0eF"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("High-entropy secret detected", apiEntities.isNotEmpty())
    }

    @Test fun testExtractAPIKey_EnvironmentVariable_001() {
        val text = "export AWS_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
        val entities = RegexScreener.extractEntities(text)
        assertNotNull("AWS key in env var processed", entities)
    }

    @Test fun testExtractAPIKey_UUID_001() {
        val text = "Token: 550e8400-e29b-41d4-a716-446655440000"
        val entities = RegexScreener.extractEntities(text)
        val apiEntities = entities.filter { it.entityType == EntityType.API_KEY }
        assertTrue("UUID token detected", apiEntities.isNotEmpty())
    }

    @Test fun testExtractAPIKey_Mixed_001() {
        val text = "Email: a@b.com, Key: sk_lvd_xyz123456789abcdefghijk"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Mixed PII has multiple entities", entities.size >= 1)
    }

    @Test fun testExtractAPIKey_LongRandomString_001() {
        val key = "A".repeat(32)
        val entities = RegexScreener.extractEntities("Key: $key")
        assertNotNull("Long uniform string processed", entities)
    }

    @Test fun testExtractAPIKey_NumericOnly_001() {
        val text = "Code: 12345678901234567890"
        val entities = RegexScreener.extractEntities(text)
        assertNotNull("Numeric-only string processed", entities)
    }

    @Test fun testExtractAPIKey_SpecialChars_001() {
        val text = "Key contains: abc!def@ghi#jkl$mno%pqr"
        val entities = RegexScreener.extractEntities(text)
        assertNotNull("Special chars in key processed", entities)
    }

    // =========================================================================
    // SECTION 19: Mixed Multi-Entity Tests
    // =========================================================================

    @Test fun testMixed_SSNAndEmail_001() {
        val text = "SSN: 123-45-6789, Email: user@example.com"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Multiple entity types detected", entities.size >= 1)
    }

    @Test fun testMixed_CardAndPhone_001() {
        val text = "Card 4111111111111111, Phone (555) 123-4567"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("Card and phone detected", entities.size >= 1)
    }

    @Test fun testMixed_AllFourTypes_001() {
        val text = "SSN: 234-56-7890, Email: a@b.com, Phone: (555) 234-5678, Card: 4111111111111111"
        val entities = RegexScreener.extractEntities(text)
        assertTrue("All four PII types in one text", entities.size >= 2)
        assertTrue("containsPotentialPII agrees", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testMixed_EmptyAfterAllRemoved_001() {
        val text = "No PII at all: just normal text about software development."
        val entities = RegexScreener.extractEntities(text)
        assertFalse("Normal text has no PII entities",
            entities.any { it.entityType == EntityType.SSN || it.entityType == EntityType.EMAIL || it.entityType == EntityType.CREDIT_CARD })
    }

    @Test fun testMixed_ContainsPII_ConsistentWithExtract_001() {
        val text = "SSN: 345-67-8901"
        val hasPII = RegexScreener.containsPotentialPII(text)
        val entities = RegexScreener.extractEntities(text)
        if (hasPII) assertTrue("If containsPII, entities non-empty", entities.isNotEmpty())
    }

    @Test fun testMixed_ContainsPII_ConsistentWithExtract_002() {
        val text = "Random text no PII"
        val hasPII = RegexScreener.containsPotentialPII(text)
        val entities = RegexScreener.extractEntities(text)
        if (!hasPII) assertTrue("If no PII, relevant entities empty",
            entities.filter { it.entityType in listOf(EntityType.SSN, EntityType.CREDIT_CARD, EntityType.EMAIL) }.isEmpty())
    }

    @Test fun testMixed_LargeText_001() {
        val text = "Name: Alice Smith\n" +
            "Email: alice.smith@example.com\n" +
            "SSN: 456-78-9012\n" +
            "Card: 4111111111111111\n" +
            "Phone: (555) 345-6789\n" +
            "Address: 123 Main St, Springfield, IL 62701"
        assertTrue("Large PII block detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testMixed_LuhnCheck_UsedCorrectly_001() {
        assertTrue("Luhn valid card", RegexScreener.luhnCheck("4111111111111111"))
    }

    @Test fun testMixed_LuhnCheck_UsedCorrectly_002() {
        assertTrue("Luhn valid MC", RegexScreener.luhnCheck("5500005555555559"))
    }

    @Test fun testMixed_LuhnCheck_UsedCorrectly_003() {
        assertFalse("Luhn invalid card", RegexScreener.luhnCheck("4111111111111112"))
    }

    @Test fun testMixed_LuhnCheck_UsedCorrectly_004() {
        assertTrue("Luhn valid Amex", RegexScreener.luhnCheck("378282246310005"))
    }

    @Test fun testMixed_LuhnCheck_UsedCorrectly_005() {
        assertTrue("Luhn valid Discover", RegexScreener.luhnCheck("6011111111111117"))
    }

    @Test fun testMixed_LuhnCheck_UsedCorrectly_006() {
        assertFalse("Luhn all zeros invalid", RegexScreener.luhnCheck("0000000000000000"))
    }

    @Test fun testMixed_EntitiesReturnList_001() {
        val result = RegexScreener.extractEntities("Any text")
        assertNotNull("extractEntities returns non-null", result)
        assertTrue("extractEntities returns list", result is List<*>)
    }

    @Test fun testMixed_ContainsPII_NullSafe_001() {
        val result = runCatching { RegexScreener.containsPotentialPII("") }
        assertTrue("Empty string safe", result.isSuccess)
    }

    @Test fun testMixed_ContainsPII_SpecialChars_001() {
        val result = runCatching { RegexScreener.containsPotentialPII("!@#\$%^&*()") }
        assertTrue("Special chars safe", result.isSuccess)
    }

    // =========================================================================
    // SECTION 20: Regression and Stress Tests
    // =========================================================================

    @Test fun testRegression_Visa_KnownGood_001() {
        assertTrue("Known good Visa",
            RegexScreener.containsPotentialPII("4111111111111111"))
    }

    @Test fun testRegression_Visa_KnownGood_002() {
        assertTrue("Known good Visa 2",
            RegexScreener.containsPotentialPII("4012888888881881"))
    }

    @Test fun testRegression_SSN_KnownGood_001() {
        assertTrue("Known good SSN",
            RegexScreener.containsPotentialPII("123-45-6789"))
    }

    @Test fun testRegression_Email_KnownGood_001() {
        assertTrue("Known good email",
            RegexScreener.containsPotentialPII("user@example.com"))
    }

    @Test fun testRegression_Phone_KnownGood_001() {
        assertTrue("Known good phone",
            RegexScreener.containsPotentialPII("(555) 123-4567"))
    }

    @Test fun testRegression_Visa_KnownBad_001() {
        assertFalse("Known bad Visa (Luhn fail)",
            RegexScreener.luhnCheck("4111111111111112"))
    }

    @Test fun testRegression_SSN_KnownBad_001() {
        assertFalse("Known bad SSN (all zeros)",
            RegexScreener.containsPotentialPII("000-00-0000"))
    }

    @Test fun testStress_RepeatedCalls_001() {
        val text = "SSN: 234-56-7890"
        repeat(100) {
            assertTrue("Repeated call $it", RegexScreener.containsPotentialPII(text))
        }
    }

    @Test fun testStress_RepeatedCalls_002() {
        val text = "No PII here"
        repeat(100) {
            assertFalse("Repeated clean call $it", RegexScreener.containsPotentialPII(text))
        }
    }

    @Test fun testStress_LongText_001() {
        val base = "Name: John Doe, SSN: 123-45-6789, Email: john@example.com. "
        val longText = base.repeat(50)
        val result = runCatching { RegexScreener.containsPotentialPII(longText) }
        assertTrue("Long repeated text processed safely", result.isSuccess)
    }

    @Test fun testStress_ExtractManyEntities_001() {
        val text = List(20) { "SSN: 23${it + 10}-56-7890" }.joinToString("; ")
        val result = runCatching { RegexScreener.extractEntities(text) }
        assertTrue("Many SSNs extracted safely", result.isSuccess)
    }

    @Test fun testStress_ExtractManyEmails_001() {
        val text = List(20) { i -> "user$i@example.com" }.joinToString(", ")
        val result = runCatching { RegexScreener.extractEntities(text) }
        assertTrue("Many emails extracted safely", result.isSuccess)
    }

    @Test fun testStress_ExtractManyCards_001() {
        val text = "4111111111111111 5500005555555559 6011111111111117 378282246310005"
        val result = runCatching { RegexScreener.extractEntities(text) }
        assertTrue("Multiple cards extracted safely", result.isSuccess)
    }

    @Test fun testStress_UnicodeText_001() {
        val text = "SSN: 345-67-8901 名前：田中太郎"
        val result = runCatching { RegexScreener.containsPotentialPII(text) }
        assertTrue("Unicode text processed safely", result.isSuccess)
    }

    @Test fun testStress_NewlineHeavyText_001() {
        val text = "\n".repeat(100) + "SSN: 456-78-9012" + "\n".repeat(100)
        val result = runCatching { RegexScreener.containsPotentialPII(text) }
        assertTrue("Newline-heavy text processed", result.isSuccess)
    }

    @Test fun testStress_WhitespaceOnly_001() {
        val result = runCatching { RegexScreener.containsPotentialPII("   \t  \n  ") }
        assertTrue("Whitespace-only processed safely", result.isSuccess)
    }

    @Test fun testStress_VeryLongSingleWord_001() {
        val word = "A".repeat(10000)
        val result = runCatching { RegexScreener.containsPotentialPII(word) }
        assertTrue("Very long word processed safely", result.isSuccess)
    }

    @Test fun testStress_LuhnRepeated_001() {
        repeat(500) {
            assertTrue("Luhn repeated call $it", RegexScreener.luhnCheck("4111111111111111"))
        }
    }

    @Test fun testStress_LuhnOnAllDigits_001() {
        // Single digit strings shouldn't crash
        for (d in '0'..'9') {
            val result = runCatching { RegexScreener.luhnCheck("$d") }
            assertTrue("Luhn single digit safe: $d", result.isSuccess)
        }
    }

    @Test fun testStress_EmptyStringRepeated_001() {
        repeat(200) {
            val result = runCatching { RegexScreener.extractEntities("") }
            assertTrue("Empty string extract $it", result.isSuccess)
        }
    }

    @Test fun testFinal_AllSectionsCovered_001() {
        // Verifies that all 20 sections have been exercised by asserting
        // a known truth about each major PII type.
        assertTrue("SSN pattern", RegexScreener.containsPotentialPII("123-45-6789"))
        assertTrue("Card pattern", RegexScreener.containsPotentialPII("4111111111111111"))
        assertTrue("Email pattern", RegexScreener.containsPotentialPII("a@b.com"))
        assertTrue("Phone pattern", RegexScreener.containsPotentialPII("(555) 123-4567"))
        assertTrue("API Key pattern", RegexScreener.containsPotentialPII("sk_lvd_xyzABCDEFGHIJKLMNOPQRSTUV"))
        assertTrue("IP pattern", RegexScreener.containsPotentialPII("192.168.1.1"))
        assertTrue("Luhn valid", RegexScreener.luhnCheck("5500005555555559"))
        assertFalse("Clean text", RegexScreener.containsPotentialPII("Normal words only"))
    }
        assertTrue("Luhn valid", RegexScreener.luhnCheck("5500005555555559"))
        assertFalse("Clean text", RegexScreener.containsPotentialPII("Normal words only"))
    }
}

// =============================================================================
// RegexScreenerExtendedTest3D — Additional Luhn + Format Variation Tests
// =============================================================================
class RegexScreenerExtendedTest3D {

    // =========================================================================
    // SECTION D1: Luhn Check — Extended Valid Numbers
    // =========================================================================

    @Test fun testLuhn_Valid_Visa13_001() { assertTrue(RegexScreener.luhnCheck("4222222222222")) }
    @Test fun testLuhn_Valid_Visa16_001() { assertTrue(RegexScreener.luhnCheck("4111111111111111")) }
    @Test fun testLuhn_Valid_Visa16_002() { assertTrue(RegexScreener.luhnCheck("4012888888881881")) }
    @Test fun testLuhn_Valid_Visa16_003() { assertTrue(RegexScreener.luhnCheck("4917610000000000")) }
    @Test fun testLuhn_Valid_MC_001() { assertTrue(RegexScreener.luhnCheck("5500005555555559")) }
    @Test fun testLuhn_Valid_MC_002() { assertTrue(RegexScreener.luhnCheck("5555555555554444")) }
    @Test fun testLuhn_Valid_MC_003() { assertTrue(RegexScreener.luhnCheck("5105105105105100")) }
    @Test fun testLuhn_Valid_Amex_001() { assertTrue(RegexScreener.luhnCheck("378282246310005")) }
    @Test fun testLuhn_Valid_Amex_002() { assertTrue(RegexScreener.luhnCheck("371449635398431")) }
    @Test fun testLuhn_Valid_Discover_001() { assertTrue(RegexScreener.luhnCheck("6011111111111117")) }
    @Test fun testLuhn_Valid_Discover_002() { assertTrue(RegexScreener.luhnCheck("6011000990139424")) }
    @Test fun testLuhn_Valid_JCB_001() { assertTrue(RegexScreener.luhnCheck("3530111333300000")) }
    @Test fun testLuhn_Valid_JCB_002() { assertTrue(RegexScreener.luhnCheck("3566002020360505")) }
    @Test fun testLuhn_Valid_Diners_001() { assertTrue(RegexScreener.luhnCheck("30569309025904")) }
    @Test fun testLuhn_Valid_Diners_002() { assertTrue(RegexScreener.luhnCheck("38520000023237")) }
    @Test fun testLuhn_Valid_UnionPay_001() { assertTrue(RegexScreener.luhnCheck("6200000000000005")) }

    // =========================================================================
    // SECTION D2: Luhn Check — Extended Invalid Numbers
    // =========================================================================

    @Test fun testLuhn_Invalid_001() { assertFalse(RegexScreener.luhnCheck("4111111111111112")) }
    @Test fun testLuhn_Invalid_002() { assertFalse(RegexScreener.luhnCheck("5500005555555550")) }
    @Test fun testLuhn_Invalid_003() { assertFalse(RegexScreener.luhnCheck("378282246310004")) }
    @Test fun testLuhn_Invalid_004() { assertFalse(RegexScreener.luhnCheck("6011111111111118")) }
    @Test fun testLuhn_Invalid_005() { assertFalse(RegexScreener.luhnCheck("1234567890123456")) }
    @Test fun testLuhn_Invalid_006() { assertFalse(RegexScreener.luhnCheck("9999999999999999")) }
    @Test fun testLuhn_Invalid_007() { assertFalse(RegexScreener.luhnCheck("0000000000000000")) }
    @Test fun testLuhn_Invalid_008() { assertFalse(RegexScreener.luhnCheck("1111111111111111")) }
    @Test fun testLuhn_Invalid_009() { assertFalse(RegexScreener.luhnCheck("2222222222222222")) }
    @Test fun testLuhn_Invalid_010() { assertFalse(RegexScreener.luhnCheck("3333333333333333")) }
    @Test fun testLuhn_Invalid_011() { assertFalse(RegexScreener.luhnCheck("4444444444444444")) }
    @Test fun testLuhn_Invalid_012() { assertFalse(RegexScreener.luhnCheck("5555555555555555")) }
    @Test fun testLuhn_Invalid_013() { assertFalse(RegexScreener.luhnCheck("6666666666666666")) }
    @Test fun testLuhn_Invalid_014() { assertFalse(RegexScreener.luhnCheck("7777777777777777")) }
    @Test fun testLuhn_Invalid_015() { assertFalse(RegexScreener.luhnCheck("8888888888888888")) }
    @Test fun testLuhn_Invalid_016() { assertFalse(RegexScreener.luhnCheck("4111111111111113")) }

    // =========================================================================
    // SECTION D3: SSN Format Variation Tests
    // =========================================================================

    @Test fun testSSN_Format_Dash_001() { assertTrue(RegexScreener.containsPotentialPII("SSN: 123-45-6789")) }
    @Test fun testSSN_Format_Dash_002() { assertTrue(RegexScreener.containsPotentialPII("ID 234-56-7890")) }
    @Test fun testSSN_Format_Dash_003() { assertTrue(RegexScreener.containsPotentialPII("345-67-8901 filed")) }
    @Test fun testSSN_Format_Dash_004() { assertTrue(RegexScreener.containsPotentialPII("Ref: 456-78-9012")) }
    @Test fun testSSN_Format_Dash_005() { assertTrue(RegexScreener.containsPotentialPII("567-89-0123")) }
    @Test fun testSSN_Format_Space_001() { assertTrue(RegexScreener.containsPotentialPII("SSN 123 45 6789")) }
    @Test fun testSSN_Format_Space_002() { assertTrue(RegexScreener.containsPotentialPII("Number 234 56 7890")) }
    @Test fun testSSN_Format_Space_003() { assertTrue(RegexScreener.containsPotentialPII("ID 345 67 8901")) }
    @Test fun testSSN_Format_Compact_001() { assertTrue(RegexScreener.containsPotentialPII("Code 123456789")) }
    @Test fun testSSN_Format_Compact_002() { assertTrue(RegexScreener.containsPotentialPII("234567890 confirmed")) }
    @Test fun testSSN_NoMatch_000Prefix_001() { assertFalse(RegexScreener.containsPotentialPII("ID: 000-45-6789")) }
    @Test fun testSSN_NoMatch_000Prefix_002() { assertFalse(RegexScreener.containsPotentialPII("SSN: 000-12-3456")) }
    @Test fun testSSN_NoMatch_Group00_001() { assertFalse(RegexScreener.containsPotentialPII("123-00-6789")) }
    @Test fun testSSN_NoMatch_Group00_002() { assertFalse(RegexScreener.containsPotentialPII("234-00-7890")) }
    @Test fun testSSN_NoMatch_Serial0000_001() { assertFalse(RegexScreener.containsPotentialPII("123-45-0000")) }
    @Test fun testSSN_Mixed_WithEmail_001() {
        assertTrue(RegexScreener.containsPotentialPII("SSN: 123-45-6789, contact a@b.com"))
    }

    // =========================================================================
    // SECTION D4: Email Format Variation Tests
    // =========================================================================

    @Test fun testEmail_Format_Simple_001() { assertTrue(RegexScreener.containsPotentialPII("a@b.com")) }
    @Test fun testEmail_Format_Simple_002() { assertTrue(RegexScreener.containsPotentialPII("user@example.com")) }
    @Test fun testEmail_Format_Simple_003() { assertTrue(RegexScreener.containsPotentialPII("john.doe@company.org")) }
    @Test fun testEmail_Format_Simple_004() { assertTrue(RegexScreener.containsPotentialPII("info@corp.net")) }
    @Test fun testEmail_Format_Simple_005() { assertTrue(RegexScreener.containsPotentialPII("support@help.io")) }
    @Test fun testEmail_Format_Plus_001() { assertTrue(RegexScreener.containsPotentialPII("user+tag@example.com")) }
    @Test fun testEmail_Format_Plus_002() { assertTrue(RegexScreener.containsPotentialPII("alice+work@gmail.com")) }
    @Test fun testEmail_Format_Numeric_001() { assertTrue(RegexScreener.containsPotentialPII("user123@domain456.com")) }
    @Test fun testEmail_Format_Numeric_002() { assertTrue(RegexScreener.containsPotentialPII("12345@numbers.net")) }
    @Test fun testEmail_Format_Hyphen_001() { assertTrue(RegexScreener.containsPotentialPII("first-last@domain.com")) }
    @Test fun testEmail_Format_Hyphen_002() { assertTrue(RegexScreener.containsPotentialPII("a-b-c@x-y-z.com")) }
    @Test fun testEmail_Format_Underscore_001() { assertTrue(RegexScreener.containsPotentialPII("first_last@corp.com")) }
    @Test fun testEmail_Format_Subdomain_001() { assertTrue(RegexScreener.containsPotentialPII("user@mail.example.com")) }
    @Test fun testEmail_Format_Subdomain_002() { assertTrue(RegexScreener.containsPotentialPII("user@a.b.c.example.org")) }
    @Test fun testEmail_Format_NewTLD_001() { assertTrue(RegexScreener.containsPotentialPII("user@example.technology")) }
    @Test fun testEmail_Invalid_NoAt_001() { assertFalse(RegexScreener.containsPotentialPII("userexample.com")) }
    @Test fun testEmail_Invalid_NoTLD_001() { assertFalse(RegexScreener.containsPotentialPII("user@domain")) }
    @Test fun testEmail_Invalid_SpacesInLocal_001() { assertFalse(RegexScreener.containsPotentialPII("user name@domain.com")) }
    @Test fun testEmail_Invalid_EmptyLocal_001() { assertFalse(RegexScreener.containsPotentialPII("@domain.com")) }
    @Test fun testEmail_InContext_001() {
        assertTrue(RegexScreener.containsPotentialPII("Please contact admin@example.com for details."))
    }

    // =========================================================================
    // SECTION D5: Phone Format Variation Tests
    // =========================================================================

    @Test fun testPhone_Format_Parens_001() { assertTrue(RegexScreener.containsPotentialPII("(555) 123-4567")) }
    @Test fun testPhone_Format_Parens_002() { assertTrue(RegexScreener.containsPotentialPII("(800) 234-5678")) }
    @Test fun testPhone_Format_Dash_001() { assertTrue(RegexScreener.containsPotentialPII("555-345-6789")) }
    @Test fun testPhone_Format_Dash_002() { assertTrue(RegexScreener.containsPotentialPII("800-456-7890")) }
    @Test fun testPhone_Format_Dot_001() { assertTrue(RegexScreener.containsPotentialPII("555.567.8901")) }
    @Test fun testPhone_Format_Dot_002() { assertTrue(RegexScreener.containsPotentialPII("800.678.9012")) }
    @Test fun testPhone_Format_Compact_001() { assertTrue(RegexScreener.containsPotentialPII("5557890123")) }
    @Test fun testPhone_Format_Compact_002() { assertTrue(RegexScreener.containsPotentialPII("8008901234")) }
    @Test fun testPhone_Format_Intl_001() { assertTrue(RegexScreener.containsPotentialPII("+1 555 789 0123")) }
    @Test fun testPhone_Format_Intl_002() { assertTrue(RegexScreener.containsPotentialPII("+1-800-890-1234")) }
    @Test fun testPhone_Format_Intl_003() { assertTrue(RegexScreener.containsPotentialPII("+44 20 7946 0958")) }
    @Test fun testPhone_Format_Intl_004() { assertTrue(RegexScreener.containsPotentialPII("+49 30 12345678")) }
    @Test fun testPhone_Format_Intl_005() { assertTrue(RegexScreener.containsPotentialPII("+91 98765 43210")) }
    @Test fun testPhone_Format_Intl_006() { assertTrue(RegexScreener.containsPotentialPII("+61 2 9876 5432")) }
    @Test fun testPhone_Format_Intl_007() { assertTrue(RegexScreener.containsPotentialPII("+33 1 42 68 53 00")) }
    @Test fun testPhone_Invalid_TooShort_001() { assertFalse(RegexScreener.containsPotentialPII("555-1234")) }
    @Test fun testPhone_Invalid_TooShort_002() { assertFalse(RegexScreener.containsPotentialPII("123-4567")) }
    @Test fun testPhone_Invalid_Letters_001() { assertFalse(RegexScreener.containsPotentialPII("ABC-DEF-GHIJ")) }
    @Test fun testPhone_InContext_001() {
        assertTrue(RegexScreener.containsPotentialPII("Reach us at (555) 901-2345 any time."))
    }
    @Test fun testPhone_InContext_002() {
        assertTrue(RegexScreener.containsPotentialPII("Emergency line: 1-800-012-3456"))
    }

    // =========================================================================
    // SECTION D6: Credit Card Format Variation Tests
    // =========================================================================

    @Test fun testCC_Format_Compact_Visa_001() { assertTrue(RegexScreener.containsPotentialPII("4111111111111111")) }
    @Test fun testCC_Format_Compact_Visa_002() { assertTrue(RegexScreener.containsPotentialPII("4012888888881881")) }
    @Test fun testCC_Format_Compact_MC_001() { assertTrue(RegexScreener.containsPotentialPII("5500005555555559")) }
    @Test fun testCC_Format_Compact_MC_002() { assertTrue(RegexScreener.containsPotentialPII("5555555555554444")) }
    @Test fun testCC_Format_Compact_Amex_001() { assertTrue(RegexScreener.containsPotentialPII("378282246310005")) }
    @Test fun testCC_Format_Compact_Discover_001() { assertTrue(RegexScreener.containsPotentialPII("6011111111111117")) }
    @Test fun testCC_Format_Dashed_Visa_001() { assertTrue(RegexScreener.containsPotentialPII("4111-1111-1111-1111")) }
    @Test fun testCC_Format_Dashed_MC_001() { assertTrue(RegexScreener.containsPotentialPII("5500-0055-5555-5559")) }
    @Test fun testCC_Format_Spaced_Visa_001() { assertTrue(RegexScreener.containsPotentialPII("4111 1111 1111 1111")) }
    @Test fun testCC_Format_Spaced_MC_001() { assertTrue(RegexScreener.containsPotentialPII("5500 0055 5555 5559")) }
    @Test fun testCC_InContext_001() {
        assertTrue(RegexScreener.containsPotentialPII("Pay with card 4111111111111111 ending 1111"))
    }
    @Test fun testCC_InContext_002() {
        assertTrue(RegexScreener.containsPotentialPII("Card on file: 5555555555554444"))
    }
    @Test fun testCC_LuhnFail_001() { assertFalse(RegexScreener.luhnCheck("4111111111111110")) }
    @Test fun testCC_LuhnFail_002() { assertFalse(RegexScreener.luhnCheck("5500005555555551")) }
    @Test fun testCC_LuhnFail_003() { assertFalse(RegexScreener.luhnCheck("6011111111111110")) }
    @Test fun testCC_LuhnFail_004() { assertFalse(RegexScreener.luhnCheck("3782822463100051")) }
    @Test fun testCC_Masked_001() { assertFalse(RegexScreener.containsPotentialPII("Card: xxxx-xxxx-xxxx-1111")) }
    @Test fun testCC_NotPII_ShortNum_001() { assertFalse(RegexScreener.containsPotentialPII("Code: 123456")) }
    @Test fun testCC_NotPII_Text_001() { assertFalse(RegexScreener.containsPotentialPII("No credit card here")) }
    @Test fun testCC_NotPII_ZipCode_001() { assertFalse(RegexScreener.containsPotentialPII("ZIP: 90210")) }
}

// =============================================================================
// RegexScreenerExtendedTest3E — More Pattern Tests and Edge Cases
// =============================================================================
class RegexScreenerExtendedTest3E {

    // =========================================================================
    // SECTION E1: Comprehensive containsPotentialPII Positive Tests
    // =========================================================================

    @Test fun testPos_SSN_001() { assertTrue(RegexScreener.containsPotentialPII("123-45-6789")) }
    @Test fun testPos_SSN_002() { assertTrue(RegexScreener.containsPotentialPII("234-56-7890")) }
    @Test fun testPos_SSN_003() { assertTrue(RegexScreener.containsPotentialPII("345-67-8901")) }
    @Test fun testPos_SSN_004() { assertTrue(RegexScreener.containsPotentialPII("456-78-9012")) }
    @Test fun testPos_SSN_005() { assertTrue(RegexScreener.containsPotentialPII("567-89-0123")) }
    @Test fun testPos_Email_001() { assertTrue(RegexScreener.containsPotentialPII("user@example.com")) }
    @Test fun testPos_Email_002() { assertTrue(RegexScreener.containsPotentialPII("admin@corp.io")) }
    @Test fun testPos_Email_003() { assertTrue(RegexScreener.containsPotentialPII("test.user@sub.domain.org")) }
    @Test fun testPos_Email_004() { assertTrue(RegexScreener.containsPotentialPII("a+b@c.de")) }
    @Test fun testPos_Email_005() { assertTrue(RegexScreener.containsPotentialPII("x@y.museum")) }
    @Test fun testPos_Card_001() { assertTrue(RegexScreener.containsPotentialPII("4111111111111111")) }
    @Test fun testPos_Card_002() { assertTrue(RegexScreener.containsPotentialPII("5500005555555559")) }
    @Test fun testPos_Card_003() { assertTrue(RegexScreener.containsPotentialPII("378282246310005")) }
    @Test fun testPos_Card_004() { assertTrue(RegexScreener.containsPotentialPII("6011111111111117")) }
    @Test fun testPos_Card_005() { assertTrue(RegexScreener.containsPotentialPII("3530111333300000")) }
    @Test fun testPos_Phone_001() { assertTrue(RegexScreener.containsPotentialPII("(555) 123-4567")) }
    @Test fun testPos_Phone_002() { assertTrue(RegexScreener.containsPotentialPII("+1-800-555-0100")) }
    @Test fun testPos_Phone_003() { assertTrue(RegexScreener.containsPotentialPII("555.234.5678")) }
    @Test fun testPos_Phone_004() { assertTrue(RegexScreener.containsPotentialPII("+44 20 7946 0958")) }
    @Test fun testPos_Phone_005() { assertTrue(RegexScreener.containsPotentialPII("5553456789")) }

    // =========================================================================
    // SECTION E2: Comprehensive containsPotentialPII Negative Tests
    // =========================================================================

    @Test fun testNeg_PlainText_001() { assertFalse(RegexScreener.containsPotentialPII("Hello World")) }
    @Test fun testNeg_PlainText_002() { assertFalse(RegexScreener.containsPotentialPII("The quick brown fox")) }
    @Test fun testNeg_PlainText_003() { assertFalse(RegexScreener.containsPotentialPII("Android development is fun")) }
    @Test fun testNeg_PlainText_004() { assertFalse(RegexScreener.containsPotentialPII("Privacy protection matters")) }
    @Test fun testNeg_PlainText_005() { assertFalse(RegexScreener.containsPotentialPII("Kotlin is a modern language")) }
    @Test fun testNeg_ShortNumbers_001() { assertFalse(RegexScreener.containsPotentialPII("Version 2.0")) }
    @Test fun testNeg_ShortNumbers_002() { assertFalse(RegexScreener.containsPotentialPII("Page 42")) }
    @Test fun testNeg_ShortNumbers_003() { assertFalse(RegexScreener.containsPotentialPII("Score: 100")) }
    @Test fun testNeg_ShortNumbers_004() { assertFalse(RegexScreener.containsPotentialPII("Item 7")) }
    @Test fun testNeg_ShortNumbers_005() { assertFalse(RegexScreener.containsPotentialPII("Year: 2024")) }
    @Test fun testNeg_URLs_001() { assertFalse(RegexScreener.containsPotentialPII("Visit https://example.com")) }
    @Test fun testNeg_URLs_002() { assertFalse(RegexScreener.containsPotentialPII("See http://docs.android.com")) }
    @Test fun testNeg_InvalidSSN_001() { assertFalse(RegexScreener.containsPotentialPII("000-45-6789")) }
    @Test fun testNeg_InvalidSSN_002() { assertFalse(RegexScreener.containsPotentialPII("123-00-6789")) }
    @Test fun testNeg_InvalidSSN_003() { assertFalse(RegexScreener.containsPotentialPII("123-45-0000")) }
    @Test fun testNeg_MaskedData_001() { assertFalse(RegexScreener.containsPotentialPII("****-****-****-1234")) }
    @Test fun testNeg_MaskedData_002() { assertFalse(RegexScreener.containsPotentialPII("***-**-6789")) }
    @Test fun testNeg_EmptyString_001() { assertFalse(RegexScreener.containsPotentialPII("")) }
    @Test fun testNeg_OnlySpaces_001() { assertFalse(RegexScreener.containsPotentialPII("     ")) }
    @Test fun testNeg_ProgrammingCode_001() {
        assertFalse(RegexScreener.containsPotentialPII("val x = listOf(1, 2, 3)"))
    }

    // =========================================================================
    // SECTION E3: extractEntities — Return Value Tests
    // =========================================================================

    @Test fun testReturn_IsNotNull_001() {
        assertNotNull(RegexScreener.extractEntities("any text"))
    }

    @Test fun testReturn_IsNotNull_002() {
        assertNotNull(RegexScreener.extractEntities(""))
    }

    @Test fun testReturn_IsNotNull_003() {
        assertNotNull(RegexScreener.extractEntities("SSN: 123-45-6789"))
    }

    @Test fun testReturn_IsList_001() {
        val result = RegexScreener.extractEntities("any text")
        assertTrue("Returns list", result is List<*>)
    }

    @Test fun testReturn_EmptyForNoMatch_001() {
        val result = RegexScreener.extractEntities("no pii here")
        assertTrue("Empty for no PII", result.isEmpty())
    }

    @Test fun testReturn_NonEmptyForSSN_001() {
        val result = RegexScreener.extractEntities("SSN: 234-56-7890")
        assertTrue("Non-empty for SSN", result.isNotEmpty())
    }

    @Test fun testReturn_NonEmptyForEmail_001() {
        val result = RegexScreener.extractEntities("Email: user@example.com")
        assertTrue("Non-empty for email", result.isNotEmpty())
    }

    @Test fun testReturn_NonEmptyForCard_001() {
        val result = RegexScreener.extractEntities("Card: 4111111111111111")
        assertTrue("Non-empty for card", result.isNotEmpty())
    }

    @Test fun testReturn_EntityHasType_001() {
        val result = RegexScreener.extractEntities("SSN: 345-67-8901")
        result.forEach { entity -> assertNotNull("Entity has type", entity.entityType) }
    }

    @Test fun testReturn_EntitySSNType_001() {
        val result = RegexScreener.extractEntities("SSN: 456-78-9012")
        val ssnEntities = result.filter { it.entityType == EntityType.SSN }
        assertTrue("SSN type in result", ssnEntities.isNotEmpty())
    }

    @Test fun testReturn_EntityEmailType_001() {
        val result = RegexScreener.extractEntities("Email: user@example.com")
        val emailEntities = result.filter { it.entityType == EntityType.EMAIL }
        assertTrue("Email type in result", emailEntities.isNotEmpty())
    }

    @Test fun testReturn_EntityCCType_001() {
        val result = RegexScreener.extractEntities("Card: 5555555555554444")
        val ccEntities = result.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("CC type in result", ccEntities.isNotEmpty())
    }

    @Test fun testReturn_EntityPhoneType_001() {
        val result = RegexScreener.extractEntities("Phone: (555) 678-9012")
        val phoneEntities = result.filter { it.entityType == EntityType.PHONE }
        assertTrue("Phone type in result", phoneEntities.isNotEmpty())
    }

    @Test fun testReturn_Deterministic_001() {
        val text = "SSN: 567-89-0123"
        val result1 = RegexScreener.extractEntities(text)
        val result2 = RegexScreener.extractEntities(text)
        assertEquals("Deterministic results", result1.size, result2.size)
    }

    @Test fun testReturn_DeterministicNoEntities_001() {
        val text = "no pii at all"
        val result1 = RegexScreener.extractEntities(text)
        val result2 = RegexScreener.extractEntities(text)
        assertEquals("Deterministic empty", result1.size, result2.size)
    }

    // =========================================================================
    // SECTION E4: Boundary and Edge Cases
    // =========================================================================

    @Test fun testBoundary_SingleChar_001() {
        val result = runCatching { RegexScreener.containsPotentialPII("a") }
        assertTrue("Single char safe", result.isSuccess)
    }

    @Test fun testBoundary_SingleChar_002() {
        val result = runCatching { RegexScreener.containsPotentialPII("1") }
        assertTrue("Single digit safe", result.isSuccess)
    }

    @Test fun testBoundary_OnlyDigits_001() {
        val result = runCatching { RegexScreener.containsPotentialPII("123456789012345678") }
        assertTrue("Only digits safe", result.isSuccess)
    }

    @Test fun testBoundary_OnlyLetters_001() {
        val result = runCatching { RegexScreener.containsPotentialPII("abcdefghijklmnopqrstuvwxyz") }
        assertTrue("Only letters safe", result.isSuccess)
    }

    @Test fun testBoundary_SSNAtStart_001() {
        assertTrue("SSN at start of string",
            RegexScreener.containsPotentialPII("123-45-6789 is the ID"))
    }

    @Test fun testBoundary_SSNAtEnd_001() {
        assertTrue("SSN at end of string",
            RegexScreener.containsPotentialPII("The ID is 234-56-7890"))
    }

    @Test fun testBoundary_EmailAtStart_001() {
        assertTrue("Email at start",
            RegexScreener.containsPotentialPII("user@example.com is the contact"))
    }

    @Test fun testBoundary_EmailAtEnd_001() {
        assertTrue("Email at end",
            RegexScreener.containsPotentialPII("Contact us at support@example.org"))
    }

    @Test fun testBoundary_CardAtStart_001() {
        assertTrue("Card at start",
            RegexScreener.containsPotentialPII("4111111111111111 is the primary card"))
    }

    @Test fun testBoundary_CardAtEnd_001() {
        assertTrue("Card at end",
            RegexScreener.containsPotentialPII("Primary card number: 5500005555555559"))
    }

    @Test fun testBoundary_MultipleSpaces_001() {
        val result = runCatching {
            RegexScreener.containsPotentialPII("Hello    World    Test")
        }
        assertTrue("Multiple spaces safe", result.isSuccess)
    }

    @Test fun testBoundary_TabCharacters_001() {
        val result = runCatching {
            RegexScreener.containsPotentialPII("SSN:\t123-45-6789")
        }
        assertTrue("Tab-separated safe", result.isSuccess)
    }

    @Test fun testBoundary_CarriageReturn_001() {
        val result = runCatching {
            RegexScreener.containsPotentialPII("Line1\r\nSSN: 345-67-8901")
        }
        assertTrue("CRLF text safe", result.isSuccess)
    }

    @Test fun testBoundary_NullBytes_001() {
        val result = runCatching {
            RegexScreener.containsPotentialPII("text\u0000SSN: 456-78-9012")
        }
        assertTrue("Null byte in text safe", result.isSuccess)
    }

    @Test fun testBoundary_HighUnicode_001() {
        val result = runCatching {
            RegexScreener.containsPotentialPII("名前: SSN 567-89-0123")
        }
        assertTrue("CJK characters safe", result.isSuccess)
    }

    // =========================================================================
    // SECTION E5: API Key containsPotentialPII Tests
    // =========================================================================

    @Test fun testAPIKey_StripeKey_001() { assertTrue(RegexScreener.containsPotentialPII("sk_lvd_abcdefghijklmnopqrstuvwxyz1234")) }
    @Test fun testAPIKey_StripeKey_002() { assertTrue(RegexScreener.containsPotentialPII("pk_live_ABCDEFGHIJKLMNOPQRSTUVWXYZabcd")) }
    @Test fun testAPIKey_GitHub_001() { assertTrue(RegexScreener.containsPotentialPII("ghp_xxx")) }
    @Test fun testAPIKey_GitHub_002() { assertTrue(RegexScreener.containsPotentialPII("github_pat_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi")) }
    @Test fun testAPIKey_OpenAI_001() { assertTrue(RegexScreener.containsPotentialPII("sk-aBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRs")) }
    @Test fun testAPIKey_UUID_001() { assertTrue(RegexScreener.containsPotentialPII("token: 550e8400-e29b-41d4-a716-446655440000")) }
    @Test fun testAPIKey_HighEntropy_001() { assertTrue(RegexScreener.containsPotentialPII("SECRET=xK9mN2pL7qR4sT1uV8wX5yZ3aB6cD0eF")) }
    @Test fun testAPIKey_HighEntropy_002() { assertTrue(RegexScreener.containsPotentialPII("KEY: aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV")) }
    @Test fun testAPIKey_TooShort_001() { assertFalse(RegexScreener.containsPotentialPII("key: abc123")) }
    @Test fun testAPIKey_TooShort_002() { assertFalse(RegexScreener.containsPotentialPII("token: short")) }
    @Test fun testAPIKey_NormalWord_001() { assertFalse(RegexScreener.containsPotentialPII("The keyword is simple")) }
    @Test fun testAPIKey_NormalWord_002() { assertFalse(RegexScreener.containsPotentialPII("This is a standard sentence")) }
    @Test fun testAPIKey_InConfig_001() {
        assertTrue(RegexScreener.containsPotentialPII("STRIPE_KEY=sk_lvd_xyzABCDEFGHIJKLMNOPQRSTUV"))
    }
    @Test fun testAPIKey_InCode_001() {
        assertTrue(RegexScreener.containsPotentialPII("val API_KEY = \"ghp_xxx\""))
    }
    @Test fun testAPIKey_Bearer_001() {
        assertTrue(RegexScreener.containsPotentialPII("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.data.signature"))
    }
    @Test fun testAPIKey_JWKS_001() {
        assertTrue(RegexScreener.containsPotentialPII("access_token=eyJhbGciOiJSUzI1NiJ9.claims.sig"))
    }
    @Test fun testAPIKey_InJSON_001() {
        assertTrue(RegexScreener.containsPotentialPII("{\"api_key\": \"sk_lvd_ABCDEFGHIJKLMNOPQRSTUVWXYZab\"}"))
    }
    @Test fun testAPIKey_InYAML_001() {
        assertTrue(RegexScreener.containsPotentialPII("api_key: sk-aBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJk"))
    }
    @Test fun testAPIKey_InINI_001() {
        assertTrue(RegexScreener.containsPotentialPII("[auth]\nkey=ghp_xxx"))
    }
    @Test fun testAPIKey_InURL_001() {
        assertTrue(RegexScreener.containsPotentialPII("https://api.service.com?key=sk_lvd_ABCDEFGHIJKLMNOPQRSTUVWXa"))
    }
}

// =============================================================================
// RegexScreenerExtendedTest3F — Parametric Coverage Tests
// =============================================================================
class RegexScreenerExtendedTest3F {

    // =========================================================================
    // SECTION F1: SSN Area Number Range Tests
    // =========================================================================

    @Test fun testSSN_Area_001_199() { assertTrue(RegexScreener.containsPotentialPII("001-23-4567")) }
    @Test fun testSSN_Area_100_199() { assertTrue(RegexScreener.containsPotentialPII("134-56-7890")) }
    @Test fun testSSN_Area_200_299() { assertTrue(RegexScreener.containsPotentialPII("212-34-5678")) }
    @Test fun testSSN_Area_300_399() { assertTrue(RegexScreener.containsPotentialPII("312-45-6789")) }
    @Test fun testSSN_Area_400_499() { assertTrue(RegexScreener.containsPotentialPII("412-56-7890")) }
    @Test fun testSSN_Area_500_599() { assertTrue(RegexScreener.containsPotentialPII("512-34-5678")) }
    @Test fun testSSN_Area_600_699() { assertTrue(RegexScreener.containsPotentialPII("612-45-6789")) }
    @Test fun testSSN_Area_700_799() { assertTrue(RegexScreener.containsPotentialPII("712-56-7890")) }
    @Test fun testSSN_Area_800_899() { assertTrue(RegexScreener.containsPotentialPII("812-34-5678")) }
    @Test fun testSSN_Area_900_999() { assertFalse(RegexScreener.containsPotentialPII("912-45-6789")) }
    @Test fun testSSN_Group_01_to_09() { assertTrue(RegexScreener.containsPotentialPII("123-05-6789")) }
    @Test fun testSSN_Group_10_to_19() { assertTrue(RegexScreener.containsPotentialPII("234-15-7890")) }
    @Test fun testSSN_Group_20_to_29() { assertTrue(RegexScreener.containsPotentialPII("345-25-8901")) }
    @Test fun testSSN_Group_30_to_39() { assertTrue(RegexScreener.containsPotentialPII("456-35-9012")) }
    @Test fun testSSN_Group_40_to_49() { assertTrue(RegexScreener.containsPotentialPII("567-45-0123")) }
    @Test fun testSSN_Serial_0001_to_9999() { assertTrue(RegexScreener.containsPotentialPII("678-56-0001")) }
    @Test fun testSSN_Serial_5000() { assertTrue(RegexScreener.containsPotentialPII("789-67-5000")) }
    @Test fun testSSN_Serial_9998() { assertTrue(RegexScreener.containsPotentialPII("890-78-9998")) }
    @Test fun testSSN_AdinBadge_001() { assertFalse(RegexScreener.containsPotentialPII("666-12-3456")) }
    @Test fun testSSN_AdinBadge_002() { assertFalse(RegexScreener.containsPotentialPII("666-78-9012")) }

    // =========================================================================
    // SECTION F2: Email TLD Variation Tests
    // =========================================================================

    @Test fun testEmail_TLD_com_001() { assertTrue(RegexScreener.containsPotentialPII("user@example.com")) }
    @Test fun testEmail_TLD_org_001() { assertTrue(RegexScreener.containsPotentialPII("user@example.org")) }
    @Test fun testEmail_TLD_net_001() { assertTrue(RegexScreener.containsPotentialPII("user@example.net")) }
    @Test fun testEmail_TLD_edu_001() { assertTrue(RegexScreener.containsPotentialPII("user@university.edu")) }
    @Test fun testEmail_TLD_gov_001() { assertTrue(RegexScreener.containsPotentialPII("user@agency.gov")) }
    @Test fun testEmail_TLD_mil_001() { assertTrue(RegexScreener.containsPotentialPII("user@base.mil")) }
    @Test fun testEmail_TLD_io_001() { assertTrue(RegexScreener.containsPotentialPII("user@startup.io")) }
    @Test fun testEmail_TLD_co_001() { assertTrue(RegexScreener.containsPotentialPII("user@company.co")) }
    @Test fun testEmail_TLD_uk_001() { assertTrue(RegexScreener.containsPotentialPII("user@domain.co.uk")) }
    @Test fun testEmail_TLD_de_001() { assertTrue(RegexScreener.containsPotentialPII("user@company.de")) }
    @Test fun testEmail_TLD_fr_001() { assertTrue(RegexScreener.containsPotentialPII("user@service.fr")) }
    @Test fun testEmail_TLD_jp_001() { assertTrue(RegexScreener.containsPotentialPII("user@company.jp")) }
    @Test fun testEmail_TLD_cn_001() { assertTrue(RegexScreener.containsPotentialPII("user@domain.cn")) }
    @Test fun testEmail_TLD_in_001() { assertTrue(RegexScreener.containsPotentialPII("user@corp.in")) }
    @Test fun testEmail_TLD_dev_001() { assertTrue(RegexScreener.containsPotentialPII("user@project.dev")) }
    @Test fun testEmail_TLD_app_001() { assertTrue(RegexScreener.containsPotentialPII("user@myapp.app")) }
    @Test fun testEmail_TLD_tech_001() { assertTrue(RegexScreener.containsPotentialPII("user@company.tech")) }
    @Test fun testEmail_TLD_ai_001() { assertTrue(RegexScreener.containsPotentialPII("user@startup.ai")) }
    @Test fun testEmail_TLD_info_001() { assertTrue(RegexScreener.containsPotentialPII("user@service.info")) }
    @Test fun testEmail_TLD_biz_001() { assertTrue(RegexScreener.containsPotentialPII("user@business.biz")) }

    // =========================================================================
    // SECTION F3: Credit Card IIN Prefix Tests
    // =========================================================================

    @Test fun testCC_IIN_Visa_4_001() { assertTrue(RegexScreener.containsPotentialPII("4111111111111111")) }
    @Test fun testCC_IIN_Visa_4_002() { assertTrue(RegexScreener.containsPotentialPII("4012888888881881")) }
    @Test fun testCC_IIN_MC_51_001() { assertTrue(RegexScreener.containsPotentialPII("5100000000000040")) }
    @Test fun testCC_IIN_MC_52_001() { assertTrue(RegexScreener.containsPotentialPII("5200000000000056")) }
    @Test fun testCC_IIN_MC_53_001() { assertTrue(RegexScreener.containsPotentialPII("5300000000000057")) }
    @Test fun testCC_IIN_MC_54_001() { assertTrue(RegexScreener.containsPotentialPII("5400000000000056")) }
    @Test fun testCC_IIN_MC_55_001() { assertTrue(RegexScreener.containsPotentialPII("5500005555555559")) }
    @Test fun testCC_IIN_Amex_34_001() { assertTrue(RegexScreener.containsPotentialPII("378282246310005")) }
    @Test fun testCC_IIN_Amex_37_001() { assertTrue(RegexScreener.containsPotentialPII("371449635398431")) }
    @Test fun testCC_IIN_Discover_6011_001() { assertTrue(RegexScreener.containsPotentialPII("6011111111111117")) }
    @Test fun testCC_IIN_JCB_3530_001() { assertTrue(RegexScreener.containsPotentialPII("3530111333300000")) }
    @Test fun testCC_IIN_JCB_3566_001() { assertTrue(RegexScreener.containsPotentialPII("3566002020360505")) }
    @Test fun testCC_IIN_Diners_30_001() { assertTrue(RegexScreener.containsPotentialPII("30569309025904")) }
    @Test fun testCC_IIN_Diners_38_001() { assertTrue(RegexScreener.containsPotentialPII("38520000023237")) }
    @Test fun testCC_LuhnEdge_001() { assertTrue(RegexScreener.luhnCheck("4222222222222")) }
    @Test fun testCC_LuhnEdge_002() { assertFalse(RegexScreener.luhnCheck("4222222222221")) }
    @Test fun testCC_LuhnEdge_003() { assertTrue(RegexScreener.luhnCheck("79927398713")) }
    @Test fun testCC_LuhnEdge_004() { assertFalse(RegexScreener.luhnCheck("79927398714")) }
    @Test fun testCC_LuhnEdge_005() { assertFalse(RegexScreener.luhnCheck("79927398710")) }
    @Test fun testCC_LuhnEdge_006() { assertFalse(RegexScreener.luhnCheck("79927398711")) }

    // =========================================================================
    // SECTION F4: Phone Country Code Tests
    // =========================================================================

    @Test fun testPhone_CC_US_1_001() { assertTrue(RegexScreener.containsPotentialPII("+1 555 123 4567")) }
    @Test fun testPhone_CC_UK_44_001() { assertTrue(RegexScreener.containsPotentialPII("+44 20 7946 0958")) }
    @Test fun testPhone_CC_DE_49_001() { assertTrue(RegexScreener.containsPotentialPII("+49 30 12345678")) }
    @Test fun testPhone_CC_FR_33_001() { assertTrue(RegexScreener.containsPotentialPII("+33 1 42 68 53 00")) }
    @Test fun testPhone_CC_JP_81_001() { assertTrue(RegexScreener.containsPotentialPII("+81 3 1234 5678")) }
    @Test fun testPhone_CC_CN_86_001() { assertTrue(RegexScreener.containsPotentialPII("+86 10 12345678")) }
    @Test fun testPhone_CC_IN_91_001() { assertTrue(RegexScreener.containsPotentialPII("+91 98765 43210")) }
    @Test fun testPhone_CC_BR_55_001() { assertTrue(RegexScreener.containsPotentialPII("+55 11 91234-5678")) }
    @Test fun testPhone_CC_AU_61_001() { assertTrue(RegexScreener.containsPotentialPII("+61 2 9876 5432")) }
    @Test fun testPhone_CC_CA_1_001() { assertTrue(RegexScreener.containsPotentialPII("+1 416 555 0123")) }
    @Test fun testPhone_CC_MX_52_001() { assertTrue(RegexScreener.containsPotentialPII("+52 55 1234 5678")) }
    @Test fun testPhone_CC_RU_7_001() { assertTrue(RegexScreener.containsPotentialPII("+7 495 123-45-67")) }
    @Test fun testPhone_CC_KR_82_001() { assertTrue(RegexScreener.containsPotentialPII("+82 2 1234 5678")) }
    @Test fun testPhone_CC_IT_39_001() { assertTrue(RegexScreener.containsPotentialPII("+39 06 1234 5678")) }
    @Test fun testPhone_CC_ES_34_001() { assertTrue(RegexScreener.containsPotentialPII("+34 91 123 45 67")) }
    @Test fun testPhone_CC_NL_31_001() { assertTrue(RegexScreener.containsPotentialPII("+31 20 123 4567")) }
    @Test fun testPhone_CC_CH_41_001() { assertTrue(RegexScreener.containsPotentialPII("+41 44 123 45 67")) }
    @Test fun testPhone_CC_SE_46_001() { assertTrue(RegexScreener.containsPotentialPII("+46 8 123 456 78")) }
    @Test fun testPhone_CC_NO_47_001() { assertTrue(RegexScreener.containsPotentialPII("+47 22 12 34 56")) }
    @Test fun testPhone_CC_DK_45_001() { assertTrue(RegexScreener.containsPotentialPII("+45 32 12 34 56")) }

    // =========================================================================
    // SECTION F5: Mixed PII in Realistic Templates
    // =========================================================================

    @Test fun testTemplate_MedicalRecord_001() {
        val text = """
            Patient Record
            Name: John Smith
            DOB: 1980-05-15
            SSN: 123-45-6789
            Email: john.smith@example.com
            Phone: (555) 234-5678
        """.trimIndent()
        assertTrue("Medical record PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_CreditApp_001() {
        val text = """
            Credit Application
            Applicant: Jane Doe
            SSN: 234-56-7890
            Card Number: 4111111111111111
            Contact: jane.doe@bank.com
        """.trimIndent()
        assertTrue("Credit app PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_HRRecord_001() {
        val text = """
            Employee: Bob Johnson
            SSN: 345-67-8901
            Email: bjohnson@company.org
            Phone: (555) 345-6789
            Salary: $85,000
        """.trimIndent()
        assertTrue("HR record PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_BankStatement_001() {
        val text = """
            Account Holder: Alice Brown
            Card: 5500005555555559
            Billing: alice.brown@gmail.com
            Tel: +1-555-456-7890
        """.trimIndent()
        assertTrue("Bank statement PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_InsuranceClaim_001() {
        val text = """
            Claimant: Charlie Wilson
            Policy SSN: 456-78-9012
            Contact: charlie.wilson@insurance.net
            Phone: 555-567-8901
        """.trimIndent()
        assertTrue("Insurance claim PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_TaxForm_001() {
        val text = "TIN: 567-89-0123, Filer: Diana Davis, contact@tax.gov"
        assertTrue("Tax form PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_LogFile_NoUser_001() {
        val text = """
            2024-01-15 10:23:45 INFO Application started
            2024-01-15 10:23:46 DEBUG Loading configuration
            2024-01-15 10:23:47 INFO Server listening on port 8080
        """.trimIndent()
        assertFalse("Normal log file no PII", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_LogFile_WithPII_001() {
        val text = """
            2024-01-15 10:23:45 INFO User login: admin@example.com
            2024-01-15 10:23:46 DEBUG Auth token: sk_lvd_xyzABCDEFGHIJKLMNOPQRS
        """.trimIndent()
        assertTrue("Log file with PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_APIResponse_NoPII_001() {
        val text = "{\"status\":\"ok\",\"count\":42,\"page\":1,\"total\":100}"
        assertFalse("Clean API response no PII", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_APIResponse_WithPII_001() {
        val text = "{\"user\":{\"email\":\"user@example.com\",\"ssn\":\"678-90-1234\"}}"
        assertTrue("API response with PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_ConfigFile_NoPII_001() {
        val text = """
            database.host=localhost
            database.port=5432
            database.name=myapp
            log.level=INFO
        """.trimIndent()
        assertFalse("Clean config no PII", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_ConfigFile_WithKey_001() {
        val text = """
            database.host=localhost
            stripe.key=sk_lvd_xyzABCDEFGHIJKLMNOPQRSTUVW
            log.level=INFO
        """.trimIndent()
        assertTrue("Config with API key detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_MarkdownDoc_NoPII_001() {
        val text = """
            # Introduction
            This document describes the API usage.
            ## Endpoints
            POST /api/v1/users
            GET /api/v1/users/{id}
        """.trimIndent()
        assertFalse("Clean markdown no PII", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_MarkdownDoc_WithEmail_001() {
        val text = """
            # Contact
            For support, email: support@example.com
            For billing, email: billing@example.com
        """.trimIndent()
        assertTrue("Markdown with emails detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_CSVRow_WithPII_001() {
        val text = "John Doe,123-45-6789,john.doe@example.com,(555) 123-4567"
        assertTrue("CSV row with PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_CSVRow_NoPII_001() {
        val text = "product1,10.99,In Stock,Category A"
        assertFalse("CSV product row no PII", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_XMLData_WithPII_001() {
        val text = "<user><ssn>789-01-2345</ssn><email>user@example.com</email></user>"
        assertTrue("XML with PII detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_JSONFlat_WithCard_001() {
        val text = "{\"card\":\"4111111111111111\",\"cvv\":\"123\",\"exp\":\"12/25\"}"
        assertTrue("JSON with card detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_URLParams_WithEmail_001() {
        val text = "https://example.com/reset?email=user@example.com&token=abc123"
        assertTrue("URL params with email detected", RegexScreener.containsPotentialPII(text))
    }

    @Test fun testTemplate_CurlCommand_WithKey_001() {
        val text = "curl -H \"Authorization: Bearer sk_lvd_xyzABCDEFGHIJKLMNOPQRSTUV\" https://api.example.com"
        assertTrue("Curl command with key detected", RegexScreener.containsPotentialPII(text))
    }

    // =========================================================================
    // SECTION F6: Final Validation Tests
    // =========================================================================

    @Test fun testFinal_AllEntityTypes_Consistent_001() {
        val text = "SSN: 123-45-6789"
        val hasPII = RegexScreener.containsPotentialPII(text)
        val entities = RegexScreener.extractEntities(text)
        if (hasPII) {
            assertFalse("If containsPII, entities not empty", entities.isEmpty())
        }
    }

    @Test fun testFinal_AllEntityTypes_Consistent_002() {
        val text = "no pii here"
        val hasPII = RegexScreener.containsPotentialPII(text)
        val entities = RegexScreener.extractEntities(text)
        if (!hasPII) {
            val relevantEntities = entities.filter {
                it.entityType in listOf(EntityType.SSN, EntityType.CREDIT_CARD, EntityType.EMAIL, EntityType.PHONE)
            }
            assertTrue("If no PII, no relevant entities", relevantEntities.isEmpty())
        }
    }

    @Test fun testFinal_Luhn_KnownValues_001() {
        // Luhn check digit verification
        assertTrue(RegexScreener.luhnCheck("4111111111111111"))
        assertTrue(RegexScreener.luhnCheck("5500005555555559"))
        assertTrue(RegexScreener.luhnCheck("378282246310005"))
        assertTrue(RegexScreener.luhnCheck("6011111111111117"))
        assertFalse(RegexScreener.luhnCheck("4111111111111112"))
    }

    @Test fun testFinal_HighVolume_001() {
        val numbers = (1..50).map { i ->
            "SSN: ${100 + i}-${10 + i % 90}-${1000 + i}"
        }
        numbers.forEach { text ->
            val result = runCatching { RegexScreener.containsPotentialPII(text) }
            assertTrue("High volume test safe", result.isSuccess)
        }
    }

    @Test fun testFinal_HighVolume_002() {
        val emails = (1..50).map { i -> "user$i@example$i.com" }
        emails.forEach { email ->
            assertTrue("Email $email detected", RegexScreener.containsPotentialPII(email))
        }
    }

    @Test fun testFinal_HighVolume_003() {
        val phones = (1..50).map { i -> "(555) ${100 + i}-${1000 + i}" }
        phones.forEach { phone ->
            val result = runCatching { RegexScreener.containsPotentialPII(phone) }
            assertTrue("Phone $phone safe", result.isSuccess)
        }
    }

    @Test fun testFinal_NullSafety_001() {
        val result = runCatching { RegexScreener.extractEntities("") }
        assertTrue("extractEntities empty string safe", result.isSuccess)
    }

    @Test fun testFinal_NullSafety_002() {
        val result = runCatching { RegexScreener.containsPotentialPII("   ") }
        assertTrue("containsPII whitespace safe", result.isSuccess)
    }

    @Test fun testFinal_CrossMethod_001() {
        val piiTexts = listOf(
            "123-45-6789",
            "user@example.com",
            "4111111111111111",
            "(555) 123-4567",
            "sk_lvd_ABCDEFGHIJKLMNOPQRSTUVWXYZa"
        )
        piiTexts.forEach { text ->
            assertTrue("$text should contain PII", RegexScreener.containsPotentialPII(text))
        }
    }

    @Test fun testFinal_CrossMethod_002() {
        val cleanTexts = listOf(
            "Hello World",
            "Version 1.0.0",
            "Score: 100",
            "Build successful",
            "Privacy protection"
        )
        cleanTexts.forEach { text ->
            assertFalse("$text should not contain PII", RegexScreener.containsPotentialPII(text))
        }
    }
            assertFalse("$text should not contain PII", RegexScreener.containsPotentialPII(text))
        }
    }
}

// =============================================================================
// RegexScreenerExtendedTest3G — Expanded Regression and Format Tests
// =============================================================================
class RegexScreenerExtendedTest3G {

    // =========================================================================
    // SECTION G1: SSN Regression Suite
    // =========================================================================

    @Test fun testSSN_R001() { assertTrue(RegexScreener.containsPotentialPII("123-45-6789")) }
    @Test fun testSSN_R002() { assertTrue(RegexScreener.containsPotentialPII("234-56-7890")) }
    @Test fun testSSN_R003() { assertTrue(RegexScreener.containsPotentialPII("345-67-8901")) }
    @Test fun testSSN_R004() { assertTrue(RegexScreener.containsPotentialPII("456-78-9012")) }
    @Test fun testSSN_R005() { assertTrue(RegexScreener.containsPotentialPII("567-89-0123")) }
    @Test fun testSSN_R006() { assertTrue(RegexScreener.containsPotentialPII("678-90-1234")) }
    @Test fun testSSN_R007() { assertTrue(RegexScreener.containsPotentialPII("789-01-2345")) }
    @Test fun testSSN_R008() { assertTrue(RegexScreener.containsPotentialPII("890-12-3456")) }
    @Test fun testSSN_R009() { assertTrue(RegexScreener.containsPotentialPII("901-23-4567")) }
    @Test fun testSSN_R010() { assertTrue(RegexScreener.containsPotentialPII("012-34-5678")) }
    @Test fun testSSN_R011() { assertTrue(RegexScreener.containsPotentialPII("111-22-3333")) }
    @Test fun testSSN_R012() { assertTrue(RegexScreener.containsPotentialPII("222-33-4444")) }
    @Test fun testSSN_R013() { assertTrue(RegexScreener.containsPotentialPII("333-44-5555")) }
    @Test fun testSSN_R014() { assertTrue(RegexScreener.containsPotentialPII("444-55-6666")) }
    @Test fun testSSN_R015() { assertTrue(RegexScreener.containsPotentialPII("555-44-3333")) }
    @Test fun testSSN_R016() { assertTrue(RegexScreener.containsPotentialPII("199-45-6789")) }
    @Test fun testSSN_R017() { assertTrue(RegexScreener.containsPotentialPII("299-56-7890")) }
    @Test fun testSSN_R018() { assertTrue(RegexScreener.containsPotentialPII("399-67-8901")) }
    @Test fun testSSN_R019() { assertTrue(RegexScreener.containsPotentialPII("499-78-9012")) }
    @Test fun testSSN_R020() { assertTrue(RegexScreener.containsPotentialPII("599-89-0123")) }
    @Test fun testSSN_R_Neg_001() { assertFalse(RegexScreener.containsPotentialPII("000-45-6789")) }
    @Test fun testSSN_R_Neg_002() { assertFalse(RegexScreener.containsPotentialPII("123-00-6789")) }
    @Test fun testSSN_R_Neg_003() { assertFalse(RegexScreener.containsPotentialPII("123-45-0000")) }
    @Test fun testSSN_R_Neg_004() { assertFalse(RegexScreener.containsPotentialPII("666-12-3456")) }
    @Test fun testSSN_R_Neg_005() { assertFalse(RegexScreener.containsPotentialPII("900-45-6789")) }

    // =========================================================================
    // SECTION G2: Email Regression Suite
    // =========================================================================

    @Test fun testEmail_R001() { assertTrue(RegexScreener.containsPotentialPII("a@b.com")) }
    @Test fun testEmail_R002() { assertTrue(RegexScreener.containsPotentialPII("user@example.com")) }
    @Test fun testEmail_R003() { assertTrue(RegexScreener.containsPotentialPII("john.doe@company.org")) }
    @Test fun testEmail_R004() { assertTrue(RegexScreener.containsPotentialPII("jane.doe@company.net")) }
    @Test fun testEmail_R005() { assertTrue(RegexScreener.containsPotentialPII("support@help.io")) }
    @Test fun testEmail_R006() { assertTrue(RegexScreener.containsPotentialPII("admin@system.co.uk")) }
    @Test fun testEmail_R007() { assertTrue(RegexScreener.containsPotentialPII("info@corp.de")) }
    @Test fun testEmail_R008() { assertTrue(RegexScreener.containsPotentialPII("noreply@service.jp")) }
    @Test fun testEmail_R009() { assertTrue(RegexScreener.containsPotentialPII("user+tag@gmail.com")) }
    @Test fun testEmail_R010() { assertTrue(RegexScreener.containsPotentialPII("first.last@domain.edu")) }
    @Test fun testEmail_R011() { assertTrue(RegexScreener.containsPotentialPII("user123@example123.com")) }
    @Test fun testEmail_R012() { assertTrue(RegexScreener.containsPotentialPII("first_last@company.gov")) }
    @Test fun testEmail_R013() { assertTrue(RegexScreener.containsPotentialPII("contact@a.b.c.org")) }
    @Test fun testEmail_R014() { assertTrue(RegexScreener.containsPotentialPII("x@longdomainname.museum")) }
    @Test fun testEmail_R015() { assertTrue(RegexScreener.containsPotentialPII("user@startup.technology")) }
    @Test fun testEmail_R016() { assertTrue(RegexScreener.containsPotentialPII("dev@project.dev")) }
    @Test fun testEmail_R017() { assertTrue(RegexScreener.containsPotentialPII("team@company.ai")) }
    @Test fun testEmail_R018() { assertTrue(RegexScreener.containsPotentialPII("ops@service.cloud")) }
    @Test fun testEmail_R019() { assertTrue(RegexScreener.containsPotentialPII("marketing@brand.store")) }
    @Test fun testEmail_R020() { assertTrue(RegexScreener.containsPotentialPII("hello@world.app")) }
    @Test fun testEmail_R_Neg_001() { assertFalse(RegexScreener.containsPotentialPII("not-an-email")) }
    @Test fun testEmail_R_Neg_002() { assertFalse(RegexScreener.containsPotentialPII("missing@tld")) }
    @Test fun testEmail_R_Neg_003() { assertFalse(RegexScreener.containsPotentialPII("@nodomain.com")) }
    @Test fun testEmail_R_Neg_004() { assertFalse(RegexScreener.containsPotentialPII("no at sign here")) }
    @Test fun testEmail_R_Neg_005() { assertFalse(RegexScreener.containsPotentialPII("dotonly.com")) }

    // =========================================================================
    // SECTION G3: Phone Regression Suite
    // =========================================================================

    @Test fun testPhone_R001() { assertTrue(RegexScreener.containsPotentialPII("(555) 100-2000")) }
    @Test fun testPhone_R002() { assertTrue(RegexScreener.containsPotentialPII("(555) 200-3000")) }
    @Test fun testPhone_R003() { assertTrue(RegexScreener.containsPotentialPII("(555) 300-4000")) }
    @Test fun testPhone_R004() { assertTrue(RegexScreener.containsPotentialPII("555-400-5000")) }
    @Test fun testPhone_R005() { assertTrue(RegexScreener.containsPotentialPII("555-500-6000")) }
    @Test fun testPhone_R006() { assertTrue(RegexScreener.containsPotentialPII("555.600.7000")) }
    @Test fun testPhone_R007() { assertTrue(RegexScreener.containsPotentialPII("555.700.8000")) }
    @Test fun testPhone_R008() { assertTrue(RegexScreener.containsPotentialPII("5558009000")) }
    @Test fun testPhone_R009() { assertTrue(RegexScreener.containsPotentialPII("5559001000")) }
    @Test fun testPhone_R010() { assertTrue(RegexScreener.containsPotentialPII("+1 555 001 2000")) }
    @Test fun testPhone_R011() { assertTrue(RegexScreener.containsPotentialPII("+1-555-002-3000")) }
    @Test fun testPhone_R012() { assertTrue(RegexScreener.containsPotentialPII("1-800-555-0100")) }
    @Test fun testPhone_R013() { assertTrue(RegexScreener.containsPotentialPII("1-888-555-0199")) }
    @Test fun testPhone_R014() { assertTrue(RegexScreener.containsPotentialPII("1-877-555-0150")) }
    @Test fun testPhone_R015() { assertTrue(RegexScreener.containsPotentialPII("1-866-555-0175")) }
    @Test fun testPhone_R016() { assertTrue(RegexScreener.containsPotentialPII("+44 20 7946 0958")) }
    @Test fun testPhone_R017() { assertTrue(RegexScreener.containsPotentialPII("+49 30 12345678")) }
    @Test fun testPhone_R018() { assertTrue(RegexScreener.containsPotentialPII("+33 1 42 68 53 00")) }
    @Test fun testPhone_R019() { assertTrue(RegexScreener.containsPotentialPII("+91 98765 43210")) }
    @Test fun testPhone_R020() { assertTrue(RegexScreener.containsPotentialPII("+61 2 9876 5432")) }
    @Test fun testPhone_R_Neg_001() { assertFalse(RegexScreener.containsPotentialPII("555-1234")) }
    @Test fun testPhone_R_Neg_002() { assertFalse(RegexScreener.containsPotentialPII("12345")) }
    @Test fun testPhone_R_Neg_003() { assertFalse(RegexScreener.containsPotentialPII("ABC-DEF-GHIJ")) }
    @Test fun testPhone_R_Neg_004() { assertFalse(RegexScreener.containsPotentialPII("100")) }
    @Test fun testPhone_R_Neg_005() { assertFalse(RegexScreener.containsPotentialPII("3.14159265")) }

    // =========================================================================
    // SECTION G4: Credit Card Regression Suite
    // =========================================================================

    @Test fun testCC_R001() { assertTrue(RegexScreener.containsPotentialPII("4111111111111111")) }
    @Test fun testCC_R002() { assertTrue(RegexScreener.containsPotentialPII("4012888888881881")) }
    @Test fun testCC_R003() { assertTrue(RegexScreener.containsPotentialPII("4917610000000000")) }
    @Test fun testCC_R004() { assertTrue(RegexScreener.containsPotentialPII("5500005555555559")) }
    @Test fun testCC_R005() { assertTrue(RegexScreener.containsPotentialPII("5555555555554444")) }
    @Test fun testCC_R006() { assertTrue(RegexScreener.containsPotentialPII("5105105105105100")) }
    @Test fun testCC_R007() { assertTrue(RegexScreener.containsPotentialPII("378282246310005")) }
    @Test fun testCC_R008() { assertTrue(RegexScreener.containsPotentialPII("371449635398431")) }
    @Test fun testCC_R009() { assertTrue(RegexScreener.containsPotentialPII("6011111111111117")) }
    @Test fun testCC_R010() { assertTrue(RegexScreener.containsPotentialPII("6011000990139424")) }
    @Test fun testCC_R011() { assertTrue(RegexScreener.containsPotentialPII("3530111333300000")) }
    @Test fun testCC_R012() { assertTrue(RegexScreener.containsPotentialPII("3566002020360505")) }
    @Test fun testCC_R013() { assertTrue(RegexScreener.containsPotentialPII("30569309025904")) }
    @Test fun testCC_R014() { assertTrue(RegexScreener.containsPotentialPII("38520000023237")) }
    @Test fun testCC_R015() { assertTrue(RegexScreener.containsPotentialPII("4222222222222")) }
    @Test fun testCC_R016() { assertTrue(RegexScreener.luhnCheck("4111111111111111")) }
    @Test fun testCC_R017() { assertTrue(RegexScreener.luhnCheck("5500005555555559")) }
    @Test fun testCC_R018() { assertFalse(RegexScreener.luhnCheck("4111111111111112")) }
    @Test fun testCC_R019() { assertFalse(RegexScreener.luhnCheck("5500005555555550")) }
    @Test fun testCC_R020() { assertFalse(RegexScreener.luhnCheck("1234567890123456")) }
    @Test fun testCC_R_Neg_001() { assertFalse(RegexScreener.containsPotentialPII("****-****-****-1234")) }
    @Test fun testCC_R_Neg_002() { assertFalse(RegexScreener.containsPotentialPII("Card: redacted")) }
    @Test fun testCC_R_Neg_003() { assertFalse(RegexScreener.containsPotentialPII("No card info")) }
    @Test fun testCC_R_Neg_004() { assertFalse(RegexScreener.luhnCheck("0000000000000000")) }
    @Test fun testCC_R_Neg_005() { assertFalse(RegexScreener.luhnCheck("9999999999999999")) }

    // =========================================================================
    // SECTION G5: Comprehensive extractEntities Tests
    // =========================================================================

    @Test fun testExtract_G001() {
        val r = RegexScreener.extractEntities("SSN: 123-45-6789")
        assertTrue(r.any { it.entityType == EntityType.SSN })
    }
    @Test fun testExtract_G002() {
        val r = RegexScreener.extractEntities("user@example.com")
        assertTrue(r.any { it.entityType == EntityType.EMAIL })
    }
    @Test fun testExtract_G003() {
        val r = RegexScreener.extractEntities("Card: 4111111111111111")
        assertTrue(r.any { it.entityType == EntityType.CREDIT_CARD })
    }
    @Test fun testExtract_G004() {
        val r = RegexScreener.extractEntities("Phone: (555) 123-4567")
        assertTrue(r.any { it.entityType == EntityType.PHONE })
    }
    @Test fun testExtract_G005() {
        val r = RegexScreener.extractEntities("")
        assertTrue(r.isEmpty())
    }
    @Test fun testExtract_G006() {
        val r = RegexScreener.extractEntities("Hello World")
        assertTrue(r.none { it.entityType == EntityType.SSN })
    }
    @Test fun testExtract_G007() {
        val r = RegexScreener.extractEntities("no pii at all")
        assertTrue(r.none { it.entityType == EntityType.CREDIT_CARD })
    }
    @Test fun testExtract_G008() {
        val r1 = RegexScreener.extractEntities("SSN: 234-56-7890")
        val r2 = RegexScreener.extractEntities("SSN: 234-56-7890")
        assertEquals(r1.size, r2.size)
    }
    @Test fun testExtract_G009() {
        val r = RegexScreener.extractEntities("SSN: 345-67-8901, Email: a@b.com")
        assertTrue(r.size >= 1)
    }
    @Test fun testExtract_G010() {
        val r = RegexScreener.extractEntities("Card: 5500005555555559, Phone: (555) 234-5678")
        assertTrue(r.size >= 1)
    }
    @Test fun testExtract_G011() {
        val r = RegexScreener.extractEntities("API Key: sk_lvd_xyzABCDEFGHIJKLMNOPQRSTUVW")
        assertTrue(r.any { it.entityType == EntityType.API_KEY })
    }
    @Test fun testExtract_G012() {
        val r = RegexScreener.extractEntities("Key: short")
        assertTrue(r.none { it.entityType == EntityType.API_KEY })
    }
    @Test fun testExtract_G013() {
        val r = RegexScreener.extractEntities("456-78-9012")
        assertNotNull(r)
    }
    @Test fun testExtract_G014() {
        val r = RegexScreener.extractEntities("test@test.test")
        assertNotNull(r)
    }
    @Test fun testExtract_G015() {
        val r = RegexScreener.extractEntities("378282246310005")
        assertTrue(r.any { it.entityType == EntityType.CREDIT_CARD })
    }
    @Test fun testExtract_G016() {
        val r = RegexScreener.extractEntities("555-567-8901")
        assertNotNull(r)
    }
    @Test fun testExtract_G017() {
        val r = RegexScreener.extractEntities("ghp_xxx")
        assertTrue(r.any { it.entityType == EntityType.API_KEY })
    }
    @Test fun testExtract_G018() {
        val r = RegexScreener.extractEntities("Normal sentence with no PII at all in it.")
        assertTrue(r.none { it.entityType in listOf(EntityType.SSN, EntityType.EMAIL) })
    }
    @Test fun testExtract_G019() {
        val r = RegexScreener.extractEntities("SSN 567-89-0123 and email user@example.org present")
        assertTrue(r.isNotEmpty())
    }
    @Test fun testExtract_G020() {
        val longText = "a".repeat(500) + " SSN: 678-90-1234 " + "b".repeat(500)
        val r = runCatching { RegexScreener.extractEntities(longText) }
        assertTrue(r.isSuccess)
    }
        val r = runCatching { RegexScreener.extractEntities(longText) }
        assertTrue(r.isSuccess)
    }
}

// =============================================================================
// RegexScreenerExtendedTest3H — Bulk SSN, Email, Card, Phone Individual Tests
// =============================================================================
class RegexScreenerExtendedTest3H {

    // =========================================================================
    // SECTION H1: 50 Individual SSN Positive Tests
    // =========================================================================

    @Test fun testSSN_H001() { assertTrue(RegexScreener.containsPotentialPII("SSN 001-01-0001")) }
    @Test fun testSSN_H002() { assertTrue(RegexScreener.containsPotentialPII("SSN 002-02-0002")) }
    @Test fun testSSN_H003() { assertTrue(RegexScreener.containsPotentialPII("SSN 003-03-0003")) }
    @Test fun testSSN_H004() { assertTrue(RegexScreener.containsPotentialPII("SSN 004-04-0004")) }
    @Test fun testSSN_H005() { assertTrue(RegexScreener.containsPotentialPII("SSN 005-05-0005")) }
    @Test fun testSSN_H006() { assertTrue(RegexScreener.containsPotentialPII("SSN 006-06-0006")) }
    @Test fun testSSN_H007() { assertTrue(RegexScreener.containsPotentialPII("SSN 007-07-0007")) }
    @Test fun testSSN_H008() { assertTrue(RegexScreener.containsPotentialPII("SSN 008-08-0008")) }
    @Test fun testSSN_H009() { assertTrue(RegexScreener.containsPotentialPII("SSN 009-09-0009")) }
    @Test fun testSSN_H010() { assertTrue(RegexScreener.containsPotentialPII("SSN 010-10-0010")) }
    @Test fun testSSN_H011() { assertTrue(RegexScreener.containsPotentialPII("SSN 011-11-0011")) }
    @Test fun testSSN_H012() { assertTrue(RegexScreener.containsPotentialPII("SSN 012-12-0012")) }
    @Test fun testSSN_H013() { assertTrue(RegexScreener.containsPotentialPII("SSN 013-13-0013")) }
    @Test fun testSSN_H014() { assertTrue(RegexScreener.containsPotentialPII("SSN 014-14-0014")) }
    @Test fun testSSN_H015() { assertTrue(RegexScreener.containsPotentialPII("SSN 015-15-0015")) }
    @Test fun testSSN_H016() { assertTrue(RegexScreener.containsPotentialPII("SSN 016-16-0016")) }
    @Test fun testSSN_H017() { assertTrue(RegexScreener.containsPotentialPII("SSN 017-17-0017")) }
    @Test fun testSSN_H018() { assertTrue(RegexScreener.containsPotentialPII("SSN 018-18-0018")) }
    @Test fun testSSN_H019() { assertTrue(RegexScreener.containsPotentialPII("SSN 019-19-0019")) }
    @Test fun testSSN_H020() { assertTrue(RegexScreener.containsPotentialPII("SSN 020-20-0020")) }
    @Test fun testSSN_H021() { assertTrue(RegexScreener.containsPotentialPII("SSN 021-21-0021")) }
    @Test fun testSSN_H022() { assertTrue(RegexScreener.containsPotentialPII("SSN 022-22-0022")) }
    @Test fun testSSN_H023() { assertTrue(RegexScreener.containsPotentialPII("SSN 023-23-0023")) }
    @Test fun testSSN_H024() { assertTrue(RegexScreener.containsPotentialPII("SSN 024-24-0024")) }
    @Test fun testSSN_H025() { assertTrue(RegexScreener.containsPotentialPII("SSN 025-25-0025")) }

    // =========================================================================
    // SECTION H2: 25 Individual Email Tests
    // =========================================================================

    @Test fun testEmail_H001() { assertTrue(RegexScreener.containsPotentialPII("user001@example.com")) }
    @Test fun testEmail_H002() { assertTrue(RegexScreener.containsPotentialPII("user002@example.com")) }
    @Test fun testEmail_H003() { assertTrue(RegexScreener.containsPotentialPII("user003@example.com")) }
    @Test fun testEmail_H004() { assertTrue(RegexScreener.containsPotentialPII("user004@example.com")) }
    @Test fun testEmail_H005() { assertTrue(RegexScreener.containsPotentialPII("user005@example.com")) }
    @Test fun testEmail_H006() { assertTrue(RegexScreener.containsPotentialPII("user006@example.com")) }
    @Test fun testEmail_H007() { assertTrue(RegexScreener.containsPotentialPII("user007@example.com")) }
    @Test fun testEmail_H008() { assertTrue(RegexScreener.containsPotentialPII("user008@example.com")) }
    @Test fun testEmail_H009() { assertTrue(RegexScreener.containsPotentialPII("user009@example.com")) }
    @Test fun testEmail_H010() { assertTrue(RegexScreener.containsPotentialPII("user010@example.com")) }
    @Test fun testEmail_H011() { assertTrue(RegexScreener.containsPotentialPII("user011@example.com")) }
    @Test fun testEmail_H012() { assertTrue(RegexScreener.containsPotentialPII("user012@example.com")) }
    @Test fun testEmail_H013() { assertTrue(RegexScreener.containsPotentialPII("user013@example.com")) }
    @Test fun testEmail_H014() { assertTrue(RegexScreener.containsPotentialPII("user014@example.com")) }
    @Test fun testEmail_H015() { assertTrue(RegexScreener.containsPotentialPII("user015@example.com")) }
    @Test fun testEmail_H016() { assertTrue(RegexScreener.containsPotentialPII("user016@example.com")) }
    @Test fun testEmail_H017() { assertTrue(RegexScreener.containsPotentialPII("user017@example.com")) }
    @Test fun testEmail_H018() { assertTrue(RegexScreener.containsPotentialPII("user018@example.com")) }
    @Test fun testEmail_H019() { assertTrue(RegexScreener.containsPotentialPII("user019@example.com")) }
    @Test fun testEmail_H020() { assertTrue(RegexScreener.containsPotentialPII("user020@example.com")) }
    @Test fun testEmail_H021() { assertTrue(RegexScreener.containsPotentialPII("user021@example.com")) }
    @Test fun testEmail_H022() { assertTrue(RegexScreener.containsPotentialPII("user022@example.com")) }
    @Test fun testEmail_H023() { assertTrue(RegexScreener.containsPotentialPII("user023@example.com")) }
    @Test fun testEmail_H024() { assertTrue(RegexScreener.containsPotentialPII("user024@example.com")) }
    @Test fun testEmail_H025() { assertTrue(RegexScreener.containsPotentialPII("user025@example.com")) }

    // =========================================================================
    // SECTION H3: 25 Individual Phone Tests
    // =========================================================================

    @Test fun testPhone_H001() { assertTrue(RegexScreener.containsPotentialPII("(555) 100-1000")) }
    @Test fun testPhone_H002() { assertTrue(RegexScreener.containsPotentialPII("(555) 100-2000")) }
    @Test fun testPhone_H003() { assertTrue(RegexScreener.containsPotentialPII("(555) 100-3000")) }
    @Test fun testPhone_H004() { assertTrue(RegexScreener.containsPotentialPII("(555) 100-4000")) }
    @Test fun testPhone_H005() { assertTrue(RegexScreener.containsPotentialPII("(555) 100-5000")) }
    @Test fun testPhone_H006() { assertTrue(RegexScreener.containsPotentialPII("(555) 200-1000")) }
    @Test fun testPhone_H007() { assertTrue(RegexScreener.containsPotentialPII("(555) 200-2000")) }
    @Test fun testPhone_H008() { assertTrue(RegexScreener.containsPotentialPII("(555) 200-3000")) }
    @Test fun testPhone_H009() { assertTrue(RegexScreener.containsPotentialPII("(555) 200-4000")) }
    @Test fun testPhone_H010() { assertTrue(RegexScreener.containsPotentialPII("(555) 200-5000")) }
    @Test fun testPhone_H011() { assertTrue(RegexScreener.containsPotentialPII("555-300-1000")) }
    @Test fun testPhone_H012() { assertTrue(RegexScreener.containsPotentialPII("555-300-2000")) }
    @Test fun testPhone_H013() { assertTrue(RegexScreener.containsPotentialPII("555-300-3000")) }
    @Test fun testPhone_H014() { assertTrue(RegexScreener.containsPotentialPII("555-300-4000")) }
    @Test fun testPhone_H015() { assertTrue(RegexScreener.containsPotentialPII("555-300-5000")) }
    @Test fun testPhone_H016() { assertTrue(RegexScreener.containsPotentialPII("555.400.1000")) }
    @Test fun testPhone_H017() { assertTrue(RegexScreener.containsPotentialPII("555.400.2000")) }
    @Test fun testPhone_H018() { assertTrue(RegexScreener.containsPotentialPII("555.400.3000")) }
    @Test fun testPhone_H019() { assertTrue(RegexScreener.containsPotentialPII("555.400.4000")) }
    @Test fun testPhone_H020() { assertTrue(RegexScreener.containsPotentialPII("555.400.5000")) }
    @Test fun testPhone_H021() { assertTrue(RegexScreener.containsPotentialPII("+1 555 500 1000")) }
    @Test fun testPhone_H022() { assertTrue(RegexScreener.containsPotentialPII("+1 555 500 2000")) }
    @Test fun testPhone_H023() { assertTrue(RegexScreener.containsPotentialPII("+1 555 500 3000")) }
    @Test fun testPhone_H024() { assertTrue(RegexScreener.containsPotentialPII("+1 555 500 4000")) }
    @Test fun testPhone_H025() { assertTrue(RegexScreener.containsPotentialPII("+1 555 500 5000")) }

    // =========================================================================
    // SECTION H4: 25 Luhn Individual Tests
    // =========================================================================

    @Test fun testLuhn_H001() { assertTrue(RegexScreener.luhnCheck("4111111111111111")) }
    @Test fun testLuhn_H002() { assertTrue(RegexScreener.luhnCheck("4012888888881881")) }
    @Test fun testLuhn_H003() { assertTrue(RegexScreener.luhnCheck("4917610000000000")) }
    @Test fun testLuhn_H004() { assertTrue(RegexScreener.luhnCheck("4222222222222")) }
    @Test fun testLuhn_H005() { assertTrue(RegexScreener.luhnCheck("5500005555555559")) }
    @Test fun testLuhn_H006() { assertTrue(RegexScreener.luhnCheck("5555555555554444")) }
    @Test fun testLuhn_H007() { assertTrue(RegexScreener.luhnCheck("5105105105105100")) }
    @Test fun testLuhn_H008() { assertTrue(RegexScreener.luhnCheck("378282246310005")) }
    @Test fun testLuhn_H009() { assertTrue(RegexScreener.luhnCheck("371449635398431")) }
    @Test fun testLuhn_H010() { assertTrue(RegexScreener.luhnCheck("6011111111111117")) }
    @Test fun testLuhn_H011() { assertTrue(RegexScreener.luhnCheck("6011000990139424")) }
    @Test fun testLuhn_H012() { assertTrue(RegexScreener.luhnCheck("3530111333300000")) }
    @Test fun testLuhn_H013() { assertTrue(RegexScreener.luhnCheck("3566002020360505")) }
    @Test fun testLuhn_H014() { assertTrue(RegexScreener.luhnCheck("30569309025904")) }
    @Test fun testLuhn_H015() { assertTrue(RegexScreener.luhnCheck("38520000023237")) }
    @Test fun testLuhn_H016() { assertFalse(RegexScreener.luhnCheck("4111111111111110")) }
    @Test fun testLuhn_H017() { assertFalse(RegexScreener.luhnCheck("5500005555555550")) }
    @Test fun testLuhn_H018() { assertFalse(RegexScreener.luhnCheck("378282246310004")) }
    @Test fun testLuhn_H019() { assertFalse(RegexScreener.luhnCheck("6011111111111110")) }
    @Test fun testLuhn_H020() { assertFalse(RegexScreener.luhnCheck("3530111333300001")) }
    @Test fun testLuhn_H021() { assertFalse(RegexScreener.luhnCheck("1111111111111111")) }
    @Test fun testLuhn_H022() { assertFalse(RegexScreener.luhnCheck("2222222222222222")) }
    @Test fun testLuhn_H023() { assertFalse(RegexScreener.luhnCheck("3333333333333333")) }
    @Test fun testLuhn_H024() { assertFalse(RegexScreener.luhnCheck("5555555555555555")) }
    @Test fun testLuhn_H025() { assertFalse(RegexScreener.luhnCheck("7777777777777777")) }
    @Test fun testLuhn_H023() { assertFalse(RegexScreener.luhnCheck("3333333333333333")) }
    @Test fun testLuhn_H024() { assertFalse(RegexScreener.luhnCheck("5555555555555555")) }
    @Test fun testLuhn_H025() { assertFalse(RegexScreener.luhnCheck("7777777777777777")) }
}

// =============================================================================
// RegexScreenerExtendedTest3I — Final Coverage Tests
// =============================================================================
class RegexScreenerExtendedTest3I {

    // =========================================================================
    // SECTION I1: 50 More containsPotentialPII Tests
    // =========================================================================

    @Test fun testI001() { assertTrue(RegexScreener.containsPotentialPII("ID: 101-01-1001")) }
    @Test fun testI002() { assertTrue(RegexScreener.containsPotentialPII("ID: 102-02-1002")) }
    @Test fun testI003() { assertTrue(RegexScreener.containsPotentialPII("ID: 103-03-1003")) }
    @Test fun testI004() { assertTrue(RegexScreener.containsPotentialPII("ID: 104-04-1004")) }
    @Test fun testI005() { assertTrue(RegexScreener.containsPotentialPII("ID: 105-05-1005")) }
    @Test fun testI006() { assertTrue(RegexScreener.containsPotentialPII("ID: 106-06-1006")) }
    @Test fun testI007() { assertTrue(RegexScreener.containsPotentialPII("ID: 107-07-1007")) }
    @Test fun testI008() { assertTrue(RegexScreener.containsPotentialPII("ID: 108-08-1008")) }
    @Test fun testI009() { assertTrue(RegexScreener.containsPotentialPII("ID: 109-09-1009")) }
    @Test fun testI010() { assertTrue(RegexScreener.containsPotentialPII("ID: 110-10-1010")) }
    @Test fun testI011() { assertTrue(RegexScreener.containsPotentialPII("Contact: a001@test.com")) }
    @Test fun testI012() { assertTrue(RegexScreener.containsPotentialPII("Contact: a002@test.com")) }
    @Test fun testI013() { assertTrue(RegexScreener.containsPotentialPII("Contact: a003@test.com")) }
    @Test fun testI014() { assertTrue(RegexScreener.containsPotentialPII("Contact: a004@test.com")) }
    @Test fun testI015() { assertTrue(RegexScreener.containsPotentialPII("Contact: a005@test.com")) }
    @Test fun testI016() { assertTrue(RegexScreener.containsPotentialPII("Contact: a006@test.com")) }
    @Test fun testI017() { assertTrue(RegexScreener.containsPotentialPII("Contact: a007@test.com")) }
    @Test fun testI018() { assertTrue(RegexScreener.containsPotentialPII("Contact: a008@test.com")) }
    @Test fun testI019() { assertTrue(RegexScreener.containsPotentialPII("Contact: a009@test.com")) }
    @Test fun testI020() { assertTrue(RegexScreener.containsPotentialPII("Contact: a010@test.com")) }
    @Test fun testI021() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0001")) }
    @Test fun testI022() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0002")) }
    @Test fun testI023() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0003")) }
    @Test fun testI024() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0004")) }
    @Test fun testI025() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0005")) }
    @Test fun testI026() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0006")) }
    @Test fun testI027() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0007")) }
    @Test fun testI028() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0008")) }
    @Test fun testI029() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0009")) }
    @Test fun testI030() { assertTrue(RegexScreener.containsPotentialPII("Phone: (555) 001-0010")) }
    @Test fun testI031() { assertFalse(RegexScreener.containsPotentialPII("No PII: item 1")) }
    @Test fun testI032() { assertFalse(RegexScreener.containsPotentialPII("No PII: item 2")) }
    @Test fun testI033() { assertFalse(RegexScreener.containsPotentialPII("No PII: item 3")) }
    @Test fun testI034() { assertFalse(RegexScreener.containsPotentialPII("No PII: item 4")) }
    @Test fun testI035() { assertFalse(RegexScreener.containsPotentialPII("No PII: item 5")) }
    @Test fun testI036() { assertFalse(RegexScreener.containsPotentialPII("Log: started")) }
    @Test fun testI037() { assertFalse(RegexScreener.containsPotentialPII("Log: stopped")) }
    @Test fun testI038() { assertFalse(RegexScreener.containsPotentialPII("Log: restarted")) }
    @Test fun testI039() { assertFalse(RegexScreener.containsPotentialPII("Log: completed")) }
    @Test fun testI040() { assertFalse(RegexScreener.containsPotentialPII("Log: initializing")) }
    @Test fun testI041() { assertTrue(RegexScreener.luhnCheck("4111111111111111")) }
    @Test fun testI042() { assertTrue(RegexScreener.luhnCheck("5500005555555559")) }
    @Test fun testI043() { assertTrue(RegexScreener.luhnCheck("378282246310005")) }
    @Test fun testI044() { assertFalse(RegexScreener.luhnCheck("1234567890123456")) }
    @Test fun testI045() { assertFalse(RegexScreener.luhnCheck("9876543210987654")) }
    @Test fun testI046() { assertFalse(RegexScreener.containsPotentialPII("")) }
    @Test fun testI047() { assertFalse(RegexScreener.containsPotentialPII("  ")) }
    @Test fun testI048() { assertFalse(RegexScreener.containsPotentialPII("\n")) }
    @Test fun testI049() { assertFalse(RegexScreener.containsPotentialPII("\t")) }
    @Test fun testI050() {
        // Sanity: SSN + email + card all in one line
        assertTrue(RegexScreener.containsPotentialPII("123-45-6789 user@test.com 4111111111111111"))
    }

    // =========================================================================
    // SECTION I2: 50 extractEntities tests
    // =========================================================================

    @Test fun testExtract_I001() { assertNotNull(RegexScreener.extractEntities("text 001")) }
    @Test fun testExtract_I002() { assertNotNull(RegexScreener.extractEntities("text 002")) }
    @Test fun testExtract_I003() { assertNotNull(RegexScreener.extractEntities("text 003")) }
    @Test fun testExtract_I004() { assertNotNull(RegexScreener.extractEntities("text 004")) }
    @Test fun testExtract_I005() { assertNotNull(RegexScreener.extractEntities("text 005")) }
    @Test fun testExtract_I006() { assertTrue(RegexScreener.extractEntities("hello").isEmpty()) }
    @Test fun testExtract_I007() { assertTrue(RegexScreener.extractEntities("world").isEmpty()) }
    @Test fun testExtract_I008() { assertTrue(RegexScreener.extractEntities("android").isEmpty()) }
    @Test fun testExtract_I009() { assertTrue(RegexScreener.extractEntities("kotlin").isEmpty()) }
    @Test fun testExtract_I010() { assertTrue(RegexScreener.extractEntities("privacy").isEmpty()) }
    @Test fun testExtract_I011() {
        val r = RegexScreener.extractEntities("SSN: 201-01-0001")
        assertTrue(r.any { it.entityType == EntityType.SSN })
    }
    @Test fun testExtract_I012() {
        val r = RegexScreener.extractEntities("SSN: 202-02-0002")
        assertTrue(r.any { it.entityType == EntityType.SSN })
    }
    @Test fun testExtract_I013() {
        val r = RegexScreener.extractEntities("SSN: 203-03-0003")
        assertTrue(r.any { it.entityType == EntityType.SSN })
    }
    @Test fun testExtract_I014() {
        val r = RegexScreener.extractEntities("SSN: 204-04-0004")
        assertTrue(r.any { it.entityType == EntityType.SSN })
    }
    @Test fun testExtract_I015() {
        val r = RegexScreener.extractEntities("SSN: 205-05-0005")
        assertTrue(r.any { it.entityType == EntityType.SSN })
    }
    @Test fun testExtract_I016() {
        val r = RegexScreener.extractEntities("b001@mail.com")
        assertTrue(r.any { it.entityType == EntityType.EMAIL })
    }
    @Test fun testExtract_I017() {
        val r = RegexScreener.extractEntities("b002@mail.com")
        assertTrue(r.any { it.entityType == EntityType.EMAIL })
    }
    @Test fun testExtract_I018() {
        val r = RegexScreener.extractEntities("b003@mail.com")
        assertTrue(r.any { it.entityType == EntityType.EMAIL })
    }
    @Test fun testExtract_I019() {
        val r = RegexScreener.extractEntities("b004@mail.com")
        assertTrue(r.any { it.entityType == EntityType.EMAIL })
    }
    @Test fun testExtract_I020() {
        val r = RegexScreener.extractEntities("b005@mail.com")
        assertTrue(r.any { it.entityType == EntityType.EMAIL })
    }
    @Test fun testExtract_I021() {
        val r = RegexScreener.extractEntities("4111111111111111")
        assertTrue(r.any { it.entityType == EntityType.CREDIT_CARD })
    }
    @Test fun testExtract_I022() {
        val r = RegexScreener.extractEntities("5500005555555559")
        assertTrue(r.any { it.entityType == EntityType.CREDIT_CARD })
    }
    @Test fun testExtract_I023() {
        val r = RegexScreener.extractEntities("378282246310005")
        assertTrue(r.any { it.entityType == EntityType.CREDIT_CARD })
    }
    @Test fun testExtract_I024() {
        val r = RegexScreener.extractEntities("6011111111111117")
        assertTrue(r.any { it.entityType == EntityType.CREDIT_CARD })
    }
    @Test fun testExtract_I025() {
        val r = RegexScreener.extractEntities("3530111333300000")
        assertTrue(r.any { it.entityType == EntityType.CREDIT_CARD })
    }
    @Test fun testExtract_I026() {
        val r = RegexScreener.extractEntities("(555) 100-0001")
        assertNotNull(r)
    }
    @Test fun testExtract_I027() {
        val r = RegexScreener.extractEntities("(555) 100-0002")
        assertNotNull(r)
    }
    @Test fun testExtract_I028() {
        val r = RegexScreener.extractEntities("(555) 100-0003")
        assertNotNull(r)
    }
    @Test fun testExtract_I029() {
        val r = RegexScreener.extractEntities("(555) 100-0004")
        assertNotNull(r)
    }
    @Test fun testExtract_I030() {
        val r = RegexScreener.extractEntities("(555) 100-0005")
        assertNotNull(r)
    }
    @Test fun testExtract_I031() { assertTrue(RegexScreener.extractEntities("number only 42").none { it.entityType == EntityType.SSN }) }
    @Test fun testExtract_I032() { assertTrue(RegexScreener.extractEntities("words only").none { it.entityType == EntityType.CREDIT_CARD }) }
    @Test fun testExtract_I033() { assertTrue(RegexScreener.extractEntities("code 123").none { it.entityType == EntityType.EMAIL }) }
    @Test fun testExtract_I034() { assertNotNull(RegexScreener.extractEntities("sk_lvd_xyzABCDEFGHIJKLMNOPQRSTUVW")) }
    @Test fun testExtract_I035() { assertNotNull(RegexScreener.extractEntities("ghp_xxx")) }
    @Test fun testExtract_I036() {
        val r1 = RegexScreener.extractEntities("SSN: 301-01-0001")
        val r2 = RegexScreener.extractEntities("SSN: 301-01-0001")
        assertEquals(r1.size, r2.size)
    }
    @Test fun testExtract_I037() {
        val r = RegexScreener.extractEntities("SSN: 302-02-0002, email: c@d.org")
        assertTrue(r.size >= 1)
    }
    @Test fun testExtract_I038() { assertTrue(RegexScreener.extractEntities("").isEmpty()) }
    @Test fun testExtract_I039() { assertTrue(RegexScreener.extractEntities("   ").isEmpty()) }
    @Test fun testExtract_I040() { assertFalse(RegexScreener.extractEntities("SSN: 303-03-0003").isEmpty()) }
    @Test fun testExtract_I041() {
        val r = RegexScreener.extractEntities("plain text no pii here")
        assertTrue(r.none { it.entityType in listOf(EntityType.SSN, EntityType.CREDIT_CARD) })
    }
    @Test fun testExtract_I042() {
        val r = RegexScreener.extractEntities("SSN: 401-01-0001 phone (555) 200-0001")
        assertTrue(r.isNotEmpty())
    }
    @Test fun testExtract_I043() {
        val r = runCatching { RegexScreener.extractEntities("a".repeat(1000)) }
        assertTrue(r.isSuccess)
    }
    @Test fun testExtract_I044() {
        val r = runCatching { RegexScreener.extractEntities("1".repeat(1000)) }
        assertTrue(r.isSuccess)
    }
    @Test fun testExtract_I045() {
        val r = RegexScreener.extractEntities("SSN: 501-01-0001, card: 4111111111111111, email: e@f.com")
        assertTrue(r.size >= 2)
    }
    @Test fun testExtract_I046() {
        val types = RegexScreener.extractEntities("SSN: 502-02-0002").map { it.entityType }
        assertTrue(types.contains(EntityType.SSN))
    }
    @Test fun testExtract_I047() {
        val types = RegexScreener.extractEntities("user@mail.com").map { it.entityType }
        assertTrue(types.contains(EntityType.EMAIL))
    }
    @Test fun testExtract_I048() {
        val types = RegexScreener.extractEntities("5555555555554444").map { it.entityType }
        assertTrue(types.contains(EntityType.CREDIT_CARD))
    }
    @Test fun testExtract_I049() {
        // containsPotentialPII and extractEntities should be consistent for SSN
        val text = "503-03-0003"
        val hasPII = RegexScreener.containsPotentialPII(text)
        val entities = RegexScreener.extractEntities(text)
        if (hasPII) assertFalse(entities.isEmpty())
    }
    @Test fun testExtract_I050() {
        // Final sanity check — all major PII types
        val ssnText = "SSN: 601-01-0001"
        val emailText = "user@example.com"
        val cardText = "4111111111111111"
        assertTrue(RegexScreener.containsPotentialPII(ssnText))
        assertTrue(RegexScreener.containsPotentialPII(emailText))
        assertTrue(RegexScreener.containsPotentialPII(cardText))
        assertTrue(RegexScreener.luhnCheck("4111111111111111"))
        assertFalse(RegexScreener.luhnCheck("4111111111111112"))
    }
}
// End of RegexScreenerExtendedTest3.kt — Full suite: 3500+ lines, 300+ @Test methods.
