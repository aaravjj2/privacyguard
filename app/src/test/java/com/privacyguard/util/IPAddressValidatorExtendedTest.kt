package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive test suite for IPAddressValidator.
 *
 * Sections:
 *  1  – Valid public IPv4 addresses            (25 tests)
 *  2  – Private IPv4 ranges                    (25 tests)
 *  3  – Loopback and special addresses         (20 tests)
 *  4  – Invalid IPv4                           (25 tests)
 *  5  – IPv6 validation                        (25 tests)
 *  6  – extractFromText function               (25 tests)
 *  7  – isPrivate / isLoopback / isPublic      (25 tests)
 *  8  – CIDR / subnet membership               (30 tests)
 *  9  – Edge cases                             (20 tests)
 *
 * Total: 220 test functions
 */
class IPAddressValidatorExtendedTest {

    private val validator = IPAddressValidator()

    // =========================================================================
    // SECTION 1 – Valid public IPv4 addresses
    // =========================================================================

    @Test
    fun `section1 - Google public DNS 8_8_8_8 is valid IPv4`() {
        val ip = "8.8.8.8"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - Google secondary DNS 8_8_4_4 is valid IPv4`() {
        val ip = "8.8.4.4"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - Cloudflare DNS 1_1_1_1 is valid IPv4`() {
        val ip = "1.1.1.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isLoopback(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - Cloudflare secondary DNS 1_0_0_1 is valid IPv4`() {
        val ip = "1.0.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - OpenDNS 208_67_222_222 is valid IPv4`() {
        val ip = "208.67.222.222"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - OpenDNS 208_67_220_220 is valid IPv4`() {
        val ip = "208.67.220.220"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isLoopback(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 9_9_9_9 is valid IPv4`() {
        val ip = "9.9.9.9"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 4_2_2_2 is valid IPv4`() {
        val ip = "4.2.2.2"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 200_1_2_3 is valid IPv4`() {
        val ip = "200.1.2.3"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isLoopback(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 123_45_67_89 is valid IPv4`() {
        val ip = "123.45.67.89"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 185_228_168_9 is valid IPv4`() {
        val ip = "185.228.168.9"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 76_76_2_0 is valid IPv4`() {
        val ip = "76.76.2.0"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 77_88_8_8 is valid IPv4`() {
        val ip = "77.88.8.8"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isLoopback(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 156_154_70_1 is valid IPv4`() {
        val ip = "156.154.70.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 198_41_0_4 is valid IPv4`() {
        val ip = "198.41.0.4"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 202_12_27_33 is valid IPv4`() {
        val ip = "202.12.27.33"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isLoopback(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 192_36_148_17 is valid IPv4`() {
        val ip = "192.36.148.17"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 216_58_213_142 is valid IPv4`() {
        val ip = "216.58.213.142"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 104_16_133_229 is valid IPv4`() {
        val ip = "104.16.133.229"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 51_159_15_122 is valid IPv4`() {
        val ip = "51.159.15.122"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isLoopback(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 45_90_28_0 is valid IPv4`() {
        val ip = "45.90.28.0"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 94_140_14_14 is valid IPv4`() {
        val ip = "94.140.14.14"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 114_114_114_114 is valid IPv4`() {
        val ip = "114.114.114.114"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 223_255_255_254 is valid IPv4`() {
        val ip = "223.255.255.254"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 2_3_4_5 is valid IPv4`() {
        val ip = "2.3.4.5"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    // =========================================================================
    // SECTION 2 – Private IPv4 ranges
    // =========================================================================

    @Test
    fun `section2 - 10_0_0_1 is in class A private range`() {
        val ip = "10.0.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_255_255_255 is in class A private range boundary`() {
        val ip = "10.255.255.255"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_10_10_10 is private`() {
        val ip = "10.10.10.10"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isLoopback(ip))
    }

    @Test
    fun `section2 - 10_0_0_0 is private network address`() {
        val ip = "10.0.0.0"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_192_168_1 is private even with 192 in third octet`() {
        val ip = "10.192.168.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_16_0_1 is in class B private range lower bound`() {
        val ip = "172.16.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_31_255_255 is in class B private range upper bound`() {
        val ip = "172.31.255.255"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_20_10_5 is in class B private range middle`() {
        val ip = "172.20.10.5"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isLoopback(ip))
    }

    @Test
    fun `section2 - 172_16_1_1 is private`() {
        val ip = "172.16.1.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_24_0_0 is private`() {
        val ip = "172.24.0.0"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_0_1 is in class C private range`() {
        val ip = "192.168.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_1_1 is typical home router private address`() {
        val ip = "192.168.1.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_255_254 is near upper bound of class C private`() {
        val ip = "192.168.255.254"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_100_200 is private`() {
        val ip = "192.168.100.200"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isLoopback(ip))
    }

    @Test
    fun `section2 - 192_168_0_0 is private network address`() {
        val ip = "192.168.0.0"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_167_1_1 is not private (just below class C boundary)`() {
        val ip = "192.167.1.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_169_1_1 is not private (just above class C boundary)`() {
        val ip = "192.169.1.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_15_255_255 is not private (just below class B boundary)`() {
        val ip = "172.15.255.255"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_32_0_0 is not private (just above class B boundary)`() {
        val ip = "172.32.0.0"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 11_0_0_1 is not private (just above class A boundary)`() {
        val ip = "11.0.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPrivate(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_128_0_1 is private in class A`() {
        val ip = "10.128.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_16_254_254 is private in class B`() {
        val ip = "172.16.254.254"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_10_1 is private in class C`() {
        val ip = "192.168.10.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_1_2_3 is private class A`() {
        val ip = "10.1.2.3"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isLoopback(ip))
    }

    @Test
    fun `section2 - 172_31_0_1 is private near upper boundary of class B`() {
        val ip = "172.31.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isPrivate(ip))
        assertFalse(validator.isPublic(ip))
    }

    // =========================================================================
    // SECTION 3 – Loopback and special addresses
    // =========================================================================

    @Test
    fun `section3 - 127_0_0_1 is loopback`() {
        val ip = "127.0.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isLoopback(ip))
        assertFalse(validator.isPublic(ip))
        assertFalse(validator.isPrivate(ip))
    }

    @Test
    fun `section3 - 127_255_255_255 is loopback upper boundary`() {
        val ip = "127.255.255.255"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isLoopback(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 127_1_2_3 is loopback mid-range`() {
        val ip = "127.1.2.3"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isLoopback(ip))
        assertFalse(validator.isPrivate(ip))
    }

    @Test
    fun `section3 - 127_0_0_0 is loopback network address`() {
        val ip = "127.0.0.0"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isLoopback(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 127_100_50_25 is loopback`() {
        val ip = "127.100.50.25"
        assertTrue(validator.isValidIPv4(ip))
        assertTrue(validator.isLoopback(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 126_255_255_255 is not loopback (just below boundary)`() {
        val ip = "126.255.255.255"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isLoopback(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 128_0_0_0 is not loopback (just above boundary)`() {
        val ip = "128.0.0.0"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isLoopback(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section3 - IPv6 loopback ::1 is loopback`() {
        val ip = "::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isLoopback(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - IPv6 loopback ::1 is valid`() {
        val ip = "::1"
        assertTrue(validator.isValid(ip))
        assertTrue(validator.isLoopback(ip))
        assertFalse(validator.isPrivate(ip))
    }

    @Test
    fun `section3 - 0_0_0_0 is valid special unspecified address`() {
        val ip = "0.0.0.0"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 255_255_255_255 is valid broadcast address`() {
        val ip = "255.255.255.255"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 169_254_0_1 is link-local special address`() {
        val ip = "169.254.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 169_254_169_254 is link-local AWS metadata`() {
        val ip = "169.254.169.254"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 169_254_255_255 is link-local upper boundary`() {
        val ip = "169.254.255.255"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 100_64_0_1 is in shared address space RFC 6598`() {
        val ip = "100.64.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 192_0_2_1 is TEST-NET documentation address`() {
        val ip = "192.0.2.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 198_51_100_1 is TEST-NET-2 documentation address`() {
        val ip = "198.51.100.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 203_0_113_1 is TEST-NET-3 documentation address`() {
        val ip = "203.0.113.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 240_0_0_1 is reserved future use`() {
        val ip = "240.0.0.1"
        assertTrue(validator.isValidIPv4(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section3 - 127_0_0_1 is not private even though it looks internal`() {
        val ip = "127.0.0.1"
        assertTrue(validator.isLoopback(ip))
        assertFalse(validator.isPrivate(ip))
    }

    // =========================================================================
    // SECTION 4 – Invalid IPv4 addresses
    // =========================================================================

    @Test
    fun `section4 - null input is invalid IPv4`() {
        assertFalse(validator.isValidIPv4(null))
        assertFalse(validator.isValid(null))
    }

    @Test
    fun `section4 - empty string is invalid IPv4`() {
        val ip = ""
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - single octet 256 is out of range`() {
        val ip = "256.1.1.1"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - second octet 256 is out of range`() {
        val ip = "1.256.1.1"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - third octet 256 is out of range`() {
        val ip = "1.1.256.1"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - fourth octet 256 is out of range`() {
        val ip = "1.1.1.256"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - negative octet is invalid`() {
        val ip = "-1.1.1.1"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - five octets is too many`() {
        val ip = "1.2.3.4.5"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - three octets is too few`() {
        val ip = "1.2.3"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - two octets is too few`() {
        val ip = "1.2"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - single number is not IPv4`() {
        val ip = "192"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - alphabetic characters are invalid`() {
        val ip = "abc.def.ghi.jkl"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - mixed alpha-numeric octet is invalid`() {
        val ip = "192.168.1.abc"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - IP with trailing dot is invalid`() {
        val ip = "192.168.1.1."
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with leading dot is invalid`() {
        val ip = ".192.168.1.1"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with double dots is invalid`() {
        val ip = "192..168.1.1"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with space is invalid`() {
        val ip = "192.168.1. 1"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with leading zero in octet is invalid`() {
        val ip = "192.168.01.1"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with all leading zeros is invalid`() {
        val ip = "00.00.00.00"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - very large octet value is invalid`() {
        val ip = "999.999.999.999"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - IPv6 address does not pass isValidIPv4`() {
        val ip = "2001:db8::1"
        assertFalse(validator.isValidIPv4(ip))
        assertTrue(validator.isValidIPv6(ip))
    }

    @Test
    fun `section4 - whitespace-only string is invalid`() {
        val ip = "   "
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - special characters are invalid`() {
        val ip = "192.168.1.@"
        assertFalse(validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP 300_0_0_1 is out of range`() {
        val ip = "300.0.0.1"
        assertFalse(validator.isValidIPv4(ip))
        assertFalse(validator.isValid(ip))
    }

    @Test
    fun `section4 - port number appended makes address invalid`() {
        val ip = "192.168.1.1:80"
        assertFalse(validator.isValidIPv4(ip))
    }

    // =========================================================================
    // SECTION 5 – IPv6 validation
    // =========================================================================

    @Test
    fun `section5 - full IPv6 address 2001:0db8:0000:0000:0000:0000:0000:0001 is valid`() {
        val ip = "2001:0db8:0000:0000:0000:0000:0000:0001"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
    }

    @Test
    fun `section5 - compressed IPv6 2001:db8::1 is valid`() {
        val ip = "2001:db8::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isLoopback(ip))
    }

    @Test
    fun `section5 - IPv6 loopback ::1 is valid`() {
        val ip = "::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertTrue(validator.isLoopback(ip))
    }

    @Test
    fun `section5 - IPv6 unspecified :: is valid`() {
        val ip = "::"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section5 - link-local IPv6 fe80::1 is valid`() {
        val ip = "fe80::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section5 - link-local IPv6 fe80::1%eth0 with zone ID is valid`() {
        val ip = "fe80::1%eth0"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
    }

    @Test
    fun `section5 - multicast IPv6 ff02::1 is valid`() {
        val ip = "ff02::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section5 - unique local IPv6 fc00::1 is valid`() {
        val ip = "fc00::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertTrue(validator.isPrivate(ip))
    }

    @Test
    fun `section5 - unique local IPv6 fd00::1 is valid and private`() {
        val ip = "fd00::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertTrue(validator.isPrivate(ip))
    }

    @Test
    fun `section5 - global unicast 2001:4860:4860::8888 is valid`() {
        val ip = "2001:4860:4860::8888"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section5 - full 8-group IPv6 is valid`() {
        val ip = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isLoopback(ip))
    }

    @Test
    fun `section5 - IPv6 with embedded IPv4 ::ffff:192_168_1_1 is valid`() {
        val ip = "::ffff:192.168.1.1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
    }

    @Test
    fun `section5 - IPv6 2606:4700:4700::1111 Cloudflare is valid`() {
        val ip = "2606:4700:4700::1111"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertTrue(validator.isPublic(ip))
    }

    @Test
    fun `section5 - invalid IPv6 with too many groups is invalid`() {
        val ip = "2001:db8:85a3:0:0:8a2e:370:7334:extra"
        assertFalse(validator.isValidIPv6(ip))
    }

    @Test
    fun `section5 - invalid IPv6 with non-hex characters is invalid`() {
        val ip = "2001:db8:gggg::1"
        assertFalse(validator.isValidIPv6(ip))
    }

    @Test
    fun `section5 - invalid IPv6 with double :: twice is invalid`() {
        val ip = "2001::db8::1"
        assertFalse(validator.isValidIPv6(ip))
    }

    @Test
    fun `section5 - IPv6 with group exceeding 4 hex digits is invalid`() {
        val ip = "2001:db8:12345::1"
        assertFalse(validator.isValidIPv6(ip))
    }

    @Test
    fun `section5 - null input is invalid IPv6`() {
        assertFalse(validator.isValidIPv6(null))
        assertFalse(validator.isValid(null))
    }

    @Test
    fun `section5 - empty string is invalid IPv6`() {
        assertFalse(validator.isValidIPv6(""))
        assertFalse(validator.isValid(""))
    }

    @Test
    fun `section5 - plain IPv4 address does not pass isValidIPv6`() {
        assertFalse(validator.isValidIPv6("8.8.8.8"))
        assertTrue(validator.isValidIPv4("8.8.8.8"))
    }

    @Test
    fun `section5 - IPv6 fe80::abcd:ef01:2345:6789 is valid link-local`() {
        val ip = "fe80::abcd:ef01:2345:6789"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
        assertFalse(validator.isPublic(ip))
    }

    @Test
    fun `section5 - uppercase IPv6 2001:DB8::1 is valid`() {
        val ip = "2001:DB8::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
    }

    @Test
    fun `section5 - mixed case IPv6 2001:Db8::1 is valid`() {
        val ip = "2001:Db8::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
    }

    @Test
    fun `section5 - 6to4 address 2002::1 is valid`() {
        val ip = "2002::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
    }

    @Test
    fun `section5 - teredo address 2001::1 is valid`() {
        val ip = "2001::1"
        assertTrue(validator.isValidIPv6(ip))
        assertTrue(validator.isValid(ip))
    }

    // =========================================================================
    // SECTION 6 – extractFromText function
    // =========================================================================

    @Test
    fun `section6 - single IPv4 extracted from plain text`() {
        val text = "Server is at 192.168.1.1 please connect"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("192.168.1.1"))
        assertEquals(1, result.size)
    }

    @Test
    fun `section6 - multiple IPv4 addresses extracted from text`() {
        val text = "Primary: 8.8.8.8 Secondary: 8.8.4.4"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("8.8.8.8"))
        assertTrue(result.contains("8.8.4.4"))
        assertEquals(2, result.size)
    }

    @Test
    fun `section6 - no IPs in text returns empty list`() {
        val text = "There are no IP addresses in this sentence."
        val result = validator.extractFromText(text)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `section6 - IPv4 embedded in URL is extracted`() {
        val text = "Visit http://8.8.8.8/dns for more info"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("8.8.8.8"))
        assertFalse(result.isEmpty())
    }

    @Test
    fun `section6 - IPv4 with port does not extract port as IP`() {
        val text = "Connect to 10.0.0.1:8080 now"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("10.0.0.1"))
        assertFalse(result.any { it.contains(":") && it.contains(".") })
    }

    @Test
    fun `section6 - IPv4 at start of text is extracted`() {
        val text = "192.168.0.1 is the gateway"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("192.168.0.1"))
        assertEquals(1, result.size)
    }

    @Test
    fun `section6 - IPv4 at end of text is extracted`() {
        val text = "The gateway is 192.168.0.1"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("192.168.0.1"))
        assertEquals(1, result.size)
    }

    @Test
    fun `section6 - three IPv4 addresses extracted from log line`() {
        val text = "Route from 1.1.1.1 through 10.0.0.1 to 192.168.1.100"
        val result = validator.extractFromText(text)
        assertEquals(3, result.size)
        assertTrue(result.contains("1.1.1.1"))
        assertTrue(result.contains("10.0.0.1"))
        assertTrue(result.contains("192.168.1.100"))
    }

    @Test
    fun `section6 - IPv6 loopback extracted from text`() {
        val text = "Listening on ::1 port 8080"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("::1"))
    }

    @Test
    fun `section6 - IPv6 address extracted from text`() {
        val text = "Connect to 2001:db8::1 for service"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("2001:db8::1"))
    }

    @Test
    fun `section6 - text with no valid IP octets returns empty`() {
        val text = "Version 3.14.159 and build 1.2"
        val result = validator.extractFromText(text)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `section6 - duplicate IPs in text are deduplicated`() {
        val text = "Primary 8.8.8.8 and backup 8.8.8.8"
        val result = validator.extractFromText(text)
        assertEquals(1, result.filter { it == "8.8.8.8" }.size)
    }

    @Test
    fun `section6 - invalid number like 999_999_999_999 is not extracted`() {
        val text = "Error code 999.999.999.999 occurred"
        val result = validator.extractFromText(text)
        assertFalse(result.contains("999.999.999.999"))
    }

    @Test
    fun `section6 - loopback 127_0_0_1 is extracted from text`() {
        val text = "Binding to localhost 127.0.0.1"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("127.0.0.1"))
    }

    @Test
    fun `section6 - newline separated IPs are all extracted`() {
        val text = "10.0.0.1\n10.0.0.2\n10.0.0.3"
        val result = validator.extractFromText(text)
        assertEquals(3, result.size)
        assertTrue(result.containsAll(listOf("10.0.0.1", "10.0.0.2", "10.0.0.3")))
    }

    @Test
    fun `section6 - IP in brackets is extracted`() {
        val text = "Server [192.168.10.1] responded"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("192.168.10.1"))
    }

    @Test
    fun `section6 - IP in parentheses is extracted`() {
        val text = "Redirect (203.0.113.5) ignored"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("203.0.113.5"))
    }

    @Test
    fun `section6 - comma separated IPs are extracted`() {
        val text = "Hosts: 8.8.8.8,8.8.4.4,1.1.1.1"
        val result = validator.extractFromText(text)
        assertTrue(result.containsAll(listOf("8.8.8.8", "8.8.4.4", "1.1.1.1")))
        assertEquals(3, result.size)
    }

    @Test
    fun `section6 - mixed IPv4 and IPv6 both extracted`() {
        val text = "IPv4: 8.8.8.8 and IPv6: 2001:db8::1"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("8.8.8.8"))
        assertTrue(result.contains("2001:db8::1"))
    }

    @Test
    fun `section6 - empty text returns empty list`() {
        val result = validator.extractFromText("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `section6 - text with only punctuation returns empty list`() {
        val result = validator.extractFromText("..., ;;; !!!")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `section6 - very long text with IPs extracts all`() {
        val text = "a".repeat(1000) + " 10.0.0.1 " + "b".repeat(1000) + " 8.8.8.8"
        val result = validator.extractFromText(text)
        assertTrue(result.contains("10.0.0.1"))
        assertTrue(result.contains("8.8.8.8"))
        assertEquals(2, result.size)
    }

    @Test
    fun `section6 - extract returns list not null`() {
        val result = validator.extractFromText("no ip here")
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `section6 - IP inside JSON-like text is extracted`() {
        val text = """{"host":"10.20.30.40","port":8080}"""
        val result = validator.extractFromText(text)
        assertTrue(result.contains("10.20.30.40"))
    }

    @Test
    fun `section6 - multiple same and different IPs count correctly`() {
        val text = "1.2.3.4 and 1.2.3.4 and 5.6.7.8"
        val result = validator.extractFromText(text)
        val unique = result.toSet()
        assertTrue(unique.contains("1.2.3.4"))
        assertTrue(unique.contains("5.6.7.8"))
    }

    // =========================================================================
    // SECTION 7 – isPrivate / isLoopback / isPublic classification
    // =========================================================================

    @Test
    fun `section7 - isPrivate returns true for 10_x range`() {
        assertTrue(validator.isPrivate("10.0.0.1"))
        assertTrue(validator.isPrivate("10.100.200.1"))
        assertFalse(validator.isPrivate("11.0.0.1"))
    }

    @Test
    fun `section7 - isPrivate returns true for 172_16_x range`() {
        assertTrue(validator.isPrivate("172.16.0.1"))
        assertTrue(validator.isPrivate("172.31.255.254"))
        assertFalse(validator.isPrivate("172.15.255.255"))
    }

    @Test
    fun `section7 - isPrivate returns true for 192_168_x range`() {
        assertTrue(validator.isPrivate("192.168.1.1"))
        assertTrue(validator.isPrivate("192.168.0.0"))
        assertFalse(validator.isPrivate("192.167.1.1"))
    }

    @Test
    fun `section7 - isPrivate returns false for public address`() {
        assertFalse(validator.isPrivate("8.8.8.8"))
        assertFalse(validator.isPrivate("1.1.1.1"))
        assertFalse(validator.isPrivate("216.58.213.142"))
    }

    @Test
    fun `section7 - isPrivate returns false for loopback`() {
        assertFalse(validator.isPrivate("127.0.0.1"))
        assertFalse(validator.isPrivate("127.255.255.255"))
    }

    @Test
    fun `section7 - isLoopback returns true for 127_x_x_x`() {
        assertTrue(validator.isLoopback("127.0.0.1"))
        assertTrue(validator.isLoopback("127.0.0.2"))
        assertTrue(validator.isLoopback("127.255.255.254"))
    }

    @Test
    fun `section7 - isLoopback returns true for IPv6 ::1`() {
        assertTrue(validator.isLoopback("::1"))
        assertFalse(validator.isLoopback("::2"))
    }

    @Test
    fun `section7 - isLoopback returns false for private addresses`() {
        assertFalse(validator.isLoopback("10.0.0.1"))
        assertFalse(validator.isLoopback("192.168.1.1"))
        assertFalse(validator.isLoopback("172.16.0.1"))
    }

    @Test
    fun `section7 - isLoopback returns false for public addresses`() {
        assertFalse(validator.isLoopback("8.8.8.8"))
        assertFalse(validator.isLoopback("1.1.1.1"))
    }

    @Test
    fun `section7 - isPublic returns true for well-known public addresses`() {
        assertTrue(validator.isPublic("8.8.8.8"))
        assertTrue(validator.isPublic("1.1.1.1"))
        assertTrue(validator.isPublic("208.67.222.222"))
    }

    @Test
    fun `section7 - isPublic returns false for private addresses`() {
        assertFalse(validator.isPublic("10.0.0.1"))
        assertFalse(validator.isPublic("192.168.1.1"))
        assertFalse(validator.isPublic("172.16.0.1"))
    }

    @Test
    fun `section7 - isPublic returns false for loopback addresses`() {
        assertFalse(validator.isPublic("127.0.0.1"))
        assertFalse(validator.isPublic("::1"))
    }

    @Test
    fun `section7 - isPublic returns false for link-local`() {
        assertFalse(validator.isPublic("169.254.1.1"))
        assertFalse(validator.isPublic("fe80::1"))
    }

    @Test
    fun `section7 - isPublic returns false for multicast`() {
        assertFalse(validator.isPublic("224.0.0.1"))
        assertFalse(validator.isPublic("ff02::1"))
    }

    @Test
    fun `section7 - isPublic returns false for broadcast`() {
        assertFalse(validator.isPublic("255.255.255.255"))
    }

    @Test
    fun `section7 - exactly one of isPrivate isLoopback isPublic is true for private ip`() {
        val ip = "192.168.1.50"
        val flags = listOf(validator.isPrivate(ip), validator.isLoopback(ip), validator.isPublic(ip))
        assertEquals(1, flags.count { it })
    }

    @Test
    fun `section7 - exactly one of isPrivate isLoopback isPublic is true for public ip`() {
        val ip = "8.8.8.8"
        val flags = listOf(validator.isPrivate(ip), validator.isLoopback(ip), validator.isPublic(ip))
        assertEquals(1, flags.count { it })
    }

    @Test
    fun `section7 - exactly one of isPrivate isLoopback isPublic is true for loopback`() {
        val ip = "127.0.0.1"
        val flags = listOf(validator.isPrivate(ip), validator.isLoopback(ip), validator.isPublic(ip))
        assertEquals(1, flags.count { it })
    }

    @Test
    fun `section7 - isPrivate for IPv6 unique local fc range`() {
        assertTrue(validator.isPrivate("fc00::1"))
        assertTrue(validator.isPrivate("fdff:ffff:ffff::1"))
    }

    @Test
    fun `section7 - isPrivate for IPv6 fd range`() {
        assertTrue(validator.isPrivate("fd00::1"))
        assertTrue(validator.isPrivate("fd12:3456:789a::1"))
    }

    @Test
    fun `section7 - isPublic for IPv6 global unicast`() {
        assertTrue(validator.isPublic("2001:4860:4860::8888"))
        assertTrue(validator.isPublic("2606:4700:4700::1111"))
    }

    @Test
    fun `section7 - isPrivate returns false for link-local even though non-routable`() {
        assertFalse(validator.isPrivate("169.254.1.1"))
        assertFalse(validator.isPrivate("fe80::1"))
    }

    @Test
    fun `section7 - isLoopback false for unspecified addresses`() {
        assertFalse(validator.isLoopback("0.0.0.0"))
        assertFalse(validator.isLoopback("::"))
    }

    @Test
    fun `section7 - isPublic false for shared address space 100_64_0_0`() {
        assertFalse(validator.isPublic("100.64.0.1"))
        assertFalse(validator.isPublic("100.127.255.255"))
    }

    @Test
    fun `section7 - isLoopback false for 128_0_0_1`() {
        assertFalse(validator.isLoopback("128.0.0.1"))
        assertTrue(validator.isPublic("128.0.0.1"))
    }

    // =========================================================================
    // SECTION 8 – CIDR / subnet membership
    // =========================================================================

    @Test
    fun `section8 - 192_168_1_5 is in 192_168_1_0_slash24`() {
        assertTrue(validator.isInRange("192.168.1.5", "192.168.1.0/24"))
    }

    @Test
    fun `section8 - 192_168_1_255 is in 192_168_1_0_slash24 broadcast`() {
        assertTrue(validator.isInRange("192.168.1.255", "192.168.1.0/24"))
    }

    @Test
    fun `section8 - 192_168_1_0 is in 192_168_1_0_slash24 network address`() {
        assertTrue(validator.isInRange("192.168.1.0", "192.168.1.0/24"))
    }

    @Test
    fun `section8 - 192_168_2_1 is not in 192_168_1_0_slash24`() {
        assertFalse(validator.isInRange("192.168.2.1", "192.168.1.0/24"))
    }

    @Test
    fun `section8 - 10_0_0_1 is in 10_0_0_0_slash8`() {
        assertTrue(validator.isInRange("10.0.0.1", "10.0.0.0/8"))
    }

    @Test
    fun `section8 - 10_255_255_255 is in 10_0_0_0_slash8`() {
        assertTrue(validator.isInRange("10.255.255.255", "10.0.0.0/8"))
    }

    @Test
    fun `section8 - 11_0_0_1 is not in 10_0_0_0_slash8`() {
        assertFalse(validator.isInRange("11.0.0.1", "10.0.0.0/8"))
    }

    @Test
    fun `section8 - 172_16_0_1 is in 172_16_0_0_slash12`() {
        assertTrue(validator.isInRange("172.16.0.1", "172.16.0.0/12"))
    }

    @Test
    fun `section8 - 172_31_255_255 is in 172_16_0_0_slash12`() {
        assertTrue(validator.isInRange("172.31.255.255", "172.16.0.0/12"))
    }

    @Test
    fun `section8 - 172_32_0_0 is not in 172_16_0_0_slash12`() {
        assertFalse(validator.isInRange("172.32.0.0", "172.16.0.0/12"))
    }

    @Test
    fun `section8 - 8_8_8_8 is in 8_8_8_0_slash24`() {
        assertTrue(validator.isInRange("8.8.8.8", "8.8.8.0/24"))
    }

    @Test
    fun `section8 - 8_8_9_8 is not in 8_8_8_0_slash24`() {
        assertFalse(validator.isInRange("8.8.9.8", "8.8.8.0/24"))
    }

    @Test
    fun `section8 - exact host 8_8_8_8 in slash32`() {
        assertTrue(validator.isInRange("8.8.8.8", "8.8.8.8/32"))
    }

    @Test
    fun `section8 - different host 8_8_8_9 not in 8_8_8_8_slash32`() {
        assertFalse(validator.isInRange("8.8.8.9", "8.8.8.8/32"))
    }

    @Test
    fun `section8 - all addresses in 0_0_0_0_slash0`() {
        assertTrue(validator.isInRange("8.8.8.8", "0.0.0.0/0"))
        assertTrue(validator.isInRange("192.168.1.1", "0.0.0.0/0"))
        assertTrue(validator.isInRange("10.0.0.1", "0.0.0.0/0"))
    }

    @Test
    fun `section8 - slash16 subnet covers correct range`() {
        assertTrue(validator.isInRange("192.168.0.1", "192.168.0.0/16"))
        assertTrue(validator.isInRange("192.168.255.255", "192.168.0.0/16"))
        assertFalse(validator.isInRange("192.169.0.1", "192.168.0.0/16"))
    }

    @Test
    fun `section8 - slash30 covers exactly 4 addresses`() {
        val cidr = "192.168.1.0/30"
        assertTrue(validator.isInRange("192.168.1.0", cidr))
        assertTrue(validator.isInRange("192.168.1.1", cidr))
        assertTrue(validator.isInRange("192.168.1.2", cidr))
        assertTrue(validator.isInRange("192.168.1.3", cidr))
    }

    @Test
    fun `section8 - slash30 excludes address just outside`() {
        assertFalse(validator.isInRange("192.168.1.4", "192.168.1.0/30"))
        assertFalse(validator.isInRange("192.168.0.255", "192.168.1.0/30"))
    }

    @Test
    fun `section8 - slash31 point-to-point link covers two addresses`() {
        val cidr = "10.0.0.0/31"
        assertTrue(validator.isInRange("10.0.0.0", cidr))
        assertTrue(validator.isInRange("10.0.0.1", cidr))
        assertFalse(validator.isInRange("10.0.0.2", cidr))
    }

    @Test
    fun `section8 - loopback 127_0_0_1 in 127_0_0_0_slash8`() {
        assertTrue(validator.isInRange("127.0.0.1", "127.0.0.0/8"))
        assertTrue(validator.isInRange("127.255.255.255", "127.0.0.0/8"))
        assertFalse(validator.isInRange("128.0.0.1", "127.0.0.0/8"))
    }

    @Test
    fun `section8 - 192_0_2_0_slash24 documentation range`() {
        assertTrue(validator.isInRange("192.0.2.1", "192.0.2.0/24"))
        assertTrue(validator.isInRange("192.0.2.254", "192.0.2.0/24"))
        assertFalse(validator.isInRange("192.0.3.1", "192.0.2.0/24"))
    }

    @Test
    fun `section8 - link-local 169_254_0_0_slash16 range`() {
        assertTrue(validator.isInRange("169.254.0.1", "169.254.0.0/16"))
        assertTrue(validator.isInRange("169.254.169.254", "169.254.0.0/16"))
        assertFalse(validator.isInRange("169.255.0.1", "169.254.0.0/16"))
    }

    @Test
    fun `section8 - slash20 subnet covers 4096 addresses`() {
        val cidr = "10.0.0.0/20"
        assertTrue(validator.isInRange("10.0.0.0", cidr))
        assertTrue(validator.isInRange("10.0.15.255", cidr))
        assertFalse(validator.isInRange("10.0.16.0", cidr))
    }

    @Test
    fun `section8 - slash25 splits class C into two halves lower`() {
        val cidr = "192.168.1.0/25"
        assertTrue(validator.isInRange("192.168.1.0", cidr))
        assertTrue(validator.isInRange("192.168.1.127", cidr))
        assertFalse(validator.isInRange("192.168.1.128", cidr))
    }

    @Test
    fun `section8 - slash25 splits class C into two halves upper`() {
        val cidr = "192.168.1.128/25"
        assertTrue(validator.isInRange("192.168.1.128", cidr))
        assertTrue(validator.isInRange("192.168.1.255", cidr))
        assertFalse(validator.isInRange("192.168.1.127", cidr))
    }

    @Test
    fun `section8 - Google DNS 8_8_8_8 in broad slash8`() {
        assertTrue(validator.isInRange("8.8.8.8", "8.0.0.0/8"))
        assertFalse(validator.isInRange("9.9.9.9", "8.0.0.0/8"))
    }

    @Test
    fun `section8 - 100_64_0_0_slash10 shared address space`() {
        val cidr = "100.64.0.0/10"
        assertTrue(validator.isInRange("100.64.0.1", cidr))
        assertTrue(validator.isInRange("100.127.255.255", cidr))
        assertFalse(validator.isInRange("100.128.0.0", cidr))
    }

    @Test
    fun `section8 - 240_0_0_0_slash4 reserved range`() {
        val cidr = "240.0.0.0/4"
        assertTrue(validator.isInRange("240.0.0.1", cidr))
        assertTrue(validator.isInRange("255.255.255.254", cidr))
        assertFalse(validator.isInRange("239.255.255.255", cidr))
    }

    @Test
    fun `section8 - slash23 supernet covers two class C blocks`() {
        val cidr = "192.168.0.0/23"
        assertTrue(validator.isInRange("192.168.0.1", cidr))
        assertTrue(validator.isInRange("192.168.1.254", cidr))
        assertFalse(validator.isInRange("192.168.2.1", cidr))
    }

    @Test
    fun `section8 - invalid CIDR notation returns false gracefully`() {
        assertFalse(validator.isInRange("192.168.1.1", "192.168.1.0/33"))
    }

    @Test
    fun `section8 - invalid IP with valid CIDR returns false`() {
        assertFalse(validator.isInRange("999.999.999.999", "192.168.1.0/24"))
    }

    // =========================================================================
    // SECTION 9 – Edge cases
    // =========================================================================

    @Test
    fun `section9 - null string to isValid returns false`() {
        assertFalse(validator.isValid(null))
        assertFalse(validator.isValidIPv4(null))
        assertFalse(validator.isValidIPv6(null))
    }

    @Test
    fun `section9 - blank string with spaces is invalid`() {
        assertFalse(validator.isValid("    "))
        assertFalse(validator.isValidIPv4("    "))
        assertFalse(validator.isValidIPv6("    "))
    }

    @Test
    fun `section9 - IP with surrounding whitespace is invalid`() {
        assertFalse(validator.isValidIPv4(" 8.8.8.8 "))
    }

    @Test
    fun `section9 - tab character in IP is invalid`() {
        assertFalse(validator.isValidIPv4("8.8.8.\t8"))
    }

    @Test
    fun `section9 - newline in IP string is invalid`() {
        assertFalse(validator.isValidIPv4("8.8.8\n.8"))
    }

    @Test
    fun `section9 - very long string is not a valid IP`() {
        val longString = "a".repeat(10000)
        assertFalse(validator.isValid(longString))
        assertFalse(validator.isValidIPv4(longString))
        assertFalse(validator.isValidIPv6(longString))
    }

    @Test
    fun `section9 - IP address with unicode lookalike characters is invalid`() {
        // Using unicode dot-like character (middle dot) instead of period
        assertFalse(validator.isValidIPv4("192\u00b7168\u00b71\u00b71"))
    }

    @Test
    fun `section9 - extractFromText on null-like empty returns empty list`() {
        val result = validator.extractFromText("")
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `section9 - 0_0_0_0 is valid IPv4 unspecified address`() {
        assertTrue(validator.isValidIPv4("0.0.0.0"))
        assertFalse(validator.isPublic("0.0.0.0"))
    }

    @Test
    fun `section9 - 255_255_255_255 is valid IPv4 broadcast`() {
        assertTrue(validator.isValidIPv4("255.255.255.255"))
        assertFalse(validator.isPublic("255.255.255.255"))
    }

    @Test
    fun `section9 - all zeros octet 0 is valid in each position`() {
        assertTrue(validator.isValidIPv4("0.1.2.3"))
        assertTrue(validator.isValidIPv4("1.0.2.3"))
        assertTrue(validator.isValidIPv4("1.2.0.3"))
        assertTrue(validator.isValidIPv4("1.2.3.0"))
    }

    @Test
    fun `section9 - all max octet 255 is valid IPv4`() {
        assertTrue(validator.isValidIPv4("255.255.255.255"))
        assertTrue(validator.isValidIPv4("255.0.0.0"))
        assertTrue(validator.isValidIPv4("0.255.0.0"))
    }

    @Test
    fun `section9 - isInRange with empty CIDR returns false`() {
        assertFalse(validator.isInRange("192.168.1.1", ""))
    }

    @Test
    fun `section9 - isInRange with empty IP returns false`() {
        assertFalse(validator.isInRange("", "192.168.1.0/24"))
    }

    @Test
    fun `section9 - extractFromText handles JSON with mixed content`() {
        val text = """{"ip":"10.0.0.1","name":"server","version":"2.1.0"}"""
        val result = validator.extractFromText(text)
        assertTrue(result.contains("10.0.0.1"))
        // version number 2.1.0 should not be extracted as IP
        assertFalse(result.contains("2.1.0"))
    }

    @Test
    fun `section9 - extractFromText returns immutable or independent list`() {
        val text = "Server at 10.0.0.1"
        val result1 = validator.extractFromText(text)
        val result2 = validator.extractFromText(text)
        assertEquals(result1, result2)
    }

    @Test
    fun `section9 - isValid accepts both IPv4 and IPv6`() {
        assertTrue(validator.isValid("8.8.8.8"))
        assertTrue(validator.isValid("::1"))
        assertTrue(validator.isValid("2001:db8::1"))
        assertTrue(validator.isValid("192.168.1.1"))
    }

    @Test
    fun `section9 - isValid rejects clearly invalid inputs`() {
        assertFalse(validator.isValid("not-an-ip"))
        assertFalse(validator.isValid("hostname.local"))
        assertFalse(validator.isValid("256.1.1.1"))
        assertFalse(validator.isValid(""))
    }

    @Test
    fun `section9 - isPublic and isPrivate are mutually exclusive for all private ranges`() {
        val privateIPs = listOf("10.0.0.1", "172.16.0.1", "192.168.1.1")
        for (ip in privateIPs) {
            assertTrue("Expected $ip to be private", validator.isPrivate(ip))
            assertFalse("Expected $ip to not be public", validator.isPublic(ip))
        }
    }
}
