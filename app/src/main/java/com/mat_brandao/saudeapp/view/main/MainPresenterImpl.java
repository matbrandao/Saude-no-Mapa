package com.mat_brandao.saudeapp.view.main;

import android.content.Context;

import com.mat_brandao.saudeapp.view.establishment.EstablishmentFragment;

public class MainPresenterImpl implements MainPresenter {

    private MainInteractorImpl mInteractor;
    private Context mContext;
    private MainView mView;

    public MainPresenterImpl(MainView view, Context context) {
        mInteractor = new MainInteractorImpl(context);
        mContext = context;
        mView = view;

        mView.showFragment(EstablishmentFragment.newInstance());
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