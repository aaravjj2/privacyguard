package com.privacyguard.ml

import com.privacyguard.util.ConfidenceThresholds
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

/**
 * Decodes raw model output logits into PIIEntity objects.
 * Handles softmax, argmax, span extraction, and confidence filtering.
 */
class OutputDecoder {

    companion object {
        // NER label map: O (non-entity) = 0, then B-/I- pairs for each entity type
        const val NUM_LABELS = 21
        const val LABEL_O = 0
        const val O_LABEL = 0

        // B- labels (begin entity)
        const val LABEL_B_CREDIT_CARD = 1
        const val LABEL_I_CREDIT_CARD = 2
        const val LABEL_B_SSN = 3
        const val LABEL_I_SSN = 4
        const val LABEL_B_PASSWORD = 5
        const val LABEL_I_PASSWORD = 6
        const val LABEL_B_API_KEY = 7
        const val LABEL_I_API_KEY = 8
        const val LABEL_B_EMAIL = 9
        const val LABEL_I_EMAIL = 10
        const val LABEL_B_PHONE = 11
        const val LABEL_I_PHONE = 12
        const val LABEL_B_NAME = 13
        const val LABEL_I_NAME = 14
        const val LABEL_B_ADDRESS = 15
        const val LABEL_I_ADDRESS = 16
        const val LABEL_B_DOB = 17
        const val LABEL_I_DOB = 18
        const val LABEL_B_MEDICAL = 19
        const val LABEL_I_MEDICAL = 20

        private val labelToEntityType = mapOf(
            1 to EntityType.CREDIT_CARD, 2 to EntityType.CREDIT_CARD,
            3 to EntityType.SSN, 4 to EntityType.SSN,
            5 to EntityType.PASSWORD, 6 to EntityType.PASSWORD,
            7 to EntityType.API_KEY, 8 to EntityType.API_KEY,
            9 to EntityType.EMAIL, 10 to EntityType.EMAIL,
            11 to EntityType.PHONE, 12 to EntityType.PHONE,
            13 to EntityType.PERSON_NAME, 14 to EntityType.PERSON_NAME,
            15 to EntityType.ADDRESS, 16 to EntityType.ADDRESS,
            17 to EntityType.DATE_OF_BIRTH, 18 to EntityType.DATE_OF_BIRTH,
            19 to EntityType.MEDICAL_ID, 20 to EntityType.MEDICAL_ID
        )

        private fun isBLabel(label: Int): Boolean = label > 0 && label % 2 == 1
        private fun isILabel(label: Int): Boolean = label > 0 && label % 2 == 0

        private fun getEntityTypeForLabel(label: Int): EntityType? = labelToEntityType[label]

        private fun bLabelMatchesILabel(bLabel: Int, iLabel: Int): Boolean {
            return bLabel > 0 && iLabel == bLabel + 1
        }
    }

    /**
     * Decode model output buffer into a list of PIIEntity objects.
     *
     * @param outputBuffer Raw model output of shape [1, seqLen, numLabels]
     * @param originalText The original input text for span extraction
     * @param tokenToCharMap Mapping from token indices to character indices
     * @param sequenceLength Number of real (non-padding) tokens
     */
    fun decode(
        outputBuffer: ByteBuffer?,
        originalText: String,
        tokenToCharMap: Map<Int, IntRange> = emptyMap(),
        sequenceLength: Int = PIITokenizer.MAX_SEQUENCE_LENGTH
    ): List<PIIEntity> {
        if (outputBuffer == null || originalText.isEmpty()) return emptyList()

        outputBuffer.rewind()
        outputBuffer.order(ByteOrder.LITTLE_ENDIAN)

        val entities = mutableListOf<PIIEntity>()
        val logits = Array(sequenceLength) { FloatArray(NUM_LABELS) }

        // Read logits from buffer
        try {
            val floatBuffer = outputBuffer.asFloatBuffer()
            for (i in 0 until sequenceLength) {
                for (j in 0 until NUM_LABELS) {
                    logits[i][j] = floatBuffer.get()
                }
            }
        } catch (e: Exception) {
            return emptyList()
        }

        // Apply softmax and extract predictions
        val predictions = Array(sequenceLength) { i ->
            val probs = softmax(logits[i])
            val argmax = probs.indices.maxByOrNull { probs[it] } ?: 0
            Pair(argmax, probs[argmax])
        }

        // Group consecutive tokens into entity spans
        var currentEntityStart = -1
        var currentEntityLabel = O_LABEL
        var currentEntityConfSum = 0f
        var currentEntityTokenCount = 0

        for (i in 1 until sequenceLength - 1) { // Skip CLS and SEP
            val (label, confidence) = predictions[i]

            if (isBLabel(label)) {
                // Finish previous entity if exists
                if (currentEntityStart >= 0) {
                    addEntity(entities, currentEntityStart, i - 1, currentEntityLabel,
                        currentEntityConfSum / currentEntityTokenCount, originalText, tokenToCharMap)
                }
                // Start new entity
                currentEntityStart = i
                currentEntityLabel = label
                currentEntityConfSum = confidence
                currentEntityTokenCount = 1
            } else if (isILabel(label) && currentEntityStart >= 0 &&
                       bLabelMatchesILabel(currentEntityLabel, label)) {
                // Continue current entity
                currentEntityConfSum += confidence
                currentEntityTokenCount++
            } else {
                // End current entity if exists
                if (currentEntityStart >= 0) {
                    addEntity(entities, currentEntityStart, i - 1, currentEntityLabel,
                        currentEntityConfSum / currentEntityTokenCount, originalText, tokenToCharMap)
                    currentEntityStart = -1
                    currentEntityLabel = O_LABEL
                    currentEntityConfSum = 0f
                    currentEntityTokenCount = 0
                }
            }
        }

        // Handle entity at end of sequence
        if (currentEntityStart >= 0) {
            addEntity(entities, currentEntityStart, sequenceLength - 2, currentEntityLabel,
                currentEntityConfSum / currentEntityTokenCount, originalText, tokenToCharMap)
        }

        return entities.filter { entity ->
            entity.meetsThreshold(ConfidenceThresholds.getThreshold(entity.entityType))
        }
    }

