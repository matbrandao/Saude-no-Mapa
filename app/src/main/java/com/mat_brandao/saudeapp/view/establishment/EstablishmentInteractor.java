package com.mat_brandao.saudeapp.view.establishment;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public interface EstablishmentInteractor {
    boolean hasGps();

    boolean isGpsOn();

    void requestMyLocation(OnLocationFound listener);

    Observable<Response<List<Establishment>>> requestEstablishmentsByLocation(Location location, int pagination);

    void clearMarkers(GoogleMap map);

    void drawEstablishment(GoogleMap map, Establishment establishment);

    void animateCameraToAllEstablishments(GoogleMap mMap);

    void animateMarketToTop(GoogleMap map, Marker marker, double mapHeight);

    Establishment getEstablishmentFromMarker(Marker marker);

    String getFluxoClientelaText(String fluxoClientela);

    String getAddressText(String logradouro, String numero, String bairro, String cidade, String uf, String cep);

    String getServicesText(Establishment establishment);
}