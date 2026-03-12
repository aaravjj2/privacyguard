package com.privacyguard.ml

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Result of tokenization: token IDs and attention mask as ByteBuffers.
 */
data class TokenizerOutput(
    val ids: IntArray,
    val mask: IntArray,
    val idBuffer: ByteBuffer,
    val maskBuffer: ByteBuffer
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TokenizerOutput) return false
        return ids.contentEquals(other.ids) && mask.contentEquals(other.mask)
    }

    override fun hashCode(): Int = 31 * ids.contentHashCode() + mask.contentHashCode()
}

/**
 * WordPiece tokenizer for the Melange TextAnonymizer model.
 * Converts raw text to token IDs and attention masks.
 */
class PIITokenizer(vocabStream: InputStream? = null) {

    companion object {
        const val MAX_SEQUENCE_LENGTH = 512
        const val PAD_TOKEN_ID = 0
        const val UNK_TOKEN_ID = 100
        const val CLS_TOKEN_ID = 101
        const val SEP_TOKEN_ID = 102
        private const val WORD_PIECE_PREFIX = "##"
    }

    private val vocab: Map<String, Int>
    private val idToToken: Map<Int, String>

    init {
        vocab = if (vocabStream != null) {
            loadVocab(vocabStream)
        } else {
            buildDefaultVocab()
        }
        idToToken = vocab.entries.associate { (k, v) -> v to k }
    }

