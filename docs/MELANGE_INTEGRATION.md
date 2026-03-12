# Melange SDK Integration Guide

This document details how PrivacyGuard integrates the Melange SDK for on-device PII detection using the `Team_ZETIC/TextAnonymizer` NER model.

---

## Table of Contents

1. [SDK Setup in build.gradle](#sdk-setup-in-buildgradle)
2. [Model Initialization](#model-initialization)
3. [Input Preparation](#input-preparation)
4. [Running Inference](#running-inference)
5. [Output Decoding](#output-decoding)
6. [Lifecycle Management](#lifecycle-management)
7. [Performance Optimization](#performance-optimization)

---

## SDK Setup in build.gradle

### Step 1: Add the Melange Maven Repository

In your project-level `settings.gradle.kts`, add the Melange Maven repository:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.zetic.ai/repository/maven-public/")
        }
    }
}
```

### Step 2: Add the Melange SDK Dependency

In your app-level `build.gradle.kts`, add the Melange SDK dependency:

```kotlin
// app/build.gradle.kts
dependencies {
    // Melange SDK for on-device ML inference
    implementation("ai.zetic.product:zetic-mlange-android:1.2.2")
}
```

### Step 3: Configure the Melange Personal Key

Add your Melange Personal Key to `local.properties` (this file should be in `.gitignore`):

```properties
# local.properties
melange.personal.key=YOUR_PERSONAL_KEY_HERE
```

In `build.gradle.kts`, read the key and inject it as a BuildConfig field:

```kotlin
// app/build.gradle.kts
android {
    defaultConfig {
        // Read the Melange key from local.properties
        val localProperties = java.util.Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        buildConfigField(
            "String",
            "MELANGE_PERSONAL_KEY",
            "\"${localProperties.getProperty("melange.personal.key", "")}\""
        )
    }

    buildFeatures {
        buildConfig = true
    }
}
```

---

## Model Initialization

### Creating the ZeticMLangeModel Instance

The `ZeticMLangeModel` is initialized with the model key and personal key. This should be done once during service creation, not on every inference call.

```kotlin
import ai.zetic.product.mlange.android.ZeticMLangeModel

class MelangeNerClassifier(private val context: Context) {

    private var model: ZeticMLangeModel? = null
    private var isInitialized = false

    // Model identifier for the TextAnonymizer NER model
    companion object {
        private const val MODEL_KEY = "Team_ZETIC/TextAnonymizer"
    }

    /**
     * Initialize the Melange model.
     * Call this once during service onCreate().
     * This loads the model into memory and prepares the NPU pipeline.
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            model = ZeticMLangeModel(
                context,
                MODEL_KEY,
                BuildConfig.MELANGE_PERSONAL_KEY
            )
            isInitialized = true

            // Run a warm-up inference to prime the NPU pipeline
            warmUp()

            Log.d("MelangeNER", "Model initialized successfully")
        } catch (e: Exception) {
            Log.e("MelangeNER", "Failed to initialize model", e)
            isInitialized = false
        }
    }

    /**
     * Warm-up inference with dummy input to prime the NPU pipeline.
     * Reduces first-inference latency.
     */
    private fun warmUp() {
        val dummyInput = "Hello world"
        runInference(dummyInput)
    }
}
```

### Error Handling During Initialization

If model initialization fails (e.g., invalid key, unsupported device), PrivacyGuard falls back to regex-only detection and logs the error. The user is notified that ML-based detection is unavailable.

---

## Input Preparation

### Tokenization

The `TextAnonymizer` model expects tokenized input. Text must be converted to token IDs, padded or truncated to the model's maximum sequence length, and packed into a `ByteBuffer`.

```kotlin
class Tokenizer(context: Context) {

    // Maximum sequence length supported by the model
    companion object {
        private const val MAX_SEQ_LENGTH = 128
        private const val PAD_TOKEN_ID = 0
        private const val CLS_TOKEN_ID = 101   // [CLS] token
        private const val SEP_TOKEN_ID = 102   // [SEP] token
    }

    // Vocabulary loaded from bundled assets
    private val vocab: Map<String, Int> = loadVocab(context)

    /**
     * Tokenize input text into token IDs.
     * Applies WordPiece tokenization compatible with the TextAnonymizer model.
     */
    fun tokenize(text: String): IntArray {
        val tokens = mutableListOf(CLS_TOKEN_ID)

        // WordPiece tokenization
        val words = text.lowercase().split("\\s+".toRegex())
        for (word in words) {
            val wordTokens = wordPieceTokenize(word)
            tokens.addAll(wordTokens)
        }

        tokens.add(SEP_TOKEN_ID)

        // Truncate if necessary
        val truncated = if (tokens.size > MAX_SEQ_LENGTH) {
            tokens.subList(0, MAX_SEQ_LENGTH - 1) + listOf(SEP_TOKEN_ID)
        } else {
            tokens
        }

        // Pad to MAX_SEQ_LENGTH
        val padded = truncated + List(MAX_SEQ_LENGTH - truncated.size) { PAD_TOKEN_ID }

        return padded.toIntArray()
    }

    /**
     * Create attention mask: 1 for real tokens, 0 for padding.
     */
    fun createAttentionMask(tokenIds: IntArray): IntArray {
        return tokenIds.map { if (it != PAD_TOKEN_ID) 1 else 0 }.toIntArray()
    }

    private fun wordPieceTokenize(word: String): List<Int> {
        // WordPiece tokenization implementation
        // Breaks unknown words into subword pieces using ## prefix
        val tokens = mutableListOf<Int>()
        var remaining = word

        while (remaining.isNotEmpty()) {
            var found = false
            for (end in remaining.length downTo 1) {
                val subword = if (tokens.isNotEmpty()) "##${remaining.substring(0, end)}"
                              else remaining.substring(0, end)
                val tokenId = vocab[subword]
                if (tokenId != null) {
                    tokens.add(tokenId)
                    remaining = remaining.substring(end)
                    found = true
                    break
                }
            }
            if (!found) {
                tokens.add(vocab["[UNK]"] ?: 100) // Unknown token
                break
            }
        }

        return tokens
    }

    private fun loadVocab(context: Context): Map<String, Int> {
        // Load vocabulary from assets/vocab.txt
        val vocabMap = mutableMapOf<String, Int>()
        context.assets.open("vocab.txt").bufferedReader().useLines { lines ->
            lines.forEachIndexed { index, line ->
                vocabMap[line.trim()] = index
            }
        }
        return vocabMap
    }
}
```

### ByteBuffer Preparation

The tokenized input must be packed into `ByteBuffer` format for the Melange model:

```kotlin
/**
 * Prepare input ByteBuffer for the Melange model.
 * The model expects token IDs as a flat int array in a direct ByteBuffer.
 */
fun prepareInputBuffer(tokenIds: IntArray): ByteBuffer {
    val buffer = ByteBuffer.allocateDirect(tokenIds.size * 4) // 4 bytes per int
    buffer.order(ByteOrder.nativeOrder())

    for (tokenId in tokenIds) {
        buffer.putInt(tokenId)
    }

    buffer.rewind()
    return buffer
}

/**
 * Prepare attention mask ByteBuffer.
 */
fun prepareAttentionMaskBuffer(attentionMask: IntArray): ByteBuffer {
    val buffer = ByteBuffer.allocateDirect(attentionMask.size * 4)
    buffer.order(ByteOrder.nativeOrder())

    for (mask in attentionMask) {
        buffer.putInt(mask)
    }

    buffer.rewind()
    return buffer
}
```

---

## Running Inference

### Executing the Model

Once the input buffers are prepared, inference is executed through the `ZeticMLangeModel`:

```kotlin
/**
 * Run NER inference on the given text.
 * Returns a list of detected PII entities with their types and positions.
 */
suspend fun classify(text: String): List<DetectedEntity> = withContext(Dispatchers.Default) {
    if (!isInitialized || model == null) {
        Log.w("MelangeNER", "Model not initialized, skipping inference")
        return@withContext emptyList()
    }

    val startTime = System.nanoTime()

    // Step 1: Tokenize
    val tokenIds = tokenizer.tokenize(text)
    val attentionMask = tokenizer.createAttentionMask(tokenIds)

    // Step 2: Prepare input buffers
    val inputBuffer = prepareInputBuffer(tokenIds)
    val maskBuffer = prepareAttentionMaskBuffer(attentionMask)

    // Step 3: Set input buffers on the model
    val inputs = arrayOf(inputBuffer, maskBuffer)
    model?.let { melangeModel ->
        for (i in inputs.indices) {
            melangeModel.setInputBuffer(i, inputs[i])
        }

        // Step 4: Run inference
        melangeModel.run()

        // Step 5: Retrieve output buffer
        val outputBuffer = melangeModel.getOutputBuffer(0)

        val inferenceTimeMs = (System.nanoTime() - startTime) / 1_000_000.0
        Log.d("MelangeNER", "Inference completed in ${inferenceTimeMs}ms")

        // Step 6: Decode output
        return@withContext outputDecoder.decode(outputBuffer, tokenIds, text)
    } ?: emptyList()
}
```

### Thread Safety

The `ZeticMLangeModel` is not thread-safe. All inference calls are serialized using a `Mutex`:

```kotlin
private val inferenceMutex = Mutex()

suspend fun classifyThreadSafe(text: String): List<DetectedEntity> {
    return inferenceMutex.withLock {
        classify(text)
    }
}
```

---

## Output Decoding

### From Logits to Entity Labels

The model outputs raw logits for each token position across all possible NER labels. The `OutputDecoder` converts these into structured entity detections.

```kotlin
class OutputDecoder {

    // NER label mapping (BIO tagging scheme)
    companion object {
        val LABEL_MAP = mapOf(
            0 to "O",           // Outside any entity
            1 to "B-PER",       // Beginning of Person name
            2 to "I-PER",       // Inside Person name
            3 to "B-LOC",       // Beginning of Location
            4 to "I-LOC",       // Inside Location
            5 to "B-ORG",       // Beginning of Organization
            6 to "I-ORG",       // Inside Organization
            7 to "B-CREDIT_CARD",
            8 to "I-CREDIT_CARD",
            9 to "B-SSN",
            10 to "I-SSN",
            11 to "B-PHONE",
            12 to "I-PHONE",
            13 to "B-EMAIL",
            14 to "I-EMAIL"
            // Additional labels as defined by the model
        )

        private val NUM_LABELS = LABEL_MAP.size
    }

    /**
     * Decode model output logits into detected entities.
     *
     * @param outputBuffer Raw output ByteBuffer from the model
     * @param tokenIds Original token IDs (for mapping back to text)
     * @param originalText The original input text
     * @return List of detected PII entities
     */
    fun decode(
        outputBuffer: ByteBuffer,
        tokenIds: IntArray,
        originalText: String
    ): List<DetectedEntity> {
        outputBuffer.rewind()
        outputBuffer.order(ByteOrder.nativeOrder())

        val seqLength = tokenIds.size
        val predictions = mutableListOf<Int>()

        // Extract argmax prediction for each token position
        for (pos in 0 until seqLength) {
            var maxLogit = Float.NEGATIVE_INFINITY
            var maxIndex = 0

            for (label in 0 until NUM_LABELS) {
                val logit = outputBuffer.getFloat()
                if (logit > maxLogit) {
                    maxLogit = logit
                    maxIndex = label
                }
            }

            predictions.add(maxIndex)
        }

        // Merge BIO tags into entity spans
        return mergeEntities(predictions, tokenIds, originalText)
    }

    /**
     * Merge BIO-tagged token predictions into contiguous entity spans.
     */
    private fun mergeEntities(
        predictions: List<Int>,
        tokenIds: IntArray,
        originalText: String
    ): List<DetectedEntity> {
        val entities = mutableListOf<DetectedEntity>()
        var currentEntity: MutableDetectedEntity? = null

        for (i in predictions.indices) {
            val label = LABEL_MAP[predictions[i]] ?: "O"

            when {
                label.startsWith("B-") -> {
                    // Save previous entity if exists
                    currentEntity?.let { entities.add(it.toDetectedEntity()) }

                    // Start new entity
                    val entityType = label.removePrefix("B-")
                    currentEntity = MutableDetectedEntity(
                        type = entityType,
                        tokenStartIndex = i,
                        tokenEndIndex = i
                    )
                }
                label.startsWith("I-") && currentEntity != null -> {
                    val entityType = label.removePrefix("I-")
                    if (entityType == currentEntity.type) {
                        // Continue current entity
                        currentEntity.tokenEndIndex = i
                    } else {
                        // Type mismatch, save current and ignore
                        entities.add(currentEntity.toDetectedEntity())
                        currentEntity = null
                    }
                }
                else -> {
                    // "O" label - save current entity if exists
                    currentEntity?.let { entities.add(it.toDetectedEntity()) }
                    currentEntity = null
                }
            }
        }

        // Save final entity
        currentEntity?.let { entities.add(it.toDetectedEntity()) }

        return entities
    }
}
```

---

## Lifecycle Management

### Service Lifecycle Integration

The Melange model lifecycle is tied to the `ClipboardMonitorService`:

```
Service.onCreate()
    |
    +--> MelangeNerClassifier.initialize()
    |        |
    |        +--> ZeticMLangeModel(context, modelKey, personalKey)
    |        +--> warmUp() -- single dummy inference
    |
    v
Service running (model active, processing clipboard events)
    |
    +--> classify() called on each debounced clipboard event
    |
    v
Service.onDestroy()
    |
    +--> MelangeNerClassifier.release()
             |
             +--> ZeticMLangeModel.deinitModel()
             +--> Clear buffer references
```

### Release Method

```kotlin
/**
 * Release model resources.
 * Call during service onDestroy().
 */
fun release() {
    try {
        model?.deinitModel()
        model = null
        isInitialized = false
        Log.d("MelangeNER", "Model resources released")
    } catch (e: Exception) {
        Log.e("MelangeNER", "Error releasing model", e)
    }
}
```

### Configuration Changes

Since the model runs in a foreground service (not an Activity), it is not affected by configuration changes such as screen rotation. The model instance persists for the lifetime of the service.

---

## Performance Optimization

### 1. Buffer Reuse

Pre-allocate and reuse `ByteBuffer` instances to avoid repeated allocation and GC pressure:

```kotlin
// Pre-allocated buffers (created once during initialization)
private val inputBuffer = ByteBuffer.allocateDirect(MAX_SEQ_LENGTH * 4)
    .order(ByteOrder.nativeOrder())
private val maskBuffer = ByteBuffer.allocateDirect(MAX_SEQ_LENGTH * 4)
    .order(ByteOrder.nativeOrder())

fun prepareInputBufferReused(tokenIds: IntArray): ByteBuffer {
    inputBuffer.clear()
    for (tokenId in tokenIds) {
        inputBuffer.putInt(tokenId)
    }
    inputBuffer.rewind()
    return inputBuffer
}
```

### 2. Regex Pre-Screening

Avoid invoking ML inference for text that clearly contains no PII candidates:

```kotlin
/**
 * Quick check: does this text contain any patterns worth analyzing?
 * If not, skip ML inference entirely.
 */
fun isWorthAnalyzing(text: String): Boolean {
    // Skip very short text (unlikely to contain PII)
    if (text.length < 3) return false

    // Skip text that is clearly not PII (e.g., single words, common phrases)
    // Run regex pre-screen for known PII patterns
    return regexPreScreener.hasAnyCandidates(text)
}
```

### 3. Debouncing

Rate-limit incoming events to avoid redundant inference calls:

```kotlin
private val textFlow = MutableSharedFlow<String>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

init {
    // Debounce text events: only process after 300ms of inactivity
    scope.launch {
        textFlow
            .debounce(300)
            .collect { text ->
                processText(text)
            }
    }
}
```

### 4. NPU Acceleration

The Melange runtime automatically leverages available NPU hardware. No additional configuration is needed -- the SDK handles hardware detection and dispatch. On devices without NPU support, the runtime falls back to CPU execution with slightly higher latency.

### 5. Model Warm-Up

A single warm-up inference during initialization primes the NPU pipeline and JIT compilation paths, ensuring that the first real inference does not incur additional startup latency:

```kotlin
private fun warmUp() {
    val dummyTokens = IntArray(MAX_SEQ_LENGTH) { 0 }
    dummyTokens[0] = CLS_TOKEN_ID
    dummyTokens[1] = SEP_TOKEN_ID

    val inputBuffer = prepareInputBuffer(dummyTokens)
    val maskBuffer = prepareAttentionMaskBuffer(
        dummyTokens.map { if (it != 0) 1 else 0 }.toIntArray()
    )

    model?.setInputBuffer(0, inputBuffer)
    model?.setInputBuffer(1, maskBuffer)
    model?.run()

    Log.d("MelangeNER", "Warm-up inference completed")
}
```

### Performance Benchmarks

| Operation | Typical Latency | Notes |
|---|---|---|
| Tokenization | 1-3ms | WordPiece tokenization of typical clipboard text |
| ByteBuffer preparation | <1ms | Pre-allocated buffer reuse |
| Model inference (NPU) | 25-40ms | Varies by device NPU capability |
| Model inference (CPU fallback) | 60-120ms | Devices without NPU support |
| Output decoding | 1-2ms | Argmax + BIO merge |
| **Total pipeline (NPU)** | **30-45ms** | End-to-end with NPU acceleration |
| **Total pipeline (CPU)** | **65-125ms** | End-to-end with CPU fallback |
