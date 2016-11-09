package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Conteudo;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.view.favorites.fav_establishment.FavEstablishmentAdapter;
import com.mat_brandao.saudeapp.view.favorites.fav_establishment.FavEstablishmentPresenterImpl;
import com.mat_brandao.saudeapp.view.remedy.RemedyPresenterImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class FavRemedyPresenterImpl implements FavRemedyPresenter, GenericObjectClickListener<Remedy> {

    private FavRemedyInteractorImpl mInteractor;
    private Context mContext;
    private FavRemedyView mView;

    private Observable mLastObservable;
    private Observer mLastObserver;

    private List<Long> mRemedyCodeList = new ArrayList<>();
    private List<Remedy> mRemedyList = new ArrayList<>();

    private CompositeSubscription mSubscription = new CompositeSubscription();

    private int mAdapterCountAfterFetching;

    private boolean isLiked;
    private ProgressBar mRemedyProgress;

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
        mLastObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mLastObserver);
    }

    public FavRemedyPresenterImpl(FavRemedyView view, Context context) {
        mInteractor = new FavRemedyInteractorImpl(context);
        mContext = context;
        mView = view;

        requestFavRemedies();
    }

    private void requestFavRemedies() {
        showProgressBar();
        mInteractor.clearLikedRemedies();
        mRemedyList.clear();
        mRemedyCodeList.clear();
        mLastObservable = mInteractor.requestGetUserPosts();
        mLastObserver = postResponseObserver;
        mSubscription.add(mInteractor.requestGetUserPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postResponseObserver));
    }

    @Override
    public void onItemClick(Remedy remedy) {
        showRemedyBottomDialog(remedy);
    }

    private void showRemedyBottomDialog(Remedy remedy) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_remedy, null);
        BottomViews bottomViews = new BottomViews();
        ButterKnife.bind(bottomViews, dialogView);

        mRemedyProgress = bottomViews.remedyProgress;

        bottomViews.priceText.setText("R$ " + String.format(Locale.getDefault(), "%.2f", remedy.getPmc0()) + " a " +
                "R$ " + String.format(Locale.getDefault(), "%.2f", remedy.getPmc20()));
        bottomViews.establishmentTitle.setText(GenericUtil.capitalize(remedy.getProduto().toLowerCase()));
        bottomViews.apresentacaoText.setText(GenericUtil.capitalize(remedy.getApresentacao().toLowerCase()));
        bottomViews.classeTerapeuticaText.setText(GenericUtil.capitalize(remedy.getClasseTerapeutica().toLowerCase()));
        bottomViews.laboratorioText.setText(GenericUtil.capitalize(remedy.getLaboratorio().toLowerCase()));
        bottomViews.principioAtivoText.setText(GenericUtil.capitalize(remedy.getPrincipioAtivo().toLowerCase()));
        bottomViews.registroText.setText(remedy.getRegistro());
        bottomViews.cnpjText.setText(remedy.getCnpj());

        if (remedy.getRestricao().equals("Sim")) {
            bottomViews.possuiRestricaoText.setVisibility(View.VISIBLE);
        }

        bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
        isLiked = true;
        bottomViews.likeImage.setOnClickListener(v -> {
            mRemedyProgress.setVisibility(View.VISIBLE);
            if (isLiked) {
                mSubscription.add(mInteractor.requestDisLikeRemedy(Long.valueOf(remedy.getCodBarraEan()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn(throwable -> null)
                        .subscribe(responseBodyResponse -> {
                            mRemedyProgress.setVisibility(View.GONE);
                            if (responseBodyResponse != null && responseBodyResponse.isSuccessful()) {
                                isLiked = false;
                                mInteractor.removeDislikedContentCode();
                                bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_empty));
                            } else {
                                mView.showToast(mContext.getString(R.string.http_error_generic));
                            }
                        }));
            } else {
                requestLikeRemedies(Long.valueOf(remedy.getCodBarraEan()), bottomViews.likeImage);
            }
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(R.color.default_dialog_background);

        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight(mView.getRootHeight() - 200);

        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (mInteractor.getLikedRemedyCount() < mAdapterCountAfterFetching) {
                requestFavRemedies();
            }
        });

        bottomSheetDialog.show();
    }

    private void requestLikeRemedies(Long codRemedy, ImageView likeImage) {
        mSubscription.add(mInteractor.requestLikeRemedy(codRemedy)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(likeResponse -> {
                    mRemedyProgress.setVisibility(View.GONE);
                    if (likeResponse != null && likeResponse.isSuccessful()) {
                        isLiked = true;
                        mInteractor.addRemedyToLikedList(GenericUtil.getContentIdFromUrl(String.valueOf(mInteractor.getPostCode()),
                                likeResponse.headers().get("location")), codRemedy);
                        likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
                    } else {
                        mView.showToast(mContext.getString(R.string.http_error_generic));
                    }
                }));
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
            mView.showNoConnectionSnackBar();
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

                    Observable.from(codConteudoList)
                            .flatMap(codConteudo -> mInteractor.requestGetPostContent(codConteudo))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(postContentObserver);
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
            mLastObservable = Observable.from(mRemedyCodeList)
                    .flatMap(remedyCode -> mInteractor.requestGetRemedy(remedyCode));
            mLastObserver = establishmentObserver;

            Observable.from(mRemedyCodeList)
                    .flatMap(remedyCode -> mInteractor.requestGetRemedy(remedyCode))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(establishmentObserver);
        }

        @Override
        public void onError(Throwable e) {
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<PostContent> postContentResponse) {
            if (postContentResponse.isSuccessful()) {
                mRemedyCodeList.add(GenericUtil.getNumbersFromString(postContentResponse.body().getJSON()));
                mInteractor.addRemedyToLikedList(postContentResponse.body().getCodConteudoPost(),
                        GenericUtil.getNumbersFromString(postContentResponse.body().getJSON()));
            } else {
                mView.showToast(mContext.getString(R.string.http_error_generic));
            }
        }
    };

    private Observer<Response<List<Remedy>>> establishmentObserver = new Observer<Response<List<Remedy>>>() {
        @Override
        public void onCompleted() {
            if (mRemedyList.size() > 0) {
                showList();
                mAdapterCountAfterFetching = mRemedyList.size();
                mView.setRecyclerAdapter(new FavRemedyAdapter(mContext, mRemedyList, FavRemedyPresenterImpl.this));
            } else {
                showEmptyView();
            }
        }

        @Override
        public void onError(Throwable e) {
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<List<Remedy>> listResponse) {
            if (listResponse.isSuccessful()) {
                if (listResponse.body() != null && listResponse.body().size() > 0) {
                    mRemedyList.add(listResponse.body().get(0));
                }
            } else {
                mView.showToast(mContext.getString(R.string.http_error_generic));
            }
        }
    };

    class BottomViews {
        @Bind(R.id.establishment_title)
        TextView establishmentTitle;
        @Bind(R.id.price_text)
        TextView priceText;
        @Bind(R.id.principio_ativo_text)
        TextView principioAtivoText;
        @Bind(R.id.classe_terapeutica_text)
        TextView classeTerapeuticaText;
        @Bind(R.id.apresentacao_text)
        TextView apresentacaoText;
        @Bind(R.id.laboratorio_text)
        TextView laboratorioText;
        @Bind(R.id.cnpj_text)
        TextView cnpjText;
        @Bind(R.id.registro_text)
        TextView registroText;
        @Bind(R.id.possui_restricao_text)
        TextView possuiRestricaoText;
        @Bind(R.id.bottom_sheet)
        NestedScrollView bottomSheet;
        @Bind(R.id.remedy_like_image)
        ImageView likeImage;
        @Bind(R.id.establishment_progress)
        ProgressBar remedyProgress;
    }
}