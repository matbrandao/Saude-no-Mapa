package com.mat_brandao.saudeapp.view.remedy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class RemedyActivity extends BaseActivity implements RemedyView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.scan_barcode_fab)
    FloatingActionButton fab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    private RemedyPresenterImpl mPresenter;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remedy);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mPresenter = new RemedyPresenterImpl(this, this);
    }

    @OnClick(R.id.scan_barcode_fab)
    void scanBarcodeFabClick() {
        mPresenter.onScanBarcodeFabClick();
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
        super.showConnectionError(coordinatorLayout, view -> {
            mPresenter.onRetryClicked();
        });
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
    public void gotoActivityWithResult(Intent intent) {
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("data");
                mPresenter.onScanSuccess(contents);
                Timber.d("contents: " + contents);
            } else if (resultCode == RESULT_CANCELED) {
                Timber.d("RESULT_CANCELED");
            }
        }
    }
}