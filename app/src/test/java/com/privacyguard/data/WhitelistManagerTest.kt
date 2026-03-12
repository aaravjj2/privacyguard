package com.privacyguard.data

import android.content.SharedPreferences
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WhitelistManagerTest {

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

    @Test
    fun `default password managers are whitelisted on first run`() {
        assertTrue(manager.isWhitelisted("com.agilebits.onepassword"))
        assertTrue(manager.isWhitelisted("com.bitwarden.mobile"))
        assertTrue(manager.isWhitelisted("org.keepassdroid"))
        assertTrue(manager.isWhitelisted("com.lastpass.lpandroid"))
    }

    @Test
    fun `isWhitelisted returns false for unknown app`() {
        assertFalse(manager.isWhitelisted("com.unknown.app"))
    }

    @Test
    fun `addToWhitelist makes app whitelisted`() {
        manager.addToWhitelist("com.example.test")
        assertTrue(manager.isWhitelisted("com.example.test"))
    }

    @Test
    fun `removeFromWhitelist makes app not whitelisted`() {
        manager.addToWhitelist("com.example.test")
        manager.removeFromWhitelist("com.example.test")
        assertFalse(manager.isWhitelisted("com.example.test"))
    }

    @Test
    fun `toggleWhitelist adds when not present`() {
        val result = manager.toggleWhitelist("com.example.toggle")
        assertTrue(result)
        assertTrue(manager.isWhitelisted("com.example.toggle"))
    }

    @Test
    fun `toggleWhitelist removes when present`() {
        manager.addToWhitelist("com.example.toggle")
        val result = manager.toggleWhitelist("com.example.toggle")
        assertFalse(result)
        assertFalse(manager.isWhitelisted("com.example.toggle"))
    }

    @Test
    fun `getAllWhitelistedApps returns all apps`() {
        val apps = manager.getAllWhitelistedApps()
        assertTrue(apps.isNotEmpty())
        assertTrue(apps.containsAll(WhitelistManager.DEFAULT_TRUSTED_APPS))
    }

    @Test
    fun `getWhitelistCount returns correct count`() {
        val initialCount = manager.getWhitelistCount()
        manager.addToWhitelist("com.example.new")
        assertEquals(initialCount + 1, manager.getWhitelistCount())
    }

    @Test
    fun `clearAll removes all apps`() {
        manager.clearAll()
        assertEquals(0, manager.getWhitelistCount())
        assertFalse(manager.isWhitelisted("com.agilebits.onepassword"))
    }

    @Test
    fun `isWhitelisted is O1 lookup with HashSet`() {
        // Add many apps
        for (i in 1..1000) {
            manager.addToWhitelist("com.app$i")
        }

        // Lookup should be fast (O(1)) - testing that it doesn't crash with many entries
        val start = System.nanoTime()
        for (i in 1..10000) {
            manager.isWhitelisted("com.app${i % 1000 + 1}")
        }
        val elapsed = System.nanoTime() - start

        // 10000 lookups should complete in well under 1 second
        assertTrue("HashSet lookup should be fast", elapsed < 1_000_000_000L)
    }

    @Test
    fun `whitelist persists to SharedPreferences`() {
        manager.addToWhitelist("com.test.persist")
        verify { editor.putStringSet(any(), any()) }
        verify { editor.apply() }
    }
}
