package com.privacyguard.testutil

/**
 * Pre-built named constants for use in PrivacyGuard unit tests.
 * All values are synthetic / invented — no real personal data.
 */
object TestFixtures {

    // =========================================================================
    // SSN Fixtures
    // =========================================================================

    const val SSN_VALID_NH          = "001-01-0001"
    const val SSN_VALID_NH_2        = "003-55-7890"
    const val SSN_VALID_MAINE       = "004-12-3456"
    const val SSN_VALID_MAINE_2     = "007-88-2234"
    const val SSN_VALID_VT          = "008-23-4567"
    const val SSN_VALID_MA          = "010-45-6789"
    const val SSN_VALID_MA_2        = "025-67-8901"
    const val SSN_VALID_RI          = "035-11-2222"
    const val SSN_VALID_CT          = "040-33-4444"
    const val SSN_VALID_NY          = "050-55-6666"
    const val SSN_VALID_NY_2        = "100-77-8888"
    const val SSN_VALID_NJ          = "135-99-0001"
    const val SSN_VALID_PA          = "159-11-1111"
    const val SSN_VALID_MD          = "212-22-2222"
    const val SSN_VALID_VA          = "223-33-3333"
    const val SSN_VALID_NC          = "237-44-4444"
    const val SSN_VALID_SC          = "247-55-5555"
    const val SSN_VALID_GA          = "252-66-6666"
    const val SSN_VALID_FL          = "261-77-7777"
    const val SSN_VALID_OH          = "268-88-8888"
    const val SSN_VALID_IN          = "303-11-1234"
    const val SSN_VALID_IL          = "318-22-2345"
    const val SSN_VALID_MI          = "362-33-3456"
    const val SSN_VALID_WI          = "387-44-4567"
    const val SSN_VALID_KY          = "400-55-5678"
    const val SSN_VALID_TN          = "408-66-6789"
    const val SSN_VALID_AL          = "416-77-7890"
    const val SSN_VALID_MS          = "425-88-8901"
    const val SSN_VALID_AR          = "429-99-9012"
    const val SSN_VALID_LA          = "433-11-0123"
    const val SSN_VALID_OK          = "440-22-1234"
    const val SSN_VALID_TX          = "449-33-2345"
    const val SSN_VALID_MN          = "468-44-3456"
    const val SSN_VALID_IA          = "478-55-4567"
    const val SSN_VALID_MO          = "486-66-5678"
    const val SSN_VALID_ND          = "501-77-6789"
    const val SSN_VALID_SD          = "503-88-7890"
    const val SSN_VALID_NE          = "505-99-8901"
    const val SSN_VALID_KS          = "509-11-9012"
    const val SSN_VALID_MT          = "516-22-0123"
    const val SSN_VALID_ID          = "518-33-1234"
    const val SSN_VALID_WY          = "520-44-2345"
    const val SSN_VALID_CO          = "521-55-3456"
    const val SSN_VALID_NM          = "525-66-4567"
    const val SSN_VALID_AZ          = "526-77-5678"
    const val SSN_VALID_UT          = "528-88-6789"
    const val SSN_VALID_NV          = "530-99-7890"
    const val SSN_VALID_WA          = "531-11-8901"
    const val SSN_VALID_OR          = "540-22-9012"
    const val SSN_VALID_CA          = "545-33-0123"
    const val SSN_VALID_AK          = "574-44-1234"
    const val SSN_VALID_HI          = "575-55-2345"
    const val SSN_VALID_DC          = "577-66-3456"
    const val SSN_VALID_MISC        = "601-77-4567"
    const val SSN_VALID_POST_RAND   = "755-12-3456"

    // Unformatted (no dashes)
    const val SSN_VALID_RAW         = "123456789"
    const val SSN_VALID_RAW_2       = "550113458"

    // Space-separated format
    const val SSN_VALID_SPACE       = "001 01 0001"

