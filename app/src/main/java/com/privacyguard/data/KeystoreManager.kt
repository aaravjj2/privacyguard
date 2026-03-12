package com.privacyguard.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.content.SharedPreferences

/**
 * Manages Android Keystore master keys for encrypted storage.
 */
class KeystoreManager(private val context: Context) {

    companion object {
        private const val MASTER_KEY_ALIAS = "privacyguard_master_key"
    }

    private var masterKey: MasterKey? = null

    fun getMasterKey(): MasterKey {
        return masterKey ?: createMasterKey().also { masterKey = it }
    }

    private fun createMasterKey(): MasterKey {
        val spec = KeyGenParameterSpec.Builder(
            MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        return MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
    }

    fun createEncryptedPreferences(fileName: String): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            fileName,
            getMasterKey(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isKeyValid(): Boolean {
        return try {
            getMasterKey()
            true
        } catch (e: Exception) {
            false
        }
    }
}
