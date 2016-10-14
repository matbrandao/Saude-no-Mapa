package com.mat_brandao.saudeapp.view.splash;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface SplashView extends BaseView.BaseProgressView {
    void animateLogoImage(Runnable endRunnable);
}