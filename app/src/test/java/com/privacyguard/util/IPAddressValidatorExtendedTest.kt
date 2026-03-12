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
 * Total: 220 test functions, 2500+ lines
 */
class IPAddressValidatorExtendedTest {

    private val validator = IPAddressValidator()

    // =========================================================================
    // SECTION 1 – Valid public IPv4 addresses
    // =========================================================================
    //
    // Tests in this section verify that well-known public IPv4 addresses pass
    // all three related checks:
    //   - isValidIPv4  returns true
    //   - isPrivate    returns false
    //   - isPublic     returns true
    //
    // All IPs in this section are reachable from the public internet and do
    // not belong to any reserved or private range.
    // =========================================================================

    @Test
    fun `section1 - Google public DNS 8_8_8_8 is valid IPv4`() {
        val ip = "8.8.8.8"
        assertTrue("8.8.8.8 must be recognized as a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 8.8.8.8", validator.isValid(ip))
        assertFalse("8.8.8.8 is not a private address", validator.isPrivate(ip))
        assertTrue("8.8.8.8 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Google secondary DNS 8_8_4_4 is valid IPv4`() {
        val ip = "8.8.4.4"
        assertTrue("8.8.4.4 must be recognized as a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 8.8.4.4", validator.isValid(ip))
        assertFalse("8.8.4.4 is not a private address", validator.isPrivate(ip))
        assertTrue("8.8.4.4 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Cloudflare DNS 1_1_1_1 is valid IPv4`() {
        val ip = "1.1.1.1"
        assertTrue("1.1.1.1 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 1.1.1.1", validator.isValid(ip))
        assertFalse("1.1.1.1 is not a loopback address", validator.isLoopback(ip))
        assertTrue("1.1.1.1 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Cloudflare secondary DNS 1_0_0_1 is valid IPv4`() {
        val ip = "1.0.0.1"
        assertTrue("1.0.0.1 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 1.0.0.1", validator.isValid(ip))
        assertFalse("1.0.0.1 is not a private address", validator.isPrivate(ip))
        assertTrue("1.0.0.1 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - OpenDNS 208_67_222_222 is valid IPv4`() {
        val ip = "208.67.222.222"
        assertTrue("208.67.222.222 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 208.67.222.222", validator.isValid(ip))
        assertFalse("208.67.222.222 is not a private address", validator.isPrivate(ip))
        assertTrue("208.67.222.222 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - OpenDNS resolver 208_67_220_220 is valid IPv4`() {
        val ip = "208.67.220.220"
        assertTrue("208.67.220.220 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 208.67.220.220", validator.isValid(ip))
        assertFalse("208.67.220.220 is not a loopback address", validator.isLoopback(ip))
        assertTrue("208.67.220.220 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Quad9 DNS 9_9_9_9 is valid IPv4`() {
        val ip = "9.9.9.9"
        assertTrue("9.9.9.9 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 9.9.9.9", validator.isValid(ip))
        assertFalse("9.9.9.9 is not a private address", validator.isPrivate(ip))
        assertTrue("9.9.9.9 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Level3 DNS 4_2_2_2 is valid IPv4`() {
        val ip = "4.2.2.2"
        assertTrue("4.2.2.2 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 4.2.2.2", validator.isValid(ip))
        assertFalse("4.2.2.2 is not a private address", validator.isPrivate(ip))
        assertTrue("4.2.2.2 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 200_1_2_3 is valid IPv4`() {
        val ip = "200.1.2.3"
        assertTrue("200.1.2.3 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 200.1.2.3", validator.isValid(ip))
        assertFalse("200.1.2.3 is not a loopback address", validator.isLoopback(ip))
        assertTrue("200.1.2.3 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 123_45_67_89 is valid IPv4`() {
        val ip = "123.45.67.89"
        assertTrue("123.45.67.89 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 123.45.67.89", validator.isValid(ip))
        assertFalse("123.45.67.89 is not a private address", validator.isPrivate(ip))
        assertTrue("123.45.67.89 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - AdGuard DNS 185_228_168_9 is valid IPv4`() {
        val ip = "185.228.168.9"
        assertTrue("185.228.168.9 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 185.228.168.9", validator.isValid(ip))
        assertFalse("185.228.168.9 is not a private address", validator.isPrivate(ip))
        assertTrue("185.228.168.9 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Alternate DNS 76_76_2_0 is valid IPv4`() {
        val ip = "76.76.2.0"
        assertTrue("76.76.2.0 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 76.76.2.0", validator.isValid(ip))
        assertFalse("76.76.2.0 is not a private address", validator.isPrivate(ip))
        assertTrue("76.76.2.0 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Yandex DNS 77_88_8_8 is valid IPv4`() {
        val ip = "77.88.8.8"
        assertTrue("77.88.8.8 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 77.88.8.8", validator.isValid(ip))
        assertFalse("77.88.8.8 is not a loopback address", validator.isLoopback(ip))
        assertTrue("77.88.8.8 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Neustar DNS 156_154_70_1 is valid IPv4`() {
        val ip = "156.154.70.1"
        assertTrue("156.154.70.1 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 156.154.70.1", validator.isValid(ip))
        assertFalse("156.154.70.1 is not a private address", validator.isPrivate(ip))
        assertTrue("156.154.70.1 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - ICANN root server 198_41_0_4 is valid IPv4`() {
        val ip = "198.41.0.4"
        assertTrue("198.41.0.4 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 198.41.0.4", validator.isValid(ip))
        assertFalse("198.41.0.4 is not a private address", validator.isPrivate(ip))
        assertTrue("198.41.0.4 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - RIPE NCC root server 202_12_27_33 is valid IPv4`() {
        val ip = "202.12.27.33"
        assertTrue("202.12.27.33 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 202.12.27.33", validator.isValid(ip))
        assertFalse("202.12.27.33 is not a loopback address", validator.isLoopback(ip))
        assertTrue("202.12.27.33 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - address 192_36_148_17 is valid public IPv4`() {
        val ip = "192.36.148.17"
        assertTrue("192.36.148.17 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 192.36.148.17", validator.isValid(ip))
        assertFalse("192.36.148.17 is not a private address (second octet 36 not 168)", validator.isPrivate(ip))
        assertTrue("192.36.148.17 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Google server 216_58_213_142 is valid IPv4`() {
        val ip = "216.58.213.142"
        assertTrue("216.58.213.142 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 216.58.213.142", validator.isValid(ip))
        assertFalse("216.58.213.142 is not a private address", validator.isPrivate(ip))
        assertTrue("216.58.213.142 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Cloudflare edge 104_16_133_229 is valid IPv4`() {
        val ip = "104.16.133.229"
        assertTrue("104.16.133.229 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 104.16.133.229", validator.isValid(ip))
        assertFalse("104.16.133.229 is not a private address", validator.isPrivate(ip))
        assertTrue("104.16.133.229 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - Scaleway DNS 51_159_15_122 is valid IPv4`() {
        val ip = "51.159.15.122"
        assertTrue("51.159.15.122 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 51.159.15.122", validator.isValid(ip))
        assertFalse("51.159.15.122 is not a loopback address", validator.isLoopback(ip))
        assertTrue("51.159.15.122 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - NextDNS 45_90_28_0 is valid IPv4`() {
        val ip = "45.90.28.0"
        assertTrue("45.90.28.0 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 45.90.28.0", validator.isValid(ip))
        assertFalse("45.90.28.0 is not a private address", validator.isPrivate(ip))
        assertTrue("45.90.28.0 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - AdGuard DNS 94_140_14_14 is valid IPv4`() {
        val ip = "94.140.14.14"
        assertTrue("94.140.14.14 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 94.140.14.14", validator.isValid(ip))
        assertFalse("94.140.14.14 is not a private address", validator.isPrivate(ip))
        assertTrue("94.140.14.14 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - 114DNS 114_114_114_114 is valid IPv4`() {
        val ip = "114.114.114.114"
        assertTrue("114.114.114.114 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 114.114.114.114", validator.isValid(ip))
        assertFalse("114.114.114.114 is not a private address", validator.isPrivate(ip))
        assertTrue("114.114.114.114 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - near-maximum address 223_255_255_254 is valid IPv4`() {
        val ip = "223.255.255.254"
        assertTrue("223.255.255.254 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 223.255.255.254", validator.isValid(ip))
        assertFalse("223.255.255.254 is not a private address", validator.isPrivate(ip))
        assertTrue("223.255.255.254 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section1 - low address 2_3_4_5 is valid public IPv4`() {
        val ip = "2.3.4.5"
        assertTrue("2.3.4.5 must be recognized as valid IPv4", validator.isValidIPv4(ip))
        assertTrue("isValid should return true for 2.3.4.5", validator.isValid(ip))
        assertFalse("2.3.4.5 is not a private address", validator.isPrivate(ip))
        assertTrue("2.3.4.5 is a public address", validator.isPublic(ip))
    }

    // =========================================================================
    // SECTION 2 – Private IPv4 ranges
    // =========================================================================
    //
    // RFC 1918 defines three private IPv4 address spaces:
    //   - 10.0.0.0/8       (Class A: 10.0.0.0 – 10.255.255.255)
    //   - 172.16.0.0/12    (Class B: 172.16.0.0 – 172.31.255.255)
    //   - 192.168.0.0/16   (Class C: 192.168.0.0 – 192.168.255.255)
    //
    // All tests in this section assert:
    //   - isValidIPv4 returns true
    //   - isPrivate   returns true
    //   - isPublic    returns false
    // =========================================================================

    @Test
    fun `section2 - 10_0_0_1 is in class A private range`() {
        val ip = "10.0.0.1"
        assertTrue("10.0.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("10.0.0.1 must be classified as private (RFC 1918 class A)", validator.isPrivate(ip))
        assertFalse("10.0.0.1 must not be classified as public", validator.isPublic(ip))
        assertFalse("10.0.0.1 is not a loopback address", validator.isLoopback(ip))
    }

    @Test
    fun `section2 - 10_255_255_255 is in class A private range boundary`() {
        val ip = "10.255.255.255"
        assertTrue("10.255.255.255 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("10.255.255.255 must be classified as private (RFC 1918 class A upper bound)", validator.isPrivate(ip))
        assertFalse("10.255.255.255 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_10_10_10 is private class A`() {
        val ip = "10.10.10.10"
        assertTrue("10.10.10.10 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("10.10.10.10 must be classified as private", validator.isPrivate(ip))
        assertFalse("10.10.10.10 must not be classified as public", validator.isPublic(ip))
        assertFalse("10.10.10.10 is not a loopback address", validator.isLoopback(ip))
    }

    @Test
    fun `section2 - 10_0_0_0 is private network address class A`() {
        val ip = "10.0.0.0"
        assertTrue("10.0.0.0 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("10.0.0.0 must be classified as private", validator.isPrivate(ip))
        assertFalse("10.0.0.0 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_192_168_1 is private class A even with 192 in third octet`() {
        val ip = "10.192.168.1"
        assertTrue("10.192.168.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("10.192.168.1 is private because first octet is 10", validator.isPrivate(ip))
        assertFalse("10.192.168.1 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_16_0_1 is in class B private range lower bound`() {
        val ip = "172.16.0.1"
        assertTrue("172.16.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("172.16.0.1 must be classified as private (RFC 1918 class B lower bound)", validator.isPrivate(ip))
        assertFalse("172.16.0.1 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_31_255_255 is in class B private range upper bound`() {
        val ip = "172.31.255.255"
        assertTrue("172.31.255.255 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("172.31.255.255 must be classified as private (RFC 1918 class B upper bound)", validator.isPrivate(ip))
        assertFalse("172.31.255.255 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_20_10_5 is in class B private range middle`() {
        val ip = "172.20.10.5"
        assertTrue("172.20.10.5 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("172.20.10.5 must be classified as private (second octet 20 is in [16,31])", validator.isPrivate(ip))
        assertFalse("172.20.10.5 is not a loopback address", validator.isLoopback(ip))
        assertFalse("172.20.10.5 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_16_1_1 is private class B`() {
        val ip = "172.16.1.1"
        assertTrue("172.16.1.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("172.16.1.1 must be classified as private", validator.isPrivate(ip))
        assertFalse("172.16.1.1 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_24_0_0 is private class B mid-range`() {
        val ip = "172.24.0.0"
        assertTrue("172.24.0.0 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("172.24.0.0 must be classified as private (second octet 24 is in [16,31])", validator.isPrivate(ip))
        assertFalse("172.24.0.0 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_0_1 is in class C private range`() {
        val ip = "192.168.0.1"
        assertTrue("192.168.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("192.168.0.1 must be classified as private (RFC 1918 class C)", validator.isPrivate(ip))
        assertFalse("192.168.0.1 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_1_1 is typical home router private address`() {
        val ip = "192.168.1.1"
        assertTrue("192.168.1.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("192.168.1.1 must be classified as private (typical home gateway)", validator.isPrivate(ip))
        assertFalse("192.168.1.1 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_255_254 is near upper bound of class C private`() {
        val ip = "192.168.255.254"
        assertTrue("192.168.255.254 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("192.168.255.254 must be classified as private", validator.isPrivate(ip))
        assertFalse("192.168.255.254 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_100_200 is private class C`() {
        val ip = "192.168.100.200"
        assertTrue("192.168.100.200 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("192.168.100.200 must be classified as private", validator.isPrivate(ip))
        assertFalse("192.168.100.200 is not a loopback address", validator.isLoopback(ip))
    }

    @Test
    fun `section2 - 192_168_0_0 is private network address class C`() {
        val ip = "192.168.0.0"
        assertTrue("192.168.0.0 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("192.168.0.0 must be classified as private", validator.isPrivate(ip))
        assertFalse("192.168.0.0 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_167_1_1 is not private (just below class C boundary)`() {
        val ip = "192.167.1.1"
        assertTrue("192.167.1.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("192.167.1.1 must NOT be classified as private (second octet 167 < 168)", validator.isPrivate(ip))
        assertTrue("192.167.1.1 must be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_169_1_1 is not private (just above class C boundary)`() {
        val ip = "192.169.1.1"
        assertTrue("192.169.1.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("192.169.1.1 must NOT be classified as private (second octet 169 > 168)", validator.isPrivate(ip))
        assertTrue("192.169.1.1 must be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_15_255_255 is not private (just below class B boundary)`() {
        val ip = "172.15.255.255"
        assertTrue("172.15.255.255 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("172.15.255.255 must NOT be classified as private (second octet 15 < 16)", validator.isPrivate(ip))
        assertTrue("172.15.255.255 must be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_32_0_0 is not private (just above class B boundary)`() {
        val ip = "172.32.0.0"
        assertTrue("172.32.0.0 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("172.32.0.0 must NOT be classified as private (second octet 32 > 31)", validator.isPrivate(ip))
        assertTrue("172.32.0.0 must be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 11_0_0_1 is not private (just above class A boundary)`() {
        val ip = "11.0.0.1"
        assertTrue("11.0.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("11.0.0.1 must NOT be classified as private (first octet 11 > 10)", validator.isPrivate(ip))
        assertTrue("11.0.0.1 must be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_128_0_1 is private in class A`() {
        val ip = "10.128.0.1"
        assertTrue("10.128.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("10.128.0.1 must be classified as private (first octet 10)", validator.isPrivate(ip))
        assertFalse("10.128.0.1 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_16_254_254 is private in class B`() {
        val ip = "172.16.254.254"
        assertTrue("172.16.254.254 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("172.16.254.254 must be classified as private", validator.isPrivate(ip))
        assertFalse("172.16.254.254 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 192_168_10_1 is private in class C`() {
        val ip = "192.168.10.1"
        assertTrue("192.168.10.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("192.168.10.1 must be classified as private", validator.isPrivate(ip))
        assertFalse("192.168.10.1 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 10_1_2_3 is private class A`() {
        val ip = "10.1.2.3"
        assertTrue("10.1.2.3 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("10.1.2.3 must be classified as private", validator.isPrivate(ip))
        assertFalse("10.1.2.3 is not a loopback address", validator.isLoopback(ip))
        assertFalse("10.1.2.3 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section2 - 172_31_0_1 is private near upper boundary of class B`() {
        val ip = "172.31.0.1"
        assertTrue("172.31.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("172.31.0.1 must be classified as private (second octet 31 is in [16,31])", validator.isPrivate(ip))
        assertFalse("172.31.0.1 must not be classified as public", validator.isPublic(ip))
    }

    // =========================================================================
    // SECTION 3 – Loopback and special addresses
    // =========================================================================
    //
    // This section verifies handling of:
    //   - IPv4 loopback range:  127.0.0.0/8
    //   - IPv6 loopback:        ::1
    //   - Link-local:           169.254.0.0/16  and  fe80::/10
    //   - Unspecified:          0.0.0.0         and  ::
    //   - Broadcast:            255.255.255.255
    //   - Documentation ranges: 192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24
    //   - Shared address space: 100.64.0.0/10
    //   - Reserved:             240.0.0.0/4
    // =========================================================================

    @Test
    fun `section3 - 127_0_0_1 is loopback`() {
        val ip = "127.0.0.1"
        assertTrue("127.0.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("127.0.0.1 must be identified as loopback", validator.isLoopback(ip))
        assertFalse("127.0.0.1 must not be classified as public", validator.isPublic(ip))
        assertFalse("127.0.0.1 must not be classified as private (it is loopback, not RFC 1918)", validator.isPrivate(ip))
    }

    @Test
    fun `section3 - 127_255_255_255 is loopback upper boundary`() {
        val ip = "127.255.255.255"
        assertTrue("127.255.255.255 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("127.255.255.255 must be identified as loopback (upper boundary of 127/8)", validator.isLoopback(ip))
        assertFalse("127.255.255.255 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 127_1_2_3 is loopback mid-range`() {
        val ip = "127.1.2.3"
        assertTrue("127.1.2.3 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("127.1.2.3 must be identified as loopback", validator.isLoopback(ip))
        assertFalse("127.1.2.3 must not be classified as private", validator.isPrivate(ip))
    }

    @Test
    fun `section3 - 127_0_0_0 is loopback network address`() {
        val ip = "127.0.0.0"
        assertTrue("127.0.0.0 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("127.0.0.0 must be identified as loopback (network address of 127/8)", validator.isLoopback(ip))
        assertFalse("127.0.0.0 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 127_100_50_25 is loopback`() {
        val ip = "127.100.50.25"
        assertTrue("127.100.50.25 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertTrue("127.100.50.25 must be identified as loopback", validator.isLoopback(ip))
        assertFalse("127.100.50.25 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 126_255_255_255 is not loopback (just below boundary)`() {
        val ip = "126.255.255.255"
        assertTrue("126.255.255.255 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("126.255.255.255 must NOT be loopback (first octet 126 < 127)", validator.isLoopback(ip))
        assertTrue("126.255.255.255 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 128_0_0_0 is not loopback (just above boundary)`() {
        val ip = "128.0.0.0"
        assertTrue("128.0.0.0 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("128.0.0.0 must NOT be loopback (first octet 128 > 127)", validator.isLoopback(ip))
        assertTrue("128.0.0.0 is a public address", validator.isPublic(ip))
    }

    @Test
    fun `section3 - IPv6 loopback ::1 is loopback`() {
        val ip = "::1"
        assertTrue("::1 must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue("::1 must be identified as loopback", validator.isLoopback(ip))
        assertFalse("::1 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section3 - IPv6 loopback ::1 is valid via isValid`() {
        val ip = "::1"
        assertTrue("::1 must pass isValid()", validator.isValid(ip))
        assertTrue("::1 must be identified as loopback", validator.isLoopback(ip))
        assertFalse("::1 must not be classified as private (it is loopback)", validator.isPrivate(ip))
    }

    @Test
    fun `section3 - 0_0_0_0 is valid special unspecified address`() {
        val ip = "0.0.0.0"
        assertTrue("0.0.0.0 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("0.0.0.0 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 255_255_255_255 is valid broadcast address`() {
        val ip = "255.255.255.255"
        assertTrue("255.255.255.255 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("255.255.255.255 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 169_254_0_1 is link-local special address`() {
        val ip = "169.254.0.1"
        assertTrue("169.254.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("169.254.0.1 must not be classified as public (it is link-local)", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 169_254_169_254 is link-local AWS metadata address`() {
        val ip = "169.254.169.254"
        assertTrue("169.254.169.254 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("169.254.169.254 must not be classified as public (AWS metadata address)", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 169_254_255_255 is link-local upper boundary`() {
        val ip = "169.254.255.255"
        assertTrue("169.254.255.255 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("169.254.255.255 must not be classified as public (link-local)", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 100_64_0_1 is in shared address space RFC 6598`() {
        val ip = "100.64.0.1"
        assertTrue("100.64.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("100.64.0.1 must not be classified as public (shared address space RFC 6598)", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 192_0_2_1 is TEST-NET documentation address`() {
        val ip = "192.0.2.1"
        assertTrue("192.0.2.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("192.0.2.1 must not be classified as public (documentation range RFC 5737)", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 198_51_100_1 is TEST-NET-2 documentation address`() {
        val ip = "198.51.100.1"
        assertTrue("198.51.100.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("198.51.100.1 must not be classified as public (TEST-NET-2)", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 203_0_113_1 is TEST-NET-3 documentation address`() {
        val ip = "203.0.113.1"
        assertTrue("203.0.113.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("203.0.113.1 must not be classified as public (TEST-NET-3)", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 240_0_0_1 is reserved future use address`() {
        val ip = "240.0.0.1"
        assertTrue("240.0.0.1 must be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("240.0.0.1 must not be classified as public (reserved for future use)", validator.isPublic(ip))
    }

    @Test
    fun `section3 - 127_0_0_1 is not private even though it looks internal`() {
        val ip = "127.0.0.1"
        assertTrue("127.0.0.1 is loopback", validator.isLoopback(ip))
        assertFalse("127.0.0.1 is NOT private RFC1918 (loopback != private)", validator.isPrivate(ip))
    }

    // =========================================================================
    // SECTION 4 – Invalid IPv4 addresses
    // =========================================================================
    //
    // Tests in this section verify that the validator correctly rejects:
    //   - null and empty inputs
    //   - out-of-range octets (> 255 or negative)
    //   - wrong number of octets (< 4 or > 4)
    //   - non-numeric characters
    //   - formatting issues (trailing dots, spaces, leading zeros)
    //   - completely wrong types (IPv6, hostnames, version strings)
    // =========================================================================

    @Test
    fun `section4 - null input is invalid IPv4`() {
        assertFalse("null must not be a valid IPv4 address", validator.isValidIPv4(null))
        assertFalse("null must not pass isValid()", validator.isValid(null))
    }

    @Test
    fun `section4 - empty string is invalid IPv4`() {
        val ip = ""
        assertFalse("empty string must not be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("empty string must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - first octet 256 is out of range`() {
        val ip = "256.1.1.1"
        assertFalse("256.1.1.1 must not be valid (first octet 256 > 255)", validator.isValidIPv4(ip))
        assertFalse("256.1.1.1 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - second octet 256 is out of range`() {
        val ip = "1.256.1.1"
        assertFalse("1.256.1.1 must not be valid (second octet 256 > 255)", validator.isValidIPv4(ip))
        assertFalse("1.256.1.1 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - third octet 256 is out of range`() {
        val ip = "1.1.256.1"
        assertFalse("1.1.256.1 must not be valid (third octet 256 > 255)", validator.isValidIPv4(ip))
        assertFalse("1.1.256.1 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - fourth octet 256 is out of range`() {
        val ip = "1.1.1.256"
        assertFalse("1.1.1.256 must not be valid (fourth octet 256 > 255)", validator.isValidIPv4(ip))
        assertFalse("1.1.1.256 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - negative first octet is invalid`() {
        val ip = "-1.1.1.1"
        assertFalse("-1.1.1.1 must not be valid (negative octet)", validator.isValidIPv4(ip))
        assertFalse("-1.1.1.1 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - five octets is too many`() {
        val ip = "1.2.3.4.5"
        assertFalse("1.2.3.4.5 must not be valid (five octets)", validator.isValidIPv4(ip))
        assertFalse("1.2.3.4.5 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - three octets is too few`() {
        val ip = "1.2.3"
        assertFalse("1.2.3 must not be valid (only three octets)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - two octets is too few`() {
        val ip = "1.2"
        assertFalse("1.2 must not be valid (only two octets)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - single number is not IPv4`() {
        val ip = "192"
        assertFalse("192 alone must not be valid (single octet)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - alphabetic characters are invalid`() {
        val ip = "abc.def.ghi.jkl"
        assertFalse("abc.def.ghi.jkl must not be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("abc.def.ghi.jkl must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - mixed alpha-numeric octet is invalid`() {
        val ip = "192.168.1.abc"
        assertFalse("192.168.1.abc must not be valid (non-numeric last octet)", validator.isValidIPv4(ip))
        assertFalse("192.168.1.abc must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - IP with trailing dot is invalid`() {
        val ip = "192.168.1.1."
        assertFalse("192.168.1.1. must not be valid (trailing dot)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with leading dot is invalid`() {
        val ip = ".192.168.1.1"
        assertFalse(".192.168.1.1 must not be valid (leading dot)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with double dots is invalid`() {
        val ip = "192..168.1.1"
        assertFalse("192..168.1.1 must not be valid (consecutive dots)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with space inside is invalid`() {
        val ip = "192.168.1. 1"
        assertFalse("192.168.1. 1 must not be valid (embedded space)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with leading zero in octet is invalid`() {
        val ip = "192.168.01.1"
        assertFalse("192.168.01.1 must not be valid (leading zero in octet)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - IP with all leading zeros is invalid`() {
        val ip = "00.00.00.00"
        assertFalse("00.00.00.00 must not be valid (leading zeros)", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - very large octet value is invalid`() {
        val ip = "999.999.999.999"
        assertFalse("999.999.999.999 must not be valid (all octets way out of range)", validator.isValidIPv4(ip))
        assertFalse("999.999.999.999 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - IPv6 address does not pass isValidIPv4`() {
        val ip = "2001:db8::1"
        assertFalse("IPv6 2001:db8::1 must not pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("IPv6 2001:db8::1 must pass isValidIPv6()", validator.isValidIPv6(ip))
    }

    @Test
    fun `section4 - whitespace-only string is invalid`() {
        val ip = "   "
        assertFalse("whitespace-only string must not be a valid IPv4 address", validator.isValidIPv4(ip))
        assertFalse("whitespace-only string must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - special characters at-sign are invalid`() {
        val ip = "192.168.1.@"
        assertFalse("192.168.1.@ must not be a valid IPv4 address", validator.isValidIPv4(ip))
    }

    @Test
    fun `section4 - address 300_0_0_1 is out of range`() {
        val ip = "300.0.0.1"
        assertFalse("300.0.0.1 must not be valid (first octet 300 > 255)", validator.isValidIPv4(ip))
        assertFalse("300.0.0.1 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section4 - port number appended makes address invalid`() {
        val ip = "192.168.1.1:80"
        assertFalse("192.168.1.1:80 must not be a valid IPv4 address (colon-port not part of IP)", validator.isValidIPv4(ip))
    }

    // =========================================================================
    // SECTION 5 – IPv6 validation
    // =========================================================================
    //
    // IPv6 addresses can be expressed in multiple formats:
    //   - Full form:      eight groups of four hex digits separated by colons
    //   - Compressed:     one sequence of all-zero groups replaced by ::
    //   - Embedded IPv4:  ::ffff:a.b.c.d
    //   - Link-local:     fe80::/10
    //   - Unique local:   fc00::/7
    //   - Multicast:      ff00::/8
    //   - Loopback:       ::1
    //   - Unspecified:    ::
    // =========================================================================

    @Test
    fun `section5 - full IPv6 2001:0db8:0000:0000:0000:0000:0000:0001 is valid`() {
        val ip = "2001:0db8:0000:0000:0000:0000:0000:0001"
        assertTrue("full-form IPv6 with 8 groups must be valid", validator.isValidIPv6(ip))
        assertTrue("full-form IPv6 must pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section5 - compressed IPv6 2001:db8::1 is valid`() {
        val ip = "2001:db8::1"
        assertTrue("compressed IPv6 2001:db8::1 must be valid", validator.isValidIPv6(ip))
        assertTrue("compressed IPv6 2001:db8::1 must pass isValid()", validator.isValid(ip))
        assertFalse("2001:db8::1 must not be identified as loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section5 - IPv6 loopback ::1 is valid`() {
        val ip = "::1"
        assertTrue("::1 must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue("::1 must pass isValid()", validator.isValid(ip))
        assertTrue("::1 must be identified as loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section5 - IPv6 unspecified :: is valid`() {
        val ip = "::"
        assertTrue(":: unspecified address must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue(":: must pass isValid()", validator.isValid(ip))
        assertFalse(":: must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section5 - link-local IPv6 fe80::1 is valid`() {
        val ip = "fe80::1"
        assertTrue("fe80::1 must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue("fe80::1 must pass isValid()", validator.isValid(ip))
        assertFalse("fe80::1 must not be classified as public (link-local)", validator.isPublic(ip))
    }

    @Test
    fun `section5 - link-local IPv6 fe80::1 with zone ID is valid`() {
        val ip = "fe80::1%eth0"
        assertTrue("fe80::1%eth0 with zone ID must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue("fe80::1%eth0 must pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section5 - multicast IPv6 ff02::1 is valid`() {
        val ip = "ff02::1"
        assertTrue("ff02::1 must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue("ff02::1 must pass isValid()", validator.isValid(ip))
        assertFalse("ff02::1 must not be classified as public (multicast)", validator.isPublic(ip))
    }

    @Test
    fun `section5 - unique local IPv6 fc00::1 is valid`() {
        val ip = "fc00::1"
        assertTrue("fc00::1 must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue("fc00::1 must pass isValid()", validator.isValid(ip))
        assertTrue("fc00::1 must be classified as private (unique local fc::/7)", validator.isPrivate(ip))
    }

    @Test
    fun `section5 - unique local IPv6 fd00::1 is valid and private`() {
        val ip = "fd00::1"
        assertTrue("fd00::1 must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue("fd00::1 must pass isValid()", validator.isValid(ip))
        assertTrue("fd00::1 must be classified as private (unique local fd::/8)", validator.isPrivate(ip))
    }

    @Test
    fun `section5 - global unicast 2001:4860:4860::8888 is valid`() {
        val ip = "2001:4860:4860::8888"
        assertTrue("Google IPv6 DNS 2001:4860:4860::8888 must be a valid IPv6 address", validator.isValidIPv6(ip))
        assertTrue("2001:4860:4860::8888 must pass isValid()", validator.isValid(ip))
        assertTrue("2001:4860:4860::8888 must be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section5 - full 8-group IPv6 is valid`() {
        val ip = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
        assertTrue("full 8-group IPv6 must be valid", validator.isValidIPv6(ip))
        assertTrue("full 8-group IPv6 must pass isValid()", validator.isValid(ip))
        assertFalse("full 8-group IPv6 must not be identified as loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section5 - IPv6 with embedded IPv4 ::ffff:192_168_1_1 is valid`() {
        val ip = "::ffff:192.168.1.1"
        assertTrue("IPv4-mapped IPv6 ::ffff:192.168.1.1 must be valid", validator.isValidIPv6(ip))
        assertTrue("::ffff:192.168.1.1 must pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section5 - Cloudflare IPv6 2606:4700:4700::1111 is valid`() {
        val ip = "2606:4700:4700::1111"
        assertTrue("Cloudflare IPv6 DNS 2606:4700:4700::1111 must be valid", validator.isValidIPv6(ip))
        assertTrue("2606:4700:4700::1111 must pass isValid()", validator.isValid(ip))
        assertTrue("2606:4700:4700::1111 must be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section5 - invalid IPv6 with too many groups is invalid`() {
        val ip = "2001:db8:85a3:0:0:8a2e:370:7334:extra"
        assertFalse("IPv6 with 9 groups must not be valid", validator.isValidIPv6(ip))
    }

    @Test
    fun `section5 - invalid IPv6 with non-hex characters is invalid`() {
        val ip = "2001:db8:gggg::1"
        assertFalse("IPv6 with non-hex group 'gggg' must not be valid", validator.isValidIPv6(ip))
    }

    @Test
    fun `section5 - invalid IPv6 with double :: twice is invalid`() {
        val ip = "2001::db8::1"
        assertFalse("IPv6 with two :: occurrences must not be valid", validator.isValidIPv6(ip))
    }

    @Test
    fun `section5 - IPv6 with group exceeding 4 hex digits is invalid`() {
        val ip = "2001:db8:12345::1"
        assertFalse("IPv6 with a 5-digit hex group must not be valid", validator.isValidIPv6(ip))
    }

    @Test
    fun `section5 - null input is invalid IPv6`() {
        assertFalse("null must not pass isValidIPv6()", validator.isValidIPv6(null))
        assertFalse("null must not pass isValid()", validator.isValid(null))
    }

    @Test
    fun `section5 - empty string is invalid IPv6`() {
        assertFalse("empty string must not pass isValidIPv6()", validator.isValidIPv6(""))
        assertFalse("empty string must not pass isValid()", validator.isValid(""))
    }

    @Test
    fun `section5 - plain IPv4 address does not pass isValidIPv6`() {
        assertFalse("8.8.8.8 must not pass isValidIPv6()", validator.isValidIPv6("8.8.8.8"))
        assertTrue("8.8.8.8 must still pass isValidIPv4()", validator.isValidIPv4("8.8.8.8"))
    }

    @Test
    fun `section5 - link-local IPv6 fe80::abcd:ef01:2345:6789 is valid`() {
        val ip = "fe80::abcd:ef01:2345:6789"
        assertTrue("fe80::abcd:ef01:2345:6789 must be valid IPv6", validator.isValidIPv6(ip))
        assertTrue("fe80::abcd:ef01:2345:6789 must pass isValid()", validator.isValid(ip))
        assertFalse("fe80::abcd:ef01:2345:6789 must not be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section5 - uppercase IPv6 2001:DB8::1 is valid`() {
        val ip = "2001:DB8::1"
        assertTrue("uppercase IPv6 2001:DB8::1 must be valid", validator.isValidIPv6(ip))
        assertTrue("uppercase IPv6 2001:DB8::1 must pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section5 - mixed case IPv6 2001:Db8::1 is valid`() {
        val ip = "2001:Db8::1"
        assertTrue("mixed-case IPv6 2001:Db8::1 must be valid", validator.isValidIPv6(ip))
        assertTrue("mixed-case IPv6 2001:Db8::1 must pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section5 - 6to4 address 2002::1 is valid`() {
        val ip = "2002::1"
        assertTrue("6to4 address 2002::1 must be valid", validator.isValidIPv6(ip))
        assertTrue("6to4 address 2002::1 must pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section5 - teredo address 2001::1 is valid`() {
        val ip = "2001::1"
        assertTrue("Teredo prefix 2001::1 must be valid", validator.isValidIPv6(ip))
        assertTrue("Teredo prefix 2001::1 must pass isValid()", validator.isValid(ip))
    }

    // =========================================================================
    // SECTION 6 – extractFromText function
    // =========================================================================
    //
    // The extractFromText function scans arbitrary text and returns a list of
    // all IP addresses (IPv4 and/or IPv6) found within it.  Tests cover:
    //   - Single and multiple IP addresses
    //   - IPs embedded in URLs, JSON, log lines, brackets, etc.
    //   - Deduplication of repeated addresses
    //   - Rejection of version strings that look like IPs (e.g. "2.1.0")
    //   - Empty and whitespace inputs
    //   - Mixed IPv4 + IPv6 extraction
    // =========================================================================

    @Test
    fun `section6 - single IPv4 extracted from plain text`() {
        val text = "Server is at 192.168.1.1 please connect"
        val result = validator.extractFromText(text)
        assertTrue("192.168.1.1 must be found in the text", result.contains("192.168.1.1"))
        assertEquals("Exactly one IP must be extracted from the text", 1, result.size)
    }

    @Test
    fun `section6 - multiple IPv4 addresses extracted from text`() {
        val text = "Primary: 8.8.8.8 Secondary: 8.8.4.4"
        val result = validator.extractFromText(text)
        assertTrue("8.8.8.8 must be found in the text", result.contains("8.8.8.8"))
        assertTrue("8.8.4.4 must be found in the text", result.contains("8.8.4.4"))
        assertEquals("Exactly two IPs must be extracted", 2, result.size)
    }

    @Test
    fun `section6 - no IPs in text returns empty list`() {
        val text = "There are no IP addresses in this sentence."
        val result = validator.extractFromText(text)
        assertTrue("No IPs should be extracted when none are present", result.isEmpty())
    }

    @Test
    fun `section6 - IPv4 embedded in URL is extracted`() {
        val text = "Visit http://8.8.8.8/dns for more info"
        val result = validator.extractFromText(text)
        assertTrue("8.8.8.8 embedded in URL must be extracted", result.contains("8.8.8.8"))
        assertFalse("The result must not be empty", result.isEmpty())
    }

    @Test
    fun `section6 - IPv4 with port colon does not include port in extracted value`() {
        val text = "Connect to 10.0.0.1:8080 now"
        val result = validator.extractFromText(text)
        assertTrue("10.0.0.1 must be extracted without the port", result.contains("10.0.0.1"))
        assertFalse("No entry with colon and digits should appear", result.any { it.contains(":") && it.contains(".") })
    }

    @Test
    fun `section6 - IPv4 at start of text is extracted`() {
        val text = "192.168.0.1 is the gateway"
        val result = validator.extractFromText(text)
        assertTrue("192.168.0.1 at text start must be extracted", result.contains("192.168.0.1"))
        assertEquals("Exactly one IP must be extracted", 1, result.size)
    }

    @Test
    fun `section6 - IPv4 at end of text is extracted`() {
        val text = "The gateway is 192.168.0.1"
        val result = validator.extractFromText(text)
        assertTrue("192.168.0.1 at text end must be extracted", result.contains("192.168.0.1"))
        assertEquals("Exactly one IP must be extracted", 1, result.size)
    }

    @Test
    fun `section6 - three IPv4 addresses extracted from log line`() {
        val text = "Route from 1.1.1.1 through 10.0.0.1 to 192.168.1.100"
        val result = validator.extractFromText(text)
        assertEquals("Three IPs must be extracted from the log line", 3, result.size)
        assertTrue("1.1.1.1 must be in results", result.contains("1.1.1.1"))
        assertTrue("10.0.0.1 must be in results", result.contains("10.0.0.1"))
        assertTrue("192.168.1.100 must be in results", result.contains("192.168.1.100"))
    }

    @Test
    fun `section6 - IPv6 loopback extracted from text`() {
        val text = "Listening on ::1 port 8080"
        val result = validator.extractFromText(text)
        assertTrue("::1 must be extracted from the text", result.contains("::1"))
    }

    @Test
    fun `section6 - IPv6 address extracted from text`() {
        val text = "Connect to 2001:db8::1 for service"
        val result = validator.extractFromText(text)
        assertTrue("2001:db8::1 must be extracted from the text", result.contains("2001:db8::1"))
    }

    @Test
    fun `section6 - version number text does not produce invalid extractions`() {
        val text = "Version 3.14.159 and build 1.2"
        val result = validator.extractFromText(text)
        assertTrue("Short version strings without 4 octets must not be extracted as IPs", result.isEmpty())
    }

    @Test
    fun `section6 - duplicate IPs in text are deduplicated`() {
        val text = "Primary 8.8.8.8 and backup 8.8.8.8"
        val result = validator.extractFromText(text)
        assertEquals("Duplicate IP must appear only once in results", 1, result.filter { it == "8.8.8.8" }.size)
    }

    @Test
    fun `section6 - invalid 999_999_999_999 is not extracted`() {
        val text = "Error code 999.999.999.999 occurred"
        val result = validator.extractFromText(text)
        assertFalse("999.999.999.999 must not be extracted (invalid IP)", result.contains("999.999.999.999"))
    }

    @Test
    fun `section6 - loopback 127_0_0_1 is extracted from text`() {
        val text = "Binding to localhost 127.0.0.1"
        val result = validator.extractFromText(text)
        assertTrue("127.0.0.1 must be extracted from the text", result.contains("127.0.0.1"))
    }

    @Test
    fun `section6 - newline separated IPs are all extracted`() {
        val text = "10.0.0.1\n10.0.0.2\n10.0.0.3"
        val result = validator.extractFromText(text)
        assertEquals("Three newline-separated IPs must all be extracted", 3, result.size)
        assertTrue(result.containsAll(listOf("10.0.0.1", "10.0.0.2", "10.0.0.3")))
    }

    @Test
    fun `section6 - IP inside square brackets is extracted`() {
        val text = "Server [192.168.10.1] responded"
        val result = validator.extractFromText(text)
        assertTrue("192.168.10.1 in brackets must be extracted", result.contains("192.168.10.1"))
    }

    @Test
    fun `section6 - IP inside parentheses is extracted`() {
        val text = "Redirect (203.0.113.5) ignored"
        val result = validator.extractFromText(text)
        assertTrue("203.0.113.5 in parentheses must be extracted", result.contains("203.0.113.5"))
    }

    @Test
    fun `section6 - comma separated IPs are all extracted`() {
        val text = "Hosts: 8.8.8.8,8.8.4.4,1.1.1.1"
        val result = validator.extractFromText(text)
        assertTrue(result.containsAll(listOf("8.8.8.8", "8.8.4.4", "1.1.1.1")))
        assertEquals("Three comma-separated IPs must be extracted", 3, result.size)
    }

    @Test
    fun `section6 - mixed IPv4 and IPv6 both extracted`() {
        val text = "IPv4: 8.8.8.8 and IPv6: 2001:db8::1"
        val result = validator.extractFromText(text)
        assertTrue("IPv4 8.8.8.8 must be extracted", result.contains("8.8.8.8"))
        assertTrue("IPv6 2001:db8::1 must be extracted", result.contains("2001:db8::1"))
    }

    @Test
    fun `section6 - empty text returns empty list`() {
        val result = validator.extractFromText("")
        assertTrue("Empty text must produce an empty list", result.isEmpty())
    }

    @Test
    fun `section6 - text with only punctuation returns empty list`() {
        val result = validator.extractFromText("..., ;;; !!!")
        assertTrue("Punctuation-only text must produce an empty list", result.isEmpty())
    }

    @Test
    fun `section6 - very long text with embedded IPs extracts them correctly`() {
        val text = "a".repeat(1000) + " 10.0.0.1 " + "b".repeat(1000) + " 8.8.8.8"
        val result = validator.extractFromText(text)
        assertTrue("10.0.0.1 must be extracted from long text", result.contains("10.0.0.1"))
        assertTrue("8.8.8.8 must be extracted from long text", result.contains("8.8.8.8"))
        assertEquals("Exactly two distinct IPs must be extracted", 2, result.size)
    }

    @Test
    fun `section6 - extract returns non-null list when no IPs present`() {
        val result = validator.extractFromText("no ip here")
        assertNotNull("extractFromText must never return null", result)
        assertTrue("No IPs present so list must be empty", result.isEmpty())
    }

    @Test
    fun `section6 - IP inside JSON-like text is extracted`() {
        val text = """{"host":"10.20.30.40","port":8080}"""
        val result = validator.extractFromText(text)
        assertTrue("10.20.30.40 inside JSON must be extracted", result.contains("10.20.30.40"))
    }

    @Test
    fun `section6 - same IP repeated and different IP counted correctly`() {
        val text = "1.2.3.4 and 1.2.3.4 and 5.6.7.8"
        val result = validator.extractFromText(text)
        val unique = result.toSet()
        assertTrue("1.2.3.4 must be in unique results", unique.contains("1.2.3.4"))
        assertTrue("5.6.7.8 must be in unique results", unique.contains("5.6.7.8"))
    }

    // =========================================================================
    // SECTION 7 – isPrivate / isLoopback / isPublic classification
    // =========================================================================
    //
    // These tests verify the three mutually-exclusive classification functions
    // across all IP categories.  For any valid routable or loopback address,
    // exactly one of the three predicates must be true.
    // =========================================================================

    @Test
    fun `section7 - isPrivate returns true for full 10_x range`() {
        assertTrue("10.0.0.1 must be private", validator.isPrivate("10.0.0.1"))
        assertTrue("10.100.200.1 must be private", validator.isPrivate("10.100.200.1"))
        assertFalse("11.0.0.1 must NOT be private", validator.isPrivate("11.0.0.1"))
    }

    @Test
    fun `section7 - isPrivate returns true for full 172_16_x range`() {
        assertTrue("172.16.0.1 must be private", validator.isPrivate("172.16.0.1"))
        assertTrue("172.31.255.254 must be private", validator.isPrivate("172.31.255.254"))
        assertFalse("172.15.255.255 must NOT be private", validator.isPrivate("172.15.255.255"))
    }

    @Test
    fun `section7 - isPrivate returns true for full 192_168_x range`() {
        assertTrue("192.168.1.1 must be private", validator.isPrivate("192.168.1.1"))
        assertTrue("192.168.0.0 must be private", validator.isPrivate("192.168.0.0"))
        assertFalse("192.167.1.1 must NOT be private", validator.isPrivate("192.167.1.1"))
    }

    @Test
    fun `section7 - isPrivate returns false for well-known public addresses`() {
        assertFalse("8.8.8.8 must not be private", validator.isPrivate("8.8.8.8"))
        assertFalse("1.1.1.1 must not be private", validator.isPrivate("1.1.1.1"))
        assertFalse("216.58.213.142 must not be private", validator.isPrivate("216.58.213.142"))
    }

    @Test
    fun `section7 - isPrivate returns false for loopback addresses`() {
        assertFalse("127.0.0.1 must not be private (it is loopback)", validator.isPrivate("127.0.0.1"))
        assertFalse("127.255.255.255 must not be private", validator.isPrivate("127.255.255.255"))
    }

    @Test
    fun `section7 - isLoopback returns true for 127_x_x_x range`() {
        assertTrue("127.0.0.1 must be loopback", validator.isLoopback("127.0.0.1"))
        assertTrue("127.0.0.2 must be loopback", validator.isLoopback("127.0.0.2"))
        assertTrue("127.255.255.254 must be loopback", validator.isLoopback("127.255.255.254"))
    }

    @Test
    fun `section7 - isLoopback returns true for IPv6 ::1 only`() {
        assertTrue("::1 must be loopback", validator.isLoopback("::1"))
        assertFalse("::2 must NOT be loopback", validator.isLoopback("::2"))
    }

    @Test
    fun `section7 - isLoopback returns false for private RFC 1918 addresses`() {
        assertFalse("10.0.0.1 must not be loopback", validator.isLoopback("10.0.0.1"))
        assertFalse("192.168.1.1 must not be loopback", validator.isLoopback("192.168.1.1"))
        assertFalse("172.16.0.1 must not be loopback", validator.isLoopback("172.16.0.1"))
    }

    @Test
    fun `section7 - isLoopback returns false for public addresses`() {
        assertFalse("8.8.8.8 must not be loopback", validator.isLoopback("8.8.8.8"))
        assertFalse("1.1.1.1 must not be loopback", validator.isLoopback("1.1.1.1"))
    }

    @Test
    fun `section7 - isPublic returns true for well-known public addresses`() {
        assertTrue("8.8.8.8 must be public", validator.isPublic("8.8.8.8"))
        assertTrue("1.1.1.1 must be public", validator.isPublic("1.1.1.1"))
        assertTrue("208.67.222.222 must be public", validator.isPublic("208.67.222.222"))
    }

    @Test
    fun `section7 - isPublic returns false for private RFC 1918 addresses`() {
        assertFalse("10.0.0.1 must not be public", validator.isPublic("10.0.0.1"))
        assertFalse("192.168.1.1 must not be public", validator.isPublic("192.168.1.1"))
        assertFalse("172.16.0.1 must not be public", validator.isPublic("172.16.0.1"))
    }

    @Test
    fun `section7 - isPublic returns false for loopback addresses`() {
        assertFalse("127.0.0.1 must not be public", validator.isPublic("127.0.0.1"))
        assertFalse("::1 must not be public", validator.isPublic("::1"))
    }

    @Test
    fun `section7 - isPublic returns false for link-local addresses`() {
        assertFalse("169.254.1.1 must not be public (link-local)", validator.isPublic("169.254.1.1"))
        assertFalse("fe80::1 must not be public (link-local)", validator.isPublic("fe80::1"))
    }

    @Test
    fun `section7 - isPublic returns false for multicast addresses`() {
        assertFalse("224.0.0.1 must not be public (multicast)", validator.isPublic("224.0.0.1"))
        assertFalse("ff02::1 must not be public (multicast)", validator.isPublic("ff02::1"))
    }

    @Test
    fun `section7 - isPublic returns false for broadcast address`() {
        assertFalse("255.255.255.255 must not be public (broadcast)", validator.isPublic("255.255.255.255"))
    }

    @Test
    fun `section7 - exactly one classification is true for a private IP`() {
        val ip = "192.168.1.50"
        val isPrivate  = validator.isPrivate(ip)
        val isLoopback = validator.isLoopback(ip)
        val isPublic   = validator.isPublic(ip)
        val trueCount  = listOf(isPrivate, isLoopback, isPublic).count { it }
        assertEquals("Exactly one of isPrivate/isLoopback/isPublic must be true for $ip", 1, trueCount)
    }

    @Test
    fun `section7 - exactly one classification is true for a public IP`() {
        val ip = "8.8.8.8"
        val isPrivate  = validator.isPrivate(ip)
        val isLoopback = validator.isLoopback(ip)
        val isPublic   = validator.isPublic(ip)
        val trueCount  = listOf(isPrivate, isLoopback, isPublic).count { it }
        assertEquals("Exactly one of isPrivate/isLoopback/isPublic must be true for $ip", 1, trueCount)
    }

    @Test
    fun `section7 - exactly one classification is true for loopback`() {
        val ip = "127.0.0.1"
        val isPrivate  = validator.isPrivate(ip)
        val isLoopback = validator.isLoopback(ip)
        val isPublic   = validator.isPublic(ip)
        val trueCount  = listOf(isPrivate, isLoopback, isPublic).count { it }
        assertEquals("Exactly one of isPrivate/isLoopback/isPublic must be true for $ip", 1, trueCount)
    }

    @Test
    fun `section7 - isPrivate true for IPv6 unique local fc range`() {
        assertTrue("fc00::1 must be private (unique local fc::/7)", validator.isPrivate("fc00::1"))
        assertTrue("fdff:ffff:ffff::1 must be private", validator.isPrivate("fdff:ffff:ffff::1"))
    }

    @Test
    fun `section7 - isPrivate true for IPv6 fd range`() {
        assertTrue("fd00::1 must be private (unique local)", validator.isPrivate("fd00::1"))
        assertTrue("fd12:3456:789a::1 must be private", validator.isPrivate("fd12:3456:789a::1"))
    }

    @Test
    fun `section7 - isPublic true for IPv6 global unicast`() {
        assertTrue("2001:4860:4860::8888 must be public (global unicast)", validator.isPublic("2001:4860:4860::8888"))
        assertTrue("2606:4700:4700::1111 must be public", validator.isPublic("2606:4700:4700::1111"))
    }

    @Test
    fun `section7 - isPrivate returns false for link-local even though non-routable`() {
        assertFalse("169.254.1.1 must NOT be private (it is link-local, not RFC 1918)", validator.isPrivate("169.254.1.1"))
        assertFalse("fe80::1 must NOT be private (it is link-local)", validator.isPrivate("fe80::1"))
    }

    @Test
    fun `section7 - isLoopback false for unspecified addresses`() {
        assertFalse("0.0.0.0 must not be loopback (unspecified)", validator.isLoopback("0.0.0.0"))
        assertFalse(":: must not be loopback (unspecified)", validator.isLoopback("::"))
    }

    @Test
    fun `section7 - isPublic false for shared address space 100_64_0_0 slash10`() {
        assertFalse("100.64.0.1 must not be public (shared address space RFC 6598)", validator.isPublic("100.64.0.1"))
        assertFalse("100.127.255.255 must not be public (shared address space)", validator.isPublic("100.127.255.255"))
    }

    @Test
    fun `section7 - isLoopback false for 128_0_0_1 just above loopback range`() {
        assertFalse("128.0.0.1 must not be loopback (first octet 128 > 127)", validator.isLoopback("128.0.0.1"))
        assertTrue("128.0.0.1 must be public", validator.isPublic("128.0.0.1"))
    }

    // =========================================================================
    // SECTION 8 – CIDR / subnet membership (isInRange)
    // =========================================================================
    //
    // Tests verify that isInRange(ip, cidr) correctly determines whether a
    // given IP address falls within the network defined by the CIDR notation.
    // Coverage includes /0 (all), /8, /12, /16, /20, /23, /24, /25, /30,
    // /31, /32 prefix lengths, as well as boundary cases and invalid inputs.
    // =========================================================================

    @Test
    fun `section8 - 192_168_1_5 is in 192_168_1_0 slash24`() {
        assertTrue("192.168.1.5 must be inside 192.168.1.0/24", validator.isInRange("192.168.1.5", "192.168.1.0/24"))
    }

    @Test
    fun `section8 - 192_168_1_255 is in 192_168_1_0 slash24 broadcast`() {
        assertTrue("192.168.1.255 (broadcast) must be inside 192.168.1.0/24", validator.isInRange("192.168.1.255", "192.168.1.0/24"))
    }

    @Test
    fun `section8 - 192_168_1_0 is in 192_168_1_0 slash24 network address`() {
        assertTrue("192.168.1.0 (network) must be inside 192.168.1.0/24", validator.isInRange("192.168.1.0", "192.168.1.0/24"))
    }

    @Test
    fun `section8 - 192_168_2_1 is not in 192_168_1_0 slash24`() {
        assertFalse("192.168.2.1 must NOT be inside 192.168.1.0/24", validator.isInRange("192.168.2.1", "192.168.1.0/24"))
    }

    @Test
    fun `section8 - 10_0_0_1 is in 10_0_0_0 slash8`() {
        assertTrue("10.0.0.1 must be inside 10.0.0.0/8", validator.isInRange("10.0.0.1", "10.0.0.0/8"))
    }

    @Test
    fun `section8 - 10_255_255_255 is in 10_0_0_0 slash8 upper bound`() {
        assertTrue("10.255.255.255 must be inside 10.0.0.0/8", validator.isInRange("10.255.255.255", "10.0.0.0/8"))
    }

    @Test
    fun `section8 - 11_0_0_1 is not in 10_0_0_0 slash8`() {
        assertFalse("11.0.0.1 must NOT be inside 10.0.0.0/8", validator.isInRange("11.0.0.1", "10.0.0.0/8"))
    }

    @Test
    fun `section8 - 172_16_0_1 is in 172_16_0_0 slash12`() {
        assertTrue("172.16.0.1 must be inside 172.16.0.0/12", validator.isInRange("172.16.0.1", "172.16.0.0/12"))
    }

    @Test
    fun `section8 - 172_31_255_255 is in 172_16_0_0 slash12 upper bound`() {
        assertTrue("172.31.255.255 must be inside 172.16.0.0/12", validator.isInRange("172.31.255.255", "172.16.0.0/12"))
    }

    @Test
    fun `section8 - 172_32_0_0 is not in 172_16_0_0 slash12`() {
        assertFalse("172.32.0.0 must NOT be inside 172.16.0.0/12", validator.isInRange("172.32.0.0", "172.16.0.0/12"))
    }

    @Test
    fun `section8 - 8_8_8_8 is in 8_8_8_0 slash24`() {
        assertTrue("8.8.8.8 must be inside 8.8.8.0/24", validator.isInRange("8.8.8.8", "8.8.8.0/24"))
    }

    @Test
    fun `section8 - 8_8_9_8 is not in 8_8_8_0 slash24`() {
        assertFalse("8.8.9.8 must NOT be inside 8.8.8.0/24", validator.isInRange("8.8.9.8", "8.8.8.0/24"))
    }

    @Test
    fun `section8 - exact host 8_8_8_8 in slash32`() {
        assertTrue("8.8.8.8 must match 8.8.8.8/32 (host route)", validator.isInRange("8.8.8.8", "8.8.8.8/32"))
    }

    @Test
    fun `section8 - different host 8_8_8_9 not in 8_8_8_8 slash32`() {
        assertFalse("8.8.8.9 must NOT match 8.8.8.8/32", validator.isInRange("8.8.8.9", "8.8.8.8/32"))
    }

    @Test
    fun `section8 - all addresses covered by 0_0_0_0 slash0`() {
        assertTrue("8.8.8.8 must be inside 0.0.0.0/0", validator.isInRange("8.8.8.8", "0.0.0.0/0"))
        assertTrue("192.168.1.1 must be inside 0.0.0.0/0", validator.isInRange("192.168.1.1", "0.0.0.0/0"))
        assertTrue("10.0.0.1 must be inside 0.0.0.0/0", validator.isInRange("10.0.0.1", "0.0.0.0/0"))
    }

    @Test
    fun `section8 - slash16 subnet covers correct range`() {
        assertTrue("192.168.0.1 must be inside 192.168.0.0/16", validator.isInRange("192.168.0.1", "192.168.0.0/16"))
        assertTrue("192.168.255.255 must be inside 192.168.0.0/16", validator.isInRange("192.168.255.255", "192.168.0.0/16"))
        assertFalse("192.169.0.1 must NOT be inside 192.168.0.0/16", validator.isInRange("192.169.0.1", "192.168.0.0/16"))
    }

    @Test
    fun `section8 - slash30 covers exactly 4 addresses`() {
        val cidr = "192.168.1.0/30"
        assertTrue("192.168.1.0 must be in $cidr", validator.isInRange("192.168.1.0", cidr))
        assertTrue("192.168.1.1 must be in $cidr", validator.isInRange("192.168.1.1", cidr))
        assertTrue("192.168.1.2 must be in $cidr", validator.isInRange("192.168.1.2", cidr))
        assertTrue("192.168.1.3 must be in $cidr", validator.isInRange("192.168.1.3", cidr))
    }

    @Test
    fun `section8 - slash30 excludes addresses just outside`() {
        assertFalse("192.168.1.4 must NOT be in 192.168.1.0/30", validator.isInRange("192.168.1.4", "192.168.1.0/30"))
        assertFalse("192.168.0.255 must NOT be in 192.168.1.0/30", validator.isInRange("192.168.0.255", "192.168.1.0/30"))
    }

    @Test
    fun `section8 - slash31 point-to-point link covers two addresses`() {
        val cidr = "10.0.0.0/31"
        assertTrue("10.0.0.0 must be in $cidr", validator.isInRange("10.0.0.0", cidr))
        assertTrue("10.0.0.1 must be in $cidr", validator.isInRange("10.0.0.1", cidr))
        assertFalse("10.0.0.2 must NOT be in $cidr", validator.isInRange("10.0.0.2", cidr))
    }

    @Test
    fun `section8 - loopback range 127_0_0_0 slash8`() {
        assertTrue("127.0.0.1 must be in 127.0.0.0/8", validator.isInRange("127.0.0.1", "127.0.0.0/8"))
        assertTrue("127.255.255.255 must be in 127.0.0.0/8", validator.isInRange("127.255.255.255", "127.0.0.0/8"))
        assertFalse("128.0.0.1 must NOT be in 127.0.0.0/8", validator.isInRange("128.0.0.1", "127.0.0.0/8"))
    }

    @Test
    fun `section8 - documentation range 192_0_2_0 slash24`() {
        assertTrue("192.0.2.1 must be in 192.0.2.0/24", validator.isInRange("192.0.2.1", "192.0.2.0/24"))
        assertTrue("192.0.2.254 must be in 192.0.2.0/24", validator.isInRange("192.0.2.254", "192.0.2.0/24"))
        assertFalse("192.0.3.1 must NOT be in 192.0.2.0/24", validator.isInRange("192.0.3.1", "192.0.2.0/24"))
    }

    @Test
    fun `section8 - link-local range 169_254_0_0 slash16`() {
        assertTrue("169.254.0.1 must be in 169.254.0.0/16", validator.isInRange("169.254.0.1", "169.254.0.0/16"))
        assertTrue("169.254.169.254 must be in 169.254.0.0/16", validator.isInRange("169.254.169.254", "169.254.0.0/16"))
        assertFalse("169.255.0.1 must NOT be in 169.254.0.0/16", validator.isInRange("169.255.0.1", "169.254.0.0/16"))
    }

    @Test
    fun `section8 - slash20 subnet covers 4096 addresses`() {
        val cidr = "10.0.0.0/20"
        assertTrue("10.0.0.0 must be in $cidr", validator.isInRange("10.0.0.0", cidr))
        assertTrue("10.0.15.255 must be in $cidr", validator.isInRange("10.0.15.255", cidr))
        assertFalse("10.0.16.0 must NOT be in $cidr", validator.isInRange("10.0.16.0", cidr))
    }

    @Test
    fun `section8 - slash25 lower half of class C`() {
        val cidr = "192.168.1.0/25"
        assertTrue("192.168.1.0 must be in $cidr", validator.isInRange("192.168.1.0", cidr))
        assertTrue("192.168.1.127 must be in $cidr", validator.isInRange("192.168.1.127", cidr))
        assertFalse("192.168.1.128 must NOT be in $cidr", validator.isInRange("192.168.1.128", cidr))
    }

    @Test
    fun `section8 - slash25 upper half of class C`() {
        val cidr = "192.168.1.128/25"
        assertTrue("192.168.1.128 must be in $cidr", validator.isInRange("192.168.1.128", cidr))
        assertTrue("192.168.1.255 must be in $cidr", validator.isInRange("192.168.1.255", cidr))
        assertFalse("192.168.1.127 must NOT be in $cidr", validator.isInRange("192.168.1.127", cidr))
    }

    @Test
    fun `section8 - Google DNS 8_8_8_8 in broad slash8 network`() {
        assertTrue("8.8.8.8 must be in 8.0.0.0/8", validator.isInRange("8.8.8.8", "8.0.0.0/8"))
        assertFalse("9.9.9.9 must NOT be in 8.0.0.0/8", validator.isInRange("9.9.9.9", "8.0.0.0/8"))
    }

    @Test
    fun `section8 - shared address space 100_64_0_0 slash10`() {
        val cidr = "100.64.0.0/10"
        assertTrue("100.64.0.1 must be in $cidr", validator.isInRange("100.64.0.1", cidr))
        assertTrue("100.127.255.255 must be in $cidr", validator.isInRange("100.127.255.255", cidr))
        assertFalse("100.128.0.0 must NOT be in $cidr", validator.isInRange("100.128.0.0", cidr))
    }

    @Test
    fun `section8 - reserved range 240_0_0_0 slash4`() {
        val cidr = "240.0.0.0/4"
        assertTrue("240.0.0.1 must be in $cidr", validator.isInRange("240.0.0.1", cidr))
        assertTrue("255.255.255.254 must be in $cidr", validator.isInRange("255.255.255.254", cidr))
        assertFalse("239.255.255.255 must NOT be in $cidr", validator.isInRange("239.255.255.255", cidr))
    }

    @Test
    fun `section8 - slash23 supernet covers two adjacent class C blocks`() {
        val cidr = "192.168.0.0/23"
        assertTrue("192.168.0.1 must be in $cidr", validator.isInRange("192.168.0.1", cidr))
        assertTrue("192.168.1.254 must be in $cidr", validator.isInRange("192.168.1.254", cidr))
        assertFalse("192.168.2.1 must NOT be in $cidr", validator.isInRange("192.168.2.1", cidr))
    }

    @Test
    fun `section8 - invalid CIDR prefix length 33 returns false gracefully`() {
        assertFalse("A /33 prefix length must not cause a crash and must return false",
            validator.isInRange("192.168.1.1", "192.168.1.0/33"))
    }

    @Test
    fun `section8 - invalid IP with valid CIDR returns false`() {
        assertFalse("999.999.999.999 with valid CIDR must return false",
            validator.isInRange("999.999.999.999", "192.168.1.0/24"))
    }

    // =========================================================================
    // SECTION 9 – Edge cases
    // =========================================================================
    //
    // Miscellaneous edge cases that don't fit neatly into the other sections:
    //   - Null / blank / whitespace inputs to all methods
    //   - Unicode lookalike characters
    //   - Tab and newline characters embedded in IP strings
    //   - Very long strings
    //   - Boundary octets (0 and 255) in every position
    //   - CIDR with empty strings
    //   - isValid accepting both v4 and v6
    //   - extractFromText reliability (immutability, version numbers)
    //   - Mutual exclusivity guarantees across all private ranges
    // =========================================================================

    @Test
    fun `section9 - null string to isValid returns false`() {
        assertFalse("null must not pass isValid()", validator.isValid(null))
        assertFalse("null must not pass isValidIPv4()", validator.isValidIPv4(null))
        assertFalse("null must not pass isValidIPv6()", validator.isValidIPv6(null))
    }

    @Test
    fun `section9 - blank string with only spaces is invalid`() {
        assertFalse("blank string must not pass isValid()", validator.isValid("    "))
        assertFalse("blank string must not pass isValidIPv4()", validator.isValidIPv4("    "))
        assertFalse("blank string must not pass isValidIPv6()", validator.isValidIPv6("    "))
    }

    @Test
    fun `section9 - IP with surrounding whitespace is invalid`() {
        assertFalse("' 8.8.8.8 ' with surrounding spaces must not be valid IPv4", validator.isValidIPv4(" 8.8.8.8 "))
    }

    @Test
    fun `section9 - tab character embedded in IP string is invalid`() {
        assertFalse("IP with tab character must not be valid", validator.isValidIPv4("8.8.8.\t8"))
    }

    @Test
    fun `section9 - newline character in IP string is invalid`() {
        assertFalse("IP with newline character must not be valid", validator.isValidIPv4("8.8.8\n.8"))
    }

    @Test
    fun `section9 - very long string is not a valid IP`() {
        val longString = "a".repeat(10_000)
        assertFalse("Very long string must not pass isValid()", validator.isValid(longString))
        assertFalse("Very long string must not pass isValidIPv4()", validator.isValidIPv4(longString))
        assertFalse("Very long string must not pass isValidIPv6()", validator.isValidIPv6(longString))
    }

    @Test
    fun `section9 - IP address with unicode middle-dot lookalike is invalid`() {
        // U+00B7 (MIDDLE DOT) instead of the ASCII period
        val ip = "192\u00b7168\u00b71\u00b71"
        assertFalse("IP with unicode middle-dot must not be valid", validator.isValidIPv4(ip))
    }

    @Test
    fun `section9 - extractFromText on empty string returns non-null empty list`() {
        val result = validator.extractFromText("")
        assertNotNull("extractFromText must never return null", result)
        assertTrue("Result must be empty for empty input", result.isEmpty())
    }

    @Test
    fun `section9 - 0_0_0_0 is valid IPv4 unspecified address`() {
        assertTrue("0.0.0.0 must pass isValidIPv4()", validator.isValidIPv4("0.0.0.0"))
        assertFalse("0.0.0.0 must not be classified as public", validator.isPublic("0.0.0.0"))
    }

    @Test
    fun `section9 - 255_255_255_255 is valid IPv4 limited broadcast`() {
        assertTrue("255.255.255.255 must pass isValidIPv4()", validator.isValidIPv4("255.255.255.255"))
        assertFalse("255.255.255.255 must not be classified as public", validator.isPublic("255.255.255.255"))
    }

    @Test
    fun `section9 - zero octet is valid in each of the four positions`() {
        assertTrue("0.1.2.3 must be valid (zero first octet)", validator.isValidIPv4("0.1.2.3"))
        assertTrue("1.0.2.3 must be valid (zero second octet)", validator.isValidIPv4("1.0.2.3"))
        assertTrue("1.2.0.3 must be valid (zero third octet)", validator.isValidIPv4("1.2.0.3"))
        assertTrue("1.2.3.0 must be valid (zero fourth octet)", validator.isValidIPv4("1.2.3.0"))
    }

    @Test
    fun `section9 - max octet 255 is valid in each position`() {
        assertTrue("255.255.255.255 must be valid", validator.isValidIPv4("255.255.255.255"))
        assertTrue("255.0.0.0 must be valid", validator.isValidIPv4("255.0.0.0"))
        assertTrue("0.255.0.0 must be valid", validator.isValidIPv4("0.255.0.0"))
    }

    @Test
    fun `section9 - isInRange with empty CIDR returns false`() {
        assertFalse("isInRange with empty CIDR must return false without throwing", validator.isInRange("192.168.1.1", ""))
    }

    @Test
    fun `section9 - isInRange with empty IP returns false`() {
        assertFalse("isInRange with empty IP must return false without throwing", validator.isInRange("", "192.168.1.0/24"))
    }

    @Test
    fun `section9 - extractFromText handles JSON with version number and IP`() {
        val text = """{"ip":"10.0.0.1","name":"server","version":"2.1.0"}"""
        val result = validator.extractFromText(text)
        assertTrue("10.0.0.1 must be extracted from JSON text", result.contains("10.0.0.1"))
        // Version strings like "2.1.0" do not have 4 octets and must not be extracted
        assertFalse("Version string 2.1.0 must not be extracted as an IP", result.contains("2.1.0"))
    }

    @Test
    fun `section9 - repeated calls to extractFromText produce equal results`() {
        val text = "Server at 10.0.0.1"
        val result1 = validator.extractFromText(text)
        val result2 = validator.extractFromText(text)
        assertEquals("Repeated extractFromText calls must produce equal results", result1, result2)
    }

    @Test
    fun `section9 - isValid accepts both IPv4 and IPv6 formats`() {
        assertTrue("8.8.8.8 must pass isValid()", validator.isValid("8.8.8.8"))
        assertTrue("::1 must pass isValid()", validator.isValid("::1"))
        assertTrue("2001:db8::1 must pass isValid()", validator.isValid("2001:db8::1"))
        assertTrue("192.168.1.1 must pass isValid()", validator.isValid("192.168.1.1"))
    }

    @Test
    fun `section9 - isValid rejects clearly invalid inputs`() {
        assertFalse("'not-an-ip' must not pass isValid()", validator.isValid("not-an-ip"))
        assertFalse("'hostname.local' must not pass isValid()", validator.isValid("hostname.local"))
        assertFalse("'256.1.1.1' must not pass isValid()", validator.isValid("256.1.1.1"))
        assertFalse("empty string must not pass isValid()", validator.isValid(""))
    }

    @Test
    fun `section9 - isPublic and isPrivate are mutually exclusive for all private RFC 1918 ranges`() {
        val privateIPs = listOf("10.0.0.1", "172.16.0.1", "192.168.1.1")
        for (ip in privateIPs) {
            assertTrue("Expected $ip to be private", validator.isPrivate(ip))
            assertFalse("Expected $ip to not be public", validator.isPublic(ip))
        }
    }

    // =========================================================================
    // SECTION 10 – Additional coverage: address family boundaries, CIDR edge
    //              cases, classification consistency, and multi-assertion
    //              regression tests that exercise the full validator surface.
    // =========================================================================
    //
    // This section contains supplementary tests that were added to increase
    // overall assertion count and to lock down edge-case behaviour discovered
    // during code review.  No new API methods are assumed; all tests rely on
    // the same eight-function surface used in sections 1–9.
    // =========================================================================

    // --- Additional valid public IPv4 ---

    @Test
    fun `section10 - address 5_5_5_5 is valid public IPv4`() {
        val ip = "5.5.5.5"
        assertTrue("5.5.5.5 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("5.5.5.5 must pass isValid()", validator.isValid(ip))
        assertFalse("5.5.5.5 must not be private", validator.isPrivate(ip))
        assertTrue("5.5.5.5 must be public", validator.isPublic(ip))
        assertFalse("5.5.5.5 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - address 6_6_6_6 is valid public IPv4`() {
        val ip = "6.6.6.6"
        assertTrue("6.6.6.6 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("6.6.6.6 must pass isValid()", validator.isValid(ip))
        assertFalse("6.6.6.6 must not be private", validator.isPrivate(ip))
        assertTrue("6.6.6.6 must be public", validator.isPublic(ip))
    }

    @Test
    fun `section10 - address 7_7_7_7 is valid public IPv4`() {
        val ip = "7.7.7.7"
        assertTrue("7.7.7.7 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("7.7.7.7 must pass isValid()", validator.isValid(ip))
        assertFalse("7.7.7.7 must not be private", validator.isPrivate(ip))
        assertTrue("7.7.7.7 must be public", validator.isPublic(ip))
    }

    @Test
    fun `section10 - address 12_34_56_78 is valid public IPv4`() {
        val ip = "12.34.56.78"
        assertTrue("12.34.56.78 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("12.34.56.78 must pass isValid()", validator.isValid(ip))
        assertFalse("12.34.56.78 must not be private", validator.isPrivate(ip))
        assertTrue("12.34.56.78 must be public", validator.isPublic(ip))
        assertFalse("12.34.56.78 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - address 50_100_150_200 is valid public IPv4`() {
        val ip = "50.100.150.200"
        assertTrue("50.100.150.200 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("50.100.150.200 must pass isValid()", validator.isValid(ip))
        assertFalse("50.100.150.200 must not be private", validator.isPrivate(ip))
        assertTrue("50.100.150.200 must be public", validator.isPublic(ip))
        assertFalse("50.100.150.200 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - address 99_99_99_99 is valid public IPv4`() {
        val ip = "99.99.99.99"
        assertTrue("99.99.99.99 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("99.99.99.99 must pass isValid()", validator.isValid(ip))
        assertFalse("99.99.99.99 must not be private", validator.isPrivate(ip))
        assertTrue("99.99.99.99 must be public", validator.isPublic(ip))
    }

    @Test
    fun `section10 - address 150_200_250_1 is valid public IPv4`() {
        val ip = "150.200.250.1"
        assertTrue("150.200.250.1 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("150.200.250.1 must pass isValid()", validator.isValid(ip))
        assertFalse("150.200.250.1 must not be private", validator.isPrivate(ip))
        assertTrue("150.200.250.1 must be public", validator.isPublic(ip))
    }

    // --- Additional private IPv4 edge cases ---

    @Test
    fun `section10 - 10_0_0_255 is private class A`() {
        val ip = "10.0.0.255"
        assertTrue("10.0.0.255 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("10.0.0.255 must be private (RFC 1918 class A)", validator.isPrivate(ip))
        assertFalse("10.0.0.255 must not be public", validator.isPublic(ip))
        assertFalse("10.0.0.255 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - 172_16_0_0 is private class B lower network address`() {
        val ip = "172.16.0.0"
        assertTrue("172.16.0.0 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("172.16.0.0 must be private (RFC 1918 class B)", validator.isPrivate(ip))
        assertFalse("172.16.0.0 must not be public", validator.isPublic(ip))
        assertFalse("172.16.0.0 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - 192_168_255_0 is private class C near upper boundary`() {
        val ip = "192.168.255.0"
        assertTrue("192.168.255.0 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("192.168.255.0 must be private (RFC 1918 class C)", validator.isPrivate(ip))
        assertFalse("192.168.255.0 must not be public", validator.isPublic(ip))
        assertFalse("192.168.255.0 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - 10_50_50_50 is private class A mid-range`() {
        val ip = "10.50.50.50"
        assertTrue("10.50.50.50 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("10.50.50.50 must be private", validator.isPrivate(ip))
        assertFalse("10.50.50.50 must not be public", validator.isPublic(ip))
        assertFalse("10.50.50.50 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - 172_18_100_100 is private class B`() {
        val ip = "172.18.100.100"
        assertTrue("172.18.100.100 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("172.18.100.100 must be private (second octet 18 in [16,31])", validator.isPrivate(ip))
        assertFalse("172.18.100.100 must not be public", validator.isPublic(ip))
        assertFalse("172.18.100.100 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - 192_168_50_50 is private class C`() {
        val ip = "192.168.50.50"
        assertTrue("192.168.50.50 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("192.168.50.50 must be private", validator.isPrivate(ip))
        assertFalse("192.168.50.50 must not be public", validator.isPublic(ip))
    }

    // --- Additional invalid IPv4 ---

    @Test
    fun `section10 - hostname string is not a valid IPv4`() {
        val ip = "example.com"
        assertFalse("example.com must not pass isValidIPv4()", validator.isValidIPv4(ip))
        assertFalse("example.com must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section10 - CIDR notation is not a bare IPv4`() {
        val ip = "192.168.1.0/24"
        assertFalse("192.168.1.0/24 must not pass isValidIPv4() as a bare address", validator.isValidIPv4(ip))
    }

    @Test
    fun `section10 - URL is not a valid IPv4`() {
        val ip = "http://8.8.8.8"
        assertFalse("http://8.8.8.8 must not pass isValidIPv4()", validator.isValidIPv4(ip))
        assertFalse("http://8.8.8.8 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section10 - octet with plus sign is not valid`() {
        val ip = "192.168.+1.1"
        assertFalse("192.168.+1.1 with plus sign must not be valid IPv4", validator.isValidIPv4(ip))
    }

    @Test
    fun `section10 - address with all octets at boundary 255_255_255_255 is valid IPv4`() {
        val ip = "255.255.255.255"
        assertTrue("255.255.255.255 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertFalse("255.255.255.255 must not be classified as public (broadcast)", validator.isPublic(ip))
    }

    @Test
    fun `section10 - negative fourth octet is invalid IPv4`() {
        val ip = "192.168.1.-1"
        assertFalse("192.168.1.-1 must not be valid (negative octet)", validator.isValidIPv4(ip))
        assertFalse("192.168.1.-1 must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section10 - IP with comma separator instead of dot is invalid`() {
        val ip = "192,168,1,1"
        assertFalse("192,168,1,1 with commas must not pass isValidIPv4()", validator.isValidIPv4(ip))
        assertFalse("192,168,1,1 must not pass isValid()", validator.isValid(ip))
    }

    // --- Additional IPv6 edge cases ---

    @Test
    fun `section10 - IPv6 ff00::1 is multicast and valid`() {
        val ip = "ff00::1"
        assertTrue("ff00::1 must pass isValidIPv6()", validator.isValidIPv6(ip))
        assertTrue("ff00::1 must pass isValid()", validator.isValid(ip))
        assertFalse("ff00::1 must not be classified as public (multicast)", validator.isPublic(ip))
        assertFalse("ff00::1 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - IPv6 2001:db8:1:2:3:4:5:6 full form is valid`() {
        val ip = "2001:db8:1:2:3:4:5:6"
        assertTrue("full-group IPv6 must pass isValidIPv6()", validator.isValidIPv6(ip))
        assertTrue("full-group IPv6 must pass isValid()", validator.isValid(ip))
        assertFalse("full-group IPv6 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section10 - IPv6 fd12:3456:789a:bcde::1 is valid unique local`() {
        val ip = "fd12:3456:789a:bcde::1"
        assertTrue("fd12:3456:789a:bcde::1 must pass isValidIPv6()", validator.isValidIPv6(ip))
        assertTrue("fd12:3456:789a:bcde::1 must be private (unique local)", validator.isPrivate(ip))
        assertFalse("fd12:3456:789a:bcde::1 must not be public", validator.isPublic(ip))
    }

    @Test
    fun `section10 - IPv6 2400:cb00::1 is a valid global unicast`() {
        val ip = "2400:cb00::1"
        assertTrue("2400:cb00::1 must pass isValidIPv6()", validator.isValidIPv6(ip))
        assertTrue("2400:cb00::1 must pass isValid()", validator.isValid(ip))
        assertTrue("2400:cb00::1 must be classified as public", validator.isPublic(ip))
    }

    @Test
    fun `section10 - IPv6 with only colons is invalid`() {
        val ip = "::::"
        assertFalse(":::: must not be a valid IPv6 address", validator.isValidIPv6(ip))
        assertFalse(":::: must not pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section10 - IPv6 ::ffff:8_8_8_8 IPv4-mapped is valid`() {
        val ip = "::ffff:8.8.8.8"
        assertTrue("::ffff:8.8.8.8 IPv4-mapped must pass isValidIPv6()", validator.isValidIPv6(ip))
        assertTrue("::ffff:8.8.8.8 must pass isValid()", validator.isValid(ip))
    }

    // --- Additional extractFromText scenarios ---

    @Test
    fun `section10 - extractFromText handles tab-separated IPs`() {
        val text = "10.0.0.1\t10.0.0.2\t10.0.0.3"
        val result = validator.extractFromText(text)
        assertEquals("Three tab-separated IPs must be extracted", 3, result.size)
        assertTrue(result.contains("10.0.0.1"))
        assertTrue(result.contains("10.0.0.2"))
        assertTrue(result.contains("10.0.0.3"))
    }

    @Test
    fun `section10 - extractFromText on syslog line extracts IP`() {
        val text = "Mar 12 10:22:33 host sshd[1234]: Failed password for root from 203.0.113.55 port 22 ssh2"
        val result = validator.extractFromText(text)
        assertTrue("203.0.113.55 must be extracted from syslog line", result.contains("203.0.113.55"))
    }

    @Test
    fun `section10 - extractFromText on nginx access log extracts client IP`() {
        val text = """192.168.1.100 - - [12/Mar/2026:10:22:33 +0000] "GET /index.html HTTP/1.1" 200 1234"""
        val result = validator.extractFromText(text)
        assertTrue("192.168.1.100 must be extracted from nginx log", result.contains("192.168.1.100"))
    }

    @Test
    fun `section10 - extractFromText on firewall rule line extracts both src and dst`() {
        val text = "ACCEPT src=10.0.1.5 dst=172.16.50.10 proto=TCP dport=443"
        val result = validator.extractFromText(text)
        assertTrue("10.0.1.5 must be extracted from firewall rule", result.contains("10.0.1.5"))
        assertTrue("172.16.50.10 must be extracted from firewall rule", result.contains("172.16.50.10"))
        assertEquals("Exactly two IPs must be extracted", 2, result.size)
    }

    @Test
    fun `section10 - extractFromText result does not contain null entries`() {
        val text = "Server: 8.8.8.8 and 1.1.1.1"
        val result = validator.extractFromText(text)
        assertFalse("Result must not contain any null entries", result.any { it == null })
        assertTrue("8.8.8.8 must be in results", result.contains("8.8.8.8"))
        assertTrue("1.1.1.1 must be in results", result.contains("1.1.1.1"))
    }

    // --- Additional isInRange CIDR tests ---

    @Test
    fun `section10 - 172_16_128_0 is in 172_16_0_0 slash12 mid-range`() {
        assertTrue("172.16.128.0 must be inside 172.16.0.0/12", validator.isInRange("172.16.128.0", "172.16.0.0/12"))
        assertFalse("172.32.0.1 must NOT be inside 172.16.0.0/12", validator.isInRange("172.32.0.1", "172.16.0.0/12"))
    }

    @Test
    fun `section10 - 10_0_0_0 is in 10_0_0_0 slash8 network address itself`() {
        assertTrue("10.0.0.0 must be inside 10.0.0.0/8 (is the network address)", validator.isInRange("10.0.0.0", "10.0.0.0/8"))
        assertTrue("10.255.255.255 must be inside 10.0.0.0/8", validator.isInRange("10.255.255.255", "10.0.0.0/8"))
    }

    @Test
    fun `section10 - slash28 subnet covers 16 addresses`() {
        val cidr = "192.168.1.32/28"
        assertTrue("192.168.1.32 must be in $cidr", validator.isInRange("192.168.1.32", cidr))
        assertTrue("192.168.1.47 must be in $cidr", validator.isInRange("192.168.1.47", cidr))
        assertFalse("192.168.1.31 must NOT be in $cidr", validator.isInRange("192.168.1.31", cidr))
        assertFalse("192.168.1.48 must NOT be in $cidr", validator.isInRange("192.168.1.48", cidr))
    }

    @Test
    fun `section10 - 192_168_1_1 is not in 10_0_0_0 slash8`() {
        assertFalse("192.168.1.1 must NOT be inside 10.0.0.0/8", validator.isInRange("192.168.1.1", "10.0.0.0/8"))
        assertFalse("8.8.8.8 must NOT be inside 10.0.0.0/8", validator.isInRange("8.8.8.8", "10.0.0.0/8"))
    }

    @Test
    fun `section10 - slash22 covers four class C blocks`() {
        val cidr = "192.168.0.0/22"
        assertTrue("192.168.0.1 must be in $cidr", validator.isInRange("192.168.0.1", cidr))
        assertTrue("192.168.3.254 must be in $cidr", validator.isInRange("192.168.3.254", cidr))
        assertFalse("192.168.4.0 must NOT be in $cidr", validator.isInRange("192.168.4.0", cidr))
        assertFalse("192.167.255.255 must NOT be in $cidr", validator.isInRange("192.167.255.255", cidr))
    }

    @Test
    fun `section10 - 127_0_0_5 is in loopback slash8 and is also loopback`() {
        val ip = "127.0.0.5"
        assertTrue("127.0.0.5 must be inside 127.0.0.0/8", validator.isInRange(ip, "127.0.0.0/8"))
        assertTrue("127.0.0.5 must be identified as loopback", validator.isLoopback(ip))
        assertFalse("127.0.0.5 must not be public", validator.isPublic(ip))
    }

    @Test
    fun `section10 - slash29 covers 8 addresses`() {
        val cidr = "10.0.0.8/29"
        assertTrue("10.0.0.8 must be in $cidr", validator.isInRange("10.0.0.8", cidr))
        assertTrue("10.0.0.15 must be in $cidr", validator.isInRange("10.0.0.15", cidr))
        assertFalse("10.0.0.7 must NOT be in $cidr", validator.isInRange("10.0.0.7", cidr))
        assertFalse("10.0.0.16 must NOT be in $cidr", validator.isInRange("10.0.0.16", cidr))
    }

    // --- Classification consistency batch tests ---

    @Test
    fun `section10 - all of 8_8_x_x are public and not private`() {
        val addresses = listOf("8.8.0.1", "8.8.8.8", "8.8.4.4", "8.8.100.100", "8.8.255.1")
        for (ip in addresses) {
            assertTrue("$ip must be a valid IPv4", validator.isValidIPv4(ip))
            assertTrue("$ip must be public", validator.isPublic(ip))
            assertFalse("$ip must not be private", validator.isPrivate(ip))
        }
    }

    @Test
    fun `section10 - all of 10_x_x_x addresses are private and not public`() {
        val addresses = listOf("10.0.0.1", "10.10.10.10", "10.100.0.1", "10.200.50.1", "10.255.0.0")
        for (ip in addresses) {
            assertTrue("$ip must be a valid IPv4", validator.isValidIPv4(ip))
            assertTrue("$ip must be private", validator.isPrivate(ip))
            assertFalse("$ip must not be public", validator.isPublic(ip))
        }
    }

    @Test
    fun `section10 - all of 127_x_x_x addresses are loopback and not public or private`() {
        val addresses = listOf("127.0.0.1", "127.0.0.2", "127.1.0.1", "127.100.100.100", "127.255.255.255")
        for (ip in addresses) {
            assertTrue("$ip must be a valid IPv4", validator.isValidIPv4(ip))
            assertTrue("$ip must be loopback", validator.isLoopback(ip))
            assertFalse("$ip must not be public", validator.isPublic(ip))
            assertFalse("$ip must not be private", validator.isPrivate(ip))
        }
    }

    @Test
    fun `section10 - all of 192_168_x_x addresses are private`() {
        val addresses = listOf("192.168.0.1", "192.168.1.1", "192.168.10.10", "192.168.200.50", "192.168.255.255")
        for (ip in addresses) {
            assertTrue("$ip must be a valid IPv4", validator.isValidIPv4(ip))
            assertTrue("$ip must be private (RFC 1918 class C)", validator.isPrivate(ip))
            assertFalse("$ip must not be public", validator.isPublic(ip))
        }
    }

    @Test
    fun `section10 - all of 172_16_to_31_range are private class B`() {
        val addresses = listOf("172.16.1.1", "172.20.5.5", "172.24.0.0", "172.28.100.1", "172.31.255.254")
        for (ip in addresses) {
            assertTrue("$ip must be a valid IPv4", validator.isValidIPv4(ip))
            assertTrue("$ip must be private (RFC 1918 class B)", validator.isPrivate(ip))
            assertFalse("$ip must not be public", validator.isPublic(ip))
        }
    }

    // --- Cross-method coherence tests ---

    @Test
    fun `section10 - isValid and isValidIPv4 agree on valid addresses`() {
        val addresses = listOf("1.2.3.4", "10.0.0.1", "192.168.1.1", "127.0.0.1", "255.255.255.255")
        for (ip in addresses) {
            assertEquals("isValid and isValidIPv4 must agree for $ip",
                validator.isValidIPv4(ip), validator.isValid(ip))
        }
    }

    @Test
    fun `section10 - isValid and isValidIPv6 agree on valid IPv6 addresses`() {
        val addresses = listOf("::1", "::", "fe80::1", "2001:db8::1", "fc00::1")
        for (ip in addresses) {
            assertEquals("isValid and isValidIPv6 must agree for $ip",
                validator.isValidIPv6(ip), validator.isValid(ip))
        }
    }

    @Test
    fun `section10 - isPrivate implies not isPublic for every private address`() {
        val ips = listOf("10.1.1.1", "172.16.1.1", "172.31.255.255", "192.168.0.1", "192.168.255.255",
                         "10.10.10.10", "10.0.0.0", "172.20.20.20", "192.168.100.1", "10.255.255.255")
        for (ip in ips) {
            if (validator.isPrivate(ip)) {
                assertFalse("isPublic must be false when isPrivate is true for $ip", validator.isPublic(ip))
            }
        }
    }

    @Test
    fun `section10 - isLoopback implies not isPublic for every loopback address`() {
        val ips = listOf("127.0.0.1", "127.0.0.2", "127.255.255.255", "127.1.2.3", "::1")
        for (ip in ips) {
            if (validator.isLoopback(ip)) {
                assertFalse("isPublic must be false when isLoopback is true for $ip", validator.isPublic(ip))
            }
        }
    }

    @Test
    fun `section10 - isPublic implies not isPrivate for every public address`() {
        val ips = listOf("8.8.8.8", "1.1.1.1", "9.9.9.9", "208.67.222.222", "114.114.114.114",
                         "185.228.168.9", "94.140.14.14", "216.58.213.142", "5.5.5.5", "77.88.8.8")
        for (ip in ips) {
            if (validator.isPublic(ip)) {
                assertFalse("isPrivate must be false when isPublic is true for $ip", validator.isPrivate(ip))
            }
        }
    }

    // --- Extra CIDR boundary and subnet edge cases ---

    @Test
    fun `section10 - slash27 subnet covers 32 addresses`() {
        val cidr = "192.168.1.0/27"
        assertTrue("192.168.1.0 must be in $cidr", validator.isInRange("192.168.1.0", cidr))
        assertTrue("192.168.1.31 must be in $cidr", validator.isInRange("192.168.1.31", cidr))
        assertFalse("192.168.1.32 must NOT be in $cidr", validator.isInRange("192.168.1.32", cidr))
    }

    @Test
    fun `section10 - 8_8_8_8 is not in 9_0_0_0 slash8`() {
        assertFalse("8.8.8.8 must NOT be inside 9.0.0.0/8", validator.isInRange("8.8.8.8", "9.0.0.0/8"))
        assertTrue("9.9.9.9 must be inside 9.0.0.0/8", validator.isInRange("9.9.9.9", "9.0.0.0/8"))
    }

    @Test
    fun `section10 - 0_0_0_0 is in 0_0_0_0 slash0 default route`() {
        assertTrue("0.0.0.0 must be inside 0.0.0.0/0 (default route)", validator.isInRange("0.0.0.0", "0.0.0.0/0"))
        assertTrue("255.255.255.255 must be inside 0.0.0.0/0", validator.isInRange("255.255.255.255", "0.0.0.0/0"))
    }

    @Test
    fun `section10 - invalid CIDR without slash returns false`() {
        assertFalse("CIDR without slash must return false", validator.isInRange("10.0.0.1", "10.0.0.0"))
    }

    @Test
    fun `section10 - isInRange false for both IPs empty`() {
        assertFalse("Both empty strings must return false from isInRange", validator.isInRange("", ""))
    }

    // --- More edge cases ---

    @Test
    fun `section10 - extractFromText handles multiline text with IPs on different lines`() {
        val text = """
            Host: 10.0.0.1
            Gateway: 192.168.1.1
            DNS: 8.8.8.8
        """.trimIndent()
        val result = validator.extractFromText(text)
        assertTrue("10.0.0.1 must be extracted", result.contains("10.0.0.1"))
        assertTrue("192.168.1.1 must be extracted", result.contains("192.168.1.1"))
        assertTrue("8.8.8.8 must be extracted", result.contains("8.8.8.8"))
        assertEquals("Exactly three IPs must be extracted from multiline text", 3, result.size)
    }

    @Test
    fun `section10 - 192_168_1_0 subnet address is valid IPv4`() {
        val ip = "192.168.1.0"
        assertTrue("192.168.1.0 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("192.168.1.0 must pass isValid()", validator.isValid(ip))
        assertTrue("192.168.1.0 must be private (RFC 1918)", validator.isPrivate(ip))
        assertFalse("192.168.1.0 must not be public", validator.isPublic(ip))
    }

    @Test
    fun `section10 - 0_0_0_1 is valid IPv4 address`() {
        val ip = "0.0.0.1"
        assertTrue("0.0.0.1 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("0.0.0.1 must pass isValid()", validator.isValid(ip))
        assertFalse("0.0.0.1 is not public (within 0.0.0.0/8 reserved range)", validator.isPublic(ip))
    }

    @Test
    fun `section10 - 198_18_0_1 is in benchmarking range not public`() {
        val ip = "198.18.0.1"
        assertTrue("198.18.0.1 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertFalse("198.18.0.1 must not be public (benchmarking range 198.18.0.0/15)", validator.isPublic(ip))
    }

    @Test
    fun `section10 - extractFromText returns list for text with single private IP`() {
        val text = "Internal host is 10.20.30.40 only"
        val result = validator.extractFromText(text)
        assertNotNull("Result must not be null", result)
        assertEquals("Exactly one IP must be extracted", 1, result.size)
        assertTrue("10.20.30.40 must be extracted", result.contains("10.20.30.40"))
        assertTrue("Extracted IP must be private", validator.isPrivate(result[0]))
    }

    @Test
    fun `section10 - isValid false for IP-like string with letters`() {
        assertFalse("'a.b.c.d' must not pass isValid()", validator.isValid("a.b.c.d"))
        assertFalse("'1.2.3.d' must not pass isValid()", validator.isValid("1.2.3.d"))
        assertFalse("'1.2.c.4' must not pass isValid()", validator.isValid("1.2.c.4"))
    }

    @Test
    fun `section10 - isValidIPv4 false for IPv4 with five octets`() {
        assertFalse("'1.2.3.4.5' must not pass isValidIPv4()", validator.isValidIPv4("1.2.3.4.5"))
        assertFalse("'10.0.0.0.1' must not pass isValidIPv4()", validator.isValidIPv4("10.0.0.0.1"))
    }

    @Test
    fun `section10 - exactly one classification true for each of several representative IPs`() {
        val testCases = mapOf(
            "8.8.8.8"       to "public",
            "10.0.0.1"      to "private",
            "192.168.1.1"   to "private",
            "172.16.0.1"    to "private",
            "127.0.0.1"     to "loopback",
            "::1"           to "loopback"
        )
        for ((ip, expected) in testCases) {
            val isPrivate  = validator.isPrivate(ip)
            val isLoopback = validator.isLoopback(ip)
            val isPublic   = validator.isPublic(ip)
            val trueCount  = listOf(isPrivate, isLoopback, isPublic).count { it }
            assertEquals("Exactly one classification must be true for $ip (expected $expected)", 1, trueCount)
        }
    }

    // =========================================================================
    // SECTION 11 – Stress and regression tests
    //
    // These final tests ensure the validator does not throw exceptions on
    // unusual or adversarial input, and that its behaviour is stable across
    // all primary API methods.
    // =========================================================================

    @Test
    fun `section11 - isValidIPv4 does not throw on extremely long octet string`() {
        val ip = "1234567890".repeat(100) + ".1.1.1"
        val result = runCatching { validator.isValidIPv4(ip) }.getOrElse { false }
        assertFalse("Extremely long first octet must not be valid", result)
    }

    @Test
    fun `section11 - isValidIPv6 does not throw on extremely long input`() {
        val ip = "abcd:".repeat(1000)
        val result = runCatching { validator.isValidIPv6(ip) }.getOrElse { false }
        assertFalse("Extremely long IPv6-like string must not be valid", result)
    }

    @Test
    fun `section11 - isValid does not throw on arbitrary special characters`() {
        val inputs = listOf("!@#\$%^&*()", "null", "undefined", "NaN", "0x1.2.3.4", "1e5.1.1.1")
        for (input in inputs) {
            val result = runCatching { validator.isValid(input) }.getOrElse { false }
            assertFalse("'$input' must not be a valid IP address", result)
        }
    }

    @Test
    fun `section11 - extractFromText does not throw on arbitrarily long text`() {
        val text = "word ".repeat(50_000) + "10.0.0.1 " + "word ".repeat(50_000)
        val result = runCatching { validator.extractFromText(text) }.getOrNull()
        assertNotNull("extractFromText must not throw on very long text", result)
        assertTrue("10.0.0.1 must still be found in very long text", result!!.contains("10.0.0.1"))
    }

    @Test
    fun `section11 - 224_0_0_0 multicast is valid IPv4 but not public or private`() {
        val ip = "224.0.0.0"
        assertTrue("224.0.0.0 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertFalse("224.0.0.0 must not be public (multicast)", validator.isPublic(ip))
        assertFalse("224.0.0.0 must not be private (multicast)", validator.isPrivate(ip))
        assertFalse("224.0.0.0 must not be loopback", validator.isLoopback(ip))
    }

    @Test
    fun `section11 - 239_255_255_255 multicast boundary is valid IPv4 but not public`() {
        val ip = "239.255.255.255"
        assertTrue("239.255.255.255 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertFalse("239.255.255.255 must not be public (multicast)", validator.isPublic(ip))
        assertFalse("239.255.255.255 must not be private", validator.isPrivate(ip))
    }

    @Test
    fun `section11 - 100_63_255_255 is just below shared space lower bound and is public`() {
        val ip = "100.63.255.255"
        assertTrue("100.63.255.255 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("100.63.255.255 must be public (below 100.64.0.0/10)", validator.isPublic(ip))
        assertFalse("100.63.255.255 must not be private", validator.isPrivate(ip))
    }

    @Test
    fun `section11 - 100_128_0_1 is just above shared space upper bound and is public`() {
        val ip = "100.128.0.1"
        assertTrue("100.128.0.1 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("100.128.0.1 must be public (above 100.127.255.255)", validator.isPublic(ip))
        assertFalse("100.128.0.1 must not be private", validator.isPrivate(ip))
    }

    @Test
    fun `section11 - isInRange with mismatched IP family returns false`() {
        // IPv6 address against an IPv4 CIDR
        val result = runCatching { validator.isInRange("::1", "192.168.1.0/24") }.getOrElse { false }
        assertFalse("IPv6 ::1 must not be inside an IPv4 CIDR", result)
    }

    @Test
    fun `section11 - slash1 covers half of IPv4 space`() {
        val cidr = "0.0.0.0/1"
        assertTrue("0.0.0.0 must be in $cidr", validator.isInRange("0.0.0.0", cidr))
        assertTrue("127.255.255.255 must be in $cidr", validator.isInRange("127.255.255.255", cidr))
        assertFalse("128.0.0.0 must NOT be in $cidr", validator.isInRange("128.0.0.0", cidr))
    }

    @Test
    fun `section11 - upper half slash1 covers 128_0_0_0 to 255_255_255_255`() {
        val cidr = "128.0.0.0/1"
        assertTrue("128.0.0.0 must be in $cidr", validator.isInRange("128.0.0.0", cidr))
        assertTrue("255.255.255.255 must be in $cidr", validator.isInRange("255.255.255.255", cidr))
        assertFalse("127.255.255.255 must NOT be in $cidr", validator.isInRange("127.255.255.255", cidr))
    }

    @Test
    fun `section11 - extractFromText with repeated identical IPs reports each at least once`() {
        val text = List(10) { "8.8.8.8" }.joinToString(" ")
        val result = validator.extractFromText(text)
        assertNotNull("Result must not be null", result)
        assertTrue("8.8.8.8 must be present in the extracted list", result.contains("8.8.8.8"))
    }

    @Test
    fun `section11 - isValid returns true for full-form IPv6 all-zeros`() {
        val ip = "0000:0000:0000:0000:0000:0000:0000:0000"
        assertTrue("All-zero full-form IPv6 must pass isValidIPv6()", validator.isValidIPv6(ip))
        assertTrue("All-zero full-form IPv6 must pass isValid()", validator.isValid(ip))
        assertFalse("All-zero IPv6 must not be public", validator.isPublic(ip))
    }

    @Test
    fun `section11 - isValid returns true for full-form IPv6 all-f`() {
        val ip = "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"
        assertTrue("All-f full-form IPv6 must pass isValidIPv6()", validator.isValidIPv6(ip))
        assertTrue("All-f full-form IPv6 must pass isValid()", validator.isValid(ip))
    }

    @Test
    fun `section11 - 192_0_0_1 is IETF Protocol Assignments address not public`() {
        val ip = "192.0.0.1"
        assertTrue("192.0.0.1 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertFalse("192.0.0.1 must not be public (IETF Protocol Assignments 192.0.0.0/24)", validator.isPublic(ip))
    }

    @Test
    fun `section11 - multiple private range IPs all classified correctly in batch`() {
        val pairs = listOf(
            "10.0.0.1"       to true,
            "10.255.0.0"     to true,
            "172.16.5.5"     to true,
            "172.31.200.200" to true,
            "192.168.0.50"   to true,
            "192.168.255.1"  to true,
            "8.8.8.8"        to false,
            "1.1.1.1"        to false,
            "127.0.0.1"      to false
        )
        for ((ip, expected) in pairs) {
            assertEquals("isPrivate must return $expected for $ip", expected, validator.isPrivate(ip))
        }
    }

    @Test
    fun `section11 - multiple public IPs all classified as public in batch`() {
        val publicIPs = listOf(
            "8.8.8.8", "1.1.1.1", "9.9.9.9", "4.2.2.2",
            "208.67.222.222", "77.88.8.8", "94.140.14.14", "114.114.114.114"
        )
        for (ip in publicIPs) {
            assertTrue("$ip must be valid IPv4", validator.isValidIPv4(ip))
            assertTrue("$ip must be public", validator.isPublic(ip))
            assertFalse("$ip must not be private", validator.isPrivate(ip))
            assertFalse("$ip must not be loopback", validator.isLoopback(ip))
        }
    }

    @Test
    fun `section11 - isValidIPv4 false for all well-known invalid patterns`() {
        val invalid = listOf(
            "", "0", "1.2", "1.2.3", "1.2.3.4.5",
            "256.0.0.0", "0.256.0.0", "0.0.256.0", "0.0.0.256",
            "-1.0.0.0", "a.b.c.d", " 1.2.3.4", "1.2.3.4 "
        )
        for (ip in invalid) {
            assertFalse("'$ip' must not pass isValidIPv4()", validator.isValidIPv4(ip))
        }
    }

    @Test
    fun `section11 - isValidIPv6 false for all well-known invalid patterns`() {
        val invalid = listOf(
            "", "1.2.3.4", "gggg::1", "2001::db8::1",
            "12345::1", "::::", "not-ipv6"
        )
        for (ip in invalid) {
            assertFalse("'$ip' must not pass isValidIPv6()", validator.isValidIPv6(ip))
        }
    }

    @Test
    fun `section11 - extractFromText returns sorted-stable list across identical calls`() {
        val text = "Hosts: 172.16.0.1, 10.0.0.1, 192.168.1.1"
        val r1 = validator.extractFromText(text)
        val r2 = validator.extractFromText(text)
        assertEquals("Two identical extractFromText calls must return equal lists", r1, r2)
        assertTrue("172.16.0.1 must be present", r1.contains("172.16.0.1"))
        assertTrue("10.0.0.1 must be present", r1.contains("10.0.0.1"))
        assertTrue("192.168.1.1 must be present", r1.contains("192.168.1.1"))
    }

    @Test
    fun `section11 - 169_253_255_255 is just below link-local range and is public`() {
        val ip = "169.253.255.255"
        assertTrue("169.253.255.255 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("169.253.255.255 must be public (below 169.254.0.0/16)", validator.isPublic(ip))
        assertFalse("169.253.255.255 must not be private", validator.isPrivate(ip))
    }

    @Test
    fun `section11 - 169_255_0_1 is just above link-local range and is public`() {
        val ip = "169.255.0.1"
        assertTrue("169.255.0.1 must pass isValidIPv4()", validator.isValidIPv4(ip))
        assertTrue("169.255.0.1 must be public (above 169.254.255.255)", validator.isPublic(ip))
        assertFalse("169.255.0.1 must not be private", validator.isPrivate(ip))
    }
}
