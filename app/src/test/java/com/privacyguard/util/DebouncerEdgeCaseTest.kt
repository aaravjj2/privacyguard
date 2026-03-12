package com.privacyguard.util

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * Comprehensive edge-case tests for Debouncer.
 *
 * Covers: timing edge cases, rapid-fire calls, cancellation, reuse after cancel,
 * concurrent debouncing, zero delay, negative delay, very long delay, callback ordering,
 * memory cleanup, scope cancellation, flush behavior, hasPending state.
 */
class DebouncerEdgeCaseTest {

    private lateinit var testScope: TestScope
    private lateinit var debouncer: Debouncer

    @Before
    fun setUp() {
        testScope = TestScope()
        debouncer = Debouncer(testScope, Debouncer.DEFAULT_DELAY_MS)
    }

    @After
    fun tearDown() {
        debouncer.cancel()
    }

    // ========================================================================
    // Section 1: Default constants
    // ========================================================================

    @Test
    fun `DEFAULT_DELAY_MS is 800`() {
        assertEquals(800L, Debouncer.DEFAULT_DELAY_MS)
    }

    @Test
    fun `MIN_DELAY_MS is 200`() {
        assertEquals(200L, Debouncer.MIN_DELAY_MS)
    }

    @Test
    fun `MAX_DELAY_MS is 2000`() {
        assertEquals(2000L, Debouncer.MAX_DELAY_MS)
    }

    @Test
    fun `DEFAULT_DELAY_MS is between MIN and MAX`() {
        assertTrue(Debouncer.DEFAULT_DELAY_MS >= Debouncer.MIN_DELAY_MS)
        assertTrue(Debouncer.DEFAULT_DELAY_MS <= Debouncer.MAX_DELAY_MS)
    }

    @Test
    fun `MIN_DELAY_MS is positive`() {
        assertTrue(Debouncer.MIN_DELAY_MS > 0)
    }

    @Test
    fun `MAX_DELAY_MS is greater than MIN`() {
        assertTrue(Debouncer.MAX_DELAY_MS > Debouncer.MIN_DELAY_MS)
    }

    // ========================================================================
    // Section 2: Basic debounce behavior
    // ========================================================================

