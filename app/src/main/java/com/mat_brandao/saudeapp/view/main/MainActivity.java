package com.mat_brandao.saudeapp.view.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.google.android.gms.maps.SupportMapFragment;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.map_container)
    LinearLayout mapContainer;

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

        mPresenter = new MainPresenterImpl(this, this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mPresenter);
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
    public void showGpsDialog(DialogInterface.OnClickListener onAcceptListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.dialog_gps_title);
        alertBuilder.setMessage(R.string.dialog_gps_message);
        alertBuilder.setPositiveButton("Ligar GPS", onAcceptListener);
        alertBuilder.setNegativeButton("Continuar", (dialog, which) -> {
            mPresenter.onGpsTurnedOff();
        });
        alertBuilder.create().show();
    }

    @Override
    public void startGpsIntent() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 202);
    }

    @Override
    public double getMapContainerHeight() {
        return (double) mapContainer.getHeight();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!TextUtils.isEmpty(provider)) {
            mPresenter.onGpsTurnedOn();
        } else {
            mPresenter.onGpsTurnedOff();
        }
    }
}