package com.githubusers.android.datasource.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class AppPrefs(val context: Context) {

    companion object {
        private const val PREFS_SECRET_FILENAME = "app_secret_preferences"
        private const val PREFS_FILENAME = "app_preferences"
        private const val KEY_IS_FIRST_APP_USE = "first_use"
        private const val KEY_TOKEN = "token_jwt"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    private fun secretPreferences(): SharedPreferences {
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREFS_SECRET_FILENAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var isFirstUse: Boolean
        get() = sharedPrefs.getBoolean(KEY_IS_FIRST_APP_USE, true)
        set(value) = with (sharedPrefs.edit()) {
            putBoolean(KEY_IS_FIRST_APP_USE, value)
            apply()
        }

    var token: String?
        get() = secretPreferences().getString(KEY_TOKEN, null)
        set(value) {
            secretPreferences().edit().apply {
                putString(KEY_TOKEN, value)
                apply()
            }
        }
}
