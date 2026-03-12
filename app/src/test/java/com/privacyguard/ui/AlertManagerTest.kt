package com.privacyguard.ui

import com.privacyguard.ml.EntityType
import com.privacyguard.ml.PIIAnalysisResult
import com.privacyguard.ml.PIIEntity
import com.privacyguard.ml.Severity
import com.privacyguard.ui.alert.AlertManager
import com.privacyguard.ui.alert.AlertState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertManagerTest {

    private lateinit var alertManager: AlertManager

    @Before
    fun setUp() {
        val scope = TestScope(StandardTestDispatcher())
        alertManager = AlertManager(scope)
    }

    @Test
    fun `initial state is None`() {
        assertEquals(AlertState.None, alertManager.currentAlert.value)
    }

    @Test
    fun `show with CRITICAL severity creates Critical alert`() {
        val result = createResult(EntityType.CREDIT_CARD) // CRITICAL
        alertManager.show(result, "com.test")

        val state = alertManager.currentAlert.value
        assertTrue("Should be Critical alert", state is AlertState.Critical)
    }

    @Test
    fun `show with HIGH severity creates High alert`() {
        val result = createResult(EntityType.EMAIL) // HIGH
        alertManager.show(result, "com.test")

        val state = alertManager.currentAlert.value
        assertTrue("Should be High alert", state is AlertState.High)
    }

    @Test
    fun `show with MEDIUM severity creates Medium alert`() {
        val result = createResult(EntityType.PERSON_NAME) // MEDIUM
        alertManager.show(result, "com.test")

        val state = alertManager.currentAlert.value
        assertTrue("Should be Medium alert", state is AlertState.Medium)
    }

    @Test
    fun `dismiss sets state to None`() {
        val result = createResult(EntityType.CREDIT_CARD)
        alertManager.show(result, "com.test")
        alertManager.dismiss()

        assertEquals(AlertState.None, alertManager.currentAlert.value)
    }

    @Test
    fun `cooldown prevents duplicate alerts`() {
        val result = createResult(EntityType.CREDIT_CARD)

        alertManager.show(result, "com.test")
        alertManager.dismiss()

        // Same entity+app should be on cooldown
        alertManager.show(result, "com.test")
        assertEquals(AlertState.None, alertManager.currentAlert.value)
    }

    @Test
    fun `different entity types are not on same cooldown`() {
        val ccResult = createResult(EntityType.CREDIT_CARD)
        val emailResult = createResult(EntityType.EMAIL)

        alertManager.show(ccResult, "com.test")
        alertManager.dismiss()

        alertManager.show(emailResult, "com.test")
        assertTrue("Different entity should show", alertManager.currentAlert.value is AlertState.High)
    }

    @Test
    fun `clearCooldowns resets all cooldowns`() {
        val result = createResult(EntityType.CREDIT_CARD)
        alertManager.show(result, "com.test")
        alertManager.dismiss()

        alertManager.clearCooldowns()
        alertManager.show(result, "com.test")
        assertTrue("After cooldown clear, should show again",
            alertManager.currentAlert.value is AlertState.Critical)
    }

    @Test
    fun `empty result does not show alert`() {
        val result = PIIAnalysisResult(entities = emptyList())
        alertManager.show(result, "com.test")
        assertEquals(AlertState.None, alertManager.currentAlert.value)
    }

    @Test
    fun `queue holds alerts when one is active`() {
        val cc = createResult(EntityType.CREDIT_CARD)
        alertManager.show(cc, "com.app1")

        // This should be queued because CREDIT_CARD alert is active
        val ssn = createResult(EntityType.SSN)
        alertManager.show(ssn, "com.app2")

        // First alert is still critical
        assertTrue(alertManager.currentAlert.value is AlertState.Critical)
    }

    @Test
    fun `handleClearClipboard fires callback and dismisses`() {
        var cleared = false
        alertManager.onClearClipboard = { cleared = true }

        val result = createResult(EntityType.CREDIT_CARD)
        alertManager.show(result, "com.test")
        alertManager.handleClearClipboard()

        assertTrue("Clipboard should be cleared", cleared)
        assertEquals(AlertState.None, alertManager.currentAlert.value)
    }

    @Test
    fun `handleWhitelist calls callback with source app`() {
        var whitelistedApp: String? = null

        val result = createResult(EntityType.CREDIT_CARD)
        alertManager.show(result, "com.test")
        alertManager.handleWhitelist("com.test") { whitelistedApp = it }

        assertEquals("com.test", whitelistedApp)
    }

    private fun createResult(entityType: EntityType): PIIAnalysisResult {
        return PIIAnalysisResult(
            entities = listOf(
                PIIEntity(
                    entityType = entityType,
                    confidence = 0.95f,
                    startIndex = 0,
                    endIndex = 20,
                    rawText = "test data"
                )
            ),
            inferenceTimeMs = 35L
        )
    }
}
