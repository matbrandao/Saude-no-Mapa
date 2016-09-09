package com.mat_brandao.saudeapp.view.main;

import com.mat_brandao.saudeapp.view.base.BasePresenter;

public interface MainPresenter extends BasePresenter {
    void onGpsTurnedOn();

    void onGpsTurnedOff();
}