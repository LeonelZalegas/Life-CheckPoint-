package com.example.influencer.Core.Data.Preferences;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class DataStore {
    //https://www.notion.so/DataStore-5ff1d598536947498542c292ae3f2702
    private static final String PREFERENCES_NAME = "user_preferences";
    private final RxDataStore<Preferences> dataStoreInstance;

    @Inject
    public DataStore(@ApplicationContext Context context) {
        dataStoreInstance = new RxPreferenceDataStoreBuilder(context, PREFERENCES_NAME).build();
    }

    public RxDataStore<Preferences> getDataStoreInstance() {
        return dataStoreInstance;
    }
}
