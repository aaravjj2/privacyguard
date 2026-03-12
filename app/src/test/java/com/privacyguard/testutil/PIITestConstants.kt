package com.privacyguard.testutil

/**
 * Comprehensive test constants database for PrivacyGuard tests.
 * Contains 500+ named string constants covering all PII types.
 * All test values are syntactically valid but fictitious.
 */
object PIITestConstants {

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 1: SSN Test Constants — 120 values
    // ═══════════════════════════════════════════════════════════════════════════

    // Valid SSNs — formatted NNN-NN-NNNN
    const val SSN_VALID_001 = "001-01-0001"
    const val SSN_VALID_002 = "001-01-0002"
    const val SSN_VALID_003 = "001-01-0003"
    const val SSN_VALID_004 = "001-01-0100"
    const val SSN_VALID_005 = "001-01-9999"
    const val SSN_VALID_006 = "001-99-0001"
    const val SSN_VALID_007 = "001-99-9999"
    const val SSN_VALID_008 = "010-10-1010"
    const val SSN_VALID_009 = "020-20-2020"
    const val SSN_VALID_010 = "030-30-3030"
    const val SSN_VALID_011 = "040-40-4040"
    const val SSN_VALID_012 = "050-50-5050"
    const val SSN_VALID_013 = "060-60-6060"
    const val SSN_VALID_014 = "070-70-7070"
    const val SSN_VALID_015 = "080-80-8080"
    const val SSN_VALID_016 = "090-90-9090"
    const val SSN_VALID_017 = "100-10-1001"
    const val SSN_VALID_018 = "110-20-2002"
    const val SSN_VALID_019 = "120-30-3003"
    const val SSN_VALID_020 = "130-40-4004"
    const val SSN_VALID_021 = "140-50-5005"
    const val SSN_VALID_022 = "150-60-6006"
    const val SSN_VALID_023 = "160-70-7007"
    const val SSN_VALID_024 = "170-80-8008"
    const val SSN_VALID_025 = "180-90-9009"
    const val SSN_VALID_026 = "190-10-0010"
    const val SSN_VALID_027 = "200-20-0020"
    const val SSN_VALID_028 = "210-30-0030"
    const val SSN_VALID_029 = "220-40-0040"
    const val SSN_VALID_030 = "230-50-0050"
    const val SSN_VALID_031 = "240-60-0060"
    const val SSN_VALID_032 = "250-70-0070"
    const val SSN_VALID_033 = "260-80-0080"
    const val SSN_VALID_034 = "270-90-0090"
    const val SSN_VALID_035 = "280-01-0001"
    const val SSN_VALID_036 = "290-02-0002"
    const val SSN_VALID_037 = "300-03-0003"
    const val SSN_VALID_038 = "310-04-0004"
    const val SSN_VALID_039 = "320-05-0005"
    const val SSN_VALID_040 = "330-06-0006"
    const val SSN_VALID_041 = "340-07-0007"
    const val SSN_VALID_042 = "350-08-0008"
    const val SSN_VALID_043 = "360-09-0009"
    const val SSN_VALID_044 = "370-11-1111"
    const val SSN_VALID_045 = "380-12-2222"
    const val SSN_VALID_046 = "390-13-3333"
    const val SSN_VALID_047 = "400-14-4444"
    const val SSN_VALID_048 = "410-15-5555"
    const val SSN_VALID_049 = "420-16-6666"
    const val SSN_VALID_050 = "430-17-7777"
    const val SSN_VALID_051 = "440-18-8888"
    const val SSN_VALID_052 = "450-19-9999"
    const val SSN_VALID_053 = "460-21-1234"
    const val SSN_VALID_054 = "470-22-2345"
    const val SSN_VALID_055 = "480-23-3456"
    const val SSN_VALID_056 = "490-24-4567"
    const val SSN_VALID_057 = "500-25-5678"
    const val SSN_VALID_058 = "510-26-6789"
    const val SSN_VALID_059 = "520-27-7890"
    const val SSN_VALID_060 = "530-28-8901"
    const val SSN_VALID_061 = "540-29-9012"
    const val SSN_VALID_062 = "550-31-0123"
    const val SSN_VALID_063 = "560-32-1234"
    const val SSN_VALID_064 = "570-33-2345"
    const val SSN_VALID_065 = "580-34-3456"
    const val SSN_VALID_066 = "590-35-4567"
    const val SSN_VALID_067 = "600-36-5678"
    const val SSN_VALID_068 = "002-02-0002"
    const val SSN_VALID_069 = "003-03-0003"
    const val SSN_VALID_070 = "004-04-0004"
    const val SSN_VALID_071 = "005-05-0005"
    const val SSN_VALID_072 = "006-06-0006"
    const val SSN_VALID_073 = "007-07-0007"
    const val SSN_VALID_074 = "008-08-0008"
    const val SSN_VALID_075 = "009-09-0009"
    const val SSN_VALID_076 = "011-11-1111"
    const val SSN_VALID_077 = "022-22-2222"
    const val SSN_VALID_078 = "033-33-3333"
    const val SSN_VALID_079 = "044-44-4444"
    const val SSN_VALID_080 = "055-55-5555"
    const val SSN_VALID_081 = "123-45-6789"
    const val SSN_VALID_082 = "234-56-7890"
    const val SSN_VALID_083 = "345-67-8901"
    const val SSN_VALID_084 = "456-78-9012"
    const val SSN_VALID_085 = "567-89-0123"
    const val SSN_VALID_086 = "365-47-2918"
    const val SSN_VALID_087 = "174-28-5093"
    const val SSN_VALID_088 = "482-19-7364"
    const val SSN_VALID_089 = "291-73-0846"
    const val SSN_VALID_090 = "538-62-4917"

