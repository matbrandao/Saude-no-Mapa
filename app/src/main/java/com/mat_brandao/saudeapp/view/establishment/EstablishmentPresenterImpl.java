package com.mat_brandao.saudeapp.view.establishment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Conteudo;
import com.mat_brandao.saudeapp.domain.model.Error401;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.mat_brandao.saudeapp.domain.util.StringListener;
import com.mat_brandao.saudeapp.view.group.GroupActivity;
import com.mat_brandao.saudeapp.view.main.MainActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


public class EstablishmentPresenterImpl implements EstablishmentPresenter, OnMapReadyCallback, OnLocationFound,
        StringListener {
    private static final String TAG = "MainPresenterImpl";
    private static final float DEFAULT_ZOOM = 14f;
    private static final int ESTABLISHMENT_SEARCH_LIMIT = 30;
    public static final String ESTABLISHMENT_INTENT_KEY = "establishment_intent_key";

    private Activity mActivity;

    private EstablishmentInteractorImpl mInteractor;
    private Context mContext;
    private EstablishmentView mView;

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
    private SimpleRatingBar mRatingView;
    private ProgressBar mEstablishmentProgressBar;
    private ArrayAdapter<String> mAdapter;
    private List<String> mUfList;
    private String mSearchText;
    private String mSearchUf;

    @Override
    public void onResume() {
        ((MainActivity) mContext).setNavigationItemChecked(R.id.menu_item_establishments);
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

    public EstablishmentPresenterImpl(EstablishmentView view, Context context, Activity activity) {
        mInteractor = new EstablishmentInteractorImpl(context);
        mContext = context;
        mActivity = activity;
        mView = view;

        setSpinnerAdapter();
        requestLikedEstablishments();
        setupSearchObservers();
    }

    private void setupSearchObservers() {
        Observable<CharSequence> searchTextObservable =  mView.registerSearchEditTextObserver();
        Observable<Integer> ufSpinnerObservable =  mView.registerUfSpinnerObserver();

        searchTextObservable.subscribe();
        searchTextObservable.subscribe();

        Observable.combineLatest(searchTextObservable, ufSpinnerObservable,
                (charSequence, integer) -> {
                    mSearchText = charSequence.toString();
                    mSearchUf = mUfList.get(integer);
                    return (mSearchText.trim().length() > 3);
                }).debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shouldFetch -> {
                    if (shouldFetch) {
                        try {
                            mCategoriaList.clear();
                        } catch (Exception e) {}
                        try {
                            mEstablishmentList.clear();
                        } catch (Exception e) {}
                        try {
                            mRemoveList.clear();
                        } catch (Exception e) {}
                        try {
                            mRedeAtendimentoList.clear();
                        } catch (Exception e) {}
                        mInteractor.clearMarkers(mMap);
                        mView.setProgressFabVisibility(View.VISIBLE);
                        mLastObservable = mInteractor.requestEstablishmentsByName(mSearchText, mSearchUf);
                        mLastObserver = requestEstablishmentsObserver;
                        mSubscription.add(mInteractor.requestEstablishmentsByName(mSearchText, mSearchUf)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(requestEstablishmentsObserver));
                    } else {
                        mInteractor.clearMarkers(mMap);
                        showMapPins(mFilteredEstablishmentList);
                        try {
                            mInteractor.animateCameraToAllEstablishments(mMap);
                        } catch (IllegalStateException e) {}
                    }
                });
    }

    private void setSpinnerAdapter() {
        mUfList = mInteractor.getUfList();
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mUfList);
        mView.setUfSpinnerAdapter(mAdapter);
    }

    private void requestLikedEstablishments() {
        mSubscription.add(mInteractor.requestGetUserLikePosts()
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(listResponse -> {
                    if (listResponse != null && listResponse.isSuccessful()) {
                        if (listResponse.body() != null && listResponse.body().size() > 0) {
                            mInteractor.saveUserLikePostCode(listResponse.body().get(0).getCodPostagem());
                            for (Conteudo conteudo : listResponse.body().get(0).getConteudos()) {
                                mInteractor.requestGetPostContent(conteudo.getCodConteudoPostagem())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .onErrorReturn(throwable -> null)
                                        .subscribe(postContentResponse -> {
                                            if (postContentResponse != null && postContentResponse.isSuccessful()) {
                                                mInteractor.addEstablishmentToLikedList(postContentResponse.body().getCodConteudoPost(),
                                                        GenericUtil.getNumbersFromString(postContentResponse.body().getJSON()));
                                            }
                                        });
                            }
                        } else {
                            mSubscription.add(mInteractor.requestCreateLikePost()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .onErrorReturn(throwable -> null)
                                    .subscribe(createPostResponse -> {
                                        if (createPostResponse != null && createPostResponse.isSuccessful()) {
                                            mInteractor.saveUserLikePostCode(GenericUtil.getNumbersFromString(createPostResponse.headers().get("location")));
                                        }
                                    }));
                        }
                    }
                }));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style);
        mMap.setMapStyle(style);
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
                        onNoGpsLayout();
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
                    Establishment establishment = mInteractor.getEstablishmentFromMarker(marker);
                    new Handler().postDelayed(() -> {
                        mActivity.runOnUiThread(() -> {
                            showMarkerBottomSheetDialog(establishment);
                            getEstablishmentRating(Long.valueOf(establishment.getCodUnidade()));
                        });
                    }, 500);
                    marker.showInfoWindow();
                    lastOpenned = marker;
                    return true;
                });
    }

    private void getEstablishmentRating(Long codUnidade) {
        mSubscription.add(mInteractor.requestGetEstablishmentRatingPost(codUnidade)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(listResponse -> {
                    if (listResponse != null && listResponse.isSuccessful()) {
                        if (listResponse.body() != null && listResponse.body().size() > 0) {
                            mInteractor.addEstablishmentToRatingList(listResponse.body().get(0).getCodPostagem(), codUnidade);
                            if (listResponse.body().get(0).getConteudos().size() > 0) {
                                mInteractor.addEstablishmentToContentList(listResponse.body().
                                                get(0).getConteudos().get(0).getCodConteudoPostagem()
                                        , codUnidade);
                            }
                            mInteractor.requestEstablishmentRating(codUnidade)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .onErrorReturn(throwable -> null)
                                    .subscribe(responseBodyResponse -> {
                                        mEstablishmentProgressBar.setVisibility(View.GONE);
                                        if (responseBodyResponse == null) {
                                            mView.showToast(mContext.getString(R.string.error_get_establishment_review));
                                        } else {
                                            mRatingView.setIndicator(false);
                                            if (responseBodyResponse.isSuccessful()) {
                                                mRatingView.setRating(responseBodyResponse.body().getMedia());
                                            }
                                        }
                                    });
                        } else {
                            mEstablishmentProgressBar.setVisibility(View.GONE);
                            mSubscription.add(mInteractor.requestCreateRatingPost(codUnidade)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .onErrorReturn(throwable -> null)
                                    .retry(3)
                                    .subscribe());
                        }
                    } else {
                        mView.showToast(mContext.getString(R.string.error_get_establishment_review));
                    }
                }));
    }

    private void showMarkerBottomSheetDialog(@Nullable Establishment establishment) {
        if (establishment == null) {
            return;
        }
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);

        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_marker, null);
        MarkerViews bottomViews = new MarkerViews();
        ButterKnife.bind(bottomViews, dialogView);

        mEstablishmentProgressBar = bottomViews.establishmentProgress;
        mEstablishmentProgressBar.setVisibility(View.VISIBLE);

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

        bottomViews.phoneText.setOnClickListener(v -> {
            showCallToPhoneDialog(establishment.getTelefone());
        });

        bottomViews.groupImage.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, GroupActivity.class);
            intent.putExtra(ESTABLISHMENT_INTENT_KEY, establishment.getNomeFantasia());
            mView.goToActivity(intent);
        });

        if (mInteractor.isEstablishmentLiked(Long.valueOf(establishment.getCodUnidade()))) {
            bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
        }

        mRatingView = bottomViews.ratingView;