    private fun loadVocab(inputStream: InputStream): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        inputStream.bufferedReader().useLines { lines ->
            lines.forEachIndexed { index, line ->
                result[line.trim()] = index
            }
        }
        return result
    }

    private fun buildDefaultVocab(): Map<String, Int> {
        val v = mutableMapOf<String, Int>()
        v["[PAD]"] = PAD_TOKEN_ID
        v["[UNK]"] = UNK_TOKEN_ID
        v["[CLS]"] = CLS_TOKEN_ID
        v["[SEP]"] = SEP_TOKEN_ID
        v["[MASK]"] = 103

        // Basic ASCII characters and digits
        for (c in 'a'..'z') { v[c.toString()] = v.size }
        for (c in 'A'..'Z') { v[c.toString()] = v.size }
        for (c in '0'..'9') { v[c.toString()] = v.size }

        // Common punctuation
        listOf(".", ",", "!", "?", ":", ";", "-", "_", "/", "\\", "@", "#",
               "$", "%", "^", "&", "*", "(", ")", "+", "=", "{", "}", "[", "]",
               "|", "<", ">", "~", "`", "'", "\"", " ").forEach {
            v[it] = v.size
        }

        // Common WordPiece subwords
        for (c in 'a'..'z') { v["##$c"] = v.size }
        for (c in '0'..'9') { v["##$c"] = v.size }

        // Common words
        listOf("the", "is", "at", "my", "card", "number", "credit", "email",
               "phone", "name", "address", "password", "key", "ssn", "social",
               "security", "api", "token", "secret", "call", "me").forEach {
            v[it] = v.size
        }

        return v
    }

    /**
     * Encode text into token IDs and attention mask.
     * Adds [CLS] at start, [SEP] at end, pads to MAX_SEQUENCE_LENGTH.
     */
    fun encode(text: String?): TokenizerOutput {
        if (text.isNullOrEmpty()) {
            return createEmptyOutput()
        }

        val tokens = tokenize(text)
        val tokenIds = mutableListOf(CLS_TOKEN_ID)

        // Convert tokens to IDs, truncate if needed (leave room for CLS and SEP)
        val maxTokens = MAX_SEQUENCE_LENGTH - 2
        val truncatedTokens = if (tokens.size > maxTokens) {
            tokens.subList(0, maxTokens)
        } else {
            tokens
        }

        for (token in truncatedTokens) {
            tokenIds.add(vocab[token] ?: UNK_TOKEN_ID)
        }
        tokenIds.add(SEP_TOKEN_ID)

        // Create attention mask (1 for real tokens, 0 for padding)
        val attentionMask = IntArray(MAX_SEQUENCE_LENGTH)
        for (i in tokenIds.indices) {
            attentionMask[i] = 1
        }

        // Pad token IDs to max length
        val paddedIds = IntArray(MAX_SEQUENCE_LENGTH)
        for (i in tokenIds.indices) {
            paddedIds[i] = tokenIds[i]
        }
        // Remaining positions are already 0 (PAD_TOKEN_ID)

        return TokenizerOutput(
            ids = paddedIds,
            mask = attentionMask,
            idBuffer = intArrayToByteBuffer(paddedIds),
            maskBuffer = intArrayToByteBuffer(attentionMask)
        )
    }

    /**
     * WordPiece tokenization: split text into tokens using the vocabulary.
     */
    fun tokenize(text: String): List<String> {
        val result = mutableListOf<String>()
        val words = basicTokenize(text)

        for (word in words) {
            val subTokens = wordPieceTokenize(word)
            result.addAll(subTokens)
        }

        return result
    }

    /**
     * Basic whitespace and punctuation tokenization.
     */
    private fun basicTokenize(text: String): List<String> {
        val result = mutableListOf<String>()
        val cleaned = text.trim()
        if (cleaned.isEmpty()) return result

        val current = StringBuilder()
        for (char in cleaned) {
            when {
                char.isWhitespace() -> {
                    if (current.isNotEmpty()) {
                        result.add(current.toString())
                        current.clear()
                    }
                }
                isPunctuation(char) -> {
                    if (current.isNotEmpty()) {
                        result.add(current.toString())
                        current.clear()
                    }
                    result.add(char.toString())
                }
                else -> current.append(char)
            }
        }
        if (current.isNotEmpty()) {
            result.add(current.toString())
        }

        return result
    }

    /**
     * WordPiece tokenization of a single word.
     */
    private fun wordPieceTokenize(word: String): List<String> {
        if (word.isEmpty()) return emptyList()

        val lowerWord = word.lowercase()
        if (vocab.containsKey(lowerWord)) {
            return listOf(lowerWord)
        }

        val tokens = mutableListOf<String>()
        var start = 0

        while (start < lowerWord.length) {
            var end = lowerWord.length
            var found = false

            while (start < end) {
                val substr = if (start == 0) {
                    lowerWord.substring(start, end)
                } else {
                    "$WORD_PIECE_PREFIX${lowerWord.substring(start, end)}"
                }

                if (vocab.containsKey(substr)) {
                    tokens.add(substr)
                    found = true
                    break
                }
                end--
            }

            if (!found) {
                // Character not in vocab, use [UNK] for the entire remaining word
                tokens.add("[UNK]")
                break
            }
            start = end
        }

        return tokens
    }

    private fun isPunctuation(char: Char): Boolean {
        val cp = char.code
        if ((cp in 33..47) || (cp in 58..64) || (cp in 91..96) || (cp in 123..126)) {
            return true
        }
        return char.category in setOf(
            CharCategory.DASH_PUNCTUATION,
            CharCategory.START_PUNCTUATION,
            CharCategory.END_PUNCTUATION,
            CharCategory.CONNECTOR_PUNCTUATION,
            CharCategory.OTHER_PUNCTUATION,
            CharCategory.INITIAL_QUOTE_PUNCTUATION,
            CharCategory.FINAL_QUOTE_PUNCTUATION
        )
    }

    private fun intArrayToByteBuffer(array: IntArray): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(array.size * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (value in array) {
            buffer.putInt(value)
        }
        buffer.rewind()
        return buffer
    }

    private fun createEmptyOutput(): TokenizerOutput {
        val ids = IntArray(MAX_SEQUENCE_LENGTH)
        val mask = IntArray(MAX_SEQUENCE_LENGTH)
        ids[0] = CLS_TOKEN_ID
        ids[1] = SEP_TOKEN_ID
        mask[0] = 1
        mask[1] = 1

        return TokenizerOutput(
            ids = ids,
            mask = mask,
            idBuffer = intArrayToByteBuffer(ids),
            maskBuffer = intArrayToByteBuffer(mask)
        )
    }

    fun getVocabSize(): Int = vocab.size
}