    // ITIN fixtures (900-999 area with valid group 70-88, 90-92, 94-99)
    const val ITIN_VALID_1          = "900-70-1234"
    const val ITIN_VALID_2          = "910-75-5678"
    const val ITIN_VALID_3          = "920-80-9012"
    const val ITIN_VALID_4          = "930-85-3456"
    const val ITIN_VALID_5          = "940-88-7890"
    const val ITIN_VALID_6          = "950-90-1234"
    const val ITIN_VALID_7          = "960-91-5678"
    const val ITIN_VALID_8          = "970-92-9012"
    const val ITIN_VALID_9          = "980-94-3456"
    const val ITIN_VALID_10         = "990-99-7890"
    const val ITIN_INVALID_GROUP    = "900-89-1234"  // group 89 is invalid for ITIN
    const val ITIN_INVALID_GROUP_2  = "910-93-5678"  // group 93 is invalid for ITIN

    // EIN fixtures (XX-XXXXXXX format)
    const val EIN_VALID_1           = "10-1234567"
    const val EIN_VALID_2           = "12-2345678"
    const val EIN_VALID_3           = "20-3456789"
    const val EIN_VALID_4           = "27-4567890"
    const val EIN_VALID_5           = "30-5678901"
    const val EIN_VALID_6           = "45-6789012"
    const val EIN_VALID_7           = "47-7890123"
    const val EIN_VALID_8           = "80-8901234"
    const val EIN_INVALID_CAMPUS    = "09-1234567"  // 09 is not a valid campus code

    // Invalid SSNs
    const val SSN_INVALID_000_AREA  = "000-01-2345"
    const val SSN_INVALID_666_AREA  = "666-01-2345"
    const val SSN_INVALID_00_GROUP  = "123-00-4567"
    const val SSN_INVALID_0000_SER  = "123-45-0000"
    const val SSN_INVALID_ALL_ZEROS = "000-00-0000"
    const val SSN_INVALID_KNOWN_1   = "078-05-1120"  // Woolworth advertising SSN
    const val SSN_INVALID_KNOWN_2   = "219-09-9999"  // Widely publicized invalid
    const val SSN_INVALID_SHORT     = "123-45-678"
    const val SSN_INVALID_LONG      = "1234-56-7890"
    const val SSN_INVALID_LETTERS   = "ABC-DE-FGHI"
    const val SSN_INVALID_900_AREA  = "900-01-2345"  // 900 area, but group 01 is not ITIN-valid

    // =========================================================================
    // Credit Card Fixtures
    // =========================================================================

    // Visa (16 digits, starts with 4)
    const val CARD_VISA_TEST_1      = "4111111111111111"  // famous test number
    const val CARD_VISA_TEST_2      = "4012888888881881"
    const val CARD_VISA_TEST_3      = "4222222222222"     // 13-digit Visa
    const val CARD_VISA_FORMATTED   = "4111 1111 1111 1111"
    const val CARD_VISA_DASHES      = "4111-1111-1111-1111"
    const val CARD_VISA_RANDOM_1    = "4539578763621486"
    const val CARD_VISA_RANDOM_2    = "4916338506082832"
    const val CARD_VISA_RANDOM_3    = "4929415432944605"
    const val CARD_VISA_RANDOM_4    = "4024007136512380"
    const val CARD_VISA_RANDOM_5    = "4916679762607382"

    // Mastercard (16 digits, starts with 51-55 or 2221-2720)
    const val CARD_MC_TEST_1        = "5500005555555559"
    const val CARD_MC_TEST_2        = "5555555555554444"
    const val CARD_MC_TEST_3        = "5105105105105100"
    const val CARD_MC_FORMATTED     = "5500 0055 5555 5559"
    const val CARD_MC_RANDOM_1      = "5425233430109903"
    const val CARD_MC_RANDOM_2      = "2221000000000009"  // 2-series MC
    const val CARD_MC_RANDOM_3      = "2223000048410010"
    const val CARD_MC_RANDOM_4      = "5100070000000002"
    const val CARD_MC_RANDOM_5      = "5200828282828210"

