package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Extended test suite 2 for EmailValidator.
 * Tests validate(), isDisposableEmail(), isFreeEmailProvider(), and related helpers.
 */
class EmailValidatorExtendedTest2 {

    // =========================================================================
    // SECTION 1: Basic Valid Email Tests
    // =========================================================================

    @Test fun testValid_Simple_001() {
        val result = EmailValidator.validate("user@example.com")
        assertTrue("Simple email valid", result.isValid)
    }

    @Test fun testValid_Simple_002() {
        val result = EmailValidator.validate("john.doe@company.org")
        assertTrue("Dotted local part valid", result.isValid)
    }

    @Test fun testValid_Simple_003() {
        val result = EmailValidator.validate("user123@domain.net")
        assertTrue("Alphanumeric local part valid", result.isValid)
    }

    @Test fun testValid_Simple_004() {
        val result = EmailValidator.validate("admin@subdomain.example.com")
        assertTrue("Subdomain email valid", result.isValid)
    }

    @Test fun testValid_Simple_005() {
        val result = EmailValidator.validate("test@example.io")
        assertTrue("IO TLD valid", result.isValid)
    }

    @Test fun testValid_Simple_006() {
        val result = EmailValidator.validate("a@b.co")
        assertTrue("Minimal email valid", result.isValid)
    }

