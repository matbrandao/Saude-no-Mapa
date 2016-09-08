package com.mat_brandao.saudeapp.view.register;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;


public interface RegisterInteractor {
    Observable<Response<ResponseBody>> requestCreateUser(String name, String email, String sex,
                                                         String cep, long birthDate, String password);
}