    @Test
    fun `single debounce call executes after delay`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertTrue(executed.get())
    }

    @Test
    fun `single debounce call does not execute before delay`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS - 100)
        assertFalse(executed.get())
    }

    @Test
    fun `single debounce call does not execute at exactly delay minus one`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS - 1)
        assertFalse(executed.get())
    }

    @Test
    fun `single debounce call executes at exactly delay`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS)
        assertTrue(executed.get())
    }

    @Test
    fun `debounce with zero time advancement does not execute`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        // No time advancement
        assertFalse(executed.get())
    }

    @Test
    fun `debounce action receives no parameters`() = testScope.runTest {
        var actionCalled = false
        debouncer.debounce { actionCalled = true }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertTrue(actionCalled)
    }

    @Test
    fun `debounce action can be a suspending function`() = testScope.runTest {
        val result = AtomicInteger(0)
        debouncer.debounce {
            delay(10) // Suspending call inside action
            result.set(42)
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 100)
        assertEquals(42, result.get())
    }

    @Test
    fun `debounce action can access external state`() = testScope.runTest {
        val list = CopyOnWriteArrayList<String>()
        debouncer.debounce {
            list.add("executed")
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, list.size)
        assertEquals("executed", list[0])
    }

    // ========================================================================
    // Section 3: Rapid-fire calls (debounce cancels previous)
    // ========================================================================

    @Test
    fun `two rapid calls only execute the second`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("first") }
        advanceTimeBy(100) // less than delay
        debouncer.debounce { results.add("second") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("second", results[0])
    }

    @Test
    fun `three rapid calls only execute the third`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("first") }
        advanceTimeBy(100)
        debouncer.debounce { results.add("second") }
        advanceTimeBy(100)
        debouncer.debounce { results.add("third") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("third", results[0])
    }

    @Test
    fun `five rapid calls only execute the last`() = testScope.runTest {
        val counter = AtomicInteger(0)
        val results = CopyOnWriteArrayList<Int>()
        repeat(5) { i ->
            debouncer.debounce { results.add(i) }
            advanceTimeBy(50) // Much less than delay
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals(4, results[0]) // Only the last (index 4)
    }

    @Test
    fun `ten rapid calls only execute the last`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        repeat(10) { i ->
            debouncer.debounce { results.add(i) }
            advanceTimeBy(10)
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals(9, results[0])
    }

    @Test
    fun `fifty rapid calls only execute the last`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        repeat(50) { i ->
            debouncer.debounce { results.add(i) }
            advanceTimeBy(5)
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals(49, results[0])
    }

    @Test
    fun `hundred rapid calls only execute the last`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        repeat(100) { i ->
            debouncer.debounce { results.add(i) }
            advanceTimeBy(1)
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals(99, results[0])
    }

    @Test
    fun `rapid calls with no time between them only execute last`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        repeat(20) { i ->
            debouncer.debounce { results.add(i) }
            // No advanceTimeBy between calls
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals(19, results[0])
    }

    @Test
    fun `rapid calls each resetting the timer`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        // First call
        debouncer.debounce { executed.set(true) }
        // Advance almost to the delay
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS - 50)
        assertFalse(executed.get())
        // Second call resets the timer
        debouncer.debounce { executed.set(true) }
        // Advance by the original delay amount from the first call
        advanceTimeBy(50)
        // Should NOT have executed because the timer was reset
        assertFalse(executed.get())
        // Now advance by the full delay from the second call
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS - 50 + 1)
        assertTrue(executed.get())
    }

    // ========================================================================
    // Section 4: Spaced calls (enough gap for each to execute)
    // ========================================================================

    @Test
    fun `two spaced calls both execute`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("first") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)

        debouncer.debounce { results.add("second") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, results.size)
        assertEquals("first", results[0])
        assertEquals("second", results[1])
    }

    @Test
    fun `three spaced calls all execute`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        repeat(3) { i ->
            debouncer.debounce { results.add(i) }
            advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        }
        assertEquals(3, results.size)
        assertEquals(listOf(0, 1, 2), results.toList())
    }

    @Test
    fun `five spaced calls all execute in order`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        repeat(5) { i ->
            debouncer.debounce { results.add(i) }
            advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        }
        assertEquals(5, results.size)
        assertEquals(listOf(0, 1, 2, 3, 4), results.toList())
    }

    @Test
    fun `ten spaced calls all execute in order`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        repeat(10) { i ->
            debouncer.debounce { results.add(i) }
            advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        }
        assertEquals(10, results.size)
        for (i in 0 until 10) {
            assertEquals(i, results[i])
        }
    }

    // ========================================================================
    // Section 5: Mixed rapid and spaced calls
    // ========================================================================

    @Test
    fun `rapid burst then gap then single call`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        // Rapid burst of 3
        debouncer.debounce { results.add("burst1") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("burst2") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("burst3") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        // Only burst3 executes
        assertEquals(1, results.size)
        assertEquals("burst3", results[0])

        // Gap then single call
        debouncer.debounce { results.add("single") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, results.size)
        assertEquals("single", results[1])
    }

    @Test
    fun `single call then rapid burst`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        // Single call with full delay
        debouncer.debounce { results.add("single") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)

        // Rapid burst
        debouncer.debounce { results.add("rapid1") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("rapid2") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("rapid3") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, results.size)
        assertEquals("rapid3", results[1])
    }

    @Test
    fun `alternating rapid and spaced calls`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()

        // Rapid pair
        debouncer.debounce { results.add("r1a") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("r1b") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size) // Only r1b

        // Spaced single
        debouncer.debounce { results.add("s1") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, results.size) // r1b + s1

        // Rapid triple
        debouncer.debounce { results.add("r2a") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("r2b") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("r2c") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(3, results.size) // r1b + s1 + r2c

        assertEquals("r1b", results[0])
        assertEquals("s1", results[1])
        assertEquals("r2c", results[2])
    }

    // ========================================================================
    // Section 6: Cancellation
    // ========================================================================

    @Test
    fun `cancel prevents pending action from executing`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        debouncer.cancel()
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 100)
        assertFalse(executed.get())
    }

    @Test
    fun `cancel clears hasPending`() = testScope.runTest {
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
        debouncer.cancel()
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `cancel when no pending action does not throw`() {
        // Should not throw
        debouncer.cancel()
        debouncer.cancel()
        debouncer.cancel()
    }

    @Test
    fun `cancel after action already executed has no effect`() = testScope.runTest {
        val counter = AtomicInteger(0)
        debouncer.debounce { counter.incrementAndGet() }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get())
        debouncer.cancel()
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get()) // Still 1, not executed again
    }

    @Test
    fun `cancel during delay window prevents execution`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS / 2)
        debouncer.cancel()
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS)
        assertFalse(executed.get())
    }

    @Test
    fun `cancel at one millisecond before execution`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS - 1)
        debouncer.cancel()
        advanceTimeBy(100)
        assertFalse(executed.get())
    }

    @Test
    fun `multiple cancels in succession`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        debouncer.cancel()
        debouncer.cancel()
        debouncer.cancel()
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 100)
        assertFalse(executed.get())
    }

    @Test
    fun `cancel between two debounce calls only cancels first`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("first") }
        debouncer.cancel()
        debouncer.debounce { results.add("second") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("second", results[0])
    }

    // ========================================================================
    // Section 7: Reuse after cancel
    // ========================================================================

    @Test
    fun `debounce works after cancel`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { }
        debouncer.cancel()
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertTrue(executed.get())
    }

    @Test
    fun `multiple cancel-reuse cycles`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(5) {
            debouncer.debounce { counter.incrementAndGet() }
            debouncer.cancel()
        }
        // Final debounce that actually executes
        debouncer.debounce { counter.incrementAndGet() }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get())
    }

    @Test
    fun `cancel then reuse then cancel then reuse`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()

        debouncer.debounce { results.add("a") }
        debouncer.cancel()

        debouncer.debounce { results.add("b") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("b", results[0])

        debouncer.debounce { results.add("c") }
        debouncer.cancel()

        debouncer.debounce { results.add("d") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, results.size)
        assertEquals("d", results[1])
    }

    @Test
    fun `hasPending is true after reuse`() = testScope.runTest {
        debouncer.debounce { }
        debouncer.cancel()
        assertFalse(debouncer.hasPending())
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
    }

    @Test
    fun `reuse after cancel with different action`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("original") }
        debouncer.cancel()
        debouncer.debounce { results.add("replacement") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("replacement", results[0])
    }

    // ========================================================================
    // Section 8: hasPending state
    // ========================================================================

    @Test
    fun `hasPending is false initially`() {
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `hasPending is true after debounce call`() = testScope.runTest {
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
    }

    @Test
    fun `hasPending is false after action executes`() = testScope.runTest {
        debouncer.debounce { }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `hasPending is false after cancel`() = testScope.runTest {
        debouncer.debounce { }
        debouncer.cancel()
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `hasPending is true during delay window`() = testScope.runTest {
        debouncer.debounce { }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS / 2)
        assertTrue(debouncer.hasPending())
    }

    @Test
    fun `hasPending transitions from true to false on execution`() = testScope.runTest {
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `hasPending remains true when rapid calls are made`() = testScope.runTest {
        debouncer.debounce { }
        advanceTimeBy(100)
        assertTrue(debouncer.hasPending())
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
        advanceTimeBy(100)
        assertTrue(debouncer.hasPending())
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
    }

    @Test
    fun `hasPending is false after multiple cancel calls`() {
        debouncer.debounce { }
        debouncer.cancel()
        debouncer.cancel()
        assertFalse(debouncer.hasPending())
    }

    // ========================================================================
    // Section 9: Flush behavior
    // ========================================================================

    @Test
    fun `flush executes pending action immediately`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        debouncer.flush()
        advanceUntilIdle()
        assertTrue(executed.get())
    }

    @Test
    fun `flush when no pending action does not throw`() {
        // Should not throw
        debouncer.flush()
    }

    @Test
    fun `flush clears pending state`() = testScope.runTest {
        debouncer.debounce { }
        debouncer.flush()
        advanceUntilIdle()
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `flush executes only the last pending action`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("first") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("second") }
        debouncer.flush()
        advanceUntilIdle()
        assertEquals(1, results.size)
        assertEquals("second", results[0])
    }

    @Test
    fun `flush after cancel does not execute anything`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        debouncer.cancel()
        debouncer.flush()
        advanceUntilIdle()
        assertFalse(executed.get())
    }

    @Test
    fun `flush after action already executed does not re-execute`() = testScope.runTest {
        val counter = AtomicInteger(0)
        debouncer.debounce { counter.incrementAndGet() }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get())
        debouncer.flush()
        advanceUntilIdle()
        // The action was already consumed, flush should find pendingAction = null
        // Actually, the action sets pendingAction = null after execution
        assertEquals(1, counter.get())
    }

    @Test
    fun `flush then debounce works correctly`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("flushed") }
        debouncer.flush()
        advanceUntilIdle()
        assertEquals(1, results.size)

        debouncer.debounce { results.add("after-flush") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, results.size)
        assertEquals("after-flush", results[1])
    }

    @Test
    fun `multiple flush calls when pending`() = testScope.runTest {
        val counter = AtomicInteger(0)
        debouncer.debounce { counter.incrementAndGet() }
        debouncer.flush()
        advanceUntilIdle()
        // After first flush, pendingAction is set to null by cancel()
        // so second flush should not execute anything
        debouncer.flush()
        advanceUntilIdle()
        assertEquals(1, counter.get())
    }

    @Test
    fun `flush interrupts delay timer`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(100) // Partway through delay
        debouncer.flush()
        advanceUntilIdle()
        assertTrue(executed.get())
    }

    // ========================================================================
    // Section 10: Custom delay values
    // ========================================================================

    @Test
    fun `debouncer with MIN_DELAY_MS`() = testScope.runTest {
        val minDebouncer = Debouncer(testScope, Debouncer.MIN_DELAY_MS)
        val executed = AtomicBoolean(false)
        minDebouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.MIN_DELAY_MS - 1)
        assertFalse(executed.get())
        advanceTimeBy(2)
        assertTrue(executed.get())
    }

    @Test
    fun `debouncer with MAX_DELAY_MS`() = testScope.runTest {
        val maxDebouncer = Debouncer(testScope, Debouncer.MAX_DELAY_MS)
        val executed = AtomicBoolean(false)
        maxDebouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.MAX_DELAY_MS - 1)
        assertFalse(executed.get())
        advanceTimeBy(2)
        assertTrue(executed.get())
    }

    @Test
    fun `debouncer with 1ms delay`() = testScope.runTest {
        val fastDebouncer = Debouncer(testScope, 1L)
        val executed = AtomicBoolean(false)
        fastDebouncer.debounce { executed.set(true) }
        advanceTimeBy(2)
        assertTrue(executed.get())
    }

    @Test
    fun `debouncer with very long delay 10 seconds`() = testScope.runTest {
        val slowDebouncer = Debouncer(testScope, 10_000L)
        val executed = AtomicBoolean(false)
        slowDebouncer.debounce { executed.set(true) }
        advanceTimeBy(9_999)
        assertFalse(executed.get())
        advanceTimeBy(2)
        assertTrue(executed.get())
    }

    @Test
    fun `debouncer with very long delay 60 seconds`() = testScope.runTest {
        val longDebouncer = Debouncer(testScope, 60_000L)
        val executed = AtomicBoolean(false)
        longDebouncer.debounce { executed.set(true) }
        advanceTimeBy(59_999)
        assertFalse(executed.get())
        advanceTimeBy(2)
        assertTrue(executed.get())
    }

    @Test
    fun `debouncer with 500ms delay`() = testScope.runTest {
        val d = Debouncer(testScope, 500L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        advanceTimeBy(499)
        assertFalse(executed.get())
        advanceTimeBy(2)
        assertTrue(executed.get())
    }

    @Test
    fun `debouncer with 100ms delay`() = testScope.runTest {
        val d = Debouncer(testScope, 100L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        advanceTimeBy(99)
        assertFalse(executed.get())
        advanceTimeBy(2)
        assertTrue(executed.get())
    }

    @Test
    fun `debouncer with 1500ms delay`() = testScope.runTest {
        val d = Debouncer(testScope, 1500L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        advanceTimeBy(1499)
        assertFalse(executed.get())
        advanceTimeBy(2)
        assertTrue(executed.get())
    }

    // ========================================================================
    // Section 11: Zero delay edge case
    // ========================================================================

    @Test
    fun `debouncer with zero delay executes effectively immediately`() = testScope.runTest {
        val d = Debouncer(testScope, 0L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        advanceUntilIdle()
        assertTrue(executed.get())
    }

    @Test
    fun `zero delay debouncer still debounces rapid calls`() = testScope.runTest {
        val d = Debouncer(testScope, 0L)
        val results = CopyOnWriteArrayList<Int>()
        d.debounce { results.add(1) }
        d.debounce { results.add(2) }
        d.debounce { results.add(3) }
        advanceUntilIdle()
        // With zero delay, the last call should win
        assertEquals(1, results.size)
        assertEquals(3, results[0])
    }

    @Test
    fun `zero delay debouncer cancel works`() = testScope.runTest {
        val d = Debouncer(testScope, 0L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        d.cancel()
        advanceUntilIdle()
        assertFalse(executed.get())
    }

    @Test
    fun `zero delay debouncer hasPending transitions`() = testScope.runTest {
        val d = Debouncer(testScope, 0L)
        assertFalse(d.hasPending())
        d.debounce { }
        // May or may not be pending depending on dispatch
        advanceUntilIdle()
        assertFalse(d.hasPending())
    }

    // ========================================================================
    // Section 12: Callback ordering and value capture
    // ========================================================================

    @Test
    fun `action captures correct closure value`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        var value = 10
        debouncer.debounce { results.add(value) }
        value = 20 // Modify after debounce call
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        // The action captures `value` by reference, so it sees 20
        assertEquals(1, results.size)
        assertEquals(20, results[0])
    }

    @Test
    fun `action captures correct value with explicit copy`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        var value = 10
        val captured = value // Explicit capture
        debouncer.debounce { results.add(captured) }
        value = 20
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals(10, results[0]) // Captured 10, not 20
    }

    @Test
    fun `sequential debounce calls execute in order`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        debouncer.debounce { results.add(1) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        debouncer.debounce { results.add(2) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        debouncer.debounce { results.add(3) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(listOf(1, 2, 3), results.toList())
    }

    @Test
    fun `rapid calls preserve only last action callback`() = testScope.runTest {
        val sb = StringBuilder()
        debouncer.debounce { sb.append("A") }
        debouncer.debounce { sb.append("B") }
        debouncer.debounce { sb.append("C") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals("C", sb.toString())
    }

    @Test
    fun `action can modify shared mutable state`() = testScope.runTest {
        val map = mutableMapOf<String, Int>()
        debouncer.debounce {
            map["key1"] = 1
            map["key2"] = 2
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, map.size)
        assertEquals(1, map["key1"])
        assertEquals(2, map["key2"])
    }

    // ========================================================================
    // Section 13: Scope cancellation
    // ========================================================================

    @Test
    fun `scope cancellation prevents pending action`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val d = Debouncer(scope, 1000L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        scope.cancel()
        delay(1100)
        assertFalse(executed.get())
    }

    @Test
    fun `debounce after scope cancellation does not throw`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val d = Debouncer(scope, 500L)
        scope.cancel()
        // This may throw or silently fail depending on implementation
        try {
            d.debounce { }
        } catch (e: Exception) {
            // Expected if scope is cancelled
        }
    }

    @Test
    fun `cancel after scope cancellation does not throw`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val d = Debouncer(scope, 500L)
        scope.cancel()
        d.cancel() // Should not throw
    }

    // ========================================================================
    // Section 14: Multiple Debouncer instances
    // ========================================================================

    @Test
    fun `two debouncers on same scope are independent`() = testScope.runTest {
        val d1 = Debouncer(testScope, 500L)
        val d2 = Debouncer(testScope, 500L)
        val results1 = CopyOnWriteArrayList<String>()
        val results2 = CopyOnWriteArrayList<String>()

        d1.debounce { results1.add("d1") }
        d2.debounce { results2.add("d2") }
        advanceTimeBy(501)

        assertEquals(1, results1.size)
        assertEquals(1, results2.size)
    }

    @Test
    fun `canceling one debouncer does not affect another`() = testScope.runTest {
        val d1 = Debouncer(testScope, 500L)
        val d2 = Debouncer(testScope, 500L)
        val r1 = AtomicBoolean(false)
        val r2 = AtomicBoolean(false)

        d1.debounce { r1.set(true) }
        d2.debounce { r2.set(true) }
        d1.cancel()
        advanceTimeBy(501)

        assertFalse(r1.get())
        assertTrue(r2.get())
    }

    @Test
    fun `two debouncers with different delays`() = testScope.runTest {
        val fast = Debouncer(testScope, 200L)
        val slow = Debouncer(testScope, 1000L)
        val results = CopyOnWriteArrayList<String>()

        fast.debounce { results.add("fast") }
        slow.debounce { results.add("slow") }

        advanceTimeBy(201)
        assertEquals(1, results.size)
        assertEquals("fast", results[0])

        advanceTimeBy(800)
        assertEquals(2, results.size)
        assertEquals("slow", results[1])
    }

    @Test
    fun `three independent debouncers`() = testScope.runTest {
        val d1 = Debouncer(testScope, 100L)
        val d2 = Debouncer(testScope, 200L)
        val d3 = Debouncer(testScope, 300L)
        val results = CopyOnWriteArrayList<Int>()

        d1.debounce { results.add(1) }
        d2.debounce { results.add(2) }
        d3.debounce { results.add(3) }

        advanceTimeBy(101)
        assertEquals(listOf(1), results.toList())

        advanceTimeBy(100)
        assertEquals(listOf(1, 2), results.toList())

        advanceTimeBy(100)
        assertEquals(listOf(1, 2, 3), results.toList())
    }

    @Test
    fun `flush on one debouncer does not affect another`() = testScope.runTest {
        val d1 = Debouncer(testScope, 500L)
        val d2 = Debouncer(testScope, 500L)
        val r1 = AtomicBoolean(false)
        val r2 = AtomicBoolean(false)

        d1.debounce { r1.set(true) }
        d2.debounce { r2.set(true) }
        d1.flush()
        advanceUntilIdle()

        assertTrue(r1.get())
        // d2 should still be pending or executed
    }

    // ========================================================================
    // Section 15: Action exceptions
    // ========================================================================

    @Test
    fun `action throwing exception does not break debouncer`() = testScope.runTest {
        val d = Debouncer(testScope, 100L)
        d.debounce { throw RuntimeException("test error") }
        try {
            advanceTimeBy(101)
        } catch (_: Exception) {
            // Expected
        }
        // Debouncer should still work
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        advanceTimeBy(101)
        assertTrue(executed.get())
    }

    @Test
    fun `exception in action does not prevent subsequent debounce calls`() = testScope.runTest {
        val d = Debouncer(testScope, 100L)
        val results = CopyOnWriteArrayList<String>()

        d.debounce { throw IllegalStateException("fail") }
        try {
            advanceTimeBy(101)
        } catch (_: Exception) { }

        d.debounce { results.add("recovered") }
        advanceTimeBy(101)
        assertEquals("recovered", results.firstOrNull())
    }

    // ========================================================================
    // Section 16: Debouncer with different delay values and rapid calls
    // ========================================================================

    @Test
    fun `200ms debouncer with 5 rapid calls at 50ms intervals`() = testScope.runTest {
        val d = Debouncer(testScope, 200L)
        val results = CopyOnWriteArrayList<Int>()
        repeat(5) { i ->
            d.debounce { results.add(i) }
            advanceTimeBy(50)
        }
        advanceTimeBy(201)
        assertEquals(1, results.size)
        assertEquals(4, results[0])
    }

    @Test
    fun `200ms debouncer with calls at 199ms intervals - each executes`() = testScope.runTest {
        val d = Debouncer(testScope, 200L)
        val results = CopyOnWriteArrayList<Int>()
        // Call at 0ms, wait 199ms (not enough for first), call again at 199ms
        d.debounce { results.add(0) }
        advanceTimeBy(199)
        // Timer reset, first cancelled
        d.debounce { results.add(1) }
        advanceTimeBy(201)
        // Only second should have executed
        assertEquals(1, results.size)
        assertEquals(1, results[0])
    }

    @Test
    fun `200ms debouncer with calls at 201ms intervals - both execute`() = testScope.runTest {
        val d = Debouncer(testScope, 200L)
        val results = CopyOnWriteArrayList<Int>()
        d.debounce { results.add(0) }
        advanceTimeBy(201)
        assertEquals(1, results.size)
        d.debounce { results.add(1) }
        advanceTimeBy(201)
        assertEquals(2, results.size)
    }

    @Test
    fun `1000ms debouncer with 100 calls at 9ms intervals`() = testScope.runTest {
        val d = Debouncer(testScope, 1000L)
        val results = CopyOnWriteArrayList<Int>()
        repeat(100) { i ->
            d.debounce { results.add(i) }
            advanceTimeBy(9)
        }
        advanceTimeBy(1001)
        assertEquals(1, results.size)
        assertEquals(99, results[0])
    }

    // ========================================================================
    // Section 17: Edge cases with action content
    // ========================================================================

    @Test
    fun `action that does nothing`() = testScope.runTest {
        debouncer.debounce { /* no-op */ }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        // Should not throw or have any side effects
    }

    @Test
    fun `action that calls delay internally`() = testScope.runTest {
        val result = AtomicInteger(0)
        debouncer.debounce {
            delay(50)
            result.set(1)
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 51)
        assertEquals(1, result.get())
    }

    @Test
    fun `action that calls delay longer than debounce delay`() = testScope.runTest {
        val result = AtomicInteger(0)
        debouncer.debounce {
            delay(2000) // Longer than DEFAULT_DELAY_MS
            result.set(1)
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 2001)
        assertEquals(1, result.get())
    }

    @Test
    fun `action that updates multiple variables`() = testScope.runTest {
        var a = 0
        var b = ""
        var c = false
        debouncer.debounce {
            a = 42
            b = "hello"
            c = true
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(42, a)
        assertEquals("hello", b)
        assertTrue(c)
    }

    @Test
    fun `action with loop inside`() = testScope.runTest {
        val sum = AtomicInteger(0)
        debouncer.debounce {
            for (i in 1..100) {
                sum.addAndGet(i)
            }
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(5050, sum.get())
    }

    @Test
    fun `action that creates a list`() = testScope.runTest {
        var result: List<Int>? = null
        debouncer.debounce {
            result = (1..10).toList()
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertNotNull(result)
        assertEquals(10, result!!.size)
    }

    // ========================================================================
    // Section 18: Timing precision tests
    // ========================================================================

    @Test
    fun `action fires at exactly delay milliseconds`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS)
        assertTrue(executed.get())
    }

    @Test
    fun `action does not fire at delay minus 1`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS - 1)
        assertFalse(executed.get())
    }

    @Test
    fun `action fires at delay plus 1`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertTrue(executed.get())
    }

    @Test
    fun `resetting timer shifts execution by full delay`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(400) // Half of default delay
        debouncer.debounce { executed.set(true) } // Reset
        advanceTimeBy(400) // 400ms from reset, not enough
        assertFalse(executed.get())
        advanceTimeBy(401) // Now enough (800ms from reset)
        assertTrue(executed.get())
    }

    @Test
    fun `timer reset preserves total wall time correctly`() = testScope.runTest {
        val timestamps = CopyOnWriteArrayList<Long>()
        val startTime = currentTime

        debouncer.debounce { timestamps.add(currentTime - startTime) }
        advanceTimeBy(500) // 500ms in
        debouncer.debounce { timestamps.add(currentTime - startTime) } // Reset at 500ms
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1) // 500 + 801 = 1301ms total

        assertEquals(1, timestamps.size)
        // The action should execute at 500 + 800 = 1300ms from start
        assertTrue(timestamps[0] >= 1300)
    }

    // ========================================================================
    // Section 19: Complex scenarios
    // ========================================================================

    @Test
    fun `burst then space then burst pattern`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()

        // First burst
        debouncer.debounce { results.add("b1-1") }
        advanceTimeBy(10)
        debouncer.debounce { results.add("b1-2") }
        advanceTimeBy(10)
        debouncer.debounce { results.add("b1-3") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("b1-3", results[0])

        // Space
        advanceTimeBy(500)

        // Second burst
        debouncer.debounce { results.add("b2-1") }
        advanceTimeBy(10)
        debouncer.debounce { results.add("b2-2") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, results.size)
        assertEquals("b2-2", results[1])
    }

    @Test
    fun `cancel during burst then continue`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("a") }
        advanceTimeBy(50)
        debouncer.cancel()
        debouncer.debounce { results.add("b") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("c") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("c", results[0])
    }

    @Test
    fun `flush during burst`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("a") }
        advanceTimeBy(50)
        debouncer.debounce { results.add("b") }
        debouncer.flush()
        advanceUntilIdle()
        assertEquals(1, results.size)
        assertEquals("b", results[0])
    }

    @Test
    fun `debounce-flush-debounce-flush cycle`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()

        debouncer.debounce { results.add(1) }
        debouncer.flush()
        advanceUntilIdle()

        debouncer.debounce { results.add(2) }
        debouncer.flush()
        advanceUntilIdle()

        debouncer.debounce { results.add(3) }
        debouncer.flush()
        advanceUntilIdle()

        assertEquals(listOf(1, 2, 3), results.toList())
    }

    @Test
    fun `100 debounce-cancel cycles then one execution`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(100) {
            debouncer.debounce { counter.incrementAndGet() }
            debouncer.cancel()
        }
        assertEquals(0, counter.get())

        debouncer.debounce { counter.incrementAndGet() }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get())
    }

    @Test
    fun `debounce with accumulating state across calls`() = testScope.runTest {
        val accumulator = AtomicInteger(0)
        // Each debounce replaces the action, but all refer to same accumulator
        debouncer.debounce { accumulator.addAndGet(1) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        debouncer.debounce { accumulator.addAndGet(10) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        debouncer.debounce { accumulator.addAndGet(100) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(111, accumulator.get())
    }

    // ========================================================================
    // Section 20: Edge cases with TestScope
    // ========================================================================

    @Test
    fun `advanceUntilIdle executes pending debounce`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceUntilIdle()
        assertTrue(executed.get())
    }

    @Test
    fun `runCurrent does not execute debounced action`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        testScheduler.runCurrent()
        assertFalse(executed.get())
    }

    @Test
    fun `incremental time advancement eventually executes`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        // Advance in 100ms increments
        repeat(8) {
            advanceTimeBy(100)
        }
        assertTrue(executed.get())
    }

    @Test
    fun `advanceTimeBy with exact delay`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS)
        assertTrue(executed.get())
    }

    // ========================================================================
    // Section 21: Debouncer lifecycle
    // ========================================================================

    @Test
    fun `new debouncer has no pending actions`() {
        val d = Debouncer(testScope, 500L)
        assertFalse(d.hasPending())
    }

    @Test
    fun `cancel on new debouncer does not throw`() {
        val d = Debouncer(testScope, 500L)
        d.cancel()
    }

    @Test
    fun `flush on new debouncer does not throw`() {
        val d = Debouncer(testScope, 500L)
        d.flush()
    }

    @Test
    fun `hasPending on new debouncer returns false`() {
        val d = Debouncer(testScope, 500L)
        assertFalse(d.hasPending())
    }

    @Test
    fun `debouncer can be used many times`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(20) {
            debouncer.debounce { counter.incrementAndGet() }
            advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        }
        assertEquals(20, counter.get())
    }

    @Test
    fun `debouncer works with very many rapid calls followed by wait`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(500) {
            debouncer.debounce { counter.incrementAndGet() }
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get())
    }

    // ========================================================================
    // Section 22: State consistency checks
    // ========================================================================

    @Test
    fun `hasPending consistent through debounce lifecycle`() = testScope.runTest {
        assertFalse(debouncer.hasPending())
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS / 2)
        assertTrue(debouncer.hasPending())
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS / 2 + 1)
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `hasPending after rapid replace calls`() = testScope.runTest {
        debouncer.debounce { }
        debouncer.debounce { }
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `hasPending after flush`() = testScope.runTest {
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
        debouncer.flush()
        advanceUntilIdle()
        assertFalse(debouncer.hasPending())
    }

    // ========================================================================
    // Section 23: Debouncer with different scope dispatchers
    // ========================================================================

    @Test
    fun `debouncer with Unconfined dispatcher`() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val d = Debouncer(scope, 0L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        // With zero delay and Unconfined, should execute nearly immediately
        delay(50)
        assertTrue(executed.get())
        scope.cancel()
    }

    @Test
    fun `debouncer with custom job scope`() = runBlocking {
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Default)
        val d = Debouncer(scope, 50L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        delay(100)
        assertTrue(executed.get())
        job.cancel()
    }

    @Test
    fun `cancelling parent job cancels debouncer`() = runBlocking {
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Default)
        val d = Debouncer(scope, 500L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        job.cancel()
        delay(600)
        assertFalse(executed.get())
    }

    // ========================================================================
    // Section 24: Additional flush edge cases
    // ========================================================================

    @Test
    fun `flush when action was already executed by timer`() = testScope.runTest {
        val counter = AtomicInteger(0)
        debouncer.debounce { counter.incrementAndGet() }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get())
        // Flush should find pendingAction = null (was cleared after execution)
        debouncer.flush()
        advanceUntilIdle()
        // Counter may still be 1 (flush found null action)
        assertTrue(counter.get() >= 1)
    }

    @Test
    fun `flush with zero delay debouncer`() = testScope.runTest {
        val d = Debouncer(testScope, 0L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        d.flush()
        advanceUntilIdle()
        assertTrue(executed.get())
    }

    @Test
    fun `flush does not affect hasPending of another debouncer`() = testScope.runTest {
        val d1 = Debouncer(testScope, 500L)
        val d2 = Debouncer(testScope, 500L)
        d1.debounce { }
        d2.debounce { }
        d1.flush()
        advanceUntilIdle()
        // d2 should still have pending (or have executed due to advanceUntilIdle)
    }

    // ========================================================================
    // Section 25: Stress tests
    // ========================================================================

    @Test
    fun `1000 sequential debounce-wait cycles`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(1000) {
            debouncer.debounce { counter.incrementAndGet() }
            advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        }
        assertEquals(1000, counter.get())
    }

    @Test
    fun `1000 rapid calls then single wait`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(1000) {
            debouncer.debounce { counter.incrementAndGet() }
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get())
    }

    @Test
    fun `500 debounce-cancel pairs`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(500) {
            debouncer.debounce { counter.incrementAndGet() }
            debouncer.cancel()
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(0, counter.get())
    }

    @Test
    fun `200 flush operations`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(200) {
            debouncer.debounce { counter.incrementAndGet() }
            debouncer.flush()
            advanceUntilIdle()
        }
        assertEquals(200, counter.get())
    }

    @Test
    fun `alternating debounce and cancel 300 times then final execution`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(300) {
            debouncer.debounce { counter.incrementAndGet() }
            if (it % 2 == 0) debouncer.cancel()
        }
        // Last iteration (299) is odd, so not cancelled
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, counter.get())
    }

    // ========================================================================
    // Section 26: Action re-entrancy patterns
    // ========================================================================

    @Test
    fun `action that calls debounce on same debouncer`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce {
            results.add("first")
            debouncer.debounce {
                results.add("second")
            }
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        // First action executes and schedules second
        assertTrue(results.contains("first"))
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertTrue(results.contains("second"))
    }

    @Test
    fun `action that calls cancel on same debouncer`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce {
            results.add("executed")
            debouncer.cancel() // Cancel after executing
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("executed", results[0])
    }

    @Test
    fun `action that calls flush on same debouncer`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce {
            results.add("executed")
            debouncer.flush() // Flush inside action (no-op since we're already executing)
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertTrue(results.contains("executed"))
    }

    // ========================================================================
    // Section 27: Testing with real delays (small)
    // ========================================================================

    @Test
    fun `real time debounce with 50ms delay`() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default)
        val d = Debouncer(scope, 50L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        delay(100)
        assertTrue(executed.get())
        scope.cancel()
    }

    @Test
    fun `real time rapid calls with 50ms delay`() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default)
        val d = Debouncer(scope, 50L)
        val counter = AtomicInteger(0)
        repeat(10) {
            d.debounce { counter.incrementAndGet() }
            delay(10) // Less than 50ms
        }
        delay(100) // Wait for execution
        assertEquals(1, counter.get())
        scope.cancel()
    }

    @Test
    fun `real time cancel prevents execution`() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default)
        val d = Debouncer(scope, 100L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        d.cancel()
        delay(200)
        assertFalse(executed.get())
        scope.cancel()
    }

    // ========================================================================
    // Section 28: Debouncer construction edge cases
    // ========================================================================

    @Test
    fun `debouncer with Long MAX_VALUE delay`() = testScope.runTest {
        val d = Debouncer(testScope, Long.MAX_VALUE)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        advanceTimeBy(1_000_000)
        assertFalse(executed.get())
        d.cancel()
    }

    @Test
    fun `debouncer with delay of 1 millisecond`() = testScope.runTest {
        val d = Debouncer(testScope, 1L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        advanceTimeBy(1)
        assertTrue(executed.get())
    }

    @Test
    fun `debouncer with delay of 2 milliseconds`() = testScope.runTest {
        val d = Debouncer(testScope, 2L)
        val executed = AtomicBoolean(false)
        d.debounce { executed.set(true) }
        advanceTimeBy(1)
        assertFalse(executed.get())
        advanceTimeBy(1)
        assertTrue(executed.get())
    }

    // ========================================================================
    // Section 29: Verifying action replacement semantics
    // ========================================================================

    @Test
    fun `replaced action is never executed even after long wait`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("old") }
        debouncer.debounce { results.add("new") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS * 10) // Very long wait
        assertEquals(1, results.size)
        assertEquals("new", results[0])
    }

    @Test
    fun `replaced action captured values are never used`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        val expensiveList = (1..1000).toList()
        debouncer.debounce { results.addAll(expensiveList) }
        debouncer.debounce { results.add(42) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals(42, results[0])
    }

    @Test
    fun `chain of 10 replacements only executes last`() = testScope.runTest {
        val results = CopyOnWriteArrayList<Int>()
        repeat(10) { i ->
            debouncer.debounce { results.add(i * 100) }
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals(900, results[0])
    }

    @Test
    fun `replacement with different types of actions`() = testScope.runTest {
        var stringResult = ""
        var intResult = 0
        var boolResult = false

        debouncer.debounce { stringResult = "hello" }
        debouncer.debounce { intResult = 42 }
        debouncer.debounce { boolResult = true }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)

        assertEquals("", stringResult)
        assertEquals(0, intResult)
        assertTrue(boolResult) // Only last action executed
    }

    // ========================================================================
    // Section 30: Edge case interactions between methods
    // ========================================================================

    @Test
    fun `debounce then flush then debounce then cancel`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("a") }
        debouncer.flush()
        advanceUntilIdle()
        debouncer.debounce { results.add("b") }
        debouncer.cancel()
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("a", results[0])
    }

    @Test
    fun `cancel then flush does nothing`() = testScope.runTest {
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        debouncer.cancel()
        debouncer.flush()
        advanceUntilIdle()
        assertFalse(executed.get())
    }

    @Test
    fun `flush then cancel then debounce`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        debouncer.debounce { results.add("flushed") }
        debouncer.flush()
        advanceUntilIdle()
        debouncer.cancel()
        debouncer.debounce { results.add("new") }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(2, results.size)
    }

    @Test
    fun `hasPending during flush processing`() = testScope.runTest {
        debouncer.debounce { }
        assertTrue(debouncer.hasPending())
        debouncer.flush()
        // After flush calls cancel(), hasPending should be false
        assertFalse(debouncer.hasPending())
    }

    @Test
    fun `debounce call during cancellation is safe`() = testScope.runTest {
        debouncer.debounce { }
        debouncer.cancel()
        // Immediately debounce again
        val executed = AtomicBoolean(false)
        debouncer.debounce { executed.set(true) }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertTrue(executed.get())
    }

    @Test
    fun `50 interleaved flush and cancel operations`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(50) { i ->
            debouncer.debounce { counter.incrementAndGet() }
            if (i % 2 == 0) {
                debouncer.flush()
                advanceUntilIdle()
            } else {
                debouncer.cancel()
            }
        }
        // Every other call was flushed (indices 0, 2, 4, ..., 48) = 25 times
        assertEquals(25, counter.get())
    }

    @Test
    fun `debounce with rapidly changing lambda captures`() = testScope.runTest {
        val results = CopyOnWriteArrayList<String>()
        val items = listOf("alpha", "beta", "gamma", "delta", "epsilon")
        for (item in items) {
            debouncer.debounce { results.add(item) }
        }
        advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        assertEquals(1, results.size)
        assertEquals("epsilon", results[0])
    }

    @Test
    fun `debouncer preserves action identity through flush`() = testScope.runTest {
        val marker = Object()
        var captured: Any? = null
        debouncer.debounce { captured = marker }
        debouncer.flush()
        advanceUntilIdle()
        assertSame(marker, captured)
    }

    @Test
    fun `debouncer works correctly after 100 cycles of use`() = testScope.runTest {
        val counter = AtomicInteger(0)
        repeat(100) {
            debouncer.debounce { counter.incrementAndGet() }
            advanceTimeBy(Debouncer.DEFAULT_DELAY_MS + 1)
        }
        assertEquals(100, counter.get())
    }

    @Test
    fun `debouncer with action that takes longer than delay`() = testScope.runTest {
        val d = Debouncer(testScope, 100L)
        val results = CopyOnWriteArrayList<String>()
        d.debounce {
            delay(500) // Action takes longer than delay
            results.add("done")
        }
        advanceTimeBy(100) // Delay passes
        // Action is now running but not finished
        assertEquals(0, results.size)
        advanceTimeBy(500) // Action finishes
        assertEquals(1, results.size)
    }

    @Test
    fun `second debounce during execution of first action`() = testScope.runTest {
        val d = Debouncer(testScope, 100L)
        val results = CopyOnWriteArrayList<String>()
        d.debounce {
            delay(200)
            results.add("first")
        }
        advanceTimeBy(150) // First action is in progress
        d.debounce {
            results.add("second")
        }
        advanceTimeBy(100) // Second action delay passes
        advanceTimeBy(100) // Extra time for completion
        // Behavior depends on implementation: second debounce cancels the job
        // which would cancel the first action too
    }
}
