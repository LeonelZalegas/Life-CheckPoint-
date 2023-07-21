package com.example.influencer.Core;

import android.app.Application;

public class MyApp extends Application {
    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApp getInstance() {
        return instance;
    }

    public String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }
}
