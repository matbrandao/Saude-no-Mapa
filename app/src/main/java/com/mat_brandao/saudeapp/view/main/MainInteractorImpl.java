package com.mat_brandao.saudeapp.view.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.HashMap;
import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit2.Response;
import rx.Observable;

public class MainInteractorImpl implements MainInteractor {
    private static final Double SEARCH_RADIUS = 10.0;

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private User mUser;
    private HashMap<String, Marker> mDeviceMarkerHash;


    public MainInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
        mDeviceMarkerHash = new HashMap<>();
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

    @Override
    public void clearMarkers(GoogleMap map) {
        if (map != null) {
            if (mDeviceMarkerHash.size() > 0) {
                for (Marker marker : mDeviceMarkerHash.values()) {
                    marker.remove();
                }
            }
        }
    }

    @Override
    public void drawEstablishment(GoogleMap map, Establishment establishment) {
        if (map != null) {
            mDeviceMarkerHash.put(establishment.getCodUnidade(), map
                    .addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_hospital))
                            .position(new LatLng(establishment.getLatitude(), establishment.getLongitude()))
                            .title(establishment.getNomeFantasia())));
        }
    }

    @Override
    public void animateCameraToAllEstablishments(GoogleMap mMap) {
        if (mMap != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : mDeviceMarkerHash.values()) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

    @Override
    public void animateMarketToTop(GoogleMap map, Marker marker, double mapHeight) {
        float zoom_lvl = map.getCameraPosition().zoom;
        double dpPerdegree = 256.0 * Math.pow(2, zoom_lvl) / 170.0;
        double screen_height_30p = 25.0 * mapHeight / 100.0;
        double degree_30p = screen_height_30p / dpPerdegree;
        LatLng latLng = marker.getPosition();
        LatLng centerlatlng = new LatLng(latLng.latitude - degree_30p, latLng.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerlatlng, 15), 500, null);
    }
}