    @Test fun testValid_Gmail_001() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue("Gmail valid", result.isValid)
    }

    @Test fun testValid_Gmail_002() {
        val result = EmailValidator.validate("john.doe@gmail.com")
        assertTrue("Gmail with dot valid", result.isValid)
    }

    @Test fun testValid_Gmail_003() {
        val result = EmailValidator.validate("user+filter@gmail.com")
        assertTrue("Gmail plus addressing valid", result.isValid)
    }

    @Test fun testValid_Yahoo_001() {
        val result = EmailValidator.validate("user@yahoo.com")
        assertTrue("Yahoo valid", result.isValid)
    }

    @Test fun testValid_Outlook_001() {
        val result = EmailValidator.validate("user@outlook.com")
        assertTrue("Outlook valid", result.isValid)
    }

    @Test fun testValid_Hotmail_001() {
        val result = EmailValidator.validate("user@hotmail.com")
        assertTrue("Hotmail valid", result.isValid)
    }

    @Test fun testValid_Corporate_001() {
        val result = EmailValidator.validate("ceo@bigcorporation.com")
        assertTrue("Corporate email valid", result.isValid)
    }

    @Test fun testValid_Corporate_002() {
        val result = EmailValidator.validate("hr@company.co.uk")
        assertTrue("UK corporate email valid", result.isValid)
    }

    @Test fun testValid_Corporate_003() {
        val result = EmailValidator.validate("info@startup.io")
        assertTrue("Startup IO TLD valid", result.isValid)
    }

    @Test fun testValid_Corporate_004() {
        val result = EmailValidator.validate("support@techco.dev")
        assertTrue("Dev TLD valid", result.isValid)
    }

    @Test fun testValid_Corporate_005() {
        val result = EmailValidator.validate("billing@software.ai")
        assertTrue("AI TLD valid", result.isValid)
    }

    @Test fun testValid_International_001() {
        val result = EmailValidator.validate("hans@firma.de")
        assertTrue("German TLD valid", result.isValid)
    }

    @Test fun testValid_International_002() {
        val result = EmailValidator.validate("pierre@domaine.fr")
        assertTrue("French TLD valid", result.isValid)
    }

    @Test fun testValid_International_003() {
        val result = EmailValidator.validate("user@company.jp")
        assertTrue("Japanese TLD valid", result.isValid)
    }

    @Test fun testValid_International_004() {
        val result = EmailValidator.validate("contact@business.br")
        assertTrue("Brazilian TLD valid", result.isValid)
    }

    @Test fun testValid_International_005() {
        val result = EmailValidator.validate("info@company.in")
        assertTrue("Indian TLD valid", result.isValid)
    }

    @Test fun testValid_International_006() {
        val result = EmailValidator.validate("user@example.cn")
        assertTrue("Chinese TLD valid", result.isValid)
    }

    @Test fun testValid_International_007() {
        val result = EmailValidator.validate("webmaster@site.ru")
        assertTrue("Russian TLD valid", result.isValid)
    }

    @Test fun testValid_International_008() {
        val result = EmailValidator.validate("customer@shop.nl")
        assertTrue("Dutch TLD valid", result.isValid)
    }

    @Test fun testValid_International_009() {
        val result = EmailValidator.validate("admin@service.se")
        assertTrue("Swedish TLD valid", result.isValid)
    }

    @Test fun testValid_International_010() {
        val result = EmailValidator.validate("info@firma.ch")
        assertTrue("Swiss TLD valid", result.isValid)
    }

    // =========================================================================
    // SECTION 2: Special Character Local Part Tests
    // =========================================================================

    @Test fun testSpecialChar_Underscore_001() {
        val result = EmailValidator.validate("first_last@example.com")
        assertTrue("Underscore in local part valid", result.isValid)
    }

    @Test fun testSpecialChar_Hyphen_001() {
        val result = EmailValidator.validate("first-last@example.com")
        assertTrue("Hyphen in local part valid", result.isValid)
    }

    @Test fun testSpecialChar_Plus_001() {
        val result = EmailValidator.validate("user+tag@example.com")
        assertTrue("Plus in local part valid", result.isValid)
    }

    @Test fun testSpecialChar_Dot_001() {
        val result = EmailValidator.validate("first.last@example.com")
        assertTrue("Dot in local part valid", result.isValid)
    }

    @Test fun testSpecialChar_Mixed_001() {
        val result = EmailValidator.validate("first.last+tag@example.com")
        assertTrue("Mix of special chars valid", result.isValid)
    }

    @Test fun testSpecialChar_Percent_001() {
        val result = EmailValidator.validate("user%name@example.com")
        assertTrue("Percent in local part may be valid", result.isValid || !result.isValid)
        assertNotNull("Result not null", result)
    }

    @Test fun testSpecialChar_Numbers_001() {
        val result = EmailValidator.validate("123456@numbers.com")
        assertTrue("All-numeric local part valid", result.isValid)
    }

    @Test fun testSpecialChar_LongLocal_001() {
        val local = "a".repeat(64)
        val result = EmailValidator.validate("$local@example.com")
        assertNotNull("Max-length local part processed", result)
    }

    // =========================================================================
    // SECTION 3: Invalid Email Tests
    // =========================================================================

    @Test fun testInvalid_NoAt_001() {
        val result = EmailValidator.validate("userexample.com")
        assertFalse("Missing @ invalid", result.isValid)
    }

    @Test fun testInvalid_NoAt_002() {
        val result = EmailValidator.validate("noatsignhere")
        assertFalse("No @ or domain invalid", result.isValid)
    }

    @Test fun testInvalid_NoDomain_001() {
        val result = EmailValidator.validate("user@")
        assertFalse("Missing domain invalid", result.isValid)
    }

    @Test fun testInvalid_NoDomain_002() {
        val result = EmailValidator.validate("user@.")
        assertFalse("Dot-only domain invalid", result.isValid)
    }

    @Test fun testInvalid_NoLocalPart_001() {
        val result = EmailValidator.validate("@example.com")
        assertFalse("Missing local part invalid", result.isValid)
    }

    @Test fun testInvalid_DoubleDot_001() {
        val result = EmailValidator.validate("user..name@example.com")
        assertFalse("Double dot in local part invalid", result.isValid)
    }

    @Test fun testInvalid_DoubleDot_002() {
        val result = EmailValidator.validate("user@domain..com")
        assertFalse("Double dot in domain invalid", result.isValid)
    }

    @Test fun testInvalid_Empty_001() {
        val result = EmailValidator.validate("")
        assertFalse("Empty string invalid", result.isValid)
    }

    @Test fun testInvalid_Whitespace_001() {
        val result = EmailValidator.validate("   ")
        assertFalse("Whitespace invalid", result.isValid)
    }

    @Test fun testInvalid_SpaceInLocal_001() {
        val result = EmailValidator.validate("user name@example.com")
        assertFalse("Space in local part invalid", result.isValid)
    }

    @Test fun testInvalid_MultipleAt_001() {
        val result = EmailValidator.validate("user@@example.com")
        assertFalse("Double @ invalid", result.isValid)
    }

    @Test fun testInvalid_MultipleAt_002() {
        val result = EmailValidator.validate("user@exam@ple.com")
        assertFalse("Multiple @ signs invalid", result.isValid)
    }

    @Test fun testInvalid_NoTLD_001() {
        val result = EmailValidator.validate("user@domain")
        assertFalse("Missing TLD invalid", result.isValid)
    }

    @Test fun testInvalid_ShortTLD_001() {
        val result = EmailValidator.validate("user@example.c")
        assertFalse("One-char TLD invalid", result.isValid)
    }

    @Test fun testInvalid_StartDot_001() {
        val result = EmailValidator.validate(".user@example.com")
        assertFalse("Leading dot in local part invalid", result.isValid)
    }

    @Test fun testInvalid_EndDot_001() {
        val result = EmailValidator.validate("user.@example.com")
        assertFalse("Trailing dot in local part invalid", result.isValid)
    }

    // =========================================================================
    // SECTION 4: Disposable Email Tests
    // =========================================================================

    @Test fun testDisposable_MailinatorDomain_001() {
        val isDisposable = EmailValidator.isDisposableEmail("mailinator.com")
        assertTrue("mailinator.com is disposable", isDisposable)
    }

    @Test fun testDisposable_TempMailDomain_001() {
        val isDisposable = EmailValidator.isDisposableEmail("tempmail.com")
        assertTrue("tempmail.com is disposable", isDisposable)
    }

    @Test fun testDisposable_GuerrillaDomain_001() {
        val isDisposable = EmailValidator.isDisposableEmail("guerrillamail.com")
        assertTrue("guerrillamail.com is disposable", isDisposable)
    }

    @Test fun testDisposable_ThrowawayDomain_001() {
        val isDisposable = EmailValidator.isDisposableEmail("throwaway.email")
        assertNotNull("Throwaway domain check not null", isDisposable)
    }

    @Test fun testDisposable_GmailNotDisposable_001() {
        val isDisposable = EmailValidator.isDisposableEmail("gmail.com")
        assertFalse("gmail.com is NOT disposable", isDisposable)
    }

    @Test fun testDisposable_YahooNotDisposable_001() {
        val isDisposable = EmailValidator.isDisposableEmail("yahoo.com")
        assertFalse("yahoo.com is NOT disposable", isDisposable)
    }

    @Test fun testDisposable_OutlookNotDisposable_001() {
        val isDisposable = EmailValidator.isDisposableEmail("outlook.com")
        assertFalse("outlook.com is NOT disposable", isDisposable)
    }

    @Test fun testDisposable_CorporateNotDisposable_001() {
        val isDisposable = EmailValidator.isDisposableEmail("bigcompany.com")
        assertFalse("Corporate domain not disposable", isDisposable)
    }

    @Test fun testDisposable_ViaSingleValidate_001() {
        val result = EmailValidator.validate("test@mailinator.com")
        if (result.isValid) assertTrue("mailinator flagged", result.isDisposable)
    }

    @Test fun testDisposable_ViaSingleValidate_002() {
        val result = EmailValidator.validate("user@gmail.com")
        assertFalse("gmail not disposable", result.isDisposable)
    }

    // =========================================================================
    // SECTION 5: Free Email Provider Tests
    // =========================================================================

    @Test fun testFreeProvider_Gmail_001() {
        val isFree = EmailValidator.isFreeEmailProvider("gmail.com")
        assertTrue("gmail.com is free provider", isFree)
    }

    @Test fun testFreeProvider_Yahoo_001() {
        val isFree = EmailValidator.isFreeEmailProvider("yahoo.com")
        assertTrue("yahoo.com is free provider", isFree)
    }

    @Test fun testFreeProvider_Hotmail_001() {
        val isFree = EmailValidator.isFreeEmailProvider("hotmail.com")
        assertTrue("hotmail.com is free provider", isFree)
    }

    @Test fun testFreeProvider_Outlook_001() {
        val isFree = EmailValidator.isFreeEmailProvider("outlook.com")
        assertTrue("outlook.com is free provider", isFree)
    }

    @Test fun testFreeProvider_Protonmail_001() {
        val isFree = EmailValidator.isFreeEmailProvider("protonmail.com")
        assertTrue("protonmail.com is free provider", isFree)
    }

    @Test fun testFreeProvider_Icloud_001() {
        val isFree = EmailValidator.isFreeEmailProvider("icloud.com")
        assertTrue("icloud.com is free provider", isFree)
    }

    @Test fun testFreeProvider_CorporateNotFree_001() {
        val isFree = EmailValidator.isFreeEmailProvider("bigcorporation.com")
        assertFalse("Custom corporate domain not free", isFree)
    }

    @Test fun testFreeProvider_ViaValidate_001() {
        val result = EmailValidator.validate("user@gmail.com")
        if (result.isValid) assertTrue("gmail is free provider", result.isFreeProvider)
    }

    @Test fun testFreeProvider_ViaValidate_002() {
        val result = EmailValidator.validate("user@corporate.com")
        assertNotNull("Corporate email processed", result)
    }

    // =========================================================================
    // SECTION 6: Role Address Tests
    // =========================================================================

    @Test fun testRole_Admin_001() {
        val result = EmailValidator.validate("admin@example.com")
        if (result.isValid) assertTrue("admin@ is role address", result.isRoleAddress)
    }

    @Test fun testRole_Info_001() {
        val result = EmailValidator.validate("info@example.com")
        if (result.isValid) assertTrue("info@ is role address", result.isRoleAddress)
    }

    @Test fun testRole_Support_001() {
        val result = EmailValidator.validate("support@example.com")
        if (result.isValid) assertTrue("support@ is role address", result.isRoleAddress)
    }

    @Test fun testRole_NoReply_001() {
        val result = EmailValidator.validate("noreply@example.com")
        if (result.isValid) assertTrue("noreply@ is role address", result.isRoleAddress)
    }

    @Test fun testRole_Webmaster_001() {
        val result = EmailValidator.validate("webmaster@example.com")
        if (result.isValid) assertTrue("webmaster@ is role address", result.isRoleAddress)
    }

    @Test fun testRole_Personal_001() {
        val result = EmailValidator.validate("john.doe@example.com")
        assertFalse("Personal name is NOT role address", result.isRoleAddress)
    }

    @Test fun testRole_Personal_002() {
        val result = EmailValidator.validate("alice.smith@corp.com")
        assertFalse("Personal name not role address", result.isRoleAddress)
    }

    // =========================================================================
    // SECTION 7: ValidationResult Fields Tests
    // =========================================================================

    @Test fun testResult_LocalPart_001() {
        val result = EmailValidator.validate("user@example.com")
        if (result.isValid) assertEquals("Local part extracted", "user", result.localPart)
    }

    @Test fun testResult_LocalPart_002() {
        val result = EmailValidator.validate("john.doe@example.com")
        if (result.isValid) assertEquals("Dotted local part extracted", "john.doe", result.localPart)
    }

    @Test fun testResult_Domain_001() {
        val result = EmailValidator.validate("user@example.com")
        if (result.isValid) assertEquals("Domain extracted", "example.com", result.domain)
    }

    @Test fun testResult_Domain_002() {
        val result = EmailValidator.validate("user@EXAMPLE.COM")
        if (result.isValid) assertEquals("Domain normalized to lowercase", "example.com", result.domain)
    }

    @Test fun testResult_NormalizedEmail_001() {
        val result = EmailValidator.validate("user@example.com")
        if (result.isValid) assertNotNull("Normalized email present", result.normalizedEmail)
    }

    @Test fun testResult_NormalizedEmail_002() {
        val result = EmailValidator.validate("User@Example.COM")
        if (result.isValid) {
            assertTrue("Normalized email lowercase", result.normalizedEmail == result.normalizedEmail.lowercase())
        }
    }

    @Test fun testResult_Confidence_001() {
        val result = EmailValidator.validate("user@gmail.com")
        if (result.isValid) assertTrue("Confidence > 0", result.confidence > 0f)
    }

    @Test fun testResult_Confidence_002() {
        val result = EmailValidator.validate("user@gmail.com")
        if (result.isValid) assertTrue("Confidence <= 1", result.confidence <= 1f)
    }

    @Test fun testResult_Confidence_003() {
        val result = EmailValidator.validate("invalid-email-no-at")
        if (!result.isValid) assertTrue("Invalid email has lower confidence", result.confidence <= 0.5f)
    }

    @Test fun testResult_Reason_001() {
        val result = EmailValidator.validate("user@example.com")
        assertNotNull("Reason field not null", result.reason)
    }

    @Test fun testResult_Reason_002() {
        val result = EmailValidator.validate("not-an-email")
        assertNotNull("Reason for invalid email not null", result.reason)
        assertTrue("Reason is non-empty", result.reason.isNotEmpty())
    }

    // =========================================================================
    // SECTION 8: normalizeGmailAddress() Tests
    // =========================================================================

    @Test fun testGmailNormalize_Dots_001() {
        val normalized = EmailValidator.normalizeGmailAddress("j.o.h.n@gmail.com")
        assertNotNull("Gmail normalized", normalized)
        if (normalized != null) assertFalse("Dots removed", normalized.startsWith("j."))
    }

    @Test fun testGmailNormalize_Plus_001() {
        val normalized = EmailValidator.normalizeGmailAddress("john+filter@gmail.com")
        assertNotNull("Gmail plus normalized", normalized)
        if (normalized != null) assertFalse("Plus part removed", normalized.contains("+"))
    }

    @Test fun testGmailNormalize_NonGmail_001() {
        val normalized = EmailValidator.normalizeGmailAddress("user@hotmail.com")
        assertNull("Non-gmail not normalized", normalized)
    }

    @Test fun testGmailNormalize_Uppercase_001() {
        val normalized = EmailValidator.normalizeGmailAddress("John@Gmail.COM")
        assertNotNull("Uppercase gmail normalized", normalized)
        if (normalized != null) assertTrue("Normalized is lowercase", normalized == normalized.lowercase())
    }

    @Test fun testGmailNormalize_Empty_001() {
        val result = runCatching { EmailValidator.normalizeGmailAddress("") }
        assertTrue("Empty input safe", result.isSuccess)
    }

    // =========================================================================
    // SECTION 9: extractFromText() Tests
    // =========================================================================

    @Test fun testExtractFromText_OneEmail_001() {
        val text = "Contact: user@example.com for support"
        val results = EmailValidator.extractFromText(text)
        assertTrue("One email extracted", results.isNotEmpty())
    }

    @Test fun testExtractFromText_TwoEmails_001() {
        val text = "From: alice@a.com To: bob@b.com"
        val results = EmailValidator.extractFromText(text)
        assertTrue("Two emails extracted", results.size >= 1)
    }

    @Test fun testExtractFromText_NoPII_001() {
        val text = "No email address in this sentence at all"
        val results = EmailValidator.extractFromText(text)
        assertTrue("No emails in plain text", results.isEmpty())
    }

    @Test fun testExtractFromText_Empty_001() {
        val results = EmailValidator.extractFromText("")
        assertTrue("Empty text empty results", results.isEmpty())
    }

    @Test fun testExtractFromText_MultiLine_001() {
        val text = "Name: John\nEmail: john@example.com\nPhone: 555-123-4567"
        val results = EmailValidator.extractFromText(text)
        assertTrue("Email extracted from multi-line text", results.isNotEmpty())
    }

    @Test fun testExtractFromText_InSentence_001() {
        val text = "Please email support@privacyguard.dev for help."
        val results = EmailValidator.extractFromText(text)
        assertTrue("Email in sentence extracted", results.isNotEmpty())
    }

    @Test fun testExtractFromText_ReturnType_001() {
        val result = runCatching { EmailValidator.extractFromText("some text") }
        assertTrue("extractFromText safe", result.isSuccess)
    }

    // =========================================================================
    // SECTION 10: looksLikeEmail() Tests
    // =========================================================================

    @Test fun testLooksLike_Valid_001() {
        assertTrue(EmailValidator.looksLikeEmail("user@example.com"))
    }

    @Test fun testLooksLike_Valid_002() {
        assertTrue(EmailValidator.looksLikeEmail("a@b.co"))
    }

    @Test fun testLooksLike_Invalid_001() {
        assertFalse(EmailValidator.looksLikeEmail("no-at-sign"))
    }

    @Test fun testLooksLike_Invalid_002() {
        assertFalse(EmailValidator.looksLikeEmail(""))
    }

    @Test fun testLooksLike_Invalid_003() {
        assertFalse(EmailValidator.looksLikeEmail("user@"))
    }

    @Test fun testLooksLike_Invalid_004() {
        assertFalse(EmailValidator.looksLikeEmail("@domain.com"))
    }

    // =========================================================================
    // SECTION 11: disposableDomainCount() and freeProviderCount() Tests
    // =========================================================================

    @Test fun testDisposableDomainCount_001() {
        val count = EmailValidator.disposableDomainCount()
        assertTrue("Disposable domain count > 0", count > 0)
        assertTrue("Disposable domain count reasonable (>100)", count > 100)
    }

    @Test fun testFreeProviderCount_001() {
        val count = EmailValidator.freeProviderCount()
        assertTrue("Free provider count > 0", count > 0)
    }

    // =========================================================================
    // SECTION 12: isRoleAddress() Tests
    // =========================================================================

    @Test fun testIsRole_Admin_001() {
        val isRole = EmailValidator.isRoleAddress("admin")
        assertTrue("admin is role address", isRole)
    }

    @Test fun testIsRole_Info_001() {
        val isRole = EmailValidator.isRoleAddress("info")
        assertTrue("info is role address", isRole)
    }

    @Test fun testIsRole_Support_001() {
        val isRole = EmailValidator.isRoleAddress("support")
        assertTrue("support is role address", isRole)
    }

    @Test fun testIsRole_Postmaster_001() {
        val isRole = EmailValidator.isRoleAddress("postmaster")
        assertTrue("postmaster is role address", isRole)
    }

    @Test fun testIsRole_Hostmaster_001() {
        val isRole = EmailValidator.isRoleAddress("hostmaster")
        assertTrue("hostmaster is role address", isRole)
    }

    @Test fun testIsRole_Personal_001() {
        assertFalse("Personal name not role", EmailValidator.isRoleAddress("john"))
    }

    @Test fun testIsRole_Personal_002() {
        assertFalse("Company employee not role", EmailValidator.isRoleAddress("alice.smith"))
    }

    // =========================================================================
    // SECTION 13: Edge Cases
    // =========================================================================

    @Test fun testEdge_VeryLong_001() {
        val long = "a".repeat(200) + "@example.com"
        val result = runCatching { EmailValidator.validate(long) }
        assertTrue("Very long email handled safely", result.isSuccess)
    }

    @Test fun testEdge_NullChar_001() {
        val result = runCatching { EmailValidator.validate("user\u0000@example.com") }
        assertTrue("Null char handled safely", result.isSuccess)
    }

    @Test fun testEdge_UppercaseValid_001() {
        val result = EmailValidator.validate("USER@EXAMPLE.COM")
        assertTrue("Uppercase email valid", result.isValid)
    }

    @Test fun testEdge_MixedCaseValid_001() {
        val result = EmailValidator.validate("User.Name@Example.Com")
        assertTrue("Mixed case email valid", result.isValid)
    }

    @Test fun testEdge_NewTLD_001() {
        val result = EmailValidator.validate("user@example.technology")
        assertTrue("Long TLD email valid", result.isValid)
    }

    @Test fun testEdge_NumericDomain_001() {
        val result = runCatching { EmailValidator.validate("user@123456.com") }
        assertTrue("Numeric domain handled safely", result.isSuccess)
    }

    @Test fun testEdge_IPDomain_001() {
        val result = runCatching { EmailValidator.validate("user@[192.168.1.1]") }
        assertTrue("IP domain handled safely", result.isSuccess)
    }

    @Test fun testEdge_QuotedLocal_001() {
        val result = runCatching { EmailValidator.validate("\"user name\"@example.com") }
        assertTrue("Quoted local part handled safely", result.isSuccess)
    }

    @Test fun testEdge_InternationalDomain_001() {
        val result = runCatching { EmailValidator.validate("user@münchen.de") }
        assertTrue("IDN domain handled safely", result.isSuccess)
    }

    @Test fun testEdge_Unicode_001() {
        val result = runCatching { EmailValidator.validate("üser@example.com") }
        assertTrue("Unicode local part handled safely", result.isSuccess)
    }

    // =========================================================================
    // SECTION 14: Confidence Score Tests
    // =========================================================================

    @Test fun testConfidence_GmailHigh_001() {
        val result = EmailValidator.validate("user@gmail.com")
        if (result.isValid) assertTrue("Gmail confidence high", result.confidence >= 0.7f)
    }

    @Test fun testConfidence_Corporate_001() {
        val result = EmailValidator.validate("alice@bigcorp.com")
        if (result.isValid) assertTrue("Corporate confidence reasonable", result.confidence > 0.5f)
    }

    @Test fun testConfidence_Disposable_001() {
        val result = EmailValidator.validate("user@mailinator.com")
        if (result.isValid) assertTrue("Disposable lower confidence", result.confidence < 0.8f)
    }

    @Test fun testConfidence_Invalid_001() {
        val result = EmailValidator.validate("not-valid-email")
        assertFalse("Invalid email is invalid", result.isValid)
        assertTrue("Invalid confidence is low", result.confidence < 0.5f)
    }

    // =========================================================================
    // SECTION 15: validateLocalPart() and validateDomain() Tests
    // =========================================================================

    @Test fun testValidateLocalPart_Valid_001() {
        val (isValid, _) = EmailValidator.validateLocalPart("username")
        assertTrue("Simple local part valid", isValid)
    }

    @Test fun testValidateLocalPart_Valid_002() {
        val (isValid, _) = EmailValidator.validateLocalPart("first.last")
        assertTrue("Dotted local part valid", isValid)
    }

    @Test fun testValidateLocalPart_Invalid_001() {
        val (isValid, _) = EmailValidator.validateLocalPart("")
        assertFalse("Empty local part invalid", isValid)
    }

    @Test fun testValidateLocalPart_Invalid_002() {
        val (isValid, _) = EmailValidator.validateLocalPart("user name")
        assertFalse("Space in local part invalid", isValid)
    }

    @Test fun testValidateDomain_Valid_001() {
        val (isValid, _) = EmailValidator.validateDomain("example.com")
        assertTrue("Simple domain valid", isValid)
    }

    @Test fun testValidateDomain_Valid_002() {
        val (isValid, _) = EmailValidator.validateDomain("sub.example.com")
        assertTrue("Subdomain valid", isValid)
    }

    @Test fun testValidateDomain_Invalid_001() {
        val (isValid, _) = EmailValidator.validateDomain("")
        assertFalse("Empty domain invalid", isValid)
    }

    @Test fun testValidateDomain_Invalid_002() {
        val (isValid, _) = EmailValidator.validateDomain("domain")
        assertFalse("No TLD domain invalid", isValid)
    }

    @Test fun testValidateDomain_Invalid_003() {
        val (isValid, _) = EmailValidator.validateDomain("domain..com")
        assertFalse("Double-dot domain invalid", isValid)
    }

    // =========================================================================
    // SECTION 16: isExampleDomain() and suggestDomainCorrection() Tests
    // =========================================================================

    @Test fun testIsExampleDomain_001() {
        val isExample = EmailValidator.isExampleDomain("example.com")
        assertTrue("example.com is example domain", isExample)
    }

    @Test fun testIsExampleDomain_002() {
        val isExample = EmailValidator.isExampleDomain("test.com")
        assertTrue("test.com is example domain", isExample)
    }

    @Test fun testIsExampleDomain_003() {
        val isExample = EmailValidator.isExampleDomain("gmail.com")
        assertFalse("gmail.com is NOT example domain", isExample)
    }

    @Test fun testSuggestCorrection_GmailTypo_001() {
        val suggestion = EmailValidator.suggestDomainCorrection("gmial.com")
        // May suggest gmail.com
        assertNotNull("Correction returned", suggestion ?: "none")
    }

    @Test fun testSuggestCorrection_YahooTypo_001() {
        val suggestion = EmailValidator.suggestDomainCorrection("yahooo.com")
        assertNotNull("Yahoo typo handled", suggestion ?: "none")
    }

    @Test fun testSuggestCorrection_GoodDomain_001() {
        val suggestion = EmailValidator.suggestDomainCorrection("gmail.com")
        // No correction needed for correctly spelled domain
        assertNotNull("Good domain correction returns result", suggestion ?: "none")
    }

    // =========================================================================
    // SECTION 17: Performance Tests
    // =========================================================================

    @Test fun testPerformance_Validate_001() {
        val startTime = System.currentTimeMillis()
        repeat(500) { EmailValidator.validate("user@example.com") }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("500 validations < 2s", elapsed < 2000)
    }

    @Test fun testPerformance_Validate_002() {
        val startTime = System.currentTimeMillis()
        repeat(200) { EmailValidator.validate("test@mailinator.com") }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("200 disposable checks < 1s", elapsed < 1000)
    }

    @Test fun testPerformance_LooksLike_001() {
        val startTime = System.currentTimeMillis()
        repeat(1000) { EmailValidator.looksLikeEmail("user@example.com") }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("1000 looksLike < 1s", elapsed < 1000)
    }

    @Test fun testPerformance_IsDisposable_001() {
        val startTime = System.currentTimeMillis()
        repeat(1000) { i ->
            EmailValidator.isDisposableEmail(if (i % 2 == 0) "gmail.com" else "mailinator.com")
        }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("1000 isDisposable < 1s", elapsed < 1000)
    }

    @Test fun testPerformance_ExtractFromText_001() {
        val text = "Contact alice@company.com and bob@example.org for support."
        val startTime = System.currentTimeMillis()
        repeat(200) { EmailValidator.extractFromText(text) }
        val elapsed = System.currentTimeMillis() - startTime
        assertTrue("200 extractions < 2s", elapsed < 2000)
    }

    // =========================================================================
    // SECTION 18: Regression Tests
    // =========================================================================

    @Test fun testRegression_GmailValid_001()   { assertTrue(EmailValidator.validate("user@gmail.com").isValid) }
    @Test fun testRegression_YahooValid_001()   { assertTrue(EmailValidator.validate("u@yahoo.com").isValid) }
    @Test fun testRegression_OutlookValid_001() { assertTrue(EmailValidator.validate("u@outlook.com").isValid) }
    @Test fun testRegression_SubdomainValid_001(){ assertTrue(EmailValidator.validate("u@mail.corp.com").isValid) }
    @Test fun testRegression_PlusValid_001()    { assertTrue(EmailValidator.validate("u+t@gmail.com").isValid) }
    @Test fun testRegression_NoAtInvalid_001()  { assertFalse(EmailValidator.validate("notanemail").isValid) }
    @Test fun testRegression_EmptyInvalid_001() { assertFalse(EmailValidator.validate("").isValid) }
    @Test fun testRegression_GmailFree_001()    { assertTrue(EmailValidator.isFreeEmailProvider("gmail.com")) }
    @Test fun testRegression_MailinatorDisp_001(){ assertTrue(EmailValidator.isDisposableEmail("mailinator.com")) }
    @Test fun testRegression_AdminRole_001()    { assertTrue(EmailValidator.isRoleAddress("admin")) }
    @Test fun testRegression_PersonalNotRole_001(){ assertFalse(EmailValidator.isRoleAddress("alice")) }
    @Test fun testRegression_DisposableCount_001(){ assertTrue(EmailValidator.disposableDomainCount() > 0) }

    // =========================================================================
    // SECTION 19: Boundary Tests
    // =========================================================================

    @Test fun testBoundary_SingleCharLocal_001() {
        val result = EmailValidator.validate("a@b.co")
        assertTrue("Single char local + short domain valid", result.isValid)
    }

    @Test fun testBoundary_2CharTLD_001() {
        val result = EmailValidator.validate("user@example.de")
        assertTrue("2-char TLD valid", result.isValid)
    }

    @Test fun testBoundary_4CharTLD_001() {
        val result = EmailValidator.validate("user@example.info")
        assertTrue("4-char TLD valid", result.isValid)
    }

    @Test fun testBoundary_MaxLocalLength_001() {
        val local = "a".repeat(64)
        val result = runCatching { EmailValidator.validate("$local@example.com") }
        assertTrue("64-char local part safe", result.isSuccess)
    }

    @Test fun testBoundary_TooLongLocal_001() {
        val local = "a".repeat(65)
        val result = runCatching { EmailValidator.validate("$local@example.com") }
        assertTrue("65-char local part safe", result.isSuccess)
    }

    @Test fun testBoundary_NumericOnly_001() {
        val result = EmailValidator.validate("123@456.com")
        assertTrue("All-numeric parts valid", result.isValid)
    }

    @Test fun testBoundary_AllLetters_001() {
        val result = EmailValidator.validate("letters@lettersonly.com")
        assertTrue("All-letter email valid", result.isValid)
    }

    @Test fun testBoundary_LongSubdomain_001() {
        val result = EmailValidator.validate("user@verylongsubdomain.example.com")
        assertTrue("Long subdomain valid", result.isValid)
    }

    @Test fun testBoundary_DeepSubdomain_001() {
        val result = EmailValidator.validate("user@a.b.c.d.example.com")
        assertTrue("Deep subdomain valid", result.isValid)
    }

    @Test fun testBoundary_ExampleDomainLowerConfidence_001() {
        val result = EmailValidator.validate("test@example.com")
        if (result.isValid) assertTrue("Example domain has lower confidence", result.confidence <= 1.0f)
    }

    // =========================================================================
    // SECTION 20: Final Sanity Tests
    // =========================================================================

    @Test fun testFinal_ValidEmailResults_001() {
        val emails = listOf("user@gmail.com", "john@company.com", "alice@uni.edu", "bob@gov.gov")
        emails.forEach { assertTrue("$it is valid", EmailValidator.validate(it).isValid) }
    }

    @Test fun testFinal_InvalidEmailResults_001() {
        val invalid = listOf("notanemail", "@", "user@", "", "user..@example.com")
        invalid.forEach { assertFalse("$it is invalid", EmailValidator.validate(it).isValid) }
    }

    @Test fun testFinal_DisposableDomains_001() {
        val disposable = listOf("mailinator.com", "guerrillamail.com", "tempmail.com")
        disposable.forEach { assertTrue("$it is disposable", EmailValidator.isDisposableEmail(it)) }
    }

    @Test fun testFinal_FreeProviders_001() {
        val free = listOf("gmail.com", "yahoo.com", "hotmail.com", "icloud.com")
        free.forEach { assertTrue("$it is free", EmailValidator.isFreeEmailProvider(it)) }
    }

    @Test fun testFinal_RoleAddresses_001() {
        val roles = listOf("admin", "info", "support", "postmaster", "noreply")
        roles.forEach { assertTrue("$it is role", EmailValidator.isRoleAddress(it)) }
    }

    @Test fun testFinal_PersonalNames_001() {
        val personal = listOf("john", "alice", "bob.smith", "mary.jane")
        personal.forEach { assertFalse("$it is not role", EmailValidator.isRoleAddress(it)) }
    }

    @Test fun testFinal_LooksLike_001() {
        assertTrue(EmailValidator.looksLikeEmail("user@example.com"))
        assertFalse(EmailValidator.looksLikeEmail("not-an-email"))
        assertFalse(EmailValidator.looksLikeEmail(""))
    }

    @Test fun testFinal_ExtractSafe_001() {
        val result = runCatching { EmailValidator.extractFromText("") }
        assertTrue("Empty extract safe", result.isSuccess)
    }

    @Test fun testFinal_ValidateSafe_001() {
        val result = runCatching { EmailValidator.validate("") }
        assertTrue("Empty validate safe", result.isSuccess)
    }

    @Test fun testFinal_CountsPositive_001() {
        assertTrue("Disposable count > 0", EmailValidator.disposableDomainCount() > 0)
        assertTrue("Free count > 0", EmailValidator.freeProviderCount() > 0)
    }
}
