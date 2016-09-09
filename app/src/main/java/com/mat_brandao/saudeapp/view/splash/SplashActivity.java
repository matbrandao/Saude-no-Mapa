package com.mat_brandao.saudeapp.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private SplashPresenterImpl mPresenter;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mPresenter = new SplashPresenterImpl(this, this);
    }

    @Override
    public void showToast(String text) {
        super.showToast(text);
    }

    @Override
    public void goToActivity(Class<?> activity) {
        super.goToActivity(activity);
    }

    @Override
    public void goToActivity(Intent intent) {
        super.goToActivity(intent);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showNoConnectionSnackBar() {
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

}