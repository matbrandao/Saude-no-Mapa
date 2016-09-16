package com.mat_brandao.saudeapp.view.establishment;

import android.content.DialogInterface;
import android.view.View;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface EstablishmentView extends BaseView.BaseProgressView {
    void toggleFabButton(boolean enabled);

    void showGpsDialog(DialogInterface.OnClickListener onAcceptListener);

    void startGpsIntent();

    double getMapContainerHeight();

    void setProgressFabVisibility(int visibility);
}