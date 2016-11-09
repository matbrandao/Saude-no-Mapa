package com.mat_brandao.saudeapp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Created by Mateus Brandão on 07-Sep-16.
 */

public class SaudeApp extends Application {
    private static final String TAG = "SaudeApp";
    public static final long UPDATE_TIME_IN_MILLIS = 1 * 1000 * 60;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Timber.plant(new Timber.DebugTree());

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(configuration);

        FacebookSdk.sdkInitialize(getApplicationContext());
        Timber.i("Signature " +  FacebookSdk.getApplicationSignature(getApplicationContext()));
    }
}
