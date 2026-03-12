package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

class EmailValidatorTest {

    // ========================================================================
    // SECTION 1: Basic Valid Email Tests
    // ========================================================================

    @Test
    fun `validate simple email is valid`() {
        val result = EmailValidator.validate("user@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with numbers in local part`() {
        val result = EmailValidator.validate("user123@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with dots in local part`() {
        val result = EmailValidator.validate("first.last@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with hyphens in local part`() {
        val result = EmailValidator.validate("first-last@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with underscores in local part`() {
        val result = EmailValidator.validate("first_last@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with plus addressing`() {
        val result = EmailValidator.validate("user+tag@example.com")
        assertTrue(result.isValid)
        assertTrue(result.hasSubAddress)
        assertEquals("tag", result.subAddress)
    }

    @Test
    fun `validate email with percent sign`() {
        val result = EmailValidator.validate("user%tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with exclamation mark`() {
        val result = EmailValidator.validate("user!tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with hash sign`() {
        val result = EmailValidator.validate("user#tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with dollar sign`() {
        val result = EmailValidator.validate("user$@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with ampersand`() {
        val result = EmailValidator.validate("user&tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with apostrophe`() {
        val result = EmailValidator.validate("user'tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with asterisk`() {
        val result = EmailValidator.validate("user*tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with equals sign`() {
        val result = EmailValidator.validate("user=tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with caret`() {
        val result = EmailValidator.validate("user^tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with backtick`() {
        val result = EmailValidator.validate("user\`tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with curly braces`() {
        val result = EmailValidator.validate("user{tag}@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with pipe`() {
        val result = EmailValidator.validate("user|tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with tilde`() {
        val result = EmailValidator.validate("user~tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with slash`() {
        val result = EmailValidator.validate("user/tag@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with question mark`() {
        val result = EmailValidator.validate("user?tag@example.com")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 2: Invalid Email Tests
    // ========================================================================

    @Test
    fun `validate empty string is invalid`() {
        val result = EmailValidator.validate("")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("Empty"))
    }

    @Test
    fun `validate whitespace only is invalid`() {
        val result = EmailValidator.validate("   ")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate missing at sign is invalid`() {
        val result = EmailValidator.validate("userexample.com")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("@"))
    }

    @Test
    fun `validate multiple at signs is invalid`() {
        val result = EmailValidator.validate("user@@example.com")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("Multiple @"))
    }

    @Test
    fun `validate at sign at start is invalid`() {
        val result = EmailValidator.validate("@example.com")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate at sign at end is invalid`() {
        val result = EmailValidator.validate("user@")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate just at sign is invalid`() {
        val result = EmailValidator.validate("@")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate space in local part is invalid`() {
        val result = EmailValidator.validate("us er@example.com")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate no domain is invalid`() {
        val result = EmailValidator.validate("user@")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate domain without dot is invalid`() {
        val result = EmailValidator.validate("user@localhost")
        // validateDomain requires at least two parts
        assertFalse(result.isValid)
    }

    @Test
    fun `validate domain with only dot is invalid`() {
        val result = EmailValidator.validate("user@.")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate domain starting with dot is invalid`() {
        val result = EmailValidator.validate("user@.example.com")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate domain ending with dot is invalid`() {
        val result = EmailValidator.validate("user@example.com.")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate double dot in domain is invalid`() {
        val result = EmailValidator.validate("user@example..com")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate numeric TLD is invalid`() {
        val result = EmailValidator.validate("user@example.123")
        assertFalse(result.isValid)
    }

    // ========================================================================
    // SECTION 3: Local Part Edge Cases (RFC 5322)
    // ========================================================================

    @Test
    fun `validate local part starting with dot is invalid`() {
        val result = EmailValidator.validate(".user@example.com")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("dot"))
    }

    @Test
    fun `validate local part ending with dot is invalid`() {
        val result = EmailValidator.validate("user.@example.com")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("dot"))
    }

    @Test
    fun `validate local part with consecutive dots is invalid`() {
        val result = EmailValidator.validate("user..name@example.com")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("consecutive dots"))
    }

    @Test
    fun `validate empty local part is invalid`() {
        val result = EmailValidator.validate("@example.com")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate local part at max length 64 is valid`() {
        val localPart = "a".repeat(64)
        val result = EmailValidator.validate("$localPart@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate local part exceeding 64 characters is invalid`() {
        val localPart = "a".repeat(65)
        val result = EmailValidator.validate("$localPart@example.com")
        assertFalse(result.isValid)
        assertTrue(result.reason.contains("maximum length"))
    }

    @Test
    fun `validate single character local part is valid`() {
        val result = EmailValidator.validate("a@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate numeric only local part is valid`() {
        val result = EmailValidator.validate("12345@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate quoted local part with spaces is valid`() {
        val result = EmailValidator.validate("\"user name\"@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate quoted local part with at sign is valid`() {
        val result = EmailValidator.validate("\"user@name\"@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate quoted local part with dots is valid`() {
        val result = EmailValidator.validate("\"user..name\"@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate quoted empty string is valid`() {
        val result = EmailValidator.validate("\"\"@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate quoted local part with escaped quote is valid`() {
        val result = EmailValidator.validate("\"user\\\"name\"@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate quoted local part with trailing backslash is invalid`() {
        val result = EmailValidator.validate("\"user\\\"@example.com")
        assertFalse(result.isValid)
    }

    // ========================================================================
    // SECTION 4: Domain Validation Tests
    // ========================================================================

    @Test
    fun `validateDomain simple domain is valid`() {
        val result = EmailValidator.validateDomain("example.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain subdomain is valid`() {
        val result = EmailValidator.validateDomain("sub.example.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain multiple subdomains is valid`() {
        val result = EmailValidator.validateDomain("a.b.c.example.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain with hyphen in label is valid`() {
        val result = EmailValidator.validateDomain("my-domain.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain with numbers is valid`() {
        val result = EmailValidator.validateDomain("domain123.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain empty string is invalid`() {
        val result = EmailValidator.validateDomain("")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain single label is invalid`() {
        val result = EmailValidator.validateDomain("localhost")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain label starting with hyphen is invalid`() {
        val result = EmailValidator.validateDomain("-example.com")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain label ending with hyphen is invalid`() {
        val result = EmailValidator.validateDomain("example-.com")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain label exceeding 63 characters is invalid`() {
        val longLabel = "a".repeat(64)
        val result = EmailValidator.validateDomain("$longLabel.com")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain label at 63 characters is valid`() {
        val label63 = "a".repeat(63)
        val result = EmailValidator.validateDomain("$label63.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain domain exceeding 253 characters is invalid`() {
        val longDomain = ("a".repeat(50) + ".").repeat(6) + "com"
        val result = EmailValidator.validateDomain(longDomain)
        // Total length would be over 253
        if (longDomain.length > 253) {
            assertFalse(result.first)
        }
    }

    @Test
    fun `validateDomain consecutive dots is invalid`() {
        val result = EmailValidator.validateDomain("example..com")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain numeric TLD is invalid`() {
        val result = EmailValidator.validateDomain("example.123")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain underscore in label is invalid`() {
        val result = EmailValidator.validateDomain("my_domain.com")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain IP literal IPv4 is valid`() {
        val result = EmailValidator.validateDomain("[192.168.1.1]")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain IP literal IPv6 is valid`() {
        val result = EmailValidator.validateDomain("[IPv6:2001:db8::1]")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain invalid IP literal`() {
        val result = EmailValidator.validateDomain("[999.999.999.999]")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain dot at start is invalid`() {
        val result = EmailValidator.validateDomain(".example.com")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain dot at end is invalid`() {
        val result = EmailValidator.validateDomain("example.com.")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain with country code TLD is valid`() {
        val result = EmailValidator.validateDomain("example.co.uk")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain with long TLD is valid`() {
        val result = EmailValidator.validateDomain("example.technology")
        assertTrue(result.first)
    }

    // ========================================================================
    // SECTION 5: Disposable Email Detection Tests
    // ========================================================================

    @Test
    fun `isDisposableEmail returns true for mailinator`() {
        assertTrue(EmailValidator.isDisposableEmail("mailinator.com"))
    }

    @Test
    fun `isDisposableEmail returns true for guerrillamail`() {
        assertTrue(EmailValidator.isDisposableEmail("guerrillamail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for tempmail`() {
        assertTrue(EmailValidator.isDisposableEmail("tempmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for throwaway email`() {
        assertTrue(EmailValidator.isDisposableEmail("throwaway.email"))
    }

    @Test
    fun `isDisposableEmail returns true for yopmail`() {
        assertTrue(EmailValidator.isDisposableEmail("yopmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for 10minutemail`() {
        assertTrue(EmailValidator.isDisposableEmail("10minutemail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for maildrop`() {
        assertTrue(EmailValidator.isDisposableEmail("maildrop.cc"))
    }

    @Test
    fun `isDisposableEmail returns true for trashmail`() {
        assertTrue(EmailValidator.isDisposableEmail("trashmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for fakeinbox`() {
        assertTrue(EmailValidator.isDisposableEmail("fakeinbox.com"))
    }

    @Test
    fun `isDisposableEmail returns true for discard email`() {
        assertTrue(EmailValidator.isDisposableEmail("discard.email"))
    }

    @Test
    fun `isDisposableEmail returns true for spamgourmet`() {
        assertTrue(EmailValidator.isDisposableEmail("spamgourmet.com"))
    }

    @Test
    fun `isDisposableEmail returns true for getairmail`() {
        assertTrue(EmailValidator.isDisposableEmail("getairmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for mailexpire`() {
        assertTrue(EmailValidator.isDisposableEmail("mailexpire.com"))
    }

    @Test
    fun `isDisposableEmail returns true for burner kiwi`() {
        assertTrue(EmailValidator.isDisposableEmail("burner.kiwi"))
    }

    @Test
    fun `isDisposableEmail returns true for mohmal`() {
        assertTrue(EmailValidator.isDisposableEmail("mohmal.com"))
    }

    @Test
    fun `isDisposableEmail returns false for gmail`() {
        assertFalse(EmailValidator.isDisposableEmail("gmail.com"))
    }

    @Test
    fun `isDisposableEmail returns false for yahoo`() {
        assertFalse(EmailValidator.isDisposableEmail("yahoo.com"))
    }

    @Test
    fun `isDisposableEmail returns false for outlook`() {
        assertFalse(EmailValidator.isDisposableEmail("outlook.com"))
    }

    @Test
    fun `isDisposableEmail returns false for custom domain`() {
        assertFalse(EmailValidator.isDisposableEmail("company.com"))
    }

    @Test
    fun `isDisposableEmail is case insensitive`() {
        assertTrue(EmailValidator.isDisposableEmail("MAILINATOR.COM"))
    }

    @Test
    fun `validate disposable email has isDisposable flag`() {
        val result = EmailValidator.validate("test@mailinator.com")
        assertTrue(result.isValid)
        assertTrue(result.isDisposable)
    }

    @Test
    fun `validate guerrillamail de is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("guerrillamail.de"))
    }

    @Test
    fun `validate guerrillamail net is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("guerrillamail.net"))
    }

    @Test
    fun `validate guerrillamail org is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("guerrillamail.org"))
    }

    @Test
    fun `validate sharklasers is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("sharklasers.com"))
    }

    @Test
    fun `validate mailnesia is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("mailnesia.com"))
    }

    @Test
    fun `validate getnada is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("getnada.com"))
    }

    @Test
    fun `validate mailsac is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("mailsac.com"))
    }

    @Test
    fun `validate armyspy is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("armyspy.com"))
    }

    @Test
    fun `validate cuvox is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("cuvox.de"))
    }

    @Test
    fun `validate rhyta is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("rhyta.com"))
    }

    @Test
    fun `validate teleworm is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("teleworm.us"))
    }

    // ========================================================================
    // SECTION 6: Free Email Provider Detection Tests
    // ========================================================================

    @Test
    fun `isFreeEmailProvider returns true for gmail`() {
        assertTrue(EmailValidator.isFreeEmailProvider("gmail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for googlemail`() {
        assertTrue(EmailValidator.isFreeEmailProvider("googlemail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for hotmail`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for icloud`() {
        assertTrue(EmailValidator.isFreeEmailProvider("icloud.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for aol`() {
        assertTrue(EmailValidator.isFreeEmailProvider("aol.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for protonmail`() {
        assertTrue(EmailValidator.isFreeEmailProvider("protonmail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for protonmail ch`() {
        assertTrue(EmailValidator.isFreeEmailProvider("protonmail.ch"))
    }

    @Test
    fun `isFreeEmailProvider returns true for proton me`() {
        assertTrue(EmailValidator.isFreeEmailProvider("proton.me"))
    }

    @Test
    fun `isFreeEmailProvider returns true for tutanota`() {
        assertTrue(EmailValidator.isFreeEmailProvider("tutanota.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for zoho`() {
        assertTrue(EmailValidator.isFreeEmailProvider("zoho.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for gmx`() {
        assertTrue(EmailValidator.isFreeEmailProvider("gmx.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for mail com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("mail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for fastmail`() {
        assertTrue(EmailValidator.isFreeEmailProvider("fastmail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yandex`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yandex.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for mail ru`() {
        assertTrue(EmailValidator.isFreeEmailProvider("mail.ru"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for msn`() {
        assertTrue(EmailValidator.isFreeEmailProvider("msn.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for me com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("me.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for mac com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("mac.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for ymail`() {
        assertTrue(EmailValidator.isFreeEmailProvider("ymail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for rocketmail`() {
        assertTrue(EmailValidator.isFreeEmailProvider("rocketmail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns false for custom domain`() {
        assertFalse(EmailValidator.isFreeEmailProvider("company.com"))
    }

    @Test
    fun `isFreeEmailProvider returns false for unknown domain`() {
        assertFalse(EmailValidator.isFreeEmailProvider("randomdomain.org"))
    }

    @Test
    fun `isFreeEmailProvider is case insensitive`() {
        assertTrue(EmailValidator.isFreeEmailProvider("GMAIL.COM"))
    }

    @Test
    fun `validate gmail email has isFreeProvider flag`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate yahoo email has isFreeProvider flag`() {
        val result = EmailValidator.validate("user@yahoo.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `isFreeEmailProvider returns true for hotmail co uk`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.co.uk"))
    }

    @Test
    fun `isFreeEmailProvider returns true for hotmail fr`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.fr"))
    }

    @Test
    fun `isFreeEmailProvider returns true for hotmail de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo co uk`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.co.uk"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo fr`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.fr"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook co uk`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.co.uk"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live co uk`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.co.uk"))
    }

    @Test
    fun `isFreeEmailProvider returns true for gmx de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("gmx.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for gmx net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("gmx.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for web de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("web.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for comcast net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("comcast.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for verizon net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("verizon.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for att net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("att.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for naver`() {
        assertTrue(EmailValidator.isFreeEmailProvider("naver.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for 163 com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("163.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for bigpond`() {
        assertTrue(EmailValidator.isFreeEmailProvider("bigpond.com"))
    }

    // ========================================================================
    // SECTION 7: Role Address Detection Tests
    // ========================================================================

    @Test
    fun `isRoleAddress returns true for admin`() {
        assertTrue(EmailValidator.isRoleAddress("admin"))
    }

    @Test
    fun `isRoleAddress returns true for administrator`() {
        assertTrue(EmailValidator.isRoleAddress("administrator"))
    }

    @Test
    fun `isRoleAddress returns true for webmaster`() {
        assertTrue(EmailValidator.isRoleAddress("webmaster"))
    }

    @Test
    fun `isRoleAddress returns true for postmaster`() {
        assertTrue(EmailValidator.isRoleAddress("postmaster"))
    }

    @Test
    fun `isRoleAddress returns true for info`() {
        assertTrue(EmailValidator.isRoleAddress("info"))
    }

    @Test
    fun `isRoleAddress returns true for support`() {
        assertTrue(EmailValidator.isRoleAddress("support"))
    }

    @Test
    fun `isRoleAddress returns true for sales`() {
        assertTrue(EmailValidator.isRoleAddress("sales"))
    }

    @Test
    fun `isRoleAddress returns true for marketing`() {
        assertTrue(EmailValidator.isRoleAddress("marketing"))
    }

    @Test
    fun `isRoleAddress returns true for abuse`() {
        assertTrue(EmailValidator.isRoleAddress("abuse"))
    }

    @Test
    fun `isRoleAddress returns true for noreply`() {
        assertTrue(EmailValidator.isRoleAddress("noreply"))
    }

    @Test
    fun `isRoleAddress returns true for no-reply`() {
        assertTrue(EmailValidator.isRoleAddress("no-reply"))
    }

    @Test
    fun `isRoleAddress returns true for billing`() {
        assertTrue(EmailValidator.isRoleAddress("billing"))
    }

    @Test
    fun `isRoleAddress returns true for hr`() {
        assertTrue(EmailValidator.isRoleAddress("hr"))
    }

    @Test
    fun `isRoleAddress returns true for legal`() {
        assertTrue(EmailValidator.isRoleAddress("legal"))
    }

    @Test
    fun `isRoleAddress returns true for feedback`() {
        assertTrue(EmailValidator.isRoleAddress("feedback"))
    }

    @Test
    fun `isRoleAddress returns true for jobs`() {
        assertTrue(EmailValidator.isRoleAddress("jobs"))
    }

    @Test
    fun `isRoleAddress returns true for privacy`() {
        assertTrue(EmailValidator.isRoleAddress("privacy"))
    }

    @Test
    fun `isRoleAddress returns true for gdpr`() {
        assertTrue(EmailValidator.isRoleAddress("gdpr"))
    }

    @Test
    fun `isRoleAddress returns true for api`() {
        assertTrue(EmailValidator.isRoleAddress("api"))
    }

    @Test
    fun `isRoleAddress returns true for dev`() {
        assertTrue(EmailValidator.isRoleAddress("dev"))
    }

    @Test
    fun `isRoleAddress returns true for test`() {
        assertTrue(EmailValidator.isRoleAddress("test"))
    }

    @Test
    fun `isRoleAddress returns true for demo`() {
        assertTrue(EmailValidator.isRoleAddress("demo"))
    }

    @Test
    fun `isRoleAddress returns true for help`() {
        assertTrue(EmailValidator.isRoleAddress("help"))
    }

    @Test
    fun `isRoleAddress returns true for security`() {
        assertTrue(EmailValidator.isRoleAddress("security"))
    }

    @Test
    fun `isRoleAddress returns true for contact`() {
        assertTrue(EmailValidator.isRoleAddress("contact"))
    }

    @Test
    fun `isRoleAddress returns true for press`() {
        assertTrue(EmailValidator.isRoleAddress("press"))
    }

    @Test
    fun `isRoleAddress returns true for office`() {
        assertTrue(EmailValidator.isRoleAddress("office"))
    }

    @Test
    fun `isRoleAddress returns true for newsletter`() {
        assertTrue(EmailValidator.isRoleAddress("newsletter"))
    }

    @Test
    fun `isRoleAddress returns true for team`() {
        assertTrue(EmailValidator.isRoleAddress("team"))
    }

    @Test
    fun `isRoleAddress returns true for ops`() {
        assertTrue(EmailValidator.isRoleAddress("ops"))
    }

    @Test
    fun `isRoleAddress returns true for devops`() {
        assertTrue(EmailValidator.isRoleAddress("devops"))
    }

    @Test
    fun `isRoleAddress returns false for personal name`() {
        assertFalse(EmailValidator.isRoleAddress("john"))
    }

    @Test
    fun `isRoleAddress returns false for random string`() {
        assertFalse(EmailValidator.isRoleAddress("xyzuser123"))
    }

    @Test
    fun `isRoleAddress is case insensitive`() {
        assertTrue(EmailValidator.isRoleAddress("ADMIN"))
    }

    @Test
    fun `validate role address has isRoleAddress flag`() {
        val result = EmailValidator.validate("admin@company.com")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `validate support role address has flag`() {
        val result = EmailValidator.validate("support@company.com")
        assertTrue(result.isRoleAddress)
    }

    // ========================================================================
    // SECTION 8: Typo Suggestion Tests
    // ========================================================================

    @Test
    fun `suggestDomainCorrection for gmial returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gmial.com")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for gmaill returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gmaill.com")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for gamil returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gamil.com")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for gnail returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gnail.com")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for gmail co returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gmail.co")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for gmail cm returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gmail.cm")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for gmail con returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gmail.con")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for gmail vom returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gmail.vom")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for gmail org returns gmail`() {
        val correction = EmailValidator.suggestDomainCorrection("gmail.org")
        assertEquals("gmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for yaho returns yahoo`() {
        val correction = EmailValidator.suggestDomainCorrection("yaho.com")
        assertEquals("yahoo.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for yahooo returns yahoo`() {
        val correction = EmailValidator.suggestDomainCorrection("yahooo.com")
        assertEquals("yahoo.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for yhaoo returns yahoo`() {
        val correction = EmailValidator.suggestDomainCorrection("yhaoo.com")
        assertEquals("yahoo.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for yahoo con returns yahoo`() {
        val correction = EmailValidator.suggestDomainCorrection("yahoo.con")
        assertEquals("yahoo.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for hotamil returns hotmail`() {
        val correction = EmailValidator.suggestDomainCorrection("hotamil.com")
        assertEquals("hotmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for hotmal returns hotmail`() {
        val correction = EmailValidator.suggestDomainCorrection("hotmal.com")
        assertEquals("hotmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for hotmial returns hotmail`() {
        val correction = EmailValidator.suggestDomainCorrection("hotmial.com")
        assertEquals("hotmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for hotmail con returns hotmail`() {
        val correction = EmailValidator.suggestDomainCorrection("hotmail.con")
        assertEquals("hotmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for hitmail returns hotmail`() {
        val correction = EmailValidator.suggestDomainCorrection("hitmail.com")
        assertEquals("hotmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for outloo returns outlook`() {
        val correction = EmailValidator.suggestDomainCorrection("outloo.com")
        assertEquals("outlook.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for outlok returns outlook`() {
        val correction = EmailValidator.suggestDomainCorrection("outlok.com")
        assertEquals("outlook.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for outlook con returns outlook`() {
        val correction = EmailValidator.suggestDomainCorrection("outlook.con")
        assertEquals("outlook.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for iclould returns icloud`() {
        val correction = EmailValidator.suggestDomainCorrection("iclould.com")
        assertEquals("icloud.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for icloud con returns icloud`() {
        val correction = EmailValidator.suggestDomainCorrection("icloud.con")
        assertEquals("icloud.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for aol co returns aol`() {
        val correction = EmailValidator.suggestDomainCorrection("aol.co")
        assertEquals("aol.com", correction)
    }

    @Test
    fun `suggestDomainCorrection for protonmal returns protonmail`() {
        val correction = EmailValidator.suggestDomainCorrection("protonmal.com")
        assertEquals("protonmail.com", correction)
    }

    @Test
    fun `suggestDomainCorrection returns null for correct domain`() {
        val correction = EmailValidator.suggestDomainCorrection("gmail.com")
        assertNull(correction)
    }

    @Test
    fun `suggestDomainCorrection returns null for unknown domain`() {
        val correction = EmailValidator.suggestDomainCorrection("mycompany.com")
        assertNull(correction)
    }

    @Test
    fun `validate email with typo domain includes suggestion`() {
        val result = EmailValidator.validate("user@gmial.com")
        assertTrue(result.isValid)
        assertNotNull(result.suggestedCorrection)
        assertEquals("user@gmail.com", result.suggestedCorrection)
    }

    @Test
    fun `validate email with correct domain has no suggestion`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertNull(result.suggestedCorrection)
    }

    // ========================================================================
    // SECTION 9: Plus Addressing (Sub-addressing) Tests
    // ========================================================================

    @Test
    fun `validate email with plus tag has subAddress`() {
        val result = EmailValidator.validate("user+shopping@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.hasSubAddress)
        assertEquals("shopping", result.subAddress)
    }

    @Test
    fun `validate email with plus and multiple words`() {
        val result = EmailValidator.validate("user+my.tag@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.hasSubAddress)
        assertEquals("my.tag", result.subAddress)
    }

    @Test
    fun `validate email without plus has no subAddress`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.isValid)
        assertFalse(result.hasSubAddress)
        assertNull(result.subAddress)
    }

    @Test
    fun `validate email with empty plus tag`() {
        val result = EmailValidator.validate("user+@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.hasSubAddress)
        assertEquals("", result.subAddress)
    }

    @Test
    fun `validate email with plus and numbers`() {
        val result = EmailValidator.validate("user+123@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.hasSubAddress)
        assertEquals("123", result.subAddress)
    }

    @Test
    fun `validate normalizedEmail strips subAddress`() {
        val result = EmailValidator.validate("user+tag@gmail.com")
        assertEquals("user@gmail.com", result.normalizedEmail)
    }

    @Test
    fun `validate normalizedEmail without subAddress keeps local part`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertEquals("user@gmail.com", result.normalizedEmail)
    }

    @Test
    fun `validate normalizedEmail lowercases domain`() {
        val result = EmailValidator.validate("User@GMAIL.COM")
        assertEquals("User@gmail.com", result.normalizedEmail)
    }

    // ========================================================================
    // SECTION 10: Gmail Normalization Tests
    // ========================================================================

    @Test
    fun `normalizeGmailAddress removes dots`() {
        val normalized = EmailValidator.normalizeGmailAddress("u.s.e.r@gmail.com")
        assertEquals("user@gmail.com", normalized)
    }

    @Test
    fun `normalizeGmailAddress removes plus addressing`() {
        val normalized = EmailValidator.normalizeGmailAddress("user+tag@gmail.com")
        assertEquals("user@gmail.com", normalized)
    }

    @Test
    fun `normalizeGmailAddress removes dots and plus addressing`() {
        val normalized = EmailValidator.normalizeGmailAddress("u.s.e.r+tag@gmail.com")
        assertEquals("user@gmail.com", normalized)
    }

    @Test
    fun `normalizeGmailAddress works with googlemail domain`() {
        val normalized = EmailValidator.normalizeGmailAddress("user@googlemail.com")
        assertEquals("user@gmail.com", normalized)
    }

    @Test
    fun `normalizeGmailAddress returns as-is for non-gmail`() {
        val normalized = EmailValidator.normalizeGmailAddress("user@yahoo.com")
        assertEquals("user@yahoo.com", normalized)
    }

    @Test
    fun `normalizeGmailAddress returns null for invalid email`() {
        val normalized = EmailValidator.normalizeGmailAddress("invalid")
        assertNull(normalized)
    }

    @Test
    fun `normalizeGmailAddress with no dots or plus returns same`() {
        val normalized = EmailValidator.normalizeGmailAddress("user@gmail.com")
        assertEquals("user@gmail.com", normalized)
    }

    @Test
    fun `normalizeGmailAddress with many dots`() {
        val normalized = EmailValidator.normalizeGmailAddress("a.b.c.d.e.f@gmail.com")
        assertEquals("abcdef@gmail.com", normalized)
    }

    @Test
    fun `normalizeGmailAddress with complex plus tag`() {
        val normalized = EmailValidator.normalizeGmailAddress("user+tag1+tag2@gmail.com")
        // indexOf('+') finds first + , so subAddress is everything after first +
        assertEquals("user@gmail.com", normalized)
    }

    // ========================================================================
    // SECTION 11: Example Domain Tests
    // ========================================================================

    @Test
    fun `isExampleDomain returns true for example com`() {
        assertTrue(EmailValidator.isExampleDomain("example.com"))
    }

    @Test
    fun `isExampleDomain returns true for example org`() {
        assertTrue(EmailValidator.isExampleDomain("example.org"))
    }

    @Test
    fun `isExampleDomain returns true for example net`() {
        assertTrue(EmailValidator.isExampleDomain("example.net"))
    }

    @Test
    fun `isExampleDomain returns true for test com`() {
        assertTrue(EmailValidator.isExampleDomain("test.com"))
    }

    @Test
    fun `isExampleDomain returns true for localhost`() {
        assertTrue(EmailValidator.isExampleDomain("localhost"))
    }

    @Test
    fun `isExampleDomain returns true for invalid`() {
        assertTrue(EmailValidator.isExampleDomain("invalid"))
    }

    @Test
    fun `isExampleDomain returns false for gmail`() {
        assertFalse(EmailValidator.isExampleDomain("gmail.com"))
    }

    @Test
    fun `isExampleDomain is case insensitive`() {
        assertTrue(EmailValidator.isExampleDomain("EXAMPLE.COM"))
    }

    // ========================================================================
    // SECTION 12: extractFromText Tests
    // ========================================================================

    @Test
    fun `extractFromText finds single email`() {
        val results = EmailValidator.extractFromText("Contact us at user@example.com for info.")
        assertTrue(results.isNotEmpty())
        assertEquals("user", results[0].localPart)
    }

    @Test
    fun `extractFromText finds multiple emails`() {
        val text = "Email john@example.com or jane@example.org"
        val results = EmailValidator.extractFromText(text)
        assertTrue(results.size >= 2)
    }

    @Test
    fun `extractFromText returns empty for no emails`() {
        val results = EmailValidator.extractFromText("No emails here at all.")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText returns empty for empty string`() {
        val results = EmailValidator.extractFromText("")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText finds email in HTML`() {
        val html = "<a href='mailto:user@example.com'>Contact</a>"
        val results = EmailValidator.extractFromText(html)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds email in JSON`() {
        val json = """{"email": "user@example.com", "name": "Test"}"""
        val results = EmailValidator.extractFromText(json)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds email in CSV`() {
        val csv = "name,email\nJohn,john@example.com"
        val results = EmailValidator.extractFromText(csv)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds email in log line`() {
        val log = "2024-01-01 INFO User john@example.com logged in"
        val results = EmailValidator.extractFromText(log)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `extractFromText handles multiline text`() {
        val text = """
            First: user1@example.com
            Second: user2@example.org
            Third: user3@example.net
        """.trimIndent()
        val results = EmailValidator.extractFromText(text)
        assertTrue(results.size >= 3)
    }

    @Test
    fun `extractFromText finds gmail address`() {
        val results = EmailValidator.extractFromText("Send to user@gmail.com")
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].isFreeProvider)
    }

    @Test
    fun `extractFromText finds email with plus addressing`() {
        val results = EmailValidator.extractFromText("Email user+tag@gmail.com")
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].hasSubAddress)
    }

    @Test
    fun `extractFromText does not find partial emails`() {
        val results = EmailValidator.extractFromText("@example.com is not valid")
        assertTrue(results.isEmpty())
    }

    // ========================================================================
    // SECTION 13: looksLikeEmail Tests
    // ========================================================================

    @Test
    fun `looksLikeEmail returns true for simple email`() {
        assertTrue(EmailValidator.looksLikeEmail("user@example.com"))
    }

    @Test
    fun `looksLikeEmail returns true for gmail`() {
        assertTrue(EmailValidator.looksLikeEmail("test@gmail.com"))
    }

    @Test
    fun `looksLikeEmail returns false for empty`() {
        assertFalse(EmailValidator.looksLikeEmail(""))
    }

    @Test
    fun `looksLikeEmail returns false for too short`() {
        assertFalse(EmailValidator.looksLikeEmail("a@b"))
    }

    @Test
    fun `looksLikeEmail returns false for no at sign`() {
        assertFalse(EmailValidator.looksLikeEmail("userexample.com"))
    }

    @Test
    fun `looksLikeEmail returns false for no dot after at`() {
        assertFalse(EmailValidator.looksLikeEmail("user@example"))
    }

    @Test
    fun `looksLikeEmail returns false for at at start`() {
        assertFalse(EmailValidator.looksLikeEmail("@example.com"))
    }

    @Test
    fun `looksLikeEmail returns false for too long`() {
        val longEmail = "a".repeat(300) + "@example.com"
        assertFalse(EmailValidator.looksLikeEmail(longEmail))
    }

    @Test
    fun `looksLikeEmail returns false for ending with dot after at`() {
        assertFalse(EmailValidator.looksLikeEmail("user@example."))
    }

    @Test
    fun `looksLikeEmail returns true for email with subdomain`() {
        assertTrue(EmailValidator.looksLikeEmail("user@sub.example.com"))
    }

    // ========================================================================
    // SECTION 14: Validation Result Property Tests
    // ========================================================================

    @Test
    fun `valid email has correct localPart`() {
        val result = EmailValidator.validate("testuser@example.com")
        assertEquals("testuser", result.localPart)
    }

    @Test
    fun `valid email has correct domain`() {
        val result = EmailValidator.validate("testuser@example.com")
        assertEquals("example.com", result.domain)
    }

    @Test
    fun `valid email has non-empty normalizedEmail`() {
        val result = EmailValidator.validate("testuser@example.com")
        assertTrue(result.normalizedEmail.isNotEmpty())
    }

    @Test
    fun `valid email has positive confidence`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.confidence > 0f)
    }

    @Test
    fun `valid email has non-empty reason`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `invalid email has isValid false`() {
        val result = EmailValidator.validate("")
        assertFalse(result.isValid)
    }

    @Test
    fun `invalid email has non-empty reason`() {
        val result = EmailValidator.validate("invalid")
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `valid email reason contains Valid`() {
        val result = EmailValidator.validate("user@example.com")
        assertTrue(result.reason.contains("Valid"))
    }

    @Test
    fun `disposable email reason contains WARNING`() {
        val result = EmailValidator.validate("user@mailinator.com")
        assertTrue(result.reason.contains("WARNING"))
    }

    @Test
    fun `example domain email reason contains WARNING`() {
        val result = EmailValidator.validate("user@example.com")
        assertTrue(result.reason.contains("WARNING"))
    }

    @Test
    fun `role address email reason contains role`() {
        val result = EmailValidator.validate("admin@company.com")
        assertTrue(result.reason.contains("role"))
    }

    @Test
    fun `free provider email reason contains free`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.reason.contains("free"))
    }

    // ========================================================================
    // SECTION 15: Confidence Score Tests
    // ========================================================================

    @Test
    fun `confidence is higher for gmail address`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.confidence > 0.8f)
    }

    @Test
    fun `confidence is lower for disposable address`() {
        val result = EmailValidator.validate("user@mailinator.com")
        val gmailResult = EmailValidator.validate("user@gmail.com")
        assertTrue(result.confidence < gmailResult.confidence)
    }

    @Test
    fun `confidence is lower for example domain`() {
        val result = EmailValidator.validate("user@example.com")
        assertTrue(result.confidence < 0.8f)
    }

    @Test
    fun `confidence is lower for very short local part`() {
        val result = EmailValidator.validate("a@unknowndomain.com")
        val result2 = EmailValidator.validate("johndoe@unknowndomain.com")
        assertTrue(result.confidence <= result2.confidence)
    }

    @Test
    fun `confidence is between 0 and 1`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.confidence >= 0f)
        assertTrue(result.confidence <= 1f)
    }

    @Test
    fun `confidence for regular custom domain`() {
        val result = EmailValidator.validate("user@company.com")
        assertTrue(result.confidence > 0.5f)
    }

    // ========================================================================
    // SECTION 16: Email Length Edge Cases
    // ========================================================================

    @Test
    fun `validate email at max total length 254`() {
        val localPart = "a".repeat(64)
        val domainLabel = "b".repeat(63)
        val email = "$localPart@$domainLabel.com"
        if (email.length <= 254) {
            val result = EmailValidator.validate(email)
            assertTrue(result.isValid)
        }
    }

    @Test
    fun `validate email exceeding max total length is invalid`() {
        val localPart = "a".repeat(64)
        val longDomain = "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(63) + ".com"
        val email = "$localPart@$longDomain"
        if (email.length > 254) {
            val result = EmailValidator.validate(email)
            assertFalse(result.isValid)
        }
    }

    @Test
    fun `validate minimum viable email a at b dot c`() {
        val result = EmailValidator.validate("a@b.co")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate two character local part`() {
        val result = EmailValidator.validate("ab@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate three character TLD`() {
        val result = EmailValidator.validate("user@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate two character TLD`() {
        val result = EmailValidator.validate("user@example.co")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate long TLD`() {
        val result = EmailValidator.validate("user@example.technology")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 17: Domain with Special Characters
    // ========================================================================

    @Test
    fun `validate domain with numbers`() {
        val result = EmailValidator.validate("user@domain123.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate domain with hyphens`() {
        val result = EmailValidator.validate("user@my-domain.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate domain all numbers except TLD`() {
        val result = EmailValidator.validate("user@123.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate domain with multiple subdomains`() {
        val result = EmailValidator.validate("user@a.b.c.d.example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate domain with country code TLD`() {
        val result = EmailValidator.validate("user@example.co.uk")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate domain with new gTLD`() {
        val result = EmailValidator.validate("user@example.xyz")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate domain with museum TLD`() {
        val result = EmailValidator.validate("user@example.museum")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 18: validateLocalPart Direct Tests
    // ========================================================================

    @Test
    fun `validateLocalPart simple username is valid`() {
        val result = EmailValidator.validateLocalPart("user")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart empty is invalid`() {
        val result = EmailValidator.validateLocalPart("")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart at max 64 chars is valid`() {
        val result = EmailValidator.validateLocalPart("a".repeat(64))
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart exceeding 64 chars is invalid`() {
        val result = EmailValidator.validateLocalPart("a".repeat(65))
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart starting with dot is invalid`() {
        val result = EmailValidator.validateLocalPart(".user")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart ending with dot is invalid`() {
        val result = EmailValidator.validateLocalPart("user.")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with consecutive dots is invalid`() {
        val result = EmailValidator.validateLocalPart("user..name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with single dot is valid`() {
        val result = EmailValidator.validateLocalPart("user.name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with multiple dots is valid`() {
        val result = EmailValidator.validateLocalPart("a.b.c.d")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with all valid special chars`() {
        val result = EmailValidator.validateLocalPart("user!#\$%&'*+/=?^_\`{|}~-name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with comma is invalid`() {
        val result = EmailValidator.validateLocalPart("user,name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with parenthesis is invalid`() {
        val result = EmailValidator.validateLocalPart("user(name)")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with brackets is invalid`() {
        val result = EmailValidator.validateLocalPart("user[name]")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with colon is invalid`() {
        val result = EmailValidator.validateLocalPart("user:name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with semicolon is invalid`() {
        val result = EmailValidator.validateLocalPart("user;name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with angle brackets is invalid`() {
        val result = EmailValidator.validateLocalPart("user<name>")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with backslash is invalid`() {
        val result = EmailValidator.validateLocalPart("user\\name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart quoted with space is valid`() {
        val result = EmailValidator.validateLocalPart("\"user name\"")
        assertTrue(result.first)
    }

    // ========================================================================
    // SECTION 19: Disposable Domain Count Test
    // ========================================================================

    @Test
    fun `disposableDomainCount returns positive number`() {
        assertTrue(EmailValidator.disposableDomainCount() > 0)
    }

    @Test
    fun `disposableDomainCount matches set size`() {
        assertEquals(EmailValidator.disposableEmailDomains.size, EmailValidator.disposableDomainCount())
    }

    @Test
    fun `freeProviderCount returns positive number`() {
        assertTrue(EmailValidator.freeProviderCount() > 0)
    }

    @Test
    fun `freeProviderCount matches set size`() {
        assertEquals(EmailValidator.freeEmailProviders.size, EmailValidator.freeProviderCount())
    }

    // ========================================================================
    // SECTION 20: IP Domain Literal Tests
    // ========================================================================

    @Test
    fun `validate email with IPv4 domain literal`() {
        val result = EmailValidator.validate("user@[192.168.1.1]")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with IPv6 domain literal`() {
        val result = EmailValidator.validate("user@[IPv6:2001:db8::1]")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with invalid IP domain literal`() {
        val result = EmailValidator.validate("user@[999.999.999.999]")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate email with IPv4 0 0 0 0`() {
        val result = EmailValidator.validate("user@[0.0.0.0]")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with IPv4 255 255 255 255`() {
        val result = EmailValidator.validate("user@[255.255.255.255]")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with IPv4 127 0 0 1`() {
        val result = EmailValidator.validate("user@[127.0.0.1]")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 21: Email Validation with checkDisposable and suggestCorrections flags
    // ========================================================================

    @Test
    fun `validate with checkDisposable false skips disposable check`() {
        val result = EmailValidator.validate("user@mailinator.com", checkDisposable = false)
        assertTrue(result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `validate with checkDisposable true detects disposable`() {
        val result = EmailValidator.validate("user@mailinator.com", checkDisposable = true)
        assertTrue(result.isValid)
        assertTrue(result.isDisposable)
    }

    @Test
    fun `validate with suggestCorrections false skips suggestions`() {
        val result = EmailValidator.validate("user@gmial.com", suggestCorrections = false)
        assertTrue(result.isValid)
        assertNull(result.suggestedCorrection)
    }

    @Test
    fun `validate with suggestCorrections true includes suggestions`() {
        val result = EmailValidator.validate("user@gmial.com", suggestCorrections = true)
        assertTrue(result.isValid)
        assertNotNull(result.suggestedCorrection)
    }

    @Test
    fun `validate with both flags false`() {
        val result = EmailValidator.validate("user@mailinator.com", checkDisposable = false, suggestCorrections = false)
        assertTrue(result.isValid)
        assertFalse(result.isDisposable)
    }

    // ========================================================================
    // SECTION 22: International TLD and Domain Tests
    // ========================================================================

    @Test
    fun `validate email with io TLD`() {
        val result = EmailValidator.validate("user@example.io")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with dev TLD`() {
        val result = EmailValidator.validate("user@example.dev")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with app TLD`() {
        val result = EmailValidator.validate("user@example.app")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with ai TLD`() {
        val result = EmailValidator.validate("user@example.ai")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with me TLD`() {
        val result = EmailValidator.validate("user@example.me")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with info TLD`() {
        val result = EmailValidator.validate("user@example.info")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with biz TLD`() {
        val result = EmailValidator.validate("user@example.biz")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with edu TLD`() {
        val result = EmailValidator.validate("user@example.edu")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with gov TLD`() {
        val result = EmailValidator.validate("user@example.gov")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with mil TLD`() {
        val result = EmailValidator.validate("user@example.mil")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 23: Comprehensive Validation Scenarios
    // ========================================================================

    @Test
    fun `validate typical user signup email`() {
        val result = EmailValidator.validate("john.doe@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
        assertFalse(result.isDisposable)
        assertFalse(result.isRoleAddress)
    }

    @Test
    fun `validate corporate email`() {
        val result = EmailValidator.validate("john.doe@company.com")
        assertTrue(result.isValid)
        assertFalse(result.isFreeProvider)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `validate disposable email flags correctly`() {
        val result = EmailValidator.validate("throwaway@yopmail.com")
        assertTrue(result.isValid)
        assertTrue(result.isDisposable)
    }

    @Test
    fun `validate role email at free provider`() {
        val result = EmailValidator.validate("admin@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email with subdomain`() {
        val result = EmailValidator.validate("user@mail.company.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with long local part`() {
        val localPart = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz12"
        val result = EmailValidator.validate("$localPart@example.com")
        assertTrue(result.isValid)
        assertEquals(64, localPart.length)
    }

    @Test
    fun `validate email from known ISP`() {
        val result = EmailValidator.validate("user@comcast.net")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from Asian provider`() {
        val result = EmailValidator.validate("user@naver.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from Chinese provider`() {
        val result = EmailValidator.validate("user@163.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from German provider`() {
        val result = EmailValidator.validate("user@web.de")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from French provider`() {
        val result = EmailValidator.validate("user@orange.fr")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from Italian provider`() {
        val result = EmailValidator.validate("user@libero.it")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from Brazilian provider`() {
        val result = EmailValidator.validate("user@uol.com.br")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from Australian provider`() {
        val result = EmailValidator.validate("user@bigpond.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from Canadian provider`() {
        val result = EmailValidator.validate("user@shaw.ca")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from UK provider`() {
        val result = EmailValidator.validate("user@btinternet.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email from Indian provider`() {
        val result = EmailValidator.validate("user@rediffmail.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    // ========================================================================
    // SECTION 24: Stress and Edge Case Tests
    // ========================================================================

    @Test
    fun `validate same email repeatedly gives consistent results`() {
        val email = "user@gmail.com"
        val r1 = EmailValidator.validate(email)
        val r2 = EmailValidator.validate(email)
        assertEquals(r1.isValid, r2.isValid)
        assertEquals(r1.isFreeProvider, r2.isFreeProvider)
        assertEquals(r1.isDisposable, r2.isDisposable)
    }

    @Test
    fun `validate email with leading whitespace is trimmed`() {
        val result = EmailValidator.validate("  user@gmail.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with trailing whitespace is trimmed`() {
        val result = EmailValidator.validate("user@gmail.com  ")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with leading and trailing whitespace`() {
        val result = EmailValidator.validate("  user@gmail.com  ")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with tab in middle is invalid`() {
        val result = EmailValidator.validate("us\ter@gmail.com")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate email with newline is invalid`() {
        val result = EmailValidator.validate("us\ner@gmail.com")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate email preserves original local part case`() {
        val result = EmailValidator.validate("JohnDoe@gmail.com")
        assertEquals("JohnDoe", result.localPart)
    }

    @Test
    fun `validate email lowercases domain`() {
        val result = EmailValidator.validate("user@GMAIL.COM")
        assertEquals("gmail.com", result.domain)
    }

    // ========================================================================
    // SECTION 25: EmailValidationResult Data Class Tests
    // ========================================================================

    @Test
    fun `EmailValidationResult defaults are correct`() {
        val result = EmailValidator.EmailValidationResult(isValid = false)
        assertFalse(result.isValid)
        assertEquals("", result.localPart)
        assertEquals("", result.domain)
        assertEquals("", result.normalizedEmail)
        assertFalse(result.isDisposable)
        assertFalse(result.isFreeProvider)
        assertFalse(result.isRoleAddress)
        assertFalse(result.hasSubAddress)
        assertNull(result.subAddress)
        assertNull(result.suggestedCorrection)
        assertEquals(0f, result.confidence, 0.001f)
        assertEquals("", result.reason)
    }

    @Test
    fun `EmailValidationResult with all fields set`() {
        val result = EmailValidator.EmailValidationResult(
            isValid = true,
            localPart = "user",
            domain = "gmail.com",
            normalizedEmail = "user@gmail.com",
            isDisposable = false,
            isFreeProvider = true,
            isRoleAddress = false,
            hasSubAddress = true,
            subAddress = "tag",
            suggestedCorrection = null,
            confidence = 0.95f,
            reason = "Valid"
        )
        assertTrue(result.isValid)
        assertEquals("user", result.localPart)
        assertEquals("gmail.com", result.domain)
        assertEquals("user@gmail.com", result.normalizedEmail)
        assertFalse(result.isDisposable)
        assertTrue(result.isFreeProvider)
        assertFalse(result.isRoleAddress)
        assertTrue(result.hasSubAddress)
        assertEquals("tag", result.subAddress)
        assertNull(result.suggestedCorrection)
        assertEquals(0.95f, result.confidence, 0.001f)
    }

    // ========================================================================
    // SECTION 26: Additional Typo Corrections Tests
    // ========================================================================

    @Test
    fun `suggestDomainCorrection for gmal returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmal.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmai returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmai.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmail cim returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.cim"))
    }

    @Test
    fun `suggestDomainCorrection for gmail comn returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.comn"))
    }

    @Test
    fun `suggestDomainCorrection for gmail come returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.come"))
    }

    @Test
    fun `suggestDomainCorrection for gmail comm returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.comm"))
    }

    @Test
    fun `suggestDomainCorrection for gmail xom returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.xom"))
    }

    @Test
    fun `suggestDomainCorrection for gmsil returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmsil.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmeil returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmeil.com"))
    }

    @Test
    fun `suggestDomainCorrection for gimail returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gimail.com"))
    }

    @Test
    fun `suggestDomainCorrection for gemail returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gemail.com"))
    }

    @Test
    fun `suggestDomainCorrection for yahoo cm returns yahoo`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoo.cm"))
    }

    @Test
    fun `suggestDomainCorrection for yahoo co returns yahoo`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoo.co"))
    }

    @Test
    fun `suggestDomainCorrection for yahoo vom returns yahoo`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoo.vom"))
    }

    @Test
    fun `suggestDomainCorrection for yhoo returns yahoo`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yhoo.com"))
    }

    @Test
    fun `suggestDomainCorrection for yaboo returns yahoo`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yaboo.com"))
    }

    @Test
    fun `suggestDomainCorrection for yaoo returns yahoo`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yaoo.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmaill returns hotmail`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmaill.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmai returns hotmail`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmai.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmil returns hotmail`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmil.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmail cm returns hotmail`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmail.cm"))
    }

    @Test
    fun `suggestDomainCorrection for hotmail co returns hotmail`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmail.co"))
    }

    @Test
    fun `suggestDomainCorrection for hotnail returns hotmail`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotnail.com"))
    }

    @Test
    fun `suggestDomainCorrection for outloook returns outlook`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outloook.com"))
    }

    @Test
    fun `suggestDomainCorrection for oulook returns outlook`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("oulook.com"))
    }

    @Test
    fun `suggestDomainCorrection for outlooik returns outlook`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlooik.com"))
    }

    @Test
    fun `suggestDomainCorrection for outlook cm returns outlook`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlook.cm"))
    }

    @Test
    fun `suggestDomainCorrection for outlook co returns outlook`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlook.co"))
    }

    @Test
    fun `suggestDomainCorrection for outlouk returns outlook`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlouk.com"))
    }

    @Test
    fun `suggestDomainCorrection for iclud returns icloud`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("iclud.com"))
    }

    @Test
    fun `suggestDomainCorrection for iclod returns icloud`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("iclod.com"))
    }

    @Test
    fun `suggestDomainCorrection for icloud co returns icloud`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("icloud.co"))
    }

    @Test
    fun `suggestDomainCorrection for icloud cm returns icloud`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("icloud.cm"))
    }

    @Test
    fun `suggestDomainCorrection for aol cm returns aol`() {
        assertEquals("aol.com", EmailValidator.suggestDomainCorrection("aol.cm"))
    }

    @Test
    fun `suggestDomainCorrection for aol con returns aol`() {
        assertEquals("aol.com", EmailValidator.suggestDomainCorrection("aol.con"))
    }

    @Test
    fun `suggestDomainCorrection for protommail returns protonmail`() {
        assertEquals("protonmail.com", EmailValidator.suggestDomainCorrection("protommail.com"))
    }

    @Test
    fun `suggestDomainCorrection for protonmail co returns protonmail`() {
        assertEquals("protonmail.com", EmailValidator.suggestDomainCorrection("protonmail.co"))
    }

    @Test
    fun `suggestDomainCorrection for protonmail cm returns protonmail`() {
        assertEquals("protonmail.com", EmailValidator.suggestDomainCorrection("protonmail.cm"))
    }

    @Test
    fun `suggestDomainCorrection for gmail net returns gmail`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.net"))
    }

    @Test
    fun `suggestDomainCorrection for yahoo org returns yahoo`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoo.org"))
    }

    @Test
    fun `suggestDomainCorrection for hotmail org returns hotmail`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmail.org"))
    }

    // ========================================================================
    // SECTION 27: Additional Role Addresses
    // ========================================================================

    @Test
    fun `isRoleAddress returns true for hostmaster`() {
        assertTrue(EmailValidator.isRoleAddress("hostmaster"))
    }

    @Test
    fun `isRoleAddress returns true for information`() {
        assertTrue(EmailValidator.isRoleAddress("information"))
    }

    @Test
    fun `isRoleAddress returns true for contacts`() {
        assertTrue(EmailValidator.isRoleAddress("contacts"))
    }

    @Test
    fun `isRoleAddress returns true for helpdesk`() {
        assertTrue(EmailValidator.isRoleAddress("helpdesk"))
    }

    @Test
    fun `isRoleAddress returns true for service`() {
        assertTrue(EmailValidator.isRoleAddress("service"))
    }

    @Test
    fun `isRoleAddress returns true for media`() {
        assertTrue(EmailValidator.isRoleAddress("media"))
    }

    @Test
    fun `isRoleAddress returns true for spam`() {
        assertTrue(EmailValidator.isRoleAddress("spam"))
    }

    @Test
    fun `isRoleAddress returns true for noc`() {
        assertTrue(EmailValidator.isRoleAddress("noc"))
    }

    @Test
    fun `isRoleAddress returns true for donotreply`() {
        assertTrue(EmailValidator.isRoleAddress("donotreply"))
    }

    @Test
    fun `isRoleAddress returns true for do-not-reply`() {
        assertTrue(EmailValidator.isRoleAddress("do-not-reply"))
    }

    @Test
    fun `isRoleAddress returns true for mailer-daemon`() {
        assertTrue(EmailValidator.isRoleAddress("mailer-daemon"))
    }

    @Test
    fun `isRoleAddress returns true for root`() {
        assertTrue(EmailValidator.isRoleAddress("root"))
    }

    @Test
    fun `isRoleAddress returns true for sysadmin`() {
        assertTrue(EmailValidator.isRoleAddress("sysadmin"))
    }

    @Test
    fun `isRoleAddress returns true for devnull`() {
        assertTrue(EmailValidator.isRoleAddress("devnull"))
    }

    @Test
    fun `isRoleAddress returns true for finance`() {
        assertTrue(EmailValidator.isRoleAddress("finance"))
    }

    @Test
    fun `isRoleAddress returns true for accounting`() {
        assertTrue(EmailValidator.isRoleAddress("accounting"))
    }

    @Test
    fun `isRoleAddress returns true for payroll`() {
        assertTrue(EmailValidator.isRoleAddress("payroll"))
    }

    @Test
    fun `isRoleAddress returns true for humanresources`() {
        assertTrue(EmailValidator.isRoleAddress("humanresources"))
    }

    @Test
    fun `isRoleAddress returns true for compliance`() {
        assertTrue(EmailValidator.isRoleAddress("compliance"))
    }

    @Test
    fun `isRoleAddress returns true for reception`() {
        assertTrue(EmailValidator.isRoleAddress("reception"))
    }

    @Test
    fun `isRoleAddress returns true for general`() {
        assertTrue(EmailValidator.isRoleAddress("general"))
    }

    @Test
    fun `isRoleAddress returns true for suggestions`() {
        assertTrue(EmailValidator.isRoleAddress("suggestions"))
    }

    @Test
    fun `isRoleAddress returns true for complaints`() {
        assertTrue(EmailValidator.isRoleAddress("complaints"))
    }

    @Test
    fun `isRoleAddress returns true for subscribe`() {
        assertTrue(EmailValidator.isRoleAddress("subscribe"))
    }

    @Test
    fun `isRoleAddress returns true for unsubscribe`() {
        assertTrue(EmailValidator.isRoleAddress("unsubscribe"))
    }

    @Test
    fun `isRoleAddress returns true for careers`() {
        assertTrue(EmailValidator.isRoleAddress("careers"))
    }

    @Test
    fun `isRoleAddress returns true for recruitment`() {
        assertTrue(EmailValidator.isRoleAddress("recruitment"))
    }

    @Test
    fun `isRoleAddress returns true for hiring`() {
        assertTrue(EmailValidator.isRoleAddress("hiring"))
    }

    @Test
    fun `isRoleAddress returns true for staff`() {
        assertTrue(EmailValidator.isRoleAddress("staff"))
    }

    @Test
    fun `isRoleAddress returns true for everyone`() {
        assertTrue(EmailValidator.isRoleAddress("everyone"))
    }

    @Test
    fun `isRoleAddress returns true for all`() {
        assertTrue(EmailValidator.isRoleAddress("all"))
    }

    @Test
    fun `isRoleAddress returns true for orders`() {
        assertTrue(EmailValidator.isRoleAddress("orders"))
    }

    @Test
    fun `isRoleAddress returns true for returns`() {
        assertTrue(EmailValidator.isRoleAddress("returns"))
    }

    @Test
    fun `isRoleAddress returns true for shipping`() {
        assertTrue(EmailValidator.isRoleAddress("shipping"))
    }

    @Test
    fun `isRoleAddress returns true for dpo`() {
        assertTrue(EmailValidator.isRoleAddress("dpo"))
    }

    @Test
    fun `isRoleAddress returns true for developer`() {
        assertTrue(EmailValidator.isRoleAddress("developer"))
    }

    @Test
    fun `isRoleAddress returns true for developers`() {
        assertTrue(EmailValidator.isRoleAddress("developers"))
    }

    @Test
    fun `isRoleAddress returns true for tech`() {
        assertTrue(EmailValidator.isRoleAddress("tech"))
    }

    @Test
    fun `isRoleAddress returns true for technical`() {
        assertTrue(EmailValidator.isRoleAddress("technical"))
    }

    @Test
    fun `isRoleAddress returns true for engineering`() {
        assertTrue(EmailValidator.isRoleAddress("engineering"))
    }

    @Test
    fun `isRoleAddress returns true for operations`() {
        assertTrue(EmailValidator.isRoleAddress("operations"))
    }

    @Test
    fun `isRoleAddress returns true for testing`() {
        assertTrue(EmailValidator.isRoleAddress("testing"))
    }

    @Test
    fun `isRoleAddress returns true for debug`() {
        assertTrue(EmailValidator.isRoleAddress("debug"))
    }

    @Test
    fun `isRoleAddress returns true for example`() {
        assertTrue(EmailValidator.isRoleAddress("example"))
    }

    @Test
    fun `isRoleAddress returns true for sample`() {
        assertTrue(EmailValidator.isRoleAddress("sample"))
    }

    // ========================================================================
    // SECTION 28: Additional Disposable Domains
    // ========================================================================

    @Test
    fun `validate temp-mail org is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("temp-mail.org"))
    }

    @Test
    fun `validate temp-mail io is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("temp-mail.io"))
    }

    @Test
    fun `validate dispostable is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("dispostable.com"))
    }

    @Test
    fun `validate minutemail is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("minutemail.com"))
    }

    @Test
    fun `validate emailondeck is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("emailondeck.com"))
    }

    @Test
    fun `validate 33mail is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("33mail.com"))
    }

    @Test
    fun `validate harakirimail is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("harakirimail.com"))
    }

    @Test
    fun `validate tempmailo is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("tempmailo.com"))
    }

    @Test
    fun `validate mailcatch is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("mailcatch.com"))
    }

    @Test
    fun `validate emailfake is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("emailfake.com"))
    }

    @Test
    fun `validate crazymailing is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("crazymailing.com"))
    }

    @Test
    fun `validate dayrep is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("dayrep.com"))
    }

    @Test
    fun `validate fleckens is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("fleckens.hu"))
    }

    @Test
    fun `validate jourrapide is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("jourrapide.com"))
    }

    @Test
    fun `validate superrito is disposable`() {
        assertTrue(EmailValidator.isDisposableEmail("superrito.com"))
    }

    // ========================================================================
    // SECTION 29: domainTypoCorrections Map Structure Tests
    // ========================================================================

    @Test
    fun `domainTypoCorrections has entries for gmail`() {
        assertTrue(EmailValidator.domainTypoCorrections.values.any { it == "gmail.com" })
    }

    @Test
    fun `domainTypoCorrections has entries for yahoo`() {
        assertTrue(EmailValidator.domainTypoCorrections.values.any { it == "yahoo.com" })
    }

    @Test
    fun `domainTypoCorrections has entries for hotmail`() {
        assertTrue(EmailValidator.domainTypoCorrections.values.any { it == "hotmail.com" })
    }

    @Test
    fun `domainTypoCorrections has entries for outlook`() {
        assertTrue(EmailValidator.domainTypoCorrections.values.any { it == "outlook.com" })
    }

    @Test
    fun `domainTypoCorrections has entries for icloud`() {
        assertTrue(EmailValidator.domainTypoCorrections.values.any { it == "icloud.com" })
    }

    @Test
    fun `domainTypoCorrections has entries for aol`() {
        assertTrue(EmailValidator.domainTypoCorrections.values.any { it == "aol.com" })
    }

    @Test
    fun `domainTypoCorrections has entries for protonmail`() {
        assertTrue(EmailValidator.domainTypoCorrections.values.any { it == "protonmail.com" })
    }

    @Test
    fun `domainTypoCorrections map is not empty`() {
        assertTrue(EmailValidator.domainTypoCorrections.isNotEmpty())
    }

    @Test
    fun `domainTypoCorrections has many entries`() {
        assertTrue(EmailValidator.domainTypoCorrections.size > 50)
    }

    // ========================================================================
    // SECTION 30: Regression and Boundary Tests
    // ========================================================================

    @Test
    fun `validate consecutive validate calls with different emails`() {
        val r1 = EmailValidator.validate("user@gmail.com")
        val r2 = EmailValidator.validate("admin@mailinator.com")
        val r3 = EmailValidator.validate("test@example.com")
        assertTrue(r1.isValid)
        assertTrue(r2.isValid)
        assertTrue(r3.isValid)
        assertTrue(r1.isFreeProvider)
        assertTrue(r2.isDisposable)
    }

    @Test
    fun `validate email with all allowed special chars in local part`() {
        val result = EmailValidator.validate("a!b#c\$d%e&f'g*h+i/j=k?l^m_n\`o{p|q}r~s@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with hyphen in domain`() {
        val result = EmailValidator.validate("user@my-company-name.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with many subdomains`() {
        val result = EmailValidator.validate("user@a.b.c.d.e.f.g.example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with single char domain labels`() {
        val result = EmailValidator.validate("user@a.b.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with numeric domain labels`() {
        val result = EmailValidator.validate("user@123.456.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email where domain label is exactly 63 chars`() {
        val label = "a".repeat(63)
        val result = EmailValidator.validate("user@$label.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate normalizedEmail for plus addressed gmail`() {
        val result = EmailValidator.validate("user+newsletter@gmail.com")
        assertEquals("user@gmail.com", result.normalizedEmail)
    }

    @Test
    fun `validate consistent results for edge case email`() {
        val email = "x@y.zz"
        val r1 = EmailValidator.validate(email)
        val r2 = EmailValidator.validate(email)
        assertEquals(r1.isValid, r2.isValid)
    }

    // ========================================================================
    // SECTION 31: Additional Free Provider International Variants
    // ========================================================================

    @Test
    fun `isFreeEmailProvider returns true for hotmail es`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.es"))
    }

    @Test
    fun `isFreeEmailProvider returns true for hotmail ca`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.ca"))
    }

    @Test
    fun `isFreeEmailProvider returns true for hotmail com au`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.com.au"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo it`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.it"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo es`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.es"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo ca`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.ca"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo com au`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.com.au"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo co in`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.co.in"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook fr`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.fr"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook in`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.in"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live fr`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.fr"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live in`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.in"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live nl`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.nl"))
    }

    @Test
    fun `isFreeEmailProvider returns true for aim`() {
        assertTrue(EmailValidator.isFreeEmailProvider("aim.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for pm me`() {
        assertTrue(EmailValidator.isFreeEmailProvider("pm.me"))
    }

    @Test
    fun `isFreeEmailProvider returns true for tutanota de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("tutanota.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for tuta io`() {
        assertTrue(EmailValidator.isFreeEmailProvider("tuta.io"))
    }

    @Test
    fun `isFreeEmailProvider returns true for zohomail`() {
        assertTrue(EmailValidator.isFreeEmailProvider("zohomail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for mailfence`() {
        assertTrue(EmailValidator.isFreeEmailProvider("mailfence.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yandex ru`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yandex.ru"))
    }

    @Test
    fun `isFreeEmailProvider returns true for ya ru`() {
        assertTrue(EmailValidator.isFreeEmailProvider("ya.ru"))
    }

    @Test
    fun `isFreeEmailProvider returns true for inbox ru`() {
        assertTrue(EmailValidator.isFreeEmailProvider("inbox.ru"))
    }

    @Test
    fun `isFreeEmailProvider returns true for list ru`() {
        assertTrue(EmailValidator.isFreeEmailProvider("list.ru"))
    }

    @Test
    fun `isFreeEmailProvider returns true for bk ru`() {
        assertTrue(EmailValidator.isFreeEmailProvider("bk.ru"))
    }

    @Test
    fun `isFreeEmailProvider returns true for 126 com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("126.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for sina com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("sina.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for sohu com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("sohu.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for freenet de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("freenet.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for t-online de`() {
        assertTrue(EmailValidator.isFreeEmailProvider("t-online.de"))
    }

    @Test
    fun `isFreeEmailProvider returns true for wanadoo fr`() {
        assertTrue(EmailValidator.isFreeEmailProvider("wanadoo.fr"))
    }

    @Test
    fun `isFreeEmailProvider returns true for free fr`() {
        assertTrue(EmailValidator.isFreeEmailProvider("free.fr"))
    }

    @Test
    fun `isFreeEmailProvider returns true for laposte net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("laposte.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for virgilio it`() {
        assertTrue(EmailValidator.isFreeEmailProvider("virgilio.it"))
    }

    @Test
    fun `isFreeEmailProvider returns true for alice it`() {
        assertTrue(EmailValidator.isFreeEmailProvider("alice.it"))
    }

    @Test
    fun `isFreeEmailProvider returns true for tiscali it`() {
        assertTrue(EmailValidator.isFreeEmailProvider("tiscali.it"))
    }

    @Test
    fun `isFreeEmailProvider returns true for sky com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("sky.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for talktalk net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("talktalk.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for rogers com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("rogers.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for telus net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("telus.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for daum net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("daum.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for hanmail net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hanmail.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for optusnet com au`() {
        assertTrue(EmailValidator.isFreeEmailProvider("optusnet.com.au"))
    }

    @Test
    fun `isFreeEmailProvider returns true for sbcglobal net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("sbcglobal.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for cox net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("cox.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for charter net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("charter.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for earthlink net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("earthlink.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for email com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("email.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for fastmail fm`() {
        assertTrue(EmailValidator.isFreeEmailProvider("fastmail.fm"))
    }

    @Test
    fun `isFreeEmailProvider returns true for aol co uk`() {
        assertTrue(EmailValidator.isFreeEmailProvider("aol.co.uk"))
    }

    @Test
    fun `isFreeEmailProvider returns true for rediff com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("rediff.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for googlemail co uk`() {
        assertTrue(EmailValidator.isFreeEmailProvider("googlemail.co.uk"))
    }

    @Test
    fun `isFreeEmailProvider returns true for gmx at`() {
        assertTrue(EmailValidator.isFreeEmailProvider("gmx.at"))
    }

    @Test
    fun `isFreeEmailProvider returns true for gmx ch`() {
        assertTrue(EmailValidator.isFreeEmailProvider("gmx.ch"))
    }

    @Test
    fun `isFreeEmailProvider returns true for hotmail it`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.it"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook es`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.es"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook it`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.it"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook com au`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.com.au"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo com br`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.com.br"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo com mx`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.com.mx"))
    }

    @Test
    fun `isFreeEmailProvider returns true for ig com br`() {
        assertTrue(EmailValidator.isFreeEmailProvider("ig.com.br"))
    }

    @Test
    fun `isFreeEmailProvider returns true for bol com br`() {
        assertTrue(EmailValidator.isFreeEmailProvider("bol.com.br"))
    }

    @Test
    fun `isFreeEmailProvider returns true for terra com br`() {
        assertTrue(EmailValidator.isFreeEmailProvider("terra.com.br"))
    }
}