    private fun addEntity(
        entities: MutableList<PIIEntity>,
        tokenStart: Int,
        tokenEnd: Int,
        bLabel: Int,
        avgConfidence: Float,
        originalText: String,
        tokenToCharMap: Map<Int, IntRange>
    ) {
        val entityType = getEntityTypeForLabel(bLabel) ?: return

        // Map token indices to character indices
        val charStart = tokenToCharMap[tokenStart]?.first ?: estimateCharIndex(tokenStart, originalText)
        val charEnd = (tokenToCharMap[tokenEnd]?.last?.plus(1)) ?: estimateCharIndex(tokenEnd + 1, originalText)

        val safeStart = charStart.coerceIn(0, originalText.length)
        val safeEnd = charEnd.coerceIn(safeStart, originalText.length)

        val rawText = if (safeStart < safeEnd) originalText.substring(safeStart, safeEnd) else ""

        entities.add(
            PIIEntity(
                entityType = entityType,
                confidence = avgConfidence,
                startIndex = safeStart,
                endIndex = safeEnd,
                rawText = rawText
            )
        )
    }

    private fun estimateCharIndex(tokenIndex: Int, text: String): Int {
        // Rough estimation: assume ~4 chars per token on average
        return (tokenIndex * 4).coerceIn(0, text.length)
    }

    fun labelToEntityType(label: Int): EntityType? = getEntityTypeForLabel(label)

    fun argmax(values: FloatArray): Int = values.indices.maxByOrNull { values[it] } ?: 0

    fun softmax(logits: FloatArray): FloatArray {
        val max = logits.max()
        val exps = FloatArray(logits.size) { exp((logits[it] - max).toDouble()).toFloat() }
        val sum = exps.sum()
        return FloatArray(logits.size) { exps[it] / sum }
    }

    /**
     * Decode from a simple label array (for testing/regex pre-screener integration).
     */
    fun decodeFromLabels(
        labels: IntArray,
        confidences: FloatArray,
        originalText: String,
        tokenToCharMap: Map<Int, IntRange> = emptyMap()
    ): List<PIIEntity> {
        val entities = mutableListOf<PIIEntity>()

        var currentEntityStart = -1
        var currentEntityLabel = O_LABEL
        var currentEntityConfSum = 0f
        var currentEntityTokenCount = 0

        for (i in labels.indices) {
            val label = labels[i]
            val confidence = confidences[i]

            if (isBLabel(label)) {
                if (currentEntityStart >= 0) {
                    addEntity(entities, currentEntityStart, i - 1, currentEntityLabel,
                        currentEntityConfSum / currentEntityTokenCount, originalText, tokenToCharMap)
                }
                currentEntityStart = i
                currentEntityLabel = label
                currentEntityConfSum = confidence
                currentEntityTokenCount = 1
            } else if (isILabel(label) && currentEntityStart >= 0 &&
                       bLabelMatchesILabel(currentEntityLabel, label)) {
                currentEntityConfSum += confidence
                currentEntityTokenCount++
            } else {
                if (currentEntityStart >= 0) {
                    addEntity(entities, currentEntityStart, i - 1, currentEntityLabel,
                        currentEntityConfSum / currentEntityTokenCount, originalText, tokenToCharMap)
                    currentEntityStart = -1
                    currentEntityLabel = O_LABEL
                    currentEntityConfSum = 0f
                    currentEntityTokenCount = 0
                }
            }
        }

        if (currentEntityStart >= 0) {
            addEntity(entities, currentEntityStart, labels.size - 1, currentEntityLabel,
                currentEntityConfSum / currentEntityTokenCount, originalText, tokenToCharMap)
        }

        return entities.filter { entity ->
            entity.meetsThreshold(ConfidenceThresholds.getThreshold(entity.entityType))
        }
    }
}
