package com.mat_brandao.saudeapp.view.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.text.TextUtils;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit2.Response;
import rx.Observable;

public class MainInteractorImpl implements MainInteractor {
    private static final Double SEARCH_RADIUS = 10.0;

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private User mUser;

    public MainInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
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

    @Override
    public Observable<Response<List<Establishment>>> requestEstablishmentsByLocation(Location location, int pagination) {
        return RestClient.getHeader(mUser.getAppToken())
                .getEstablishmentsByGeoLocation(location.getLatitude(),
                        location.getLongitude(), SEARCH_RADIUS, pagination);
    }
}