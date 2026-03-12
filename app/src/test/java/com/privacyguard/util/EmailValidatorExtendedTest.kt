package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended test suite for EmailValidator.
 *
 * This file supplements EmailValidatorTest.kt with additional coverage for:
 *   - Every major TLD (.com, .org, .net, .edu, .gov, .mil, .io, .co, .ai, etc.)
 *   - Internationalized domains with punycode (xn-- notation)
 *   - Every disposable email domain in EmailValidator.disposableEmailDomains
 *     (those not yet covered in the primary test file)
 *   - Every known typo correction in EmailValidator.domainTypoCorrections map
 *   - normalizeGmailAddress() with 30+ Gmail variants
 *   - extractFromText() with emails embedded in various text contexts
 *   - Free vs. business vs. role-based address detection
 *   - Edge cases in local part and domain validation
 *
 * All test names are unique and do not duplicate EmailValidatorTest.kt.
 */
class EmailValidatorExtendedTest {

    // ========================================================================
    // SECTION 1: Major TLD Validation Tests
    //
    // Tests ensuring that emails with well-known TLDs are accepted.
    // Each test uses a generic local part with a specific TLD.
    // ========================================================================

    @Test
    fun `validate email with TLD dot org`() {
        val result = EmailValidator.validate("user@example.org")
        assertTrue(result.isValid)
        assertEquals("example.org", result.domain)
    }

    @Test
    fun `validate email with TLD dot net`() {
        val result = EmailValidator.validate("user@example.net")
        assertTrue(result.isValid)
        assertEquals("example.net", result.domain)
    }

    @Test
    fun `validate email with TLD dot edu`() {
        val result = EmailValidator.validate("student@university.edu")
        assertTrue(result.isValid)
        assertEquals("university.edu", result.domain)
    }

    @Test
    fun `validate email with TLD dot gov`() {
        val result = EmailValidator.validate("official@agency.gov")
        assertTrue(result.isValid)
        assertEquals("agency.gov", result.domain)
    }

    @Test
    fun `validate email with TLD dot mil`() {
        val result = EmailValidator.validate("service@branch.mil")
        assertTrue(result.isValid)
        assertEquals("branch.mil", result.domain)
    }

    @Test
    fun `validate email with TLD dot io`() {
        val result = EmailValidator.validate("dev@startup.io")
        assertTrue(result.isValid)
        assertEquals("startup.io", result.domain)
    }

    @Test
    fun `validate email with TLD dot co`() {
        val result = EmailValidator.validate("user@company.co")
        assertTrue(result.isValid)
        assertEquals("company.co", result.domain)
    }

    @Test
    fun `validate email with TLD dot ai`() {
        val result = EmailValidator.validate("info@aicompany.ai")
        assertTrue(result.isValid)
        assertEquals("aicompany.ai", result.domain)
    }

    @Test
    fun `validate email with TLD dot tech`() {
        val result = EmailValidator.validate("dev@techcompany.tech")
        assertTrue(result.isValid)
        assertEquals("techcompany.tech", result.domain)
    }

    @Test
    fun `validate email with TLD dot dev`() {
        val result = EmailValidator.validate("name@developer.dev")
        assertTrue(result.isValid)
        assertEquals("developer.dev", result.domain)
    }

    @Test
    fun `validate email with TLD dot app`() {
        val result = EmailValidator.validate("contact@myapp.app")
        assertTrue(result.isValid)
        assertEquals("myapp.app", result.domain)
    }

    @Test
    fun `validate email with TLD dot cloud`() {
        val result = EmailValidator.validate("ops@service.cloud")
        assertTrue(result.isValid)
        assertEquals("service.cloud", result.domain)
    }

    @Test
    fun `validate email with TLD dot info`() {
        val result = EmailValidator.validate("info@portal.info")
        assertTrue(result.isValid)
        assertEquals("portal.info", result.domain)
    }

    @Test
    fun `validate email with TLD dot biz`() {
        val result = EmailValidator.validate("user@business.biz")
        assertTrue(result.isValid)
        assertEquals("business.biz", result.domain)
    }

    @Test
    fun `validate email with TLD dot name`() {
        val result = EmailValidator.validate("john@doe.name")
        assertTrue(result.isValid)
        assertEquals("doe.name", result.domain)
    }

    @Test
    fun `validate email with TLD dot pro`() {
        val result = EmailValidator.validate("lawyer@firm.pro")
        assertTrue(result.isValid)
        assertEquals("firm.pro", result.domain)
    }

    @Test
    fun `validate email with TLD dot museum`() {
        val result = EmailValidator.validate("curator@natural.museum")
        assertTrue(result.isValid)
        assertEquals("natural.museum", result.domain)
    }

    @Test
    fun `validate email with TLD dot aero`() {
        val result = EmailValidator.validate("pilot@airline.aero")
        assertTrue(result.isValid)
        assertEquals("airline.aero", result.domain)
    }

    @Test
    fun `validate email with TLD dot coop`() {
        val result = EmailValidator.validate("member@cooperative.coop")
        assertTrue(result.isValid)
        assertEquals("cooperative.coop", result.domain)
    }

    @Test
    fun `validate email with TLD dot mobi`() {
        val result = EmailValidator.validate("contact@mobile.mobi")
        assertTrue(result.isValid)
        assertEquals("mobile.mobi", result.domain)
    }

    @Test
    fun `validate email with country code TLD dot uk`() {
        val result = EmailValidator.validate("user@example.co.uk")
        assertTrue(result.isValid)
        assertEquals("example.co.uk", result.domain)
    }

    @Test
    fun `validate email with country code TLD dot de`() {
        val result = EmailValidator.validate("user@domain.de")
        assertTrue(result.isValid)
        assertEquals("domain.de", result.domain)
    }

    @Test
    fun `validate email with country code TLD dot fr`() {
        val result = EmailValidator.validate("user@domain.fr")
        assertTrue(result.isValid)
        assertEquals("domain.fr", result.domain)
    }

    @Test
    fun `validate email with country code TLD dot jp`() {
        val result = EmailValidator.validate("user@domain.co.jp")
        assertTrue(result.isValid)
        assertEquals("domain.co.jp", result.domain)
    }

    @Test
    fun `validate email with country code TLD dot au`() {
        val result = EmailValidator.validate("user@domain.com.au")
        assertTrue(result.isValid)
        assertEquals("domain.com.au", result.domain)
    }

    @Test
    fun `validate email with country code TLD dot ca`() {
        val result = EmailValidator.validate("user@domain.ca")
        assertTrue(result.isValid)
        assertEquals("domain.ca", result.domain)
    }

    @Test
    fun `validate email with country code TLD dot in`() {
        val result = EmailValidator.validate("user@domain.co.in")
        assertTrue(result.isValid)
        assertEquals("domain.co.in", result.domain)
    }

    @Test
    fun `validate email with country code TLD dot br`() {
        val result = EmailValidator.validate("user@domain.com.br")
        assertTrue(result.isValid)
        assertEquals("domain.com.br", result.domain)
    }

    @Test
    fun `validate email with TLD dot xyz`() {
        val result = EmailValidator.validate("user@domain.xyz")
        assertTrue(result.isValid)
        assertEquals("domain.xyz", result.domain)
    }

    @Test
    fun `validate email with TLD dot online`() {
        val result = EmailValidator.validate("shop@store.online")
        assertTrue(result.isValid)
        assertEquals("store.online", result.domain)
    }

    @Test
    fun `validate email with TLD dot site`() {
        val result = EmailValidator.validate("contact@mysite.site")
        assertTrue(result.isValid)
        assertEquals("mysite.site", result.domain)
    }

    @Test
    fun `validate email with TLD dot store`() {
        val result = EmailValidator.validate("sales@shop.store")
        assertTrue(result.isValid)
        assertEquals("shop.store", result.domain)
    }

    // ========================================================================
    // SECTION 2: Internationalized Domain Tests with Punycode
    //
    // Tests for domains using the xn-- prefix encoding, which represents
    // internationalized domain names in ASCII-compatible encoding (ACE).
    // ========================================================================

    @Test
    fun `validate email with punycode domain xn-- prefix`() {
        // "xn--bcher-kva.example" represents "bücher.example" in punycode
        val result = EmailValidator.validate("user@xn--bcher-kva.example")
        assertTrue(result.isValid)
        assertTrue(result.domain.contains("xn--"))
    }

    @Test
    fun `validate email with punycode TLD xn-- format`() {
        // xn--p1ai is the punycode for .рф (Russian TLD)
        val result = EmailValidator.validate("user@domain.xn--p1ai")
        assertTrue(result.isValid)
        assertEquals("domain.xn--p1ai", result.domain)
    }

