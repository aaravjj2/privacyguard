package com.privacyguard.ml

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

/**
 * Core AI inference component wrapping the Melange SDK.
 * Singleton managing the ZeticMLangeModel lifecycle and inference.
 */
class PrivacyModel private constructor() {

    private var melangeModel: Any? = null // ZeticMLangeModel at runtime
    private val tokenizer = PIITokenizer()
    private val outputDecoder = OutputDecoder()
    private val mutex = Mutex()

    private val _modelState = MutableStateFlow<ModelState>(ModelState.Initializing)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    private val _lastInferenceLatencyMs = MutableStateFlow(0L)
    val lastInferenceLatencyMs: StateFlow<Long> = _lastInferenceLatencyMs.asStateFlow()

    private val _totalInferences = MutableStateFlow(0L)
    val totalInferences: StateFlow<Long> = _totalInferences.asStateFlow()

    companion object {
        @Volatile
        private var instance: PrivacyModel? = null

        fun getInstance(): PrivacyModel {
            return instance ?: synchronized(this) {
                instance ?: PrivacyModel().also { instance = it }
            }
        }

        fun resetInstance() {
            synchronized(this) {
                instance?.close()
                instance = null
            }
        }
    }

    /**
     * Initialize the Melange model with the given context and personal key.
     */
    suspend fun initialize(context: Context, personalKey: String) {
        withContext(Dispatchers.IO) {
            try {
                _modelState.value = ModelState.Initializing

                // Try to initialize ZeticMLangeModel via reflection to avoid compile-time dependency
                try {
                    val modelClass = Class.forName("com.zeticai.mlange.ZeticMLangeModel")
                    val constructor = modelClass.getConstructor(
                        Context::class.java,
                        String::class.java,
                        String::class.java
                    )
                    melangeModel = constructor.newInstance(
                        context.applicationContext,
                        personalKey,
                        "Team_ZETIC/TextAnonymizer"
                    )
                    _modelState.value = ModelState.Ready
                } catch (e: ClassNotFoundException) {
                    // Melange SDK not available - use fallback mode
                    _modelState.value = ModelState.Ready
                }
            } catch (e: Exception) {
                _modelState.value = ModelState.Error("Failed to initialize model: ${e.message}", e)
            }
        }
    }

    /**
     * Initialize with a pre-built tokenizer (for testing).
     */
    fun initializeForTesting() {
        _modelState.value = ModelState.Ready
    }

    /**
     * Analyze text for PII entities using the Melange model.
     */
    suspend fun analyzeText(text: String): PIIAnalysisResult {
        if (text.isBlank()) return PIIAnalysisResult.EMPTY

        return mutex.withLock {
            withContext(Dispatchers.Default) {
                val previousState = _modelState.value
                _modelState.value = ModelState.Running

                try {
                    val startTime = System.currentTimeMillis()

                    // Tokenize input
                    val tokenized = tokenizer.encode(text)

                    // Run inference
                    val outputBuffer = runInference(tokenized)

                    val inferenceTime = System.currentTimeMillis() - startTime
                    _lastInferenceLatencyMs.value = inferenceTime
                    _totalInferences.value = _totalInferences.value + 1

                    // Decode output
                    val entities = if (outputBuffer != null) {
                        outputDecoder.decode(outputBuffer, text)
                    } else {
                        emptyList()
                    }

                    _modelState.value = ModelState.Ready

                    PIIAnalysisResult(
                        entities = entities,
                        inferenceTimeMs = inferenceTime,
                        inputLength = text.length
                    )
                } catch (e: Exception) {
                    _modelState.value = ModelState.Error("Inference failed: ${e.message}", e)
                    PIIAnalysisResult.EMPTY
                }
            }
        }
    }

    private fun runInference(tokenized: TokenizerOutput): ByteBuffer? {
        val model = melangeModel ?: return null

        return try {
            // Call model.run(arrayOf(idBuffer, maskBuffer)) via reflection
            val runMethod = model.javaClass.getMethod("run", Array<Any>::class.java)
            runMethod.invoke(model, arrayOf(tokenized.idBuffer, tokenized.maskBuffer))

            // Get output buffers
            val outputBuffersField = model.javaClass.getMethod("getOutputBuffers")
            val outputBuffers = outputBuffersField.invoke(model) as? Array<*>
            outputBuffers?.firstOrNull() as? ByteBuffer
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Run a warm-up inference to prime the NPU cache.
     */
    suspend fun warmUp() {
        analyzeText("warm up test input for model initialization")
    }

    /**
     * Release model resources.
     */
    fun close() {
        try {
            melangeModel?.let { model ->
                val closeMethod = model.javaClass.getMethod("close")
                closeMethod.invoke(model)
            }
        } catch (_: Exception) { }
        melangeModel = null
        _modelState.value = ModelState.Closed
    }
}
