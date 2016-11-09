package com.mat_brandao.saudeapp.view.emergency;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Conteudo;
import com.mat_brandao.saudeapp.domain.model.Error401;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.mat_brandao.saudeapp.domain.util.StringListener;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.google.android.gms.internal.zzs.TAG;

public class EmergencyPresenterImpl implements EmergencyPresenter, OnLocationFound, OnMapReadyCallback, StringListener {

    private static final long DEBOUNCE_TIMER = 500;
    private static final float DEFAULT_ZOOM = 14f;
    private static final int ESTABLISHMENT_SEARCH_LIMIT = 30;

    private EmergencyInteractorImpl mInteractor;
    private Activity mContext;
    private EmergencyView mView;

    private Handler mHandler = new Handler();
    private Runnable mDismissKeyboardRunnable = () -> mView.dismissKeyboard();

    private List<Establishment> mEstablishmentList = new ArrayList<>();
    private List<Establishment> mRemoveList = new ArrayList<>();

    private CompositeSubscription mSubscription = new CompositeSubscription();

    private Observable<Response<List<Establishment>>> mLastObservable;
    private Observer<Response<List<Establishment>>> mLastObserver;
    private GoogleMap mMap;
    private Marker lastOpenned;
    private Location mLocation;

    private ProgressBar mEstablishmentProgress;
    private SimpleRatingBar mRatingView;
    private boolean isLiked;
    private List<String> mUfList;
    private ArrayAdapter mAdapter;
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

    public EmergencyPresenterImpl(EmergencyView view, Activity context) {
        mInteractor = new EmergencyInteractorImpl(context);
        mContext = context;
        mView = view;

        setSpinnerAdapter();
        setupSearchObservable();
        requestLikedEstablishments();
    }