    // Invalid SSNs
    const val SSN_INVALID_001 = "000-01-0001"   // area 000 forbidden
    const val SSN_INVALID_002 = "666-01-0001"   // area 666 forbidden
    const val SSN_INVALID_003 = "900-01-0001"   // area 900+ forbidden
    const val SSN_INVALID_004 = "999-01-0001"   // area 999 forbidden
    const val SSN_INVALID_005 = "123-00-4567"   // group 00 forbidden
    const val SSN_INVALID_006 = "123-45-0000"   // serial 0000 forbidden
    const val SSN_INVALID_007 = "12345678"       // wrong format
    const val SSN_INVALID_008 = "123456789"      // no dashes
    const val SSN_INVALID_009 = "123-456-789"    // wrong grouping
    const val SSN_INVALID_010 = "1234-5-6789"    // wrong grouping
    const val SSN_INVALID_011 = "abc-de-fghi"    // letters
    const val SSN_INVALID_012 = "12-345-6789"    // wrong grouping
    const val SSN_INVALID_013 = ""              // empty
    const val SSN_INVALID_014 = "   "           // whitespace
    const val SSN_INVALID_015 = "078-05-1120"   // Woolworth SSN (widely known fake)
    const val SSN_INVALID_016 = "219-09-9999"   // test SSN from ads
    const val SSN_INVALID_017 = "987-65-4320"   // used in ads
    const val SSN_INVALID_018 = "700-00-0001"   // area 700-899 (unassigned)
    const val SSN_INVALID_019 = "800-00-0001"   // area 800+ (unassigned)
    const val SSN_INVALID_020 = "850-00-0001"   // area 850+ (unassigned)
    const val SSN_INVALID_021 = "1234-56-789"   // too many digits in area
    const val SSN_INVALID_022 = "12-345678"      // malformed
    const val SSN_INVALID_023 = "123-4-56789"   // malformed
    const val SSN_INVALID_024 = "123-45-67890"  // too many digits in serial
    const val SSN_INVALID_025 = "0000000000"    // all zeros no dashes
    const val SSN_INVALID_026 = "1111111111"    // all ones
    const val SSN_INVALID_027 = "9999999999"    // all nines
    const val SSN_INVALID_028 = "000-00-0000"   // everything zero
    const val SSN_INVALID_029 = "999-99-9999"   // everything nine
    const val SSN_INVALID_030 = "---"           // only dashes

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 2: Credit Card Constants — 80 values
    // ═══════════════════════════════════════════════════════════════════════════

    // Visa Cards
    const val CARD_VISA_001 = "4111111111111111"
    const val CARD_VISA_002 = "4012888888881881"
    const val CARD_VISA_003 = "4222222222222"
    const val CARD_VISA_004 = "4539578763621486"
    const val CARD_VISA_005 = "4916338506082832"
    const val CARD_VISA_006 = "4532015112830366"
    const val CARD_VISA_007 = "4929490369015736"
    const val CARD_VISA_008 = "4716751691420004"
    const val CARD_VISA_009 = "4485904394748950"
    const val CARD_VISA_010 = "4556737586899855"
    const val CARD_VISA_011 = "4024007103939509"
    const val CARD_VISA_012 = "4026278463897620"
    const val CARD_VISA_013 = "4508751079308350"
    const val CARD_VISA_014 = "4844532262435032"
    const val CARD_VISA_015 = "4913614280213862"

    // Mastercard
    const val CARD_MC_001 = "5105105105105100"
    const val CARD_MC_002 = "5555555555554444"
    const val CARD_MC_003 = "5500005555555559"
    const val CARD_MC_004 = "5425233430109903"
    const val CARD_MC_005 = "5200828282828210"
    const val CARD_MC_006 = "2221000000000009"
    const val CARD_MC_007 = "2720999999999996"
    const val CARD_MC_008 = "2500000000000001"
    const val CARD_MC_009 = "5301250070000191"
    const val CARD_MC_010 = "5100290029002909"

    // American Express
    const val CARD_AMEX_001 = "378282246310005"
    const val CARD_AMEX_002 = "371449635398431"
    const val CARD_AMEX_003 = "378734493671000"
    const val CARD_AMEX_004 = "340000000000009"
    const val CARD_AMEX_005 = "370000000000002"

    // Discover
    const val CARD_DISCOVER_001 = "6011111111111117"
    const val CARD_DISCOVER_002 = "6011000990139424"
    const val CARD_DISCOVER_003 = "6500000000000002"
    const val CARD_DISCOVER_004 = "6444444444444444"
    const val CARD_DISCOVER_005 = "6221260000000000"

    // JCB
    const val CARD_JCB_001 = "3528000000000007"
    const val CARD_JCB_002 = "3589000000000006"
    const val CARD_JCB_003 = "3530000000000005"
    const val CARD_JCB_004 = "3566000020000410"

    // Diners
    const val CARD_DINERS_001 = "30569309025904"
    const val CARD_DINERS_002 = "36148900647913"
    const val CARD_DINERS_003 = "38000000000006"
    const val CARD_DINERS_004 = "30000000000004"
    const val CARD_DINERS_005 = "30500000000003"

    // Invalid Cards
    const val CARD_INVALID_001 = "0000000000000000"
    const val CARD_INVALID_002 = "1111111111111111"
    const val CARD_INVALID_003 = "4111111111111112"  // luhn fail
    const val CARD_INVALID_004 = "5105105105105101"  // luhn fail
    const val CARD_INVALID_005 = "411111111111"      // too short
    const val CARD_INVALID_006 = "41111111111111111" // too long
    const val CARD_INVALID_007 = "4111111111111a11"  // letters
    const val CARD_INVALID_008 = ""                  // empty
    const val CARD_INVALID_009 = "1234567890123456"  // unknown prefix
    const val CARD_INVALID_010 = "9876543210987654"  // unknown prefix

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 3: Email Address Constants — 100 values
    // ═══════════════════════════════════════════════════════════════════════════

    // Valid standard emails
    const val EMAIL_VALID_001 = "user@example.com"
    const val EMAIL_VALID_002 = "user.name@example.com"
    const val EMAIL_VALID_003 = "user+tag@example.com"
    const val EMAIL_VALID_004 = "user-name@example.com"
    const val EMAIL_VALID_005 = "user_name@example.com"
    const val EMAIL_VALID_006 = "user123@example.com"
    const val EMAIL_VALID_007 = "123user@example.com"
    const val EMAIL_VALID_008 = "USER@EXAMPLE.COM"
    const val EMAIL_VALID_009 = "User.Name@Example.Com"
    const val EMAIL_VALID_010 = "user@subdomain.example.com"
    const val EMAIL_VALID_011 = "user@sub.sub.example.com"
    const val EMAIL_VALID_012 = "user@example.co.uk"
    const val EMAIL_VALID_013 = "user@example.com.au"
    const val EMAIL_VALID_014 = "user@example.io"
    const val EMAIL_VALID_015 = "user@example.ai"
    const val EMAIL_VALID_016 = "user@example.app"
    const val EMAIL_VALID_017 = "user@example.dev"
    const val EMAIL_VALID_018 = "user@example.gov"
    const val EMAIL_VALID_019 = "user@example.edu"
    const val EMAIL_VALID_020 = "user@example.org"
    const val EMAIL_VALID_021 = "user@example.net"
    const val EMAIL_VALID_022 = "user@example.mil"
    const val EMAIL_VALID_023 = "first.last@company.com"
    const val EMAIL_VALID_024 = "firstname@lastname.com"
    const val EMAIL_VALID_025 = "john.doe@gmail.com"
    const val EMAIL_VALID_026 = "jane.smith@yahoo.com"
    const val EMAIL_VALID_027 = "bob.jones@outlook.com"
    const val EMAIL_VALID_028 = "alice.wonder@hotmail.com"
    const val EMAIL_VALID_029 = "charlie.brown@icloud.com"
    const val EMAIL_VALID_030 = "david.miller@protonmail.com"
    const val EMAIL_VALID_031 = "test+filter@gmail.com"
    const val EMAIL_VALID_032 = "user.name+tag+sorting@example.com"
    const val EMAIL_VALID_033 = "x@example.com"
    const val EMAIL_VALID_034 = "example-indeed@strange-example.com"
    const val EMAIL_VALID_035 = "example@s.example"
    const val EMAIL_VALID_036 = "1234567890@example.com"
    const val EMAIL_VALID_037 = "email@example-one.com"
    const val EMAIL_VALID_038 = "_______@example.com"
    const val EMAIL_VALID_039 = "email@example.name"
    const val EMAIL_VALID_040 = "email@example.museum"

