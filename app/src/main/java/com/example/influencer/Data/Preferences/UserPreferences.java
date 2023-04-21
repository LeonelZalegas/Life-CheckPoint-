package com.example.influencer.Data.Preferences;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import java.util.Objects;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class UserPreferences {

    Preferences.Key<Boolean> LOGGED_IN_KEY;
    private RxDataStore<Preferences> dataStoreInstance;
    private static UserPreferences userPreferencesinstance;

    private UserPreferences(RxDataStore<Preferences> dataStoreInstance){
        this.dataStoreInstance = dataStoreInstance;
        this.LOGGED_IN_KEY = PreferencesKeys.booleanKey("signed_in_key");
    }

    public static synchronized UserPreferences getInstance(Context context) {
        if (userPreferencesinstance == null) {
            userPreferencesinstance = new UserPreferences(DataStore.getDataStoreInstance(context));
        }
        return userPreferencesinstance;
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


