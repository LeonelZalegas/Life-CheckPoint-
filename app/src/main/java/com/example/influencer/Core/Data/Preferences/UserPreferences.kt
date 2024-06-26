package com.example.influencer.Core.Data.Preferences


import androidx.datastore.preferences.core.booleanPreferencesKey
import javax.inject.Singleton
import javax.inject.Inject
import androidx.datastore.rxjava3.RxDataStore
import io.reactivex.rxjava3.core.Single
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


//https://www.notion.so/DataStore-5ff1d598536947498542c292ae3f2702
@Singleton
class UserPreferences @Inject constructor(private val dataStore: DataStore) {
    companion object {
        private val LOGGED_IN_KEY = booleanPreferencesKey("signed_in_key")
    }

    val isSignedIn: Flow<Boolean> = dataStore.dataStoreInstance.data
        .map { preferences ->
            preferences[LOGGED_IN_KEY] ?: false
        }

    suspend fun setSignedIn(signedIn: Boolean) {
        dataStore.dataStoreInstance.edit { preferences ->
            preferences[LOGGED_IN_KEY] = signedIn
        }
    }

    // For compatibility with existing code
    var isSignedInSync: Boolean
        get() = runBlocking { isSignedIn.first() }
        set(value) = runBlocking { setSignedIn(value) }
}