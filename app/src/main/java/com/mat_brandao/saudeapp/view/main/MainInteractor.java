package com.mat_brandao.saudeapp.view.main;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.util.OnLocationFound;

public interface MainInteractor {
    boolean hasGps();

    boolean isGpsOn();

    void requestMyLocation(OnLocationFound listener);
}