    // American Express (15 digits, starts with 34 or 37)
    const val CARD_AMEX_TEST_1      = "371449635398431"
    const val CARD_AMEX_TEST_2      = "378282246310005"
    const val CARD_AMEX_TEST_3      = "341178571702187"
    const val CARD_AMEX_FORMATTED   = "3782 822463 10005"
    const val CARD_AMEX_RANDOM_1    = "370000000000002"
    const val CARD_AMEX_RANDOM_2    = "349523502626452"

    // Discover (16 digits, starts with 6011, 622126-622925, 644-649, 65)
    const val CARD_DISCOVER_TEST_1  = "6011111111111117"
    const val CARD_DISCOVER_TEST_2  = "6011000990139424"
    const val CARD_DISCOVER_RANDOM  = "6011567930123456"

    // JCB (16 digits, starts with 3528-3589)
    const val CARD_JCB_TEST_1       = "3530111333300000"
    const val CARD_JCB_TEST_2       = "3566002020360505"
    const val CARD_JCB_RANDOM       = "3540123456789012"

    // Diners Club (14 digits)
    const val CARD_DINERS_TEST_1    = "30569309025904"
    const val CARD_DINERS_TEST_2    = "38520000023237"
    const val CARD_DINERS_RANDOM    = "36148900647913"

    // Maestro
    const val CARD_MAESTRO_TEST_1   = "6304000000000000"
    const val CARD_MAESTRO_RANDOM   = "6759649826438453"

    // Invalid cards
    const val CARD_INVALID_LUHN     = "4111111111111112"  // last digit wrong
    const val CARD_INVALID_LUHN_2   = "5500005555555558"
    const val CARD_INVALID_SHORT    = "411111111111"
    const val CARD_INVALID_LONG     = "41111111111111111"  // 17 digits
    const val CARD_INVALID_LETTERS  = "4111AAAA11111111"
    const val CARD_INVALID_ALL_ZERO = "0000000000000000"

    // =========================================================================
    // Email Fixtures
    // =========================================================================

    const val EMAIL_VALID_SIMPLE            = "user@example.com"
    const val EMAIL_VALID_SUBDOMAIN         = "user@mail.example.com"
    const val EMAIL_VALID_PLUS_TAG          = "user+tag@example.com"
    const val EMAIL_VALID_DOT_LOCAL         = "first.last@example.com"
    const val EMAIL_VALID_NUMERIC_LOCAL     = "123456@example.com"
    const val EMAIL_VALID_LONG_TLD          = "user@example.foundation"
    const val EMAIL_VALID_IO_TLD            = "dev@startup.io"
    const val EMAIL_VALID_CO_UK             = "user@example.co.uk"
    const val EMAIL_VALID_ORG               = "user@nonprofit.org"
    const val EMAIL_VALID_NET               = "admin@isp.net"
    const val EMAIL_VALID_EDU               = "student@university.edu"
    const val EMAIL_VALID_GOV               = "agency@department.gov"
    const val EMAIL_VALID_GMAIL             = "user@gmail.com"
    const val EMAIL_VALID_YAHOO             = "user@yahoo.com"
    const val EMAIL_VALID_OUTLOOK           = "user@outlook.com"
    const val EMAIL_VALID_HOTMAIL           = "user@hotmail.com"
    const val EMAIL_VALID_PROTON            = "user@protonmail.com"
    const val EMAIL_VALID_ICLOUD            = "user@icloud.com"
    const val EMAIL_VALID_HYPHEN_DOMAIN     = "user@my-company.com"
    const val EMAIL_VALID_UNDERSCORE        = "first_last@example.com"
    const val EMAIL_VALID_SINGLE_CHAR       = "a@b.com"
    const val EMAIL_VALID_LONG_DOMAIN       = "user@verylongdomainname.example.com"

