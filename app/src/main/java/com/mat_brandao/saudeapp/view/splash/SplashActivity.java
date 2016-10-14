package com.mat_brandao.saudeapp.view.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashView {
    @Bind(R.id.login_background_image)
    ImageView loginBackgroundImage;
    @Bind(R.id.logo_image)
    ImageView logoImage;
    @Bind(R.id.contest_text)
    TextView contestText;

    private SplashPresenterImpl mPresenter;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.shouldAnimate = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }

        mPresenter = new SplashPresenterImpl(this, this);
    }

    @SuppressLint("NewApi")
    private void setupWindowAnimations() {
        Transition slideLeft = TransitionInflater.from(this).inflateTransition(android.R.transition.fade);
        Transition slideRight = TransitionInflater.from(this).inflateTransition(android.R.transition.fade);
        getWindow().setExitTransition(slideLeft);
        getWindow().setEnterTransition(slideRight);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#793A37")));
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
        new Handler().postDelayed(() -> {
            runOnUiThread(this::supportFinishAfterTransition);
        }, 2000);
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

    @Override
    public void animateLogoImage(Runnable endRunnable) {
        logoImage.animate()
                .alpha(1)
                .setDuration(2000)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(endRunnable);

        loginBackgroundImage.animate()
                .scaleXBy(0.5f)
                .scaleYBy(0.5f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(2000);
    }
}