package com.privacyguard.testutil

/**
 * Extended Test Data Factory – Part 2
 * Comprehensive data collections for testing all PII validator types.
 * Includes 1000+ data points across SSNs, emails, phones, IPs, cards, IBANs, dates, and texts.
 */
object TestDataFactory2 {

    // ─────────────────────────────────────────────────────────────────────────
    // VALID SSNs – 300 entries grouped by area
    // ─────────────────────────────────────────────────────────────────────────

    val VALID_SSNS_SET2: List<String> = listOf(
        // Area 001-009 (Northeast – historical)
        "001-01-0001", "001-02-0002", "001-03-0003", "001-04-0004", "001-05-0005",
        "002-01-0001", "002-02-0002", "002-03-0003", "002-04-0004", "002-05-0005",
        "003-01-0001", "003-02-0002", "003-03-0003", "003-04-0004", "003-05-0005",
        "004-01-0001", "004-02-0002", "004-03-0003", "004-04-0004", "004-05-0005",
        "005-01-0001", "005-02-0002", "005-03-0003", "005-04-0004", "005-05-0005",
        "006-01-0001", "006-02-0002", "006-03-0003", "006-04-0004", "006-05-0005",
        "007-01-0001", "007-02-0002", "007-03-0003", "007-04-0004", "007-05-0005",
        "008-01-0001", "008-02-0002", "008-03-0003", "008-04-0004", "008-05-0005",
        "009-01-0001", "009-02-0002", "009-03-0003", "009-04-0004", "009-05-0005",
        // Area 010-019
        "010-01-0001", "010-10-1000", "010-20-2000", "010-30-3000", "010-40-4000",
        "011-01-0001", "011-10-1000", "011-20-2000", "011-30-3000", "011-40-4000",
        "012-01-0001", "012-10-1000", "012-20-2000", "012-30-3000", "012-40-4000",
        "013-01-0001", "013-10-1000", "013-20-2000", "013-30-3000", "013-40-4000",
        "014-01-0001", "014-10-1000", "014-20-2000", "014-30-3000", "014-40-4000",
        "015-01-0001", "015-10-1000", "015-20-2000", "015-30-3000", "015-40-4000",
        "016-01-0001", "016-10-1000", "016-20-2000", "016-30-3000", "016-40-4000",
        "017-01-0001", "017-10-1000", "017-20-2000", "017-30-3000", "017-40-4000",
        "018-01-0001", "018-10-1000", "018-20-2000", "018-30-3000", "018-40-4000",
        "019-01-0001", "019-10-1000", "019-20-2000", "019-30-3000", "019-40-4000",
        // Area 020-029
        "020-01-0001", "021-01-0001", "022-01-0001", "023-01-0001", "024-01-0001",
        "025-01-0001", "026-01-0001", "027-01-0001", "028-01-0001", "029-01-0001",
        // Area 030-039
        "030-01-0001", "031-01-0001", "032-01-0001", "033-01-0001", "034-01-0001",
        "035-01-0001", "036-01-0001", "037-01-0001", "038-01-0001", "039-01-0001",
        // Area 040-049
        "040-01-0001", "041-01-0001", "042-01-0001", "043-01-0001", "044-01-0001",
        "045-01-0001", "046-01-0001", "047-01-0001", "048-01-0001", "049-01-0001",
        // Area 050-059
        "050-01-0001", "051-01-0001", "052-01-0001", "053-01-0001", "054-01-0001",
        "055-01-0001", "056-01-0001", "057-01-0001", "058-01-0001", "059-01-0001",
        // Area 060-069
        "060-01-0001", "061-01-0001", "062-01-0001", "063-01-0001", "064-01-0001",
        "065-01-0001", "066-01-0001", "067-01-0001", "068-01-0001", "069-01-0001",
        // Area 070-079
        "070-01-0001", "071-01-0001", "072-01-0001", "073-01-0001", "074-01-0001",
        "075-01-0001", "076-01-0001", "077-01-0001", "078-01-0001", "079-01-0001",
        // Area 080-089
        "080-01-0001", "081-01-0001", "082-01-0001", "083-01-0001", "084-01-0001",
        "085-01-0001", "086-01-0001", "087-01-0001", "088-01-0001", "089-01-0001",
        // Area 090-099
        "090-01-0001", "091-01-0001", "092-01-0001", "093-01-0001", "094-01-0001",
        "095-01-0001", "096-01-0001", "097-01-0001", "098-01-0001", "099-01-0001",
        // Area 100-149
        "100-01-0001", "101-01-0001", "102-01-0001", "103-01-0001", "104-01-0001",
        "105-01-0001", "106-01-0001", "107-01-0001", "108-01-0001", "109-01-0001",
        "110-01-0001", "111-01-0001", "112-01-0001", "113-01-0001", "114-01-0001",
        "115-01-0001", "116-01-0001", "117-01-0001", "118-01-0001", "119-01-0001",
        "120-01-0001", "121-01-0001", "122-01-0001", "123-01-0001", "124-01-0001",
        "125-01-0001", "126-01-0001", "127-01-0001", "128-01-0001", "129-01-0001",
        "130-01-0001", "131-01-0001", "132-01-0001", "133-01-0001", "134-01-0001",
        "135-01-0001", "136-01-0001", "137-01-0001", "138-01-0001", "139-01-0001",
        "140-01-0001", "141-01-0001", "142-01-0001", "143-01-0001", "144-01-0001",
        "145-01-0001", "146-01-0001", "147-01-0001", "148-01-0001", "149-01-0001",
        // Area 200-249
        "200-01-0001", "201-01-0001", "202-01-0001", "203-01-0001", "204-01-0001",
        "205-01-0001", "206-01-0001", "207-01-0001", "208-01-0001", "209-01-0001",
        "210-01-0001", "211-01-0001", "212-01-0001", "213-01-0001", "214-01-0001",
        "215-01-0001", "216-01-0001", "217-01-0001", "218-01-0001", "219-01-0001",
        "220-01-0001", "221-01-0001", "222-01-0001", "223-01-0001", "224-01-0001",
        "225-01-0001", "226-01-0001", "227-01-0001", "228-01-0001", "229-01-0001",
        "230-01-0001", "231-01-0001", "232-01-0001", "233-01-0001", "234-01-0001",
        "235-01-0001", "236-01-0001", "237-01-0001", "238-01-0001", "239-01-0001",
        "240-01-0001", "241-01-0001", "242-01-0001", "243-01-0001", "244-01-0001",
        "245-01-0001", "246-01-0001", "247-01-0001", "248-01-0001", "249-01-0001",
        // Additional random valid SSNs
        "123-45-6789", "234-56-7890", "345-67-8901", "456-78-9012", "567-89-0123",
        "678-90-1234", "789-01-2345", "321-54-9876", "432-65-0987", "543-76-1098",
        "654-87-2109", "765-98-3210", "876-09-4321", "154-23-6789", "265-34-7890"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // INVALID SSNs – 100 entries
    // ─────────────────────────────────────────────────────────────────────────

    val INVALID_SSNS_SET2: List<String> = listOf(
        "", " ", "\t", "\n", "0", "00", "000", "0000", "00000",
        "000-00-0000", "666-00-0000", "900-00-0000", "999-99-9999",
        "000-12-3456", "001-00-1234", "123-45-0000", "666-45-6789",
        "901-01-0001", "902-01-0001", "903-01-0001", "904-01-0001", "905-01-0001",
        "910-01-0001", "920-01-0001", "930-01-0001", "940-01-0001", "950-01-0001",
        "960-01-0001", "970-01-0001", "980-01-0001", "990-01-0001", "999-01-0001",
        "ABC-DE-FGHI", "123-AB-5678", "123-45-WXYZ",
        "12-345-6789", "1234-56-789", "123-456-789", "123-45-67890",
        "123.45.6789", "123/45/6789", "123 456 789", "123*45*6789",
        "-123-45-6789", "123-45-6789-", "123--45-6789", "123-45--6789",
        "XXXXXXXXX", "000000000", "111111111", "222222222",
        "12345", "1234567", "12345678", "1234567890",
        "hello world", "!@#$%^&*()", "null", "undefined",
        "N/A", "n/a", "-", "--", "---", "----",
        "078-05-1120", "219-09-9999", "457-55-5462", // advertising test SSNs
        "(123) 45-6789", "[123-45-6789]", "{123-45-6789}", "<123-45-6789>",
        "123\u200045-6789", "1\u202323-45-6789", "12\u20043-45-6789",
        "１２３-４５-６７８９", "１２３４５６７８９",
        "123-45-6789 ", " 123-45-6789", "1 2 3 - 4 5 - 6 7 8 9",
        "one-two-three", "SSN", "social security", "ssn number",
        "000000001", "999999998", "0-0-1", "0-1-0", "1-0-0"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // VALID EMAILS – 300 entries
    // ─────────────────────────────────────────────────────────────────────────

    val VALID_EMAILS_SET2: List<String> = listOf(
        // Standard addresses
        "a@b.com", "b@c.net", "c@d.org", "d@e.io", "e@f.co",
        "ab@cd.ef", "abc@def.ghi", "abcd@efgh.ijkl", "abcde@fghij.klmno",
        "test@test.test", "email@email.email",
        // All major providers
        "user001@gmail.com", "user002@gmail.com", "user003@gmail.com", "user004@gmail.com", "user005@gmail.com",
        "user006@yahoo.com", "user007@yahoo.com", "user008@yahoo.co.uk", "user009@yahoo.fr", "user010@yahoo.de",
        "user011@outlook.com", "user012@outlook.co.uk", "user013@hotmail.com", "user014@hotmail.co.uk",
        "user015@live.com", "user016@live.co.uk", "user017@msn.com", "user018@windowslive.com",
        "user019@icloud.com", "user020@me.com", "user021@mac.com",
        "user022@protonmail.com", "user023@pm.me", "user024@proton.me",
        "user025@tutanota.com", "user026@tutamail.com",
        "user027@fastmail.com", "user028@fastmail.fm",
        "user029@zoho.com", "user030@zohomail.com",
        // With special chars in local
        "user.name@domain.com", "user-name@domain.com", "user_name@domain.com",
        "user+tag@domain.com", "user+tag1+tag2@domain.com",
        "firstname.lastname@domain.com", "first.middle.last@domain.com",
        "f.m.l@domain.com", ".user@domain.com", "user.@domain.com",
        // With numbers
        "user123@domain.com", "123user@domain.com", "12345@domain.com",
        "a1b2c3@d4e5f6.com", "test.123@example.456.com",
        // Multiple TLDs
        "user@domain.co.uk", "user@domain.com.au", "user@domain.co.jp",
        "user@domain.co.in", "user@domain.co.nz", "user@domain.co.za",
        "user@domain.com.br", "user@domain.com.mx", "user@domain.com.ar",
        "user@domain.org.uk", "user@domain.net.au", "user@domain.edu.au",
        // New TLDs
        "user@domain.io", "user@domain.app", "user@domain.dev", "user@domain.tech",
        "user@domain.online", "user@domain.store", "user@domain.shop", "user@domain.blog",
        "user@domain.media", "user@domain.news", "user@domain.info", "user@domain.biz",
        "user@domain.pro", "user@domain.today", "user@domain.global", "user@domain.world",
        "user@domain.solutions", "user@domain.systems", "user@domain.services",
        // Subdomains
        "user@mail.domain.com", "user@smtp.domain.com", "user@pop3.domain.com",
        "user@subdomain1.subdomain2.domain.com",
        "user@a.b.c.d.e.domain.com",
        // Very short
        "a@b.io", "x@y.co", "z@w.ca", "m@n.de", "p@q.fr",
        // Very long local
        "averylongemailusernamethatexceedsnormalexpectations@domain.com",
        "first.very.long.middle.name@long-domain-name-here.example.com",
        "firstname+category+subcategory+tag@verylongdomainname123.co.uk",
        // Case variations
        "User@Domain.COM", "USER@DOMAIN.COM", "user@DOMAIN.com", "USER@domain.com",
        "Mixed.Case@MixedCase.Com", "UPPERCASE@UPPERCASE.COM",
        // Role-based
        "admin@company.com", "info@company.com", "support@company.com",
        "sales@company.com", "marketing@company.com", "contact@company.com",
        "hello@company.com", "help@company.com", "billing@company.com",
        "accounts@company.com", "hr@company.com", "legal@company.com",
        "press@company.com", "media@company.com", "partners@company.com",
        "webmaster@company.com", "hostmaster@company.com", "postmaster@company.com",
        "abuse@company.com", "security@company.com",
        // Department-based
        "it@company.com", "dev@company.com", "ops@company.com", "sre@company.com",
        "qa@company.com", "pm@company.com", "design@company.com",
        "finance@company.com", "accounting@company.com", "payroll@company.com",
        // Country-specific domains
        "user@company.de", "user@company.fr", "user@company.es", "user@company.it",
        "user@company.nl", "user@company.be", "user@company.ch", "user@company.at",
        "user@company.se", "user@company.no", "user@company.dk", "user@company.fi",
        "user@company.pl", "user@company.cz", "user@company.hu", "user@company.ro",
        "user@company.jp", "user@company.cn", "user@company.kr", "user@company.in",
        "user@company.au", "user@company.nz", "user@company.ca", "user@company.mx",
        "user@company.br", "user@company.ar", "user@company.cl", "user@company.co",
        "user@company.za", "user@company.ng", "user@company.ke", "user@company.gh",
        // Numeric domains
        "user@123.com", "user@456.net", "user@789.org",
        // Common test emails
        "test@test.com", "example@example.com", "demo@demo.com",
        "sample@sample.com", "trial@trial.com", "temp@temp.com",
        "dummy@dummy.com", "fake@fake.com", "noreply@noreply.com",
        // Person-like
        "john.doe@example.com", "jane.doe@example.com", "john.smith@example.com",
        "jane.smith@example.com", "bob.jones@example.com", "alice.wang@example.com",
        "charlie.brown@example.com", "diana.prince@example.com",
        "edward.norton@example.com", "fiona.apple@example.com",
        "george.washington@example.com", "helen.keller@example.com",
        "ivan.the.terrible@example.com", "julia.roberts@example.com",
        "kevin.hart@example.com", "laura.ingalls@example.com",
        "michael.jordan@example.com", "nancy.drew@example.com",
        "oscar.wilde@example.com", "penny.wise@example.com",
        "robert.frost@example.com", "sarah.connor@example.com",
        "thomas.edison@example.com", "ursula.k@example.com",
        "victoria.beckham@example.com", "william.shakespeare@example.com",
        // Person-like with numbers
        "john.doe1@example.com", "jane.doe2@example.com",
        "user501@enterprise.com", "user502@enterprise.com",
        "user601@enterprise.net", "user602@enterprise.net",
        // Real-looking business
        "ceo@startup.io", "cfo@startup.io", "cto@startup.io",
        "vp.sales@startup.io", "vp.marketing@startup.io",
        "director@department.company.com", "manager@team.company.com",
        // Additional
        "investor.relations@publiccompany.com", "media.inquiries@publiccompany.com",
        "press.releases@publiccompany.com", "shareholder@publiccompany.com",
        "customer.success@saas.company", "account.manager@saas.company",
        "technical.support@software.co", "product.feedback@software.co",
        "security.report@company.com", "vulnerability@responsible.disclosure.com",
        "compliance@regulated.industry.com", "privacy@gdpr.compliant.com",
        "dpo@dataprotection.eu", "gdpr@privacy.org"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // INVALID EMAILS – 100 entries
    // ─────────────────────────────────────────────────────────────────────────

    val INVALID_EMAILS_SET2: List<String> = listOf(
        "", " ", "\t", "\n",
        "@", "@@", "@.", ".@", "@.com", "user@", "user",
        "user@.com", "user@.", "user.@.", ".user@.com",
        "user name@domain.com", "user@domain com", "user @domain.com",
        "user@ domain.com", " user@domain.com", "user@domain.com ",
        "user@@domain.com", "user@domain@.com", "user@domain..com",
        "user@domain.c", "user@domain.", "user@.domain.com",
        "user@domain-.com", "user@-domain.com",
        "user@domain.123456789012345678", // TLD too long
        "user@[192.168.1.1]", // IP in brackets (debatable – often valid)
        "#user@domain.com", "user#@domain.com", "user@domain#.com",
        "user@domain.c@m", "user@domain@com", "@domain.com",
        "пользователь@example.com", // Cyrillic (may be valid in IDN)
        "user@例子.测试", // Punycode valid but raw unicode may fail
        "userATdomain.com", "user(at)domain.com",
        "user at domain dot com",
        "just a string", "not an email at all",
        "1234", "abcdef", "!@#$%^",
        "user@", "@domain.com", "user.domain.com",
        "missing-at-sign.example.com", "user@missingtld",
        "user@.missingtld", "user@-.com",
        "null", "undefined", "none", "N/A", "n/a",
        "test@test@test.com",
        "\"user\"@domain.com", // quoted - debatable
        "a\"b@domain.com",
        "user@[123.456.789.012]", // invalid IP
        "用户@sample.com", // Chinese chars in local
        "user@xn--", // partial punycode
        "abc@abc.", "abc@.abc", ".abc@abc.com",
        "abc.@abc.com", "abc@abc.com.",
        "email@email", // no TLD dot
        "e@b", // no TLD
        "a@b", // no TLD (not cc tld)
        "verylong" + "a".repeat(250) + "@domain.com" // too long
    )

    // ─────────────────────────────────────────────────────────────────────────
    // VALID CREDIT CARD NUMBERS – 200 entries (Luhn-valid formats)
    // ─────────────────────────────────────────────────────────────────────────

    val VALID_CARDS_SET2: List<String> = listOf(
        // Visa (start with 4, length 16)
        "4111111111111111", "4242424242424242", "4000056655665556",
        "4000002500003155", "4000002760003184", "4000003720000278",
        "4000004840008001", "4000005260000236", "4000007240000007",
        "4000007520000008", "4111111145551142", "4012888888881881",
        "4222222222222", // Visa 13-digit (old)
        "4532015112830366", "4485771175621890", "4716219000023345",
        "4916338506082832", "4539578763621486", "4024007137660136",
        "4000056655665556", "4000002500003155",
        // Mastercard (start with 51-55 or 2221-2720, length 16)
        "5425233430109903", "5105105105105100", "5500005555555559",
        "5200828282828210", "5204230000000009", "5100290029002909",
        "5362650234876302", "5543965000000007", "5170185001064235",
        "5490154003397814", "5302029680753200", "5311079811925174",
        "5102065748699988", "5425116566453684", "5200000000000007",
        "5200000000000114", "5200000000000122", "5200000000000130",
        "5200000000000148", "5500000000000004",
        // Mastercard 2-series
        "2221000000000009", "2223000048410010", "2300000000000013",
        "2500000000000001", "2718929006754025", "2720000000000005",
        // Amex (start with 34 or 37, length 15)
        "371449635398431", "378282246310005", "370000000000002",
        "378734493671000", "341111111111111", "343434343434343",
        "374251018720955", "370000000000002",
        // Discover (start with 6011, 622126-622925, 644-649, 65, length 16)
        "6011111111111117", "6011000990139424", "6011601160116611",
        "6011564654789032", "6011000400000000", "6500000000000002",
        "6500000000000010", "6543210987654321",
        // JCB (start with 3528-3589, length 16)
        "3530111333300000", "3566002020360505", "3528000000000007",
        "3530111333300000",
        // Diners Club (start with 300-305, 36, 38, length 14)
        "30569309025904", "38520000023237", "36491580399388",
        "30054992773938", "38000000000006",
        // Maestro and other
        "6304000000000000", "6759649826438453", "6763220000000000",
        // UnionPay (start with 62, length 16-19)
        "6200000000000005", "6200000000000013", "6200000000000021",
        "6200000000000039", "6200000000000047",
        // Additional Visa
        "4929420809913939", "4024007125476637", "4024007127818024",
        "4532644368932390", "4485275742308327", "4000056655665556",
        "4000002500003155", "4000002760003184", "4000003720000278",
        "4000004840008001", "4000005260000236", "4000007240000007",
        // Additional MC
        "5425233430109903", "5105105105105100", "5500005555555559",
        "5200828282828210", "5204230000000009", "5100290029002909",
        "5362650234876302", "5543965000000007", "5170185001064235",
        "5490154003397814",
        // Additional Amex
        "371449635398431", "378282246310005", "370000000000002",
        "378734493671000", "341111111111111",
        // Additional Discover
        "6011111111111117", "6011000990139424", "6011601160116611",
        "6011564654789032", "6543210987654321",
        // Formatted versions
        "4111-1111-1111-1111", "4242-4242-4242-4242",
        "5425-2334-3010-9903", "5105-1051-0510-5100",
        "3714 496353 98431", "3782 822463 10005",
        "6011 1111 1111 1117", "6011 0009 9013 9424",
        "3530 1113 3330 0000", "3566 0020 2036 0505",
        "4111 1111 1111 1111", "4242 4242 4242 4242",
        "5500 0055 5555 5559", "5200 8282 8282 8210"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // INVALID CREDIT CARD NUMBERS – 100 entries
    // ─────────────────────────────────────────────────────────────────────────

    val INVALID_CARDS_SET2: List<String> = listOf(
        "", " ", "\t", "\n",
        "0", "1", "12", "123", "1234", "12345",
        "1234567890", "12345678901", "123456789012",
        "1234567890123", // 13 digits non-Visa
        "12345678901234567890", // 20 digits too long
        "AAAAAAAAAAAAAAAA", "ABCDEFGHIJKLMNOP",
        "4111111111111112", // Luhn fail
        "4111111111111113", "4111111111111114",
        "5425233430109904", "5425233430109905",
        "371449635398432", "371449635398433",
        "6011111111111118", "6011111111111119",
        "0000000000000000", "1111111111111111",
        "2222222222222222", "3333333333333333",
        "6666666666666666", "7777777777777777",
        "8888888888888888", "9999999999999999",
        "4111-1111-1111-1112", // formatted but Luhn fail
        "5425-2334-3010-9904",
        "null", "undefined", "none", "N/A",
        "card number", "credit card", "debit card",
        "4111 1111 1111 1112", // space-formatted but Luhn fail
        "-4111111111111111", "4111111111111111-",
        "+4111111111111111",
        "4111.1111.1111.1111", // dot separated invalid
        "41111111111111110000", // too long
        "00000000000000", "99999999999999",
        "1234567890123456789", // 19 digits that fail Luhn
        "9999999999999999", "0000000000000000",
        "4111111111111110" // Luhn check digit wrong
    )

    // ─────────────────────────────────────────────────────────────────────────
    // VALID IBANS – 100 entries by country
    // ─────────────────────────────────────────────────────────────────────────

    val VALID_IBANS_SET2: List<String> = listOf(
        // Germany DE (22)
        "DE89370400440532013000", "DE02100500000054540402", "DE88200400600959149847",
        "DE43500105178623946701",
        // United Kingdom GB (22)
        "GB29NWBK60161331926819", "GB82WEST12345698765432", "GB33BUKB20201555555555",
        "GB94BARC20201530093459",
        // France FR (27)
        "FR7630006000011234567890189", "FR7614508001800400773860024",
        // Netherlands NL (18)
        "NL91ABNA0417164300", "NL46RABO0142381636", "NL20INGB0001234567",
        // Belgium BE (16)
        "BE68539007547034", "BE71096123456769", "BE43068999999501",
        // Spain ES (24)
        "ES9121000418450200051332", "ES6000491500051234567892",
        // Italy IT (27)
        "IT60X0542811101000000123456", "IT23A0336844430152923804660",
        // Switzerland CH (21)
        "CH9300762011623852957", "CH5604835012345678009",
        // Austria AT (20)
        "AT611904300234573201", "AT483200000012345864",
        // Sweden SE (24)
        "SE4550000000058398257466", "SE7280000810340009783242",
        // Norway NO (15)
        "NO9386011117947", "NO4517634000015",
        // Denmark DK (18)
        "DK5000400440116243", "DK9520000123456789",
        // Poland PL (28)
        "PL61109010140000071219812874", "PL27114020040000300201355387",
        // Portugal PT (25)
        "PT50000201231234567890154",
        // Finland FI (18)
        "FI2112345600000785", "FI9214283500171141",
        // Greece GR (27)
        "GR1601101250000000012300695",
        // Luxembourg LU (20)
        "LU280019400644750000",
        // Czech Republic CZ (24)
        "CZ6508000000192000145399",
        // Hungary HU (28)
        "HU42117730161111101800000000",
        // Romania RO (24)
        "RO49AAAA1B31007593840000",
        // Turkey TR (26)
        "TR330006100519786457841326",
        // Russia RU (33)
        "RU0204452560040702810412345678901",
        // United Arab Emirates AE (23)
        "AE070331234567890123456",
        // Saudi Arabia SA (24)
        "SA0380000000608010167519",
        // Israel IL (23)
        "IL620108000000099999999",
        // Cyprus CY (28)
        "CY17002001280000001200527600",
        // Malta MT (31)
        "MT84MALT011000012345MTLCAST001S",
        // Slovenia SI (19)
        "SI56263300012039086",
        // Croatia HR (21)
        "HR1210010051863000160",
        // Bulgaria BG (22)
        "BG80BNBG96611020345678",
        // Latvia LV (21)
        "LV80BANK0000435195001",
        // Lithuania LT (20)
        "LT121000011101001000",
        // Estonia EE (20)
        "EE382200221020145685",
        // Iceland IS (26)
        "IS140159260076545510730339",
        // Ireland IE (22)
        "IE29AIBK93115212345678",
        // Slovakia SK (24)
        "SK3112000000198742637541",
        // Additional Germany
        "DE75512108001245126199", "DE01234567890123456789",
        // Additional UK
        "GB12BARC20000055779911", "GB36LOYD30949301273801",
        // Additional France
        "FR1420041010050500013M02606",
        // Additional Spain
        "ES0700120345030000067905",
        // Additional Italy
        "IT73O0306901765100000300046",
        // Additional Netherlands
        "NL08RABO0428247581",
        // Additional Belgium
        "BE75945204640733",
        // More...
        "DE12345678901234567890",
        "GB98MIDL07009312345678",
        "FR7617569000703461796692680",
        "NL24INGB0001234560",
        "BE54798260008500",
        "ES2914659790522214965893",
        "IT45Z0200802428000104030729",
        "CH5604835012345678009",
        "AT021100000622888600",
        "SE8550000000054400059334"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // INVALID IBANS – 100 entries
    // ─────────────────────────────────────────────────────────────────────────

    val INVALID_IBANS_SET2: List<String> = listOf(
        "", " ", "\t", "A", "AB", "ABC", "ABCD",
        "DE", "GB", "FR", "NL", "BE",
        "DE00", "GB00", "FR00", "NL00",
        // Wrong check digits
        "DE00370400440532013000", "GB00NWBK60161331926819",
        "DE01370400440532013000", "GB01NWBK60161331926819",
        "DE99370400440532013000", "GB99NWBK60161331926819",
        // Wrong length
        "DE89370400440532013",    // too short
        "DE8937040044053201300000", // too long
        "GB29NWBK601613319268",    // too short
        "GB29NWBK6016133192681900", // too long
        // Invalid country code
        "XX89370400440532013000", "ZZ29NWBK60161331926819",
        "11ABCDEF0123456789012", "99ABCDEF0123456789012",
        // Letters where numbers expected
        "DEABCDEF0123456789AB", "GBABCDNWBKABCDEFABCD",
        // Spaces
        "DE89 3704 0044 0532 0130 00", "GB29 NWBK 6016 1331 9268 19",
        // Dashes
        "DE89-3704-0044-0532-0130-00",
        // Lowercase
        "de89370400440532013000", "gb29nwbk60161331926819",
        // Garbled
        "DE$$370400440532013000", "GB29NWBK!!!!!331926819",
        "DE89370400440532013@00", "GB29NWBK6016133192%819",
        // Null-like
        "null", "undefined", "none", "N/A", "n/a",
        "IBAN", "iban", "IBAN:", "iban:",
        // Unicode
        "DE89370400440532013０00", // unicode zero
        "GB29ＮWBK60161331926819",  // unicode N
        // Repetitive
        "AAAAAAAAAAAAAAAAAAAAAA", "00000000000000000000000",
        "BBBBBBBBBBBBBBBBBBBBBB",
        // Check digit 00 and 01 always invalid
        "DE00100500000054540402", "DE01100500000054540402",
        "GB00BARC20201530093459", "GB01BARC20201530093459",
        // Numeric only
        "1234567890123456789012", "0000000000000000000000",
        "9999999999999999999999"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // VALID ABA ROUTING NUMBERS – 100 entries
    // ─────────────────────────────────────────────────────────────────────────

    val VALID_ABA_ROUTING: List<String> = listOf(
        "021000021", "021000089", "021001033", "021100361", "021101108",
        "021200025", "021200339", "021201501", "021202106", "021202367",
        "021300077", "021300461", "021301115", "021302649", "021303618",
        "021400078", "021401968", "021500120", "021600148", "021700030",
        "022000020", "022000046", "022000065", "022001045", "022300100",
        "026005092", "026007986", "026008849", "026009593", "026010786",
        "029200100", "031000053", "031000058", "031000098", "031000117",
        "031100157", "031100209", "031100225", "031176110", "031201360",
        "033000905", "036001808", "041000014", "041000124", "041000153",
        "042000042", "042000314", "042100175", "042101512", "043000096",
        "043000179", "044000037", "044000077", "044100012", "044100076",
        "051000017", "051000020", "051400549", "053000219", "053100494",
        "054001547", "055001096", "055002707", "056100202", "061000010",
        "061000020", "061000058", "061000104", "061092387", "063000026",
        "065000090", "065000232", "067000067", "067012112", "069000069",
        "071000013", "071000039", "071000052", "071001066", "071001122",
        "073000176", "075000012", "075000022", "075000034", "079000012",
        "081000032", "081000062", "082000073", "083000108", "084000026",
        "091000019", "091000022", "091000042", "091001456", "091010286",
        "101000019", "101000028", "101000035", "101000048", "101200453"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // VALID DATES – 200 entries
    // ─────────────────────────────────────────────────────────────────────────

    val VALID_ISO_DATES: List<String> = listOf(
        "1900-01-01", "1910-02-14", "1920-03-21", "1930-04-15", "1940-05-10",
        "1950-06-22", "1960-07-04", "1970-08-28", "1980-09-15", "1990-10-31",
        "2000-11-11", "2010-12-25", "2020-01-01", "2023-06-15", "2024-02-29",
        "1945-05-08", "1969-07-20", "1989-11-09", "2001-09-11", "2011-03-11",
        "1950-01-01", "1951-01-01", "1952-01-01", "1953-01-01", "1954-01-01",
        "1955-01-01", "1956-01-01", "1957-01-01", "1958-01-01", "1959-01-01",
        "1960-01-01", "1961-01-01", "1962-01-01", "1963-01-01", "1964-01-01",
        "1965-01-01", "1966-01-01", "1967-01-01", "1968-01-01", "1969-01-01",
        "1970-01-01", "1971-01-01", "1972-01-01", "1973-01-01", "1974-01-01",
        "1975-01-01", "1976-01-01", "1977-01-01", "1978-01-01", "1979-01-01",
        "1980-01-01", "1981-01-01", "1982-01-01", "1983-01-01", "1984-01-01",
        "1985-01-01", "1986-01-01", "1987-01-01", "1988-01-01", "1989-01-01",
        "1990-01-01", "1991-01-01", "1992-01-01", "1993-01-01", "1994-01-01",
        "1995-01-01", "1996-01-01", "1997-01-01", "1998-01-01", "1999-01-01",
        "2000-01-01", "2001-01-01", "2002-01-01", "2003-01-01", "2004-01-01",
        "2005-01-01", "2006-01-01", "2007-01-01", "2008-01-01", "2009-01-01",
        "2010-01-01", "2011-01-01", "2012-01-01", "2013-01-01", "2014-01-01",
        "2015-01-01", "2016-01-01", "2017-01-01", "2018-01-01", "2019-01-01",
        "2020-01-01", "2021-01-01", "2022-01-01", "2023-01-01",
        "1980-01-31", "1980-02-29", "1980-03-31", "1980-04-30", "1980-05-31",
        "1980-06-30", "1980-07-31", "1980-08-31", "1980-09-30", "1980-10-31",
        "1980-11-30", "1980-12-31",
        "1985-01-15", "1985-02-15", "1985-03-15", "1985-04-15", "1985-05-15",
        "1985-06-15", "1985-07-15", "1985-08-15", "1985-09-15", "1985-10-15",
        "1985-11-15", "1985-12-15",
        "1990-02-28", "1992-02-29", "1996-02-29", "2000-02-29", "2004-02-29",
        "2008-02-29", "2012-02-29", "2016-02-29", "2020-02-29",
        "2023-01-01", "2023-01-31", "2023-02-28", "2023-03-31", "2023-04-30",
        "2023-05-31", "2023-06-30", "2023-07-31", "2023-08-31", "2023-09-30",
        "2023-10-31", "2023-11-30", "2023-12-31",
        "1970-01-01", "2038-01-19", "1901-12-13", "2106-02-07",
        "2000-06-15", "1975-11-22", "1988-04-07", "1993-08-30",
        "1967-02-14", "1955-09-05", "1942-06-22",
        "2023-03-14", "2023-06-28", "2023-09-22", "2023-12-07",
        "2024-01-15", "2024-02-29", "2024-03-20", "2024-06-01"
    )

    val VALID_US_DATES: List<String> = listOf(
        "01/01/1970", "12/31/1999", "06/15/1985", "03/22/1978", "11/07/1992",
        "04/01/2000", "07/04/1976", "10/31/1989", "02/14/1965", "09/30/2005",
        "01/15/1980", "02/29/1980", "03/31/1980", "04/30/1980", "05/31/1980",
        "06/30/1980", "07/31/1980", "08/31/1980", "09/30/1980", "10/31/1980",
        "11/30/1980", "12/31/1980",
        "01/01/1950", "02/28/1950", "03/15/1960", "04/20/1970", "05/25/1980",
        "06/30/1990", "07/04/2000", "08/15/2010", "09/22/2020", "10/31/2023"
    )

    val VALID_EU_DATES: List<String> = listOf(
        "01.01.1970", "31.12.1999", "15.06.1985", "22.03.1978", "07.11.1992",
        "01.04.2000", "04.07.1976", "31.10.1989", "14.02.1965", "30.09.2005",
        "15.01.1980", "29.02.1980", "31.03.1980", "30.04.1980", "31.05.1980",
        "30.06.1980", "31.07.1980", "31.08.1980", "30.09.1980", "31.10.1980",
        "30.11.1980", "31.12.1980"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // COMPLEX DOCUMENT SAMPLES – 50 entries
    // ─────────────────────────────────────────────────────────────────────────

    val COMPLEX_DOCS: List<String> = listOf(
        """
        MEDICAL RECORD
        Patient: Alice Johnson, DOB: 1985-03-22, Gender: F
        SSN: 123-45-6789, MRN: MRN-789456
        Contact: alice.johnson@email.com, (555) 123-4567
        Insurance: BCBS ID 987654321, Group: GRP-001
        Emergency Contact: Bob Johnson (555) 234-5678
        """.trimIndent(),

        """
        FINANCIAL APPLICATION
        Applicant: John Smith, SSN: 234-56-7890, DOB: 1978-11-15
        Email: john.smith@banking.com, Phone: (555) 987-6543
        Annual Income: $75,000, Employment: ABC Corp
        Credit Card: 4532015112830366, Routing: 021000021, Account: 9876543210
        """.trimIndent(),

        """
        HR EMPLOYEE FILE
        Name: Carol White, Employee ID: EMP-002345
        SSN: 345-67-8901, DOB: 1990-06-30
        Personal Email: carol.white@gmail.com, Work: carol.white@company.com
        Home Phone: (555) 456-7890, Mobile: (555) 345-6789
        Emergency: Dave White (555) 567-8901
        """.trimIndent(),

        """
        E-COMMERCE ORDER
        Customer: Eve Davis, Email: eve.davis@shop.com
        Phone: +1-555-678-9012
        Card: 5425233430109903, Expiry: 12/26, CVV: ***
        Billing Address: 100 Main St, Anytown, CA 90210
        Shipping IP: 203.0.113.42
        """.trimIndent(),

        """
        GOVERNMENT FORM
        Full Name: Frank Miller, SSN: 456-78-9012
        Date of Birth: April 15, 1968
        Passport: A12345678, Country: US
        Contact: frank.miller@gov.example.com, (555) 789-0123
        Address: 200 Oak Ave, Capital City, DC 20001
        """.trimIndent(),

        """
        INSURANCE CLAIM
        Claimant: Grace Lee, SSN: 567-89-0123, DOB: 1975-09-10
        Policy Number: POL-12345678, Claim #: CLM-2024-001
        Date of Loss: 2024-01-15, Type: Auto
        Contact: grace.lee@insurance.com, (555) 890-1234
        Vehicle VIN: 1HGCM82633A004352
        """.trimIndent(),

        """
        WIRE TRANSFER
        Sender: Henry Brown
        Routing: 026009593, Account: 1234567890123
        IBAN: DE89370400440532013000
        SWIFT: DEUTDEDB
        Amount: EUR 5,000.00, Reference: INV-789
        Beneficiary: Omega GmbH
        """.trimIndent(),

        """
        LOGIN AUDIT LOG
        [2024-01-15 09:30:00] WARN user=iris.tan@company.com ip=198.51.100.5 action=failed_login
        [2024-01-15 09:30:15] INFO user=iris.tan@company.com ip=198.51.100.5 action=login_success
        [2024-01-15 09:45:22] INFO user=iris.tan@company.com ip=198.51.100.5 action=view_ssn ssn_last4=6789
        """.trimIndent(),

        """
        BACKGROUND CHECK REQUEST
        Subject: Jack Wilson
        SSN: 678-90-1234, DOB: 1982-12-01
        Driver's License: A9876543, State: CA
        Email: jack.wilson@bgcheck.com
        Current Address: 300 Elm St, Los Angeles, CA 90001
        Previous Address: 400 Pine St, San Francisco, CA 94102
        """.trimIndent(),

        """
        API KEY CONFIG (DEVELOPMENT)
        Environment: dev
        Stripe_key: sk_tst_4eC39HqLyjWDarjtT1zdp7dc
        Github_pat: ghp_abcdefghijklmnopqrstuvwxyz12345
        AWS_access: AKIAIOSFODNN7EXAMPLE
        Admin email: admin@company.com
        Log IP: 192.168.1.100
        """.trimIndent(),

        """
        PATIENT DISCHARGE SUMMARY
        Patient Name: Karen Thomas, Age: 45
        Medical Record: MRN-00456789
        SSN: 789-01-2345
        Date of Birth: 1979-03-15
        Attending Physician: Dr. James Reid
        Diagnosis: Type 2 Diabetes, HTN
        Discharge Date: 2024-01-20
        Follow-up: karen.thomas@patient.com, (555) 012-3456
        """.trimIndent(),

        """
        LOAN APPLICATION
        Applicant: Laura Kim, SSN: 321-54-9876
        Co-Applicant: Mike Kim, SSN: 432-65-0987
        DOB (Applicant): 1988-07-22
        Property: 500 Harbor Blvd, Miami, FL 33101
        Loan Amount: $350,000
        Email: laura.kim@loanapp.com
        Card on file: 6011111111111117
        Routing: 021000021
        """.trimIndent(),

        """
        COMPLIANCE REPORT
        Review Date: 2024-02-01
        Subject: Nancy Chen
        SSN Exposure: 543-76-1098 (found in email body)
        Card Exposure: 371449635398431 (found in database log)
        IP Logged: 203.0.113.100
        Email Exposed: nancy.chen@compliance.com
        Risk Level: CRITICAL
        Action Required: Immediate notification and remediation
        """.trimIndent(),

        """
        REGISTRATION FORM
        First Name: Oscar, Last Name: Rivera
        Date of Birth: 07/14/1995
        Social Security: 654-87-2109
        Email Address: oscar.rivera@newuser.com
        Phone: (555) 543-2109
        Credit Card: 4485771175621890
        Billing Zip: 10001
        IP Address: 198.51.100.200
        """.trimIndent(),

        """
        TAX RETURN SUMMARY
        Taxpayer: Penny White, EIN/SSN: 765-98-3210
        Spouse: Paul White, SSN: 876-09-4321
        Filing Year: 2023
        Total Income: $120,000
        Tax Owed: $18,500
        Refund: $2,000
        Email: penny.white@taxfiling.com
        Bank Routing: 044000037
        Bank Account: 1234509876
        """.trimIndent(),

        """
        INTERNATIONAL WIRE
        Originator: Quinn Davis
        IBAN: GB29NWBK60161331926819
        BIC/SWIFT: HSBCGB2L
        Bank: HSBC Bank UK
        Amount: GBP 10,000.00
        OUR Reference: QWIRE-2024-123
        Beneficiary Email: qrecipient@international.bank.co.uk
        """.trimIndent(),

        """
        CUSTOMER SUPPORT TICKET #CS-789456
        Customer: Robert Brown
        Email: robert.brown@customer.com
        Phone: +1-800-555-0199
        Account SSN (last 4): ***-**-1234
        Card on File (last 4): ****-****-****-5678
        Issue: Unauthorized charge of $299.99
        Reporter IP: 198.51.100.50
        """.trimIndent(),

        """
        IMMIGRATION FORM I-551
        Family Name: GARCIA, Given Name: SOFIA
        Date of Birth: 15/08/1982
        Country of Birth: MEXICO
        Passport: MX1234567
        SSN (if any): 110-22-3344
        Email: sofia.garcia@immigration.gov.example
        Phone: +52 55 1234 5678
        """.trimIndent(),

        """
        PAYROLL RECORD
        Employee: Thomas Evans, ID: E-004567
        SSN: 220-33-4455, DOB: 1986-01-30
        Bank Routing: 071000013
        Account: 9876543210987
        Pay Type: Direct Deposit
        Gross: $5,833.33/month
        Email: thomas.evans@hrpayroll.com
        """.trimIndent(),

        """
        EDUCATION TRANSCRIPT REQUEST
        Student: Ursula Pham, Student ID: STU-00789
        SSN: 330-44-5566
        DOB: 1999-05-12
        Email: ursula.pham@university.edu
        Phone: (555) 765-4321
        Graduating Class: 2023
        GPA: 3.85
        Program: Computer Science BS
        """.trimIndent(),

        """
        RENTAL APPLICATION
        Applicant: Victor Santos, DOB: 1984-10-20
        SSN: 440-55-6677, Email: victor.santos@renting.com
        Phone: (555) 876-5432
        Monthly Income: $4,500
        Employer: XYZ Corp, (555) 234-5678
        Previous Landlord: (555) 345-6789
        Desired Move-in: 2024-03-01
        Credit Card for Deposit: 5543965000000007
        """.trimIndent(),

        """
        MEMBERSHIP ENROLLMENT
        Member: Wendy Clark, SSN: 550-66-7788
        DOB: 1977-02-14, Gender: F
        Email: wendy.clark@membership.org
        Phone: +1-555-901-2345
        Emergency Contact: Xavier Clark (555) 012-3456
        Payment: Card 6011111111111117
        Routing: 026009593
        """.trimIndent(),

        """
        TECH INCIDENT REPORT
        Reporter: Xavier Liu, Email: xavier.liu@security.com
        IP Logged: 203.0.113.75
        Timestamp: 2024-01-15T14:30:00Z
        Incident: PII leak found in logs
        Leaked data: SSN 660-77-8899, card 4242424242424242, email admin@company.com
        Severity: HIGH
        """.trimIndent(),

        """
        EMERGENCY CONTACT FORM
        Patient: Yvonne Torres
        SSN: 770-88-9900, DOB: 1963-07-28
        Primary Contact: Zane Torres
        Phone: (555) 890-1234, Email: zane.torres@family.com
        Relationship: Spouse
        Secondary: Bob Torres (555) 901-2345
        Insurance IBAN: DE89370400440532013000
        """.trimIndent(),

        """
        ACCOUNT TAKEOVER ATTEMPT LOG
        [2024-01-15 02:15:33] ALERT user=admin@critical.system.com ip=198.51.100.99
        Attempted account: john.doe@company.com
        Stolen info used: SSN 111-11-1111, card 4111111111111111
        Device fingerprint: Mozilla/5.0 (Windows NT 10.0)
        Action: Account frozen, user notified
        """.trimIndent()
    )

    // ─────────────────────────────────────────────────────────────────────────
    // CLEAN TEXTS (no PII) – 100 entries
    // ─────────────────────────────────────────────────────────────────────────

    val CLEAN_TEXTS: List<String> = listOf(
        "The weather today is sunny and warm.",
        "Meeting scheduled for next Thursday at 3pm.",
        "The quarterly report shows 15% growth.",
        "Please review the attached document.",
        "The project deadline is approaching.",
        "Team lunch is at noon today.",
        "The new feature was deployed successfully.",
        "Bug fix is ready for review.",
        "The database migration completed.",
        "Performance metrics look great this week.",
        "Hello, how are you doing today?",
        "The sky is blue and the grass is green.",
        "Mathematics is the language of the universe.",
        "The library opens at 9am on weekdays.",
        "Please submit your expense reports by Friday.",
        "The conference is scheduled for next month.",
        "All tests are passing now.",
        "The CI/CD pipeline ran successfully.",
        "Code review approved and merged.",
        "Release 2.1.0 is live.",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "Ut enim ad minim veniam, quis nostrud exercitation.",
        "Duis aute irure dolor in reprehenderit in voluptate velit.",
        "Excepteur sint occaecat cupidatat non proident.",
        "The quick brown fox jumps over the lazy dog.",
        "To be or not to be, that is the question.",
        "All that glitters is not gold.",
        "A stitch in time saves nine.",
        "Actions speak louder than words.",
        "The product roadmap for Q2 includes three features.",
        "Sprint planning is Monday at 10am.",
        "The API rate limit is 1000 requests per minute.",
        "Database indexes have been optimized.",
        "The load balancer is distributing traffic evenly.",
        "Memory usage is within acceptable limits.",
        "CPU utilization peaked at 75% during the test.",
        "The CDN cache hit rate is 98%.",
        "Backup completed successfully at 3am.",
        "The SSL certificate renews in 30 days.",
        "Version 3.0 introduces breaking changes.",
        "The migration script should run in under 5 minutes.",
        "Unit test coverage is at 87%.",
        "The static analysis found 3 warnings.",
        "Documentation updated for the new API.",
        "The changelog has been updated.",
        "Release notes are ready for review.",
        "The hotfix was deployed to production.",
        "Monitoring alerts are configured.",
        "The on-call rotation starts Monday.",
        "Annual review documents are ready.",
        "Budget planning for next year begins soon.",
        "The office supply order has been placed.",
        "The new hire orientation is this week.",
        "Parking permits are available at reception.",
        "The cafeteria menu this week includes…",
        "Please book rooms through the portal.",
        "The fire drill is next Tuesday at 2pm.",
        "IT maintenance window is Saturday 11pm-3am.",
        "WiFi password for guests has been updated.",
        "The printer on floor 3 is back online.",
        "Company picnic is July 15 at Riverside Park.",
        "The all-hands meeting is next Friday.",
        "Performance reviews start next week.",
        "Training resources are in the learning portal.",
        "The new benefits package is effective Jan 1.",
        "Holiday schedule posted in the break room.",
        "The CEO town hall is Thursday at 4pm.",
        "Teambuilding event is postponed to April.",
        "The office reopening plan is under review.",
        "Remote work policy updated.",
        "The pantry has been restocked.",
        "Construction on floor 7 starts Monday.",
        "The gym discount offer has been extended.",
        "Bike-to-work commuter benefits now available.",
        "The carpool program has new participants.",
        "Green initiative: paper reduction underway.",
        "The recycling bins have been moved.",
        "Lights-off policy now in effect after 8pm.",
        "The building HVAC will be serviced Thursday.",
        "Ergonomic assessment program is available.",
        "The sit-stand desk ordering window is open.",
        "Monthly newsletter is ready to read.",
        "The blog post has been published.",
        "Social media post scheduled for Monday.",
        "The webinar attracted 500 attendees.",
        "The press release is approved.",
        "Customer satisfaction score improved.",
        "Net Promoter Score increased by 5 points.",
        "The partnership announcement is next week.",
        "Award nomination period is open.",
        "The podcast episode 42 is now live.",
        "New case study published on the website.",
        "The video tutorial series is complete.",
        "Product Hunt launch is scheduled.",
        "The beta testing group has been notified.",
        "Feedback from users has been collected.",
        "A/B test results favor option B.",
        "The feature flag was enabled for 10% of users.",
        "Rollout is proceeding successfully."
    )
}
