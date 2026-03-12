package com.privacyguard.data

import android.content.SharedPreferences
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WhitelistManagerComprehensiveTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var manager: WhitelistManager
    private val storage = mutableMapOf<String, Any?>()

    @Before
    fun setUp() {
        editor = mockk(relaxed = true)
        prefs = mockk(relaxed = true)

        every { prefs.edit() } returns editor
        every { editor.putStringSet(any(), any()) } answers {
            storage[firstArg()] = secondArg<Set<String>>()
            editor
        }
        every { editor.putBoolean(any(), any()) } answers {
            storage[firstArg()] = secondArg<Boolean>()
            editor
        }
        every { editor.apply() } just Runs
        every { prefs.getStringSet(any(), any()) } answers {
            @Suppress("UNCHECKED_CAST")
            storage[firstArg()] as? Set<String> ?: secondArg()
        }
        every { prefs.getBoolean(any(), any()) } answers {
            storage[firstArg()] as? Boolean ?: secondArg()
        }

        storage.clear()
        manager = WhitelistManager(prefs)
    }

    // === DEFAULT POPULATION ===

    @Test fun `1Password whitelisted by default`() = assertTrue(manager.isWhitelisted("com.agilebits.onepassword"))
    @Test fun `Bitwarden whitelisted by default`() = assertTrue(manager.isWhitelisted("com.bitwarden.mobile"))
    @Test fun `KeePassDroid whitelisted by default`() = assertTrue(manager.isWhitelisted("org.keepassdroid"))
    @Test fun `LastPass whitelisted by default`() = assertTrue(manager.isWhitelisted("com.lastpass.lpandroid"))
    @Test fun `Bitwarden alt whitelisted by default`() = assertTrue(manager.isWhitelisted("com.x8bit.bitwarden"))
    @Test fun `Keepass2Android whitelisted by default`() = assertTrue(manager.isWhitelisted("keepass2android.keepass2android"))

    @Test
    fun `default count matches DEFAULT_TRUSTED_APPS size`() {
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size, manager.getWhitelistCount())
    }

    // === ADD / REMOVE / TOGGLE ===

    @Test fun `add new app makes it whitelisted`() {
        manager.addToWhitelist("com.new.app")
        assertTrue(manager.isWhitelisted("com.new.app"))
    }

    @Test fun `add duplicate does not crash`() {
        manager.addToWhitelist("com.dup.app")
        manager.addToWhitelist("com.dup.app")
        assertTrue(manager.isWhitelisted("com.dup.app"))
    }

    @Test fun `remove existing app`() {
        manager.addToWhitelist("com.rem.app")
        manager.removeFromWhitelist("com.rem.app")
        assertFalse(manager.isWhitelisted("com.rem.app"))
    }

    @Test fun `remove non-existing app does not crash`() {
        manager.removeFromWhitelist("com.never.added")
        assertFalse(manager.isWhitelisted("com.never.added"))
    }

    @Test fun `toggle adds when absent`() {
        val result = manager.toggleWhitelist("com.toggle.app")
        assertTrue(result)
        assertTrue(manager.isWhitelisted("com.toggle.app"))
    }

    @Test fun `toggle removes when present`() {
        manager.addToWhitelist("com.toggle.app")
        val result = manager.toggleWhitelist("com.toggle.app")
        assertFalse(result)
        assertFalse(manager.isWhitelisted("com.toggle.app"))
    }

    @Test fun `toggle toggle returns to original state`() {
        manager.addToWhitelist("com.flip.app")
        manager.toggleWhitelist("com.flip.app")
        manager.toggleWhitelist("com.flip.app")
        assertTrue(manager.isWhitelisted("com.flip.app"))
    }

    // === QUERY METHODS ===

    @Test fun `isWhitelisted returns false for random package`() = assertFalse(manager.isWhitelisted("com.random.xyz"))
    @Test fun `isWhitelisted returns false for empty string`() = assertFalse(manager.isWhitelisted(""))

    @Test fun `getAllWhitelistedApps returns set`() {
        val apps = manager.getAllWhitelistedApps()
        assertNotNull(apps)
        assertTrue(apps is Set<String>)
    }

    @Test fun `getAllWhitelistedApps includes defaults`() {
        val apps = manager.getAllWhitelistedApps()
        assertTrue(apps.containsAll(WhitelistManager.DEFAULT_TRUSTED_APPS))
    }

    @Test fun `getAllWhitelistedApps includes added apps`() {
        manager.addToWhitelist("com.custom.app")
        assertTrue(manager.getAllWhitelistedApps().contains("com.custom.app"))
    }

    @Test fun `getWhitelistCount increments on add`() {
        val before = manager.getWhitelistCount()
        manager.addToWhitelist("com.count.test")
        assertEquals(before + 1, manager.getWhitelistCount())
    }

    @Test fun `getWhitelistCount decrements on remove`() {
        manager.addToWhitelist("com.count.test2")
        val after = manager.getWhitelistCount()
        manager.removeFromWhitelist("com.count.test2")
        assertEquals(after - 1, manager.getWhitelistCount())
    }

    // === CLEAR ALL ===

    @Test fun `clearAll removes everything`() {
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
    }

    @Test fun `clearAll removes defaults`() {
        manager.clearAll()
        assertFalse(manager.isWhitelisted("com.agilebits.onepassword"))
    }

    @Test fun `clearAll removes custom apps`() {
        manager.addToWhitelist("com.custom.clear")
        manager.clearAll()
        assertFalse(manager.isWhitelisted("com.custom.clear"))
    }

    @Test fun `can add after clearAll`() {
        manager.clearAll()
        manager.addToWhitelist("com.after.clear")
        assertTrue(manager.isWhitelisted("com.after.clear"))
    }

    // === O(1) PERFORMANCE ===

    @Test fun `O1 lookup with 10000 entries`() {
        for (i in 1..10000) manager.addToWhitelist("com.perf.app$i")
        val start = System.nanoTime()
        repeat(100000) { manager.isWhitelisted("com.perf.app${it % 10000 + 1}") }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("100K lookups should be under 1 second", elapsedMs < 1000)
    }

    @Test fun `bulk add 1000 apps`() {
        for (i in 1..1000) manager.addToWhitelist("com.bulk.app$i")
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size + 1000, manager.getWhitelistCount())
    }

    // === FLOW ===

    @Test fun `flow emits current state`() = runTest {
        val apps = manager.getWhitelistedAppsFlow().first()
        assertNotNull(apps)
        assertTrue(apps.containsAll(WhitelistManager.DEFAULT_TRUSTED_APPS))
    }

    // === PERSISTENCE ===

    @Test fun `add triggers preferences save`() {
        manager.addToWhitelist("com.save.test")
        verify(atLeast = 1) { editor.putStringSet(any(), any()) }
        verify(atLeast = 1) { editor.apply() }
    }

    @Test fun `remove triggers preferences save`() {
        manager.addToWhitelist("com.save.rem")
        clearMocks(editor, answers = false)
        every { editor.putStringSet(any(), any()) } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.apply() } just Runs
        manager.removeFromWhitelist("com.save.rem")
        verify(atLeast = 1) { editor.apply() }
    }

    // === SUGGESTED APP NAMES ===

    @Test fun `suggested app names map is not empty`() {
        assertTrue(WhitelistManager.SUGGESTED_APP_NAMES.isNotEmpty())
    }

    @Test fun `all default apps have suggested names`() {
        WhitelistManager.DEFAULT_TRUSTED_APPS.forEach { pkg ->
            assertTrue("$pkg should have a name", WhitelistManager.SUGGESTED_APP_NAMES.containsKey(pkg))
        }
    }

    @Test fun `suggested names are not blank`() {
        WhitelistManager.SUGGESTED_APP_NAMES.values.forEach { name ->
            assertTrue(name.isNotBlank())
        }
    }

    // === EDGE CASES ===

    @Test fun `whitelist with special chars in package name`() {
        manager.addToWhitelist("com.app-with-dashes")
        assertTrue(manager.isWhitelisted("com.app-with-dashes"))
    }

    @Test fun `whitelist with very long package name`() {
        val longPkg = "com." + "a".repeat(500)
        manager.addToWhitelist(longPkg)
        assertTrue(manager.isWhitelisted(longPkg))
    }

    @Test fun `whitelist preserves case sensitivity`() {
        manager.addToWhitelist("com.Case.Sensitive")
        assertTrue(manager.isWhitelisted("com.Case.Sensitive"))
        assertFalse(manager.isWhitelisted("com.case.sensitive"))
    }

    @Test fun `prePopulateDefaults is idempotent`() {
        manager.prePopulateDefaults()
        manager.prePopulateDefaults()
        assertEquals(WhitelistManager.DEFAULT_TRUSTED_APPS.size,
            manager.getAllWhitelistedApps().intersect(WhitelistManager.DEFAULT_TRUSTED_APPS).size)
    }
}