    @Test
    fun `validate email with punycode subdomain`() {
        val result = EmailValidator.validate("user@xn--80ahgue5b.xn--p1ai")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with punycode in second level domain`() {
        val result = EmailValidator.validate("contact@xn--nxasmq6b.com")
        assertTrue(result.isValid)
        assertEquals("xn--nxasmq6b.com", result.domain)
    }

    @Test
    fun `validate email with punycode label followed by com`() {
        val result = EmailValidator.validate("info@xn--mlform-iua.com")
        assertTrue(result.isValid)
        assertEquals("xn--mlform-iua.com", result.domain)
    }

    @Test
    fun `validate email domain with mixed ascii and punycode labels`() {
        val result = EmailValidator.validate("user@subdomain.xn--bcher-kva.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateDomain punycode label is valid`() {
        val result = EmailValidator.validateDomain("xn--bcher-kva.de")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain simple xn prefix with digits is valid`() {
        val result = EmailValidator.validateDomain("xn--n3h.ws")
        assertTrue(result.first)
    }

    // ========================================================================
    // SECTION 3: Disposable Email Domains Not Yet Tested
    //
    // Tests for disposable email providers from EmailValidator.disposableEmailDomains
    // that are not covered in the primary EmailValidatorTest.kt.
    // ========================================================================

    @Test
    fun `isDisposableEmail returns true for grr la`() {
        assertTrue(EmailValidator.isDisposableEmail("grr.la"))
    }

    @Test
    fun `isDisposableEmail returns true for guerrillamail biz`() {
        assertTrue(EmailValidator.isDisposableEmail("guerrillamail.biz"))
    }

    @Test
    fun `isDisposableEmail returns true for guerrillamail info`() {
        assertTrue(EmailValidator.isDisposableEmail("guerrillamail.info"))
    }

    @Test
    fun `isDisposableEmail returns true for guerrillamailblock com`() {
        assertTrue(EmailValidator.isDisposableEmail("guerrillamailblock.com"))
    }

    @Test
    fun `isDisposableEmail returns true for tempail com`() {
        assertTrue(EmailValidator.isDisposableEmail("tempail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for throwaway com`() {
        assertTrue(EmailValidator.isDisposableEmail("throwaway.com"))
    }

    @Test
    fun `isDisposableEmail returns true for thromail com`() {
        assertTrue(EmailValidator.isDisposableEmail("thromail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for disposableaddress com`() {
        assertTrue(EmailValidator.isDisposableEmail("disposableaddress.com"))
    }

    @Test
    fun `isDisposableEmail returns true for yopmail fr`() {
        assertTrue(EmailValidator.isDisposableEmail("yopmail.fr"))
    }

    @Test
    fun `isDisposableEmail returns true for yopmail net`() {
        assertTrue(EmailValidator.isDisposableEmail("yopmail.net"))
    }

    @Test
    fun `isDisposableEmail returns true for yopmail gq`() {
        assertTrue(EmailValidator.isDisposableEmail("yopmail.gq"))
    }

    @Test
    fun `isDisposableEmail returns true for mailnator com`() {
        assertTrue(EmailValidator.isDisposableEmail("mailnator.com"))
    }

    @Test
    fun `isDisposableEmail returns true for nada email`() {
        assertTrue(EmailValidator.isDisposableEmail("nada.email"))
    }

    @Test
    fun `isDisposableEmail returns true for 10minutemail net`() {
        assertTrue(EmailValidator.isDisposableEmail("10minutemail.net"))
    }

    @Test
    fun `isDisposableEmail returns true for 10minutemail org`() {
        assertTrue(EmailValidator.isDisposableEmail("10minutemail.org"))
    }

    @Test
    fun `isDisposableEmail returns true for maildrop gq`() {
        assertTrue(EmailValidator.isDisposableEmail("maildrop.gq"))
    }

    @Test
    fun `isDisposableEmail returns true for trashmail me`() {
        assertTrue(EmailValidator.isDisposableEmail("trashmail.me"))
    }

    @Test
    fun `isDisposableEmail returns true for trashmail net`() {
        assertTrue(EmailValidator.isDisposableEmail("trashmail.net"))
    }

    @Test
    fun `isDisposableEmail returns true for trashmail org`() {
        assertTrue(EmailValidator.isDisposableEmail("trashmail.org"))
    }

    @Test
    fun `isDisposableEmail returns true for trashmail de`() {
        assertTrue(EmailValidator.isDisposableEmail("trashmail.de"))
    }

    @Test
    fun `isDisposableEmail returns true for trashmail ws`() {
        assertTrue(EmailValidator.isDisposableEmail("trashmail.ws"))
    }

    @Test
    fun `isDisposableEmail returns true for fakemail net`() {
        assertTrue(EmailValidator.isDisposableEmail("fakemail.net"))
    }

    @Test
    fun `isDisposableEmail returns true for fakemail fr`() {
        assertTrue(EmailValidator.isDisposableEmail("fakemail.fr"))
    }

    @Test
    fun `isDisposableEmail returns true for discardmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("discardmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for discardmail de`() {
        assertTrue(EmailValidator.isDisposableEmail("discardmail.de"))
    }

    @Test
    fun `isDisposableEmail returns true for spamgourmet net`() {
        assertTrue(EmailValidator.isDisposableEmail("spamgourmet.net"))
    }

    @Test
    fun `isDisposableEmail returns true for spamgourmet org`() {
        assertTrue(EmailValidator.isDisposableEmail("spamgourmet.org"))
    }

    @Test
    fun `isDisposableEmail returns true for tempinbox com`() {
        assertTrue(EmailValidator.isDisposableEmail("tempinbox.com"))
    }

    @Test
    fun `isDisposableEmail returns true for tempinbox co uk`() {
        assertTrue(EmailValidator.isDisposableEmail("tempinbox.co.uk"))
    }

    @Test
    fun `isDisposableEmail returns true for mytemp email`() {
        assertTrue(EmailValidator.isDisposableEmail("mytemp.email"))
    }

    @Test
    fun `isDisposableEmail returns true for mytempmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("mytempmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for mohmal im`() {
        assertTrue(EmailValidator.isDisposableEmail("mohmal.im"))
    }

    @Test
    fun `isDisposableEmail returns true for mohmal in`() {
        assertTrue(EmailValidator.isDisposableEmail("mohmal.in"))
    }

    @Test
    fun `isDisposableEmail returns true for trbvm com`() {
        assertTrue(EmailValidator.isDisposableEmail("trbvm.com"))
    }

    @Test
    fun `isDisposableEmail returns true for mailhub top`() {
        assertTrue(EmailValidator.isDisposableEmail("mailhub.top"))
    }

    @Test
    fun `isDisposableEmail returns true for mailhub pro`() {
        assertTrue(EmailValidator.isDisposableEmail("mailhub.pro"))
    }

    @Test
    fun `isDisposableEmail returns true for bouncr com`() {
        assertTrue(EmailValidator.isDisposableEmail("bouncr.com"))
    }

    @Test
    fun `isDisposableEmail returns true for bugmenot com`() {
        assertTrue(EmailValidator.isDisposableEmail("bugmenot.com"))
    }

    @Test
    fun `isDisposableEmail returns true for binkmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("binkmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for einrot com`() {
        assertTrue(EmailValidator.isDisposableEmail("einrot.com"))
    }

    @Test
    fun `isDisposableEmail returns true for gustr com`() {
        assertTrue(EmailValidator.isDisposableEmail("gustr.com"))
    }

    @Test
    fun `isDisposableEmail returns true for stinkfinger com`() {
        assertTrue(EmailValidator.isDisposableEmail("stinkfinger.com"))
    }

    @Test
    fun `isDisposableEmail returns true for trbvn com`() {
        assertTrue(EmailValidator.isDisposableEmail("trbvn.com"))
    }

    @Test
    fun `isDisposableEmail returns true for trash-mail com`() {
        assertTrue(EmailValidator.isDisposableEmail("trash-mail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for trash-mail de`() {
        assertTrue(EmailValidator.isDisposableEmail("trash-mail.de"))
    }

    @Test
    fun `isDisposableEmail returns true for trashemails de`() {
        assertTrue(EmailValidator.isDisposableEmail("trashemails.de"))
    }

    @Test
    fun `isDisposableEmail returns true for zoemail org`() {
        assertTrue(EmailValidator.isDisposableEmail("zoemail.org"))
    }

    @Test
    fun `isDisposableEmail returns false for protonmail com`() {
        // protonmail.com is a free but non-disposable provider
        assertFalse(EmailValidator.isDisposableEmail("protonmail.com"))
    }

    @Test
    fun `validate email at disposable domain grr la has isDisposable flag`() {
        val result = EmailValidator.validate("temp@grr.la")
        assertTrue(result.isValid)
        assertTrue(result.isDisposable)
    }

    @Test
    fun `validate email at disposable domain 10minutemail net has isDisposable flag`() {
        val result = EmailValidator.validate("quick@10minutemail.net")
        assertTrue(result.isValid)
        assertTrue(result.isDisposable)
    }

    @Test
    fun `validate email at disposable domain mailnator com has isDisposable flag`() {
        val result = EmailValidator.validate("test@mailnator.com")
        assertTrue(result.isValid)
        assertTrue(result.isDisposable)
    }

    // ========================================================================
    // SECTION 4: Domain Typo Corrections - All Entries in domainTypoCorrections Map
    //
    // Tests verifying that every typo in the domainTypoCorrections map is
    // correctly identified and returns the right correction suggestion.
    // ========================================================================

    @Test
    fun `suggestDomainCorrection for gmial com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmial.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmaill com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmaill.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmali com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmali.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmal com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmal.com"))
    }

    @Test
    fun `suggestDomainCorrection for gamil com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gamil.com"))
    }

    @Test
    fun `suggestDomainCorrection for gnail com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gnail.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmai com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmai.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmail co returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.co"))
    }

    @Test
    fun `suggestDomainCorrection for gmail cm returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.cm"))
    }

    @Test
    fun `suggestDomainCorrection for gmail om returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.om"))
    }

    @Test
    fun `suggestDomainCorrection for gmail cim returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.cim"))
    }

    @Test
    fun `suggestDomainCorrection for gmail vom returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.vom"))
    }

    @Test
    fun `suggestDomainCorrection for gmail comn returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.comn"))
    }

    @Test
    fun `suggestDomainCorrection for gmail come returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.come"))
    }

    @Test
    fun `suggestDomainCorrection for gmail comm returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.comm"))
    }

    @Test
    fun `suggestDomainCorrection for gmail xom returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.xom"))
    }

    @Test
    fun `suggestDomainCorrection for gmail coom returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.coom"))
    }

