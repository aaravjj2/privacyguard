package com.privacyguard.testutil

/**
 * Comprehensive test data factory providing pre-built collections for all PII types.
 * Use this class to access large collections of test data for thorough unit/integration tests.
 */
object TestDataFactory {

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 1 — SSN COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /** 200 known-valid SSN values for positive testing. */
    val VALID_SSNS: List<String> = listOf(
        "001-01-0001", "001-01-0002", "001-01-0003", "001-01-0010", "001-01-0100",
        "001-01-1000", "001-01-9999", "001-10-0001", "001-99-0001", "001-99-9999",
        "002-01-0001", "002-02-0002", "003-03-0003", "004-04-0004", "005-05-0005",
        "006-06-0006", "007-07-0007", "008-08-0008", "009-09-0009", "010-01-0001",
        "010-10-1010", "011-11-1111", "012-12-1212", "013-13-1313", "014-14-1414",
        "015-15-1515", "016-16-1616", "017-17-1717", "018-18-1818", "019-19-1919",
        "020-20-2020", "021-21-2121", "022-22-2222", "023-23-2323", "024-24-2424",
        "025-25-2525", "026-26-2626", "027-27-2727", "028-28-2828", "029-29-2929",
        "030-30-3030", "031-31-3131", "032-32-3232", "033-33-3333", "034-34-3434",
        "035-35-3535", "036-36-3636", "037-37-3737", "038-38-3838", "039-39-3939",
        "040-40-4040", "041-41-4141", "042-42-4242", "043-43-4343", "044-44-4444",
        "045-45-4545", "046-46-4646", "047-47-4747", "048-48-4848", "049-49-4949",
        "050-50-5050", "051-51-5151", "052-52-5252", "053-53-5353", "054-54-5454",
        "055-55-5555", "056-56-5656", "057-57-5757", "058-58-5858", "059-59-5959",
        "060-60-6060", "061-61-6161", "062-62-6262", "063-63-6363", "064-64-6464",
        "065-65-6565", "067-67-6767", "068-68-6868", "069-69-6969", "070-70-7070",
        "071-71-7171", "072-72-7272", "073-73-7373", "074-74-7474", "075-75-7575",
        "076-76-7676", "077-77-7777", "078-78-7878", "079-79-7979", "080-80-8080",
        "081-81-8181", "082-82-8282", "083-83-8383", "084-84-8484", "085-85-8585",
        "086-86-8686", "087-87-8787", "088-88-8888", "089-89-8989", "090-90-9090",
        "091-91-9191", "092-92-9292", "093-93-9393", "094-94-9494", "095-95-9595",
        "096-96-9696", "097-97-9797", "098-98-9898", "099-99-9999", "100-01-0001",
        "100-10-1001", "110-20-2002", "120-30-3003", "130-40-4004", "140-50-5005",
        "150-60-6006", "160-70-7007", "170-80-8008", "180-90-9009", "190-10-0010",
        "200-20-0020", "210-30-0030", "220-40-0040", "230-50-0050", "240-60-0060",
        "250-70-0070", "260-80-0080", "270-90-0090", "280-01-0001", "290-02-0002",
        "300-03-0003", "310-04-0004", "320-05-0005", "330-06-0006", "340-07-0007",
        "350-08-0008", "360-09-0009", "370-11-1111", "380-12-2222", "390-13-3333",
        "400-14-4444", "410-15-5555", "420-16-6666", "430-17-7777", "440-18-8888",
        "450-19-9999", "460-21-1234", "470-22-2345", "480-23-3456", "490-24-4567",
        "500-25-5678", "510-26-6789", "520-27-7890", "530-28-8901", "540-29-9012",
        "550-31-0123", "560-32-1234", "570-33-2345", "580-34-3456", "590-35-4567",
        "600-36-5678", "123-45-6789", "234-56-7890", "345-67-8901", "456-78-9012",
        "365-47-2918", "174-28-5093", "482-19-7364", "291-73-0846", "538-62-4917",
        "147-83-2659", "263-91-7480", "391-47-8562", "478-23-0519", "503-64-8127",
        "164-72-3859", "417-93-2648", "285-46-7031", "531-82-4176", "378-59-2413",
        "192-64-8375", "446-27-9130", "263-48-0597", "519-73-8264", "385-16-7429",
        "142-73-5860", "427-09-5348", "263-51-8942", "518-74-2093", "374-92-6185",
        "491-63-8720", "256-87-4193", "382-14-6597", "175-93-4268", "438-62-0175",
        "293-71-4860", "517-38-2964", "164-82-7359", "428-57-3019", "351-94-6827",
        "286-43-0951", "493-28-7641", "175-64-3928", "318-45-7629", "462-91-3748",
        "235-78-4069", "497-13-8624", "363-85-2049", "521-47-9306", "184-63-7920",
        "412-73-8059", "267-19-4803", "375-92-8461", "148-36-7829", "521-84-3069"
    )