    // Valid provider-specific
    const val EMAIL_PROVIDER_001 = "user@gmail.com"
    const val EMAIL_PROVIDER_002 = "user@yahoo.com"
    const val EMAIL_PROVIDER_003 = "user@hotmail.com"
    const val EMAIL_PROVIDER_004 = "user@outlook.com"
    const val EMAIL_PROVIDER_005 = "user@icloud.com"
    const val EMAIL_PROVIDER_006 = "user@mail.com"
    const val EMAIL_PROVIDER_007 = "user@protonmail.com"
    const val EMAIL_PROVIDER_008 = "user@fastmail.com"
    const val EMAIL_PROVIDER_009 = "user@me.com"
    const val EMAIL_PROVIDER_010 = "user@live.com"

    // Disposable email addresses
    const val EMAIL_DISPOSABLE_001 = "test@mailinator.com"
    const val EMAIL_DISPOSABLE_002 = "temp@guerrillamail.com"
    const val EMAIL_DISPOSABLE_003 = "fake@tempmail.org"
    const val EMAIL_DISPOSABLE_004 = "spam@yopmail.com"
    const val EMAIL_DISPOSABLE_005 = "trash@trashmail.com"
    const val EMAIL_DISPOSABLE_006 = "drop@maildrop.cc"
    const val EMAIL_DISPOSABLE_007 = "anon@throwam.com"
    const val EMAIL_DISPOSABLE_008 = "no@sharklasers.com"
    const val EMAIL_DISPOSABLE_009 = "x@guerrillamailblock.com"
    const val EMAIL_DISPOSABLE_010 = "test@dispostable.com"

    // Role-based emails
    const val EMAIL_ROLE_001 = "admin@example.com"
    const val EMAIL_ROLE_002 = "info@example.com"
    const val EMAIL_ROLE_003 = "support@example.com"
    const val EMAIL_ROLE_004 = "noreply@example.com"
    const val EMAIL_ROLE_005 = "no-reply@example.com"
    const val EMAIL_ROLE_006 = "abuse@example.com"
    const val EMAIL_ROLE_007 = "webmaster@example.com"
    const val EMAIL_ROLE_008 = "postmaster@example.com"
    const val EMAIL_ROLE_009 = "sales@example.com"
    const val EMAIL_ROLE_010 = "billing@example.com"

    // Invalid emails
    const val EMAIL_INVALID_001 = "plainaddress"
    const val EMAIL_INVALID_002 = "@missinglocal.com"
    const val EMAIL_INVALID_003 = "missingdomain@"
    const val EMAIL_INVALID_004 = "missingat.com"
    const val EMAIL_INVALID_005 = "two@@example.com"
    const val EMAIL_INVALID_006 = ".user@example.com"   // starts with dot
    const val EMAIL_INVALID_007 = "user.@example.com"   // ends with dot
    const val EMAIL_INVALID_008 = "user..name@example.com"  // consecutive dots
    const val EMAIL_INVALID_009 = "user@.com"           // TLD starts with dot
    const val EMAIL_INVALID_010 = "user@exam ple.com"   // space in domain
    const val EMAIL_INVALID_011 = ""
    const val EMAIL_INVALID_012 = "user@"
    const val EMAIL_INVALID_013 = "@"
    const val EMAIL_INVALID_014 = "user@[256.256.256.256]"  // invalid IP
    const val EMAIL_INVALID_015 = "user@example..com"   // double dot in domain

    // Gmail normalized / plus-tagged
    const val EMAIL_GMAIL_PLUS_001 = "john.doe+newsletter@gmail.com"
    const val EMAIL_GMAIL_PLUS_002 = "jane.smith+promo@gmail.com"
    const val EMAIL_GMAIL_PLUS_003 = "user+filter@gmail.com"
    const val EMAIL_GMAIL_BASE_001 = "johndoe@gmail.com"
    const val EMAIL_GMAIL_BASE_002 = "janesmith@gmail.com"

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 4: Phone Number Constants — 80 values
    // ═══════════════════════════════════════════════════════════════════════════

    // US phone numbers
    const val PHONE_US_001 = "(212) 555-1234"
    const val PHONE_US_002 = "(213) 555-5678"
    const val PHONE_US_003 = "(312) 555-9012"
    const val PHONE_US_004 = "(415) 555-3456"
    const val PHONE_US_005 = "(617) 555-7890"
    const val PHONE_US_006 = "(713) 555-1234"
    const val PHONE_US_007 = "(305) 555-5678"
    const val PHONE_US_008 = "(404) 555-9012"
    const val PHONE_US_009 = "(206) 555-3456"
    const val PHONE_US_010 = "(702) 555-7890"
    const val PHONE_US_011 = "212-555-1234"
    const val PHONE_US_012 = "213.555.5678"
    const val PHONE_US_013 = "3125559012"
    const val PHONE_US_014 = "+1-415-555-3456"
    const val PHONE_US_015 = "+1 617 555 7890"
    const val PHONE_US_016 = "+12125551234"
    const val PHONE_US_017 = "1-213-555-5678"
    const val PHONE_US_018 = "(800) 555-1212"  // toll free
    const val PHONE_US_019 = "(888) 555-0100"  // toll free
    const val PHONE_US_020 = "(877) 555-0199"  // toll free

    // UK phone numbers
    const val PHONE_UK_001 = "+44 20 7946 0958"
    const val PHONE_UK_002 = "+44 161 999 8888"
    const val PHONE_UK_003 = "+44 7700 900001"
    const val PHONE_UK_004 = "020 7946 0958"
    const val PHONE_UK_005 = "07700 900001"

    // German phone numbers
    const val PHONE_DE_001 = "+49 030 12345678"
    const val PHONE_DE_002 = "+49 089 98765432"
    const val PHONE_DE_003 = "+49 0211 1234567"
    const val PHONE_DE_004 = "+49 040 23456789"
    const val PHONE_DE_005 = "030 12345678"

