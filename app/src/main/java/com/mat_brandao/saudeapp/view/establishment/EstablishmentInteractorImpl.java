package com.mat_brandao.saudeapp.view.establishment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit2.Response;
import rx.Observable;

public class EstablishmentInteractorImpl implements EstablishmentInteractor {
    private static final Double SEARCH_RADIUS = 10.0;

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private User mUser;
    private HashMap<Establishment, Marker> mDeviceMarkerHash;


    public EstablishmentInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
        mDeviceMarkerHash = new HashMap<>();
    }

    @Override
    public boolean hasGps() {
        PackageManager packageManager = mContext.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    @Override
    public boolean isGpsOn() {
        String provider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return !TextUtils.isEmpty(provider);
    }

    @Override
    public void requestMyLocation(OnLocationFound listener) {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(mContext);
        locationProvider.getLastKnownLocation()
                .retry(10)
                .subscribe(listener::onLocationFound);
    }

    @Override
    public Observable<Response<List<Establishment>>> requestEstablishmentsByLocation(Location location, int pagination) {
        return RestClient.getHeader(mUser.getAppToken())
                .getEstablishmentsByGeoLocation(location.getLatitude(),
                        location.getLongitude(), SEARCH_RADIUS, pagination);
    }

    @Override
    public void clearMarkers(GoogleMap map) {
        if (map != null) {
            if (mDeviceMarkerHash.size() > 0) {
                for (Marker marker : mDeviceMarkerHash.values()) {
                    marker.remove();
                }
            }
        }
    }

    @Override
    public void drawEstablishment(GoogleMap map, Establishment establishment) {
        if (map != null) {
            mDeviceMarkerHash.put(establishment, map
                    .addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_hospital))
                            .position(new LatLng(establishment.getLatitude(), establishment.getLongitude()))
                            .title(GenericUtil.capitalize(establishment.getNomeFantasia().toLowerCase()))));
        }
    }

    @Override
    public void animateCameraToAllEstablishments(GoogleMap mMap) {
        if (mMap != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : mDeviceMarkerHash.values()) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

    @Override
    public void animateMarketToTop(GoogleMap map, Marker marker, double mapHeight) {
        double dpPerdegree = 256.0 * Math.pow(2, 15) / 170.0;
        double screen_height_30p = 25.0 * mapHeight / 100.0;
        double degree_30p = screen_height_30p / dpPerdegree;
        LatLng latLng = marker.getPosition();
        LatLng centerlatlng = new LatLng(latLng.latitude - degree_30p, latLng.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerlatlng, 15), 500, null);
    }

    @Override
    public Establishment getEstablishmentFromMarker(Marker marker) {
        for (Map.Entry entry : mDeviceMarkerHash.entrySet()) {
            if (marker.equals(entry.getValue())) {
                return (Establishment) entry.getKey();
            }
        }
        return null;
    }

    @Override
    public String getFluxoClientelaText(String fluxoClientela) {
        if (fluxoClientela.toLowerCase().contains("espontânea") && fluxoClientela.toLowerCase().contains("referenciada")) {
            return "Atendimento espontâneo e referenciado";
        } else if (fluxoClientela.contains("espontânea") && !fluxoClientela.contains("referenciada")) {
            return "Apenas atendimento espontâneo";
        } else if (!fluxoClientela.contains("espontânea") && fluxoClientela.contains("referenciada")) {
            return "Apenas atendimento referenciado";
        }
        return "Indeterminado";
    }

    @Override
    public String getAddressText(String logradouro, String numero, String bairro, String cidade, String uf, String cep) {
        return logradouro + ", Número: " + numero + ". " + GenericUtil.capitalize(bairro) + ", " +
                GenericUtil.capitalize(cidade) + ", " + uf + " - " + MaskUtil.mask("#####-###", cep);
    }

    @Override
    public String getServicesText(Establishment establishment) {
        String result = "";
        if (establishment.getTemAtendimentoUrgencia().equals("Sim")) {
            result += "Atendimento Urgencial - ";
        }
        if (establishment.getTemAtendimentoAmbulatorial().equals("Sim")) {
            result += "Atendimento Ambulatorial - ";
        }
        if (establishment.getTemCentroCirurgico().equals("Sim")) {
            result += "Centro Cirúrgico - ";
        }
        if (establishment.getTemObstetra().equals("Sim")) {
            result += "Obstetra - ";
        }
        if (establishment.getTemNeoNatal().equals("Sim")) {
            result += "Neo Natal - ";
        }
        if (establishment.getTemDialise().equals("Sim")) {
            result += "Diálise - ";
        }

        return result.substring(0, result.length() - 3);
    }
}