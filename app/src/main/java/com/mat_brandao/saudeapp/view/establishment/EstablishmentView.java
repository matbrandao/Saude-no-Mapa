package com.mat_brandao.saudeapp.view.establishment;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;

import com.mat_brandao.saudeapp.view.base.BaseView;

import rx.Observable;

public interface EstablishmentView extends BaseView.BaseProgressView {
    void toggleFabButton(boolean enabled);

    void showGpsDialog(DialogInterface.OnClickListener onAcceptListener);

    void startGpsIntent();

    double getMapContainerHeight();

    void setProgressFabVisibility(int visibility);

    void setFabVisibility(int visibility);

    void setUfSpinnerAdapter(ArrayAdapter<String> mAdapter);

    void setUfSpinnerSelection(int selection);

    Observable<CharSequence> registerSearchEditTextObserver();

    Observable<Integer> registerUfSpinnerObserver();
}