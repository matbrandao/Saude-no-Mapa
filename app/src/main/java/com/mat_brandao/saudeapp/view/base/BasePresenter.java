package com.mat_brandao.saudeapp.view.base;

/**
 * Created by Mateus Brandão on 04-Apr-16.
 */
public interface BasePresenter {
    void onResume();

    void onPause();

    void onDestroy();

    void onRetryClicked();
}
