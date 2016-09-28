package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.mat_brandao.saudeapp.domain.model.Conteudo;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
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
        // TODO: 28/09/2016
    }

    public FavEstablishmentPresenterImpl(FavEstablishmentView view, Context context) {
        mInteractor = new FavEstablishmentInteractorImpl(context);
        mContext = context;
        mView = view;

        requestFavEstablishments();
    }

    private void requestFavEstablishments() {
        mSubscription.add(mInteractor.requestGetUserPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(listResponse -> {
                    if (listResponse != null && listResponse.isSuccessful()) {
                        if (listResponse.body() != null && listResponse.body().size() > 0) {
                            mInteractor.saveUserLikePostCode(listResponse.body().get(0).getCodPostagem());
                            List<Long> codConteudoList = new ArrayList<>();
                            for (Conteudo conteudo : listResponse.body().get(0).getConteudos()) {
                                codConteudoList.add(conteudo.getCodConteudoPostagem());
                            }
                            Observable.from(codConteudoList)
                                    .flatMap(codConteudo -> mInteractor.requestGetPostContent(codConteudo))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(postContentObserver);
                        } else {
                            // TODO: 28/09/2016 show error to user
                        }
                    } else {
                        // TODO: 28/09/2016 show error to user
                    }
                }));
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

    private Observer<Response<PostContent>> postContentObserver = new Observer<Response<PostContent>>() {
        @Override
        public void onCompleted() {
            Observable.from(mEstablishmentCodeList)
                    .flatMap(establishmentCode -> mInteractor.requestGetEstablishment(establishmentCode))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(establishmentObserver);
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(Response<PostContent> postContentResponse) {
            if (postContentResponse.isSuccessful()) {
                mEstablishmentCodeList.add(GenericUtil.getNumbersFromString(postContentResponse.body().getJSON()));
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

        }

        @Override
        public void onNext(Response<List<Establishment>> listResponse) {
            if (listResponse.isSuccessful()) {
                if (listResponse.body() != null && listResponse.body().size() > 0) {
                    mEstablishmentList.add(listResponse.body().get(0));
                }
            }
        }
    };
}