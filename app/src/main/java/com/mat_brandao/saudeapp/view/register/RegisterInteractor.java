package com.mat_brandao.saudeapp.view.register;

import com.mat_brandao.saudeapp.domain.model.User;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;


public interface RegisterInteractor {
    Observable<Response<ResponseBody>> requestCreateNormalUser(String name, String email, String sex,
                                                         String cep, long birthDate, String password);

    Observable<Response<ResponseBody>> requestCreateFacebookUser(String name, String email, String sex,
                                                                 String cep, long birthDate, String password);

    Observable<Response<ResponseBody>> requestCreateGoogleUser(String name, String email, String sex,
                                                                 String cep, long birthDate, String password);

    User getUser();
}