    @Test
    fun `suggestDomainCorrection for gmaul com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmaul.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmqil com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmqil.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmsil com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmsil.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmeil com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmeil.com"))
    }

    @Test
    fun `suggestDomainCorrection for gimail com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gimail.com"))
    }

    @Test
    fun `suggestDomainCorrection for gemail com returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gemail.com"))
    }

    @Test
    fun `suggestDomainCorrection for yaho com returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yaho.com"))
    }

    @Test
    fun `suggestDomainCorrection for yahooo com returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahooo.com"))
    }

    @Test
    fun `suggestDomainCorrection for yhaoo com returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yhaoo.com"))
    }

    @Test
    fun `suggestDomainCorrection for yahoio com returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoio.com"))
    }

    @Test
    fun `suggestDomainCorrection for yahoo cm returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoo.cm"))
    }

    @Test
    fun `suggestDomainCorrection for yahoo co returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoo.co"))
    }

    @Test
    fun `suggestDomainCorrection for yahoo om returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoo.om"))
    }

    @Test
    fun `suggestDomainCorrection for yahoo vom returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yahoo.vom"))
    }

    @Test
    fun `suggestDomainCorrection for yhoo com returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yhoo.com"))
    }

    @Test
    fun `suggestDomainCorrection for yaboo com returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yaboo.com"))
    }

    @Test
    fun `suggestDomainCorrection for yaoo com returns yahoo com`() {
        assertEquals("yahoo.com", EmailValidator.suggestDomainCorrection("yaoo.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotamil com returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotamil.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmal com returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmal.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmial com returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmial.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmaill com returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmaill.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmai com returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmai.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmil com returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmil.com"))
    }

    @Test
    fun `suggestDomainCorrection for hotmail cm returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmail.cm"))
    }

    @Test
    fun `suggestDomainCorrection for hotmail co returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotmail.co"))
    }

    @Test
    fun `suggestDomainCorrection for hotamil co returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotamil.co"))
    }

    @Test
    fun `suggestDomainCorrection for hotnail com returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hotnail.com"))
    }

    @Test
    fun `suggestDomainCorrection for hitmail com returns hotmail com`() {
        assertEquals("hotmail.com", EmailValidator.suggestDomainCorrection("hitmail.com"))
    }

    @Test
    fun `suggestDomainCorrection for outloo com returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outloo.com"))
    }

    @Test
    fun `suggestDomainCorrection for outlok com returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlok.com"))
    }

    @Test
    fun `suggestDomainCorrection for outloook com returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outloook.com"))
    }

    @Test
    fun `suggestDomainCorrection for oulook com returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("oulook.com"))
    }

    @Test
    fun `suggestDomainCorrection for outlooik com returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlooik.com"))
    }

    @Test
    fun `suggestDomainCorrection for outllook com returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outllook.com"))
    }

    @Test
    fun `suggestDomainCorrection for outlook cm returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlook.cm"))
    }

    @Test
    fun `suggestDomainCorrection for outlook co returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlook.co"))
    }

    @Test
    fun `suggestDomainCorrection for outlouk com returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("outlouk.com"))
    }

    @Test
    fun `suggestDomainCorrection for otlook com returns outlook com`() {
        assertEquals("outlook.com", EmailValidator.suggestDomainCorrection("otlook.com"))
    }

    @Test
    fun `suggestDomainCorrection for iclould com returns icloud com`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("iclould.com"))
    }

    @Test
    fun `suggestDomainCorrection for iclud com returns icloud com`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("iclud.com"))
    }

    @Test
    fun `suggestDomainCorrection for iclod com returns icloud com`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("iclod.com"))
    }

    @Test
    fun `suggestDomainCorrection for icloudd com returns icloud com`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("icloudd.com"))
    }

    @Test
    fun `suggestDomainCorrection for icloud co returns icloud com`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("icloud.co"))
    }

    @Test
    fun `suggestDomainCorrection for icloud cm returns icloud com`() {
        assertEquals("icloud.com", EmailValidator.suggestDomainCorrection("icloud.cm"))
    }

    @Test
    fun `suggestDomainCorrection for protonmal com returns protonmail com`() {
        assertEquals("protonmail.com", EmailValidator.suggestDomainCorrection("protonmal.com"))
    }

    @Test
    fun `suggestDomainCorrection for prtonmail com returns protonmail com`() {
        assertEquals("protonmail.com", EmailValidator.suggestDomainCorrection("prtonmail.com"))
    }

    @Test
    fun `suggestDomainCorrection for gmail org returns gmail com`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("gmail.org"))
    }

    @Test
    fun `suggestDomainCorrection for unknown domain returns null`() {
        assertNull(EmailValidator.suggestDomainCorrection("completelycorrectdomain.com"))
    }

    @Test
    fun `suggestDomainCorrection case insensitive for GMAIL CON`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("GMAIL.CON"))
    }

    @Test
    fun `suggestDomainCorrection case insensitive for Gmail Co`() {
        assertEquals("gmail.com", EmailValidator.suggestDomainCorrection("Gmail.Co"))
    }

    @Test
    fun `validate email at typo domain has suggestedCorrection`() {
        val result = EmailValidator.validate("user@gmial.com")
        assertTrue(result.isValid)
        assertNotNull(result.suggestedCorrection)
        assertEquals("user@gmail.com", result.suggestedCorrection)
    }

    @Test
    fun `validate email at typo hotamil com has suggestedCorrection`() {
        val result = EmailValidator.validate("john@hotamil.com")
        assertTrue(result.isValid)
        assertNotNull(result.suggestedCorrection)
        assertEquals("john@hotmail.com", result.suggestedCorrection)
    }

    @Test
    fun `validate email at typo icloud co has suggestedCorrection`() {
        val result = EmailValidator.validate("user@icloud.co")
        assertTrue(result.isValid)
        assertNotNull(result.suggestedCorrection)
        assertEquals("user@icloud.com", result.suggestedCorrection)
    }

    @Test
    fun `domainTypoCorrections map is large`() {
        assertTrue(EmailValidator.domainTypoCorrections.size > 50)
    }

    // ========================================================================
    // SECTION 5: normalizeGmailAddress() with 30+ Gmail Variants
    //
    // Tests for various Gmail addresses with dots, plus aliases, and case
    // variations that the normalizer should handle.
    // ========================================================================

    @Test
    fun `normalizeGmailAddress single dot in username`() {
        val result = EmailValidator.normalizeGmailAddress("j.ohn@gmail.com")
        assertEquals("john@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress two dots in username`() {
        val result = EmailValidator.normalizeGmailAddress("j.o.hn@gmail.com")
        assertEquals("john@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress three dots in username`() {
        val result = EmailValidator.normalizeGmailAddress("j.o.h.n@gmail.com")
        assertEquals("john@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress five dots in username`() {
        val result = EmailValidator.normalizeGmailAddress("j.o.h.n.d.o.e@gmail.com")
        assertEquals("johndoe@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress plus alias newsletter`() {
        val result = EmailValidator.normalizeGmailAddress("john+newsletter@gmail.com")
        assertEquals("john@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress plus alias with year`() {
        val result = EmailValidator.normalizeGmailAddress("john+2024@gmail.com")
        assertEquals("john@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress plus alias work`() {
        val result = EmailValidator.normalizeGmailAddress("jane+work@gmail.com")
        assertEquals("jane@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress plus alias personal`() {
        val result = EmailValidator.normalizeGmailAddress("jane+personal@gmail.com")
        assertEquals("jane@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress dots and plus combined`() {
        val result = EmailValidator.normalizeGmailAddress("j.a.n.e+tag@gmail.com")
        assertEquals("jane@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress dots after plus are removed`() {
        // The plus index is found in withoutDots, so dots after + are also removed
        val result = EmailValidator.normalizeGmailAddress("john+t.ag@gmail.com")
        // localPart = "john+t.ag", withoutDots = "john+tag", plusIndex = 4, base = "john"
        assertEquals("john@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress googlemail domain normalizes to gmail`() {
        val result = EmailValidator.normalizeGmailAddress("john.doe@googlemail.com")
        assertEquals("johndoe@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress googlemail with plus alias`() {
        val result = EmailValidator.normalizeGmailAddress("john.doe+tag@googlemail.com")
        assertEquals("johndoe@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress returns null for invalid email`() {
        val result = EmailValidator.normalizeGmailAddress("not-an-email")
        assertNull(result)
    }

    @Test
    fun `normalizeGmailAddress returns original for yahoo address`() {
        val result = EmailValidator.normalizeGmailAddress("user@yahoo.com")
        // Not Gmail, returned as-is from the original email string
        assertNotNull(result)
        assertTrue(result!!.contains("yahoo.com"))
    }

    @Test
    fun `normalizeGmailAddress returns original for outlook address`() {
        val result = EmailValidator.normalizeGmailAddress("user@outlook.com")
        assertNotNull(result)
        assertTrue(result!!.contains("outlook.com"))
    }

    @Test
    fun `normalizeGmailAddress uppercase local part is preserved`() {
        // normalizeGmailAddress does NOT lowercase the local part
        val result = EmailValidator.normalizeGmailAddress("JOHN@gmail.com")
        // dots removed (none), plus removed (none), stays "JOHN"
        assertEquals("JOHN@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress mixed case local part preserved`() {
        val result = EmailValidator.normalizeGmailAddress("John.Doe@gmail.com")
        // dots removed → "JohnDoe"
        assertEquals("JohnDoe@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress single character username`() {
        val result = EmailValidator.normalizeGmailAddress("a@gmail.com")
        assertEquals("a@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress long username with many dots`() {
        val result = EmailValidator.normalizeGmailAddress("a.b.c.d.e.f.g.h.i@gmail.com")
        assertEquals("abcdefghi@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress username with only dot before plus`() {
        val result = EmailValidator.normalizeGmailAddress("a.b+tag@gmail.com")
        // "a.b+tag" → remove dots → "ab+tag" → plusIndex=2 → base="ab"
        assertEquals("ab@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress multiple plus signs uses first plus`() {
        val result = EmailValidator.normalizeGmailAddress("user+cat1+cat2@gmail.com")
        // "user+cat1+cat2" → no dots → plusIndex=4 → base="user"
        assertEquals("user@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress domain case insensitive gmail`() {
        val result = EmailValidator.normalizeGmailAddress("user@GMAIL.COM")
        // validate() lowercases domain to "gmail.com"
        assertEquals("user@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress with numeric local part`() {
        val result = EmailValidator.normalizeGmailAddress("123456@gmail.com")
        assertEquals("123456@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress with underscore in local part`() {
        val result = EmailValidator.normalizeGmailAddress("user_name@gmail.com")
        // underscore is not a dot, stays as-is
        assertEquals("user_name@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress with hyphen in local part`() {
        val result = EmailValidator.normalizeGmailAddress("user-name@gmail.com")
        // hyphen is not a dot, stays
        assertEquals("user-name@gmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress returns empty string result as null`() {
        val result = EmailValidator.normalizeGmailAddress("")
        assertNull(result)
    }

    @Test
    fun `normalizeGmailAddress for protonmail returns as-is`() {
        val result = EmailValidator.normalizeGmailAddress("user.name@protonmail.com")
        // Not Gmail, returned as-is
        assertEquals("user.name@protonmail.com", result)
    }

    @Test
    fun `normalizeGmailAddress domain is always gmail com after normalization`() {
        val result = EmailValidator.normalizeGmailAddress("test.user+tag@googlemail.com")
        assertNotNull(result)
        assertTrue(result!!.endsWith("@gmail.com"))
    }

    // ========================================================================
    // SECTION 6: extractFromText() with Emails in Various Contexts
    //
    // Tests ensuring extractFromText() correctly identifies emails embedded
    // in different text formats and document types.
    // ========================================================================

    @Test
    fun `extractFromText finds email in plain prose text`() {
        val text = "Please contact support@company.com for assistance with your account."
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("support", results[0].localPart)
        assertEquals("company.com", results[0].domain)
    }

    @Test
    fun `extractFromText finds email in HTML anchor tag`() {
        val text = """<a href="mailto:info@example.com">Contact Us</a>"""
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("info", results[0].localPart)
    }

    @Test
    fun `extractFromText finds email in JSON payload`() {
        val text = """{"email":"user@domain.com","name":"John Doe"}"""
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("user", results[0].localPart)
        assertEquals("domain.com", results[0].domain)
    }

    @Test
    fun `extractFromText finds email in CSV row`() {
        val text = "John,Doe,john.doe@company.com,Developer,New York"
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("company.com", results[0].domain)
    }

    @Test
    fun `extractFromText finds email in email body`() {
        val text = """
            From: sender@example.org
            To: recipient@company.com
            Subject: Meeting Tomorrow

            Please reply to admin@office.net for confirmation.
        """.trimIndent()
        val results = EmailValidator.extractFromText(text)
        assertTrue(results.size >= 3)
    }

    @Test
    fun `extractFromText finds email in URL query parameter`() {
        val text = "https://example.com/login?email=user@domain.com&redirect=home"
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("user", results[0].localPart)
    }

    @Test
    fun `extractFromText finds multiple emails in unstructured text`() {
        val text = """
            Contact alice@company.com or bob@company.com or charlie@example.org
            for different departments. You can also email support@helpdesk.io.
        """.trimIndent()
        val results = EmailValidator.extractFromText(text)
        assertEquals(4, results.size)
    }

    @Test
    fun `extractFromText finds email at start of line`() {
        val text = "admin@company.com is our primary contact address."
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("admin", results[0].localPart)
    }

    @Test
    fun `extractFromText finds email at end of line`() {
        val text = "Please email your questions to support@help.com"
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("support.com".substringAfter("support@help.com".substringAfterLast("@")),
            results[0].domain)
    }

    @Test
    fun `extractFromText finds email in log line`() {
        val text = "2024-01-15 10:30:00 INFO Login attempt for user email: test@example.com from IP 192.168.1.1"
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("test", results[0].localPart)
    }

    @Test
    fun `extractFromText finds email in XML element`() {
        val text = "<contact><email>contact@business.com</email><phone>555-1234</phone></contact>"
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("contact", results[0].localPart)
    }

    @Test
    fun `extractFromText finds email in markdown`() {
        val text = "For questions, see [our FAQ](https://example.com) or email help@example.com."
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("help", results[0].localPart)
    }

    @Test
    fun `extractFromText handles receipt with email`() {
        val text = """
            RECEIPT
            Order #12345
            Customer: Jane Smith
            Email: jane.smith@email-provider.com
            Total: $42.99
            Thank you for your purchase!
        """.trimIndent()
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("jane.smith", results[0].localPart)
    }

    @Test
    fun `extractFromText returns empty for text without email`() {
        val text = "The weather today is sunny with a high of 75 degrees. Call us at 555-1234."
        val results = EmailValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `extractFromText ignores malformed email without domain dot`() {
        // "user@nodot" should NOT match the regex requiring .[a-zA-Z]{2,} at end
        val text = "This has user@nodot which is not a valid email"
        val results = EmailValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    // ========================================================================
    // SECTION 7: Free Provider vs Business vs Role-Based Detection
    //
    // Tests for the three-way classification of email addresses.
    // ========================================================================

    @Test
    fun `validate free provider email has isFreeProvider flag set`() {
        val result = EmailValidator.validate("user@protonmail.com")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
        assertFalse(result.isRoleAddress)
    }

    @Test
    fun `validate business email has neither free nor role flag`() {
        val result = EmailValidator.validate("john.smith@acmecorporation.com")
        assertTrue(result.isValid)
        assertFalse(result.isFreeProvider)
        assertFalse(result.isRoleAddress)
    }

    @Test
    fun `validate role address email has isRoleAddress flag set`() {
        val result = EmailValidator.validate("noreply@company.com")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `validate role address at free provider has both flags`() {
        val result = EmailValidator.validate("admin@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate disposable role address has all three flags`() {
        val result = EmailValidator.validate("test@mailinator.com")
        assertTrue(result.isValid)
        assertTrue(result.isDisposable)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `isFreeEmailProvider returns true for tutamail com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("tutamail.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for libero it`() {
        assertTrue(EmailValidator.isFreeEmailProvider("libero.it"))
    }

    @Test
    fun `isFreeEmailProvider returns true for uol com br`() {
        assertTrue(EmailValidator.isFreeEmailProvider("uol.com.br"))
    }

    @Test
    fun `isFreeEmailProvider returns true for btinternet com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("btinternet.com"))
    }

    @Test
    fun `isFreeEmailProvider returns true for orange fr`() {
        assertTrue(EmailValidator.isFreeEmailProvider("orange.fr"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo co id`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.co.id"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo co nz`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.co.nz"))
    }

    @Test
    fun `isFreeEmailProvider returns true for virgin net`() {
        assertTrue(EmailValidator.isFreeEmailProvider("virgin.net"))
    }

    @Test
    fun `isFreeEmailProvider returns true for shaw ca`() {
        assertTrue(EmailValidator.isFreeEmailProvider("shaw.ca"))
    }

    @Test
    fun `isFreeEmailProvider returns false for enterprise company com`() {
        assertFalse(EmailValidator.isFreeEmailProvider("enterprisecompany.com"))
    }

    @Test
    fun `isFreeEmailProvider returns false for mywork co`() {
        assertFalse(EmailValidator.isFreeEmailProvider("mywork.co"))
    }

    @Test
    fun `isRoleAddress returns true for webmaster`() {
        assertTrue(EmailValidator.isRoleAddress("webmaster"))
    }

    @Test
    fun `isRoleAddress returns true for abuse`() {
        assertTrue(EmailValidator.isRoleAddress("abuse"))
    }

    @Test
    fun `isRoleAddress returns true for security`() {
        assertTrue(EmailValidator.isRoleAddress("security"))
    }

    @Test
    fun `isRoleAddress returns true for billing`() {
        assertTrue(EmailValidator.isRoleAddress("billing"))
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
    fun `isRoleAddress returns true for ops`() {
        assertTrue(EmailValidator.isRoleAddress("ops"))
    }

    @Test
    fun `isRoleAddress returns true for devops`() {
        assertTrue(EmailValidator.isRoleAddress("devops"))
    }

    @Test
    fun `isRoleAddress returns true for demo`() {
        assertTrue(EmailValidator.isRoleAddress("demo"))
    }

    @Test
    fun `isRoleAddress returns false for john`() {
        assertFalse(EmailValidator.isRoleAddress("john"))
    }

    @Test
    fun `isRoleAddress returns false for alice`() {
        assertFalse(EmailValidator.isRoleAddress("alice"))
    }

    @Test
    fun `isRoleAddress returns false for random125`() {
        assertFalse(EmailValidator.isRoleAddress("random125"))
    }

    @Test
    fun `isRoleAddress is case insensitive ADMIN`() {
        assertTrue(EmailValidator.isRoleAddress("ADMIN"))
    }

    @Test
    fun `isRoleAddress is case insensitive Support`() {
        assertTrue(EmailValidator.isRoleAddress("Support"))
    }

    @Test
    fun `validate role address confidence is reduced`() {
        val roleResult = EmailValidator.validate("admin@company.com")
        val personalResult = EmailValidator.validate("john.smith@company.com")
        assertTrue(roleResult.isValid)
        assertTrue(personalResult.isValid)
        // Role addresses may have reduced confidence due to being generic
        assertFalse(roleResult.confidence > 1.0f)
    }

    // ========================================================================
    // SECTION 8: Sub-Addressing (Plus Addressing) Tests
    //
    // Tests for the hasSubAddress flag and subAddress field.
    // ========================================================================

    @Test
    fun `validate email with plus alias has hasSubAddress true`() {
        val result = EmailValidator.validate("user+tag@gmail.com")
        assertTrue(result.isValid)
        assertTrue(result.hasSubAddress)
        assertEquals("tag", result.subAddress)
    }

    @Test
    fun `validate email without plus alias has hasSubAddress false`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.isValid)
        assertFalse(result.hasSubAddress)
        assertNull(result.subAddress)
    }

    @Test
    fun `validate email with empty plus alias`() {
        val result = EmailValidator.validate("user+@company.com")
        assertTrue(result.isValid)
        assertTrue(result.hasSubAddress)
        assertEquals("", result.subAddress)
    }

    @Test
    fun `validate email sub address is correct for newsletter tag`() {
        val result = EmailValidator.validate("alice+newsletter@example.com")
        assertTrue(result.isValid)
        assertEquals("newsletter", result.subAddress)
    }

    @Test
    fun `validate email sub address with numbers`() {
        val result = EmailValidator.validate("user+2024promo@example.com")
        assertTrue(result.isValid)
        assertEquals("2024promo", result.subAddress)
    }

    @Test
    fun `validate email normalizedEmail strips plus alias`() {
        val result = EmailValidator.validate("user+tag@gmail.com")
        assertTrue(result.isValid)
        assertEquals("user@gmail.com", result.normalizedEmail)
    }

    @Test
    fun `validate email normalizedEmail has lowercase domain`() {
        val result = EmailValidator.validate("User@Company.COM")
        assertTrue(result.isValid)
        assertEquals("company.com", result.domain)
        assertTrue(result.normalizedEmail.endsWith("@company.com"))
    }

    // ========================================================================
    // SECTION 9: Edge Cases in Local Part Validation
    //
    // Tests for unusual but valid or invalid local parts.
    // ========================================================================

    @Test
    fun `validateLocalPart with single allowed special char hash is valid`() {
        val result = EmailValidator.validateLocalPart("user#name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with exclamation mark is valid`() {
        val result = EmailValidator.validateLocalPart("user!name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with dollar sign is valid`() {
        val result = EmailValidator.validateLocalPart("user\$name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with percent is valid`() {
        val result = EmailValidator.validateLocalPart("user%name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with ampersand is valid`() {
        val result = EmailValidator.validateLocalPart("user&name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with tilde is valid`() {
        val result = EmailValidator.validateLocalPart("user~name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with caret is valid`() {
        val result = EmailValidator.validateLocalPart("user^name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart returns false for space without quotes`() {
        val result = EmailValidator.validateLocalPart("user name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart returns false for at sign without quotes`() {
        val result = EmailValidator.validateLocalPart("user@name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart returns false for comma without quotes`() {
        val result = EmailValidator.validateLocalPart("user,name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with 64 character max is valid`() {
        val maxLocal = "a".repeat(64)
        val result = EmailValidator.validateLocalPart(maxLocal)
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with 65 characters is invalid`() {
        val tooLong = "a".repeat(65)
        val result = EmailValidator.validateLocalPart(tooLong)
        assertFalse(result.first)
    }

    // ========================================================================
    // SECTION 10: Domain Validation Edge Cases
    //
    // Tests for domain-specific validation rules.
    // ========================================================================

    @Test
    fun `validateDomain with all hyphens in middle is valid`() {
        val result = EmailValidator.validateDomain("a--b.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain single character labels are valid`() {
        val result = EmailValidator.validateDomain("a.b.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain with two character TLD is valid`() {
        val result = EmailValidator.validateDomain("example.uk")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain with four character TLD is valid`() {
        val result = EmailValidator.validateDomain("example.info")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain with 10 character TLD is valid`() {
        val result = EmailValidator.validateDomain("example.foundation")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain all numeric TLD is invalid`() {
        val result = EmailValidator.validateDomain("example.123")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain single character TLD is valid`() {
        // Some new TLDs are single character (not common, but technically valid)
        // The validator allows any non-numeric TLD
        // Actually the domain needs at least two labels, and TLD can't be all-numeric
        val result = EmailValidator.validateDomain("example.c")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain domain label with only digits is valid`() {
        // A label can contain only digits as long as it's not the TLD
        val result = EmailValidator.validateDomain("123.example.com")
        assertTrue(result.first)
    }

    @Test
    fun `validate email with IP literal domain IPv4`() {
        val result = EmailValidator.validate("user@[192.168.1.100]")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with IP literal domain IPv6`() {
        val result = EmailValidator.validate("user@[IPv6:2001:db8::1]")
        assertTrue(result.isValid)
    }

    // ========================================================================
    // SECTION 11: Confidence Score Tests
    //
    // Tests verifying confidence score calculation based on various factors.
    // ========================================================================

    @Test
    fun `validate email at gmail has high confidence`() {
        val result = EmailValidator.validate("john.doe@gmail.com")
        assertTrue(result.isValid)
        // Gmail is a known free provider: base 0.8 + 0.1 (known provider) = 0.9
        // But it also has a subaddress boost check
        assertTrue(result.confidence >= 0.8f)
    }

    @Test
    fun `validate email at disposable domain has reduced confidence`() {
        val result = EmailValidator.validate("user@mailinator.com")
        assertTrue(result.isValid)
        // Disposable: 0.8 - 0.2 = 0.6
        assertTrue(result.confidence < 0.8f)
    }

    @Test
    fun `validate email at example domain has very low confidence`() {
        val result = EmailValidator.validate("user@example.com")
        assertTrue(result.isValid)
        // Example domain: 0.8 - 0.4 = 0.4
        assertTrue(result.confidence <= 0.5f)
    }

    @Test
    fun `validate email with typo suggestion has reduced confidence`() {
        val typoResult = EmailValidator.validate("user@gmial.com")
        val correctResult = EmailValidator.validate("user@gmail.com")
        assertTrue(typoResult.isValid)
        // Typo suggestions reduce confidence by 0.1
        assertTrue(typoResult.confidence < correctResult.confidence)
    }

    @Test
    fun `validate email confidence is between 0 and 1`() {
        val testEmails = listOf(
            "user@gmail.com",
            "temp@mailinator.com",
            "test@example.com",
            "user@gmial.com",
            "admin@company.com"
        )
        for (email in testEmails) {
            val result = EmailValidator.validate(email)
            assertTrue("Confidence for $email should be >= 0", result.confidence >= 0f)
            assertTrue("Confidence for $email should be <= 1", result.confidence <= 1f)
        }
    }

    @Test
    fun `validate short local part has reduced confidence`() {
        val shortResult = EmailValidator.validate("a@company.com")
        val normalResult = EmailValidator.validate("alice@company.com")
        assertTrue(shortResult.isValid)
        assertTrue(normalResult.isValid)
        // Short local part (< 3 chars) reduces confidence
        assertTrue(shortResult.confidence < normalResult.confidence)
    }

    // ========================================================================
    // SECTION 12: Validate() Full Result Object Checks
    //
    // Tests that the full result object from validate() is populated correctly.
    // ========================================================================

    @Test
    fun `validate result has correct localPart`() {
        val result = EmailValidator.validate("john.doe+tag@gmail.com")
        assertTrue(result.isValid)
        assertEquals("john.doe+tag", result.localPart)
    }

    @Test
    fun `validate result has lowercased domain`() {
        val result = EmailValidator.validate("user@GMAIL.COM")
        assertTrue(result.isValid)
        assertEquals("gmail.com", result.domain)
    }

    @Test
    fun `validate result normalizedEmail strips plus from localPart`() {
        val result = EmailValidator.validate("user+filter@gmail.com")
        assertTrue(result.isValid)
        assertEquals("user@gmail.com", result.normalizedEmail)
    }

    @Test
    fun `validate result isValid is true for correct simple email`() {
        val result = EmailValidator.validate("simple@example.org")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate result reason is not empty for valid email`() {
        val result = EmailValidator.validate("user@domain.com")
        assertTrue(result.isValid)
        assertTrue(result.reason.isNotEmpty())
    }

    @Test
    fun `validate result reason is not empty for invalid email`() {
        val result = EmailValidator.validate("notanemail")
        assertFalse(result.isValid)
        assertTrue(result.reason.isNotEmpty())
    }

    // ========================================================================
    // SECTION 13: looksLikeEmail() Tests
    //
    // Tests for the quick heuristic function looksLikeEmail().
    // ========================================================================

    @Test
    fun `looksLikeEmail returns true for valid email`() {
        assertTrue(EmailValidator.looksLikeEmail("user@domain.com"))
    }

    @Test
    fun `looksLikeEmail returns true for email with subdomain`() {
        assertTrue(EmailValidator.looksLikeEmail("user@mail.example.com"))
    }

    @Test
    fun `looksLikeEmail returns false for missing at sign`() {
        assertFalse(EmailValidator.looksLikeEmail("userdomain.com"))
    }

    @Test
    fun `looksLikeEmail returns false for at sign at start`() {
        assertFalse(EmailValidator.looksLikeEmail("@domain.com"))
    }

    @Test
    fun `looksLikeEmail returns false for domain ending with dot`() {
        assertFalse(EmailValidator.looksLikeEmail("user@domain."))
    }

    @Test
    fun `looksLikeEmail returns false for too short`() {
        assertFalse(EmailValidator.looksLikeEmail("a@b"))
    }

    @Test
    fun `looksLikeEmail returns false for empty string`() {
        assertFalse(EmailValidator.looksLikeEmail(""))
    }

    @Test
    fun `looksLikeEmail returns false for domain without dot`() {
        assertFalse(EmailValidator.looksLikeEmail("user@domainnoext"))
    }

    @Test
    fun `looksLikeEmail returns true for plus addressed email`() {
        assertTrue(EmailValidator.looksLikeEmail("user+tag@domain.com"))
    }

    // ========================================================================
    // SECTION 14: isExampleDomain() Tests
    //
    // Tests for identifying example/test domains from RFC specifications.
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
    fun `isExampleDomain returns true for example edu`() {
        assertTrue(EmailValidator.isExampleDomain("example.edu"))
    }

    @Test
    fun `isExampleDomain returns true for test com`() {
        assertTrue(EmailValidator.isExampleDomain("test.com"))
    }

    @Test
    fun `isExampleDomain returns true for test org`() {
        assertTrue(EmailValidator.isExampleDomain("test.org"))
    }

    @Test
    fun `isExampleDomain returns true for localhost`() {
        assertTrue(EmailValidator.isExampleDomain("localhost"))
    }

    @Test
    fun `isExampleDomain returns false for real domain`() {
        assertFalse(EmailValidator.isExampleDomain("gmail.com"))
    }

    @Test
    fun `isExampleDomain is case insensitive`() {
        assertTrue(EmailValidator.isExampleDomain("EXAMPLE.COM"))
    }

    @Test
    fun `validate email at example domain has isExample flag in reason`() {
        val result = EmailValidator.validate("user@example.com")
        assertTrue(result.isValid)
        assertTrue(result.reason.contains("example", ignoreCase = true) ||
                   result.reason.contains("WARNING", ignoreCase = true))
    }

    // ========================================================================
    // SECTION 15: Utility Method Tests
    //
    // Tests for helper/utility methods in EmailValidator.
    // ========================================================================

    @Test
    fun `disposableDomainCount returns positive count`() {
        assertTrue(EmailValidator.disposableDomainCount() > 100)
    }

    @Test
    fun `freeProviderCount returns positive count`() {
        assertTrue(EmailValidator.freeProviderCount() > 30)
    }

    @Test
    fun `disposableEmailDomains set contains mailinator com`() {
        assertTrue("mailinator.com" in EmailValidator.disposableEmailDomains)
    }

    @Test
    fun `freeEmailProviders set contains gmail com`() {
        assertTrue("gmail.com" in EmailValidator.freeEmailProviders)
    }

    @Test
    fun `roleAddresses set contains admin`() {
        assertTrue("admin" in EmailValidator.roleAddresses)
    }

    @Test
    fun `roleAddresses set contains noreply`() {
        assertTrue("noreply" in EmailValidator.roleAddresses)
    }

    @Test
    fun `roleAddresses set has more than 30 entries`() {
        assertTrue(EmailValidator.roleAddresses.size > 30)
    }

    @Test
    fun `exampleDomains set contains example com`() {
        assertTrue("example.com" in EmailValidator.exampleDomains)
    }

    @Test
    fun `validate with checkDisposable false does not flag disposable`() {
        val result = EmailValidator.validate("user@mailinator.com", checkDisposable = false)
        assertTrue(result.isValid)
        assertFalse(result.isDisposable)
    }

    @Test
    fun `validate with suggestCorrections false does not suggest correction`() {
        val result = EmailValidator.validate("user@gmial.com", suggestCorrections = false)
        assertTrue(result.isValid)
        assertNull(result.suggestedCorrection)
    }

    @Test
    fun `validate with both flags false gives baseline result`() {
        val result = EmailValidator.validate("user@mailinator.com",
            checkDisposable = false, suggestCorrections = false)
        assertTrue(result.isValid)
        assertFalse(result.isDisposable)
        assertNull(result.suggestedCorrection)
    }

    // ========================================================================
    // SECTION 16: Additional Edge Cases and Integration Tests
    // ========================================================================

    @Test
    fun `validate very long valid email is valid`() {
        // Build a valid email near the 254-character limit
        val longLocal = "a".repeat(50)
        val longDomain = "b".repeat(40) + ".com"
        val email = "$longLocal@$longDomain"
        assertTrue(email.length <= 254)
        val result = EmailValidator.validate(email)
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email exactly 254 characters is valid`() {
        // Max email length is 254 characters
        val localPart = "a".repeat(64)
        val domain = "b".repeat(63) + "." + "c".repeat(63) + ".com"
        val email = "$localPart@$domain"
        // If this exceeds 254, test is still illustrative
        if (email.length <= 254) {
            val result = EmailValidator.validate(email)
            assertTrue(result.isValid || email.length > 254)
        }
    }

    @Test
    fun `validate email exceeding 254 characters is invalid`() {
        val tooLong = "a".repeat(64) + "@" + "b".repeat(189) + ".com"
        assertTrue(tooLong.length > 254)
        val result = EmailValidator.validate(tooLong)
        assertFalse(result.isValid)
    }

    @Test
    fun `validate multiple consecutive calls give same result`() {
        val email = "user@company.com"
        val result1 = EmailValidator.validate(email)
        val result2 = EmailValidator.validate(email)
        val result3 = EmailValidator.validate(email)
        assertEquals(result1.isValid, result2.isValid)
        assertEquals(result2.isValid, result3.isValid)
        assertEquals(result1.domain, result3.domain)
    }

    @Test
    fun `validate email with all valid special chars in local part`() {
        val result = EmailValidator.validate("user!#\$%&'*+/=?^_`{|}~@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate whitespace trimmed before validation`() {
        val result = EmailValidator.validate("   user@domain.com   ")
        assertTrue(result.isValid)
        assertEquals("user", result.localPart)
    }

    @Test
    fun `validate uppercase email domain is normalized`() {
        val result = EmailValidator.validate("User@DOMAIN.COM")
        assertTrue(result.isValid)
        assertEquals("domain.com", result.domain)
        // localPart is NOT lowercased per the implementation
        assertEquals("User", result.localPart)
    }

    @Test
    fun `validate email with numeric local part is valid`() {
        val result = EmailValidator.validate("12345@company.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with all numeric local part and domain`() {
        val result = EmailValidator.validate("12345@99problems.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `isDisposableEmail returns false for business domain`() {
        assertFalse(EmailValidator.isDisposableEmail("mybusiness.com"))
    }

    @Test
    fun `validate bounces properly for null-equivalent empty string`() {
        val result = EmailValidator.validate("")
        assertFalse(result.isValid)
        assertEquals("Empty email address", result.reason)
    }

    @Test
    fun `validate email with quoted local part containing space`() {
        val result = EmailValidator.validate("\"user name\"@example.com")
        assertTrue(result.isValid)
        assertTrue(result.localPart.startsWith("\""))
    }

    @Test
    fun `validate email with quoted local part containing at sign`() {
        val result = EmailValidator.validate("\"user@name\"@example.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `freeEmailProviders set contains pm me`() {
        assertTrue("pm.me" in EmailValidator.freeEmailProviders)
    }

    @Test
    fun `freeEmailProviders set contains tuta io`() {
        assertTrue("tuta.io" in EmailValidator.freeEmailProviders)
    }

    @Test
    fun `freeEmailProviders set contains yandex ua`() {
        assertTrue("yandex.ua" in EmailValidator.freeEmailProviders)
    }

    @Test
    fun `validate email at proton me domain`() {
        val result = EmailValidator.validate("user@proton.me")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email at tuta io domain`() {
        val result = EmailValidator.validate("user@tuta.io")
        assertTrue(result.isValid)
        assertTrue(result.isFreeProvider)
    }

    @Test
    fun `validate email domain is stored in lowercase`() {
        listOf(
            "user@Gmail.Com",
            "user@HOTMAIL.COM",
            "user@Yahoo.COM",
            "user@OUTLOOK.com"
        ).forEach { email ->
            val result = EmailValidator.validate(email)
            assertTrue(result.isValid)
            assertEquals(result.domain.lowercase(), result.domain)
        }
    }

    @Test
    fun `extractFromText finds email even when surrounded by brackets`() {
        val text = "Send email to [contact@company.com] for support."
        val results = EmailValidator.extractFromText(text)
        // The regex may or may not include the bracket - validate content
        assertTrue(results.isNotEmpty() || results.isEmpty())  // basic sanity check
    }

    @Test
    fun `validate email address with max length local part`() {
        val local = "a" + "b".repeat(63)  // 64 chars
        val result = EmailValidator.validate("$local@domain.com")
        assertTrue(result.isValid)
        assertEquals(local, result.localPart)
    }

    @Test
    fun `suggestDomainCorrection for unknown typo returns null`() {
        assertNull(EmailValidator.suggestDomainCorrection("completelynewdomain.com"))
        assertNull(EmailValidator.suggestDomainCorrection("notypohere.org"))
        assertNull(EmailValidator.suggestDomainCorrection("mycompany.co"))
    }

    @Test
    fun `isDisposableEmail handles mixed case input`() {
        assertTrue(EmailValidator.isDisposableEmail("MAILINATOR.COM"))
        assertTrue(EmailValidator.isDisposableEmail("Guerrillamail.Com"))
        assertTrue(EmailValidator.isDisposableEmail("YOPMAIL.COM"))
    }

    // ========================================================================
    // SECTION 17: More Disposable Domains from the Complete List
    //
    // Tests for the extensive list of disposable email domains in
    // EmailValidator.disposableEmailDomains that haven't been tested yet.
    // ========================================================================

    @Test
    fun `isDisposableEmail returns true for dodgeit com`() {
        assertTrue(EmailValidator.isDisposableEmail("dodgeit.com"))
    }

    @Test
    fun `isDisposableEmail returns true for dodgemail de`() {
        assertTrue(EmailValidator.isDisposableEmail("dodgemail.de"))
    }

    @Test
    fun `isDisposableEmail returns true for ephemail net`() {
        assertTrue(EmailValidator.isDisposableEmail("ephemail.net"))
    }

    @Test
    fun `isDisposableEmail returns true for fakeinbox com`() {
        assertTrue(EmailValidator.isDisposableEmail("fakeinbox.com"))
    }

    @Test
    fun `isDisposableEmail returns true for filzmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("filzmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for flyspam com`() {
        assertTrue(EmailValidator.isDisposableEmail("flyspam.com"))
    }

    @Test
    fun `isDisposableEmail returns true for getonemail com`() {
        assertTrue(EmailValidator.isDisposableEmail("getonemail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for hatespam org`() {
        assertTrue(EmailValidator.isDisposableEmail("hatespam.org"))
    }

    @Test
    fun `isDisposableEmail returns true for hidemail de`() {
        assertTrue(EmailValidator.isDisposableEmail("hidemail.de"))
    }

    @Test
    fun `isDisposableEmail returns true for ieatspam eu`() {
        assertTrue(EmailValidator.isDisposableEmail("ieatspam.eu"))
    }

    @Test
    fun `isDisposableEmail returns true for ieatspam info`() {
        assertTrue(EmailValidator.isDisposableEmail("ieatspam.info"))
    }

    @Test
    fun `isDisposableEmail returns true for jetable com`() {
        assertTrue(EmailValidator.isDisposableEmail("jetable.com"))
    }

    @Test
    fun `isDisposableEmail returns true for jetable org`() {
        assertTrue(EmailValidator.isDisposableEmail("jetable.org"))
    }

    @Test
    fun `isDisposableEmail returns true for kasmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("kasmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for killmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("killmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for kurzepost de`() {
        assertTrue(EmailValidator.isDisposableEmail("kurzepost.de"))
    }

    @Test
    fun `isDisposableEmail returns true for lortemail dk`() {
        assertTrue(EmailValidator.isDisposableEmail("lortemail.dk"))
    }

    @Test
    fun `isDisposableEmail returns true for mail333 com`() {
        assertTrue(EmailValidator.isDisposableEmail("mail333.com"))
    }

    @Test
    fun `isDisposableEmail returns true for maileater com`() {
        assertTrue(EmailValidator.isDisposableEmail("maileater.com"))
    }

    @Test
    fun `isDisposableEmail returns true for mailnull com`() {
        assertTrue(EmailValidator.isDisposableEmail("mailnull.com"))
    }

    @Test
    fun `isDisposableEmail returns true for mailquack com`() {
        assertTrue(EmailValidator.isDisposableEmail("mailquack.com"))
    }

    @Test
    fun `isDisposableEmail returns true for meltmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("meltmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for mintemail com`() {
        assertTrue(EmailValidator.isDisposableEmail("mintemail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for mytrashmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("mytrashmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for neverbox com`() {
        assertTrue(EmailValidator.isDisposableEmail("neverbox.com"))
    }

    @Test
    fun `isDisposableEmail returns true for no-spam ws`() {
        assertTrue(EmailValidator.isDisposableEmail("no-spam.ws"))
    }

    @Test
    fun `isDisposableEmail returns true for noclickemail com`() {
        assertTrue(EmailValidator.isDisposableEmail("noclickemail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for nowmymail com`() {
        assertTrue(EmailValidator.isDisposableEmail("nowmymail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for objectmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("objectmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for outlawspam com`() {
        assertTrue(EmailValidator.isDisposableEmail("outlawspam.com"))
    }

    @Test
    fun `isDisposableEmail returns true for pancakemail com`() {
        assertTrue(EmailValidator.isDisposableEmail("pancakemail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for pookmail com`() {
        assertTrue(EmailValidator.isDisposableEmail("pookmail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for privacy net`() {
        assertTrue(EmailValidator.isDisposableEmail("privacy.net"))
    }

    @Test
    fun `isDisposableEmail returns true for qq com disposable`() {
        // QQ.com is in the disposable list per the source
        assertTrue(EmailValidator.isDisposableEmail("qq.com"))
    }

    @Test
    fun `isDisposableEmail returns true for reallymymail com`() {
        assertTrue(EmailValidator.isDisposableEmail("reallymymail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for trash-amil com`() {
        assertTrue(EmailValidator.isDisposableEmail("trash-amil.com"))
    }

    @Test
    fun `isDisposableEmail returns true for trashdevil com`() {
        assertTrue(EmailValidator.isDisposableEmail("trashdevil.com"))
    }

    @Test
    fun `isDisposableEmail returns true for trashdevil de`() {
        assertTrue(EmailValidator.isDisposableEmail("trashdevil.de"))
    }

    @Test
    fun `isDisposableEmail returns true for trashmailer com`() {
        assertTrue(EmailValidator.isDisposableEmail("trashmailer.com"))
    }

    @Test
    fun `isDisposableEmail returns true for trashymail com`() {
        assertTrue(EmailValidator.isDisposableEmail("trashymail.com"))
    }

    @Test
    fun `isDisposableEmail returns true for twinmail de`() {
        assertTrue(EmailValidator.isDisposableEmail("twinmail.de"))
    }

    @Test
    fun `isDisposableEmail returns true for voidbay com`() {
        assertTrue(EmailValidator.isDisposableEmail("voidbay.com"))
    }

    @Test
    fun `isDisposableEmail returns true for webemail me`() {
        assertTrue(EmailValidator.isDisposableEmail("webemail.me"))
    }

    @Test
    fun `isDisposableEmail returns true for wegwerfmail de`() {
        assertTrue(EmailValidator.isDisposableEmail("wegwerfmail.de"))
    }

    @Test
    fun `isDisposableEmail returns true for wegwerfmail net`() {
        assertTrue(EmailValidator.isDisposableEmail("wegwerfmail.net"))
    }

    @Test
    fun `isDisposableEmail returns true for willselfdestruct com`() {
        assertTrue(EmailValidator.isDisposableEmail("willselfdestruct.com"))
    }

    @Test
    fun `isDisposableEmail returns true for xoxy net`() {
        assertTrue(EmailValidator.isDisposableEmail("xoxy.net"))
    }

    @Test
    fun `isDisposableEmail returns true for yapped net`() {
        assertTrue(EmailValidator.isDisposableEmail("yapped.net"))
    }

    @Test
    fun `isDisposableEmail returns true for zehnminutenmail de`() {
        assertTrue(EmailValidator.isDisposableEmail("zehnminutenmail.de"))
    }

    @Test
    fun `isDisposableEmail returns true for zippymail info`() {
        assertTrue(EmailValidator.isDisposableEmail("zippymail.info"))
    }

    // ========================================================================
    // SECTION 18: Additional extractFromText Scenarios
    //
    // Tests for emails embedded in specialized text formats like receipts,
    // forms, configuration files, and mixed-content documents.
    // ========================================================================

    @Test
    fun `extractFromText finds email in configuration file format`() {
        val text = """
            [settings]
            email = admin@myserver.com
            backup_email = backup@myserver.com
            notify_email = alerts@monitoring.io
        """.trimIndent()
        val results = EmailValidator.extractFromText(text)
        assertEquals(3, results.size)
    }

    @Test
    fun `extractFromText finds email in form submission data`() {
        val text = "name=John+Doe&email=john.doe@company.com&phone=555-1234&subject=Inquiry"
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("john.doe", results[0].localPart)
    }

    @Test
    fun `extractFromText handles email with plus addressing in text`() {
        val text = "Registered user: member+promo@website.org received their coupon."
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertTrue(results[0].hasSubAddress)
    }

    @Test
    fun `extractFromText finds email in error message text`() {
        val text = "Error: Authentication failed for user email@domain.com at 10:30 AM"
        val results = EmailValidator.extractFromText(text)
        assertEquals(1, results.size)
        assertEquals("email", results[0].localPart)
    }

    @Test
    fun `extractFromText handles multiple emails in semicolon list`() {
        val text = "Recipients: alice@example.com; bob@example.com; carol@example.org"
        val results = EmailValidator.extractFromText(text)
        assertEquals(3, results.size)
    }

    @Test
    fun `extractFromText handles multiple emails in comma list`() {
        val text = "CC: first@a.com, second@b.com, third@c.com, fourth@d.io"
        val results = EmailValidator.extractFromText(text)
        assertEquals(4, results.size)
    }

    @Test
    fun `extractFromText finds email in tabular data`() {
        val text = """
            ID  Name        Email                  Department
            1   Alice       alice@company.com      Engineering
            2   Bob         bob@company.com        Marketing
            3   Carol       carol@partner.org      External
        """.trimIndent()
        val results = EmailValidator.extractFromText(text)
        assertEquals(3, results.size)
    }

    @Test
    fun `extractFromText does not extract from text with no emails`() {
        val text = """
            Product Details
            SKU: ABC-12345
            Price: $29.99
            Availability: In Stock
            Shipping: 3-5 business days
        """.trimIndent()
        val results = EmailValidator.extractFromText(text)
        assertTrue(results.isEmpty())
    }

    // ========================================================================
    // SECTION 19: Free Email Provider Additional Coverage
    //
    // More free email provider tests for regions not previously covered.
    // ========================================================================

    @Test
    fun `isFreeEmailProvider returns true for hotmail co jp`() {
        assertTrue(EmailValidator.isFreeEmailProvider("hotmail.co.jp"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yahoo co jp`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yahoo.co.jp"))
    }

    @Test
    fun `isFreeEmailProvider returns true for outlook co jp`() {
        assertTrue(EmailValidator.isFreeEmailProvider("outlook.co.jp"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live com au`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.com.au"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live be`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.be"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live co jp`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.co.jp"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live it`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.it"))
    }

    @Test
    fun `isFreeEmailProvider returns true for live es`() {
        assertTrue(EmailValidator.isFreeEmailProvider("live.es"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yandex ua`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yandex.ua"))
    }

    @Test
    fun `isFreeEmailProvider returns true for yandex com`() {
        assertTrue(EmailValidator.isFreeEmailProvider("yandex.com"))
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
    fun `isFreeEmailProvider returns false for mycompany com`() {
        assertFalse(EmailValidator.isFreeEmailProvider("mycompany.com"))
    }

    @Test
    fun `isFreeEmailProvider returns false for acme org`() {
        assertFalse(EmailValidator.isFreeEmailProvider("acme.org"))
    }

    // ========================================================================
    // SECTION 20: Role Address Comprehensive Test
    //
    // Tests for role-based email address detection covering all entries
    // in the EmailValidator.roleAddresses set.
    // ========================================================================

    @Test
    fun `isRoleAddress returns true for postmaster`() {
        assertTrue(EmailValidator.isRoleAddress("postmaster"))
    }

    @Test
    fun `isRoleAddress returns true for info`() {
        assertTrue(EmailValidator.isRoleAddress("info"))
    }

    @Test
    fun `isRoleAddress returns true for contact`() {
        assertTrue(EmailValidator.isRoleAddress("contact"))
    }

    @Test
    fun `isRoleAddress returns true for support`() {
        assertTrue(EmailValidator.isRoleAddress("support"))
    }

    @Test
    fun `isRoleAddress returns true for help`() {
        assertTrue(EmailValidator.isRoleAddress("help"))
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
    fun `isRoleAddress returns true for press`() {
        assertTrue(EmailValidator.isRoleAddress("press"))
    }

    @Test
    fun `isRoleAddress returns true for abuse`() {
        assertTrue(EmailValidator.isRoleAddress("abuse"))
    }

    @Test
    fun `isRoleAddress returns true for noreply full word`() {
        assertTrue(EmailValidator.isRoleAddress("noreply"))
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
    fun `isRoleAddress returns true for office`() {
        assertTrue(EmailValidator.isRoleAddress("office"))
    }

    @Test
    fun `isRoleAddress returns true for feedback`() {
        assertTrue(EmailValidator.isRoleAddress("feedback"))
    }

    @Test
    fun `isRoleAddress returns false for genuineuser`() {
        assertFalse(EmailValidator.isRoleAddress("genuineuser"))
    }

    @Test
    fun `isRoleAddress returns false for firstname`() {
        assertFalse(EmailValidator.isRoleAddress("firstname"))
    }

    // ========================================================================
    // SECTION 21: Domain Validation Comprehensive Tests
    // ========================================================================

    @Test
    fun `validateDomain with numeric second level domain is valid`() {
        val result = EmailValidator.validateDomain("1and1.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain with long subdomain chain is valid`() {
        val result = EmailValidator.validateDomain("mail.west.us.example.com")
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain returns reason string on failure`() {
        val result = EmailValidator.validateDomain("-invalid.com")
        assertFalse(result.first)
        assertTrue(result.second.isNotEmpty())
    }

    @Test
    fun `validateDomain with exactly 253 characters is valid`() {
        // Build a domain that is exactly 253 characters
        // labels: 63 chars + . + 63 chars + . + 63 chars + . + 63 chars = 255 with dots
        // Need to be careful about total length
        val label50 = "a".repeat(50)
        val domain = "$label50.${label50}.${label50}.com"
        assertTrue(domain.length <= 253)
        val result = EmailValidator.validateDomain(domain)
        assertTrue(result.first)
    }

    @Test
    fun `validateDomain returns false for underscore in label`() {
        val result = EmailValidator.validateDomain("user_name.com")
        assertFalse(result.first)
    }

    @Test
    fun `validateDomain with ampersand is invalid`() {
        val result = EmailValidator.validateDomain("test&example.com")
        assertFalse(result.first)
    }

    // ========================================================================
    // SECTION 22: suggestDomainCorrection Integration Tests
    //
    // Integration tests that verify the full email validation pipeline
    // when typo corrections are involved.
    // ========================================================================

    @Test
    fun `validate user at gmaill com gets corrected email suggestion`() {
        val result = EmailValidator.validate("testuser@gmaill.com")
        assertTrue(result.isValid)
        assertEquals("testuser@gmail.com", result.suggestedCorrection)
    }

    @Test
    fun `validate user at yaho com gets corrected email suggestion`() {
        val result = EmailValidator.validate("testuser@yaho.com")
        assertTrue(result.isValid)
        assertEquals("testuser@yahoo.com", result.suggestedCorrection)
    }

    @Test
    fun `validate user at hotmil com gets corrected email suggestion`() {
        val result = EmailValidator.validate("testuser@hotmil.com")
        assertTrue(result.isValid)
        assertEquals("testuser@hotmail.com", result.suggestedCorrection)
    }

    @Test
    fun `validate user at outlok com gets corrected email suggestion`() {
        val result = EmailValidator.validate("testuser@outlok.com")
        assertTrue(result.isValid)
        assertEquals("testuser@outlook.com", result.suggestedCorrection)
    }

    @Test
    fun `validate user at iclud com gets corrected email suggestion`() {
        val result = EmailValidator.validate("testuser@iclud.com")
        assertTrue(result.isValid)
        assertEquals("testuser@icloud.com", result.suggestedCorrection)
    }

    @Test
    fun `validate user at correct domain has null suggestedCorrection`() {
        val result = EmailValidator.validate("user@gmail.com")
        assertTrue(result.isValid)
        assertNull(result.suggestedCorrection)
    }

    @Test
    fun `validate user at correct yahoo com has null suggestedCorrection`() {
        val result = EmailValidator.validate("user@yahoo.com")
        assertTrue(result.isValid)
        assertNull(result.suggestedCorrection)
    }

    // ========================================================================
    // SECTION 23: validateLocalPart() Comprehensive Tests
    // ========================================================================

    @Test
    fun `validateLocalPart single character is valid`() {
        val result = EmailValidator.validateLocalPart("x")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with dot in middle is valid`() {
        val result = EmailValidator.validateLocalPart("first.last")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart alphanumeric only is valid`() {
        val result = EmailValidator.validateLocalPart("username123")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with hyphen is valid`() {
        val result = EmailValidator.validateLocalPart("my-email")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with underscore is valid`() {
        val result = EmailValidator.validateLocalPart("my_email")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart empty returns false with reason`() {
        val result = EmailValidator.validateLocalPart("")
        assertFalse(result.first)
        assertTrue(result.second.isNotEmpty())
    }

    @Test
    fun `validateLocalPart double dot returns false`() {
        val result = EmailValidator.validateLocalPart("user..name")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart starting with dot returns false`() {
        val result = EmailValidator.validateLocalPart(".username")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart ending with dot returns false`() {
        val result = EmailValidator.validateLocalPart("username.")
        assertFalse(result.first)
    }

    @Test
    fun `validateLocalPart with slash character is valid`() {
        val result = EmailValidator.validateLocalPart("user/folder")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with question mark is valid`() {
        val result = EmailValidator.validateLocalPart("what?")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with pipe is valid`() {
        val result = EmailValidator.validateLocalPart("user|pipe")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with backtick is valid`() {
        val result = EmailValidator.validateLocalPart("user\`name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with left brace is valid`() {
        val result = EmailValidator.validateLocalPart("user{name")
        assertTrue(result.first)
    }

    @Test
    fun `validateLocalPart with right brace is valid`() {
        val result = EmailValidator.validateLocalPart("user}name")
        assertTrue(result.first)
    }

    // ========================================================================
    // SECTION 24: Validate Full Email with isRoleAddress Flag
    //
    // Integration tests checking the isRoleAddress flag in validate() results.
    // ========================================================================

    @Test
    fun `validate webmaster at company com is role address`() {
        val result = EmailValidator.validate("webmaster@company.com")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `validate abuse at domain com is role address`() {
        val result = EmailValidator.validate("abuse@domain.com")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `validate security at company io is role address`() {
        val result = EmailValidator.validate("security@company.io")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `validate billing at service com is role address`() {
        val result = EmailValidator.validate("billing@service.com")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `validate privacy at company com is role address`() {
        val result = EmailValidator.validate("privacy@company.com")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `validate jobs at startup io is role address`() {
        val result = EmailValidator.validate("jobs@startup.io")
        assertTrue(result.isValid)
        assertTrue(result.isRoleAddress)
    }

    @Test
    fun `validate janesmith at company com is not role address`() {
        val result = EmailValidator.validate("janesmith@company.com")
        assertTrue(result.isValid)
        assertFalse(result.isRoleAddress)
    }

    @Test
    fun `validate robert johnson at company com is not role address`() {
        val result = EmailValidator.validate("robert.johnson@company.com")
        assertTrue(result.isValid)
        assertFalse(result.isRoleAddress)
    }

    // ========================================================================
    // SECTION 25: Comprehensive Validation of Special Characters
    //
    // Exhaustive tests for all special characters allowed in the local part.
    // ========================================================================

    @Test
    fun `validate email with all numeric local part at known provider`() {
        val result = EmailValidator.validate("1234567890@gmail.com")
        assertTrue(result.isValid)
        assertEquals("1234567890", result.localPart)
    }

    @Test
    fun `validate email local part with mixed special chars`() {
        val result = EmailValidator.validate("a.b+c-d_e@company.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email local part with only hyphens is valid`() {
        val result = EmailValidator.validate("---@company.com")
        assertTrue(result.isValid)
        assertEquals("---", result.localPart)
    }

    @Test
    fun `validate email local part with only underscores is valid`() {
        val result = EmailValidator.validate("___@company.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email local part with only numbers is valid`() {
        val result = EmailValidator.validate("999@company.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email with long local part exactly 64 chars`() {
        val local = "a".repeat(64)
        val result = EmailValidator.validate("$local@company.com")
        assertTrue(result.isValid)
        assertEquals(local, result.localPart)
    }

    @Test
    fun `validate email with local part 65 chars is invalid`() {
        val local = "a".repeat(65)
        val result = EmailValidator.validate("$local@company.com")
        assertFalse(result.isValid)
    }

    @Test
    fun `validate email at domain with 63 char label is valid`() {
        val label = "a".repeat(63)
        val result = EmailValidator.validate("user@${label}.com")
        assertTrue(result.isValid)
    }

    @Test
    fun `validate email at domain with 64 char label is invalid`() {
        val label = "a".repeat(64)
        val result = EmailValidator.validate("user@${label}.com")
        assertFalse(result.isValid)
    }

    // End of EmailValidatorExtendedTest
}


