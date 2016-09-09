package com.mat_brandao.saudeapp.view.main;

import android.content.Context;
import android.location.Location;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public interface MainInteractor {
    boolean hasGps();

    boolean isGpsOn();

    void requestMyLocation(OnLocationFound listener);

    Observable<Response<List<Establishment>>> requestEstablishmentsByLocation(Location location, int pagination);
}