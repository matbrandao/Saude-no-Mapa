package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.Rating;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface FavEstablishmentInteractor {
    Observable<Response<List<PostResponse>>> requestGetUserPosts();

    Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem);

    Observable<Response<List<Establishment>>> requestGetEstablishment(Long establishmentCode);

    Observable<Response<ResponseBody>> requestLikeEstablishment(Long postCode, Long codEstablishment);

    Observable<Response<ResponseBody>> requestLikeEstablishment(Long codEstablishment);

    Observable<Response<ResponseBody>> requestDislikeEstablishment(Long codEstablishment);

    Observable<Response<List<PostResponse>>> requestGetEstablishmentRatingPost(Long codUnidade);

    Observable<Response<Rating>> requestEstablishmentRating(Long codUnidade);

    Observable<Response<ResponseBody>> requestCreateRatingPost(Long codUnidade);

    String getFluxoClientelaText(String fluxoClientela);

    String getAddressText(String logradouro, String numero, String bairro,
                          String cidade, String uf, String cep);

    String getServicesText(Establishment establishment);

    void addEstablishmentToLikedList(Long contentCode, Long establishmentCode);

    void saveUserLikePostCode(Long likePostCode);

    void removeDislikedContentCode();

    void clearLikedEstablishments();

    String getPostCode();

    int getLikedEstablishmentCount();
}