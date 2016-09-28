package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import android.content.Context;

public class FavRemedyPresenterImpl implements FavRemedyPresenter {

    private FavRemedyInteractorImpl mInteractor;
    private Context mContext;
    private FavRemedyView mView;

    public FavRemedyPresenterImpl(FavRemedyView view, Context context) {
        mInteractor = new FavRemedyInteractorImpl(context);
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
        // TODO: 27/09/2016
    }
}