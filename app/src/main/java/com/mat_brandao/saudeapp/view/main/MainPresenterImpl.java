package com.mat_brandao.saudeapp.view.main;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Error401;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


public class MainPresenterImpl implements MainPresenter, OnMapReadyCallback, OnLocationFound {
    private static final String TAG = "MainPresenterImpl";
    private static final float DEFAULT_ZOOM = 14f;
    private static final int ESTABLISHMENT_SEARCH_LIMIT = 30;

    private MainInteractorImpl mInteractor;
    private Context mContext;
    private MainView mView;

    private GoogleMap mMap;
    private Location mLocation;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    private List<Establishment> mEstablishmentList = new ArrayList<>();
    private Marker lastOpenned;

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mSubscription.unsubscribe();
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
        configureMapClickListener();
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

    private void configureMapClickListener() {
        mMap.setOnMarkerClickListener(
                marker -> {
                    if (lastOpenned != null) {
                        lastOpenned.hideInfoWindow();
                        if (lastOpenned.equals(marker)) {
                            lastOpenned = null;
                            return true;
                        }
                    }

                    mInteractor.animateMarketToTop(mMap, marker, mView.getMapContainerHeight());
                    new Handler().postDelayed(() -> {
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            showMarkerBottomSheetDialog(marker);
                        });
                    }, 500);
                    marker.showInfoWindow();
                    lastOpenned = marker;
                    return true;
                });
    }

    private void showMarkerBottomSheetDialog(Marker marker) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_marker, null);

        bottomSheetDialog.setContentView(dialogView);

        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight((int) (mView.getMapContainerHeight() - 200));

        bottomSheetDialog.show();
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
        mLocation = location;
        mMap.setMyLocationEnabled(true);
        updateMapLocation();

        requestEstablishments(0);
    }

    private void requestEstablishments(int pagination) {
        mSubscription.add(mInteractor.requestEstablishmentsByLocation(mLocation, pagination)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(requestEstablishmentsObserver));
    }

    private void updateMapLocation() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLocation.getLatitude(),
                        mLocation.getLongitude()),
                DEFAULT_ZOOM));
    }

    private void showMapPins(List<Establishment> establishmentList) {
        for (Establishment establishment : establishmentList) {
            mInteractor.drawEstablishment(mMap, establishment);
        }
    }

    private Observer<Response<List<Establishment>>> requestEstablishmentsObserver = new Observer<Response<List<Establishment>>>() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted() called with: " + "");
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<List<Establishment>> listResponse) {
            if (listResponse.isSuccessful()) {
                mEstablishmentList.addAll(listResponse.body());

                showMapPins(listResponse.body());
                if (listResponse.body().size() == ESTABLISHMENT_SEARCH_LIMIT) {
                    requestEstablishments(mEstablishmentList.size());
                }
                if (mEstablishmentList.size() <= ESTABLISHMENT_SEARCH_LIMIT) {
                    mInteractor.animateCameraToAllEstablishments(mMap);
                }
            } else {
                try {
                    Error401 error401 = new Gson().fromJson(listResponse.errorBody().string(), Error401.class);
                    mView.showToast(error401.getMessageList().get(0).getText());
                } catch (Exception e) {
                    mView.showToast(mContext.getString(R.string.http_error_generic));
                }
                mView.showToast(mContext.getString(R.string.http_error_500));
            }
        }
    };
}