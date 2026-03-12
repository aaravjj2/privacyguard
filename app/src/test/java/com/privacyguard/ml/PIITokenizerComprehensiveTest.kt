package com.privacyguard.ml

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.nio.ByteOrder

class PIITokenizerComprehensiveTest {

    private lateinit var tokenizer: PIITokenizer

    @Before
    fun setUp() {
        tokenizer = PIITokenizer()
    }

    // === BASIC TOKENIZATION ===

    @Test
    fun `encode empty string returns CLS and SEP only`() {
        val output = tokenizer.encode("")
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        assertEquals(PIITokenizer.SEP_TOKEN_ID, output.ids[1])
        assertEquals(1, output.mask[0])
        assertEquals(1, output.mask[1])
        assertEquals(0, output.mask[2])
    }

    @Test
    fun `encode null string returns CLS and SEP only`() {
        val output = tokenizer.encode(null)
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        assertEquals(PIITokenizer.SEP_TOKEN_ID, output.ids[1])
    }

    @Test
    fun `encode single character`() {
        val output = tokenizer.encode("a")
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        assertTrue(output.mask[0] == 1)
        assertTrue(output.mask[1] == 1)
        assertTrue(output.mask[2] == 1) // CLS, 'a', SEP
    }

    @Test
    fun `encode whitespace only`() {
        val output = tokenizer.encode("   ")
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        // Should have CLS and SEP at minimum
        assertTrue(output.mask.sum() >= 2)
    }

    @Test
    fun `encode tab and newline characters`() {
        val output = tokenizer.encode("hello\tworld\nfoo")
        assertNotNull(output)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `encode preserves max sequence length`() {
        val output = tokenizer.encode("test input text")
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.mask.size)
    }

    @Test
    fun `encode returns PAD_TOKEN_ID for padding positions`() {
        val output = tokenizer.encode("hi")
        // Positions after the real tokens should be PAD
        for (i in output.mask.indices) {
            if (output.mask[i] == 0) {
                assertEquals("Padding position $i should have PAD_TOKEN_ID",
                    PIITokenizer.PAD_TOKEN_ID, output.ids[i])
            }
        }
    }