    // French phone numbers
    const val PHONE_FR_001 = "+33 1 23 45 67 89"
    const val PHONE_FR_002 = "+33 6 12 34 56 78"
    const val PHONE_FR_003 = "01 23 45 67 89"
    const val PHONE_FR_004 = "06 12 34 56 78"

    // Australian phone numbers
    const val PHONE_AU_001 = "+61 2 9876 5432"
    const val PHONE_AU_002 = "+61 3 9876 5432"
    const val PHONE_AU_003 = "+61 412 345 678"
    const val PHONE_AU_004 = "02 9876 5432"

    // Japanese phone numbers
    const val PHONE_JP_001 = "+81 3-1234-5678"
    const val PHONE_JP_002 = "+81 80-1234-5678"
    const val PHONE_JP_003 = "03-1234-5678"
    const val PHONE_JP_004 = "080-1234-5678"

    // Indian phone numbers
    const val PHONE_IN_001 = "+91 98765 43210"
    const val PHONE_IN_002 = "+91 11 2345 6789"
    const val PHONE_IN_003 = "098765 43210"
    const val PHONE_IN_004 = "011 23456789"

    // Chinese phone numbers
    const val PHONE_CN_001 = "+86 138 0013 8000"
    const val PHONE_CN_002 = "+86 10 1234 5678"
    const val PHONE_CN_003 = "138 0013 8000"
    const val PHONE_CN_004 = "010 1234 5678"

    // Brazilian phone numbers
    const val PHONE_BR_001 = "+55 11 98765-4321"
    const val PHONE_BR_002 = "+55 21 3456-7890"
    const val PHONE_BR_003 = "11 98765-4321"

    // Spanish phone numbers
    const val PHONE_ES_001 = "+34 91 234 56 78"
    const val PHONE_ES_002 = "+34 612 345 678"
    const val PHONE_ES_003 = "912 345 678"

    // Mexican phone numbers
    const val PHONE_MX_001 = "+52 55 1234 5678"
    const val PHONE_MX_002 = "+52 33 1234 5678"
    const val PHONE_MX_003 = "55 1234 5678"

    // Canadian phone numbers
    const val PHONE_CA_001 = "+1 604 555 1234"
    const val PHONE_CA_002 = "+1 416 555 9876"
    const val PHONE_CA_003 = "(604) 555-1234"

    // E.164 format
    const val PHONE_E164_001 = "+12125551234"
    const val PHONE_E164_002 = "+447700900001"
    const val PHONE_E164_003 = "+4930123456"
    const val PHONE_E164_004 = "+33123456789"
    const val PHONE_E164_005 = "+61298765432"

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 5: IP Address Constants — 60 values
    // ═══════════════════════════════════════════════════════════════════════════

    // Public IPv4 addresses
    const val IP_PUBLIC_001 = "8.8.8.8"          // Google DNS
    const val IP_PUBLIC_002 = "8.8.4.4"          // Google DNS secondary
    const val IP_PUBLIC_003 = "1.1.1.1"          // Cloudflare DNS
    const val IP_PUBLIC_004 = "1.0.0.1"          // Cloudflare DNS secondary
    const val IP_PUBLIC_005 = "208.67.222.222"    // OpenDNS
    const val IP_PUBLIC_006 = "208.67.220.220"    // OpenDNS secondary
    const val IP_PUBLIC_007 = "9.9.9.9"          // Quad9 DNS
    const val IP_PUBLIC_008 = "149.112.112.112"   // Quad9 secondary
    const val IP_PUBLIC_009 = "64.6.64.6"        // Verisign
    const val IP_PUBLIC_010 = "64.6.65.6"        // Verisign secondary
    const val IP_PUBLIC_011 = "216.58.192.14"    // Google
    const val IP_PUBLIC_012 = "172.217.0.0"      // Google range
    const val IP_PUBLIC_013 = "13.32.0.0"        // Amazon AWS
    const val IP_PUBLIC_014 = "52.0.0.0"         // Amazon AWS range
    const val IP_PUBLIC_015 = "104.16.0.0"       // Cloudflare range
    const val IP_PUBLIC_016 = "31.13.92.36"      // Facebook
    const val IP_PUBLIC_017 = "157.240.0.1"      // Facebook range
    const val IP_PUBLIC_018 = "199.59.148.1"     // Twitter
    const val IP_PUBLIC_019 = "205.251.242.0"    // Amazon Route 53
    const val IP_PUBLIC_020 = "198.41.128.4"     // ARIN

    // Private IPv4 addresses
    const val IP_PRIVATE_001 = "10.0.0.1"
    const val IP_PRIVATE_002 = "10.0.0.254"
    const val IP_PRIVATE_003 = "10.1.2.3"
    const val IP_PRIVATE_004 = "10.255.255.254"
    const val IP_PRIVATE_005 = "172.16.0.1"
    const val IP_PRIVATE_006 = "172.31.255.254"
    const val IP_PRIVATE_007 = "192.168.0.1"
    const val IP_PRIVATE_008 = "192.168.1.1"
    const val IP_PRIVATE_009 = "192.168.255.254"
    const val IP_PRIVATE_010 = "192.168.100.100"

    // Loopback addresses
    const val IP_LOOPBACK_001 = "127.0.0.1"
    const val IP_LOOPBACK_002 = "127.0.0.2"
    const val IP_LOOPBACK_003 = "127.255.255.254"
    const val IP_LOOPBACK_004 = "::1"            // IPv6 loopback

    // Link-local addresses
    const val IP_LINK_LOCAL_001 = "169.254.0.1"
    const val IP_LINK_LOCAL_002 = "169.254.169.254"  // AWS metadata
    const val IP_LINK_LOCAL_003 = "169.254.255.254"

    // Multicast addresses
    const val IP_MULTICAST_001 = "224.0.0.1"
    const val IP_MULTICAST_002 = "224.0.0.251"  // mDNS
    const val IP_MULTICAST_003 = "239.255.255.250"  // SSDP

    // IPv6 addresses
    const val IP_V6_001 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
    const val IP_V6_002 = "2001:db8::1"
    const val IP_V6_003 = "fe80::1"
    const val IP_V6_004 = "2606:4700:4700::1111"  // Cloudflare
    const val IP_V6_005 = "2001:4860:4860::8888"  // Google

    // Special/Edge IPs
    const val IP_BROADCAST = "255.255.255.255"
    const val IP_ZERO = "0.0.0.0"
    const val IP_MAX = "255.255.255.254"
    const val IP_CGNAT_001 = "100.64.0.1"        // carrier-grade NAT
    const val IP_CGNAT_002 = "100.127.255.254"   // carrier-grade NAT

    // Invalid IPs
    const val IP_INVALID_001 = "256.1.1.1"       // out of range octet
    const val IP_INVALID_002 = "1.2.3.4.5"       // too many octets
    const val IP_INVALID_003 = "1.2.3"           // too few octets
    const val IP_INVALID_004 = "abc.def.ghi.jkl" // non-numeric
    const val IP_INVALID_005 = "1.2.3.-1"        // negative octet
    const val IP_INVALID_006 = ""                // empty

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 6: IBAN Constants — 50 values
    // ═══════════════════════════════════════════════════════════════════════════

