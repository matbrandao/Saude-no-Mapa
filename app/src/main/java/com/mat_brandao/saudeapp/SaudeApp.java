package com.mat_brandao.saudeapp;

import android.app.Application;

import com.facebook.FacebookSdk;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Created by Mateus Brandão on 07-Sep-16.
 */

public class SaudeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                // TODO: 27/07/2016
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(configuration);

        FacebookSdk.sdkInitialize(getApplicationContext());
        Timber.i("Signature " +  FacebookSdk.getApplicationSignature(getApplicationContext()));
    }
}
