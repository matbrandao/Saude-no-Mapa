package com.mat_brandao.saudeapp.view.main;

import android.support.v4.app.Fragment;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface MainView extends BaseView.BaseProgressView {
    void showFragment(Fragment fragment);
}