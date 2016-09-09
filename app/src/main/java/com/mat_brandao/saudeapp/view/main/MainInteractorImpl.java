package com.mat_brandao.saudeapp.view.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.text.TextUtils;

import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.functions.Action1;

public class MainInteractorImpl implements MainInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public MainInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }

    @Override
    public boolean hasGps() {
        PackageManager packageManager = mContext.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    @Override
    public boolean isGpsOn() {
        String provider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return !TextUtils.isEmpty(provider);
    }

    @Override
    public void requestMyLocation(OnLocationFound listener) {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(mContext);
        locationProvider.getLastKnownLocation()
                .retry(10)
                .subscribe(listener::onLocationFound);
    }
}