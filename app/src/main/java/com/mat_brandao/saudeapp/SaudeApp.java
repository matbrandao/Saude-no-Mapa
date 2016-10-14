package com.mat_brandao.saudeapp;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.util.GroupsService;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Created by Mateus Brandão on 07-Sep-16.
 */

public class SaudeApp extends Application {
    private static final String TAG = "SaudeApp";
    public static final long UPDATE_TIME_IN_MILLIS = 2 * 1000 * 60;

    private User mUser;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(configuration);

        FacebookSdk.sdkInitialize(getApplicationContext());
        Timber.i("Signature " +  FacebookSdk.getApplicationSignature(getApplicationContext()));

        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser = realm.where(User.class).findFirst();
        });

        setAlarm();
    }

    private void setAlarm() {
        Log.d(TAG, "setAlarm: setting alarm");
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(new Date());

        Intent intent = new Intent(this, GroupsService.class);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, cal_now.getTimeInMillis(), UPDATE_TIME_IN_MILLIS, pendingIntent);
    }
}
