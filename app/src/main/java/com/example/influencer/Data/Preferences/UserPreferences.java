package com.example.influencer.Data.Preferences;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class UserPreferences {
//https://www.notion.so/DataStore-5ff1d598536947498542c292ae3f2702
    private final Preferences.Key<Boolean> LOGGED_IN_KEY;
    private RxDataStore<Preferences> dataStoreInstance;

    @Inject
    private UserPreferences(DataStore dataStore){
        this.dataStoreInstance = dataStore.getDataStoreInstance();
        this.LOGGED_IN_KEY = PreferencesKeys.booleanKey("signed_in_key");
    }

    public boolean isSignedIn(){
        return dataStoreInstance.data().map(prefs -> prefs.get(LOGGED_IN_KEY)).onErrorReturnItem(false).blockingFirst(false);
    }

    public void setSignedIn(boolean signedIn){
        dataStoreInstance.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(LOGGED_IN_KEY,signedIn);
            return Single.just(mutablePreferences);
        });
    }
}


