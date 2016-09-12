package com.mat_brandao.saudeapp.view.main;

import android.content.DialogInterface;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface MainView extends BaseView.BaseProgressView {
    void showGpsDialog(DialogInterface.OnClickListener onAcceptListener);

    void startGpsIntent();

    double getMapContainerHeight();
}