package com.mat_brandao.saudeapp.network.retrofit;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.Installation;
import com.mat_brandao.saudeapp.domain.model.Post;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.mat_brandao.saudeapp.domain.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by infosolo2 on 6/24/2015.
 */
public interface RetrofitInterface {
    @POST("appCivicoRS/rest/instalacoes")
    Observable<Response<Installation>> createInstallation(@Body Installation installation);

    @PUT("appCivicoRS/rest/instalacoes")
    Observable<Response<Installation>> updateInstallation(@Body Installation installation);

    @GET("appCivicoRS/rest/instalacoes/{installationId}")
    Observable<Response<Installation>> getInstallation(@Path("installationId") Long installationId);

    @GET("appCivicoRS/rest/pessoas/autenticar")
    Observable<Response<User>> login(@Header("email") String email, @Header("senha") String password);

    @POST("appCivicoRS/rest/pessoas")
    Observable<Response<ResponseBody>> createUser(@Body User user);

    @GET("appCivicoRS/rest/pessoas/autenticar")
    Observable<Response<User>> loginWithFacebook(@Header("email") String email, @Header("facebookToken") String token);

    @GET("appCivicoRS/rest/pessoas/autenticar")
    Observable<Response<User>> loginWithGoogle(@Header("email") String email, @Header("googleToken") String token);

    @Multipart
    @POST("appCivicoRS/rest/pessoas/{userId}/fotoPerfil")
    Observable<Response<ResponseBody>> saveProfilePhoto(@Path("userId") long userId,
                                                        @Part MultipartBody.Part filePart);

    @GET("appCivicoRS/rest/pessoas/{userId}/fotoPerfil")
    Observable<Response<ResponseBody>> getProfilePhoto(@Path("userId") long userId);

    @GET("appCivicoRS/rest/postagens")
    Observable<Response<List<PostResponse>>> getPosts(@Query("codAplicativo") Long codAplicativo,
                                                      @Query("codAutor") Long codAutor, @Query("codObjetoDestino") Long codObjetoDestino);

    @GET("appCivicoRS/rest/postagens/{codPostagem}/conteudos/{codConteudo}")
    Observable<Response<PostContent>> getPostContent(@Path("codPostagem") Long codPostagem, @Path("codConteudo") Long codConteudo);

    @POST("appCivicoRS/rest/postagens")
    Observable<Response<ResponseBody>> createPost(@Body Post post);

    @DELETE("appCivicoRS/rest/postagens/{codPostagem}/conteudos/{codConteudo}")
    Observable<Response<ResponseBody>> deleteContent(@Path("codPostagem") Long codPostagem, @Path("codConteudo") Long codConteudo);

    @POST("appCivicoRS/rest/postagens/{codPostagem}/conteudos")
    Observable<Response<ResponseBody>> likeEstablishment(@Path("codPostagem") Long codPostagem, @Body PostContent content);

    @GET("mapa-da-saude/rest/estabelecimentos/latitude/{latitude}/longitude/{longitude}/raio/{raio}")
    Observable<Response<List<Establishment>>> getEstablishmentsByGeoLocation(
            @Path("latitude") Double latitude, @Path("longitude") Double longitude,
            @Path("raio") Double radius, @Query("pagina") Integer pagination);

    @GET("mapa-da-saude/rest/remedios")
    Observable<Response<List<Remedy>>> getRemedies(@Query("codBarraEan") String barCode, @Query("produto") String name);
}