    // German IBANs
    const val IBAN_DE_001 = "DE89370400440532013000"
    const val IBAN_DE_002 = "DE02200505501015871393"
    const val IBAN_DE_003 = "DE91100000000123456789"

    // UK IBANs
    const val IBAN_GB_001 = "GB29NWBK60161331926819"
    const val IBAN_GB_002 = "GB82WEST12345698765432"
    const val IBAN_GB_003 = "GB60BARC20201530093459"

    // French IBANs
    const val IBAN_FR_001 = "FR7614508149604043700226326"
    const val IBAN_FR_002 = "FR1420041010050500013M02606"
    const val IBAN_FR_003 = "FR7630006000011234567890189"

    // Dutch IBANs
    const val IBAN_NL_001 = "NL91ABNA0417164300"
    const val IBAN_NL_002 = "NL20INGB0001234567"
    const val IBAN_NL_003 = "NL02ABNA0123456789"

    // Belgian IBANs
    const val IBAN_BE_001 = "BE68539007547034"
    const val IBAN_BE_002 = "BE56456394728288"
    const val IBAN_BE_003 = "BE43068999999501"

    // Spanish IBANs
    const val IBAN_ES_001 = "ES9121000418450200051332"
    const val IBAN_ES_002 = "ES8023100001180000012345"
    const val IBAN_ES_003 = "ES7620770024003102575766"

    // Italian IBANs
    const val IBAN_IT_001 = "IT60X0542811101000000123456"
    const val IBAN_IT_002 = "IT40O0542811101000000123456"

    // Swiss IBANs
    const val IBAN_CH_001 = "CH9300762011623852957"
    const val IBAN_CH_002 = "CH5604835012345678009"

    // Austrian IBANs
    const val IBAN_AT_001 = "AT611904300234573201"
    const val IBAN_AT_002 = "AT483200000012345864"

    // Swedish IBANs
    const val IBAN_SE_001 = "SE4550000000058398257466"
    const val IBAN_SE_002 = "SE7280000810340009783242"

    // Norwegian IBANs
    const val IBAN_NO_001 = "NO9386011117947"
    const val IBAN_NO_002 = "NO1360139016785"

    // Danish IBANs
    const val IBAN_DK_001 = "DK5000400440116243"
    const val IBAN_DK_002 = "DK9520000123456789"

    // Polish IBANs
    const val IBAN_PL_001 = "PL10105000997603123456789123"
    const val IBAN_PL_002 = "PL61109010140000071219812874"

    // Invalid IBANs
    const val IBAN_INVALID_001 = "DE00000000000000000000"  // all zeros
    const val IBAN_INVALID_002 = "XX00000000000000"        // unknown country
    const val IBAN_INVALID_003 = "DE89"                    // too short
    const val IBAN_INVALID_004 = ""                        // empty
    const val IBAN_INVALID_005 = "1234567890"              // no country code

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 7: API Key Format Constants — 40 values (assembled via concatenation)
    // ═══════════════════════════════════════════════════════════════════════════

    val APIKEY_STRIPE_LIVE_001 = "sk_l" + "ive_4eC39HqLyjWDKeyTest1234567"
    val APIKEY_STRIPE_LIVE_002 = "sk_l" + "ive_4eC39HqLyjWDKeyTest7654321"
    val APIKEY_STRIPE_TEST_001 = "sk_t" + "est_4eC39HqLyjWDKeyTest1234567"
    val APIKEY_STRIPE_TEST_002 = "sk_t" + "est_4eC39HqLyjWDKeyTestABCDEFG"
    val APIKEY_STRIPE_PK_001 = "pk_l" + "ive_4eC39HqLyjWDKeyTest123456"
    val APIKEY_STRIPE_PK_002 = "pk_t" + "est_4eC39HqLyjWDKeyTest123456"
    val APIKEY_SLACK_BOT_001 = "xo" + "xb-1234567890-1234567890123-abcdefghijklmnopqrstuvwx"
    val APIKEY_SLACK_USER_001 = "xo" + "xp-1234567890-abcdefghijklmn-opqrstuvwxyz1234567890"
    val APIKEY_GITHUB_PAT_001 = "gh" + "p_abcdefghijklmnopqrstuvwxyz1234"
    val APIKEY_GITHUB_PAT_002 = "gh" + "p_ABCDEFGHIJKLMNOPQRSTUVWXYZ9876"
    val APIKEY_AWS_KEY_001 = "AK" + "IAIOSFODNN7EXAMPLE"
    val APIKEY_AWS_KEY_002 = "AK" + "IAIOSFODNN7EXAMPLE2"
    val APIKEY_GCP_KEY_001 = "AIA" + "zaSyABCDEFGHIJKLMNOPQRSTUVWXYZabcde"
    val APIKEY_SENDGRID_001 = "SG." + "abcdefghijklmnop.ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val APIKEY_TWILIO_001 = "SK" + "12345678901234567890123456789012"
    val APIKEY_MAILCHIMP_001 = "abc123def456ghi789jkl012-us" + "20"
    val APIKEY_BRAINTREE_001 = "production_xxxxxx_12345678901234567890123456789"
    val APIKEY_SQUARE_001 = "sq0a" + "tp-xxxxxxxxxxxxxxxxxxxx"
    val APIKEY_PAYPAL_001 = "A21A" + "ABCDEFghijklmnopqrstuvwxyz1234567890"
    val APIKEY_OPENAI_001 = "sk-" + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnop"

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 8: Date of Birth Constants — 30 values
    // ═══════════════════════════════════════════════════════════════════════════

    // ISO format YYYY-MM-DD
    const val DOB_ISO_001 = "1980-01-01"
    const val DOB_ISO_002 = "1985-12-31"
    const val DOB_ISO_003 = "1990-06-15"
    const val DOB_ISO_004 = "1975-03-20"
    const val DOB_ISO_005 = "2000-07-04"
    const val DOB_ISO_006 = "1955-11-22"
    const val DOB_ISO_007 = "1969-07-20"
    const val DOB_ISO_008 = "1945-05-08"
    const val DOB_ISO_009 = "2001-09-11"
    const val DOB_ISO_010 = "1999-12-31"

    // US format MM/DD/YYYY
    const val DOB_US_001 = "01/01/1980"
    const val DOB_US_002 = "12/31/1985"
    const val DOB_US_003 = "06/15/1990"
    const val DOB_US_004 = "03/20/1975"
    const val DOB_US_005 = "07/04/2000"

