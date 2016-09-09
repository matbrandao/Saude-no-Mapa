package com.mat_brandao.saudeapp.network.retrofit;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by infosolo2 on 6/24/2015.
 */
public interface RetrofitInterface {
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

    @GET("mapa-da-saude/rest/estabelecimentos/latitude/{latitude}/longitude/{longitude}/raio/{raio}")
    Observable<Response<List<Establishment>>> getEstablishmentsByGeoLocation(
            @Path("latitude") Double latitude, @Path("longitude") Double longitude,
            @Path("raio") Double radius, @Query("pagina") Integer pagination);
}
