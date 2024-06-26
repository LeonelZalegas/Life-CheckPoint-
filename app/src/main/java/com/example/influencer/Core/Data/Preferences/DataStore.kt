package com.example.influencer.Core.Data.Preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Singleton
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.rxjava3.RxDataStore
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.core.DataStore

//https://www.notion.so/DataStore-5ff1d598536947498542c292ae3f2702
@Singleton
class DataStore @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private const val PREFERENCES_NAME = "user_preferences"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)
    }

    val dataStoreInstance: DataStore<Preferences> = context.dataStore
}