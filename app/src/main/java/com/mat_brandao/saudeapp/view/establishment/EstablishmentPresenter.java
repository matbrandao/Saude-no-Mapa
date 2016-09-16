package com.mat_brandao.saudeapp.view.establishment;

import com.mat_brandao.saudeapp.view.base.BasePresenter;

public interface EstablishmentPresenter extends BasePresenter {
    void onGpsTurnedOn();

    void onGpsTurnedOff();

    void onFilterFabClick();
}