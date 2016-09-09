package com.mat_brandao.saudeapp.view.splash;

import android.content.Context;
import android.text.TextUtils;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.view.login.LoginActivity;
import com.mat_brandao.saudeapp.view.main.MainActivity;

public class SplashPresenterImpl implements SplashPresenter {

    private User mUser;
    private SplashInteractorImpl mInteractor;
    private Context mContext;
    private SplashView mView;

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

    public SplashPresenterImpl(SplashView view, Context context) {
        mInteractor = new SplashInteractorImpl(context);
        mContext = context;
        mView = view;

        mUser = mInteractor.getUser();
        if (mUser == null || TextUtils.isEmpty(mUser.getAppToken())) {
            mView.goToActivity(LoginActivity.class);
            mView.finishActivity();
        } else {
            mView.goToActivity(MainActivity.class);
            mView.finishActivity();
        }
    }
}