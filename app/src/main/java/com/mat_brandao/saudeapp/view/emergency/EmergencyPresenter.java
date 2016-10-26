package com.mat_brandao.saudeapp.view.emergency;

import com.mat_brandao.saudeapp.view.base.BasePresenter;

public interface EmergencyPresenter extends BasePresenter {
    void onGpsTurnedOn();

    void onGpsTurnedOff();

    void onFilterFabClick();
}