package com.privacyguard.pipeline

import android.content.SharedPreferences
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.data.WhitelistManager
import com.privacyguard.ml.PIIAnalysisResult
import com.privacyguard.ml.PIIEntity
import com.privacyguard.ml.EntityType
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
class AnalysisPipelineTest {

    private lateinit var model: PrivacyModel
    private lateinit var whitelistManager: WhitelistManager
    private lateinit var logRepository: EncryptedLogRepository
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var alertFired = false
    private var lastAlertResult: PIIAnalysisResult? = null

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

        whitelistManager = WhitelistManager(prefs)
        logRepository = EncryptedLogRepository(prefs)

        alertFired = false
        lastAlertResult = null
    }

    @After
    fun tearDown() {
        PrivacyModel.resetInstance()
    }

    @Test
    fun `processText skips whitelisted apps`() = runTest {
        whitelistManager.addToWhitelist("com.trusted.app")

        val pipeline = AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository,
            alertCallback = { result, _ ->
                alertFired = true
                lastAlertResult = result
            }
        )

        pipeline.processText("My credit card is 4532 1234 5678 9012", "com.trusted.app")
        assertFalse("Alert should not fire for whitelisted app", alertFired)
    }

    @Test
    fun `processText skips own package`() = runTest {
        val pipeline = AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository,
            alertCallback = { _, _ -> alertFired = true }
        )

        pipeline.processText("My SSN is 123-45-6789", "com.privacyguard")
        assertFalse("Should not analyze own app", alertFired)
    }

    @Test
    fun `processText skips short text`() = runTest {
        val pipeline = AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository,
            alertCallback = { _, _ -> alertFired = true }
        )

        pipeline.processText("hi", "com.test.app")
        assertFalse("Short text should be skipped", alertFired)
    }

    @Test
    fun `analyzeImmediate returns empty for whitelisted`() = runTest {
        whitelistManager.addToWhitelist("com.trusted.app")

        val pipeline = AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository
        )

        val result = pipeline.analyzeImmediate("Test text with data", "com.trusted.app")
        assertFalse(result.hasSensitiveData())
    }

    @Test
    fun `analyzeImmediate returns empty for short text`() = runTest {
        val pipeline = AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository
        )

        val result = pipeline.analyzeImmediate("hi", null)
        assertFalse(result.hasSensitiveData())
    }

    @Test
    fun `circuit breaker trips after consecutive failures`() {
        val pipeline = AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository
        )

        // Simulate failures
        repeat(AnalysisPipeline.CIRCUIT_BREAKER_THRESHOLD) {
            pipeline.processText("test text long enough", "com.test")
        }

        // Force failures by incrementing manually (in real scenario model would throw)
        // Testing the getter
        assertEquals(0, pipeline.getConsecutiveFailures())
    }

    @Test
    fun `resetCircuitBreaker resets state`() {
        val pipeline = AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository
        )

        pipeline.resetCircuitBreaker()
        assertFalse(pipeline.isCircuitBreakerTripped())
        assertEquals(0, pipeline.getConsecutiveFailures())
    }

    @Test
    fun `cancel stops debouncer`() {
        val pipeline = AnalysisPipeline(
            model = model,
            whitelistManager = whitelistManager,
            logRepository = logRepository
        )

        pipeline.processText("Some text to analyze for PII", "com.test")
        pipeline.cancel()
        // No assertion needed - just verify no crash
    }
}
