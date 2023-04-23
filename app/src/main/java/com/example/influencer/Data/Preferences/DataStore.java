package com.example.influencer.Data.Preferences;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

public class DataStore {
    //https://www.notion.so/DataStore-5ff1d598536947498542c292ae3f2702
    private static final String PREFERENCES_NAME = "user_preferences";
    private static RxDataStore<Preferences> dataStoreInstance;

    public static  RxDataStore<Preferences> getDataStoreInstance(Context context){
        if(dataStoreInstance == null){
            dataStoreInstance = new RxPreferenceDataStoreBuilder(context,PREFERENCES_NAME).build();
        }

        return dataStoreInstance;
    }

}
