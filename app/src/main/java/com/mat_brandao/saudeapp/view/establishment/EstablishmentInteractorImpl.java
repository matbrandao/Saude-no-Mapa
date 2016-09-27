package com.mat_brandao.saudeapp.view.establishment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Autor;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.Post;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.PostType;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.mat_brandao.saudeapp.domain.util.MetaModelConstants;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import okhttp3.ResponseBody;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit2.Response;
import rx.Observable;
import timber.log.Timber;

public class EstablishmentInteractorImpl implements EstablishmentInteractor {
    private static final Double SEARCH_RADIUS = 10.0;

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private User mUser;
    private HashMap<Establishment, Marker> mDeviceMarkerHash;
//    private List<Long> mLikedEstablishmentCodes;
    private HashMap<Long, Long> mLikedEstablishment;

    public EstablishmentInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
        mDeviceMarkerHash = new HashMap<>();
        mLikedEstablishment = new HashMap<>();
//        mLikedEstablishmentCodes = new ArrayList<>();
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
        return RestClient.getHeader(mUser.getAppToken(), null)
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
            String categoria = establishment.getCategoriaUnidade().toLowerCase();
            mDeviceMarkerHash.put(establishment, map
                    .addMarker(new MarkerOptions()
                            .icon(getBitmapDescriptorForCategory(categoria))
                            .position(new LatLng(establishment.getLatitude(), establishment.getLongitude()))
                            .title(GenericUtil.capitalize(establishment.getNomeFantasia().toLowerCase()))));
        }
    }

    private BitmapDescriptor getBitmapDescriptorForCategory(String categoria) {
        BitmapDescriptor mapIcon;
        if (categoria.contains("consultório")) {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_consultorio);
        } else if (categoria.contains("clínica")) {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_clinica);
        } else if (categoria.contains("laboratório")) {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_laboratorio);
        } else if (categoria.contains("urgência")) {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_urgente);
        } else if (categoria.contains("hospital")) {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital);
        } else if (categoria.contains("atendimento domiciliar")) {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_domiciliar);
        } else if (categoria.contains("posto de saúde")) {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_urgente);
        } else if (categoria.contains("samu")) {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_samu);
        } else {
            mapIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_urgente);
        }

        return mapIcon;
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
        double screen_height_30p = 15.0 * mapHeight / 100.0;
        double degree_30p = screen_height_30p / dpPerdegree;
        LatLng latLng = marker.getPosition();
        LatLng centerlatlng = new LatLng(latLng.latitude - degree_30p, latLng.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerlatlng, 15), 500, null);
    }

    @Nullable
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
        String address = logradouro + ", Número: " + numero + ". " + GenericUtil.capitalize(bairro) + ", " +
                GenericUtil.capitalize(cidade) + ", " + uf + " - " + MaskUtil.mask("#####-###", cep);
        if (address.contains("null")) {
            return null;
        } else {
            return address;
        }
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

//    @Override
//    public boolean isEstablishmentLiked(Long codUnidade) {
//        if (mLikedEstablishmentCodes.isEmpty()) {
//            return false;
//        } else {
//            for (Long mLikedEstablishmentCode : mLikedEstablishmentCodes) {
//                if (mLikedEstablishmentCode.equals(codUnidade)) {
//                    return true;
//                }
//            }
//            return false;
//        }
//    }

    @Override
    public boolean isEstablishmentLiked(Long codUnidade) {
        if (mLikedEstablishment.isEmpty()) {
            return false;
        } else {
            for (Long code : mLikedEstablishment.values()) {
                if (code.equals(codUnidade)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Observable<Response<ResponseBody>> requestLikeEstablishment(Long postCode, String codUnidade) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .likeEstablishment(postCode, assemblePostContent(codUnidade));
    }

    @Override
    public Observable<Response<ResponseBody>> requestLikeEstablishment(String codUnidade) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .likeEstablishment(mUser.getLikePostCode(), assemblePostContent(codUnidade));
    }

    @Override
    public Observable<Response<ResponseBody>> requestDisLikeEstablishment(String codUnidade) {
        Long codUnidadeLong  = Long.valueOf(codUnidade);
        Long contentCode = 0L;
        for (Map.Entry<Long, Long> longLongEntry : mLikedEstablishment.entrySet()) {
            if (longLongEntry.getValue().equals(codUnidadeLong)) {
                contentCode = longLongEntry.getKey();
            }
        }

        return RestClient.getHeader(mUser.getAppToken(), null)
                .deleteContent(mUser.getLikePostCode(), contentCode);
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateLikePost() {
        return RestClient.getHeader(mUser.getAppToken(), mContext.getString(R.string.app_id))
                .createLikePost(assemblePost());
    }

    @Override
    public boolean hasLikePostCode() {
        return mUser.getLikePostCode() != null;
    }

    @Override
    public void saveUserLikePostCode(Long likePostCode) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser.setLikePostCode(likePostCode);
        });
    }

    @Override
    public Observable<Response<List<PostResponse>>> requestGetUserPosts() {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getLikePosts(Long.valueOf(mContext.getString(R.string.app_id)), mUser.getId(),
                        Long.valueOf(String.valueOf(MetaModelConstants.COD_POST_ESTABLISHMENT)));
    }

    @Override
    public Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem) {
        return RestClient
                .getHeader(mUser.getAppToken(), null)
                .getPostContent(mUser.getLikePostCode(), codConteudoPostagem);
    }

    @Override
    public void addEstablishmentToLikedList(Long code, Long establishmentCode) {
        if (code == null) {
            mLikedEstablishment.put(mUser.getLikePostCode(), establishmentCode);
        } else {
            mLikedEstablishment.put(code, establishmentCode);
        }
    }

    private Post assemblePost() {
        return new Post(new Autor(mUser.getId()), MetaModelConstants.COD_POST_ESTABLISHMENT,
                new PostType(MetaModelConstants.COD_OBJECT_ESTABLISHMENT));
    }

    private PostContent assemblePostContent(String codUnidade) {
        PostContent postContent = new PostContent();
        postContent.setJSON("{codEstabelecimento:" + codUnidade + "}");
        postContent.setTexto("");
        postContent.setLinks(null);
        postContent.setValor(0L);
        return postContent;
    }

    @Override
    public String getPostCode() {
        return String.valueOf(mUser.getLikePostCode());
    }
}