package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Conteudo;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.view.group.GroupActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mat_brandao.saudeapp.view.establishment.EstablishmentPresenterImpl.ESTABLISHMENT_INTENT_KEY;


public class FavEstablishmentPresenterImpl implements FavEstablishmentPresenter, GenericObjectClickListener<Establishment> {
    private static final String TAG = "FavEstablishmentPresent";

    private FavEstablishmentInteractorImpl mInteractor;
    private Context mContext;
    private FavEstablishmentView mView;

    private List<Long> mEstablishmentCodeList = new ArrayList<>();
    private List<Establishment> mEstablishmentList = new ArrayList<>();

    private Observable mLastObservable;
    private Observer mLastObserver;
    private int mAdapterCountAfterFetching;

    private CompositeSubscription mSubscription = new CompositeSubscription();
    private boolean isLiked;
    private ProgressBar mEstablishmentProgress;
    private SimpleRatingBar mRatingView;

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
        showProgressBar();
        mSubscription.add(mLastObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mLastObserver));
    }

    public FavEstablishmentPresenterImpl(FavEstablishmentView view, Context context) {
        mInteractor = new FavEstablishmentInteractorImpl(context);
        mContext = context;
        mView = view;

        requestFavEstablishments();
    }

    private void requestFavEstablishments() {
        showProgressBar();
        mInteractor.clearLikedEstablishments();
        mEstablishmentList.clear();
        mEstablishmentCodeList.clear();
        mLastObservable = mInteractor.requestGetUserPosts();
        mLastObserver = postResponseObserver;
        mSubscription.add(mInteractor.requestGetUserPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postResponseObserver));
    }

    @Override
    public void onItemClick(Establishment establishment) {
        showEstablishmentBottomDialog(establishment);
        requestEstablishmentRating(Long.valueOf(establishment.getCodUnidade()));
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
//        mEstablishmentProgress.setVisibility(View.VISIBLE);

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

        isLiked = true;
        bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
        bottomViews.likeImage.setOnClickListener(v -> {
            mEstablishmentProgress.setVisibility(View.VISIBLE);
            if (isLiked) {
                mSubscription.add(mInteractor.requestDislikeEstablishment(Long.valueOf(establishment.getCodUnidade()))
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
            if (mInteractor.getLikedEstablishmentCount() < mAdapterCountAfterFetching) {
                requestFavEstablishments();
            }
        });

        bottomSheetDialog.show();
    }

    private void requestLikeEstablishment(Long codUnidade, ImageView likeImage) {
        mEstablishmentProgress.setVisibility(View.VISIBLE);
        mSubscription.add(mInteractor.requestLikeEstablishment(codUnidade)
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

    private void showList() {
        mView.setProgressLayoutVisibility(View.GONE);
        mView.setEmptyViewVisibility(View.GONE);
    }

    private void showProgressBar() {
        mView.setProgressLayoutVisibility(View.VISIBLE);
        mView.setEmptyViewVisibility(View.GONE);
    }

    private void showEmptyView() {
        mView.setProgressLayoutVisibility(View.GONE);
        mView.setEmptyViewVisibility(View.VISIBLE);
    }

    private Observer<Response<List<PostResponse>>> postResponseObserver = new Observer<Response<List<PostResponse>>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof UnknownHostException) {
                mView.setProgressLayoutVisibility(View.GONE);
                mView.showNoConnectionSnackBar();
            }
        }

        @Override
        public void onNext(Response<List<PostResponse>> listResponse) {
            if (listResponse.isSuccessful()) {
                if (listResponse.body() != null && listResponse.body().size() > 0) {
                    mInteractor.saveUserLikePostCode(listResponse.body().get(0).getCodPostagem());
                    List<Long> codConteudoList = new ArrayList<>();
                    for (Conteudo conteudo : listResponse.body().get(0).getConteudos()) {
                        codConteudoList.add(conteudo.getCodConteudoPostagem());
                    }
                    mLastObservable = Observable.from(codConteudoList)
                            .flatMap(codConteudo -> mInteractor.requestGetPostContent(codConteudo));
                    mLastObserver = postContentObserver;

                    mSubscription.add(Observable.from(codConteudoList)
                            .flatMap(codConteudo -> mInteractor.requestGetPostContent(codConteudo))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(postContentObserver));
                } else {
                    showEmptyView();
                }
            } else {
                mView.showToast(mContext.getString(R.string.http_error_generic));
            }
        }
    };

    private Observer<Response<PostContent>> postContentObserver = new Observer<Response<PostContent>>() {
        @Override
        public void onCompleted() {
            mLastObservable = Observable.from(mEstablishmentCodeList)
                    .flatMap(establishmentCode -> mInteractor.requestGetEstablishment(establishmentCode));
            mLastObserver = establishmentObserver;

            mSubscription.add(Observable.from(mEstablishmentCodeList)
                    .flatMap(establishmentCode -> mInteractor.requestGetEstablishment(establishmentCode))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(establishmentObserver));
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof UnknownHostException) {
                mView.setProgressLayoutVisibility(View.GONE);
                mView.showNoConnectionSnackBar();
            }
        }

        @Override
        public void onNext(Response<PostContent> postContentResponse) {
            if (postContentResponse.isSuccessful()) {
                mEstablishmentCodeList.add(GenericUtil.getNumbersFromString(postContentResponse.body().getJSON()));
                mInteractor.addEstablishmentToLikedList(postContentResponse.body().getCodConteudoPost(),
                        GenericUtil.getNumbersFromString(postContentResponse.body().getJSON()));
            } else {
                mView.showToast(mContext.getString(R.string.http_error_generic));
            }
        }
    };

    private Observer<Response<List<Establishment>>> establishmentObserver = new Observer<Response<List<Establishment>>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof UnknownHostException) {
                mView.setProgressLayoutVisibility(View.GONE);
                mView.showNoConnectionSnackBar();
            }
        }

        @Override
        public void onNext(Response<List<Establishment>> listResponse) {
            if (listResponse.isSuccessful()) {
                if (listResponse.body() != null && listResponse.body().size() > 0) {
                    mEstablishmentList.add(listResponse.body().get(0));
                }
            } else {
                mView.showToast(mContext.getString(R.string.http_error_generic));
            }
            if (mEstablishmentList.size() > 0) {
                showList();
                mAdapterCountAfterFetching = mEstablishmentList.size();
                mView.setRecyclerAdapter(new FavEstablishmentAdapter(mContext, mEstablishmentList, FavEstablishmentPresenterImpl.this));
            } else {
                showEmptyView();
            }
        }
    };

    class MarkerViews {
        @Bind(R.id.group_image)
        ImageView groupImage;
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
        @Bind(R.id.establishment_progress)
        ProgressBar establishmentProgress;
        @Bind(R.id.rating_view)
        SimpleRatingBar ratingView;
    }
}