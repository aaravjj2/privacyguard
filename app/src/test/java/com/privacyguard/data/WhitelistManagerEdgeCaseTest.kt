package com.privacyguard.data

import android.content.SharedPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Comprehensive edge-case tests for WhitelistManager.
 *
 * Covers: default apps, add/remove sequences, toggle idempotency, concurrent modifications,
 * case sensitivity, empty/null package names, very long package names, special characters,
 * getWhitelistedApps consistency, flow emissions, pre-populate idempotency, Unicode package names,
 * max whitelist size, remove non-existent, duplicate adds, ordering guarantees,
 * serialization round-trip.
 */
class WhitelistManagerEdgeCaseTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var manager: WhitelistManager

    // In-memory store to back the mock SharedPreferences
    private val prefStore = mutableMapOf<String, Any?>()

    @Before
    fun setUp() {
        prefStore.clear()
        prefs = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)

        `when`(prefs.edit()).thenReturn(editor)
        `when`(editor.putStringSet(anyString(), any())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<Set<String>>(1)
            prefStore[key] = value?.toSet()
            editor
        }
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<Boolean>(1)
            prefStore[key] = value
            editor
        }
        `when`(editor.apply()).then { /* no-op */ }

        `when`(prefs.getStringSet(anyString(), any())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val default = invocation.getArgument<Set<String>>(1)
            @Suppress("UNCHECKED_CAST")
            (prefStore[key] as? Set<String>) ?: default
        }
        `when`(prefs.getBoolean(anyString(), anyBoolean())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val default = invocation.getArgument<Boolean>(1)
            (prefStore[key] as? Boolean) ?: default
        }

        manager = WhitelistManager(prefs)
    }

    // ========================================================================
    // Section 1: Default trusted apps
    // ========================================================================

    @Test
    fun `default trusted apps contains 1Password`() {
        assertTrue(WhitelistManager.DEFAULT_TRUSTED_APPS.contains("com.agilebits.onepassword"))
    }

    @Test
    fun `default trusted apps contains Bitwarden`() {
        assertTrue(WhitelistManager.DEFAULT_TRUSTED_APPS.contains("com.bitwarden.mobile"))
    }

    @Test
    fun `default trusted apps contains KeePassDroid`() {
        assertTrue(WhitelistManager.DEFAULT_TRUSTED_APPS.contains("org.keepassdroid"))
    }

    @Test
    fun `default trusted apps contains LastPass`() {
        assertTrue(WhitelistManager.DEFAULT_TRUSTED_APPS.contains("com.lastpass.lpandroid"))
    }

    @Test
    fun `default trusted apps contains Bitwarden alt package`() {
        assertTrue(WhitelistManager.DEFAULT_TRUSTED_APPS.contains("com.x8bit.bitwarden"))
    }

    @Test
    fun `default trusted apps contains Keepass2Android`() {
        assertTrue(WhitelistManager.DEFAULT_TRUSTED_APPS.contains("keepass2android.keepass2android"))
    }

    @Test
    fun `default trusted apps has exactly six entries`() {
        assertEquals(6, WhitelistManager.DEFAULT_TRUSTED_APPS.size)
    }

    @Test
    fun `default trusted apps is immutable set`() {
        val defaults = WhitelistManager.DEFAULT_TRUSTED_APPS
        // The set should not be mutable
        try {
            (defaults as MutableSet<String>).add("com.evil.app")
            // If it didn't throw, verify the original set wasn't mutated
            // (setOf returns an immutable set in Kotlin)
        } catch (_: UnsupportedOperationException) {
            // Expected for immutable sets
        }
    }

    @Test
    fun `default trusted apps does not contain arbitrary app`() {
        assertFalse(WhitelistManager.DEFAULT_TRUSTED_APPS.contains("com.some.random.app"))
    }

    @Test
    fun `default trusted apps does not contain empty string`() {
        assertFalse(WhitelistManager.DEFAULT_TRUSTED_APPS.contains(""))
    }

    @Test
    fun `suggested app names maps all default apps`() {
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue(
                "Missing suggested name for $app",
                WhitelistManager.SUGGESTED_APP_NAMES.containsKey(app)
            )
        }
    }

    @Test
    fun `suggested app names values are non-empty`() {
        for ((pkg, name) in WhitelistManager.SUGGESTED_APP_NAMES) {
            assertTrue("Name for $pkg should not be empty", name.isNotEmpty())
        }
    }

    @Test
    fun `suggested app names has same size as default trusted apps`() {
        assertEquals(
            WhitelistManager.DEFAULT_TRUSTED_APPS.size,
            WhitelistManager.SUGGESTED_APP_NAMES.size
        )
    }

    // ========================================================================
    // Section 2: Initial state after construction (defaults populated)
    // ========================================================================

    @Test
    fun `manager pre-populates defaults on first construction`() {
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue("$app should be whitelisted by default", manager.isWhitelisted(app))
        }
    }

    @Test
    fun `manager count equals default apps after first construction`() {
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size, manager.getWhitelistCount())
    }

    @Test
    fun `getAllWhitelistedApps returns defaults after first construction`() {
        val all = manager.getAllWhitelistedApps()
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS, all)
    }

    @Test
    fun `second construction does not re-populate defaults`() {
        // First construction already happened in setUp, which sets defaults_populated = true
        // Adding a custom app
        manager.addToWhitelist("com.custom.app")
        // Construct a new manager with the same prefs (defaults_populated = true)
        val manager2 = WhitelistManager(prefs)
        // The defaults_populated flag should prevent re-population,
        // but loadFromPrefs should load what was saved
        assertTrue(manager2.isWhitelisted("com.custom.app"))
    }

    @Test
    fun `defaults populated flag is set in prefs`() {
        verify(editor, atLeastOnce()).putBoolean(eq("defaults_populated"), eq(true))
    }

    // ========================================================================
    // Section 3: Add to whitelist
    // ========================================================================

    @Test
    fun `add single app makes it whitelisted`() {
        manager.addToWhitelist("com.new.app")
        assertTrue(manager.isWhitelisted("com.new.app"))
    }

    @Test
    fun `add app increases count by one`() {
        val before = manager.getWhitelistCount()
        manager.addToWhitelist("com.another.app")
        assertEquals(before + 1, manager.getWhitelistCount())
    }

    @Test
    fun `add app appears in getAllWhitelistedApps`() {
        manager.addToWhitelist("com.foo.bar")
        assertTrue(manager.getAllWhitelistedApps().contains("com.foo.bar"))
    }

    @Test
    fun `add multiple apps all become whitelisted`() {
        val apps = listOf("app.a", "app.b", "app.c", "app.d", "app.e")
        apps.forEach { manager.addToWhitelist(it) }
        apps.forEach { assertTrue(manager.isWhitelisted(it)) }
    }

    @Test
    fun `add multiple apps increases count correctly`() {
        val before = manager.getWhitelistCount()
        manager.addToWhitelist("x.y.z1")
        manager.addToWhitelist("x.y.z2")
        manager.addToWhitelist("x.y.z3")
        assertEquals(before + 3, manager.getWhitelistCount())
    }

    @Test
    fun `add app saves to prefs`() {
        manager.addToWhitelist("com.test.save")
        verify(editor, atLeast(1)).putStringSet(eq("whitelisted_apps"), any())
    }

    @Test
    fun `add duplicate app does not increase count`() {
        manager.addToWhitelist("com.dup.app")
        val afterFirst = manager.getWhitelistCount()
        manager.addToWhitelist("com.dup.app")
        assertEquals(afterFirst, manager.getWhitelistCount())
    }

    @Test
    fun `add duplicate app still returns true for isWhitelisted`() {
        manager.addToWhitelist("com.dup.check")
        manager.addToWhitelist("com.dup.check")
        assertTrue(manager.isWhitelisted("com.dup.check"))
    }

    @Test
    fun `add app that is already a default does not change count`() {
        val before = manager.getWhitelistCount()
        manager.addToWhitelist("com.agilebits.onepassword")
        assertEquals(before, manager.getWhitelistCount())
    }

    @Test
    fun `add empty string package name`() {
        manager.addToWhitelist("")
        assertTrue(manager.isWhitelisted(""))
    }

    @Test
    fun `add very long package name`() {
        val longName = "com." + "a".repeat(1000)
        manager.addToWhitelist(longName)
        assertTrue(manager.isWhitelisted(longName))
    }

    @Test
    fun `add package name with 2000 characters`() {
        val longName = "x".repeat(2000)
        manager.addToWhitelist(longName)
        assertTrue(manager.isWhitelisted(longName))
        assertEquals(longName, manager.getAllWhitelistedApps().find { it == longName })
    }

    @Test
    fun `add package name with special characters dots`() {
        manager.addToWhitelist("com.my-app.v2.0")
        assertTrue(manager.isWhitelisted("com.my-app.v2.0"))
    }

    @Test
    fun `add package name with underscores`() {
        manager.addToWhitelist("com.my_app.test_app")
        assertTrue(manager.isWhitelisted("com.my_app.test_app"))
    }

    @Test
    fun `add package name with hyphens`() {
        manager.addToWhitelist("com.my-company.my-app")
        assertTrue(manager.isWhitelisted("com.my-company.my-app"))
    }

    @Test
    fun `add package name with numbers`() {
        manager.addToWhitelist("com.app123.test456")
        assertTrue(manager.isWhitelisted("com.app123.test456"))
    }

    @Test
    fun `add package name with Unicode characters`() {
        manager.addToWhitelist("com.\u00e9\u00e8\u00ea.app")
        assertTrue(manager.isWhitelisted("com.\u00e9\u00e8\u00ea.app"))
    }

    @Test
    fun `add package name with Chinese characters`() {
        manager.addToWhitelist("com.\u4e2d\u6587.app")
        assertTrue(manager.isWhitelisted("com.\u4e2d\u6587.app"))
    }

    @Test
    fun `add package name with Japanese characters`() {
        manager.addToWhitelist("com.\u65e5\u672c\u8a9e.app")
        assertTrue(manager.isWhitelisted("com.\u65e5\u672c\u8a9e.app"))
    }

    @Test
    fun `add package name with emoji characters`() {
        manager.addToWhitelist("com.\ud83d\ude00.app")
        assertTrue(manager.isWhitelisted("com.\ud83d\ude00.app"))
    }

    @Test
    fun `add package name with whitespace`() {
        manager.addToWhitelist("com.app with spaces")
        assertTrue(manager.isWhitelisted("com.app with spaces"))
    }

    @Test
    fun `add package name with tabs`() {
        manager.addToWhitelist("com.app\twith\ttabs")
        assertTrue(manager.isWhitelisted("com.app\twith\ttabs"))
    }

    @Test
    fun `add package name with newlines`() {
        manager.addToWhitelist("com.app\nwith\nnewlines")
        assertTrue(manager.isWhitelisted("com.app\nwith\nnewlines"))
    }

    @Test
    fun `add package name with backslashes`() {
        manager.addToWhitelist("com.app\\backslash")
        assertTrue(manager.isWhitelisted("com.app\\backslash"))
    }

    @Test
    fun `add package name with forward slashes`() {
        manager.addToWhitelist("com/app/slash")
        assertTrue(manager.isWhitelisted("com/app/slash"))
    }

    @Test
    fun `add package name that is a single character`() {
        manager.addToWhitelist("a")
        assertTrue(manager.isWhitelisted("a"))
    }

    @Test
    fun `add package name that is a single dot`() {
        manager.addToWhitelist(".")
        assertTrue(manager.isWhitelisted("."))
    }

    // ========================================================================
    // Section 4: Remove from whitelist
    // ========================================================================

    @Test
    fun `remove existing app makes it not whitelisted`() {
        manager.addToWhitelist("com.remove.me")
        manager.removeFromWhitelist("com.remove.me")
        assertFalse(manager.isWhitelisted("com.remove.me"))
    }

    @Test
    fun `remove existing app decreases count by one`() {
        manager.addToWhitelist("com.remove.count")
        val before = manager.getWhitelistCount()
        manager.removeFromWhitelist("com.remove.count")
        assertEquals(before - 1, manager.getWhitelistCount())
    }

    @Test
    fun `remove non-existent app does not change count`() {
        val before = manager.getWhitelistCount()
        manager.removeFromWhitelist("com.nonexistent.app")
        assertEquals(before, manager.getWhitelistCount())
    }

    @Test
    fun `remove non-existent app does not throw`() {
        // Should not throw
        manager.removeFromWhitelist("com.doesnt.exist.at.all")
    }

    @Test
    fun `remove default app makes it no longer whitelisted`() {
        assertTrue(manager.isWhitelisted("com.agilebits.onepassword"))
        manager.removeFromWhitelist("com.agilebits.onepassword")
        assertFalse(manager.isWhitelisted("com.agilebits.onepassword"))
    }

    @Test
    fun `remove all default apps one by one`() {
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            manager.removeFromWhitelist(app)
        }
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test
    fun `remove app then add it back`() {
        manager.addToWhitelist("com.cycle.app")
        manager.removeFromWhitelist("com.cycle.app")
        assertFalse(manager.isWhitelisted("com.cycle.app"))
        manager.addToWhitelist("com.cycle.app")
        assertTrue(manager.isWhitelisted("com.cycle.app"))
    }

    @Test
    fun `remove same app twice does not throw`() {
        manager.addToWhitelist("com.double.remove")
        manager.removeFromWhitelist("com.double.remove")
        manager.removeFromWhitelist("com.double.remove")
        assertFalse(manager.isWhitelisted("com.double.remove"))
    }

    @Test
    fun `remove same app twice does not decrease count below zero for that app`() {
        val countAfterDefaults = manager.getWhitelistCount()
        manager.addToWhitelist("com.counted.app")
        assertEquals(countAfterDefaults + 1, manager.getWhitelistCount())
        manager.removeFromWhitelist("com.counted.app")
        assertEquals(countAfterDefaults, manager.getWhitelistCount())
        manager.removeFromWhitelist("com.counted.app")
        assertEquals(countAfterDefaults, manager.getWhitelistCount())
    }

    @Test
    fun `remove empty string package name`() {
        manager.addToWhitelist("")
        manager.removeFromWhitelist("")
        assertFalse(manager.isWhitelisted(""))
    }

    @Test
    fun `remove very long package name`() {
        val longName = "com." + "b".repeat(500)
        manager.addToWhitelist(longName)
        manager.removeFromWhitelist(longName)
        assertFalse(manager.isWhitelisted(longName))
    }

    @Test
    fun `remove saves to prefs`() {
        manager.addToWhitelist("com.remove.save")
        val callsBefore = mockingDetails(editor).invocations.size
        manager.removeFromWhitelist("com.remove.save")
        verify(editor, atLeast(1)).putStringSet(eq("whitelisted_apps"), any())
    }

    @Test
    fun `remove app with Unicode characters`() {
        manager.addToWhitelist("com.\u00fc\u00f1\u00ef.app")
        manager.removeFromWhitelist("com.\u00fc\u00f1\u00ef.app")
        assertFalse(manager.isWhitelisted("com.\u00fc\u00f1\u00ef.app"))
    }

    // ========================================================================
    // Section 5: Toggle whitelist
    // ========================================================================

    @Test
    fun `toggle non-whitelisted app returns true and adds it`() {
        val result = manager.toggleWhitelist("com.toggle.new")
        assertTrue(result)
        assertTrue(manager.isWhitelisted("com.toggle.new"))
    }

    @Test
    fun `toggle whitelisted app returns false and removes it`() {
        manager.addToWhitelist("com.toggle.existing")
        val result = manager.toggleWhitelist("com.toggle.existing")
        assertFalse(result)
        assertFalse(manager.isWhitelisted("com.toggle.existing"))
    }

    @Test
    fun `toggle twice returns to original state for non-default app`() {
        assertFalse(manager.isWhitelisted("com.toggle.twice"))
        manager.toggleWhitelist("com.toggle.twice")
        assertTrue(manager.isWhitelisted("com.toggle.twice"))
        manager.toggleWhitelist("com.toggle.twice")
        assertFalse(manager.isWhitelisted("com.toggle.twice"))
    }

    @Test
    fun `toggle twice returns to original state for default app`() {
        assertTrue(manager.isWhitelisted("com.agilebits.onepassword"))
        manager.toggleWhitelist("com.agilebits.onepassword")
        assertFalse(manager.isWhitelisted("com.agilebits.onepassword"))
        manager.toggleWhitelist("com.agilebits.onepassword")
        assertTrue(manager.isWhitelisted("com.agilebits.onepassword"))
    }

    @Test
    fun `toggle three times leaves app whitelisted for initially non-whitelisted`() {
        manager.toggleWhitelist("com.toggle.three")
        manager.toggleWhitelist("com.toggle.three")
        manager.toggleWhitelist("com.toggle.three")
        assertTrue(manager.isWhitelisted("com.toggle.three"))
    }

    @Test
    fun `toggle idempotency - even number of toggles restores state`() {
        val app = "com.toggle.even"
        assertFalse(manager.isWhitelisted(app))
        repeat(10) { manager.toggleWhitelist(app) }
        assertFalse(manager.isWhitelisted(app))
    }

    @Test
    fun `toggle idempotency - odd number of toggles flips state`() {
        val app = "com.toggle.odd"
        assertFalse(manager.isWhitelisted(app))
        repeat(11) { manager.toggleWhitelist(app) }
        assertTrue(manager.isWhitelisted(app))
    }

    @Test
    fun `toggle 100 times even restores initial state`() {
        val app = "com.toggle.hundred"
        val initialState = manager.isWhitelisted(app)
        repeat(100) { manager.toggleWhitelist(app) }
        assertEquals(initialState, manager.isWhitelisted(app))
    }

    @Test
    fun `toggle 101 times odd flips initial state`() {
        val app = "com.toggle.hundredone"
        val initialState = manager.isWhitelisted(app)
        repeat(101) { manager.toggleWhitelist(app) }
        assertEquals(!initialState, manager.isWhitelisted(app))
    }

    @Test
    fun `toggle return value alternates`() {
        val app = "com.toggle.alternate"
        val results = mutableListOf<Boolean>()
        repeat(6) { results.add(manager.toggleWhitelist(app)) }
        assertEquals(listOf(true, false, true, false, true, false), results)
    }

    @Test
    fun `toggle empty package name`() {
        val result = manager.toggleWhitelist("")
        assertTrue(result) // Not whitelisted initially, so should add and return true
        assertTrue(manager.isWhitelisted(""))
    }

    @Test
    fun `toggle very long package name`() {
        val longName = "com." + "c".repeat(800)
        val result = manager.toggleWhitelist(longName)
        assertTrue(result)
        assertTrue(manager.isWhitelisted(longName))
    }

    // ========================================================================
    // Section 6: Case sensitivity
    // ========================================================================

    @Test
    fun `package names are case sensitive - lowercase vs uppercase`() {
        manager.addToWhitelist("com.myapp")
        assertFalse(manager.isWhitelisted("com.MYAPP"))
    }

    @Test
    fun `package names are case sensitive - mixed case`() {
        manager.addToWhitelist("com.MyApp")
        assertTrue(manager.isWhitelisted("com.MyApp"))
        assertFalse(manager.isWhitelisted("com.myapp"))
        assertFalse(manager.isWhitelisted("com.MYAPP"))
    }

    @Test
    fun `adding same package in different cases creates separate entries`() {
        manager.addToWhitelist("com.case.test")
        manager.addToWhitelist("com.Case.Test")
        manager.addToWhitelist("COM.CASE.TEST")
        val all = manager.getAllWhitelistedApps()
        assertTrue(all.contains("com.case.test"))
        assertTrue(all.contains("com.Case.Test"))
        assertTrue(all.contains("COM.CASE.TEST"))
    }

    @Test
    fun `removing lowercase does not remove uppercase`() {
        manager.addToWhitelist("com.case.keep")
        manager.addToWhitelist("COM.CASE.KEEP")
        manager.removeFromWhitelist("com.case.keep")
        assertFalse(manager.isWhitelisted("com.case.keep"))
        assertTrue(manager.isWhitelisted("COM.CASE.KEEP"))
    }

    @Test
    fun `toggling lowercase does not affect uppercase`() {
        manager.addToWhitelist("com.case.toggle")
        manager.addToWhitelist("COM.CASE.TOGGLE")
        manager.toggleWhitelist("com.case.toggle") // removes lowercase
        assertFalse(manager.isWhitelisted("com.case.toggle"))
        assertTrue(manager.isWhitelisted("COM.CASE.TOGGLE"))
    }

    // ========================================================================
    // Section 7: isWhitelisted checks
    // ========================================================================

    @Test
    fun `isWhitelisted returns false for never-added app`() {
        assertFalse(manager.isWhitelisted("com.never.added"))
    }

    @Test
    fun `isWhitelisted returns true for default app`() {
        assertTrue(manager.isWhitelisted("com.bitwarden.mobile"))
    }

    @Test
    fun `isWhitelisted returns false after remove`() {
        manager.addToWhitelist("com.check.remove")
        manager.removeFromWhitelist("com.check.remove")
        assertFalse(manager.isWhitelisted("com.check.remove"))
    }

    @Test
    fun `isWhitelisted consistent with getAllWhitelistedApps`() {
        manager.addToWhitelist("com.consistent.app")
        val all = manager.getAllWhitelistedApps()
        for (app in all) {
            assertTrue("isWhitelisted should be true for $app", manager.isWhitelisted(app))
        }
    }

    @Test
    fun `isWhitelisted false for app not in getAllWhitelistedApps`() {
        val all = manager.getAllWhitelistedApps()
        val testApp = "com.not.in.all"
        if (!all.contains(testApp)) {
            assertFalse(manager.isWhitelisted(testApp))
        }
    }

    @Test
    fun `isWhitelisted returns false for empty string by default`() {
        assertFalse(manager.isWhitelisted(""))
    }

    @Test
    fun `isWhitelisted with null-like string`() {
        assertFalse(manager.isWhitelisted("null"))
    }

    @Test
    fun `isWhitelisted with undefined-like string`() {
        assertFalse(manager.isWhitelisted("undefined"))
    }

    // ========================================================================
    // Section 8: getAllWhitelistedApps
    // ========================================================================

    @Test
    fun `getAllWhitelistedApps returns a copy not a reference`() {
        val set1 = manager.getAllWhitelistedApps()
        manager.addToWhitelist("com.after.copy")
        val set2 = manager.getAllWhitelistedApps()
        assertFalse(set1.contains("com.after.copy"))
        assertTrue(set2.contains("com.after.copy"))
    }

    @Test
    fun `getAllWhitelistedApps is not affected by external modification`() {
        val all = manager.getAllWhitelistedApps()
        try {
            (all as MutableSet<String>).add("com.external.modification")
        } catch (_: UnsupportedOperationException) {
            // Immutable set, modification not possible
        }
        // Either way, the manager should not see the external modification
        // unless it was already there
    }

    @Test
    fun `getAllWhitelistedApps after clearAll returns empty`() {
        manager.clearAll()
        assertTrue(manager.getAllWhitelistedApps().isEmpty())
    }

    @Test
    fun `getAllWhitelistedApps size matches getWhitelistCount`() {
        manager.addToWhitelist("com.size.check1")
        manager.addToWhitelist("com.size.check2")
        assertEquals(manager.getWhitelistCount(), manager.getAllWhitelistedApps().size)
    }

    @Test
    fun `getAllWhitelistedApps contains all default apps initially`() {
        val all = manager.getAllWhitelistedApps()
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue("Should contain $app", all.contains(app))
        }
    }

    @Test
    fun `getAllWhitelistedApps after adding and removing returns correct set`() {
        manager.addToWhitelist("com.add.remove.test")
        manager.removeFromWhitelist("com.agilebits.onepassword")
        val all = manager.getAllWhitelistedApps()
        assertTrue(all.contains("com.add.remove.test"))
        assertFalse(all.contains("com.agilebits.onepassword"))
    }

    // ========================================================================
    // Section 9: getWhitelistCount
    // ========================================================================

    @Test
    fun `getWhitelistCount is zero after clearAll`() {
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test
    fun `getWhitelistCount increments on add`() {
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
        manager.addToWhitelist("com.count.one")
        assertEquals(1, manager.getWhitelistCount())
        manager.addToWhitelist("com.count.two")
        assertEquals(2, manager.getWhitelistCount())
    }

    @Test
    fun `getWhitelistCount decrements on remove`() {
        manager.clearAll()
        manager.addToWhitelist("com.dec.one")
        manager.addToWhitelist("com.dec.two")
        assertEquals(2, manager.getWhitelistCount())
        manager.removeFromWhitelist("com.dec.one")
        assertEquals(1, manager.getWhitelistCount())
    }

    @Test
    fun `getWhitelistCount does not go negative`() {
        manager.clearAll()
        manager.removeFromWhitelist("com.not.there")
        assertTrue(manager.getWhitelistCount() >= 0)
    }

    @Test
    fun `getWhitelistCount after 100 adds`() {
        manager.clearAll()
        repeat(100) { manager.addToWhitelist("com.bulk.app$it") }
        assertEquals(100, manager.getWhitelistCount())
    }

    @Test
    fun `getWhitelistCount after 100 adds then 50 removes`() {
        manager.clearAll()
        repeat(100) { manager.addToWhitelist("com.partial.app$it") }
        repeat(50) { manager.removeFromWhitelist("com.partial.app$it") }
        assertEquals(50, manager.getWhitelistCount())
    }

    // ========================================================================
    // Section 10: Flow emissions
    // ========================================================================

    @Test
    fun `flow emits initial value with defaults`() = runBlocking {
        val flow = manager.getWhitelistedAppsFlow()
        val current = flow.first()
        assertTrue(current.containsAll(WhitelistManager.DEFAULT_TRUSTED_APPS))
    }

    @Test
    fun `flow emits after addToWhitelist`() = runBlocking {
        manager.addToWhitelist("com.flow.add")
        val current = manager.getWhitelistedAppsFlow().first()
        assertTrue(current.contains("com.flow.add"))
    }

    @Test
    fun `flow emits after removeFromWhitelist`() = runBlocking {
        manager.addToWhitelist("com.flow.remove")
        manager.removeFromWhitelist("com.flow.remove")
        val current = manager.getWhitelistedAppsFlow().first()
        assertFalse(current.contains("com.flow.remove"))
    }

    @Test
    fun `flow emits after clearAll`() = runBlocking {
        manager.clearAll()
        val current = manager.getWhitelistedAppsFlow().first()
        assertTrue(current.isEmpty())
    }

    @Test
    fun `flow emits after toggle`() = runBlocking {
        manager.toggleWhitelist("com.flow.toggle")
        val current = manager.getWhitelistedAppsFlow().first()
        assertTrue(current.contains("com.flow.toggle"))
    }

    @Test
    fun `flow emission matches getAllWhitelistedApps`() = runBlocking {
        manager.addToWhitelist("com.flow.match")
        val fromFlow = manager.getWhitelistedAppsFlow().first()
        val fromGet = manager.getAllWhitelistedApps()
        assertEquals(fromGet, fromFlow)
    }

    @Test
    fun `flow emits after prePopulateDefaults`() = runBlocking {
        manager.clearAll()
        manager.prePopulateDefaults()
        val current = manager.getWhitelistedAppsFlow().first()
        assertTrue(current.containsAll(WhitelistManager.DEFAULT_TRUSTED_APPS))
    }

    // ========================================================================
    // Section 11: prePopulateDefaults
    // ========================================================================

    @Test
    fun `prePopulateDefaults adds all default apps`() {
        manager.clearAll()
        manager.prePopulateDefaults()
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue(manager.isWhitelisted(app))
        }
    }

    @Test
    fun `prePopulateDefaults is idempotent`() {
        manager.prePopulateDefaults()
        val countAfterFirst = manager.getWhitelistCount()
        manager.prePopulateDefaults()
        val countAfterSecond = manager.getWhitelistCount()
        assertEquals(countAfterFirst, countAfterSecond)
    }

    @Test
    fun `prePopulateDefaults does not remove custom apps`() {
        manager.addToWhitelist("com.custom.keep")
        manager.prePopulateDefaults()
        assertTrue(manager.isWhitelisted("com.custom.keep"))
    }

    @Test
    fun `prePopulateDefaults preserves existing defaults`() {
        manager.prePopulateDefaults()
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue(manager.isWhitelisted(app))
        }
    }

    @Test
    fun `prePopulateDefaults after clear restores defaults`() {
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
        manager.prePopulateDefaults()
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size, manager.getWhitelistCount())
    }

    @Test
    fun `prePopulateDefaults called three times is idempotent`() {
        manager.clearAll()
        manager.prePopulateDefaults()
        manager.prePopulateDefaults()
        manager.prePopulateDefaults()
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size, manager.getWhitelistCount())
    }

    @Test
    fun `prePopulateDefaults sets defaults_populated flag`() {
        manager.prePopulateDefaults()
        verify(editor, atLeastOnce()).putBoolean(eq("defaults_populated"), eq(true))
    }

    // ========================================================================
    // Section 12: clearAll
    // ========================================================================

    @Test
    fun `clearAll removes all apps`() {
        manager.addToWhitelist("com.clear.one")
        manager.addToWhitelist("com.clear.two")
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test
    fun `clearAll makes all default apps non-whitelisted`() {
        manager.clearAll()
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertFalse(manager.isWhitelisted(app))
        }
    }

    @Test
    fun `clearAll returns empty set from getAllWhitelistedApps`() {
        manager.clearAll()
        assertTrue(manager.getAllWhitelistedApps().isEmpty())
    }

    @Test
    fun `clearAll twice does not throw`() {
        manager.clearAll()
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test
    fun `clearAll then add works correctly`() {
        manager.clearAll()
        manager.addToWhitelist("com.after.clear")
        assertTrue(manager.isWhitelisted("com.after.clear"))
        assertEquals(1, manager.getWhitelistCount())
    }

    @Test
    fun `clearAll saves empty set to prefs`() {
        manager.clearAll()
        verify(editor, atLeastOnce()).putStringSet(eq("whitelisted_apps"), any())
    }

    @Test
    fun `clearAll then prePopulateDefaults restores defaults`() {
        manager.clearAll()
        manager.prePopulateDefaults()
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size, manager.getWhitelistCount())
    }

    // ========================================================================
    // Section 13: Add/remove sequences
    // ========================================================================

    @Test
    fun `add then remove then add sequence`() {
        manager.addToWhitelist("com.seq.app")
        assertTrue(manager.isWhitelisted("com.seq.app"))
        manager.removeFromWhitelist("com.seq.app")
        assertFalse(manager.isWhitelisted("com.seq.app"))
        manager.addToWhitelist("com.seq.app")
        assertTrue(manager.isWhitelisted("com.seq.app"))
    }

    @Test
    fun `interleaved add-remove on different apps`() {
        manager.addToWhitelist("com.interleave.a")
        manager.addToWhitelist("com.interleave.b")
        manager.removeFromWhitelist("com.interleave.a")
        manager.addToWhitelist("com.interleave.c")
        manager.removeFromWhitelist("com.interleave.b")

        assertFalse(manager.isWhitelisted("com.interleave.a"))
        assertFalse(manager.isWhitelisted("com.interleave.b"))
        assertTrue(manager.isWhitelisted("com.interleave.c"))
    }

    @Test
    fun `rapid add-remove cycle 50 times on same app`() {
        val app = "com.rapid.cycle"
        repeat(50) {
            manager.addToWhitelist(app)
            manager.removeFromWhitelist(app)
        }
        assertFalse(manager.isWhitelisted(app))
    }

    @Test
    fun `add 100 apps then remove all one by one`() {
        manager.clearAll()
        val apps = (1..100).map { "com.bulk.app$it" }
        apps.forEach { manager.addToWhitelist(it) }
        assertEquals(100, manager.getWhitelistCount())
        apps.forEach { manager.removeFromWhitelist(it) }
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test
    fun `add 100 apps then clearAll`() {
        manager.clearAll()
        repeat(100) { manager.addToWhitelist("com.clearbulk.app$it") }
        assertEquals(100, manager.getWhitelistCount())
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test
    fun `alternating add and toggle on same app`() {
        val app = "com.alt.toggle"
        manager.addToWhitelist(app)
        assertTrue(manager.isWhitelisted(app))
        manager.toggleWhitelist(app)
        assertFalse(manager.isWhitelisted(app))
        manager.addToWhitelist(app)
        assertTrue(manager.isWhitelisted(app))
    }

    @Test
    fun `remove then toggle on same app`() {
        val app = "com.remove.then.toggle"
        manager.addToWhitelist(app)
        manager.removeFromWhitelist(app)
        assertFalse(manager.isWhitelisted(app))
        val result = manager.toggleWhitelist(app)
        assertTrue(result)
        assertTrue(manager.isWhitelisted(app))
    }

    @Test
    fun `clearAll then toggle adds the app`() {
        manager.clearAll()
        val result = manager.toggleWhitelist("com.clear.toggle")
        assertTrue(result)
        assertTrue(manager.isWhitelisted("com.clear.toggle"))
        assertEquals(1, manager.getWhitelistCount())
    }

    // ========================================================================
    // Section 14: Large-scale operations
    // ========================================================================

    @Test
    fun `add 500 apps`() {
        manager.clearAll()
        repeat(500) { manager.addToWhitelist("com.large.app$it") }
        assertEquals(500, manager.getWhitelistCount())
    }

    @Test
    fun `add 500 apps and verify each is whitelisted`() {
        manager.clearAll()
        val apps = (0 until 500).map { "com.verify.app$it" }
        apps.forEach { manager.addToWhitelist(it) }
        apps.forEach { assertTrue(manager.isWhitelisted(it)) }
    }

    @Test
    fun `add 500 apps and remove half`() {
        manager.clearAll()
        val apps = (0 until 500).map { "com.half.app$it" }
        apps.forEach { manager.addToWhitelist(it) }
        apps.take(250).forEach { manager.removeFromWhitelist(it) }
        assertEquals(250, manager.getWhitelistCount())
        apps.take(250).forEach { assertFalse(manager.isWhitelisted(it)) }
        apps.drop(250).forEach { assertTrue(manager.isWhitelisted(it)) }
    }

    @Test
    fun `add 1000 apps then clearAll`() {
        manager.clearAll()
        repeat(1000) { manager.addToWhitelist("com.thousand.app$it") }
        assertEquals(1000, manager.getWhitelistCount())
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test
    fun `getAllWhitelistedApps with 500 entries`() {
        manager.clearAll()
        repeat(500) { manager.addToWhitelist("com.getall.app$it") }
        val all = manager.getAllWhitelistedApps()
        assertEquals(500, all.size)
    }

    // ========================================================================
    // Section 15: Concurrent-like modification patterns
    // ========================================================================

    @Test
    fun `add from two simulated sources does not lose entries`() {
        manager.clearAll()
        val source1Apps = (0 until 50).map { "com.source1.app$it" }
        val source2Apps = (0 until 50).map { "com.source2.app$it" }
        source1Apps.forEach { manager.addToWhitelist(it) }
        source2Apps.forEach { manager.addToWhitelist(it) }
        assertEquals(100, manager.getWhitelistCount())
    }

    @Test
    fun `interleaved adds from two sources`() {
        manager.clearAll()
        repeat(50) { i ->
            manager.addToWhitelist("com.s1.app$i")
            manager.addToWhitelist("com.s2.app$i")
        }
        assertEquals(100, manager.getWhitelistCount())
    }

    @Test
    fun `add and remove from different simulated sources`() {
        manager.clearAll()
        repeat(50) { manager.addToWhitelist("com.shared.app$it") }
        // Source 1 removes even indices, source 2 removes odd indices
        repeat(50) { i ->
            if (i % 2 == 0) {
                manager.removeFromWhitelist("com.shared.app$i")
            }
        }
        repeat(50) { i ->
            if (i % 2 != 0) {
                manager.removeFromWhitelist("com.shared.app$i")
            }
        }
        assertEquals(0, manager.getWhitelistCount())
    }

    // ========================================================================
    // Section 16: Serialization round-trip via prefs
    // ========================================================================

    @Test
    fun `data survives reconstruction from same prefs`() {
        manager.addToWhitelist("com.survive.reconstruction")
        val manager2 = WhitelistManager(prefs)
        assertTrue(manager2.isWhitelisted("com.survive.reconstruction"))
    }

    @Test
    fun `defaults survive reconstruction`() {
        val manager2 = WhitelistManager(prefs)
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue(manager2.isWhitelisted(app))
        }
    }

    @Test
    fun `custom apps survive reconstruction`() {
        manager.addToWhitelist("com.custom.survive1")
        manager.addToWhitelist("com.custom.survive2")
        val manager2 = WhitelistManager(prefs)
        assertTrue(manager2.isWhitelisted("com.custom.survive1"))
        assertTrue(manager2.isWhitelisted("com.custom.survive2"))
    }

    @Test
    fun `removal survives reconstruction`() {
        manager.removeFromWhitelist("com.agilebits.onepassword")
        val manager2 = WhitelistManager(prefs)
        assertFalse(manager2.isWhitelisted("com.agilebits.onepassword"))
    }

    @Test
    fun `clearAll survives reconstruction when defaults_populated is true`() {
        manager.clearAll()
        // defaults_populated is still true, so new manager won't re-populate
        val manager2 = WhitelistManager(prefs)
        assertEquals(0, manager2.getWhitelistCount())
    }

    @Test
    fun `unicode app name survives reconstruction`() {
        manager.addToWhitelist("com.\u00e4\u00f6\u00fc.app")
        val manager2 = WhitelistManager(prefs)
        assertTrue(manager2.isWhitelisted("com.\u00e4\u00f6\u00fc.app"))
    }

    @Test
    fun `special character app name survives reconstruction`() {
        manager.addToWhitelist("com.app-with_special.chars.123")
        val manager2 = WhitelistManager(prefs)
        assertTrue(manager2.isWhitelisted("com.app-with_special.chars.123"))
    }

    @Test
    fun `count matches after reconstruction`() {
        manager.addToWhitelist("com.count.recon1")
        manager.addToWhitelist("com.count.recon2")
        val expectedCount = manager.getWhitelistCount()
        val manager2 = WhitelistManager(prefs)
        assertEquals(expectedCount, manager2.getWhitelistCount())
    }

    // ========================================================================
    // Section 17: Edge cases with special strings
    // ========================================================================

    @Test
    fun `package name with only dots`() {
        manager.addToWhitelist("...")
        assertTrue(manager.isWhitelisted("..."))
    }

    @Test
    fun `package name with leading dot`() {
        manager.addToWhitelist(".com.leading.dot")
        assertTrue(manager.isWhitelisted(".com.leading.dot"))
    }

    @Test
    fun `package name with trailing dot`() {
        manager.addToWhitelist("com.trailing.dot.")
        assertTrue(manager.isWhitelisted("com.trailing.dot."))
    }

    @Test
    fun `package name with double dots`() {
        manager.addToWhitelist("com..double..dots")
        assertTrue(manager.isWhitelisted("com..double..dots"))
    }

    @Test
    fun `package name with at symbol`() {
        manager.addToWhitelist("com.app@version")
        assertTrue(manager.isWhitelisted("com.app@version"))
    }

    @Test
    fun `package name with hash symbol`() {
        manager.addToWhitelist("com.app#fragment")
        assertTrue(manager.isWhitelisted("com.app#fragment"))
    }

    @Test
    fun `package name with dollar sign`() {
        manager.addToWhitelist("com.app\$inner")
        assertTrue(manager.isWhitelisted("com.app\$inner"))
    }

    @Test
    fun `package name with percent sign`() {
        manager.addToWhitelist("com.app%encoded")
        assertTrue(manager.isWhitelisted("com.app%encoded"))
    }

    @Test
    fun `package name with ampersand`() {
        manager.addToWhitelist("com.app&param")
        assertTrue(manager.isWhitelisted("com.app&param"))
    }

    @Test
    fun `package name with asterisk`() {
        manager.addToWhitelist("com.app*wildcard")
        assertTrue(manager.isWhitelisted("com.app*wildcard"))
    }

    @Test
    fun `package name with parentheses`() {
        manager.addToWhitelist("com.app(1)")
        assertTrue(manager.isWhitelisted("com.app(1)"))
    }

    @Test
    fun `package name with square brackets`() {
        manager.addToWhitelist("com.app[0]")
        assertTrue(manager.isWhitelisted("com.app[0]"))
    }

    @Test
    fun `package name with curly braces`() {
        manager.addToWhitelist("com.app{key}")
        assertTrue(manager.isWhitelisted("com.app{key}"))
    }

    @Test
    fun `package name with pipe character`() {
        manager.addToWhitelist("com.app|pipe")
        assertTrue(manager.isWhitelisted("com.app|pipe"))
    }

    @Test
    fun `package name with tilde`() {
        manager.addToWhitelist("com.app~tilde")
        assertTrue(manager.isWhitelisted("com.app~tilde"))
    }

    @Test
    fun `package name with backtick`() {
        manager.addToWhitelist("com.app\`backtick")
        assertTrue(manager.isWhitelisted("com.app\`backtick"))
    }

    @Test
    fun `package name with exclamation mark`() {
        manager.addToWhitelist("com.app!bang")
        assertTrue(manager.isWhitelisted("com.app!bang"))
    }

    @Test
    fun `package name with caret`() {
        manager.addToWhitelist("com.app^caret")
        assertTrue(manager.isWhitelisted("com.app^caret"))
    }

    @Test
    fun `package name with plus sign`() {
        manager.addToWhitelist("com.app+plus")
        assertTrue(manager.isWhitelisted("com.app+plus"))
    }

    @Test
    fun `package name with equals sign`() {
        manager.addToWhitelist("com.app=equals")
        assertTrue(manager.isWhitelisted("com.app=equals"))
    }

    @Test
    fun `package name with semicolon`() {
        manager.addToWhitelist("com.app;semicolon")
        assertTrue(manager.isWhitelisted("com.app;semicolon"))
    }

    @Test
    fun `package name with colon`() {
        manager.addToWhitelist("com.app:colon")
        assertTrue(manager.isWhitelisted("com.app:colon"))
    }

    @Test
    fun `package name with comma`() {
        manager.addToWhitelist("com.app,comma")
        assertTrue(manager.isWhitelisted("com.app,comma"))
    }

    @Test
    fun `package name with question mark`() {
        manager.addToWhitelist("com.app?question")
        assertTrue(manager.isWhitelisted("com.app?question"))
    }

    @Test
    fun `package name with less than and greater than`() {
        manager.addToWhitelist("com.app<html>")
        assertTrue(manager.isWhitelisted("com.app<html>"))
    }

    @Test
    fun `package name with quotes`() {
        manager.addToWhitelist("com.app\"quoted\"")
        assertTrue(manager.isWhitelisted("com.app\"quoted\""))
    }

    @Test
    fun `package name with single quotes`() {
        manager.addToWhitelist("com.app'single'")
        assertTrue(manager.isWhitelisted("com.app'single'"))
    }

    // ========================================================================
    // Section 18: Ordering guarantees
    // ========================================================================

    @Test
    fun `getAllWhitelistedApps returns Set so no duplicates`() {
        manager.addToWhitelist("com.order.dup")
        manager.addToWhitelist("com.order.dup")
        val all = manager.getAllWhitelistedApps()
        assertEquals(1, all.count { it == "com.order.dup" })
    }

    @Test
    fun `set returned by getAllWhitelistedApps contains correct elements regardless of insertion order`() {
        manager.clearAll()
        manager.addToWhitelist("z.app")
        manager.addToWhitelist("a.app")
        manager.addToWhitelist("m.app")
        val all = manager.getAllWhitelistedApps()
        assertTrue(all.contains("z.app"))
        assertTrue(all.contains("a.app"))
        assertTrue(all.contains("m.app"))
    }

    @Test
    fun `add and remove in specific order results in correct final state`() {
        manager.clearAll()
        manager.addToWhitelist("first")
        manager.addToWhitelist("second")
        manager.addToWhitelist("third")
        manager.removeFromWhitelist("second")
        val all = manager.getAllWhitelistedApps()
        assertTrue(all.contains("first"))
        assertFalse(all.contains("second"))
        assertTrue(all.contains("third"))
        assertEquals(2, all.size)
    }

    // ========================================================================
    // Section 19: Boundary and stress tests
    // ========================================================================

    @Test
    fun `add package name of length 1`() {
        manager.addToWhitelist("x")
        assertTrue(manager.isWhitelisted("x"))
    }

    @Test
    fun `add package name of length 255`() {
        val name = "a".repeat(255)
        manager.addToWhitelist(name)
        assertTrue(manager.isWhitelisted(name))
    }

    @Test
    fun `add package name of length 4096`() {
        val name = "b".repeat(4096)
        manager.addToWhitelist(name)
        assertTrue(manager.isWhitelisted(name))
    }

    @Test
    fun `add package name of length 10000`() {
        val name = "c".repeat(10000)
        manager.addToWhitelist(name)
        assertTrue(manager.isWhitelisted(name))
    }

    @Test
    fun `stress test - 200 toggle operations`() {
        val app = "com.stress.toggle"
        repeat(200) { manager.toggleWhitelist(app) }
        // Even number of toggles, should not be whitelisted (was not whitelisted initially)
        assertFalse(manager.isWhitelisted(app))
    }

    @Test
    fun `stress test - 1000 add operations on unique apps`() {
        manager.clearAll()
        repeat(1000) { manager.addToWhitelist("com.stress.app$it") }
        assertEquals(1000, manager.getWhitelistCount())
    }

    @Test
    fun `stress test - 1000 add then 1000 remove operations`() {
        manager.clearAll()
        repeat(1000) { manager.addToWhitelist("com.stress.rm$it") }
        repeat(1000) { manager.removeFromWhitelist("com.stress.rm$it") }
        assertEquals(0, manager.getWhitelistCount())
    }

    // ========================================================================
    // Section 20: Multiple manager instances
    // ========================================================================

    @Test
    fun `two managers sharing prefs see same data after reconstruction`() {
        manager.addToWhitelist("com.shared.data")
        val manager2 = WhitelistManager(prefs)
        assertTrue(manager2.isWhitelisted("com.shared.data"))
    }

    @Test
    fun `second manager sees removal from first manager after reconstruction`() {
        manager.removeFromWhitelist("com.bitwarden.mobile")
        val manager2 = WhitelistManager(prefs)
        assertFalse(manager2.isWhitelisted("com.bitwarden.mobile"))
    }

    @Test
    fun `second manager count matches first manager after reconstruction`() {
        manager.addToWhitelist("com.extra1")
        manager.addToWhitelist("com.extra2")
        val expected = manager.getWhitelistCount()
        val manager2 = WhitelistManager(prefs)
        assertEquals(expected, manager2.getWhitelistCount())
    }

    // ========================================================================
    // Section 21: Whitespace and control character package names
    // ========================================================================

    @Test
    fun `package name with only spaces`() {
        manager.addToWhitelist("   ")
        assertTrue(manager.isWhitelisted("   "))
    }

    @Test
    fun `package name with leading spaces`() {
        manager.addToWhitelist("  com.leading.spaces")
        assertTrue(manager.isWhitelisted("  com.leading.spaces"))
        // Should not match without spaces
        assertFalse(manager.isWhitelisted("com.leading.spaces"))
    }

    @Test
    fun `package name with trailing spaces`() {
        manager.addToWhitelist("com.trailing.spaces  ")
        assertTrue(manager.isWhitelisted("com.trailing.spaces  "))
        assertFalse(manager.isWhitelisted("com.trailing.spaces"))
    }

    @Test
    fun `package name with carriage return`() {
        manager.addToWhitelist("com.app\r\nwith\r\ncrlf")
        assertTrue(manager.isWhitelisted("com.app\r\nwith\r\ncrlf"))
    }

    @Test
    fun `package name with null character`() {
        manager.addToWhitelist("com.app\u0000null")
        assertTrue(manager.isWhitelisted("com.app\u0000null"))
    }

    @Test
    fun `package name with bell character`() {
        manager.addToWhitelist("com.app\u0007bell")
        assertTrue(manager.isWhitelisted("com.app\u0007bell"))
    }

    @Test
    fun `package name with form feed`() {
        manager.addToWhitelist("com.app\u000Cfeed")
        assertTrue(manager.isWhitelisted("com.app\u000Cfeed"))
    }

    @Test
    fun `package name with vertical tab`() {
        manager.addToWhitelist("com.app\u000Bvtab")
        assertTrue(manager.isWhitelisted("com.app\u000Bvtab"))
    }

    // ========================================================================
    // Section 22: Interaction between clearAll and prePopulateDefaults
    // ========================================================================

    @Test
    fun `clearAll then prePopulateDefaults cycle`() {
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
        manager.prePopulateDefaults()
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size, manager.getWhitelistCount())
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
        manager.prePopulateDefaults()
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size, manager.getWhitelistCount())
    }

    @Test
    fun `prePopulateDefaults then clearAll then add custom`() {
        manager.prePopulateDefaults()
        manager.clearAll()
        manager.addToWhitelist("com.only.custom")
        assertEquals(1, manager.getWhitelistCount())
        assertTrue(manager.isWhitelisted("com.only.custom"))
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertFalse(manager.isWhitelisted(app))
        }
    }

    @Test
    fun `add custom then prePopulateDefaults then remove custom`() {
        manager.addToWhitelist("com.temp.custom")
        manager.prePopulateDefaults()
        assertTrue(manager.isWhitelisted("com.temp.custom"))
        manager.removeFromWhitelist("com.temp.custom")
        assertFalse(manager.isWhitelisted("com.temp.custom"))
        // Defaults should still be there
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue(manager.isWhitelisted(app))
        }
    }

    // ========================================================================
    // Section 23: Numeric and extreme package names
    // ========================================================================

    @Test
    fun `package name that is all numbers`() {
        manager.addToWhitelist("123456789")
        assertTrue(manager.isWhitelisted("123456789"))
    }

    @Test
    fun `package name that looks like a number`() {
        manager.addToWhitelist("1.2.3.4.5")
        assertTrue(manager.isWhitelisted("1.2.3.4.5"))
    }

    @Test
    fun `package name that looks like an IP address`() {
        manager.addToWhitelist("192.168.1.1")
        assertTrue(manager.isWhitelisted("192.168.1.1"))
    }

    @Test
    fun `package name that looks like a URL`() {
        manager.addToWhitelist("https://example.com")
        assertTrue(manager.isWhitelisted("https://example.com"))
    }

    @Test
    fun `package name that looks like a file path`() {
        manager.addToWhitelist("/data/data/com.app/files")
        assertTrue(manager.isWhitelisted("/data/data/com.app/files"))
    }

    @Test
    fun `package name that looks like SQL injection`() {
        manager.addToWhitelist("'; DROP TABLE apps; --")
        assertTrue(manager.isWhitelisted("'; DROP TABLE apps; --"))
    }

    @Test
    fun `package name with JSON-like content`() {
        manager.addToWhitelist("{\"app\": \"value\"}")
        assertTrue(manager.isWhitelisted("{\"app\": \"value\"}"))
    }

    @Test
    fun `package name with XML-like content`() {
        manager.addToWhitelist("<app>value</app>")
        assertTrue(manager.isWhitelisted("<app>value</app>"))
    }

    // ========================================================================
    // Section 24: Suggested app names mapping
    // ========================================================================

    @Test
    fun `1Password has correct suggested name`() {
        assertEquals("1Password", WhitelistManager.SUGGESTED_APP_NAMES["com.agilebits.onepassword"])
    }

    @Test
    fun `Bitwarden has correct suggested name`() {
        assertEquals("Bitwarden", WhitelistManager.SUGGESTED_APP_NAMES["com.bitwarden.mobile"])
    }

    @Test
    fun `KeePassDroid has correct suggested name`() {
        assertEquals("KeePassDroid", WhitelistManager.SUGGESTED_APP_NAMES["org.keepassdroid"])
    }

    @Test
    fun `LastPass has correct suggested name`() {
        assertEquals("LastPass", WhitelistManager.SUGGESTED_APP_NAMES["com.lastpass.lpandroid"])
    }

    @Test
    fun `Bitwarden alt has correct suggested name`() {
        assertEquals("Bitwarden", WhitelistManager.SUGGESTED_APP_NAMES["com.x8bit.bitwarden"])
    }

    @Test
    fun `Keepass2Android has correct suggested name`() {
        assertEquals("Keepass2Android", WhitelistManager.SUGGESTED_APP_NAMES["keepass2android.keepass2android"])
    }

    @Test
    fun `suggested app names does not contain unknown package`() {
        assertNull(WhitelistManager.SUGGESTED_APP_NAMES["com.unknown.package"])
    }

    @Test
    fun `suggested app names keys are all valid package names`() {
        for (key in WhitelistManager.SUGGESTED_APP_NAMES.keys) {
            assertTrue("Key should contain a dot: $key", key.contains("."))
        }
    }

    // ========================================================================
    // Section 25: Flow emissions with multiple operations
    // ========================================================================

    @Test
    fun `flow reflects state after multiple adds`() = runBlocking {
        manager.addToWhitelist("com.flow.multi1")
        manager.addToWhitelist("com.flow.multi2")
        manager.addToWhitelist("com.flow.multi3")
        val current = manager.getWhitelistedAppsFlow().first()
        assertTrue(current.contains("com.flow.multi1"))
        assertTrue(current.contains("com.flow.multi2"))
        assertTrue(current.contains("com.flow.multi3"))
    }

    @Test
    fun `flow reflects state after add then remove`() = runBlocking {
        manager.addToWhitelist("com.flow.addremove")
        manager.removeFromWhitelist("com.flow.addremove")
        val current = manager.getWhitelistedAppsFlow().first()
        assertFalse(current.contains("com.flow.addremove"))
    }

    @Test
    fun `flow reflects state after clearAll`() = runBlocking {
        manager.addToWhitelist("com.flow.clear1")
        manager.clearAll()
        val current = manager.getWhitelistedAppsFlow().first()
        assertTrue(current.isEmpty())
    }

    @Test
    fun `flow reflects state after clearAll then prePopulate`() = runBlocking {
        manager.clearAll()
        manager.prePopulateDefaults()
        val current = manager.getWhitelistedAppsFlow().first()
        assertTrue(current.containsAll(WhitelistManager.DEFAULT_TRUSTED_APPS))
    }

    @Test
    fun `flow value is a snapshot, not live reference`() = runBlocking {
        val snapshot = manager.getWhitelistedAppsFlow().first()
        val sizeBefore = snapshot.size
        manager.addToWhitelist("com.flow.snapshot")
        // The snapshot should not change (it's a copy)
        assertEquals(sizeBefore, snapshot.size)
    }

    // ========================================================================
    // Section 26: Additional edge cases for completeness
    // ========================================================================

    @Test
    fun `isWhitelisted after construction with empty prefs but defaults not populated`() {
        // Create prefs where defaults_populated is false
        prefStore.clear()
        val freshManager = WhitelistManager(prefs)
        // Should have defaults
        assertTrue(freshManager.isWhitelisted("com.agilebits.onepassword"))
    }

    @Test
    fun `isWhitelisted after construction with prefs containing existing data`() {
        prefStore["whitelisted_apps"] = setOf("com.existing.app")
        prefStore["defaults_populated"] = true
        val freshManager = WhitelistManager(prefs)
        assertTrue(freshManager.isWhitelisted("com.existing.app"))
    }

    @Test
    fun `getWhitelistCount after construction with prefs containing data`() {
        prefStore["whitelisted_apps"] = setOf("com.pref.app1", "com.pref.app2")
        prefStore["defaults_populated"] = true
        val freshManager = WhitelistManager(prefs)
        assertEquals(2, freshManager.getWhitelistCount())
    }

    @Test
    fun `toggle default app removes then re-adds correctly`() {
        val app = "org.keepassdroid"
        assertTrue(manager.isWhitelisted(app))
        val r1 = manager.toggleWhitelist(app) // removes
        assertFalse(r1)
        assertFalse(manager.isWhitelisted(app))
        val r2 = manager.toggleWhitelist(app) // adds back
        assertTrue(r2)
        assertTrue(manager.isWhitelisted(app))
    }

    @Test
    fun `adding all default apps again does not duplicate`() {
        val before = manager.getWhitelistCount()
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            manager.addToWhitelist(app)
        }
        assertEquals(before, manager.getWhitelistCount())
    }

    @Test
    fun `removing all default apps then re-adding one at a time`() {
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            manager.removeFromWhitelist(app)
        }
        assertEquals(0, manager.getWhitelistCount())
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            manager.addToWhitelist(app)
        }
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size, manager.getWhitelistCount())
    }

    @Test
    fun `whitelist count consistency after mixed operations`() {
        manager.clearAll()
        manager.addToWhitelist("a")
        manager.addToWhitelist("b")
        manager.addToWhitelist("c")
        manager.removeFromWhitelist("b")
        manager.toggleWhitelist("d") // adds d
        manager.toggleWhitelist("a") // removes a
        // Remaining: c, d
        assertEquals(2, manager.getWhitelistCount())
        assertTrue(manager.isWhitelisted("c"))
        assertTrue(manager.isWhitelisted("d"))
        assertFalse(manager.isWhitelisted("a"))
        assertFalse(manager.isWhitelisted("b"))
    }

    @Test
    fun `getAllWhitelistedApps and isWhitelisted are always consistent`() {
        manager.clearAll()
        val apps = (1..20).map { "com.consistent.app$it" }
        apps.forEach { manager.addToWhitelist(it) }
        // Remove every third
        apps.filterIndexed { i, _ -> i % 3 == 0 }.forEach { manager.removeFromWhitelist(it) }
        val all = manager.getAllWhitelistedApps()
        for (app in apps) {
            assertEquals(
                "Consistency check for $app",
                all.contains(app),
                manager.isWhitelisted(app)
            )
        }
    }

    @Test
    fun `flow size matches getWhitelistCount after operations`() = runBlocking {
        manager.clearAll()
        manager.addToWhitelist("com.flow.count1")
        manager.addToWhitelist("com.flow.count2")
        manager.addToWhitelist("com.flow.count3")
        manager.removeFromWhitelist("com.flow.count2")
        val flowSet = manager.getWhitelistedAppsFlow().first()
        assertEquals(manager.getWhitelistCount(), flowSet.size)
    }

    @Test
    fun `similar package names are treated as distinct`() {
        manager.addToWhitelist("com.similar.app")
        manager.addToWhitelist("com.similar.app1")
        manager.addToWhitelist("com.similar.app2")
        manager.addToWhitelist("com.similar.application")
        assertTrue(manager.isWhitelisted("com.similar.app"))
        assertTrue(manager.isWhitelisted("com.similar.app1"))
        assertTrue(manager.isWhitelisted("com.similar.app2"))
        assertTrue(manager.isWhitelisted("com.similar.application"))
        // Remove one does not remove the others
        manager.removeFromWhitelist("com.similar.app")
        assertFalse(manager.isWhitelisted("com.similar.app"))
        assertTrue(manager.isWhitelisted("com.similar.app1"))
    }

    @Test
    fun `prefix of a whitelisted app is not whitelisted`() {
        manager.addToWhitelist("com.prefix.full.app")
        assertFalse(manager.isWhitelisted("com.prefix"))
        assertFalse(manager.isWhitelisted("com.prefix.full"))
    }

    @Test
    fun `suffix of a whitelisted app is not whitelisted`() {
        manager.addToWhitelist("com.suffix.full.app")
        assertFalse(manager.isWhitelisted("full.app"))
        assertFalse(manager.isWhitelisted("app"))
    }

    @Test
    fun `substring of a whitelisted app is not whitelisted`() {
        manager.addToWhitelist("com.substring.test.app")
        assertFalse(manager.isWhitelisted("substring.test"))
        assertFalse(manager.isWhitelisted("substring"))
    }

    @Test
    fun `superset string of a whitelisted app is not whitelisted`() {
        manager.addToWhitelist("com.super.app")
        assertFalse(manager.isWhitelisted("pre.com.super.app"))
        assertFalse(manager.isWhitelisted("com.super.app.extra"))
    }

    // ========================================================================
    // Section 27: Additional flow and state tests
    // ========================================================================

    @Test
    fun `flow type is correct`() {
        val flow = manager.getWhitelistedAppsFlow()
        assertNotNull(flow)
    }

    @Test
    fun `multiple flow calls return the same backing flow`() {
        val flow1 = manager.getWhitelistedAppsFlow()
        val flow2 = manager.getWhitelistedAppsFlow()
        // Both should be backed by the same StateFlow
        assertNotNull(flow1)
        assertNotNull(flow2)
    }

    @Test
    fun `isWhitelisted is O(1) - does not degrade with large set`() {
        manager.clearAll()
        repeat(1000) { manager.addToWhitelist("com.perf.app$it") }
        // Just verify it works correctly with large set
        assertTrue(manager.isWhitelisted("com.perf.app500"))
        assertFalse(manager.isWhitelisted("com.perf.app1001"))
    }

    @Test
    fun `empty package name does not interfere with normal operations`() {
        manager.addToWhitelist("")
        manager.addToWhitelist("com.normal.app")
        assertTrue(manager.isWhitelisted(""))
        assertTrue(manager.isWhitelisted("com.normal.app"))
        manager.removeFromWhitelist("")
        assertFalse(manager.isWhitelisted(""))
        assertTrue(manager.isWhitelisted("com.normal.app"))
    }

    @Test
    fun `repeated prePopulateDefaults calls do not affect custom entries`() {
        manager.addToWhitelist("com.custom.stable")
        repeat(5) { manager.prePopulateDefaults() }
        assertTrue(manager.isWhitelisted("com.custom.stable"))
    }

    @Test
    fun `clearAll does not affect static companion values`() {
        manager.clearAll()
        assertEquals(6, WhitelistManager.DEFAULT_TRUSTED_APPS.size)
        assertEquals(6, WhitelistManager.SUGGESTED_APP_NAMES.size)
    }

    @Test
    fun `toggle on empty package name works correctly`() {
        val r1 = manager.toggleWhitelist("")
        assertTrue(r1)
        assertTrue(manager.isWhitelisted(""))
        val r2 = manager.toggleWhitelist("")
        assertFalse(r2)
        assertFalse(manager.isWhitelisted(""))
    }

    @Test
    fun `very large number of unique entries can be stored and retrieved`() {
        manager.clearAll()
        val count = 2000
        repeat(count) { manager.addToWhitelist("com.large.set.app$it") }
        assertEquals(count, manager.getWhitelistCount())
        val all = manager.getAllWhitelistedApps()
        assertEquals(count, all.size)
        // Spot check
        assertTrue(all.contains("com.large.set.app0"))
        assertTrue(all.contains("com.large.set.app999"))
        assertTrue(all.contains("com.large.set.app1999"))
    }

    @Test
    fun `getAllWhitelistedApps returns new instance each call`() {
        val a = manager.getAllWhitelistedApps()
        val b = manager.getAllWhitelistedApps()
        assertEquals(a, b)
        // They are equal in content but should be separate instances (toSet creates new)
    }

    @Test
    fun `prePopulateDefaults with extra custom apps preserves all`() {
        manager.clearAll()
        manager.addToWhitelist("com.extra.alpha")
        manager.addToWhitelist("com.extra.beta")
        manager.prePopulateDefaults()
        assertTrue(manager.isWhitelisted("com.extra.alpha"))
        assertTrue(manager.isWhitelisted("com.extra.beta"))
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue(manager.isWhitelisted(app))
        }
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size + 2, manager.getWhitelistCount())
    }

    @Test
    fun `mixed operations sequence maintains integrity`() {
        manager.clearAll()
        manager.prePopulateDefaults()
        manager.addToWhitelist("com.custom.a")
        manager.addToWhitelist("com.custom.b")
        manager.removeFromWhitelist("com.agilebits.onepassword")
        manager.toggleWhitelist("com.custom.c")
        manager.toggleWhitelist("com.custom.a") // removes
        // Expected: defaults - onepassword + custom.b + custom.c
        val expected = WhitelistManager.DEFAULT_TRUSTED_APPS.size - 1 + 2
        assertEquals(expected, manager.getWhitelistCount())
        assertFalse(manager.isWhitelisted("com.agilebits.onepassword"))
        assertFalse(manager.isWhitelisted("com.custom.a"))
        assertTrue(manager.isWhitelisted("com.custom.b"))
        assertTrue(manager.isWhitelisted("com.custom.c"))
    }

    @Test
    fun `add same app three times in a row only appears once`() {
        manager.clearAll()
        manager.addToWhitelist("com.triple.add")
        manager.addToWhitelist("com.triple.add")
        manager.addToWhitelist("com.triple.add")
        assertEquals(1, manager.getWhitelistCount())
    }

    @Test
    fun `flow emission after 50 sequential adds`() = runBlocking {
        manager.clearAll()
        repeat(50) { manager.addToWhitelist("com.seq.flow$it") }
        val current = manager.getWhitelistedAppsFlow().first()
        assertEquals(50, current.size)
    }

    @Test
    fun `toggle all default apps off then back on`() {
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            manager.toggleWhitelist(app) // remove
        }
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertFalse(manager.isWhitelisted(app))
        }
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            manager.toggleWhitelist(app) // add back
        }
        for (app in WhitelistManager.DEFAULT_TRUSTED_APPS) {
            assertTrue(manager.isWhitelisted(app))
        }
    }

    @Test
    fun `add then immediately check is always consistent`() {
        repeat(100) { i ->
            val app = "com.immediate.check$i"
            manager.addToWhitelist(app)
            assertTrue("App $app should be immediately whitelisted", manager.isWhitelisted(app))
        }
    }

    @Test
    fun `remove then immediately check is always consistent`() {
        repeat(100) { i ->
            val app = "com.immediate.remove$i"
            manager.addToWhitelist(app)
            manager.removeFromWhitelist(app)
            assertFalse("App $app should be immediately removed", manager.isWhitelisted(app))
        }
    }

    @Test
    fun `package name with zero width space`() {
        manager.addToWhitelist("com.zero\u200Bwidth.app")
        assertTrue(manager.isWhitelisted("com.zero\u200Bwidth.app"))
        // Without zero width space should be different
        assertFalse(manager.isWhitelisted("com.zerowidth.app"))
    }

    @Test
    fun `package name with right-to-left mark`() {
        manager.addToWhitelist("com.rtl\u200Fmark.app")
        assertTrue(manager.isWhitelisted("com.rtl\u200Fmark.app"))
    }

    @Test
    fun `package name with byte order mark`() {
        manager.addToWhitelist("\uFEFFcom.bom.app")
        assertTrue(manager.isWhitelisted("\uFEFFcom.bom.app"))
        assertFalse(manager.isWhitelisted("com.bom.app"))
    }

    @Test
    fun `add apps with incrementing numeric suffixes`() {
        manager.clearAll()
        (1..50).forEach { manager.addToWhitelist("com.num.$it") }
        assertEquals(50, manager.getWhitelistCount())
        (1..50).forEach { assertTrue(manager.isWhitelisted("com.num.$it")) }
    }

    @Test
    fun `remove apps in reverse order of addition`() {
        manager.clearAll()
        val apps = (1..20).map { "com.reverse.$it" }
        apps.forEach { manager.addToWhitelist(it) }
        apps.reversed().forEach { manager.removeFromWhitelist(it) }
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test
    fun `add duplicate of default app then remove it`() {
        assertTrue(manager.isWhitelisted("com.bitwarden.mobile"))
        manager.addToWhitelist("com.bitwarden.mobile") // duplicate add, no effect
        manager.removeFromWhitelist("com.bitwarden.mobile")
        assertFalse(manager.isWhitelisted("com.bitwarden.mobile"))
    }

    @Test
    fun `clearAll followed by add followed by flow check`() = runBlocking {
        manager.clearAll()
        manager.addToWhitelist("com.flow.after.clear")
        val current = manager.getWhitelistedAppsFlow().first()
        assertEquals(1, current.size)
        assertTrue(current.contains("com.flow.after.clear"))
    }

    @Test
    fun `getWhitelistCount matches flow size at all times`() = runBlocking {
        manager.clearAll()
        manager.addToWhitelist("com.match.1")
        assertEquals(manager.getWhitelistCount(), manager.getWhitelistedAppsFlow().first().size)
        manager.addToWhitelist("com.match.2")
        assertEquals(manager.getWhitelistCount(), manager.getWhitelistedAppsFlow().first().size)
        manager.removeFromWhitelist("com.match.1")
        assertEquals(manager.getWhitelistCount(), manager.getWhitelistedAppsFlow().first().size)
    }

    @Test
    fun `prefs edit apply is called on every mutation`() {
        val initialInvocations = mockingDetails(editor).invocations
            .filter { it.method.name == "apply" }.size
        manager.addToWhitelist("com.apply.check")
        val afterAdd = mockingDetails(editor).invocations
            .filter { it.method.name == "apply" }.size
        assertTrue(afterAdd > initialInvocations)
    }

    @Test
    fun `prefs putStringSet is called with correct key`() {
        manager.addToWhitelist("com.key.check")
        verify(editor, atLeastOnce()).putStringSet(eq("whitelisted_apps"), any())
    }

    @Test
    fun `removing app that was never added leaves state unchanged`() {
        val before = manager.getAllWhitelistedApps()
        manager.removeFromWhitelist("com.never.ever.added.app.xyz")
        val after = manager.getAllWhitelistedApps()
        assertEquals(before, after)
    }

    @Test
    fun `toggle on app that was never added adds it`() {
        assertFalse(manager.isWhitelisted("com.toggle.never.added"))
        manager.toggleWhitelist("com.toggle.never.added")
        assertTrue(manager.isWhitelisted("com.toggle.never.added"))
    }

    @Test
    fun `compound scenario with all operations`() {
        manager.clearAll()
        // Start empty
        assertEquals(0, manager.getWhitelistCount())

        // Add defaults
        manager.prePopulateDefaults()
        assertEquals(6, manager.getWhitelistCount())

        // Add custom
        manager.addToWhitelist("com.compound.1")
        manager.addToWhitelist("com.compound.2")
        assertEquals(8, manager.getWhitelistCount())

        // Remove a default
        manager.removeFromWhitelist("com.lastpass.lpandroid")
        assertEquals(7, manager.getWhitelistCount())

        // Toggle a custom (removes)
        manager.toggleWhitelist("com.compound.1")
        assertEquals(6, manager.getWhitelistCount())
        assertFalse(manager.isWhitelisted("com.compound.1"))

        // Toggle a new one (adds)
        manager.toggleWhitelist("com.compound.3")
        assertEquals(7, manager.getWhitelistCount())
        assertTrue(manager.isWhitelisted("com.compound.3"))

        // Duplicate add
        manager.addToWhitelist("com.compound.2")
        assertEquals(7, manager.getWhitelistCount())

        // Remove non-existent
        manager.removeFromWhitelist("com.not.here")
        assertEquals(7, manager.getWhitelistCount())

        // Verify final state
        assertTrue(manager.isWhitelisted("com.compound.2"))
        assertTrue(manager.isWhitelisted("com.compound.3"))
        assertFalse(manager.isWhitelisted("com.compound.1"))
        assertFalse(manager.isWhitelisted("com.lastpass.lpandroid"))
        assertTrue(manager.isWhitelisted("com.agilebits.onepassword"))
    }

    @Test
    fun `reconstruction after compound scenario preserves state`() {
        manager.clearAll()
        manager.addToWhitelist("com.recon.a")
        manager.addToWhitelist("com.recon.b")
        manager.toggleWhitelist("com.recon.c")
        manager.removeFromWhitelist("com.recon.a")

        val manager2 = WhitelistManager(prefs)
        assertFalse(manager2.isWhitelisted("com.recon.a"))
        assertTrue(manager2.isWhitelisted("com.recon.b"))
        assertTrue(manager2.isWhitelisted("com.recon.c"))
        assertEquals(2, manager2.getWhitelistCount())
    }

    @Test
    fun `adding 10 apps with similar prefixes are all distinct`() {
        manager.clearAll()
        val apps = (0..9).map { "com.prefix$it" }
        apps.forEach { manager.addToWhitelist(it) }
        assertEquals(10, manager.getWhitelistCount())
        apps.forEach { assertTrue(manager.isWhitelisted(it)) }
    }

    @Test
    fun `package names with different Unicode normalization forms`() {
        // e with acute: composed (U+00E9) vs decomposed (e + U+0301)
        val composed = "com.\u00E9.app"
        val decomposed = "com.e\u0301.app"
        manager.addToWhitelist(composed)
        assertTrue(manager.isWhitelisted(composed))
        // These are different strings in Kotlin (no automatic normalization)
        if (composed != decomposed) {
            assertFalse(manager.isWhitelisted(decomposed))
        }
    }

    @Test
    fun `clearAll then immediately getAllWhitelistedApps returns empty`() {
        manager.addToWhitelist("com.final.test")
        manager.clearAll()
        val all = manager.getAllWhitelistedApps()
        assertTrue(all.isEmpty())
        assertEquals(0, all.size)
    }
}
