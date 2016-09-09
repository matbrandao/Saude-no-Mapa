package com.mat_brandao.saudeapp.view.main;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.tbruyelle.rxpermissions.RxPermissions;

public class MainPresenterImpl implements MainPresenter, OnMapReadyCallback, OnLocationFound {
    private static final String TAG = "MainPresenterImpl";
    private static final float DEFAULT_ZOOM = 14f;

    private MainInteractorImpl mInteractor;
    private Context mContext;
    private MainView mView;
    private GoogleMap mMap;

    private Location mLocation;

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onRetryClicked() {

    }

    public MainPresenterImpl(MainView view, Context context) {
        mInteractor = new MainInteractorImpl(context);
        mContext = context;
        mView = view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkPermissions();
    }

    private void checkPermissions() {
        RxPermissions.getInstance(mContext)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        requestUserLocation();
                    } else {
                        mView.showToast(mContext.getString(R.string.needed_location_permission));
                    }
                });
    }

    private void requestUserLocation() {
        if (mInteractor.hasGps()) {
            if (mInteractor.isGpsOn()) {
                onGpsLayout();
            } else {
                mView.showGpsDialog((dialogInterface, i) -> {
                    mView.startGpsIntent();
                });
            }
        } else {
            onNoGpsLayout();
        }
    }

    private void onGpsLayout() {
        mInteractor.requestMyLocation(this);
    }

    private void onNoGpsLayout() {
        // TODO: 09/09/2016 treat no gps layout;
    }

    @Override
    public void onGpsTurnedOn() {
        onGpsLayout();
    }

    @Override
    public void onGpsTurnedOff() {
        onNoGpsLayout();
    }

    @Override
    public void onLocationFound(Location location) {
        // TODO: 09/09/2016
        Log.d(TAG, "onLocationFound() called with: " + "location = [" + location + "]");
        mLocation = location;
        mMap.setMyLocationEnabled(true);
        updateMapLocation();
    }

    private void updateMapLocation() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), DEFAULT_ZOOM));
    }
}