package com.example.mad_final.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(private val context: Context) {

    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_IMAGE_URI = stringPreferencesKey("user_image_uri")
        val ADMIN_IMAGE_URI = stringPreferencesKey("admin_image_uri")
        val GUEST_ID = stringPreferencesKey("guest_id")
    }

    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[AUTH_TOKEN]
        }

    val userId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    val userRole: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ROLE]
        }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL]
        }

    val userName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME]
        }

    val userImageUri: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_IMAGE_URI]
        }

    val adminImageUri: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ADMIN_IMAGE_URI]
        }

    val guestId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[GUEST_ID]
        }

    suspend fun saveGuestId(id: String) {
        context.dataStore.edit { preferences ->
            if (preferences[GUEST_ID] == null) {
                preferences[GUEST_ID] = id
            }
        }
    }

    suspend fun saveUserData(token: String, userId: String, role: String, email: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
            preferences[USER_ID] = userId
            preferences[USER_ROLE] = role
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
        }
    }

    suspend fun updateProfile(name: String, email: String, imageUri: String?, isAdmin: Boolean = false) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            if (imageUri != null) {
                if (isAdmin) {
                    preferences[ADMIN_IMAGE_URI] = imageUri
                } else {
                    preferences[USER_IMAGE_URI] = imageUri
                }
            }
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)
            preferences.remove(USER_ID)
            preferences.remove(USER_ROLE)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_NAME)
            // We keep USER_IMAGE_URI and ADMIN_IMAGE_URI so they persist across sessions on this device
        }
    }
}