    /** 60 known-invalid SSN values for negative testing. */
    val INVALID_SSNS: List<String> = listOf(
        "000-01-0001",   // area 000 forbidden
        "666-01-0001",   // area 666 forbidden
        "900-01-0001",   // area 900+ forbidden
        "901-01-0001",   // area 900+ forbidden
        "910-01-0001",   // area 900+ forbidden
        "999-99-9999",   // area 999 forbidden
        "700-01-0001",   // area 700+
        "710-01-0001",   // area 700+
        "720-01-0001",   // area 700+
        "730-01-0001",   // area 700+
        "740-01-0001",   // area 700+
        "750-01-0001",   // area 700+
        "760-01-0001",   // area 700+
        "770-01-0001",   // area 700+
        "780-01-0001",   // area 700+
        "790-01-0001",   // area 700+
        "800-01-0001",   // area 800+
        "850-01-0001",   // area 850+
        "123-00-4567",   // group 00 forbidden
        "123-45-0000",   // serial 0000 forbidden
        "000-00-0000",   // all zeros
        "000-00-0001",   // area 000
        "000-01-0000",   // area 000 + serial 0000
        "12345678",      // no dashes - 8 digits
        "123456789",     // no dashes - 9 digits
        "1234567890",    // no dashes - 10 digits
        "123-456-789",   // wrong grouping 3-3-3
        "1234-5-6789",   // wrong grouping 4-1-4
        "12-345-6789",   // wrong grouping 2-3-4
        "123-4-56789",   // wrong grouping 3-1-5
        "abc-de-fghi",   // letters
        "123-ab-4567",   // letters in group
        "123-45-WXYZ",   // letters in serial
        "1234-56-789",   // too many in area
        "12-345678",     // malformed 2-6
        "123-4567",      // missing serial
        "1234-567",      // short malformed
        "",              // empty string
        " ",             // single space
        "   ",           // whitespace only
        "\t",            // tab
        "\n",            // newline
        "078-05-1120",   // Woolworth SSN (famous test SSN)
        "219-09-9999",   // used in advertising
        "987-65-4320",   // used in advertising
        "---",           // only dashes
        "-01-0001",      // missing area
        "123--0001",     // empty group
        "123-45-",       // missing serial
        "-45-6789",      // missing area
        "123 45 6789",   // spaces instead of dashes
        "123.45.6789",   // dots instead of dashes
        "123/45/6789",   // slashes instead of dashes
        "123:45:6789",   // colons instead of dashes
        "+23-45-6789",   // plus sign
        "*23-45-6789",   // asterisk
        "1 23-45-6789",  // extra characters
        "123-45-6789-0", // trailing extra
        "0123-45-6789",  // leading extra digit
        "-123-45-6789",  // leading dash
        "123-45-67890"   // too many in serial
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 2 — CREDIT CARD COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Valid Visa card numbers (pass Luhn). */
    val VALID_VISA_CARDS: List<String> = listOf(
        "4111111111111111", "4012888888881881", "4222222222222",
        "4539578763621486", "4916338506082832", "4532015112830366",
        "4929490369015736", "4716751691420004", "4485904394748950",
        "4556737586899855", "4024007103939509", "4026278463897620",
        "4508751079308350", "4844532262435032", "4913614280213862",
        "4444444444444448", "4000000000000002", "4000000000000010",
        "4000000000000028", "4000000000000036", "4000000000000044",
        "4000000000000051", "4000000000000069", "4000000000000077",
        "4000000000000085", "4000000000000093", "4000000000000101",
        "4000000000000119", "4000000000000127", "4000000000000135"
    )

    /** Valid Mastercard numbers. */
    val VALID_MASTERCARD_NUMBERS: List<String> = listOf(
        "5105105105105100", "5555555555554444", "5500005555555559",
        "5425233430109903", "5200828282828210", "2221000000000009",
        "2720999999999996", "2500000000000001", "5301250070000191",
        "5100290029002909", "5211476012005955", "5311561559145891",
        "5411111111111115", "5511111111111116",
        "5000000000000009", "5100000000000008", "5200000000000007",
        "5300000000000006", "5400000000000005", "5500000000000004",
        "2221000000000009", "2310000000000004", "2400000000000008",
        "2500000000000001", "2600000000000006", "2700000000000001",
        "2710000000000000", "2720000000000005", "2220000000000004",
        "2290000000000003"
    )

    /** Valid American Express numbers. */
    val VALID_AMEX_NUMBERS: List<String> = listOf(
        "378282246310005", "371449635398431", "378734493671000",
        "340000000000009", "370000000000002",
        "341234567890123", "375275741457347", "374251018720955",
        "376895900900977", "345679879876543",
        "370000000000002", "340000000000009", "371200000000003",
        "370000000000002", "340000000000009",
        "378282246310005", "371449635398431",
        "370000000000002", "340000000000009", "374251018720955"
    )

    /** Valid Discover card numbers. */
    val VALID_DISCOVER_NUMBERS: List<String> = listOf(
        "6011111111111117", "6011000990139424", "6500000000000002",
        "6444444444444444", "6221260000000000", "6221261111111118",
        "6229250000000006", "6011000000000004001",
        "6500000000000002", "6011111111111117",
        "6444000000000000", "6011220000000007"
    )

    /** Valid JCB card numbers. */
    val VALID_JCB_NUMBERS: List<String> = listOf(
        "3528000000000007", "3589000000000006", "3530000000000005",
        "3566000020000410", "3528000000000000004"
    )

    /** Valid Diners Club card numbers. */
    val VALID_DINERS_NUMBERS: List<String> = listOf(
        "30569309025904", "36148900647913", "38000000000006",
        "30000000000004", "30500000000003"
    )

    /** Invalid card numbers for negative tests. */
    val INVALID_CARD_NUMBERS: List<String> = listOf(
        "0000000000000000", "1111111111111111", "4111111111111112",
        "5105105105105101", "411111111111", "41111111111111111",
        "4111111111111a11", "", "1234567890123456", "9876543210987654",
        "6011000000000", "37828224631000", "305693090259",
        "35280000000000", "2221000000000010", "4000",
        "123456789012345", "   ", "-", "0",
        "4111-1111-1111-1111", "4111 1111 1111 111", "abcdefghijklmnop",
        "0000000000000001", "9999999999999999", "1234567890",
        "123456789012", "12345678901234", "1234567890123",
        "1234567890123456789", "12345678901234567890", "4111111111111111a"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 3 — EMAIL COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /** 200 valid email addresses. */
    val VALID_EMAILS: List<String> = listOf(
        "user@example.com", "user.name@example.com", "user+tag@example.com",
        "user-name@example.com", "user_name@example.com", "user123@example.com",
        "123user@example.com", "USER@EXAMPLE.COM", "User.Name@Example.Com",
        "user@subdomain.example.com", "user@sub.sub.example.com",
        "user@example.co.uk", "user@example.com.au", "user@example.io",
        "user@example.ai", "user@example.app", "user@example.dev",
        "user@example.gov", "user@example.edu", "user@example.org",
        "user@example.net", "user@example.mil", "first.last@company.com",
        "john.doe@gmail.com", "jane.smith@yahoo.com", "bob.jones@outlook.com",
        "alice.wonder@hotmail.com", "charlie.brown@icloud.com",
        "david.miller@protonmail.com", "test+filter@gmail.com",
        "user.name+tag+sorting@example.com", "x@example.com",
        "example-indeed@strange-example.com", "example@s.example",
        "1234567890@example.com", "email@example-one.com",
        "_______@example.com", "email@example.name", "email@example.museum",
        "user@gmail.com", "user@yahoo.com", "user@hotmail.com",
        "user@outlook.com", "user@icloud.com", "user@mail.com",
        "user@protonmail.com", "user@fastmail.com", "user@me.com",
        "user@live.com", "alex.johnson@company.co.uk",
        "test.email.with+symbol@example.com", "id-with-dash@example.com",
        "a@b.c", "test123@test123.test123", "admin+filter@my-domain.org",
        "sales.team@global-corp.net", "hr_department@bigcorp.co",
        "dev.team+releases@company.tech", "newsletter@weekly.updates.com",
        "auto-reply@mailer.domain.net", "user@example.travel",
        "user@example.photography", "user@example.technology",
        "user@xn--nxasmq6b.com", "team@very-long-company-name-here.com",
        "shortened@co.uk", "user@example.info", "user@example.us",
        "user1@example.com", "user2@example.com", "user3@example.com",
        "user4@example.com", "user5@example.com", "user6@example.com",
        "user7@example.com", "user8@example.com", "user9@example.com",
        "user10@example.com", "user11@example.com", "user12@example.com",
        "user13@example.com", "user14@example.com", "user15@example.com",
        "user16@example.com", "user17@example.com", "user18@example.com",
        "user19@example.com", "user20@example.com",
        "alice@company.com", "bob@company.com", "carol@company.com",
        "dave@company.com", "eve@company.com", "frank@company.com",
        "grace@company.com", "henry@company.com", "iris@company.com",
        "jack@company.com", "karen@company.com", "larry@company.com",
        "mary@company.com", "nora@company.com", "oscar@company.com",
        "paul@company.com", "quinn@company.com", "rachel@company.com",
        "steve@company.com", "tina@company.com",
        "admin@example.com", "info@example.com", "support@example.com",
        "noreply@example.com", "no-reply@example.com", "abuse@example.com",
        "webmaster@example.com", "postmaster@example.com",
        "sales@example.com", "billing@example.com",
        "contact@startup.io", "hello@product.co", "team@webapp.dev",
        "feedback@service.app", "help@platform.ai", "jobs@company.careers",
        "press@org.media", "security@company.com", "privacy@company.com",
        "legal@company.com",
        "newsletter@company.com", "updates@company.com",
        "announcements@company.com", "digest@company.com",
        "notifications@company.com", "alerts@company.com",
        "reminders@company.com", "receipts@company.com",
        "invoices@company.com", "statements@company.com",
        "test@mailinator.com", "temp@guerrillamail.com",
        "fake@tempmail.org", "spam@yopmail.com", "trash@trashmail.com",
        "drop@maildrop.cc", "anon@throwam.com", "no@sharklasers.com",
        "x@guerrillamailblock.com", "test@dispostable.com",
        "alice@department.gov", "bob@agency.mil", "carol@school.edu",
        "dave@university.edu", "eve@institution.ac.uk",
        "frank@organization.org", "grace@association.net",
        "henry@foundation.com", "iris@charity.org", "jack@ngo.net",
        "user@example.br", "user@example.mx", "user@example.ar",
        "user@example.cl", "user@example.co", "user@example.pe",
        "user@example.ec", "user@example.ve", "user@example.uy",
        "user@example.py",
        "user@example.de", "user@example.fr", "user@example.it",
        "user@example.es", "user@example.pt", "user@example.nl",
        "user@example.be", "user@example.at", "user@example.ch",
        "user@example.pl",
        "user@example.jp", "user@example.cn", "user@example.in",
        "user@example.kr", "user@example.sg", "user@example.au",
        "user@example.nz", "user@example.hk", "user@example.tw",
        "user@example.ph"
    )

    /** 80 invalid email addresses. */
    val INVALID_EMAILS: List<String> = listOf(
        "plainaddress", "@missinglocal.com", "missingdomain@",
        "missingat.com", "two@@example.com", ".user@example.com",
        "user.@example.com", "user..name@example.com", "user@.com",
        "user@exam ple.com", "", "user@", "@", "user@[256.256.256.256]",
        "user@example..com", "missing-dot@com", "user@-domain.com",
        "user@domain-.com", "user@.domain.com", "user@domain.c",
        "user@domain", "user@domain.", "@domain.com", "user name@domain.com",
        "user\t@domain.com", "user\n@domain.com", "user@",
        "user", "@example.com", "user@.example.com",
        "user@example.", "user@@example.com", ".@example.com",
        "user@example.c", "user@-example.com", "user@example-.com",
        "user@[192.168.1.1]", "user@192.168.1.1",
        "INVALID", "not-email", "partial@",
        "@partial.com", "spaces in name@example.com",
        "tabs\there@example.com", "newlines\nhere@example.com",
        "user@exam[ple.com", "user@exam]ple.com",
        "user@exam{ple.com", "user@exam}ple.com",
        "user@exam|ple.com", "user@exam\\ple.com",
        "user@exam^ple.com", "user@exam~ple.com",
        "user@exam`ple.com", "user@exam'ple.com",
        "user@exam\"ple.com", "user@exam<ple.com",
        "user@exam>ple.com", "user@exam?ple.com",
        "user@exam!ple.com", "user@exam#ple.com",
        "user@exam$ple.com", "user@exam&ple.com",
        "user@exam*ple.com", "user@exam(ple.com",
        "user@exam)ple.com", "user@exam=ple.com",
        "user@exam+ple.com", "user@exam/ple.com",
        "user@exam,ple.com", "user@exam;ple.com",
        "user@exam:ple.com", "user@exam@ple.com",
        "#user@example.com", "$user@example.com",
        "%user@example.com", "^user@example.com",
        "&user@example.com", "*user@example.com",
        "(user)@example.com", "[user]@example.com"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 4 — PHONE NUMBER COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Valid US phone numbers in various formats. */
    val VALID_US_PHONES: List<String> = listOf(
        "(212) 555-1234", "(213) 555-5678", "(312) 555-9012",
        "(415) 555-3456", "(617) 555-7890", "(713) 555-1234",
        "(305) 555-5678", "(404) 555-9012", "(206) 555-3456",
        "(702) 555-7890", "(202) 555-0100", "(503) 555-0200",
        "(719) 555-0300", "(901) 555-0400", "(201) 555-0500",
        "(301) 555-0600", "(401) 555-0700", "(501) 555-0800",
        "(601) 555-0900", "(701) 555-1000",
        "212-555-1234", "213.555.5678", "3125559012",
        "+1-415-555-3456", "+1 617 555 7890", "+12125551234",
        "1-213-555-5678", "(800) 555-1212", "(888) 555-0100",
        "(877) 555-0199", "(866) 555-0200", "(855) 555-0300",
        "(844) 555-0400", "(833) 555-0500", "(822) 555-0600",
        "(811) 555-0700", "(900) 555-0800",
        "4155553456", "6175557890", "7135551234",
        "+14155553456", "+16175557890", "+17135551234",
        "1 (212) 555-1234", "1 (213) 555-5678", "1 (312) 555-9012"
    )

    /** Valid international phone numbers. */
    val VALID_INTL_PHONES: List<String> = listOf(
        "+44 20 7946 0958", "+44 161 999 8888", "+44 7700 900001",
        "020 7946 0958", "07700 900001",
        "+49 030 12345678", "+49 089 98765432", "+49 0211 1234567",
        "+49 040 23456789", "030 12345678",
        "+33 1 23 45 67 89", "+33 6 12 34 56 78",
        "01 23 45 67 89", "06 12 34 56 78",
        "+61 2 9876 5432", "+61 3 9876 5432",
        "+61 412 345 678", "02 9876 5432",
        "+81 3-1234-5678", "+81 80-1234-5678",
        "03-1234-5678", "080-1234-5678",
        "+91 98765 43210", "+91 11 2345 6789",
        "098765 43210", "011 23456789",
        "+86 138 0013 8000", "+86 10 1234 5678",
        "138 0013 8000", "010 1234 5678",
        "+55 11 98765-4321", "+55 21 3456-7890",
        "+34 91 234 56 78", "+34 612 345 678",
        "+52 55 1234 5678", "+1 604 555 1234",
        "+7 495 123-45-67", "+39 02 1234 5678",
        "+31 20 123 4567", "+41 44 123 45 67",
        "+46 8 123 456 78", "+47 22 12 34 56",
        "+45 32 12 34 56", "+48 22 123 45 67",
        "+32 2 123 45 67", "+43 1 123 4567"
    )

    /** Phone numbers that should NOT be detected as phone numbers. */
    val NON_PHONE_STRINGS: List<String> = listOf(
        "12345", "1234567", "9876",
        "3.14159", "1/15/2024", "100",
        "$1234.56", "1234-5678", "01/01/1980",
        "#123456", "step 3 of 12",
        "version 1.2.3", "item 1234", "code ABC123",
        "ref: 98765", "10-20-30", "2024-01-15",
        "latitude: 40.7589", "longitude: -73.9851",
        "123 Main Street", "zip: 90210"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 5 — IP ADDRESS COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Valid public IPv4 addresses. */
    val PUBLIC_IPV4_ADDRESSES: List<String> = listOf(
        "8.8.8.8", "8.8.4.4", "1.1.1.1", "1.0.0.1",
        "208.67.222.222", "208.67.220.220", "9.9.9.9",
        "149.112.112.112", "64.6.64.6", "64.6.65.6",
        "216.58.192.14", "172.217.0.0", "13.32.0.0",
        "52.0.0.0", "104.16.0.0", "31.13.92.36",
        "157.240.0.1", "199.59.148.1", "205.251.242.0",
        "198.41.128.4", "74.125.0.0", "172.253.0.0",
        "35.186.0.0", "130.211.0.0", "107.21.0.0",
        "54.84.0.0", "185.220.100.1", "91.108.4.1",
        "149.154.160.1", "184.168.131.241",
        "166.62.28.1", "23.185.0.1", "185.191.169.0",
        "195.201.0.1", "188.166.0.1", "165.22.0.1",
        "134.209.0.1", "178.128.0.1", "167.172.0.1",
        "159.65.0.1", "209.97.0.1", "157.230.0.1",
        "206.189.0.1", "161.35.0.1", "64.227.0.1",
        "104.248.0.1", "137.184.0.1", "143.198.0.1",
        "147.182.0.1", "68.183.0.1", "139.59.0.1",
        "46.101.0.1", "159.89.0.1", "165.227.0.1",
        "206.81.0.1", "104.131.0.1", "192.241.0.1",
        "104.236.0.1", "198.199.0.1", "45.55.0.1",
        "107.170.0.1", "128.199.0.1", "162.243.0.1",
        "184.72.0.1", "204.236.0.1", "107.23.0.1",
        "107.22.0.1", "23.20.0.1", "23.21.0.1",
        "23.22.0.1", "23.23.0.1", "23.24.0.1",
        "23.25.0.1", "23.26.0.1", "23.27.0.1",
        "23.28.0.1", "23.29.0.1", "23.30.0.1",
        "23.31.0.1", "50.16.0.1", "50.17.0.1",
        "50.18.0.1", "50.19.0.1"
    )

    /** Private IPv4 addresses. */
    val PRIVATE_IPV4_ADDRESSES: List<String> = listOf(
        "10.0.0.1", "10.0.0.2", "10.0.0.3", "10.0.0.10",
        "10.0.0.100", "10.0.0.254", "10.0.1.1", "10.0.2.1",
        "10.1.0.1", "10.1.1.1", "10.10.0.1", "10.10.10.10",
        "10.100.0.1", "10.200.0.1", "10.255.255.254",
        "172.16.0.1", "172.16.0.2", "172.16.1.1", "172.17.0.1",
        "172.18.0.1", "172.19.0.1", "172.20.0.1", "172.21.0.1",
        "172.22.0.1", "172.23.0.1", "172.24.0.1", "172.25.0.1",
        "172.26.0.1", "172.27.0.1", "172.28.0.1", "172.29.0.1",
        "172.30.0.1", "172.31.0.1", "172.31.255.254",
        "192.168.0.1", "192.168.0.2", "192.168.1.1", "192.168.1.2",
        "192.168.1.100", "192.168.1.254", "192.168.2.1",
        "192.168.10.1", "192.168.100.1", "192.168.255.254"
    )

    /** Invalid IP address strings. */
    val INVALID_IPV4_ADDRESSES: List<String> = listOf(
        "256.1.1.1", "1.256.1.1", "1.1.256.1", "1.1.1.256",
        "1.2.3.4.5", "1.2.3", "1.2", "1",
        "abc.def.ghi.jkl", "1.2.3.-1", "-1.2.3.4",
        "1.2.3.4.", ".1.2.3.4", "1..2.3.4", "1.2..3.4",
        "", "   ", "::1", "2001:db8::1",
        "999.999.999.999", "300.300.300.300",
        "1.2.3.4/24", "1.2.3.4:80",
        "localhost", "example.com"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 6 — IBAN COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    val VALID_IBANS: List<String> = listOf(
        // Germany
        "DE89370400440532013000", "DE02200505501015871393", "DE91100000000123456789",
        // UK
        "GB29NWBK60161331926819", "GB82WEST12345698765432", "GB60BARC20201530093459",
        // France
        "FR7614508149604043700226326", "FR1420041010050500013M02606",
        // Netherlands
        "NL91ABNA0417164300", "NL20INGB0001234567", "NL02ABNA0123456789",
        // Belgium
        "BE68539007547034", "BE56456394728288", "BE43068999999501",
        // Spain
        "ES9121000418450200051332", "ES8023100001180000012345",
        // Italy
        "IT60X0542811101000000123456", "IT40O0542811101000000123456",
        // Switzerland
        "CH9300762011623852957", "CH5604835012345678009",
        // Austria
        "AT611904300234573201", "AT483200000012345864",
        // Sweden
        "SE4550000000058398257466", "SE7280000810340009783242",
        // Norway
        "NO9386011117947", "NO1360139016785",
        // Denmark
        "DK5000400440116243", "DK9520000123456789",
        // Poland
        "PL10105000997603123456789123", "PL61109010140000071219812874"
    )

    val INVALID_IBANS: List<String> = listOf(
        "", "DE00000000000000000000", "XX00000000000000",
        "DE89", "1234567890", "NLLLLLL",
        "DE00", "GB00", "FR00", "NOT_AN_IBAN",
        "1234", "ABCD", "DE99999999999999999999"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 7 — TEXT SCENARIOS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Texts clearly containing PII. */
    val TEXTS_WITH_PII: List<String> = listOf(
        "My Social Security Number is 123-45-6789.",
        "SSN: 234-56-7890 for account verification.",
        "Please charge card 4111111111111111 for the purchase.",
        "Card: 5555555555554444, expiry 12/25.",
        "Contact me at john.doe@gmail.com for details.",
        "Email: jane.smith@yahoo.com",
        "Call (212) 555-1234 after 6 PM.",
        "Mobile: +1-415-555-3456",
        "Device at IP 192.168.1.100 was blocked.",
        "Server 10.0.0.50 is down.",
        "Transfer to IBAN DE89370400440532013000.",
        "Bank: GB29NWBK60161331926819 at Barclays.",
        "Patient Jane Doe, SSN 123-45-6789, DOB 01/01/1980.",
        "Name: John Smith, Card: 4111111111111111, Phone: (212) 555-1234.",
        "Employee SSN 234-56-7890, email alice@company.com.",
        "My Amex 378282246310005 should be on file.",
        "Discover card 6011111111111117 was declined.",
        "The device 192.168.1.1 is firewall-protected.",
        "Send confirmation to billing@company-name.com.",
        "Reach us at support@product.io."
    )

    /** Texts that do NOT contain PII. */
    val TEXTS_WITHOUT_PII: List<String> = listOf(
        "The weather today is sunny with a high of 75 degrees.",
        "Please review the quarterly report attached.",
        "The project deadline has been moved to end of month.",
        "Our team will meet on Tuesday at 3 PM in conference room B.",
        "The software update includes performance improvements.",
        "Kotlin is a modern statically typed programming language.",
        "The Android documentation provides comprehensive guides.",
        "Machine learning models require large training data.",
        "Privacy protection is essential in today's digital world.",
        "The application processes data locally.",
        "Security best practices include strong encryption.",
        "The user interface was redesigned for accessibility.",
        "Testing is a critical part of software development.",
        "The open source community contributes greatly.",
        "Documentation helps new team members understand the codebase.",
        "The product roadmap includes three major releases.",
        "Code review improves quality and knowledge sharing.",
        "Continuous integration ensures automated test coverage.",
        "The deployment pipeline includes staging environments.",
        "Monitoring and alerting detect issues proactively.",
        "The quarterly report shows 12% revenue growth.",
        "All tests passed on the latest commit.",
        "The build was successful with no warnings.",
        "Configuration changes take effect on restart.",
        "The feature was merged to the main branch.",
        "Release notes are available in the changelog.",
        "The API rate limit is 1000 requests per hour.",
        "Background jobs run every 15 minutes.",
        "The cache is invalidated after 24 hours.",
        "Logs are retained for 90 days."
    )

    /** Mixed texts requiring careful analysis. */
    val BOUNDARY_TEXTS: List<String> = listOf(
        "Reference number: 123456789",       // looks like SSN without dashes
        "Order #1234567890123456",            // looks like card without spaces
        "Error code: 404-00-0000",            // looks like SSN but isn't
        "Build version: 3.1.4.1592653",       // contains dots like IP
        "Document ID: DE89ABC123456789",      // starts like IBAN
        "Support ticket: TKT-123-456-789",    // dash pattern
        "Product SKU: CARD-4111-1111",        // contains CARD
        "Step 192 of 168",                    // numbers containing private IP prefix
        "Line 10 of 0.50 budget",             // contains IP-like values
        "Section 66.6: Prohibited uses",      // contains 666 area-like number
        "Chapter 9.0.0 in the manual",        // IP octet pattern
        "Version 10.0.0.1 released",          // actual IP embedded in version
        "User ID: 800-555-0199",              // phone formatted like SSN
        "The checksum is 0x12345678",         // hex number
        "UUID: 550e8400-e29b-41d4-a716-446655440000"  // UUID
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 8 — DATE AND TIME COLLECTIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Dates of birth in ISO format. */
    val DATES_ISO: List<String> = listOf(
        "1980-01-01", "1985-12-31", "1990-06-15", "1975-03-20", "2000-07-04",
        "1955-11-22", "1969-07-20", "1945-05-08", "2001-09-11", "1999-12-31",
        "1960-04-01", "1970-08-15", "1950-01-25", "1940-10-10", "1935-05-05",
        "1925-06-06", "2010-03-03", "2015-07-07", "2020-11-11", "1995-02-28",
        "1988-09-14", "1977-04-23", "1963-12-07", "1948-08-30", "1933-11-19"
    )

    /** Dates of birth in US format MM/DD/YYYY. */
    val DATES_US: List<String> = listOf(
        "01/01/1980", "12/31/1985", "06/15/1990", "03/20/1975", "07/04/2000",
        "11/22/1955", "07/20/1969", "05/08/1945", "09/11/2001", "12/31/1999",
        "04/01/1960", "08/15/1970", "01/25/1950", "10/10/1940", "05/05/1935",
        "06/06/1925", "03/03/2010", "07/07/2015", "11/11/2020", "02/28/1995"
    )

    /** Dates in EU format DD/MM/YYYY. */
    val DATES_EU: List<String> = listOf(
        "01/01/1980", "31/12/1985", "15/06/1990", "20/03/1975", "04/07/2000",
        "22/11/1955", "20/07/1969", "08/05/1945", "11/09/2001", "31/12/1999",
        "01/04/1960", "15/08/1970", "25/01/1950", "10/10/1940", "05/05/1935"
    )

    /** Dates in long format. */
    val DATES_LONG: List<String> = listOf(
        "January 1, 1980", "December 31, 1985", "June 15, 1990",
        "March 20, 1975", "July 4, 2000", "November 22, 1955",
        "July 20, 1969", "May 8, 1945", "September 11, 2001", "December 31, 1999",
        "1 January 1980", "31 December 1985", "15 June 1990",
        "20 March 1975", "4 July 2000"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 9 — COMPLEX DOCUMENT SCENARIOS
    // ═══════════════════════════════════════════════════════════════════════════

    val MEDICAL_RECORD_1 = """
        PATIENT RECORD — STRICTLY CONFIDENTIAL

        Patient Name: Jane Marie Doe
        Date of Birth: January 1, 1980
        Social Security Number: 123-45-6789
        Medical Record Number: MRN-12345678
        Insurance ID: BC-9876543210

        Primary Contact: (212) 555-0100
        Emergency Contact: (213) 555-0200
        Email: jane.doe@email.com

        Chief Complaint: Chest pain and shortness of breath.

        Vitals:
        - Blood Pressure: 140/90 mmHg
        - Heart Rate: 88 bpm
        - Temperature: 98.6°F
        - SpO2: 97%

        Assessment: Hypertension. Ordered labs and EKG.
        Physician: Dr. Smith, MD
        Date: 2024-01-15
    """.trimIndent()

    val FINANCIAL_APPLICATION_1 = """
        LOAN APPLICATION — CONFIDENTIAL

        Applicant: John B. Smith
        SSN: 234-56-7890
        DOB: 06/15/1975
        Email: john.smith@gmail.com
        Phone: (415) 555-0300

        Employment: Software Engineer at TechCorp Inc.
        Salary: $120,000/year

        Loan Request: $50,000 personal loan

        Primary Bank Account: 1234567890 (Routing: 021000021)

        Credit Cards:
        - Visa: 4111111111111111 (Exp: 12/2025)
        - Amex: 378282246310005 (Exp: 09/2026)

        Signature: ___________________   Date: 01/15/2024
    """.trimIndent()

    val HR_FORM_1 = """
        EMPLOYEE ONBOARDING — HR CONFIDENTIAL

        Full Name: Alice M. Johnson
        Social Security Number: 345-67-8901
        Date of Birth: March 20, 1990
        Personal Email: alice@personal.com
        Work Email: alice.johnson@company.com
        Emergency Phone: (617) 555-0400

        Department: Engineering
        Role: Senior Developer
        Start Date: 2024-02-01
        Salary: $115,000

        Direct Deposit Bank: Chase (Routing: 021000021)
        Account Number: 9876543210

        Emergency Contact: Bob Johnson (spouse), (617) 555-0500
    """.trimIndent()

    val ECOMMERCE_ORDER_1 = """
        ORDER CONFIRMATION — SECURE

        Order Number: ORD-2024-001234
        Customer: Charlie Brown
        Email: charlie.brown@example.com
        Phone: (312) 555-0600
        IP Address: 198.51.100.42

        Shipping Address: 123 Main St, Anytown, CA 90210
        Billing Address: Same as above

        Payment:
        Card: 5555555555554444 (Mastercard)
        Expiry: 03/2026
        Billing Zip: 90210

        Items:
        1. Widget Pro (x2): $49.99 each
        2. Gadget Plus (x1): $99.99

        Subtotal: $199.97
        Tax: $16.50
        Total: $216.47

        Order placed: 2024-01-15 16:37:22 UTC
    """.trimIndent()

    val GOVERNMENT_FORM_1 = """
        FEDERAL TAX FORM W-9 (SAMPLE/TEST)

        Name: David E. Miller
        Business Name: Miller Consulting LLC

        Taxpayer ID (SSN): 456-78-9012
        Or EIN: 12-3456789

        Address: 456 Oak Avenue, Springfield, IL 62701

        Certify: The number shown is my correct taxpayer ID number.

        Signature: ___________________
        Date: January 15, 2024
    """.trimIndent()

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 10 — EDGE CASE TEXT SCENARIOS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Empty and whitespace edge cases. */
    val EMPTY_STRINGS: List<String> = listOf(
        "", " ", "  ", "   ", "    ",
        "\t", "\n", "\r", "\r\n",
        "\t\t", "\n\n", "\u0000"
    )

    /** Very long strings. */
    val LONG_TEXTS: List<String> = listOf(
        "a".repeat(10000),
        "b".repeat(1000) + "123-45-6789" + "c".repeat(1000),
        "d".repeat(5000) + "user@example.com" + "e".repeat(5000),
        "The quick brown fox ".repeat(500),
        "Lorem ipsum dolor sit amet ".repeat(200)
    )

    /** Strings containing special characters. */
    val SPECIAL_CHAR_TEXTS: List<String> = listOf(
        "🎉 Party time! 🎊", "こんにちは世界", "مرحبا بالعالم",
        "Привет мир", "你好世界", "안녕하세요",
        "Ñoño niño", "Ü über Ä", "Ç est la vie",
        "Special: &<>\"/", "SQL: ' OR '1'='1",
        "Script: <script>alert(1)</script>",
        "Null bytes: \u0000\u0000", "Unicode: \uFFFE\uFEFF",
        "RTL: \u200F مرحبا \u200E"
    )

    /** JSON-formatted texts containing PII. */
    val JSON_TEXTS_WITH_PII: List<String> = listOf(
        """{"ssn": "123-45-6789", "name": "John Doe"}""",
        """{"card_number": "4111111111111111", "expiry": "12/25"}""",
        """{"email": "user@example.com", "password": "secret"}""",
        """{"phone": "+1 (212) 555-1234", "type": "mobile"}""",
        """{"ip_address": "192.168.1.100", "user_agent": "Mozilla/5.0"}""",
        """{"iban": "DE89370400440532013000", "bic": "DEUTDEDB"}""",
        """[{"ssn":"234-56-7890"},{"ssn":"345-67-8901"}]""",
        """{"profile": {"name": "Alice", "ssn": "456-78-9012", "dob": "1985-06-15"}}"""
    )

    /** CSV-formatted texts containing PII. */
    val CSV_TEXTS_WITH_PII: List<String> = listOf(
        "name,ssn,email\nJohn Doe,123-45-6789,john@example.com",
        "card,type,expiry\n4111111111111111,VISA,12/25\n5555555555554444,MC,03/26",
        "phone,type,country\n(212) 555-1234,mobile,US\n+44 20 7946 0958,landline,UK"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MODULE 11 — RISK LEVEL SCENARIOS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Text fragments with low PII risk. */
    val LOW_RISK_TEXTS: List<String> = listOf(
        "Please email support if you have questions.",
        "Our website is available 24/7 for your convenience.",
        "The documentation covers all major features.",
        "Release notes are available in the changelog.",
        "Configuration changes require a system restart.",
        "All data is processed according to privacy guidelines.",
        "Contact us through the official channel.",
        "For help, see the user guide in the app settings.",
        "Performance metrics are logged automatically.",
        "The system backup completed successfully."
    )

    /** Text fragments with medium PII risk (email or phone only). */
    val MEDIUM_RISK_TEXTS: List<String> = listOf(
        "Please reach me at alice@example.com for more info.",
        "Call (212) 555-0100 for support.",
        "My email is bob.jones@company.com and I'm available after 3 PM.",
        "Leave a message at +1-888-555-0200.",
        "Contact info: carol@startup.io or text 310-555-0300."
    )

    /** Text fragments with high PII risk (SSN or financial data). */
    val HIGH_RISK_TEXTS: List<String> = listOf(
        "My SSN is 123-45-6789.",
        "Credit card: 4111111111111111 expires 12/25.",
        "Wire to IBAN DE89370400440532013000.",
        "Tax ID: 234-56-7890, DOB: 01/01/1980.",
        "Card 5555555555554444, CVV 123, billing zip 90210."
    )

    /** Text fragments with critical PII risk (multiple sensitive types). */
    val CRITICAL_RISK_TEXTS: List<String> = listOf(
        "SSN: 123-45-6789, Card: 4111111111111111, Email: user@example.com, Phone: (212) 555-0100",
        "Patient DOB 01/01/1980, SSN 234-56-7890, Insurance card 5555555555554444",
        "Name: John Smith, SSN 345-67-8901, passport A12345678, card 378282246310005",
        "Wire \$50,000 to DE89370400440532013000 from account 1234567890 (routing 021000021), auth code via (212) 555-0100",
        "PII database dump: SSN,Card,Email\n123-45-6789,4111111111111111,user@example.com"
    )
}
