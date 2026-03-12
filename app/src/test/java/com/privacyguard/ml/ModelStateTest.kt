package com.privacyguard.ml

import org.junit.Assert.*
import org.junit.Test

class ModelStateTest {

    @Test
    fun `initializing state properties`() {
        val state = ModelState.Initializing
        assertFalse(state.isReady)
        assertFalse(state.isRunning)
        assertFalse(state.isError)
        assertFalse(state.canRunInference)
    }

    @Test
    fun `ready state properties`() {
        val state = ModelState.Ready
        assertTrue(state.isReady)
        assertFalse(state.isRunning)
        assertFalse(state.isError)
        assertTrue(state.canRunInference)
    }

    @Test
    fun `running state properties`() {
        val state = ModelState.Running
        assertFalse(state.isReady)
        assertTrue(state.isRunning)
        assertFalse(state.isError)
        assertFalse(state.canRunInference)
    }

    @Test
    fun `error state properties`() {
        val state = ModelState.Error("test error")
        assertFalse(state.isReady)
        assertFalse(state.isRunning)
        assertTrue(state.isError)
        assertFalse(state.canRunInference)
    }

    @Test
    fun `error state stores message`() {
        val state = ModelState.Error("Something went wrong")
        assertTrue(state is ModelState.Error)
        assertEquals("Something went wrong", (state as ModelState.Error).message)
    }

    @Test
    fun `error state stores cause`() {
        val exception = RuntimeException("test")
        val state = ModelState.Error("fail", exception)
        assertEquals(exception, (state as ModelState.Error).cause)
    }

    @Test
    fun `closed state properties`() {
        val state = ModelState.Closed
        assertFalse(state.isReady)
        assertFalse(state.isRunning)
        assertFalse(state.isError)
        assertFalse(state.canRunInference)
    }
}
