package com.privacyguard.ml

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class PrivacyModelTest {

    @Before
    fun setUp() {
        PrivacyModel.resetInstance()
    }

    @After
    fun tearDown() {
        PrivacyModel.resetInstance()
    }

    @Test
    fun `getInstance returns same instance`() {
        val model1 = PrivacyModel.getInstance()
        val model2 = PrivacyModel.getInstance()
        assertSame(model1, model2)
    }

    @Test
    fun `initial state is Initializing`() {
        val model = PrivacyModel.getInstance()
        assertTrue(model.modelState.value is ModelState.Initializing)
    }

    @Test
    fun `initializeForTesting sets state to Ready`() {
        val model = PrivacyModel.getInstance()
        model.initializeForTesting()
        assertTrue(model.modelState.value is ModelState.Ready)
    }

    @Test
    fun `analyzeText returns empty for blank text`() = runTest {
        val model = PrivacyModel.getInstance()
        model.initializeForTesting()
        val result = model.analyzeText("")
        assertFalse(result.hasSensitiveData())
    }

    @Test
    fun `analyzeText returns empty for whitespace only`() = runTest {
        val model = PrivacyModel.getInstance()
        model.initializeForTesting()
        val result = model.analyzeText("   ")
        assertFalse(result.hasSensitiveData())
    }

    @Test
    fun `analyzeText returns result for text without model`() = runTest {
        val model = PrivacyModel.getInstance()
        model.initializeForTesting()
        val result = model.analyzeText("My credit card is 4532 1234 5678 9012")
        // Without actual Melange model, inference returns empty (no output buffer)
        assertNotNull(result)
        assertEquals(0, result.entityCount)
    }

    @Test
    fun `close sets state to Closed`() {
        val model = PrivacyModel.getInstance()
        model.initializeForTesting()
        model.close()
        assertTrue(model.modelState.value is ModelState.Closed)
    }

    @Test
    fun `resetInstance creates new instance`() {
        val model1 = PrivacyModel.getInstance()
        model1.initializeForTesting()
        PrivacyModel.resetInstance()
        val model2 = PrivacyModel.getInstance()
        assertNotSame(model1, model2)
    }

    @Test
    fun `last inference latency starts at 0`() {
        val model = PrivacyModel.getInstance()
        assertEquals(0L, model.lastInferenceLatencyMs.value)
    }

    @Test
    fun `total inferences starts at 0`() {
        val model = PrivacyModel.getInstance()
        assertEquals(0L, model.totalInferences.value)
    }
}
