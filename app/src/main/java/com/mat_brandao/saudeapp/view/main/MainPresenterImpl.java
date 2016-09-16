package com.mat_brandao.saudeapp.view.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
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

    private List<Establishment> mFilteredEstablishmentList = new ArrayList<>();
    private List<Establishment> mEstablishmentList = new ArrayList<>();

    private List<Establishment> mRemoveList = new ArrayList<>();

    private Marker lastOpenned;
    private List<String> mRedeAtendimentoList, mCategoriaList;
    private Observable<Response<List<Establishment>>> mLastObservable;
    private Observer<Response<List<Establishment>>> mLastObserver;

    private boolean mIsFiltered;
    private TextView mCurrentFilterTitle;

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
        bottomViews.descricaoCompletaText.setText(GenericUtil.capitalize(establishment.getDescricaoCompleta().toLowerCase()));
        bottomViews.enderecoText.setText(mInteractor.getAddressText(establishment.getLogradouro(), establishment.getNumero(),
                establishment.getBairro(), establishment.getCidade(), establishment.getUf(), establishment.getCep()));
        if (TextUtils.isEmpty(establishment.getTelefone())) {
            bottomViews.phoneLayout.setVisibility(View.GONE);
        } else {
            bottomViews.phoneText.setText(establishment.getTelefone());
        }
        bottomViews.turnoAtendimento.setText(establishment.getTurnoAtendimento());
        bottomViews.tipoUnidadeText.setText(GenericUtil.capitalize(establishment.getTipoUnidade().toLowerCase()));
        bottomViews.redeAtendimentoText.setText(GenericUtil.capitalize(establishment.getEsferaAdministrativa().toLowerCase()));
        bottomViews.vinculoSusText.setText(GenericUtil.capitalize(establishment.getVinculoSus().toLowerCase()));
        bottomViews.fluxoClientelaText.setText(mInteractor.getFluxoClientelaText(establishment.getFluxoClientela()));
        bottomViews.cnpjText.setText(establishment.getCnpj());
        bottomViews.servicesText.setText(mInteractor.getServicesText(establishment));
        bottomViews.enderecoText.setOnClickListener(view -> {
            showGoToAddressDialog(establishment.getLatitude(), establishment.getLongitude());
        });

        bottomViews.phoneText.setOnClickListener(view -> {
            showCallToPhoneDialog(establishment.getTelefone());
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(R.color.default_dialog_background);

        bottomViews.mainInfoCard.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight(bottomViews.mainInfoCard.getMeasuredHeight() + 100);

        bottomSheetDialog.setOnDismissListener(dialogInterface -> {
            lastOpenned.hideInfoWindow();
        });

        bottomSheetDialog.show();
    }

    private void showCallToPhoneDialog(String telefone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.call_dialog_title);
        builder.setMessage(mContext.getString(R.string.call_dialog_message) + telefone + "?");
        builder.setPositiveButton(R.string.call_dialog_positive, (dialogInterface, i) -> {
            RxPermissions.getInstance(mContext)
                    .request(Manifest.permission.CALL_PHONE)
                    .subscribe(granted -> {
                        if (granted) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telefone));
                            mView.goToActivity(intent);
                        } else {
                            mView.showToast(mContext.getString(R.string.call_phone_permission_needed));
                        }
                    });
        });
        builder.setNegativeButton(R.string.call_dialog_negative, (dialogInterface, i) -> {});
        builder.create().show();
    }

    private void showGoToAddressDialog(Double latitude, Double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.location_dialog_title);
        builder.setMessage(mContext.getString(R.string.location_dialog_message));
        builder.setPositiveButton(R.string.location_dialog_positive, (dialogInterface, i) -> {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude));
            mView.goToActivity(intent);
        });
        builder.setNegativeButton(R.string.location_dialog_negative, (dialogInterface, i) -> {});
        builder.create().show();
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
        mView.toggleFabButton(false);
        new Handler().postDelayed(() -> {
            ((MainActivity) mContext).runOnUiThread(() -> {
                mView.toggleFabButton(true);
            });
        }, 1000);
        showFilterBottomSheetDialog();
    }

    private void doFilter(FilterViews bottomViews) {
        mFilteredEstablishmentList.clear();
        mFilteredEstablishmentList.addAll(mEstablishmentList);
        mRemoveList.clear();

        int redePosition = bottomViews.redeAtendimentoSpinner.getSelectedItemPosition();
        String redeAtendimento = mRedeAtendimentoList.get(redePosition == 0 ? redePosition : redePosition - 1);
        for (Establishment establishment : mEstablishmentList) {
            if (redePosition > 0 && !redeAtendimento.equals(establishment.getEsferaAdministrativa())) {
                mRemoveList.add(establishment);
            }
        }

        int categoriaPosition = bottomViews.categoriaSpinner.getSelectedItemPosition();
        String categoria = mCategoriaList.get(categoriaPosition == 0 ? categoriaPosition : categoriaPosition - 1).toUpperCase();
        for (Establishment establishment : mEstablishmentList) {
            if (categoriaPosition > 0 && !categoria.equals(establishment.getCategoriaUnidade())) {
                mRemoveList.add(establishment);
            }
        }

        boolean temVinculoSus = bottomViews.vinculoSusCheckbox.isChecked();
        for (Establishment establishment : mFilteredEstablishmentList) {
            if (temVinculoSus && !establishment.getVinculoSus().equals("Sim")) {
                mRemoveList.add(establishment);
            }
        }

        boolean temAtendimentoUrgencial = bottomViews.atendimentoUrgencialCheckbox.isChecked();
        for (Establishment establishment : mFilteredEstablishmentList) {
            if (temAtendimentoUrgencial && !establishment.getTemAtendimentoUrgencia().equals("Sim")) {
                mRemoveList.add(establishment);
            }
        }

        boolean temAtendimentoAmbulatorial = bottomViews.atendimentoAmbulatorialCheckbox.isChecked();
        for (Establishment establishment : mFilteredEstablishmentList) {
            if (temAtendimentoAmbulatorial && !establishment.getTemAtendimentoAmbulatorial().equals("Sim")) {
                mRemoveList.add(establishment);
            }
        }

        boolean temCentroCirurgico = bottomViews.centroCirurgicoCheckbox.isChecked();
        for (Establishment establishment : mFilteredEstablishmentList) {
            if (temCentroCirurgico && !establishment.getTemCentroCirurgico().equals("Sim")) {
                mRemoveList.add(establishment);
            }
        }

        boolean temObstetra = bottomViews.obstetraCheckbox.isChecked();
        for (Establishment establishment : mFilteredEstablishmentList) {
            if (temObstetra && !establishment.getTemObstetra().equals("Sim")) {
                mRemoveList.add(establishment);
            }
        }

        boolean temNeoNatal = bottomViews.neoNatalCheckbox.isChecked();
        for (Establishment establishment : mFilteredEstablishmentList) {
            if (temNeoNatal && !establishment.getTemNeoNatal().equals("Sim")) {
                mRemoveList.add(establishment);
            }
        }

        boolean temDialise = bottomViews.dialiseCheckbox.isChecked();
        for (Establishment establishment : mFilteredEstablishmentList) {
            if (temDialise && !establishment.getTemDialise().equals("Sim")) {
                mRemoveList.add(establishment);
            }
        }

        mFilteredEstablishmentList.removeAll(mRemoveList);
        updateCurrentFilterTitle();
    }

    private void showFilterBottomSheetDialog() {
        mIsFiltered = false;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_filter, null);
        FilterViews bottomViews = new FilterViews();
        ButterKnife.bind(bottomViews, dialogView);

        mCurrentFilterTitle = bottomViews.filterTitle;
        updateCurrentFilterTitle();

        bottomViews.redeAtendimentoSpinner.setAdapter(getRedeAtendimentoAdapter());
        bottomViews.categoriaSpinner.setAdapter(getCategoriaSpinner());

        RxAdapterView.itemSelections(bottomViews.redeAtendimentoSpinner).subscribe(integer -> {
            doFilter(bottomViews);
        });

        RxAdapterView.itemSelections(bottomViews.categoriaSpinner).subscribe(integer -> {
            doFilter(bottomViews);
        });

        RxCompoundButton.checkedChanges(bottomViews.vinculoSusCheckbox).subscribe(isChecked -> {
            doFilter(bottomViews);
        });

        RxCompoundButton.checkedChanges(bottomViews.atendimentoUrgencialCheckbox).subscribe(isChecked -> {
            doFilter(bottomViews);
        });

        RxCompoundButton.checkedChanges(bottomViews.atendimentoAmbulatorialCheckbox).subscribe(isChecked -> {
            doFilter(bottomViews);
        });

        RxCompoundButton.checkedChanges(bottomViews.centroCirurgicoCheckbox).subscribe(isChecked -> {
            doFilter(bottomViews);
        });

        RxCompoundButton.checkedChanges(bottomViews.obstetraCheckbox).subscribe(isChecked -> {
            doFilter(bottomViews);
        });

        RxCompoundButton.checkedChanges(bottomViews.neoNatalCheckbox).subscribe(isChecked -> {
            doFilter(bottomViews);
        });

        RxCompoundButton.checkedChanges(bottomViews.dialiseCheckbox).subscribe(isChecked -> {
            doFilter(bottomViews);
        });

        bottomViews.filterButton.setOnClickListener(view -> {
            mIsFiltered = true;
            mInteractor.clearMarkers(mMap);
            showMapPins(mFilteredEstablishmentList);
            mInteractor.animateCameraToAllEstablishments(mMap);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(R.color.default_dialog_background);

        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight((int) (mView.getMapContainerHeight() + 400));

        bottomSheetDialog.setOnDismissListener(dialogInterface -> {
            if (!mIsFiltered) {
                mFilteredEstablishmentList.clear();
                mFilteredEstablishmentList.addAll(mEstablishmentList);
            }
        });

        bottomSheetDialog.show();
    }

    private void updateCurrentFilterTitle() {
        mCurrentFilterTitle.setText(mContext.getString(R.string.filter_dialog_title) + " (" + mFilteredEstablishmentList.size() + ")");
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
            mView.setProgressFabVisibility(View.INVISIBLE);
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<List<Establishment>> listResponse) {
            if (listResponse.isSuccessful()) {
                mFilteredEstablishmentList.addAll(listResponse.body());
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
        @Bind(R.id.descricao_completa_text)
        TextView descricaoCompletaText;
        @Bind(R.id.endereco_text)
        TextView enderecoText;
        @Bind(R.id.phone_text)
        TextView phoneText;
        @Bind(R.id.main_info_card)
        LinearLayout mainInfoCard;
        @Bind(R.id.tipo_unidade_text)
        TextView tipoUnidadeText;
        @Bind(R.id.rede_atendimento_text)
        TextView redeAtendimentoText;
        @Bind(R.id.vinculo_sus_text)
        TextView vinculoSusText;
        @Bind(R.id.fluxo_clientela_text)
        TextView fluxoClientelaText;
        @Bind(R.id.cnpj_text)
        TextView cnpjText;
        @Bind(R.id.turno_atendimento_text)
        TextView turnoAtendimento;
        @Bind(R.id.services_text)
        TextView servicesText;
        @Bind(R.id.bottom_sheet)
        NestedScrollView bottomSheet;
        @Bind(R.id.phone_layout)
        LinearLayout phoneLayout;
    }

    class FilterViews {
        @Bind(R.id.filter_title)
        TextView filterTitle;
        @Bind(R.id.categoria_spinner)
        Spinner categoriaSpinner;
        @Bind(R.id.rede_atendimento_spinner)
        Spinner redeAtendimentoSpinner;
        @Bind(R.id.vinculo_sus_checkbox)
        CheckBox vinculoSusCheckbox;
        @Bind(R.id.atendimento_urgencial_checkbox)
        CheckBox atendimentoUrgencialCheckbox;
        @Bind(R.id.atendimento_ambulatorial_checkbox)
        CheckBox atendimentoAmbulatorialCheckbox;
        @Bind(R.id.centro_cirurgico_checkbox)
        CheckBox centroCirurgicoCheckbox;
        @Bind(R.id.obstetra_checkbox)
        CheckBox obstetraCheckbox;
        @Bind(R.id.neo_natal_checkbox)
        CheckBox neoNatalCheckbox;
        @Bind(R.id.dialise_checkbox)
        CheckBox dialiseCheckbox;
        @Bind(R.id.filter_button)
        Button filterButton;
    }
}