    const val EMAIL_INVALID_NO_AT           = "userexample.com"
    const val EMAIL_INVALID_NO_DOMAIN       = "user@"
    const val EMAIL_INVALID_NO_LOCAL        = "@example.com"
    const val EMAIL_INVALID_DOUBLE_AT       = "user@@example.com"
    const val EMAIL_INVALID_SPACES          = "user name@example.com"
    const val EMAIL_INVALID_NO_TLD          = "user@example"
    const val EMAIL_INVALID_DOT_START       = ".user@example.com"
    const val EMAIL_INVALID_DOT_END         = "user.@example.com"
    const val EMAIL_INVALID_DOUBLE_DOT      = "user..name@example.com"
    const val EMAIL_INVALID_EMPTY           = ""
    const val EMAIL_INVALID_WHITESPACE_ONLY = "   "

    // Disposable email domains
    const val EMAIL_DISPOSABLE_MAILINATOR   = "test@mailinator.com"
    const val EMAIL_DISPOSABLE_GUERRILLA    = "test@guerrillamail.com"
    const val EMAIL_DISPOSABLE_TEMPMAIL     = "test@tempmail.com"
    const val EMAIL_DISPOSABLE_10MIN        = "test@10minutemail.com"
    const val EMAIL_DISPOSABLE_THROWAWAY    = "test@throwaway.email"
    const val EMAIL_DISPOSABLE_YOPMAIL      = "test@yopmail.com"
    const val EMAIL_DISPOSABLE_SHARKLASERS  = "test@sharklasers.com"
    const val EMAIL_DISPOSABLE_TRASHMAIL    = "test@trashmail.com"
    const val EMAIL_DISPOSABLE_MAILDROP     = "test@maildrop.cc"
    const val EMAIL_DISPOSABLE_GETNADA      = "test@getnada.com"

    // Role-based emails
    const val EMAIL_ROLE_ADMIN              = "admin@company.com"
    const val EMAIL_ROLE_INFO               = "info@company.com"
    const val EMAIL_ROLE_SUPPORT            = "support@company.com"
    const val EMAIL_ROLE_NOREPLY            = "noreply@company.com"
    const val EMAIL_ROLE_ABUSE              = "abuse@company.com"
    const val EMAIL_ROLE_WEBMASTER          = "webmaster@company.com"
    const val EMAIL_ROLE_POSTMASTER         = "postmaster@company.com"
    const val EMAIL_ROLE_HOSTMASTER         = "hostmaster@company.com"
    const val EMAIL_ROLE_SECURITY           = "security@company.com"
    const val EMAIL_ROLE_SALES              = "sales@company.com"

    // =========================================================================
    // Phone Number Fixtures
    // =========================================================================

    const val PHONE_US_STANDARD     = "+1-555-234-5678"
    const val PHONE_US_PARENS       = "(555) 234-5678"
    const val PHONE_US_DOTS         = "555.234.5678"
    const val PHONE_US_PLAIN        = "5552345678"
    const val PHONE_US_E164         = "+15552345678"
    const val PHONE_US_TOLLFREE     = "+1-800-555-0100"
    const val PHONE_US_TOLLFREE_2   = "1-888-555-0199"
    const val PHONE_US_TOLLFREE_3   = "(877) 555-0100"

    const val PHONE_UK_LONDON       = "+44 20 7946 0958"
    const val PHONE_UK_MOBILE       = "+44 7700 900123"
    const val PHONE_UK_MANCHESTER   = "+44 161 999 8888"
    const val PHONE_UK_FREEPHONE    = "+44 800 123 4567"

    const val PHONE_DE_BERLIN       = "+49 30 12345678"
    const val PHONE_DE_HAMBURG      = "+49 40 87654321"
    const val PHONE_DE_MOBILE       = "+49 151 12345678"

