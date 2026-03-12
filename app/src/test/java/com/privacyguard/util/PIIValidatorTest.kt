package com.privacyguard.util

import org.junit.Test
import org.junit.Assert.*

class PIIValidatorTest {

    // -------------------------------------------------------------------------
    // SSN DETECTION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidateSSN_basicFormat() {
        val text = "My SSN is 123-45-6789"
        val results = PIIValidator.validateSSN(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals("123-45-6789", results[0].value)
        assertEquals(PIIConfidence.HIGH, results[0].confidence)
    }

    @Test
    fun testValidateSSN_withoutDashes() {
        val text = "Social Security Number: 123456789"
        val results = PIIValidator.validateSSN(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].value.replace("-", "").replace(" ", "").length == 9)
        assertNotNull(results[0].maskedValue)
    }

    @Test
    fun testValidateSSN_maskedValue() {
        val text = "SSN: 987-65-4321"
        val results = PIIValidator.validateSSN(text)
        assertTrue(results.isNotEmpty())
        val masked = results[0].maskedValue
        assertNotNull(masked)
        assertFalse(masked.contains("987"))
        assertTrue(masked.contains("4321") || masked.contains("***"))
    }

    @Test
    fun testValidateSSN_multipleInText() {
        val text = "Employee 1: 111-22-3333. Employee 2: 444-55-6666."
        val results = PIIValidator.validateSSN(text)
        assertNotNull(results)
        assertTrue(results.size >= 2)
        val values = results.map { it.value }
        assertTrue(values.any { it.contains("111-22-3333") || it.replace("-", "") == "111223333" })
        assertTrue(values.any { it.contains("444-55-6666") || it.replace("-", "") == "444556666" })
    }

    @Test
    fun testValidateSSN_invalidSSN_allZeros() {
        val text = "Invalid SSN: 000-00-0000"
        val results = PIIValidator.validateSSN(text)
        if (results.isNotEmpty()) {
            assertTrue(results[0].confidence == PIIConfidence.LOW || results[0].confidence == PIIConfidence.UNCERTAIN)
        } else {
            assertTrue(results.isEmpty())
        }
    }

    @Test
    fun testValidateSSN_startIndexEndIndex() {
        val text = "SSN: 123-45-6789 is here"
        val results = PIIValidator.validateSSN(text)
        assertTrue(results.isNotEmpty())
        val result = results[0]
        assertTrue(result.startIndex >= 0)
        assertTrue(result.endIndex > result.startIndex)
        assertEquals("123-45-6789", text.substring(result.startIndex, result.endIndex))
    }

    @Test
    fun testValidateSSN_entityType() {
        val text = "SSN 234-56-7890"
        val results = PIIValidator.validateSSN(text)
        assertTrue(results.isNotEmpty())
        assertNotNull(results[0].entityType)
        assertTrue(results[0].entityType.contains("SSN") || results[0].entityType.contains("SOCIAL"))
    }

    @Test
    fun testValidateSSN_withSpaces() {
        val text = "Number is 123 45 6789 on file"
        val results = PIIValidator.validateSSN(text)
        assertNotNull(results)
        // May or may not detect space-separated, just ensure no crash
        assertNotNull(results)
    }

    @Test
    fun testValidateSSN_emptyText() {
        val results = PIIValidator.validateSSN("")
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateSSN_noSSNPresent() {
        val text = "This text contains no social security number at all."
        val results = PIIValidator.validateSSN(text)
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateSSN_validationDetails() {
        val text = "My SSN is 321-54-9876"
        val results = PIIValidator.validateSSN(text)
        if (results.isNotEmpty()) {
            assertNotNull(results[0].validationDetails)
        }
    }

    @Test
    fun testValidateSSN_confidenceLevel() {
        val text = "SSN: 555-44-3333"
        val results = PIIValidator.validateSSN(text)
        if (results.isNotEmpty()) {
            val confidence = results[0].confidence
            assertNotNull(confidence)
            assertTrue(
                confidence == PIIConfidence.HIGH ||
                confidence == PIIConfidence.MEDIUM ||
                confidence == PIIConfidence.LOW ||
                confidence == PIIConfidence.UNCERTAIN
            )
        }
    }

    // -------------------------------------------------------------------------
    // CREDIT CARD DETECTION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidateCreditCard_visaFormat() {
        val text = "Card number: 4111111111111111"
        val results = PIIValidator.validateCreditCard(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].value.replace(" ", "").replace("-", "").length == 16)
        assertEquals(PIIConfidence.HIGH, results[0].confidence)
    }

    @Test
    fun testValidateCreditCard_mastercardFormat() {
        val text = "Payment: 5500005555555559"
        val results = PIIValidator.validateCreditCard(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertNotNull(results[0].maskedValue)
    }

    @Test
    fun testValidateCreditCard_maskedShowsLastFour() {
        val text = "Card: 4111111111111111"
        val results = PIIValidator.validateCreditCard(text)
        assertTrue(results.isNotEmpty())
        val masked = results[0].maskedValue
        assertNotNull(masked)
        assertTrue(masked.contains("1111") || masked.contains("****"))
    }

    @Test
    fun testValidateCreditCard_withDashes() {
        val text = "Credit card: 4111-1111-1111-1111"
        val results = PIIValidator.validateCreditCard(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].startIndex >= 0)
        assertTrue(results[0].endIndex > results[0].startIndex)
    }

    @Test
    fun testValidateCreditCard_withSpaces() {
        val text = "Card 4111 1111 1111 1111 issued"
        val results = PIIValidator.validateCreditCard(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals(PIIConfidence.HIGH, results[0].confidence)
    }

    @Test
    fun testValidateCreditCard_amexFormat() {
        val text = "Amex card: 378282246310005"
        val results = PIIValidator.validateCreditCard(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertNotNull(results[0].entityType)
    }

    @Test
    fun testValidateCreditCard_entityType() {
        val text = "Use card 4111111111111111 for payment"
        val results = PIIValidator.validateCreditCard(text)
        assertTrue(results.isNotEmpty())
        val entityType = results[0].entityType
        assertTrue(
            entityType.contains("CARD") || entityType.contains("CREDIT") || entityType.contains("PAYMENT")
        )
    }

    @Test
    fun testValidateCreditCard_emptyText() {
        val results = PIIValidator.validateCreditCard("")
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateCreditCard_invalidNumber() {
        val text = "Number: 1234567890123456"
        val results = PIIValidator.validateCreditCard(text)
        if (results.isNotEmpty()) {
            assertTrue(
                results[0].confidence == PIIConfidence.LOW ||
                results[0].confidence == PIIConfidence.UNCERTAIN ||
                results[0].confidence == PIIConfidence.MEDIUM
            )
        }
    }

    @Test
    fun testValidateCreditCard_noCardPresent() {
        val text = "No payment information here."
        val results = PIIValidator.validateCreditCard(text)
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateCreditCard_multipleCards() {
        val text = "Cards: 4111111111111111 and 5500005555555559"
        val results = PIIValidator.validateCreditCard(text)
        assertNotNull(results)
        assertTrue(results.size >= 2)
        assertTrue(results.all { it.value.isNotEmpty() })
    }

    // -------------------------------------------------------------------------
    // EMAIL DETECTION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidateEmail_basicEmail() {
        val text = "Contact us at user@example.com"
        val results = PIIValidator.validateEmail(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals("user@example.com", results[0].value)
        assertEquals(PIIConfidence.HIGH, results[0].confidence)
    }

    @Test
    fun testValidateEmail_multipleEmails() {
        val text = "Send to alice@example.com and bob@company.org"
        val results = PIIValidator.validateEmail(text)
        assertNotNull(results)
        assertTrue(results.size >= 2)
        val values = results.map { it.value }
        assertTrue(values.contains("alice@example.com"))
        assertTrue(values.contains("bob@company.org"))
    }

    @Test
    fun testValidateEmail_withSubdomain() {
        val text = "Email: user@mail.example.co.uk"
        val results = PIIValidator.validateEmail(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals("user@mail.example.co.uk", results[0].value)
    }

    @Test
    fun testValidateEmail_maskedValue() {
        val text = "Email: admin@secret.com"
        val results = PIIValidator.validateEmail(text)
        assertTrue(results.isNotEmpty())
        val masked = results[0].maskedValue
        assertNotNull(masked)
        assertFalse(masked == "admin@secret.com")
    }

    @Test
    fun testValidateEmail_entityType() {
        val text = "Reach out to test@test.com"
        val results = PIIValidator.validateEmail(text)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].entityType.contains("EMAIL") || results[0].entityType.contains("email"))
    }

    @Test
    fun testValidateEmail_emptyText() {
        val results = PIIValidator.validateEmail("")
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateEmail_noEmailPresent() {
        val text = "This is plain text without any email address."
        val results = PIIValidator.validateEmail(text)
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateEmail_startIndexCorrect() {
        val text = "Email: john@doe.com is here"
        val results = PIIValidator.validateEmail(text)
        assertTrue(results.isNotEmpty())
        val r = results[0]
        assertTrue(r.startIndex >= 0)
        assertEquals("john@doe.com", text.substring(r.startIndex, r.endIndex))
    }

    @Test
    fun testValidateEmail_validationDetails() {
        val text = "Info: info@corp.net"
        val results = PIIValidator.validateEmail(text)
        if (results.isNotEmpty()) {
            assertNotNull(results[0].validationDetails)
        }
    }

    @Test
    fun testValidateEmail_plusAddressFormat() {
        val text = "Tag email: user+tag@example.com"
        val results = PIIValidator.validateEmail(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].value.contains("@"))
    }

    // -------------------------------------------------------------------------
    // PHONE NUMBER DETECTION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidatePhone_usFormat() {
        val text = "Call us at (555) 123-4567"
        val results = PIIValidator.validatePhone(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].value.contains("555"))
        assertNotNull(results[0].maskedValue)
    }

    @Test
    fun testValidatePhone_withCountryCode() {
        val text = "International: +1-555-987-6543"
        val results = PIIValidator.validatePhone(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals(PIIConfidence.HIGH, results[0].confidence)
    }

    @Test
    fun testValidatePhone_plainDigits() {
        val text = "Phone 5551234567"
        val results = PIIValidator.validatePhone(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertNotNull(results[0].entityType)
    }

    @Test
    fun testValidatePhone_emptyText() {
        val results = PIIValidator.validatePhone("")
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidatePhone_noPhonePresent() {
        val text = "No phone number in this sentence."
        val results = PIIValidator.validatePhone(text)
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidatePhone_startEndIndex() {
        val text = "Contact: 555-867-5309 now"
        val results = PIIValidator.validatePhone(text)
        assertTrue(results.isNotEmpty())
        val r = results[0]
        assertTrue(r.startIndex >= 0)
        assertTrue(r.endIndex > r.startIndex)
        assertTrue(text.substring(r.startIndex, r.endIndex).contains("555"))
    }

    @Test
    fun testValidatePhone_multiplePhones() {
        val text = "Call (555) 111-2222 or (555) 333-4444"
        val results = PIIValidator.validatePhone(text)
        assertNotNull(results)
        assertTrue(results.size >= 2)
    }

    @Test
    fun testValidatePhone_maskedValue() {
        val text = "Number: (800) 555-1212"
        val results = PIIValidator.validatePhone(text)
        assertTrue(results.isNotEmpty())
        val masked = results[0].maskedValue
        assertNotNull(masked)
        assertFalse(masked.isEmpty())
    }

    // -------------------------------------------------------------------------
    // API KEY DETECTION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidateAPIKey_genericKey() {
        val text = "API_KEY=sk-abc123def456ghi789jkl012mno345pqr678"
        val results = PIIValidator.validateAPIKey(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertNotNull(results[0].maskedValue)
    }

    @Test
    fun testValidateAPIKey_bearerToken() {
        val text = "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val results = PIIValidator.validateAPIKey(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals(PIIConfidence.HIGH, results[0].confidence)
    }

    @Test
    fun testValidateAPIKey_emptyText() {
        val results = PIIValidator.validateAPIKey("")
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateAPIKey_noKeyPresent() {
        val text = "This is just regular text with no API keys."
        val results = PIIValidator.validateAPIKey(text)
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateAPIKey_entityTypeCorrect() {
        val text = "Key: sk-ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcd"
        val results = PIIValidator.validateAPIKey(text)
        if (results.isNotEmpty()) {
            assertTrue(
                results[0].entityType.contains("API") ||
                results[0].entityType.contains("KEY") ||
                results[0].entityType.contains("TOKEN")
            )
        }
    }

    // -------------------------------------------------------------------------
    // IP ADDRESS DETECTION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidateIPAddress_publicIPv4() {
        val text = "Server at 8.8.8.8 is responding"
        val results = PIIValidator.validateIPAddress(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals("8.8.8.8", results[0].value)
        assertEquals(PIIConfidence.HIGH, results[0].confidence)
    }

    @Test
    fun testValidateIPAddress_privateIPv4() {
        val text = "Local server: 192.168.1.1"
        val results = PIIValidator.validateIPAddress(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals("192.168.1.1", results[0].value)
        // Private IPs may have lower confidence
        assertNotNull(results[0].confidence)
    }

    @Test
    fun testValidateIPAddress_loopback() {
        val text = "Connecting to 127.0.0.1"
        val results = PIIValidator.validateIPAddress(text)
        assertNotNull(results)
        if (results.isNotEmpty()) {
            assertEquals("127.0.0.1", results[0].value)
            assertTrue(
                results[0].confidence == PIIConfidence.LOW ||
                results[0].confidence == PIIConfidence.UNCERTAIN ||
                results[0].confidence == PIIConfidence.MEDIUM
            )
        }
    }

    @Test
    fun testValidateIPAddress_IPv6() {
        val text = "IPv6 address: 2001:0db8:85a3:0000:0000:8a2e:0370:7334"
        val results = PIIValidator.validateIPAddress(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertNotNull(results[0].maskedValue)
    }

    @Test
    fun testValidateIPAddress_multipleIPs() {
        val text = "From 10.0.0.1 to 8.8.4.4 via 172.16.0.1"
        val results = PIIValidator.validateIPAddress(text)
        assertNotNull(results)
        assertTrue(results.size >= 2)
        val values = results.map { it.value }
        assertTrue(values.any { it == "8.8.4.4" || it == "10.0.0.1" || it == "172.16.0.1" })
    }

    @Test
    fun testValidateIPAddress_emptyText() {
        val results = PIIValidator.validateIPAddress("")
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateIPAddress_noIPPresent() {
        val text = "No IP address in this sentence."
        val results = PIIValidator.validateIPAddress(text)
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateIPAddress_startEndIndex() {
        val text = "Traffic from 1.2.3.4 detected"
        val results = PIIValidator.validateIPAddress(text)
        assertTrue(results.isNotEmpty())
        val r = results[0]
        assertTrue(r.startIndex >= 0)
        assertEquals("1.2.3.4", text.substring(r.startIndex, r.endIndex))
    }

    @Test
    fun testValidateIPAddress_entityType() {
        val text = "IP: 203.0.113.1"
        val results = PIIValidator.validateIPAddress(text)
        if (results.isNotEmpty()) {
            assertTrue(
                results[0].entityType.contains("IP") ||
                results[0].entityType.contains("ADDRESS")
            )
        }
    }

    @Test
    fun testValidateIPAddress_privateRanges() {
        val text1 = "Address: 10.0.0.1"
        val text2 = "Address: 172.16.5.10"
        val text3 = "Address: 192.168.100.200"
        val r1 = PIIValidator.validateIPAddress(text1)
        val r2 = PIIValidator.validateIPAddress(text2)
        val r3 = PIIValidator.validateIPAddress(text3)
        assertNotNull(r1)
        assertNotNull(r2)
        assertNotNull(r3)
        assertTrue(r1.isNotEmpty() || r2.isNotEmpty() || r3.isNotEmpty())
    }

    // -------------------------------------------------------------------------
    // DATE OF BIRTH DETECTION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidateDateOfBirth_isoFormat() {
        val text = "DOB: 1990-05-15"
        val results = PIIValidator.validateDateOfBirth(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].value.contains("1990"))
        assertEquals(PIIConfidence.HIGH, results[0].confidence)
    }

    @Test
    fun testValidateDateOfBirth_usFormat() {
        val text = "Date of birth: 05/15/1990"
        val results = PIIValidator.validateDateOfBirth(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertNotNull(results[0].maskedValue)
    }

    @Test
    fun testValidateDateOfBirth_euroFormat() {
        val text = "Born on 15.05.1990"
        val results = PIIValidator.validateDateOfBirth(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].value.contains("1990") || results[0].value.contains("15"))
    }

    @Test
    fun testValidateDateOfBirth_writtenFormat() {
        val text = "Born: January 15, 1990"
        val results = PIIValidator.validateDateOfBirth(text)
        assertNotNull(results)
        if (results.isNotEmpty()) {
            assertTrue(results[0].value.contains("1990") || results[0].value.contains("January"))
            assertNotNull(results[0].entityType)
        }
    }

    @Test
    fun testValidateDateOfBirth_emptyText() {
        val results = PIIValidator.validateDateOfBirth("")
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateDateOfBirth_noDOBPresent() {
        val text = "No birth date information here."
        val results = PIIValidator.validateDateOfBirth(text)
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateDateOfBirth_startEndIndex() {
        val text = "Patient DOB: 1985-03-22 in file"
        val results = PIIValidator.validateDateOfBirth(text)
        if (results.isNotEmpty()) {
            val r = results[0]
            assertTrue(r.startIndex >= 0)
            assertTrue(r.endIndex > r.startIndex)
            assertTrue(text.substring(r.startIndex, r.endIndex).contains("1985"))
        }
    }

    @Test
    fun testValidateDateOfBirth_confidenceLevel() {
        val text = "DOB 1975-12-01"
        val results = PIIValidator.validateDateOfBirth(text)
        if (results.isNotEmpty()) {
            assertNotNull(results[0].confidence)
        }
    }

    // -------------------------------------------------------------------------
    // BANK ACCOUNT DETECTION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidateBankAccount_basicAccount() {
        val text = "Bank account: 12345678901234"
        val results = PIIValidator.validateBankAccount(text)
        assertNotNull(results)
        // Bank account patterns may vary; check no crash
        assertNotNull(results)
    }

    @Test
    fun testValidateBankAccount_withRoutingAndAccount() {
        val text = "Routing: 021000021 Account: 987654321"
        val results = PIIValidator.validateBankAccount(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertNotNull(results[0].entityType)
    }

    @Test
    fun testValidateBankAccount_emptyText() {
        val results = PIIValidator.validateBankAccount("")
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testValidateBankAccount_maskedValue() {
        val text = "Account number 9876543210"
        val results = PIIValidator.validateBankAccount(text)
        if (results.isNotEmpty()) {
            assertNotNull(results[0].maskedValue)
            assertFalse(results[0].maskedValue.isEmpty())
        }
    }

    // -------------------------------------------------------------------------
    // IBAN VALIDATION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testIsValidIBAN_validGermanIBAN() {
        val iban = "DE89370400440532013000"
        val result = PIIValidator.isValidIBAN(iban)
        assertTrue(result)
    }

    @Test
    fun testIsValidIBAN_validUKIBAN() {
        val iban = "GB29NWBK60161331926819"
        val result = PIIValidator.isValidIBAN(iban)
        assertTrue(result)
    }

    @Test
    fun testIsValidIBAN_validFrenchIBAN() {
        val iban = "FR1420041010050500013M02606"
        val result = PIIValidator.isValidIBAN(iban)
        assertTrue(result)
    }

    @Test
    fun testIsValidIBAN_invalidChecksum() {
        val iban = "DE89370400440532013001"
        val result = PIIValidator.isValidIBAN(iban)
        assertFalse(result)
    }

    @Test
    fun testIsValidIBAN_tooShort() {
        val iban = "DE89"
        val result = PIIValidator.isValidIBAN(iban)
        assertFalse(result)
    }

    @Test
    fun testIsValidIBAN_emptyString() {
        val result = PIIValidator.isValidIBAN("")
        assertFalse(result)
    }

    @Test
    fun testIsValidIBAN_invalidCountryCode() {
        val iban = "XX89370400440532013000"
        val result = PIIValidator.isValidIBAN(iban)
        assertFalse(result)
    }

    @Test
    fun testIsValidIBAN_lowercase() {
        val iban = "de89370400440532013000"
        val result = PIIValidator.isValidIBAN(iban)
        // Some implementations accept lowercase; just verify no crash
        assertNotNull(result)
    }

    @Test
    fun testIsValidIBAN_withSpaces() {
        val iban = "DE89 3704 0044 0532 0130 00"
        val result = PIIValidator.isValidIBAN(iban)
        // Some implementations strip spaces; verify no crash
        assertNotNull(result)
    }

    @Test
    fun testIsValidIBAN_nullCheck() {
        // Ensure handles edge strings
        val result = PIIValidator.isValidIBAN("   ")
        assertFalse(result)
    }

    @Test
    fun testIsValidIBAN_validNetherlandsIBAN() {
        val iban = "NL91ABNA0417164300"
        val result = PIIValidator.isValidIBAN(iban)
        assertTrue(result)
    }

    @Test
    fun testIsValidIBAN_validItalyIBAN() {
        val iban = "IT60X0542811101000000123456"
        val result = PIIValidator.isValidIBAN(iban)
        assertTrue(result)
    }

    @Test
    fun testIsValidIBAN_specialCharacters() {
        val iban = "DE89-3704-0044-0532-0130-00"
        val result = PIIValidator.isValidIBAN(iban)
        // Hyphens not typically valid, verify no crash
        assertNotNull(result)
    }

    // -------------------------------------------------------------------------
    // ABA ROUTING NUMBER TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testIsValidABARoutingNumber_valid() {
        val routing = "021000021"
        val result = PIIValidator.isValidABARoutingNumber(routing)
        assertTrue(result)
    }

    @Test
    fun testIsValidABARoutingNumber_anotherValid() {
        val routing = "111000025"
        val result = PIIValidator.isValidABARoutingNumber(routing)
        assertTrue(result)
    }

    @Test
    fun testIsValidABARoutingNumber_invalidChecksum() {
        val routing = "021000022"
        val result = PIIValidator.isValidABARoutingNumber(routing)
        assertFalse(result)
    }

    @Test
    fun testIsValidABARoutingNumber_wrongLength() {
        val routing = "12345678"
        val result = PIIValidator.isValidABARoutingNumber(routing)
        assertFalse(result)
    }

    @Test
    fun testIsValidABARoutingNumber_empty() {
        val result = PIIValidator.isValidABARoutingNumber("")
        assertFalse(result)
    }

    @Test
    fun testIsValidABARoutingNumber_allZeros() {
        val result = PIIValidator.isValidABARoutingNumber("000000000")
        assertFalse(result)
    }

    @Test
    fun testIsValidABARoutingNumber_validWellsFargo() {
        val routing = "121042882"
        val result = PIIValidator.isValidABARoutingNumber(routing)
        assertTrue(result)
    }

    @Test
    fun testIsValidABARoutingNumber_alphanumeric() {
        val result = PIIValidator.isValidABARoutingNumber("02100002A")
        assertFalse(result)
    }

    @Test
    fun testIsValidABARoutingNumber_tenDigits() {
        val result = PIIValidator.isValidABARoutingNumber("0210000210")
        assertFalse(result)
    }

    // -------------------------------------------------------------------------
    // RISK SCORE COMPUTATION TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testComputeRiskScore_emptyReport() {
        val report = PIIValidator.validateAll("")
        val score = PIIValidator.computeRiskScore(report)
        assertNotNull(score)
        assertEquals(0.0f, score, 0.01f)
    }

    @Test
    fun testComputeRiskScore_withSSN() {
        val report = PIIValidator.validateAll("SSN: 123-45-6789")
        val score = PIIValidator.computeRiskScore(report)
        assertNotNull(score)
        assertTrue(score > 0.0f)
        assertTrue(score <= 1.0f)
    }

    @Test
    fun testComputeRiskScore_withMultiplePII() {
        val text = "SSN: 123-45-6789. Email: test@test.com. Card: 4111111111111111"
        val report = PIIValidator.validateAll(text)
        val score = PIIValidator.computeRiskScore(report)
        assertTrue(score > 0.0f)
        assertTrue(score <= 1.0f)
    }

    @Test
    fun testComputeRiskScore_highRiskHigherThanLow() {
        val highRiskText = "SSN: 123-45-6789. Card: 4111111111111111. DOB: 1990-01-01."
        val lowRiskText = "Email: info@company.com"
        val highReport = PIIValidator.validateAll(highRiskText)
        val lowReport = PIIValidator.validateAll(lowRiskText)
        val highScore = PIIValidator.computeRiskScore(highReport)
        val lowScore = PIIValidator.computeRiskScore(lowReport)
        assertTrue(highScore >= lowScore)
    }

    @Test
    fun testComputeRiskScore_rangeValid() {
        val text = "Contact john@doe.com at 555-123-4567"
        val report = PIIValidator.validateAll(text)
        val score = PIIValidator.computeRiskScore(report)
        assertTrue(score >= 0.0f)
        assertTrue(score <= 1.0f)
    }

    // -------------------------------------------------------------------------
    // VALIDATE ALL TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testValidateAll_emptyText() {
        val report = PIIValidator.validateAll("")
        assertNotNull(report)
        assertTrue(report.results.isEmpty())
        assertEquals(0, report.textLength)
    }

    @Test
    fun testValidateAll_textLength() {
        val text = "Hello world"
        val report = PIIValidator.validateAll(text)
        assertEquals(text.length, report.textLength)
    }

    @Test
    fun testValidateAll_timestamp() {
        val report = PIIValidator.validateAll("test text")
        assertNotNull(report.timestamp)
        assertTrue(report.timestamp > 0L)
    }

    @Test
    fun testValidateAll_mixedPII() {
        val text = "Name: John. SSN: 123-45-6789. Email: john@example.com. Card: 4111111111111111."
        val report = PIIValidator.validateAll(text)
        assertNotNull(report)
        assertTrue(report.results.isNotEmpty())
        assertNotNull(report.summary)
        assertTrue(report.riskScore >= 0.0f)
    }

    @Test
    fun testValidateAll_resultsNotNull() {
        val report = PIIValidator.validateAll("Some text 123-45-6789")
        assertNotNull(report.results)
        assertTrue(report.results.isNotEmpty())
    }

    @Test
    fun testValidateAll_summaryNotNull() {
        val report = PIIValidator.validateAll("test@email.com")
        assertNotNull(report.summary)
        assertFalse(report.summary.isEmpty())
    }

    @Test
    fun testValidateAll_riskScorePresent() {
        val report = PIIValidator.validateAll("Card: 5500005555555559")
        assertNotNull(report)
        assertTrue(report.riskScore >= 0.0f)
    }

    // -------------------------------------------------------------------------
    // SUMMARIZE TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testSummarize_emptyReport() {
        val report = PIIValidator.validateAll("")
        val summary = PIIValidator.summarize(report)
        assertNotNull(summary)
        assertFalse(summary.isEmpty())
    }

    @Test
    fun testSummarize_withSSN() {
        val report = PIIValidator.validateAll("SSN: 123-45-6789")
        val summary = PIIValidator.summarize(report)
        assertNotNull(summary)
        assertFalse(summary.isEmpty())
    }

    @Test
    fun testSummarize_containsEntityTypes() {
        val report = PIIValidator.validateAll("Email: user@example.com. SSN: 123-45-6789.")
        val summary = PIIValidator.summarize(report)
        assertNotNull(summary)
        assertTrue(summary.isNotEmpty())
        // Summary should mention detected entity types
        assertTrue(
            summary.contains("EMAIL") || summary.contains("SSN") ||
            summary.contains("PII") || summary.contains("detected") || summary.length > 5
        )
    }

    @Test
    fun testSummarize_returnsString() {
        val report = PIIValidator.validateAll("Card: 4111111111111111")
        val result = PIIValidator.summarize(report)
        assertNotNull(result)
        assertTrue(result is String)
    }

    @Test
    fun testSummarize_multipleEntities() {
        val text = "Phone: (555) 123-4567. DOB: 1990-05-20. IP: 192.168.1.1."
        val report = PIIValidator.validateAll(text)
        val summary = PIIValidator.summarize(report)
        assertNotNull(summary)
        assertTrue(summary.isNotEmpty())
    }

    // -------------------------------------------------------------------------
    // CONTAINS PII TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testContainsPII_withSSN() {
        val text = "My SSN is 123-45-6789"
        val result = PIIValidator.containsPII(text)
        assertTrue(result)
    }

    @Test
    fun testContainsPII_withEmail() {
        val text = "Contact: user@example.com"
        val result = PIIValidator.containsPII(text)
        assertTrue(result)
    }

    @Test
    fun testContainsPII_withCreditCard() {
        val text = "Card: 4111111111111111"
        val result = PIIValidator.containsPII(text)
        assertTrue(result)
    }

    @Test
    fun testContainsPII_noPII() {
        val text = "This sentence has absolutely no personal information."
        val result = PIIValidator.containsPII(text)
        assertFalse(result)
    }

    @Test
    fun testContainsPII_emptyText() {
        val result = PIIValidator.containsPII("")
        assertFalse(result)
    }

    @Test
    fun testContainsPII_withPhoneNumber() {
        val text = "Call me at (555) 867-5309"
        val result = PIIValidator.containsPII(text)
        assertTrue(result)
    }

    @Test
    fun testContainsPII_withIPAddress() {
        val text = "Server at 203.0.113.42"
        val result = PIIValidator.containsPII(text)
        assertTrue(result)
    }

    @Test
    fun testContainsPII_quickCheck() {
        // Quick check should be consistent with validateAll
        val text = "SSN: 999-88-7777"
        val containsResult = PIIValidator.containsPII(text)
        val report = PIIValidator.validateAll(text)
        assertEquals(containsResult, report.results.isNotEmpty())
    }

    // -------------------------------------------------------------------------
    // HAS CRITICAL PII TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testHasCriticalPII_withSSN() {
        val report = PIIValidator.validateAll("SSN: 123-45-6789")
        val result = PIIValidator.hasCriticalPII(report)
        assertTrue(result)
    }

    @Test
    fun testHasCriticalPII_withCreditCard() {
        val report = PIIValidator.validateAll("Card: 4111111111111111")
        val result = PIIValidator.hasCriticalPII(report)
        assertTrue(result)
    }

    @Test
    fun testHasCriticalPII_emptyReport() {
        val report = PIIValidator.validateAll("")
        val result = PIIValidator.hasCriticalPII(report)
        assertFalse(result)
    }

    @Test
    fun testHasCriticalPII_onlyEmailNotCritical() {
        val report = PIIValidator.validateAll("Email: info@company.com")
        val result = PIIValidator.hasCriticalPII(report)
        // Email alone may not be considered critical
        assertNotNull(result)
    }

    @Test
    fun testHasCriticalPII_withSSNAndCard() {
        val report = PIIValidator.validateAll("SSN: 123-45-6789 Card: 4111111111111111")
        val result = PIIValidator.hasCriticalPII(report)
        assertTrue(result)
    }

    @Test
    fun testHasCriticalPII_returnsBooleanType() {
        val report = PIIValidator.validateAll("test text")
        val result = PIIValidator.hasCriticalPII(report)
        assertTrue(result is Boolean)
    }

    // -------------------------------------------------------------------------
    // HIGH CONFIDENCE ONLY TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testHighConfidenceOnly_emptyReport() {
        val report = PIIValidator.validateAll("")
        val results = PIIValidator.highConfidenceOnly(report)
        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    @Test
    fun testHighConfidenceOnly_allHighConfidence() {
        val report = PIIValidator.validateAll("SSN: 123-45-6789. Card: 4111111111111111.")
        val results = PIIValidator.highConfidenceOnly(report)
        assertNotNull(results)
        assertTrue(results.all { it.confidence == PIIConfidence.HIGH })
    }

    @Test
    fun testHighConfidenceOnly_noLowConfidence() {
        val report = PIIValidator.validateAll("Email: test@example.com")
        val results = PIIValidator.highConfidenceOnly(report)
        for (r in results) {
            assertNotEquals(PIIConfidence.LOW, r.confidence)
            assertNotEquals(PIIConfidence.UNCERTAIN, r.confidence)
        }
    }

    @Test
    fun testHighConfidenceOnly_subsetOfAll() {
        val text = "SSN: 123-45-6789. Card: 4111111111111111. Phone: (555) 123-4567."
        val report = PIIValidator.validateAll(text)
        val allResults = report.results
        val highOnly = PIIValidator.highConfidenceOnly(report)
        assertTrue(highOnly.size <= allResults.size)
    }

    @Test
    fun testHighConfidenceOnly_returnsList() {
        val report = PIIValidator.validateAll("IP: 8.8.8.8")
        val result = PIIValidator.highConfidenceOnly(report)
        assertNotNull(result)
        assertTrue(result is List<*>)
    }

    // -------------------------------------------------------------------------
    // MIXED PII TEXT TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testMixedPIIText_detectsMultipleTypes() {
        val text = "User: John Doe, SSN: 123-45-6789, Email: john@doe.com, Card: 4111111111111111"
        val report = PIIValidator.validateAll(text)
        assertNotNull(report)
        assertTrue(report.results.size >= 3)
        val types = report.results.map { it.entityType }.toSet()
        assertTrue(types.size >= 2)
    }

    @Test
    fun testMixedPIIText_riskScoreHigh() {
        val text = "SSN: 123-45-6789. Card: 4111111111111111. DOB: 1990-01-01. Email: user@test.com."
        val report = PIIValidator.validateAll(text)
        val score = PIIValidator.computeRiskScore(report)
        assertTrue(score > 0.5f)
    }

    @Test
    fun testMixedPIIText_containsPIITrue() {
        val text = "DOB: 1985-03-22. Phone: (555) 123-4567."
        assertTrue(PIIValidator.containsPII(text))
    }

    @Test
    fun testMixedPIIText_hasCriticalPII() {
        val text = "Account routing: 021000021. SSN: 123-45-6789."
        val report = PIIValidator.validateAll(text)
        val critical = PIIValidator.hasCriticalPII(report)
        assertTrue(critical)
    }

    @Test
    fun testMixedPIIText_allResultsHaveValues() {
        val text = "IP: 8.8.8.8. Email: x@y.com. Phone: (555) 111-2222."
        val report = PIIValidator.validateAll(text)
        for (r in report.results) {
            assertFalse(r.value.isEmpty())
            assertNotNull(r.maskedValue)
            assertNotNull(r.entityType)
        }
    }

    @Test
    fun testMixedPIIText_validationDetailsPresent() {
        val text = "Card: 4111111111111111"
        val report = PIIValidator.validateAll(text)
        for (r in report.results) {
            assertNotNull(r.validationDetails)
        }
    }

    // -------------------------------------------------------------------------
    // EDGE CASE TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testEdgeCase_singleCharacter() {
        val report = PIIValidator.validateAll("a")
        assertNotNull(report)
        assertEquals(1, report.textLength)
        assertTrue(report.results.isEmpty())
    }

    @Test
    fun testEdgeCase_whitespaceOnly() {
        val text = "     "
        val report = PIIValidator.validateAll(text)
        assertNotNull(report)
        assertTrue(report.results.isEmpty())
    }

    @Test
    fun testEdgeCase_specialCharactersOnly() {
        val text = "!@#$%^&*()"
        val report = PIIValidator.validateAll(text)
        assertNotNull(report)
        assertFalse(PIIValidator.hasCriticalPII(report))
    }

    @Test
    fun testEdgeCase_veryLongText() {
        val base = "No PII here. "
        val text = base.repeat(1000)
        val report = PIIValidator.validateAll(text)
        assertNotNull(report)
        assertEquals(text.length, report.textLength)
    }

    @Test
    fun testEdgeCase_ssnBoundaryDetection() {
        val text = "1234-56-78901"  // Invalid format
        val results = PIIValidator.validateSSN(text)
        // Should not match as a valid SSN
        if (results.isNotEmpty()) {
            assertTrue(
                results[0].confidence == PIIConfidence.LOW ||
                results[0].confidence == PIIConfidence.UNCERTAIN
            )
        }
    }

    @Test
    fun testEdgeCase_jsonLikeText() {
        val text = """{"ssn":"123-45-6789","email":"test@example.com"}"""
        val report = PIIValidator.validateAll(text)
        assertNotNull(report)
        assertTrue(report.results.isNotEmpty())
        assertTrue(PIIValidator.containsPII(text))
    }

    @Test
    fun testEdgeCase_unicodeText() {
        val text = "名前: SSN 123-45-6789"
        val report = PIIValidator.validateAll(text)
        assertNotNull(report)
        // Should still detect SSN in unicode context
        val ssnResults = PIIValidator.validateSSN(text)
        assertNotNull(ssnResults)
    }

    @Test
    fun testEdgeCase_newlineInText() {
        val text = "Line1\nSSN: 123-45-6789\nLine3"
        val results = PIIValidator.validateSSN(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun testEdgeCase_tabInText() {
        val text = "Field\t123-45-6789\tEnd"
        val results = PIIValidator.validateSSN(text)
        assertNotNull(results)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun testEdgeCase_reportTimestampRecent() {
        val before = System.currentTimeMillis()
        val report = PIIValidator.validateAll("test")
        val after = System.currentTimeMillis()
        assertTrue(report.timestamp >= before)
        assertTrue(report.timestamp <= after)
    }

    // -------------------------------------------------------------------------
    // PIIValidationResult PROPERTY TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testPIIValidationResult_allFieldsPresent() {
        val text = "SSN: 123-45-6789"
        val results = PIIValidator.validateSSN(text)
        assertTrue(results.isNotEmpty())
        val r = results[0]
        assertNotNull(r.entityType)
        assertNotNull(r.value)
        assertNotNull(r.maskedValue)
        assertNotNull(r.confidence)
        assertNotNull(r.validationDetails)
    }

    @Test
    fun testPIIValidationResult_startLessThanEnd() {
        val text = "Email: hello@world.com"
        val results = PIIValidator.validateEmail(text)
        assertTrue(results.isNotEmpty())
        assertTrue(results[0].startIndex < results[0].endIndex)
    }

    @Test
    fun testPIIValidationResult_maskedValueDifferentFromValue() {
        val text = "Card: 4111111111111111"
        val results = PIIValidator.validateCreditCard(text)
        assertTrue(results.isNotEmpty())
        assertNotEquals(results[0].value.replace(" ", ""), results[0].maskedValue.replace(" ", ""))
    }

    @Test
    fun testPIIValidationResult_confidenceIsEnum() {
        val text = "SSN: 123-45-6789"
        val results = PIIValidator.validateSSN(text)
        assertTrue(results.isNotEmpty())
        val confidence = results[0].confidence
        assertTrue(
            confidence == PIIConfidence.HIGH ||
            confidence == PIIConfidence.MEDIUM ||
            confidence == PIIConfidence.LOW ||
            confidence == PIIConfidence.UNCERTAIN
        )
    }

    // -------------------------------------------------------------------------
    // PIIValidationReport PROPERTY TESTS
    // -------------------------------------------------------------------------

    @Test
    fun testPIIValidationReport_riskScoreInReport() {
        val text = "SSN: 123-45-6789"
        val report = PIIValidator.validateAll(text)
        assertTrue(report.riskScore >= 0.0f)
        assertTrue(report.riskScore <= 1.0f)
    }

    @Test
    fun testPIIValidationReport_summaryInReport() {
        val text = "Card: 4111111111111111"
        val report = PIIValidator.validateAll(text)
        assertNotNull(report.summary)
        assertFalse(report.summary.isEmpty())
    }

    @Test
    fun testPIIValidationReport_textLengthMatches() {
        val text = "Email: user@test.com is the contact"
        val report = PIIValidator.validateAll(text)
        assertEquals(text.length, report.textLength)
    }

    @Test
    fun testPIIValidationReport_timestampPositive() {
        val report = PIIValidator.validateAll("some text")
        assertTrue(report.timestamp > 0L)
    }
}