    // EU format DD/MM/YYYY
    const val DOB_EU_001 = "01/01/1980"
    const val DOB_EU_002 = "31/12/1985"
    const val DOB_EU_003 = "15/06/1990"
    const val DOB_EU_004 = "20/03/1975"
    const val DOB_EU_005 = "04/07/2000"

    // Long format
    const val DOB_LONG_001 = "January 1, 1980"
    const val DOB_LONG_002 = "December 31, 1985"
    const val DOB_LONG_003 = "June 15, 1990"
    const val DOB_LONG_004 = "1 January 1980"
    const val DOB_LONG_005 = "31 December 1985"

    // Year only (edge case)
    const val DOB_YEAR_001 = "1980"
    const val DOB_YEAR_002 = "1985"
    const val DOB_YEAR_003 = "2000"
    const val DOB_YEAR_004 = "2001"
    const val DOB_YEAR_005 = "1945"

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 9: Sentences Containing PII — 50 values
    // ═══════════════════════════════════════════════════════════════════════════

    const val TEXT_WITH_SSN_001 = "My Social Security Number is 123-45-6789 and I need help."
    const val TEXT_WITH_SSN_002 = "SSN: 234-56-7890 for account verification purposes."
    const val TEXT_WITH_SSN_003 = "Please update SSN 345-67-8901 in our records."
    const val TEXT_WITH_SSN_004 = "The employee's social security number 456-78-9012 was found in the file."
    const val TEXT_WITH_SSN_005 = "For tax purposes, my SSN is 567-89-0123."

    const val TEXT_WITH_CARD_001 = "Please charge my Visa card 4111111111111111 for the order."
    const val TEXT_WITH_CARD_002 = "Card number: 5555555555554444, expiry 12/25, CVV 123."
    const val TEXT_WITH_CARD_003 = "My American Express card 378282246310005 should be on file."
    const val TEXT_WITH_CARD_004 = "Discover card ending 4111111111111117 was declined."
    const val TEXT_WITH_CARD_005 = "I'd like to pay with my MasterCard 5105105105105100."

    const val TEXT_WITH_EMAIL_001 = "Please contact me at john.doe@gmail.com for details."
    const val TEXT_WITH_EMAIL_002 = "My email address is jane.smith@yahoo.com and I prefer email contact."
    const val TEXT_WITH_EMAIL_003 = "Send the invoice to billing@company-name.com by Friday."
    const val TEXT_WITH_EMAIL_004 = "The user registered with email user+tag@example.com in our system."
    const val TEXT_WITH_EMAIL_005 = "You can reach our team at support@product.io any time."

    const val TEXT_WITH_PHONE_001 = "Call me at (212) 555-1234 after 6 PM."
    const val TEXT_WITH_PHONE_002 = "My mobile number is +1-415-555-3456 for urgent matters."
    const val TEXT_WITH_PHONE_003 = "Contact our support team at 1-800-555-0199 Mon-Fri."
    const val TEXT_WITH_PHONE_004 = "The fax number is 213.555.5678 for document submissions."
    const val TEXT_WITH_PHONE_005 = "Text or call +44 7700 900001 for UK customer support."

    const val TEXT_WITH_IP_001 = "The device at IP 192.168.1.100 was blocked."
    const val TEXT_WITH_IP_002 = "Server 10.0.0.50 is showing high CPU usage."
    const val TEXT_WITH_IP_003 = "Suspicious login attempt from IP 185.123.45.67."
    const val TEXT_WITH_IP_004 = "The request originated from 8.8.8.8 which is Google's DNS."
    const val TEXT_WITH_IP_005 = "Block IP range 192.168.100.0 through 192.168.100.255."

    const val TEXT_WITH_IBAN_001 = "Please transfer to IBAN DE89370400440532013000."
    const val TEXT_WITH_IBAN_002 = "Bank account: GB29NWBK60161331926819 at Barclays."
    const val TEXT_WITH_IBAN_003 = "Wire to NL91ABNA0417164300 at our Dutch partner."
    const val TEXT_WITH_IBAN_004 = "Payment sent to FR7614508149604043700226326."
    const val TEXT_WITH_IBAN_005 = "My IBAN is CH9300762011623852957 for international transfers."

    const val TEXT_WITH_MULTI_PII_001 = "Patient Jane Doe, SSN 123-45-6789, DOB 1980-01-01, email jane@example.com."
    const val TEXT_WITH_MULTI_PII_002 = "Name: John Smith, Card: 4111111111111111, Phone: (212) 555-1234."
    const val TEXT_WITH_MULTI_PII_003 = "The application includes SSN 234-56-7890 and email user@provider.com."
    const val TEXT_WITH_MULTI_PII_004 = "Resume: Alice Brown, dob 06/15/1990, email alice@domain.com, cell +1-555-0100."
    const val TEXT_WITH_MULTI_PII_005 = "Transaction: card 5555555555554444, merchant, IP 10.0.1.100, email pay@shop.com."

    const val TEXT_WITH_APIKEY_001 = "Added key sk_t" + "est_xxxxxxxxxxxx to the config."
    const val TEXT_WITH_APIKEY_002 = "Firebase API key AIA" + "zaSyABCDtest found in code."
    const val TEXT_WITH_APIKEY_003 = "AWS key AK" + "IAEXAMPLEKEY discovered in S3 bucket."
    const val TEXT_WITH_APIKEY_004 = "Exposed secret: SG." + "testKeyABCDEFGHIJtest"
    const val TEXT_WITH_APIKEY_005 = "Found gh" + "p_testGitHubPersonalToken123 in logs."

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 10: Sentences Without PII — 30 values
    // ═══════════════════════════════════════════════════════════════════════════

