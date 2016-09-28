package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import android.content.Context;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class FavRemedyPresenterImpl implements FavRemedyPresenter, GenericObjectClickListener<Remedy> {

    private FavRemedyInteractorImpl mInteractor;
    private Context mContext;
    private FavRemedyView mView;

    private Observable mLastObservable;
    private Observer mLastObserver;

    private List<Long> mRemedyCodeList = new ArrayList<>();
    private List<Remedy> mRemedyList = new ArrayList<>();

    private CompositeSubscription mSubscription = new CompositeSubscription();

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
        mLastObservable = mInteractor.requestGetUserPosts();
        mLastObserver = postResponseObserver;
        mSubscription.add(mInteractor.requestGetUserPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postResponseObserver));
    }

    @Override
    public void onItemClick(Remedy object) {

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
}