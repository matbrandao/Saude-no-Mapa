package com.mat_brandao.saudeapp.view.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Error401;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
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
    private List<String> mRedeAtendimentoList, mCategoriaList, mVinculoSusList;
    private Observable<Response<List<Establishment>>> mLastObservable;
    private Observer<Response<List<Establishment>>> mLastObserver;

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
        mView.toggleFabButton(false);
        mView.setProgressFabVisibility(View.VISIBLE);
        mLastObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mLastObserver);
    }

    public MainPresenterImpl(MainView view, Context context) {
        mInteractor = new MainInteractorImpl(context);
        mContext = context;
        mView = view;

        // TODO: 12/09/2016 add loading to first loading
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
                            showMarkerBottomSheetDialog(mInteractor.getEstablishmentFromMarker(marker));
                        });
                    }, 500);
                    marker.showInfoWindow();
                    lastOpenned = marker;
                    return true;
                });
    }

    private void showMarkerBottomSheetDialog(Establishment establishment) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_marker, null);
        MarkerViews bottomViews = new MarkerViews();
        ButterKnife.bind(bottomViews, dialogView);

        bottomViews.establishmentTitle.setText(GenericUtil.capitalize(establishment.getNomeFantasia().toLowerCase()));

        bottomSheetDialog.setContentView(dialogView);

        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight((int) (mView.getMapContainerHeight() - 400));

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
    public void onFilterFabClick() {
        showFilterBottomSheetDialog();
    }

    private void showFilterBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_filter, null);
        FilterViews bottomViews = new FilterViews();
        ButterKnife.bind(bottomViews, dialogView);

        bottomViews.redeAtendimentoSpinner.setAdapter(getRedeAtendimentoAdapter());
//        bottomViews.vinculoSusSpinner.setAdapter(getVinculoSusAdapter());
        bottomViews.categoriaSpinner.setAdapter(getCategoriaSpinner());
        bottomSheetDialog.setContentView(dialogView);

        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight((int) (mView.getMapContainerHeight() - 400));

        bottomSheetDialog.show();
    }

    private SpinnerAdapter getCategoriaSpinner() {
        List<String> itemList = new ArrayList<>();
        itemList.add("Selecionar");

        mCategoriaList = new ArrayList<>();
        for (Establishment establishment : mEstablishmentList) {
            String categoria = GenericUtil.capitalize(establishment.getCategoriaUnidade().toLowerCase());
            if (!mCategoriaList.contains(categoria)) {
                mCategoriaList.add(categoria);
            }
        }

        itemList.addAll(mCategoriaList);
        return new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, itemList);
    }

    private ArrayAdapter<String> getRedeAtendimentoAdapter() {
        List<String> itemList = new ArrayList<>();
        itemList.add("Selecionar");

        mRedeAtendimentoList = new ArrayList<>();
        for (Establishment establishment : mEstablishmentList) {
            if (!mRedeAtendimentoList.contains(establishment.getEsferaAdministrativa())) {
                mRedeAtendimentoList.add(establishment.getEsferaAdministrativa());
            }
        }

        itemList.addAll(mRedeAtendimentoList);
        return new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, itemList);
    }

    @Override
    public void onLocationFound(Location location) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = location;
        mMap.setMyLocationEnabled(true);
        updateMapLocation();

        requestEstablishments(0);
    }

    private void requestEstablishments(int pagination) {
        mLastObservable = mInteractor.requestEstablishmentsByLocation(mLocation, pagination);
        mLastObserver = requestEstablishmentsObserver;
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
                if (mEstablishmentList.size() > 0) {
                    mView.toggleFabButton(true);
                    mView.setProgressFabVisibility(View.GONE);
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

    class MarkerViews {
        @Bind(R.id.establishment_title)
        TextView establishmentTitle;
    }

    class FilterViews {
        @Bind(R.id.rede_atendimento_spinner)
        Spinner redeAtendimentoSpinner;
//        @Bind(R.id.vinculo_sus_spinner)
//        Spinner vinculoSusSpinner;
        @Bind(R.id.categoria_spinner)
        Spinner categoriaSpinner;
    }
}