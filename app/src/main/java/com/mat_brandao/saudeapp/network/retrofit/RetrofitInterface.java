package com.mat_brandao.saudeapp.network.retrofit;

import com.mat_brandao.saudeapp.domain.model.User;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by infosolo2 on 6/24/2015.
 */
public interface RetrofitInterface {
    @GET("appCivicoRS/rest/pessoas/autenticar")
    Observable<Response<User>> login(@Header("email") String email, @Header("senha") String password);

    @POST("appCivicoRS/rest/pessoas")
    Observable<Response<ResponseBody>> createUser(@Body User user);

    @POST("appCivicoRS/rest/pessoas/autenticar")
    Observable<Response<User>> loginWithFacebook(@Header("email") String email, @Header("facebookToken") String token);

    @POST("appCivicoRS/rest/pessoas/autenticar")
    Observable<Response<User>> loginWithGoogle(@Header("email") String email, @Header("googleToken") String token);


}