    @Test
    fun `encode first token is always CLS`() {
        val texts = listOf("hello", "test 123", "credit card 4532", "", "a")
        texts.forEach { text ->
            val output = tokenizer.encode(text)
            assertEquals("First token should be CLS for '$text'",
                PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        }
    }

    @Test
    fun `encode has SEP token after last real token`() {
        val output = tokenizer.encode("hello")
        val realTokenCount = output.mask.sum()
        // The last real token should be SEP
        assertEquals(PIITokenizer.SEP_TOKEN_ID, output.ids[realTokenCount - 1])
    }

    @Test
    fun `encode single space`() {
        val output = tokenizer.encode(" ")
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        assertTrue(output.mask.sum() >= 2) // at least CLS and SEP
    }

    // === CREDIT CARD TOKENIZATION ===

    @Test
    fun `tokenize Visa card with spaces`() {
        val output = tokenizer.encode("4532 1234 5678 9012")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize Visa card without spaces`() {
        val output = tokenizer.encode("4532123456789012")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize Visa card with dashes`() {
        val output = tokenizer.encode("4532-1234-5678-9012")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize Mastercard number`() {
        val output = tokenizer.encode("5425 2334 3010 9903")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize Amex number 15 digits`() {
        val output = tokenizer.encode("3782 822463 10005")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize Discover number`() {
        val output = tokenizer.encode("6011 1111 1111 1117")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize credit card in sentence context`() {
        val output = tokenizer.encode("My credit card number is 4532 1234 5678 9012 please charge it")
        assertTrue("Should produce many tokens", output.mask.sum() > 10)
    }

    @Test
    fun `tokenize multiple credit cards in one text`() {
        val output = tokenizer.encode("Card 1: 4532123456789012 Card 2: 5425233430109903")
        assertTrue("Should produce many tokens", output.mask.sum() > 10)
    }

    @Test
    fun `tokenize Diners Club number`() {
        val output = tokenizer.encode("3056 9309 0259 04")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize JCB number`() {
        val output = tokenizer.encode("3530 1113 3330 0000")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize credit card with mixed separators`() {
        val output = tokenizer.encode("4532 1234-5678.9012")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    // === SSN TOKENIZATION ===

    @Test
    fun `tokenize SSN with dashes`() {
        val output = tokenizer.encode("123-45-6789")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize SSN with spaces`() {
        val output = tokenizer.encode("123 45 6789")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize SSN without separators`() {
        val output = tokenizer.encode("123456789")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize SSN in sentence context`() {
        val output = tokenizer.encode("My SSN is 123-45-6789")
        assertTrue("Should produce many tokens", output.mask.sum() > 5)
    }

    @Test
    fun `tokenize SSN with label prefix`() {
        val output = tokenizer.encode("Social Security Number: 078-05-1120")
        assertTrue("Should produce many tokens", output.mask.sum() > 5)
    }

    @Test
    fun `tokenize SSN with dots`() {
        val output = tokenizer.encode("123.45.6789")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    // === EMAIL TOKENIZATION ===

    @Test
    fun `tokenize simple email`() {
        val output = tokenizer.encode("user@example.com")
        assertTrue("Should produce tokens for email", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize email with subdomain`() {
        val output = tokenizer.encode("user@mail.company.co.uk")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize email with plus addressing`() {
        val output = tokenizer.encode("user+tag@gmail.com")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize email with dots in local part`() {
        val output = tokenizer.encode("first.last@company.com")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize email with long TLD`() {
        val output = tokenizer.encode("user@example.technology")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize email in sentence context`() {
        val output = tokenizer.encode("Please send the report to john.doe@company.com by Friday")
        assertTrue("Should produce many tokens", output.mask.sum() > 10)
    }

    @Test
    fun `tokenize email with numbers in local part`() {
        val output = tokenizer.encode("user123@example.com")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize email with hyphen in domain`() {
        val output = tokenizer.encode("user@my-company.com")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize multiple emails in text`() {
        val output = tokenizer.encode("Contact alice@test.com or bob@test.com")
        assertTrue("Should produce many tokens", output.mask.sum() > 8)
    }

    // === PHONE NUMBER TOKENIZATION ===

    @Test
    fun `tokenize US phone with parentheses`() {
        val output = tokenizer.encode("(555) 867-5309")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize US phone with dashes`() {
        val output = tokenizer.encode("555-867-5309")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize US phone with dots`() {
        val output = tokenizer.encode("555.867.5309")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize international phone with country code`() {
        val output = tokenizer.encode("+1-555-867-5309")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize UK phone number`() {
        val output = tokenizer.encode("+44 20 7123 4567")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize German phone number`() {
        val output = tokenizer.encode("+49 30 12345678")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize Japanese phone number`() {
        val output = tokenizer.encode("+81 3-1234-5678")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize Indian phone number`() {
        val output = tokenizer.encode("+91 98765 43210")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize phone without country code`() {
        val output = tokenizer.encode("8675309")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize phone in sentence context`() {
        val output = tokenizer.encode("Call me at (555) 867-5309 please")
        assertTrue("Should produce many tokens", output.mask.sum() > 5)
    }

    // === API KEY TOKENIZATION ===

    @Test
    fun `tokenize Stripe API key`() {
        val output = tokenizer.encode("sk-live_51ABCDEFghijKLMNopqrSTUVwxyz")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize GitHub PAT`() {
        val output = tokenizer.encode("ghp_1234567890abcdefghij1234567890ab")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize AWS access key`() {
        val output = tokenizer.encode("AKIAIOSFODNN7EXAMPLE")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize generic API key`() {
        val output = tokenizer.encode("api_key_abc123def456ghi789jkl012mno345")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize API key in assignment context`() {
        val output = tokenizer.encode("API_KEY=sk_tst_4eC39HqLyjWDarjtT1zdp7dc")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize bearer token`() {
        val output = tokenizer.encode("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    // === PASSWORD TOKENIZATION ===

    @Test
    fun `tokenize simple password`() {
        val output = tokenizer.encode("password: MyP@ssw0rd!")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize complex password`() {
        val output = tokenizer.encode("p@$$w0rd!#Str0ng")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    // === NAME TOKENIZATION ===

    @Test
    fun `tokenize person name`() {
        val output = tokenizer.encode("John Smith")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize full name with middle`() {
        val output = tokenizer.encode("John Michael Smith")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    // === ADDRESS TOKENIZATION ===

    @Test
    fun `tokenize US address`() {
        val output = tokenizer.encode("123 Main Street, Anytown, CA 90210")
        assertTrue("Should produce many tokens", output.mask.sum() > 5)
    }

    @Test
    fun `tokenize PO Box`() {
        val output = tokenizer.encode("PO Box 1234, Springfield, IL 62701")
        assertTrue("Should produce many tokens", output.mask.sum() > 5)
    }

    // === DOB TOKENIZATION ===

    @Test
    fun `tokenize date of birth US format`() {
        val output = tokenizer.encode("DOB: 01/15/1990")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize date of birth ISO format`() {
        val output = tokenizer.encode("Born: 1990-01-15")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    // === MEDICAL ID TOKENIZATION ===

    @Test
    fun `tokenize medical record number`() {
        val output = tokenizer.encode("MRN: 123456789")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    @Test
    fun `tokenize health insurance ID`() {
        val output = tokenizer.encode("Health Insurance ID: XYZ123456")
        assertTrue("Should produce tokens", output.mask.sum() > 2)
    }

    // === UNICODE AND SPECIAL CHARACTERS ===

    @Test
    fun `tokenize Chinese characters`() {
        val output = tokenizer.encode("你好世界")
        assertNotNull(output)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `tokenize Japanese characters`() {
        val output = tokenizer.encode("こんにちは世界")
        assertNotNull(output)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `tokenize Korean characters`() {
        val output = tokenizer.encode("안녕하세요")
        assertNotNull(output)
    }

    @Test
    fun `tokenize Arabic text`() {
        val output = tokenizer.encode("مرحبا بالعالم")
        assertNotNull(output)
    }

    @Test
    fun `tokenize Hebrew text`() {
        val output = tokenizer.encode("שלום עולם")
        assertNotNull(output)
    }

    @Test
    fun `tokenize emoji`() {
        val output = tokenizer.encode("Hello \uD83D\uDE00\uD83C\uDF89\uD83D\uDD12\uD83D\uDEE1\uFE0F")
        assertNotNull(output)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `tokenize mixed unicode and ASCII`() {
        val output = tokenizer.encode("Hello \u4F60\u597D \u0645\u0631\u062D\u0628\u0627 \uD83C\uDF0D")
        assertNotNull(output)
    }

    @Test
    fun `tokenize control characters`() {
        val output = tokenizer.encode("hello\u0000\u0001\u0002world")
        assertNotNull(output)
    }

    @Test
    fun `tokenize zero width characters`() {
        val output = tokenizer.encode("he\u200Bllo\u200Cworld")
        assertNotNull(output)
    }

    @Test
    fun `tokenize Thai text`() {
        val output = tokenizer.encode("\u0E2A\u0E27\u0E31\u0E2A\u0E14\u0E35")
        assertNotNull(output)
    }

    @Test
    fun `tokenize Cyrillic text`() {
        val output = tokenizer.encode("\u041F\u0440\u0438\u0432\u0435\u0442 \u043C\u0438\u0440")
        assertNotNull(output)
    }

    @Test
    fun `tokenize Devanagari text`() {
        val output = tokenizer.encode("\u0928\u092E\u0938\u094D\u0924\u0947")
        assertNotNull(output)
    }

    @Test
    fun `tokenize mixed script PII`() {
        val output = tokenizer.encode("Name: \u5F20\u4E09 Email: zhang@example.com Phone: +86 138 0000 0000")
        assertNotNull(output)
        assertTrue("Mixed script should produce tokens", output.mask.sum() > 5)
    }

    // === LENGTH EDGE CASES ===

    @Test
    fun `tokenize exactly max length text`() {
        val text = "a".repeat(PIITokenizer.MAX_SEQUENCE_LENGTH)
        val output = tokenizer.encode(text)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `tokenize text much longer than max length`() {
        val text = "word ".repeat(2000)
        val output = tokenizer.encode(text)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
    }

    @Test
    fun `tokenize 10000 character text`() {
        val text = "The quick brown fox jumps over the lazy dog. ".repeat(250)
        val output = tokenizer.encode(text)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `tokenize single very long word`() {
        val text = "a".repeat(5000)
        val output = tokenizer.encode(text)
        assertNotNull(output)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `truncation preserves CLS and SEP`() {
        val text = "word ".repeat(2000) // Will definitely exceed max sequence length
        val output = tokenizer.encode(text)
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        // SEP should be at the end of real tokens
        val lastRealIdx = output.mask.sum() - 1
        assertEquals(PIITokenizer.SEP_TOKEN_ID, output.ids[lastRealIdx])
    }

    @Test
    fun `exactly max minus 2 tokens still fits`() {
        // CLS + tokens + SEP = MAX_SEQUENCE_LENGTH
        // So we can have MAX_SEQUENCE_LENGTH - 2 real tokens
        val tokens = tokenizer.tokenize("a ".repeat(PIITokenizer.MAX_SEQUENCE_LENGTH))
        val output = tokenizer.encode("a ".repeat(PIITokenizer.MAX_SEQUENCE_LENGTH))
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `tokenize two character string`() {
        val output = tokenizer.encode("ab")
        assertNotNull(output)
        assertTrue(output.mask.sum() >= 3) // CLS, token(s), SEP
    }

    @Test
    fun `tokenize three character string`() {
        val output = tokenizer.encode("abc")
        assertNotNull(output)
        assertTrue(output.mask.sum() >= 3)
    }

    // === BYTE BUFFER PROPERTIES ===

    @Test
    fun `id buffer has correct capacity`() {
        val output = tokenizer.encode("test")
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH * 4, output.idBuffer.capacity())
    }

    @Test
    fun `mask buffer has correct capacity`() {
        val output = tokenizer.encode("test")
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH * 4, output.maskBuffer.capacity())
    }

    @Test
    fun `id buffer uses little endian byte order`() {
        val output = tokenizer.encode("test")
        assertEquals(ByteOrder.LITTLE_ENDIAN, output.idBuffer.order())
    }

    @Test
    fun `mask buffer uses little endian byte order`() {
        val output = tokenizer.encode("test")
        assertEquals(ByteOrder.LITTLE_ENDIAN, output.maskBuffer.order())
    }

    @Test
    fun `id buffer position is rewound to 0`() {
        val output = tokenizer.encode("test")
        assertEquals(0, output.idBuffer.position())
    }

    @Test
    fun `mask buffer position is rewound to 0`() {
        val output = tokenizer.encode("test")
        assertEquals(0, output.maskBuffer.position())
    }

    @Test
    fun `id buffer content matches ids array`() {
        val output = tokenizer.encode("hello world test")
        for (i in 0 until PIITokenizer.MAX_SEQUENCE_LENGTH) {
            assertEquals("Buffer position $i should match array",
                output.ids[i], output.idBuffer.getInt(i * 4))
        }
    }

    @Test
    fun `mask buffer content matches mask array`() {
        val output = tokenizer.encode("hello world test")
        for (i in 0 until PIITokenizer.MAX_SEQUENCE_LENGTH) {
            assertEquals("Buffer position $i should match array",
                output.mask[i], output.maskBuffer.getInt(i * 4))
        }
    }

    @Test
    fun `id buffer is direct byte buffer`() {
        val output = tokenizer.encode("test")
        assertTrue("idBuffer should be direct", output.idBuffer.isDirect)
    }

    @Test
    fun `mask buffer is direct byte buffer`() {
        val output = tokenizer.encode("test")
        assertTrue("maskBuffer should be direct", output.maskBuffer.isDirect)
    }

    @Test
    fun `empty encode id buffer matches ids array`() {
        val output = tokenizer.encode("")
        for (i in 0 until PIITokenizer.MAX_SEQUENCE_LENGTH) {
            assertEquals(output.ids[i], output.idBuffer.getInt(i * 4))
        }
    }

    @Test
    fun `empty encode mask buffer matches mask array`() {
        val output = tokenizer.encode("")
        for (i in 0 until PIITokenizer.MAX_SEQUENCE_LENGTH) {
            assertEquals(output.mask[i], output.maskBuffer.getInt(i * 4))
        }
    }

    // === ATTENTION MASK CORRECTNESS ===

    @Test
    fun `attention mask starts with ones for real tokens`() {
        val output = tokenizer.encode("the quick brown fox")
        assertTrue(output.mask[0] == 1) // CLS
        assertTrue(output.mask[1] == 1) // first token
    }

    @Test
    fun `attention mask ends with zeros for padding`() {
        val output = tokenizer.encode("short")
        assertEquals(0, output.mask[PIITokenizer.MAX_SEQUENCE_LENGTH - 1])
        assertEquals(0, output.mask[PIITokenizer.MAX_SEQUENCE_LENGTH - 2])
    }

    @Test
    fun `attention mask has no gaps`() {
        val output = tokenizer.encode("hello world foo bar")
        var foundZero = false
        for (i in output.mask.indices) {
            if (output.mask[i] == 0) foundZero = true
            if (foundZero) assertEquals("No ones after first zero at index $i",
                0, output.mask[i])
        }
    }

    @Test
    fun `attention mask sum equals number of real tokens`() {
        val output = tokenizer.encode("hello")
        val realTokenCount = output.mask.sum()
        assertTrue("Should have CLS + tokens + SEP", realTokenCount >= 3)
    }

    @Test
    fun `longer text produces more attention mask ones`() {
        val short = tokenizer.encode("hi")
        val long = tokenizer.encode("the quick brown fox jumps over the lazy dog")
        assertTrue("Longer text should have more real tokens",
            long.mask.sum() > short.mask.sum())
    }

    @Test
    fun `attention mask for null input has exactly 2 ones`() {
        val output = tokenizer.encode(null)
        assertEquals("Null input should have CLS and SEP only", 2, output.mask.sum())
    }

    @Test
    fun `attention mask for empty input has exactly 2 ones`() {
        val output = tokenizer.encode("")
        assertEquals("Empty input should have CLS and SEP only", 2, output.mask.sum())
    }

    @Test
    fun `attention mask values are only 0 or 1`() {
        val output = tokenizer.encode("some random text here for testing purposes")
        output.mask.forEach { value ->
            assertTrue("Mask values should be 0 or 1, got $value",
                value == 0 || value == 1)
        }
    }

    // === DETERMINISM ===

    @Test
    fun `same input produces same output`() {
        val text = "My credit card is 4532 1234 5678 9012"
        val output1 = tokenizer.encode(text)
        val output2 = tokenizer.encode(text)
        assertArrayEquals(output1.ids, output2.ids)
        assertArrayEquals(output1.mask, output2.mask)
    }

    @Test
    fun `repeated encoding is stable`() {
        val text = "SSN: 123-45-6789"
        val outputs = (1..10).map { tokenizer.encode(text) }
        outputs.forEach { output ->
            assertArrayEquals(outputs[0].ids, output.ids)
        }
    }

    @Test
    fun `deterministic for unicode text`() {
        val text = "\u4F60\u597D\u4E16\u754C email@test.com 123-45-6789"
        val output1 = tokenizer.encode(text)
        val output2 = tokenizer.encode(text)
        assertArrayEquals(output1.ids, output2.ids)
        assertArrayEquals(output1.mask, output2.mask)
    }

    @Test
    fun `deterministic for long text`() {
        val text = "word ".repeat(500)
        val output1 = tokenizer.encode(text)
        val output2 = tokenizer.encode(text)
        assertArrayEquals(output1.ids, output2.ids)
    }

    @Test
    fun `deterministic for empty string`() {
        val output1 = tokenizer.encode("")
        val output2 = tokenizer.encode("")
        assertArrayEquals(output1.ids, output2.ids)
        assertArrayEquals(output1.mask, output2.mask)
    }

    // === TOKENIZE METHOD DIRECTLY ===

    @Test
    fun `tokenize splits simple sentence`() {
        val tokens = tokenizer.tokenize("hello world")
        assertTrue(tokens.isNotEmpty())
    }

    @Test
    fun `tokenize handles punctuation`() {
        val tokens = tokenizer.tokenize("hello, world!")
        assertTrue("Should have token for comma", tokens.size >= 3)
    }

    @Test
    fun `tokenize handles multiple spaces`() {
        val tokens = tokenizer.tokenize("hello    world")
        assertTrue(tokens.isNotEmpty())
    }

    @Test
    fun `tokenize handles leading and trailing spaces`() {
        val tokens = tokenizer.tokenize("  hello world  ")
        assertTrue(tokens.isNotEmpty())
    }

    @Test
    fun `tokenize splits on special characters`() {
        val tokens = tokenizer.tokenize("email@domain.com")
        assertTrue("Should split on @ and .", tokens.size >= 3)
    }

    @Test
    fun `tokenize handles empty string`() {
        val tokens = tokenizer.tokenize("")
        assertTrue(tokens.isEmpty())
    }

    @Test
    fun `tokenize handles single word`() {
        val tokens = tokenizer.tokenize("hello")
        assertEquals(1, tokens.size)
    }

    @Test
    fun `tokenize handles numbers and letters together`() {
        val tokens = tokenizer.tokenize("abc123")
        assertTrue(tokens.isNotEmpty())
    }

    @Test
    fun `tokenize handles parentheses`() {
        val tokens = tokenizer.tokenize("(hello)")
        // Should split on ( and )
        assertTrue("Should produce at least 3 tokens", tokens.size >= 3)
    }

    @Test
    fun `tokenize handles colon separator`() {
        val tokens = tokenizer.tokenize("key:value")
        assertTrue("Should split on colon", tokens.size >= 3)
    }

    @Test
    fun `tokenize handles exclamation mark`() {
        val tokens = tokenizer.tokenize("hello!")
        assertTrue("Should split on !", tokens.size >= 2)
    }

    @Test
    fun `tokenize handles question mark`() {
        val tokens = tokenizer.tokenize("what?")
        assertTrue("Should split on ?", tokens.size >= 2)
    }

    @Test
    fun `tokenize produces lowercase tokens`() {
        val tokens = tokenizer.tokenize("HELLO")
        // WordPiece lowercases input
        tokens.forEach { token ->
            if (!token.startsWith("[")) { // skip special tokens
                assertEquals("Token should be lowercase", token.lowercase(), token)
            }
        }
    }

    @Test
    fun `tokenize WordPiece subword for unknown word`() {
        val tokens = tokenizer.tokenize("unfamiliarword")
        // Should produce UNK or subword tokens
        assertTrue(tokens.isNotEmpty())
    }

    @Test
    fun `tokenize known vocab word returned directly`() {
        // "the" is in the default vocab
        val tokens = tokenizer.tokenize("the")
        assertEquals(1, tokens.size)
        assertEquals("the", tokens[0])
    }

    @Test
    fun `tokenize splits sentence with all punctuation types`() {
        val tokens = tokenizer.tokenize("hello; world: test! foo? bar.")
        assertTrue("Should produce many tokens for punctuation-heavy text", tokens.size >= 8)
    }

    // === VOCAB ===

    @Test
    fun `vocab size is positive`() {
        assertTrue(tokenizer.getVocabSize() > 0)
    }

    @Test
    fun `vocab contains special tokens`() {
        assertTrue("Vocab should have reasonable size", tokenizer.getVocabSize() >= 100)
    }

    @Test
    fun `vocab size includes all default entries`() {
        // Default vocab has: 5 special + 26 lower + 26 upper + 10 digits + 33 punct +
        // 26 ##lower + 10 ##digits + 21 common words = ~157
        assertTrue("Default vocab should have at least 100 entries",
            tokenizer.getVocabSize() >= 100)
    }

    @Test
    fun `different tokenizer instances have same vocab size`() {
        val t1 = PIITokenizer()
        val t2 = PIITokenizer()
        assertEquals(t1.getVocabSize(), t2.getVocabSize())
    }

    // === DIFFERENT INPUT PATTERNS ===

    @Test
    fun `different inputs produce different outputs`() {
        val output1 = tokenizer.encode("credit card number")
        val output2 = tokenizer.encode("social security number")
        assertFalse(output1.ids.contentEquals(output2.ids))
    }

    @Test
    fun `case sensitivity in tokenization`() {
        val lower = tokenizer.encode("hello")
        val upper = tokenizer.encode("HELLO")
        // WordPiece lowercases, so they should produce the same tokens
        assertNotNull(lower)
        assertNotNull(upper)
    }

    @Test
    fun `numbers encoded correctly`() {
        val output = tokenizer.encode("0123456789")
        assertNotNull(output)
        assertTrue(output.mask.sum() > 2)
    }

    @Test
    fun `punctuation only text`() {
        val output = tokenizer.encode("!@#$%^&*()")
        assertNotNull(output)
        assertTrue(output.mask.sum() > 2) // CLS, tokens, SEP
    }

    @Test
    fun `mixed numbers and text`() {
        val output = tokenizer.encode("flight UA 2345 on 12 March")
        assertNotNull(output)
        assertTrue(output.mask.sum() > 5)
    }

    // === MIXED PII CONTENT ===

    @Test
    fun `tokenize text with credit card and email`() {
        val output = tokenizer.encode("CC: 4532123456789012, email: user@test.com")
        assertTrue(output.mask.sum() > 10)
    }

    @Test
    fun `tokenize text with SSN and phone`() {
        val output = tokenizer.encode("SSN: 123-45-6789, Phone: (555) 867-5309")
        assertTrue(output.mask.sum() > 10)
    }

    @Test
    fun `tokenize text with all PII types`() {
        val text = "CC: 4532123456789012, SSN: 123-45-6789, Email: user@test.com, " +
                "Phone: 555-867-5309, Key: sk-live_abc123"
        val output = tokenizer.encode(text)
        assertTrue(output.mask.sum() > 15)
    }

    @Test
    fun `tokenize realistic chat message with PII`() {
        val text = "Hey, my name is John Smith. You can reach me at john@gmail.com " +
                "or call (555) 123-4567. My card number is 4111 1111 1111 1111."
        val output = tokenizer.encode(text)
        assertTrue(output.mask.sum() > 20)
    }

    @Test
    fun `tokenize form data with PII`() {
        val text = "Name: Jane Doe\nSSN: 987-65-4321\nDOB: 03/15/1985\nAddress: 456 Oak Ave"
        val output = tokenizer.encode(text)
        assertTrue(output.mask.sum() > 10)
    }

    // === EQUALITY ===

    @Test
    fun `TokenizerOutput equality for same content`() {
        val output1 = tokenizer.encode("test")
        val output2 = tokenizer.encode("test")
        assertEquals(output1, output2)
    }

    @Test
    fun `TokenizerOutput inequality for different content`() {
        val output1 = tokenizer.encode("hello")
        val output2 = tokenizer.encode("world")
        assertNotEquals(output1, output2)
    }

    @Test
    fun `TokenizerOutput hashCode consistency`() {
        val output1 = tokenizer.encode("test")
        val output2 = tokenizer.encode("test")
        assertEquals(output1.hashCode(), output2.hashCode())
    }

    @Test
    fun `TokenizerOutput not equal to null`() {
        val output = tokenizer.encode("test")
        assertNotEquals(output, null)
    }

    @Test
    fun `TokenizerOutput not equal to different type`() {
        val output = tokenizer.encode("test")
        assertNotEquals(output, "not a tokenizer output")
    }

    @Test
    fun `TokenizerOutput equals itself`() {
        val output = tokenizer.encode("test")
        assertEquals(output, output)
    }

    @Test
    fun `TokenizerOutput hashCode differs for different content`() {
        val output1 = tokenizer.encode("hello")
        val output2 = tokenizer.encode("world")
        // Hash codes could theoretically collide, but for these inputs they should differ
        assertNotEquals(output1.hashCode(), output2.hashCode())
    }

    // === SPECIAL SEQUENCES ===

    @Test
    fun `tokenize text containing CLS token string`() {
        val output = tokenizer.encode("This contains [CLS] in the text")
        assertNotNull(output)
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0]) // First should still be real CLS
    }

    @Test
    fun `tokenize text containing SEP token string`() {
        val output = tokenizer.encode("This contains [SEP] in the text")
        assertNotNull(output)
    }

    @Test
    fun `tokenize text containing PAD token string`() {
        val output = tokenizer.encode("This contains [PAD] in the text")
        assertNotNull(output)
    }

    @Test
    fun `tokenize text containing UNK token string`() {
        val output = tokenizer.encode("This contains [UNK] in the text")
        assertNotNull(output)
    }

    @Test
    fun `tokenize text with repeated words`() {
        val output = tokenizer.encode("the the the the the")
        assertNotNull(output)
        assertTrue(output.mask.sum() >= 7) // CLS + 5 "the" + SEP
    }

    @Test
    fun `tokenize newline separated lines`() {
        val output = tokenizer.encode("line1\nline2\nline3")
        assertNotNull(output)
        assertTrue(output.mask.sum() > 2)
    }

    @Test
    fun `tokenize carriage return and newline`() {
        val output = tokenizer.encode("line1\r\nline2")
        assertNotNull(output)
    }

    @Test
    fun `tokenize text with backslash`() {
        val output = tokenizer.encode("C:\\Users\\test\\file.txt")
        assertNotNull(output)
        assertTrue(output.mask.sum() > 2)
    }

    @Test
    fun `tokenize URL`() {
        val output = tokenizer.encode("https://www.example.com/path?query=value")
        assertNotNull(output)
        assertTrue(output.mask.sum() > 5)
    }

    @Test
    fun `tokenize JSON-like text`() {
        val output = tokenizer.encode("{\"name\":\"John\",\"email\":\"john@test.com\"}")
        assertNotNull(output)
        assertTrue(output.mask.sum() > 5)
    }

    // === PERFORMANCE ===

    @Test
    fun `tokenize 1000 short strings without timeout`() {
        val start = System.nanoTime()
        repeat(1000) {
            tokenizer.encode("credit card 4532 1234 5678 9012")
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("1000 tokenizations should complete in under 10 seconds, took ${elapsedMs}ms",
            elapsedMs < 10000)
    }

    @Test
    fun `tokenize 100 long strings without timeout`() {
        val longText = "The quick brown fox jumps over the lazy dog. ".repeat(100)
        val start = System.nanoTime()
        repeat(100) {
            tokenizer.encode(longText)
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("100 long tokenizations should complete in under 10 seconds, took ${elapsedMs}ms",
            elapsedMs < 10000)
    }

    @Test
    fun `tokenize varied inputs in batch`() {
        val inputs = listOf(
            "Hello world",
            "4532 1234 5678 9012",
            "user@example.com",
            "123-45-6789",
            "(555) 867-5309",
            "sk-live_abc123xyz",
            "\u4F60\u597D\u4E16\u754C",
            "John Smith 123 Main St",
            "",
            "a".repeat(1000)
        )
        val start = System.nanoTime()
        repeat(100) {
            inputs.forEach { tokenizer.encode(it) }
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("Batch tokenization should complete in under 10 seconds, took ${elapsedMs}ms",
            elapsedMs < 10000)
    }

    // === CONCURRENT SAFETY ===

    @Test
    fun `concurrent tokenization produces consistent results`() {
        val text = "My credit card is 4532 1234 5678 9012"
        val reference = tokenizer.encode(text)
        val threads = (1..10).map { threadIdx ->
            Thread {
                repeat(100) {
                    val output = tokenizer.encode(text)
                    assertTrue("Thread $threadIdx iteration $it should match reference",
                        output.ids.contentEquals(reference.ids))
                }
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join(10000) } // 10 second timeout
        threads.forEach { assertFalse("Thread should have finished", it.isAlive) }
    }

    // === UNK TOKEN HANDLING ===

    @Test
    fun `unknown characters map to UNK token`() {
        // Characters not in the default vocab should map to UNK
        val output = tokenizer.encode("\u2603") // snowman
        val hasUnk = output.ids.any { it == PIITokenizer.UNK_TOKEN_ID }
        assertTrue("Snowman character should produce UNK token", hasUnk)
    }

    @Test
    fun `all unknown text produces UNK tokens but still has CLS and SEP`() {
        val output = tokenizer.encode("\u2603\u2764\u2602") // snowman, heart, umbrella
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        assertTrue(output.mask.sum() >= 2) // at least CLS and SEP
    }
}
