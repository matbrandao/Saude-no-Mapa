package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.content.Context;

public class FavEstablishmentPresenterImpl implements FavEstablishmentPresenter {

    private FavEstablishmentInteractorImpl mInteractor;
    private Context mContext;
    private FavEstablishmentView mView;

    public FavEstablishmentPresenterImpl(FavEstablishmentView view, Context context) {
        mInteractor = new FavEstablishmentInteractorImpl(context);
        mContext = context;
        mView = view;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onRetryClicked() {

    }
}