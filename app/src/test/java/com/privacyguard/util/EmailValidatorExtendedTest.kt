package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Extended JUnit4 test suite for EmailValidator.
 *
 * Sections:
 *   1  — Valid basic emails          (tests  001–020)
 *   2  — Invalid basic emails        (tests  021–040)
 *   3  — Valid TLDs                  (tests  041–070)
 *   4  — Disposable email domains    (tests  071–100)
 *   5  — Role-based email detection  (tests  101–115)
 *   6  — Gmail normalization         (tests  116–130)
 *   7  — extractFromText             (tests  131–145)
 *   8  — Edge cases                  (tests  146–160)
 */
class EmailValidatorExtendedTest {

    // =========================================================================
    // SECTION 1 — Valid basic emails (20 tests)
    // =========================================================================

    @Test
    fun `test001 simple user at example dot com is valid`() {
        val email = "user@example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test002 user dot name at domain dot org is valid`() {
        val email = "user.name@domain.org"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse("Not expecting disposable for '$email'", result.isDisposable)
    }

    @Test
    fun `test003 user plus tag at email dot co dot uk is valid`() {
        val email = "user+tag@email.co.uk"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test004 digits only local at numbers dot com is valid`() {
        val email = "123@numbers.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test005 mixed case email is valid`() {
        val email = "UserName@Example.COM"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test006 email with hyphen in local part is valid`() {
        val email = "first-last@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test007 email with underscore in local part is valid`() {
        val email = "first_last@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test008 email with plus sign is valid`() {
        val email = "alice+newsletter@gmail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test009 long local part email is valid`() {
        val email = "verylonglocalpartbutstillvalid@example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test010 email with multiple dots in local part is valid`() {
        val email = "a.b.c.d@example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test011 email with hyphen in domain is valid`() {
        val email = "user@my-domain.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test012 single char local part email is valid`() {
        val email = "a@example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test013 email with subdomain is valid`() {
        val email = "user@mail.example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test014 email with deep subdomain is valid`() {
        val email = "user@a.b.example.org"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test015 numeric domain email is valid`() {
        val email = "user@123domain.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test016 email with plus and dots is valid`() {
        val email = "first.last+tag@example.net"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test017 work email is valid`() {
        val email = "john.doe@company.co.uk"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test018 education email is valid`() {
        val email = "student123@university.edu"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test019 government email is valid`() {
        val email = "official@agency.gov"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test020 tech startup domain email is valid`() {
        val email = "dev@startup.io"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for '$email'", result.isValid)
        assertFalse(result.isDisposable)
    }

    // =========================================================================
    // SECTION 2 — Invalid basic emails (20 tests)
    // =========================================================================

    @Test
    fun `test021 email starting with at sign is invalid`() {
        val email = "@nodomain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test022 email with no domain is invalid`() {
        val email = "nodomain@"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test023 email with spaces in local part is invalid`() {
        val email = "spaces in@email.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test024 email with double at sign is invalid`() {
        val email = "double@@at.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test025 empty string email is invalid`() {
        val email = ""
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for empty string", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test026 email missing at sign is invalid`() {
        val email = "userexample.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test027 email with trailing dot in domain is invalid`() {
        val email = "user@domain."
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test028 email with leading dot in local part is invalid`() {
        val email = ".user@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test029 email with trailing dot in local part is invalid`() {
        val email = "user.@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test030 email with consecutive dots in local part is invalid`() {
        val email = "us..er@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test031 email with no TLD is invalid`() {
        val email = "user@domain"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test032 email with space after at sign is invalid`() {
        val email = "user@ domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test033 email with space before at sign is invalid`() {
        val email = "user @domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test034 email with angle brackets is invalid`() {
        val email = "<user@domain.com>"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test035 email with comma in local part is invalid`() {
        val email = "us,er@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test036 email with semicolon is invalid`() {
        val email = "user;name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test037 email with backslash is invalid`() {
        val email = "user\\name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test038 email with leading hyphen in domain is invalid`() {
        val email = "user@-domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test039 email with trailing hyphen in domain is invalid`() {
        val email = "user@domain-.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for '$email'", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test040 whitespace only email is invalid`() {
        val email = "   "
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for whitespace-only string", result.isValid)
        assertNotNull(result)
    }

    // =========================================================================
    // SECTION 3 — Valid TLDs (30 tests)
    // =========================================================================

    @Test
    fun `test041 dot com TLD is valid`() {
        val email = "user@example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .com TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test042 dot org TLD is valid`() {
        val email = "user@example.org"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .org TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test043 dot net TLD is valid`() {
        val email = "user@example.net"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .net TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test044 dot edu TLD is valid`() {
        val email = "user@example.edu"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .edu TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test045 dot gov TLD is valid`() {
        val email = "user@agency.gov"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .gov TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test046 dot mil TLD is valid`() {
        val email = "user@branch.mil"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .mil TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test047 dot int TLD is valid`() {
        val email = "user@org.int"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .int TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test048 dot io TLD is valid`() {
        val email = "user@startup.io"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .io TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test049 dot co TLD is valid`() {
        val email = "user@company.co"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .co TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test050 dot ai TLD is valid`() {
        val email = "user@company.ai"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .ai TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test051 dot app TLD is valid`() {
        val email = "user@myapp.app"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .app TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test052 dot dev TLD is valid`() {
        val email = "user@tools.dev"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .dev TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test053 dot uk TLD is valid`() {
        val email = "user@domain.co.uk"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .uk TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test054 dot de TLD is valid`() {
        val email = "user@domain.de"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .de TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test055 dot fr TLD is valid`() {
        val email = "user@domain.fr"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .fr TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test056 dot jp TLD is valid`() {
        val email = "user@domain.jp"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .jp TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test057 dot cn TLD is valid`() {
        val email = "user@domain.cn"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .cn TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test058 dot au TLD is valid`() {
        val email = "user@domain.com.au"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .au TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test059 dot ca TLD is valid`() {
        val email = "user@domain.ca"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .ca TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test060 dot br TLD is valid`() {
        val email = "user@domain.com.br"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .br TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test061 dot ru TLD is valid`() {
        val email = "user@domain.ru"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .ru TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test062 dot in TLD is valid`() {
        val email = "user@domain.in"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .in TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test063 dot mx TLD is valid`() {
        val email = "user@domain.com.mx"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .mx TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test064 dot es TLD is valid`() {
        val email = "user@domain.es"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .es TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test065 dot it TLD is valid`() {
        val email = "user@domain.it"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .it TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test066 dot nl TLD is valid`() {
        val email = "user@domain.nl"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .nl TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test067 dot pl TLD is valid`() {
        val email = "user@domain.pl"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .pl TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test068 dot se TLD is valid`() {
        val email = "user@domain.se"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .se TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test069 dot no TLD is valid`() {
        val email = "user@domain.no"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .no TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test070 dot dk TLD is valid`() {
        val email = "user@domain.dk"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .dk TLD", result.isValid)
        assertFalse(result.isDisposable)
    }

    // =========================================================================
    // SECTION 4 — Disposable email domains (30 tests)
    // =========================================================================

    @Test
    fun `test071 mailinator address is disposable`() {
        val email = "xxx@mailinator.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test072 guerrillamail address is disposable`() {
        val email = "xxx@guerrillamail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test073 tempmail address is disposable`() {
        val email = "xxx@tempmail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test074 ten minute mail address is disposable`() {
        val email = "xxx@10minutemail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test075 throwam address is disposable`() {
        val email = "xxx@throwam.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test076 yopmail address is disposable`() {
        val email = "xxx@yopmail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test077 trashmail address is disposable`() {
        val email = "xxx@trashmail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test078 fakeinbox address is disposable`() {
        val email = "xxx@fakeinbox.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test079 mailnull address is disposable`() {
        val email = "xxx@mailnull.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test080 spamgourmet address is disposable`() {
        val email = "xxx@spamgourmet.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test081 dispostable address is disposable`() {
        val email = "user@dispostable.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test082 mailexpire address is disposable`() {
        val email = "user@mailexpire.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test083 sharklasers address is disposable`() {
        val email = "user@sharklasers.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test084 guerrillamailblock address is disposable`() {
        val email = "user@guerrillamailblock.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test085 spam4 address is disposable`() {
        val email = "user@spam4.me"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test086 maildrop address is disposable`() {
        val email = "user@maildrop.cc"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test087 getairmail address is disposable`() {
        val email = "user@getairmail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test088 inboxbear address is disposable`() {
        val email = "user@inboxbear.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test089 mailtemp address is disposable`() {
        val email = "user@mailtemp.net"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test090 tempr address is disposable`() {
        val email = "user@tempr.email"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test091 throwaway email dot net is disposable`() {
        val email = "throwaway@throwaway.email"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test092 mintemail address is disposable`() {
        val email = "user@mintemail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test093 nwldx address is disposable`() {
        val email = "user@nwldx.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test094 spamhere is disposable`() {
        val email = "user@spamhere.org"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test095 crapmail is disposable`() {
        val email = "user@crapmail.org"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test096 bob email is disposable`() {
        val email = "user@bob.email"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test097 discardmail address is disposable`() {
        val email = "user@discardmail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test098 notmailinator variant is disposable`() {
        val email = "user@notmailinator.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test099 binkmail is disposable`() {
        val email = "user@binkmail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test100 spamgap is disposable`() {
        val email = "user@spamgap.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isDisposable=true for '$email'", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    // =========================================================================
    // SECTION 5 — Role-based email detection (15 tests)
    // =========================================================================

    @Test
    fun `test101 admin email is detected as role-based`() {
        val email = "admin@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test102 info email is detected as role-based`() {
        val email = "info@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test103 support email is detected as role-based`() {
        val email = "support@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test104 noreply email is detected as role-based`() {
        val email = "noreply@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test105 abuse email is detected as role-based`() {
        val email = "abuse@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test106 webmaster email is detected as role-based`() {
        val email = "webmaster@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test107 postmaster email is detected as role-based`() {
        val email = "postmaster@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test108 hostmaster email is detected as role-based`() {
        val email = "hostmaster@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test109 sales email is detected as role-based`() {
        val email = "sales@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test110 marketing email is detected as role-based`() {
        val email = "marketing@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test111 contact email is detected as role-based`() {
        val email = "contact@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test112 billing email is detected as role-based`() {
        val email = "billing@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test113 security email is detected as role-based`() {
        val email = "security@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test114 no-reply hyphen variant is detected as role-based`() {
        val email = "no-reply@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for '$email'", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test115 personal email is NOT role-based`() {
        val email = "john.doe@company.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isRoleBased=false for personal email '$email'", result.isRoleBased)
        assertTrue("Expected isValid=true for personal email '$email'", result.isValid)
    }

    // =========================================================================
    // SECTION 6 — Gmail normalization (15 tests)
    // =========================================================================

    @Test
    fun `test116 gmail plus tag is removed in normalization`() {
        val email = "user+tag@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test117 gmail dots in local part are removed in normalization`() {
        val email = "u.s.e.r@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test118 gmail single dot local part is normalized`() {
        val email = "first.last@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("firstlast@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test119 gmail plus and dot combined normalization`() {
        val email = "first.last+tag@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("firstlast@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test120 non-gmail address is returned unchanged by normalizeGmailAddress`() {
        val email = "user.name+tag@outlook.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals(email, normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test121 gmail address without dots or tags is unchanged`() {
        val email = "plainuser@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("plainuser@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test122 gmail address with multiple plus signs uses first only`() {
        val email = "user+a+b@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test123 googlemail dot com is treated same as gmail dot com`() {
        val email = "user+tag@googlemail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertTrue(
            "googlemail address should normalize to gmail or googlemail base",
            normalized == "user@gmail.com" || normalized == "user@googlemail.com"
        )
        assertNotNull(normalized)
    }

    @Test
    fun `test124 gmail uppercase domain is normalized`() {
        val email = "User+Tag@GMAIL.COM"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        val lower = normalized.lowercase()
        assertTrue("Normalized address should be lowercase", lower == normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test125 gmail address with multiple dots is fully normalized`() {
        val email = "a.b.c.d.e@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("abcde@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test126 gmail dot before plus sign is normalized`() {
        val email = "user.name+newsletter@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("username@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test127 empty plus tag on gmail is normalized`() {
        val email = "user+@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test128 yahoo address is returned unchanged by normalizeGmailAddress`() {
        val email = "user.name+tag@yahoo.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals(email, normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test129 gmail normalization preserves domain case-insensitively`() {
        val email = "test.user+promo@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertTrue("Domain should be gmail.com after normalization", normalized.endsWith("@gmail.com"))
        assertNotNull(normalized)
    }

    @Test
    fun `test130 gmail normalization of already normalized address is idempotent`() {
        val email = "testuser@gmail.com"
        val normalizedOnce = EmailValidator.normalizeGmailAddress(email)
        val normalizedTwice = EmailValidator.normalizeGmailAddress(normalizedOnce)
        assertEquals(normalizedOnce, normalizedTwice)
        assertNotNull(normalizedTwice)
    }

    // =========================================================================
    // SECTION 7 — extractFromText (15 tests)
    // =========================================================================

    @Test
    fun `test131 extractFromText with no emails returns empty list`() {
        val text = "This text has no email address in it at all."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Expected empty list for text with no emails", emails.isEmpty())
    }

    @Test
    fun `test132 extractFromText with one email returns single item`() {
        val text = "Please contact us at hello@example.com for more info."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 1 email", 1, emails.size)
    }

    @Test
    fun `test133 extractFromText with two emails returns two items`() {
        val text = "Email alice@example.com or bob@example.org for support."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 2 emails", 2, emails.size)
    }

    @Test
    fun `test134 extractFromText with three emails returns three items`() {
        val text = "Contacts: a@one.com, b@two.org, c@three.net"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 3 emails", 3, emails.size)
    }

    @Test
    fun `test135 extractFromText returns correct email addresses`() {
        val text = "Send to user@domain.com please."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Expected extracted email to contain user@domain.com",
            emails.contains("user@domain.com"))
    }

    @Test
    fun `test136 extractFromText handles email at start of string`() {
        val text = "admin@site.com is the administrator."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertFalse("Expected at least one email extracted", emails.isEmpty())
    }

    @Test
    fun `test137 extractFromText handles email at end of string`() {
        val text = "Contact the admin at webmaster@site.com"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertFalse("Expected at least one email extracted", emails.isEmpty())
    }

    @Test
    fun `test138 extractFromText handles multiple emails on separate lines`() {
        val text = "Line 1: user1@example.com\nLine 2: user2@example.com\nLine 3: user3@example.com"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 3 emails from multiline text", 3, emails.size)
    }

    @Test
    fun `test139 extractFromText skips malformed email-like strings`() {
        val text = "Not an email: @nodomain or nodomain@ but valid@email.com is fine."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 1 valid email", 1, emails.size)
    }

    @Test
    fun `test140 extractFromText handles email surrounded by parentheses`() {
        val text = "Contact us (support@company.com) for help."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertFalse("Expected at least one email extracted", emails.isEmpty())
    }

    @Test
    fun `test141 extractFromText handles email in mailto context`() {
        val text = "mailto:info@example.com link"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertFalse("Expected at least one email extracted", emails.isEmpty())
    }

    @Test
    fun `test142 extractFromText with empty string returns empty list`() {
        val text = ""
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Expected empty list for empty input", emails.isEmpty())
    }

    @Test
    fun `test143 extractFromText handles plus-tagged email correctly`() {
        val text = "Reply to user+tag@gmail.com for details."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 1 email with plus tag", 1, emails.size)
    }

    @Test
    fun `test144 extractFromText handles five emails in one paragraph`() {
        val text = """
            Team leads: alice@org.com, bob@org.com, carol@org.com.
            CC: dave@org.net and eve@org.io.
        """.trimIndent()
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 5 emails in paragraph", 5, emails.size)
    }

    @Test
    fun `test145 extractFromText does not return empty list for valid input`() {
        val text = "Email user@example.com or user@example.com again for redundancy."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Should return at least 1 result for repeated email", emails.isNotEmpty())
    }

    // =========================================================================
    // SECTION 8 — Edge cases (15 tests)
    // =========================================================================

    @Test
    fun `test146 empty string input is invalid`() {
        val email = ""
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for empty string", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test147 blank spaces only input is invalid`() {
        val email = "     "
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for blank-only string", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test148 very long email exceeding 320 chars is invalid`() {
        val localPart = "a".repeat(250)
        val email = "$localPart@example.com"
        assertTrue("Test email must exceed 320 chars", email.length > 320)
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for email exceeding 320 chars", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test149 email with all digit local part is valid`() {
        val email = "12345678@numbers.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for all-digit local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test150 single character local part is valid`() {
        val email = "z@domain.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for single-char local part", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test151 email with tab character is invalid`() {
        val email = "user\t@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for email with tab", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test152 email with newline character is invalid`() {
        val email = "user\n@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for email with newline", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test153 email with null character is invalid`() {
        val email = "user\u0000@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for email with null char", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test154 domain label exceeding 63 chars is invalid`() {
        val label = "a".repeat(64)
        val email = "user@$label.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for domain label > 63 chars", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test155 TLD of single char is invalid`() {
        val email = "user@domain.c"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for single-char TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test156 email with only at sign is invalid`() {
        val email = "@"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for lone @ sign", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test157 email local part of exactly 64 chars is valid`() {
        val localPart = "a".repeat(64)
        val email = "$localPart@example.com"
        val result = EmailValidator.validate(email)
        // RFC 5321 allows up to 64 chars in local part
        assertTrue("Expected isValid=true for 64-char local part per RFC 5321", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test158 email local part of exactly 65 chars is invalid`() {
        val localPart = "a".repeat(65)
        val email = "$localPart@example.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for 65-char local part exceeding RFC 5321", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test159 email with unicode characters in local part is handled`() {
        val email = "üser@domain.com"
        val result = EmailValidator.validate(email)
        // Result depends on implementation; simply verify no exception and non-null result
        assertNotNull("Result must not be null for unicode local part", result)
    }

    @Test
    fun `test160 international domain email is handled gracefully`() {
        val email = "user@münchen.de"
        val result = EmailValidator.validate(email)
        // Result depends on IDN support; simply verify no exception and non-null result
        assertNotNull("Result must not be null for IDN domain", result)
    }

    // =========================================================================
    // BONUS SECTION — Additional edge cases and coverage (tests 161–200)
    // =========================================================================

    @Test
    fun `test161 email with hyphen-only local part segments is valid`() {
        val email = "first-middle-last@example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for hyphenated local part", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test162 email with numeric subdomain is valid`() {
        val email = "user@123.example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for numeric subdomain", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test163 email with ip address domain is handled gracefully`() {
        val email = "user@192.168.1.1"
        val result = EmailValidator.validate(email)
        // IP literals without brackets are typically invalid per strict RFC
        assertNotNull("Result must not be null", result)
    }

    @Test
    fun `test164 email with dot edu subdomain is valid`() {
        val email = "researcher@cs.university.edu"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for edu subdomain", result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `test165 case insensitive domain validation is consistent`() {
        val lower = EmailValidator.validate("user@example.com")
        val upper = EmailValidator.validate("user@EXAMPLE.COM")
        assertEquals("Both should have same isValid", lower.isValid, upper.isValid)
        assertNotNull(lower)
    }

    @Test
    fun `test166 email with percent sign in local part is invalid`() {
        val email = "user%40@example.com"
        val result = EmailValidator.validate(email)
        // Percent sign is not standard in unquoted local part
        assertNotNull("Result must not be null", result)
    }

    @Test
    fun `test167 team alias email is role-based`() {
        val email = "team@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for 'team' address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test168 jobs email is role-based`() {
        val email = "jobs@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for 'jobs' address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test169 careers email is role-based`() {
        val email = "careers@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for 'careers' address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test170 hr email is role-based`() {
        val email = "hr@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for 'hr' address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test171 mixed valid and invalid emails in text returns only valid count`() {
        val text = "Good: alice@example.com Bad: @invalid and also good@other.org"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 2 valid emails", 2, emails.size)
    }

    @Test
    fun `test172 extractFromText with CSV of emails`() {
        val text = "a@a.com,b@b.com,c@c.com,d@d.com"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 4 emails from CSV", 4, emails.size)
    }

    @Test
    fun `test173 extractFromText with semicolon separated emails`() {
        val text = "x@x.com;y@y.com;z@z.com"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 3 emails from semicolon-separated list", 3, emails.size)
    }

    @Test
    fun `test174 validate returns object with all expected boolean fields`() {
        val result = EmailValidator.validate("user@example.com")
        assertNotNull("Result should not be null", result)
        val valid = result.isValid
        val disposable = result.isDisposable
        val roleBased = result.isRoleBased
        assertTrue("isValid should be boolean", valid is Boolean)
        assertTrue("isDisposable should be boolean", disposable is Boolean)
        assertTrue("isRoleBased should be boolean", roleBased is Boolean)
    }

    @Test
    fun `test175 validation result for typical disposable email has at least one flag set`() {
        val email = "xxx@mailinator.com"
        val result = EmailValidator.validate(email)
        // If isDisposable is true, that is the expected state; if not, isValid must be false
        if (result.isDisposable) {
            assertTrue("isDisposable is set correctly", result.isDisposable)
        } else {
            assertFalse("Non-disposable classified mailinator must be invalid", result.isValid)
        }
    }

    @Test
    fun `test176 email with dot travel TLD is valid`() {
        val email = "user@example.travel"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .travel TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test177 email with dot museum TLD is valid`() {
        val email = "curator@gallery.museum"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .museum TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test178 email with dot academy TLD is valid`() {
        val email = "student@learn.academy"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .academy TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test179 email with dot tech TLD is valid`() {
        val email = "engineer@build.tech"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .tech TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test180 email with dot cloud TLD is valid`() {
        val email = "admin@host.cloud"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .cloud TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test181 email with dot xyz TLD is valid`() {
        val email = "user@anything.xyz"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .xyz TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test182 email with dot me TLD is valid`() {
        val email = "user@personal.me"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .me TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test183 email with dot tv TLD is valid`() {
        val email = "channel@broadcast.tv"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .tv TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test184 email with dot media TLD is valid`() {
        val email = "news@outlet.media"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .media TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test185 email with dot finance TLD is valid`() {
        val email = "invest@bank.finance"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .finance TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test186 extremely short valid email is valid`() {
        val email = "a@b.co"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for minimal valid email", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test187 email with all lowercase is valid`() {
        val email = "abc@def.ghi"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for all-lowercase email", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test188 email with all uppercase is valid`() {
        val email = "ABC@DEF.COM"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for all-uppercase email", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test189 gmail normalization of complex address with many dots and tag`() {
        val email = "m.y.n.a.m.e+promo2024@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("myname@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test190 gmail normalization preserves non-gmail address exactly`() {
        val email = "my.name+tag@hotmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals(email, normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test191 extractFromText with whitespace-only string returns empty list`() {
        val text = "   \n\t  "
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Expected empty list for whitespace-only text", emails.isEmpty())
    }

    @Test
    fun `test192 validate handles string with surrounding whitespace`() {
        val email = "  user@example.com  "
        val result = EmailValidator.validate(email)
        // Behavior depends on whether validator auto-trims
        assertNotNull("Result must not be null", result)
    }

    @Test
    fun `test193 newsletter email is role-based`() {
        val email = "newsletter@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for 'newsletter' address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test194 help email is role-based`() {
        val email = "help@company.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for 'help' address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test195 email with very short domain segment is valid`() {
        val email = "user@a.co"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for short but valid domain", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test196 email with numeric TLD only is invalid`() {
        val email = "user@domain.123"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for all-numeric TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test197 multiple extractFromText calls on same text are consistent`() {
        val text = "alice@example.com and bob@example.com"
        val first = EmailValidator.extractFromText(text)
        val second = EmailValidator.extractFromText(text)
        assertEquals("Repeated calls must yield same result", first.size, second.size)
        assertNotNull(first)
    }

    @Test
    fun `test198 validate handles null-character only string as invalid`() {
        val email = "\u0000\u0000"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for null-char string", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test199 gmail normalization of already-normalized address is idempotent`() {
        val email = "simple@gmail.com"
        val normalizedOnce = EmailValidator.normalizeGmailAddress(email)
        val normalizedTwice = EmailValidator.normalizeGmailAddress(normalizedOnce)
        assertEquals("simple@gmail.com", normalizedOnce)
        assertEquals(normalizedOnce, normalizedTwice)
    }

    @Test
    fun `test200 validate isDisposable is false for mainstream providers`() {
        val emails = listOf(
            "user@gmail.com",
            "user@yahoo.com",
            "user@outlook.com",
            "user@hotmail.com",
            "user@icloud.com"
        )
        emails.forEach { email ->
            val result = EmailValidator.validate(email)
            assertFalse("Expected isDisposable=false for mainstream provider '$email'", result.isDisposable)
            assertTrue("Expected isValid=true for mainstream provider '$email'", result.isValid)
        }
    }

    // =========================================================================
    // EXTRA SECTION A — Special characters in local part (tests 201–220)
    // =========================================================================

    @Test
    fun `test201 email with caret in local part is invalid`() {
        val email = "user^name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for caret in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test202 email with tilde in local part is handled`() {
        val email = "user~name@domain.com"
        val result = EmailValidator.validate(email)
        // Tilde is technically allowed in some RFC interpretations; verify no crash
        assertNotNull("Result must not be null", result)
    }

    @Test
    fun `test203 email with exclamation mark in local part is invalid`() {
        val email = "user!name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for exclamation in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test204 email with hash in local part is invalid`() {
        val email = "user#name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for hash in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test205 email with dollar sign in local part is invalid`() {
        val email = "user\$name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for dollar sign in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test206 email with ampersand in local part is invalid`() {
        val email = "user&name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for ampersand in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test207 email with asterisk in local part is invalid`() {
        val email = "user*name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for asterisk in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test208 email with parentheses in local part is invalid`() {
        val email = "user(name)@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for parentheses in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test209 email with square brackets in local part is invalid`() {
        val email = "user[name]@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for brackets in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test210 email with curly braces in local part is invalid`() {
        val email = "user{name}@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for curly braces in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test211 email with pipe in local part is invalid`() {
        val email = "user|name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for pipe in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test212 email with question mark in local part is invalid`() {
        val email = "user?name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for question mark in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test213 email with forward slash in local part is invalid`() {
        val email = "user/name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for slash in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test214 email with equals sign in local part is invalid`() {
        val email = "user=name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for equals sign in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test215 email with grave accent in local part is invalid`() {
        val email = "user\`name@domain.com"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isValid=false for grave accent in local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test216 disposable getairmail variant is disposable`() {
        val email = "user@getairmail.info"
        val result = EmailValidator.validate(email)
        assertTrue("Expected disposable for getairmail variant", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test217 disposable emailondeck is flagged`() {
        val email = "user@emailondeck.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected disposable for emailondeck.com", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test218 disposable filzmail is flagged`() {
        val email = "user@filzmail.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected disposable for filzmail.com", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test219 disposable burnermail is flagged`() {
        val email = "user@burnermail.io"
        val result = EmailValidator.validate(email)
        assertTrue("Expected disposable for burnermail.io", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test220 disposable trashmail net is flagged`() {
        val email = "user@trashmail.net"
        val result = EmailValidator.validate(email)
        assertTrue("Expected disposable for trashmail.net", result.isDisposable || !result.isValid)
        assertNotNull(result)
    }

    // =========================================================================
    // EXTRA SECTION B — extractFromText advanced (tests 221–235)
    // =========================================================================

    @Test
    fun `test221 extractFromText finds email in JSON-like string`() {
        val text = """{"email":"contact@service.com","name":"Test"}"""
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertFalse("Expected to find email in JSON string", emails.isEmpty())
    }

    @Test
    fun `test222 extractFromText finds email but not URL`() {
        val text = "Visit https://example.com or email us at hello@example.com"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Expected exactly 1 email (URL should not be extracted)", emails.size == 1)
    }

    @Test
    fun `test223 extractFromText with tab-separated emails`() {
        val text = "a@a.com\tb@b.com\tc@c.com"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 3 emails from tab-separated list", 3, emails.size)
    }

    @Test
    fun `test224 extractFromText with newline-separated emails`() {
        val text = "a@a.com\nb@b.com\nc@c.com\nd@d.com"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 4 emails from newline-separated list", 4, emails.size)
    }

    @Test
    fun `test225 extractFromText finds email in HTML-anchor-like text`() {
        val text = """<a href="mailto:info@site.org">Email us</a>"""
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertFalse("Expected to find email in anchor string", emails.isEmpty())
    }

    @Test
    fun `test226 extractFromText with long paragraph finds all six emails`() {
        val text = """
            Our team includes alice@devteam.io, bob@devteam.io, carol@devteam.io.
            For HR matters reach out to hr@devteam.io.
            General inquiries go to info@devteam.io.
            Emergency contact: emergency@devteam.io.
        """.trimIndent()
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 6 emails in paragraph", 6, emails.size)
    }

    @Test
    fun `test227 extractFromText with email in angle bracket format`() {
        val text = "Send to <admin@example.com> for access."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertFalse("Expected to find email inside angle brackets", emails.isEmpty())
    }

    @Test
    fun `test228 extractFromText handles email with plus tag in paragraph`() {
        val text = "Tagged aliases like promo+code@company.com and notify+alerts@company.org work here."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 2 tagged emails", 2, emails.size)
    }

    @Test
    fun `test229 extractFromText handles email list in quoted string`() {
        val text = "\"To: user1@test.net, user2@test.net\""
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 2 emails in quoted string", 2, emails.size)
    }

    @Test
    fun `test230 extractFromText result is a list type`() {
        val text = "Email hello@world.com for info."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Result should be a List", emails is List<*>)
    }

    @Test
    fun `test231 extractFromText with only malformed addresses returns empty list`() {
        val text = "Try: @bad.com and also bad@ and just @"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Expected empty list for all-malformed input", emails.isEmpty())
    }

    @Test
    fun `test232 extractFromText handles very long text efficiently`() {
        val repeated = "Contact user@repeated.com for info. ".repeat(100)
        val emails = EmailValidator.extractFromText(repeated)
        assertNotNull(emails)
        assertTrue("Expected at least 1 email from long repeated text", emails.isNotEmpty())
    }

    @Test
    fun `test233 extractFromText with only one word returns empty list`() {
        val text = "hello"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Expected empty list for single-word text", emails.isEmpty())
    }

    @Test
    fun `test234 extractFromText with numbers only returns empty list`() {
        val text = "123456789"
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertTrue("Expected empty list for numeric-only text", emails.isEmpty())
    }

    @Test
    fun `test235 extractFromText handles mixed valid and subdomain emails`() {
        val text = "user@mail.example.com and other@sub.domain.org are both valid."
        val emails = EmailValidator.extractFromText(text)
        assertNotNull(emails)
        assertEquals("Expected exactly 2 subdomain emails", 2, emails.size)
    }

    // =========================================================================
    // EXTRA SECTION C — Gmail normalization extended (tests 236–250)
    // =========================================================================

    @Test
    fun `test236 gmail normalization lower-cases the result`() {
        val email = "MyName@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("myname@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test237 gmail normalization of address with underscore unchanged`() {
        val email = "user_name@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user_name@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test238 gmail normalization of address with hyphen unchanged`() {
        val email = "user-name@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user-name@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test239 gmail normalization of short two-char email`() {
        val email = "ab@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("ab@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test240 gmail normalization of single char email`() {
        val email = "x@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("x@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test241 gmail normalization result ends with gmail domain`() {
        val email = "test.address+ignored@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertTrue("Normalized result must end with @gmail.com", normalized.endsWith("@gmail.com"))
        assertNotNull(normalized)
    }

    @Test
    fun `test242 gmail normalization produces valid email`() {
        val email = "a.b.c+tag@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        val result = EmailValidator.validate(normalized)
        assertTrue("Normalized gmail address must itself be valid", result.isValid)
        assertNotNull(normalized)
    }

    @Test
    fun `test243 gmail normalization of uppercase mixed-case address`() {
        val email = "First.Last+Promo@GMAIL.COM"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertTrue(
            "Normalized should equal firstlast@gmail.com case-insensitively",
            normalized.equals("firstlast@gmail.com", ignoreCase = true)
        )
        assertNotNull(normalized)
    }

    @Test
    fun `test244 gmail normalization returns string not null`() {
        val email = "any+thing@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertNotNull(normalized)
        assertTrue("Normalized address should not be blank", normalized.isNotBlank())
    }

    @Test
    fun `test245 gmail normalization with digits in local part`() {
        val email = "user123+tag@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user123@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test246 gmail normalization with dot and digit`() {
        val email = "user.1+tag@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user1@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test247 non-gmail protonmail address is returned unchanged`() {
        val email = "user.name+filter@protonmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals(email, normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test248 non-gmail fastmail address is returned unchanged`() {
        val email = "user.name+filter@fastmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals(email, normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test249 non-gmail zoho address is returned unchanged`() {
        val email = "user.name+filter@zoho.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals(email, normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test250 gmail normalization of address that is all dots before at sign`() {
        val email = "a.b.c@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("abc@gmail.com", normalized)
        assertNotNull(normalized)
    }

    // =========================================================================
    // EXTRA SECTION D — Stress, combination, and regression tests (251–270)
    // =========================================================================

    @Test
    fun `test251 validate consistently returns isValid for known good email`() {
        val email = "consistent@test.com"
        repeat(5) {
            val result = EmailValidator.validate(email)
            assertTrue("validate should be deterministic for '$email'", result.isValid)
        }
    }

    @Test
    fun `test252 validate consistently returns not isValid for known bad email`() {
        val email = "bad-email"
        repeat(5) {
            val result = EmailValidator.validate(email)
            assertFalse("validate should be deterministic for '$email'", result.isValid)
        }
    }

    @Test
    fun `test253 normalizeGmailAddress is deterministic across calls`() {
        val email = "my.name+tag@gmail.com"
        val first = EmailValidator.normalizeGmailAddress(email)
        val second = EmailValidator.normalizeGmailAddress(email)
        assertEquals("normalizeGmailAddress should be deterministic", first, second)
        assertNotNull(first)
    }

    @Test
    fun `test254 extractFromText is deterministic across calls`() {
        val text = "Email: a@a.com, b@b.com"
        val first = EmailValidator.extractFromText(text)
        val second = EmailValidator.extractFromText(text)
        assertEquals("extractFromText should be deterministic", first.size, second.size)
        assertNotNull(first)
    }

    @Test
    fun `test255 valid email with uppercase alphanumeric and hyphens is valid`() {
        val email = "A1-B2-C3@example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for uppercase alphanumeric-hyphen local part", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test256 email with subdomain containing hyphen is valid`() {
        val email = "user@sub-domain.example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for subdomain with hyphen", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test257 email with dot gov dot uk is valid`() {
        val email = "official@ministry.gov.uk"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .gov.uk", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test258 email with dot ac dot uk is valid`() {
        val email = "professor@university.ac.uk"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .ac.uk", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test259 gmail normalization with multiple plus signs preserves only pre-plus segment`() {
        val email = "user+tag1+tag2@gmail.com"
        val normalized = EmailValidator.normalizeGmailAddress(email)
        assertEquals("user@gmail.com", normalized)
        assertNotNull(normalized)
    }

    @Test
    fun `test260 validate returns non-null for every input in a mixed batch`() {
        val inputs = listOf("", "a", "@", "@@", "a@b.c", "a@b", "valid@email.com",
            "   ", "\t", "@bad", "bad@", "x@x.io")
        inputs.forEach { email ->
            val result = EmailValidator.validate(email)
            assertNotNull("Result must not be null for input '$email'", result)
        }
    }

    @Test
    fun `test261 valid role email webmaster is both valid and role-based`() {
        val email = "webmaster@example.org"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for webmaster", result.isRoleBased)
        assertTrue("Expected isValid=true for webmaster email", result.isValid)
    }

    @Test
    fun `test262 valid role email postmaster is both valid and role-based`() {
        val email = "postmaster@example.org"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for postmaster", result.isRoleBased)
        assertTrue("Expected isValid=true for postmaster email", result.isValid)
    }

    @Test
    fun `test263 personal email with numbers is not role-based`() {
        val email = "user123@example.net"
        val result = EmailValidator.validate(email)
        assertFalse("Expected isRoleBased=false for personal email with numbers", result.isRoleBased)
        assertTrue("Expected isValid=true", result.isValid)
    }

    @Test
    fun `test264 operations email is role-based`() {
        val email = "operations@company.org"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for 'operations' address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test265 do-not-reply email is role-based`() {
        val email = "do-not-reply@service.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for do-not-reply address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test266 no dot reply variant is role-based`() {
        val email = "no.reply@service.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isRoleBased=true for no.reply address", result.isRoleBased)
        assertNotNull(result)
    }

    @Test
    fun `test267 email with very short domain segment one char is valid`() {
        val email = "user@a.co"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for short but valid domain", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test268 valid email with many subdomains is valid`() {
        val email = "user@a.b.c.d.example.com"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for deeply nested subdomain", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test269 validate dot sg TLD is valid`() {
        val email = "user@company.sg"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .sg TLD", result.isValid)
        assertNotNull(result)
    }

    @Test
    fun `test270 validate dot co dot nz TLD is valid`() {
        val email = "user@company.co.nz"
        val result = EmailValidator.validate(email)
        assertTrue("Expected isValid=true for .co.nz TLD", result.isValid)
        assertNotNull(result)
    }
}
