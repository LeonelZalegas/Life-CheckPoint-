package com.example.influencer.Core;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //eliminar luego
    //public static MyApp getInstance() {
      //  return instance;
  //  }

    //eliminar luego
    //public String getAString(int resId){return getResources().getString(resId);}
}
