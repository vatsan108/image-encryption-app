package com.example.disastermanagementsender.acommon;

import android.app.Application;

import com.parse.Parse;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();



        // AESENC
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tGUuWYnHvJzXdri64IPUMNauQ7NyD756NFIiPPL5")
                // if defined
                .clientKey("DqKLL5WUUe0NlTy4K6fcGIDGRVsrqnVUzhVci65G")
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}