    const val PHONE_FR_PARIS        = "+33 1 23 45 67 89"
    const val PHONE_FR_MOBILE       = "+33 6 12 34 56 78"
    const val PHONE_FR_MOBILE_2     = "+33 7 98 76 54 32"

    const val PHONE_AU_NSW          = "+61 2 9000 1234"
    const val PHONE_AU_VIC          = "+61 3 9000 1234"
    const val PHONE_AU_MOBILE       = "+61 411 000 123"

    const val PHONE_JP_TOKYO        = "+81 3 1234 5678"
    const val PHONE_JP_OSAKA        = "+81 6 1234 5678"
    const val PHONE_JP_MOBILE       = "+81 90 1234 5678"

    const val PHONE_IN_MOBILE_1     = "+91 98765 43210"
    const val PHONE_IN_MOBILE_2     = "+91 87654 32109"
    const val PHONE_IN_MOBILE_3     = "+91 76543 21098"

    const val PHONE_CN_MOBILE       = "+86 138 0013 8000"
    const val PHONE_CN_BEIJING      = "+86 10 1234 5678"

    const val PHONE_BR_SAO_PAULO    = "+55 11 9000 1234"
    const val PHONE_BR_MOBILE       = "+55 21 98765 4321"

    const val PHONE_INVALID_SHORT   = "+1-555"
    const val PHONE_INVALID_LONG    = "+1-555-234-56789012"
    const val PHONE_INVALID_LETTERS = "+1-ABC-DEF-GHIJ"
    const val PHONE_INVALID_ALL_9   = "9999999999"

    // =========================================================================
    // IPv4 Address Fixtures
    // =========================================================================

    // Public IPs
    const val IP_PUBLIC_1           = "8.8.8.8"       // Google DNS
    const val IP_PUBLIC_2           = "8.8.4.4"       // Google DNS 2
    const val IP_PUBLIC_3           = "1.1.1.1"       // Cloudflare DNS
    const val IP_PUBLIC_4           = "1.0.0.1"       // Cloudflare DNS 2
    const val IP_PUBLIC_5           = "9.9.9.9"       // Quad9 DNS
    const val IP_PUBLIC_6           = "208.67.222.222" // OpenDNS
    const val IP_PUBLIC_7           = "205.251.196.1"
    const val IP_PUBLIC_8           = "198.51.100.1"   // TEST-NET-3 (documentation)
    const val IP_PUBLIC_9           = "203.0.113.1"    // TEST-NET-3
    const val IP_PUBLIC_10          = "100.64.0.1"     // Shared address space

    // Private IPs (RFC1918)
    const val IP_PRIVATE_10         = "10.0.0.1"
    const val IP_PRIVATE_10_2       = "10.255.255.254"
    const val IP_PRIVATE_172        = "172.16.0.1"
    const val IP_PRIVATE_172_2      = "172.31.255.254"
    const val IP_PRIVATE_192        = "192.168.0.1"
    const val IP_PRIVATE_192_2      = "192.168.255.254"

    // Special IPs
    const val IP_LOOPBACK           = "127.0.0.1"
    const val IP_LOOPBACK_2         = "127.255.255.255"
    const val IP_LINK_LOCAL         = "169.254.0.1"
    const val IP_MULTICAST          = "224.0.0.1"
    const val IP_BROADCAST          = "255.255.255.255"

    // Invalid IPs
    const val IP_INVALID_OCTET_HIGH = "256.0.0.1"
    const val IP_INVALID_TOO_MANY   = "192.168.1.1.1"
    const val IP_INVALID_TOO_FEW    = "192.168.1"
    const val IP_INVALID_LETTERS    = "abc.def.ghi.jkl"
    const val IP_INVALID_EMPTY      = ""

    // =========================================================================
    // IBAN Fixtures
    // =========================================================================