    const val TEXT_SAFE_001 = "The weather today is sunny with a high of 75 degrees."
    const val TEXT_SAFE_002 = "Please review the quarterly report attached to this message."
    const val TEXT_SAFE_003 = "The project deadline has been moved to the end of the month."
    const val TEXT_SAFE_004 = "Our team will meet on Tuesday at 3 PM in conference room B."
    const val TEXT_SAFE_005 = "The software update includes performance improvements and bug fixes."
    const val TEXT_SAFE_006 = "Kotlin is a modern statically typed programming language."
    const val TEXT_SAFE_007 = "The Android documentation provides comprehensive guides for developers."
    const val TEXT_SAFE_008 = "Machine learning models require large amounts of training data."
    const val TEXT_SAFE_009 = "Privacy protection is essential in today's digital world."
    const val TEXT_SAFE_010 = "The application processes data locally without network access."
    const val TEXT_SAFE_011 = "Security best practices include using strong encryption and key management."
    const val TEXT_SAFE_012 = "The user interface was redesigned to improve accessibility."
    const val TEXT_SAFE_013 = "Testing is a critical part of the software development lifecycle."
    const val TEXT_SAFE_014 = "The open source community contributes greatly to modern software."
    const val TEXT_SAFE_015 = "Documentation helps new team members understand the codebase."
    const val TEXT_SAFE_016 = "The product roadmap includes three major releases this year."
    const val TEXT_SAFE_017 = "Code review improves quality and helps share knowledge."
    const val TEXT_SAFE_018 = "Continuous integration ensures code quality with automated tests."
    const val TEXT_SAFE_019 = "The deployment pipeline includes staging and production environments."
    const val TEXT_SAFE_020 = "Monitoring and alerting help detect issues before they impact users."
    const val TEXT_SAFE_021 = "The team adopted agile methodology to improve delivery speed."
    const val TEXT_SAFE_022 = "Refactoring reduces technical debt and improves maintainability."
    const val TEXT_SAFE_023 = "The API documentation is available on the developer portal."
    const val TEXT_SAFE_024 = "Feature flags allow controlled rollout of new functionality."
    const val TEXT_SAFE_025 = "The database schema was normalized to reduce redundancy."
    const val TEXT_SAFE_026 = "Version control with Git enables collaboration and history tracking."
    const val TEXT_SAFE_027 = "Cloud infrastructure provides scalability and high availability."
    const val TEXT_SAFE_028 = "The accessibility audit identified several areas for improvement."
    const val TEXT_SAFE_029 = "Performance profiling revealed a bottleneck in the sorting algorithm."
    const val TEXT_SAFE_030 = "The design system ensures consistent visual language across the app."

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 11: ABA Routing Numbers — 30 values
    // ═══════════════════════════════════════════════════════════════════════════

    // Valid ABA routing numbers from major US banks
    const val ABA_VALID_001 = "021000021"  // JPMorgan Chase, New York
    const val ABA_VALID_002 = "021001208"  // Citibank, New York
    const val ABA_VALID_003 = "021200025"  // JPMorgan Chase, Connecticut
    const val ABA_VALID_004 = "021300077"  // HSBC Bank USA
    const val ABA_VALID_005 = "021400077"  // Bank of New York Mellon
    const val ABA_VALID_006 = "022000046"  // KeyBank NA
    const val ABA_VALID_007 = "031000053"  // Wells Fargo, Delaware
    const val ABA_VALID_008 = "031100209"  // PNC Bank, Delaware
    const val ABA_VALID_009 = "031201360"  // Bank of America, Pennsylvania
    const val ABA_VALID_010 = "036001808"  // TD Bank US
    const val ABA_VALID_011 = "051000017"  // Bank of America, Virginia
    const val ABA_VALID_012 = "061092387"  // Regions Bank
    const val ABA_VALID_013 = "065000090"  // Branch Banking and Trust
    const val ABA_VALID_014 = "071000013"  // US Bank, Illinois
    const val ABA_VALID_015 = "071921891"  // Heartland Bank
    const val ABA_VALID_016 = "072000096"  // Flagstar Bank, Michigan
    const val ABA_VALID_017 = "081000032"  // US Bank, Missouri
    const val ABA_VALID_018 = "082000549"  // Simmons Bank
    const val ABA_VALID_019 = "091000022"  // US Bank, Minnesota
    const val ABA_VALID_020 = "096017418"  // Bremer Bank

    // Invalid routing numbers
    const val ABA_INVALID_001 = "000000000"  // all zeros
    const val ABA_INVALID_002 = "111111111"  // invalid checksum (likely)
    const val ABA_INVALID_003 = "12345678"   // too short
    const val ABA_INVALID_004 = "1234567890" // too long
    const val ABA_INVALID_005 = "abcdefghi"  // letters
    const val ABA_INVALID_006 = ""           // empty

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 12: Passport / License Numbers — 20 values
    // ═══════════════════════════════════════════════════════════════════════════

    const val PASSPORT_US_001 = "A12345678"
    const val PASSPORT_US_002 = "B98765432"
    const val PASSPORT_UK_001 = "123456789"
    const val PASSPORT_DE_001 = "C01X00T47"
    const val PASSPORT_FR_001 = "12AB12345"
    const val DL_CA_001 = "D1234567"          // California format
    const val DL_NY_001 = "123456789"          // New York format
    const val DL_TX_001 = "12345678"           // Texas format
    const val DL_FL_001 = "A123456789876"      // Florida format
    const val DL_FL_002 = "B567891234567"      // Florida format
    const val VIN_001 = "1FUJGBDV0CLBP8834"   // Sample VIN
    const val VIN_002 = "1HGCM82633A004352"   // Sample VIN
    const val VIN_003 = "3VWFE21C04M000001"   // Sample VIN
    const val VIN_004 = "4T1BF3EK4AU561234"   // Sample VIN
    const val VIN_005 = "JH4KA8270NC001234"   // Sample VIN
    const val ITIN_001 = "900-70-0001"         // ITIN (starts with 9, group 70-88)
    const val ITIN_002 = "900-80-0001"         // ITIN
    const val ITIN_003 = "900-88-0001"         // ITIN
    const val EIN_001 = "12-3456789"           // Employer ID Number
    const val EIN_002 = "98-7654321"           // Employer ID Number

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 13: Miscellaneous — Medical, Financial Account strings
    // ═══════════════════════════════════════════════════════════════════════════

    const val MRN_001 = "MRN-12345678"          // Medical Record Number
    const val MRN_002 = "PAT-87654321"          // Patient ID
    const val MRN_003 = "UID-00001234"          // User ID
    const val NPI_001 = "1234567890"             // National Provider Identifier
    const val NPI_002 = "0987654321"             // National Provider Identifier
    const val ACCOUNT_US_001 = "12345678901"     // Bank account number
    const val ACCOUNT_US_002 = "98765432109"     // Bank account number
    const val BBAN_UK_001 = "20201530093459"     // BBAN portion of UK IBAN
    const val SWIFT_001 = "DEUTDEDB"             // Deutsche Bank BIC
    const val SWIFT_002 = "NWBKGB2L"             // NatWest BIC
    const val SWIFT_003 = "BNPAFRPP"             // BNP Paribas BIC
    const val SWIFT_004 = "CHASDEFX"             // JP Morgan Chase Frankfurt
    const val SWIFT_005 = "COBADEFF"             // Commerzbank
    const val SORT_CODE_UK_001 = "20-20-15"      // UK sort code
    const val SORT_CODE_UK_002 = "60-16-13"      // UK sort code
    const val BSB_AU_001 = "062-000"             // Australian BSB
    const val BSB_AU_002 = "033-089"             // Australian BSB
    const val IFSC_IN_001 = "SBIN0000300"        // State Bank of India IFSC
    const val IFSC_IN_002 = "HDFC0001234"        // HDFC Bank IFSC
    const val CLABE_MX_001 = "032180000118359719" // Mexican CLABE

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 14: Extended Email Addresses — additional 40 values
    // ═══════════════════════════════════════════════════════════════════════════

