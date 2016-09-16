package com.mat_brandao.saudeapp.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private MainPresenterImpl mPresenter;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mPresenter = new MainPresenterImpl(this, this);
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
        // TODO: 16/09/2016  
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @Override
    public void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_content, fragment)
                .commit();
    }
}