    private void setSpinnerAdapter() {
        mUfList = mInteractor.getUfList();
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mUfList);
        mView.setUfSpinnerAdapter(mAdapter);
    }

    private void setupSearchObservable() {
        mView.registerSearchEditTextObserver()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    mHandler.removeCallbacks(mDismissKeyboardRunnable);
                });

        Observable<CharSequence> searchTextObservable = mView.registerSearchEditTextObserver();
        Observable<Integer> ufSpinnerObservable = mView.registerUfSpinnerObserver();

        searchTextObservable.subscribe();
        searchTextObservable.subscribe();

        Observable.combineLatest(searchTextObservable, ufSpinnerObservable,
                (charSequence, integer) -> {
                    mSearchText = charSequence.toString();
                    mSearchUf = mUfList.get(integer);
                    return (mSearchText.trim().length() > 3);
                }).debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .skip(1)
                .subscribe(shouldFetch -> {
                    if (shouldFetch) {
                        try {
                            mEstablishmentList.clear();
                        } catch (Exception e) {
                        }
                        try {
                            mRemoveList.clear();
                        } catch (Exception e) {
                        }
                        mInteractor.clearMarkers(mMap);
                        mView.setProgressFabVisibility(View.VISIBLE);
                        mLastObservable = mInteractor.requestEstablishmentsByName(mSearchText, mSearchUf);
                        mLastObserver = requestEstablishmentsObserver;
                        mSubscription.add(mInteractor.requestEstablishmentsByName(mSearchText, mSearchUf)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(requestEstablishmentsObserver));
                    } else {
                        mInteractor.clearMarkers(mMap);
                        mEstablishmentList.clear();
                        mEstablishmentList.addAll(mFilteredEstablishmentList);
                        showMapPins(mEstablishmentList);
                        try {
                            mInteractor.animateCameraToAllEstablishments(mMap);
                        } catch (IllegalStateException e) {
                        }
                    }
                });
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
        showEmergencyBottomSheetDialog();
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

                    mInteractor.animateCameraToMarker(mMap, marker);
                    Establishment establishment = mInteractor.getEstablishmentFromMarker(marker);
                    new Handler().postDelayed(() -> {
                        mContext.runOnUiThread(() -> {
                            showEstablishmentBottomDialog(establishment);
                            getEstablishmentRating(Long.valueOf(establishment.getCodUnidade()));
                        });
                    }, 500);
                    marker.showInfoWindow();
                    lastOpenned = marker;
                    return true;
                });
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
                                        mEstablishmentProgress.setVisibility(View.GONE);
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
                            mEstablishmentProgress.setVisibility(View.GONE);
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

    private void showEmergencyBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);

        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_emergency, null);
        EmergencyViews emergencyViews = new EmergencyViews();
        ButterKnife.bind(emergencyViews, dialogView);

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(R.color.default_dialog_background);

        emergencyViews.mainInfoCard.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        emergencyViews.samuLayout.setOnClickListener(v -> {
            mView.showSamuDialog((dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "33233323"));
                mContext.startActivity(intent);
            });
        });

        emergencyViews.firemanLayout.setOnClickListener(v -> {
            mView.showFiremanDialog((dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + 193));
                mContext.startActivity(intent);
            });
        });

        emergencyViews.policeLayout.setOnClickListener(v -> {
            mView.showPoliceDialog((dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + 190));
                mContext.startActivity(intent);
            });
        });

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight(emergencyViews.mainInfoCard.getMeasuredHeight() + 115);

        bottomSheetDialog.show();
    }

    private void showEstablishmentBottomDialog(Establishment establishment) {
        if (establishment == null) {
            return;
        }
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);

        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_marker, null);
        MarkerViews bottomViews = new MarkerViews();
        ButterKnife.bind(bottomViews, dialogView);

        mRatingView = bottomViews.ratingView;
        mRatingView.setIndicator(true);
        mEstablishmentProgress = bottomViews.establishmentProgress;
        mEstablishmentProgress.setVisibility(View.VISIBLE);

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

        if (mInteractor.isEstablishmentLiked(Long.valueOf(establishment.getCodUnidade()))) {
            bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
        }

        isLiked = mInteractor.isEstablishmentLiked(Long.valueOf(establishment.getCodUnidade()));
        bottomViews.likeImage.setOnClickListener(v -> {
            mEstablishmentProgress.setVisibility(View.VISIBLE);
            if (isLiked) {
                mSubscription.add(mInteractor.requestDisLikeEstablishment(establishment.getCodUnidade())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn(throwable -> null)
                        .subscribe(responseBodyResponse -> {
                            mEstablishmentProgress.setVisibility(View.GONE);
                            if (responseBodyResponse != null && responseBodyResponse.isSuccessful()) {
                                isLiked = false;
                                mInteractor.removeDislikedContentCode();
                                bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_empty));
                            } else {
                                mView.showToast(mContext.getString(R.string.http_error_generic));
                            }
                        }));
            } else {
                requestLikeEstablishment(Long.valueOf(establishment.getCodUnidade()), bottomViews.likeImage);
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

    private void updateMapLocation() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLocation.getLatitude(),
                        mLocation.getLongitude()),
                DEFAULT_ZOOM));
    }

    private void requestLikeEstablishment(Long codUnidade, ImageView likeImage) {
        mEstablishmentProgress.setVisibility(View.VISIBLE);
        mSubscription.add(mInteractor.requestLikeEstablishment(String.valueOf(codUnidade))
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(likeResponse -> {
                    mEstablishmentProgress.setVisibility(View.GONE);
                    if (likeResponse != null && likeResponse.isSuccessful()) {
                        isLiked = true;
                        mInteractor.addEstablishmentToLikedList(GenericUtil.getContentIdFromUrl(String.valueOf(mInteractor.getPostCode()),
                                likeResponse.headers().get("location")), codUnidade);
                        likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
                    } else {
                        mView.showToast(mContext.getString(R.string.http_error_generic));
                    }
                }));
    }

    private void requestEstablishmentRating(Long codUnidade) {
        mEstablishmentProgress.setVisibility(View.VISIBLE);
        mSubscription.add(mInteractor.requestGetEstablishmentRatingPost(codUnidade)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(listResponse -> {
                    if (listResponse != null && listResponse.isSuccessful()) {
                        if (listResponse.body() != null && listResponse.body().size() > 0) {
                            if (listResponse.body().get(0).getConteudos().size() > 0) {
                                mInteractor.requestEstablishmentRating(codUnidade)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .onErrorReturn(throwable -> null)
                                        .subscribe(responseBodyResponse -> {
                                            mEstablishmentProgress.setVisibility(View.GONE);
                                            if (responseBodyResponse == null) {
                                                mView.showToast(mContext.getString(R.string.error_get_establishment_review));
                                            } else {
                                                if (responseBodyResponse.isSuccessful()) {
                                                    mRatingView.setRating(responseBodyResponse.body().getMedia());
                                                }
                                            }
                                        });
                            } else {
                                mEstablishmentProgress.setVisibility(View.GONE);
                            }
                        } else {
                            mEstablishmentProgress.setVisibility(View.GONE);
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

    private void requestEstablishments(int pagination) {
        mLastObservable = mInteractor.requestEstablishmentsByLocation(mLocation, pagination);
        mLastObserver = requestEstablishmentsObserver;
        mSubscription.add(mInteractor.requestEstablishmentsByLocation(mLocation, pagination)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(requestEstablishmentsObserver));
    }

    private void showMapPins(List<Establishment> establishmentList) {
        for (Establishment establishment : establishmentList) {
            mInteractor.drawEstablishment(mMap, establishment);
        }
    }

    private void showGoToAddressDialog(Double latitude, Double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.location_dialog_title);
        builder.setMessage(mContext.getString(R.string.location_dialog_message));
        builder.setPositiveButton(R.string.location_dialog_positive, (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude));
            mView.goToActivity(intent);
        });
        builder.setNegativeButton(R.string.location_dialog_negative, (dialogInterface, i) -> {
        });
        builder.create().show();
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
        builder.setNegativeButton(R.string.call_dialog_negative, (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    private List<Establishment> mFilteredEstablishmentList = new ArrayList<>();
    private Observer<Response<List<Establishment>>> requestEstablishmentsObserver = new Observer<Response<List<Establishment>>>() {
        @Override
        public void onCompleted() {
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
                mEstablishmentList.addAll(listResponse.body());
                mFilteredEstablishmentList.addAll(listResponse.body());
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

    @Override
    public void onNext(String uf) {
        mView.setUfSpinnerSelection(mUfList.indexOf(uf));
    }

    class EmergencyViews {
        @Bind(R.id.main_info_card)
        LinearLayout mainInfoCard;
        @Bind(R.id.bottom_sheet)
        NestedScrollView bottomSheet;
        @Bind(R.id.samu_layout)
        LinearLayout samuLayout;
        @Bind(R.id.fireman_layout)
        LinearLayout firemanLayout;
        @Bind(R.id.police_layout)
        LinearLayout policeLayout;
    }

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
}