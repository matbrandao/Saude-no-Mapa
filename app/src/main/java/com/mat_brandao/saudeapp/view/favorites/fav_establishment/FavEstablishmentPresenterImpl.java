package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Conteudo;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


public class FavEstablishmentPresenterImpl implements FavEstablishmentPresenter, GenericObjectClickListener<Establishment> {
    private static final String TAG = "FavEstablishmentPresent";

    private FavEstablishmentInteractorImpl mInteractor;
    private Context mContext;
    private FavEstablishmentView mView;

    private List<Long> mEstablishmentCodeList = new ArrayList<>();
    private List<Establishment> mEstablishmentList = new ArrayList<>();

    private Observable mLastObservable;
    private Observer mLastObserver;

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

    public FavEstablishmentPresenterImpl(FavEstablishmentView view, Context context) {
        mInteractor = new FavEstablishmentInteractorImpl(context);
        mContext = context;
        mView = view;

        requestFavEstablishments();
    }

    private void requestFavEstablishments() {
        mLastObservable = mInteractor.requestGetUserPosts();
        mLastObserver = postResponseObserver;
        mSubscription.add(mInteractor.requestGetUserPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(postResponseObserver));
    }

    @Override
    public void onItemClick(Establishment establishment) {

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
            mLastObservable = Observable.from(mEstablishmentCodeList)
                    .flatMap(establishmentCode -> mInteractor.requestGetEstablishment(establishmentCode));
            mLastObserver = establishmentObserver;

            Observable.from(mEstablishmentCodeList)
                    .flatMap(establishmentCode -> mInteractor.requestGetEstablishment(establishmentCode))
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
                mEstablishmentCodeList.add(GenericUtil.getNumbersFromString(postContentResponse.body().getJSON()));
            } else {
                mView.showToast(mContext.getString(R.string.http_error_generic));
            }
        }
    };

    private Observer<Response<List<Establishment>>> establishmentObserver = new Observer<Response<List<Establishment>>>() {
        @Override
        public void onCompleted() {
            if (mEstablishmentList.size() > 0) {
                showList();
                mView.setRecyclerAdapter(new FavEstablishmentAdapter(mContext, mEstablishmentList, FavEstablishmentPresenterImpl.this));
            } else {
                showEmptyView();
            }
        }

        @Override
        public void onError(Throwable e) {
            mView.showNoConnectionSnackBar();
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
        }
    };
}