package com.privacyguard.pipeline

import com.privacyguard.util.Debouncer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DebouncerTest {

    @Test
    fun `debounce fires action after delay`() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val debouncer = Debouncer(scope, 500L)
        var fired = false

        debouncer.debounce { fired = true }

        advanceTimeBy(400)
        assertFalse("Should not fire before delay", fired)

        advanceTimeBy(200)
        assertTrue("Should fire after delay", fired)
    }

    @Test
    fun `debounce cancels previous action on rapid calls`() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val debouncer = Debouncer(scope, 500L)
        var callCount = 0

        debouncer.debounce { callCount++ }
        advanceTimeBy(200)
        debouncer.debounce { callCount++ }
        advanceTimeBy(200)
        debouncer.debounce { callCount++ }

        advanceTimeBy(600)
        assertEquals("Should only fire once for burst", 1, callCount)
    }

    @Test
    fun `cancel stops pending action`() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val debouncer = Debouncer(scope, 500L)
        var fired = false

        debouncer.debounce { fired = true }
        debouncer.cancel()

        advanceTimeBy(1000)
        assertFalse("Cancel should prevent execution", fired)
    }

    @Test
    fun `hasPending returns true when action is queued`() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val debouncer = Debouncer(scope, 500L)

        assertFalse(debouncer.hasPending())
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
    }

    @Test
    fun `separate calls after delay fire independently`() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val debouncer = Debouncer(scope, 500L)
        var callCount = 0

        debouncer.debounce { callCount++ }
        advanceTimeBy(600)
        assertEquals(1, callCount)

        debouncer.debounce { callCount++ }
        advanceTimeBy(600)
        assertEquals(2, callCount)
    }
}
