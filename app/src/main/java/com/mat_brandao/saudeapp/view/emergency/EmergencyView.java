package com.mat_brandao.saudeapp.view.emergency;

import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import com.mat_brandao.saudeapp.view.base.BaseView;

import rx.Observable;

public interface EmergencyView extends BaseView.BaseProgressView {
    void showGpsDialog(DialogInterface.OnClickListener onAcceptListener);

    void startGpsIntent();

    void toggleFabButton(boolean enabled);

    double getMapContainerHeight();

    void setProgressFabVisibility(int visibility);

    Observable<CharSequence> registerSearchEditTextObserver();

    Observable<Integer> registerUfSpinnerObserver();

    void dismissKeyboard();

    void setUfSpinnerAdapter(ArrayAdapter adapter);

    void setUfSpinnerSelection(int selection);
}