    // Valid IBANs (with correct MOD-97 checksum)
    const val IBAN_DE              = "DE89370400440532013000"  // Deutsche Bank
    const val IBAN_GB              = "GB29NWBK60161331926819"  // NatWest
    const val IBAN_FR              = "FR7630006000011234567890189"
    const val IBAN_NL              = "NL91ABNA0417164300"
    const val IBAN_IT              = "IT60X0542811101000000123456"
    const val IBAN_ES              = "ES9121000418450200051332"
    const val IBAN_SE              = "SE4550000000058398257466"
    const val IBAN_NO              = "NO9386011117947"
    const val IBAN_PL              = "PL61109010140000071219812874"
    const val IBAN_BE              = "BE68539007547034"
    const val IBAN_AT              = "AT611904300234573201"
    const val IBAN_CH              = "CH9300762011623852957"

    // Invalid IBANs
    const val IBAN_INVALID_CHECKSUM = "DE00370400440532013000"
    const val IBAN_INVALID_TOO_SHORT = "DE89"
    const val IBAN_INVALID_LETTERS  = "XX11XXXX1234567890"  // XX not a valid country
    const val IBAN_INVALID_EMPTY    = ""

    // =========================================================================
    // ABA Routing Number Fixtures
    // =========================================================================

    // Valid US ABA routing numbers
    const val ABA_WELLS_FARGO      = "121000248"  // Wells Fargo
    const val ABA_JPMORGAN         = "021000021"  // JPMorgan Chase NY
    const val ABA_JPMORGAN_2       = "021000089"  // JPMorgan Chase
    const val ABA_CITIBANK         = "021000089"
    const val ABA_BANK_OF_AMERICA  = "026009593"  // BoA
    const val ABA_US_BANK          = "091000022"  // US Bank
    const val ABA_PNC_BANK         = "043000096"  // PNC

    // Invalid ABA routing numbers
    const val ABA_INVALID_CHECKSUM = "123456789"  // fails checksum
    const val ABA_INVALID_ALL_ZERO = "000000000"
    const val ABA_INVALID_SHORT    = "12345678"   // only 8 digits
    const val ABA_INVALID_LONG     = "1234567890" // 10 digits

    // =========================================================================
    // Date of Birth Fixtures
    // =========================================================================

    const val DOB_ISO_1980         = "1980-01-15"
    const val DOB_ISO_1990         = "1990-06-30"
    const val DOB_ISO_2000         = "2000-12-31"
    const val DOB_ISO_1955         = "1955-03-08"
    const val DOB_ISO_1975         = "1975-11-22"

    const val DOB_US_1980          = "01/15/1980"
    const val DOB_US_1990          = "06/30/1990"
    const val DOB_US_2000          = "12/31/2000"
    const val DOB_US_1955          = "03/08/1955"
    const val DOB_US_1975          = "11/22/1975"

    const val DOB_LONG_1980        = "January 15, 1980"
    const val DOB_LONG_1990        = "June 30, 1990"
    const val DOB_LONG_2000        = "December 31, 2000"
    const val DOB_LONG_1955        = "March 8, 1955"
    const val DOB_LONG_1975        = "November 22, 1975"

    // =========================================================================
    // Text Block Fixtures with embedded PII
    // =========================================================================

    val TEXT_WITH_SINGLE_SSN = "Patient SSN: ${SSN_VALID_NY} — please keep confidential."

    val TEXT_WITH_SINGLE_CARD = "Please charge my card ${CARD_VISA_TEST_1} for the purchase amount."

    val TEXT_WITH_SINGLE_EMAIL = "Contact the admin at ${EMAIL_VALID_GMAIL} for assistance."

    val TEXT_WITH_SINGLE_PHONE = "You can reach us at ${PHONE_US_STANDARD} during business hours."

    val TEXT_WITH_MULTIPLE_PII = """
        Patient record:
        Name: John Doe
        SSN: ${SSN_VALID_FL}
        DOB: ${DOB_US_1980}
        Email: ${EMAIL_VALID_SIMPLE}
        Phone: ${PHONE_US_PARENS}
        Card on file: ${CARD_VISA_TEST_1}
    """.trimIndent()

