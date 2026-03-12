package com.privacyguard.ml

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PIITokenizerTest {

    private lateinit var tokenizer: PIITokenizer

    @Before
    fun setUp() {
        tokenizer = PIITokenizer()
    }

    @Test
    fun `encode returns correct buffer size`() {
        val output = tokenizer.encode("Hello world")
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.mask.size)
    }

    @Test
    fun `encode starts with CLS token`() {
        val output = tokenizer.encode("test")
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
    }

    @Test
    fun `encode ends real tokens with SEP`() {
        val output = tokenizer.encode("test")
        // Find the last non-zero position before padding
        val lastRealToken = output.ids.indexOfFirst { it == PIITokenizer.SEP_TOKEN_ID }
        assertTrue("SEP token should be present", lastRealToken > 0)
    }

    @Test
    fun `encode pads remaining positions with PAD`() {
        val output = tokenizer.encode("hi")
        // After CLS, token, SEP, rest should be PAD (0)
        val firstPad = output.mask.indexOfFirst { it == 0 }
        assertTrue("Padding should start after real tokens", firstPad > 0)
        for (i in firstPad until PIITokenizer.MAX_SEQUENCE_LENGTH) {
            assertEquals("Position $i should be padded", PIITokenizer.PAD_TOKEN_ID, output.ids[i])
            assertEquals("Mask position $i should be 0", 0, output.mask[i])
        }
    }

    @Test
    fun `encode credit card number produces tokens`() {
        val output = tokenizer.encode("4532 1234 5678 9012")
        assertTrue("Should have more than CLS+SEP tokens", output.mask.sum() > 2)
        assertEquals(1, output.mask[0]) // CLS mask
    }

    @Test
    fun `encode SSN produces tokens`() {
        val output = tokenizer.encode("123-45-6789")
        assertTrue("Should have tokens for SSN", output.mask.sum() > 2)
    }

    @Test
    fun `encode email address produces tokens`() {
        val output = tokenizer.encode("user@example.com")
        assertTrue("Should have tokens for email", output.mask.sum() > 2)
    }

    @Test
    fun `encode special characters does not crash`() {
        val output = tokenizer.encode("Hello! @#\$%^&*() 你好 🎉")
        assertNotNull(output)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
    }

    @Test
    fun `encode empty string returns valid output`() {
        val output = tokenizer.encode("")
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        assertEquals(PIITokenizer.SEP_TOKEN_ID, output.ids[1])
        assertEquals(1, output.mask[0])
        assertEquals(1, output.mask[1])
        assertEquals(0, output.mask[2])
    }

    @Test
    fun `encode null string returns valid output`() {
        val output = tokenizer.encode(null)
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
        assertEquals(PIITokenizer.SEP_TOKEN_ID, output.ids[1])
    }

    @Test
    fun `encode long text truncates properly`() {
        val longText = "a ".repeat(1000)
        val output = tokenizer.encode(longText)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.ids.size)
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH, output.mask.size)
        // First token should be CLS
        assertEquals(PIITokenizer.CLS_TOKEN_ID, output.ids[0])
    }

    @Test
    fun `byte buffers have correct capacity`() {
        val output = tokenizer.encode("test")
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH * 4, output.idBuffer.capacity())
        assertEquals(PIITokenizer.MAX_SEQUENCE_LENGTH * 4, output.maskBuffer.capacity())
    }

    @Test
    fun `attention mask ones count matches real tokens`() {
        val output = tokenizer.encode("hello world")
        val onesCount = output.mask.count { it == 1 }
        assertTrue("Should have at least CLS + 2 tokens + SEP", onesCount >= 4)
    }

    @Test
    fun `tokenize splits on whitespace`() {
        val tokens = tokenizer.tokenize("hello world")
        assertTrue("Should have at least 2 tokens", tokens.size >= 2)
    }

    @Test
    fun `tokenize splits on punctuation`() {
        val tokens = tokenizer.tokenize("hello, world!")
        assertTrue("Punctuation should create separate tokens", tokens.size >= 3)
    }

    @Test
    fun `vocab size is non-zero`() {
        assertTrue(tokenizer.getVocabSize() > 0)
    }

    @Test
    fun `encode produces different outputs for different inputs`() {
        val output1 = tokenizer.encode("credit card 4532")
        val output2 = tokenizer.encode("hello world")
        assertFalse("Different inputs should produce different token IDs",
            output1.ids.contentEquals(output2.ids))
    }
}
