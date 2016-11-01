package com.mat_brandao.saudeapp.view.emergency;

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
import com.mat_brandao.saudeapp.domain.model.Rating;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.mat_brandao.saudeapp.domain.util.MetaModelConstants;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.mat_brandao.saudeapp.domain.util.StringListener;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import okhttp3.ResponseBody;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.mat_brandao.saudeapp.R.id.map;

public class EmergencyInteractorImpl implements EmergencyInteractor {

    private static final Double SEARCH_RADIUS = 10.0;

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private User mUser;
    private HashMap<Establishment, Marker> mDeviceMarkerHash;
    private HashMap<Long, Long> mLikedEstablishment;
    private HashMap<Long, Long> mRatedEstablishment;
    private HashMap<Long, Long> mContentCodeEstablishment;
    private Long mDislikedContentCode;

    public EmergencyInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
        mDeviceMarkerHash = new HashMap<>();
        mLikedEstablishment = new HashMap<>();
        mRatedEstablishment = new HashMap<>();
        mContentCodeEstablishment = new HashMap<>();
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
                .getEmergencyEstablishmentsByGeoLocation(location.getLatitude(),
                        location.getLongitude(), SEARCH_RADIUS, pagination, "URGÊNCIA");
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
                .createContent(postCode, assemblePostContent(codUnidade));
    }

    @Override
    public Observable<Response<ResponseBody>> requestLikeEstablishment(String codUnidade) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .createContent(mUser.getEstablishmentLikePost(), assemblePostContent(codUnidade));
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

        mDislikedContentCode = contentCode;

        return RestClient.getHeader(mUser.getAppToken(), null)
                .deleteContent(mUser.getEstablishmentLikePost(), contentCode);
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateLikePost() {
        return RestClient.getHeader(mUser.getAppToken(), mContext.getString(R.string.app_id))
                .createPost(assemblePost());
    }

    @Override
    public boolean hasLikePostCode() {
        return mUser.getEstablishmentLikePost() != null;
    }

    @Override
    public void saveUserLikePostCode(Long likePostCode) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser.setEstablishmentLikePost(likePostCode);
        });
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateRatingPost(Long codUnidade) {
        return RestClient.getHeader(mUser.getAppToken(), mContext.getString(R.string.app_id))
                .createPost(assembleRatingPost(codUnidade));
    }

    @Override
    public Observable<Response<List<PostResponse>>> requestGetUserLikePosts() {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getPosts(Long.valueOf(mContext.getString(R.string.app_id)), mUser.getId(),
                        Long.valueOf(String.valueOf(MetaModelConstants.COD_OBJECT_ESTABLISHMENT)),
                        MetaModelConstants.COD_POST_ESTABLISHMENT_LIKE, null);
    }

    @Override
    public Observable<Response<List<PostResponse>>> requestGetEstablishmentRatingPost(Long codUnidade) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getPosts(Long.valueOf(mContext.getString(R.string.app_id)), mUser.getId(),
                        MetaModelConstants.COD_OBJECT_ESTABLISHMENT, MetaModelConstants.COD_POST_ESTABLISHMENT_RATING,
                        codUnidade);
    }

    @Override
    public Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem) {
        return RestClient
                .getHeader(mUser.getAppToken(), null)
                .getPostContent(mUser.getEstablishmentLikePost(), codConteudoPostagem);
    }

    @Override
    public void addEstablishmentToLikedList(Long code, Long establishmentCode) {
        mLikedEstablishment.put(code, establishmentCode);
    }

    @Override
    public void addEstablishmentToRatingList(Long contentCode, Long establishmentCode) {
        mRatedEstablishment.put(contentCode, establishmentCode);
    }

    @Override
    public void addEstablishmentToContentList(Long contentCode, Long codUnidade) {
        mContentCodeEstablishment.put(contentCode, codUnidade);
    }

    @Override
    public void removeEstablishmentFromLikedList(String codUnidade) {
        Long key = 12312312312L;
        for (Long code : mLikedEstablishment.keySet()) {
            if (mLikedEstablishment.get(code).equals(Long.valueOf(codUnidade))) {
                key = code;
            }
        }
        mLikedEstablishment.remove(key);
    }

    private Long getEstablishmentRatingPostCode(Long establishmentCode) {
        Long contentCode = 0L;
        for (Map.Entry<Long, Long> longLongEntry : mRatedEstablishment.entrySet()) {
            if (longLongEntry.getValue().equals(establishmentCode)) {
                contentCode = longLongEntry.getKey();
            }
        }

        return contentCode;
    }

    @Override
    public Observable<Response<Rating>> requestEstablishmentRating(Long codUnidade) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getObjectRating(MetaModelConstants.COD_POST_ESTABLISHMENT_RATING, MetaModelConstants.COD_OBJECT_ESTABLISHMENT, codUnidade);
    }

    private Post assemblePost() {
        return new Post(new Autor(mUser.getId()), MetaModelConstants.COD_OBJECT_ESTABLISHMENT,
                new PostType(MetaModelConstants.COD_POST_ESTABLISHMENT_LIKE));
    }

    private Post assembleRatingPost(Long codUnidade) {
        return new Post(new Autor(mUser.getId()), MetaModelConstants.COD_OBJECT_ESTABLISHMENT,
                new PostType(MetaModelConstants.COD_POST_ESTABLISHMENT_RATING), codUnidade);
    }

    private PostContent assemblePostContent(String codUnidade) {
        PostContent postContent = new PostContent();
        postContent.setJSON("{codEstabelecimento:" + codUnidade + "}");
        postContent.setTexto("");
        postContent.setLinks(null);
        postContent.setValor(0L);
        return postContent;
    }

    private PostContent assembleRatingPostContent(float value) {
        PostContent postContent = new PostContent();
        postContent.setJSON("");
        postContent.setTexto("");
        postContent.setLinks(null);
        postContent.setValor((long) value);
        return postContent;
    }

    @Override
    public String getPostCode() {
        return String.valueOf(mUser.getEstablishmentLikePost());
    }

    @Override
    public List<String> getUfList() {
        List<String> ufList = new ArrayList<>();
        ufList.add("AC");
        ufList.add("AL");
        ufList.add("AP");
        ufList.add("AM");
        ufList.add("BA");
        ufList.add("CE");
        ufList.add("DF");
        ufList.add("ES");
        ufList.add("GO");
        ufList.add("MS");
        ufList.add("MT");
        ufList.add("MS");
        ufList.add("MG");
        ufList.add("PR");
        ufList.add("PB");
        ufList.add("PA");
        ufList.add("PE");
        ufList.add("PI");
        ufList.add("RJ");
        ufList.add("RN");
        ufList.add("RS");
        ufList.add("RO");
        ufList.add("RR");
        ufList.add("SC");
        ufList.add("SE");
        ufList.add("SP");
        ufList.add("TO");

        return ufList;
    }

    @Override
    public void requestUserUf(Double lat, Double lng, StringListener listener) {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(mContext);
        locationProvider.getReverseGeocodeObservable(lat, lng, 1)
                .retry(10)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable1 -> new ArrayList<>())
                .subscribe(addresses -> {
                    if (addresses != null) {
                        String addressLine = addresses.get(0).getAddressLine(1);
                        if (addressLine.contains("-")) {
                            listener.onNext(getUfFromAddress(addressLine));
                        } else {
                            listener.onNext(addressLine);
                        }
                    }
                });
    }

    @Override
    public Observable<Response<List<Establishment>>> requestEstablishmentsByName(String searchText, String uf) {
        return RestClient.get()
                .getUrgencyEstablishmentByName(searchText, uf, "URGÊNCIA");
    }

    @Override
    public void removeDislikedContentCode() {
        mLikedEstablishment.remove(mDislikedContentCode);
    }

    @Override
    public void animateCameraToMarker(GoogleMap mMap, Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 500, null);
    }

    private String getUfFromAddress(String addressLine) {
        String[] adresses = addressLine.split("-");
        return adresses[1].replace(" ", "");
    }
}