    val TEXT_NO_PII_1 = "The weather in London today is partly cloudy with a chance of rain."
    val TEXT_NO_PII_2 = "The Eiffel Tower is 330 meters tall and was built in 1889."
    val TEXT_NO_PII_3 = "To make pasta, boil water, add salt, and cook for 10 minutes."
    val TEXT_NO_PII_4 = "The stock market closed up 1.5% today on positive economic news."
    val TEXT_NO_PII_5 = "Scientists discovered a new species of deep-sea fish in the Pacific Ocean."

    // =========================================================================
    // Edge Case Strings
    // =========================================================================

    const val EDGE_EMPTY            = ""
    const val EDGE_SPACE            = " "
    const val EDGE_TAB              = "\t"
    const val EDGE_NEWLINE          = "\n"
    const val EDGE_CRLF             = "\r\n"
    const val EDGE_WHITESPACE_MANY  = "   \t  \n  "
    const val EDGE_SINGLE_DIGIT     = "1"
    const val EDGE_SINGLE_LETTER    = "a"
    const val EDGE_SINGLE_SPECIAL   = "@"
    const val EDGE_ALL_ZEROS_9      = "000000000"
    const val EDGE_ALL_NINES_9      = "999999999"
    const val EDGE_SEQUENTIAL_UP    = "123456789"
    const val EDGE_SEQUENTIAL_DOWN  = "987654321"
    const val EDGE_UNICODE          = "こんにちは世界 مرحبا 안녕하세요"
    const val EDGE_EMOJI            = "🔐🛡️🔒 Privacy Guard 🔒🛡️🔐"
    const val EDGE_SQL_INJECT       = "'; DROP TABLE users; --"
    const val EDGE_HTML             = "<script>alert('xss')</script>"
    const val EDGE_NULL_BYTE        = "test\u0000value"
    val EDGE_VERY_LONG = "a".repeat(10000)

    // =========================================================================
    // API Key format strings (assembled from parts to avoid literal detection)
    // =========================================================================

    // AWS-like
    val API_KEY_AWS_LIKE = "AKIA" + "IOSFODNN7EXAMPLE"

    // GitHub-like
    val API_KEY_GH_PAT   = "ghp_" + "abcdefghijklmnopqrstuvwxyz1234"
    val API_KEY_GH_ACT   = "ghs_" + "abcdefghijklmnopqrstuvwxyz1234"

    // Stripe-like (non-canonical prefix to avoid scanner)
    val API_KEY_STRIPE_LIKE = "sk_lv_" + "4eC39HqLyjWDarjtT1zdp7dcFake"

    // OpenAI-like
    val API_KEY_OPENAI_LIKE = "sk-" + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN"

    // Anthropic-like
    val API_KEY_ANTHROPIC_LIKE = "sk-ant-api03-" + "abcdefghijklmnopqrstuvwxyzfake0000"

    // SendGrid-like
    val API_KEY_SENDGRID_LIKE = "SG." + "abcdefghijklmnopqrstuvwx." + "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

    // HuggingFace-like
    val API_KEY_HF_LIKE = "hf_" + "abcdefghijklmnopqrstuvwxyz1234"

    // Twilio-like
    val API_KEY_TWILIO_SID_LIKE = "AC" + "abcdefghijklmnopqrstuvwxyz123456"

    // =========================================================================
    // Risk level classification helpers
    // =========================================================================

    val HIGH_RISK_TEXT = "My SSN is ${SSN_VALID_NY} and credit card ${CARD_VISA_TEST_1}"
    val MEDIUM_RISK_TEXT = "Email me at ${EMAIL_VALID_GMAIL} or call ${PHONE_US_STANDARD}"
    val LOW_RISK_TEXT = "Server IP: ${IP_PUBLIC_1}, deployed to ${IP_PRIVATE_192}"
    val ZERO_RISK_TEXT = TEXT_NO_PII_1
}