//        bottomViews.ratingView.setIndicator(true);
        Observable<View> clickEventObservable = Observable.create(new Observable.OnSubscribe<View>() {
            @Override
            public void call(final Subscriber<? super View> subscriber) {
                bottomViews.ratingView.setOnRatingBarChangeListener((simpleRatingBar, rating, fromUser) -> {
                    if (subscriber.isUnsubscribed()) return;
                    subscriber.onNext(simpleRatingBar);
                });
            }
        });

        clickEventObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .skip(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view -> {
                    mEstablishmentProgressBar.setVisibility(View.VISIBLE);
                    requestRateEstablishment(Long.valueOf(establishment.getCodUnidade()), bottomViews.ratingView);
                });

        bottomViews.likeImage.setOnClickListener(v -> {
            mEstablishmentProgressBar.setVisibility(View.VISIBLE);
            if (mInteractor.isEstablishmentLiked(Long.valueOf(establishment.getCodUnidade()))) {
                mSubscription.add(mInteractor.requestDisLikeEstablishment(establishment.getCodUnidade())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn(throwable -> null)
                        .subscribe(responseBodyResponse -> {
                            mEstablishmentProgressBar.setVisibility(View.GONE);
                            if (responseBodyResponse != null && responseBodyResponse.isSuccessful()) {
                                mInteractor.removeEstablishmentFromLikedList(establishment.getCodUnidade());
                                bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_empty));
                            } else {
                                mView.showToast(mContext.getString(R.string.http_error_generic));
                            }
                        }));
            } else {
                requestLikeEstablishment(establishment.getCodUnidade(), bottomViews.likeImage);
            }
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(R.color.default_dialog_background);

        bottomViews.mainInfoCard.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight(bottomViews.mainInfoCard.getMeasuredHeight() + 115);

        bottomSheetDialog.setOnDismissListener(dialogInterface -> {
            lastOpenned.hideInfoWindow();
        });

        bottomSheetDialog.show();
    }

    private void requestRateEstablishment(Long establishmentCode, SimpleRatingBar ratingView) {
        ratingView.setIndicator(true);
        mInteractor.requestRateEstablishment(establishmentCode, ratingView.getRating())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(responseBodyResponse -> {
                    mEstablishmentProgressBar.setVisibility(View.GONE);
                    ratingView.setIndicator(false);
                    if (responseBodyResponse != null && responseBodyResponse.isSuccessful()) {
                    } else {
                        mView.showToast(mContext.getString(R.string.error_rating_establishment_try_again));
                    }
                });
    }

    private void requestLikeEstablishment(String codUnidade, ImageView likeImage) {
        if (mInteractor.hasLikePostCode()) {
            mSubscription.add(mInteractor.requestLikeEstablishment(codUnidade)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(throwable -> null)
                    .subscribe(likeResponse -> {
                        mEstablishmentProgressBar.setVisibility(View.GONE);
                        if (likeResponse != null && likeResponse.isSuccessful()) {
                            mInteractor.addEstablishmentToLikedList(GenericUtil.getContentIdFromUrl(String.valueOf(mInteractor.getPostCode()),
                                    likeResponse.headers().get("location")), Long.valueOf(codUnidade));
                            likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
                        } else {
                            mView.showToast(mContext.getString(R.string.http_error_generic));
                        }
                    }));
        } else {
            mSubscription.add(mInteractor.requestCreateLikePost()
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(throwable -> null)
                    .subscribe(createPostResponse -> {
                        if (createPostResponse != null && createPostResponse.isSuccessful()) {
                            Long postCode = GenericUtil.getNumbersFromString(createPostResponse.headers().get("location"));
                            mInteractor.saveUserLikePostCode(postCode);
                            mSubscription.add(mInteractor.requestLikeEstablishment(postCode, codUnidade)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .onErrorReturn(throwable -> null)
                                    .subscribe(likeResponse -> {
                                        mEstablishmentProgressBar.setVisibility(View.GONE);
                                        if (likeResponse != null && likeResponse.isSuccessful()) {
                                            mInteractor.addEstablishmentToLikedList(GenericUtil.getContentIdFromUrl(String.valueOf(postCode), likeResponse.headers().get("location")), Long.valueOf(codUnidade));
                                            likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
                                        } else {
                                            mView.showToast(mContext.getString(R.string.http_error_generic));
                                        }
                                    }));
                        } else {
                            mView.showToast(mContext.getString(R.string.http_error_generic));
                        }
                    }));
        }
    }

    private void showCallToPhoneDialog(String telefone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.call_dialog_title);
        builder.setMessage(mContext.getString(R.string.call_dialog_message) + telefone + "?");
        builder.setPositiveButton(R.string.call_dialog_positive, (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + telefone));
            mContext.startActivity(intent);
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
        mView.setProgressFabVisibility(View.GONE);
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
            mActivity.runOnUiThread(() -> {
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
        mMap.setMyLocationEnabled(true);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = location;
        mInteractor.requestUserUf(location.getLatitude(), location.getLongitude(), this);
        updateMapLocation();

        requestEstablishments(0);
    }

    /**
     * On Geocode found.
     * @param uf
     */
    @Override
    public void onNext(String uf) {
        try {
            mView.setUfSpinnerSelection(mUfList.indexOf(uf));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            mView.setProgressFabVisibility(View.GONE);
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<List<Establishment>> listResponse) {
            if (listResponse.isSuccessful()) {
                mFilteredEstablishmentList.addAll(listResponse.body());
                mEstablishmentList.addAll(listResponse.body());

                if (mEstablishmentList.size() > 0) {
                    showMapPins(listResponse.body());
                    if (listResponse.body().size() == ESTABLISHMENT_SEARCH_LIMIT) {
                        requestEstablishments(mEstablishmentList.size());
                    }
                    if (mEstablishmentList.size() <= ESTABLISHMENT_SEARCH_LIMIT) {
                        mInteractor.animateCameraToAllEstablishments(mMap);
                    }

                    mView.toggleFabButton(true);
                    mView.setProgressFabVisibility(View.GONE);
                } else {
                    mView.toggleFabButton(false);
                    mView.showToast(mContext.getString(R.string.establishment_no_results));
                    mView.setProgressFabVisibility(View.GONE);
                }
            } else {
                try {
                    Error401 error401 = new Gson().fromJson(listResponse.errorBody().string(), Error401.class);
                    mView.showToast(error401.getMessageList().get(0).getText());
                } catch (Exception e) {
                    mView.showToast(mContext.getString(R.string.http_error_generic));
                }
                mView.setProgressFabVisibility(View.GONE);
                mView.showNoConnectionSnackBar();
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
        @Bind(R.id.establishment_like_image)
        ImageView likeImage;
        @Bind(R.id.group_image)
        ImageView groupImage;
        @Bind(R.id.rating_view)
        SimpleRatingBar ratingView;
        @Bind(R.id.establishment_progress)
        ProgressBar establishmentProgress;
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