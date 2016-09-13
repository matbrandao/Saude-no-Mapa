package com.mat_brandao.saudeapp.view.remedy;

import android.content.Intent;

import com.mat_brandao.saudeapp.view.base.BaseView;

import rx.Observable;

public interface RemedyView extends BaseView.BaseProgressView {
    void setProgressLayoutVisibility(int visibility);

    void setEmptyTextVisibility(int visibility);

    void setNoResultsTextVisibility(int visibility);

    Observable<CharSequence> registerSearchObservable();

    void gotoActivityWithResult(Intent intent);

    void dismissKeyboard();
}