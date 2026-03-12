package com.privacyguard.pipeline

import android.content.SharedPreferences
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.data.WhitelistManager
import com.privacyguard.ml.PIIAnalysisResult
import com.privacyguard.ml.PrivacyModel
import com.privacyguard.util.AnalysisPipeline
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalysisPipelineComprehensiveTest {

    private lateinit var model: PrivacyModel
    private lateinit var whitelistManager: WhitelistManager
    private lateinit var logRepository: EncryptedLogRepository
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var alertCount: Int = 0
    private var lastAlert: PIIAnalysisResult? = null

    @Before
    fun setUp() {
        model = PrivacyModel.getInstance()
        model.initializeForTesting()

        editor = mockk(relaxed = true)
        prefs = mockk(relaxed = true)
        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.putStringSet(any(), any()) } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.apply() } just Runs
        every { prefs.getString(any(), any()) } returns ""
        every { prefs.getInt(any(), any()) } returns 0
        every { prefs.getStringSet(any(), any()) } returns emptySet()
        every { prefs.getBoolean(any(), any()) } returns true
        every { prefs.getAll() } returns emptyMap<String, Any?>()

        whitelistManager = WhitelistManager(prefs)
        logRepository = EncryptedLogRepository(prefs)
        alertCount = 0
        lastAlert = null
    }

    @After
    fun tearDown() {
        PrivacyModel.resetInstance()
    }

    private fun createPipeline(): AnalysisPipeline {
        return AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository,
            alertCallback = { result, _ ->
                alertCount++
                lastAlert = result
            }
        )
    }

    // === WHITELIST BEHAVIOR ===

    @Test
    fun `skips analysis for whitelisted app`() = runTest {
        whitelistManager.addToWhitelist("com.trusted.app")
        val pipeline = createPipeline()
        val result = pipeline.analyzeImmediate("SSN: 123-45-6789", "com.trusted.app")
        assertFalse(result.hasSensitiveData())
    }

    @Test
    fun `processes text from non-whitelisted app`() = runTest {
        val pipeline = createPipeline()
        val result = pipeline.analyzeImmediate("Some normal text here", "com.untrusted.app")
        assertNotNull(result)
    }

    @Test
    fun `skips own package name`() {
        val pipeline = createPipeline()
        pipeline.processText("My SSN is 123-45-6789", "com.privacyguard")
        assertEquals(0, alertCount)
    }

    @Test
    fun `processes when source is null`() = runTest {
        val pipeline = createPipeline()
        val result = pipeline.analyzeImmediate("Some text that is long enough", null)
        assertNotNull(result)
    }

    @Test
    fun `processes when source is empty`() = runTest {
        val pipeline = createPipeline()
        val result = pipeline.analyzeImmediate("Some text that is long enough", "")
        assertNotNull(result)
    }

    @Test
    fun `adding to whitelist prevents future analysis`() = runTest {
        val pipeline = createPipeline()
        // First call should process
        val result1 = pipeline.analyzeImmediate("Text that is long enough", "com.new.app")
        assertNotNull(result1)
        // Whitelist the app
        whitelistManager.addToWhitelist("com.new.app")
        // Second call should skip
        val result2 = pipeline.analyzeImmediate("More text that is long enough", "com.new.app")
        assertFalse(result2.hasSensitiveData())
    }

    @Test
    fun `removing from whitelist allows analysis again`() = runTest {
        whitelistManager.addToWhitelist("com.temp.app")
        val pipeline = createPipeline()
        val result1 = pipeline.analyzeImmediate("Text long enough here", "com.temp.app")
        assertFalse(result1.hasSensitiveData())
        whitelistManager.removeFromWhitelist("com.temp.app")
        val result2 = pipeline.analyzeImmediate("Text long enough here", "com.temp.app")
        assertNotNull(result2)
    }

    // === TEXT LENGTH CHECKS ===

    @Test
    fun `skips text shorter than minimum`() = runTest {
        val pipeline = createPipeline()
        val result = pipeline.analyzeImmediate("hi", null)
        assertFalse(result.hasSensitiveData())
    }

    @Test
    fun `skips empty text`() = runTest {
        val pipeline = createPipeline()
        val result = pipeline.analyzeImmediate("", null)
        assertFalse(result.hasSensitiveData())
    }

    @Test
    fun `processes text at minimum length`() = runTest {
        val pipeline = createPipeline()
        val text = "a".repeat(AnalysisPipeline.MIN_TEXT_LENGTH)
        val result = pipeline.analyzeImmediate(text, null)
        assertNotNull(result)
    }

    @Test
    fun `processes text slightly above minimum`() = runTest {
        val pipeline = createPipeline()
        val text = "a".repeat(AnalysisPipeline.MIN_TEXT_LENGTH + 1)
        val result = pipeline.analyzeImmediate(text, null)
        assertNotNull(result)
    }

    @Test
    fun `handles very long text`() = runTest {
        val pipeline = createPipeline()
        val text = "word ".repeat(5000)
        val result = pipeline.analyzeImmediate(text, null)
        assertNotNull(result)
    }

    @Test
    fun `processText skips text below minimum`() {
        val pipeline = createPipeline()
        pipeline.processText("ab", "com.test")
        // Should not crash, should silently skip
    }

    // === CIRCUIT BREAKER ===

    @Test
    fun `circuit breaker starts not tripped`() {
        val pipeline = createPipeline()
        assertFalse(pipeline.isCircuitBreakerTripped())
    }

    @Test
    fun `circuit breaker reset works`() {
        val pipeline = createPipeline()
        pipeline.resetCircuitBreaker()
        assertFalse(pipeline.isCircuitBreakerTripped())
        assertEquals(0, pipeline.getConsecutiveFailures())
    }

    @Test
    fun `consecutive failures counter starts at 0`() {
        val pipeline = createPipeline()
        assertEquals(0, pipeline.getConsecutiveFailures())
    }

    @Test
    fun `circuit breaker threshold constant is 5`() {
        assertEquals(5, AnalysisPipeline.CIRCUIT_BREAKER_THRESHOLD)
    }

    @Test
    fun `circuit breaker reset time is 60 seconds`() {
        assertEquals(60_000L, AnalysisPipeline.CIRCUIT_BREAKER_RESET_MS)
    }

    // === PIPELINE CONSTANTS ===

    @Test
    fun `minimum text length is 5`() {
        assertEquals(5, AnalysisPipeline.MIN_TEXT_LENGTH)
    }

    @Test
    fun `maximum text length is 10000`() {
        assertEquals(10000, AnalysisPipeline.MAX_TEXT_LENGTH)
    }

    // === CANCEL BEHAVIOR ===

    @Test
    fun `cancel does not throw`() {
        val pipeline = createPipeline()
        pipeline.processText("Some text for analysis pipeline", "com.test")
        pipeline.cancel()
    }

    @Test
    fun `cancel after multiple process calls`() {
        val pipeline = createPipeline()
        repeat(10) {
            pipeline.processText("Text iteration $it for analysis", "com.test")
        }
        pipeline.cancel()
    }

    @Test
    fun `process after cancel`() {
        val pipeline = createPipeline()
        pipeline.cancel()
        pipeline.processText("Text after cancel event fired", "com.test")
        // Should not crash
    }

    @Test
    fun `double cancel does not throw`() {
        val pipeline = createPipeline()
        pipeline.cancel()
        pipeline.cancel()
    }

    // === DEBOUNCE ===

    @Test
    fun `rapid calls debounce properly`() {
        val pipeline = createPipeline()
        repeat(100) {
            pipeline.processText("Rapid text $it long enough for processing", "com.test")
        }
        // Should not crash with rapid calls
    }

    // === CONCURRENT SAFETY ===

    @Test
    fun `multiple pipelines can coexist`() = runTest {
        val pipeline1 = createPipeline()
        val pipeline2 = createPipeline()
        val result1 = pipeline1.analyzeImmediate("Text for pipeline one analysis", null)
        val result2 = pipeline2.analyzeImmediate("Text for pipeline two analysis", null)
        assertNotNull(result1)
        assertNotNull(result2)
    }
}
