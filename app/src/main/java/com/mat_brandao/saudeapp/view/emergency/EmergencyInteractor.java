package com.mat_brandao.saudeapp.view.emergency;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.Rating;
import com.mat_brandao.saudeapp.domain.util.OnLocationFound;
import com.mat_brandao.saudeapp.domain.util.StringListener;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface EmergencyInteractor {
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

    String getAddressText(String logradouro, String numero, String bairro,
                          String cidade, String uf, String cep);

    String getServicesText(Establishment establishment);

    boolean isEstablishmentLiked(Long codUnidade);

    Observable<Response<ResponseBody>> requestLikeEstablishment(Long postCode, String codUnidade);

    Observable<Response<ResponseBody>> requestLikeEstablishment(String codUnidade);

    Observable<Response<ResponseBody>> requestDisLikeEstablishment(String codUnidade);

    Observable<Response<ResponseBody>> requestCreateLikePost();

    Observable<Response<List<PostResponse>>> requestGetUserLikePosts();

    Observable<Response<List<PostResponse>>> requestGetEstablishmentRatingPost(Long codUnidade);

    Observable<Response<Rating>> requestEstablishmentRating(Long codUnidade);

    Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem);

    String getPostCode();

    boolean hasLikePostCode();

    void saveUserLikePostCode(Long likePostCode);

    void addEstablishmentToLikedList(Long contentCode, Long establishmentCode);

    void addEstablishmentToRatingList(Long contentCode, Long establishmentCode);

    void addEstablishmentToContentList(Long contentCode, Long codUnidade);

    void removeEstablishmentFromLikedList(String codUnidade);

    List<String> getUfList();

    void requestUserUf(Double lat, Double lng, StringListener listener);

    Observable<Response<ResponseBody>> requestCreateRatingPost(Long codUnidade);

    Observable<Response<List<Establishment>>> requestEstablishmentsByName(String searchText, String uf);

    void removeDislikedContentCode();

    void animateCameraToMarker(GoogleMap mMap, Marker marker);
}