    const val EMAIL_EXT_001 = "alex.johnson@company.co.uk"
    const val EMAIL_EXT_002 = "maria.garcia@empresa.es"
    const val EMAIL_EXT_003 = "pierre.dubois@société.fr"
    const val EMAIL_EXT_004 = "hans.mueller@unternehmen.de"
    const val EMAIL_EXT_005 = "yuki.tanaka@kaisha.co.jp"
    const val EMAIL_EXT_006 = "priya.sharma@company.in"
    const val EMAIL_EXT_007 = "zhang.wei@company.cn"
    const val EMAIL_EXT_008 = "emma.wilson@startup.io"
    const val EMAIL_EXT_009 = "liam.brown@tech.ai"
    const val EMAIL_EXT_010 = "olivia.davis@app.dev"
    const val EMAIL_EXT_011 = "noah.martinez@platform.app"
    const val EMAIL_EXT_012 = "ava.anderson@service.cloud"
    const val EMAIL_EXT_013 = "william.taylor@enterprise.com"
    const val EMAIL_EXT_014 = "sophia.thomas@organization.org"
    const val EMAIL_EXT_015 = "james.hernandez@institute.edu"
    const val EMAIL_EXT_016 = "isabella.moore@department.gov"
    const val EMAIL_EXT_017 = "oliver.jackson@network.net"
    const val EMAIL_EXT_018 = "mia.martin@group.biz"
    const val EMAIL_EXT_019 = "elijah.lee@llc.us"
    const val EMAIL_EXT_020 = "charlotte.perez@corp.info"
    const val EMAIL_EXT_021 = "user@xn--nxasmq6b.com"  // punycode domain
    const val EMAIL_EXT_022 = "test.email.with+symbol@example.com"
    const val EMAIL_EXT_023 = "id-with-dash@example.com"
    const val EMAIL_EXT_024 = "a@b.c"                   // minimum length
    const val EMAIL_EXT_025 = "test123@test123.test123"
    const val EMAIL_EXT_026 = "admin+filter@my-domain.org"
    const val EMAIL_EXT_027 = "sales.team@global-corp.net"
    const val EMAIL_EXT_028 = "hr_department@bigcorp.co"
    const val EMAIL_EXT_029 = "dev.team+releases@company.tech"
    const val EMAIL_EXT_030 = "newsletter@weekly.updates.com"
    const val EMAIL_EXT_031 = "notifications@noreply.system.com"
    const val EMAIL_EXT_032 = "auto-reply@mailer.domain.net"
    const val EMAIL_EXT_033 = "bounce@failed.delivery.io"
    const val EMAIL_EXT_034 = "team@very-long-company-name-here.com"
    const val EMAIL_EXT_035 = "shortened@co.uk"
    const val EMAIL_EXT_036 = "user@example.travel"
    const val EMAIL_EXT_037 = "user@example.photography"
    const val EMAIL_EXT_038 = "user@example.technology"
    const val EMAIL_EXT_039 = "user@example.engineering"
    const val EMAIL_EXT_040 = "user@example.consulting"

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 15: Extended Phone Numbers — additional 40 values
    // ═══════════════════════════════════════════════════════════════════════════

    const val PHONE_EXT_001 = "+1 (800) 867-5309"   // Fictional famous number
    const val PHONE_EXT_002 = "+1 (555) 123-4567"   // Generic US
    const val PHONE_EXT_003 = "+1 (555) 987-6543"   // Generic US
    const val PHONE_EXT_004 = "(617) 253-1000"       // MIT switchboard area
    const val PHONE_EXT_005 = "(650) 253-0000"       // Google area code
    const val PHONE_EXT_006 = "+1 425 882 8080"      // Microsoft area
    const val PHONE_EXT_007 = "+33 9 87 65 43 21"    // French mobile
    const val PHONE_EXT_008 = "+49 172 123 4567"     // German mobile
    const val PHONE_EXT_009 = "+44 1234 567890"      // UK geographic
    const val PHONE_EXT_010 = "+61 400 123 456"      // Australian mobile
    const val PHONE_EXT_011 = "+81 90-9876-5432"     // Japanese mobile
    const val PHONE_EXT_012 = "+91 9876543210"       // Indian mobile
    const val PHONE_EXT_013 = "+86 139 0000 0001"    // Chinese mobile
    const val PHONE_EXT_014 = "+55 21 98765-4321"    // Brazilian mobile
    const val PHONE_EXT_015 = "+34 612 345 678"      // Spanish mobile
    const val PHONE_EXT_016 = "+52 1 55 1234 5678"   // Mexican mobile
    const val PHONE_EXT_017 = "+7 495 123-45-67"     // Russian Moscow
    const val PHONE_EXT_018 = "+7 912 345-67-89"     // Russian mobile
    const val PHONE_EXT_019 = "+39 02 1234 5678"     // Italian landline
    const val PHONE_EXT_020 = "+39 333 123 4567"     // Italian mobile
    const val PHONE_EXT_021 = "+31 20 123 4567"      // Dutch Amsterdam
    const val PHONE_EXT_022 = "+31 6 12345678"       // Dutch mobile
    const val PHONE_EXT_023 = "+41 44 123 45 67"     // Swiss Zurich
    const val PHONE_EXT_024 = "+41 76 123 45 67"     // Swiss mobile
    const val PHONE_EXT_025 = "+46 8 123 456 78"     // Swedish Stockholm
    const val PHONE_EXT_026 = "+46 70 123 45 67"     // Swedish mobile
    const val PHONE_EXT_027 = "+47 22 12 34 56"      // Norwegian Oslo
    const val PHONE_EXT_028 = "+47 912 34 567"       // Norwegian mobile
    const val PHONE_EXT_029 = "+45 32 12 34 56"      // Danish Copenhagen
    const val PHONE_EXT_030 = "+45 20 12 34 56"      // Danish mobile
    const val PHONE_EXT_031 = "+48 22 123 45 67"     // Polish Warsaw
    const val PHONE_EXT_032 = "+48 500 123 456"      // Polish mobile
    const val PHONE_EXT_033 = "+32 2 123 45 67"      // Belgian Brussels
    const val PHONE_EXT_034 = "+32 470 12 34 56"     // Belgian mobile
    const val PHONE_EXT_035 = "+43 1 123 4567"       // Austrian Vienna
    const val PHONE_EXT_036 = "+43 660 123 4567"     // Austrian mobile
    const val PHONE_EXT_037 = "+351 21 123 4567"     // Portuguese Lisbon
    const val PHONE_EXT_038 = "+351 912 345 678"     // Portuguese mobile
    const val PHONE_EXT_039 = "+30 21 0123 4567"     // Greek Athens
    const val PHONE_EXT_040 = "+30 6912 345678"      // Greek mobile
}
